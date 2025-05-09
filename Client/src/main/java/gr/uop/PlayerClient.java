package gr.uop;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

//import javafx.application.Application;
import javafx.application.Platform;
//import javafx.scene.control.Alert;



/**
 * handles client socket communication with server and provides critical fields for player and game
 */
public class PlayerClient {    

    String serverip = "localhost";
    int port = 7777;
    /**
     * the socket
     */
    Socket clientSocket;
    /**
     * send data
     */
    PrintWriter toServer;
    /**
     * read data
     */
    Scanner fromServer;

    /**
    * the Client object that initialize this playerclient (see constructor) 
    */
    Client thisClient;
    /**
     *  message logging for Client
     */
    public String logMessage="";
    /**
     * is socket connected
     */
    public boolean isConnected=false;
    /**
     * was player registration succesgull
     */
    public boolean registrationSuccess = false;
    /**
     * has connection error
     */
    public boolean connError = false;
    /**
     * connection error message
     */
    public String connErrorMsg = "";
    /**
     * stores last server message
     */
    String lastServerMessage="";
    /**
     * stores client number assigned by server
     */
    public int ClientNumber = 0;
    /**
     * stores playmode sent by server
     */
    public int gplaymode = 4;
    /**
     * stores player number assigned by server
     */
    int gplayerNo = 0;
    /**
     * is current client can request start game it nust be 2nd or third client
     */
    public boolean canStartTheGame = false;
    /**
     * player name choosed for registration
     */
    String regPlayerName = "";
    /**
     * setter for regPlayerName 
     * @param mpln
     */
    public void setRegPlayerName(String mpln){
        regPlayerName=mpln;
    }
    /**
     * player color choosed for registration
     */
    public String regPlayerColor = "";
    /**
     * setter for regPlayerColor
     * @param mplc
     */
    public void setRegPlayerColor(String mplc){
        regPlayerColor=mplc;
    }
    /**
     * player number for registration
     */
    public int regPlayerNumber = -1;
    /**
     * setter for regPlayerNumber
     * @param mpln
     */
    public void setRegPlayerNumber(int mpln){
        regPlayerNumber=mpln;
    }
    /**
     * king number for registration
     */
    int regKingNumber = -1;
    /**
     * setter for regKingNumber
     * @param mknn
     */
    public void setRegKingNumber(int mknn){
        regKingNumber=mknn;
    }
    /**
     * start game now flag
     */
    public boolean StartGameNow = false;
    /**
     * setter for StartGameNow
     * @param startgame
     */
    public void setStartGameNow(boolean startgame){
        StartGameNow=startgame;
    }
    /**
     * quit request if true
     */
    public boolean quitRequest = false;
    /**
     * setter for quitRequest
     * @param qreq
     */
    public void setQuitRequest( boolean qreq){
        quitRequest=qreq;
    }
    /**
     * server listeber class installs scheduled timertask and monitors server transmitions
     */
    ServerListener serverListener;
    /**
     * class with crtitical enums ServerActions, Client Actions like http verbs see <b>CommMessages class</b>
     */
    public CommMessages.GameStatus gameStatus = CommMessages.GameStatus.NEW_GAME;

    String debugIndicativeStr = "none";
    int otherPlayers = 0;

    Timer timer;
    TimerTask listenServertask;
    /**
     * flag to notify ServerListener it is ok to listen to server or not every time listenServerTasks run
     */
    boolean listenToServer=false;
    /** 
     * flag, indicates if getdatafromserver should readdata waiting or just return without reading input stream
     * because, client is polling server we some times need to skip this withour stopping the polling permanently
     */
    public boolean wait_server = true;

    /**
     * holds array or winners announced by server at the end of the fame
     */
    public class Winner {
        
        String playerName;
        String playerColor;
        int playerScore;
    
        public Winner(String plname, String plcolor, int pscore){
            this.playerName=plname;
            this.playerColor=plcolor;
            this.playerScore=pscore;
        }
    }
 
    /**
     * holds all players objects (1 or 2 in game 2players) assigned to this client
     */
    public Player[] players = {null, null};

