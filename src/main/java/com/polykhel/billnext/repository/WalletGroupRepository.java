package com.polykhel.billnext.repository;

import com.polykhel.billnext.domain.WalletGroup;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the WalletGroup entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletGroupRepository extends JpaRepository<WalletGroup, Long>, JpaSpecificationExecutor<WalletGroup> {}
