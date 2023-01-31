package clonecoding.tinder.matching.repository;

import clonecoding.tinder.matching.model.Comment;
import clonecoding.tinder.matching.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByRoom(Room room);
}
