package com.epam.esm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomSearchRequest implements Serializable {
    private String namePrefix;
    private String descriptionPrefix;
    private String tagNamePrefix;
    private String sortField;
    private String sortMethod;
}
