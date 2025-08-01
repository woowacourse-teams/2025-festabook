package com.daedan.festabook.lostitem.domain;

import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LostItem {

    private static final int MAX_STORAGE_LOCATION_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, length = MAX_STORAGE_LOCATION_LENGTH)
    private String storageLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PickupStatus pickupStatus;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected LostItem(
            Long id,
            String imageUrl,
            String storageLocation,
            PickupStatus pickupStatus,
            LocalDateTime createdAt
    ) {
        validateImageUrl(imageUrl);
        validateStorageLocation(storageLocation);
        validatePickupStatus(pickupStatus);

        this.id = id;
        this.imageUrl = imageUrl;
        this.storageLocation = storageLocation;
        this.pickupStatus = pickupStatus;
        this.createdAt = createdAt;
    }

    public LostItem(
            String imageUrl,
            String storageLocation,
            PickupStatus pickupStatus
    ) {
        this(
                null,
                imageUrl,
                storageLocation,
                pickupStatus,
                null
        );
    }

    private void validateImageUrl(String imageUrl) {
        if (!StringUtils.hasText(imageUrl)) {
            throw new BusinessException("이미지 URL은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateStorageLocation(String storageLocation) {
        if (!StringUtils.hasText(storageLocation)) {
            throw new BusinessException("보관 장소는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (storageLocation.length() > MAX_STORAGE_LOCATION_LENGTH) {
            throw new BusinessException(
                    String.format("보관 장소는 %d자를 초과할 수 없습니다.", MAX_STORAGE_LOCATION_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validatePickupStatus(PickupStatus pickupStatus) {
        if (pickupStatus == null) {
            throw new BusinessException("수령 상태는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
