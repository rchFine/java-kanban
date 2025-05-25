package com.kanban.tracker.http.handlers;

import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.exceptions.NotFoundException;
import com.kanban.tracker.model.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();

            if (path.equals("/tasks")) {
                switch (method) {
                    case "GET" -> handleGetAll(httpExchange);
                    case "POST" -> handlePost(httpExchange);
                    default -> sendMethodNotAllowed(httpExchange, "Метод не поддерживается");
                }
            } else if ("/tasks/task".equals(path)) {
                switch (method) {
                    case "GET" -> handleGetById(httpExchange, query);
                    case "DELETE" -> handleDeleteById(httpExchange, query);
                    default -> sendMethodNotAllowed(httpExchange, "Метод не поддерживается");
                }
            } else {
                sendNotFound(httpExchange, "Ресурс не найден");
            }

        } catch (NotFoundException e) {
            sendNotFound(httpExchange, e.getMessage());
        } catch (IllegalArgumentException e) {
            sendHasInteractions(httpExchange, e.getMessage());
        } catch (IOException e) {
            sendServerError(httpExchange, "Ошибка при обработке запроса: " + e.getMessage());
        }
    }

    private void handleGetAll(HttpExchange httpExchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(httpExchange, gson.toJson(tasks));
    }

    private void handleGetById(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        Task task = taskManager.getTaskById(id);
        sendText(httpExchange, gson.toJson(task));
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        InputStream input = httpExchange.getRequestBody();
        String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(json, Task.class);

        if (task.getId() == 0) {
            taskManager.createTask(task);
        } else {
            taskManager.updateTask(task);
        }

        sendCreated(httpExchange);
    }

    private void handleDeleteById(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        taskManager.deleteTaskById(id);
        sendText(httpExchange, "Задача удалена.");
    }
}