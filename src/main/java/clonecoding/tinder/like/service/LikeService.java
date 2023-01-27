package clonecoding.tinder.like.service;

import clonecoding.tinder.like.dto.LikeResponseDto;
import clonecoding.tinder.like.dto.LikedMemberResponseDto;
import clonecoding.tinder.like.entity.Likes;
import clonecoding.tinder.like.repository.LikeRepository;
import clonecoding.tinder.members.entity.Member;
import clonecoding.tinder.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    //좋아요
    @Transactional
    public LikeResponseDto like(Long id, Member likingMember) {
            //좋아요가 눌릴 사람 찾기
            Member likedMember = memberRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("존재하지 않는 유저입니다.")
            );
            Likes likes = new Likes(likedMember.getId(),likingMember.getId());
            //db에 저장
            likeRepository.save(likes);
            return new LikeResponseDto("좋아요를 눌렀습니다.", HttpStatus.OK.value());
        }
    //좋아요 취소
    @Transactional
    public LikeResponseDto dislike(Long id, Member member) {
        //좋아요가 되어있는 사람 찾기
        Likes likes = likeRepository.findByLikedMemberAndLikingMember(id, member.getId()).orElseThrow(
                () -> new IllegalArgumentException("유저가 좋아요를 누른 상대방이 아닙니다.")
        );
        likeRepository.delete(likes);
        return new LikeResponseDto("좋아요를 취소했습니다.", HttpStatus.OK.value());
    }

    //로그인한 유저가 좋아요 누른 사람들 불러오기
    @Transactional(readOnly = true)
    public List<LikedMemberResponseDto> getLikedMembers(Member member) {
        List<Likes> likesList = likeRepository.findAllByLikingMember(member.getId()).orElse(new ArrayList<>());
        //새로운 LikedMemberResponseDto 리스트 생성
        List<LikedMemberResponseDto> dtoList = new ArrayList<>();
        for(Likes likes : likesList) {
            Member likedMember = memberRepository.findById(likes.getLikedMember()).orElseThrow(
                    ()-> new IllegalArgumentException("좋아요를 누른 대상이 존재하지 않습니다.")
            );
            dtoList.add(new LikedMemberResponseDto(likedMember));
        }
        return dtoList;
    }
}
