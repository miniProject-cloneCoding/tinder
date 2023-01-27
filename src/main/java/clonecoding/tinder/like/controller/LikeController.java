package clonecoding.tinder.like.controller;

import clonecoding.tinder.like.dto.LikeResponseDto;
import clonecoding.tinder.like.dto.LikedMemberResponseDto;
import clonecoding.tinder.like.service.LikeService;
import clonecoding.tinder.security.UserDetailsImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Api(tags = {"Like"})
@RestController
@RequiredArgsConstructor
@Slf4j
public class LikeController {
    private final LikeService likeService;

    //좋아요
    @PatchMapping("like/{id}")
    @ApiOperation(value = "좋아요 누르기", notes = "좋아하는 사람에게 좋아요를 눌러준다!")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public LikeResponseDto like(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.like(id,userDetails.getMember());
    }
    //좋아요 취소하기
    @PatchMapping("dislike/{id}")
    @ApiOperation(value = "좋아요 취소", notes = "좋아요 누른 사람 좋아요 취소하기!")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public LikeResponseDto dislike(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.dislike(id,userDetails.getMember());
    }
    //좋아요한 사람 리스트 받아오기
    @GetMapping("like/list")
    @ApiOperation(value = "좋아요한 사람 리스트 주기", notes = "좋아요 누른 사람들 전체 리스트를 돌려준다!")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public List<LikedMemberResponseDto> getLikedMembers(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return likeService.getLikedMembers(userDetails.getMember());
    }

    //좋아요 랭킹
    @GetMapping("like/ranking")
    @ApiOperation(value = "좋아요한 사람 리스트 주기", notes = "좋아요 누른 사람들 전체 리스트를 돌려준다!")
    @ApiImplicitParam(name = "authorization", value = "authorization", required = true, dataType = "string", paramType = "header")
    public List<LikedMemberResponseDto> getRanking() {
        return likeService.getRanking();
    }
}
