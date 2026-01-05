package com.healsweets.service;

import com.healsweets.dto.ContactDto;
import com.healsweets.entity.Contact;
import com.healsweets.entity.Member;
import com.healsweets.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;

    public Contact createContact(Member member, ContactDto dto) {
        Contact contact = Contact.builder()
                .member(member)
                .category(dto.getCategory())
                .subject(dto.getSubject())
                .message(dto.getMessage())
                .email(member != null ? member.getEmail() : dto.getEmail())
                .build();

        return contactRepository.save(contact);
    }

    @Transactional(readOnly = true)
    public List<Contact> findByMember(Member member) {
        return contactRepository.findByMemberOrderByCreatedAtDesc(member);
    }
}
