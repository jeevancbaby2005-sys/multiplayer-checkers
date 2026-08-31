import java.util.ArrayList;
import java.util.HashMap;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class GuiClient extends Application {

	Label main, name,prompt,wait;
	Label feedback=new Label();
	Label feedback1=new Label();
	StackPane circ,circ1;
	Stage primaryStage;
	String user,opp="";
	TextField username;
	Button ai,game,back;
	HashMap<String, Scene> sceneMap;
	VBox clientBox,clientBox1,clientBox3,clientBox4,clientBox5,clientBox6;
	Client clientConnection;
	ListView<String> listItems2;
	ListView<String> listItems3;
	GridPane grid,grid1;
	char[][] board = new char[8][8];
	String side="1";



	public boolean isUnique(String name, ObservableList<String> users) {
		if (name.trim().isEmpty()) {
			return false;
		}
		for (int i = 0; i < users.size(); i++) {
			if(users.get(i).equals(name)) return false;
		}
		if(name.equals("all") || name.equals("computer")||name.equals("draw")||name.equals("you")) return false;
		return true;
	}

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		listItems2 = new ListView<>();
		listItems3 = new ListView<>();

		clientConnection = new Client(data -> {
			Platform.runLater(() -> {
				listItems2.getItems().clear();
				for(int i = 0; i < data.size(); i++) {
					listItems2.getItems().add(data.get(i));
					listItems3.scrollTo(0);}
			});
		}, data -> {
			Platform.runLater(() -> {
				listItems3.getItems().add(0,data);});
		},data -> {
			Platform.runLater(() -> {
				side=data;});
		},data -> {
			Platform.runLater(() -> {
				opp=data;});
		},data -> {
			Platform.runLater(() -> {
				if(data.equals("computer")){
					sceneMap.put("computer",createClientGui4());
					primaryStage.setScene(sceneMap.get("computer"));
				}else if(data.equals("home")){
					primaryStage.setScene(createClientGui7());
					PauseTransition pause = new PauseTransition(Duration.seconds(1));
					pause.setOnFinished(event -> {
						primaryStage.setScene(sceneMap.get("home"));
					});
					pause.play();
				}else{
					sceneMap.put("opp",createClientGui5());
					primaryStage.setScene(sceneMap.get("opp"));
				};});
		},data -> {
			Platform.runLater(() -> {
				move2(data);});
		},data -> {
			Platform.runLater(() -> {
				move3(data);});
		},data -> {
			Platform.runLater(() -> {
				PauseTransition pause = new PauseTransition(Duration.seconds(1));
				pause.setOnFinished(event -> {
					sceneMap.put("end",createClientGui6(data));
					primaryStage.setScene(sceneMap.get("end"));
				});
				pause.play();
			});
		}
		);

		clientConnection.start();



		sceneMap = new HashMap<>();
		sceneMap.put("client", createClientGui1());
		sceneMap.put("home",createClientGui2());
		sceneMap.put("wait",createClientGui3());


		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});

		primaryStage.setScene(sceneMap.get("client"));

		primaryStage.setTitle("Client");
		primaryStage.show();
	}
	public Scene createClientGui4() {
		listItems3.getItems().clear();
		Label color=new Label();
		color.setText(" You are black piece ");

		color.setStyle("-fx-font: bold;  -fx-font-size: 22px; ");
		Label activity=new Label("  Activity  ");
		activity.setStyle("-fx-background-color: cyan;");
		feedback.setText(" Your move ");
		feedback.setStyle("-fx-background-color: white;  -fx-font-size: 20px;");
		VBox v1=new VBox(20,activity,listItems3,feedback);
		v1.setAlignment(Pos.CENTER);
		Label comp=new Label("  Computer  ");
		comp.setStyle("-fx-background-color: white;  -fx-font-size: 20px;");
		grid= new GridPane();
		setBoard(1);
		drawBoard(grid);
		Label hint=new Label(" type start Row+Col Final Row+Col to move. Ex: 50 41");
		TextField move= new TextField();
		move.setPromptText("Press ENTER to submit");
		move.setOnAction(e->{
			move(move.getText());
			move.setText("");
		});
		clientBox4 =new VBox(20,color,comp,grid,hint,move);
		clientBox4.setAlignment(Pos.CENTER);
		HBox h1=new HBox(10,clientBox4,v1);
		h1.setStyle("-fx-background-color:  TOMATO;");

		return new Scene(h1,600,600);
	}


	public Scene createClientGui5( ) {
		listItems3.getItems().clear();
		Label color=new Label();
		if(side.equals("1")){
			color.setText(" You are black piece ");
			feedback1.setText(" Your move ");
		}else{
			color.setText(" You are red piece ");
			feedback1.setText(opp +"'s move ");
		}
		Label activity=new Label("  Activity  ");
		activity.setStyle("-fx-background-color: cyan;");

		color.setStyle("-fx-font: bold;  -fx-font-size: 22px; ");
		feedback1.setStyle("-fx-background-color: white; -fx-font-size: 20px;");
		Button send=new Button("Send Message");
		send.setStyle("-fx-background-color: deepskyblue;");
		TextField text=new TextField();
		send.setOnAction(e->
				{
					clientConnection.text(text.getText());
					text.setText("");
				}
		);

		VBox v1=new VBox(20,activity,listItems3,feedback1,text,send);
		v1.setAlignment(Pos.CENTER);

		Label comp=new Label(" "+opp+" ");
		comp.setStyle("-fx-background-color: white;  -fx-font-size: 20px;");
		grid1= new GridPane();
		setBoard(Integer.parseInt(side));
		drawBoard(grid1);
		Label hint=new Label(" type start Row+Col Final Row+Col to move. Ex: 50 41");
		TextField move= new TextField();
		move.setPromptText("Press ENTER to submit");
		move.setOnAction(e->{
			move(move.getText());
			move.setText("");
		});
		clientBox5 =new VBox(20,color,comp,grid1,hint,move);
		clientBox5.setAlignment(Pos.CENTER);
		HBox h1=new HBox(10,clientBox5,v1);
		h1.setStyle("-fx-background-color:  TOMATO;");

		return new Scene(h1,600,600);
	}
	public Scene createClientGui6(String winner) {
		Label win;
		if(winner.equals("draw")){
			win=new Label(" Game ended on Draw ");
		}else if(winner.equals(user)){
			win=new Label(" You won the game ");
		}else{
			win=new Label(" "+opp+" won the game ");
		}
		win.setStyle(" -fx-font-style: bold; ; -fx-font-size: 25px;");

		Button again=new Button("Play Again");
		again.setStyle("-fx-background-color: deepskyblue;");
		again.setOnAction(e->{
			win.setText("waiting for opponent's feedback");
			clientConnection.send("again");
		});

		Button home=new Button("Home");
		home.setStyle("-fx-background-color: deepskyblue;");
		home.setOnAction(e->{
			primaryStage.setScene(sceneMap.get("home"));
			clientConnection.send("home");
		});

		clientBox6=new VBox(30,win,again,home);

		clientBox6.setAlignment(Pos.CENTER);

		return new Scene(clientBox6,600,600);
	}

	public Scene createClientGui1() {
		prompt=new Label("  Enter a unique username (press ENTER to submit)  ");
		prompt.setStyle("-fx-background-color: white;"+" -fx-font-size: 22px;");
		username = new TextField();
		username.setPromptText("Type Here");

		username.setOnAction(e -> {
			ObservableList<String> items = listItems2.getItems();
			if(isUnique(username.getText(), items)) {
				user = username.getText();
				clientConnection.setUsername(user);
				primaryStage.setScene(sceneMap.get("home"));
			} else {
				prompt.setText(" Invalid or duplicate username, type another one ");
				username.clear();
			}
		});
		circ=piece(30,"black","darkgrey");

		main = new Label("  Welcome to checkers game  ");
		main.setStyle("-fx-background-color: white;"+" -fx-font-size: 25px;");




		clientBox = new VBox(60, circ, main, prompt, username);
		clientBox.setStyle("-fx-background-color: tomato; -fx-font-family: 'serif';");
		clientBox.setAlignment(Pos.CENTER);
		return new Scene(clientBox, 600, 600);
	}
	public Scene createClientGui2() {
		name=new Label(user);

		circ1=piece(30,"black","darkgrey");

		ai=new Button("Play with AI");
		ai.setStyle("-fx-background-color: deepskyblue;");

		game=new Button("Play with others");
		game.setStyle("-fx-background-color: deepskyblue;");

		ai.setOnAction(e->{
			clientConnection.send("ai");
			primaryStage.setScene(sceneMap.get("ai"));
		});
		game.setOnAction(e->{
			primaryStage.setScene(sceneMap.get("wait"));
			clientConnection.send("player");
		});

		clientBox1 = new VBox(50, circ1, name, ai, game);
		clientBox1.setStyle("-fx-background-color: tomato; -fx-font-family: 'serif';");
		clientBox1.setAlignment(Pos.CENTER);
		return new Scene(clientBox1, 600, 600);
	}

	public Scene createClientGui3() {
		wait=new Label(" Waiting for players....... ");
		back=new Button("Go back");
		back.setStyle("-fx-background-color: deepskyblue;");
		back.setOnAction(e->{
			primaryStage.setScene(sceneMap.get("home"));
			clientConnection.send("back");
		});
		clientBox3=new VBox(30,back,wait);

		clientBox3.setAlignment(Pos.CENTER);

		return new Scene(clientBox3,600,600);
	}
	public Scene createClientGui7() {
		Label left = new Label(" Opponent left ");

		left.setStyle("-fx-background-color: deepskyblue; -fx-font-size: 22px;");

		VBox clientBox8=new VBox(30,left);

		clientBox8.setAlignment(Pos.CENTER);

		return new Scene(clientBox8,600,600);
	}

	public void drawBoard(GridPane grid) {
		grid.getChildren().clear();
		for (int row = 0; row <= 8; row++) {
			for (int col = 0; col <= 8; col++) {

				if (row == 0 && col == 0) continue;
				if (col == 0) {
					Label label = new Label(String.valueOf(row-1));
					label.setStyle("-fx-font-size: 20px;");
					grid.add(label, col, row);
					continue;
				}
				if (row == 0) {
					Label label = new Label("   "+(col-1)+" ");
					label.setStyle("-fx-font-size: 20px;");
					grid.add(label, col, row);
					continue;
				}

				StackPane cell = new StackPane();
				cell.setPrefSize(40, 40);
				if(board[row-1][col-1]=='w'){
					cell.setStyle("-fx-background-color: white;");
				}else if(board[row-1][col-1]=='o'){
					cell.setStyle("-fx-background-color: orange;");
				}else if(board[row-1][col-1]=='b'){
					cell.setStyle("-fx-background-color: orange;");
					cell.getChildren().add(piece(18,"black","darkgrey"));

				}else if(board[row-1][col-1]=='B'){
					cell.setStyle("-fx-background-color: orange;");
					cell.getChildren().add(king(18,"black","darkgrey"));

				}else if(board[row-1][col-1]=='r'){
					cell.setStyle("-fx-background-color: orange;");
					cell.getChildren().add(piece(18,"red","orangered"));

				}else if(board[row-1][col-1]=='R'){
					cell.setStyle("-fx-background-color: orange;");
					cell.getChildren().add(king(18,"red","orangered"));

				}
				grid.add(cell, col, row);
			}
		}
	}


	public void setBoard(int choice) {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {

				if ((row + col) % 2 == 0) {
					board[row][col] = 'w';
				} else {

					if (choice == 1) {
						if (row < 3) {
							board[row][col] = 'r';
						} else if (row > 4) {
							board[row][col] = 'b';
						} else {
							board[row][col] = 'o';
						}

					} else {
						if (row < 3) {
							board[row][col] = 'b';
						} else if (row > 4) {
							board[row][col] = 'r';
						} else {
							board[row][col] = 'o';
						}
					}
				}
			}
		}
	}
	public boolean multiple(int r,int c,ArrayList<String> re){

		ArrayList<String> jum=new ArrayList<>();
		char piece,opp;
		if(side.equals("1")){
			piece='b';
			opp='r';
		}else{
			piece='r';
			opp='b';
		}

		if((r-2)>=0 && (c+2)<8){
			if(Character.toLowerCase(board[r-1][c+1])==opp){
				if(board[r-2][c+2]=='o'){
					re.add((r-2)+""+(c+2));
				}
			}
		}

		if((r-2)>=0 && (c-2)>=0){
			if(Character.toLowerCase(board[r-1][c-1])==opp){
				if(board[r-2][c-2]=='o'){
					re.add((r-2)+""+(c-2));
				}
			}
		}
		if(Character.isUpperCase(board[r][c])){
			if((r+2)<8 && (c+2)<8){
				if(Character.toLowerCase(board[r+1][c+1])==opp){
					if(board[r+2][c+2]=='o'){
						re.add((r+2)+""+(c+2));
					}
				}
			}
			if((r+2)<8 && (c-2)>=0){
				if(Character.toLowerCase(board[r+1][c-1])==opp){
					if(board[r+2][c-2]=='o'){
						re.add((r+2)+""+(c-2));
					}
				}
			}

		}
		if(re.size()==0){
			return false;
		}
		return true;
	}
	public ArrayList<String> rest(int r,int c){
		ArrayList<String> res=new ArrayList<>();

		if((r-1)>=0 && (c+1)<8){
			if(board[r-1][c+1]=='o'){
				res.add((r-1)+""+(c+1));
			}
		}
		if((r-1)>=0 && (c-1)>=0){
			if(board[r-1][c-1]=='o'){
				res.add((r-1)+""+(c-1));
			}
		}
		if(Character.isUpperCase(board[r][c])){
			if((r+1)<8 && (c+1)<8){
				if(board[r+1][c+1]=='o'){
					res.add((r+1)+""+(c+1));
				}
			}
			if((r+1)<8 && (c-1)>=0){
				if(board[r+1][c-1]=='o'){
					res.add((r+1)+""+(c-1));
				}
			}
		}
		return res;

	}
	public void promote(int r, int c) {
		if(side.equals("1")){
			if (board[r][c] == 'b' && r == 0) {
				board[r][c] = 'B';
			}
			if (board[r][c] == 'r' && r == 7) {
				board[r][c] = 'R';
			}
		}else{
			if (board[r][c] == 'b' && r == 7) {
				board[r][c] = 'B';
			}
			if (board[r][c] == 'r' && r == 0) {
				board[r][c] = 'R';
			}
		}

	}
	public void jump(int r1, int c1,int r2,int c2) {
		if (Math.abs(r2 - r1) == 2) {
			int midR = (r1 + r2)/2;
			int midC = (c1 + c2)/2;
			board[midR][midC] = 'o';
		}
	}
	public boolean lock(){
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if(side.equals("1")){
					if(board[row][col]=='b'||board[row][col]=='B'){
						ArrayList<String> ju=new ArrayList<>();
						if(multiple(row,col,ju)){
							return true;
						}
					}
				}else{
					if(board[row][col]=='r'||board[row][col]=='R'){
						ArrayList<String> ju=new ArrayList<>();
						if(multiple(row,col,ju)){
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean invalid(int r1, int c1, int r2,int c2){


		if(r1<0 ||r2<0||c1<0||c2<0||r1>7 ||r2>7||c1>7||c2>7){
			return true;
		}

		if(board[r2][c2]!='o'){
			return true;
		}
		ArrayList<String> r=rest(r1,c1);
		if(!r.contains(r2+""+c2)){
			return true;
		}
		return false;

	}
	public void move(String mov) {
		Label feedbackLabel;
		GridPane gameGrid;

		if(opp.equals("computer")){
			feedbackLabel = feedback;
			gameGrid = grid;
			if(feedback.getText().equals(opp+"'s move ")||feedback.getText().equals(" Not your move ")) {
				feedback.setText(" Not your move ");
				return;
			}
		}else{
			feedbackLabel = feedback1;
			gameGrid = grid1;
			if(feedback1.getText().equals(opp+"'s move ")||feedback1.getText().equals(" Not your move ")){
				feedback1.setText(" Not your move ");
				return;
			}
		}

		if(!mov.matches("\\d{2} \\d{2}")){
			feedbackLabel.setText(" Invalid format ");
			return;
		}
		String mo = mov.replace(" ", "");
		int r1= mo.charAt(0) - '0';
		int c1= mo.charAt(1) - '0';
		int r2= mo.charAt(2) - '0';
		int c2= mo.charAt(3) - '0';

		if(side.equals("1")){
			if(board[r1][c1]=='b'|| board[r1][c1]=='B'){

			}else{
				feedbackLabel.setText(" Not a valid piece ");
				return;
			}
		}else{
			if(board[r1][c1]=='r'|| board[r1][c1]=='R'){

			}else{
				feedbackLabel.setText(" Not a valid piece ");
				return;
			}

		}

		if(lock()){
			feedbackLabel.setText(" Must take a jump ");
			ArrayList<String> capture=new ArrayList<>();

			if(!multiple(r1,c1,capture)){
				feedbackLabel.setText(" Not a valid move ");

			}else{
				if(capture.contains(r2+""+c2)){
					board[r2][c2]=board[r1][c1];
					board[r1][c1]='o';
					promote(r2,c2);
					jump(r1,c1,r2,c2);
					drawBoard(gameGrid);

					ArrayList<String> nextCapture=new ArrayList<>();
					while(multiple(r2,c2,nextCapture)){
						feedbackLabel.setText(" Must take a jump ");
						clientConnection.jump(mov);
						return;
					}
					feedbackLabel.setText(opp+"'s move ");
					clientConnection.move(mov);
				}
			}

		}else{
			if(invalid(r1,c1,r2,c2)){
				feedbackLabel.setText(" Not a valid move ");
				return;
			}
			board[r2][c2]=board[r1][c1];
			board[r1][c1]='o';
			promote(r2,c2);
			feedbackLabel.setText(opp+"'s move ");
			drawBoard(gameGrid);
			clientConnection.move(mov);
		}
		if(side.equals("1")){
			endGame();
		}

	}
	public void move2(String mov) {

		String mo = mov.replace(" ", "");

		int r1= mo.charAt(0) - '0';
		int c1= mo.charAt(1) - '0';
		int r2= mo.charAt(2) - '0';
		int c2= mo.charAt(3) - '0';
		board[r2][c2]=board[r1][c1];
		board[r1][c1]='o';
		promote(r2,c2);
		jump(r1,c1,r2,c2);
		if(opp.equals("computer")){
			drawBoard(grid);
			feedback.setText(" Your Move ");

			if(lock()){
				feedback.setText(" Must take a jump ");
			}


		}else{
			drawBoard(grid1);
			feedback1.setText(" Your Move ");

			if(lock()){
				feedback1.setText(" Must take a jump ");
			}
		}
		if(side.equals("1")){
			endGame();
		}
	}
	public void move3(String mov){
		String mo = mov.replace(" ", "");

		int r1= mo.charAt(0) - '0';
		int c1= mo.charAt(1) - '0';
		int r2= mo.charAt(2) - '0';
		int c2= mo.charAt(3) - '0';
		board[r2][c2]=board[r1][c1];
		board[r1][c1]='o';
		promote(r2,c2);
		jump(r1,c1,r2,c2);
		if(opp.equals("computer")){
			drawBoard(grid);
		}else{
			drawBoard(grid1);
		}

	}

	public StackPane piece(int radius, String color1, String color2) {
		Circle circle = new Circle(radius);

		circle.setStyle(String.format(
				"-fx-fill: linear-gradient(to bottom, %s, %s);",
				color1, color2
		));
		Circle inner=new Circle(radius/1.5);
		inner.setStyle(String.format(
				"-fx-fill: linear-gradient(to top, %s, %s);",
				color1, color2
		));
		return new StackPane(circle,inner);
	}
	public StackPane king(int radius, String color1, String color2) {
		Circle circle = new Circle(radius);

		circle.setStyle(String.format(
				"-fx-fill: linear-gradient(to bottom, %s, %s);",
				color1, color2
		));
		Label inner=new Label("K");
		inner.setStyle(String.format(
				"-fx-background-color:"+color2+";"
		));
		return new StackPane(circle,inner);
	}
	public void endGame(){
		boolean red=true;
		boolean black=true;
		if(lock()){
			return;
		}
		for(int r=0;r<8;r++){
				for(int c=0;c<8;c++){
					if(Character.toLowerCase(board[r][c])=='b'){
						if(r-1>=0 && c-1>=0){
							if(board[r-1][c-1]=='o'){
								black=false;
								break;
							}
						}
						if(r-1>=0 && c+1<8){
							if(board[r-1][c+1]=='o'){
								black=false;
								break;
							}
						}
						if(r-2>=0 && c-2>=0){
							if(board[r-2][c-2]=='o'){
								black=false;
								break;
							}
						}
						if(r-2>=0 && c+2<8){
							if(board[r-2][c+2]=='o'){
								black=false;
								break;
							}
						}

					}
					if(board[r][c]=='B'){
						if(r+1<8 && c-1>=0){
							if(board[r+1][c-1]=='o'){
								black=false;
								break;
							}
						}
						if(r+1<8 && c+1<8){
							if(board[r+1][c+1]=='o'){
								black=false;
								break;
							}
						}
						if(r+2<8 && c-2>=0){
							if(board[r+2][c-2]=='o'){
								black=false;
								break;
							}
						}
						if(r+2<8 && c+2<8){
							if(board[r+2][c+2]=='o'){
								black=false;
								break;
							}
						}
					}
				}
		}
		for(int r=0;r<8;r++){
			for(int c=0;c<8;c++){
				if(board[r][c]=='R'){
					if(r-1>=0 && c-1>=0){
						if(board[r-1][c-1]=='o'){
							red=false;
							break;
						}
					}
					if(r-1>=0 && c+1<8){
						if(board[r-1][c+1]=='o'){
							red=false;
							break;
						}
					}
					if(r-2>=0 && c-2>=0){
						if(board[r-2][c-2]=='o'){
							red=false;
							break;
						}
					}
					if(r-2>=0 && c+2<8){
						if(board[r-2][c+2]=='o'){
							red=false;
							break;
						}
					}

				}
				if(board[r][c]=='r'||board[r][c]=='R'){
					if(r+1<8 && c-1>=0){
						if(board[r+1][c-1]=='o'){
							red=false;
							break;
						}
					}
					if(r+1<8 && c+1<8){
						if(board[r+1][c+1]=='o'){
							red=false;
							break;
						}
					}
					if(r+2<8 && c-2>=0){
						if(board[r+2][c-2]=='o'){
							red=false;
							break;
						}
					}
					if(r+2<8 && c+2<8){
						if(board[r+2][c+2]=='o'){
							red=false;
							break;
						}
					}
				}
			}
		}
		if(black && red){
			clientConnection.end("draw");
		}else if(black){
			clientConnection.end(opp);
		}else if(red){
			clientConnection.end(user);
		}

	}


}