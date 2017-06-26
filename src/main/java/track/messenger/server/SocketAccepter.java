package track.messenger.server;

import track.messenger.Constants;
import track.messenger.server.utils.Socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Queue;

/**
 * Created by jjenkov on 24-10-2015.
 * Modified by artem on 12.04.2017.
 */
public class SocketAccepter implements Runnable {

    private int tcpPort = 0;
    private ServerSocketChannel serverSocket = null;

    private Queue socketQueue = null;

    public SocketAccepter(int tcpPort, Queue socketQueue) {
        this.tcpPort = tcpPort;
        this.socketQueue = socketQueue;
    }


    public void run() {
        try {
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.configureBlocking(false);
            this.serverSocket.bind(new InetSocketAddress(tcpPort));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        while (true) {
            try {
                SocketChannel socketChannel = this.serverSocket.accept();
                if (null != socketChannel) {
                    System.out.println("Socket accepted: " + socketChannel);

                    if (this.socketQueue.size() < Constants.SERVER_SOCKET_QUEUE_SIZE) {
                        this.socketQueue.add(new Socket(socketChannel));
                    } else {
                        socketChannel.close();
                        System.out.println(String.format("Socket queue is full: %i from %i",
                                this.socketQueue.size(),
                                Constants.SERVER_SOCKET_QUEUE_SIZE));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
}
