package com.epam.esm.dao.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.exception.TagNotFoundException;
import com.epam.esm.model.entity.TagEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Component
public class TagDaoImpl implements TagDao {
    private final JdbcTemplate jdbcTemplate;


    private static final RowMapper<TagEntity> TAG_ROW_MAPPER = (ResultSet resultSet, int rowNum) -> {
        return new TagEntity(
                resultSet.getLong("id"),
                resultSet.getString("name"));
    };

    private static final String COLUMN_ID = "id";

    private static final int ZERO_INDEX = 0;

    private static final String FIND_ALL_TAGS_QUERY = "select * from tag";
    private static final String FIND_TAG_BY_ID_QUERY = "select * from tag where id = ?";
    private static final String DELETE_TAG_BY_ID = "delete from tag where id = ?";
    private static final String INSERT_TAG_QUERY = "insert into tag (name) values (?)";
    private static final String SELECT_TAGS_BY_NAME = "SELECT * FROM tag WHERE name = ?";
    private static final String SELECT_TAGS_BY_PART_NAME = "SELECT * FROM tag WHERE name like ?";

    private static final String ZERO_OR_MORE_ELEMENTS_WILDCARD = "%";
    private static final Integer DEFAULT_TAG_LIST_SIZE = 1;
    private static final Integer ZERO_ELEMENT_INDEX = 0;


    @Autowired
    public TagDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public List<TagEntity> findAllTags(){
        return jdbcTemplate.query(FIND_ALL_TAGS_QUERY, TAG_ROW_MAPPER);
    }

    @Override
    public TagEntity findTagById(Long tagId){
        List<TagEntity> query = jdbcTemplate.query(FIND_TAG_BY_ID_QUERY, TAG_ROW_MAPPER, tagId);

        if (query.size() != DEFAULT_TAG_LIST_SIZE) {
            throw new TagNotFoundException();
        }

        return query.get(ZERO_ELEMENT_INDEX);
    }

    @Override
    public List<TagEntity> findTagByName(String tagName){
        return jdbcTemplate.query(SELECT_TAGS_BY_NAME, TAG_ROW_MAPPER, tagName);
    }

    @Override
    public List<TagEntity> findTagByPartName(String tagName){
        return jdbcTemplate.query(SELECT_TAGS_BY_PART_NAME,
                TAG_ROW_MAPPER,
                ZERO_OR_MORE_ELEMENTS_WILDCARD + tagName + ZERO_OR_MORE_ELEMENTS_WILDCARD);
    }

    @Override
    public TagEntity createTag(TagEntity tagEntity){
        List<TagEntity> tagByPartName = findTagByName(tagEntity.getName());

        if (!tagByPartName.isEmpty()) {
            return tagByPartName.get(ZERO_INDEX);
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_TAG_QUERY, new String[]{COLUMN_ID});
                    ps.setString(1, tagEntity.getName());
                    return ps;
                },
                keyHolder);


        Long tagId = (Long) keyHolder.getKey();

        tagEntity.setId(tagId);

        return tagEntity;
    }

    @Override
    public void deleteTagById(Long tagId){
        jdbcTemplate.update(DELETE_TAG_BY_ID, tagId);
    }
}
