package com.kneu.prescriptions;

import com.kneu.medications.Medication;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne 
    @JoinColumn (name = "medicine_id", nullable = false)
    private Medication medicine;

    private Long medicationId;

    private String time;

    private String name;

    protected Prescription() {

    }

    public Prescription(String name, String time, Long medicationId) {
        this.name = name;
        this.time = time;
        this.medicationId = medicationId;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public Long getMedicationId() {
        return medicationId;
    }
}

