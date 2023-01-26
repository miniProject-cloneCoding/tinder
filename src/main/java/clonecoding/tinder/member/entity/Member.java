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
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false)
    private String phoneNum;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = false)
    private String password;

//todo 주석풀기

    @Column(nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String profile;

//    @Column(nullable = false)
    private String email;

    private double latitude;
    private double longitude;

    public Member() {}

    public Member(String nickName, String phoneNum, String password, double latitude, double longitude, String birthDate, String profile) {
        this.phoneNum = phoneNum;
        this.nickName = nickName;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.birthDate = birthDate;
        this.profile = profile;
    }
}
