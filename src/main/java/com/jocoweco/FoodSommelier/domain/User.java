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
    @Column(nullable = true)
    private String recentlyStore;
    @Column(name = "saved_store", nullable = true)
    private String savedStore;

    public void updateUser(String userId, String nickName, String user_pw, String savedStore) {
        this.userId = userId;
        this.nickName = nickName;
        this.user_pw = user_pw;
        this.savedStore = savedStore;
    }

}
