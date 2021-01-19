package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.dao.impl.TagDaoImpl;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.model.entity.TagEntity;
import com.epam.esm.service.TagService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {
    private static TagDao tagDao;
    private static TagService tagService;

    @BeforeAll
    static void setup(){
        tagDao = mock(TagDaoImpl.class);
        tagService = new TagServiceImpl(tagDao);
    }

    @Test
    void getAllTags() {
        List<TagEntity> tagEntityList = new ArrayList<>();
        tagEntityList.add(TagEntity.builder()
                .id(1L)
                .name("name1").build());
        tagEntityList.add(TagEntity.builder()
                .id(2L)
                .name("name2").build());
        tagEntityList.add(TagEntity.builder()
                .id(3L)
                .name("name3").build());

        when(tagDao.findAllTags()).thenReturn(tagEntityList);
        List<TagDto> allTags = tagService.getAllTags();

        assertEquals(3, allTags.size());
    }

    @Test
    void getTagById() {
        long id = 1L;
        when(tagDao.findTagById(anyLong())).thenReturn(TagEntity.builder()
                .id(id)
                .name("name1").build());

        TagDto tagById = tagService.getTagById(id);

        assertEquals("name1", tagById.getName());
    }

    @Test
    void getTagByName() {
        List<TagEntity> tagEntityList = new ArrayList<>();
        tagEntityList.add(TagEntity.builder()
                .id(1L)
                .name("name1").build());

        when(tagDao.findTagByName(anyString())).thenReturn(tagEntityList);
        List<TagDto> allTags = tagService.getTagByName("name1");

        assertEquals(1, allTags.size());
        assertEquals("name1", allTags.get(0).getName());
    }

    @Test
    void getTagByPartName() {
        List<TagEntity> tagEntityList = new ArrayList<>();
        tagEntityList.add(TagEntity.builder()
                .id(1L)
                .name("name1").build());
        tagEntityList.add(TagEntity.builder()
                .id(2L)
                .name("name2").build());
        tagEntityList.add(TagEntity.builder()
                .id(3L)
                .name("name3").build());

        when(tagDao.findTagByPartName(anyString())).thenReturn(tagEntityList);
        List<TagDto> allTags = tagService.getTagByPartName("name");

        assertEquals(3, allTags.size());
        assertTrue(allTags.get(0).getName().contains("name"));
        assertTrue(allTags.get(0).getName().contains("name"));
        assertTrue(allTags.get(0).getName().contains("name"));
    }

    @Test
    void createTag() {
        TagEntity tagEntity = TagEntity.builder()
                .id(1L)
                .name("name").build();


        when(tagDao.createTag(any(TagEntity.class))).thenReturn(tagEntity);

        TagDto tagDto = tagService.createTag(TagDto.builder()
                .name("name").build());

        assertEquals(1L, tagDto.getId());
        assertEquals("name", tagDto.getName());

    }
}