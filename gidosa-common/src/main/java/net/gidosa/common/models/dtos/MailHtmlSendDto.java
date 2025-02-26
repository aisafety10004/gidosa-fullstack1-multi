package net.gidosa.common.models.dtos;

import lombok.Getter;

/**
 * MimeMessageHelper 기반의 HTML 메일 전송
 */
@Getter
public class MailHtmlSendDto {

    private String emailAddr;                   // 수신자 이메일

    private String[] ccList;                    // 참조 수신자 목록

    private String[] bccList;                   // 숨은 참조 수신자 목록

    private String subject;                     // 이메일 제목

    private String content;                     // 이메일 내용

    private String target;                      // 이메일 대상 타겟을 지정합니다.

    public MailHtmlSendDto(String emailAddr, String[] ccList, String[] bccList, 
                          String subject, String content, String target) {
        this.emailAddr = emailAddr;
        this.ccList = ccList;
        this.bccList = bccList;
        this.subject = subject;
        this.content = content;
        this.target = target;
    }
}
