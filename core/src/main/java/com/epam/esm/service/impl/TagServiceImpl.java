package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.model.entity.TagEntity;
import com.epam.esm.service.TagService;
import com.epam.esm.util.converter.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public List<TagDto> getAllTags() {
        List<TagEntity> allTags = tagDao.findAllTags();

        return allTags.stream()
                .map(EntityConverter::convertTagEntityDto)
                .collect(Collectors.toList());
    }

    @Override
    public TagDto getTagById(Long tagId) {
        return EntityConverter.convertTagEntityDto(tagDao.findTagById(tagId));
    }

    @Override
    public List<TagDto> getTagByName(String tagName) {
        List<TagEntity> tagByName = tagDao.findTagByName(tagName);

        return tagByName.stream()
                .map(EntityConverter::convertTagEntityDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TagDto> getTagByPartName(String tagName){
        List<TagEntity> tagByName = tagDao.findTagByPartName(tagName);

        return tagByName.stream()
                .map(EntityConverter::convertTagEntityDto)
                .collect(Collectors.toList());
    }

    @Override
    public TagDto createTag(TagDto tagDto){
        TagEntity tagEntity = EntityConverter.convertTagDtoEntity(tagDto);

        return EntityConverter.convertTagEntityDto(tagDao.createTag(tagEntity));
    }

    @Override
    public void deleteTagById(Long tagId){
        tagDao.deleteTagById(tagId);
    }


}
