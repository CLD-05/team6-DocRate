package com.team.docrate.domain.user.repository;

import com.team.docrate.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
    
    boolean existsByNicknameAndEmailNot(String nickname, String email);

    Optional<User> findByEmail(String email);
}
