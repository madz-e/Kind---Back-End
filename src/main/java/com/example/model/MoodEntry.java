package com.example.model;

import com.example.model.enumerations.MoodCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoodEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    // TODO max the field to 10 - validation
    @Column(nullable = false)
    private Integer moodValue;  // Slider 1-10

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MoodCategory moodCategory;

    private String note;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ADDED: skip Hibernate proxy internals so JSON serialization works
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "mood_entry_emotion",
            joinColumns = @JoinColumn(name = "mood_entry_id"),
            inverseJoinColumns = @JoinColumn(name = "emotion_id")
    )
    private Set<Emotion> selectedEmotions = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "mood_entry_factor",
            joinColumns = @JoinColumn(name = "mood_entry_id"),
            inverseJoinColumns = @JoinColumn(name = "mood_factor_id")
    )
    private Set<MoodFactor> selectedFactors = new HashSet<>();

    // TODO optional!!!
    @OneToOne(mappedBy = "moodEntry", fetch = FetchType.LAZY)
    private JournalEntry journalEntry;

    // Automatically calculates category when setting mood value
    public void setMoodValue(Integer moodValue) {
        this.moodValue = moodValue;
        this.moodCategory = MoodCategory.fromValue(moodValue);
    }
}


