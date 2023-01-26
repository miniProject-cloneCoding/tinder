package clonecoding.tinder.members_temp.controller;

import clonecoding.tinder.members_temp.dto.MembersResponseDto;
import clonecoding.tinder.members_temp.service.MembersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MembersController {

    private final MembersService membersService;

    // 전체 회원 조회해서 가져오기
    @GetMapping
    public Page<MembersResponseDto> getMembers(Pageable pageable, HttpServletRequest request) {
        log.info("컨트롤러 실행");
        return  membersService.getMembers(request, pageable);
    }
}
