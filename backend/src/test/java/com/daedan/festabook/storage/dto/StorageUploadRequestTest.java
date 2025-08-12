package com.daedan.festabook.storage.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.daedan.festabook.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class StorageUploadRequestTest {

    @Nested
    class validateMultipartFile {

        @Test
        void 예외_null_값_MultipartFile() {
            // given
            String filePath = "test.jpg";

            // when & then
            assertThatThrownBy(() -> new StorageUploadRequest(null, filePath))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일은 비어있을 수 없습니다.");
        }

        @Test
        void 예외_비어있는_MultipartFile() {
            // given
            String filePath = "test.jpg";
            MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);

            // when & then
            assertThatThrownBy(() -> new StorageUploadRequest(emptyFile, filePath))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일은 비어있을 수 없습니다.");
        }
    }

    @Nested
    class validateFilePath {

        @ParameterizedTest(name = "fileName: {0}")
        @NullAndEmptySource
        void 예외_비어있는_filePath(String filePath) {
            // given
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> new StorageUploadRequest(file, filePath))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일 경로는 비어있을 수 없습니다.");
        }

        @Test
        void 예외_길이_제한_초과_filePath() {
            // given
            int maxFileNameLength = 255;
            String filePath = "a".repeat(maxFileNameLength + 1);
            MultipartFile file = new MockMultipartFile(
                    "file",
                    "test.jpg",
                    "image/jpeg",
                    new byte[10]
            );

            // when & then
            assertThatThrownBy(() -> new StorageUploadRequest(file, filePath))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage("파일 경로는 %d자를 초과할 수 없습니다.", maxFileNameLength);
        }
    }
}
