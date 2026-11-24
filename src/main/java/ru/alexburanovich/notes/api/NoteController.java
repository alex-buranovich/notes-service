package ru.alexburanovich.notes.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.alexburanovich.notes.model.Note;
import ru.alexburanovich.notes.service.NoteService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService service;

    public NoteController(NoteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<NoteResponse> create(@Valid @RequestBody NoteRequest request) {
        Note note = service.create(request.title(), request.content(), request.tags());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(note.id()).toUri();
        return ResponseEntity.created(location).body(NoteResponse.from(note));
    }

    @GetMapping("/{id}")
    public NoteResponse get(@PathVariable UUID id) {
        return NoteResponse.from(service.get(id));
    }

    @GetMapping
    public List<NoteResponse> list(@RequestParam(required = false) String tag) {
        return service.list(tag).stream().map(NoteResponse::from).toList();
    }

    @PutMapping("/{id}")
    public NoteResponse update(@PathVariable UUID id, @Valid @RequestBody NoteRequest request) {
        return NoteResponse.from(service.update(id, request.title(), request.content(), request.tags()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
