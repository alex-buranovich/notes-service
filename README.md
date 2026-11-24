# notes-service

REST API для заметок. Spring Boot 3, Java 17, хранение в памяти.

## API

- `POST /notes` — создать заметку (201)
- `GET /notes/{id}` — получить (404, если нет)
- `GET /notes?tag=work` — список, фильтр по тегу необязательный
- `PUT /notes/{id}` — обновить
- `DELETE /notes/{id}` — удалить (204)

```json
{ "title": "План", "content": "Текст", "tags": ["work"] }
```

## Запуск

```bash
mvn test
mvn spring-boot:run
```

Docker:

```bash
docker build -t notes-service .
docker run -p 8080:8080 notes-service
```
