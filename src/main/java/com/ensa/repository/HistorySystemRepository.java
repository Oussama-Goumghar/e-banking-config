package com.ensa.repository;

import com.ensa.domain.HistorySystem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the HistorySystem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface HistorySystemRepository extends JpaRepository<HistorySystem, Long> {}
