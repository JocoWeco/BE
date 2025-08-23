package com.jocoweco.FoodSommelier.user.domain;

import com.jocoweco.FoodSommelier.constant.Gender;
import com.jocoweco.FoodSommelier.constant.LoginType;
import com.jocoweco.FoodSommelier.constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "USER")
@EntityListeners(AuditingEntityListener.class)
@Entity
@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "uid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "login_type")
    private LoginType loginType;

    @Column(name = "nickname", unique = true, length = 30)
    private String nickname;

    @Column(name = "email", length = 30)
    private String email;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "excluded_ingredient")
    private String excludedIngredient;


    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;


    @Column(name = "agree_location")
    private boolean agreeLocation;

    @Column(name = "agree_privacy")
    private boolean agreePrivacy;

    @Column(name = "is_active")
    private boolean isActive;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private LocalUser localUser;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private SocialUser socialUser;
}
