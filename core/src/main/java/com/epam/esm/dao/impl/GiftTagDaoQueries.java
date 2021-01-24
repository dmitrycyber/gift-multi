package com.epam.esm.dao.impl;

public class GiftTagDaoQueries {
    static final String FIND_ALL_GIFT_TAGS = "select * from gift_tags";
    static final String INSERT_GIFT_TAG = "insert into gift_tags (gift_id, tag_id) values (?, ?)";
}
