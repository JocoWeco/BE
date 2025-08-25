package com.jocoweco.FoodSommelier.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "LOCAL_USER")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LocalUser {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uid", referencedColumnName = "uid", nullable = false, unique = true)
    private User user;

    @Column(name = "local_id", nullable = false, unique = true, length = 30)
    private String localId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "password_updated_at")
    @LastModifiedDate
    private LocalDateTime passwordUpdatedAt;

}
