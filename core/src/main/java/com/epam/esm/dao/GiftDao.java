package com.epam.esm.dao;

import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.entity.GiftCertificateEntity;
import java.util.List;

public interface GiftDao {


    /**
     * Find all gift certificates
     * @return List GiftCertificateEntity
     * if fount no gifts - return empty list
     */
    List<GiftCertificateEntity> findAllGifts();

    /**
     * Find gift certificate by gift id
     * @param giftId
     * @return GiftCertificateEntity
     * @throws com.epam.esm.dao.exception.GiftNotFoundException if fount no gifts
     */
    GiftCertificateEntity findGiftById(Long giftId);

    /**
     * Find gift certificate by criteria
     * @param customSearchRequest
     * @return List GiftCertificateEntity which matches the search conditions
     */
    List<GiftCertificateEntity> findAndSortGifts(CustomSearchRequest customSearchRequest);

    /**
     * Create gift
     * @param giftCertificateEntity
     * @return created gift
     */
    GiftCertificateEntity createGift(GiftCertificateEntity giftCertificateEntity);

    /**
     * Update gift
     * @param giftCertificateEntity
     * @return updated gift
     * @throws com.epam.esm.dao.exception.GiftNotFoundException if fount no gifts
     */
    GiftCertificateEntity updateGift(GiftCertificateEntity giftCertificateEntity);

    /**
     * Delete gift by id
     * @param giftId
     * @throws com.epam.esm.dao.exception.GiftNotFoundException if fount no gifts
     */
    void deleteGiftById(Long giftId);
}