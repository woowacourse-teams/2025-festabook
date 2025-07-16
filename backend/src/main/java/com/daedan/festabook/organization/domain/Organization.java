package com.daedan.festabook.organization.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer zoom;

    @Column(nullable = false)
    private Coordinate centerCoordinate;

    // TODO: 해결 안된 N+1
    @CollectionTable(
            name = "organization_polygon_hole_boundary",
            joinColumns = @JoinColumn(nullable = false)
    )
    @ElementCollection
    private List<Coordinate> polygonHoleBoundary = new ArrayList<>();

    protected Organization(
            Long id,
            String name,
            Integer zoom,
            Coordinate centerCoordinate,
            List<Coordinate> polygonHoleBoundary
    ) {
        this.id = id;
        this.name = name;
        this.zoom = zoom;
        this.centerCoordinate = centerCoordinate;
        this.polygonHoleBoundary = polygonHoleBoundary;
    }

    public Organization(
            String name,
            Integer zoom,
            Coordinate centerCoordinate
    ) {
        this.name = name;
        this.zoom = zoom;
        this.centerCoordinate = centerCoordinate;
    }
}
