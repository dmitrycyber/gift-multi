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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GiftServiceImpl implements GiftService {
    private final GiftDao giftDao;
    private final TagDao tagDao;
    private final GiftTagDao giftTagDao;

    @Autowired
    public GiftServiceImpl(@Qualifier("giftDaoImpl") GiftDao giftDao, @Qualifier("tagDaoImpl") TagDao tagDao, GiftTagDao giftTagDao) {
        this.giftDao = giftDao;
        this.tagDao = tagDao;
        this.giftTagDao = giftTagDao;
    }

    @Override
    @Transactional
    public List<GiftCertificateDto> getAllGifts() {
        List<GiftCertificateDto> giftCertificateDtoList = giftDao.findAllGifts().stream()
                .map(EntityConverter::convertGiftEntityToDto)
                .collect(Collectors.toList());
        return giftCertificateDtoList;
    }

    @Override
    @Transactional
    public GiftCertificateDto getGiftById(Long giftId) {
        return EntityConverter.convertGiftEntityToDto(giftDao.findGiftById(giftId));
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
        GiftCertificateEntity giftEntity = giftDao.createGift(EntityConverter.convertGiftDtoToEntity(giftCertificateDto));


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
        Set<TagDto> tagDtoSet = giftCertificateDto.getTags();
        if (tagDtoSet == null){
            Set<TagEntity> savedTags = giftDao.findGiftById(giftCertificateDto.getId()).getTags();
            if (savedTags != null){
                savedTags.
                        forEach(tagEntity -> {
                            deleteGiftById(tagEntity.getId());
                            giftTagDao.deleteGiftTag(giftCertificateDto.getId(), tagEntity.getId());
                        });
            }
        }

        GiftCertificateEntity giftEntity = giftDao.updateGift(EntityConverter.convertGiftDtoToEntity(giftCertificateDto));
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
