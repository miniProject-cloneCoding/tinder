package clonecoding.tinder.matching.service;

import clonecoding.tinder.matching.model.Comments;
import clonecoding.tinder.matching.model.dto.CommentRequestDto;
import clonecoding.tinder.matching.model.dto.CommentResponseDto;
import clonecoding.tinder.matching.repository.MatchingRedisRepository;
import clonecoding.tinder.members.entity.Member;
import clonecoding.tinder.members.repository.MemberRedisRepository;
import clonecoding.tinder.members.repository.MemberRepository;
import clonecoding.tinder.security.UserDetailsImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final MemberRedisRepository redisRepository;
    private final MatchingRedisRepository matchingRedisRepository;

    //상대방과 내가 주고받은 모든 댓글 가져오기
    public List<CommentResponseDto> getComments(String phoneNum, CommentRequestDto requestDto) {

        Member my = findMember(phoneNum);
        Member oppositeMember = findOppositeMember(requestDto);

        //해당 대화방에서 주고받은 모든 댓글 가져오기
        Optional<Comments> comments = matchingRedisRepository.getComments(requestDto.getRoomId());

        //댓글이 하나도 없는 경우에는 대화 상대방과 나의 기본 정보만 반환한다
        if (comments.isEmpty()) {
            List<CommentResponseDto> result = new ArrayList<>();
            result.add(new CommentResponseDto(my.getNickName(), null, null, requestDto.getRoomId(),
                    oppositeMember.getNickName(), oppositeMember.getProfile(), my.getNickName(), my.getProfile()));
            return result;
        }

        Comments result = new Comments();
        result = comments.get();

        //서로 보낸 댓글들을 작성일자 순서대로 정렬하여 return
        return getComments(result);
    }

    //댓글 작성하기
    public CommentResponseDto createComments(String phoneNum, CommentRequestDto requestDto) {

        //내 정보 찾아오기
        Member my = findMember(phoneNum);

        //상대방 정보 찾아오기
        Member oppositeMember = findOppositeMember(requestDto);
        log.info("내가 댓글 보내는 상대방 id = {}", oppositeMember.getId());

        LocalDateTime now = LocalDateTime.now();

        //LocalDateTime 직렬화 해주기
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        try {
            String jsonStr = objectMapper.writeValueAsString(now);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //작성한 댓글로 Dto 만들기
        CommentResponseDto commentResponseDto = new CommentResponseDto(my.getNickName(), requestDto.getContent(), now, requestDto.getRoomId(),
                oppositeMember.getNickName(), oppositeMember.getProfile(), my.getNickName(), my.getProfile());

        //redis에서 Comments (commentsDto 리스트) 가져오기
        Optional<Comments> comments = matchingRedisRepository.getComments(requestDto.getRoomId());

        List<CommentResponseDto> dtoList = new ArrayList<>();

        Comments saveComments = new Comments();

        //redis에서 가져온 정보가 있으면
        if (comments.isPresent()) {
            dtoList = comments.get().getComments(); //댓글 목록을 가져와서
            dtoList.add(commentResponseDto); //작성한 댓글을 추가해주고
            comments.get().setComments(dtoList); //추가한 목록을 다시 저장한다
            saveComments = comments.get();
        } else { //redis에서 가져온 정보가 없으면 새로운 ArrayList에 새 댓글을 추가한다
            dtoList.add(commentResponseDto);
            saveComments.setComments(dtoList);
        }

        //대화방 id와 댓글 목록이 들어있는 saveComments를 redis에 저장
        matchingRedisRepository.setComments(saveComments, requestDto.getRoomId());
        return commentResponseDto;
    }

    //상대방 정보 찾기
    private Member findOppositeMember(CommentRequestDto requestDto) {
        return memberRepository.findById(requestDto.getOppositeMember()).orElseThrow(
                () -> new IllegalArgumentException("일치하는 회원이 없습니다"));
    }

    //로그인한 내 정보를 phoneNum 으로 찾아오기 (Redis에서 먼저 검색 후 없으면 DB 접근)
    private Member findMember(String phoneNum) {
        UserDetailsImpl member = redisRepository.getUser(phoneNum).orElseGet(
                () -> memberRepository.findByPhoneNum(phoneNum).map(UserDetailsImpl::fromEntity).orElseThrow(
                        () -> new IllegalArgumentException("로그인을 해주세요.")
                ));

        return member.getMember();
    }

    //나와 상대방의 id를 가지고 nickName 반환
    private String findNameById(Long id, Member my, Member oppositeMember) {
        if (id == my.getId()) {
            return my.getNickName();
        }
        return oppositeMember.getNickName();
    }

    //redis에서 해당 roomId의 Comments 가져옴(Response dto List 포함되어있음)
    private List<CommentResponseDto> getComments(Comments comments) {
        return comments.getComments().stream()
                .sorted(Comparator.comparing(CommentResponseDto::getCreatedAt))
                .collect(Collectors.toList());
    }

}
