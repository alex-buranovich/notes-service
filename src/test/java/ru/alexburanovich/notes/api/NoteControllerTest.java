package ru.alexburanovich.notes.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.alexburanovich.notes.model.Note;
import ru.alexburanovich.notes.service.NoteNotFoundException;
import ru.alexburanovich.notes.service.NoteService;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NoteService service;

    @Test
    void createReturns201WithLocation() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.create(any(), any(), any()))
                .thenReturn(new Note(id, "Plan", "text", Instant.now(), Set.of("work")));

        mvc.perform(post("/notes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Plan\",\"content\":\"text\",\"tags\":[\"work\"]}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/notes/" + id))
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

    @Test
    void createWithBlankTitleReturns400() throws Exception {
        mvc.perform(post("/notes").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\" \",\"content\":\"text\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMissingReturns404() throws Exception {
        UUID id = UUID.randomUUID();
        when(service.get(id)).thenThrow(new NoteNotFoundException(id));

        mvc.perform(get("/notes/{id}", id)).andExpect(status().isNotFound());
    }

    @Test
    void listByTag() throws Exception {
        when(service.list("work")).thenReturn(
                List.of(new Note(UUID.randomUUID(), "a", "b", Instant.now(), Set.of("work"))));

        mvc.perform(get("/notes").param("tag", "work"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void deleteReturns204() throws Exception {
        mvc.perform(delete("/notes/{id}", UUID.randomUUID())).andExpect(status().isNoContent());
    }
}
