package com.example.jpaRepository;

import com.example.model.CalmingSound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CalmingSoundRepository extends JpaRepository<CalmingSound, Long> {
    // No custom methods needed - just get all sounds
}