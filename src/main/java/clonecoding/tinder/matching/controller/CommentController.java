package clonecoding.tinder.matching.controller;

import clonecoding.tinder.matching.model.dto.CommentRequestDto;
import clonecoding.tinder.matching.model.dto.CommentResponseDto;
import clonecoding.tinder.matching.model.dto.CommentUpdateDto;
import clonecoding.tinder.matching.model.dto.ProfileResponseDto;
import clonecoding.tinder.matching.service.CommentService;
import clonecoding.tinder.security.UserDetailsImpl;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    //상대방과 나 사이의 댓글 다 가져오기
    @ApiOperation(value = "나와 매칭된 회원 댓글 조회")
    @GetMapping("/comments/{roomId}")
    public List<CommentResponseDto> getComments(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long roomId) {
        return commentService.getComments(userDetails.getMember().getPhoneNum(), roomId);
    }

    //댓글 작성하기
    @ApiOperation(value = "댓글달기")
    @PostMapping("/comments")
    public String createComments(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CommentRequestDto requestDto) {
        commentService.createComments(userDetails.getMember().getPhoneNum(), requestDto);
        return "댓글 작성 완료";
    }
    @ApiOperation(value = "댓글수정")
    @PatchMapping("/comments/{commentId}")
    public String updateComments(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @RequestBody CommentUpdateDto requestDto,
                                 @PathVariable Long commentId) {
        commentService.updateComments(userDetails.getMember().getPhoneNum(), requestDto, commentId);
        return "댓글 수정 완료";
    }

    @ApiOperation(value = "댓글삭제")
    @DeleteMapping("/comments/{commentId}")
    public String deleteComments(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                 @PathVariable Long commentId) {
        commentService.deleteComments(userDetails.getMember().getPhoneNum(), commentId);
        return "댓글 삭제 완료";
    }

    @ApiOperation(value = "나와 매칭된 회원 조회 상세페이지")
    @GetMapping("/profile/{roomId}")
    public ProfileResponseDto getProfile(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable Long roomId) {
        return commentService.getProfile(userDetails.getMember().getPhoneNum(), roomId);
    }
}
