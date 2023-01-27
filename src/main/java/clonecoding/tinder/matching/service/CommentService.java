package clonecoding.tinder.matching.service;

import clonecoding.tinder.matching.model.Comment;
import clonecoding.tinder.matching.model.dto.CommentRequestDto;
import clonecoding.tinder.matching.model.dto.CommentResponseDto;
import clonecoding.tinder.matching.repository.CommentRepository;
import clonecoding.tinder.members.dto.MembersResponseDto;
import clonecoding.tinder.members.entity.Member;
import clonecoding.tinder.members.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    //상대방과 나 사이의 모든 댓글 가져오기
    public List<CommentResponseDto> getComments(String phoneNum, CommentRequestDto requestDto) {

        Member my = findMember(phoneNum);

        //내가 상대방에게 쓴 댓글 가져오기
        List<Comment> send = commentRepository.findAllBySenderAndReceiver(my.getId(), requestDto.getOppositeMember());

        //상대방이 내개 보낸 댓글 가져오기
        List<Comment> received = commentRepository.findAllBySenderAndReceiver(requestDto.getOppositeMember(), my.getId());

        //댓글 내역 합치기
        List<Comment> comments = new ArrayList<>();
        comments.addAll(send);
        comments.addAll(received);

        //서로 보낸 댓글들을 시간 순서대로 정렬하여 return
        return comments.stream().map(comment -> CommentResponseDto.builder()
                .sender(comment.getSender())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build())
                .sorted(Comparator.comparing(CommentResponseDto::getCreatedAt))
                .collect(Collectors.toList());
    }

    //댓글 작성하기
    public CommentResponseDto createComments(String phoneNum, CommentRequestDto requestDto) {
        Member my = findMember(phoneNum);

        Comment comment = new Comment(requestDto.getContent(), my.getId(), requestDto.getOppositeMember());
        Comment savedComment = commentRepository.save(comment);
        return CommentResponseDto.builder()
                .sender(savedComment.getSender())
                .content(savedComment.getContent())
                .createdAt(savedComment.getCreatedAt())
                .build();
    }

    //로그인한 사용자 정보 찾아오기
    private Member findMember(String phoneNum) {
        return memberRepository.findByPhoneNum(phoneNum).orElseThrow(() -> new IllegalArgumentException("로그인을 해주세요"));
    }

}
