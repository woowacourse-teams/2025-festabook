package com.daedan.festabook.device.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceIdentifier;

    private String fcmToken;

    protected Device(
            Long id,
            String deviceIdentifier,
            String fcmToken
    ) {
        this.id = id;
        this.deviceIdentifier = deviceIdentifier;
        this.fcmToken = fcmToken;
    }

    public Device(
            String deviceIdentifier,
            String fcmToken
    ) {
        this(
                null,
                deviceIdentifier,
                fcmToken
        );
    }
}
