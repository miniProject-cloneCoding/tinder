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

import javax.servlet.http.HttpServletRequest;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public Page<MembersResponseDto> getMembers(HttpServletRequest request, Pageable pageable) {

        // 토큰에서 사용자 이름 가져오기
        Claims claims = getClaims(request);
        String username = claims.getSubject();

        Member my = memberRepository.findByNickName(username).orElseThrow(() -> new IllegalArgumentException("로그인을 해주세요"));

        //사용자를 제외한 전체 멤버 가져오기
        //Entity -> dto 변환
        Page<MembersResponseDto> responseDtos = memberRepository.findAllByNickNameNot(username, pageable).map(MembersResponseDto::fromEntity);

        //dto 안에서 거리를 계산하고 거리 순으로 정렬한다
        return new PageImpl<>(responseDtos.stream().map(membersResponseDto ->
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
