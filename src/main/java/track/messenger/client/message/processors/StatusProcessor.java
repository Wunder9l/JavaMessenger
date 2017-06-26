package track.messenger.client.message.processors;

import track.messenger.client.SessionClient;
import track.messenger.client.entity.User;
import track.messenger.messages.Message;
import track.messenger.messages.StatusMessage;

import java.util.InvalidPropertiesFormatException;

/**
 * Created by artem on 14.06.17.
 */
public class StatusProcessor extends Processor {
    @Override
    public void processMessage(Message message, SessionClient session) {
        if (message instanceof StatusMessage) {
            StatusMessage statusMessage = (StatusMessage) message;
            if (statusMessage.getStatusCode().equals(StatusMessage.StatusCode.OK)) {
                switch (statusMessage.getReason()) {
                    case LOGIN:
                        loginResponse(statusMessage, session);
                        break;
                    case TEXT:
                        textResponse(statusMessage, session);
                        break;
                    case NEW_USER:
                        newUserResponse(statusMessage, session);
                        break;
                    case NEW_CHAT:
                        newChatResponse(statusMessage, session);
                        break;
                    case ADD_USER_TO_CHAT:
                        addUsersToChatResponse(statusMessage, session);
                        break;
                    case LEAVE_CHAT:
                        leaveChatResponse(statusMessage, session);
                        break;
                    default:
                        log.error("Unknown Reason: " + statusMessage.getReason().toString());
                        break;
                }
            } else {
                if (statusMessage.getStatusCode().equals(StatusMessage.StatusCode.ERROR)) {
                    log.error(statusMessage.toString());
                } else {
                    log.error("Unknown status code: " + statusMessage.getStatusCode().toString());
                }
            }
        }
    }

    private void leaveChatResponse(StatusMessage statusMessage, SessionClient session) {
        log.info("Chat was left");
    }

    private void addUsersToChatResponse(StatusMessage statusMessage, SessionClient session) {
        log.info("Users were added");
    }

    private void newChatResponse(StatusMessage statusMessage, SessionClient session) {
        log.info("Chat successfully created\nUpdating chat lists...");
        session.processCommand("/chatslist");
    }

    private void newUserResponse(StatusMessage statusMessage, SessionClient session) {
        log.info("Account successfully created");
    }

    private void textResponse(StatusMessage statusMessage, SessionClient session) {
        log.info("Message successfully sent");
    }

    private void loginResponse(StatusMessage statusMessage, SessionClient session) {
        if (statusMessage.getStatusCode() == StatusMessage.StatusCode.OK) {
            try {
                User user = new User(statusMessage.getUserId(), statusMessage.getMessage());
                session.setUser(user);
            } catch (InvalidPropertiesFormatException e) {
                log.error(e.getMessage());
            }
        }
    }
}