    public Player getPlayerByGuiNo(int no){
        for(Player p : this.players) {
            if( p != null && no == p.GuiPlayerNumber) return p;
       }
       return null;
    }
    public Player getPlayerByPalyerNo(int no){
        for(Player p : this.players) {
            if( p != null && no == p.playerNumber) return p;
       }
       return null;
   }
    public Player getPlayerByKingNo(int no){
        for(Player p : this.players) {
             if( p != null && no == p.kingNumber) return p;
        }
        return null;
    }
    
    /**
     * constructor
     * @param ip  ip address or hostname of kingdomino server
     * @param prt port of kingdomino server
     * @param thisclient the Client object that initialized this playerclient
     */
    public PlayerClient( String ip, int prt, Client thisclient){
        this.serverip = ip;
        this.port = prt;
        this.thisClient = thisclient;

        this.logMessage="";
        this.isConnected=false;
        this.registrationSuccess = false;
        this.connError = false;
        this.connErrorMsg = "";
        this.lastServerMessage="";
        this.ClientNumber = 0;
        this.gplaymode = 4;
        this.gplayerNo = 0;
        this.canStartTheGame = false;
        this.regPlayerName = "";
        this.regPlayerColor = "";
        this.regPlayerNumber = -1;
        this.regKingNumber = -1;
        this.StartGameNow = false;
        this.quitRequest = false;
        this.debugIndicativeStr = "none";
        this.otherPlayers = 0;
        this.listenToServer=false;
        this.wait_server = true;

                
        serverListener = new ServerListener();
        serverListener.start();

    }

    /**
     * stop client, ie, close socket
     */
    public void stopClient()
    {
        try {
            if(clientSocket.isConnected() && clientSocket.isClosed()==false) {
                clientSocket.close();
            }
        } catch(Exception e){

        }
    }
    
