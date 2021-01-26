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
        QueryArgModel queryArgModel = defineQueryAndArgs(customSearchRequest);
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
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        int rows = jdbcTemplate.update(GiftDaoQueries.UPDATE_BY_ID_QUERY,
                giftCertificateEntity.getName(),
                giftCertificateEntity.getDescription(),
                giftCertificateEntity.getPrice(),
                giftCertificateEntity.getDuration(),
                currentTimestamp,
                giftCertificateEntity.getId());

        if (rows == 0) {
            throw new GiftNotFoundException();
        }
        Set<TagEntity> tags = getTagsByGiftId(giftCertificateEntity.getId());
        giftCertificateEntity.setTags(tags);
        giftCertificateEntity.setLastUpdateDate(currentTimestamp);

        return giftCertificateEntity;
    }

    @Override
    public void deleteGiftById(Long giftId) {
        jdbcTemplate.update(GiftDaoQueries.DELETE_GIFT_BY_ID, giftId);
    }

    private Set<TagEntity> getTagsByGiftId(Long giftId) {
        List<TagEntity> queryTags = jdbcTemplate.query(GiftDaoQueries.SELECT_TAGS_BY_GIFT, DaoMappers.TAG_ROW_MAPPER, giftId);

        return new HashSet<>(queryTags);
    }

    private QueryArgModel defineQueryAndArgs(CustomSearchRequest customSearchRequest) {
        String namePrefix = customSearchRequest.getNamePrefix();
        String descriptionPrefix = customSearchRequest.getDescriptionPrefix();
        String tagNamePrefix = customSearchRequest.getTagNamePrefix();
        String sortField = customSearchRequest.getSortField();
        String sortMethod = customSearchRequest.getSortMethod();
        Object[] args = new Object[0];


        String namePrefixWithWildCard = GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD + namePrefix + GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD;
        String descriptionPrefixWithWildCard = GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD + descriptionPrefix + GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD;
        String tagNamePrefixWithWildCard = GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD + tagNamePrefix + GiftDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD;

        StringBuilder query = new StringBuilder();

        if (namePrefix != null && descriptionPrefix == null && tagNamePrefix == null) {
            query.append(GiftDaoQueries.SELECT_GIFT_BY_NAME);
            args = new Object[1];
            args[0] = namePrefixWithWildCard;
        }
        if (namePrefix == null && descriptionPrefix != null && tagNamePrefix == null) {
            query.append(GiftDaoQueries.SELECT_GIFT_BY_DESCRIPTION);
            args = new Object[1];
            args[0] = descriptionPrefixWithWildCard;
        }
        if (namePrefix == null && descriptionPrefix == null && tagNamePrefix != null) {
            query.append(GiftDaoQueries.SELECT_GIFT_BY_TAG_NAME);
            args = new Object[1];
            args[0] = tagNamePrefixWithWildCard;
        }
        if (namePrefix != null && descriptionPrefix != null && tagNamePrefix == null) {
            query.append(GiftDaoQueries.SELECT_GIFT_BY_NAME_AND_DESCRIPTION);
            args = new Object[2];
            args[0] = namePrefixWithWildCard;
            args[1] = descriptionPrefixWithWildCard;
        }
        if (namePrefix == null && descriptionPrefix != null && tagNamePrefix != null) {
            query.append(GiftDaoQueries.SELECT_GIFT_BY_TAG_NAME_AND_GIFT_DESCRIPTION);
            args = new Object[2];
            args[0] = descriptionPrefixWithWildCard;
            args[1] = tagNamePrefixWithWildCard;
        }
        if (namePrefix != null && descriptionPrefix == null && tagNamePrefix != null) {
            query.append(GiftDaoQueries.SELECT_GIFT_BY_TAG_NAME_AND_GIFT_NAME);
            args = new Object[2];
            args[0] = namePrefixWithWildCard;
            args[1] = tagNamePrefixWithWildCard;
        }
        if (namePrefix != null && descriptionPrefix != null && tagNamePrefix != null) {
            query.append(GiftDaoQueries.SELECT_GIFT_BY_TAG_NAME_AND_GIFT_NAME_AND_DESCRIPTION);
            args = new Object[3];
            args[0] = namePrefixWithWildCard;
            args[1] = descriptionPrefixWithWildCard;
            args[2] = tagNamePrefixWithWildCard;
        }

        if (sortField != null && sortMethod != null) {
            if (sortField.equals(SearchConstants.NAME_FIELD)) {
                if (sortMethod.equals(SearchConstants.ASC_METHOD_SORT)) {
                    query.append(GiftDaoQueries.SORT_BY_NAME_ASC);
                }
                if (sortMethod.equals(SearchConstants.DESC_METHOD_SORT)) {
                    query.append(GiftDaoQueries.SORT_BY_NAME_DESC);
                }
            }
            if (sortField.equals(SearchConstants.DATE_FIELD)) {
                if (sortMethod.equals(SearchConstants.ASC_METHOD_SORT)) {
                    query.append(GiftDaoQueries.SORT_BY_CREATE_DATE_ASC);
                }
                if (sortMethod.equals(SearchConstants.DESC_METHOD_SORT)) {
                    query.append(GiftDaoQueries.SORT_BY_CREATE_DATE_DESC);
                }
            }
        }
        return QueryArgModel.builder()
                .query(query.toString())
                .args(args).build();
    }

}
