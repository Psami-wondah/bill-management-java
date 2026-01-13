package backend.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.*;
import java.util.*;

import backend.models.BaseModel;
import backend.models.Database;

public abstract class BaseRepository<T extends BaseModel> {
    private final File dbFile = new File("data/db.json");

    // Each concrete repo will say which list in Database it uses
    protected abstract List<T> getCollection(Database db);

    protected abstract void setCollection(Database db, List<T> items);

    protected String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public Database loadFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        if (dbFile.exists()) {
            try {
                return mapper.readValue(dbFile, Database.class);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new Database();
    }

    private void saveToJson(Database data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(dbFile, data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Database readDB() {
        if (!dbFile.exists()) {
            return new Database();
        }
        try {
            return loadFromJson();
        } catch (Exception e) {
            System.out.println("Error reading DB from file " + dbFile.getName() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private ArrayList<T> readAll() {
        Database db = readDB();
        List<T> list = getCollection(db);
        if (list == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(list);
    }

    private void writeAll(ArrayList<T> items) {
        Database db = readDB();
        setCollection(db, new ArrayList<>(items));
        saveToJson(db);
    }

    public void add(T item) {
        ArrayList<T> items = readAll();

        // update if exists
        items.removeIf(i -> i.getId().equals(item.getId()));
        items.add(item);

        writeAll(items);
    }

    public ArrayList<T> findAll() {
        return readAll();
    }

    public ArrayList<T> filter(java.util.function.Predicate<T> predicate) {
        ArrayList<T> items = readAll();
        ArrayList<T> result = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public Optional<T> findById(String id) {
        return readAll().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst();

    }

    public void deleteById(String id) {
        ArrayList<T> items = readAll();
        items.removeIf(i -> i.getId().equals(id));
        writeAll(items);
    }

    public String generateId(int digits) {
        return String.format("%0" + digits + "d", this.readAll().size() + 1);
    }

}
