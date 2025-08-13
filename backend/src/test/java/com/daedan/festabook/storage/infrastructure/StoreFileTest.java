package com.daedan.festabook.storage.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import java.io.IOException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StoreFileTest {

    @Nested
    class validateMultipartFile {

        @Test
        void 예외_null_값_MultipartFile() {
            // given
            String storagePath = "test.jpg";

            // when & then
            assertThatThrownBy(() -> new StoreFile(null, storagePath))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일은 비어있을 수 없습니다.");
        }

        @Test
        void 예외_비어있는_MultipartFile() {
            // given
            String storagePath = "test.jpg";
            MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

            // when & then
            assertThatThrownBy(() -> new StoreFile(emptyFile, storagePath))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일은 비어있을 수 없습니다.");
        }
    }

    @Nested
    class validateStoragePath {

        @ParameterizedTest(name = "storagePath: {0}")
        @NullAndEmptySource
        void 예외_비어있는_storagePath(String storagePath) {
            // given
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> new StoreFile(file, storagePath))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일 경로는 비어있을 수 없습니다.");
        }

        @Test
        void 예외_길이_제한_초과_storagePath() {
            // given
            int maxFileNameLength = 255;
            String storagePath = "a".repeat(maxFileNameLength + 1);
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> new StoreFile(file, storagePath))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일 경로는 %d자를 초과할 수 없습니다.", maxFileNameLength);
        }
    }

    @Nested
    class getBytes {

        @Test
        void 성공_파일_바이트_읽기() throws IOException {
            // given
            byte[] fileContent = new byte[10];
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    fileContent
            );
            String storagePath = "test.jpg";

            // when
            byte[] result = new StoreFile(file, storagePath).getBytes();

            // then
            assertThat(result).isEqualTo(fileContent);
        }

        @Test
        void 예외_파일_바이트_읽기_실패() {
            // given
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    new byte[10]
            ) {
                @Override
                public byte[] getBytes() throws IOException {
                    throw new IOException();
                }
            };
            String storagePath = "test.jpg";

            // when & then
            assertThatThrownBy(() -> new StoreFile(file, storagePath).getBytes())
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("MultipartFile 에서 Byte 데이터를 읽기 실패했습니다.");
        }
    }
}
