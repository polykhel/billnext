package com.polykhel.billnext.repository;

import com.polykhel.billnext.domain.Wallet;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Wallet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    @Query("select wallet from Wallet wallet where wallet.user.login = ?#{principal.preferredUsername}")
    List<Wallet> findByUserIsCurrentUser();
}
