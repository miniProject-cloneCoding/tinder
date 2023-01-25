package clonecoding.tinder.member.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String profile;

    @Column(nullable = false)
    private String email;

    public Member() {}
}
