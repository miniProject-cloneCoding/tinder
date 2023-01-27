package clonecoding.tinder.like.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
@Getter
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
