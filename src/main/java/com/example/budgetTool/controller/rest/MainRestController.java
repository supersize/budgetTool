package com.example.budgetTool.controller.rest;

import com.example.budgetTool.model.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * packageName    : com.example.budgetTool.controller.rest
 * author         : kimjaehyeong
 * date           : 11/9/25
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 11/9/25        kimjaehyeong       created
 */
@RestController
@Slf4j
@RequiredArgsConstructor
public class MainRestController {

    @GetMapping("/logout")
    public ApiResponse<String> logout () {
        ApiResponse<String> res = new ApiResponse<>();


        return res;
    }
}
