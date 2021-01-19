package com.epam.esm.dao.impl;

import com.epam.esm.dao.exception.GiftNotFoundException;
import com.epam.esm.model.entity.GiftCertificateEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;


public class GiftDaoImplTest {
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private GiftDaoImpl giftDao;

    @BeforeEach
    public void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .addDefaultScripts()
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(dataSource);
        giftDao = new GiftDaoImpl(jdbcTemplate);
    }


    @Test
    public void findAllGifts() {
        String s = "1" + null + "2";
        System.out.println(s);


//        List<GiftCertificateEntity> allGifts = giftDao.findAllGifts();
//        assertNotNull(allGifts);
//        assertEquals(5, allGifts.size());
    }

    @Test
    public void findGiftById() {
        GiftCertificateEntity expectedEntity = GiftCertificateEntity.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .price(10)
                .duration(10).build();

        GiftCertificateEntity actualEntity = giftDao.findGiftById(1L);

        assertEquals(expectedEntity, actualEntity);
    }

//    @Test
//    public void successfulFindGiftByName() {
//        String expectedName = "name";
//
//        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
//                .name(expectedName).build();
//
//        List<GiftCertificateEntity> giftByName = giftDao.findAndSortGift(customSearchRequest);
//        assertTrue(giftByName.size() > 0);
//
//        for (GiftCertificateEntity entity : giftByName) {
//            assertTrue(entity.getName().contains(expectedName));
//        }
//    }
//
//    @Test
//    public void negativeFindGiftByName() {
//        String expectedName = "qwe";
//
//        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
//                .name(expectedName).build();
//
//        List<GiftCertificateEntity> giftByName = giftDao.findAndSortGift(customSearchRequest);
//        assertEquals(0, giftByName.size());
//    }

//    @Test
//    public void findGiftByPrice() {
//        Integer priceFrom = 10;
//        Integer priceTo = 20;
//
//
//        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
//                .priceFrom(priceFrom)
//                .priceTo(priceTo).build();
//
//        List<GiftCertificateEntity> giftByPrice = giftDao.findGiftByPrice(customSearchRequest);
//        assertNotNull(giftByPrice);
//        assertEquals(2, giftByPrice.size());
//    }
//
//    @Test
//    public void findGiftByDuration() {
//        Integer durationFrom = 10;
//        Integer durationTo = 20;
//
//
//        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
//                .durationFrom(durationFrom)
//                .durationTo(durationTo).build();
//
//        List<GiftCertificateEntity> giftByDuration = giftDao.findGiftByDuration(customSearchRequest);
//        assertNotNull(giftByDuration);
//        assertEquals(2, giftByDuration.size());
//    }

    @Test
    public void createGiftWithoutTags() {
        GiftCertificateEntity entityToSave = GiftCertificateEntity.builder()
                .name("testName")
                .description("testDescription")
                .price(100)
                .duration(100)
                .createDate(null)
                .lastUpdateDate(null).build();

        GiftCertificateEntity savedEntity = giftDao.createGift(entityToSave);
        assertNotNull(savedEntity);
        assertEquals(6, giftDao.findAllGifts().size());
    }

    @Test
    public void updateGift() {
        GiftCertificateEntity entityToSave = GiftCertificateEntity.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .price(20)
                .duration(10).build();

        GiftCertificateEntity savedEntity = giftDao.updateGift(entityToSave);
        assertNotNull(savedEntity);
        assertEquals(20, (int) giftDao.findGiftById(1L).getPrice());
    }

    @Test
    public void deleteGiftById() {
        giftDao.deleteGiftById(1L);

        assertEquals(4, giftDao.findAllGifts().size());
    }

    @Test
    public void giftNotFoundExceptionCheck(){
        assertThrows(GiftNotFoundException.class, () -> giftDao.findGiftById(6L));
    }
}
