package clonecoding.tinder.member.service;

import clonecoding.tinder.member.dto.MemberJoinRequestDto;
import clonecoding.tinder.member.dto.MemberResponseMsgDto;
import clonecoding.tinder.member.entity.Member;
import clonecoding.tinder.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponseMsgDto signup(MemberJoinRequestDto memberJoinRequestDto, HttpServletResponse response) {
        String phoneNum = memberJoinRequestDto.getPhoneNum();

        /*
         * 핸드폰 번호 검증을 먼저 실행합니다.
         *
         */

        //시작은 010으로 시작, 뒤에는 숫자만 8자리가 와야함.
        String phoneRegExp = "^010(\\d{8})$";

        //위 정규식과 다르면 입력 양식이 잘못된 것
        if (!phoneNum.matches(phoneRegExp)) {
            return new MemberResponseMsgDto("번호 양식을 지켜주세요!", HttpStatus.BAD_REQUEST.value());
        }

        //db에서 입력된 핸드폰 번호로 회원 조회
        Optional<Member> existMember = memberRepository.findByPhoneNum(phoneNum);

        //입력된 핸드폰 번호가 db에 있으면 이미 가입한 회원
        if (existMember.isPresent()) {
            return new MemberResponseMsgDto("이미 가입한 회원입니다!", HttpStatus.BAD_REQUEST.value());
        }


        /*
         * 생년월일 검증입니다.
         *
         */



        //정규식 적용이 안돼서 그냥 matches에 넣었습니다.
        // \\d{2}연도 두 자리는 아무거나 와도 되니까 따로 조건을 걸지 않고 숫자만 오면 상관없게 하였음.

        // ([0]\\d|[1][0-2]) 월의 경우 앞자리가 0이면 뒤에는 1-9만 오도록 하였음.
        // 앞의 자리가 1인 경우에는 0,1,2만 올 수 있도록 하였음.

        //([0][1-9]|[1-2]\\d|[3][0-1]) 일의 경우 앞자리가 0이면 뒤에는 1-9,
        // 1이나 2인 경우엔 숫자 아무거나, 3인 경우에는 0이나 1만 올 수 있도록 함.
//        String birthDateRegExp = "^\\d{2}([0]\\d|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])$";

        if (!memberJoinRequestDto.getBirthDate().matches("^\\d{2}([0]\\d|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])$")) {
            return new MemberResponseMsgDto("생년월일 양식을 지켜주세요!", HttpStatus.BAD_REQUEST.value());
        }


        /*
         * 닉네임 검증입니다.
         *
         */

        //생년월일과 마찬가지 입니다.
//        String nickNameRegExp = "^[가-힣a-zA-Z]{2,6}$";
        if (!memberJoinRequestDto.getNickName().matches("^[가-힣a-zA-Z]{2,6}$")) {
            return new MemberResponseMsgDto("닉네임 양식을 지켜주세요!", HttpStatus.BAD_REQUEST.value());
        }


        /*
         * 비밀번호 검증입니다.
         *
         */
        // ?= 뒷부분을 확인하겠다. .* 하나라도 있는 지 체크. .은 어떤 한 개의 문자, *은 앞의 문자가 0개 이상 있음을 의미.
        if (memberJoinRequestDto.getPassword().matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,12}$")) {
            return new MemberResponseMsgDto("비밀번호는 영어 대소문자, 숫자의 최소 8자에서 최대 12자리여야 합니다.", HttpStatus.BAD_REQUEST.value());
        }


        //빌더패턴으로 save를 위한 entity 생성
        Member member = Member.builder()
                .phoneNum(memberJoinRequestDto.getPhoneNum())
                .nickName(memberJoinRequestDto.getNickName())
                .birthDate(memberJoinRequestDto.getBirthDate())
                .profile(memberJoinRequestDto.getProfile())
                .email("0")
                .build();

        //회원 저장
        memberRepository.save(member);

        //value에 작성된 출처에 브라우저가 리소스를 접근할 수 있도록 허용합니다.
        //*을 사용할 수 있을 것 같은데 일단 프론트 분들이 보통 쓰시는 포트번호로 열어두었습니다.
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        return new MemberResponseMsgDto("회원가입 성공!", HttpStatus.OK.value());
    }
}
