package ru.alexburanovich.notes.repository;

import ru.alexburanovich.notes.model.Note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository {
    Note save(Note note);
    Optional<Note> findById(UUID id);
    List<Note> findAll();
    List<Note> findByTag(String tag);
    boolean deleteById(UUID id);
}
