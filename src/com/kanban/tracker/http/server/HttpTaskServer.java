package com.kanban.tracker.http.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kanban.tracker.controllers.HistoryManager;
import com.kanban.tracker.controllers.InMemoryHistoryManager;
import com.kanban.tracker.controllers.InMemoryTaskManager;
import com.kanban.tracker.http.handlers.*;
import com.kanban.tracker.util.DurationAdapter;
import com.kanban.tracker.util.LocalDateTimeAdapter;
import com.sun.net.httpserver.HttpServer;
import com.kanban.tracker.controllers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    private final HttpServer httpServer;
    private static final int PORT = 8080;
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServer(TaskManager taskManager) throws IOException {

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        httpServer.createContext("/tasks", new TaskHandler(taskManager, GSON));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager, GSON));
        httpServer.createContext("/epics", new EpicHandler(taskManager, GSON));
        httpServer.createContext("/history", new HistoryHandler(taskManager, GSON));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager, GSON));
    }


    public void start() {
        System.out.println("Запускаем HTTP сервер на порту " + PORT);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(1);
    }

    public static void main(String[] args) {
        try {
            HistoryManager historyManager = new InMemoryHistoryManager();
            TaskManager manager = new InMemoryTaskManager(historyManager);
            HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
            httpTaskServer.start();
        } catch (IOException e) {
            System.out.println("Ошибка запуска сервера " + e.getMessage());
        }
    }

    public static Gson getGson() {
        return GSON;
    }
}
