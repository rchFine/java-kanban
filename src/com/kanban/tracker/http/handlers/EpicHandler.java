package com.kanban.tracker.http.handlers;

import com.google.gson.Gson;
import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.exceptions.NotFoundException;
import com.kanban.tracker.model.EpicTask;
import com.kanban.tracker.model.SubTask;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String query = httpExchange.getRequestURI().getQuery();

            if ("/epics".equals(path)) {
                switch (method) {
                    case "GET" -> handleGetAll(httpExchange);
                    case "POST" -> handlePost(httpExchange);
                    default -> sendNotFound(httpExchange, "Метод не поддерживается");
                }
            } else if ("/epics/epic".equals(path)) {
                switch (method) {
                    case "GET" -> handleGetById(httpExchange, query);
                    case "DELETE" -> handleDeleteById(httpExchange, query);
                    default -> sendNotFound(httpExchange, "Метод не поддерживается");
                }
            } else if ("/epics/subtasks".equals(path)) {
                if ("GET".equals(method)) {
                    handleGetSubtasksByEpic(httpExchange, query);
                } else {
                    sendNotFound(httpExchange, "Метод не поддерживается");
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
        List<EpicTask> epics = taskManager.getAllEpics();
        sendText(httpExchange, gson.toJson(epics));
    }

    private void handleGetById(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        EpicTask epic = taskManager.getEpicTaskById(id);
        sendText(httpExchange, gson.toJson(epic));
    }

    private void handleGetSubtasksByEpic(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        taskManager.getEpicTaskById(id);
        List<SubTask> subtasks = taskManager.getSubTasksByEpic(id);
        sendText(httpExchange, gson.toJson(subtasks));
    }

    private void handlePost(HttpExchange httpExchange) throws IOException {
        InputStream input = httpExchange.getRequestBody();
        String json = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        EpicTask epic = gson.fromJson(json, EpicTask.class);

        if (epic.getId() == 0) {
            taskManager.createEpicTask(epic);
        } else {
            taskManager.updateEpicTask(epic);
        }

        sendCreated(httpExchange);
    }

    private void handleDeleteById(HttpExchange httpExchange, String query) throws IOException {
        int id = parseId(query);
        taskManager.deleteEpicTaskById(id);
        sendText(httpExchange, "Эпик удалён.");
    }
}