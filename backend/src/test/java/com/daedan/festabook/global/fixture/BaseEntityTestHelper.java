package com.daedan.festabook.global.fixture;

import com.daedan.festabook.global.domain.BaseEntity;
import java.lang.reflect.Field;
import java.time.LocalDateTime;

/**
 * 테스트에서만 사용하는 BaseEntity 필드 설정 헬퍼 클래스 리플렉션을 통해 BaseEntity의 private 필드들을 설정합니다.
 */
public class BaseEntityTestHelper {

    /**
     * 테스트 객체의 createdAt을 설정합니다.
     *
     * @param entity    설정할 엔티티
     * @param createdAt 설정할 생성 시간
     * @param <T>       BaseEntity를 상속받은 엔티티 타입
     * @return 설정된 엔티티
     */
    public static <T extends BaseEntity> T setCreatedAt(T entity, LocalDateTime createdAt) {
        try {
            Field field = BaseEntity.class.getDeclaredField("createdAt");
            field.setAccessible(true);
            field.set(entity, createdAt);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("테스트 객체의 createdAt 설정에 실패했습니다.", e);
        }
    }

    /**
     * 테스트 객체의 updatedAt을 설정합니다.
     *
     * @param entity    설정할 엔티티
     * @param updatedAt 설정할 수정 시간
     * @param <T>       BaseEntity를 상속받은 엔티티 타입
     * @return 설정된 엔티티
     */
    public static <T extends BaseEntity> T setUpdatedAt(T entity, LocalDateTime updatedAt) {
        try {
            Field field = BaseEntity.class.getDeclaredField("updatedAt");
            field.setAccessible(true);
            field.set(entity, updatedAt);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("테스트 객체의 updatedAt 설정에 실패했습니다.", e);
        }
    }

    /**
     * 테스트 객체를 삭제 상태로 설정합니다.
     *
     * @param entity    설정할 엔티티
     * @param deletedAt 삭제 시간
     * @param <T>       BaseEntity를 상속받은 엔티티 타입
     * @return 설정된 엔티티
     */
    public static <T extends BaseEntity> T setDeleted(T entity, LocalDateTime deletedAt) {
        try {
            Field deletedField = BaseEntity.class.getDeclaredField("deleted");
            Field deletedAtField = BaseEntity.class.getDeclaredField("deletedAt");

            deletedField.setAccessible(true);
            deletedAtField.setAccessible(true);

            deletedField.set(entity, true);
            deletedAtField.set(entity, deletedAt);

            return entity;
        } catch (Exception e) {
            throw new RuntimeException("테스트 객체의 삭제 상태 설정에 실패했습니다.", e);
        }
    }

    /**
     * 테스트 객체의 ID를 설정합니다.
     *
     * @param entity 설정할 엔티티
     * @param id     설정할 ID
     * @param <T>    BaseEntity를 상속받은 엔티티 타입
     * @return 설정된 엔티티
     */
    public static <T extends BaseEntity> T setId(T entity, Long id) {
        try {
            // 엔티티 클래스에서 id 필드를 찾습니다
            Field field = findIdField(entity.getClass());
            field.setAccessible(true);
            field.set(entity, id);
            return entity;
        } catch (Exception e) {
            throw new RuntimeException("테스트 객체의 ID 설정에 실패했습니다.", e);
        }
    }

    /**
     * 클래스 계층구조에서 id 필드를 찾습니다.
     */
    private static Field findIdField(Class<?> clazz) throws NoSuchFieldException {
        Class<?> currentClass = clazz;
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField("id");
            } catch (NoSuchFieldException e) {
                currentClass = currentClass.getSuperclass();
            }
        }
        throw new NoSuchFieldException("id 필드를 찾을 수 없습니다: " + clazz.getName());
    }
}
