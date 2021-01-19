package com.epam.esm.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagEntity implements Serializable {
    private Long id;
    private String name;
    private Set<GiftCertificateEntity> gifts;

    public TagEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
