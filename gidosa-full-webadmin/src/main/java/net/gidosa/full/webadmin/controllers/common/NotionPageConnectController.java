package net.gidosa.full.webadmin.controllers.common;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import net.gidosa.full.webadmin.models.dtos.NotionResponseDto;

@Controller
@RequestMapping("/notion")
@RequiredArgsConstructor
public class NotionPageConnectController {

    private final String NOTION_DATABASE_ID = "20357e21c8c28028ab89cb344b230ca9";
    private final String NOTION_API_KEY = "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";

    @GetMapping("/connect")
    public String notionConnect() {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.notion.com/v1/databases/" + NOTION_DATABASE_ID + "/query";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + NOTION_API_KEY);
        headers.set("Content-Type", "application/json");
        headers.set("Notion-Version", "2022-06-28");

        ResponseEntity<NotionResponseDto> response = restTemplate
                .exchange(url, HttpMethod.POST, new HttpEntity<>(headers), NotionResponseDto.class);

        System.out.println("res: " + response.getBody());

        return "main/notion/connect";
    }
}
