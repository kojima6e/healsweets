package com.healsweets.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 20)
    private String phone;

    @Column
    private LocalDate birthDate;

    @Column(length = 10)
    private String postalCode;

    @Column(length = 50)
    private String prefecture;

    @Column(length = 100)
    private String city;

    @Column(length = 200)
    private String address1;

    @Column(length = 200)
    private String address2;

    @ElementCollection
    @CollectionTable(name = "member_allergies", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "allergy")
    @Builder.Default
    private List<String> allergies = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private Boolean newsletter = false;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String role = "USER";

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        return lastName + " " + firstName;
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append("〒").append(postalCode).append("\n");
        sb.append(prefecture).append(city).append(address1);
        if (address2 != null && !address2.isEmpty()) {
            sb.append(" ").append(address2);
        }
        return sb.toString();
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }
}
