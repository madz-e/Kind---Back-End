package com.example.service;

import com.example.jpaRepository.AffirmationRepository;
import com.example.model.Affirmation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class AffirmationService {
    private final AffirmationRepository affirmationRepository;

    // Pre-set affirmations as backup
    private static final List<String> DEFAULT_AFFIRMATIONS = Arrays.asList(
            "I am worthy of love and respect.",
            "I choose to see the good in every situation.",
            "My potential is limitless.",
            "I am capable of achieving my goals.",
            "I radiate positive energy.",
            "I am grateful for my strength and resilience.",
            "Every day, I grow stronger and wiser.",
            "I trust the journey of my life.",
            "I am at peace with who I am.",
            "I attract abundance and prosperity.",
            "My mind is calm and focused.",
            "I deserve happiness and fulfillment.",
            "I am confident in my abilities.",
            "I let go of what no longer serves me.",
            "I am creating a life I love."
    );

    // 1. Get random affirmation (for daily affirmation)
    public Affirmation getRandomAffirmation() {
// Try to get from database first
        Optional<Affirmation> randomAffirmation = affirmationRepository.findRandom();

        if (randomAffirmation.isPresent()) {
            return randomAffirmation.get();
        }

        // If database is empty, check if we should initialize it
        if (affirmationRepository.count() == 0) {
            // Initialize with defaults
            initializeDefaultAffirmations();

            // Try again after initialization
            return affirmationRepository.findRandom()
                    .orElse(getFallbackAffirmation());
        }

        // Database exists but findRandom returned empty (shouldn't happen)
        return getFallbackAffirmation();
    }

    // 2. Get all affirmations
    public List<Affirmation> getAllAffirmations() {
        List<Affirmation> affirmations = affirmationRepository.findAll();

        // If empty, initialize with defaults
        if (affirmations.isEmpty()) {
            initializeDefaultAffirmations();
            affirmations = affirmationRepository.findAll();
        }

        return affirmations;
    }

    @Transactional
    public void initializeDefaultAffirmations() {
        if (affirmationRepository.count() == 0) {
            for (String text : DEFAULT_AFFIRMATIONS) {
                Affirmation affirmation = new Affirmation();
                affirmation.setAffirmationText(text);
                affirmationRepository.save(affirmation);
            }
        }
    }

    /**
     * Get today's affirmation (same for entire day)
     * Uses day of year to pick consistent affirmation
     */
    public Affirmation getTodaysAffirmation() {
        // Get all affirmations
        List<Affirmation> allAffirmations = getAllAffirmations();

        if (allAffirmations.isEmpty()) {
            return getFallbackAffirmation();
        }

        // Use day of year to pick consistent affirmation per day
        int dayOfYear = java.time.LocalDate.now().getDayOfYear();
        int index = dayOfYear % allAffirmations.size();

        return allAffirmations.get(index);
    }

    // 3. Get affirmation by ID
    public Affirmation getAffirmationById(Long id) {
        return affirmationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Affirmation not found with id: " + id));
    }

    // 4. Save new affirmation
    public Affirmation createAffirmation(Affirmation affirmation) {
        if (affirmation.getAffirmationText() == null || affirmation.getAffirmationText().trim().isEmpty()) {
            throw new IllegalArgumentException("Affirmation text cannot be empty");
        }
        return affirmationRepository.save(affirmation);
    }

    // 5. Update affirmation
    public Affirmation updateAffirmation(Long id, String newText) {
        Affirmation affirmation = getAffirmationById(id);
        affirmation.setAffirmationText(newText);
        return affirmationRepository.save(affirmation);
    }

    // 6. Delete affirmation
    @Transactional
    public void deleteAffirmation(Long id) {
        // Check if affirmation exists
        Affirmation affirmation = getAffirmationById(id);

        // Check if it's being used in any reminders
        if (!affirmation.getReminders().isEmpty()) {
            throw new IllegalStateException("Cannot delete affirmation. It is being used in " +
                    affirmation.getReminders().size() + " reminder(s).");
        }

        affirmationRepository.deleteById(id);
    }

    /**
     * Fallback affirmation if everything else fails
     */
    private Affirmation getFallbackAffirmation() {
        Affirmation fallback = new Affirmation();
        fallback.setId(0L);
        fallback.setAffirmationText("You are enough just as you are.");
        return fallback;
    }



}