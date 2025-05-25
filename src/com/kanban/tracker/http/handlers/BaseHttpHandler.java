package com.kanban.tracker.http.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kanban.tracker.controllers.HistoryManager;
import com.kanban.tracker.controllers.TaskManager;
import com.kanban.tracker.util.DurationAdapter;
import com.kanban.tracker.util.LocalDateTimeAdapter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {


    protected final HistoryManager historyManager;
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.historyManager = taskManager.getHistoryManager();
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    protected void sendText(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        httpExchange.sendResponseHeaders(200, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendCreated(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(201, 0);
        httpExchange.close();
    }

    protected void sendNotFound(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(404, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendHasInteractions(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(406, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendServerError(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(500, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected void sendMethodNotAllowed(HttpExchange httpExchange, String text) throws IOException {
        byte[] response = text.getBytes(StandardCharsets.UTF_8);
        httpExchange.sendResponseHeaders(405, response.length);
        try (OutputStream os = httpExchange.getResponseBody()) {
            os.write(response);
        }
    }

    protected int parseId(String query) {
        if (query == null || !query.startsWith("id=")) {
            throw new IllegalArgumentException("Неверный формат запроса. Ожидается id=");
        }
        return Integer.parseInt(query.replaceFirst("id=", ""));
    }
}
