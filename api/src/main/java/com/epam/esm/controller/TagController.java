package com.epam.esm.controller;

import com.epam.esm.model.ErrorResponse;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.service.TagService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    @ApiOperation(value = "Api v1. Get all tags")
    public ResponseEntity<List<TagDto>> allTags(@RequestParam(required = false) String name) {
        List<TagDto> tags;

        if (name != null){
            tags = tagService.getTagByPartName(name);
        }else {
            tags = tagService.getAllTags();
        }
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Api v1. Get tag by id")
    public ResponseEntity<TagDto> tagById(
            @PathVariable Long id
    ) {
        TagDto tagById = tagService.getTagById(id);
        return ResponseEntity.ok(tagById);
    }

    @PostMapping
    @ApiOperation(value = "Api v1. Create tag")
    public ResponseEntity<TagDto> create(
            @RequestBody TagDto tagDto
    ) {
        TagDto tag = tagService.createTag(tagDto);

        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Api v1. Delete tag by id")
    public ResponseEntity<ErrorResponse> deleteGift(
            @PathVariable Long id
    ) {
        tagService.deleteTagById(id);
        return ResponseEntity.ok().build();
    }
}
