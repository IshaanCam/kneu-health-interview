package com.kneu.prescriptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.kneu.medications.Medication;
import com.kneu.medications.MedicationRepository;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrescriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PrescriptionsRepository prescriptionRepository;
    @Autowired
    private ObjectMapper objectWrapper;
    @Autowired 
    private MedicationRepository medicationRepository;

    @Test
    void getAllPrescriptions() throws Exception {
        Medication aspirin = new Medication("Aspirin", "100mg");
        Medication metformin = new Medication("Metformin", "500mg");
        
        long m_id_1 = medicationRepository.save(aspirin).getId();
        long m_id_2 = medicationRepository.save(metformin).getId();


        long p_id_1 = prescriptionRepository.save(new Prescription("Aspirin", "8:00", aspirin)).getId();
        long p_id_2 = prescriptionRepository.save(new Prescription("Metformin", "12:00", metformin)).getId();

        mockMvc.perform(get("/prescriptions"))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("""
                [
                    {"id": %d, "name": "Aspirin", "time": "8:00", "medicine":{"id": %d, "name": "Aspirin", "dosage": "100mg"}},
                    {"id": %d, "name": "Metformin", "time": "12:00", "medicine":{"id": %d, "name": "Metformin", "dosage": "500mg"}}
                ]
                """, p_id_1, m_id_1, p_id_2, m_id_2)));
    }

    @Test
    void getAllPrescriptionsWithoutName() throws Exception {
        Medication aspirin = new Medication("Aspirin", "100mg");
        Medication metformin = new Medication("Metformin", "500mg");

        long m_id_1 = medicationRepository.save(aspirin).getId();
        long m_id_2 = medicationRepository.save(metformin).getId();

        long p_id_1 = prescriptionRepository.save(new Prescription("8:00", aspirin)).getId();
        long p_id_2 = prescriptionRepository.save(new Prescription("12:00", metformin)).getId();

        mockMvc.perform(get("/prescriptions"))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("""
                [
                    {"id": %d, "name": "Aspirin 100mg", "time": "8:00", "medicine":{"id": %d, "name": "Aspirin", "dosage": "100mg"}},
                    {"id": %d, "name": "Metformin 500mg", "time": "12:00", "medicine":{"id": %d, "name": "Metformin", "dosage": "500mg"}}
                ]
                """, p_id_1, m_id_1, p_id_2, m_id_2)));
    }

    @Test 
    void createPrescription() throws Exception {
        Medication aspirin = new Medication("Aspirin", "100mg");
        medicationRepository.save(aspirin);

        Prescription prescription = new Prescription("8:00", aspirin);
        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWrapper.writeValueAsString(prescription)))
                .andExpect(status().isCreated());
    }

    @Test 
    void createPrescriptionFail() throws Exception {
        Medication ibuprofen = new Medication("Ibuprofen", "100mg");

        Prescription prescription = new Prescription("8:00", ibuprofen);
        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWrapper.writeValueAsString(prescription)))
                .andExpect(status().isBadRequest());
        
    }

    @AfterEach 
    void cleanup() {
        prescriptionRepository.deleteAll();
        medicationRepository.deleteAll();
    }
}
