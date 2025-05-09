package gr.uop;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

//import javax.imageio.ImageIO;

public class OldPlayerClient  {    // extends Application

    String serverip = "localhost";
    int port = 7777;

    Socket clientSocket;
    PrintWriter toServer;
    Scanner fromServer;

    Client thisClient;

    // messages to appear at the log textareas
    public String logMessage="";

    public boolean isConnected=false;
    public boolean registrationSuccess = false;


    public boolean connError = false;
    public String connErrorMsg = "";
    String lastServerMessage="";
    
    
    // prefered name and color
    public int ClientNumber = 0;
    public String myName = "george";
    public String myColor = "red";
    public int gplaymode = 4;
    public int playerNo = 0;
    
    public boolean canStartTheGame = false;
    

    public String myPlayerName = "";
    public void setMyPlayerName(String mpln){
        myPlayerName=mpln;
    }

    public boolean StartGameNow = false;
    public void setStartGameNow(boolean startgame){
        StartGameNow=startgame;
    }


    public boolean quitRequest = false;
    public void setQuitRequest( boolean qreq){
        quitRequest=qreq;
    }

    ServerListener serverListener;

    public CommMessages.GameStatus gameStatus = CommMessages.GameStatus.NEW_GAME;

    public class BoardSquare {
        public int row ;
        public int col ;
        public BoardSquare(int r, int c){
            this.row=r;
            this.col=1;
        }
    }

    public int kingSelected = -1;
    public Tile tileForKingSelected = null;
    public Node NodeForSquare1Selected=null;
    public Node NodeForSquare2Selected=null;

    public Node NodeForKingSelected=null;   // 1 fora enimeronetai !!

    
    private KingTile King1Tile =null;
    private KingTile King2Tile =null;

    public boolean tile1Sqr1Placed = false;
    public boolean tile1Sqr2Placed = false;
    public boolean tile1Placed = false;
    public boolean King1Placed = false;
    public boolean King2Placed = false;
    public boolean tile2Sqr1Placed = false;
    public boolean tile2Sqr2Placed = false;
    public boolean tile2Placed = false;

    public int[] boardSquare1 = {-1,-1};    // row, col
    public int[] boardSquare2 = {-1, -1};   // row, col

    public int[] lastTileCoord = {-1, -1, -1, -1};

    public PlayerBoard player1Board;
    public ArrayList<Tile> player1DrawLineOne ;
    public ArrayList<Tile> player1DrawLineTwo ;

    public PlayerBoard player2Board;
    public ArrayList<Tile> player2DrawLineOne ;
    public ArrayList<Tile> player2DrawLineTwo ;

    public PlayerBoard player3Board;
    public PlayerBoard player4Board;


    public Button btnfipSquares ;
    public Button btnplacementDone;
    public Button btnDiscardTile;

  
    String debugIndicativeStr = "none";


    int otherPlayers = 0;

    // Timer timer;
    TimerTask listenServertask;
    boolean listenToServer=false;
    
    // --------------- FX ---------------------------------------
    
    Stage this_stage;
    Scene scene;

    MenuBar menuBar;
    Menu menuGame;
    MenuItem connToserver;
    MenuItem stopGame;
    MenuItem exit;
    Menu menuHelp;
    MenuItem help;
    MenuItem about;

    private final TextArea logtext = new TextArea();
    private final Label user1info = new Label("Player1 ...");;
    private final Label user2info = new Label("Player2 ...");
    private final Label user3info = new Label("Player3 ...");
    private final Label user4info = new Label("Player4 ...");
    
    private final Button tile1 = new Button(" Tile1 ... ");;
    private final Button tile2 = new Button(" Tile2 ... ");;

    class OLDKingTile extends Tile {
        public ImageView castleImageView;
        Color castleColor;
        public OLDKingTile(int guiking, int kingno, String color ){
            super(color);
            if(color.equals("red")) castleColor = Color.RED;
            else if(color.equals("green"))castleColor = Color.GREEN;
            else if(color.equals("blue"))castleColor = Color.BLUE;
            else if(color.equals("yellow"))castleColor = Color.YELLOW;
            else castleColor = Color.WHITE;
            this.castleImageView = imageKingCastle(guiking, kingno, color);
        }
        public ImageView imageKingCastle(int guiking, int kingno, String color){
            FileInputStream pnginput;
            try {
                if(color.equals("red")){
                    pnginput = new FileInputStream("castlered.png");
                }
                else if(color.equals("green")){
                    pnginput = new FileInputStream("castlegreen.png");
                }          
                else if(color.equals("blue")){
                    pnginput = new FileInputStream("castleblue.png");
                }           
                else if(color.equals("yellow")){
                    pnginput = new FileInputStream("castleyellow.png");
                }           
                else {
                    pnginput = new FileInputStream("blank.png");
                }          
                Image img = new Image(pnginput);
                ImageView imageView = new ImageView(img);
                imageView.setFitHeight(imgHeight/2);
                imageView.setFitWidth(imgWidth/2);
                return imageView;
        
            } catch (Exception e) {
                System.out.println("castlexxxxx.png not found");
                return null;
            }      
        }
    }


    private final GridPane pl1selcards = new GridPane();
    private final GridPane pl2selcards = new GridPane();
    private final GridPane pl1board = new GridPane();
    private final GridPane pl2board = new GridPane();

    private final GridPane pl3board = new GridPane();
    private final GridPane pl4board = new GridPane();

    private final TabPane tabPane = new TabPane();
    private Tab tab1 = null;
    private Tab tab2 = null;
    private Tab tab3 = null;
    private Tab tab4 = null;

    Image blankimg; // = new Image("UIControls/logo.png");
    Image imgcastle1;
    Image imgcastle2;

    final int imgWidth = 50;
    final int imgHeight = 50;



    
    // flag, indicates if getdatafromserver should readdata waiting or just return without reading input stream
    // because, client is polling server we some times need to skip this withour stopping the poll permanently
    public boolean wait_server = true;

    // holds player variables
    public class Player {
        
        int GuiPlayerNumber;
        int playerNumber ;
        int kingNumber ;
        String playerName;
        String playerColor;
        Color PCOLOR;
    
        public Player(int guiplayernum, int plnum, int kgnum, String plname, String plcolor ){
            this.GuiPlayerNumber = guiplayernum ;
            this.playerNumber=plnum;
            this.kingNumber=kgnum;
            this.playerName=plname;
            this.playerColor=plcolor;
            if(plcolor.equals("red")) this.PCOLOR=Color.RED;
            else if(plcolor.equals("green")) this.PCOLOR=Color.GREEN;
            else if(plcolor.equals("blue")) this.PCOLOR=Color.BLUE;
            else this.PCOLOR=Color.YELLOW;
        }
    }
        
    // holds all players objects (1 or 2 in game 2players) assigned to this client
    public Player[] players = {null, null};

    // old constructor
    public OldPlayerClient( Socket socket){
        this.clientSocket = socket;
    }
    
    // new constructor

    public OldPlayerClient( String ip, int prt, Client thisclient){
        this.serverip = ip;
        this.port = prt;
        this.thisClient = thisclient;
        // thisClient.doAlert( "Called from player client", "called from playerclient class");
        serverListener = new ServerListener();

        listenToServer=false;
        serverListener.start();

    }

    // stop client, ie, close socket
    public void stop()
    {
        try {
            if(clientSocket.isConnected() && clientSocket.isClosed()==false) {
                clientSocket.close();
            }
        } catch(Exception e){

        }
    }
    
    // general method to send unprocessed data to server
    public void sendToServer(String message){
        /*  general client format:  %d;%d;%d;%s;%s;action=%s;%d
            [0] client number
            [1] player number
            [2] king number
            [3] playername
            [4] playercolor
            [5] action=.....
            [6] rest of data
        */
        System.out.println(" sendToServer:: sending [" + message +"]");
        toServer.println(message);
    }

