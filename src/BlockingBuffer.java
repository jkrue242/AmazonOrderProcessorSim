/**
 * Input/output buffer between nodes (taken from textbook examples)
 */
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
public class BlockingBuffer {

    /**
     * Constructor
     * @param capacity capacity of blocking queue
     */
    public BlockingBuffer(int capacity) {

        this.capacity = capacity;
        buffer = new ArrayBlockingQueue<>(capacity);
    }

    /**
     * inputs to buffer
     * @param values input
     * @return true if free space, false if full
     * @throws InterruptedException exception
     */
    public boolean blockingPut(ArrayList<String> values) throws InterruptedException {
        if (buffer.size() < capacity)
        {
            buffer.put(values);
            return true;
        }
        return false;
    }

    /**
     * return value from buffer
     * @return String
     * @throws InterruptedException exception
     */
    public ArrayList<String> blockingGet() throws InterruptedException {
        return buffer.take();
    }

    public ArrayBlockingQueue<ArrayList<String>> buffer;
    int capacity;
}