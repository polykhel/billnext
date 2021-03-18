package com.polykhel.billnext.repository;

import com.polykhel.billnext.domain.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Wallet entity.
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("select wallet from Wallet wallet where wallet.user.login = ?#{principal.claims['preferred_username']}")
    Page<Wallet> findByUserIsCurrentUser(Pageable pageable);
}
