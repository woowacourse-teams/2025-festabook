package com.daedan.festabook.council.service;

import com.daedan.festabook.council.domain.Council;
import com.daedan.festabook.council.dto.CouncilLoginRequest;
import com.daedan.festabook.council.dto.CouncilLoginResponse;
import com.daedan.festabook.council.dto.CouncilRequest;
import com.daedan.festabook.council.dto.CouncilResponse;
import com.daedan.festabook.council.dto.CouncilUpdateRequest;
import com.daedan.festabook.council.dto.CouncilUpdateResponse;
import com.daedan.festabook.council.infrastructure.CouncilJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.security.role.RoleType;
import com.daedan.festabook.global.security.util.JwtProvider;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouncilService {

    private final CouncilJpaRepository councilJpaRepository;
    private final FestivalJpaRepository festivalJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public CouncilResponse createCouncil(CouncilRequest request) {
        validateUsername(request);

        Festival festival = getFestivalByFestivalId(request.festivalId());
        String encodedPassword = passwordEncoder.encode(request.password());

        Council council = new Council(
                festival,
                request.username(),
                encodedPassword
        );

        // TODO: 추후 회원가입 방식이 생긴다면 삭제
        council.updateRole(Set.of(RoleType.ROLE_COUNCIL));

        Council savedCouncil = councilJpaRepository.save(council);

        return CouncilResponse.from(savedCouncil);
    }

    @Transactional(readOnly = true)
    public CouncilLoginResponse loginCouncil(CouncilLoginRequest request) {
        Council council = getCouncilByUsername(request.username());

        validatePassword(request.password(), council);

        String accessToken = jwtProvider.createToken(council.getUsername(), council.getFestival().getId());

        return CouncilLoginResponse.from(council.getFestival().getId(), accessToken);
    }

    @Transactional
    public CouncilUpdateResponse updatePassword(Long councilId, CouncilUpdateRequest request) {
        Council council = getCouncilByCouncilId(councilId);

        validatePassword(request.currentPassword(), council);

        String encodedNewPassword = passwordEncoder.encode(request.newPassword());

        council.updatePassword(encodedNewPassword);

        return CouncilUpdateResponse.from(council);
    }

    // TODO: JWT 기반 로그아웃 구현

    private void validateUsername(CouncilRequest request) {
        if (councilJpaRepository.existsByUsername(request.username())) {
            throw new BusinessException("이미 존재하는 사용자명입니다.", HttpStatus.CONFLICT);
        }
    }

    private void validatePassword(String password, Council council) {
        if (!passwordEncoder.matches(password, council.getPassword())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }
    }

    private Council getCouncilByUsername(String username) {
        return councilJpaRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("존재하지 않는 학생회입니다.", HttpStatus.NOT_FOUND));
    }

    private Council getCouncilByCouncilId(Long councilId) {
        return councilJpaRepository.findById(councilId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 학생회입니다.", HttpStatus.NOT_FOUND));
    }

    private Festival getFestivalByFestivalId(Long festivalId) {
        return festivalJpaRepository.findById(festivalId)
                .orElseThrow(() -> new BusinessException("존재하지 않는 축제입니다.", HttpStatus.BAD_REQUEST));
    }
}
