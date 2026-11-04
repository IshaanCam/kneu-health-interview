package com.kneu.prescriptions;


import org.springframework.data.jpa.repository.JpaRepository;

public interface PrescriptionsRepository extends JpaRepository<Prescription, Long> {
}
