package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.model.entity.GiftTagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GiftTagDaoImpl implements GiftTagDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String INSERT_GIFT_TAG_QUERY = "insert into gift_tags (gift_id, tag_id) values (?, ?)";

    @Autowired
    public GiftTagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertNewGiftTagEntity(GiftTagEntity giftTagEntity) {
        jdbcTemplate.update(INSERT_GIFT_TAG_QUERY, giftTagEntity.getGiftId(), giftTagEntity.getTagId());
    }
}
