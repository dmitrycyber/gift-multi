package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftTagDao;
import com.epam.esm.model.entity.GiftTagEntity;
import com.epam.esm.model.entity.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;

@Component
public class GiftTagDaoImpl implements GiftTagDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_GIFT_TAGS_QUERY = "select * from gift_tags";
    private static final String INSERT_GIFT_TAG_QUERY = "insert into gift_tags (gift_id, tag_id) values (?, ?)";

    private static final RowMapper<GiftTagEntity> GIFT_TAG_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new GiftTagEntity(
                resultSet.getLong("gift_id"),
                resultSet.getLong("tag_id"));
    };

    @Autowired
    public GiftTagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void insertNewGiftTagEntity(GiftTagEntity giftTagEntity) {
        jdbcTemplate.update(INSERT_GIFT_TAG_QUERY, giftTagEntity.getGiftId(), giftTagEntity.getTagId());
    }

    @Override
    public List<GiftTagEntity> findAllGiftTagEntity() {
        return jdbcTemplate.query(FIND_ALL_GIFT_TAGS_QUERY, GIFT_TAG_ROW_MAPPER);
    }
}
