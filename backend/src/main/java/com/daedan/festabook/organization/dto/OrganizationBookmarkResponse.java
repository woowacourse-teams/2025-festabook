package com.daedan.festabook.organization.dto;

import com.daedan.festabook.organization.domain.OrganizationBookmark;

public record OrganizationBookmarkResponse(
        Long id
) {

    public static OrganizationBookmarkResponse from(OrganizationBookmark organizationBookmark) {
        return new OrganizationBookmarkResponse(
                organizationBookmark.getId()
        );
    }
}
