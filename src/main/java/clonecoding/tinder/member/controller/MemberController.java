package clonecoding.tinder.member.controller;

import clonecoding.tinder.member.dto.MemberLoginRequestDto;
import clonecoding.tinder.member.dto.MemberSignupRequestDto;
import clonecoding.tinder.member.dto.MemberResponseMsgDto;
import clonecoding.tinder.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public MemberResponseMsgDto signup(@RequestBody MemberSignupRequestDto memberSignupRequestDto, HttpServletResponse response) {
        return memberService.signup(memberSignupRequestDto, response);
    }

    @PostMapping("/login")
    public MemberResponseMsgDto login(@RequestBody MemberLoginRequestDto memberLoginRequestDto, HttpServletResponse response) {
        return memberService.login(memberLoginRequestDto, response);
    }
}
