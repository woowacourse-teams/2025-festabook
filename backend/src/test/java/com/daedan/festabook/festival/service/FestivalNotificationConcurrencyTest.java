package com.daedan.festabook.festival.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.FestivalNotificationManager;
import com.daedan.festabook.festival.dto.FestivalNotificationRequest;
import com.daedan.festabook.festival.dto.FestivalNotificationRequestFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import com.daedan.festabook.festival.infrastructure.FestivalNotificationJpaRepository;
import com.daedan.festabook.global.lock.ConcurrencyTestHelper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FestivalNotificationConcurrencyTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private FestivalNotificationJpaRepository festivalNotificationJpaRepository;

    @MockitoBean
    private FestivalNotificationManager festivalNotificationManager;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    class subscribeFestivalNotification {

        @Test
        void 동시성_중복_알림_등록_방지() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            Device device = DeviceFixture.create();
            deviceJpaRepository.save(device);

            FestivalNotificationRequest request = FestivalNotificationRequestFixture.create(device.getId());

            int requestCount = 100;
            AtomicInteger duplicateErrorCount = new AtomicInteger(0);

            Runnable httpRequest = () -> {
                Response response = RestAssured
                        .given()
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post("/festivals/{festivalId}/notifications", festival.getId());

                if (response.getStatusCode() == HttpStatus.CONFLICT.value()) {
                    String responseBody = response.getBody().asString();
                    if (responseBody.contains("중복된 데이터 삽입이 발생했습니다.")) {
                        duplicateErrorCount.incrementAndGet();
                    }
                }
            };

            // when
            ConcurrencyTestHelper.test(requestCount, httpRequest);

            // then
            Long result = festivalNotificationJpaRepository.countByFestivalIdAndDeviceId(
                    festival.getId(), device.getId());
            assertThat(result).isEqualTo(1);

            assertThat(duplicateErrorCount.get()).isEqualTo(99);
        }
    }
}
