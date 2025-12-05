package com.example.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
// TODO check the logic of this
public class JournalPrompt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // This holds the main prompt question (for GENERAL) or the title (for ANT_EXERCISE)
    @Lob
    @Column(nullable = false)
    private String promptText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JournalPromptType type;

    // This holds the 7 structured questions only for the ANT_EXERCISE type.
    @Lob
    @Column(nullable = true)
    private String prefilledQuestions;

    @OneToMany(mappedBy = "journalPrompt")
    private Set<JournalEntry> journalEntries;
}
