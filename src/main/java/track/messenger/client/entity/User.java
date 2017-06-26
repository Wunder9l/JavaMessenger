package track.messenger.client.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by artem on 14.06.17.
 */
public class User {

    private String username;
    private Long userId;
    private List<Long> chatIds;
    private Map<Long, ChatHistory> chats = new HashMap<>();
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public User(Long userId, String messageToParse) throws InvalidPropertiesFormatException {
        this.userId = userId;
        parseToGetUsernameAndChatsIds(messageToParse);
    }

    public User(String username, Long userId, List<Long> chatIds) {
        this.username = username;
        this.userId = userId;
        this.chatIds = chatIds;
    }

    public String getUsername() {
        return username;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEncodedData() {
        String chatsString = chatIds.stream().
                map((s) -> s.toString()).collect(Collectors.joining(","));
        return String.format("%s,%s", username, chatsString);
    }

    private void parseToGetUsernameAndChatsIds(String messageToParse)
            throws InvalidPropertiesFormatException {
        Scanner scanner = new Scanner(messageToParse).useDelimiter(",");
        try {
            username = scanner.next();
        } catch (Exception e) {
            scanner.close();
            throw new InvalidPropertiesFormatException(e.getMessage());
        }

        this.chatIds = new ArrayList<>();
        try {
            while (true) {
                Long id = scanner.nextLong();
                chatIds.add(id);
            }
        } catch (InputMismatchException e) {
            throw new InvalidPropertiesFormatException(e.getMessage());
        } catch (NoSuchElementException e) {
        } finally {
            scanner.close();
        }
    }

    public List<Long> getChatIds() {
        return chatIds;
    }

    public ChatHistory getChatHistory(Long chatId) {
        if (chatIds.contains(chatId)) {
            if (!chats.containsKey(chatId)) {
                ChatHistory newChatHistory = new ChatHistory(chatId);
                chats.put(chatId, newChatHistory);
                log.info("New ChatHistory with id {} was added", chatId);
            }
            return chats.get(chatId);
        }
        return null;
    }

    public void setChatIds(List<Long> chatIds) {
        this.chatIds = chatIds;
    }

    public void showChats() {
        if ((null != chatIds) && (chatIds.size() > 0)) {
            for (Long chatId :
                    chatIds) {
                System.out.println(String.format("Chat id: %d, chat name: %s", chatId));
                if (chats.containsKey(chatId)) {
                    System.out.println(chats.get(chatId).toString());
                }
            }
        } else {
            System.out.println("No chats available");
        }
    }
}
