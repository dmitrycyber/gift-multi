package com.epam.esm.util.converter;

import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.TagEntity;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityConverter {

    public static GiftCertificateDto convertGiftEntityToDto(GiftCertificateEntity source) {
        Set<TagEntity> tags = source.getTags();
        Set<TagDto> tagDtoSet = null;

        if (tags != null){
            tagDtoSet = tags.stream()
                    .map(EntityConverter::convertTagEntityToDto)
                    .collect(Collectors.toSet());
        }

        Timestamp createDate = source.getCreateDate();
        Timestamp lastUpdateDate = source.getLastUpdateDate();

        return GiftCertificateDto.builder()
                .id(source.getId())
                .name(source.getName())
                .description(source.getDescription())
                .price(source.getPrice())
                .duration(source.getDuration())
                .createDate(createDate == null ? null : createDate.toLocalDateTime())
                .lastUpdateDate(lastUpdateDate == null ? null : lastUpdateDate.toLocalDateTime())
                .tags(tagDtoSet)
                .build();
    }

    public static TagDto convertTagEntityToDto(TagEntity source) {
        return TagDto.builder()
                .id(source.getId())
                .name(source.getName())
                .build();
    }

    public static GiftCertificateEntity convertGiftDtoToEntity(GiftCertificateDto source) {
        Set<TagDto> tags = source.getTags();
        Set<TagEntity> tagEntitySet = null;

        if (tags != null){
            tagEntitySet = tags.stream()
                    .map(EntityConverter::convertTagDtoToEntity)
                    .collect(Collectors.toSet());
        }
        LocalDateTime createDate = source.getCreateDate();
        LocalDateTime lastUpdateDate = source.getLastUpdateDate();
        return GiftCertificateEntity.builder()
                .id(source.getId())
                .name(source.getName())
                .description(source.getDescription())
                .price(source.getPrice())
                .duration(source.getDuration())
                .createDate(createDate==null ? null : Timestamp.valueOf(createDate))
                .createDate(lastUpdateDate==null ? null : Timestamp.valueOf(lastUpdateDate))
                .tags(tagEntitySet)
                .build();
    }

    public static TagEntity convertTagDtoToEntity(TagDto source) {
        return TagEntity.builder()
                .id(source.getId())
                .name(source.getName())
                .build();
    }
}
