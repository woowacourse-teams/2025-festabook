package com.daedan.festabook.organization.domain;

import com.daedan.festabook.organization.dto.OrganizationBookmarkRequest;

public class OrganizationBookmarkRequestFixture {

    public static OrganizationBookmarkRequest create(
            Long deviceId
    ) {
        return new OrganizationBookmarkRequest(
                deviceId
        );
    }
}
