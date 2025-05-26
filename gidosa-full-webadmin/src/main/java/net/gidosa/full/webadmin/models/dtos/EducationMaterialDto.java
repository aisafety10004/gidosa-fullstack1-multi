package net.gidosa.full.webadmin.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EducationMaterialDto {
    private Long id;
    private String title;
    private String description;
    private String type;
    private String tag;
    private String duration;
    private String writerType;
}