package track.messenger.server.processors.beforelogon;

import track.messenger.db.models.UserEntity;
import track.messenger.messages.CreateUserMessage;
import track.messenger.messages.MessageWrapper;
import track.messenger.messages.StatusMessage;
import track.messenger.net.protocol.ProtocolException;
import track.messenger.server.WriteProxy;
import track.messenger.server.utils.Socket;
import track.messenger.utils.Validator;

import java.util.Map;

/**
 * Created by artem on 23.06.17.
 */
public class CreateUserProcessor extends ProcessorBeforeLogon {

    @Override
    public void dispatchMessage(MessageWrapper messageWrapper,
                                WriteProxy proxy,
                                Map<Long, Socket> userIdToSocketMap,
                                Map<Long, Socket> socketIdToSocketMap) throws ProtocolException {
        CreateUserMessage createUserMessage = (CreateUserMessage) messageWrapper.message;
        StatusMessage answerMessage = checkUsername(createUserMessage.getName());
        if (null == answerMessage) {
            answerMessage = doesPasswordSatisfyRequirements(createUserMessage.getPass());
            if (null == answerMessage) {
                UserEntity userEntity = new UserEntity(createUserMessage.getName(), createUserMessage.getPass());
                if (null != userService.add(userEntity)) {
                    answerMessage = new StatusMessage(
                            0L,
                            StatusMessage.StatusCode.OK,
                            StatusMessage.Reason.NEW_USER,
                            "Success");
                } else {
                    answerMessage = new StatusMessage(
                            0L,
                            StatusMessage.StatusCode.ERROR,
                            StatusMessage.Reason.NEW_USER,
                            "Error during adding to database");
                }
            }
        }

        messageWrapper.messageRawData = protocol.encode(answerMessage);
        proxy.enqueue(messageWrapper);
    }

    private StatusMessage checkUsername(String name) {
        String error = Validator.validateUsername(name);
        //region Additional check into track.messenger.db
        if (null == error) {
            if (null != userService.getByUserame(name)) {
                error = "User with such username is already exists";
            }
        }
        //endregion

        if (null != error) {
            return new StatusMessage(
                    0L,
                    StatusMessage.StatusCode.ERROR,
                    StatusMessage.Reason.NEW_USER,
                    error);
        } else {
            return null;
        }
    }

    private StatusMessage doesPasswordSatisfyRequirements(String password) {
        String error = Validator.validatePassword(password);
        if (null != error) {
            return new StatusMessage(
                    0L,
                    StatusMessage.StatusCode.ERROR,
                    StatusMessage.Reason.NEW_USER,
                    error);
        } else {
            return null;
        }
    }

}
