package ru.alexburanovich.notes.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.alexburanovich.notes.model.Note;
import ru.alexburanovich.notes.repository.NoteRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultNoteServiceTest {

    private static final Instant NOW = Instant.parse("2026-11-24T10:00:00Z");

    @Mock
    private NoteRepository repository;

    private DefaultNoteService service;

    @BeforeEach
    void setUp() {
        service = new DefaultNoteService(repository, Clock.fixed(NOW, ZoneOffset.UTC));
    }

    @Test
    void createSetsIdTimestampAndNormalizesTags() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Note note = service.create("  Plan ", "text", Set.of(" Work ", "IDEAS", " "));

        ArgumentCaptor<Note> captor = ArgumentCaptor.forClass(Note.class);
        verify(repository).save(captor.capture());
        Note saved = captor.getValue();
        assertThat(saved.id()).isNotNull();
        assertThat(saved.title()).isEqualTo("Plan");
        assertThat(saved.createdAt()).isEqualTo(NOW);
        assertThat(saved.tags()).containsExactlyInAnyOrder("work", "ideas");
        assertThat(note).isEqualTo(saved);
    }

    @Test
    void getThrowsWhenNoteMissing() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(id))
                .isInstanceOf(NoteNotFoundException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void listFiltersByNormalizedTag() {
        Note n = new Note(UUID.randomUUID(), "t", "c", NOW, Set.of("work"));
        when(repository.findByTag("work")).thenReturn(List.of(n));

        assertThat(service.list(" Work ")).containsExactly(n);
        verify(repository, never()).findAll();
    }

    @Test
    void listWithoutTagReturnsAll() {
        when(repository.findAll()).thenReturn(List.of());

        assertThat(service.list(null)).isEmpty();
        verify(repository).findAll();
        verify(repository, never()).findByTag(any());
    }

    @Test
    void updateKeepsIdAndCreatedAt() {
        UUID id = UUID.randomUUID();
        Instant created = NOW.minusSeconds(3600);
        when(repository.findById(id)).thenReturn(Optional.of(new Note(id, "old", "old", created, Set.of())));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Note updated = service.update(id, "new", "body", Set.of("home"));

        assertThat(updated.id()).isEqualTo(id);
        assertThat(updated.createdAt()).isEqualTo(created);
        assertThat(updated.title()).isEqualTo("new");
        assertThat(updated.tags()).containsExactly("home");
    }

    @Test
    void deleteThrowsWhenNoteMissing() {
        UUID id = UUID.randomUUID();
        when(repository.deleteById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(id)).isInstanceOf(NoteNotFoundException.class);
    }
}
