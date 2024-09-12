package com.uvg.digital.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurriculumDTO {
	
	private Long id;
    private String sectionName;
    private String lessonName;
    private String content;
    private Integer order;
    private String duration;
    private String type;
    private String resourceLink;
    private Boolean isMandatory;

}
