package com.epam.esm.dao.impl;

import com.epam.esm.dao.exception.TagNotFoundException;
import com.epam.esm.model.entity.TagEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import javax.sql.DataSource;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        assertNotNull(allTags);
        assertEquals(5, allTags.size());
    }

    @Test
    public void testFindTagById() {
        TagEntity expectedEntity = TagEntity.builder()
                .id(1L)
                .name("name1").build();

        TagEntity actualEntity = tagDao.findTagById(1L);

        assertEquals(expectedEntity, actualEntity);
    }

    @Test
    public void successfulFindTagByName() {
        String expectedName = "name1";

        List<TagEntity> tagByName = tagDao.findTagByName(expectedName);
        int size = tagByName.size();
        assertEquals(1, size);
    }

    @Test
    public void negativeFindTagByName() {
        String expectedName = "name12";
        List<TagEntity> tagByName = tagDao.findTagByName(expectedName);
        int size = tagByName.size();
        assertEquals(0, size);
    }


    @Test
    public void successfulTestFindTagByPartName() {
        String expectedName = "name";

        List<TagEntity> tagByName = tagDao.findTagByPartName(expectedName);
        int size = tagByName.size();
        assertEquals(5, size);
    }

    @Test
    public void negativeTestFindTagByPartName() {
        String expectedName = "notName";

        List<TagEntity> tagByName = tagDao.findTagByPartName(expectedName);
        int size = tagByName.size();
        assertEquals(0, size);
    }


    @Test
    public void testCreateTag() {
        TagEntity entityToSave = TagEntity.builder()
                .name("testName").build();

        TagEntity savedEntity = tagDao.createTag(entityToSave);
        assertNotNull(savedEntity);
        assertEquals(6, tagDao.findAllTags().size());
    }


    @Test
    public void testDeleteTagById() {
        tagDao.deleteTagById(1L);

        assertEquals(4, tagDao.findAllTags().size());
    }

    @Test
    public void giftNotFoundExceptionCheck(){
        assertThrows(TagNotFoundException.class, () -> tagDao.findTagById(6L));
    }
}