package net.gidosa.full.webapp.services;

import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import net.gidosa.common.models.dtos.MailHtmlSendDto;
import net.gidosa.common.models.dtos.MailTxtSendDto;
import net.gidosa.common.utils.EncodeDecodeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;


@Log4j2
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String emailSender;
    @Value("${spring.mail.sender-display}")
    private String emailSenderDisplay;

    public void sendTempPassword(String to, String username, String tempPassword) {
      MimeMessage message = mailSender.createMimeMessage();

      try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(emailSender, emailSenderDisplay));
            helper.setTo(to);
            helper.setSubject("[위험안전관리 지도] 임시 비밀번호 발급 안내");
            helper.setText(String.format("""
            안녕하세요, %s님

            요청하신 임시 비밀번호가 발급되었습니다.
            임시 비밀번호: %s

            보안을 위해 로그인 후 반드시 비밀번호를 변경해주세요.

            감사합니다.
            """, username, tempPassword));

            mailSender.send(message);
        } catch (MessagingException e) {
            System.out.println("[-] Thymeleaf 템플릿 이메일 전송 중 오류 발생: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 텍스트 기반 메일 전송
     *
     * @param mailTxtSendDto
     */
    public void sendTxtEmail(MailTxtSendDto mailTxtSendDto) {
        SimpleMailMessage smm = new SimpleMailMessage();
        smm.setTo(mailTxtSendDto.getEmailAddr());               // 받는 사람 이메일
        //smm.setFrom(emailSender);
        smm.setSubject(mailTxtSendDto.getSubject());            // 이메일 제목
        smm.setText(mailTxtSendDto.getContent());               // 이메일 내용

        try {
            mailSender.send(smm);                   // 메일 보내기
            log.info("이메일 전송 성공!");
        } catch (MailException e) {
            log.error("[-] 이메일 전송중에 오류가 발생하였습니다 " + e.getMessage());
            throw e;
        }
    }

    /**
     * html 기반 메일 전송
     *
     * @param mailHtmlSendDto
     */
    public void sendHtmlEmail(MailHtmlSendDto mailHtmlSendDto) throws IOException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("subject", mailHtmlSendDto.getSubject());
            context.setVariable("message", mailHtmlSendDto.getContent());
            if (mailHtmlSendDto.getTarget().equals("user")) {
                context.setVariable("userType", "일반 사용자");
            } else if (mailHtmlSendDto.getTarget().equals("admin")) {
                context.setVariable("userType", "관리자");
            }

            String base64Image = EncodeDecodeUtil.getBase64EncodedImage("static/assets/images/logo.png");
            context.setVariable("logoImage", base64Image);

            String htmlContent = templateEngine.process("email-template", context);
            helper.setTo(mailHtmlSendDto.getEmailAddr());
            helper.setSubject(mailHtmlSendDto.getSubject());
            helper.setText(htmlContent, true);
            helper.setFrom(emailSender);
//            helper.setFrom(FROM_USER);

            mailSender.send(message);
            log.info("Thymeleaf 템플릿 이메일 전송 성공!");
        } catch (MessagingException e) {
            log.error("[-] Thymeleaf 템플릿 이메일 전송 중 오류 발생: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
} 