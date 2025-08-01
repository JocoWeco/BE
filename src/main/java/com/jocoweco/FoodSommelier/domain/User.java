package com.jocoweco.FoodSommelier.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
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
    @Column(nullable = true)
    private String recently_store;
    @Column(nullable = true)
    private String saved_store;

    public void updateUser(String userId, String nickName, String user_pw, String recently_store, String saved_store) {
        if (userId != null) this.userId = userId;
        if (nickName != null) this.nickName = nickName;
        if (user_pw != null) this.user_pw = user_pw;
        this.recently_store = recently_store;
        this.saved_store = saved_store;
    }

}
