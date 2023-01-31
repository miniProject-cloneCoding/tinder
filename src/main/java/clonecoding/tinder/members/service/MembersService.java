package clonecoding.tinder.members.service;

import clonecoding.tinder.jwt.JwtUtil;
import clonecoding.tinder.like.entity.Likes;
import clonecoding.tinder.like.repository.LikeRepository;
import clonecoding.tinder.matching.model.Room;
import clonecoding.tinder.matching.repository.RoomRepository;
import clonecoding.tinder.members.dto.*;
import clonecoding.tinder.members.entity.Member;
import clonecoding.tinder.members.repository.MemberRedisRepository;
import clonecoding.tinder.members.repository.MemberRepository;
import clonecoding.tinder.security.UserDetailsImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MembersService {
    private final RoomRepository roomRepository;

    private final MemberRepository memberRepository;
    private final LikeRepository likeRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;
    private final MemberRedisRepository redisRepository;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.token.expired-time-ms}")
    private Long expiredTimeMs;


    //todo 초기데이터 삭제할 것
//    @PostConstruct
    public void init() {
        Member member1 = new Member("member1", "011", "pass", 126.925205, 37.4787760, "930206", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 0, true, true); //소산
        Member member2 = new Member("member2", "012", "pass", 126.923300, 37.5818396, "000123", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 0, true, false); //보노
        Member member3 = new Member("member3", "013", "pass", 126.729566, 37.4928588, "980703", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 0, true, false); //인천
        Member member4 = new Member("member4", "014", "pass", 127.028230, 37.5007549, "650403", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 1, false, true); //강남
        Member member5 = new Member("member5", "015", "pass", 126.925386, 37.4788392, "850519", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 1, false, true); //문성로
        Member member6 = new Member("member6", "016", "pass", 126.917397, 37.4749234, "990818", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 1, false, true); //난곡로
        Member member7 = new Member("member7", "017", "pass", 127.028230, 37.5007549, "650403", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 1, false, true); //강남
        Member member8 = new Member("member8", "018", "pass", 126.925386, 37.4788392, "850519", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 1, false, true); //문성로
        Member member9 = new Member("member9", "019", "pass", 126.917397, 37.4749234, "990818", "https://cdn.pixabay.com/photo/2017/08/06/12/52/woman-2592247_960_720.jpg", 1, false, true); //난곡로

        memberRepository.save(member1);
        memberRepository.save(member2);
        memberRepository.save(member3);
        memberRepository.save(member4);
        memberRepository.save(member5);
        memberRepository.save(member6);
        memberRepository.save(member7);
        memberRepository.save(member8);
        memberRepository.save(member9);

        Likes likes = new Likes(member5.getId(), member1.getId());
        likeRepository.save(likes);
        Likes likes2 = new Likes(member1.getId(), member5.getId());
        likeRepository.save(likes2);
        Likes likes3 = new Likes(member1.getId(), member4.getId());
        likeRepository.save(likes3);

        Room room = new Room(1L, 5L);
        roomRepository.save(room);
    }

    //API가 호출 될 때마다 회원 한 명씩 반환하기 위한 List
    public static List<MembersResponseDto> membersResponseDtoList = new ArrayList<>();
    public static int count = 0;
    public static Map<Long, MembersResponseDto> map = new HashMap<>(); //좋아요 한 회원 삭제하는 용도

    //회원 전체 페이징하여 조회하기
    public Page<MembersResponseDto> getMembers(Pageable pageable, String phoneNum) {

        //내 정보 가져오기
        Member my = findMember(phoneNum);

        // 내가 원하는 성별 memberSearch 조건에 넣어주기
        MemberSearch memberSearch = new MemberSearch(my.isWantingMale(), my.isWantingFemale());

        //전체 회원 중 이미 좋아요한 회원 제외하고 가져오기
        //파라미터 (내 아이디, 페이지번호, 페이지 사이즈)
        List<Member> members = memberRepository.findAllWithPaging(my.getId(), Long.valueOf(pageable.getOffset()).intValue(), pageable.getPageSize(), memberSearch);

        //entity -> dto 변환
        Stream<MembersResponseDto> dtoList = members.stream().map(MembersResponseDto::fromEntity);

        //dto 안에서 거리를 계산하고 거리 순으로 정렬한다
        return new PageImpl<>(sortDto(my, dtoList));
    }

    //추천회원 한명 조회하기
    public MembersResponseDto getMember(String phoneNum, MemberFindRequestDto requestDto) {
        log.info("map 사이즈는 = {}", map.size());

        Member my = findMember(phoneNum);

        //좋아요를 눌렀다면
        if (requestDto.isLike()) {

            //좋아요 할 사람의 id가 존재하지 않는다면 Exception
            if (memberRepository.findById(requestDto.getId()).isEmpty()) {
                throw new IllegalArgumentException("존재하지 않는 회원입니다");
            }

            //좋아요 테이블에 좋아요 저장
            Likes likes = new Likes(requestDto.getId(), my.getId());
            likeRepository.save(likes);

            //map 에서 좋아요 한 사람 삭제하기 (그래야 다음 조회 떄 좋아요 한 사람 나오지 않음)
            map.remove(requestDto.getId());
        }

        // 전체 회원 한 바퀴 다 돌았다면
        if (count == membersResponseDtoList.size()) {

            //dtoList 초기화하고 갱신된 map의 회원정보를 dto에 담아줌
            // -> map에는 좋아요 한 사람이 빠져있음
            membersResponseDtoList = new ArrayList<>();
            for (int i = 0; i < map.size(); i++) {
                membersResponseDtoList.add(map.get((long) i));
            }
            count = 0; //다시 0부터 카운트 시작
        }

        //map 안에 조회한 회원들의 정보가 있다면 DB 조회 안하고 바로 가져오기
        if (map.size() > 0) {
            return membersResponseDtoList.get(count++);
        }

        /*
            map 안에 정보가 없다면 -> DB에서 조회하기
         */
        //내가 원하는 성별 memberSearch 조건에 넣어주기
        MemberSearch memberSearch = new MemberSearch(my.isWantingMale(), my.isWantingFemale());

        //전체 회원 중 이미 좋아요한 회원 제외하고, 내가 원하는 성별만 가져오기
        //파라미터 (내 아이디, 성별검색조건)
        List<Member> members = memberRepository.findAllWithoutPaging(my.getId(), memberSearch);
        log.info("조회한 회원 list 사이즈는 = {}", members.size());

        //entity -> dto 변환
        Stream<MembersResponseDto> dtoList = members.stream().map(MembersResponseDto::fromEntity);

        //dto 안에서 거리를 계산하고 거리 순으로 정렬한다
        membersResponseDtoList = sortDto(my, dtoList);

        //dto의 값들을 map에 넣어준다 (나중에 좋아요한 사람의 키 값을 가지고 삭제하기 위함)
        for (MembersResponseDto responseDto : membersResponseDtoList) {
            map.put(responseDto.getId(), responseDto);
        }

        //api 호출 시마다 count++하여 dto에서 순차적으로 회원을 조회한다
        log.info("회원 조회 카운트 = {} ", count);
        return membersResponseDtoList.get(count++);
    }

    //dto에 거리와 나이를 계산하여 저장하고 거리순으로 정렬함
    private List<MembersResponseDto> sortDto(Member my, Stream<MembersResponseDto> dtoList) {
        return dtoList.map(membersResponseDto ->
                        MembersResponseDto.builder()
                                .id(membersResponseDto.getId())
                                .nickName(membersResponseDto.getNickName())
                                .birthDate(membersResponseDto.getBirthDate())
                                .profile(membersResponseDto.getProfile())
                                .distance(roundDistance(calculateDistance(my.getLatitude(), my.getLongitude(), membersResponseDto.getLatitude(), membersResponseDto.getLongitude())))
                                .age(calculateAge(membersResponseDto.getBirthDate()))
                                .build())
                .sorted(Comparator.comparing(MembersResponseDto::getDistance))
                .collect(Collectors.toList());
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

    /*
     *
     *
     *
     * 회원가입 / 로그인 파트입니다
     *
     *
     *
     */

    //예외처리해서 클라이언트 상태코드도 커스텀하기 위해
    public void memberExceptionHandler(HttpServletResponse response, String msg, int statusCode) {
        //setStatus를 통해 response의 상태 코드 set
        response.setStatus(statusCode);
        //콘텐츠 유형을 json으로 바꿔줌
        response.setContentType("application/json");
        try {
            //ObjectMapper 클래스를 사용하여 클래스의 객체를 SecurityExceptionDtoJSON 문자열로 변환.
            //objectMapper는 자바 객체를 JSON으로 직렬화나 역직렬화 하는데 쓰이는 Jackson 라이브러리 클래스.
            //Jackson 라이브러리의 기본 클래스이며 Java 개체와 JSON 데이터 간의 변환 기능을 제공한다.
            String json = new ObjectMapper().writeValueAsString(new SecurityExceptionDto(statusCode, msg));
            //response의 body에 JSON 문자열이 작성됨
            response.getOutputStream().write(json.getBytes());
        } catch (Exception e) {
            //예외를 처리하는 동안 발생하는 모든 예외(Exception)을 기록한다.
            log.error(e.getMessage());
        }
    }

    //위 핸들러를 쓰고 dto로 리턴하기
    private void handleMemberException(String message, HttpStatus status, HttpServletResponse response) {
        memberExceptionHandler(response, message, status.value());
    }

    //정규식 검증 따로 빼 두기
    private boolean isValidNickName(String nickName) {
        return nickName.matches("^[가-힣a-zA-Z]{2,6}$");
    }

    //시작은 010으로 시작, 뒤에는 숫자만 8자리가 와야함.
    private boolean isValidPhoneNum(String phoneNum) {
        return phoneNum.matches("^010(\\d{8})$");
    }

    private boolean isExistMember(String phoneNum) {
        //db에서 입력된 핸드폰 번호로 회원 조회
        Optional<Member> existMember = memberRepository.findByPhoneNum(phoneNum);

        return existMember.isPresent();
    }

    // \\d{2}연도 두 자리는 아무거나 와도 되니까 따로 조건을 걸지 않고 숫자만 오면 상관없게 하였음.

    // ([0]\\d|[1][0-2]) 월의 경우 앞자리가 0이면 뒤에는 1-9만 오도록 하였음.
    // 앞의 자리가 1인 경우에는 0,1,2만 올 수 있도록 하였음.

    //([0][1-9]|[1-2]\\d|[3][0-1]) 일의 경우 앞자리가 0이면 뒤에는 1-9,
    // 1이나 2인 경우엔 숫자 아무거나, 3인 경우에는 0이나 1만 올 수 있도록 함.
    private boolean isValidBirthDate(String birthDate) {
        return birthDate.matches("^\\d{2}([0]\\d|[1][0-2])([0][1-9]|[1-2]\\d|[3][0-1])$");
    }

    // ?= 뒷부분을 확인하겠다. .* 하나라도 있는 지 체크. .은 어떤 한 개의 문자, *은 앞의 문자가 0개 이상 있음을 의미.
    private boolean isValidPassword(String password) {
        return password.matches("(?=.*[0-9])(?=.*[a-zA-Z]).{8,12}");
    }


    @Transactional
    public MemberResponseMsgDto signup(MemberSignupRequestDto memberSignupRequestDto, HttpServletResponse response){
        String phoneNum = memberSignupRequestDto.getPhoneNum();

        /*
         * 핸드폰 번호 검증을 먼저 실행합니다.
         *
         */




        //입력된 핸드폰 번호가 db에 있으면 이미 가입한 회원
        if (isExistMember(memberSignupRequestDto.getPhoneNum())) {
//            memberExceptionHandler(response, "이미 가입한 회원입니다!", HttpStatus.BAD_REQUEST.value());
            throw new IllegalArgumentException("이미 가입한 회원입니다!");
        }

        //위 정규식과 다르면 입력 양식이 잘못된 것
        if (!isValidPhoneNum(memberSignupRequestDto.getPhoneNum())) {
//            handleMemberException("번호 양식을 지켜주세요!", HttpStatus.BAD_REQUEST, response);
            throw new IllegalArgumentException("번호 양식을 지켜주세요!");
        }


        /*
         * 생년월일 검증입니다.
         *
         */

        if (!isValidBirthDate(memberSignupRequestDto.getBirthDate())) {
//            handleMemberException("생년월일 양식을 지켜주세요!", HttpStatus.BAD_REQUEST, response);
            throw new IllegalArgumentException("생년월일 양식을 지켜주세요!");
        }


        /*
         * 닉네임 검증입니다.
         *
         */

        if (!isValidNickName(memberSignupRequestDto.getNickName())) {
//            handleMemberException("닉네임 양식을 지켜주세요!", HttpStatus.BAD_REQUEST, response);
            throw new IllegalArgumentException("닉네임 양식을 지켜주세요!");
        }


        /*
         * 비밀번호 검증입니다.
         *
         */

        if (!isValidPassword(memberSignupRequestDto.getPassword())) {
//            handleMemberException("비밀번호는 영어 대소문자, 숫자의 최소 8자에서 최대 12자리여야 합니다.", HttpStatus.BAD_REQUEST, response);
            throw new IllegalArgumentException("비밀번호는 영어 대소문자, 숫자의 최소 8자에서 최대 12자리여야 합니다.");
        }


        //빌더패턴으로 save를 위한 entity 생성
        Member member = Member.builder()
                .phoneNum(memberSignupRequestDto.getPhoneNum())
                .nickName(memberSignupRequestDto.getNickName())
                .birthDate(memberSignupRequestDto.getBirthDate())
                .profile(memberSignupRequestDto.getProfile())
                .password(passwordEncoder.encode(memberSignupRequestDto.getPassword()))
                .latitude(memberSignupRequestDto.getLatitude())
                .longitude(memberSignupRequestDto.getLongitude())
                .myGender(memberSignupRequestDto.getMyGender())
                .wantingMale(memberSignupRequestDto.isWantingMale())
                .wantingFemale(memberSignupRequestDto.isWantingFemale())
                .email("0")
                .build();

        //회원 저장
        memberRepository.save(member);

        //value에 작성된 출처에 브라우저가 리소스를 접근할 수 있도록 허용합니다.
        //*을 사용할 수 있을 것 같은데 일단 프론트 분들이 보통 쓰시는 포트번호로 열어두었습니다.
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        return new MemberResponseMsgDto("회원가입 성공!", HttpStatus.OK.value());
    }

    public MemberResponseMsgDto login(MemberLoginRequestDto memberLoginRequestDto, HttpServletResponse response) {
        String phoneNum = memberLoginRequestDto.getPhoneNum();
        String password = memberLoginRequestDto.getPassword();

        //사용자 확인 - 레디스에서 먼저 가져오고 없으면 DB 조회
        UserDetailsImpl member = redisRepository.getUser(phoneNum).orElseGet(
                () -> memberRepository.findByPhoneNum(phoneNum).map(UserDetailsImpl::fromEntity).orElseThrow(
                        () -> new IllegalArgumentException("등록된 사용자가 없습니다.")
                ));

        redisRepository.setUser(member);

        //비밀번호 확인
//        if (!passwordEncoder.matches(password, member.getPassword())) {
//            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
//        }
        //todo 주석해제

        //헤더에 토큰 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtUtil.createToken(member.getPhoneNum()));

        return new MemberResponseMsgDto("로그인 완료.", HttpStatus.OK.value());
    }

    //로그인한 내 정보 찾아오기 (Redis에서 먼저 검색 후 없으면 DB 접근)
    private Member findMember(String phoneNum) {
        UserDetailsImpl member = redisRepository.getUser(phoneNum).orElseGet(
                () -> memberRepository.findByPhoneNum(phoneNum).map(UserDetailsImpl::fromEntity).orElseThrow(
                        () -> new IllegalArgumentException("로그인을 해주세요.")
                ));

        return member.getMember();
    }

    private int roundDistance(double distance) {
        if (Math.round(distance) < 1) {
            return 1;
        }
        return (int) Math.round(distance);
    }
}
