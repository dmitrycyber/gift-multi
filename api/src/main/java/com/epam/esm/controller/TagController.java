package com.epam.esm.controller;

import com.epam.esm.model.dto.TagDto;
import com.epam.esm.service.TagService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.List;

@RestController
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

        List<TagDto> tags = name != null
                ? tagService.getTagByPartName(name)
                : tagService.getAllTags();

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
    public ResponseEntity<TagDto> createTag(
            @RequestBody @Valid TagDto tagDto
    ) {
        TagDto tag = tagService.createTag(tagDto);

        return ResponseEntity.ok(tag);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Api v1. Delete tag by id")
    public ResponseEntity deleteTag(
            @PathVariable Long id
    ) {
        tagService.deleteTagById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .build();
    }
}
