package com.healsweets.repository;

import com.healsweets.entity.Contact;
import com.healsweets.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    
    List<Contact> findByMemberOrderByCreatedAtDesc(Member member);
}
