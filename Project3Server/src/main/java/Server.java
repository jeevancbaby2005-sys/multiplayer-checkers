
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class Server {

	int count = 1;
	int game = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	HashMap<String, ClientThread> users = new HashMap<>();List<String> players = Collections.synchronizedList(new ArrayList<>());
	TheServer server;
	private Consumer<Serializable> callback;

	Server(Consumer<Serializable> call) {
		callback = call;
		server = new TheServer();
		server.start();
	}

	public class TheServer extends Thread {
		public void run() {
			try (ServerSocket mysocket = new ServerSocket(5555);) {
				System.out.println("Server is waiting for a client!");
				while (true) {
					ClientThread c = new ClientThread(mysocket.accept(), count);
					clients.add(c);
					c.start();
					count++;
				}
			} catch (Exception e) {
				callback.accept("Server socket did not launch");
			}
		}
	}

	class ClientThread extends Thread {

		Socket connection;
		String user;
		int count;
		boolean firstSide;
		int gameNum;
		ObjectInputStream in;
		ObjectOutputStream out;
		boolean inGame = false;
		String rematch="";
		ClientThread opponent;
		char[][] board=new char[8][8];
		ArrayList<String> nextMove = new ArrayList<>();
		boolean goodMove=false;

		public void jump(int r1, int c1,int r2,int c2) {
			if (Math.abs(r2 - r1) == 2) {
				int midR = (r1 + r2)/2;
				int midC = (c1 + c2)/2;
				board[midR][midC] = 'o';
			}
		}
		public void promote(int r, int c) {
			if (board[r][c] == 'b' && r == 7) {
				board[r][c] = 'B';
			}
			if (board[r][c] == 'r' && r == 0) {
				board[r][c] = 'R';
			}

		}
		public boolean lock(){
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if(board[row][col]=='r'||board[row][col]=='R'){
						ArrayList<String> ju=new ArrayList<>();
						if(multiple(row,col,ju)) {
							return true;
						}
						ju.clear();
					}

				}
			}
			return false;
		}
		public void nextJump(){
			nextMove.clear();
			goodMove=false;
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if(board[row][col]=='r'|| board[row][col]=='R'){
						if(goodMove){
							return;
						}
						multiple(row,col,nextMove);
					}

				}
			}
		}
		public void nextNormal(){
			goodMove=false;
			nextMove.clear();
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if(board[row][col]=='r'||board[row][col]=='R'){
						if(goodMove){
							return;
						}
						rest(row,col,nextMove);
					}

				}
			}
		}
		public void rest(int r,int c, ArrayList<String> res){

			ArrayList<String> re=new ArrayList<>();

			if((r-1)>=0 && (c+1)<8){
				if(board[r-1][c+1]=='o'){
					res.add(r+""+ c +" "+ (r-1)+""+(c+1));
					re.add((r-1)+""+(c+1));
				}
			}
			if((r-1)>=0 && (c-1)>=0){
				if(board[r-1][c-1]=='o'){
					res.add(r+ ""+c +" "+ (r-1)+""+(c-1));
					re.add((r-1)+""+(c-1));
				}
			}
			if(board[r][c]=='R'){
				if((r+1)<8 && (c+1)<8){
					if(board[r+1][c+1]=='o'){
						res.add(r+""+ c +" "+(r+1)+""+(c+1));
						re.add((r+1)+""+(c+1));
					}
				}
				if((r+1)<8 && (c-1)>=0){
					if(board[r+1][c-1]=='o'){
						res.add(r+""+ c +" "+(r+1)+""+(c-1));
						re.add((r+1)+""+(c-1));

					}
				}
			}
			for(int i=0;i<re.size();i++){
				String n=re.get(i);
				int ro1=n.charAt(0)-'0';
				int co1=n.charAt(1)-'0';
				if(ro1==0 && board[r][c]!='R'){
					res.add(0,r+""+ c +" "+(ro1)+""+(co1));
					goodMove=true;
					re.clear();
					return;
				}

				if((ro1-1)>=0 && (co1-1)>=0 && (ro1+1)<8 && (co1+1)<8){
					if(board[r][c]=='R') {
						if(board[ro1+1][co1-1]=='b'){
							if(board[ro1+1][co1+1]!='B'&& Character.toLowerCase(board[ro1-1][co1-1])!='b'){
								res.add(0,r+""+ c +" "+(ro1)+""+(co1));
								goodMove=true;
								re.clear();
								return;
							}
						}
						if(board[ro1+1][co1-1]=='B'){
							if(board[ro1+1][co1+1]!='B'&& Character.toLowerCase(board[ro1-1][co1-1])!='b' && Character.toLowerCase(board[ro1-1][co1+1])=='r'&& (co1+1)!=c){
								res.add(0,r+""+ c +" "+(ro1)+""+(co1));
								goodMove=true;
								re.clear();
								return;
							}
						}
						if(board[ro1+1][co1+1]=='b'){
							if(board[ro1+1][co1-1]!='B'&& Character.toLowerCase(board[ro1-1][co1+1])!='b'){
								res.add(0,r+ ""+c +" "+(ro1)+""+(co1));
								goodMove=true;
								re.clear();
								return;
							}
						}
						if(board[ro1+1][co1+1]=='B'){
							if(board[ro1+1][co1-1]!='B'&& Character.toLowerCase(board[ro1-1][co1+1])!='b' && Character.toLowerCase(board[ro1-1][co1-1])=='r'&& (co1-1)!=c ){
								res.add(0,r+""+ c +" "+(ro1)+""+(co1));
								goodMove=true;
								re.clear();
								return;
							}
						}

					}
					if(board[ro1-1][co1-1]=='b' || board[ro1-1][co1-1]=='B'){
						if(Character.toLowerCase(board[ro1-1][co1+1])!='b'&& Character.toLowerCase(board[ro1+1][co1+1])=='r'&& (co1+1)!=c){
							res.add(0,r+ ""+c +" "+(ro1)+""+(co1));
							goodMove=true;
							re.clear();
							return;
						}
					}
					if(board[ro1-1][co1+1]=='b' || board[ro1-1][co1+1]=='B'){
						if(Character.toLowerCase(board[ro1-1][co1-1])!='b'&& Character.toLowerCase(board[ro1+1][co1-1])=='r' && (co1-1)!=c){
							res.add(0,r+""+ c +" "+(ro1)+""+(co1));
							goodMove=true;
							re.clear();
							return;
						}
					}

					if(Character.toLowerCase(board[ro1-1][co1-1])!='b' && Character.toLowerCase(board[ro1-1][co1+1])!='b'){
						if(board[ro1+1][co1-1]!='B'&& board[ro1+1][co1+1]!='B'){
							res.add(0,r+ ""+c +" "+(ro1)+""+(co1));
						}
					}

				}
				if(co1==0 ||co1==7){
					res.add(0,r+""+ c +" "+(ro1)+""+(co1));
				}
				if(badMove()){
					char temp=board[r][c];
					board[ro1][co1]=temp;
					board[r][c]='o';
					if(!badMove()){
						res.add(0,r+ ""+c +" "+(ro1)+""+(co1));
						board[ro1][co1]='o';
						board[r][c]=temp;
						goodMove=true;
						re.clear();
						return;
					}
					board[ro1][co1]='o';
					board[r][c]=temp;
				}
			}
			re.clear();

		}

		public boolean badMove(){
			for(int r=0;r<8;r++){
				for(int c=0;c<8;c++){
					if(Character.toLowerCase(board[r][c])=='b'){
						if((r+2)<8 && (c-2)>=0 && (c+2)<8){
							if(Character.toLowerCase(board[r+1][c+1])=='r' && board[r+2][c+2]=='o'){
								return true;
							}
							if(Character.toLowerCase(board[r+1][c-1])=='r' && board[r+2][c-2]=='o'){
								return true;
							}
						}
					}
					if(board[r][c]=='B'){
						if((r-2)>=0 && (c-2)>=0 && (c+2)<8){
							if(Character.toLowerCase(board[r-1][c+1])=='r' && board[r-2][c+2]=='o'){
								return true;
							}
							if(Character.toLowerCase(board[r-1][c-1])=='r' && board[r-2][c-2]=='o'){
								return true;
							}
						}
					}

				}
			}
			return false;
		}

		public boolean multiple(int r,int c,ArrayList<String> re){

			char opp='b';
			ArrayList<String> ju1=new ArrayList<>();

			if((r-2)>=0 && (c+2)<8){
				if(Character.toLowerCase(board[r-1][c+1])==opp){
					if(board[r-2][c+2]=='o'){
						re.add((r)+""+(c)+" "+(r-2)+""+(c+2));
						ju1.add((r-2)+""+(c+2));
					}
				}
			}

			if((r-2)>=0 && (c-2)>=0){
				if(Character.toLowerCase(board[r-1][c-1])==opp){
					if(board[r-2][c-2]=='o'){
						re.add((r)+""+(c)+" "+(r-2)+""+(c-2));
						ju1.add((r-2)+""+(c-2));
					}
				}
			}
			if(Character.isUpperCase(board[r][c])){
				if((r+2)<8 && (c+2)<8){
					if(Character.toLowerCase(board[r+1][c+1])==opp){
						if(board[r+2][c+2]=='o'){
							re.add((r)+""+(c)+" "+(r+2)+""+(c+2));

							ju1.add((r+2)+""+(c+2));
						}
					}
				}
				if((r+2)<8 && (c-2)>=0){
					if(Character.toLowerCase(board[r+1][c-1])==opp){
						if(board[r+2][c-2]=='o'){
							re.add((r)+""+(c)+" "+(r+2)+""+(c-2));
							ju1.add((r+2)+""+(c-2));
						}
					}
				}

			}
			if(re.size()==0){
				return false;
			}
			char currentPiece = board[r][c];

			for(int i=0;i<ju1.size();i++){
				ArrayList<String> ju=new ArrayList<>();
				String n=ju1.get(i);
				int ro1=n.charAt(0)-'0';
				int co1=n.charAt(1)-'0';
				int midR = (r + ro1) / 2;
				int midC = (c + co1) / 2;
				char capturedPiece = board[midR][midC];
				board[midR][midC] = 'o';

				board[ro1][co1] = currentPiece;
				board[r][c] = 'o';
				if (board[ro1][co1] == 'r' && ro1 == 0) board[ro1][co1] = 'R';
				if (board[ro1][co1] == 'b' && ro1 == 7) board[ro1][co1] = 'B';
				if(multiple(ro1,co1,ju)){
					re.add(0,(r)+""+(c)+" "+(ro1)+""+(co1));
					goodMove=true;
				}
				board[r][c] = currentPiece;
				board[ro1][co1] = 'o';
				board[midR][midC] = capturedPiece;
			}
			ju1.clear();
			return true;
		}

		public void setBoard() {
			for (int row = 0; row < 8; row++) {
				for (int col = 0; col < 8; col++) {
					if ((row + col) % 2 == 0) {
						board[row][col]='w';
					}else{
						if(row<3){
							board[row][col]='b';
						}else if (row>4){
							board[row][col] = 'r';
						}else{
							board[row][col] = 'o';
						}
					}
				}
			}
		}

		ClientThread(Socket s, int count) {
			this.connection = s;
			this.count = count;
			this.user = "";
			setBoard();
		}


		public void next(Message mov) {
			int r1= 7-(mov.getContent().charAt(0) - '0');
			int c1= 7-(mov.getContent().charAt(1) - '0');
			int r2= 7-( mov.getContent().charAt(3) - '0');
			int c2= 7-(mov.getContent().charAt(4) - '0');

			if(mov.getType().equals("jump")){
				board[r2][c2]=board[r1][c1];
				board[r1][c1]='o';
				promote(r2,c2);
				jump(r1,c1,r2,c2);
				goodMove=false;

			}else {

				board[r2][c2]=board[r1][c1];
				board[r1][c1]='o';
				promote(r2,c2);
				jump(r1,c1,r2,c2);

				try {
					if(lock()){
						nextJump();
						String j=nextMove.get(0);
						int ro1= j.charAt(0) - '0';
						int co1= j.charAt(1) - '0';
						int ro2= j.charAt(3) - '0';
						int co2= j.charAt(4) - '0';
						ArrayList<String> nextCapture=new ArrayList<>();
						goodMove=false;
						while(multiple(ro2,co2,nextCapture)){
							String h=nextCapture.get(0);
							int rw= h.charAt(3) - '0';
							int cl= h.charAt(4) - '0';
							Thread.sleep(500);
							out.writeObject(new Message("computer",user, ro1+""+co1+" "+ro2+""+co2,  "jump", null));
							board[ro1][co1]='o';
							promote(ro2,co2);
							jump(ro1,co1,ro2,co2);
							board[ro2][co2]=board[ro1][co1];
							nextCapture.clear();
							ro1=ro2;
							co1=co2;
							ro2=rw;
							co2=cl;
							goodMove=false;
						}
						Thread.sleep(1000);
						out.writeObject(new Message("computer",user, ro1+""+co1+" "+ro2+""+co2,  "move", null));
						board[ro2][co2]=board[ro1][co1];
						board[ro1][co1]='o';
						promote(ro2,co2);
						jump(ro1,co1,ro2,co2);


					}else{
						nextNormal();
						String j=nextMove.get(0);
						int ro1= j.charAt(0) - '0';
						int co1= j.charAt(1) - '0';
						int ro2= j.charAt(3) - '0';
						int co2= j.charAt(4) - '0';
						Thread.sleep(2000);
						out.writeObject(new Message("computer", user, j, "move", null));
							board[ro2][co2] = board[ro1][co1];
							board[ro1][co1] = 'o';
							promote(ro2, co2);
					}
				} catch (Exception e) {
					System.out.println("Something happened");
				}
			}
		}

		public void updateClients(String message) {
			for (int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);
				if (!users.values().contains(t)) {
					try {
						t.out.writeObject(new Message("server", "all", message, "notice", new ArrayList<>(users.keySet())));
					} catch (Exception e) {
					}
				}
			}
		}

		public void run() {
			try {
				out = new ObjectOutputStream(connection.getOutputStream());
				in = new ObjectInputStream(connection.getInputStream());
				connection.setTcpNoDelay(true);

				out.writeObject(new Message("server", "", "", "notice", new ArrayList<>(users.keySet())));
				Message userName = (Message) in.readObject();
				this.user = userName.getContent();
				users.put(user, this);
				callback.accept("new player on server: " + this.user);
				updateClients("new player");

			} catch (Exception e) {
				System.out.println("Streams not open");
				return;
			}

			while (true) {
				try {
					Message data = (Message) in.readObject();

					if (!(this.inGame)) {

						if (data.getContent().equals("ai")) {
							callback.accept("player: " + this.user + " is playing with AI on game: " + game);
							gameNum=game;
							game++;

							this.inGame = true;
							this.opponent=null;
							this.out.writeObject(new Message("computer", user, "1", "side", null));
							setBoard();

						} else if (data.getContent().equals("player")) {
							synchronized (players) {
								callback.accept("player: " + this.user + " is waiting for players");

								if (players.size() > 0) {
									String opponentName = players.remove(0);
									ClientThread opponent = users.get(opponentName);

									callback.accept("Player: " + this.user + " is playing with " + opponentName + " on game: " + game);
									gameNum=game;


									this.out.writeObject(new Message(opponentName, this.user, "2", "side", null));
									opponent.out.writeObject(new Message(this.user, opponentName, "1", "side", null));

									this.inGame = true;
									opponent.inGame = true;

									this.opponent = opponent;
									opponent.opponent = this;
									opponent.gameNum=game;
									firstSide=false;
									opponent.firstSide=true;

									synchronized (players) {
										players.remove(this.user);
										players.remove(opponent.user);
									}
									game++;

								} else {
									players.add(this.user);
								}
							}
						} else if (data.getContent().equals("back")) {
							synchronized (players) {
								callback.accept("player: " + this.user + " done waiting for players");
								players.remove(this.user);
							}
						}
					}

					else {
						if (data.getType().equals("move") || data.getType().equals("text")||data.getType().equals("jump")) {

							if (opponent != null) {
								opponent.out.writeObject(data);
							}else{
								next(data);
							}
						}else if(data.getType().equals("end")){
							if(opponent!=null){
								if(data.getContent().equals("draw")){
									callback.accept(user+" ended on draw with "+opponent.user+ " on game: "+gameNum);
								}else if(data.getContent().equals(user)){
									callback.accept(user+" won against "+opponent.user+" on game: "+gameNum);
								}else if(data.getContent().equals(opponent.user)){
									callback.accept(opponent.user+" won against "+user+" on game: "+gameNum);
								}
								out.writeObject(new Message("server",user,data.getContent(),"end",null));
								opponent.out.writeObject(new Message("server",user,data.getContent(),"end",null));
							}else{
								if(data.getContent().equals(user)){
									callback.accept(user+" won against AI on game: "+gameNum);
								}else{
									callback.accept("AI won against "+user+" on game: "+gameNum);
								}
								out.writeObject(new Message("server",user,data.getContent(),"end",null));
							}


						}else{
							if(opponent!=null){

								if(data.getContent().equals("again")){
									rematch="again";
									if(opponent.rematch.equals("again")){
										callback.accept("Player: " + this.user + " is  on rematch with " + opponent.user + " on game: " + game);
										gameNum=game;
										opponent.gameNum=game;


										if(firstSide){
											this.out.writeObject(new Message(opponent.user, this.user, "2", "side", null));
											opponent.out.writeObject(new Message(this.user, opponent.user, "1", "side", null));
											firstSide=false;
											opponent.firstSide=true;
										}else{
											this.out.writeObject(new Message(opponent.user, this.user, "1", "side", null));
											opponent.out.writeObject(new Message(this.user, opponent.user, "2", "side", null));
											firstSide=true;
											opponent.firstSide=false;
										}

										this.inGame = true;
										opponent.inGame = true;
										rematch="";
										opponent.rematch="";
										game++;
									}
								}
								if(data.getContent().equals("home")){

									if(opponent.rematch.equals("again")){
										opponent.out.writeObject(new Message("server",opponent.user,"home","close",null));
										this.inGame=false;
										opponent.inGame=false;
										rematch="";
										opponent.rematch="";
									}
								}

							}else{
								if(data.getContent().equals("again")){
									setBoard();
									callback.accept(user+" is playing a rematch with AI on game: "+game);
									gameNum=game;
									game++;
									this.inGame = true;
									this.opponent=null;
									this.out.writeObject(new Message("computer", user, "1", "side", null));
								}else if(data.getContent().equals("home")){
									this.inGame=false;
								}
							}

						}
					}

				} catch (Exception e) {
					callback.accept("Something wrong with player: " + this.user + "...closing down!");
					if (inGame) {
						callback.accept("Game: " + gameNum + " ended abruptly");
					}
					clients.remove(this);
					users.remove(this.user);
					synchronized (players) {
						players.remove(this.user);
					}

					if (opponent != null) {
						opponent.inGame = false;
						opponent.opponent = null;
						try{
							opponent.out.writeObject(new Message("server", opponent.user, "home","close",null));
						} catch (IOException ex) {

						}

					}

					updateClients("player left");
					break;
				}
			}
		}
	}

}