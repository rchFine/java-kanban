package com.kanban.tracker.http.handlers;

import com.google.gson.Gson;
import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.exceptions.NotFoundException;
import com.kanban.tracker.model.SubTask;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();

            if ("/subtasks".equals(path)) {
                switch (method) {
                    case "GET" -> handleGetAll(httpExchange);
                    case "POST" -> handlePost(httpExchange);
                    default -> sendNotFound(httpExchange, "Метод не поддерживается");
                }
            } else if ("/subtasks/subtask".equals(path)) {
                switch (method) {
                    case "GET" -> handleGetById(httpExchange, query);
                    case "DELETE" -> handleDeleteById(httpExchange, query);
                    default -> sendNotFound(httpExchange, "Метод не поддерживается");
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
        List<SubTask> subtasks = taskManager.getAllSubTasks();
        sendText(httpExchange, gson.toJson(subtasks));
    }

    private void handleGetById(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        SubTask sub = taskManager.getSubTaskById(id);
        sendText(httpExchange, gson.toJson(sub));
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        InputStream input = httpExchange.getRequestBody();
        String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        SubTask sub = gson.fromJson(json, SubTask.class);

        if (sub.getId() == 0) {
            taskManager.createSubTask(sub);
        } else {
            taskManager.updateSubTask(sub);
        }

        sendCreated(httpExchange);
    }

    private void handleDeleteById(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        taskManager.deleteSubTaskById(id);
        sendText(httpExchange, "Подзадача удалена.");
    }
}