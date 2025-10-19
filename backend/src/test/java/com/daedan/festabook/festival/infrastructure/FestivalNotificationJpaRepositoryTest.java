package com.daedan.festabook.festival.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.device.domain.Device;
import com.daedan.festabook.device.domain.DeviceFixture;
import com.daedan.festabook.device.infrastructure.DeviceJpaRepository;
import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.domain.FestivalNotification;
import com.daedan.festabook.festival.domain.FestivalNotificationFixture;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FestivalNotificationJpaRepositoryTest {

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Autowired
    private DeviceJpaRepository deviceJpaRepository;

    @Autowired
    private FestivalNotificationJpaRepository festivalNotificationJpaRepository;

    @Nested
    class findAllWithFestivalByDeviceId {

        @Test
        void 성공_하나의_디바이스가_구독한_여러_축제_조회() {
            // given
            Device device = deviceJpaRepository.save(DeviceFixture.create());

            Festival firstFestival = festivalJpaRepository.save(FestivalFixture.create());
            Festival secondFestival = festivalJpaRepository.save(FestivalFixture.create());

            FestivalNotification first = festivalNotificationJpaRepository.save(
                    FestivalNotificationFixture.create(firstFestival, device)
            );

            FestivalNotification second = festivalNotificationJpaRepository.save(
                    FestivalNotificationFixture.create(secondFestival, device)
            );

            // when
            List<FestivalNotification> result = festivalNotificationJpaRepository.findAllWithFestivalByDeviceId(
                    device.getId());

            // then
            assertThat(result)
                    .extracting(FestivalNotification::getId)
                    .containsExactlyInAnyOrder(
                            first.getId(),
                            second.getId()
                    );
        }

        @Test
        void 성공_다른_디바이스_알림은_제외하고_조회() {
            // given
            Festival targetFestival = festivalJpaRepository.save(FestivalFixture.create());
            Device targetDevice = deviceJpaRepository.save(DeviceFixture.create());

            FestivalNotification targetNotification = festivalNotificationJpaRepository.save(
                    FestivalNotificationFixture.create(targetFestival, targetDevice)
            );

            Festival otherFestival = festivalJpaRepository.save(FestivalFixture.create());
            Device otherDevice = deviceJpaRepository.save(DeviceFixture.create());

            festivalNotificationJpaRepository.save(FestivalNotificationFixture.create(otherFestival, otherDevice));

            // when
            List<FestivalNotification> result = festivalNotificationJpaRepository.findAllWithFestivalByDeviceId(
                    targetDevice.getId()
            );

            // then
            assertThat(result).containsExactly(targetNotification);
        }

        @Test
        void 성공_삭제된_알림은_조회_안됨() {
            // given
            Festival festival = festivalJpaRepository.save(FestivalFixture.create());
            Device device = deviceJpaRepository.save(DeviceFixture.create());

            FestivalNotification festivalNotification = festivalNotificationJpaRepository.save(
                    FestivalNotificationFixture.create(festival, device)
            );

            festivalNotificationJpaRepository.delete(festivalNotification);

            // when
            List<FestivalNotification> result = festivalNotificationJpaRepository.findAllWithFestivalByDeviceId(
                    device.getId()
            );

            // then
            assertThat(result).isEmpty();
        }
    }
}
