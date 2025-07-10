package com.library.user.repository;

import com.library.user.infrastructure.persistence.entity.RefreshToken;
import com.library.user.infrastructure.persistence.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndDeleteFlg(String token, boolean deleteFlg);

    List<RefreshToken> findByUser(User user);
    
    @Modifying
    void deleteByUser(User user);
} 