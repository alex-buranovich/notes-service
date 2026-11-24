package ru.alexburanovich.notes.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record NoteRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 10_000) String content,
        Set<@NotBlank @Size(max = 50) String> tags) {
}
