package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.exception.TagNotFoundException;
import com.epam.esm.model.entity.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.util.List;

@Repository
public class TagDaoImpl implements TagDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<TagEntity> findAllTags() {
        return jdbcTemplate.query(TagDaoQueries.FIND_ALL_TAGS, DaoMappers.TAG_ROW_MAPPER);
    }

    @Override
    public TagEntity findTagById(Long tagId) {
        List<TagEntity> query = jdbcTemplate.query(TagDaoQueries.FIND_TAG_BY_ID, DaoMappers.TAG_ROW_MAPPER, tagId);

        if (query.size() != TagDaoQueries.DEFAULT_TAG_LIST_SIZE) {
            throw new TagNotFoundException(tagId.toString());
        }

        return query.get(TagDaoQueries.ZERO_ELEMENT_INDEX);
    }

    @Override
    public TagEntity findTagByName(String tagName) {
        List<TagEntity> query = jdbcTemplate.query(TagDaoQueries.SELECT_TAGS_BY_NAME, DaoMappers.TAG_ROW_MAPPER, tagName);
        return query.size() == 0
                ? null
                : query.get(0);
    }

    @Override
    public List<TagEntity> findTagByPartName(String tagName) {
        return jdbcTemplate.query(TagDaoQueries.SELECT_TAGS_BY_PART_NAME,
                DaoMappers.TAG_ROW_MAPPER,
                TagDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD + tagName + TagDaoQueries.ZERO_OR_MORE_ELEMENTS_WILDCARD);
    }

    @Override
    public TagEntity createTag(TagEntity tagEntity) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(TagDaoQueries.INSERT_TAG, new String[]{TagDaoQueries.COLUMN_ID});
                    ps.setString(1, tagEntity.getName());
                    return ps;
                },
                keyHolder);
        Long tagId = (Long) keyHolder.getKey();

        tagEntity.setId(tagId);

        return tagEntity;
    }

    @Override
    public void deleteTagById(Long tagId) {
        jdbcTemplate.update(TagDaoQueries.DELETE_TAG_BY_ID, tagId);
    }
}
