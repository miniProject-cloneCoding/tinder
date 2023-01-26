package clonecoding.tinder.members_temp.service;

import clonecoding.tinder.jwt.JwtUtil;
import clonecoding.tinder.member.entity.Member;
import clonecoding.tinder.member.repository.MemberRepository;
import clonecoding.tinder.members_temp.dto.MembersResponseDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    @PostConstruct
    public void init() {
        Member member1 = new Member("member1", "011", "pass", 126.925205, 37.4787760); //소산
        Member member2 = new Member("member2", "012", "pass", 126.923300, 37.5818396); //보노
        Member member3 = new Member("member3", "013", "pass", 126.729566, 37.4928588); //인천
        Member member4 = new Member("member4", "014", "pass", 127.028230, 37.5007549); //강남
        Member member5 = new Member("member5", "015", "pass", 126.925386, 37.4788392); //문성로
        Member member6 = new Member("member6", "016", "pass", 126.917397, 37.4749234); //난곡로

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
    }

    public Page<MembersResponseDto> getMembers(Pageable pageable, String phoneNum) {

//        // 토큰에서 사용자 이름 가져오기
//        Claims claims = getClaims(request);
//        String phoneNum = claims.getSubject();

        Member my = memberRepository.findByPhoneNum(phoneNum).orElseThrow(() -> new IllegalArgumentException("로그인을 해주세요"));

        //사용자를 제외한 전체 멤버 가져오기
        //Entity -> dto 변환
//        Page<MembersResponseDto> dtoList = memberRepository.findAllByNickNameNot(username, pageable).map(MembersResponseDto::fromEntity);
//
//        //dto 안에서 거리를 계산하고 거리 순으로 정렬한다
//        return new PageImpl<>(dtoList.stream().map(membersResponseDto ->
//                        MembersResponseDto.builder()
//                                .id(membersResponseDto.getId())
//                                .nickName(membersResponseDto.getNickName())
//                                .birthDate(membersResponseDto.getBirthDate())
//                                .profile(membersResponseDto.getProfile())
//                                .distance(
//                                        calculateDistance(my.getLatitude(), my.getLongitude(), membersResponseDto.getLatitude(), membersResponseDto.getLongitude())
//                                ).build())
//                .sorted(Comparator.comparing(MembersResponseDto::getDistance))
//                .collect(Collectors.toList()));

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
                                ).build())
                .sorted(Comparator.comparing(MembersResponseDto::getDistance))
                .collect(Collectors.toList()));
    }

    private Claims getClaims(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        log.info(token);
        return jwtUtil.getUserInfoFromToken(token);
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