    // general method, prepends clientnumber and playernumber and action to data
    public void SendMessageToServer(CommMessages.ClientActions action, int playerno, String data){
          /*  general client format:  %d;%d;%d;%s;%s;action=%s;%d
            [0] client number
            [1] player number
            [2] king number
            [3] playername
            [4] playercolor
            [5] action=.....
            [6] rest of data
        */
        String fullmessage=String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber, playerno, 0,"none", "none", action.getName(),data);
        System.out.println(" sendToServer:: ...");
        sendToServer(fullmessage);
    }


    // ============ menu actions ================================
     // menu action ....
    public boolean connectToserver() {
        this.logMessage="";
        this.registrationSuccess = false;
        String msg = ""; 
        if(isConnected) {
            msg = String.format("Opening socket with host %s:%d ... Altready Connected.", serverip, port);
            this.logMessage=msg;
            //this.logtext.clear();
            //this.logtext.setText(logMessage);
            System.out.println("DEBUG:: " + msg);
            return true;
        }       
        try {
            
            msg = "Opening socket with host "+serverip+":"+Integer.toString(port)+" ...";
            this.logMessage=msg;
            //this.logtext.clear();
            //this.logtext.setText(logMessage);
            System.out.println("DEBUG:: " + msg);
    
            clientSocket = new Socket(serverip, port);
            
            System.out.println("connectToserver:: The socket is connected: "+clientSocket.isConnected());  
            System.out.println("connectToserver:: The socket is isBound: "+clientSocket.isBound()); 
            System.out.println("connectToserver:: The socket is isClosed: "+clientSocket.isClosed()); 

            this.toServer = new PrintWriter(clientSocket.getOutputStream(), true);
            this.fromServer = new Scanner(clientSocket.getInputStream());
            String response = "";
            System.out.println("connectToserver:: Waiting for server response (blocked) ...");
            response = fromServer.nextLine();
            System.out.print("connectToserver:: Server Response is: [" + response + "]");
            // [playmode=4&client=0&player=0&king=0&action=connectionaccepted]
            String[] msgfields = response.split("&");

            String field = msgfields[1]; // client=0
            String[] fv = field.split("=");
            this.ClientNumber = Integer.parseInt(fv[1]);


            field = msgfields[4]; // action=4
            if(field.contains("connectionaccepted")){
                //Player player = new Player(1, playernumber, kingnumber, "", "");
                //players[playerNo] = player;
                this.isConnected = true;
                // this.connToserver.setDisable(true);
                // this.stopGame.setDisable(false);
                // this.exit.setDisable(true);
                if( this.ClientNumber>0) {
                    this.canStartTheGame=true;
                }else {
                    this.canStartTheGame=false;
                }
                msg = String.format("Opening socket with host %s:%d ... Connected.", serverip, port);
                this.logMessage=msg;
                // this.logtext.clear();
                // this.logtext.setText(logMessage);
                System.out.println("DEBUG:: " + msg);

                // start listening to server ...
                listenToServer=true;

                return true;
            }
            else {
                msg = String.format("Opening socket with host %s:%d ... Failed.", serverip, port);
                this.logMessage=msg;
                //this.logtext.clear();
                //this.logtext.setText(logMessage);
                System.out.println("DEBUG:: " + msg);
                return false;
            }


        }catch (UnknownHostException e) {
            System.out.println("connectToserver:: Unknown host");
            this.connError=true;
            this.connErrorMsg = "connectToserver:: Unknown host";

            msg = String.format("Opening socket with host %s:%d ... ERROR: %s", serverip, port, e.getMessage());
            this.logMessage=msg;
            // this.logtext.clear();
            // this.logtext.setText(logMessage);
            System.out.println("DEBUG:: " + msg);

            return false;
            
        }catch (IOException e) {
            msg = String.format("Opening socket with host %s:%d ... ERROR: %s", serverip, port, e.getMessage());
            this.logMessage=msg;
            //this.logtext.clear();
            //this.logtext.setText(logMessage);
            System.out.println("DEBUG:: " + msg);
            this.connError=true;
            this.connErrorMsg = "connectToserver:: "+e.getMessage();
            return false;
        }
    }


    public boolean RegisterPlayer(String playername, String KingColor, String response, boolean startgame ) {
        /*if(!(clientSocket.isConnected() && clientSocket.isBound() && clientSocket.isClosed())) {
            System.out.println("Register:: Socket problem !");
            this.connError=true;
            this.connErrorMsg = "Register:: Socket problem !";
            return false;
        }*/
        try {

            
            //System.out.println("Register:: The socket is connected: "+clientSocket.isConnected());  
            //System.out.println("Register:: The socket is isBound: "+clientSocket.isBound()); 
            //System.out.println("Register:: The socket is isClosed: "+clientSocket.isClosed()); 
            List<String> reservenames = new ArrayList<String>();
            List<String> reservedcolors = new ArrayList<String>();

            //String response = "";
            //System.out.println("Register:: Waiting for server response (blocked) ...");
            //response = fromServer.nextLine();
            //System.out.print("Register:: Server Response is: [" + response + "]");
            //System.out.println("]");

            /* message example
             * playmode=2&client=0&player=0&king=0&action=registration&registeredplayers=1;george;red@2;thanos;yellow@....
             * or
             * playmode=2&client=0&player=0&king=0&action=registration&registeredplayers=none
             * actions are: registration, play
             * 
             * playmode=0
             * client=1
             * player=2
             * king=3
             * action=4
             * registeredplayers=5
             */
            String[] msgfields = response.split("&");

            if(msgfields[0].equals("playmode=4")) gplaymode = 4;
            else if(msgfields[0].equals("playmode=3")) gplaymode = 3;
            else if(msgfields[0].equals("playmode=2")) gplaymode = 2;

            String field = msgfields[1]; // client=0
            String[] fv = field.split("=");
            ClientNumber = Integer.parseInt(fv[1]);

            field = msgfields[2]; // player=2
            fv = field.split("=");
            int playernumber =  Integer.parseInt(fv[1]);

            field = msgfields[3]; // king=3
            fv = field.split("=");
            int kingnumber =  Integer.parseInt(fv[1]);

            field = msgfields[4]; // action=4

            field = msgfields[5]; // &registeredplayers=none  or registeredplayers=0;1;george;red@1;2;thanos;yellow@.
            fv = field.split("=");
            String regplayers = fv[1];

            if (!regplayers.equals("none")) {
                
                System.out.println("Register:: next player to register ...");
                String[] arrayPlayers = response.split("@", 0);
                // System.out.println(arrayPlayers);
                for(int i=0 ; i < arrayPlayers.length;i++){
                    System.out.print(i);
                    System.out.print(") ");
                    String playerinfo = arrayPlayers[i];
                    System.out.println(" playerinfo="+playerinfo);
                    String[] aplayerinfo = playerinfo.split(";");
                    // 0;1;george;red
                    // Integer.toString(clientNumber) + ";" + Integer.toString(playerNumber) + ";" + playerName + ";"  +Integer.toString(kingNumber) + ";" + playerColor; 
                    /* clientNumber=0
                     * playerNumber=1
                     * playerName=2
                     * kingNumber=3
                     * playerColor=4
                     */
                    reservenames.add(aplayerinfo[2]);
                    reservedcolors.add(aplayerinfo[4]);

                }
            }
            else System.out.println("Register:: 1rst player to register ... ");

            myName=playername;

            myColor = "red";
            if(reservedcolors.contains(myColor)){
                myColor="green";
                if(reservedcolors.contains(myColor)){
                    myColor="blue";
                    if(reservedcolors.contains(myColor)){
                        myColor="yellow";
                    }
                }
            }
            Player player = new Player(1, playernumber, kingnumber, myName, myColor);
            players[playerNo] = player;
            
            // String message = CommMessages.ClientActions.REGISTER_PLAYER.getName() + ";" +  myName + ";" + myColor;
            // if(startgame==true) message += ";subaction="+ CommMessages.ClientActions.REQUEST_STARTGAME.getName();

            String subaction = "none";
            if(startgame==true) subaction += "subaction="+ CommMessages.ClientActions.REQUEST_STARTGAME.getName();


            /*  general client format:  %d;%d;%d;%s;%s;action=%s;%s
            [0] client number
            [1] player number
            [2] king number
            [3] playername
            [4] playercolor
            [5] action=.....
            [6] rest of data or subaction=
            String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions. .getName(), "updateSubseqRow");
            */

            String message=String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber, playernumber,kingnumber,myName,myColor,CommMessages.ClientActions.REGISTER_PLAYER.getName(), subaction);
            System.out.println("Register:: registering as [" + message +"]");
            toServer.println(message);

            return true;

        } catch (Exception e) {
            System.out.println(e);
            this.connError=true;
            this.connErrorMsg = "Register:: "+e.getMessage();
            return false;
        }
   
    }

    // menu action
    public void fmenuexit(){
        // if(isListening) pauseListening();

        if(this.isConnected) {
            
            SendMessageToServer(CommMessages.ClientActions.QUIT, 0, "");
            stop();
            /*try {
                clientSocket.close();
            }
            catch (Exception e){

            }*/
        }
        this.isConnected=false;
        this.registrationSuccess=false;
        
        System.exit(0);
    }

    void StopGame(Stage stage, boolean act){

        this.quitRequest = true;

        return ;

        /*if(isConnected) {
            
            SendMessageToServer(CommMessages.ClientActions.QUIT, 0, "");
            //stop(); // close socket
            //isConnected=false;
            registrationSuccess=false;
        }
        String msg = String.format(" Client requested Stop game... ");
        this.logtext.setText(msg);
        */
    }

 /*   public void Help(Stage stage){

    }

    public void About(Stage stage){

    }
*/

        // reads from server input stream if flag wait_server is true else just returns false
    // data is stored to lastServerMessage propery of PlayerClient
    public boolean getFromServer(){
        if(!(clientSocket.isConnected() && clientSocket.isBound() && clientSocket.isClosed()==false)) {
             System.out.println("WaitServer.getFromServer:: Socket problem !");
             connError=true;
             connErrorMsg = "WaitServer.getFromServer:: Socket problem !";
             return false;
        }
             // System.out.println("WaitServer.getFromServer:: polling for server message...");
             System.out.print("#");
             String response = "";
             
             //System.out.println("waitServer:: The socket is connected: "+clientSocket.isConnected());  
             //System.out.println("waitServer:: The socket is isBound: "+clientSocket.isBound()); 
             //System.out.println("waitServer:: The socket is isClosed: "+clientSocket.isClosed());
 
             /* isBound()	This method returns the binding state of the socket
                 isClosed()	This method returns the closed state of the socket.
                 isConnected()	This method returns the connection state of the socket.
                 isInputShutdown()	This method returns whether the read-half of the socket connection is closed
                 isOutputShutdown()	This method returns whether the write-half of the socket connection is closed.
             */
    
         //while(wait_server){
             if(wait_server==false) return false;
             try{
                     if(clientSocket.getInputStream().available()>0){
                         response = this.fromServer.nextLine();
                         System.out.println("WaitServer.getFromServer:: server Response: " + response);
                         lastServerMessage=response;
                         return true;
                     } else return false;
             } catch (NoSuchElementException e){
                     System.out.println("WaitServer.getFromServer:: NoSuchElementException: " + e.getMessage());
                     System.out.println("WaitServer.getFromServer:: The socket is connected: "+clientSocket.isConnected());  
                     System.out.println("WaitServer.getFromServer:: The socket is isBound: "+clientSocket.isBound()); 
                     System.out.println("WaitServer.getFromServer:: The socket is isClosed: "+clientSocket.isClosed()); 
                     // server did not send anything yet ...    
                     return false;
             } catch (IllegalStateException e){
                     System.out.println("WaitServer.getFromServer:: IllegalStateException: " + e.getMessage()); 
                     System.out.println("WaitServer.getFromServer:: The socket is connected: "+clientSocket.isConnected());  
                     System.out.println("WaitServer.getFromServer:: The socket is isBound: "+clientSocket.isBound()); 
                     System.out.println("WaitServer.getFromServer:: The socket is isClosed: "+clientSocket.isClosed());
                     // scanner closed !!! ie stream broken
                     //break;  
                     return false;
             } catch (Exception e) {
                     System.out.println("WaitServer.getFromServer:: Exception: " + e.getMessage()); 
                     System.out.println("WaitServer.getFromServer:: The socket is connected: "+clientSocket.isConnected());  
                     System.out.println("WaitServer.getFromServer:: The socket is isBound: "+clientSocket.isBound()); 
                     System.out.println("WaitServer.getFromServer:: The socket is isClosed: "+clientSocket.isClosed());
                     // other exception
                     //break;     
                     return false;               
             }
                     
         //}
     }

    public class ServerListener {
        public boolean isActive = false;
        private Timer innerTimer;
        private TimerTask innerTask;
        public void start(){
            innerTimer = new Timer();
            isActive=true;
            innerTask = new TimerTask() {
                public void run(){
                    if(listenToServer) {
                        // System.out.println(" ===== ServerListener is listening ...");
                        wait_server=true;
                        boolean hasmessage = getFromServer();
                        if(hasmessage){  
                            listenToServer=false;                  
                            Platform.runLater(new Runnable() {
                                    public void run() {
                                        String message = "" + lastServerMessage;
                                        lastServerMessage="";
                                        handleServerMessage(message);
                                }
                            });
                            // listenToServer=true;
                        }
                    }
                }
            };
            innerTimer.scheduleAtFixedRate(innerTask, 500, 1000);
        }
        public void stop(){
            innerTimer.cancel();
            innerTimer.purge();
            isActive=false;
        }


    }

 

    public void handleServerMessage(String message) {
        System.out.println(" == handleServerMessage :: "+message );
        /* server Response: 
        * playmode=2&client=0&player=0&king=0&action=playersjoined&registeredplayers=1;george;red@2;thanos;yellow@....
        * or
        * playmode=2&client=0&player=0&king=0&action=playersjoined&registeredplayers=none
        * playmode [0]
        * client [1]
        * player [2]
        * king  [3]
        * action [4]
        * registeredplayers [5]
            
        playmode=2&client=0&player=2&king=2&action=newkingregistration&registeredplayers=0;0;than;0;red@0;1;geog;1;green
        0          1          2        3       4                         5
 
       PLAYERS_JOINED : 
       playmode=2&client=1&player=3&king=3&action=playersjoined&registeredplayers=0;0;than;0;red@0;1;geo;1;green@0;2;than;2;blue@0;3;geo;3;yellow

        PLACE_KING :
       playmode=%d&client=%d&player=%d&king=%d&action=PLACE_KING&firstrow=%s;
        0           1        2         3         4                5

        GAME_ABORTED
        SERVER sent to player 0, client 0, message: playmode=2&client=0&player=0&king=0&action=gameaborted

        */

        String [] arfields = message.split("&");

        String[] fplaymode = arfields[0].split("=");
        int playmode = Integer.parseInt(fplaymode[1]);
        
        String[] fclientNum = arfields[1].split("=");
        int clientNum = Integer.parseInt(fclientNum[1]);
        
        String[] fplayerNum = arfields[2].split("=");
        int playerNum = Integer.parseInt(fplayerNum[1]);
        
        String[] fkingNum = arfields[3].split("=");
        int kingNum = Integer.parseInt(fkingNum[1]);
        
        String[] faction = arfields[4].split("=");
        String action = faction[1];

        //String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, ServerActions.PLACE_KING.getName(),firstrow);

        if(action.equals(CommMessages.ServerActions.REGISTRATION.getName())){
            //lclPlayerNameDialog(this_stage, canStartTheGame);
            registerToServer(StartGameNow, message ); // 
        }
        else {
            if(quitRequest == true) {
                if(isConnected) {
                    SendMessageToServer(CommMessages.ClientActions.QUIT, 0, "");
                    listenToServer = true;
                    //stop(); // close socket
                    //isConnected=false;
                    registrationSuccess=false;
                }
                String msg = String.format(" Client requested Stop game... ");
                this.logtext.setText(msg);
            }
            else {
                if(action.equals(CommMessages.ServerActions.NEWKINGREGISTRATION.getName())){    
                    register2ndKing( playmode, clientNum, playerNum, kingNum,arfields[5]); 
                 }
                else if(action.equals(CommMessages.ServerActions.STARTING_ROW.getName())){
                   // String[] ffirstrow = arfields[5].split("=");           
                   updateFirstRow( playmode, clientNum, playerNum, kingNum, arfields[5]);
                }
                else if(action.equals(CommMessages.ServerActions.PLAYERS_JOINED.getName())){
                    // playmode=2&client=1&player=3&king=3&action=playersjoined&registeredplayers=0;0;than;0;red@0;1;geo;1;green@0;2;than;2;blue@0;3;geo;3;yellow
                    // 0           1        2        3      4                     5
                    updatePlayersJoined( playmode, clientNum, playerNum, kingNum, arfields[5]);
                 }
                 else if(action.equals(CommMessages.ServerActions.PLACE_KING.getName())){
                    doPlaceKing(playmode, clientNum, playerNum, kingNum, arfields[5]);
                }
                else if(action.equals(CommMessages.ServerActions.UPDATE_FIRSTROW.getName())){
                 /*  general client format:  %d;%d;%d;%s;%s;action=%s;%s
                    [0] client number
                    [1] player number
                    [2] king number
                    [3] playername
                    [4] playercolor
                    [5] action=.....
                    [6] rest of data
                    String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", clientNum,playerNum,kingNum,"none","none",CommMessages.ClientActions. .getName(), "updateSubseqRow");
                */
        
                    reupdateFirstRow( playmode, clientNum, playerNum, kingNum, arfields[5]);
                    listenToServer = true;
                    //String replymessage = String.format("%d;updateFirstRow=%s", clientNum,  CommMessages.ClientActions.DONE.getName());
                    String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions.DONE.getName(), "updateFirstRow");
                    System.out.println("updateFirstRow:: send to server [" + replymessage +"]");
                    toServer.println(replymessage);
                }
                else if(action.equals(CommMessages.ServerActions.SUBSEQ_ROW.getName())){
                    reupdateSubseqRow(playmode, clientNum, playerNum, kingNum, arfields[5], true);
                    listenToServer = true;
                    //String replymessage = String.format("%d;updateSubseqRow=%s", clientNum,  CommMessages.ClientActions.DONE.getName());
                    String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions.DONE.getName(), "updateSubseqRow");
                    System.out.println("updateSubseqRow:: send to server [" + replymessage +"]");
                    toServer.println(replymessage);
                    // doSubseqPlaceKing(playmode, clientNum, playerNum, kingNum, arfields[5]);
                }
                else if(action.equals(CommMessages.ServerActions.PLACEAGAIN_KING.getName())){
                    doSubseqPlaceKing(playmode, clientNum, playerNum, kingNum, arfields[5]);

                }
                else if(action.equals(CommMessages.ServerActions.GAME_ABORTED.getName())){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Game Aborted");
                    alert.setHeaderText("Server notified Game Aborted");
                    alert.setContentText("Server notified Game Aborted. Exit now"); 
                    alert.showAndWait(); 
                    this.exit.setDisable(false);
                    this.stopGame.setDisable(true);
                }
            }

        }

    }


    void loadCastle(int c, int kingno, String color){

        if(c==1) {
            // KingTile(int guiking, int kingno, String color, int imgHeight, int imgWidth )
            King1Tile = new KingTile(c, kingno, color, imgHeight, imgWidth,1,1);
            this.tile1.setGraphic(King1Tile.castleImageView);
        }
        else {
            King2Tile = new KingTile(c, kingno, color, imgHeight, imgWidth,1,1);
            this.tile2.setGraphic(King2Tile.castleImageView);           
        }

        FileInputStream pnginput;
        try {
            if(color.equals("red")){
                pnginput = new FileInputStream("castlered.png");
            }
            else if(color.equals("green")){
                pnginput = new FileInputStream("castlegreen.png");
            }          
            else if(color.equals("blue")){
                pnginput = new FileInputStream("castleblue.png");
            }           
            else if(color.equals("yellow")){
                pnginput = new FileInputStream("castleyellow.png");
            }           
            else {
                pnginput = new FileInputStream("blank.png");
            }          
            Image img = new Image(pnginput);
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(imgHeight);
            imageView.setFitWidth(imgWidth);
            if(c==1) this.tile1.setGraphic(imageView);
            else this.tile2.setGraphic(imageView);

       } catch (Exception e) {
           System.out.println("castlexxxxx.png not found");

       }
    }

    public boolean Register(String playername, String response, boolean startgame ) {
        /*if(!(clientSocket.isConnected() && clientSocket.isBound() && clientSocket.isClosed())) {
            System.out.println("Register:: Socket problem !");
            this.connError=true;
            this.connErrorMsg = "Register:: Socket problem !";
            return false;
        }*/
        try {

            
            //System.out.println("Register:: The socket is connected: "+clientSocket.isConnected());  
            //System.out.println("Register:: The socket is isBound: "+clientSocket.isBound()); 
            //System.out.println("Register:: The socket is isClosed: "+clientSocket.isClosed()); 
            List<String> reservenames = new ArrayList<String>();
            List<String> reservedcolors = new ArrayList<String>();

            //String response = "";
            //System.out.println("Register:: Waiting for server response (blocked) ...");
            //response = fromServer.nextLine();
            //System.out.print("Register:: Server Response is: [" + response + "]");
            //System.out.println("]");

            /* message example
             * playmode=2&client=0&player=0&king=0&action=registration&registeredplayers=1;george;red@2;thanos;yellow@....
             * or
             * playmode=2&client=0&player=0&king=0&action=registration&registeredplayers=none
             * actions are: registration, play
             * 
             * playmode=0
             * client=1
             * player=2
             * king=3
             * action=4
             * registeredplayers=5
             */
            String[] msgfields = response.split("&");

            if(msgfields[0].equals("playmode=4")) gplaymode = 4;
            else if(msgfields[0].equals("playmode=3")) gplaymode = 3;
            else if(msgfields[0].equals("playmode=2")) gplaymode = 2;

            String field = msgfields[1]; // client=0
            String[] fv = field.split("=");
            ClientNumber = Integer.parseInt(fv[1]);

            field = msgfields[2]; // player=2
            fv = field.split("=");
            int playernumber =  Integer.parseInt(fv[1]);

            field = msgfields[3]; // king=3
            fv = field.split("=");
            int kingnumber =  Integer.parseInt(fv[1]);

            field = msgfields[4]; // action=4

            field = msgfields[5]; // &registeredplayers=none  or registeredplayers=0;1;george;red@1;2;thanos;yellow@.
            fv = field.split("=");
            String regplayers = fv[1];

            if (!regplayers.equals("none")) {
                
                System.out.println("Register:: next player to register ...");
                String[] arrayPlayers = response.split("@", 0);
                // System.out.println(arrayPlayers);
                for(int i=0 ; i < arrayPlayers.length;i++){
                    System.out.print(i);
                    System.out.print(") ");
                    String playerinfo = arrayPlayers[i];
                    System.out.println(" playerinfo="+playerinfo);
                    String[] aplayerinfo = playerinfo.split(";");
                    // 0;1;george;red
                    // Integer.toString(clientNumber) + ";" + Integer.toString(playerNumber) + ";" + playerName + ";"  +Integer.toString(kingNumber) + ";" + playerColor; 
                    /* clientNumber=0
                     * playerNumber=1
                     * playerName=2
                     * kingNumber=3
                     * playerColor=4
                     */
                    reservenames.add(aplayerinfo[2]);
                    reservedcolors.add(aplayerinfo[4]);

                }
            }
            else System.out.println("Register:: 1rst player to register ... ");

            myName=playername;

            myColor = "red";
            if(reservedcolors.contains(myColor)){
                myColor="green";
                if(reservedcolors.contains(myColor)){
                    myColor="blue";
                    if(reservedcolors.contains(myColor)){
                        myColor="yellow";
                    }
                }
            }
            Player player = new Player(1, playernumber, kingnumber, myName, myColor);
            players[playerNo] = player;
            
            // String message = CommMessages.ClientActions.REGISTER_PLAYER.getName() + ";" +  myName + ";" + myColor;
            // if(startgame==true) message += ";subaction="+ CommMessages.ClientActions.REQUEST_STARTGAME.getName();

            String subaction = "none";
            if(startgame==true) subaction += "subaction="+ CommMessages.ClientActions.REQUEST_STARTGAME.getName();


            /*  general client format:  %d;%d;%d;%s;%s;action=%s;%s
            [0] client number
            [1] player number
            [2] king number
            [3] playername
            [4] playercolor
            [5] action=.....
            [6] rest of data or subaction=
            String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions. .getName(), "updateSubseqRow");
            */

            String message=String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber, playernumber,kingnumber,myName,myColor,CommMessages.ClientActions.REGISTER_PLAYER.getName(), subaction);
            System.out.println("Register:: registering as [" + message +"]");
            toServer.println(message);

            return true;

        } catch (Exception e) {
            System.out.println(e);
            this.connError=true;
            this.connErrorMsg = "Register:: "+e.getMessage();
            return false;
        }
   
    }

    public void registerToServer(boolean startgame, String response){
        
        logMessage="";
        registrationSuccess = false;
        String msg = "";        
        try {

            registrationSuccess = Register(myPlayerName,response, startgame);
            if(registrationSuccess==false){
                clientSocket.close();
                isConnected=false;
                msg = "Client registration Failed. ";
                return ;
            } else {

                msg = "Client registration succeded. ";
                logMessage="Client registration succeded. ";
                
                //setWindowTitle("KingDomino client #" + ClientNumber);
                String l1msg="";
                String l2msg="";
                String l1clr="";
                String l2clr="";

                String l3msg="";
                String l4msg="";
                String l3clr="";
                String l4clr="";
                int cnt = 0;
                for(Player p : players){
                    if(p != null){
                        msg += "#"+Integer.toString(p.playerNumber)+ " Name="+p.playerName+", Color="+p.playerColor+" ";  
                        if (cnt==0){
                            l1msg=String.format(" [ ME: |_| Player #%d-%s , King #%d, Color=%s ]", p.playerNumber, p.playerName.toUpperCase(), p.kingNumber, p.playerColor.toUpperCase());
                            l1clr=p.playerColor;
                        } else {
                            l2msg=String.format(" [ ME: |_| Player #%d-%s , King #%d, Color=%s ]", p.playerNumber, p.playerName.toUpperCase(), p.kingNumber, p.playerColor.toUpperCase());
                            l2clr=p.playerColor;
                        }
                    }
                    cnt++;
                }
                this.user1info.setText(l1msg); 
                this.tab1.setText(l1msg);               
                BackgroundFill background_fill;
                if(l1clr.equals("red")){
                    //background_fill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
                    loadCastle(1,  this.players[0].kingNumber, "red");
                } else if(l1clr.equals("green")){
                   //background_fill = new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY);
                   loadCastle(1, this.players[0].kingNumber, "green");
                } else if(l1clr.equals("blue")){
                    //background_fill = new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY); 
                    loadCastle(1, this.players[0].kingNumber ,"blue");
                } else {
                    //background_fill = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
                    loadCastle(1, this.players[0].kingNumber, "yellow");
                }    
                //Background tbg = new Background(background_fill);
                //this.user1info.setBackground(tbg);
                
                if(players[1] != null){
                    this.user2info.setText(l2msg);
                    this.tab2.setText(l2msg);
                    if(l2clr.equals("red")){
                        //background_fill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
                        loadCastle(2, this.players[1].kingNumber, "red");
                    } else if(l2clr.equals("green")){
                        loadCastle(2, this.players[1].kingNumber, "green");
                        //background_fill = new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY);
                    } else if(l2clr.equals("blue")){
                        loadCastle(2, this.players[1].kingNumber, "blue");
                        //background_fill = new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY); 
                    } else {
                        loadCastle(2, this.players[1].kingNumber, "yellow");
                        //background_fill = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
                    }    
                    //Background bbg = new Background(background_fill);
                    //this.user2info.setBackground(bbg);
                } else {
                    //this.user2info
                    this.tile2.setVisible(false);
                    this.pl2selcards.setVisible(false);
                    this.pl2board.setVisible(false);
                }

            }
        } catch (UnknownHostException e) {
            System.out.println("Unknown host");
            msg = "Client Connection to server failed.";
            msg +=" Error is "+"Unknown host";
            //labelTopTop.setText(msg);
            logMessage=msg;
            isConnected=false;
        
        } catch (IOException e) {
                System.out.println(e);
                msg = "Client Connection to server failed.";
                msg +=" Error is "+e.getMessage();
                isConnected=false;
                logMessage=msg;
                // labelTopTop.setText(msg);
        }
        this.logtext.clear();
        this.logtext.setText(logMessage);
        // return isConnected && registrationSuccess;

        if(isConnected && registrationSuccess){
            this.logtext.setText(logMessage+" Waiting for other players to register ...");
            listenToServer=true;
            this.stopGame.setDisable(false);
            this.exit.setDisable(true);
            
            if(startgame==true) {
                //notifyStartPlayNow();
            }
            gameStatus=CommMessages.GameStatus.REGISTRATION;
        }

    }

    public void register2ndKing(int playmode, int clientNum, int playerNum, int kingNum, String registeredplayers){
       // registeredplayers=0;0;than;0;red@0;1;geog;1;green
       //                  [0             , 1               ]
       String[] msgfields = registeredplayers.split("=");
       String regplayers = msgfields[1];
       List<String> reservenames = new ArrayList<String>();
       List<String> reservedcolors = new ArrayList<String>();
       System.out.println("register2ndKing:: second king to register. registered players : ");
       String[] arrayPlayers = regplayers.split("@", 0);
       for(int i=0 ; i < arrayPlayers.length;i++){
           String playerinfo = arrayPlayers[i];
           System.out.print(String.format("(%d) : %s", i, playerinfo ));
           String[] aplayerinfo = playerinfo.split(";");
           reservenames.add(aplayerinfo[2]);
           reservedcolors.add(aplayerinfo[4]);
       }
       System.out.println("register2ndKing:: reserved names:  ");
       System.out.println(reservenames);
       System.out.println("register2ndKing:: reserved colors:  ");
       System.out.println(reservedcolors);       
       myColor = "red";
       if(reservedcolors.contains(myColor)){
           myColor="green";
           if(reservedcolors.contains(myColor)){
               myColor="blue";
               if(reservedcolors.contains(myColor)){
                   myColor="yellow";
               }
           }
       }

       Player player = new Player(2,playerNum, kingNum, myPlayerName, myColor);
        /*  general client format:  %d;%d;%d;%s;%s;action=%s;%s
            [0] client number
            [1] player number
            [2] king number
            [3] playername
            [4] playercolor
            [5] action=.....
            [6] rest of data or subaction=
            String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions. .getName(), "updateSubseqRow");
        */

       playerNo++;
       players[playerNo] = player;
       //String message = String.format("%d;%s;%s;%s", clientNum, CommMessages.ClientActions.REGISTER_NEWKING.getName(),myName , myColor);
       String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,myPlayerName,myColor,CommMessages.ClientActions.REGISTER_NEWKING.getName(), "none");
       System.out.println("register2ndKing:: registered second king as [" + message +"]");
       toServer.println(message);


       this.tile2.setVisible(true);
       this.pl2selcards.setVisible(true);
       this.pl2board.setVisible(true);
       //this.pl2selcards.setDisable(false);
       //this.pl2board.setDisable(false);

       listenToServer=true;
    
       gameStatus=CommMessages.GameStatus.NEWKINGREGISTRATION;
    }

    public void updatePlayersJoined(int playmode, int clientNum, int playerNum, int kingNum, String playersstring){
        System.out.println(" updatePlayersJoined :: ...");
        otherPlayers = 0;
        /*  registeredplayers=0;0;than;0;red@0;1;geo;1;green@0;2;than;2;blue@0;3;geo;3;yellow
        clientNumber [0]; 
        playerNumber [1]; 
        playerName [2]; 
        kingNumber [3]; 
        playerColor [4]; 
        */
        String[] arfieldv = playersstring.split("=");
        String[] arplayers = arfieldv[1].split("@");
        boolean isotherplayer=true;
        for(int i=0;i<arplayers.length;i++){
            String[] arpldata = arplayers[i].split(";");
            int plno = Integer.parseInt(arpldata[1]);
            int kgno = Integer.parseInt(arpldata[3]);
            String plname = arpldata[2];
            String plcolor = arpldata[4];
            System.out.print(" checking player="+Integer.toString(plno)+" king= "+Integer.toString(kgno)+" name="+plname+" color="+plcolor+", players[]= ");
            System.out.print(players);
            isotherplayer=true;
            for(Player p : players) {
                if(p != null) {

                    if( kgno == p.kingNumber){
                        // it is me ..
                        System.out.println(" Its My King.");
                        isotherplayer=false;


                        if(p.GuiPlayerNumber==1) {

                            this.user1info.setText(String.format(" [ UME: |_| Player #%d-%s , King #%d, Color=%s ]", playerNum , p.playerName.toUpperCase(), p.kingNumber, p.playerColor.toUpperCase()));
                            this.tab1.setText(this.user1info.getText());
                            BackgroundFill background_fill;
                            if(p.playerColor.equals("red")){
                                loadCastle(1, p.kingNumber, "red");
                            } else if(p.playerColor.equals("green")){
                                loadCastle(1, p.kingNumber,"green");
                            } else if(p.playerColor.equals("blue")){
                                loadCastle(1, p.kingNumber,"blue"); 
                            } else {
                                loadCastle(1, p.kingNumber,"yellow");
                            } 

                        }
                        else {
                            this.user2info.setText(String.format(" [ UME: |_| Player #%d-%s , King #%d, Color=%s ]", playerNum , p.playerName.toUpperCase(), p.kingNumber, p.playerColor.toUpperCase()));
                            this.tab2.setText(this.user2info.getText());
                            BackgroundFill background_fill;
                            if(p.playerColor.equals("red")){
                                loadCastle(2, p.kingNumber,"red");
                            } else if(p.playerColor.equals("green")){
                                loadCastle(2, p.kingNumber,"green");
                            } else if(p.playerColor.equals("blue")){
                                loadCastle(2, p.kingNumber,"blue"); 
                            } else {
                                loadCastle(2, p.kingNumber,"yellow");
                            } 
                        }
                        break;
                    }
                }
            }
            if(isotherplayer==true) {
                otherPlayers++;
                System.out.print(" Its other player #"+Integer.toString(otherPlayers));
                if(otherPlayers==1){

                        tab4.setText(String.format(" [ |_| Player #%d-%s , King #%d, Color=%s ]", plno, plname.toUpperCase(), kgno,plcolor.toUpperCase()));

                        this.user4info.setText(String.format(" [ |_| Player #%d-%s , King #%d, Color=%s ]", plno, plname.toUpperCase(), kgno,plcolor.toUpperCase()) );
                        BackgroundFill background_fill;
                        if(plcolor.equals("red")){
                            background_fill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user4info.setBackground(bbg);
                        }
                        else if(plcolor.equals("green")) {
                            background_fill = new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user4info.setBackground(bbg);
                        }
                        else if(plcolor.equals("blue")) {
                            background_fill = new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user4info.setBackground(bbg);
                        }
                        else if(plcolor.equals("yellow")) {
                            background_fill = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user4info.setBackground(bbg);
                        }
                }
                else if(otherPlayers==2){
                        tab3.setText(String.format(" [ |_| Player #%d-%s , King #%d, Color=%s ]", plno, plname.toUpperCase(), kgno,plcolor.toUpperCase()));
                        this.user3info.setText(String.format(" [ |_| Player #%d-%s , King #%d, Color=%s ]", plno, plname.toUpperCase(), kgno,plcolor.toUpperCase()));
                        BackgroundFill background_fill;
                        if(plcolor.equals("red")){
                            background_fill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user3info.setBackground(bbg);
                        }
                        else if(plcolor.equals("green")) {
                            background_fill = new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user3info.setBackground(bbg);
                        }
                        else if(plcolor.equals("blue")) {
                            background_fill = new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user3info.setBackground(bbg);
                        }
                        else if(plcolor.equals("yellow")) {
                            background_fill = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user3info.setBackground(bbg);
                        }
                }
                else if(otherPlayers==3){
                        tab2.setText(String.format(" [ |_| Player #%d-%s , King #%d, Color=%s ]", plno, plname.toUpperCase(), kgno,plcolor.toUpperCase()));
                        this.user2info.setText(String.format(" [ |_| Player #%d-%s , King #%d, Color=%s ]", plno, plname.toUpperCase(), kgno,plcolor.toUpperCase()));
                        BackgroundFill background_fill;
                        if(plcolor.equals("red")){
                            background_fill = new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user2info.setBackground(bbg);
                        }
                        else if(plcolor.equals("green")) {
                            background_fill = new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user2info.setBackground(bbg);
                        }
                        else if(plcolor.equals("blue")) {
                            background_fill = new BackgroundFill(Color.BLUE, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user2info.setBackground(bbg);
                        }
                        else if(plcolor.equals("yellow")) {
                            background_fill = new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY);
                            Background bbg = new Background(background_fill);
                            this.user2info.setBackground(bbg);
                        }
                }
                System.out.println(" Other player/King.");
            }

            System.out.println("");
            
        }
        if(otherPlayers>0){
            // this.menuActions.setDisable(false);
        }



        //String message = String.format("%d;updatejoinedplayers=%s", clientNum, CommMessages.ClientActions.DONE.getName());
        /*  general client format:  %d;%d;%d;%s;%s;action=%s;%s
            [0] client number
            [1] player number
            [2] king number
            [3] playername
            [4] playercolor
            [5] action=.....
            [6] rest of data or subaction=
            String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions. .getName(), "updateSubseqRow");
        */
        String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions.DONE.getName(), "updatejoinedplayers");
        System.out.println("updatePlayersJoined:: send to server [" + message +"]");
        toServer.println(message);
 
        // continue listening to server ...
        listenToServer=true;
    }

    public void doPlaceKing(int playmode, int clientNum, int playerNum, int kingNum, String firstrow) {

        reupdateFirstRow( playmode, clientNum, playerNum, kingNum, firstrow);

        int forguiplayer =0;
        // stop listening to server ...
        listenToServer=false;

        kingSelected = -1;
        tileForKingSelected = null;
        NodeForKingSelected=null;
        NodeForSquare1Selected=null;
        NodeForSquare2Selected=null;

        gameStatus=CommMessages.GameStatus.KINGS_PLACEMENT;

        for(Player p : players){
            if( kingNum == p.kingNumber){
                forguiplayer = p.GuiPlayerNumber;
                String msg = String.format(" Player#%s - King#%d %s YOUR TURN. Click on a Free Tile from First row to place your King it, then Click on an empty Square to place the first square and then click on a second square to place the second square... ", p.playerName, p.kingNumber, p.playerColor);
                this.logtext.setText(msg);
                BackgroundFill background_fill = new BackgroundFill(p.PCOLOR, CornerRadii.EMPTY, Insets.EMPTY);
                Background bbg = new Background(background_fill);
                this.logtext.setBackground(bbg);
                if(p.GuiPlayerNumber==1){
                    this.pl2selcards.setDisable(true);
                    this.pl2board.setDisable(true);
                    this.pl1selcards.setDisable(false);
                    this.pl1board.setDisable(false);
                    for(Node node : this.pl1selcards.getChildren()){
                        if(GridPane.getRowIndex(node)==1) { // accept click only in second row
                            node.setOnMouseClicked(e->{
                                onmcliked_selfrowcell(e, 1, GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                                //node.removeEventHandler(arg0, arg1); .removeEventFilter(MouseEvent.MOUSE_CLICKED, this); // at the bottom
                            });
                        }
                        else {
                            node.setOnMouseClicked(e->{
                                e.consume();
                            });
                        }
                    }
                    for(Node node : this.pl1board.getChildren()){
                        node.setOnMouseClicked(e->{
                            onmcliked_selbrdcell(e, 1, GridPane.getColumnIndex(node), GridPane.getRowIndex(node), p);
                        });
                    }
                    kingSelected = p.kingNumber;

                } else {
                    this.pl2selcards.setDisable(false);
                    this.pl2board.setDisable(false);
                    this.pl1selcards.setDisable(true);
                    this.pl1board.setDisable(true); 
                    for(Node node : this.pl2selcards.getChildren()){
                        if(GridPane.getRowIndex(node)==1) { // accept click only in second row
                            node.setOnMouseClicked(e->{
                                onmcliked_selfrowcell(e, 2, GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                            });
                        }
                        else {
                            node.setOnMouseClicked(e->{
                                e.consume();
                            });
                        }
                    }
                    for(Node node : this.pl2board.getChildren()){
                        node.setOnMouseClicked(e->{
                            onmcliked_selbrdcell(e, 2, GridPane.getColumnIndex(node), GridPane.getRowIndex(node), p);
                        });
                    }
                    kingSelected = p.kingNumber;                  
                }
            }
        }
        
    }

    public void doSubseqPlaceKing(int playmode, int clientNum, int playerNum, int kingNum, String firstrow) {

        System.out.println(" === doSubseqPlaceKing:: firstrow [" + firstrow +"]");

        reupdateSubseqRow( playmode, clientNum, playerNum, kingNum, firstrow, false);

        int forguiplayer =0;
        // stop listening to server ...
        listenToServer=false;

        kingSelected = -1;
        
        tileForKingSelected = null;
        // NodeForKingSelected=null;  -- no cleared. it must retain its value
        NodeForSquare1Selected=null;
        NodeForSquare2Selected=null;
        this.boardSquare1[0] = -1; this.boardSquare1[1] = -1;
        this.boardSquare2[0] = -1; this.boardSquare2[1] =  -1;
    
        gameStatus=CommMessages.GameStatus.KINGS_PLACEMENT;

        for(Player p : players){
            if( kingNum == p.kingNumber){
                forguiplayer = p.GuiPlayerNumber;
                String msg = String.format(" Player#%s - King#%d %s YOUR TURN. Click on a Free Tile from First row to place your King it, then Click on an empty Square to place the first square and then click on a second square to place the second square... ", p.playerName, p.kingNumber, p.playerColor);
                this.logtext.setText(msg);
                BackgroundFill background_fill = new BackgroundFill(p.PCOLOR, CornerRadii.EMPTY, Insets.EMPTY);
                Background bbg = new Background(background_fill);
                this.logtext.setBackground(bbg);
                if(p.GuiPlayerNumber==1){
                    kingSelected = p.kingNumber;
                    this.pl2selcards.setDisable(true);
                    this.pl2board.setDisable(true);
                    this.pl1selcards.setDisable(false);
                    this.pl1board.setDisable(false);
                    for(Node node : this.pl1selcards.getChildren()){
                        if(GridPane.getRowIndex(node)==1) { // accept click only in second row
                            node.setOnMouseClicked(e->{
                                onmcliked_subseqselfrowcell(e, 1, GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                               //node.removeEventHandler(arg0, arg1); .removeEventFilter(MouseEvent.MOUSE_CLICKED, this); // at the bottom
                            });
                        }
                        else {
                            node.setOnMouseClicked(e->{
                                e.consume();
                            });
                        }                        
                    }
                    for(Node node : this.pl1board.getChildren()){
                            node.setOnMouseClicked(e->{
                                onmcliked_subseqselbrdcell(e, 1, GridPane.getColumnIndex(node), GridPane.getRowIndex(node), p);
                        });
                    }
                    

                } else {
                    kingSelected = p.kingNumber; 
                    this.pl2selcards.setDisable(false);
                    this.pl2board.setDisable(false);
                    this.pl1selcards.setDisable(true);
                    this.pl1board.setDisable(true); 
                    for(Node node : this.pl2selcards.getChildren()){
                        if(GridPane.getRowIndex(node)==1) { // accept click only in second row
                            node.setOnMouseClicked(e->{
                                onmcliked_subseqselfrowcell(e, 2, GridPane.getColumnIndex(node), GridPane.getRowIndex(node));
                            });
                        }
                        else {
                            node.setOnMouseClicked(e->{
                                e.consume();
                            });
                        }
                    }
                    for(Node node : this.pl2board.getChildren()){
                        node.setOnMouseClicked(e->{
                            onmcliked_subseqselbrdcell(e, 2, GridPane.getColumnIndex(node), GridPane.getRowIndex(node), p);
                        });
                    }
                                     
                }
            }
        }
        
    }

    void flipSquares(Player p, int guiplayerno){
        if(tileForKingSelected == null) return;
        System.out.printf("==> flipSquares:: gpln=%d, kgn=%d, guipl=%d, tile=[%s] ... %n",p.GuiPlayerNumber,p.kingNumber, guiplayerno, tileForKingSelected.toDebugString());
        GridPane gpboard = null;
        if(guiplayerno==1){
            gpboard = this.pl1board;
        }
        else {
            gpboard = this.pl2board;
        }
        System.out.printf("== flipSquares:: Original tileForKingSelected = %n");
        System.out.println(tileForKingSelected);

        Tile copyTile = new Tile(tileForKingSelected, true);
        
        tileForKingSelected = copyTile;
        
        System.out.printf("== flipSquares:: Reversed tileForKingSelected = %n");
        System.out.println(tileForKingSelected);

        System.out.println("------------------");
        System.out.println(NodeForSquare1Selected.getUserData());
        System.out.println("------------------");
        System.out.println(NodeForSquare2Selected.getUserData());
        System.out.println("------------------");

        int sq1row = ((Square) NodeForSquare1Selected.getUserData()).rowonBoard;
        int sq1col = ((Square) NodeForSquare1Selected.getUserData()).colonBoard;

        int sq2row = ((Square) NodeForSquare2Selected.getUserData()).rowonBoard;
        int sq2col = ((Square) NodeForSquare2Selected.getUserData()).colonBoard;

        tileForKingSelected.Square1.setBoardPosition(sq1row, sq1col);
        tileForKingSelected.Square2.setBoardPosition(sq2row, sq2col);

        HBox hbimg1 = setBrdCellSquare(sq1col,sq1row, tileForKingSelected.Square1, tileForKingSelected, guiplayerno);
        HBox hbimg2 = setBrdCellSquare(sq2col,sq2row, tileForKingSelected.Square2, tileForKingSelected, guiplayerno);

        gpboard.getChildren().removeAll(NodeForSquare1Selected);
        gpboard.getChildren().removeAll(NodeForSquare2Selected);

        NodeForSquare1Selected = (Node) hbimg1;
        NodeForSquare2Selected = (Node) hbimg2;

        gpboard.add(hbimg1, sq1col, sq1row);

        gpboard.add(hbimg2, sq2col, sq2row);

        System.out.printf("<== flipSquares:: gpln=%d, kgn=%d, guipl=%d END %n",p.GuiPlayerNumber,p.kingNumber, guiplayerno);

    }

    void placementDone(Player p, Tile tileForKingSelected){
         // TODO:: notify server we are finished placing king and 
        String message = String.format("%d;%d;%d;%s;%s;action=%s;%d", this.ClientNumber,  p.playerNumber, p.kingNumber, p.playerName, p.playerColor, CommMessages.ClientActions.TILE_PICKED.getName(), tileForKingSelected.aa);
                                    /*  general client format:  %d;%d;%d;%s;%s;action=%s;%s
                                    [0] client number
                                    [1] player number
                                    [2] king number
                                    [3] playername
                                    [4] playercolor
                                    [5] action=.....
                                    [6] rest of data or subaction=
                                    String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions. .getName(), "updateSubseqRow");
                                    */
        System.out.println("placementDone:: send to server [" + message +"]");
        toServer.println(message);
                                                            
        String msg = String.format(" placement sent, waiting for other players to place their kings...");
        this.logtext.setText(msg);
        //BackgroundFill background_fill = new BackgroundFill(p.PCOLOR, CornerRadii.EMPTY, Insets.EMPTY);

        // continue listening to server ...
        kingSelected = -1;
        // public Tile tileForKingSelected = null;
        // public Node NodeForSquare1Selected=null;
        // public Node NodeForSquare2Selected=null;
        // public Node NodeForKingSelected=null;   // 1 fora enimeronetai !!
        // private KingTile King1Tile =null;
        // private KingTile King2Tile =null;
        King1Placed = false;
        tile1Sqr1Placed = false;
        tile1Sqr2Placed = false;
        tile1Placed = false;
        King2Placed = false;
        tile2Sqr1Placed = false;
        tile2Sqr2Placed = false;
        tile2Placed = false;

        btnfipSquares.setDisable(true);
        btnplacementDone.setDisable(true);
        listenToServer=true;
    }

    void DiscardTile(Player p, int guiplayerno, Tile tileForKingSelected) {

        if(kingSelected<0) return;
        if( tileForKingSelected == null) return;

        GridPane gpselcards = null;
        if(guiplayerno==1){
            gpselcards = this.pl1selcards;
        }
        else {
            gpselcards = this.pl2selcards;
        }
        for(Node node : gpselcards.getChildren()){
            if(node != null) {
                HBox ntile= (HBox)node;
                Tile nuserdata = (Tile)ntile.getUserData();
                if(nuserdata !=null){
                    if( (nuserdata.row == tileForKingSelected.row) && (nuserdata.col == tileForKingSelected.col) && tileForKingSelected.kingNumber<0  ){
                        tileForKingSelected.setDiscarded(true);
                        Label l = new Label("=X=");
                        ntile.getChildren().add(l);
                    }
                }
            }
        }
    }


    private String CelltypeAndColor( int col, int row, GridPane gp){
        String retstr = "";
        String retype="";
        String retcolor="";
        
        Node node = getNodeByRowColumnIndex(row,col,gp);
        if(node==null) {
            return "empty";
        }
         HBox hb = (HBox) node;
         Object hbuserdata = hb.getUserData();
         //if (Objects.isNull(getUserObject) )
         if(hbuserdata == null) return "empty";
         else {
                 if(hbuserdata instanceof Tile) return "tile";
                 else if (hbuserdata instanceof Square) retype= "square";
                 else if (hbuserdata instanceof KingTile) retype= "king";
                 else if (hbuserdata.getClass() == Tile.class) return "tile";
                 else if (hbuserdata.getClass() == Square.class) retype= "square";
                 else if (hbuserdata.getClass() == KingTile.class) retype= "king";
                 else {                        
                    try{
                        Class hbudclass = hbuserdata.getClass();
                        String t = hbudclass.toString();
                        t=hbuserdata.toString();
                        if(t.contains("type=tile")) return "tile";
                        else if(t.contains("type=square")) retype= "square";
                        else if (t.contains("type=king"))retype= "king";
                        else return "empty";
                    }
                    catch (Exception e) {
                        return "empty";
                    }
                }

                if( retype == "square") {
                    retcolor= ((Square) hbuserdata).strColor;

                }
                else if( retype== "king") {
                    retcolor= ((KingTile) hbuserdata).castlecolor;
                }
                retstr = String.format("%s@%s",retype,retcolor);
                return retstr;
        }
        

    }

    private String Celltype( int col, int row, GridPane gp){
        Node node = getNodeByRowColumnIndex(row,col,gp);
        if(node==null) {
            return "empty";
        }
        HBox hb = (HBox) node;
        Object hbuserdata = hb.getUserData();
        //if (Objects.isNull(getUserObject) )
        if(hbuserdata == null) return "empty";
        else {
             if(hbuserdata instanceof Tile) return "tile";
             else if (hbuserdata instanceof Square) return "square";
             else if (hbuserdata instanceof KingTile) return "king";
             else if (hbuserdata.getClass() == Tile.class) return "tile";
             else if (hbuserdata.getClass() == Square.class) return "square";
             else if (hbuserdata.getClass() == KingTile.class) return "king";
             else {                        
                try{
                    Class hbudclass = hbuserdata.getClass();
                    String t = hbudclass.toString();
                    t=hbuserdata.toString();
                    if(t.contains("type=tile")) return "tile";
                    else if(t.contains("type=square")) return "square";
                    else if (t.contains("type=king"))return "king";
                    else return "empty";
                }
                catch (Exception e) {
                    return "empty";
                }
            }
        }        
    }

    int getTileNofromNode(Node node){
        String retype="";
        if(node==null) return -1;
        HBox hb = (HBox) node;
        if(hb.getUserData()==null) return -1;
        Object hbuserdata = hb.getUserData();
        //if (Objects.isNull(getUserObject) )
        if(hbuserdata == null) return -1;
        else {
                if(hbuserdata instanceof Tile) return  -1;
                else if (hbuserdata.getClass() == Tile.class) return  -1;
                else if (hbuserdata instanceof Square) retype= "square";
                else if (hbuserdata instanceof KingTile) return  -1;
                else if (hbuserdata.getClass() == Square.class) retype= "square";
                else if (hbuserdata.getClass() == KingTile.class) return  -1;
                else {                        
                   try{
                       Class hbudclass = hbuserdata.getClass();
                       String t = hbudclass.toString();
                       t=hbuserdata.toString();
                       if(t.contains("type=square")) retype= "square";
                       else if(t.contains("type=king") )  return  -1;
                        else  return  -1;
                   }
                   catch (Exception e) {
                    return  -1;
                   }
               }
               if( retype == "square") return ((Square)hbuserdata).tileAa;
               else  return  -1;
               
        }     
    }
    
    private int NodeSquareAA( int col, int row, GridPane gp){
        String rettype = "";
        Node node = getNodeByRowColumnIndex(row,col,gp);
        if(node==null) {
            return -1;
        }
        HBox hb = (HBox) node;
        Object hbuserdata = hb.getUserData();
        //if (Objects.isNull(getUserObject) )
        if(hbuserdata == null) return -1;
        else {
             if(hbuserdata instanceof Tile) return -1;
             else if (hbuserdata instanceof Square) rettype = "square";
             else if (hbuserdata instanceof KingTile) return -1;
             else if (hbuserdata.getClass() == Tile.class) return -1;
             else if (hbuserdata.getClass() == Square.class) rettype = "square";
             else if (hbuserdata.getClass() == KingTile.class) return -1;
             else {                        
                try{
                    Class hbudclass = hbuserdata.getClass();
                    String t = hbudclass.toString();
                    t=hbuserdata.toString();
                    if(t.contains("type=tile")) return  -1;
                    else if(t.contains("type=square")) rettype = "square";
                    else if (t.contains("type=king")) return -1;
                    else return -1;
                }
                catch (Exception e) {
                    return -1;
                }
            }
            return ((Square)hbuserdata).tileAa;
        }        
    }

    private int NodeDiffSquareAA( int col, int row, GridPane gp, int currentaa){
        String rettype = "";
        Node node = getNodeByRowColumnIndex(row,col,gp);
        if(node==null) {
            return -1;
        }
        HBox hb = (HBox) node;
        Object hbuserdata = hb.getUserData();
        //if (Objects.isNull(getUserObject) )
        if(hbuserdata == null) return -1;
        else {
             if(hbuserdata instanceof Tile) return -1;
             else if (hbuserdata instanceof Square) rettype = "square";
             else if (hbuserdata instanceof KingTile) return -1;
             else if (hbuserdata.getClass() == Tile.class) return -1;
             else if (hbuserdata.getClass() == Square.class) rettype = "square";
             else if (hbuserdata.getClass() == KingTile.class) return -1;
             else {                        
                try{
                    Class hbudclass = hbuserdata.getClass();
                    String t = hbudclass.toString();
                    t=hbuserdata.toString();
                    if(t.contains("type=tile")) return  -1;
                    else if(t.contains("type=square")) rettype = "square";
                    else if (t.contains("type=king")) return -1;
                    else return -1;
                }
                catch (Exception e) {
                    return -1;
                }
            }
            int checkaa = ((Square)hbuserdata).tileAa;
            if( currentaa != checkaa ) return checkaa; else return -1;
        }        
    }

    Square getSquareFromNode(Node node){
        String retype="";
        if(node==null) return null;
        HBox hb = (HBox) node;
        if(hb.getUserData()==null) return null;
        Object hbuserdata = hb.getUserData();
        //if (Objects.isNull(getUserObject) )
        if(hbuserdata == null) return null;
        else {
                if(hbuserdata instanceof Tile) return  null;
                else if (hbuserdata.getClass() == Tile.class) return  null;
                else if (hbuserdata instanceof Square) retype= "square";
                else if (hbuserdata instanceof KingTile) retype= "king";
                else if (hbuserdata.getClass() == Square.class) retype= "square";
                else if (hbuserdata.getClass() == KingTile.class) retype= "king";
                else {                        
                   try{
                       Class hbudclass = hbuserdata.getClass();
                       String t = hbudclass.toString();
                       t=hbuserdata.toString();
                       if(t.contains("type=square")) retype= "square";
                       else if(t.contains("type=king") ) retype= "king";
                        else return null;
                   }
                   catch (Exception e) {
                       return null;
                   }
               }
               if( retype == "square") return (Square)hbuserdata;
               else if( retype== "king") return (Square)hbuserdata;
               else return null;
        }     
    }

    public boolean HasNearBySquare( int row, int col, GridPane gp, int tileaa){
        boolean bret =false;
        if(col>0 && NodeDiffSquareAA(col-1,row,gp,tileaa)>0 ) {
                bret=true;
        }
        if(col<8 && NodeDiffSquareAA(col+1,row,gp,tileaa)>0 ){
            bret=true;
        }
        if(row>0 && NodeDiffSquareAA(col,row-1,gp,tileaa)>0 ) {
            bret=true;
        }
        if(row<8 && NodeDiffSquareAA(col,row+1,gp,tileaa)>0 ) {
            bret=true;
        }
        return bret;
    }

    public ArrayList<String> NearBySquareColors( int row, int col, GridPane gp, int tileaa){
        ArrayList<String> alclr = new ArrayList<String>();
        if(col>0 && NodeDiffSquareAA(col-1,row,gp,tileaa)>0 ) {
            String type_clr = CelltypeAndColor(col-1,row,gp);
            if(type_clr.contains("square")){
                String[] aclr = type_clr.split("@");
                String clr = aclr[1];
                alclr.add(clr);
            }
        }
        if(col<8 && NodeDiffSquareAA(col+1,row,gp,tileaa)>0 ){
            String type_clr = CelltypeAndColor(col+1,row,gp);
            if(type_clr.contains("square")){
                String[] aclr = type_clr.split("@");
                String clr = aclr[1];
                alclr.add(clr);
            }
        }
        if(row>0 && NodeDiffSquareAA(col,row-1,gp,tileaa)>0 ) {
            String type_clr = CelltypeAndColor(col,row-1,gp);
            if(type_clr.contains("square")){
                String[] aclr = type_clr.split("@");
                String clr = aclr[1];
                alclr.add(clr);
            }
        }
        if(row<8 && NodeDiffSquareAA(col,row+1,gp,tileaa)>0 ) {
            String type_clr = CelltypeAndColor(col,row+1,gp);
            if(type_clr.contains("square")){
                String[] aclr = type_clr.split("@");
                String clr = aclr[1];
                alclr.add(clr);
            };
        }  
        return alclr;
    }

    private boolean isValid1stCell( int col, int row, GridPane gp, String color, Square theSqr){
        boolean foundmatch = false;

        if( Celltype(col, row, gp).contains("empty")==false) return false;
        boolean okAbove=false;
        boolean okUnder=false;
        boolean okLeft=false;
        boolean okRight=false;
        if( (col>0 && Celltype(col-1, row, gp).contains("empty")==true) && (col<8 && Celltype(col+1, row, gp).contains("empty")==true)  && (row>0 && Celltype(col, row-1, gp).contains("empty")==true) && (row<8 && Celltype(col, row+1, gp).contains("empty")==true) ) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Bad First Square Choice");
            alert.setHeaderText("Bad First Square Choice");
            alert.setContentText("Bad First Square Choice. 1st Square must touch existing square of same color"); 
            alert.showAndWait(); 
            return false;
        }        
        if(col>0 && Celltype(col-1, row, gp).contains("empty")==false){
            if( Celltype(col-1, row, gp).contains("square")==true){
            
                Node node = getNodeByRowColumnIndex(row,col,gp);
                if(node==null) {
                    return false;
                }
            }
        }


        
        return true;

    }
 
    /* click on a First tile to select the tile for the king */
    private void onmcliked_selfrowcell(Event e, int guiplayerno, int col, int row) {

        System.out.printf("==> onmousecliked_selfrowcell:: guiplayerno=%d, col=%d, row=%d %n",guiplayerno,col,row);

        e.consume();

        if( (guiplayerno==1 && King1Placed==false) || (guiplayerno==2 && King2Placed==false) ) {
            if(kingSelected<0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing King");
                alert.setHeaderText("King has not yet been selected");
                alert.setContentText("Please ensure that you are using the right King");
                alert.showAndWait();            
            }
            else if( tileForKingSelected == null) {
                GridPane gpselcards = null;
                if(guiplayerno==1){
                    gpselcards = this.pl1selcards;
                }
                else {
                    gpselcards = this.pl2selcards;
                }
                HBox ntile =  (HBox) getNodeByRowColumnIndex(row,col, gpselcards);
                tileForKingSelected = (Tile) ntile.getUserData();
                if(tileForKingSelected.kingNumber >-1) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Other King has placed on this tile");
                    alert.setHeaderText("Other King has placed on this tile");
                    alert.setContentText("Other King has placed on this tile, try another tile");
                    alert.showAndWait();
                    tileForKingSelected = null;
                } else {
                    tileForKingSelected.kingNumber=kingSelected;         
                    ntile.getChildren().add(new Label("[V]"));
                    String msg =this.logtext.getText(); 
                    msg += " Now select an empty board square to place the king and the tile...";
                    this.logtext.setText(msg);
                    gameStatus=CommMessages.GameStatus.SELECT_TILE;
                }
            }
            System.out.printf(" = onmousecliked_selfrowcell:: guiplayerno=%d, col=%d, row=%d tileForKingSelected OK = %s %n",guiplayerno,col,row, tileForKingSelected.toDebugString());
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("King Allready Placed");
            alert.setHeaderText("King Allready Placed");
            alert.setContentText("King Allready Placed");
            alert.showAndWait();
        }
        System.out.printf("<== onmousecliked_selfrowcell:: guiplayerno=%d, col=%d, row=%d END %n",guiplayerno,col,row);
    }

    /* click on empty square board (for first tile):
     * first click: place king <= tileForKingSelected
     * second click: place square 1 of tileForKingSelected
     * third click: place square 2 of tileForKingSelected
     */
    private void onmcliked_selbrdcell(Event e, int guiplayerno, int col, int row, Player p) {

        System.out.printf("=> onmcliked_selbrdcell, guiplayerno=%d, col=%d, row=%d %n",guiplayerno,col,row);

        e.consume();

        if( (guiplayerno==1 && King1Placed==false) || (guiplayerno==2 && King2Placed==false) ) {

            System.out.printf("= ! onmcliked_selbrdcell, No king placed continue ... %n");

            if(kingSelected<0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing King");
                alert.setHeaderText("King has not yet been selected");
                alert.setContentText("Please ensure that you are using the right King"); 
                alert.showAndWait();           
            }
            else if( tileForKingSelected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing Selected Tile");
                alert.setHeaderText("Tile from first row has not yet been selected");
                alert.setContentText("Please ensure that you have selected an available tile from first row");
                alert.showAndWait();
            }
            else {
               
                /* public int kingSelected = -1;
                public Tile tileForKingSelected = null;
                public Node NodeForKingSelected=null;
                public Node NodeForTileSelected=null;
                public boolean tilePlaced = false; */


                
                /* first click: place king <= tileForKingSelected */
                if(NodeForKingSelected==null) {

                    System.out.printf("-- onmcliked_selbrdcell, guiplayerno=%d, col=%d, row=%d selecting King Square... %n",guiplayerno,col,row);

                    GridPane gpboard = null;
                    if(guiplayerno==1){
                        gpboard = this.pl1board;
                    }
                    else {
                        gpboard = this.pl2board;
                    }

                    NodeForKingSelected=SetKingTileToBrdGridPane(guiplayerno, gpboard, col, row);                

                    System.out.printf("-- onmcliked_selbrdcell, guiplayerno=%d, col=%d, row=%d King Square selected %n",guiplayerno,col,row);

                }

                /* second click: place square 1 of tileForKingSelected */
                else if(NodeForKingSelected !=null && NodeForSquare1Selected==null )
                {
                    System.out.printf("-- onmcliked_selbrdcell, guiplayerno=%d, col=%d, row=%d Selecting Square 1... %n",guiplayerno,col,row);

                    GridPane gpboard = null;
                    if(guiplayerno==1){
                        gpboard = this.pl1board;
                    }
                    else {
                        gpboard = this.pl2board;
                    }

                    /* check if it is with king's square */
                    int kingRow = GridPane.getRowIndex(NodeForKingSelected) ;
                    int kingCol = GridPane.getColumnIndex(NodeForKingSelected);
                    if( (kingRow == row && (col==kingCol-1 || col==kingCol+1)) || (kingCol == col && (row==kingRow-1 || row==kingRow+1)) ){
                        NodeForSquare1Selected = SetSquareToBrdGridPane( guiplayerno, gpboard, col, row, tileForKingSelected, 1); // SetTileToGridPane(guiplayerno, gpboard, col, row, tileForKingSelected);                   
                        System.out.printf("-- onmcliked_selbrdcell, guiplayerno=%d, col=%d, row=%d selected square 1 %n",guiplayerno,col,row);   
                        
                        lastTileCoord[0] = row;
                        lastTileCoord[1] = col;
                        lastTileCoord[2] = -1;
                        lastTileCoord[3] = -1;
                    }
                    else {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Bad First Square Choice ");
                        alert.setHeaderText("Bad First Square Choice");
                        alert.setContentText("Bad First Square Choice. It must touch King Square !");
                        alert.showAndWait();   
                    }

                }
                else if(NodeForKingSelected !=null && NodeForSquare1Selected !=null && NodeForSquare2Selected==null ) {

                    System.out.printf("-- onmcliked_selbrdcell, guiplayerno=%d, col=%d, row=%d Selecting square 2... %n",guiplayerno,col,row);
                    
                    GridPane gpboard = null;
                    if(guiplayerno==1){
                        gpboard = this.pl1board;
                    }
                    else {
                        gpboard = this.pl2board;
                    }


                    int kingRow = GridPane.getRowIndex(NodeForKingSelected) ;
                    int kingCol = GridPane.getColumnIndex(NodeForKingSelected);
                    int sqr1Row = GridPane.getRowIndex(NodeForSquare1Selected) ;
                    int sqr1Col = GridPane.getColumnIndex(NodeForSquare1Selected);

                    if( (col==kingCol && row == kingRow) || (col==sqr1Col && row == sqr1Row) ) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Bad Second Square Choice ");
                        alert.setHeaderText("Bad Second Square Choice");
                        alert.setContentText("Bad Second Square Choice. It must be in empty Square !");
                        alert.showAndWait();
                    }
                    else if( (sqr1Row == row && (col==sqr1Col-1 || col==sqr1Col+1)) || (sqr1Col == col && (row==sqr1Row-1 || row==sqr1Row+1)) ) {

                        NodeForSquare2Selected = SetSquareToBrdGridPane( guiplayerno, gpboard, col, row, tileForKingSelected, 2);

                        System.out.printf("-- onmcliked_selbrdcell, guiplayerno=%d, col=%d, row=%d selected square 2 %n",guiplayerno,col,row);

                        /* end of king and tile placement, notify server */
                        if(NodeForKingSelected != null && NodeForSquare1Selected != null && NodeForSquare2Selected != null ) {
                            if(guiplayerno==1) {
                                King1Placed = true;
                                tile1Placed = true;
                            }
                            else if(guiplayerno==2) {
                                King2Placed = true;
                                tile2Placed = true;
                            }

                            //lastTileCoord[0] = row;
                            //lastTileCoord[1] = col;
                            lastTileCoord[2] = row;
                            lastTileCoord[3] = col;

                            btnfipSquares.setDisable(false);
                            btnplacementDone.setDisable(false);
                            btnfipSquares.setOnMouseClicked( ev ->{
                                flipSquares(p, guiplayerno); 
                            });
                            btnplacementDone.setOnMouseClicked( ev ->{
                                placementDone(p, tileForKingSelected);
                            });


                            /* moved to placementDone button hanlder ....
                            // TODO:: notify server we are finished placing king and 
                            String message = String.format("%d;%d;%d;%s;%s;action=%s;%d", this.ClientNumber,  p.playerNumber, p.kingNumber, p.playerName, p.playerColor, CommMessages.ClientActions.TILE_PICKED.getName(), tileForKingSelected.aa);

                            System.out.println("updatePlayersJoined:: send to server [" + message +"]");
                            toServer.println(message);
                                                            
                            String msg = String.format(" waiting for other players to place their kings...");
                            this.logtext.setText(msg);
                            //BackgroundFill background_fill = new BackgroundFill(p.PCOLOR, CornerRadii.EMPTY, Insets.EMPTY);

                            // continue listening to server ...
                            listenToServer=true;
                            */

                    }
                    else {

                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Bad Second Square Choice ");
                        alert.setHeaderText("Bad Second Square Choice");
                        alert.setContentText("Bad Second Square Choice. It must touch First Square !");
                        alert.showAndWait();
                        }
                    }
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Missing Choices ");
                    alert.setHeaderText("Missing Choices");
                    alert.setContentText("Missing Choices");
                    alert.showAndWait();                    
                }
            }
         }
         else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("King Allready Placed");
                alert.setHeaderText("King Allready Placed");
                alert.setContentText("King Allready Placed");
                alert.showAndWait();
        }
        System.out.printf("<== onmcliked_selbrdcell, guiplayerno=%d, col=%d, row=%d End %n",guiplayerno,col,row);
    }

   
    /* click on a subsequent tile to select the tile for the king */
    private void onmcliked_subseqselfrowcell(Event e, int guiplayerno, int col, int row) {

        System.out.printf("==> onmcliked_subseqselfrowcell, guiplayerno=%d, col=%d, row=%d %n",guiplayerno,col,row);

        e.consume();

        if( (guiplayerno==1 && King1Placed==false) || (guiplayerno==2 && King2Placed==false) ) {
            if(kingSelected<0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing King");
                alert.setHeaderText("King has not yet been selected");
                alert.setContentText("Please ensure that you are using the right King");
                alert.showAndWait();            
            }
            else if( tileForKingSelected == null) {
                GridPane gpselcards = null;
                if(guiplayerno==1){
                    gpselcards = this.pl1selcards;
                }
                else {
                    gpselcards = this.pl2selcards;
                }
                HBox ntile =  (HBox) getNodeByRowColumnIndex(row,col, gpselcards);
                tileForKingSelected = (Tile) ntile.getUserData();
                if(tileForKingSelected.kingNumber >-1) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Other King has placed on this tile");
                    alert.setHeaderText("Other King has placed on this tile");
                    alert.setContentText("Other King has placed on this tile, try another tile");
                    alert.showAndWait();
                    tileForKingSelected = null;
                } else {
                    this.boardSquare1[0] = -1; this.boardSquare1[1] = -1;
                    this.boardSquare2[0] = -1; this.boardSquare2[1] =  -1;
                    tileForKingSelected.kingNumber=kingSelected;           
                    ntile.getChildren().add(new Label("[V]"));
                    String msg =this.logtext.getText(); 
                    msg += " Now select an empty board square to place the king and the tile...";
                    this.logtext.setText(msg);
                    gameStatus=CommMessages.GameStatus.SELECT_TILE;
                }
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("King Allready Placed");
            alert.setHeaderText("King Allready Placed");
            alert.setContentText("King Allready Placed");
            alert.showAndWait();
        }
        System.out.printf("<== onmcliked_subseqselfrowcell, guiplayerno=%d, col=%d, row=%d %n",guiplayerno,col,row);
    }
    
    /*
     * click on empty square board (for subsequent tiles):
     * first click: place king <= tileForKingSelected
     * second click: place square 1 of tileForKingSelected
     * third click: place square 2 of tileForKingSelected
     */
    private void onmcliked_subseqselbrdcell(Event e, int guiplayerno, int col, int row, Player p) {

         System.out.printf("==> onmcliked_subseqselbrdcell, guiplayerno=%d, col=%d, row=%d %n",guiplayerno,col,row);

         e.consume();
 
        GridPane gpboard = null;
        if(guiplayerno==1){
            gpboard = this.pl1board;
        }
        else {
            gpboard = this.pl2board;
        }

        if( (guiplayerno==1 && King1Placed==true) || (guiplayerno==2 && King2Placed==true) ) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Your King has already been Placed !?");
            alert.setHeaderText("Your King has already been Placed !?");
            alert.setContentText("Your King has already been Placed !?");
            alert.showAndWait();
            
            return;
        }
 
        if(kingSelected<0) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing King");
                alert.setHeaderText("King has not yet been selected");
                alert.setContentText("Please ensure that you are using the right King"); 
                alert.showAndWait();   
                return;        
        }
        if( tileForKingSelected == null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing Selected Tile");
                alert.setHeaderText("Tile from first row has not yet been selected");
                alert.setContentText("Please ensure that you have selected an available tile from first row");
                alert.showAndWait();
                return;
        }

        if( Celltype(col, row, gpboard) != "empty") {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Bad  Square Choice ");
            alert.setHeaderText("Bad  Square Choice");
            alert.setContentText("Bad  Square Choice. It must be placed on an empty board square !");
            alert.showAndWait();          
            return;
        }

        if( this.boardSquare1[0] == -1 ) {
            this.boardSquare1[0] = row;
            this.boardSquare1[1] = col;
            System.out.printf("<== onmcliked_subseqselbrdcell, guiplayerno=%d, col=%d, row=%d boardSuare1( %d, %d )%n",guiplayerno,col,row, this.boardSquare1[0], this.boardSquare1[1]);
            return ;
        }
        if( this.boardSquare1[0] > -1 && this.boardSquare2[0] == -1 ) {
            this.boardSquare2[0] = row;
            this.boardSquare2[1] = col;
        }
        System.out.printf("== onmcliked_subseqselbrdcell, guiplayerno=%d, col=%d, row=%d boardSuare2( %d, %d )%n",guiplayerno,col,row,this.boardSquare2[0], this.boardSquare2[1]);

        boolean validPlacement = false;

            //lastTileCoord[0] = row;
            //lastTileCoord[1] = col;
            //lastTileCoord[2] = row;
            //lastTileCoord[3] = col;
            

        if(HasNearBySquare(this.boardSquare1[0],this.boardSquare1[1],gpboard, tileForKingSelected.aa) || HasNearBySquare(this.boardSquare2[0],this.boardSquare2[1],gpboard, tileForKingSelected.aa) ){            
            // exei diplano square apo allo tile kapoio apo ta dyo


            System.out.printf("== onmcliked_subseqselbrdcell, guiplayerno=%d, col=%d, row=%d chacking matching of colors %s, %s ... %n",guiplayerno,col,row,tileForKingSelected.Square1.strColor, tileForKingSelected.Square2.strColor);

            ArrayList<String> nrbclr1 = NearBySquareColors(this.boardSquare1[0],this.boardSquare1[1],gpboard, tileForKingSelected.aa);
            ArrayList<String> nrbclr2 = NearBySquareColors(this.boardSquare2[0],this.boardSquare2[1],gpboard, tileForKingSelected.aa);
            if(nrbclr1.size()>0){
                for(String clr : nrbclr1){
                    if( (clr.contains(tileForKingSelected.Square1.strColor)) || (clr.contains(tileForKingSelected.Square2.strColor)) ) validPlacement = true;
                }
            }
            if(nrbclr2.size()>0){
                for(String clr : nrbclr2){
                    if( (clr.contains(tileForKingSelected.Square1.strColor)) || (clr.contains(tileForKingSelected.Square2.strColor)) ) validPlacement = true;
                }
            }
            if (validPlacement==false){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Bad Square Choice ");
                alert.setHeaderText("Bad Square Choice");
                alert.setContentText("Bad Square Choice. It does not have a near by placed tile with matching color  !");
                alert.showAndWait(); 
                this.boardSquare1[0] = -1;
                this.boardSquare1[1] = -1;
                this.boardSquare2[0] = -1;
                this.boardSquare2[1] = -1;
                return;
            }
        } 
        else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Bad Square Choice ");
            alert.setHeaderText("Bad Square Choice");
            alert.setContentText("Bad Square Choice. It does not have a near by placed tile  !");
            alert.showAndWait();                       
            this.boardSquare1[0] = -1;
            this.boardSquare1[1] = -1;
            this.boardSquare2[0] = -1;
            this.boardSquare2[1] = -1;
            return;
        }

        // near by tile exists with mathing colors ...
        NodeForSquare1Selected = SetSquareToBrdGridPane( guiplayerno, gpboard, this.boardSquare1[1], this.boardSquare1[0], tileForKingSelected, 1);
        NodeForSquare2Selected = SetSquareToBrdGridPane( guiplayerno, gpboard, this.boardSquare2[1], this.boardSquare2[0], tileForKingSelected, 2);

        if(validPlacement == true) {
            btnfipSquares.setDisable(false);
            btnplacementDone.setDisable(false);
            btnDiscardTile.setDisable(false);
            btnfipSquares.setOnMouseClicked( ev ->{
                ev.consume();
                flipSquares(p, guiplayerno); 
            });
            btnplacementDone.setOnMouseClicked( ev ->{
                ev.consume();
                placementDone(p, tileForKingSelected);
            });
            btnDiscardTile.setOnMouseClicked( ev ->{
                ev.consume();
                DiscardTile(p, guiplayerno, tileForKingSelected);
            });
        }

        /* boolean validPlacement = false;
           System.out.printf(" -- onmcliked_subseqselbrdcell, guiplayerno=%d, col=%d, row=%d Selecting Square 1 ... %n",guiplayerno,col,row);                     
                    if(row>0){  // if exists row over 
                        String celltypencolor = CelltypeAndColor(col, row-1, gpboard);
                        if( celltypencolor.contains("square" )  && celltypencolor.contains( sqr1.strColor)){                            
                            // above square has same color as the target square
                            // if right or left is empty, can be placed ...                            
                            if( ( col>0 && Celltype(col-1, row, gpboard) == "empty") || ( col<8 && Celltype(col+1, row, gpboard) == "empty") ) {
                                validPlacement = true;
                            }
                        }
                    }

                    if(row<8) { // if exists row bellow 
                        String celltypencolor = CelltypeAndColor(col, row+1, gpboard);
                        if( celltypencolor.contains("square" ) && sqrfor!=null && celltypencolor.contains( sqrfor.strColor)){
                            // below square has same color as the target square
                            // if right or left is empty, can be placed ...                            
                            if( ( col>0 && Celltype(col-1, row, gpboard) == "empty") || ( row>0 && Celltype(col+1, row, gpboard) == "empty") ) {
                                validPlacement = true;
                            }
                        }
                    }
                

                        //Node checkNode = getNodeByRowColumnIndex(row,col,gpboard);


                            btnfipSquares.setDisable(false);
                            btnplacementDone.setDisable(false);
                            btnfipSquares.setOnMouseClicked( ev ->{
                                flipSquares(p, guiplayerno); 
                            });
                            btnplacementDone.setOnMouseClicked( ev ->{
                                placementDone(p, tileForKingSelected);
                            });


           */
   
    }


    //private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
            Node result = null;
            ObservableList<Node> childrens = gridPane.getChildren();        
            for (Node node : childrens) {
                if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                    result = node;
                    break;
                }
            }        
            return result;
    }
    
    private void DelNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (gridPane.getColumnIndex(node) == col && gridPane.getRowIndex(node) == row) {
                gridPane.getChildren().removeAll(node);                
            }
        }
    }

    /* sets the Tile (2 squares to the selection rows top ) */
    private HBox setSelCellTile(int col, int row, Tile tile, int guiplayerno) {
        System.out.printf("==> setCellTile tile#=%d, kingnum=%d, color=%s, Square1=%s #%d, Square2=%s #%d ... %n", tile.aa, tile.kingNumber, tile.playercolor,
                tile.Square1.Square, tile.Square1.whichSqr ,  tile.Square2.Square, tile.Square2.whichSqr);

            tile.setTileRowCol1(row, col);

            String sqr1imagename = String.format("%s.png", tile.Square1.Square);
            String sqr2imagename = String.format("%s.png", tile.Square2.Square);
            try {

                FileInputStream png1input = new FileInputStream(sqr1imagename);
                Image sqr1img = new Image(png1input);
                ImageView sqr1imageView = new ImageView(sqr1img);
                sqr1imageView.setFitHeight(imgHeight);
                sqr1imageView.setFitWidth(imgWidth);

                FileInputStream png2input = new FileInputStream(sqr2imagename);
                Image sqr2img = new Image(png2input);
                ImageView sqr2imageView = new ImageView(sqr2img);
                sqr2imageView.setFitHeight(imgHeight);
                sqr2imageView.setFitWidth(imgWidth);   

                /*sqr1imageView.setOnMouseClicked(e -> {
                            System.out.printf("Mouse clicked cell [%d, %d] player %d %n", col, row, guiplayerno);
                            e.consume();
                        });
    
                        Button btn1mg = new Button();
                        btn1mg.setGraphic(sqr1imageView);
                        btn1mg.setOnAction(e -> {
                            System.out.printf("Mouse enetered cell [%d, %d]%n", col, row);
                            e.consume();
                        });
                        coverImage.setOnMouseClicked((MouseEvent event) -> {
                            System.out.println("Tile pressed " + book.getTitle());
                            event.consume();
                        });
                */
                String msg = String.format("(%d)", tile.aa);
                if( tile.kingNumber>-1 ){
                    msg += String.format("[king=%s]", tile.playercolor);
                } else {
                    msg += "[free]";
                }
                Label l = new Label(msg);
                HBox hbimgs = new HBox();
                hbimgs.getChildren().addAll(sqr1imageView,sqr2imageView,l);
                hbimgs.setBorder(new Border(new BorderStroke(Color.RED, 
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );
                
                hbimgs.setUserData(tile);
    
                System.out.println(String.format("<== setCellTile tile#=%d, kingnum=%d, color=%s, Square1=%s #%d, img1=%s, Square2=%s #%d img2=%s", tile.aa, tile.kingNumber, tile.playercolor,
                tile.Square1.Square, tile.Square1.whichSqr, sqr1imagename  ,  tile.Square2.Square, tile.Square2.whichSqr, sqr2imagename));
    
                return hbimgs;
            }
            catch (Exception e) {
                System.out.println(String.format("<== setCellTile tile#=%d, kingnum=%d, color=%s, Square1=%s #%d, img1=%s, Square2=%s #%d img2=%s EXCEPTION !", tile.aa, tile.kingNumber, tile.playercolor,
                tile.Square1.Square, tile.Square1.whichSqr, sqr1imagename  ,  tile.Square2.Square, tile.Square2.whichSqr, sqr2imagename));
                return null;
            }
        } 
    
    /* sets the Tile (2 squares to the selection rows top ) */
    private Node SetTileToSelGridPane(int guiplayerno, GridPane gridPane, int col, int row, Tile tile) {
        System.out.printf("==> SetTileToSelGridPane player %d col=%d, row=%d, tile=%s ... %n ", guiplayerno, col, row, tile.toDebugString());
    
                for (Node node : gridPane.getChildren()) {
                    if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
    
                        System.out.printf(" == SetTileToGridPane found Node ... %n");
                        HBox cell = (HBox) node;
                        gridPane.getChildren().removeAll(node);
                        HBox hbimgs = setSelCellTile(col, row, tile, guiplayerno);
                        gridPane.add(hbimgs, col, row);
                        System.out.println(String.format(" == SetTileToGridPane:: added tile #%d, %s #%d - %s #%d",tile.aa,tile.Square1.Square,tile.Square1.whichSqr,tile.Square2.Square,tile.Square2.whichSqr));
                        System.out.printf("<== SetTileToSelGridPane player %d col=%d, row=%d End %n ", guiplayerno, col, row);
                        return node;
                    }
                }
                System.out.printf("<== SetTileToSelGridPane player %d col=%d, row=%d End  NULL !! %n ", guiplayerno, col, row);
                return null;
        }
   
    private HBox setBrdKingTile(int guiplayerno, int row, int col) {        
        HBox hbimgs = null;
        if(guiplayerno==1) {
            King1Tile.Square1.setBoardPosition(row, col);
            hbimgs = new HBox(King1Tile.castleImageView);
            hbimgs.setUserData(King1Tile);
        }
        else if(guiplayerno==2) {
            King2Tile.Square1.setBoardPosition(row, col);
            hbimgs = new HBox(King2Tile.castleImageView);
            hbimgs.setUserData(King2Tile);
        }
        return hbimgs;
    } 

    private Node SetKingTileToBrdGridPane(int guiplayerno, GridPane gridPane, int col, int row){
             for (Node node : gridPane.getChildren()) {
                if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {

                    HBox cell = (HBox) node;
                    if( cell.getUserData() != null && (cell.getUserData() instanceof Tile || cell.getUserData() instanceof KingTile)  ){
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Occupied Cell");
                        alert.setHeaderText("This cell has tile");
                        alert.setContentText("This cell has tile, Please select an empty cell");
                        alert.showAndWait();
                        return null;
                    }
                    
                    gridPane.getChildren().removeAll(node);

                    HBox hbimgs = setBrdKingTile(guiplayerno,row,col);

                    gridPane.add(hbimgs, col, row);

                    // temporary variable ...
                    tileForKingSelected.Square1.setBoardPosition(row,col);
                  
                    return (Node)hbimgs;
                    
                }
            }
            return null;
    }

 
    /* sets a square of a tile in borad */
    private HBox setBrdCellSquare(int col, int row, Square sqr, Tile tile, int guiplayerno){

        System.out.printf(" == setCellSquare:: pln=%d col=%d row=%d whichsqr=%d, tile aa=%d %n",guiplayerno,col,row, sqr.whichSqr, tile.aa);

        int tileNo = sqr.tileAa;
        String sqrName = "";
        Color sqrColor = null;
        int sqrCrowns = 0;
        String sqrimagename = "";
        Square square=null;
        
        sqrName = sqr.Square;
        sqrColor = sqr.color;
        sqrCrowns = sqr.crowns;
        sqrimagename = String.format("%s.png", sqr.Square);
        square = sqr;
        
        
        System.out.printf(" == setCellSquare:: pln=%d col=%d row=%d whichsqr=%d, tile aa=%d, sqr=%s, sqrimagename=%s ... %n",guiplayerno,col,row, sqr.whichSqr, tile.aa,sqrName,sqrimagename);
        try {
            FileInputStream pnginput = new FileInputStream(sqrimagename);
            Image sqrimg = new Image(pnginput);
            ImageView sqrimageView = new ImageView(sqrimg);
            sqrimageView.setFitHeight(imgHeight);
            sqrimageView.setFitWidth(imgWidth);
            HBox hbimg = new HBox(sqrimageView);
            //hbimg.getChildren().addAll(sqrimageView);
            hbimg.setBorder(new Border(new BorderStroke(Color.RED, 
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );

            System.out.printf(" == setCellSquare:: creating new square from tile #%d ... %n",sqr.whichSqr);
            
            square.setBoardPosition(row, col);

            hbimg.setUserData(square);

            System.out.println(String.format(" ==> setCellSquare placed tile#=%d, kingnum=%d, color=%sm square=%s . %n", sqr.tileAa, tile.kingNumber, tile.playercolor, sqrName));

            /*hbimg.setOnMouseClicked(e -> {
                System.out.printf("Mouse clicked cell [%d, %d] player %d %n", col, row, guiplayerno);
                e.consume();
            });*/

            return hbimg;
        }
        catch (Exception e) {
                return null;
        }
    }
    
    /* sets a square of a tile in borad */
    private Node SetSquareToBrdGridPane(int guiplayerno, GridPane gridPane, int col, int row, Tile tile, int whichsqr) {

        System.out.printf("==> SetSquareToGridPane:: pln=%d col=%d row=%d whichsqr=%d ... %n",guiplayerno,col,row, whichsqr);

        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                System.out.printf(" == SetSquareToBrdGridPane:: found Node %n");
                // HBox cell = (HBox) node;
                /*if( cell.getUserData() != null && (cell.getUserData() instanceof Tile || cell.getUserData() instanceof KingTile || cell.getUserData() instanceof Square )  ){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Occupied Cell");
                    alert.setHeaderText("This cell has tile");
                    alert.setContentText("This cell has tile, Please select an empty cell");
                    alert.showAndWait();
                    return null;
                }*/
                gridPane.getChildren().removeAll(node);
                HBox hbimg = null;
                if(whichsqr==1) hbimg = setBrdCellSquare(col, row, tile.Square1, tile, guiplayerno);
                else hbimg = setBrdCellSquare(col, row, tile.Square2, tile, guiplayerno);
                gridPane.add(hbimg, col, row);
                Square sq = (Square)hbimg.getUserData();
                System.out.println(String.format("<== SetSquareToGridPane:: added tile # %d , Square # %d , %s-%s %d %n",tile.aa,whichsqr,sq.Square,sq.strColor,sq.whichSqr));
                //return node;
                return (Node) hbimg;
            }
        }
        System.out.println(String.format("<== SetSquareToGridPane:: NULL !! tile # %d , Square # %d %n",tile.aa,whichsqr));
        return null; 
    }

    public void reupdateSubseqRow( int playmode, int clientNum, int playerNum, int kingNum, String firstrow, boolean updateboth){ 

        System.out.println(" === reupdateSubseqRow:: updateboth=" + Boolean.toString(updateboth) +", firstrow [" + firstrow +"]");

        int forguiplayer =0;

        for(Player p : players){

            if( kingNum == p.kingNumber){

                forguiplayer = p.GuiPlayerNumber;
                String[] arrow = firstrow.split("=");
                String[] artiles = arrow[1].split("@");
                int maxtiles=4;
                if(playmode==3) maxtiles=3; 
         
                if(updateboth==true){
                    if(p.GuiPlayerNumber==1){
                        player1DrawLineOne = new ArrayList<Tile>();
                        if( player1DrawLineTwo.size()>0) {
                            int listsize = player1DrawLineTwo.size();
                            for(int i=0;i<listsize;i++) player1DrawLineOne.add(player1DrawLineTwo.remove(0));
                        }
                    }
                    else {
                        player2DrawLineOne = new ArrayList<Tile>();
                        if( player2DrawLineTwo.size()>0) {
                            int listsize = player2DrawLineTwo.size();
                            for(int i=0;i<listsize;i++) player2DrawLineOne.add(player2DrawLineTwo.remove(0));
                        }

                    }
                }
                for(int i=0;i<maxtiles;i++){                        
                    String thetile=artiles[i];
                    String[] tileinfo = thetile.split(";");

                    Tile t = new Tile(Integer.parseInt(tileinfo[0]), Integer.parseInt(tileinfo[3]), Integer.parseInt(tileinfo[6]), tileinfo[1], tileinfo[4]  ) ;
                    t.setKingInfo(Integer.parseInt(tileinfo[8]), tileinfo[7], Integer.parseInt(tileinfo[9]), Integer.parseInt(tileinfo[10]) );
                    
                    System.out.println(" === reupdateFirstRow :: Tile = "+ t.toDebugString() );

                    System.out.println(String.format(" === reupdateFirstRow :: setting tile#=%d, kingnum=%d, color=%s, square1=%s, square2=%s", t.aa, Integer.parseInt(tileinfo[8]), tileinfo[7], tileinfo[1], tileinfo[4]));
                    
                    if(p.GuiPlayerNumber==1) player1DrawLineTwo.add(t);
                    else player2DrawLineTwo.add(t); 
  
                }
                updateUIFirstRow(p.GuiPlayerNumber, maxtiles);
            }
        }
    }

    public void reupdateFirstRow( int playmode, int clientNum, int playerNum, int kingNum, String firstrow){        
        int forguiplayer =0;
        for(Player p : players){
            if( kingNum == p.kingNumber){
                forguiplayer = p.GuiPlayerNumber;
                String[] arrow = firstrow.split("=");
                String[] artiles = arrow[1].split("@");
                int maxtiles=4;
                if(playmode==3) maxtiles=3; 
                // firstrow= t.aa, t.Square1,t.color1,t.crowns1, t.Square2,t.color2,t.crowns2,t.playercolor,t.kingNumber,t.playernumber,t.clientnumber                          
                //             0    1         2         3          4         5         6        7             8            9              10
                // playmode=2&client=1&player=1&king=1&action=placeking&
                // firstrow=17;forest;0x808080ff;0;lake;0x0000ffff;0;red;0;0;0  @25;forest1c;0x808080ff;1;farm;0xffff00ff;0;;-1;-1;-1@42;lake;0x0000ffff;0;field2c;0x008000ff;2;;-1;-1;-1@47;land;0xd3d3d3ff;0;rocks2c;0xa9a9a9ff;2;;-1;-1;-1
                //          0    1     3         3  4      5       6 7   8 9 10
                player1DrawLineOne = new ArrayList<Tile>();
                player2DrawLineOne = new ArrayList<Tile>();
                player1DrawLineTwo = new ArrayList<Tile>();
                player2DrawLineTwo = new ArrayList<Tile>();
                for(int i=0;i<maxtiles;i++){                        
                    String thetile=artiles[i];
                    String[] tileinfo = thetile.split(";");
                    /*  firstrow=26;forest1c;0x808080ff;1;farm;0xffff00ff;0
                                 0 ; `      ; 2        ;3;4   ; 5        ;5
                     */
                    Tile t = new Tile(Integer.parseInt(tileinfo[0]), Integer.parseInt(tileinfo[3]), Integer.parseInt(tileinfo[6]), tileinfo[1], tileinfo[4]  ) ;
                    t.setKingInfo(Integer.parseInt(tileinfo[8]), tileinfo[7], Integer.parseInt(tileinfo[9]), Integer.parseInt(tileinfo[10]) );
                    
                    System.out.println(String.format(" === reupdateFirstRow :: setting tile#=%d, kingnum=%d, color=%s", t.aa, Integer.parseInt(tileinfo[8]), tileinfo[7]));

                    if(p.GuiPlayerNumber==1){
                        Tile emptytile = new Tile(-1, 0, 0, "blank", "blank");
                        player1DrawLineOne.add(emptytile);
                        player1DrawLineTwo.add(t);

                    } else {  
                        Tile emptytile = new Tile(-1, 0, 0, "blank", "blank");
                        player2DrawLineOne.add(emptytile);                              
                        player2DrawLineTwo.add(t); 
                    }      
                }
                updateUIFirstRow(p.GuiPlayerNumber, maxtiles);
            }
        }
    }

    public void updateFirstRow( int playmode, int clientNum, int playerNum, int kingNum, String firstrow){        
        int forguiplayer =0;
        for(Player p : players){
            if( kingNum == p.kingNumber){
                forguiplayer = p.GuiPlayerNumber;
                String[] arrow = firstrow.split("=");
                String[] artiles = arrow[1].split("@");
                int maxtiles=4;
                if(playmode==3) maxtiles=3; 
                    //firstrow=37;0;1;lake;field1c@10;0;0;field;field@13;0;0;farm;forest@29;1;0;forest1c;field
                    // playmode=2&client=1&player=3&king=3&action=startingrow&
                    // firstrow=26;forest1c;0x808080ff;1;farm;0xffff00ff;0@39;field;0x008000ff;0;land1c;0xd3d3d3ff;1@27;forest1c;0x808080ff;1;farm;0xffff00ff;0@23;farm1c;0xffff00ff;1;rocks;0xa9a9a9ff;0

// playmode=2&client=1&player=1&king=1&action=startingrow&firstrow=.....@%d;%s;%s;%d;%s;%s;%d;%s;%d;%d;%d", t.aa, t.Square1,t.color1,t.crowns1, t.Square2,t.color2,t.crowns2,t.playercolor,t.kingNumber,t.playernumber,t.clientnumber
// firstrow= t.aa, t.Square1,t.color1,t.crowns1, t.Square2,t.color2,t.crowns2,t.playercolor,t.kingNumber,t.playernumber,t.clientnumber                          
//             0    1         2         3          4         5         6        7             8            9              10

                player1DrawLineOne = new ArrayList<Tile>();
                player2DrawLineOne = new ArrayList<Tile>();
                player1DrawLineTwo = new ArrayList<Tile>();
                player2DrawLineTwo = new ArrayList<Tile>();
                for(int i=0;i<maxtiles;i++){                        
                    String thetile=artiles[i];
                    String[] tileinfo = thetile.split(";");
                    /*  firstrow=26;forest1c;0x808080ff;1;farm;0xffff00ff;0
                                 0 ; `      ; 2        ;3;4   ; 5        ;5
                    [0]  tilenum

                    [1] sqr1
                    [2] color1
                    [3] crown1 
                    [4] sqr2
                    [5] color2
                    [6] crown2 
                    [7] t.playercolor
                    [8] ,t.kingNumber
                    [9], t.playernumber,
                    [10] t.clientnumber                     
                     */
                    Tile t = new Tile(Integer.parseInt(tileinfo[0]), Integer.parseInt(tileinfo[3]), Integer.parseInt(tileinfo[6]), tileinfo[1], tileinfo[4]  ) ;
                    t.setKingInfo(Integer.parseInt(tileinfo[8]), tileinfo[7], Integer.parseInt(tileinfo[9]), Integer.parseInt(tileinfo[10]) );

/*                    == handleServerMessage :: playmode=2&client=0&player=0&king=0&action=startingrow&
                        firstrow=8;0;0;lake;lake;-1;none;-1;-1@15;0;0;farm;field;-1;none;-1;-1@32;1;0;lake1c;forest;-1;none;-1;-1@36;0;1;farm;field1c;-1;none;-1;-1
                    Exception in thread "JavaFX Application Thread" java.lang.NumberFormatException: For input string: "lake"
                            at java.base/java.lang.NumberFormatException.forInputString(NumberFormatException.java:65)
                            at java.base/java.lang.Integer.parseInt(Integer.java:652)
                            at java.base/java.lang.Integer.parseInt(Integer.java:770)
                            at gr.uop/gr.uop.PlayerClient.updateFirstRow(PlayerClient.java:1487)
*/

                    // Tile(int aa, int crowns1, int crowns2, String sq1, String sq2)
                    if(p.GuiPlayerNumber==1){
                        Tile emptytile = new Tile(-1, 0, 0, "blank", "blank");
                        player1DrawLineOne.add(emptytile);
                        player1DrawLineTwo.add(t);
                        //Button btn =  (Button)this.pl1selcards.getChildren().get(i*4+0);
                        //Button btn =  (Button)getNodeFromGridPane(this.pl1selcards,i,0);
                        // btn.setText(tileinfo[0]+"."+tileinfo[3]+"-"+tileinfo[4]+" "+tileinfo[1]+","+tileinfo[4]);
                        // Node cell = getNodeFromGridPane(this.pl1selcards,i,0);
                    } else {
                        // Button btn =  (Button)this.pl2selcards.getChildren().get(i*4+0);
                        // Button btn =  (Button)getNodeFromGridPane(this.pl2selcards,i,0);
                        // btn.setText(tileinfo[0]+"."+tileinfo[3]+"-"+tileinfo[4]+" "+tileinfo[1]+","+tileinfo[4]);  
                        Tile emptytile = new Tile(-1, 0, 0, "blank", "blank");
                        player2DrawLineOne.add(emptytile);                              
                        player2DrawLineTwo.add(t); 
                    }      
                }
                updateUIFirstRow(p.GuiPlayerNumber, maxtiles);
            }
        }
        // TODO: call method to update gui for firstrow

        listenToServer = true;
    
        //String message = String.format("%d;updateFirstRow=%s", clientNum,  CommMessages.ClientActions.DONE.getName());
        /*  general client format:  %d;%d;%d;%s;%s;action=%s;%s
            [0] client number
            [1] player number
            [2] king number
            [3] playername
            [4] playercolor
            [5] action=.....
            [6] rest of data or subaction=
            String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions. .getName(), "updateSubseqRow");
        */
        String message = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",CommMessages.ClientActions.DONE.getName(), "updateFirstRow");
        System.out.println("updateFirstRow:: send to server [" + message +"]");
        toServer.println(message);
        gameStatus=CommMessages.GameStatus.STARTING_ROW;
       this.logtext.setText(" Waiting for my turn to place the king...");
    }

    void updateUIFirstRow(int guiplayerno, int maxtiles) {

        System.out.printf("==> updateUIFirstRow player %d ... %n ", guiplayerno);
        GridPane paneSelcards;   
        if(guiplayerno==1) {
            paneSelcards = this.pl1selcards;
            int colno = 0;
            for(Tile t: player1DrawLineOne) {
                System.out.printf(" == updateUIFirstRow player %d ", guiplayerno);
                System.out.printf("player1DrawLineOne : ... %n");
                // System.out.println(player1DrawLineOne);
                SetTileToSelGridPane(guiplayerno,paneSelcards,colno,0,t);
                colno++;
            }
            colno = 0;
            for(Tile t: player1DrawLineTwo) {
                System.out.printf(" == updateUIFirstRow player %d ", guiplayerno);
                System.out.printf("player1DrawLineTwo : ... %n");
                // System.out.println(player1DrawLineTwo);
                SetTileToSelGridPane(guiplayerno,paneSelcards,colno,1,t);
                colno++;
            }
            // this.pl2selcards.setDisable(true);
            // this.pl2board.setDisable(true);
            this.pl1selcards.setDisable(false);
            // this.pl1board.setDisable(true); 

        }
        else  {
            paneSelcards = this.pl2selcards;
            int colno = 0;
            for(Tile t: player2DrawLineOne) {
                System.out.printf(" == updateUIFirstRow player %d ", guiplayerno);
                System.out.printf("player2DrawLineOne : ... %n");
                SetTileToSelGridPane(guiplayerno,paneSelcards,colno,0,t);
                colno++;
            }
            colno = 0;
            for(Tile t: player2DrawLineTwo) {
                System.out.printf(" == updateUIFirstRow player %d ", guiplayerno);
                System.out.printf("player2DrawLineTwo : ... %n");
                SetTileToSelGridPane(guiplayerno,paneSelcards,colno,1,t);
                colno++;
            }
            this.pl2selcards.setDisable(false);
        }
        System.out.printf("<== updateUIFirstRow player %d finished. %n", guiplayerno);
    }

 








   

}
