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

import static org.junit.jupiter.api.Assertions.*;

public class SubTaskHandlerTest {

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
    public void shouldCreateSubtask() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "Epic", "For subtasks");
        taskManager.createEpicTask(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask(0, "Subtask 1", "Details", epicId, LocalDateTime.now(), Duration.ofMinutes(45));
        String json = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }

    @Test
    public void shouldReturnAllSubtasks() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "Epic", "For list");
        taskManager.createEpicTask(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask(0, "Listed Subtask", "Visible", epicId, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.createSubTask(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Listed Subtask"));
    }

    @Test
    public void shouldGetSubtaskById() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "Epic", "Parent");
        taskManager.createEpicTask(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask(0, "FindMe", "By ID", epicId, LocalDateTime.now(), Duration.ofMinutes(20));
        taskManager.createSubTask(subTask);
        int subTaskId = subTask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/subtask?id=" + subTaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("FindMe"));
    }

    @Test
    public void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        EpicTask epic = new EpicTask(0, "EpicTask", "To delete");
        taskManager.createEpicTask(epic);
        int epicId = epic.getId();

        SubTask subTask = new SubTask(0, "DeleteMe", "Soon gone", epicId, LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.createSubTask(subTask);
        int id = subTask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/subtask?id=" + id))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void shouldReturn404WhenDeletingMissingSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/subtask?id=9999"))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturn404ForMissingSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/subtask?id=12345"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturn404ForUnknownPath() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/unknown"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void shouldReturn405ForUnsupportedMethod() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .PUT(HttpRequest.BodyPublishers.ofString("wqeq32"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}