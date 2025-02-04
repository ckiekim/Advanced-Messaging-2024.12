package com.lion.ws.repository;

import com.lion.ws.entity.KisOAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KisOAuthTokenRepository extends JpaRepository<KisOAuthToken, Long> {
}
