package com.team.docrate.domain.test.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/secure-test")
    public String secureTest() {
        return "secure success";
    }

    @GetMapping("/user/test")
    public String userTest() {
        return "user 권한 접근 성공";
    }

    @GetMapping("/mypage/test")
    public String mypageTest() {
        return "mypage 접근 성공";
    }

    @GetMapping("/admin/test")
    public String adminTest() {
        return "admin 권한 접근 성공";
    }
}
