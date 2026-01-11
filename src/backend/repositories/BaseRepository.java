package backend.repositories;

import java.io.*;
import java.util.*;

import backend.models.BaseModel;

public abstract class BaseRepository<T extends BaseModel> {
    private final String key;
    private final File dbFile = new File("data/db.dat");

    protected BaseRepository(String key) {
        this.key = key;
    }

    protected String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @SuppressWarnings("unchecked")
    private Map<String, ArrayList<?>> readDB() {
        if (!dbFile.exists()) {
            return new HashMap<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(dbFile))) {
            // System.out.println("Reading DB from file " + dbFile.getName());
            return (Map<String, ArrayList<?>>) in.readObject();
        } catch (Exception e) {
            System.out.println("Error reading DB from file " + dbFile.getName() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<T> readAll() {
        Map<String, ArrayList<?>> db = readDB();
        return (ArrayList<T>) db.getOrDefault(key, new ArrayList<T>());
    }

    private void writeAll(ArrayList<T> items) {
        Map<String, ArrayList<?>> db = readDB();
        db.put(key, items);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(dbFile))) {
            out.writeObject(db);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
