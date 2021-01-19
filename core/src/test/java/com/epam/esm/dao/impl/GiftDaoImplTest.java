package com.epam.esm.dao.impl;

import com.epam.esm.dao.exception.GiftNotFoundException;
import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.TagEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;

import java.util.List;
import java.util.Set;

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
        List<GiftCertificateEntity> allGifts = giftDao.findAllGifts();
        assertNotNull(allGifts);
        assertEquals(5, allGifts.size());
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

    @Test
    public void findAndSortGiftByName(){
        String namePrefix = "1";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .namePrefix(namePrefix).build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGift(customSearchRequest);

        assertNotNull(resultList);

        for(GiftCertificateEntity giftCertificateEntity : resultList) {
            assertTrue(giftCertificateEntity.getName().contains(namePrefix));
        }
    }

    @Test
    public void findAndSortGiftByDescription(){
        String descriptionPrefix = "1";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .descriptionPrefix(descriptionPrefix).build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGift(customSearchRequest);

        assertNotNull(resultList);

        for(GiftCertificateEntity giftCertificateEntity : resultList) {
            assertTrue(giftCertificateEntity.getDescription().contains(descriptionPrefix));
        }
    }

    @Test
    public void findAndSortGiftByTagName(){
        String tagNamePrefix = "name1";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .tagNamePrefix(tagNamePrefix).build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGift(customSearchRequest);

        assertNotNull(resultList);

        for(GiftCertificateEntity giftCertificateEntity : resultList) {
            Set<TagEntity> tags = giftCertificateEntity.getTags();
            assertEquals(2, tags.size());
        }
    }

    @Test
    public void findAndSortGiftByGiftNameAndDescription(){
        String namePrefix = "1";
        String descriptionPrefix = "1";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .namePrefix(namePrefix)
                .descriptionPrefix(descriptionPrefix).build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGift(customSearchRequest);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
    }

    @Test
    public void findAndSortGiftByGiftNameAndDescriptionAndTagName(){
        String namePrefix = "2";
        String descriptionPrefix = "2";
        String tagNamePrefix = "name3";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .namePrefix(namePrefix)
                .descriptionPrefix(descriptionPrefix)
                .tagNamePrefix(tagNamePrefix).build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGift(customSearchRequest);

        assertNotNull(resultList);
        assertEquals(1, resultList.size());
    }

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
