package manager;

import server.HttpTaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Manager {
    public static TaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:8078");
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager getDefaultFile() {
        return new FileBackedTasksManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        try {
            return gsonBuilder
                    .serializeNulls()
                    .registerTypeAdapter(LocalDateTime.class, new DateTypeAdapter())
                    .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                    .create();
        } catch (NullPointerException e) {
            return gsonBuilder.serializeNulls().create();
        }
    }

    public static class DateTypeAdapter extends TypeAdapter<LocalDateTime> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm");

        @Override
        public void write(JsonWriter jsonWriter, LocalDateTime localDateTime) throws IOException, NullPointerException {
            if (localDateTime != null) {
                jsonWriter.value(localDateTime.format(formatter));
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public LocalDateTime read(JsonReader jsonReader) throws IOException, NullPointerException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return LocalDateTime.parse(jsonReader.nextString(), formatter);
            }
        }
    }

    public static class DurationTypeAdapter extends TypeAdapter<Duration> {
        @Override
        public void write(JsonWriter jsonWriter, Duration duration) throws IOException, NullPointerException {
            if (duration != null) {
                jsonWriter.value(duration.toMinutes());
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public Duration read(JsonReader jsonReader) throws IOException, NullPointerException {
            if (jsonReader.peek() == JsonToken.NULL) {
                jsonReader.nextNull();
                return null;
            } else {
                return Duration.ofMinutes(jsonReader.nextInt());
            }
        }
    }
}
