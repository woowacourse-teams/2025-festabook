package com.daedan.festabook.lostitem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;

import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.LostItemFixture;
import com.daedan.festabook.lostitem.dto.LostItemRequest;
import com.daedan.festabook.lostitem.dto.LostItemRequestFixture;
import com.daedan.festabook.lostitem.dto.LostItemResponse;
import com.daedan.festabook.lostitem.dto.LostItemResponses;
import com.daedan.festabook.lostitem.infrastructure.LostItemJpaRepository;
import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import java.util.List;
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
class LostItemServiceTest {

    private static final Long DEFAULT_ORGANIZATION_ID = 1L;

    @Mock
    private LostItemJpaRepository lostItemJpaRepository;

    @Mock
    private OrganizationJpaRepository organizationJpaRepository;

    @InjectMocks
    private LostItemService lostItemService;

    @Nested
    class createLostItem {

        @Test
        void 성공() {
            // given
            LostItemRequest request = LostItemRequestFixture.create(
                    "https://example.com/image.jpg",
                    "서울특별시 강남구"
            );

            Organization organization = OrganizationFixture.create(DEFAULT_ORGANIZATION_ID);

            given(organizationJpaRepository.findById(DEFAULT_ORGANIZATION_ID))
                    .willReturn(Optional.of(organization));

            // when
            LostItemResponse result = lostItemService.createLostItem(DEFAULT_ORGANIZATION_ID, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.imageUrl()).isEqualTo(request.imageUrl());
                s.assertThat(result.storageLocation()).isEqualTo(request.storageLocation());
            });
        }

        @Test
        void 예외_존재하지_않는_조직_ID() {
            // given
            Long invalidOrganizationId = 0L;
            LostItemRequest request = LostItemRequestFixture.create();

            given(organizationJpaRepository.findById(invalidOrganizationId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> lostItemService.createLostItem(invalidOrganizationId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 조직입니다.");
        }
    }

    @Nested
    class getAllLostItemByOrganizationId {

        @Test
        void 성공() {
            // given
            LostItem lostItem1 = LostItemFixture.create();
            LostItem lostItem2 = LostItemFixture.create();
            List<LostItem> lostItems = List.of(lostItem1, lostItem2);

            given(lostItemJpaRepository.findAllByOrganizationId(DEFAULT_ORGANIZATION_ID))
                    .willReturn(lostItems);

            // when
            LostItemResponses result = lostItemService.getAllLostItemByOrganizationId(DEFAULT_ORGANIZATION_ID);

            // then
            assertThat(result.responses()).hasSize(2);
        }
    }
}
