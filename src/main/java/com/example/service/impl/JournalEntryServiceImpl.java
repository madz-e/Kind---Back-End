package com.example.service.impl;

import com.example.jpaRepository.JournalEntryRepository;
import com.example.jpaRepository.UserRepository;
import com.example.jpaRepository.JournalPromptRepository;
import com.example.jpaRepository.MoodEntryRepository;
import com.example.model.JournalEntry;
import com.example.model.User;
import com.example.model.JournalPrompt;
import com.example.model.MoodEntry;
import com.example.model.enumerations.EntryType;
import com.example.service.JournalEntryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalEntryServiceImpl implements JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final UserRepository userRepository;
    private final JournalPromptRepository journalPromptRepository;
    private final MoodEntryRepository moodEntryRepository;

    @Override
    public JournalEntry createJournalEntry(JournalEntry journalEntry) {
        // Validate user exists
        User user = userRepository.findById(journalEntry.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        journalEntry.setUser(user);

        // Validate and set journal prompt if present
        if (journalEntry.getJournalPrompt() != null && journalEntry.getJournalPrompt().getId() != null) {
            JournalPrompt prompt = journalPromptRepository.findById(journalEntry.getJournalPrompt().getId())
                    .orElseThrow(() -> new IllegalArgumentException("JournalPrompt not found"));
            journalEntry.setJournalPrompt(prompt);
        }

        // Validate and set mood entry if present
        if (journalEntry.getMoodEntry() != null && journalEntry.getMoodEntry().getId() != null) {
            MoodEntry moodEntry = moodEntryRepository.findById(journalEntry.getMoodEntry().getId())
                    .orElseThrow(() -> new IllegalArgumentException("MoodEntry not found"));
            journalEntry.setMoodEntry(moodEntry);
        }

        return journalEntryRepository.save(journalEntry);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JournalEntry> findById(Long id) {
        return journalEntryRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JournalEntry> findByUserId(Long userId) {
        return journalEntryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JournalEntry> findByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return journalEntryRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JournalEntry> findByUserIdAndType(Long userId, EntryType type) {
        return journalEntryRepository.findByUserIdAndType(userId, type);
    }

    @Override
    public JournalEntry updateJournalEntry(Long id, JournalEntry journalEntry) {
        JournalEntry existingEntry = journalEntryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JournalEntry not found with id: " + id));

        existingEntry.setTitle(journalEntry.getTitle());
        existingEntry.setContent(journalEntry.getContent());
        existingEntry.setType(journalEntry.getType());

        // Update journal prompt if provided
        if (journalEntry.getJournalPrompt() != null && journalEntry.getJournalPrompt().getId() != null) {
            JournalPrompt prompt = journalPromptRepository.findById(journalEntry.getJournalPrompt().getId())
                    .orElseThrow(() -> new IllegalArgumentException("JournalPrompt not found"));
            existingEntry.setJournalPrompt(prompt);
        }

        return journalEntryRepository.save(existingEntry);
    }

    @Override
    public void deleteJournalEntry(Long id) {
        if (!journalEntryRepository.existsById(id)) {
            throw new IllegalArgumentException("JournalEntry not found with id: " + id);
        }
        journalEntryRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUserId(Long userId) {
        return journalEntryRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getWordFrequency(Long userId, LocalDateTime startDate, LocalDateTime endDate, int topN) {
        List<String> contents = journalEntryRepository.findAllContentByUserIdAndDateRange(userId, startDate, endDate);

        // TODO: check for AI impl
        // Process word frequency
        Map<String, Long> wordFrequency = new HashMap<>();

        for (String content : contents) {
            if (content != null && !content.isEmpty()) {
                // Convert to lowercase and split by non-word characters
                String[] words = content.toLowerCase().split("\\W+");

                for (String word : words) {
                    // Filter out very short words and common stop words
                    if (word.length() > 3 && !isStopWord(word)) {
                        wordFrequency.put(word, wordFrequency.getOrDefault(word, 0L) + 1);
                    }
                }
            }
        }

        // Return top N words
        return wordFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    private boolean isStopWord(String word) {
        // Basic stop words list - can be expanded
        Set<String> stopWords = Set.of(
                "the", "and", "that", "have", "for", "not", "with", "you", "this", "but",
                "from", "they", "was", "been", "were", "are", "has", "had", "will", "would",
                "could", "should", "can", "may", "might", "must", "shall", "about", "into",
                "through", "during", "before", "after", "above", "below", "between", "under"
        );
        return stopWords.contains(word);
    }
}