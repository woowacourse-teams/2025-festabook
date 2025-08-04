package com.daedan.festabook.place.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.daedan.festabook.organization.domain.Organization;
import com.daedan.festabook.organization.domain.OrganizationFixture;
import com.daedan.festabook.organization.infrastructure.OrganizationJpaRepository;
import com.daedan.festabook.place.domain.Place;
import com.daedan.festabook.place.domain.PlaceFixture;
import com.daedan.festabook.place.domain.PlaceImage;
import com.daedan.festabook.place.domain.PlaceImageFixture;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class PlaceImageJpaRepositoryTest {

    @Autowired
    private OrganizationJpaRepository organizationJpaRepository;

    @Autowired
    private PlaceJpaRepository placeJpaRepository;

    @Autowired
    private PlaceImageJpaRepository placeImageJpaRepository;

    @Nested
    class findAllByPlaceIdOrderBySequenceAsc {

        @Test
        void 성공_오름차순_정렬() {
            // given
            Organization organization = OrganizationFixture.create();
            organizationJpaRepository.save(organization);

            Place place = PlaceFixture.create(organization);
            placeJpaRepository.save(place);

            PlaceImage image3 = PlaceImageFixture.create(place, 3);
            PlaceImage image2 = PlaceImageFixture.create(place, 2);
            PlaceImage image1 = PlaceImageFixture.create(place, 1);
            placeImageJpaRepository.saveAll(List.of(image3, image2, image1));

            // when
            List<PlaceImage> placeImages = placeImageJpaRepository.findAllByPlaceIdOrderBySequenceAsc(place.getId());

            // then
            assertThat(placeImages).containsExactly(image1, image2, image3);
        }
    }
}
