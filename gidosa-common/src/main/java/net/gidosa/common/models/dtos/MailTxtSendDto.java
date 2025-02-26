package net.gidosa.common.models.dtos;

import lombok.Builder;
import lombok.Getter;

/**
 * SimpleMailMessage 기반 텍스트 메일 전송 DTO
 */
@Getter
public class MailTxtSendDto {

    private String emailAddr;                   // 수신자 이메일

    private String[] ccList;                    // 참조 수신자 목록

    private String[] bccList;                   // 숨은 참조 수신자 목록

    private String subject;                     // 이메일 제목

    private String content;                     // 이메일 내용

    @Builder
    public MailTxtSendDto(String emailAddr, String[] ccList, String[] bccList, String subject, String content) {
        this.emailAddr = emailAddr;
        this.ccList = ccList;
        this.bccList = bccList;
        this.subject = subject;
        this.content = content;
    }
}
