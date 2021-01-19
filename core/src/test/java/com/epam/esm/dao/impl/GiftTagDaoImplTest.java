package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.model.entity.GiftTagEntity;
import com.epam.esm.model.entity.TagEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GiftTagDaoImplTest {
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    private GiftTagDao giftTagDao;

    @BeforeEach
    public void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .addDefaultScripts()
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(dataSource);
        giftTagDao = new GiftTagDaoImpl(jdbcTemplate);
    }

    @Test
    public void findAllGiftTags() {
        List<GiftTagEntity> allGiftTagEntity = giftTagDao.findAllGiftTagEntity();

        assertEquals(5, allGiftTagEntity.size());
    }

    @Test
    public void testCreateGiftTag() {
        GiftTagEntity entityToSave = GiftTagEntity.builder()
                .giftId(1L)
                .tagId(5L).build();

        giftTagDao.insertNewGiftTagEntity(entityToSave);

        List<GiftTagEntity> allGiftTagEntity = giftTagDao.findAllGiftTagEntity();

        assertEquals(6, allGiftTagEntity.size());
    }

}