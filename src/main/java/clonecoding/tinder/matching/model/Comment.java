package clonecoding.tinder.matching.model;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Comment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    private String content;
    private Long sender;
    private Long receiver;
    @CreatedDate
    private LocalDateTime createdAt;

    public Comment(String content, Long sender, Long receiver) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
    }
}
