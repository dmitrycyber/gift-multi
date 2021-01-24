package com.epam.esm.dao.impl;

public class TagDaoQueries {
    static final String COLUMN_ID = "id";
    static final int ZERO_INDEX = 0;
    static final String FIND_ALL_TAGS = "select * from tag";
    static final String FIND_TAG_BY_ID = "select * from tag where id = ?";
    static final String DELETE_TAG_BY_ID = "delete from tag where id = ?";
    static final String INSERT_TAG = "insert into tag (name) values (?)";
    static final String SELECT_TAGS_BY_NAME = "SELECT * FROM tag WHERE name = ?";
    static final String SELECT_TAGS_BY_PART_NAME = "SELECT * FROM tag WHERE name like ?";
    static final String ZERO_OR_MORE_ELEMENTS_WILDCARD = "%";
    static final Integer DEFAULT_TAG_LIST_SIZE = 1;
    static final Integer ZERO_ELEMENT_INDEX = 0;
}
