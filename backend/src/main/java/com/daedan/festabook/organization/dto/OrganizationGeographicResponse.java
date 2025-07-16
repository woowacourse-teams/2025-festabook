package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.Organization;

public record OrganizationGeographicResponse(
        Integer zoom,
        OrganizationCoordinateResponse centerCoordinate,
        OrganizationPolygonHoleBoundaryResponse polygonHoleBoundary
) {

    public static OrganizationGeographicResponse from(Organization organization) {
        return new OrganizationGeographicResponse(
                organization.getZoom(),
                OrganizationCoordinateResponse.from(organization.getCenterCoordinate()),
                OrganizationPolygonHoleBoundaryResponse.from(organization.getPolygonHoleBoundary())
        );
    }
}
