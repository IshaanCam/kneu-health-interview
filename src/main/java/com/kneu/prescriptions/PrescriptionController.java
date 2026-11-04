package com.kneu.prescriptions;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kneu.medications.Medication;
import com.kneu.medications.MedicationRepository;


@RestController
public class PrescriptionController {

    private final PrescriptionsRepository repository;
    private final MedicationRepository medicine_repository;

    public PrescriptionController(PrescriptionsRepository repository, MedicationRepository medicine_repository) {
        this.repository = repository;
        this.medicine_repository = medicine_repository;
    }

    @GetMapping("/prescriptions")
    public List<Prescription> getAllPrescriptions() {
        return repository.findAll();
    }

    @PostMapping("/prescriptions")
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        Medication medicine = prescription.getMedicine();
        if (medicine == null || medicine.getId() == null || medicine_repository.findById(medicine.getId()).isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Prescription saved_prescription = repository.save(prescription);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved_prescription);
    }

    

}
