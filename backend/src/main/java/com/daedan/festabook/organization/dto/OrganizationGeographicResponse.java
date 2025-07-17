package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Organization;

public record OrganizationGeographicResponse(
        Integer zoom,
        CoordinateResponse centerCoordinate,
        OrganizationPolygonHoleBoundaryResponse polygonHoleBoundary
) {

    public static OrganizationGeographicResponse from(Organization organization) {
        return new OrganizationGeographicResponse(
                organization.getZoom(),
                CoordinateResponse.from(organization.getCenterCoordinate()),
                OrganizationPolygonHoleBoundaryResponse.from(organization.getPolygonHoleBoundary())
        );
    }
}
