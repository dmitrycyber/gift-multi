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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class GiftDaoImpl implements GiftDao {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_GIFTS_QUERY = "select * from gift_certificate";
    private static final String FIND_GIFT_BY_ID_QUERY = "select * from gift_certificate where id = ?";



    private static final String BY_NAME = " where name like ?";
    private static final String BY_DESCRIPTION = " where description like ?";
    private static final String BY_NAME_AND_DESCRIPTION = " where name like ? and description like ?";


    private static final String SELECT_GIFT_BY_NAME = FIND_ALL_GIFTS_QUERY + BY_NAME;
    private static final String SELECT_GIFT_BY_DESCRIPTION = FIND_ALL_GIFTS_QUERY + BY_DESCRIPTION;
    private static final String SELECT_GIFT_BY_NAME_AND_DESCRIPTION = FIND_ALL_GIFTS_QUERY + BY_NAME_AND_DESCRIPTION;

    private static final String SELECT_GIFT_BY_TAG_NAME = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id and t.name like ?";

    private static final String SELECT_GIFT_BY_TAG_NAME_AND_GIFT_NAME = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id and gc.name like ? and t.name like ?";

    private static final String SELECT_GIFT_BY_TAG_NAME_AND_GIFT_DESCRIPTION = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id and gc.description like ? and t.name like ?";

    private static final String SELECT_GIFT_BY_TAG_NAME_AND_GIFT_NAME_AND_DESCRIPTION = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id and gc.name like ? and gc.description like ? and t.name like ?";


    private static final String SORT_BY_CREATE_DATE_ASC = " order by create_date asc";
    private static final String SORT_BY_CREATE_DATE_DESC = " order by create_date desc";
    private static final String SORT_BY_NAME_ASC = " order by name asc";
    private static final String SORT_BY_NAME_DESC = " order by name desc";
    private static final String DELETE_GIFT_BY_ID = "delete from gift_certificate where id = ?";
    private static final String UPDATE_BY_ID_QUERY = "UPDATE gift_certificate set name=?, description=?, price=?, duration=? WHERE id=?;";
    private static final String SELECT_TAGS_BY_GIFT = "SELECT t.id, t.name "
            + "FROM gift_tags gt, tag t "
            + "WHERE gt.tag_id = t.id and gt.gift_id=?;";
    private static final String INSERT_GIFT_QUERY = "insert into gift_certificate (name,description,price,duration,create_date,last_update_date) values (?, ?, ?, ?, ?, ?)";

    private static final String ZERO_OR_MORE_ELEMENTS_WILDCARD = "%";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_CREATE_DATE = "create_date";
    private static final String COLUMN_LAST_UPDATE_DATE = "last_update_date";




    RowMapper<GiftCertificateEntity> GIFT_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new GiftCertificateEntity(
                resultSet.getLong(COLUMN_ID),
                resultSet.getString(COLUMN_NAME),
                resultSet.getString(COLUMN_DESCRIPTION),
                resultSet.getInt(COLUMN_PRICE),
                resultSet.getInt(COLUMN_DURATION),
                resultSet.getTimestamp(COLUMN_CREATE_DATE),
                resultSet.getTimestamp(COLUMN_LAST_UPDATE_DATE));
    };

    RowMapper<TagEntity> TAG_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new TagEntity(
                resultSet.getLong(COLUMN_ID),
                resultSet.getString(COLUMN_NAME));
    };

    @Autowired
    public GiftDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<GiftCertificateEntity> findAllGifts(){
        List<GiftCertificateEntity> query = jdbcTemplate.query(FIND_ALL_GIFTS_QUERY, GIFT_ROW_MAPPER);

        for (GiftCertificateEntity giftCertificateEntity : query){
            Set<TagEntity> tags = getTagsByGiftId(giftCertificateEntity.getId());
            giftCertificateEntity.setTags(tags);
        }

        return query;
    }

    @Override
    public GiftCertificateEntity findGiftById(Long giftId){
        List<GiftCertificateEntity> query = jdbcTemplate.query(FIND_GIFT_BY_ID_QUERY, GIFT_ROW_MAPPER, giftId);

        if (query.size() != 1){
            throw new GiftNotFoundException();
        }

        return query.get(0);
    }

    @Override
    public List<GiftCertificateEntity> findAndSortGift(CustomSearchRequest customSearchRequest){
        QueryArgModel queryArgModel = defineQueryAndArgs(customSearchRequest);
        String queryString = queryArgModel.getQuery();
        Object[] args = queryArgModel.getArgs();

        List<GiftCertificateEntity> query = jdbcTemplate.query(queryString, GIFT_ROW_MAPPER, args);

        for (GiftCertificateEntity giftCertificateEntity : query){
            Set<TagEntity> tags = getTagsByGiftId(giftCertificateEntity.getId());
            giftCertificateEntity.setTags(tags);
        }
        return query;
    }

    @Override
    public GiftCertificateEntity createGift(GiftCertificateEntity entity){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_GIFT_QUERY, new String[] {COLUMN_ID});
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
    public GiftCertificateEntity updateGift(GiftCertificateEntity giftCertificateEntity){
        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        int rows = jdbcTemplate.update(UPDATE_BY_ID_QUERY,
                giftCertificateEntity.getName(),
                giftCertificateEntity.getDescription(),
                giftCertificateEntity.getPrice(),
                giftCertificateEntity.getDuration(),
                giftCertificateEntity.getId());

        if (rows == 0){
            throw new GiftNotFoundException();
        }

        giftCertificateEntity.setLastUpdateDate(currentTimestamp);

        return giftCertificateEntity;
    }

    @Override
    public void deleteGiftById(Long giftId){
        jdbcTemplate.update(DELETE_GIFT_BY_ID, giftId);
    }

    private Set<TagEntity> getTagsByGiftId(Long giftId){
        List<TagEntity> queryTags = jdbcTemplate.query(SELECT_TAGS_BY_GIFT, TAG_ROW_MAPPER, giftId);

        return new HashSet<>(queryTags);
    }

    private QueryArgModel defineQueryAndArgs(CustomSearchRequest customSearchRequest){
        String namePrefix = customSearchRequest.getNamePrefix();
        String descriptionPrefix = customSearchRequest.getDescriptionPrefix();
        String tagNamePrefix = customSearchRequest.getTagNamePrefix();
        String sortField = customSearchRequest.getSortField();
        String sortMethod = customSearchRequest.getSortMethod();
        Object[] args = new Object[0];


        String namePrefixWithWildCard = ZERO_OR_MORE_ELEMENTS_WILDCARD + namePrefix + ZERO_OR_MORE_ELEMENTS_WILDCARD;
        String descriptionPrefixWithWildCard = ZERO_OR_MORE_ELEMENTS_WILDCARD + descriptionPrefix + ZERO_OR_MORE_ELEMENTS_WILDCARD;
        String tagNamePrefixWithWildCard = ZERO_OR_MORE_ELEMENTS_WILDCARD + tagNamePrefix + ZERO_OR_MORE_ELEMENTS_WILDCARD;

        StringBuilder query = new StringBuilder();

        if (namePrefix != null && descriptionPrefix == null && tagNamePrefix == null){
            query.append(SELECT_GIFT_BY_NAME);
            args = new Object[1];
            args[0] = namePrefixWithWildCard;
        }
        if (namePrefix == null && descriptionPrefix != null && tagNamePrefix == null){
            query.append(SELECT_GIFT_BY_DESCRIPTION);
            args = new Object[1];
            args[0] = descriptionPrefixWithWildCard;
        }
        if (namePrefix == null && descriptionPrefix == null && tagNamePrefix != null){
            query.append(SELECT_GIFT_BY_TAG_NAME);
            args = new Object[1];
            args[0] = tagNamePrefixWithWildCard;
        }
        if (namePrefix != null && descriptionPrefix != null && tagNamePrefix == null){
            query.append(SELECT_GIFT_BY_NAME_AND_DESCRIPTION);
            args = new Object[2];
            args[0] = namePrefixWithWildCard;
            args[1] = descriptionPrefixWithWildCard;
        }
        if (namePrefix == null && descriptionPrefix != null && tagNamePrefix != null){
            query.append(SELECT_GIFT_BY_TAG_NAME_AND_GIFT_DESCRIPTION);
            args = new Object[2];
            args[0] = descriptionPrefixWithWildCard;
            args[1] = tagNamePrefixWithWildCard;
        }
        if (namePrefix != null && descriptionPrefix == null && tagNamePrefix != null){
            query.append(SELECT_GIFT_BY_TAG_NAME_AND_GIFT_NAME);
            args = new Object[2];
            args[0] = namePrefixWithWildCard;
            args[1] = tagNamePrefixWithWildCard;
        }
        if (namePrefix != null && descriptionPrefix != null && tagNamePrefix != null){
            query.append(SELECT_GIFT_BY_TAG_NAME_AND_GIFT_NAME_AND_DESCRIPTION);
            args = new Object[3];
            args[0] = namePrefixWithWildCard;
            args[1] = descriptionPrefixWithWildCard;
            args[2] = tagNamePrefixWithWildCard;
        }

        if (sortField != null && sortMethod != null){
            if (sortField.equals(SearchConstants.NAME_FIELD)){
                if (sortMethod.equals(SearchConstants.ASC_METHOD_SORT)){
                    query.append(SORT_BY_NAME_ASC);
                }
                if (sortMethod.equals(SearchConstants.DESC_METHOD_SORT)){
                    query.append(SORT_BY_NAME_DESC);
                }
            }
            if (sortField.equals(SearchConstants.DATE_FIELD)){
                if (sortMethod.equals(SearchConstants.ASC_METHOD_SORT)){
                    query.append(SORT_BY_CREATE_DATE_ASC);
                }
                if (sortMethod.equals(SearchConstants.DESC_METHOD_SORT)){
                    query.append(SORT_BY_CREATE_DATE_DESC);
                }
            }
        }
        return QueryArgModel.builder()
                .query(query.toString())
                .args(args).build();
    }

}
