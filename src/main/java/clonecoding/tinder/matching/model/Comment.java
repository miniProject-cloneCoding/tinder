package clonecoding.tinder.matching.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
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
