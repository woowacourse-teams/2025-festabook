package com.daedan.festabook.organization.domain;

import com.daedan.festabook.device.domain.Device;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"organization_id", "device_id"}))
public class OrganizationNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Device device;

    protected OrganizationNotification(
            Long id,
            Organization organization,
            Device device
    ) {
        this.id = id;
        this.organization = organization;
        this.device = device;
    }

    public OrganizationNotification(
            Organization organization,
            Device device
    ) {
        this(
                null,
                organization,
                device
        );
    }
}
