package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftDao;
import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.GiftDaoImpl;
import com.epam.esm.dao.impl.GiftTagDaoImpl;
import com.epam.esm.dao.impl.TagDaoImpl;
import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.service.GiftService;
import com.epam.esm.util.SearchConstants;
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
import static org.mockito.Mockito.*;

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
        for(GiftCertificateDto giftCertificateDto : allGifts){
            assertTrue(giftCertificateDto.getName().contains("name"));
        }
    }

    @Test
    void getGiftById() {
        long id = 1L;
        when(giftDao.findGiftById(anyLong())).thenReturn(giftCertificateEntity);

        GiftCertificateDto giftById = giftService.getGiftById(id);

        assertEquals("name1", giftById.getName());
    }

    @Test
    void searchGiftTest(){
        when(giftDao.findAndSortGift(any(CustomSearchRequest.class))).thenReturn(giftCertificateEntityList);

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .tagNamePrefix("name1")
                .namePrefix("name1")
                .descriptionPrefix("description1")
                .sortField(SearchConstants.NAME_FIELD)
                .sortMethod(SearchConstants.DESC_METHOD_SORT).build();

        List<GiftCertificateDto> giftCertificateDtoList = giftService.searchGifts(customSearchRequest);

        assertNotNull(giftCertificateDtoList);
    }

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

    @Test
    void deleteGift(){
        Long id = 1L;

        giftService.deleteGiftById(id);

        verify(giftDao, times(1)).deleteGiftById(anyLong());
    }
}