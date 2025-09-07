package com.daedan.festabook.global.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import com.daedan.festabook.festival.domain.Festival;
import com.daedan.festabook.festival.domain.FestivalFixture;
import com.daedan.festabook.festival.infrastructure.FestivalJpaRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BaseEntityTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FestivalJpaRepository festivalJpaRepository;

    @Nested
    class softDelete {

        @Test
        void 성공_JPA_delete_이후_find_사용시_조회되지_않음() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);

            // when - 소프트 딜리트 실행
            festivalJpaRepository.deleteById(festival.getId());
            entityManager.flush();
            entityManager.clear();

            // then - 조회되지 않아야 함
            Optional<Festival> foundFestival = festivalJpaRepository.findById(festival.getId());
            assertThat(foundFestival).isEmpty();
        }

        @Test
        void 성공_JPA_delete_이후_deleted_deletedAt_필드가_변경됨() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);
            Long festivalId = festival.getId();

            // when
            festivalJpaRepository.deleteById(festivalId);
            entityManager.flush();
            entityManager.clear();

            // then - 엔티티 전체를 조회하여 검증
            Festival foundFestival = (Festival) entityManager.getEntityManager()
                    .createNativeQuery("SELECT * FROM festival WHERE id = ?", Festival.class)
                    .setParameter(1, festivalId)
                    .getSingleResult();

            assertSoftly(s -> {
                assertThat(foundFestival.isDeleted()).isTrue();
                assertThat(foundFestival.isActive()).isFalse();
                assertThat(foundFestival.getDeletedAt()).isNotNull();
            });
        }

        @Test
        void 성공_도메인_delete_이후_deleted_deletedAt_필드가_변경됨() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);
            Long festivalId = festival.getId();

            // when
            festival.softDelete();
            entityManager.flush();
            entityManager.clear();

            // then - 엔티티 전체를 조회하여 검증
            Festival foundFestival = (Festival) entityManager.getEntityManager()
                    .createNativeQuery("SELECT * FROM festival WHERE id = ?", Festival.class)
                    .setParameter(1, festivalId)
                    .getSingleResult();

            assertSoftly(s -> {
                assertThat(foundFestival.isDeleted()).isTrue();
                assertThat(foundFestival.isActive()).isFalse();
                assertThat(foundFestival.getDeletedAt()).isNotNull();
            });
        }

        @Test
        void 성공_도메인_restore_이후_소프트_삭제_복원() {
            // given
            Festival festival = FestivalFixture.create();
            festivalJpaRepository.save(festival);
            Long festivalId = festival.getId();
            festival.softDelete();
            entityManager.flush();

            // when
            festival.restore();
            entityManager.flush();
            entityManager.clear();

            // then - 엔티티 전체를 조회하여 검증
            Festival foundFestival = (Festival) entityManager.getEntityManager()
                    .createNativeQuery("SELECT * FROM festival WHERE id = ?", Festival.class)
                    .setParameter(1, festivalId)
                    .getSingleResult();

            assertSoftly(s -> {
                assertThat(foundFestival.isDeleted()).isFalse();
                assertThat(foundFestival.isActive()).isTrue();
                assertThat(foundFestival.getDeletedAt()).isNull();
            });
        }
    }
}
