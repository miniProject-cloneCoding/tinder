package clonecoding.tinder.like.entity;

import clonecoding.tinder.member.entity.Member;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member likedMember;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member likingMember;

    public Likes(Member likedMember, Member likingMember) {
        this.likedMember = likedMember;
        this.likingMember = likingMember;
    }
}
