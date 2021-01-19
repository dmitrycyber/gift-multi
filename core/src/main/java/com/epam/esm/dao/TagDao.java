package com.epam.esm.dao;


import com.epam.esm.model.entity.TagEntity;

import java.util.List;

public interface TagDao {
    List<TagEntity> findAllTags();
    TagEntity findTagById(Long tagId);
    List<TagEntity> findTagByName(String tagName);
    List<TagEntity> findTagByPartName(String tagName);

    TagEntity createTag(TagEntity tagEntity);
    void deleteTagById(Long tagId);


}
