package com.daedan.festabook.lostitem.domain;

import static org.assertj.core.api.Assertions.assertThat;
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
import org.springframework.http.HttpStatus;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class LostItemTest {

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
                    .hasMessage("이미지 URL은 비어 있을 수 없습니다.")
                    .extracting("status")
                    .isEqualTo(HttpStatus.BAD_REQUEST);
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
                    .hasMessage("보관 장소는 비어 있을 수 없습니다.")
                    .extracting("status")
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @ParameterizedTest(name = "보관 장소 문자열 길이: {0}")
        @ValueSource(ints = {10, 20})
        void 성공_보관_장소_문자열_길이_이하(int storageLocationLength) {
            // given
            String storageLocation = "a".repeat(storageLocationLength);

            // when
            LostItem result = LostItemFixture.createWithStorageLocation(storageLocation);

            // then
            assertThat(result.getStorageLocation()).isEqualTo(storageLocation);
        }

        @ParameterizedTest(name = "보관 장소 문자열 길이: {0}")
        @ValueSource(ints = {21, 30})
        void 예외_보관_장소_문자열_길이_초과(int invalidStorageLocationLength) {
            String storageLocation = "a".repeat(invalidStorageLocationLength);
            assertThatThrownBy(() -> LostItemFixture.createWithStorageLocation(storageLocation))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("보관 장소는 20자를 초과할 수 없습니다.")
                    .extracting("status")
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }

    @Nested
    class validatePickupStatus {

        @Test
        void 성공() {
            // given
            PickupStatus pickupStatus = PickupStatus.RETURNED;

            // when & then
            assertThatCode(() -> LostItemFixture.createWithPickupStatus(pickupStatus))
                    .doesNotThrowAnyException();
        }

        @Test
        void 예외_수령_상태_null() {
            assertThatThrownBy(() -> LostItemFixture.createWithPickupStatus(null))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("수령 상태는 null일 수 없습니다.")
                    .extracting("status")
                    .isEqualTo(HttpStatus.BAD_REQUEST);
        }
    }
}
