package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.model.entity.TagEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TagServiceImplTest {
    @Mock
    private TagDao tagDao;

    @InjectMocks
    private TagServiceImpl tagService;

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

        Mockito.when(tagDao.findAllTags()).thenReturn(tagEntityList);
        List<TagDto> allTags = tagService.getAllTags();

        Assertions.assertEquals(3, allTags.size());
        allTags
                .forEach(tagDto -> {
                    Assertions.assertNotNull(tagDto.getId());
                    Assertions.assertNotNull(tagDto.getName());
                    Assertions.assertTrue(tagDto.getName().contains("name"));
                });
    }

    @Test
    void getTagById() {
        long id = 1L;
        Mockito.when(tagDao.findTagById(Mockito.anyLong())).thenReturn(TagEntity.builder()
                .id(id)
                .name("name1").build());

        TagDto tagById = tagService.getTagById(id);

        Assertions.assertNotNull(tagById);
        Assertions.assertEquals("name1", tagById.getName());
    }

    @Test
    void getTagByName() {
        TagEntity tagEntity = TagEntity.builder()
                .id(1L)
                .name("name1").build();

        Mockito.when(tagDao.findTagByName(Mockito.anyString())).thenReturn(tagEntity);
        TagDto tagDto = tagService.getTagByName("name1");

        Assertions.assertEquals("name1", tagDto.getName());
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

        Mockito.when(tagDao.findTagByPartName(Mockito.anyString())).thenReturn(tagEntityList);
        List<TagDto> allTags = tagService.getTagByPartName("name");

        Assertions.assertEquals(3, allTags.size());
        Assertions.assertTrue(allTags.get(0).getName().contains("name"));
        Assertions.assertTrue(allTags.get(0).getName().contains("name"));
        Assertions.assertTrue(allTags.get(0).getName().contains("name"));
    }

    @Test
    void createTag() {
        TagEntity tagEntity = TagEntity.builder()
                .id(1L)
                .name("name").build();


        Mockito.when(tagDao.createTag(Mockito.any(TagEntity.class))).thenReturn(tagEntity);

        TagDto tagDto = tagService.createTag(TagDto.builder()
                .name("name").build());

        Assertions.assertEquals(1L, tagDto.getId());
        Assertions.assertEquals("name", tagDto.getName());
    }

    @Test
    void deleteGift(){
        Long id = 1L;
        tagService.deleteTagById(id);
        Mockito.verify(tagDao, Mockito.times(1)).deleteTagById(Mockito.anyLong());
    }
}