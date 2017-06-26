import org.junit.Test;
import track.messenger.Constants;
import track.messenger.client.MessengerClient;
import track.messenger.net.protocol.BinaryProtocol;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.Server;

import java.io.IOException;

/**
 * Created by artem on 19.06.17.
 */
public class ClientTest {
    @Test
    public void loginSuccess() throws InterruptedException {
        loginSuccess("Alex", "Alex");
    }

    @Test
    public void sendTextMessage() throws InterruptedException {
        MessengerClient client = loginSuccess("Alex", "Alex");
        try {
            Thread.sleep(2000);
            client.processInput("/text 1 It is test message from Alex");
        } catch (ProtocolException | IOException e) {
            System.out.println("Failed to process user input: " + e.getMessage());
        }
        Thread.sleep(2000);
//        return client;
    }

    @Test
    public void updateChatHistoryMessage() throws InterruptedException {
        MessengerClient client = loginSuccess("Alex", "Alex");
        try {
            Thread.sleep(2000);
            client.processInput("/chathist 1 3");
            Thread.sleep(2000);
            client.processInput("/showchats");
        } catch (ProtocolException | IOException e) {
            System.out.println("Failed to process user input: " + e.getMessage());
        }
        Thread.sleep(3000);
//        return client;
    }

    @Test
    public void createUserMessage() throws InterruptedException {
        StartServer();
        MessengerClient client = StartClient();
        try {
            Thread.sleep(2000);
            client.processInput("/newuser Eva Eva12");
        } catch (ProtocolException | IOException e) {
            System.out.println("Failed to process user input: " + e.getMessage());
        }
        Thread.sleep(300000);
//        return client;
    }

    @Test
    public void createChatMessage() throws InterruptedException {
        MessengerClient client = loginSuccess("Alex", "Alex");
        try {
            Thread.sleep(2000);
            client.processInput("/newchat alex_eva users:Alex,Eva");
        } catch (ProtocolException | IOException e) {
            System.out.println("Failed to process user input: " + e.getMessage());
        }
        Thread.sleep(300000);
//        return client;
    }

    @Test
    public void addUsersToChatMessage() throws InterruptedException {
        MessengerClient client = loginSuccess("Alex", "Alex");
        try {
            Thread.sleep(2000);
            client.processInput("/addtochat alex_eva users:Kate");
        } catch (ProtocolException | IOException e) {
            System.out.println("Failed to process user input: " + e.getMessage());
        }
        Thread.sleep(300000);
//        return client;
    }

    @Test
    public void leaveChatMessage() throws InterruptedException {
        MessengerClient client = loginSuccess("Kate", "Kate");
        try {
            Thread.sleep(2000);
            client.processInput("/leavechat alex_eva");
        } catch (ProtocolException | IOException e) {
            System.out.println("Failed to process user input: " + e.getMessage());
        }
        Thread.sleep(300000);
//        return client;
    }

    private MessengerClient StartClient() {
        MessengerClient client = new MessengerClient();
        client.setHost(Constants.DEFAULT_SERVER_ADDRESS);
        client.setPort(Constants.DEFAULT_SERVER_PORT);
        client.setProtocol(new BinaryProtocol());
        try {
            client.initSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return client;
    }

    private void StartServer() throws InterruptedException {
        Server.main(null);
        Thread.sleep(2000);
    }

    private MessengerClient loginSuccess(String name, String password) throws InterruptedException {
        StartServer();
        MessengerClient client = StartClient();
        try {
            client.processInput(String.format("/login %s %s", name, password));
        } catch (ProtocolException | IOException e) {
            System.out.println("Failed to process user input: " + e.getMessage());
        }
        Thread.sleep(1500);
        return client;
    }

}
