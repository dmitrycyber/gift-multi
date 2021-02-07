package com.epam.esm.service;

import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.dto.GiftCertificateDto;
import java.util.List;

public interface GiftService {
    List<GiftCertificateDto> getAllGifts();
    GiftCertificateDto getGiftById(Long giftId);
    List<GiftCertificateDto> searchGifts(CustomSearchRequest customSearchRequest);
    GiftCertificateDto createGift(GiftCertificateDto giftCertificateDto);
    GiftCertificateDto updateGift(GiftCertificateDto giftCertificateDto);
    void deleteGiftById(Long giftId);
}
