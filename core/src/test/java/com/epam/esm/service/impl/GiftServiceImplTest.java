package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftDao;
import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.GiftDaoImpl;
import com.epam.esm.dao.impl.GiftTagDaoImpl;
import com.epam.esm.dao.impl.TagDaoImpl;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.service.GiftService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GiftServiceImplTest {
    private static GiftDao giftDao;
    private static TagDao tagDao;
    private static GiftTagDao giftTagDao;
    private static GiftService giftService;

    private Timestamp currentTimestamp;
    private List<GiftCertificateEntity> giftCertificateEntityList;
    private GiftCertificateEntity giftCertificateEntity;

    @BeforeAll
    static void setup() {
        giftDao = mock(GiftDaoImpl.class);
        tagDao = mock(TagDaoImpl.class);
        giftTagDao = mock(GiftTagDaoImpl.class);
        giftService = new GiftServiceImpl(giftDao, tagDao, giftTagDao);
    }

    @BeforeEach
    void init() {
        currentTimestamp = new Timestamp(System.currentTimeMillis());
        giftCertificateEntityList = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            giftCertificateEntityList.add(GiftCertificateEntity.builder()
                    .id(i)
                    .name("name" + i)
                    .description("description" + i)
                    .price((int) i)
                    .duration((int) i)
                    .createDate(currentTimestamp)
                    .lastUpdateDate(currentTimestamp).build());
        }
        giftCertificateEntity = GiftCertificateEntity.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .price(1)
                .duration(1)
                .createDate(currentTimestamp)
                .lastUpdateDate(currentTimestamp).build();
    }

    @Test
    void getAllGifts() {
        when(giftDao.findAllGifts()).thenReturn(giftCertificateEntityList);


        List<GiftCertificateDto> allGifts = giftService.getAllGifts();

        assertEquals(5, allGifts.size());
        //check all fields
    }

    @Test
    void getGiftById() {
        long id = 1L;
        when(giftDao.findGiftById(anyLong())).thenReturn(giftCertificateEntity);

        GiftCertificateDto giftById = giftService.getGiftById(id);

        assertEquals("name1", giftById.getName());
    }

//    @Test
//    void searchGiftsByName() {
//        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
//                .name("name").build();
//
//        when(giftDao.findAndSortGift(any(CustomSearchRequest.class))).thenReturn(giftCertificateEntityList);
//
//        List<GiftCertificateDto> giftCertificateDtoList = giftService.searchGifts(customSearchRequest);
//
//        assertEquals(5, giftCertificateDtoList.size());
//
//        assertAll(
//                () -> assertEquals(5, giftCertificateDtoList.size()),
//                () -> assertTrue(giftCertificateDtoList.get(0).getName().contains("name")),
//                () -> assertTrue(giftCertificateDtoList.get(1).getName().contains("name")),
//                () -> assertTrue(giftCertificateDtoList.get(2).getName().contains("name")),
//                () -> assertTrue(giftCertificateDtoList.get(3).getName().contains("name")),
//                () -> assertTrue(giftCertificateDtoList.get(4).getName().contains("name")));
//    }
//
//    @Test
//    void searchGiftsByPrice() {
//        int priceFrom = 1;
//        int priceTo = 5;
//
//        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
//                .priceFrom(priceFrom)
//                .priceTo(priceTo).build();
//
//        when(giftDao.findGiftByPrice(any(CustomSearchRequest.class))).thenReturn(giftCertificateEntityList);
//
//        List<GiftCertificateDto> giftCertificateDtoList = giftService.searchGifts(customSearchRequest);
//
//        assertEquals(5, giftCertificateDtoList.size());
//
//        assertAll(
//                () -> assertEquals(5, giftCertificateDtoList.size()),
//                () -> assertTrue(giftCertificateDtoList.get(0).getPrice() >= priceFrom
//                        && giftCertificateDtoList.get(0).getPrice() <= priceTo),
//                () -> assertTrue(giftCertificateDtoList.get(1).getPrice() >= priceFrom
//                        && giftCertificateDtoList.get(1).getPrice() <= priceTo),
//                () -> assertTrue(giftCertificateDtoList.get(2).getPrice() >= priceFrom
//                        && giftCertificateDtoList.get(2).getPrice() <= priceTo),
//                () -> assertTrue(giftCertificateDtoList.get(3).getPrice() >= priceFrom
//                        && giftCertificateDtoList.get(3).getPrice() <= priceTo),
//                () -> assertTrue(giftCertificateDtoList.get(4).getPrice() >= priceFrom
//                        && giftCertificateDtoList.get(4).getPrice() <= priceTo)
//                );
//    }
//
//    @Test
//    void searchGiftsByDuration() {
//        int durationFrom = 1;
//        int durationTo = 5;
//
//        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
//                .durationFrom(durationFrom)
//                .durationTo(durationTo).build();
//
//        when(giftDao.findGiftByDuration(any(CustomSearchRequest.class))).thenReturn(giftCertificateEntityList);
//
//        List<GiftCertificateDto> giftCertificateDtoList = giftService.searchGifts(customSearchRequest);
//
//        assertEquals(5, giftCertificateDtoList.size());
//
//        assertAll(
//                () -> assertEquals(5, giftCertificateDtoList.size()),
//                () -> assertTrue(giftCertificateDtoList.get(0).getDuration() >= durationFrom
//                        && giftCertificateDtoList.get(0).getDuration() <= durationTo),
//                () -> assertTrue(giftCertificateDtoList.get(1).getDuration() >= durationFrom
//                        && giftCertificateDtoList.get(1).getDuration() <= durationTo),
//                () -> assertTrue(giftCertificateDtoList.get(2).getDuration() >= durationFrom
//                        && giftCertificateDtoList.get(2).getDuration() <= durationTo),
//                () -> assertTrue(giftCertificateDtoList.get(3).getDuration() >= durationFrom
//                        && giftCertificateDtoList.get(3).getDuration() <= durationTo),
//                () -> assertTrue(giftCertificateDtoList.get(4).getDuration() >= durationFrom
//                        && giftCertificateDtoList.get(4).getDuration() <= durationTo)
//        );
//    }

    @Test
    void createGift() {
        when(giftDao.createGift(any(GiftCertificateEntity.class))).thenReturn(giftCertificateEntity);

        GiftCertificateDto giftCertificateDto = giftService.createGift(GiftCertificateDto.builder()
                .name("name1")
                .description("description1")
                .price(1)
                .duration(1).build());

        assertEquals(1L, giftCertificateDto.getId());
        assertEquals("name1", giftCertificateDto.getName());
        assertEquals("description1", giftCertificateDto.getDescription());
        assertEquals(1, giftCertificateDto.getPrice());
        assertEquals(1, giftCertificateDto.getDuration());
    }

    @Test
    void updateGift() {
        when(giftDao.updateGift(any(GiftCertificateEntity.class))).thenReturn(giftCertificateEntity);

        GiftCertificateDto giftCertificateDto = giftService.updateGift(GiftCertificateDto.builder()
                .name("name1")
                .description("description1")
                .price(1)
                .duration(1).build());

        assertEquals("description1", giftCertificateDto.getDescription());
    }
}