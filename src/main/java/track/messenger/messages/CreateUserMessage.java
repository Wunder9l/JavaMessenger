package track.messenger.messages;

/**
 * Created by artem on 22.06.17.
 */
public class CreateUserMessage extends Message {
    private String name;
    private String pass;

    public CreateUserMessage(String name, String pass) {
        type = Type.MSG_USER_CREATE;
        this.name = name;
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "CreateUserMessage{" +
                "name='" + name + '\'' +
                ", pass='" + pass + '\'' +
                '}';
    }
}
