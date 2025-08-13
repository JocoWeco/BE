package com.jocoweco.FoodSommelier.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uid;
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "nick_name", nullable = false)
    private String nickName;
    @Column(nullable = false)
    private String user_pw;

    public void updateUser(String userId, String nickName, String user_pw) {
        this.userId = userId;
        this.nickName = nickName;
        this.user_pw = user_pw;
    }

}
