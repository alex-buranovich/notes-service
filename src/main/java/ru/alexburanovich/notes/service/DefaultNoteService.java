package ru.alexburanovich.notes.service;

import org.springframework.stereotype.Service;
import ru.alexburanovich.notes.model.Note;
import ru.alexburanovich.notes.repository.NoteRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DefaultNoteService implements NoteService {

    private final NoteRepository repository;
    private final Clock clock;

    public DefaultNoteService(NoteRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public Note create(String title, String content, Set<String> tags) {
        Note note = new Note(UUID.randomUUID(), title.trim(), content, Instant.now(clock), normalize(tags));
        return repository.save(note);
    }

    @Override
    public Note get(UUID id) {
        return repository.findById(id).orElseThrow(() -> new NoteNotFoundException(id));
    }

    @Override
    public List<Note> list(String tag) {
        if (tag == null || tag.isBlank()) {
            return repository.findAll();
        }
        return repository.findByTag(normalizeTag(tag));
    }

    @Override
    public Note update(UUID id, String title, String content, Set<String> tags) {
        Note existing = get(id);
        Note updated = new Note(existing.id(), title.trim(), content, existing.createdAt(), normalize(tags));
        return repository.save(updated);
    }

    @Override
    public void delete(UUID id) {
        if (!repository.deleteById(id)) {
            throw new NoteNotFoundException(id);
        }
    }

    private static Set<String> normalize(Set<String> tags) {
        if (tags == null) {
            return Set.of();
        }
        return tags.stream()
                .filter(t -> t != null && !t.isBlank())
                .map(DefaultNoteService::normalizeTag)
                .collect(Collectors.toUnmodifiableSet());
    }

    private static String normalizeTag(String tag) {
        return tag.trim().toLowerCase(Locale.ROOT);
    }
}
