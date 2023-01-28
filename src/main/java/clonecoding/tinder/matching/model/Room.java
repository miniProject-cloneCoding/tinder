package clonecoding.tinder.matching.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Room {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;
    private Long member1;
    private Long member2;

    public Room(Long member1, Long member2) {
        this.member1 = member1;
        this.member2 = member2;
    }
}
