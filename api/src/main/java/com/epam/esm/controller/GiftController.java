package com.epam.esm.controller;

import com.epam.esm.model.CustomSearchRequest;
import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.service.GiftService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gifts")
public class GiftController {
    private final GiftService giftService;
    private final CustomSearchRequest defaultCustomSearchRequest;


    @Autowired
    public GiftController(GiftService giftService) {
        this.giftService = giftService;
        defaultCustomSearchRequest = CustomSearchRequest.builder().build();
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
        List<GiftCertificateDto> allGifts;

        CustomSearchRequest customSearchRequest = CustomSearchRequest.builder()
                .descriptionPrefix(descriptionPrefix)
                .namePrefix(namePrefix)
                .sortField(sortField)
                .sortMethod(sortMethod)
                .tagNamePrefix(tagNamePrefix).build();

        if (!customSearchRequest.equals(defaultCustomSearchRequest)){
            allGifts = giftService.searchGifts(customSearchRequest);
        }
        else {
            allGifts = giftService.getAllGifts();
        }

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
            @RequestBody GiftCertificateDto giftCertificateDto
    ) {
        GiftCertificateDto gift = giftService.createGift(giftCertificateDto);

        return ResponseEntity.ok(gift);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Api v1. Update gift")
    public ResponseEntity<GiftCertificateDto> updateGift(
            @RequestBody GiftCertificateDto giftCertificateDto,
            @PathVariable Long id

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

        return ResponseEntity.ok().build();
    }
}
