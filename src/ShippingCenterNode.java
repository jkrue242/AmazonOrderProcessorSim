import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * This class handles Shipping Center
 */
public class ShippingCenterNode implements Runnable{

    /**
     * Constructor for shipping center node
     * @param from buffer location to read from
     * @param to_1 buffer location to write to
     * @param to_2 buffer location to write to
     * @param id id of shipping center
     */
    public ShippingCenterNode(BlockingBuffer from, BlockingBuffer to_1, BlockingBuffer to_2, int id)
    {
        read_buffer = from;
        writeBuffer1 = to_1;
        writeBuffer2 = to_2;
        this.id = id;
    }

    /**
     * Overridden from Runnable interface
     */
    @Override
    public void run()
    {
        // read from buffer, send data to next buffer
        while (true) {
            try
            {
                ArrayList<String> order = read_buffer.blockingGet();

                // if processing a valid order
                if (order.size() > 0)
                {
                    // if done with orders, send and break
                    if (order.get(0).equals("no more deliveries."))
                    {
                        writeBuffer1.blockingPut(order);
                        writeBuffer2.blockingPut(order);
                        break;
                    }

                    // add tracking number
                    order.add(String.valueOf(id));

                    // random pause between 0-5 seconds
                    Thread.sleep(generator.nextInt(2000));

                    // send order to shipping section
                    String category_letter = "abcdefghijklmnopABCDEFGHIJKLMNOP";
                    if (category_letter.indexOf(order.get(6).charAt(0)) >= 0)
                    {
                        writeBuffer1.blockingPut(order);
                    }
                    else
                    {
                        writeBuffer2.blockingPut(order);
                    }
                }
            }
            catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    BlockingBuffer read_buffer;
    BlockingBuffer writeBuffer1;
    BlockingBuffer writeBuffer2;
    private final int id;
    private static final SecureRandom generator = new SecureRandom();

}
