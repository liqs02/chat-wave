package com.chatwave.authservice.repository;

import com.chatwave.authservice.domain.session.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long>  {
    Optional<Session> findByAccessToken(String accessToken);

    Optional<Session> findByRefreshToken(String refreshToken);

    Optional<Session> findNotExpiredByIdAndUserId(Long sessionId, Integer userId);

    @Query("SELECT s FROM Session s WHERE s.accessToken = ?1 AND CURRENT_TIMESTAMP < s.accessTokenExpireDate AND CURRENT_DATE < s.expireDate")
    Optional<Session> findNotExpiredByAccessToken(String accessToken);

    /**
     * @return all expired sessions which accessToken or refreshToken is not null
     */
    @Query("SELECT s FROM Session s WHERE s.expireDate <= CURRENT_DATE AND (s.accessToken != null or s.refreshToken != null)")
    List<Session> findAllExpiredNotCleaned();

    @Query("SELECT s FROM Session s WHERE s.user.id = ?1 AND CURRENT_DATE < s.expireDate")
    List<Session> findAllNotExpiredByUserId(Integer userId);
}

