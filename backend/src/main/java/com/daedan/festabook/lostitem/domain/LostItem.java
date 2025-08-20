package com.daedan.festabook.lostitem.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.global.domain.BaseEntity;
import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LostItem extends BaseEntity {

    private static final int MAX_STORAGE_LOCATION_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Festival festival;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, length = MAX_STORAGE_LOCATION_LENGTH)
    private String storageLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PickupStatus status;

    public LostItem(
            Festival festival,
            String imageUrl,
            String storageLocation,
            PickupStatus status
    ) {
        validateImageUrl(imageUrl);
        validateStorageLocation(storageLocation);
        validatePickupStatus(status);

        this.festival = festival;
        this.imageUrl = imageUrl;
        this.storageLocation = storageLocation;
        this.status = status;
    }

    public void updateLostItem(String imageUrl, String storageLocation) {
        validateImageUrl(imageUrl);
        validateStorageLocation(storageLocation);

        this.imageUrl = imageUrl;
        this.storageLocation = storageLocation;
    }

    public void updateStatus(PickupStatus status) {
        validatePickupStatus(status);

        this.status = status;
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

    private void validatePickupStatus(PickupStatus status) {
        if (ObjectUtils.isEmpty(status)) {
            throw new BusinessException("수령 상태는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
