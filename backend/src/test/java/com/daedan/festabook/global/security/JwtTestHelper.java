package com.daedan.festabook.global.security;

import com.daedan.festabook.council.domain.Council;
import com.daedan.festabook.council.infrastructure.CouncilJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.security.role.RoleType;
import com.daedan.festabook.global.security.util.JwtProvider;
import jakarta.transaction.Transactional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTestHelper {

    private final CouncilJpaRepository councilRepository;
    private final FestivalJpaRepository festivalRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public String createCouncilAndLogin(Festival festival) {
        UUID uuid = UUID.randomUUID();
        String randomUsername = "test_" + uuid;
        String randomPassword = "password_" + uuid;

        festivalRepository.save(festival);
        Council council = new Council(festival, randomUsername, randomPassword);
        council.updateRole(Set.of(RoleType.ROLE_COUNCIL));
        councilRepository.save(council);

        return jwtProvider.createToken(randomUsername, festival.getId());
    }
}
