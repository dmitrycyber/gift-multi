package com.epam.esm.service;


import com.epam.esm.model.dto.TagDto;
import java.util.List;

public interface TagService {
    List<TagDto> getAllTags();
    TagDto getTagById(Long tagId);
    TagDto getTagByName(String tagName);
    List<TagDto> getTagByPartName(String tagName);
    TagDto createTag(TagDto tagDto);
    void deleteTagById(Long tagId);
}
