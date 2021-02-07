package com.epam.esm.dao.impl;

import com.epam.esm.dao.exception.TagNotFoundException;
import com.epam.esm.model.entity.TagEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;
import java.util.List;

public class TagDaoImplTest {
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private TagDaoImpl tagDao;

    @BeforeEach
    public void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .addDefaultScripts()
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(dataSource);
        tagDao = new TagDaoImpl(jdbcTemplate);
    }

    @Test
    public void testFindAllTags() {
        List<TagEntity> allTags = tagDao.findAllTags();
        Assertions.assertNotNull(allTags);
        Assertions.assertEquals(5, allTags.size());

        allTags
                .forEach(tagEntity -> {
                    Assertions.assertTrue(tagEntity.getName().contains("name"));
                    Assertions.assertNotNull(tagEntity.getId());
                });
    }

    @Test
    public void testFindTagById() {
        TagEntity expectedEntity = TagEntity.builder()
                .id(1L)
                .name("name1")
                .build();

        TagEntity actualEntity = tagDao.findTagById(1L);

        Assertions.assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void successfulFindTagByName() {
        String expectedName = "name1";

        TagEntity tagByName = tagDao.findTagByName(expectedName);

        Assertions.assertEquals(expectedName, tagByName.getName());
    }

    @Test
    public void negativeFindTagByName() {
        String expectedName = "name12";
        TagEntity tagByName = tagDao.findTagByName(expectedName);
        Assertions.assertNull(tagByName);

    }


    @Test
    public void successfulTestFindTagByPartName() {
        String expectedName = "name";

        List<TagEntity> tagByName = tagDao.findTagByPartName(expectedName);
        int size = tagByName.size();
        Assertions.assertEquals(5, size);
        tagByName
                .forEach(tagEntity -> {
                    Assertions.assertTrue(tagEntity.getName().contains(expectedName));
                });
    }

    @Test
    public void negativeTestFindTagByPartName() {
        String expectedName = "notName";

        List<TagEntity> tagByName = tagDao.findTagByPartName(expectedName);
        int size = tagByName.size();
        Assertions.assertEquals(0, size);
    }


    @Test
    public void testCreateTag() {
        String createdTagName = "testName";

        TagEntity entityToSave = TagEntity.builder()
                .name(createdTagName)
                .build();

        TagEntity savedEntity = tagDao.createTag(entityToSave);
        Assertions.assertNotNull(savedEntity);
        Assertions.assertEquals(6, tagDao.findAllTags().size());

        Assertions.assertEquals(createdTagName, savedEntity.getName());

    }

    @Test
    public void testDeleteTagById() {
        tagDao.deleteTagById(1L);

        Assertions.assertEquals(4, tagDao.findAllTags().size());
    }

    @Test
    public void giftNotFoundExceptionCheck() {
        Assertions.assertThrows(TagNotFoundException.class, () -> tagDao.findTagById(6L));
    }
}