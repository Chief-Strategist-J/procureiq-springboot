package com.procureiq.springboot_app.features.fieldservice.repository;

import com.procureiq.springboot_app.features.fieldservice.entity.Contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
