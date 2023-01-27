package clonecoding.tinder.like.repository;

import clonecoding.tinder.like.entity.Likes;

import java.util.List;

public interface CustomLikeRepository {


    List<Likes> findMyLiking(Long phoneNum);

    List<Likes> findMyLiked(Long phoneNum);
}
