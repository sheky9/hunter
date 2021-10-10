package com.abhisheksoni.hunter.repository;

import com.abhisheksoni.hunter.entity.Keyword;
import com.abhisheksoni.hunter.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    Optional<Keyword> findByName(String name);
    List<Keyword> findByUser(User user);
}
