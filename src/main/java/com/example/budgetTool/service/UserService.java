package com.example.budgetTool.service;

import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.repository.UserRepository;
import com.example.budgetTool.utils.ShaUtil;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.SortCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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
@RequiredArgsConstructor
public class UserService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;


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

            this.javaMailSender.send(mailMessage);

            log.info("Email sent to " + receiver);
        } catch (Exception e) {
            log.error("[error] failed sending email", e);
        }


    }


    public boolean exist (List<FieldCondition> fconds) {
        // I used jpa cuz it's easy, simple but the trade off was jpa is not dynamic.
        // for example, if I want to find user from db, I could add method findUser, and if wanting to another data from db
        // I have to add same method but different parameter. I think same code are repeated so I decided to use queryDsl for dynamic query.

        return this.userRepository.exist(fconds);
    }

    public User getUser(List<FieldCondition> fconds, List<SortCondition> sconds) throws Exception {
        if(fconds == null && sconds == null) {
            return null;
        }

        return this.userRepository.getUser(fconds, sconds);
    }


    /*
    exist
    getUser
    getUser(condition)
    getUserList
    getUserList(condition)
    getUserPageList(condition)
    getUserPage

    addUser
    addUserList(condition)

    modifyUser
    modifyUser(condition)
    modifyUserList(condition)

    removeUser
    removeUser(condition)
    removeUser(condition)
     */
}
