package clonecoding.tinder.matching.repository;

import clonecoding.tinder.matching.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, CustomRoomRepository {

    Optional<Room> findByMember1AndMember2(Long member1, Long member2);
}
