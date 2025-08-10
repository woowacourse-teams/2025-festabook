package com.daedan.festabook.lostitem.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.global.exception.BusinessException;
import com.daedan.festabook.lostitem.domain.LostItem;
import com.daedan.festabook.lostitem.domain.LostItemFixture;
import com.daedan.festabook.lostitem.domain.PickupStatus;
import com.daedan.festabook.lostitem.dto.LostItemRequest;
import com.daedan.festabook.lostitem.dto.LostItemRequestFixture;
import com.daedan.festabook.lostitem.dto.LostItemResponse;
import com.daedan.festabook.lostitem.dto.LostItemResponses;
import com.daedan.festabook.lostitem.dto.LostItemStatusUpdateRequest;
import com.daedan.festabook.lostitem.dto.LostItemStatusUpdateRequestFixture;
import com.daedan.festabook.lostitem.dto.LostItemStatusUpdateResponse;
import com.daedan.festabook.lostitem.dto.LostItemUpdateResponse;
import com.daedan.festabook.lostitem.infrastructure.LostItemJpaRepository;
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

    private static final Long DEFAULT_FESTIVAL_ID = 1L;

    @Mock
    private LostItemJpaRepository lostItemJpaRepository;

    @Mock
    private FestivalJpaRepository festivalJpaRepository;

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

            Festival festival = FestivalFixture.create(DEFAULT_FESTIVAL_ID);

            given(festivalJpaRepository.findById(DEFAULT_FESTIVAL_ID))
                    .willReturn(Optional.of(festival));

            // when
            LostItemResponse result = lostItemService.createLostItem(DEFAULT_FESTIVAL_ID, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.imageUrl()).isEqualTo(request.imageUrl());
                s.assertThat(result.storageLocation()).isEqualTo(request.storageLocation());
            });
        }

        @Test
        void 예외_존재하지_않는_축제_ID() {
            // given
            Long invalidFestivalId = 0L;
            LostItemRequest request = LostItemRequestFixture.create();

            given(festivalJpaRepository.findById(invalidFestivalId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> lostItemService.createLostItem(invalidFestivalId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 축제입니다.");
        }
    }

    @Nested
    class getAllLostItemByFestivalId {

        @Test
        void 성공() {
            // given
            LostItem lostItem1 = LostItemFixture.create();
            LostItem lostItem2 = LostItemFixture.create();
            List<LostItem> lostItems = List.of(lostItem1, lostItem2);

            given(lostItemJpaRepository.findAllByFestivalId(DEFAULT_FESTIVAL_ID))
                    .willReturn(lostItems);

            int expectedSize = 2;

            // when
            LostItemResponses result = lostItemService.getAllLostItemByFestivalId(DEFAULT_FESTIVAL_ID);

            // then
            assertThat(result.responses()).hasSize(expectedSize);
        }
    }

    @Nested
    class updateLostItem {

        @Test
        void 성공() {
            // given
            Long lostItemId = 1L;
            String previousImageUrl = "https://example.com/previous.jpg";
            String previousStorageLocation = "서울특별시 강남구";
            LostItem lostItem = LostItemFixture.create(lostItemId, previousImageUrl, previousStorageLocation);

            LostItemRequest request = LostItemRequestFixture.create(
                    "https://example.com/change.jpg",
                    "변화 보관소"
            );

            given(lostItemJpaRepository.findById(lostItemId))
                    .willReturn(Optional.of(lostItem));

            // when
            LostItemUpdateResponse result = lostItemService.updateLostItem(lostItemId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.lostItemId()).isEqualTo(lostItemId);
                s.assertThat(result.imageUrl()).isEqualTo(request.imageUrl());
                s.assertThat(result.storageLocation()).isEqualTo(request.storageLocation());
            });
        }

        @Test
        void 예외_존재하지_않는_분실물_ID() {
            // given
            Long invalidLostItemId = 0L;
            LostItemRequest request = LostItemRequestFixture.create();

            given(lostItemJpaRepository.findById(invalidLostItemId))
                    .willReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> lostItemService.updateLostItem(invalidLostItemId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 분실물입니다.");
        }
    }

    @Nested
    class updateLostItemStatus {

        @Test
        void 성공() {
            // given
            Long lostItemId = 1L;
            PickupStatus previousStatus = PickupStatus.PENDING;
            LostItem lostItem = LostItemFixture.create(lostItemId, previousStatus);

            PickupStatus updateStatus = PickupStatus.COMPLETED;
            LostItemStatusUpdateRequest request = LostItemStatusUpdateRequestFixture.create(updateStatus);

            given(lostItemJpaRepository.findById(lostItemId))
                    .willReturn(Optional.of(lostItem));

            // when
            LostItemStatusUpdateResponse result = lostItemService.updateLostItemStatus(lostItemId, request);

            // then
            assertSoftly(s -> {
                s.assertThat(result.lostItemId()).isEqualTo(lostItemId);
                s.assertThat(result.pickupStatus()).isEqualTo(request.pickupStatus());
            });
        }

        @Test
        void 예외_존재하지_않는_분실물_ID() {
            // given
            Long invalidLostItemId = 0L;
            LostItemStatusUpdateRequest request = LostItemStatusUpdateRequestFixture.create();

            given(lostItemJpaRepository.findById(invalidLostItemId))
                    .willReturn(Optional.empty());

            // then
            assertThatThrownBy(() -> lostItemService.updateLostItemStatus(invalidLostItemId, request))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("존재하지 않는 분실물입니다.");
        }
    }

    @Nested
    class deleteLostItemByLostItemId {

        @Test
        void 성공() {
            // given
            Long lostItemId = 1L;

            // when
            lostItemService.deleteLostItemByLostItemId(lostItemId);

            // then
            then(lostItemJpaRepository).should()
                    .deleteById(lostItemId);
        }
    }
}
