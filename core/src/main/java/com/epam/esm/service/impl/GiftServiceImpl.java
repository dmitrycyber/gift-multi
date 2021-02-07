package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftDao;
import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.GiftTagEntity;
import com.epam.esm.model.entity.TagEntity;
import com.epam.esm.service.GiftService;
import com.epam.esm.util.converter.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
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

        for (TagEntity tag : tags) {
            TagEntity tagsIfNeeded = createTagsIfNeeded(tag, giftEntity);
            tag.setId(tagsIfNeeded.getId());
        }
        return EntityConverter.convertGiftEntityToDto(giftEntity);
    }

    @Override
    @Transactional
    public GiftCertificateDto updateGift(GiftCertificateDto giftCertificateDto) {
        GiftCertificateEntity giftEntityToSave = EntityConverter.convertGiftDtoToEntity(giftCertificateDto);

        GiftCertificateEntity savedGiftEntity = giftDao.updateGift(giftEntityToSave);

        Set<TagEntity> tagsToSave = giftEntityToSave.getTags();
        if (tagsToSave != null) {
            Set<TagEntity> tagEntities = updateGiftTags(savedGiftEntity.getTags(), tagsToSave, giftEntityToSave);
            savedGiftEntity.setTags(tagEntities);
        }
        return EntityConverter.convertGiftEntityToDto(savedGiftEntity);
    }

    @Override
    public void deleteGiftById(Long giftId) {
        giftDao.deleteGiftById(giftId);
    }

    private Set<TagEntity> updateGiftTags(Set<TagEntity> savedTags, Set<TagEntity> tagsToSave, GiftCertificateEntity giftCertificateEntity) {
        if (tagsToSave.isEmpty()) {
            return deleteAllGiftTags(savedTags, tagsToSave, giftCertificateEntity);
        }

        Set<String> savedTagNames = savedTags == null
                ? new HashSet<>()
                : savedTags.stream()
                .map(TagEntity::getName)
                .collect(Collectors.toSet());
        Set<String> tagNamesToSave = tagsToSave.stream()
                .map(TagEntity::getName)
                .collect(Collectors.toSet());

        if (savedTags != null) {
            deleteGiftTagsIfNeeded(savedTags, giftCertificateEntity, tagNamesToSave);
        }
        addTagsIfNeeded(tagsToSave, giftCertificateEntity, savedTagNames);
        return tagsToSave;
    }

    private Set<TagEntity> deleteAllGiftTags(Set<TagEntity> savedTags, Set<TagEntity> tagsToSave, GiftCertificateEntity giftCertificateEntity) {
        Long id = giftCertificateEntity.getId();
        if (savedTags != null) {
            savedTags.
                    forEach(tagEntity -> {
                        deleteGiftById(tagEntity.getId());
                        giftTagDao.deleteGiftTag(id, tagEntity.getId());
                    });
        }
        return tagsToSave;
    }

    private void addTagsIfNeeded(Set<TagEntity> tagsToSave, GiftCertificateEntity giftCertificateEntity, Set<String> savedTagNames) {
        for (TagEntity tagEntity : tagsToSave) {
            if (savedTagNames.contains(tagEntity.getName())) {
                TagEntity tagByName = tagDao.findTagByName(tagEntity.getName());
                tagEntity.setId(tagByName.getId());
            } else {
                TagEntity tagsIfNeeded = createTagsIfNeeded(tagEntity, giftCertificateEntity);
                tagEntity.setId(tagsIfNeeded.getId());
            }
        }
    }

    private void deleteGiftTagsIfNeeded(Set<TagEntity> savedTags, GiftCertificateEntity giftCertificateEntity, Set<String> tagNamesToSave) {
        for (TagEntity tagEntity : savedTags) {
            if (!tagNamesToSave.contains(tagEntity.getName())) {
                giftTagDao.deleteGiftTag(giftCertificateEntity.getId(), tagEntity.getId());
            }
        }
    }

    private TagEntity createTagsIfNeeded(TagEntity tag, GiftCertificateEntity giftEntity) {
        TagEntity savedTag = tagDao.findTagByName(tag.getName());
        if (savedTag == null) {
            savedTag = tagDao.createTag(tag);
        }
        createGiftTag(giftEntity, savedTag);

        return savedTag;
    }

    private void createGiftTag(GiftCertificateEntity giftEntity, TagEntity savedTag) {
        GiftTagEntity giftTagEntity = GiftTagEntity.builder()
                .giftId(giftEntity.getId())
                .tagId(savedTag.getId())
                .build();
        giftTagDao.saveGiftTag(giftTagEntity);
    }


}
