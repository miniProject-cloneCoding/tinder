package clonecoding.tinder.member.controller;

import clonecoding.tinder.member.dto.MemberJoinRequestDto;
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
    public MemberResponseMsgDto signup(@RequestBody MemberJoinRequestDto memberJoinRequestDto, HttpServletResponse response) {
        return memberService.signup(memberJoinRequestDto, response);
    }
}
