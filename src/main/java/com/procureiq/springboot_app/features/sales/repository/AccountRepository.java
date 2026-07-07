package com.procureiq.springboot_app.features.sales.repository;

import com.procureiq.springboot_app.features.sales.types.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByIsDeletedFalse();
}
