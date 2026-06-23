package com.example.springedu2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

// @Entity -> db table
@Entity
@Table(name="members") // table 이름 변경
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id                                                     // PrimaryKey 기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // 번호 자동 증가
    private Long id;

    @Column(nullable = false, unique = true, length = 30) // NotNull, UNIQUE, varchar(30)
    private String username;

    @Column(nullable = false) // BCrypt 암호화 통과하면 길이가 길어져서 length 지정 안 함
    private String password; // 로그인 비밀번호

    @Column(nullable = false)
    private String name;        // 사용자 이름

    @Column(nullable = false)
    private String email;       // 이메일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role = Role.USER;       // 권한

    @Column(nullable = false)
    private boolean enabled = true;       // 계정 사용 가능

    @CreationTimestamp                  // 자동 입력
    @Column(nullable = false)
    private LocalDateTime created;       // 계정 생성일, 가입일

    @CreationTimestamp                  // 자동 입력
    @Column(nullable = false)
    private LocalDateTime updateAt;       // 계정 수정일
}
