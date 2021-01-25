package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.exception.TagNameRegisteredException;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.model.entity.TagEntity;
import com.epam.esm.service.TagService;
import com.epam.esm.util.converter.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    @Transactional
    public List<TagDto> getAllTags() {
        List<TagEntity> allTags = tagDao.findAllTags();

        return allTags.stream()
                .map(EntityConverter::convertTagEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TagDto getTagById(Long tagId) {
        return EntityConverter.convertTagEntityToDto(tagDao.findTagById(tagId));
    }

    @Override
    @Transactional
    public TagDto getTagByName(String tagName) {
        TagEntity tagByName = tagDao.findTagByName(tagName);

        return EntityConverter.convertTagEntityToDto(tagByName);
    }

    @Override
    @Transactional
    public List<TagDto> getTagByPartName(String tagName){
        List<TagEntity> tagByName = tagDao.findTagByPartName(tagName);

        return tagByName.stream()
                .map(EntityConverter::convertTagEntityToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TagDto createTag(TagDto tagDto){
        TagEntity tagEntity = EntityConverter.convertTagDtoToEntity(tagDto);

        TagEntity tagByName = tagDao.findTagByName(tagEntity.getName());

        if (tagByName != null) {
            throw new TagNameRegisteredException();
        }
        return EntityConverter.convertTagEntityToDto(tagDao.createTag(tagEntity));
    }

    @Override
    public void deleteTagById(Long tagId){
        tagDao.deleteTagById(tagId);
    }
}
