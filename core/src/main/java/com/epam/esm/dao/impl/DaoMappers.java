package com.epam.esm.dao.impl;

import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.GiftTagEntity;
import com.epam.esm.model.entity.TagEntity;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;

public class DaoMappers {

    static final RowMapper<GiftCertificateEntity> GIFT_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return GiftCertificateEntity.builder()
                .id(resultSet.getLong(GiftDaoQueries.COLUMN_ID))
                .name(resultSet.getString(GiftDaoQueries.COLUMN_NAME))
                .description(resultSet.getString(GiftDaoQueries.COLUMN_DESCRIPTION))
                .price(resultSet.getInt(GiftDaoQueries.COLUMN_PRICE))
                .duration(resultSet.getInt(GiftDaoQueries.COLUMN_DURATION))
                .createDate(resultSet.getTimestamp(GiftDaoQueries.COLUMN_CREATE_DATE))
                .lastUpdateDate(resultSet.getTimestamp(GiftDaoQueries.COLUMN_LAST_UPDATE_DATE))
                .build();
    };

    static final RowMapper<TagEntity> TAG_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new TagEntity(
                resultSet.getLong(GiftDaoQueries.COLUMN_ID),
                resultSet.getString(GiftDaoQueries.COLUMN_NAME));
    };

    static final RowMapper<GiftTagEntity> GIFT_TAG_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new GiftTagEntity(
                resultSet.getLong("gift_id"),
                resultSet.getLong("tag_id"));
    };

    static final RowMapper<GiftCertificateEntity> GIFT_ROW_MAPPER_TEST = (ResultSet resultSet, int rowNum) -> {
        GiftCertificateEntity giftCertificateEntity = null;
        Set<TagEntity> tags = new HashSet<>();

        do {
            if (giftCertificateEntity == null) {
                giftCertificateEntity = GiftCertificateEntity.builder()
                        .id(resultSet.getLong(GiftDaoQueries.COLUMN_ID))
                        .name(resultSet.getString(GiftDaoQueries.COLUMN_NAME))
                        .description(resultSet.getString(GiftDaoQueries.COLUMN_DESCRIPTION))
                        .price(resultSet.getInt(GiftDaoQueries.COLUMN_PRICE))
                        .duration(resultSet.getInt(GiftDaoQueries.COLUMN_DURATION))
                        .createDate(resultSet.getTimestamp(GiftDaoQueries.COLUMN_CREATE_DATE))
                        .lastUpdateDate(resultSet.getTimestamp(GiftDaoQueries.COLUMN_LAST_UPDATE_DATE))
                        .build();
            }
            if (resultSet.getString("tag_name") != null) {
                tags.add(TagEntity.builder()
                        .id(resultSet.getLong("tag_id"))
                        .name(resultSet.getString("tag_name"))
                        .build());
            }
            giftCertificateEntity.setTags(tags);
        }
        while (resultSet.next());

        return giftCertificateEntity;
    };
}
