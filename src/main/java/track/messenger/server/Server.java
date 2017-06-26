package track.messenger.server;

import track.messenger.Constants;
import track.messenger.server.utils.MessageBuffer;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by jjenkov on 24-10-2015.
 * Modified by artem on 12.04.2017.
 */
public class Server {

    private SocketAccepter socketAccepter = null;
    private SocketProcessor socketProcessor = null;

    private int tcpPort = 0;

    /**
     * Server based on NIO-server implementation of jjenkov
     *
     * @param tcpPort Can be 0, in that way the default port would be used
     */
    public Server(int tcpPort) {
        if (0 == tcpPort) {
            tcpPort = Constants.DEFAULT_SERVER_PORT;
        }
        this.tcpPort = tcpPort;
    }

    public void start() throws IOException {

        Queue socketQueue = new ArrayBlockingQueue(Constants.SERVER_SOCKET_QUEUE_SIZE);

        this.socketAccepter = new SocketAccepter(tcpPort, socketQueue);


        MessageBuffer readBuffer = new MessageBuffer();
        MessageBuffer writeBuffer = new MessageBuffer();

        this.socketProcessor = new SocketProcessor(socketQueue, readBuffer, writeBuffer);

        Thread accepterThread = new Thread(this.socketAccepter);
        Thread processorThread = new Thread(this.socketProcessor);

        accepterThread.start();
        processorThread.start();
    }

    public static void main(String[] args) {
        Server server = new Server(0);
        try {
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
