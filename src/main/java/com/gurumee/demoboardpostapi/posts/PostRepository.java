package com.gurumee.demoboardpostapi.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();
    List<Post> findByOwnerNameOrderByCreatedAtDesc(@Param("username") String username);
    List<Post> findByTitleContainingOrContentContainingOrderByCreatedAtDesc(String title, String content);
}
