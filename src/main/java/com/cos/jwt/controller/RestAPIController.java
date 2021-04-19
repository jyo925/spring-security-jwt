package com.cos.jwt.controller;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/")
@Log
public class RestAPIController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    @GetMapping("logout")
    public String logout(@RequestHeader(value="Authorization") String token) {
        //띠용
        return "logout";
    }

    @GetMapping("home")
    public String home() {
        return "<h1>home</h1>";
    }

    @GetMapping("user")
    public String user() {
        return "user";
    }

    @GetMapping("manager")
    public String manager() {
        return "manager";
    }

    @GetMapping("admin")
    public String admin() {
        return "admin";
    }

    @PostMapping("token")
    public String token() {
        return "<h1>token</h1>";
    }

    @PostMapping("join")
    public String join(@RequestBody User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles("ROLE_USER");
        userRepository.save(user);
        return "회원 가입 완료";
    }
}
