package com.epam.esm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCertificateEntity implements Serializable {
    private Long id;
    private String name;
    private String description;
    private Integer price;
    private Integer duration;
    private Timestamp createDate;
    private Timestamp lastUpdateDate;
    private Set<TagEntity> tags;

    public GiftCertificateEntity(Long id, String name, String description, Integer price, Integer duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
    }

    public GiftCertificateEntity(Long id, String name, String description, Integer price, Integer duration, Timestamp createDate, Timestamp lastUpdateDate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
    }
}
