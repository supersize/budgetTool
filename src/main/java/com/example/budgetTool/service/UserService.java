package com.example.budgetTool.service;

import com.example.budgetTool.model.dto.UserDto;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.repository.UserRepository;
import com.example.budgetTool.utils.ShaUtil;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.LogicType;
import com.example.budgetTool.utils.querydsl.Operator;
import com.example.budgetTool.utils.querydsl.SortCondition;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
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
public class UserService implements UserDetailsService {
    private final JavaMailSender javaMailSender;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<FieldCondition> fieldConditions = new ArrayList<>();
        fieldConditions.add(new FieldCondition("email", Operator.EQ, email, LogicType.AND));

        User user = this.userRepository.getUser(fieldConditions, null);

//        return new org.springframework.security.core.userdetails.User(
//                user.getEmail()
//                , user.getPasswordHash()
//                , user.getAuthorities()
//        );
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Return User entity directly (it implements UserDetails)
        return user;
    }

    /**
     * send an e-mail
     * @param receiver
     * @param subject
     * @param body
     */
    public void sendEmail (String receiver, String subject, String body) throws MessagingException {
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

    public User getUser(List<FieldCondition> fconds, List<SortCondition> sconds) throws RuntimeException {
        if(fconds == null && sconds == null) {
            return null;
        }

        return this.userRepository.getUser(fconds, sconds);
    }


    public User addUser(User user) {
        if(user == null) return null;

        User newUser = this.userRepository.save(user);
        return newUser;
    }

    public User updateUser(User user) {
        if(user == null) return null;
        
        User updatedUser = this.userRepository.save(user);
        return updatedUser;
    }

    /**
     * Update user profile information
     * @param user User entity
     * @param updateRequest Update request DTO
     * @return Updated user
     */
    public User updateUserProfile(User user, UserDto.UpdateRequest updateRequest) {
        if (user == null || updateRequest == null) {
            throw new IllegalArgumentException("User and update request cannot be null");
        }

        // Update user fields
        user.setFirstName(updateRequest.firstName());
        user.setLastName(updateRequest.lastName());
        user.setPhoneNumber(updateRequest.phoneNumber());
        user.setDateOfBirth(updateRequest.dateOfBirth());
        user.setOccupation(updateRequest.occupation());
        user.setIncomeRange(updateRequest.incomeRange());

        return this.userRepository.save(user);
    }

    /**
     * Change user password
     * @param user User entity
     * @param currentPassword Current password
     * @param newPassword New password
     * @return Updated user
     */
    public User changePassword(User user, String currentPassword, String newPassword) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        return this.userRepository.save(user);
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
