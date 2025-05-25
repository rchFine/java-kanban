package com.kanban.tracker.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {

    @Override
    public void write(JsonWriter writer, Duration value) throws IOException {
        writer.value(value.toString());
    }

    @Override
    public Duration read(JsonReader reader) throws IOException {
        return Duration.parse(reader.nextString());
    }
}
