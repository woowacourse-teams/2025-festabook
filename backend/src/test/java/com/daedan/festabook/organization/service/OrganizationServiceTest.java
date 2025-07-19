package com.daedan.festabook.organization.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.dto.OrganizationGeographyResponse;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @Nested
    class getOrganizationGeographyByOrganizationId {

        @Test
        void 성공() {
            // given
            Long organizationId = 1L;
            Organization organization = OrganizationFixture.create(organizationId);

            given(organizationJpaRepository.findById(organizationId))
                    .willReturn(Optional.of(organization));

            OrganizationGeographyResponse expected = OrganizationGeographyResponse.from(organization);

            // when
            OrganizationGeographyResponse result =
                    organizationService.getOrganizationGeographyByOrganizationId(organizationId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        void 예외_주어진_id의_조직이_존재하지_않는다면_예외가_발생한다() {
            // given
            Long notExistsId = 1L;

            given(organizationJpaRepository.findById(notExistsId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> {
                organizationService.getOrganizationGeographyByOrganizationId(notExistsId);
            }).isInstanceOf(BusinessException.class)
                    .hasMessage("조직이 존재하지 않습니다.");
        }
    }
}
