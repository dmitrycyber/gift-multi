package com.epam.esm.dao;

import com.epam.esm.dao.exception.DaoException;
import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.entity.GiftCertificateEntity;

import java.util.List;

public interface GiftDao {
    List<GiftCertificateEntity> findAllGifts();
    GiftCertificateEntity findGiftById(Long giftId) throws DaoException;
    List<GiftCertificateEntity> findAndSortGift(CustomSearchRequest customSearchRequest);
//    List<GiftCertificateEntity> findGiftByPrice(CustomSearchRequest customSearchRequest) throws DaoException;
//    List<GiftCertificateEntity> findGiftByDuration(CustomSearchRequest customSearchRequest) throws DaoException;

    GiftCertificateEntity createGift(GiftCertificateEntity giftCertificateEntity);
    GiftCertificateEntity updateGift(GiftCertificateEntity giftCertificateEntity);

    void deleteGiftById(Long giftId);
}