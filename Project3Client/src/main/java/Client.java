import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Client extends Thread {

	Socket socketClient;
	ArrayList<String> users;
	ObjectOutputStream out;
	ObjectInputStream in;
	String user,op;

	private Consumer<ArrayList<String>> list;
	private Consumer<String> active;
	private Consumer<String> side;
	private Consumer<String> move;
	private Consumer<String> jump;
	private Consumer<String> opp;
	private Consumer<String> scene;
	private Consumer<String> end;

	Client(Consumer<ArrayList<String>> userList,Consumer<String> act,Consumer<String> sid,Consumer<String> op, Consumer<String> sce,Consumer<String> mov, Consumer<String> ju,Consumer<String> en) {
		list = userList;
		active=act;
		side=sid;
		users = new ArrayList<>();
		opp=op;
		move=mov;
		scene=sce;
		jump=ju;
		end=en;
	}
	public String convert(String m){
		String clean = m.replace(" ", "");

		int r1 = clean.charAt(0) - '0';
		int c1 = clean.charAt(1) - '0';
		int r2 = clean.charAt(2) - '0';
		int c2 = clean.charAt(3) - '0';

		r1 = 7 - r1;
		c1 = 7 - c1;
		r2 = 7 - r2;
		c2 = 7 - c2;

		return "" + r1 + c1 + " " + r2 + c2;
	}

	public void run() {
		try {
			socketClient = new Socket("127.0.0.1", 5555);
			out = new ObjectOutputStream(socketClient.getOutputStream());
			in = new ObjectInputStream(socketClient.getInputStream());
			socketClient.setTcpNoDelay(true);

			Message intro = (Message) in.readObject();

			if(intro.getUsers() != null) {
				list.accept(intro.getUsers());
				users = intro.getUsers();
			}

		} catch(Exception e) {
			e.printStackTrace();
		}

		while(true) {
			try {
				Message message = (Message) in.readObject();
				if(message.getType().equals("side")){
					side.accept(message.getContent());
					opp.accept(message.getSender());
					op=message.getSender();
					scene.accept(op);

				}else if(message.getType().equals("notice")){
					list.accept(message.getUsers());
				}else if(message.getType().equals("move")||message.getType().equals("jump")){

					String conv=convert(message.getContent());
					active.accept(message.getSender()+ " moved " + conv);
					if(message.getType().equals("jump")){
						jump.accept(conv);
					}else{
						move.accept(conv);
					}

				}else if(message.getType().equals("text")){
					active.accept(message.getSender()+ " sent you '" + message.getContent()+"'");

				}else if(message.getType().equals("end")){
					end.accept(message.getContent());
				}else{
					scene.accept("home");
				}

			} catch(Exception e) {}
		}
	}

	public void setUsername(String username) {
		this.user = username;
		try {
			out.writeObject(new Message(user, "server", user, "notice", null));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void send(String data) {
		try {
			out.writeObject(new Message(user, "server", data, "notice", null));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void text(String data) {
		try {
			String dat=data.replace(" ","");
			if(dat.equals("")){
				return;
			}
			out.writeObject(new Message(user, op, data, "text", null));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void move(String data) {
		try {
			out.writeObject(new Message(user, op, data, "move", null));
		} catch (IOException e) {
			e.printStackTrace();
		}
		active.accept("you moved " + data);
	}
	public void jump(String data) {
		try {
			out.writeObject(new Message(user, op, data, "jump", null));
		} catch (IOException e) {
			e.printStackTrace();
		}
		active.accept("you moved " + data);
	}
	public void end(String data) {
		try {
			out.writeObject(new Message(user, op, data, "end", null));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}