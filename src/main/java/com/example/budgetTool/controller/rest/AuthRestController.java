package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.ApiResponse;
import com.example.budgetTool.model.dto.UserDto;
import com.example.budgetTool.model.entity.User;
import com.example.budgetTool.service.UserService;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


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
//    public ApiResponse isEmailInUse (@RequestParam String inputEmail) {
    public ApiResponse<String> isEmailInUse (@RequestParam(required = false) String inputEmail) {

        ApiResponse<String> res = new ApiResponse<>();
        try {
            String randomDigits = ((int)(Math.random() * 10000)) + "";
            this.userService.sendEmail(inputEmail, "budgetTool verification", randomDigits);
            res = ApiResponse.SUCCESS(randomDigits);
        } catch (Exception e) {
            log.error("An error occurs when findding email exists : {}", e);
            res = ApiResponse.ERROR(e.getMessage());
        }

        return res;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    class Test01 {
        String sentence;
    }


}
