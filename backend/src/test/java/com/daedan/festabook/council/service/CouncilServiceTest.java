package com.daedan.festabook.council.service;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.council.domain.Council;
import com.daedan.festabook.council.domain.CouncilFixture;
import com.daedan.festabook.council.dto.CouncilLoginRequest;
import com.daedan.festabook.council.dto.CouncilLoginRequestFixture;
import com.daedan.festabook.council.dto.CouncilLoginResponse;
import com.daedan.festabook.council.dto.CouncilRequest;
import com.daedan.festabook.council.dto.CouncilRequestFixture;
import com.daedan.festabook.council.dto.CouncilResponse;
import com.daedan.festabook.council.infrastructure.CouncilJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.security.util.JwtProvider;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouncilServiceTest {

    @Mock
    private CouncilJpaRepository councilJpaRepository;

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CouncilService councilService;

    @Nested
    class createCouncil {

        @Test
        void 성공() {
            // given
            Long festivalId = 1L;
            Festival festival = FestivalFixture.create(festivalId);
            String username = "test";
            String rawPassword = "1234";
            String encodedPassword = "{encoded}1234";
            Long councilId = 10L;
            Council council = CouncilFixture.create(councilId, festival, username, encodedPassword);

            given(councilJpaRepository.existsByUsername(username))
                    .willReturn(false);
            given(festivalJpaRepository.findById(festivalId))
                    .willReturn(Optional.of(festival));
            given(passwordEncoder.encode(rawPassword))
                    .willReturn(encodedPassword);
            given(councilJpaRepository.save(any(Council.class)))
                    .willReturn(council);

            CouncilRequest request = CouncilRequestFixture.create(festivalId, username, rawPassword);

            // when
            CouncilResponse result = councilService.createCouncil(request);

            // then
            ArgumentCaptor<Council> captor = forClass(Council.class);
            then(councilJpaRepository).should().existsByUsername(username);
            then(festivalJpaRepository).should().findById(festivalId);
            then(passwordEncoder).should().encode(rawPassword);
            then(councilJpaRepository).should().save(captor.capture());

            Council savedCouncil = captor.getValue();
            assertSoftly(s -> {
                s.assertThat(savedCouncil.getFestival()).isEqualTo(festival);
                s.assertThat(savedCouncil.getUsername()).isEqualTo(username);
                s.assertThat(savedCouncil.getPassword()).isEqualTo(encodedPassword);
                s.assertThat(result).isNotNull();
            });
        }

        @Test
        void 예외_중복_사용자명() {
            // given
            Long festivalId = 1L;
            String username = "user";
            String password = "1234";

            given(councilJpaRepository.existsByUsername(username))
                    .willReturn(true);

            CouncilRequest request = CouncilRequestFixture.create(festivalId, username, password);

            // when & then
            assertThatThrownBy(() -> councilService.createCouncil(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미 존재하는 사용자명입니다.");
        }

        @Test
        void 예외_존재하지_않는_축제() {
            // given
            Long invalidFestivalId = 0L;
            String username = "user";
            String password = "1234";

            given(councilJpaRepository.existsByUsername(username))
                    .willReturn(false);
            given(festivalJpaRepository.findById(invalidFestivalId))
                    .willReturn(Optional.empty());

            CouncilRequest request = CouncilRequestFixture.create(invalidFestivalId, username, password);

            // when & then
            assertThatThrownBy(() -> councilService.createCouncil(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class loginCouncil {

        @Test
        void 성공() {
            // given
            Long festivalId = 3L;
            String username = "user";
            String rawPassword = "1234";
            String encodedPassword = "{encoded}1234";
            String expectedToken = "token";

            Festival festival = FestivalFixture.create(festivalId);
            Council council = CouncilFixture.create(festival, username, encodedPassword);

            given(councilJpaRepository.findByUsername(username)).willReturn(Optional.of(council));
            given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);
            given(jwtProvider.createToken(username, festivalId)).willReturn(expectedToken);

            CouncilLoginRequest request = CouncilLoginRequestFixture.create(username, rawPassword);

            // when
            CouncilLoginResponse response = councilService.loginCouncil(request);

            // then
            then(passwordEncoder).should().matches(rawPassword, encodedPassword);
            then(jwtProvider).should().createToken(username, festivalId);
            assertThat(response).isNotNull();
        }

        @Test
        void 예외_존재하지_않는_학생회() {
            // given
            String username = "none";
            String password = "pass";

            given(councilJpaRepository.findByUsername(username))
                    .willReturn(Optional.empty());

            CouncilLoginRequest request = CouncilLoginRequestFixture.create(username, password);

            // when & then
            assertThatThrownBy(() -> councilService.loginCouncil(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 학생회입니다.");
        }

        @Test
        void 예외_비밀번호_불일치() {
            // given
            Long festivalId = 5L;
            String username = "user";
            String rawPassword = "wrong";
            String encodedPassword = "{encoded}1234";

            Festival festival = FestivalFixture.create(festivalId);
            Council council = CouncilFixture.create(festival, username, encodedPassword);

            given(councilJpaRepository.findByUsername(username))
                    .willReturn(Optional.of(council));
            given(passwordEncoder.matches(rawPassword, encodedPassword))
                    .willReturn(false);

            CouncilLoginRequest request = CouncilLoginRequestFixture.create(username, rawPassword);

            // when & then
            assertThatThrownBy(() -> councilService.loginCouncil(request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("비밀번호가 일치하지 않습니다.");
        }
    }
}
