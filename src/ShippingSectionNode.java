import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class handles shipping section nodes
 */
public class ShippingSectionNode implements Runnable{

    /**
     * Constructor
     * @param from read buffer
     * @param to1 truck 1 buffer
     * @param id section id
     */
    public ShippingSectionNode(BlockingBuffer from, BlockingBuffer to1, int id)
    {
        read_buffer = from;
        write_buffer = to1;
        this.id = id;
    }

    /**
     * Overridden from Runnable
     */
    @Override
    public void run()
    {
        // read from buffer, send data to next buffer
        while (true) {
            try
            {
                ArrayList<String> order = read_buffer.blockingGet();

                // if done with orders, send and break
                if (order.get(0).equals("no more deliveries."))
                {
                    write_buffer.blockingPut(order);
                    break;
                }

                // add section number
                order.add(String.valueOf(id));

                // send order to shipping dock
                write_buffer.blockingPut(order);
            }
            catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    BlockingBuffer read_buffer;
    BlockingBuffer write_buffer;
    int id;

}
