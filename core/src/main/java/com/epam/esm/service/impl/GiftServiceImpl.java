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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
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
    public List<GiftCertificateDto> getAllGifts(){
        List<GiftCertificateDto> giftCertificateDtoList = giftDao.findAllGifts().stream()
                .map(EntityConverter::convertGiftEntityDto)
                .collect(Collectors.toList());

        return giftCertificateDtoList;
    }

    @Override
    public GiftCertificateDto getGiftById(Long giftId) {
        return EntityConverter.convertGiftEntityDto(giftDao.findGiftById(giftId));
    }

    @Override
    public List<GiftCertificateDto> searchGifts(CustomSearchRequest customSearchRequest) {
        List<GiftCertificateEntity> giftList = giftDao.findAndSortGift(customSearchRequest);

        return giftList.stream()
                .map(EntityConverter::convertGiftEntityDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GiftCertificateDto createGift(GiftCertificateDto giftCertificateDto) {

        GiftCertificateEntity giftEntity = giftDao.createGift(EntityConverter.convertGiftDtoEntity(giftCertificateDto));

        Set<TagEntity> tags = giftEntity.getTags();
        if (tags != null){
            for (TagEntity tag : tags){

                TagEntity tagEntity = tagDao.createTag(tag);
                tag.setId(tagEntity.getId());

                GiftTagEntity giftTagEntity = GiftTagEntity.builder()
                        .giftId(giftEntity.getId())
                        .tagId(tagEntity.getId()).build();

                giftTagDao.insertNewGiftTagEntity(giftTagEntity);
            }
        }

        return EntityConverter.convertGiftEntityDto(giftEntity);
    }

    @Override
    public GiftCertificateDto updateGift(GiftCertificateDto giftCertificateDto) {
        GiftCertificateEntity entity = giftDao.updateGift(EntityConverter.convertGiftDtoEntity(giftCertificateDto));

        return EntityConverter.convertGiftEntityDto(entity);
    }

    @Override
    public void deleteGiftById(Long giftId) {
        giftDao.deleteGiftById(giftId);
    }
}
