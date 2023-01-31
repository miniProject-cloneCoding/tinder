package clonecoding.tinder.matching.service;

import clonecoding.tinder.matching.model.Comment;
import clonecoding.tinder.matching.model.Comments;
import clonecoding.tinder.matching.model.Room;
import clonecoding.tinder.matching.model.dto.CommentRequestDto;
import clonecoding.tinder.matching.model.dto.CommentResponseDto;
import clonecoding.tinder.matching.model.dto.ProfileResponseDto;
import clonecoding.tinder.matching.repository.CommentRepository;
import clonecoding.tinder.matching.repository.RoomRepository;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final MemberRedisRepository redisRepository;
    private final RoomRepository roomRepository;
    private final CommentRepository commentRepository;

    //상대방과 내가 주고받은 모든 댓글 가져오기
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(String phoneNum, Long roomId) {
        log.info("댓글 조회하기 실행");

        //방 정보 가져오기
        Room room = findRoom(roomId);

        //내 정보와 상대방 정보 찾아오기
        Member my = findMember(phoneNum);
        Member oppositeMember = findOppositeMember(getOppositeMemberId(room, my));

        //해당 대화방 참여자가 아닌 경우
        if (!isRoomMember(my.getId(), oppositeMember.getId())) {
            throw new IllegalArgumentException("대화방에 참여할 수 없습니다");
        }

        //해당 대화방에서 주고받은 모든 댓글 가져오기
        List<Comment> comments = commentRepository.findByRoom(room);

        //서로 보낸 댓글들을 작성일자 순서대로 정렬하여 return
        return convertToDto(comments, my);
    }

    //댓글 작성하기
    @Transactional
    public void createComments(String phoneNum, CommentRequestDto requestDto) {
        log.info("댓글 작성하기 실행");

        //내 정보 찾아오기
        Member my = findMember(phoneNum);

        Room room = findRoom(requestDto.getRoomId());

        //상대방 정보 찾아오기
        Member oppositeMember = findOppositeMember(getOppositeMemberId(room, my));

        log.info("내가 댓글 보내는 상대방 id = {}", oppositeMember.getId());

        if (!isRoomMember(my.getId(), oppositeMember.getId())) {
            throw new IllegalArgumentException("매칭되지 않은 상대에게는 댓글을 작성할 수 없습니다");
        }

        Comment comment = new Comment(my.getNickName(), requestDto.getContent(), room);
        commentRepository.save(comment);
    }

    //댓글 수정하기
    @Transactional
    public void updateComments(String phoneNum, CommentRequestDto requestDto, Long commentId) {
        //내 정보 찾아오기
        Comment comment = getComment(phoneNum, commentId);

        comment.setContent(requestDto.getContent());

        //댓글 저장
        commentRepository.save(comment);
    }

    //댓글 삭제하기
    @Transactional
    public void deleteComments(String phoneNum, Long commentId) {
        //댓글 찾아오기
        Comment comment = getComment(phoneNum, commentId);

        commentRepository.delete(comment);
    }

    //상대방과 나의 프로필 정보 가져오기
    @Transactional(readOnly = true)
    public ProfileResponseDto getProfile(String phoneNum, Long roomId) {
        //내 정보 찾아오기
        Member my = findMember(phoneNum);

        Room room = findRoom(roomId);

        //상대방 정보 찾아오기
        Member oppositeMember = findOppositeMember(getOppositeMemberId(room, my));

        return ProfileResponseDto.builder()
                .myName(my.getNickName())
                .myProfile(my.getProfile())
                .yourName(oppositeMember.getNickName())
                .yourProfile(oppositeMember.getProfile())
                .build();
    }

    /*
     댓글 찾아오기
     댓글 작성자와 로그인한 내 정보가 일치하는 경운에만 comment 가져온다
     */
    private Comment getComment(String phoneNum, Long commentId) {
        Member my = findMember(phoneNum);

        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재하지 않습니다")
        );

        //작성자가 일치하지 않는 경우
        if (!comment.getSender().equals(my.getNickName())) {
            throw new IllegalArgumentException("본인의 댓글만 수정할 수 있습니다");
        }
        return comment;
    }

    //상대방 정보 찾기
    private Member findOppositeMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
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

    //Comnment entity 리스트를 Dto로 변환
    private List<CommentResponseDto> convertToDto(List<Comment> comments, Member my) {
        return comments.stream().map(comment -> CommentResponseDto.builder()
                        .commentId(comment.getCommentId())
                        .sender(comment.getSender())
                        .content(comment.getContent())
                        .createdAt(comment.getCreatedAt())
                        .roomId(comment.getRoom().getId())
                        .status(isMyComment(comment, my.getNickName()))
                        .build())
                .sorted(Comparator.comparing(CommentResponseDto::getCreatedAt))
                .collect(Collectors.toList());
    }

    //해당 유저가 대화방에 참여할 자격이 있는지 확인
    private boolean isRoomMember(Long member1, Long member2) {
        return roomRepository.findByMember1AndMember2(member1, member2).isPresent() ||
                roomRepository.findByMember1AndMember2(member2, member1).isPresent();
    }

    //방 정보를 바탕으로 상대방 id 찾기
    private static Long getOppositeMemberId(Room room, Member my) {
        return room.getMember1() == my.getId() ? room.getMember2() : room.getMember1();
    }

    //대화방 id로 대화방 찾아오기
    private Room findRoom(Long roomId) {
        return roomRepository.findById(roomId).orElseThrow(
                () -> new IllegalArgumentException("댓글방이 존재하지 않습니다.")
        );
    }

    // comment가 내가 작성한 것인지 여부를 확인
    private boolean isMyComment(Comment comment, String myName) {
        return comment.getSender().equals(myName);
    }
}
