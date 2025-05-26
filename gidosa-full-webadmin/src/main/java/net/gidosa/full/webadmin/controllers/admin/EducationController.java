package net.gidosa.full.webadmin.controllers.admin;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.gidosa.full.webadmin.models.dtos.EducationMaterialDto;
import net.gidosa.full.webadmin.models.dtos.EducationQuestionDto;
import net.gidosa.full.webadmin.services.EducationService;

@Log4j2
@Controller
@RequestMapping("/education")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    @GetMapping("/list")
    public String list(Model model) {
        List<EducationMaterialDto> materials = new ArrayList<>();
        EducationMaterialDto material = EducationMaterialDto.builder()
                .id(1L)
                .title("학습 자료 1")
                .description("학습 자료 1 설명")
                .type("PPT")
                .tag("건설")
                .duration("10")
                .writerType("admin")
                .build();
        materials.add(material);
        model.addAttribute("materials", materials);
        model.addAttribute("totalPages", 10);
        model.addAttribute("currentPage", 0);

        return "main/exam/education/list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model,
            @PathVariable("id") Long id,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
        // EducationMaterialDto material = educationService.getMaterialById(id);
        // model.addAttribute("material", material);

        List<EducationQuestionDto> questions = new ArrayList<>();
        EducationQuestionDto questionDto = EducationQuestionDto.builder()
                .id(1L)
                .title("다음 중 건설현장에서 개인 보호구 착용과 관련된 설명으로 가장 적절하지 않은 것은 무엇인가?")
                .contents(List.of("작업장 내에서는 항상 안전모를 착용해야 한다.", "귀마개는 소음이 심한 장소에서 선택적으로 착용할 수 있다.",
                        "무더운 날에는 작업 중이라도 덥다면 안전화를 벗어도 된다.", "고소작업 시 안전벨트를 반드시 착용해야 한다."))
                .explanation("건설현장은 날씨, 지형, 장비 등 다양한 위험 요인이 존재하기 때문에 기본적인 보호장비 착용은 필수입니다. \n" +
                        "특히 안전화 미착용 시, 날카로운 물체, 낙하물, 미끄럼 등으로부터 발에 중대한 부상을 입을 수 있습니다. \n" +
                        "무더운 날씨라도 보호구 착용은 생명과 직결되므로 절대 벗어서는 안 됩니다.")
                .score(10)
                .managerName("관리자")
                .answerNumber(3L)
                .build();
        questions.add(questionDto);
        model.addAttribute("educationId", id);
        model.addAttribute("questions", questions);
        model.addAttribute("totalPages", 10);
        model.addAttribute("currentPage", 0);
        return "main/exam/education/detail";
    }

}
