package com.ucaba.reservas.repository;

import com.ucaba.reservas.model.ForecastSnapshot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForecastSnapshotRepository extends JpaRepository<ForecastSnapshot, Long> {
    List<ForecastSnapshot> findTop20ByOrderByGeneratedAtDesc();
}
