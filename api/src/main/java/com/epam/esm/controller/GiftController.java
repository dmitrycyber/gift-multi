package com.epam.esm.controller;

import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.dto.CreatingDto;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.service.GiftService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/gifts")
public class GiftController {
    private final GiftService giftService;
    private final CustomSearchRequest defaultCustomSearchRequest = CustomSearchRequest.builder().build();

    @Autowired
    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @GetMapping
    @ApiOperation(value = "Api v1. Get all gifts")
    public ResponseEntity<List<GiftCertificateDto>> allGifts(
            @ApiParam(name = "descriptionPrefix", value = "Search by gift description") String descriptionPrefix,
            @ApiParam(name = "namePrefix", value = "Search by gift name") String namePrefix,
            @ApiParam(name = "sortField", value = "Sort by fields \"name\" or \"date\"") String sortField,
            @ApiParam(name = "sortMethod", value = "Sort methods \"asc\" or \"desc\"") String sortMethod,
            @ApiParam(name = "tagNamePrefix", value = "Search by tag name") String tagNamePrefix
            ) {
        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .descriptionPrefix(descriptionPrefix)
                .namePrefix(namePrefix)
                .sortField(sortField)
                .sortMethod(sortMethod)
                .tagNamePrefix(tagNamePrefix)
                .build();

        List<GiftCertificateDto> allGifts = !customSearchRequest.equals(defaultCustomSearchRequest)
                ? giftService.searchGifts(customSearchRequest)
                : giftService.getAllGifts();

        return ResponseEntity.ok(allGifts);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Api v1. Get gift by id")
    public ResponseEntity<GiftCertificateDto> giftById(
            @PathVariable Long id
    ) {
        GiftCertificateDto giftById = giftService.getGiftById(id);

        return ResponseEntity.ok(giftById);
    }

    @PostMapping
    @ApiOperation(value = "Api v1. Create gift")
    public ResponseEntity<GiftCertificateDto> createGift(
            @RequestBody
            @Validated(CreatingDto.class)
            @Valid GiftCertificateDto giftCertificateDto
    ) {
        GiftCertificateDto gift = giftService.createGift(giftCertificateDto);

        return ResponseEntity.ok(gift);
    }

    @PatchMapping("/{id}")
    @ApiOperation(value = "Api v1. Update gift")
    public ResponseEntity<GiftCertificateDto> updateGift(
            @PathVariable Long id,
            @RequestBody @Valid GiftCertificateDto giftCertificateDto
    ) {
        giftCertificateDto.setId(id);

        GiftCertificateDto gift = giftService.updateGift(giftCertificateDto);

        return ResponseEntity.ok(gift);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Api v1. Delete gift by id")
    public ResponseEntity deleteGift(
            @PathVariable Long id
    ) {
        giftService.deleteGiftById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
