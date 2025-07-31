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

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LostItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false, length = 20)
    private String storageLocation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ClaimStatus claimStatus;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected LostItem(
            Long id,
            String imageUrl,
            String storageLocation,
            ClaimStatus claimStatus,
            LocalDateTime createdAt
    ) {
        validateImageUrl(imageUrl);
        validateStorageLocation(storageLocation);
        validateClaimStatus(claimStatus);
        this.id = id;
        this.imageUrl = imageUrl;
        this.storageLocation = storageLocation;
        this.claimStatus = claimStatus;
        this.createdAt = createdAt;
    }

    public LostItem(
            String imageUrl,
            String storageLocation,
            ClaimStatus claimStatus
    ) {
        this(
                null,
                imageUrl,
                storageLocation,
                claimStatus,
                null
        );
    }

    private void validateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new BusinessException("이미지 URL은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateStorageLocation(String storageLocation) {
        if (storageLocation == null || storageLocation.trim().isEmpty()) {
            throw new BusinessException("보관 장소는 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (storageLocation.length() > 20) {
            throw new BusinessException("보관 장소는 20자를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateClaimStatus(ClaimStatus claimStatus) {
        if (claimStatus == null) {
            throw new BusinessException("수령 상태는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
