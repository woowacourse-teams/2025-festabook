package com.daedan.festabook.place.infrastructure;

import com.daedan.festabook.place.domain.PlaceBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceBookmarkJpaRepository extends JpaRepository<PlaceBookmark, Long> {
}
