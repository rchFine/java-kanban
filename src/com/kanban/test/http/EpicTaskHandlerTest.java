package com.kanban.test.http;

import com.google.gson.Gson;
import com.kanban.tracker.controllers.InMemoryHistoryManager;
import com.kanban.tracker.controllers.InMemoryTaskManager;
import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.http.server.HttpTaskServer;
import com.kanban.tracker.model.EpicTask;
import com.kanban.tracker.model.SubTask;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EpicTaskHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer httpServer;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = HttpTaskServer.getGson();

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());
        httpServer = new HttpTaskServer(taskManager);
        httpServer.start();
    }

    @AfterEach
    public void tearDown() {
        httpServer.stop();
    }

    @Test
    public void shouldCreateEpic() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "EpicTask", "Description");
        String json = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void shouldReturnAllEpics() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "Epic All", "Check all");
        taskManager.createEpicTask(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic All"));
    }

    @Test
    public void shouldGetEpicById() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "Epic By ID", "Find me");
        taskManager.createEpicTask(epic);
        int id = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/epic?id=" + id))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic By ID"));
    }

    @Test
    public void shouldDeleteEpicById() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "Delete Epic", "To remove");
        taskManager.createEpicTask(epic);
        int id = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/epic?id=" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldReturnSubtasksForEpic() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "Parent Epic", "With subs");
        taskManager.createEpicTask(epic);
        int epicId = epic.getId();

        SubTask sub = new SubTask(0, "Subtask", "linked", epicId, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubTask(sub);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/subtasks?id=" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask"));
    }

    @Test
    public void shouldReturn404ForUnsupportedMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}

