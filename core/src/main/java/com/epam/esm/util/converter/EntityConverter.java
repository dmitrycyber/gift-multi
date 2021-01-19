package com.epam.esm.util.converter;

import com.epam.esm.model.dto.GiftCertificateDto;
import com.epam.esm.model.dto.TagDto;
import com.epam.esm.model.entity.GiftCertificateEntity;
import com.epam.esm.model.entity.TagEntity;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Timestamp;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityConverter {

    public static GiftCertificateDto convertGiftEntityDto(GiftCertificateEntity source) {
        Timestamp createDate = source.getCreateDate();
        Timestamp lastUpdateDate = source.getLastUpdateDate();
        Set<TagEntity> tags = source.getTags();
        Set<TagDto> tagDtoSet = null;

        if (tags != null){
            tagDtoSet = tags.stream()
                    .map(EntityConverter::convertTagEntityDto)
                    .collect(Collectors.toSet());
        }


        return GiftCertificateDto.builder()
                .id(source.getId())
                .name(source.getName())
                .description(source.getDescription())
                .price(source.getPrice())
                .duration(source.getDuration())
                .createDate(createDate == null ? null : new DateTime(createDate).toDateTime(DateTimeZone.UTC).toString())
                .lastUpdateDate(lastUpdateDate == null ? null : new DateTime(lastUpdateDate).toDateTime(DateTimeZone.UTC).toString())
                .tags(tagDtoSet).build();
    }

    public static TagDto convertTagEntityDto(TagEntity source) {

        return TagDto.builder()
                .id(source.getId())
                .name(source.getName()).build();
    }

    public static GiftCertificateEntity convertGiftDtoEntity(GiftCertificateDto source) {
        Set<TagDto> tags = source.getTags();
        Set<TagEntity> tagEntitySet = null;

        if (tags != null){
            tagEntitySet = tags.stream()
                    .map(EntityConverter::convertTagDtoEntity)
                    .collect(Collectors.toSet());
        }

        return GiftCertificateEntity.builder()
                .id(source.getId())
                .name(source.getName())
                .description(source.getDescription())
                .price(source.getPrice())
                .duration(source.getDuration())
                .createDate(new Timestamp(new DateTime(source.getCreateDate()).toDateTime().getMillis()))
                .lastUpdateDate(new Timestamp(new DateTime(source.getLastUpdateDate()).toDateTime().getMillis()))
                .tags(tagEntitySet).build();
    }

    public static TagEntity convertTagDtoEntity(TagDto source) {


        return TagEntity.builder()
                .id(source.getId())
                .name(source.getName()).build();
    }
}
