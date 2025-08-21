package com.daedan.festabook.council.domain;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.global.fixture.BaseEntityTestHelper;

public class CouncilFixture {

    private static final Festival DEFAULT_FESTIVAL = FestivalFixture.create();
    private static final String DEFAULT_USERNAME = "council";
    private static final String DEFAULT_PASSWORD = "password";

    public static Council create() {
        return new Council(
                DEFAULT_FESTIVAL,
                DEFAULT_USERNAME,
                DEFAULT_PASSWORD
        );
    }

    public static Council create(
            Festival festival
    ) {
        return new Council(
                festival,
                DEFAULT_USERNAME,
                DEFAULT_PASSWORD
        );
    }

    public static Council create(
            Festival festival,
            String username
    ) {
        return new Council(
                festival,
                username,
                DEFAULT_PASSWORD
        );
    }

    public static Council createWithUsername(
            String username
    ) {
        return new Council(
                DEFAULT_FESTIVAL,
                username,
                DEFAULT_PASSWORD
        );
    }

    public static Council createWithPassword(
            String password
    ) {
        return new Council(
                DEFAULT_FESTIVAL,
                DEFAULT_USERNAME,
                password
        );
    }

    public static Council create(
            Festival festival,
            String username,
            String password
    ) {
        return new Council(
                festival,
                username,
                password
        );
    }

    public static Council create(
            Long councilId,
            Festival festival,
            String username,
            String password
    ) {
        Council council = new Council(
                festival,
                username,
                password
        );
        BaseEntityTestHelper.setId(council, councilId);
        return council;
    }
}
