package com.cad.searh_service.controller;

import com.cad.searh_service.dto.memberDto.*;
import com.cad.searh_service.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemeberController {
    private final MemberService memberService;
    private final Logger log = LoggerFactory.getLogger(CadController.class);

    @PostMapping("/register")
    public ResponseEntity<MemberRegisterResponse> register(@RequestBody MemberRegisterRequest memeberRegisterRequest) {
        try {
            MemberDto memberDto = memberService.register(memeberRegisterRequest);
            MemberRegisterResponse response = new MemberRegisterResponse(memberDto.getEmployName());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Failed to register member", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<MemberLoginResponse> login(@RequestBody MemberLoginRequest memberLoginRequest) {
        try {
            String token = memberService.login(memberLoginRequest.getEmployNumber(), memberLoginRequest.getPassword());
            return new ResponseEntity<>(new MemberLoginResponse(token), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Failed to login member", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
