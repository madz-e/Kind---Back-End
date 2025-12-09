package com.example.service.impl;

import com.example.jpaRepository.JournalPromptRepository;
import com.example.model.JournalPrompt;
import com.example.model.enumerations.JournalPromptType;
import com.example.service.JournalPromptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class JournalPromptServiceImpl implements JournalPromptService {

    private final JournalPromptRepository journalPromptRepository;

    @Override
    @Transactional(readOnly = true)
    public List<JournalPrompt> getAllPrompts() {
        return journalPromptRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<JournalPrompt> findById(Long id) {
        return journalPromptRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JournalPrompt> findByType(JournalPromptType type) {
        return journalPromptRepository.findByType(type);
    }

    @Override
    public JournalPrompt createPrompt(JournalPrompt prompt) {
        return journalPromptRepository.save(prompt);
    }

    @Override
    public JournalPrompt updatePrompt(Long id, JournalPrompt prompt) {
        JournalPrompt existingPrompt = journalPromptRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("JournalPrompt not found with id: " + id));

        existingPrompt.setPromptText(prompt.getPromptText());
        existingPrompt.setType(prompt.getType());

        return journalPromptRepository.save(existingPrompt);
    }

    @Override
    public void deletePrompt(Long id) {
        if (!journalPromptRepository.existsById(id)) {
            throw new IllegalArgumentException("JournalPrompt not found with id: " + id);
        }
        journalPromptRepository.deleteById(id);
    }
}