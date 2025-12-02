
import java.io.*;
import java.util.*;

public abstract class FileRepository<T extends Serializable> {

    private final File file;

    protected FileRepository(String filename) {
        this.file = new File(filename);
    }

    protected String generateUUID() {
        return UUID.randomUUID().toString();
    }

    @SuppressWarnings("unchecked")
    protected ArrayList<T> readAll() {
        if (!file.exists()) {
            // System.out.println("File " + file.getName() + " does not exist. Returning
            // empty list.");
            return new ArrayList<>();
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            // System.out.println("Reading from file " + file.getName());
            return (ArrayList<T>) in.readObject();
        } catch (Exception e) {
            System.out.println("Error reading from file " + file.getName() + ": " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected void writeAll(ArrayList<T> items) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(items);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public abstract String getId(T item);

    public void add(T item) {
        ArrayList<T> items = readAll();

        // update if exists
        items.removeIf(i -> getId(i).equals(getId(item)));
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
                .filter(i -> getId(i).equals(id))
                .findFirst();

    }

    public void deleteById(String id) {
        ArrayList<T> items = readAll();
        items.removeIf(i -> getId(i).equals(id));
        writeAll(items);
    }
}
