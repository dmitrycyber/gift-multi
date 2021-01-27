package com.epam.esm.dao.impl;

public class GiftDaoQueries {
    static final String FIND_ALL_GIFTS = "select * from gift_certificate";
    static final String FIND_ALL_GIFTS_TEST = "select gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date, t.id as tag_id, t.name as tag_name " +
            "from gift_certificate gc " +
            "full join gift_tags gt on gc.id = gt.gift_id " +
            "full join tag t on gt.tag_id=t.id";

    static final String FIND_GIFT_BY_ID = "select * from gift_certificate where id = ?";
    static final String FIND_GIFT_BY_ID_TEST = FIND_ALL_GIFTS_TEST + " where gc.id = ?";

    static final String PARTIAL_UPDATE_GIFT = "UPDATE table_name " +
            "SET column1 = value1, column2 = value2 " +
            "WHERE condition;";


    static final String BY_NAME = " where name like ?";
    static final String BY_DESCRIPTION = " where description like ?";
    static final String BY_NAME_AND_DESCRIPTION = " where name like ? and description like ?";
    static final String SELECT_GIFT_BY_NAME = FIND_ALL_GIFTS + BY_NAME;
    static final String SELECT_GIFT_BY_DESCRIPTION = FIND_ALL_GIFTS + BY_DESCRIPTION;
    static final String SELECT_GIFT_BY_NAME_AND_DESCRIPTION = FIND_ALL_GIFTS + BY_NAME_AND_DESCRIPTION;

    static final String SELECT_GIFT_BY_TAG_NAME = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id and t.name like ?";

    static final String SELECT_GIFT_BY_TAG_NAME_AND_GIFT_NAME = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id and gc.name like ? and t.name like ?";

    static final String SELECT_GIFT_BY_TAG_NAME_AND_GIFT_DESCRIPTION = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id and gc.description like ? and t.name like ?";

    static final String SELECT_GIFT_BY_TAG_NAME_AND_GIFT_NAME_AND_DESCRIPTION = "SELECT gc.id, gc.name, gc.description, gc.price, gc.duration, gc.create_date, gc.last_update_date "
            + "FROM gift_certificate gc, gift_tags gt, tag t "
            + "WHERE gc.id = gt.gift_id and gt.tag_id = t.id and gc.name like ? and gc.description like ? and t.name like ?";

    static final String SORT_BY_CREATE_DATE_ASC = " order by create_date asc";
    static final String SORT_BY_CREATE_DATE_DESC = " order by create_date desc";
    static final String SORT_BY_NAME_ASC = " order by name asc";
    static final String SORT_BY_NAME_DESC = " order by name desc";
    static final String DELETE_GIFT_BY_ID = "delete from gift_certificate where id = ?";
    static final String UPDATE_BY_ID_QUERY = "UPDATE gift_certificate set name=?, description=?, price=?, duration=?, last_update_date=? WHERE id=?;";
    static final String SELECT_TAGS_BY_GIFT = "SELECT t.id, t.name "
            + "FROM gift_tags gt, tag t "
            + "WHERE gt.tag_id = t.id and gt.gift_id=?;";
    static final String INSERT_GIFT = "insert into gift_certificate (name,description,price,duration,create_date,last_update_date) values (?, ?, ?, ?, ?, ?)";
    static final String ZERO_OR_MORE_ELEMENTS_WILDCARD = "%";
    static final String COLUMN_ID = "id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_DESCRIPTION = "description";
    static final String COLUMN_PRICE = "price";
    static final String COLUMN_DURATION = "duration";
    static final String COLUMN_CREATE_DATE = "create_date";
    static final String COLUMN_LAST_UPDATE_DATE = "last_update_date";
}
