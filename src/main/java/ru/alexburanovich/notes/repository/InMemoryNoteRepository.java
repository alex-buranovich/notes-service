package ru.alexburanovich.notes.repository;

import org.springframework.stereotype.Repository;
import ru.alexburanovich.notes.model.Note;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryNoteRepository implements NoteRepository {

    private final Map<UUID, Note> storage = new ConcurrentHashMap<>();

    @Override
    public Note save(Note note) {
        storage.put(note.id(), note);
        return note;
    }

    @Override
    public Optional<Note> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Note> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(Note::createdAt))
                .toList();
    }

    @Override
    public List<Note> findByTag(String tag) {
        return storage.values().stream()
                .filter(n -> n.tags().contains(tag))
                .sorted(Comparator.comparing(Note::createdAt))
                .toList();
    }

    @Override
    public boolean deleteById(UUID id) {
        return storage.remove(id) != null;
    }
}
