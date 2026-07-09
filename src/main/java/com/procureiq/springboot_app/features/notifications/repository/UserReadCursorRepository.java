package com.procureiq.springboot_app.features.notifications.repository;

import com.procureiq.springboot_app.features.notifications.entity.UserReadCursor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserReadCursorRepository extends JpaRepository<UserReadCursor, Long> {
    Optional<UserReadCursor> findByUserId(Long userId);
}
