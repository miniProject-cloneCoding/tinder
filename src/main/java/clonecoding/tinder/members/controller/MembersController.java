package clonecoding.tinder.members.controller;

import clonecoding.tinder.members.dto.MemberFindRequestDto;
import clonecoding.tinder.members.dto.MembersResponseDto;
import clonecoding.tinder.members.service.MembersService;
import clonecoding.tinder.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MembersController {

    private final MembersService membersService;

    // 전체 회원 조회해서 가져오기
    @GetMapping
    public Page<MembersResponseDto> getMembers(Pageable pageable, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("컨트롤러 실행");
        return membersService.getMembers(pageable, userDetails.getMember().getPhoneNum());
    }

    @GetMapping("/one")
    public MembersResponseDto getFirstMember(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody MemberFindRequestDto requestDto) {
        return membersService.getMember(userDetails.getMember().getPhoneNum(), requestDto);
    }

    @PostMapping("/one")
    public MembersResponseDto getMember(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody MemberFindRequestDto requestDto) {
        return membersService.getMember(userDetails.getMember().getPhoneNum(), requestDto);
    }
}
