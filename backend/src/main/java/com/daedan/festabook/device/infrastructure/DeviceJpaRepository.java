package com.daedan.festabook.device.infrastructure;

import com.daedan.festabook.device.domain.Device;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceJpaRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceIdentifier(String deviceIdentifier);
}
