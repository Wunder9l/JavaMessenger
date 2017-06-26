package track.messenger.client.message.exceptions;

/**
 * Created by artem on 20.06.17.
 */
public class InvalidUserId extends Exception {
    // Parameterless Constructor
    public InvalidUserId() {}

    // Constructor that accepts a message
    public InvalidUserId(String message)
    {
        super(message);
    }
}
