package com.daedan.festabook.global.security.council;

import com.daedan.festabook.council.domain.Council;
import com.daedan.festabook.council.infrastructure.CouncilJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouncilDetailsService implements UserDetailsService {

    private final CouncilJpaRepository councilJpaRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Council council = councilJpaRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("존재하지 않는 학생회입니다.", HttpStatus.NOT_FOUND));

        return new CouncilDetails(council);
    }
}
