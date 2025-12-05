package com.example.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime entryDateTime;

    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    // TODO empty, prompt, ANT
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    //TODO optional
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mood_entry_id", unique = true)
    private MoodEntry moodEntry; // Relationship: One-to-One with MoodEntry (Optional)

    //TODO optional (nullable = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prompt_id")
    private JournalPrompt journalPrompt; // Relationship: Many-to-One wi


}