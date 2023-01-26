package clonecoding.tinder.like.controller;

import clonecoding.tinder.like.dto.LikeResponseDto;
import clonecoding.tinder.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    //좋아요
    @PatchMapping("like/{id}")
    public LikeResponseDto like(@PathVariable Long id, @RequestBody HttpServletRequest request) {
        return likeService.like(id,request);
    }
}
