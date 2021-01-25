package com.epam.esm.dao;

import com.epam.esm.model.entity.GiftTagEntity;
import java.util.List;

public interface GiftTagDao {

    /**
     * Create new note which connects gift and tag
     * @param giftTagEntity
     */
    void saveGiftTag(GiftTagEntity giftTagEntity);

    /**
     * Find all Gift tag Entities
     * @return List GiftTagEntity
     */
    List<GiftTagEntity> findAllGiftTags();

    void deleteGiftTag(Long giftId, Long tagId);
}
