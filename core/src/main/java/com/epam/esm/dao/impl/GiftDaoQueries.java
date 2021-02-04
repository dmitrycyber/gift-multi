package com.epam.esm.dao.impl;

public class GiftDaoQueries {
    static final String FIND_ALL_GIFTS = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id ";

    static final String FIND_GIFT_BY_ID = "select * from gift_certificate where id = ?";

    static final String DELETE_GIFT_BY_ID = "delete from gift_certificate where id = ?";

    static final String SELECT_TAGS_BY_GIFT = "SELECT t.id, t.name "
            + "FROM gift_tags gt, tag t "
            + "WHERE gt.tag_id = t.id and gt.gift_id=?;";

    static final String INSERT_GIFT = "insert into gift_certificate (name,description,price,duration,create_date,last_update_date) values (?, ?, ?, ?, ?, ?)";

    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_DESCRIPTION = "description";
    static final String COLUMN_PRICE = "price";
    static final String COLUMN_DURATION = "duration";
    static final String COLUMN_CREATE_DATE = "create_date";
    static final String COLUMN_LAST_UPDATE_DATE = "last_update_date";

    static final String ZERO_OR_MORE_ELEMENTS_WILDCARD = "%";
}
