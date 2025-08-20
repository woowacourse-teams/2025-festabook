package com.daedan.festabook.council.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.global.security.role.RoleType;
import java.util.EnumSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CouncilTest {

    @Nested
    class validateCouncil {

        @Test
        void 성공() {
            // given
            Festival festival = FestivalFixture.create();
            String username = "council";
            String password = "1234";

            // when & then
            assertThatCode(() -> new Council(festival, username, password))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class validateFestival {

        @Test
        void 예외_축제_null() {
            // given
            Festival festival = null;

            // when & then
            assertThatThrownBy(() -> CouncilFixture.create(festival))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("축제는 비어 있을 수 없습니다.");
        }
    }

    @Nested
    class validateUsername {

        @Test
        void 성공_아이디_정상문자열() {
            // given
            String username = "1234";

            // when & then
            assertThatCode(() -> CouncilFixture.createWithUsername(username))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_아이디_null() {
            // given
            String username = null;

            // when & then
            assertThatThrownBy(() -> CouncilFixture.createWithUsername(username))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("아이디는 비어 있을 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        void 예외_아이디_공백문자열(String blank) {
            // given
            String username = blank;

            // when & then
            assertThatThrownBy(() -> CouncilFixture.createWithUsername(username))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("아이디는 비어 있을 수 없습니다.");
        }
    }

    @Nested
    class validatePassword {

        @Test
        void 성공_비밀번호_정상문자열() {
            // given
            String password = "1234";

            // when & then
            assertThatCode(() -> CouncilFixture.createWithPassword(password))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_비밀번호_null() {
            // given
            String password = null;

            // when & then
            assertThatThrownBy(() -> CouncilFixture.createWithPassword(password))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("비밀번호는 비어 있을 수 없습니다.");
        }

        @ParameterizedTest
        @ValueSource(strings = {" ", "   ", "\t", "\n"})
        void 예외_비밀번호_공백문자열(String blank) {
            // given
            String password = blank;

            // when & then
            assertThatThrownBy(() -> CouncilFixture.createWithPassword(password))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("비밀번호는 비어 있을 수 없습니다.");
        }
    }

    @Nested
    class updateRole {

        @Test
        void 성공_역할_추가() {
            // given
            Council council = CouncilFixture.create();

            RoleType councilRole = RoleType.ROLE_COUNCIL;
            RoleType adminRole = RoleType.ROLE_ADMIN;
            Set<RoleType> roles = Set.of(councilRole, adminRole);

            // when
            council.updateRole(roles);

            // then
            assertThat(council.getRoles())
                    .containsExactlyInAnyOrder(councilRole, adminRole);
        }

        @Test
        void 성공_중복추가시_중복없음() {
            // given
            Council council = CouncilFixture.create();

            // when
            council.updateRole(EnumSet.of(RoleType.ROLE_COUNCIL));
            council.updateRole(EnumSet.of(RoleType.ROLE_COUNCIL));

            // then
            assertThat(council.getRoles())
                    .containsExactly(RoleType.ROLE_COUNCIL);
        }
    }
}
