package com.kanban.tracker.http.handlers;

import com.google.gson.Gson;
import com.kanban.tracker.controllers.HistoryManager;
import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.model.Task;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final HistoryManager historyManager;
    private final Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.historyManager = taskManager.getHistoryManager();
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
        List<Task> history = historyManager.getHistory();
        sendText(httpExchange, gson.toJson(history));
    }
}