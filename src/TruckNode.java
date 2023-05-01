import java.security.SecureRandom;
import java.util.ArrayList;

/**
 * This class handles the Truck nodes
 */
public class TruckNode implements Runnable {

    /**
     * Constructor
     * @param from read buffer from shipping dock
     * @param truck_id truck number
     */
    public TruckNode(BlockingBuffer from, int shipping_center_id, int truck_id)
    {
        read_buffer = from;
        this.truck_id = truck_id;
        this.shipping_center_id = shipping_center_id;
    }

    /**
     * Initializes gui
     */
    static void initGui()
    {
        gui = new TableGui();
    }

    /**
     * Sends finish message to gui
     */
    static void sendComplete()
    {
        String[] finish = {"ALL ORDERS COMPLETE"};
        gui.addRow(finish);
    }

    /**
     * Overridden from Runnable Interface
     */
    @Override
    public void run()
    {
        try
        {
            // loop until orders complete
            while (true)
            {
                boolean complete = false;
                ArrayList<ArrayList<String>> batch = new ArrayList<>();

                // see if no more deliveries notification
                boolean found = false;
                for (ArrayList<String> o : read_buffer.buffer)
                {
                    if (o.get(0).equals("no more deliveries."))
                    {
                        found = true;
                    }
                }

                // process if orders done or full
                if (read_buffer.buffer.size() == 4 || found)
                {

                    // loop until batch full or complete
                    while (batch.size() < 4)
                    {
                        // read new order
                        ArrayList<String> order = read_buffer.blockingGet();

                        // break if complete
                        if (order.get(0).equals("no more deliveries."))
                        {
                            complete = true;
                            break;
                        }

                        // add truck truck_id
                        order.add(String.valueOf(truck_id));
                        batch.add(order);
                    }

                    // dynamically add orders to the gui
                    for (int j = 0; j < batch.size(); j++)
                    {
                        Thread.sleep(generator.nextInt(10000));

                        // create row of order data
                        String[] row = new String[batch.get(j).size()];
                        for (int k = 0; k < batch.get(j).size(); k++)
                        {
                            row[k] = batch.get(j).get(k);
                        }

                        // add row to table
                        gui.addRow(row);
                    }

                    // break if complete
                    if (complete)
                    {
                        String[] finish = {"Center "+shipping_center_id + " Truck "+truck_id+ " Done", "", "", "", "", "", "", String.valueOf(shipping_center_id),
                                "", String.valueOf(truck_id)};
                        gui.addRow(finish);
                        break;
                    }
                }
            }

        }
        catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
        }
    }

    static TableGui gui;
    private BlockingBuffer read_buffer;
    private int truck_id;
    private int shipping_center_id;
    private static final SecureRandom generator = new SecureRandom();

}
