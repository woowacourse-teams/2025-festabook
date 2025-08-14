package com.daedan.festabook.council.infrastructure;

import com.daedan.festabook.council.domain.Council;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouncilJpaRepository extends JpaRepository<Council, Long> {

    Optional<Council> findByUsername(String username);
}
