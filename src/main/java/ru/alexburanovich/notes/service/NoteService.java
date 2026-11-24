package ru.alexburanovich.notes.service;

import ru.alexburanovich.notes.model.Note;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface NoteService {
    Note create(String title, String content, Set<String> tags);
    Note get(UUID id);
    List<Note> list(String tag);
    Note update(UUID id, String title, String content, Set<String> tags);
    void delete(UUID id);
}
