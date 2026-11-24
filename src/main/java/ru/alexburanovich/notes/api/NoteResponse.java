package ru.alexburanovich.notes.api;

import ru.alexburanovich.notes.model.Note;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record NoteResponse(UUID id, String title, String content, Instant createdAt, Set<String> tags) {
    static NoteResponse from(Note note) {
        return new NoteResponse(note.id(), note.title(), note.content(), note.createdAt(), note.tags());
    }
}
