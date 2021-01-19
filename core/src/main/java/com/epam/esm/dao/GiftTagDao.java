package com.epam.esm.dao;


import com.epam.esm.model.entity.GiftTagEntity;

import java.util.List;

public interface GiftTagDao {
    void insertNewGiftTagEntity(GiftTagEntity giftTagEntity);
    List<GiftTagEntity> findAllGiftTagEntity();
}