    /**
    * general method for sending unprocessed data to server
    */
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
        try{
            if(!(clientSocket.isConnected() && clientSocket.isBound() && clientSocket.isClosed()==false && clientSocket.isOutputShutdown()==true )) {
                System.out.println(" sendToServer:: sending [" + message +"]");
                toServer.println(message);
            }
            else {
                this.connError=true;
                this.connErrorMsg="sendToServer Failed. socket is in wrong state";
                this.logMessage="sendToServer Failed. socket is in wrong state";
            }
        } catch(Exception e){
            this.connError=true;
            this.connErrorMsg="sendToServer Exception. "+e.getMessage();
            this.logMessage="sendToServer Exception. "+e.getMessage();
        }
    }

    /** 
     * general method for sending data to server, prepends clientnumber and playernumber and action to data
     */
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


    /**
     * called by Client when it wants to connect to server
     * @return true on success false on failure
     */
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


    /** 
     * reads from server input stream if flag wait_server is true else just returns false
    * data is stored to lastServerMessage propery of PlayerClient
    * it checks socket and getInputStream().available before so not to block when no data are available
    * used by listentoServer timertask
    */
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
                        // if server sent anything wait to read it, else return
                        // this helps on avoiding unecessary blocking
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

    /**
     * install timer and schedules timertask to listen to server every 10msecs checking the socket and 
     * getInputStream().available first not to block wiating when no data are available
     */
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
                            listenToServer=false;  // skip executing the run()code                
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
            innerTimer.scheduleAtFixedRate(innerTask, 500, 500);
        }
        public void stop(){
            innerTimer.cancel();
            innerTimer.purge();
            isActive=false;
        }


    }

    /**
     * processes and dispatches to Client (calling the appopriate methods) the messages of the server 
     * called by Serverlistenet listener timertask
     * for server messages see <b>CommMessages class</b>
     */
    public void handleServerMessage(String message) {
        System.out.println(" == handleServerMessage :: "+message );

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

        if(action.equals(CommMessages.ServerActions.REGISTRATION.getName())){
            //lclPlayerNameDialog(this_stage, canStartTheGame);
            prepPlayerNameColorDialog(message);
            // wait to close dialog ... and .. client will call  Register();
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
            }
            else {

                if(action.equals(CommMessages.ServerActions.NEWKINGREGISTRATION.getName())){  
                    this.gplaymode=playmode;  
                    register2ndKing( message ); 
                    // Client will call the RegisterSecondKing(); method
                 }

                 else if(action.equals(CommMessages.ServerActions.PLAYERS_JOINED.getName())){
                    // playmode=2&client=1&player=3&king=3&action=playersjoined&registeredplayers=0;0;than;0;red@0;1;geo;1;green@0;2;than;2;blue@0;3;geo;3;yellow
                    // 0           1        2        3      4                     5
                    this.gplaymode=playmode;
                    updatePlayersJoined( playmode, clientNum, playerNum, kingNum, arfields[5]);                   
                    String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",
                        CommMessages.ClientActions.DONE.getName(), "PLAYERS_JOINED");
                    System.out.println("PLAYERS_JOINED:: send to server [" + replymessage +"]");
                    toServer.println(replymessage);
                    listenToServer = true;
                 }

                 else if(action.equals(CommMessages.ServerActions.POSITION_CASTLE.getName())){
                    this.gplaymode=playmode;
                    thisClient.positionCastle(playmode,  clientNum,  playerNum,  kingNum);
                 }

                else if(action.equals(CommMessages.ServerActions.STARTING_ROW.getName())){
                    this.gplaymode=playmode;          

                    setFirstRow(  playmode,  clientNum,  playerNum,  kingNum, arfields[5]);

                   String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",
                        CommMessages.ClientActions.DONE.getName(), "STARTING_ROW");
                   System.out.println("STARTING_ROW:: send to server [" + replymessage +"]");
                   toServer.println(replymessage);

                   listenToServer = true;

                }

                else if(action.equals(CommMessages.ServerActions.PLACE_KING.getName())){
                    this.gplaymode=playmode;
                    reupdateFirstRow(playmode, clientNum, playerNum, kingNum, arfields[5], true,false);
                    placeKing(playmode, clientNum, playerNum, kingNum, arfields[5]);
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
                    this.gplaymode=playmode;
                    reupdateFirstRow( playmode, clientNum, playerNum, kingNum, arfields[5], true,false);
                    //String replymessage = String.format("%d;updateFirstRow=%s", clientNum,  CommMessages.ClientActions.DONE.getName());
                    String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",
                    CommMessages.ClientActions.DONE.getName(), "UPDATE_FIRSTROW");
                    System.out.println("UPDATE_FIRSTROW:: send to server [" + replymessage +"]");
                    toServer.println(replymessage);

                    listenToServer = true;

                }
                
                // ????????????????????
                else if(action.equals(CommMessages.ServerActions.UPDATE_PLACEMENT.getName())){
                    // TODO: update other players boards ...
                    String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",
                        CommMessages.ClientActions.DONE.getName(), "UPDATE_PLACEMENT");
                    System.out.println(" UPDATE_PLACEMENT:: send to server [" + replymessage +"]");
                    toServer.println(replymessage);
                    listenToServer = true;
                }

        
                else if(action.equals(CommMessages.ServerActions.SUBSEQ_ROW.getName())){
                    this.gplaymode=playmode;
                    reupdateFirstRow( playmode, clientNum, playerNum, kingNum, arfields[5], false, true);
                    //String replymessage = String.format("%d;updateSubseqRow=%s", clientNum,  CommMessages.ClientActions.DONE.getName());
                    String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",
                    CommMessages.ClientActions.DONE.getName(), "SUBSEQ_ROW");
                    System.out.println("SUBSEQ_ROW:: send to server [" + replymessage +"]");
                    toServer.println(replymessage);
                    // doSubseqPlaceKing(playmode, clientNum, playerNum, kingNum, arfields[5]);
                    listenToServer = true;
                }

                else if(action.equals(CommMessages.ServerActions.PLACE_TILE_ON_BOARD.getName())){
                    this.gplaymode=playmode;
                    thisClient.placeTileOnBoard(playmode, clientNum, playerNum, kingNum);
                    // return call to TilePlaced()
                }

                else if(action.equals(CommMessages.ServerActions.PLACEAGAIN_KING.getName())){
                    reupdateFirstRow(playmode, clientNum, playerNum, kingNum, arfields[5], true,false);
                    placeKing(playmode, clientNum, playerNum, kingNum, arfields[5]);
                    listenToServer = true;

                }

                else if(action.equals(CommMessages.ServerActions.GAME_END.getName())){
                    reupdateFirstRow(playmode, clientNum, playerNum, kingNum, arfields[5], false,false);
                    thisClient.gameEnd(playerNum, kingNum);
                    String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber,playerNum,kingNum,"none","none",
                    CommMessages.ClientActions.DONE.getName(), "GAME_END");
                    System.out.println("SUBSEQ_ROW:: send to server [" + replymessage +"]");
                    toServer.println(replymessage);
                    listenToServer = true;

                }

                else if(action.equals(CommMessages.ServerActions.SEND_SCORE.getName())){                    
                    thisClient.sendScore(playerNum, kingNum);
                    // wait from client to call back this sendScore method
                }

                else if(action.equals(CommMessages.ServerActions.WINNER.getName())){                    
                    //"playmode=%d&client=%d&player=%d&king=%d&action=%s&winner=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, ServerActions.WINNER.getName(),winnerInfo);                    
                    // winnerInfo = "%s;%s;%d;%d"  gPlayers[0].playerName, gPlayers[0].playerColor, gPlayers[0].clientNumber, maxScore);
                    String[] arrwinners = arfields[5].split("@");
                    ArrayList<Winner> aWinners = new  ArrayList<Winner>();
                    for(int i=0;i<arrwinners.length;i++){
                        String[] awinner = arrwinners[i].split(";");
                        aWinners.add(new Winner(awinner[0],awinner[1], Integer.parseInt(awinner[3])));
                    /* [0] PlayerName
                     * [1] playerColor
                     * [2] clientnumber
                     * [3] maxscore
                     */
                    }
                    thisClient.announceWinner(aWinners);
                }

                else if(action.equals(CommMessages.ServerActions.GAME_ABORTED.getName())){
                    thisClient.gameStopped(playerNum,kingNum); // playmode, clientNum, playerNum, kingNum
                }
            }

        }

    }

    /**
     * dispatches REGISTRATION type server message to Client
     * @param message
     */
    void prepPlayerNameColorDialog(String message){
        List<String> reservenames = new ArrayList<String>();
        List<String> reservedcolors = new ArrayList<String>();
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
        String[] msgfields = message.split("&");

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
            String[] arrayPlayers = regplayers.split("@", 0);
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
            canStartTheGame=true;
        }
        else {
            canStartTheGame=false;
            System.out.println("Register:: 1rst player to register ... "); 
        }
        StringBuilder sb1 = new StringBuilder(); 
        StringBuilder sb2 = new StringBuilder(); 

        int i = 0;
        while (i < reservenames.size() - 1)
        {
            sb1.append(reservenames.get(i));
            sb1.append(";");
            i++;
        }
        if (reservenames.size()>0) sb1.append(reservenames.get(i));
 
        String splayers = sb1.toString();

        i = 0;
        while (i < reservedcolors.size() - 1)
        {
            sb2.append(reservedcolors.get(i));
            sb2.append(";");
            i++;
        }
        if(reservedcolors.size()>0) sb2.append(reservedcolors.get(i));
 
        String scolors = sb2.toString();

        thisClient.doPlayerNameDialog(splayers, scolors, canStartTheGame,playernumber,kingnumber);
        // client will call register !

    }
    
    /**
     *  called by client to send registration data to server
     */
    public boolean Register() { 
        // (String playername, String playercolor, String response, boolean startgame ) {
        /*if(!(clientSocket.isConnected() && clientSocket.isBound() && clientSocket.isClosed())) {
            System.out.println("Register:: Socket problem !");
            this.connError=true;
            this.connErrorMsg = "Register:: Socket problem !";
            return false;
        }*/
        try { 
            Player player = new Player(1, regPlayerNumber, regKingNumber, regPlayerName, regPlayerColor,this.ClientNumber);
            players[gplayerNo] = player;
            gplayerNo++;

            // String message = CommMessages.ClientActions.REGISTER_PLAYER.getName() + ";" +  myName + ";" + myColor;
            // if(startgame==true) message += ";subaction="+ CommMessages.ClientActions.REQUEST_STARTGAME.getName();

            String subaction = "none";
            if(StartGameNow==true) subaction = "subaction="+ CommMessages.ClientActions.REQUEST_STARTGAME.getName();

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

            String message=String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber, regPlayerNumber,regKingNumber,regPlayerName,regPlayerColor,CommMessages.ClientActions.REGISTER_PLAYER.getName(), subaction);
            System.out.println("Register:: registering as [" + message +"]");
            //toServer.println(message);
            this.sendToServer(message);
            listenToServer=true;
            return true;

        } catch (Exception e) {
            System.out.println(e);
            this.connError=true;
            this.connErrorMsg = "Register:: "+e.getMessage();
            listenToServer=true;
            return false;
        }
   
    }

     /**
     * dispatches REGISTRER_SECOND_KING type server message to Client
     * @param message
     */
    public void register2ndKing(String message){
        List<String> reservenames = new ArrayList<String>();
        List<String> reservedcolors = new ArrayList<String>();
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
        String[] msgfields = message.split("&");

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
            String[] arrayPlayers = regplayers.split("@", 0);
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
            canStartTheGame=true;
        }
        else {
            canStartTheGame=false;
            System.out.println("Register:: 1rst player to register ... "); 
        }
        StringBuilder sb1 = new StringBuilder(); 
        StringBuilder sb2 = new StringBuilder(); 

        int i = 0;
        while (i < reservenames.size() - 1)
        {
            sb1.append(reservenames.get(i));
            sb1.append(";");
            i++;
        }
        if (reservenames.size()>0) sb1.append(reservenames.get(i));
 
        String splayers = sb1.toString();

        i = 0;
        while (i < reservedcolors.size() - 1)
        {
            sb2.append(reservedcolors.get(i));
            sb2.append(";");
            i++;
        }
        if(reservedcolors.size()>0) sb2.append(reservedcolors.get(i));
 
        String scolors = sb2.toString();
   
        thisClient.RegisterSecondKing(splayers, scolors, canStartTheGame,playernumber,kingnumber);
        // client will call register !

        gameStatus=CommMessages.GameStatus.NEWKINGREGISTRATION;
     }

    /** 
     * called by client to send second king registration
     */
    public boolean RegisterSecondKing(){

        try {

            Player player = new Player(2, regPlayerNumber, regKingNumber, regPlayerName, regPlayerColor,this.ClientNumber);
            players[gplayerNo] = player;
            gplayerNo++;

            String subaction = "none";
            if(StartGameNow==true) subaction += "subaction="+ CommMessages.ClientActions.REQUEST_STARTGAME.getName();

            String message=String.format("%d;%d;%d;%s;%s;action=%s;%s", ClientNumber, regPlayerNumber,regKingNumber,regPlayerName,regPlayerColor,CommMessages.ClientActions.REGISTER_PLAYER.getName(), subaction);
            System.out.println("Register:: registering as [" + message +"]");
            //toServer.println(message);
            this.sendToServer(message);
            listenToServer=true;
            return true;

        } catch (Exception e) {
            System.out.println(e);
            this.connError=true;
            this.connErrorMsg = "Register:: "+e.getMessage();
            listenToServer=true;
            return false;
        }
    }

     /**
     * dispatches PLAYERS_JOINED type server message to Client
     * @param message
     */
    public void updatePlayersJoined( int playmode, int clientNum, int playerNum, int kingNum, String registeredplayers){
        // playmode=2&client=1&player=3&king=3&action=playersjoined&registeredplayers=0;0;than;0;red@0;1;geo;1;green@0;2;than;2;blue@0;3;geo;3;yellow
        thisClient.updatePlayersJoined( playmode, clientNum, playerNum, kingNum, registeredplayers);
    }
    /** 
     * called by Client to send castle placement to server
    */
    public void CastlePositioned(int playerNum, int kingNum, String playerName, String playerColor,  int tileaa, int row, int col){
        String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%d;%d;%d", ClientNumber,playerNum,kingNum,playerName,playerColor,
            CommMessages.ClientActions.CASTLE_PLACED.getName(), tileaa, row, col);
        System.out.println("kingPlaced TILE_PICKED:: send to server [" + replymessage +"]");
        this.sendToServer(replymessage);
        listenToServer = true;
    }


    /**
     * dispatches FIRST_ROW type server message to Client
     * @param message
     */
    public void setFirstRow( int playmode, int clientNum, int playerNum, int kingNum, String firstrow){

        // &firstrow=4;forest;gray;0;forest;gray;0;none;-1;-1;-1;0xFFFFFF;0xFFFFFF;=no@18;forest;green;0;field;none;0;none;-1;-1;-1;0xFFFFFF;0xFFFFFF;discarded=no@36;farm;green;0;field1c;none;1;none;-1;-1;-1;0xFFFFFF;0xFFFFFF;discarded=no

        /* @ 
        [0]  tilenum
       [1] sqr1
       [2] strcolor1
       [3] crown1 
       [4] sqr2
       [5] strcolor2
       [6] crown2 
       [7] playercolor
       [8] kingNumber
       [9] playernumber,
       [10] clientnumber
       [11] color1
       [12] color2 
       [13] no|yes          discarded
       [14] this.row=-1;
       [15] this.col=-1;
       [16] this.row2=-1;
       [17] this.col2=-1;

        // firstrow=
        10;field;green;0;field;none;0;none;-1;-1;-1;0xFFFFFF;0xFFFFFF;no-1;-1;-1;-1;
        @
        28;forest1c;gray;1;lake;blue;0;none;-1;-1;-1;0xFFFFFF;0xFFFFFF;no-1;-1;-1;-1;
        @
        47;land;lightgray;0;rocks2c;darkgray;2;none;-1;-1;-1;0xFFFFFF;0xFFFFFF;no-1;-1;-1;-1;


        */

        ArrayList<Tile> DrawLine = new ArrayList<Tile>();
        int maxtiles=4;
        if(playmode==3) maxtiles=3;
        String[] arrow = firstrow.split("=");
        String[] artiles = arrow[1].split("@");
        
        for(int i=0;i<maxtiles;i++){                        
            String thetile=artiles[i];
            String[] tileinfo = thetile.split(";");
            int tileaa = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_tilenum]);
            int crowns1 = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_crown1]);
            int crowns2 = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_crown2]);
            String sqr1 = tileinfo[Tile.SquareInfoFields.f_sqr1];
            String sqr2 = tileinfo[Tile.SquareInfoFields.f_sqr2];
            Tile t = new Tile(tileaa, crowns1, crowns2, sqr1, sqr2  ) ;
            // setKingInfo(int pkingnumber, String pplayercolor, int pplayernumber, int pclientnumber ){
            int pkingnumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_kingNumber]);
            String pplayercolor = tileinfo[Tile.SquareInfoFields.f_playercolor];
            int pplayernumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_playernumber]);
            int pclientnumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_clientnumber]);
            t.setKingInfo(pkingnumber, pplayercolor, pplayernumber, pclientnumber );                    
            System.out.println(String.format(" === Playerclient.setFirstRow :: DrawLine.add tile#=%d, kingnum=%d, color=%s", tileaa, pkingnumber, pplayercolor));
            DrawLine.add(t);            
        }
        System.out.print( "=== Playerclient.setFirstRow :: DrawLine = [");
        System.out.print(DrawLine);
        System.out.println("]");
        
        // updateUIFirstRow(int playmode, int clientNum, int playerNum, int kingNum, maxtiles, );
        thisClient.setTilesRow( playmode, clientNum, playerNum, kingNum, DrawLine, maxtiles );

    }

    /**
     * dispatches PLACE_KING type server message to Client
     * @param message
     */
    public void placeKing(int playmode, int clientNum, int playerNum, int kingNum, String firstrow){
        //== handleServerMessage :: playmode=4&client=0&player=0&king=0&action=placeking&firstrow=8;lake;blue;0;lake;blue;0;none;-1;-1;-1;0x0000ffff;0x0000ffff@20;farm1c;yellow;1;lake;blue;0;none;-1;-1;-1;0xffff00ff;0x0000ffff@22;farm1c;yellow;1;land;lightgray;0;none;-1;-1;-1;0xffff00ff;0xd3d3d3ff@42;lake;green;0;field2c;none;2;none;-1;-1;-1;0x0000ffff;0x008000ff
        this.thisClient.placeKing( playmode, clientNum, playerNum, kingNum);
    }

    /** 
     * called by Client to send king placement on domino to server
    */
            /* [0] ClientNumber 
                                [1] p.playerNumber, 
                                [2] p.kingNumber, 
                                [3] p.playerName, 
                                [4] p.playerColor, 
                                [5] action=tilepicked  TILE_PICKED
                                [6] p.selSquare1.tileAa, 
                                [7] "yes", (| "no" discarded selected)
                                [8] p.selSquare1.Square, 
                                [9] p.selSquare1.crowns, 
                                [10] p.selSquare1.strColor, 
                                [11] p.selSquare1.rowonBoard, 
                                [12] p.selSquare1.colonBoard,
                                [13] p.selSquare2.Square, 
                                [14] p.selSquare2.crowns, 
                                [15] p.selSquare2.strColor, 
                                [16] p.selSquare2.rowonBoard,
                                [17] p.selSquare2.colonBoard ); 

            playerClient.kingPlaced(p.playerNumber, p.kingNumber, p.playerName, p.playerColor, p.kingTile.aa, "selected", 
                            p.kingTile.Square1.Square, p.kingTile.Square1.crowns, p.kingTile.Square1.strColor, 
                            p.kingTile.Square2.Square, p.kingTile.Square2.crowns, p.kingTile.Square2.strColor);

            */   
    public void kingPlaced(int playerNum, int kingNum, String playerName, String playerColor,  int tileaa, String dominoaction,
        String square1, int cronws1, String strcolor1, int row1onboard, int col1onboard,
        String square2, int cronws2, String strcolor2, int row2onboard, int col2onboard ){
        //String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%d", ClientNumber,playerNum,kingNum,playerName,playerColor,
        //    CommMessages.ClientActions.TILE_PICKED.getName(), tileaa);
        String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;", ClientNumber,playerNum,kingNum,playerName,playerColor,
            CommMessages.ClientActions.TILE_PICKED.getName());
        replymessage += String.format("%d;%s;",  tileaa, dominoaction);
        replymessage += String.format("%s;%d;%s;%d;%d;",  square1, cronws1, strcolor1, row1onboard, col1onboard);
        replymessage += String.format("%s;%d;%s;%d;%d", square2, cronws2, strcolor2,row2onboard,  col2onboard);
        System.out.println("kingPlaced TILE_PICKED:: send to server [" + replymessage +"]");
        //toServer.println(replymessage);
        this.sendToServer(replymessage);
        listenToServer = true;
   }

    /**
     * dispatches UPDATE_FIRSTROW type server message to Client
     * @param message
     */
    public void updateFirstRow( int playmode, int clientNum, int playerNum, int kingNum, String firstrow){
        ArrayList<Tile> DrawLine = new ArrayList<Tile>();
        int maxtiles=4;
        if(playmode==3) maxtiles=3;
        String[] arrow = firstrow.split("=");
        String[] artiles = arrow[1].split("@");
        for(int i=0;i<maxtiles;i++){                        
            String thetile=artiles[i];
            String[] tileinfo = thetile.split(";");
            /*  firstrow=26;forest1c;0x808080ff;1;farm;0xffff00ff;0
                         0 ; `      ; 2        ;3;4   ; 5        ;5
             */
            //Tile t = new Tile(Integer.parseInt(tileinfo[0]), Integer.parseInt(tileinfo[3]), Integer.parseInt(tileinfo[6]), tileinfo[1], tileinfo[4]  ) ;
            //t.setKingInfo(Integer.parseInt(tileinfo[8]), tileinfo[7], Integer.parseInt(tileinfo[9]), Integer.parseInt(tileinfo[10]) );                    
            //System.out.println(String.format(" === Playerclient.setFirstRow :: setting tile#=%d, kingnum=%d, color=%s", t.aa, Integer.parseInt(tileinfo[8]), tileinfo[7]));


            int tileaa = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_tilenum]);
            int crowns1 = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_crown1]);
            int crowns2 = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_crown2]);
            String sqr1 = tileinfo[Tile.SquareInfoFields.f_sqr1];
            String sqr2 = tileinfo[Tile.SquareInfoFields.f_sqr2];
            Tile t = new Tile(tileaa, crowns1, crowns2, sqr1, sqr2  ) ;
            // setKingInfo(int pkingnumber, String pplayercolor, int pplayernumber, int pclientnumber ){
            int pkingnumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_kingNumber]);
            String pplayercolor = tileinfo[Tile.SquareInfoFields.f_playercolor];
            int pplayernumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_playernumber]);
            int pclientnumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_clientnumber]);
            t.setKingInfo(pkingnumber, pplayercolor, pplayernumber, pclientnumber );   
            System.out.println(String.format(" === Playerclient.updateFirstRow :: setting tile#=%d, kingnum=%d, color=%s", tileaa, pkingnumber, pplayercolor));

            DrawLine.add(t);
        }
        System.out.print( "=== Playerclient.updateFirstRow :: DrawLine = [");
        System.out.print(DrawLine);
        System.out.println("]");
        thisClient.updateTilesRow( playmode, clientNum, playerNum, kingNum, DrawLine, maxtiles, true, false );
    }

    /** 
     * called by Client to send tile placement to server
    */
    public void TilePlaced(int playerNum, int kingNum, String playerName, String playerColor,  int tileaa, String dominoaction,
        String square1, int cronws1, String strcolor1, int row1, int col1,
        String square2, int cronws2, String strcolor2, int row2, int col2 ){
        String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%d;%s;%s;%d;%s;%d;%d; %s;%d;%s;%d;%d", ClientNumber,playerNum,kingNum,playerName,playerColor,
            CommMessages.ClientActions.TILE_PLACED.getName(), tileaa, dominoaction, square1, cronws1, strcolor1, row1, col1,
            square2, cronws2, strcolor2, row2, col2);

        System.out.println("kingPlaced TILE_PLACED:: send to server [" + replymessage +"]");
        //toServer.println(replymessage);
        this.sendToServer(replymessage);
        listenToServer = true;
   }

    /**
     * dispatches REUPDATE_FIRSTROW type server message to Client
     * @param message
     */  
   public void reupdateFirstRow(int playmode, int clientNum, int playerNum, int kingNum, String firstrow, Boolean isfirstrow, Boolean copytoback){
    ArrayList<Tile> DrawLine = new ArrayList<Tile>();
    int maxtiles=4;
    if(playmode==3) maxtiles=3;
    String[] arrow = firstrow.split("=");
    String[] artiles = arrow[1].split("@");
    for(int i=0;i<maxtiles;i++){                        
        String thetile=artiles[i];
        String[] tileinfo = thetile.split(";");
        /*  firstrow=26;forest1c;0x808080ff;1;farm;0xffff00ff;0
                     0 ; `      ; 2        ;3;4   ; 5        ;5
         */
        //Tile t = new Tile(Integer.parseInt(tileinfo[0]), Integer.parseInt(tileinfo[3]), Integer.parseInt(tileinfo[6]), tileinfo[1], tileinfo[4]  ) ;
       // t.setKingInfo(Integer.parseInt(tileinfo[8]), tileinfo[7], Integer.parseInt(tileinfo[9]), Integer.parseInt(tileinfo[10]) );                    
       // System.out.println(String.format(" === Playerclient.setFirstRow :: setting tile#=%d, kingnum=%d, color=%s", t.aa, Integer.parseInt(tileinfo[8]), tileinfo[7]));


        int tileaa = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_tilenum]);
        int crowns1 = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_crown1]);
        int crowns2 = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_crown2]);
        String sqr1 = tileinfo[Tile.SquareInfoFields.f_sqr1];
        String sqr2 = tileinfo[Tile.SquareInfoFields.f_sqr2];
        Tile t = new Tile(tileaa, crowns1, crowns2, sqr1, sqr2  ) ;
        // setKingInfo(int pkingnumber, String pplayercolor, int pplayernumber, int pclientnumber ){
        int pkingnumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_kingNumber]);
        String pplayercolor = tileinfo[Tile.SquareInfoFields.f_playercolor];
        int pplayernumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_playernumber]);
        int pclientnumber = Integer.parseInt(tileinfo[Tile.SquareInfoFields.f_clientnumber]);
        t.setKingInfo(pkingnumber, pplayercolor, pplayernumber, pclientnumber );  
        System.out.println(String.format(" === Playerclient.reupdateFirstRow :: setting tile#=%d, kingnum=%d, color=%s", tileaa, pkingnumber, pplayercolor));

        DrawLine.add(t);
    }
    thisClient.updateTilesRow( playmode, clientNum, playerNum, kingNum, DrawLine, maxtiles, isfirstrow, copytoback );
   }

    /**
     * dispatches SEND_SCORE type server message to Client
     * @param message
    */
    public void sendScore(int playerNum, int kingNum, String playerName, String playerColor,  int score){
        String replymessage = String.format("%d;%d;%d;%s;%s;action=%s;%d", ClientNumber,playerNum,kingNum,playerName,playerColor,
            CommMessages.ClientActions.MY_SCORE.getName(), score);
        System.out.println("SEND_SCORE:: send to server [" + replymessage +"]");
        //toServer.println(replymessage);
        this.sendToServer(replymessage);
        listenToServer = true;
   }

}
