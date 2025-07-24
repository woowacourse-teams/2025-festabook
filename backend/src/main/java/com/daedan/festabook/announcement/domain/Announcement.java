package com.daedan.festabook.announcement.domain;

import com.daedan.festabook.organization.domain.Organization;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Announcement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Organization organization;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isPinned;

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected Announcement(
            Long id,
            String title,
            String content,
            boolean isPinned,
            Organization organization,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.isPinned = isPinned;
        this.organization = organization;
        this.createdAt = createdAt;
    }

    public Announcement(
            String title,
            String content,
            boolean isPinned,
            Organization organization
    ) {
        this(null, title, content, isPinned, organization, null);
    }

    public boolean isUnpinned() {
        return !isPinned;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
