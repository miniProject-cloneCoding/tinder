package clonecoding.tinder.like.service;

import clonecoding.tinder.jwt.JwtUtil;
import clonecoding.tinder.like.dto.LikeResponseDto;
import clonecoding.tinder.like.entity.Likes;
import clonecoding.tinder.like.repository.LikeRepository;
import clonecoding.tinder.member.entity.Member;
import clonecoding.tinder.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    public LikeResponseDto like(Long id, HttpServletRequest request) {

        //Request에서 토큰 가져오기
        String token = jwtUtil.resolveToken(request);
        Claims claims;
        //토큰이 있는 경우에만 좋아요 누르기 가능
        if(token != null) {
            if(jwtUtil.validateToken(token)) {
                //토큰에서 정보 가져오기
                claims = jwtUtil.getUserInfoFromToken(token);
            }else {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }
            //토큰에서 로그인한 유저 정보 가져오기
            Member likingMember = memberRepository.findByPhoneNum(claims.getSubject()).orElseThrow(
                    ()-> new IllegalArgumentException("존재하지 않는 유저입니다.")
            );
            //좋아요가 눌릴 사람 찾기
            Member likedMember = memberRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("존재하지 않는 유저입니다.")
            );
            Likes likes = new Likes(likedMember.getId(),likingMember.getId());
            //db에 저장
            likeRepository.save(likes);
            return new LikeResponseDto("좋아요를 눌렀습니다.", HttpStatus.OK.value());
        }
        return new LikeResponseDto("토큰이 비어있습니다", HttpStatus.NO_CONTENT.value());
        }
}
