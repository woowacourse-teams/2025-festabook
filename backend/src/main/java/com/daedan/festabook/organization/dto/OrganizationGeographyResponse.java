package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Organization;

public record OrganizationGeographyResponse(
        Integer zoom,
        CoordinateResponse centerCoordinate,
        PolygonHoleBoundaryResponse polygonHoleBoundary
) {

    public static OrganizationGeographyResponse from(Organization organization) {
        return new OrganizationGeographyResponse(
                organization.getZoom(),
                CoordinateResponse.from(organization.getCenterCoordinate()),
                PolygonHoleBoundaryResponse.from(organization.getPolygonHoleBoundary())
        );
    }
}
