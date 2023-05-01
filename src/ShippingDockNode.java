import java.util.ArrayList;

/**
 * This class handles the shipping dock node
 */
public class ShippingDockNode implements Runnable {

    /**
     * Constructor
     * @param from1 read buffer from section 1
     * @param from2 read buffer from section 2
     * @param to1 write buffer to truck 1
     * @param to2 write buffer to truck 2
     * @param id dock id
     */
    public ShippingDockNode(BlockingBuffer from1, BlockingBuffer from2, BlockingBuffer to1, BlockingBuffer to2, int id)
    {
        read_buffer1 = from1;
        read_buffer2 = from2;
        write_buffer1 = to1;
        write_buffer2 = to2;
        this.id = id;
    }

    /**
     * Overridden from Runnable
     */
    @Override
    public void run()
    {
        boolean section1_done = false;
        boolean section2_done = false;

        try
        {
            Thread.sleep(400);

            // process from section 1
            while (!section1_done)
            {
                section1_done = process_order(read_buffer1);
            }

            // process from section 2
            while (!section2_done)
            {
                section2_done = process_order(read_buffer2);
            }

            ArrayList<String> finish_deliver = new ArrayList<>();
            finish_deliver.add("no more deliveries.");

            boolean truck_1_complete = false;
            boolean truck_2_complete = false;

            // make sure both trucks are notified when complete
            while (true)
            {
                if (write_buffer1.blockingPut(finish_deliver))
                {
                    truck_1_complete = true;
                }
                if (write_buffer2.blockingPut(finish_deliver))
                {
                    truck_2_complete = true;
                }
                if (truck_1_complete && truck_2_complete)
                {
                    break;
                }
            }

        }

        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Processes an order and writes it to buffer
     * @param read_buffer buffer we are reading from
     * @return true if we have completed all deliveries, false otherwise
     * @throws InterruptedException exception
     */
    private boolean process_order(BlockingBuffer read_buffer) throws InterruptedException
    {
        // get order from buffer
        ArrayList<String> order = read_buffer.blockingGet();

        // wait until truck opens
        if (order.get(0).equals("no more deliveries."))
        {
            return true;
        }

        while(true)
        {
            if (write_buffer1.blockingPut(order))
            {
                return false;
            }
            if (write_buffer2.blockingPut(order))
            {
                return false;
            }
        }
    }

    BlockingBuffer read_buffer1;
    BlockingBuffer read_buffer2;
    BlockingBuffer write_buffer1;
    BlockingBuffer write_buffer2;
    int id;
}
