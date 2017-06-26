package track.messenger.client.input.handlers;

import track.messenger.client.SessionClient;
import track.messenger.client.entity.ChatHistory;
import track.messenger.client.entity.User;
import track.messenger.messages.ChatHistoryMessage;
import track.messenger.messages.Message;

import static java.lang.Math.toIntExact;

/**
 * Created by artem on 21.06.17.
 */
public class ChatHistoryHandle extends InputHandle {
    @Override
    public Message handleInput(String line, SessionClient sessionClient) {
        if (isAuthorized(sessionClient) && (null != line)) {
            String[] tokens = line.split(" ");
            if ((tokens.length > 0) && (tokens.length < 4)) {
                Long chatId;
                try {
                    chatId = Long.parseLong(tokens[0]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    return null;
                }
                User user = sessionClient.getUser();
                if (user.getChatIds().contains(chatId)) {
                    switch (tokens.length) {
                        case 1:
                            return new ChatHistoryMessage(user.getUserId(), toIntExact(chatId));
                        case 2:
                            try {
                                int preferredNumber = Integer.parseInt(tokens[1]);
                                return new ChatHistoryMessage(user.getUserId(), toIntExact(chatId), 0, -1, -1, preferredNumber);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                return null;
                            }
                        case 3:
                            try {
                                int startIndex = Integer.parseInt(tokens[1]);
                                int endIndex = Integer.parseInt(tokens[1]);
                                return new ChatHistoryMessage(user.getUserId(), toIntExact(chatId), 0, startIndex, endIndex, 0);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                return null;
                            }
                    }
                } else {
                    log.error("No chat with ID {} found. Update chats list first", chatId);
                }
            }

            help();
        }
        return null;
    }

    private void help() {
        System.out.println("To update a chat (download last messages etc.) you should enter it in format:\n" +
                "\t/chathist {chatId} - to update new chat OR\n" +
                "\t/chathist {chatId} {download_last_N_messages} - to update chat downloading N messages OR\n" +
                "\t/chathist {chatId} {first_index} {last_index} - to download messages in " +
                "range [first_index, last_index]");
    }
}
