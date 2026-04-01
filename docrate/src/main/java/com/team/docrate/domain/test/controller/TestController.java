package com.team.docrate.domain.test.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @GetMapping("/secure-test")
    @ResponseBody
    public String secureTest() {
        return "인증된 사용자만 접근 가능한 페이지입니다.";
    }
}
