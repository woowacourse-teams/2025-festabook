package com.daedan.festabook.organization.domain;

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
}
