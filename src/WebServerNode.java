import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * This class handles the AWS Node
 */

public class WebServerNode implements Runnable {

    /**
     * Constructor
     * @param to1 buffer to write to
     * @param to2 buffer to write to
     * @param path dataset path
     */
    public WebServerNode(BlockingBuffer to1, BlockingBuffer to2, String path)
    {
        // initialize shared locations
        writeBuffer1 = to1;
        writeBuffer2 = to2;
        this.path = path;
    }

    /**
     * reads csv file with orders to ArrayList
     * @param file file path
     * @return array list of strings
     */
    public ArrayList<ArrayList<String>> readCsv(String file)
    {
        ArrayList<ArrayList<String>> data = new ArrayList<>();

        // read csv file
        try
        {
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(file));
            int row = 0;
            while ((line = br.readLine()) != null)
            {
                // discard first line
                if (row != 0)
                {
                    // add line data to data list
                    ArrayList<String> line_data = new ArrayList<>();
                    line_data.addAll(Arrays.asList(line.split(",")));
                    data.add(line_data);
                }
                row+=1;
            }
        }
        // if the file could not be opened
        catch (Exception e)
        {
            System.out.println("The dataset file was not found.");
            e.getStackTrace();
        }
        return data;
    }

    /**
     * Method overridden from Runnable interface
     */
    @Override
    public void run() {

        // shipping center 1 locations
        HashSet<String> shippingCenter1Loc = new HashSet<>();
        shippingCenter1Loc.add("Los Angeles");
        shippingCenter1Loc.add("San Francisco");
        shippingCenter1Loc.add("Seattle");
        shippingCenter1Loc.add("Denver");

        // get order data
        orders = readCsv(path);

        // send orders to buffer
        for (int i = 0; i < orders.size(); i++) {

            try {
                // check if we need to go to shipping center 1, else shipping center 2
                if (shippingCenter1Loc.contains(orders.get(i).get(1)))
                {
                    writeBuffer1.blockingPut(orders.get(i));
                }
                else
                {
                    writeBuffer2.blockingPut(orders.get(i));
                }
            }
            catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }

        // send finish message
        ArrayList<String> finish_message = new ArrayList<>();
        finish_message.add("no more deliveries.");

        try {
            writeBuffer1.blockingPut(finish_message);
            writeBuffer2.blockingPut(finish_message);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    ArrayList<ArrayList<String>> orders;
    private final BlockingBuffer writeBuffer1;
    private final BlockingBuffer writeBuffer2;
    String path;
}
