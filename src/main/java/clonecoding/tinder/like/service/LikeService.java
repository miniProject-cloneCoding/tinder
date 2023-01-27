package clonecoding.tinder.like.service;

import clonecoding.tinder.like.dto.LikeResponseDto;
import clonecoding.tinder.like.dto.LikedMemberResponseDto;
import clonecoding.tinder.like.entity.Likes;
import clonecoding.tinder.like.entity.Ranking;
import clonecoding.tinder.like.repository.LikeRepository;
import clonecoding.tinder.like.repository.RankingRepository;
import clonecoding.tinder.members.entity.Member;
import clonecoding.tinder.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final RankingRepository rankingRepository;

    //좋아요
    @Transactional
    public LikeResponseDto like(Long id, Member likingMember) {
            //좋아요가 눌릴 사람 찾기
            Member likedMember = memberRepository.findById(id).orElseThrow(
                    ()-> new IllegalArgumentException("존재하지 않는 유저입니다.")
            );
            // 좋아요 객체 저장
            Likes likes = new Likes(likedMember.getId(),likingMember.getId());
            //db에 저장
            likeRepository.save(likes);
            if(rankingRepository.findByLikedMember(likedMember.getId()) == null) {
                Ranking ranking = new Ranking(likedMember.getId());
                rankingRepository.save(ranking);
            }else{
               Ranking ranking = rankingRepository.findByLikedMember(likedMember.getId()).orElseThrow(
                       ()-> new IllegalArgumentException("좋아요가 눌린 적 없는 사용자입니다.")
               );
                ranking.liked();
            }
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
        //좋아요를 취소하면 count -1
        Ranking ranking = rankingRepository.findByLikedMember(id).orElseThrow(
                ()-> new IllegalArgumentException("유저가 좋아요를 누른 상대방이 아닙니다.")
        );
        ranking.disLiked();
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
            int age = calculateAge(likedMember.getBirthDate());
            dtoList.add(new LikedMemberResponseDto(likedMember,age));
        }
        return dtoList;
    }
    // 하루동안 좋아요를 가장 많이 받은 top5 가져오기
    public List<LikedMemberResponseDto> getRanking() {
        List<Ranking> rankings = rankingRepository.findTop3ByOrderByCount();
        List<LikedMemberResponseDto> dtoList = new ArrayList<>();
        for(Ranking ranking : rankings) {
            Member likedMember = memberRepository.findById(ranking.getLikedMember()).orElseThrow(
                    ()-> new IllegalArgumentException("해당 사용자가 존재하지 않습니다.")
            );
            int age = calculateAge(likedMember.getBirthDate());
            dtoList.add(new LikedMemberResponseDto(likedMember,age));
        }
        return dtoList;
    }




    //생년월일에서 나이 가져오기
    private int calculateAge(String birthDate) {
        int year = Integer.parseInt(birthDate.substring(0, 2));
        if (year >= 0 && year <= LocalDateTime.now().getYear() - 2000) {
            year += 2000;
        } else {
            year += 1900;
        }
        return (LocalDateTime.now().getYear() - year + 1);
    }
}
