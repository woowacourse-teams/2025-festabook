package com.daedan.festabook.organization.domain;

import com.daedan.festabook.global.exception.BusinessException;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization {

    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_ZOOM = 0;
    private static final int MAX_ZOOM = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer zoom;

    @Embedded
    @Column(nullable = false)
    private Coordinate centerCoordinate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "organization_polygon_hole_boundary",
            joinColumns = @JoinColumn(nullable = false)
    )
    private List<Coordinate> polygonHoleBoundary = new ArrayList<>();

    protected Organization(
            Long id,
            String name,
            Integer zoom,
            Coordinate centerCoordinate,
            List<Coordinate> polygonHoleBoundary
    ) {
        validateName(name);
        validateZoom(zoom);
        validateCenterCoordinate(centerCoordinate);
        validatePolygonHoleBoundary(polygonHoleBoundary);

        this.id = id;
        this.name = name;
        this.zoom = zoom;
        this.centerCoordinate = centerCoordinate;
        this.polygonHoleBoundary = polygonHoleBoundary;
    }

    public Organization(
            String name,
            Integer zoom,
            Coordinate centerCoordinate,
            List<Coordinate> polygonHoleBoundary
    ) {
        this(
                null,
                name,
                zoom,
                centerCoordinate,
                polygonHoleBoundary
        );
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("조직 이름은 비어 있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new BusinessException(
                    String.format("조직 이름은 %d자를 초과할 수 없습니다.", MAX_NAME_LENGTH),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateZoom(Integer zoom) {
        if (zoom == null) {
            throw new BusinessException("줌은 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
        if (zoom < MIN_ZOOM || zoom > MAX_ZOOM) {
            throw new BusinessException(
                    String.format("줌은 %d 이상 %d 이하이어야 합니다.", MIN_ZOOM, MAX_ZOOM),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void validateCenterCoordinate(Coordinate centerCoordinate) {
        if (centerCoordinate == null) {
            throw new BusinessException("중심 좌표는 null일 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }

    private void validatePolygonHoleBoundary(List<Coordinate> polygonHoleBoundary) {
        if (polygonHoleBoundary == null || polygonHoleBoundary.isEmpty()) {
            throw new BusinessException("폴리곤 내부 구멍 좌표 리스트는 비어있을 수 없습니다.", HttpStatus.BAD_REQUEST);
        }
    }
}
