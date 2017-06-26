package track.messenger.server.processors;

import track.messenger.db.models.ChatEntity;
import track.messenger.db.models.MessageEntity;
import track.messenger.db.models.UserEntity;
import track.messenger.db.services.ChatService;
import track.messenger.db.services.UserService;
import track.messenger.client.entity.ChatMessage;
import track.messenger.messages.*;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.utils.Socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Math.toIntExact;

/**
 * Created by artem on 21.06.17.
 */
public class ChatHistProcessor extends Processor {
    private UserService userService = new UserService();
    private ChatService chatService = new ChatService();

    @Override
    public List<MessageWrapper> processMessage(Message message, Map<Long, Socket> userIdToSocketMap) throws ProtocolException {
        List<Message> messages = new ArrayList<>();
        ChatHistoryMessage chatHistoryMessage = (ChatHistoryMessage) message;
        UserEntity userEntity = userService.get(chatHistoryMessage.getUserId());
        if (null != userEntity) {
            ChatEntity chatEntity = chatService.get(chatHistoryMessage.getChatId());
            if (null != chatEntity) {
                messages.add(handleRequest(chatHistoryMessage, userEntity, chatEntity));
            } else {
                // No such chat
                messages.add(new ChatHistoryResultMessage(
                        userEntity.getId(),
                        chatEntity.getId(),
                        0L,
                        null,
                        null));
            }
        } else {
            messages.add(new StatusMessage(
                    chatHistoryMessage.getUserId(),
                    StatusMessage.StatusCode.ERROR,
                    StatusMessage.Reason.CHAT_HIST,
                    "No such user"));
        }
        return dispatchMessages(messages, userIdToSocketMap);
    }

    private ChatHistoryResultMessage handleRequest(ChatHistoryMessage message, UserEntity user, ChatEntity chat) {
        List<MessageEntity> allMessages = chat.getMessageEntities();
        Long totalMessages = (null == allMessages) ? 0L : allMessages.size();
        ChatHistoryResultMessage resultMessage = new ChatHistoryResultMessage(
                user.getId(),
                chat.getId(),
                totalMessages,
                chat.getUserEntities().stream().collect(Collectors.toMap(UserEntity::getId, UserEntity::getUsername)),
                null);

        if (0 < resultMessage.getTotalMessages()) {
            int startIndex = message.getStartIndex();
            int endIndex = message.getEndIndex();
            if ((0 <= startIndex) && (0 <= endIndex) && (totalMessages >= startIndex) && (totalMessages >= endIndex)) {
                resultMessage.setMessages(getListOfChatMessages(allMessages, startIndex, endIndex));
            } else {
                endIndex = toIntExact(totalMessages);
                startIndex = endIndex - message.getPreferredNumberOfLastMessages();
                startIndex = (endIndex == startIndex) ?
                        (endIndex - ChatHistoryMessage.DEFAULT_NUMBER_OF_LAST_MESSAGES) : startIndex;
                startIndex = (0 > startIndex) ? 0 : startIndex;
                resultMessage.setMessages(getListOfChatMessages(allMessages, startIndex, endIndex));
            }
        }
        return resultMessage;
    }

    List<ChatMessage> getListOfChatMessages(List<MessageEntity> allMessages, int startIndex, int endIndex) {
        if (startIndex == endIndex) {
            return new ArrayList<>();
        } else {
            AtomicInteger index = new AtomicInteger(startIndex);
            return allMessages.subList(startIndex, endIndex)
                    .stream().map(m -> new ChatMessage(
                            m.getDatetime(),
                            m.getUserEntity().getId(),
                            index.getAndIncrement(),
                            m.getMessage())).collect(Collectors.toList());
        }
    }
}
