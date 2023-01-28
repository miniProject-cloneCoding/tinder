package clonecoding.tinder.matching.repository;

import clonecoding.tinder.matching.model.Room;
import clonecoding.tinder.members.entity.Member;
import lombok.RequiredArgsConstructor;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Optional;

@RequiredArgsConstructor
public class CustomRoomRepositoryImpl implements CustomRoomRepository{

    @PersistenceContext
    private final EntityManager em;

}
