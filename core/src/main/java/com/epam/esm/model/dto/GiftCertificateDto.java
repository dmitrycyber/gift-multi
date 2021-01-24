package com.epam.esm.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftCertificateDto implements Serializable {
    @Null
    private Long id;

    @Size(min = 2, max = 45)
    @NotNull
    private String name;

    @Size(min = 3, max = 255)
    private String description;

    @Min(value = 1)
    @NotNull
    private Integer price;

    @Min(value = 1)
    @NotNull
    private Integer duration;

    @Null
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime createDate;

    @Null
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime lastUpdateDate;

    @Valid
    private Set<TagDto> tags;
}
