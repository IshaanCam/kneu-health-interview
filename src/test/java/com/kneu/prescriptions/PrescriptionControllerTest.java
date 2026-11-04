package com.kneu.prescriptions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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
    private MedicationRepository medicationRepository;

    @Test
    void getAllPrescriptions() throws Exception {
        Medication aspirin = new Medication("Aspirin", "100mg");
        Medication metformin = new Medication("Metformin", "500mg");
        
        long m_id_1 = medicationRepository.save(aspirin).getId();
        long m_id_2 = medicationRepository.save(metformin).getId();


        long p_id_1 = prescriptionRepository.save(new Prescription("Aspirin", "08:00", aspirin)).getId();
        long p_id_2 = prescriptionRepository.save(new Prescription("Metformin", "12:00", metformin)).getId();

        mockMvc.perform(get("/prescriptions"))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("""
                [
                    {"id": %d, "name": "Aspirin", "time": "08:00", "medicine":{"id": %d, "name": "Aspirin", "dosage": "100mg"}},
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

        long p_id_1 = prescriptionRepository.save(new Prescription("08:00", aspirin)).getId();
        long p_id_2 = prescriptionRepository.save(new Prescription("12:00", metformin)).getId();

        mockMvc.perform(get("/prescriptions"))
            .andExpect(status().isOk())
            .andExpect(content().json(String.format("""
                [
                    {"id": %d, "name": "Aspirin 100mg", "time": "08:00", "medicine":{"id": %d, "name": "Aspirin", "dosage": "100mg"}},
                    {"id": %d, "name": "Metformin 500mg", "time": "12:00", "medicine":{"id": %d, "name": "Metformin", "dosage": "500mg"}}
                ]
                """, p_id_1, m_id_1, p_id_2, m_id_2)));
    }

    @Test 
    void createPrescription() throws Exception {
        Medication aspirin = new Medication("Aspirin", "100mg");
        Long med_id = medicationRepository.save(aspirin).getId();

        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                            "name" : "Aspirin"
                            "time" : "08:00",
                            "medicationId": %d
                        }
                    """, med_id)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.medicine.name").value("Aspirin"))
                .andExpect(jsonPath("$.time").value("08:00"));
    }

    void createPrescriptionWithoutName() throws Exception {
        Medication aspirin = new Medication("Aspirin", "100mg");
        Long med_id = medicationRepository.save(aspirin).getId();

        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                            "time" : "08:00",
                            "medicationId": %d
                        }
                    """, med_id)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.medicine.name").value("Aspirin 100mg"))
                .andExpect(jsonPath("$.time").value("08:00"));
    }

    @Test 
    void createPrescriptionFail() throws Exception {
        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                            "time" : "08:00",
                            "medicationId": 999
                        }
                    """)))
                .andExpect(status().isBadRequest());
        
    }

    @Test 
    void missingTime() throws Exception {
        Medication ibuprofen = new Medication("Ibuprofen", "100mg");
        Long med_id = medicationRepository.save(ibuprofen).getId();

        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                            "medicationId": %d
                        }
                    """, med_id)))
                .andExpect(status().isBadRequest());
        
    }

    @Test 
    void missingMedicine() throws Exception {

        mockMvc.perform(post("/prescriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                        {
                            "time": "08:00"
                        }
                    """)))
                .andExpect(status().isBadRequest());
        
    }

    // The missing tests would assert the respective error messages
    // There would be a test for type errors in time and id fields
    @AfterEach 
    void cleanup() {
        prescriptionRepository.deleteAll();
        medicationRepository.deleteAll();
    }
}
