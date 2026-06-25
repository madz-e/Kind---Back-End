package com.example.model;

import com.example.model.enumerations.EntryType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"user", "moodEntry", "journalPrompt"})
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "created_at",nullable = false)
    private LocalDate createdAt;

    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryType type;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "moodEntries", "journalEntries", "habits", "reminders", "password", "authorities"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "journalEntry", "moodEntries", "journalEntries", "habits", "reminders", "selectedEmotions", "selectedFactors"})
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_entry_id", unique = true)
    private MoodEntry moodEntry;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "journalEntries"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id")
    private JournalPrompt journalPrompt;


}