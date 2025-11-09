package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.ApiResponse;
import com.example.budgetTool.model.dto.UserDto;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.service.UserService;
import com.example.budgetTool.utils.ShaUtil;
import com.example.budgetTool.utils.querydsl.FieldCondition;
import com.example.budgetTool.utils.querydsl.LogicType;
import com.example.budgetTool.utils.querydsl.Operator;
import com.example.budgetTool.utils.querydsl.SortCondition;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/Auth")
public class AuthRestController {

    private final UserService userService;

    /**
     * methodName : isEmailInUse
     * author : Jae-Hyeong Kim
     * description : check an input email exists or not
     *
     * @param input email
     * @return api response
     */
    @GetMapping("/isEmailInUse")
    public ApiResponse<Boolean> isEmailInUse (@RequestParam(required = true) String inputEmail) {

        ApiResponse<Boolean> res = new ApiResponse<>();
        try {

//            int a = 1/0;

            List<FieldCondition> fconds = new ArrayList<>();
            fconds.add(new FieldCondition("email", Operator.EQ, inputEmail, LogicType.AND));

            boolean result = this.userService.exist(fconds);

            // verification digit sending
//            String randomDigits = ((int)(Math.random() * 10000)) + "";
//            this.userService.sendEmail(inputEmail, "budgetTool verification", randomDigits);
            res = ApiResponse.SUCCESS(result);
        } catch (Exception e) {
            log.error("An error occurs when checking if email exists :", e);
            res = ApiResponse.ERROR("[error] an used email checking error occurs : " + e.getMessage());
        }

        return res;
    }


    /**
     * Processing user's logins
     * @param request
     * @param response
     * @param user
     * @return
     */
    @PostMapping("/login")
    public ApiResponse<String> loginUser(HttpServletRequest request, HttpServletResponse response
        , @RequestBody User user) {
        ApiResponse<String> res = new ApiResponse<>();

        List<FieldCondition> fconds = new ArrayList<>();
        List<SortCondition> sconds = new ArrayList<>();

        try {
            if (StringUtils.isEmpty(user.getPasswordHash()))
                throw new IllegalArgumentException("Password can't be empty");

            fconds.add(new FieldCondition("email", Operator.EQ, user.getEmail(), LogicType.AND));
//            fconds.add(new FieldCondition("passwordHash", Operator.EQ, user.getPasswordHash(), LogicType.AND));

            // getting raw ps -> hash it and comparing to ps in db.
            // 클라이언트단에서는 비번을 평문으로 받음.
            // 클라이언트단에서 평문비번이 이미 노출되었다면 클라이언트단에서 해시된 비번도 보안상 의미 없음.
            User result = this.userService.getUser(fconds, sconds);
            String currentPassword = result.getPasswordHash();

            ShaUtil shaUtil = new ShaUtil();
            String inputPassword = shaUtil.getHash(user.getPasswordHash().getBytes(), result.getSalt());
            if (!currentPassword.equals(inputPassword)) {
                return ApiResponse.ERROR("Incorrect password! Please check it again");
            }

            // generating token.


            res = ApiResponse.SUCCESS("login success");
        } catch (Exception e) {
            res = ApiResponse.ERROR("[error] an login error occurs : " + e.getMessage());
            log.error("An error occurs when checking if email exists :", e);
        }

        return res;
    }
}
