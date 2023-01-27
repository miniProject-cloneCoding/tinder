package clonecoding.tinder.like.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long likedMember;

    @Column(nullable = false)
    private int count =1;

    public Ranking(Long likedMember) {
        this.likedMember = likedMember;
    }

    public void liked() {
        this.count +=1;
    }
    public void disLiked() {
        this.count -=1;
    }
}
