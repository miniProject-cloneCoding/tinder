package clonecoding.tinder.matching.service;

import clonecoding.tinder.matching.model.Comment;
import clonecoding.tinder.matching.model.dto.CommentRequestDto;
import clonecoding.tinder.matching.model.dto.CommentResponseDto;
import clonecoding.tinder.matching.repository.CommentRepository;
import clonecoding.tinder.members.dto.MembersResponseDto;
import clonecoding.tinder.members.entity.Member;
import clonecoding.tinder.members.repository.MemberRedisRepository;
import clonecoding.tinder.members.repository.MemberRepository;
import clonecoding.tinder.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final MemberRedisRepository redisRepository;

    //상대방과 내가 주고받은 모든 댓글 가져오기
    public List<CommentResponseDto> getComments(String phoneNum, CommentRequestDto requestDto) {

        Member my = findMember(phoneNum);
        Member oppositeMember = findOppositeMember(requestDto);

        //내가 상대방에게 쓴 댓글 가져오기
        List<Comment> send = commentRepository.findAllBySenderAndReceiver(my.getId(), requestDto.getOppositeMember());

        //상대방이 내개 보낸 댓글 가져오기
        List<Comment> received = commentRepository.findAllBySenderAndReceiver(requestDto.getOppositeMember(), my.getId());

        //댓글 내역 합치기
        List<Comment> comments = new ArrayList<>();
        comments.addAll(send);
        comments.addAll(received);

        log.info("총 댓글 갯수는 = {}", comments.size());

        //서로 보낸 댓글들을 작성일자 순서대로 정렬하여 return
        return comments.stream().map(comment -> CommentResponseDto.builder()
                .sender(findNameById(comment.getSender(), my, oppositeMember))
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build())
                .sorted(Comparator.comparing(CommentResponseDto::getCreatedAt))
                .collect(Collectors.toList());
    }

    //댓글 작성하기
    public CommentResponseDto createComments(String phoneNum, CommentRequestDto requestDto) {

        //내 정보 찾아오기
        Member my = findMember(phoneNum);

        //상대방 정보 찾아오기
        Member oppositeMember = findOppositeMember(requestDto);
        log.info("내가 댓글 보내는 상대방 id = {}", oppositeMember.getId());

        Comment comment = new Comment(requestDto.getContent(), my.getId(), requestDto.getOppositeMember());
        Comment savedComment = commentRepository.save(comment);
        return CommentResponseDto.builder()
                .sender(findNameById(savedComment.getSender(), my, oppositeMember))
                .content(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .build();
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
        if (Objects.equals(id, my.getId())) {
            return my.getNickName();
        }
        return oppositeMember.getNickName();
    }
}
