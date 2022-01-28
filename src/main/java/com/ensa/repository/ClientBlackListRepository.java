package com.ensa.repository;

import com.ensa.domain.ClientBlackList;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ClientBlackList entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ClientBlackListRepository extends JpaRepository<ClientBlackList, Long> {}
