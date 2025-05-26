package net.gidosa.full.webadmin.models.dtos;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EducationQuestionDto {
    private final Long id;
    private final String title;
    private final List<String> contents;
    private final String explanation;
    private final int score;
    private final String managerName;
    private final Long answerNumber;
}
