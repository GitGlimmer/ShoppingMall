package com.glimmer.shopping.shoppingmall.entity;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * @author Glimmer
 */ // User.java
@Entity
@Table(name = "user")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private LocalDateTime createTime;
}
