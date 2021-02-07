package com.epam.esm.dao.impl;

import com.epam.esm.dao.exception.GiftNotFoundException;
import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.TagEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Assertions.assertNotNull(allGifts);
        Assertions.assertEquals(5, allGifts.size());
        allGifts
                .forEach(giftCertificateEntity -> {
                    Assertions.assertTrue(giftCertificateEntity.getName().contains("name"));
                    Assertions.assertTrue(giftCertificateEntity.getDescription().contains("description"));
                    Assertions.assertNotNull(giftCertificateEntity.getId());
                    Assertions.assertNull(giftCertificateEntity.getCreateDate());
                    Assertions.assertNull(giftCertificateEntity.getLastUpdateDate());
                    Assertions.assertNotNull(giftCertificateEntity.getPrice());
                    Assertions.assertNotNull(giftCertificateEntity.getDuration());
                });
    }

    @Test
    public void findGiftById() {
        Set<TagEntity> tags = new HashSet<>();
        tags.add(TagEntity.builder()
                .name("name1")
                .id(1L)
                .build());
        tags.add(TagEntity.builder()
                .name("name2")
                .id(2L)
                .build());

        GiftCertificateEntity expectedEntity = GiftCertificateEntity.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .price(10)
                .duration(10)
                .tags(tags)
                .build();

        GiftCertificateEntity actualEntity = giftDao.findGiftById(1L);

        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void findAndSortGiftByName() {
        String namePrefix = "1";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .namePrefix(namePrefix)
                .build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGifts(customSearchRequest);

        Assertions.assertNotNull(resultList);

        for (GiftCertificateEntity giftCertificateEntity : resultList) {
            Assertions.assertTrue(giftCertificateEntity.getName().contains(namePrefix));
        }
    }

    @Test
    public void findAndSortGiftByDescription() {
        String descriptionPrefix = "1";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .descriptionPrefix(descriptionPrefix)
                .build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGifts(customSearchRequest);

        Assertions.assertNotNull(resultList);

        for (GiftCertificateEntity giftCertificateEntity : resultList) {
            Assertions.assertTrue(giftCertificateEntity.getDescription().contains(descriptionPrefix));
        }
    }

    @Test
    public void findAndSortGiftByTagName() {
        String tagNamePrefix = "name1";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .tagNamePrefix(tagNamePrefix)
                .build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGifts(customSearchRequest);

        Assertions.assertNotNull(resultList);

        for (GiftCertificateEntity giftCertificateEntity : resultList) {
            Set<TagEntity> tags = giftCertificateEntity.getTags();
            Assertions.assertEquals(2, tags.size());
        }
    }

    @Test
    public void findAndSortGiftByGiftNameAndDescription() {
        String namePrefix = "1";
        String descriptionPrefix = "1";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .namePrefix(namePrefix)
                .descriptionPrefix(descriptionPrefix)
                .build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGifts(customSearchRequest);

        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(1, resultList.size());
    }

    @Test
    public void findAndSortGiftByGiftNameAndDescriptionAndTagName() {
        String namePrefix = "2";
        String descriptionPrefix = "2";
        String tagNamePrefix = "name3";

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .namePrefix(namePrefix)
                .descriptionPrefix(descriptionPrefix)
                .tagNamePrefix(tagNamePrefix)
                .build();

        List<GiftCertificateEntity> resultList = giftDao.findAndSortGifts(customSearchRequest);

        Assertions.assertNotNull(resultList);
        Assertions.assertEquals(1, resultList.size());
    }

    @Test
    public void createGiftWithoutTags() {
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());

        GiftCertificateEntity entityToSave = GiftCertificateEntity.builder()
                .name("testName")
                .description("testDescription")
                .price(100)
                .duration(100)
                .createDate(currentTimestamp)
                .lastUpdateDate(currentTimestamp)
                .build();

        GiftCertificateEntity savedEntity = giftDao.createGift(entityToSave);
        Assertions.assertNotNull(savedEntity);
        Assertions.assertEquals(6, giftDao.findAllGifts().size());
        Assertions.assertEquals(entityToSave.getName(), savedEntity.getName());
        Assertions.assertEquals(entityToSave.getDescription(), savedEntity.getDescription());
        Assertions.assertEquals(entityToSave.getDuration(), savedEntity.getDuration());
        Assertions.assertEquals(entityToSave.getPrice(), savedEntity.getPrice());
        Assertions.assertEquals(entityToSave.getCreateDate(), savedEntity.getCreateDate());
        Assertions.assertEquals(entityToSave.getLastUpdateDate(), savedEntity.getLastUpdateDate());
    }

    @Test
    public void updateGift() {
        GiftCertificateEntity entityToSave = GiftCertificateEntity.builder()
                .id(1L)
                .name("name1")
                .description("description1")
                .price(20)
                .duration(10)
                .build();

        GiftCertificateEntity savedEntity = giftDao.updateGift(entityToSave);
        Assertions.assertNotNull(savedEntity);
        Assertions.assertEquals(20, (int) giftDao.findGiftById(1L).getPrice());
    }

    @Test
    public void deleteGiftById() {
        giftDao.deleteGiftById(1L);

        Assertions.assertEquals(4, giftDao.findAllGifts().size());
    }

    @Test
    public void giftNotFoundExceptionCheck() {
        Assertions.assertThrows(GiftNotFoundException.class, () -> giftDao.findGiftById(6L));
    }
}
