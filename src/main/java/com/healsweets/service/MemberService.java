package com.healsweets.service;

import com.healsweets.dto.MemberRegistrationDto;
import com.healsweets.dto.MemberUpdateDto;
import com.healsweets.entity.Member;
import com.healsweets.repository.CartItemRepository;
import com.healsweets.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final CartItemRepository cartItemRepository;
    private final PasswordEncoder passwordEncoder;

    public Member register(MemberRegistrationDto dto) {
        if (memberRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("このメールアドレスは既に登録されています。");
        }

        Member member = Member.builder()
                .lastName(dto.getLastName())
                .firstName(dto.getFirstName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone())
                .birthDate(dto.getBirthDate())
                .postalCode(dto.getPostalCode())
                .prefecture(dto.getPrefecture())
                .city(dto.getCity())
                .address1(dto.getAddress1())
                .address2(dto.getAddress2())
                .allergies(dto.getAllergies() != null ? dto.getAllergies() : List.of())
                .newsletter(dto.getNewsletter() != null ? dto.getNewsletter() : false)
                .build();

        return memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public Optional<Member> findById(Long id) {
        return memberRepository.findById(id);
    }

    public Member updateMember(Long id, MemberUpdateDto dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会員が見つかりません。"));

        if (dto.getLastName() != null) member.setLastName(dto.getLastName());
        if (dto.getFirstName() != null) member.setFirstName(dto.getFirstName());
        if (dto.getPhone() != null) member.setPhone(dto.getPhone());
        if (dto.getBirthDate() != null) member.setBirthDate(dto.getBirthDate());
        if (dto.getPostalCode() != null) member.setPostalCode(dto.getPostalCode());
        if (dto.getPrefecture() != null) member.setPrefecture(dto.getPrefecture());
        if (dto.getCity() != null) member.setCity(dto.getCity());
        if (dto.getAddress1() != null) member.setAddress1(dto.getAddress1());
        if (dto.getAddress2() != null) member.setAddress2(dto.getAddress2());

        return memberRepository.save(member);
    }

    public Member updateAllergies(Long id, List<String> allergies) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会員が見つかりません。"));
        // 既存のリストをクリアして新しいアレルギーを追加（ElementCollectionの更新に対応）
        member.getAllergies().clear();
        if (allergies != null) {
            member.getAllergies().addAll(allergies);
        }
        return memberRepository.save(member);
    }

    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("会員が見つかりません。"));
        // 退会前にカートアイテムを削除
        cartItemRepository.deleteByMember(member);
        // 論理削除
        member.setEnabled(false);
        memberRepository.save(member);
    }

    public boolean existsByEmail(String email) {
        return memberRepository.existsByEmail(email);
    }
}
