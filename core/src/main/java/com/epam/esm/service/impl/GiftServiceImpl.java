package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftDao;
import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.GiftTagEntity;
import com.epam.esm.model.entity.TagEntity;
import com.epam.esm.service.GiftService;
import com.epam.esm.util.converter.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GiftServiceImpl implements GiftService {
    private final GiftDao giftDao;
    private final TagDao tagDao;
    private final GiftTagDao giftTagDao;

    @Autowired
    public GiftServiceImpl(GiftDao giftDao, TagDao tagDao, GiftTagDao giftTagDao) {
        this.giftDao = giftDao;
        this.tagDao = tagDao;
        this.giftTagDao = giftTagDao;
    }

    @Override
    @Transactional
    public List<GiftCertificateDto> getAllGifts() {
        List<GiftCertificateEntity> giftCertificateEntityList = giftDao.findAllGifts();
        return giftCertificateEntityList.stream()
                .map(EntityConverter::convertGiftEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GiftCertificateDto getGiftById(Long giftId) {
        GiftCertificateEntity giftById = giftDao.findGiftById(giftId);
        return EntityConverter.convertGiftEntityToDto(giftById);
    }

    @Override
    @Transactional
    public List<GiftCertificateDto> searchGifts(CustomSearchRequest customSearchRequest) {
        List<GiftCertificateEntity> giftList = giftDao.findAndSortGifts(customSearchRequest);
        return giftList.stream()
                .map(EntityConverter::convertGiftEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GiftCertificateDto createGift(GiftCertificateDto giftCertificateDto) {
        GiftCertificateEntity giftCertificateEntity = EntityConverter.convertGiftDtoToEntity(giftCertificateDto);
        GiftCertificateEntity giftEntity = giftDao.createGift(giftCertificateEntity);

        Set<TagEntity> tags = giftEntity.getTags();

        if (tags != null) {
            for (TagEntity tag : tags) {
                TagEntity tagsIfNeeded = createTagsIfNeeded(tag, giftEntity);
                tag.setId(tagsIfNeeded.getId());
            }
        }
        return EntityConverter.convertGiftEntityToDto(giftEntity);
    }

    @Override
    @Transactional
    public GiftCertificateDto updateGift(GiftCertificateDto giftCertificateDto) {
        GiftCertificateDto dtoToSave = defineDtoToSave(giftCertificateDto);

        Set<TagDto> tagDtoSet = dtoToSave.getTags();
        if (tagDtoSet == null) {
            Long id = dtoToSave.getId();
            Set<TagEntity> savedTags = giftDao.findGiftById(id).getTags();
            if (savedTags != null) {
                savedTags.
                        forEach(tagEntity -> {
                            deleteGiftById(tagEntity.getId());
                            giftTagDao.deleteGiftTag(id, tagEntity.getId());
                        });
            }
        }

        GiftCertificateEntity giftCertificateEntity = EntityConverter.convertGiftDtoToEntity(dtoToSave);


        Set<TagEntity> tags = giftCertificateEntity.getTags();
        GiftCertificateEntity giftEntity = giftDao.updateGift(giftCertificateEntity);


        for (TagEntity tag : tags) {
            TagEntity tagsIfNeeded = createTagsIfNeeded(tag, giftEntity);
            tag.setId(tagsIfNeeded.getId());
        }

        return EntityConverter.convertGiftEntityToDto(giftEntity);
    }

    private GiftCertificateDto defineDtoToSave(GiftCertificateDto giftCertificateDto) {
        String name = giftCertificateDto.getName();
        String description = giftCertificateDto.getDescription();
        Integer duration = giftCertificateDto.getDuration();
        Integer price = giftCertificateDto.getPrice();
        Set<TagDto> tags = giftCertificateDto.getTags();

        GiftCertificateDto giftById = getGiftById(giftCertificateDto.getId());

        if (name != null) {
            giftById.setName(name);
        }
        if (description != null) {
            giftById.setDescription(description);
        }
        if (duration != null) {
            giftById.setDuration(duration);
        }
        if (price != null) {
            giftById.setPrice(price);
        }
        if (tags != null) {
            giftById.setTags(tags);
        }
        return giftById;
    }

    @Override
    public void deleteGiftById(Long giftId) {
        giftDao.deleteGiftById(giftId);
    }

    private TagEntity createTagsIfNeeded(TagEntity tag, GiftCertificateEntity giftEntity) {
        TagEntity tagEntity;
        if (tagDao.findTagByName(tag.getName()) == null) {
            tagEntity = tagDao.createTag(tag);
            tag.setId(tagEntity.getId());

            GiftTagEntity giftTagEntity = GiftTagEntity.builder()
                    .giftId(giftEntity.getId())
                    .tagId(tagEntity.getId())
                    .build();

            giftTagDao.saveGiftTag(giftTagEntity);
            return tagEntity;
        }
        return tagDao.findTagByName(tag.getName());

    }
}
