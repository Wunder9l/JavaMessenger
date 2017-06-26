package track.messenger.server.processors;

import track.messenger.db.models.ChatEntity;
import track.messenger.db.models.MessageEntity;
import track.messenger.db.models.UserEntity;
import track.messenger.client.entity.ChatMessage;
import track.messenger.messages.*;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.utils.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by artem on 18.04.17.
 */
public class TextProcessor extends Processor {

    @Override
    public List<MessageWrapper> processMessage(Message message, Map<Long, Socket> userIdToSocketMap)
            throws ProtocolException {
        TextMessage inputMessage = (TextMessage) message;
        List<MessageWrapper> outMessages = new ArrayList<>();
        ChatEntity chatEntity = chatService.get(inputMessage.getChatId());
        if (null != chatEntity) {
            UserEntity userEntity = userService.get(inputMessage.getUserId());
            if (null != userEntity) {
                MessageEntity newMessage = new MessageEntity(userEntity, chatEntity, inputMessage.getText());
                MessageEntity dbMessage = messageService.add(newMessage);
                if (null != dbMessage) {
                    outMessages.addAll(generateUpdateMessages(
                            userEntity.getId(),
                            chatEntity,
                            dbMessage,
                            userIdToSocketMap));
                    outMessages.add(0, successMessage(userEntity.getId(), userIdToSocketMap));
                } else {
                    outMessages.add(errorMessage(
                            userEntity.getId(),
                            "Adding message failed",
                            userIdToSocketMap));
                }
            } else {
                outMessages.add(errorMessage(
                        inputMessage.getUserId(),
                        "Not valid userId",
                        userIdToSocketMap));
            }
        } else {
            outMessages.add(errorMessage(
                    inputMessage.getUserId(),
                    "Not valid chatId",
                    userIdToSocketMap));
        }
        return outMessages;
    }

    private List<MessageWrapper> generateUpdateMessages(
            Long authorId,
            ChatEntity chatEntity,
            MessageEntity messageEntity,
            Map<Long, Socket> userIdToSocketMap) throws ProtocolException {

        byte[] encodedUpdateMessage = protocol.encode(updateMessage(authorId, chatEntity, messageEntity));
        List<MessageWrapper> resultList = new ArrayList<>();

        for (UserEntity user : chatEntity.getUserEntities()) {
            if (userIdToSocketMap.containsKey(user.getId())) {
                Socket socket = userIdToSocketMap.get(user.getId());
                resultList.add(new MessageWrapper(
                        socket.socketId,
                        user.getId(),
                        encodedUpdateMessage));
            }
        }
        return resultList;
    }

    ChatHistoryResultMessage updateMessage(Long authorId,
                                           ChatEntity chatEntity,
                                           MessageEntity messageEntity) {
        Map<Long, String> members = chatEntity.getUserEntities().stream()
                .collect(Collectors.toMap(UserEntity::getId, UserEntity::getUsername));
        int index = chatEntity.getMessageEntities().size();
        Long totalMessages = (long) index + 1;
        List<ChatMessage> messageList = new ArrayList<ChatMessage>() {
            {
                add(new ChatMessage(
                        messageEntity.getDatetime(),
                        authorId,
                        index,
                        messageEntity.getMessage()));
            }
        };
        return new ChatHistoryResultMessage(
                0L,
                chatEntity.getId(),
                totalMessages,
                members,
                messageList);
    }

    private MessageWrapper successMessage(Long userId, Map<Long, Socket> userIdToSocketMap) throws ProtocolException {
        Socket socket = userIdToSocketMap.get(userId);
        StatusMessage statusMessage = new StatusMessage(
                userId,
                StatusMessage.StatusCode.OK,
                StatusMessage.Reason.TEXT,
                "Success");
        return new MessageWrapper(
                socket.socketId,
                userId,
                protocol.encode(statusMessage));
    }

    private MessageWrapper errorMessage(Long userId, String errorText, Map<Long, Socket> userIdToSocketMap)
            throws ProtocolException {
        Socket socket = userIdToSocketMap.get(userId);
        StatusMessage statusMessage = new StatusMessage(
                userId,
                StatusMessage.StatusCode.ERROR,
                StatusMessage.Reason.TEXT,
                errorText);
        return new MessageWrapper(
                socket.socketId,
                userId,
                protocol.encode(statusMessage));
    }
}
