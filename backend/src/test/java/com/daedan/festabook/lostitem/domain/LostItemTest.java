package com.daedan.festabook.lostitem.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LostItemTest {

    private static final int MAX_STORAGE_LOCATION_LENGTH = 20;

    @Nested
    class validateLostItem {

        @Test
        void 성공() {
            // given
            String imageUri = "https://www.test.com/image.png";
            String storageLocation = "총학생회 사무실";
            PickupStatus pickupStatus = PickupStatus.COMPLETED;

            // when & then
            assertThatCode(() ->
                    LostItemFixture.create(
                            imageUri,
                            storageLocation,
                            pickupStatus
                    )).doesNotThrowAnyException();
        }
    }

    @Nested
    class validateImageUrl {

        @Test
        void 성공() {
            // given
            String imageUrl = "https://www.test.com/image.png";

            // when & then
            assertThatCode(() -> LostItemFixture.createWithImageUrl(imageUrl))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "URL: {0}")
        @CsvSource(value = {
                "null",
                "'    '"
        }, nullValues = "null")
        void 예외_URL_null_혹은_빈문자열(String invalidUrl) {
            assertThatThrownBy(() -> LostItemFixture.createWithImageUrl(invalidUrl))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("이미지 URL은 비어 있을 수 없습니다.");
        }
    }

    @Nested
    class validateStorageLocation {

        @Test
        void 성공() {
            // given
            String storageLocation = "총학생회 사무실";

            // when & then
            assertThatCode(() -> LostItemFixture.createWithStorageLocation(storageLocation))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "보관 장소: {0}")
        @CsvSource(value = {
                "null",
                "'    '"
        }, nullValues = "null")
        void 예외_보관장소_null_혹은_빈문자열(String invalidStorageLocation) {
            assertThatThrownBy(() -> LostItemFixture.createWithStorageLocation(invalidStorageLocation))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("보관 장소는 비어 있을 수 없습니다.");
        }

        @ParameterizedTest(name = "보관 장소 문자열 길이: {0}")
        @ValueSource(ints = {10, 20})
        void 성공_보관_장소_문자열_길이_이하(int storageLocationLength) {
            // given
            String storageLocation = "a".repeat(storageLocationLength);

            // when & then
            assertThatCode(() -> LostItemFixture.createWithStorageLocation(storageLocation))
                    .doesNotThrowAnyException();
        }

        @ParameterizedTest(name = "보관 장소 문자열 길이: {0}")
        @ValueSource(ints = {21, 30})
        void 예외_보관_장소_문자열_길이_초과(int invalidStorageLocationLength) {
            // given
            String storageLocation = "a".repeat(invalidStorageLocationLength);

            // when & then
            assertThatThrownBy(() -> LostItemFixture.createWithStorageLocation(storageLocation))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(String.format("보관 장소는 %d자를 초과할 수 없습니다.", MAX_STORAGE_LOCATION_LENGTH));
        }
    }

    @Nested
    class validatePickupStatus {

        @Test
        void 성공() {
            // given
            PickupStatus pickupStatus = PickupStatus.COMPLETED;

            // when & then
            assertThatCode(() -> LostItemFixture.createWithPickupStatus(pickupStatus))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_수령_상태_null() {
            // given
            PickupStatus pickupStatus = null;

            // when & then
            assertThatThrownBy(() -> LostItemFixture.createWithPickupStatus(pickupStatus))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("수령 상태는 null일 수 없습니다.");
        }
    }
}
