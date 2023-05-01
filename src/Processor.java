import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Driver class
 */

public class Processor {

    public static void main(String args[]) throws InterruptedException {

        // init gui
        TruckNode.initGui();

        ExecutorService executorService = Executors.newCachedThreadPool();

        // set buffers
        BlockingBuffer serverShippingCenter1Buffer = new BlockingBuffer(25);
        BlockingBuffer serverShippingCenter2Buffer = new BlockingBuffer(25);
        
        BlockingBuffer shippingCenter1Section1Buffer = new BlockingBuffer(25);
        BlockingBuffer shippingCenter1Section2Buffer = new BlockingBuffer(25);
        
        BlockingBuffer shippingCenter1Section1ShippingDockBuffer = new BlockingBuffer(25);
        BlockingBuffer shippingCenter1Section2ShippingDockBuffer = new BlockingBuffer(25);

        BlockingBuffer shippingCenter2Section1Buffer = new BlockingBuffer(25);
        BlockingBuffer shippingCenter2Section2Buffer = new BlockingBuffer(25);

        BlockingBuffer shippingCenter2Section1ShippingDockBuffer = new BlockingBuffer(25);
        BlockingBuffer shippingCenter2Section2ShippingDockBuffer = new BlockingBuffer(25);

        BlockingBuffer shippingDock1Truck1Buffer = new BlockingBuffer(4);
        BlockingBuffer shippingDock1Truck2Buffer = new BlockingBuffer(4);
        BlockingBuffer shippingDock2Truck1Buffer = new BlockingBuffer(4);
        BlockingBuffer shippingDock2Truck2Buffer = new BlockingBuffer(4);

        // add server thread
        executorService.execute(new WebServerNode(serverShippingCenter1Buffer, serverShippingCenter2Buffer,
                "oral_exam2/S24_Amazon_Order_Processing_Hard/resources/S24_AmazonOrderProcessing_OrdersFile.csv"));

        // add shipping center threads
        executorService.execute(new ShippingCenterNode(serverShippingCenter1Buffer, shippingCenter1Section1Buffer,
                shippingCenter1Section2Buffer, 1));
        executorService.execute(new ShippingCenterNode(serverShippingCenter2Buffer, shippingCenter2Section1Buffer,
                shippingCenter2Section2Buffer, 2));

        // add shipping section threads
        executorService.execute(new ShippingSectionNode(shippingCenter1Section1Buffer, shippingCenter1Section1ShippingDockBuffer,
                1));
        executorService.execute(new ShippingSectionNode(shippingCenter1Section2Buffer, shippingCenter1Section2ShippingDockBuffer,
                2));

        executorService.execute(new ShippingSectionNode(shippingCenter2Section1Buffer, shippingCenter2Section1ShippingDockBuffer,
                1));
        executorService.execute(new ShippingSectionNode(shippingCenter2Section2Buffer, shippingCenter2Section2ShippingDockBuffer,
                2));

        // add shipping dock threads
        executorService.execute(new ShippingDockNode(shippingCenter1Section1ShippingDockBuffer, shippingCenter1Section2ShippingDockBuffer,
                shippingDock1Truck1Buffer, shippingDock1Truck2Buffer, 1));
        executorService.execute(new ShippingDockNode(shippingCenter2Section1ShippingDockBuffer, shippingCenter2Section2ShippingDockBuffer,
                shippingDock2Truck1Buffer, shippingDock2Truck2Buffer, 2));

        // add truck threads
        executorService.execute(new TruckNode(shippingDock1Truck1Buffer,1, 1));
        executorService.execute(new TruckNode(shippingDock1Truck2Buffer, 1,2));
        executorService.execute(new TruckNode(shippingDock2Truck1Buffer, 2, 1));
        executorService.execute(new TruckNode(shippingDock2Truck2Buffer, 2, 2));

        // shut down threads, wait til threads finish
        executorService.shutdown();
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        TruckNode.sendComplete();
    }
}
