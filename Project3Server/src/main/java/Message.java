import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    static final long serialVersionUID = 42L;
    private String sender, recipient, content, type;
    private ArrayList<String> users;
    public Message(String send,String rec, String cont , String typ,ArrayList<String> use) {
        this.sender=send;
        this.recipient=rec;
        this.content=cont;
        this.type=typ;
        this.users=use;
    }

    public String getType(){ return type; }
    public String getSender(){ return sender; }
    public String getRecipient(){ return recipient; }
    public String getContent() { return content; }
    public ArrayList<String> getUsers(){
        return users;
    }
}
