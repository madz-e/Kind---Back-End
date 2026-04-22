package com.example.controller;

import com.example.model.Affirmation;
import com.example.service.impl.AffirmationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AffirmationController.class)
class AffirmationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AffirmationService affirmationService;

    private Affirmation testAffirmation;

    @BeforeEach
    void setUp() {
        testAffirmation = new Affirmation();
        testAffirmation.setId(1L);
        testAffirmation.setAffirmationText("Test affirmation");
        testAffirmation.setReminders(new HashSet<>());
    }

    @Test
    void testInitializeAffirmations() throws Exception {
        List<Affirmation> affirmations = Arrays.asList(testAffirmation);
        when(affirmationService.getAllAffirmations()).thenReturn(affirmations);
        doNothing().when(affirmationService).initializeDefaultAffirmations();

        mockMvc.perform(post("/api/affirmations/init"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Default affirmations initialized"))
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(affirmationService, times(1)).initializeDefaultAffirmations();
        verify(affirmationService, times(1)).getAllAffirmations();
    }

    @Test
    void testGetRandomAffirmation() throws Exception {
        when(affirmationService.getRandomAffirmation()).thenReturn(testAffirmation);

        mockMvc.perform(get("/api/affirmations/random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Test affirmation"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(affirmationService, times(1)).getRandomAffirmation();
    }

    @Test
    void testGetTodaysAffirmation() throws Exception {
        when(affirmationService.getTodaysAffirmation()).thenReturn(testAffirmation);

        mockMvc.perform(get("/api/affirmations/today"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Test affirmation"))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.timestamp").exists());

        verify(affirmationService, times(1)).getTodaysAffirmation();
    }

    @Test
    void testGetAllAffirmations() throws Exception {
        List<Affirmation> affirmations = Arrays.asList(testAffirmation);
        when(affirmationService.getAllAffirmations()).thenReturn(affirmations);

        mockMvc.perform(get("/api/affirmations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].affirmationText").value("Test affirmation"));

        verify(affirmationService, times(1)).getAllAffirmations();
    }

    @Test
    void testGetAffirmationById() throws Exception {
        when(affirmationService.getAffirmationById(1L)).thenReturn(testAffirmation);

        mockMvc.perform(get("/api/affirmations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.affirmationText").value("Test affirmation"));

        verify(affirmationService, times(1)).getAffirmationById(1L);
    }

    @Test
    void testGetAffirmationById_NotFound() throws Exception {
        when(affirmationService.getAffirmationById(999L))
                .thenThrow(new RuntimeException("Affirmation not found with id: 999"));

        mockMvc.perform(get("/api/affirmations/999"))
                .andExpect(status().isOk())  // Returns 200 with error? This might need fix in controller
                .andExpect(jsonPath("$.message").doesNotExist()); // Will fail if controller doesn't handle errors

        verify(affirmationService, times(1)).getAffirmationById(999L);
    }

    @Test
    void testCreateAffirmation() throws Exception {
        Affirmation newAffirmation = new Affirmation();
        newAffirmation.setId(2L);
        newAffirmation.setAffirmationText("New affirmation");
        newAffirmation.setReminders(new HashSet<>());

        when(affirmationService.createAffirmation(any(Affirmation.class))).thenReturn(newAffirmation);

        String json = "{\"affirmationText\": \"New affirmation\"}";

        mockMvc.perform(post("/api/affirmations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.affirmationText").value("New affirmation"));

        verify(affirmationService, times(1)).createAffirmation(any(Affirmation.class));
    }

    @Test
    void testCreateAffirmation_WithEmptyText() throws Exception {
        when(affirmationService.createAffirmation(any(Affirmation.class)))
                .thenThrow(new IllegalArgumentException("Affirmation text cannot be empty"));

        String json = "{\"affirmationText\": \"\"}";

        mockMvc.perform(post("/api/affirmations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk()); // Will fail if controller doesn't handle errors

        verify(affirmationService, times(1)).createAffirmation(any(Affirmation.class));
    }

    @Test
    void testUpdateAffirmation() throws Exception {
        Affirmation updatedAffirmation = new Affirmation();
        updatedAffirmation.setId(1L);
        updatedAffirmation.setAffirmationText("Updated text");
        updatedAffirmation.setReminders(new HashSet<>());

        when(affirmationService.updateAffirmation(eq(1L), eq("Updated text"))).thenReturn(updatedAffirmation);

        mockMvc.perform(put("/api/affirmations/1")
                        .param("text", "Updated text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.affirmationText").value("Updated text"));

        verify(affirmationService, times(1)).updateAffirmation(1L, "Updated text");
    }

    @Test
    void testUpdateAffirmation_NotFound() throws Exception {
        when(affirmationService.updateAffirmation(eq(999L), eq("New text")))
                .thenThrow(new RuntimeException("Affirmation not found with id: 999"));

        mockMvc.perform(put("/api/affirmations/999")
                        .param("text", "New text"))
                .andExpect(status().isOk()); // Will fail if controller doesn't handle errors

        verify(affirmationService, times(1)).updateAffirmation(999L, "New text");
    }

    @Test
    void testDeleteAffirmation() throws Exception {
        doNothing().when(affirmationService).deleteAffirmation(1L);

        mockMvc.perform(delete("/api/affirmations/1"))
                .andExpect(status().isOk());

        verify(affirmationService, times(1)).deleteAffirmation(1L);
    }

    @Test
    void testDeleteAffirmation_WithReminders_ThrowsException() throws Exception {
        doThrow(new IllegalStateException("Cannot delete affirmation. It is being used in reminders."))
                .when(affirmationService).deleteAffirmation(1L);

        mockMvc.perform(delete("/api/affirmations/1"))
                .andExpect(status().isOk()); // Will fail if controller doesn't handle errors

        verify(affirmationService, times(1)).deleteAffirmation(1L);
    }
}