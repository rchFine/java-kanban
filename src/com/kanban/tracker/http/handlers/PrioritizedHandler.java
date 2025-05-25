package com.kanban.tracker.http.handlers;

import com.google.gson.Gson;
import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.model.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String method = httpExchange.getRequestMethod();

            if ("GET".equals(method)) {
                handleGet(httpExchange);
            } else {
                sendNotFound(httpExchange, "Метод не поддерживается. Только GET.");
            }
        } catch (IOException e) {
            sendServerError(httpExchange, "Ошибка обработки запроса: " + e.getMessage());
        }
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        sendText(httpExchange, gson.toJson(prioritizedTasks));
    }
}