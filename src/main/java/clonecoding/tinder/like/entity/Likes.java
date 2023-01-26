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

    private Long likedMember;

    private Long likingMember;

    public Likes(Long likedMember, Long likingMember) {
        this.likedMember = likedMember;
        this.likingMember = likingMember;
    }

    public Long getLikedMember() {
        return likedMember;
    }
}
