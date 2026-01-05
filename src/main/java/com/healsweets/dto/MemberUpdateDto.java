package com.healsweets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberUpdateDto {

    private String lastName;
    private String firstName;
    private String phone;
    private LocalDate birthDate;
    private String postalCode;
    private String prefecture;
    private String city;
    private String address1;
    private String address2;
}
