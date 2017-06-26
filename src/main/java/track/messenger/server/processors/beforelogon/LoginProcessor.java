package track.messenger.server.processors.beforelogon;

import track.messenger.db.models.UserEntity;
import track.messenger.client.entity.User;
import track.messenger.messages.LoginMessage;
import track.messenger.messages.MessageWrapper;
import track.messenger.messages.StatusMessage;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.WriteProxy;
import track.messenger.server.utils.Socket;

import java.util.Map;

/**
 * Created by artem on 23.06.17.
 */
public class LoginProcessor extends ProcessorBeforeLogon {

    @Override
    public void dispatchMessage(MessageWrapper messageWrapper,
                                WriteProxy proxy,
                                Map<Long, Socket> userIdToSocketMap,
                                Map<Long, Socket> socketIdToSocketMap) throws ProtocolException {

        LoginMessage loginMessage = (LoginMessage) messageWrapper.message;
        UserEntity userEntity = userService.getByUserame(loginMessage.getName());
        Socket socket = socketIdToSocketMap.get(messageWrapper.socketId);
        if (null != userEntity) {
            if (userEntity.getPassword().equals(loginMessage.getPass())) {
                User user = new User(userEntity.getUsername(), userEntity.getId(), userEntity.getChatIds());
                socket.userId = user.getUserId();
                userIdToSocketMap.put(user.getUserId(), socket);
                messageWrapper.messageRawData = protocol.encode(new StatusMessage(
                        user.getUserId(),
                        StatusMessage.StatusCode.OK,
                        StatusMessage.Reason.LOGIN,
                        user.getEncodedData()));
            } else {
                messageWrapper.messageRawData = protocol.encode(new StatusMessage(
                        new Long(0),
                        StatusMessage.StatusCode.ERROR,
                        StatusMessage.Reason.LOGIN,
                        "Authorization failed"));
            }
        } else {
            messageWrapper.messageRawData = protocol.encode(new StatusMessage(
                    new Long(0),
                    StatusMessage.StatusCode.ERROR,
                    StatusMessage.Reason.LOGIN,
                    "No such username"));
        }
        proxy.enqueue(messageWrapper);
    }
}
