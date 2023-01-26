package clonecoding.tinder.members.service;

import clonecoding.tinder.jwt.JwtUtil;
import clonecoding.tinder.like.entity.Likes;
import clonecoding.tinder.like.repository.LikeRepository;
import clonecoding.tinder.like.service.LikeService;
import clonecoding.tinder.member.entity.Member;
import clonecoding.tinder.member.repository.MemberRepository;
import clonecoding.tinder.members.dto.MemberFindRequestDto;
import clonecoding.tinder.members.dto.MembersResponseDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersService {

    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    @PostConstruct
    public void init() {
        Member member1 = new Member("member1", "011", "pass", 126.925205, 37.4787760, "930206", "img1"); //소산
        Member member2 = new Member("member2", "012", "pass", 126.923300, 37.5818396, "000123", "img1"); //보노
        Member member3 = new Member("member3", "013", "pass", 126.729566, 37.4928588, "980703", "img1"); //인천
        Member member4 = new Member("member4", "014", "pass", 127.028230, 37.5007549, "650403", "img1"); //강남
        Member member5 = new Member("member5", "015", "pass", 126.925386, 37.4788392, "850519", "img1"); //문성로
        Member member6 = new Member("member6", "016", "pass", 126.917397, 37.4749234, "990818", "img1"); //난곡로

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);

        Likes likes = new Likes(member5.getId(), member1.getId());
        likeRepository.save(likes);
    }

    public Page<MembersResponseDto> getMembers(Pageable pageable, String phoneNum) {

        Member my = memberRepository.findByPhoneNum(phoneNum).orElseThrow(() -> new IllegalArgumentException("로그인을 해주세요"));

        //전체 회원 중 이미 좋아요한 회원 제외하고 가져오기
        //파라미터 (내 아이디, 페이지번호, 페이지 사이즈)
        List<Member> members = memberRepository.findAllWithoutLike(my.getId(), Long.valueOf(pageable.getOffset()).intValue(), pageable.getPageSize());

        //entity -> dto 변환
        Stream<MembersResponseDto> dtoList = members.stream().map(MembersResponseDto::fromEntity);

        //dto 안에서 거리를 계산하고 거리 순으로 정렬한다
        return new PageImpl<>(dtoList.map(membersResponseDto ->
                        MembersResponseDto.builder()
                                .id(membersResponseDto.getId())
                                .nickName(membersResponseDto.getNickName())
                                .birthDate(membersResponseDto.getBirthDate())
                                .profile(membersResponseDto.getProfile())
                                .distance(
                                        calculateDistance(my.getLatitude(), my.getLongitude(), membersResponseDto.getLatitude(), membersResponseDto.getLongitude())
                                )
                                .age(calculateAge(membersResponseDto.getBirthDate()))
                                .build())
                .sorted(Comparator.comparing(MembersResponseDto::getDistance))
                .collect(Collectors.toList()));
    }

    //API가 호출 될 때마다 회원 한 명씩 반환하기 위한 List
    public static List<MembersResponseDto> membersResponseDtoList = new ArrayList<>();

    public static int count = 0;
    public static Map<Long, MembersResponseDto> map = new HashMap<>(); //좋아요 시 삭제하

    public MembersResponseDto getMember(String phoneNum, MemberFindRequestDto requestDto) {


        Member my = memberRepository.findByPhoneNum(phoneNum).orElseThrow(() -> new IllegalArgumentException("로그인을 해주세요"));

        //좋아요를 눌렀다면
        if (requestDto.isLike()) {
            //좋아요 테이블에 좋아요 기록 저장
            Likes likes = new Likes(requestDto.getId(), my.getId());
            likeRepository.save(likes);

            //todo hashmap에서 삭제하기
        }

        if (count == membersResponseDtoList.size()) {
            count = 0;
        }

        //전체 회원 중 이미 좋아요한 회원 제외하고 가져오기
        //파라미터 (내 아이디, 페이지번호, 페이지 사이즈)
        List<Member> members = memberRepository.findAllWithoutPaging(my.getId());

        //entity -> dto 변환
        Stream<MembersResponseDto> dtoList = members.stream().map(MembersResponseDto::fromEntity);



        //dto 안에서 거리를 계산하고 거리 순으로 정렬한다
        membersResponseDtoList = dtoList.map(membersResponseDto ->
                        MembersResponseDto.builder()
                                .id(membersResponseDto.getId())
                                .nickName(membersResponseDto.getNickName())
                                .birthDate(membersResponseDto.getBirthDate())
                                .profile(membersResponseDto.getProfile())
                                .distance(
                                        calculateDistance(my.getLatitude(), my.getLongitude(), membersResponseDto.getLatitude(), membersResponseDto.getLongitude())
                                )
                                .age(calculateAge(membersResponseDto.getBirthDate()))
                                .build())
                .sorted(Comparator.comparing(membersResponseDto1 -> membersResponseDto1 != null ? membersResponseDto1.getDistance() : 0))
                .collect(Collectors.toList());

        membersResponseDtoList.stream().map(membersResponseDto -> map.put(membersResponseDto.getId(), membersResponseDto));

        return membersResponseDtoList.get(count++);
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
