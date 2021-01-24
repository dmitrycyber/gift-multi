package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.model.entity.GiftTagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class GiftTagDaoImpl implements GiftTagDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GiftTagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void saveGiftTag(GiftTagEntity giftTagEntity) {
        jdbcTemplate.update(GiftTagDaoQueries.INSERT_GIFT_TAG, giftTagEntity.getGiftId(), giftTagEntity.getTagId());
    }

    @Override
    public List<GiftTagEntity> findAllGiftTags() {
        return jdbcTemplate.query(GiftTagDaoQueries.FIND_ALL_GIFT_TAGS, DaoMappers.GIFT_TAG_ROW_MAPPER);
    }
}
