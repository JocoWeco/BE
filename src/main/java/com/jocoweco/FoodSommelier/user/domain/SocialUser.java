package com.jocoweco.FoodSommelier.user.domain;

import com.jocoweco.FoodSommelier.constant.LoginType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "SOCIAL_USER")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class SocialUser {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false, unique = true)
    private User user;

    @Column(name = "provider_id", nullable = false, length = 30)
    private String providerId;

    @Column(name = "name", length = 30)
    private String name;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType provider;

    @Column(name = "connected_at")
    @CreatedDate
    private LocalDateTime connectedAt;
}
