package com.daedan.festabook.organization.domain;

import com.daedan.festabook.device.domain.Device;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Device device;

    protected OrganizationBookmark(
            Long id,
            Organization organization,
            Device device
    ) {
        this.id = id;
        this.organization = organization;
        this.device = device;
    }

    public OrganizationBookmark(
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
