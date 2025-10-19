package com.example.budgetTool.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * packageName    : com.example.budgetTool.service
 * author         : kimjaehyeong
 * date           : 9/23/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 9/23/25        kimjaehyeong       created
 */
@Slf4j
@Service
@Transactional
public class UserService {
    private MailSender mailSender;


    /**
     * send an e-mail
     * @param receiver
     * @param subject
     * @param body
     */
    public void sendEmail (String receiver, String subject, String body) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(receiver);
        mailMessage.setSubject(subject);
        mailMessage.setText("Verification Code : " + body);

        try {

            this.mailSender.send(mailMessage);
        } catch (Exception e) {
            log.error("[error] failed sending email", e);
        }


    }

}
