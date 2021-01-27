package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftDao;
import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.TagEntity;
import com.epam.esm.util.SearchConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

@ExtendWith(MockitoExtension.class)
public class GiftServiceImplTest {
    @Mock
    private static GiftDao giftDao;
    @Mock
    private static TagDao tagDao;
    @Mock
    private static GiftTagDao giftTagDao;

    @InjectMocks
    private GiftServiceImpl giftService;

    private Timestamp currentTimestamp;
    private List<GiftCertificateEntity> giftCertificateEntityList;
    private List<GiftCertificateEntity> searchGiftCertificateEntityList;
    private GiftCertificateEntity giftCertificateEntity;

    @BeforeEach
    public void init() {
        currentTimestamp = new Timestamp(System.currentTimeMillis());
        giftCertificateEntityList = new ArrayList<>();

        LongStream.range(1, 6)
                .forEach(index -> {
                    giftCertificateEntityList.add(GiftCertificateEntity.builder()
                            .id(index)
                            .name("name" + index)
                            .description("description" + index)
                            .price((int) index)
                            .duration((int) index)
                            .createDate(currentTimestamp)
                            .lastUpdateDate(currentTimestamp).build());
                });
        giftCertificateEntity = giftCertificateEntityList.get(0);

        Set<TagEntity> tags = new HashSet<>();
        tags.add(TagEntity.builder()
                .name("name1")
                .id(1L).build());
        tags.add(TagEntity.builder()
                .name("name2")
                .id(2L).build());
        searchGiftCertificateEntityList = new ArrayList<>();
        searchGiftCertificateEntityList.add(GiftCertificateEntity.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .price(1)
                .duration(1)
                .createDate(currentTimestamp)
                .lastUpdateDate(currentTimestamp)
                .tags(tags).build());


    }

    @Test
    void getAllGifts() {
        Mockito.when(giftDao.findAllGifts()).thenReturn(giftCertificateEntityList);

        List<GiftCertificateDto> allGifts = giftService.getAllGifts();

        Assertions.assertEquals(5, allGifts.size());

        allGifts
                .forEach(giftCertificateEntity -> {
                    Assertions.assertTrue(giftCertificateEntity.getName().contains("name"));
                    Assertions.assertTrue(giftCertificateEntity.getDescription().contains("description"));
                    Assertions.assertNotNull(giftCertificateEntity.getId());
                    Assertions.assertNotNull(giftCertificateEntity.getCreateDate());
                    Assertions.assertNotNull(giftCertificateEntity.getLastUpdateDate());
                    Assertions.assertNotNull(giftCertificateEntity.getPrice());
                    Assertions.assertNotNull(giftCertificateEntity.getDuration());
                    Assertions.assertEquals(currentTimestamp.toLocalDateTime(), giftCertificateEntity.getCreateDate());
                    Assertions.assertEquals(currentTimestamp.toLocalDateTime(), giftCertificateEntity.getLastUpdateDate());
                });
    }

    @Test
    void getGiftById() {
        long id = 1L;
        Mockito.when(giftDao.findGiftById(Mockito.anyLong())).thenReturn(giftCertificateEntity);

        GiftCertificateDto giftById = giftService.getGiftById(id);

        Assertions.assertEquals("name1", giftById.getName());
        Assertions.assertEquals("description1", giftById.getDescription());
        Assertions.assertEquals(1, giftById.getDuration());
        Assertions.assertEquals(1, giftById.getPrice());
        Assertions.assertEquals(currentTimestamp.toLocalDateTime(), giftById.getCreateDate());
        Assertions.assertEquals(currentTimestamp.toLocalDateTime(), giftById.getLastUpdateDate());
    }

    @Test
    void searchGiftTest() {
        Mockito.when(giftDao.findAndSortGifts(Mockito.any(CustomSearchRequest.class))).thenReturn(searchGiftCertificateEntityList);

        String expectedTagName = "name1";
        String expectedName = "name1";
        String expectedDescription = "description1";
        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .tagNamePrefix(expectedTagName)
                .namePrefix(expectedName)
                .descriptionPrefix(expectedDescription)
                .sortField(SearchConstants.NAME_FIELD)
                .sortMethod(SearchConstants.DESC_METHOD_SORT).build();

        List<GiftCertificateDto> giftCertificateDtoList = giftService.searchGifts(customSearchRequest);
        GiftCertificateDto giftCertificateDto = giftCertificateDtoList.get(0);
        Set<TagDto> tags = giftCertificateDto.getTags();

        Assertions.assertNotNull(giftCertificateDtoList);
        Assertions.assertNotNull(tags);
        Assertions.assertEquals(expectedName, giftCertificateDto.getName());
        Assertions.assertEquals(expectedDescription, giftCertificateDto.getDescription());
    }

    @Test
    void createGift() {
        System.out.println(giftCertificateEntity);

        Mockito.when(giftDao.createGift(Mockito.any(GiftCertificateEntity.class))).thenReturn(giftCertificateEntity);

        GiftCertificateDto giftCertificateDto = giftService.createGift(GiftCertificateDto.builder()
                .name("name1")
                .description("description1")
                .price(1)
                .duration(1).build());

        Assertions.assertEquals(1L, giftCertificateDto.getId());
        Assertions.assertEquals("name1", giftCertificateDto.getName());
        Assertions.assertEquals("description1", giftCertificateDto.getDescription());
        Assertions.assertEquals(1, giftCertificateDto.getPrice());
        Assertions.assertEquals(1, giftCertificateDto.getDuration());
    }

//    @Test
//    void updateGift() {
//        Mockito.when(giftDao.updateGift(Mockito.any(GiftCertificateEntity.class))).thenReturn(giftCertificateEntity);
//        Mockito.when(giftDao.findGiftById(Mockito.any())).thenReturn(giftCertificateEntity);
//
//        GiftCertificateDto giftCertificateDto = giftService.updateGift(GiftCertificateDto.builder()
//                .name("name1")
//                .description("description1")
//                .price(1)
//                .duration(1).build());
//
//        Assertions.assertEquals("description1", giftCertificateDto.getDescription());
//    }

    @Test
    void deleteGift() {
        Long id = 1L;

        giftService.deleteGiftById(id);

        Mockito.verify(giftDao, Mockito.times(1)).deleteGiftById(Mockito.anyLong());
    }
}