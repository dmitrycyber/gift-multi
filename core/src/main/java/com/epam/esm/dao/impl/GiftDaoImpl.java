package com.epam.esm.dao.impl;

import com.epam.esm.dao.GiftDao;
import com.epam.esm.dao.exception.GiftNotFoundException;
import com.epam.esm.dao.model.QueryArgModel;
import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.TagEntity;
import com.epam.esm.util.SearchConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class GiftDaoImpl implements GiftDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GiftDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<GiftCertificateEntity> findAllGifts() {
        List<GiftCertificateEntity> query = jdbcTemplate.query(GiftDaoQueries.FIND_ALL_GIFTS, DaoMappers.GIFT_ROW_MAPPER);

        for (GiftCertificateEntity giftCertificateEntity : query) {
            Set<TagEntity> tags = getTagsByGiftId(giftCertificateEntity.getId());
            giftCertificateEntity.setTags(tags);
        }
        return query;
    }

    @Override
    public GiftCertificateEntity findGiftById(Long giftId) {
        List<GiftCertificateEntity> query = jdbcTemplate.query(GiftDaoQueries.FIND_GIFT_BY_ID, DaoMappers.GIFT_ROW_MAPPER, giftId);

        if (query.size() != 1) {
            throw new GiftNotFoundException();
        }

        GiftCertificateEntity giftCertificateEntity = query.get(0);
        Set<TagEntity> tags = getTagsByGiftId(giftCertificateEntity.getId());
        giftCertificateEntity.setTags(tags);

        return giftCertificateEntity;
    }

    @Override
    public List<GiftCertificateEntity> findAndSortGifts(CustomSearchRequest customSearchRequest) {
        QueryArgModel queryArgModel = defineSearchQueryAndArgs(customSearchRequest);
        String queryString = queryArgModel.getQuery();

        Object[] args = queryArgModel.getArgs();

        List<GiftCertificateEntity> query = jdbcTemplate.query(queryString, DaoMappers.GIFT_ROW_MAPPER, args);

        for (GiftCertificateEntity giftCertificateEntity : query) {
            Set<TagEntity> tags = getTagsByGiftId(giftCertificateEntity.getId());
            giftCertificateEntity.setTags(tags);
        }
        return query;
    }

    @Override
    public GiftCertificateEntity createGift(GiftCertificateEntity entity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(GiftDaoQueries.INSERT_GIFT, new String[]{GiftDaoQueries.COLUMN_ID});
                    ps.setString(1, entity.getName());
                    ps.setString(2, entity.getDescription());
                    ps.setInt(3, entity.getPrice());
                    ps.setInt(4, entity.getDuration());
                    ps.setTimestamp(5, currentTimestamp);
                    ps.setTimestamp(6, currentTimestamp);

                    return ps;
                },
                keyHolder);
        Long giftId = (Long) keyHolder.getKey();

        entity.setId(giftId);
        entity.setCreateDate(currentTimestamp);
        entity.setLastUpdateDate(currentTimestamp);

        return entity;
    }

    @Override
    public GiftCertificateEntity updateGift(GiftCertificateEntity giftCertificateEntity) {
        QueryArgModel queryArgModel = defineUpdateQueryAndArgs(giftCertificateEntity);
        String queryString = queryArgModel.getQuery();
        Object[] args = queryArgModel.getArgs();

        jdbcTemplate.update(queryString, args);

        return findGiftById(giftCertificateEntity.getId());
    }

    private QueryArgModel defineUpdateQueryAndArgs(GiftCertificateEntity giftCertificateEntity) {
        String name = giftCertificateEntity.getName();
        String description = giftCertificateEntity.getDescription();
        Integer duration = giftCertificateEntity.getDuration();
        Integer price = giftCertificateEntity.getPrice();
        Long id = giftCertificateEntity.getId();
        List<Object> argList = new ArrayList<>();

        StringBuilder updateGift = new StringBuilder("UPDATE gift_certificate SET ");
        String nameValueRegex = "=?, ";
        String whereConditionId = "WHERE id=?;";

        argList.add(new Timestamp(System.currentTimeMillis()));
        updateGift.append(GiftDaoQueries.COLUMN_LAST_UPDATE_DATE).append(nameValueRegex);
        if (name != null) {
            argList.add(name);
            updateGift.append(GiftDaoQueries.COLUMN_NAME).append(nameValueRegex);
        }
        if (description != null) {
            argList.add(description);
            updateGift.append(GiftDaoQueries.COLUMN_DESCRIPTION).append(nameValueRegex);
        }
        if (price != null) {
            argList.add(price);
            updateGift.append(GiftDaoQueries.COLUMN_PRICE).append(nameValueRegex);
        }
        if (duration != null) {
            argList.add(duration);
            updateGift.append(GiftDaoQueries.COLUMN_DURATION).append(nameValueRegex);
        }
        updateGift.append(whereConditionId);
        argList.add(id);
        Object[] argArray = argList.toArray();

        String query = updateGift.toString().replaceAll(", WHERE", " WHERE");
        return QueryArgModel.builder()
                .query(query)
                .args(argArray)
                .build();
    }

    @Override
    public void deleteGiftById(Long giftId) {
        jdbcTemplate.update(GiftDaoQueries.DELETE_GIFT_BY_ID, giftId);
    }

    private Set<TagEntity> getTagsByGiftId(Long giftId) {
        List<TagEntity> queryTags = jdbcTemplate.query(GiftDaoQueries.SELECT_TAGS_BY_GIFT, DaoMappers.TAG_ROW_MAPPER, giftId);

        return new HashSet<>(queryTags);
    }

    private QueryArgModel defineSearchQueryAndArgs(CustomSearchRequest customSearchRequest) {
        String namePrefix = customSearchRequest.getNamePrefix();
        String descriptionPrefix = customSearchRequest.getDescriptionPrefix();
        String tagNamePrefix = customSearchRequest.getTagNamePrefix();
        String sortField = customSearchRequest.getSortField();
        String sortMethod = customSearchRequest.getSortMethod();

        StringBuilder queryBuilder = new StringBuilder(GiftDaoQueries.FIND_ALL_GIFTS);
        String propertiesRegex = " and ";
        String likeRegex = " like ? ";
        String tagTableName = "t.";
        String giftTableName = "gc.";
        String orderRegex = " order by ";
        String ascOrder = " asc";
        String descOrder = " desc";
        List<Object> argList = new ArrayList<>();

        String namePrefixWithWildCard = GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD + namePrefix + GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD;
        String descriptionPrefixWithWildCard = GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD + descriptionPrefix + GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD;
        String tagNamePrefixWithWildCard = GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD + tagNamePrefix + GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD;

        if (namePrefix != null && descriptionPrefix == null && tagNamePrefix == null) {
            argList.add(namePrefixWithWildCard);
            queryBuilder
                    .append(propertiesRegex)
                    .append(giftTableName)
                    .append(GiftDaoQueries.COLUMN_NAME)
                    .append(likeRegex);
        }
        if (namePrefix == null && descriptionPrefix != null && tagNamePrefix == null) {
            argList.add(descriptionPrefixWithWildCard);
            queryBuilder
                    .append(propertiesRegex)
                    .append(giftTableName)
                    .append(GiftDaoQueries.COLUMN_DESCRIPTION)
                    .append(likeRegex);
        }
        if (namePrefix == null && descriptionPrefix == null && tagNamePrefix != null) {
            argList.add(tagNamePrefixWithWildCard);
            queryBuilder
                    .append(propertiesRegex)
                    .append(tagTableName)
                    .append(TagDaoQueries.COLUMN_NAME)
                    .append(likeRegex);
        }
        if (namePrefix != null && descriptionPrefix != null && tagNamePrefix == null) {
            argList.add(namePrefixWithWildCard);
            argList.add(descriptionPrefixWithWildCard);
            queryBuilder
                    .append(propertiesRegex)
                    .append(giftTableName)
                    .append(GiftDaoQueries.COLUMN_NAME)
                    .append(likeRegex)
                    .append(propertiesRegex)
                    .append(giftTableName)
                    .append(GiftDaoQueries.COLUMN_DESCRIPTION)
                    .append(likeRegex);
        }
        if (namePrefix == null && descriptionPrefix != null && tagNamePrefix != null) {
            argList.add(descriptionPrefixWithWildCard);
            argList.add(tagNamePrefixWithWildCard);
            queryBuilder
                    .append(propertiesRegex)
                    .append(giftTableName)
                    .append(GiftDaoQueries.COLUMN_DESCRIPTION)
                    .append(likeRegex)
                    .append(propertiesRegex)
                    .append(tagTableName)
                    .append(TagDaoQueries.COLUMN_NAME)
                    .append(likeRegex);
        }
        if (namePrefix != null && descriptionPrefix == null && tagNamePrefix != null) {
            argList.add(namePrefixWithWildCard);
            argList.add(tagNamePrefixWithWildCard);
            queryBuilder
                    .append(propertiesRegex)
                    .append(giftTableName)
                    .append(GiftDaoQueries.COLUMN_NAME)
                    .append(likeRegex)
                    .append(propertiesRegex)
                    .append(tagTableName)
                    .append(TagDaoQueries.COLUMN_NAME)
                    .append(likeRegex);
        }
        if (namePrefix != null && descriptionPrefix != null && tagNamePrefix != null) {
            argList.add(namePrefixWithWildCard);
            argList.add(descriptionPrefixWithWildCard);
            argList.add(tagNamePrefixWithWildCard);
            queryBuilder
                    .append(propertiesRegex)
                    .append(giftTableName)
                    .append(GiftDaoQueries.COLUMN_NAME)
                    .append(likeRegex)
                    .append(propertiesRegex)
                    .append(giftTableName)
                    .append(GiftDaoQueries.COLUMN_DESCRIPTION)
                    .append(likeRegex)
                    .append(propertiesRegex)
                    .append(tagTableName)
                    .append(TagDaoQueries.COLUMN_NAME)
                    .append(likeRegex);
        }

        if (sortField != null && sortMethod != null) {
            if (sortField.equals(SearchConstants.NAME_FIELD)) {
                if (sortMethod.equals(SearchConstants.ASC_METHOD_SORT)) {
                    queryBuilder
                            .append(orderRegex)
                            .append(GiftDaoQueries.COLUMN_NAME)
                            .append(ascOrder);
                }
                if (sortMethod.equals(SearchConstants.DESC_METHOD_SORT)) {
                    queryBuilder
                            .append(orderRegex)
                            .append(GiftDaoQueries.COLUMN_NAME)
                            .append(descOrder);
                }
            }
            if (sortField.equals(SearchConstants.DATE_FIELD)) {
                if (sortMethod.equals(SearchConstants.ASC_METHOD_SORT)) {
                    queryBuilder
                            .append(orderRegex)
                            .append(GiftDaoQueries.COLUMN_CREATE_DATE)
                            .append(ascOrder);
                }
                if (sortMethod.equals(SearchConstants.DESC_METHOD_SORT)) {
                    queryBuilder
                            .append(orderRegex)
                            .append(GiftDaoQueries.COLUMN_CREATE_DATE)
                            .append(descOrder);
                }
            }
        }
        return QueryArgModel.builder()
                .query(queryBuilder.toString())
                .args(argList.toArray()).build();
    }
}
