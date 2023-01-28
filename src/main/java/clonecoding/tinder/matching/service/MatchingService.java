package clonecoding.tinder.matching.service;


import clonecoding.tinder.like.entity.Likes;
import clonecoding.tinder.like.repository.LikeRepository;
import clonecoding.tinder.matching.model.dto.MatchingDto;
import clonecoding.tinder.matching.model.dto.MatchingResponseDto;
import clonecoding.tinder.matching.repository.RoomRepository;
import clonecoding.tinder.members.entity.Member;
import clonecoding.tinder.members.repository.MemberRedisRepository;
import clonecoding.tinder.members.repository.MemberRepository;
import clonecoding.tinder.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;
    private final MemberRedisRepository redisRepository;

    //나와 매칭된 회원들 보여주기
    public List<MatchingResponseDto> getMatching(String phoneNum) {

        //내 정보 가져오기
        Member my = findMember(phoneNum);

        //내가 좋아요 한 Likes 리스트
        List<Likes> likingList = likeRepository.findMyLiking(my.getId());

        //좋아요 하나도 안 한 경우
        if (likingList.size() == 0) {
            return null;
        }

        List<MatchingDto> likingDto = likingList.stream().map(likes ->
                MatchingDto.builder()
                        .likedMember(likes.getLikedMember())
                        .likingMember(likes.getLikingMember())
                        .build()).collect(Collectors.toList());

        //내가 좋아요 받은 경우
        List<Likes> likedList = likeRepository.findMyLiked(my.getId());

        //좋아요 받은게 한 개도 없다면 return
        if (likedList.size() == 0) {
            return null;
        }

        //좋아요 한 사람과 좋아요 받은 사람의 값을 서로 바꿔서 dto에 저장한다
        // -> 이렇게하면 내 id는 liking과 liked 모두 likingMember 안에 들어가게 된다
        // -> 따라서 dto의 EqualsAndHashcode 를 사용하여 상호간에 매칭되었는지(일치) 여부를 확인할 수 있게 된다
        List<MatchingDto> likedDto = likedList.stream().map(likes ->
                MatchingDto.builder()
                        .likedMember(likes.getLikingMember())
                        .likingMember(likes.getLikedMember())
                        .build()).collect(Collectors.toList());

        //나와 매칭된 회원의 id 값들을 모아두는 곳
        List<Long> matchingIds = new ArrayList<>();

        for (MatchingDto liking : likingDto) {
            log.info("liking에 들어갈 정보 = {}", liking.getLikingMember());
            log.info("liking에 들어갈 정보 = {}", liking.getLikedMember());
            for (MatchingDto liked : likedDto) {

                log.info("liked 들어갈 정보 = {}", liked.getLikingMember());
                log.info("liked 들어갈 정보 = {}", liked.getLikedMember());

                //내가 좋아요 한 회원이 나에게도 좋아요 한 경우에 회원 아이디를 저장함
                if (liking.equals(liked)) {
                    matchingIds.add(liking.getLikedMember());
                    break;
                }
            }
        }
        log.info("나와 매칭된 사람의 숫자는 = {}",matchingIds.size());

        //매칭된 회원들의 id를 가지고 member를 검색해온다
        return memberRepository.findAllById(matchingIds).stream().map(member ->
                MatchingResponseDto.builder()
                        .memberId(member.getId())
                        .nickName(member.getNickName())
                        .profile(member.getProfile())
                        .distance(calculateDistance(my.getLatitude(), my.getLongitude(), member.getLatitude(), member.getLongitude()))
                        .age(calculateAge(member.getBirthDate()))
                        .roomId(findRoomId(my.getId(), member.getId()))
                        .build()).collect(Collectors.toList());
    }



    //로그인한 내 정보를 phoneNum 으로 찾아오기 (Redis에서 먼저 검색 후 없으면 DB 접근)
    private Member findMember(String phoneNum) {
        UserDetailsImpl member = redisRepository.getUser(phoneNum).orElseGet(
                () -> memberRepository.findByPhoneNum(phoneNum).map(UserDetailsImpl::fromEntity).orElseThrow(
                        () -> new IllegalArgumentException("로그인을 해주세요.")
                ));

        return member.getMember();
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

    //나와 매칭된 회원과의 대화방 id 가져오기
    private Long findRoomId(Long id1, Long id2) {
        if (roomRepository.findByMember1AndMember2(id1, id2).isPresent()) {
            return roomRepository.findByMember1AndMember2(id1, id2).get().getId();
        }
        if (roomRepository.findByMember1AndMember2(id1, id2).isPresent()) {
            return roomRepository.findByMember1AndMember2(id2, id1).get().getId();
        }
        return 0L;
    }

    // Haversine formula (위도, 경도로 거리 구하기)
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        lat1 = Math.toRadians(lat1);
        lon1 = Math.toRadians(lon1);
        lat2 = Math.toRadians(lat2);
        lon2 = Math.toRadians(lon2);

        double earthRadius = 6371; //Kilometers
        return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
    }
}
