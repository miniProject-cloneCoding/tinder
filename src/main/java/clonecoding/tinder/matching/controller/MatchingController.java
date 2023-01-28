package clonecoding.tinder.matching.controller;

import clonecoding.tinder.matching.model.dto.MatchingResponseDto;
import clonecoding.tinder.matching.service.MatchingService;
import clonecoding.tinder.members.dto.MembersResponseDto;
import clonecoding.tinder.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;
    @ApiOperation(value = "나와 매칭된 회원 조회")
    @GetMapping("/matching")
    public List<MatchingResponseDto> getMatching(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return matchingService.getMatching(userDetails.getMember().getPhoneNum());
    }
}
