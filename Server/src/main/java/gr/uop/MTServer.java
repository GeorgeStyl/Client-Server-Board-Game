package gr.uop;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.Scanner;
//import java.util.Timer;
//import java.util.TimerTask;

import gr.uop.ServerEnums.GameStatus;



/**
 * <h4>main class of Multithread KingDomino Server</h4>
 * see also the inner class <b> MTServerThread </b> (extends  Thread)
 * <p> this class does everything except of user interface.</p>
 * <p> it is called by Server in a new Thread</p>  
 */

/* public interface Runnable
The Runnable interface should be implemented by any class whose instances are intended to be executed by a thread. 
The class must define a method of no arguments called run.
This interface is designed to provide a common protocol for objects that wish to execute code while they are active.
 For example, Runnable is implemented by class Thread.
 Being active simply means that a thread has been started and has not yet been stopped.
 In addition, Runnable provides the means for a class to be active while not subclassing Thread.
 A class that implements Runnable can run without subclassing Thread by instantiating a Thread instance and passing itself in as the target. 
 In most cases, the Runnable interface should be used if you are only planning to override the run() method and no other Thread methods.
 This is important because classes should not be subclassed unless the programmer intends on modifying or enhancing the fundamental behavior of the class. 
 */ 
public class MTServer implements Runnable  {

//#region  fields

    /** 
     * holds the server status for displaying it in Server stage window */
    public String ServerStatusString = "";

    int serverPort = 7777;
    
    Server theServer;

    /**
     *  how many clients have been connected
     */
    public int gNumOfClients = 0;   
    /**
     * how many players have been registred
     */
    public int gNumOfPlayers = 0;   
    /**
     * how many Kings have been registred
     */
    public int gNumOfKings = 0;     
    /**
     * how many players will play
     */
    public int gMaxNumOfPlayers = 4;   
    /**
     * how many kings will be
     */
    public int gMaxNumofKings = 4;  
    /**
     * gamemode  4= 4 players/4 kings, 3= 3 players/3 kings, 2= 2 players/4 kings
     */   
    public int gplayMode = 4;       

    /**
     * number of game tiles, staring with 48 but can change
     */
    public int gNumofTiles = 48;
    /**
     * if a player asked to start the game now
     */
    public boolean gStartgamerequest = false;   
    /**
     * if a player asked to stop the game now
     */
    public boolean gStopgamerequest = false;    
    public int gPlayerWhoStopped = -1;
    public String gPlayerNameWhoStopped = "";
    /**
     *  array of up to 4 server threads that handle client communication One thread per client
     */
    public MTServerThread[] gClients = {null, null, null, null};
    /**
     * game status
     */
    public ServerEnums.GameStatus gGameStatus = ServerEnums.GameStatus.INIT;    
    /**
     *  object used for serialization of threads 
     */
    private final Object lockRegistration = new Object();
    // private final Object lockClientPoll = new Object();
    /**
     *  object used for serialization of threads 
     */
    private final Object lockPlayerStop = new Object();
    /**
     * msecs to wait before send next message to client
     */
    int mscsleepClientProbe = 500;     
    /**
     * array to hold all the 48 deck  titles  
     */
    ArrayList<Tile> totalTiles = new ArrayList<Tile>();     
    /**
     * array to hold the game titles (24 or 36 or 48 ) this is reduced in every turn
     */
    ArrayList<Tile> gameTiles = new ArrayList<Tile>();  
    /**
     * class to construct the deck and create the totalTiles
     */
    Deck deck;                                          
    /**
     * number of tiles per drow, 4 or 3
     */
    int numofTilesPerDrow;                              
    /**
     * first line of drow (pervious drow)  NOT USED 
     */
    ArrayList<Tile> drow1stLine;                        
    /**
     * second line of drow, current drow
     */
    ArrayList<Tile> drow2ndLine; 

// #endregion

//#region  inner classes and methods methods

    /**
     * Registered player data. One for each player/king
     * name, color, playernumber, plauersocket
     * for each client connected a new player object created by accept() socket
     * when 2 players game mode, then each client registers a second player
     */
    public class Player {
        public String playerName;
        public String playerColor;
        /**
         * assigned by server at connection
         */
        public int playerNumber;
        /**
         * assigned by server at connection
         */
        public int kingNumber;

        /**
         * ths socket assigned
         */
        public Socket playerSocket;

        /**
         * the thread that handles this client connection
         */
        public MTServerThread client;

        /**
         * assigned by server at connection
         */
        public int clientNumber;

        /**
         * is player registered
         */
        public boolean isRegistered;
        /**
         * player final score
        */
        public int finalScore;

        public Player() {
            this.playerName="";
            this.playerColor="";
            this.isRegistered=false;
            this.finalScore = 0;
        }

        /** 
         * Player's info string (in csv format)
         * clientNumber;playerNumber;playerName;kingNumber;playerColor
         */
        public String toString() {
            return Integer.toString(clientNumber) + ";" + Integer.toString(playerNumber) + ";" + playerName + ";"  +Integer.toString(kingNumber) + ";" + playerColor; 
        }
    }
    /**
     * array to hold up to 4 players
     */
    public Player[] gPlayers =  {null, null, null, null};
    /**
     * method of array gPlayers that returns the array 
     * @return
     */
    public Player[] getPlayers(){ return gPlayers; }
    // 
    // *  for sorting players array by player number in Ascending order
    //
    // static private Comparator<Player> ascPlayerNumber;
    /**
     *  for sorting players array by their score in Descending order
     */
    static private Comparator<Player> descFinalScore;

    /* static, initialize static variables inside a static block  */ 
    static {    
        /**
         * comparator for descending sorting of Players by score 
         */
        descFinalScore = new Comparator<Player>(){
            @Override
            public int compare(Player p1, Player p2){
                int p1score =-1;
                int p2score=-1;

                if(p1 !=null) p1score=p1.playerNumber;
                if(p2 !=null) p2score=p2.playerNumber;
                // Java 7 has an Integer#compare function
                return Integer.compare(p1score, p2score);
                // For Java < 7, use 
                // Integer.valueOf(n1).compareTo(n2);
                // DO NOT subtract numbers to make a comparison such as n2 - n1.
                // This can cause a negative overflow if the difference is larger 
                // than Integer.MAX_VALUE (e.g., n1 = 2^31 and n2 = -2^31)
            }
        };
    }
    /**
     * method called for descaning score sorting of players array
     */
    public void sortDescFinalScore(){
        Arrays.sort(gPlayers, descFinalScore);
    }
    // =============== end for sorting

    /** 
     * updates partent server stopgame variables due to client message
     * synchronized called by server thread
     * @param clientnum
     * @param playernum
     * @param playername
    */
    public void updatePlayerStop(int clientnum, int playernum, String playername) {
 
        synchronized(lockPlayerStop){
            gStopgamerequest = true;
            gPlayerWhoStopped = playernum;
            gPlayerNameWhoStopped=playername;
        }
    }    
    /** 
     * adds a new Player into server players array within thread
     * synchronized called by server thread
     * @param newplayer
     */
    public void addPLayerClient(Player newplayer){

        synchronized(lockRegistration){
            gPlayers[gNumOfPlayers] = newplayer;
            gNumOfPlayers++;
            gNumOfKings++;
        }
    }
    /** 
     * returns a string with all registered players information
     * csv and @ delimited
     * concatenates all registered player.toSting() with '@' delimiter
     * @return String
     */
    // returns a String of ALL REGISTERED users's info
    String getRegisteredPlayersString(){
        
        int _cnt=0;
        String playersString="";
        for (Player p : gPlayers ) {
           
            if ( p != null ){
                
                if(  p.playerName != null && p.playerName.length()>0 && p.playerColor!=null & p.playerColor.length()>0){
                   
                    if (_cnt == 0) playersString += p.toString();
                        // "1;george;red@2;thanos;yellow@...."
                    else playersString += "@" + p.toString();
                    _cnt++;
                }
            }
        }
        return playersString;
    }
    /**
     *  make play deck from full deck depεnding of number of players 
     */
    void setGameTiles(){
        int numofgametiles = 48;
        if (this.gplayMode==4) numofgametiles=48;
        else if(this.gplayMode==3) numofgametiles=36;
        else numofgametiles=24;

        Random r = new Random();
        int low = 0;    // inclusive
        int high = 48;  // exclusive
        // select random numofgametiles, remove them from totalTiles and
        // add them to gameTiles .
        int i=0;
        while(i<numofgametiles) {
           
            int result = r.nextInt(high-low) + low;
            if(totalTiles.size()>result){
              
                Tile tile = totalTiles.remove(result);
                // totalTiles.get(index)
                gameTiles.add(tile);
                i++;
            }
        }

    }
    /** 
     * randomize player order using helper table
     * @return int[]
     */
    int[] randomReorderPlayers(){
        int[] a = new int[gNumOfPlayers]; // a parrallel array which indicates the random position of each player
        Random r = new Random();
        int low = 0;    // inclusive
        int high = gNumOfPlayers;  // exclusive
        int result = r.nextInt(high-low) + low;
        System.out.printf(" ===  RAND_REORDER_PLAYERS low=%d, high=%d, result=%d %n",low,high, result);
        int cntr=0;
        for(int i=result;i<gNumOfPlayers;i++){
            a[cntr]=i;
            System.out.printf(" ===  ... RAND_REORDER_PLAYERS a[%d]=%d %n",cntr,a[cntr]);
            cntr++;
        }
        for(int i=0;i<result;i++){
            a[cntr]=i;
            System.out.printf(" ===  ... RAND_REORDER_PLAYERS a[%d]=%d %n",cntr,a[cntr]);
            cntr++;
        }


        // TODO: remove from final build .... debug... do not suffle order
        // cntr=0;
        // for(int i=0;i<gNumOfPlayers;i++)
        // {
        //     a[cntr]=i;
        //     cntr++;
        // }


        return a;
    }
    /** 
    * creates the drow line from game deck and substract tiles selected from game deck 
    */
    public void addTilesin2ndDrow(){
    drow2ndLine = new ArrayList<Tile>();

    for(int i=0;i<numofTilesPerDrow;i++){
        // Tile tile = gameTiles.remove(i);
        Tile tile = gameTiles.remove(0);
        drow2ndLine.add(tile);
    }
    drow2ndLine.sort(null);
   }
    /**
    *  copies drow line 2 to drow line 1 -- NOT USED
    */
    public void copy2to1drowline(){
    drow1stLine = new ArrayList<Tile>();
    for(Tile t : drow2ndLine){
        drow1stLine.add(t) ;
    }
    drow1stLine.sort(null);
   }
    /** 
    * returns an ArrayList<Tile> of tiles in a formatted csv string 
    * each line delimited by '@'. fields in line delimited by ';'
    * aa;Square1;color1;crowns1;Square2;color2;crowns2;playercolor;kingNumber;playernumber;clientnumber@aa;Square1;...
    * @param tilesrow ArrayList<Tile>
    * @return String
    */
    //firstrow=1;0;0;farm;farm;-1;;-1;-1@2;0;0;farm;farm;-1;;-1;-1@11;0;0;field;field;-1;;-1;-1@47;0;2;land;rocks2c;-1;;-1;-1
    String rowOfTiles(ArrayList<Tile> tilesrow){
        if(tilesrow.size()<=0) return "";
        String rstr = "";
        for(Tile t : tilesrow){            
            if(rstr=="") rstr +=  t.toString(); //  String.format("%d;%s;%s;%d;%s;%s;%d;%s;%d;%d;%d", t.aa, t.Square1,t.color1,t.crowns1, t.Square2,t.color2,t.crowns2,t.playercolor,t.kingNumber,t.playernumber,t.clientnumber);
            else rstr += String.format("@%s", t.toString()); // String.format("@%d;%s;%s;%d;%s;%s;%d;%s;%d;%d;%d", t.aa, t.Square1,t.color1,t.crowns1, t.Square2,t.color2,t.crowns2,t.playercolor,t.kingNumber,t.playernumber,t.clientnumber);
        }
        return rstr;
   }

    public void PositionCastle(int[] playerNos){
        System.out.println("SERVER sending  POSITION_CASTLE to all players ");
        for(int pt=0;pt<gNumOfPlayers;pt++) {
            int pturn = playerNos[pt];
            System.out.printf("SERVER POSITION_CASTLE player turn = %d %n",pturn);
            
            Player p=gPlayers[pturn];

            this.ServerStatusString = String.format("send POSITION_CASTLE to player %d %s", p.playerNumber, p.playerColor.toUpperCase());

            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }

            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }

            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&data=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
            ServerEnums.ServerActions.POSITION_CASTLE.getName(),"none");
            System.out.println(String.format("SERVER sent to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
            p.client.sendMessage(message);
            response = p.client.readMessage(true);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }                
            }
            System.out.println(String.format("SERVER received response from player: %s",response));           
        }

    }

    public void PlaceKing(int[] playerNos) {

        for(int pt=0;pt<gNumOfPlayers;pt++) {

            int pturn = playerNos[pt];
            System.out.printf("SERVER KINGS_PLACEMENTS player turn = %d %n",pturn);
            Player p=gPlayers[pturn];

            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }

            String firstrow = rowOfTiles(drow2ndLine);

            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }

            this.ServerStatusString = String.format("send PLACE_KING to player %d %s", p.playerNumber, p.playerColor.toUpperCase());


            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
            ServerEnums.ServerActions.PLACE_KING.getName(),firstrow);
            System.out.println(String.format("SERVER sent to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
            p.client.sendMessage(message);
            response = p.client.readMessage(true);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
                if( arflds[5].contains(ServerEnums.ClientActions.TILE_PICKED.getName())){
                    int tileaa = Integer.parseInt(arflds[6]);
                    String dominoaction = arflds[7];
                    for(Tile t : drow2ndLine){
                        if(t.aa == tileaa){
                            t.setKingInfo( Integer.parseInt(arflds[2]), arflds[4], Integer.parseInt(arflds[1]), Integer.parseInt(arflds[0]) );
                            if(dominoaction.contains("discarded")) t.setDiscarded(true);
                            else t.setDiscarded(false);
                        }
                    }
                }
            }
            System.out.println(String.format("SERVER received response from player: %s",response));

            // send back the firsrow to client, confirmation of acceptance
            String againfirstrow = rowOfTiles(drow2ndLine);
            message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber,
            ServerEnums.ServerActions.UPDATE_FIRSTROW.getName(),againfirstrow);
            System.out.println(String.format("SERVER sent to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
            this.ServerStatusString = String.format("send UPDATE_FIRSTROW to player %d %s", p.playerNumber, p.playerColor.toUpperCase());
            p.client.sendMessage(message);
            response = p.client.readMessage(true);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
            }

        }
   }

    public void StartingRow(int[] playerNos){

        // prepare game deck of tiles
        // select the appropriate number of tiles for game deck depending of number palyers and randomize them
        this.setGameTiles();

        System.out.println("SERVER gameTiles size = "+Integer.toString(this.gameTiles.size()));

        System.out.println(String.format("SERVER:: starting_row . num of clients=%d, num of players=%d, playmode=%d, numofTilesPerDrow=%d", gNumOfClients, gNumOfPlayers, gplayMode, numofTilesPerDrow) );
        gGameStatus=ServerEnums.GameStatus.STARTING_ROW;
        if(gplayMode==2) numofTilesPerDrow=4; else if(gplayMode==3) numofTilesPerDrow=3; else numofTilesPerDrow=4;

        addTilesin2ndDrow();
        System.out.println("SERVER Drowed first row, gameTiles size now is = "+Integer.toString(this.gameTiles.size()));
        
        String firstrow = rowOfTiles(drow2ndLine);
        
        for(int pt=0;pt<gNumOfPlayers;pt++) {

            int pturn = playerNos[pt];
            System.out.printf("SERVER STARTING_ROW player turn = %d %n",pturn);
            Player p=gPlayers[pturn];
            this.ServerStatusString = String.format("send STARTING_ROW to player %d %s", p.playerNumber, p.playerColor.toUpperCase());
            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }

            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }

            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
            ServerEnums.ServerActions.STARTING_ROW.getName(),firstrow);
            System.out.println(String.format("SERVER sent to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
            p.client.sendMessage(message);
            response = p.client.readMessage(true);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
            }
            System.out.println(String.format("SERVER received response from player: %s",response));
        }

   }

    public void UpdateFirstRow(int[] playerNos) {
        for(int pt=0;pt<gNumOfPlayers;pt++) {
            int pturn = playerNos[pt];
            System.out.printf("SERVER UPDATE_FIRSTROW player turn = %d %n",pturn);
            Player p=gPlayers[pturn];

            this.ServerStatusString = String.format("send UPDATE_FIRSTROW to player %d %s", p.playerNumber, p.playerColor.toUpperCase());

            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }

            String firstrow = rowOfTiles(drow2ndLine);
            //Player p=gPlayers[i];

            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }

            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber,
            ServerEnums.ServerActions.UPDATE_FIRSTROW.getName(),firstrow);
            System.out.println(String.format("SERVER sent to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
            p.client.sendMessage(message);
            response = p.client.readMessage(true);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
            }
            System.out.println(String.format("SERVER received response from player: %s",response));
            // p.client.pollClient=true;
    }
    System.out.println(String.format("SERVER all players updated first row..."));
}

    public int SubseqRow(int[] playerNos, int row){
        copy2to1drowline(); //    drow2ndLine ->   drow1stLine 
        addTilesin2ndDrow();
        row++ ;
        System.out.println(String.format("SERVER Drowed Next row# %d, gameTiles size now is %d ", row, this.gameTiles.size()));
        for(int pt=0;pt<gNumOfPlayers;pt++) {

            int pturn = playerNos[pt];
            System.out.printf("SERVER SUBSEQ_ROW player turn = %d %n",pturn);
            Player p=gPlayers[pturn];

            this.ServerStatusString = String.format("send SUBSEQ_ROW to player %d %s", p.playerNumber, p.playerColor.toUpperCase());

            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }

            String firstrow = rowOfTiles(drow2ndLine);

            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }

            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
            ServerEnums.ServerActions.SUBSEQ_ROW.getName(),firstrow);
            System.out.println(String.format("SERVER sent SUBSEQ_ROW to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
                p.client.sendMessage(message);
            response = p.client.readMessage(true);
            System.out.println(String.format("SERVER received response from player %d, client %d, message: %s",p.playerNumber,p.clientNumber,response));
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
            }
        }
        return row;
    }

    public void PlaceTileOnBoard(int[] playerNos) {

        for(int pt=0;pt<gNumOfPlayers;pt++) {
            int pturn = playerNos[pt];
            System.out.printf("SERVER PLACE_TILE_ON_BOARD player turn = %d %n",pturn);
            Player p=gPlayers[pturn];

            this.ServerStatusString = String.format("send PLACE_TILE_ON_BOARD to player %d %s", p.playerNumber, p.playerColor.toUpperCase());

            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }
            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }
            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&data=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
             ServerEnums.ServerActions.PLACE_TILE_ON_BOARD.getName(),"none");
            System.out.println(String.format("SERVER sent PLACE_TILE_ON_BOARD to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
            p.client.sendMessage(message);
            response = p.client.readMessage(true);
            System.out.println(String.format("SERVER received response from player %d, client %d, message: %s",p.playerNumber,p.clientNumber,response));
            if(response.length()>0){
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
                if( arflds[5].contains(ServerEnums.ClientActions.TILE_PLACED.getName())){
                    // TODO: what to do with the tile placed/or discarded on board ?
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
                    */
                }
            }
        }

  }

    public void PlaceKingAgain(int[] playerNos){
        for(int pt=0;pt<gNumOfPlayers;pt++) {
            int pturn = playerNos[pt];
            System.out.printf("SERVER PLACEAGAIN_KING player turn = %d %n",pturn);
            Player p=gPlayers[pturn];

            this.ServerStatusString = String.format("send PLACEAGAIN_KING to player %d %s", p.playerNumber, p.playerColor.toUpperCase());

            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }

            String firstrow = rowOfTiles(drow2ndLine);
 
            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    System.out.println(String.format("SERVER recieved response from player %d, client %d, GAME_ABORTED",p.playerNumber,p.clientNumber));
                    break;                    
                }
            }

            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
                ServerEnums.ServerActions.PLACEAGAIN_KING.getName(),firstrow);
       
            System.out.println(String.format("SERVER sent PLACEAGAIN_KING to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
            
            p.client.sendMessage(message);
            response = p.client.readMessage(true);
            
            System.out.println(String.format("SERVER received response from player %d, client %d, message: %s",p.playerNumber,p.clientNumber,response));
            if(response.length()>0){
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
                if( arflds[5].contains(ServerEnums.ClientActions.TILE_PICKED.getName())){
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
                    */
                    int tileaa = Integer.parseInt(arflds[6]);
                    for(Tile t : drow2ndLine){
                        if(t.aa == tileaa){
                            t.setKingInfo( Integer.parseInt(arflds[2]), arflds[4], Integer.parseInt(arflds[1]), Integer.parseInt(arflds[0]) );
                        }
                        t.setboardrowcol(Integer.parseInt(arflds[11]), Integer.parseInt(arflds[12]), Integer.parseInt(arflds[16]), Integer.parseInt(arflds[17]) );
                        if(arflds[7].contains("discarded")) t.setDiscarded(true); else t.setDiscarded(false);
                    }
                }    
            }
            System.out.println(String.format("SERVER received response from player: %s",response));


            // send back the firsrow to client, confirmation of acceptance
            String againfirstrow = rowOfTiles(drow2ndLine);
            message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber,
                ServerEnums.ServerActions.UPDATE_FIRSTROW.getName(),againfirstrow);
            System.out.println(String.format("SERVER sent to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
            this.ServerStatusString = String.format("send UPDATE_FIRSTROW to player %d %s", p.playerNumber, p.playerColor.toUpperCase());
            p.client.sendMessage(message);
            response = p.client.readMessage(true);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
            }


        }
  }

    public void GameEnd(int[] playerNos){
        System.out.println(String.format("SERVER prepare GAME_END "));
        for (int pt = 0; pt < gNumOfPlayers; pt++) {            
            System.out.printf("SERVER GAME_END player turn = %d %n",pt);
            Player p=gPlayers[pt];
            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }
            this.ServerStatusString = String.format("send GAME_END to player %d %s", p.playerNumber, p.playerColor.toUpperCase());
            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }

            String firstrow = rowOfTiles(drow2ndLine);
            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
                ServerEnums.ServerActions.GAME_END.getName(),firstrow);
            p.client.sendMessage(message);

            // wait to recieve DONE
            response = p.client.readMessage(true);
            if(response.length()>0){
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break ;
                }
            }    
        }        
    }    

    public String SendScore(int[] playerNos){

        System.out.println(String.format("SERVER prepare SEND_SCORE "));
        for (int pt = 0; pt < gNumOfPlayers; pt++) {
            int pturn = playerNos[pt];
            System.out.printf("SERVER SEND_SCORE player turn = %d %n",pturn);
            Player p=gPlayers[pturn];

            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }
            this.ServerStatusString = String.format("send SEND_SCORE to player %d %s", p.playerNumber, p.playerColor.toUpperCase());
            if(gStopgamerequest==true) { gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; break ; }

            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
                ServerEnums.ServerActions.SEND_SCORE.getName(),"none");
            p.client.sendMessage(message);

            // wait to recieve DONE
            response = p.client.readMessage(true);
            if(response.length()>0){
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.MY_SCORE.getName()))   {
                    //   0  1  2  3  4  5              6
                    // "%d;%d;%d;%s;%s;action=mysocre;56", this.ClientNumber,  p.playerNumber, p.kingNumber, p.playerName, p.playerColor, CommMessages.ClientActions.TILE_PICKED.getName(), tileForKingSelected.aa);
                    p.finalScore = Integer.parseInt(arflds[6]);
                }    
            }
            
        }
        // find biggest score
        int maxScore = gPlayers[0].finalScore;
        System.out.printf("SERVER SendScore, player=0 score=%d maxScore=%d %n", maxScore, maxScore);
        for (int i = 1; i < gNumOfPlayers; i++ ){
            if (maxScore < gPlayers[i].finalScore) {
                maxScore = gPlayers[i].finalScore;
            }
            System.out.printf("SERVER SendScore, player=%d score=%d maxScore=%d %n", i, gPlayers[i].finalScore, maxScore);
        }
        System.out.printf("SERVER SendScore, final maxScore=%d %n", maxScore);

        String winnerInfo="";
        // find all players with the same biggest score
        for (int i = 0; i < gNumOfPlayers; i++ ){
                if( maxScore == gPlayers[i].finalScore){
                    if(winnerInfo.length()==0) winnerInfo = String.format("%s;%s;%d;%d",gPlayers[i].playerName, gPlayers[i].playerColor, gPlayers[i].clientNumber, maxScore);
                    else winnerInfo += String.format("@%s;%s;%d;%d",gPlayers[i].playerName, gPlayers[i].playerColor, gPlayers[i].clientNumber, maxScore);
                }
        }
        System.out.printf("SERVER SendScore, winnerInfo=%s %n", winnerInfo);
        return winnerInfo;
    }
  
    public void Winner(int[] playerNos, String winnerInfo){
        System.out.println(String.format("SERVER prepare WINNER "));

        for (int pt = 0; pt < gNumOfPlayers; pt++) {
            int pturn = playerNos[pt];
            System.out.printf("SERVER WINNER player turn = %d %n",pturn);
            Player p=gPlayers[pturn];
            //Player p = gPlayers[i];
            this.ServerStatusString = String.format("send WINNER to player %d %s", p.playerNumber, p.playerColor.toUpperCase());
            String response = p.client.readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    gStopgamerequest=true;
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    break;                    
                }
            }

            String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&winner=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
                ServerEnums.ServerActions.WINNER.getName(),winnerInfo);
            p.client.sendMessage(message);

            // wait to recieve DONE
            response = p.client.readMessage(true);
            
        }

    }
    // #endregion


    /**
    * SERVER THREAD one for each CLIENT
    * handles the communication between server and one client 
    */
    public class MTServerThread extends  Thread {
        /**
         * the socket provided by server accept()
         */
        public  Socket connectionSocket;
        /**
         * clientNumber provided by server
         */
        public  int clientNumber;
        /**
         * input stream reader
         */
        public Scanner fromClient ;
        /**
         * output stream writer
         */
        public PrintWriter toClient ;
        /**
         * flag. thread is running while this is true
         */
        boolean running; 

        // server thread has 2 Players cause 1 client can have up to 2 kings
 
        /**
         * first Player of the client
         */
        public Player player1=null; // up to two players for each client
        /**
         * second Player of the client in case of 2 players game
         */
        public Player player2=null;
        /**
         * umber of players participating in game (2,3,4)
         */
        public int numOfPlayers=0;
        /**
         * is this thread active
         */
        public boolean isActive=false;
        // public boolean pollClient = false;      // want to poll client ?  NOT-USED

        /**
         * if client has an additional request in its response
         */
        public boolean hasClientRequest=false;  
        /**
         * if client has an additional request in its response, the request
         */
        public String ClientRequestMessage="";  
        // public ClientListener clientListener;

        /**
         * has error ?
         */
        public boolean hasError = false;        

        /** 
         * constructor
         * @param connectionSocket
         * @param clientNumber
        */
        public MTServerThread(Socket connectionSocket, int clientNumber) {
            this.running = true;
            this.connectionSocket = connectionSocket;
            this.clientNumber=clientNumber;
            this.player1=null;
            this.player2=null;
            numOfPlayers=0;
            // ClientHandler client = new ClientHandler(connectionSocket);
            try {
                fromClient = new Scanner(this.connectionSocket.getInputStream());
                toClient = new PrintWriter(this.connectionSocket.getOutputStream(), true);
                System.out.println(String.format(" SERVERTHREAD (%d) accepted connection from client", clientNumber));

                System.out.println(String.format(" SERVERTHREAD (%d) reply  CONNECTION_ACCEPTED ...", clientNumber));
                //playmode=4&client=0&player=0&king=0&action=CONNECTION_ACCEPTED("connectionaccepted")
                String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s", gplayMode, gNumOfClients,gNumOfPlayers,gNumOfKings,
                    ServerEnums.ServerActions.CONNECTION_ACCEPTED.getName());
                sendMessage(message);

                /* Wait beofre call Client to register
                 * to ensure enough time of GUI update*/
                try{
                    Thread.sleep(mscsleepClientProbe);
                } catch(Exception e){
    
                } 
                
               isActive=true;

            } catch (Exception e){
                isActive=false;
                hasError=true;
            }

        }
        
        /**
         * overriden run() method of thread
         * runs while this.running == true
         */
        @Override
        public void run() {
            System.out.printf("**** Serverthread for %d run:: called...%n",this.clientNumber);
            while(this.running){
                // keep thread running as long as MTServer does not request it
                // to stop
            }
            System.out.printf("**** Serverthread for %d run:: stopped running and exiting...%n",this.clientNumber);
        }

        /**
         * called by server to indicate that this thread should quit
         * when called, closses the connection
         */
        public void MTServerQuit() {
            System.out.printf("**** Serverthread for %d MTServerQuit:: signal running = false, closing connection...%n",this.clientNumber);
            this.running = false;            
            this.closeConnection();
        }

        /** 
         * when called it closes the active socket
         */
        public void closeConnection() {
            if(this.connectionSocket.isConnected()){
                try{
                    this.connectionSocket.close();
                }
                catch(Exception e){

                }
            } 
        }

        /**
         * send a message to the client
         * @param message the message to send
         */
        public void sendMessage(String message){
            if(connectionSocket.isClosed()==false && connectionSocket.isConnected() && connectionSocket.isOutputShutdown()==false ) toClient.println(message); 
            else System.out.printf(" sendMessage :: outbound connection failed !") ;
        }

        /**
         * reads clients message if exists
         * @param Blocked 
         *   if Blocked == true it calls fromClient.nextLine() which blocks waiting
         *   else, for 3 tries, each after 10msec, it checks if iputstream has data and if yes then calls fromClient.nextLine()
         * try to avoid blocking waiting
         */
        public String readMessage(boolean Blocked) {
            String clientMessage="";
            String lastClientMessage="";
            int tries=0;
            try {       
                if(Blocked) {
                    // if blocked == true 
                    // must wait client's response anyway
                    clientMessage = fromClient.nextLine();
                    lastClientMessage=clientMessage;
                    clientMessage="";
                } else {
                    // try 3 times every 10 msec to see if client has send something else return
                    // for example: client sent Quit
                    // used when i want to check if clients has send a Quit, before send to client next instruction
                    // so i dont want to be blocked by nextline() because client possibly has not send anything
                    while(tries<3) {                        
                        if(connectionSocket.getInputStream().available()>0){
                            clientMessage = fromClient.nextLine();
                            System.out.println(String.format(" SERVERTHREAD (%d) readMessage:: client sent message: %s",clientNumber, clientMessage));
                            if(!clientMessage.trim().isEmpty()){
                                // player=0&action=requeststartgame
                                //processPlayerRequest(clientMessage);
                                lastClientMessage=clientMessage;
                                clientMessage="";                            
                            }
                            break; // if 3 times no data, then return
                        } else {                   
                            Thread.sleep(10);
                        }
                        tries++;
                    }
                }
            } catch(Exception e){

            }   
            return lastClientMessage;            
        }

        /**
         *  will ask this thread's connected client to register a second king 
        */
        public void register2ndKing() {

            // if client has send a quit game ....
            String response = readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    updatePlayerStop(clientNumber,gNumOfPlayers,"none");
                    return;                    
                }
            }

            //pollClient = false;
            String message = String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&registeredplayers=%s", gplayMode, 
            clientNumber, gNumOfPlayers, gNumOfKings, ServerEnums.ServerActions.NEWKINGREGISTRATION.getName(),getRegisteredPlayersString() );
            System.out.println(String.format(" SERVERTHREAD (%d) register2ndKing:: Sending to client: %s", clientNumber,  message));
            toClient.println(message);
            // String line = fromClient.nextLine();
            String line = readMessage(true);
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
            System.out.println(String.format(" SERVERTHREAD (%d) register2ndKing:: received from client: %s", clientNumber,  line));
            // 1;registernewking;geo;blue
            if(line.length()>0 ) {

                String[] arrayFields = line.split(";");

                if( arrayFields[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    updatePlayerStop(clientNumber,gNumOfPlayers,"none");
                    return;                    
                }

                this.player2 = new Player();
                this.player2.playerName = arrayFields[3]; // arrayFields[2]; // playerName
                this.player2.playerColor = arrayFields[4]; // arrayFields[3]; // playColor
                this.player2.playerSocket = this.connectionSocket;
                this.player2.playerNumber = gNumOfPlayers;
                this.player2.kingNumber = gNumOfKings;
                this.player2.client = this;
                this.player2.clientNumber=this.clientNumber;
                this.player2.isRegistered=true;
                
                // thread-safe add player and modifiy main thread variables
                addPLayerClient(this.player2);
            }

            System.out.println(String.format(" SERVERTHREAD (%d) register2ndKing:: new player/king added. players  now are : ",clientNumber));
            for (Player p : gPlayers) {
                System.out.println(p);
            }
            
            //pollClient = true;

        }

        /**
        *  will ask this thread's connected client to register 
        */
        public boolean registerPlayer(  ) {
          
            /* message example
             * playmode=2&client=0&player=0&king=0&action=registration&registeredplayers=1;george;red@2;thanos;yellow@....
             * or
             * playmode=2&client=0&player=0&king=0&action=registration&registeredplayers=none
             * actions are: registration, play
             */

            // if client has send a quit game ....
            String response = readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    updatePlayerStop(clientNumber,gNumOfPlayers,"none");
                    return false;                    
                }
            }

            String message = "playmode="+Integer.toString(gplayMode);
            message += "&client="+Integer.toString(gNumOfClients);
            message += "&player="+Integer.toString(gNumOfPlayers);
            message += "&king="+Integer.toString(gNumOfKings);
            message += "&action="+ ServerEnums.ServerActions.REGISTRATION.getName();
            message += "&registeredplayers=";
            try {

                String playersString = "";
                int _cnt = 0;
                for (Player p : gPlayers ) {
                    if ( p != null ){
                        if (_cnt == 0) playersString += p.toString();
                            // "1;george;red@2;thanos;yellow@...."
                        else playersString += "@" + p.toString();
                    }
                     _cnt++;
                }
                if ( playersString.length() == 0 ) {
                    message += "none"; 
                }
                else {
                    message += playersString;
                }
                System.out.println(String.format(" SERVERTHREAD (%d) registerPlayer:: Sending to client: %s", clientNumber,  message));
                // sending to client(s)
                toClient.println(message);

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
                //String line = fromClient.nextLine();
                String line = readMessage(true);
                if(line.length()>0) { 
                    // waiting for registration response ...
                    // client register message, e.g.:: "registerplayer;george;red" ... optional ... ";action=requeststartgame"
                    String[] arrayFields = line.split(";");   

                    if( arrayFields[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                        updatePlayerStop(clientNumber,gNumOfPlayers,"none");
                        return false;                    
                    }
                    
                    System.out.println("Received: " + line);
                    this.player1 = new Player();
                    this.player1.playerName = arrayFields[3]; // arrayFields[1]; // playerName
                    this.player1.playerColor = arrayFields[4]; // arrayFields[2]; // playColor
                    this.player1.playerSocket = this.connectionSocket;
                    this.player1.playerNumber = gNumOfPlayers;
                    this.player1.kingNumber = gNumOfKings;
                    this.player1.client = this;
                    this.player1.clientNumber=this.clientNumber;
                    this.player1.isRegistered=true;

                    // thread-safe add player and modifiy main thread variables
                    addPLayerClient(this.player1);
                
                    System.out.println(String.format("   -- (%d) registerPlayer:: new player added. current players : ",clientNumber));
                    for (Player p : gPlayers) {
                        System.out.println(p);
                    }
                    if (arrayFields.length>6) {
                        
                        System.out.println(String.format("   -- (%d) registerPlayer:: arrayFields.length > 6 : %s",clientNumber, arrayFields[3]));
                        String action = arrayFields[6];
                        String[] afv=action.split("=");
                        if(afv[0].contains("subaction") && afv[1].contains("requeststartgame")){
                        // ... optional ... ";subaction=requeststartgame"  exists
                        System.out.println(String.format("   -- (%d) registerPlayer:: arrayFields.length > 6 : %s clientNumber=%d, gNumOfClients=%d, gGameStatus=%s",clientNumber, arrayFields[6],clientNumber,gNumOfClients,gGameStatus.getName()));
                        
                        if(clientNumber>0 && (gNumOfClients==1 || gNumOfClients==2)){ // remember, starting from 0 !!!!!!
                                if(gGameStatus == ServerEnums.GameStatus.REGISTRATION) {
                                    gStartgamerequest=true;
                                    System.out.println(String.format("   -- (%d) registerPlayer:: gStartgamerequest=true ",clientNumber));
                                }
                            }                         
                        }
                    }
                    return true;
                } 
                else return false;
                
            } catch (Exception e){
           
                System.out.println(e);
                return false;
           
            }
            
        }

    
        /** 
        * NOT USED first process the response of client message 
        */
        public void processPlayerRequest(String Message){

            System.out.println(String.format(" SERVERTHREAD (%d) processPlayerRequest:: client sent message: %s",clientNumber, Message));
            //pollClient = false;
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
                /*  old 
                0;0;George;red;action=requeststartgame       
                0; [0]
                0; [1]
                George; [2]
                red;    [3] 
                action=requeststartgame [4]
                */
            String[] flds = Message.split(";");
            String[] actionv = flds[5].split("=");
            if(actionv[1].equals(ServerEnums.ClientActions.QUIT.getName())){
                System.out.printf(" === processPlayerRequest ::  Received QUIT ! %d");
                updatePlayerStop(Integer.parseInt(flds[0]), Integer.parseInt(flds[1]), flds[3]);
            }
            //else if(actionv[1].equals(ClientActions.QUIT.getName()))
            //pollClient = true;
        }

        /**
         * sends players registred information string to client
         * checks if client responded with QUIT game message
         * playmode=n&client=n&player=n&king=n&action=playersjoined&registeredplayers=xxxxxxxxx
         * where registeredplayers=clnt0no;plr0no;plr0name;king0no;pllr0colr@clnt1no;plr1no;plr1name;king1no;pllr1colr@....
         */
        public void sendRegisteredPlayersInfo(){

            // if client has send a quit game ....
            String response = readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    updatePlayerStop(clientNumber,0,"none");
                    return;                    
                }
            }

            // String playersstr = "&registeredplayers="+getRegisteredPlayersString();            
            if(this.player1!=null) {  
                
                //pollClient=false;

                String message = String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&registeredplayers=%s",gplayMode,this.clientNumber,this.player1.playerNumber,
                this.player1.kingNumber,ServerEnums.ServerActions.PLAYERS_JOINED.getName(),getRegisteredPlayersString());
                sendMessage(message);
                response = readMessage(true);
                if(response.length()>0) {  
                    String[] arflds = response.split(";");
                    if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                        updatePlayerStop(clientNumber,0,"none");
                        return;                    
                    } // else we get DONE ...
                }
               // pollClient=true;
            }

            // if client has send a quit game ....
            response = readMessage(false);
            if(response.length()>0) {  
                String[] arflds = response.split(";");
                if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                    updatePlayerStop(clientNumber,0,"none");
                    return;                    
                }
            }

            if(this.player2!=null) {   
                //pollClient=false;          
                String message = String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&registeredplayers=%s",gplayMode,this.clientNumber,this.player2.playerNumber,
                this.player2.kingNumber,ServerEnums.ServerActions.PLAYERS_JOINED.getName(),getRegisteredPlayersString());
                System.out.println(" sending message PLAYERS_JOINED to current player: "+message);
                sendMessage(message);
                // wait for response
                // fromClient.nextLine();
                response = readMessage(true);
                if(response.length()>0) {  
                    String[] arflds = response.split(";");
                    if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                        updatePlayerStop(clientNumber,0,"none");
                        return;                    
                    } // else we get DONE ...
                }                
                //pollClient=true;
            }

        }

    }
//=========================END OF THREAD=============================================

    /**
     * this starts the MTserver object created by static main
     * here realized the game play sequence
     * Server calls a client for a specific reason and client responds
     * client can send QUIT response instead of a reply to server's request
     */
    public void startServer(){


        this.gGameStatus = ServerEnums.GameStatus.INIT;

        this.ServerStatusString = "SERVER started.";

        System.out.println(this.ServerStatusString);

    
        deck = new Deck();
        this.ServerStatusString = "SERVER Deck size = "+Integer.toString(deck.decTiles.size());
        System.out.println(this.ServerStatusString);
        this.totalTiles = deck.getDecTiles();
        System.out.println("SERVER totalTiles size = "+Integer.toString(this.totalTiles.size()));
        this.ServerStatusString = "SERVER Deck size = "+Integer.toString(this.totalTiles.size());

        if(gplayMode==4) {
            gMaxNumOfPlayers=4;
            gMaxNumofKings=4;
            gNumofTiles=48;
            numofTilesPerDrow=4;
        }
        else if(gplayMode==3) {
            gMaxNumOfPlayers=3;
            gMaxNumofKings=3;
            gNumofTiles=36;
            numofTilesPerDrow=3;
        }
        else if(gplayMode==2) {
            gMaxNumOfPlayers=2;
            gMaxNumofKings=4;
            gNumofTiles=24;
            numofTilesPerDrow=4;
        }
        System.out.println("SERVER gplayMode = "+Integer.toString(gplayMode)+" gNumOfPlayers="+Integer.toString(gMaxNumOfPlayers));
  

        gGameStatus = ServerEnums.GameStatus.REGISTRATION;    /* new game, regisrtation phase first */
        gNumOfClients = 0;

        System.out.println("SERVER try to listen on port "+Integer.toString(serverPort));
        try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
            System.out.println("SERVER is listening to port "+Integer.toString(serverPort)+" ...");

            //#region ====== FIRST CLIENT =====
            //while(true) {
                
                this.ServerStatusString = String.format("SERVER (port %d), Waiting for first client to connect...",serverPort);

                System.out.println("SERVER Waiting for first client to connect..."); 

                gClients[gNumOfClients] = new MTServerThread(serverSocket.accept(), gNumOfClients);
                gClients[gNumOfClients].start();

                if( gClients[gNumOfClients].registerPlayer() == false) {
                    System.out.println("SERVER, client 1 Failed to register, Quiting...");

                    this.ServerStatusString = "SERVER, client 1 Failed to register, Game ABorted.";
                    this.gGameStatus=ServerEnums.GameStatus.GAME_ABORTED;
                    
                    return;
                    // System.exit(-1);
                }                
                
                gNumOfClients++; 
                //break;
           // }
            // #endregion

            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            //#region ====== SECOND CLIENT and 3RD CLIENT =====
            while(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED) {
                System.out.println("SERVER Waiting for second client to connect..."); 

                this.ServerStatusString = "SERVER Waiting for second client to connect";

                gClients[gNumOfClients] = new MTServerThread(serverSocket.accept(), gNumOfClients);
                gClients[gNumOfClients].start(); 

                /*while(gClients[gNumOfClients].hasError==false && gClients[gNumOfClients].isActive==false){
                    try{
                        Thread.sleep(10);
                        if(gStartgamerequest==true) break;
                    } catch(Exception e){
    
                    }                    
                }*/
                if( gClients[gNumOfClients].registerPlayer() == false) {
                    System.out.println("SERVER, client 2 Failed to register, Quiting...");
                    this.ServerStatusString = "SERVER, client 2 Failed to register, Quiting";
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                    return;
                    //System.exit(-1);
                }                 
                gNumOfClients++;                
                break;
            }

            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            /*for(int i=0;i>5;i++) {
                try{
                    Thread.sleep(100);
                    if(gStartgamerequest==true) break;
                } catch(Exception e){

                }
            }*/

            // when on 2nd Client, Register and Start game is possible
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED && gStartgamerequest ==true) {
                // ... ask clients to choose 2nd king
                System.out.println("SERVER Startgame Request Recieved, 2 clients, Asking the two clients to register 2nd king ...");
                gGameStatus=ServerEnums.GameStatus.NEWKINGREGISTRATION;
                this.ServerStatusString = "SERVER Startgame Request Recieved, 2 clients, Asking the two clients to register 2nd king";
 
                gplayMode = 2;
                gMaxNumOfPlayers=4;
                numofTilesPerDrow=4;
                for(int i=0; i<gNumOfClients;i++){
                    gClients[i].register2ndKing();                    
                }
                if(gStopgamerequest==true) {
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                    this.ServerStatusString = "Stop request received. Game Aborted";
                }

            }
            else {

                // ======= send REGISTRATION info to all players  ========
                for(int i=0; i<gNumOfClients;i++){
                        gClients[i].sendRegisteredPlayersInfo();                   
                }
                
                //====== third CLIENT =======
                while(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED) {
                    System.out.println("SERVER Waiting for third client to connect..."); 
                    this.ServerStatusString = "SERVER Waiting for third client to connect";
                    gClients[gNumOfClients] = new MTServerThread(serverSocket.accept(), gNumOfClients);
                    gClients[gNumOfClients].start(); 

                    if( gClients[gNumOfClients].registerPlayer() == false) {
                        System.out.println("SERVER, client 3 Failed to register, Quiting...");
                        this.ServerStatusString = "SERVER, client 3 Failed to register, Aborted";
                        gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                        return;
                        //System.exit(-1);
                    }                     
                    gNumOfClients++;                
                    break;
                }
            }
            // #endregion            

            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            //#region 4TH CLIENT
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED && gStartgamerequest ==true) {
                System.out.println("SERVER Startgame Request Recieved, 3 clients, starting the game...");
                this.ServerStatusString = "SERVER Startgame Request Recieved, 3 clients, starting the game";
                gplayMode = 3;
                gMaxNumOfPlayers=3;
                numofTilesPerDrow=3;
            }
            else {

                // ======= send REGISTRATION info to all players  ========
                for(int i=0; i<gNumOfClients;i++){
                    gClients[i].sendRegisteredPlayersInfo();                   
                }

                while(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED) {
                    System.out.println("SERVER Waiting for Forth client to connect...");
                    this.ServerStatusString = "SERVER Waiting for Forth client to connect"; 
                    gClients[gNumOfClients] = new MTServerThread(serverSocket.accept(), gNumOfClients);
                    gClients[gNumOfClients].start(); 

                    if( gClients[gNumOfClients].registerPlayer() == false) {
                        System.out.println("SERVER, client 4 Failed to register, Quiting...");
                        gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                        return;
                        // System.exit(-1);
                    }                     
                    gNumOfClients++;  
  
                    gplayMode = 4;
                    gMaxNumOfPlayers=4; 
                    numofTilesPerDrow=4;            
                    break;
                }
            }
            // #endregion
        
            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            // ======= send REGISTRATION info to all players  ========
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED ) {
                for(int i=0; i<gNumOfClients;i++){
                    gClients[i].sendRegisteredPlayersInfo();                   
                }
            }
    
            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }
        
            String firstrow = "";
            int row = 0;
            String response = "";


            // ======= suffle the player order in help playerNos arraylist
            int[] playerNos = randomReorderPlayers();
            System.out.print(" SERVER, reordered players =[");
            for(int i=0;i<playerNos.length;i++){
                System.out.printf("%d,",playerNos[i]);
            }
            System.out.println("]");

            // ================ POSITION_CASTLE =======================
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED ) {
                gGameStatus = GameStatus.CASTLE_POSITIONING;
                PositionCastle(playerNos);
            }

            //  ============== STARTING_ROW ===========================================
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED ) {
                gGameStatus = GameStatus.STARTING_ROW;
                row = 1;
                StartingRow(playerNos);
            }

            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            // ============== PLACE_KING (wait TILE_PICKED) then UPDATE_PLACEMENT  ===========================================
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED ) {
                gGameStatus = ServerEnums.GameStatus.KINGS_PLACEMENTS;
                System.out.println(String.format("SERVER placing kings , num of players = %d, Gamestatus=%s",gNumOfPlayers,gGameStatus.getName()));

                PlaceKing(playerNos);
            }

            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            // ============== update players with placement ===========================================
            /*
                if(gGameStatus != GameStatus.GAME_ABORTED ) {
                    gGameStatus=GameStatus.KINGS_PLACEMENTS;
                    System.out.println(String.format("SERVER placing kings , num of players = %d, Gamestatus=%s",gNumOfPlayers,gGameStatus.getName()));
                    
                    for(int pt=0;pt<gNumOfPlayers;pt++) {

                        int pturn = playerNos[pt];
                        System.out.printf("SERVER KINGS_PLACEMENTS player turn = %d %n",pturn);
                        Player p=gPlayers[pturn];

                        if(gStopgamerequest==true) { gGameStatus = GameStatus.GAME_ABORTED; break ; }

                        firstrow = rowOfTiles(drow2ndLine);

                        response = p.client.readMessage(false);
                        if(response.length()>0) {  
                            String[] arflds = response.split(";");
                            if( arflds[5].contains(ClientActions.QUIT.getName())){
                                gStopgamerequest=true;
                                gGameStatus = GameStatus.GAME_ABORTED; 
                                break;                    
                            }
                        }

                        String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&firstrow=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
                            ServerActions.UPDATE_PLACEMENT.getName(),firstrow);
                        System.out.println(String.format("SERVER sent to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
                        p.client.sendMessage(message);
                        response = p.client.readMessage(true);
                        if(response.length()>0) {  
                            String[] arflds = response.split(";");
                            if( arflds[5].contains(ClientActions.QUIT.getName())){
                                gStopgamerequest=true;
                                gGameStatus = GameStatus.GAME_ABORTED; 
                                break ;
                            }
                        }
                        System.out.println(String.format("SERVER received response from player: %s",response));
                    }
                }
            */

            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            //  ============== UPDATE_FIRSTROW ===========================================
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED ) {
                gGameStatus = ServerEnums.GameStatus.UPDATE_FIRSTROW;
                UpdateFirstRow(playerNos);
            }
        
            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            //#region ============== SUBSEQ_ROW , PLACE_TILE_ON_BOARD (wait TILE_PLACED), PLACEAGAIN_KING (wait TILE_PICKED) loop ===========================================
           
            System.out.println(String.format("SERVER to Drow Next row, gameTiles size now is %d ", this.gameTiles.size()));
            while( gGameStatus != ServerEnums.GameStatus.GAME_ABORTED && gameTiles.size() >= numofTilesPerDrow ) {

                if(gameTiles.size() <= numofTilesPerDrow){
                    System.out.println("***  Last Tiles Row ***");
                }
                gGameStatus = ServerEnums.GameStatus.SUBSEQ_ROW;
                row = SubseqRow(playerNos,  row);

                if(gStopgamerequest==true) {
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                    this.ServerStatusString = "Stop request received. Game Aborted"; 
                    continue;
                }

                gGameStatus = ServerEnums.GameStatus.DOMINO_POSITIONING;
                PlaceTileOnBoard( playerNos);

                if(gStopgamerequest==true) {
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    this.ServerStatusString = "Stop request received. Game Aborted";
                    continue;
                }

                gGameStatus = ServerEnums.GameStatus.KINGS_PLACEMENTS;
                PlaceKingAgain( playerNos);
                        
                if(gStopgamerequest==true) { 
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                    this.ServerStatusString = "Stop request received. Game Aborted"; 
                    continue;
                }

                gGameStatus = ServerEnums.GameStatus.UPDATE_FIRSTROW;
                UpdateFirstRow(playerNos);

                if(gStopgamerequest==true) { 
                    gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                    this.ServerStatusString = "Stop request received. Game Aborted";
                    continue;
                }

            }

            gGameStatus = ServerEnums.GameStatus.DOMINO_POSITIONING;
            // last placement ...
            PlaceTileOnBoard( playerNos);

            if(gStopgamerequest==true) {
                gGameStatus = ServerEnums.GameStatus.GAME_ABORTED;
                this.ServerStatusString = "Stop request received. Game Aborted";
            }

            // #endregion

            // ============== GAME_END ===========================================
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED ) {
                    gGameStatus = ServerEnums.GameStatus.GAME_FINISHED;
                    GameEnd(playerNos);
            }

            String winnerInfo=""; 

            // ============== SEND_SCORE ===========================================
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED ) {
                winnerInfo= SendScore(playerNos);
            }

            // ============== WINNER ===========================================
            if(gGameStatus != ServerEnums.GameStatus.GAME_ABORTED ) { 
                
                Winner(playerNos, winnerInfo);

                System.out.println(String.format("SERVER prepare WINNER "));

                for (int pt = 0; pt < gNumOfPlayers; pt++) {
                    int pturn = playerNos[pt];
                    System.out.printf("SERVER WINNER player turn = %d %n",pturn);
                    Player p=gPlayers[pturn];
                    //Player p = gPlayers[i];

                    response = p.client.readMessage(false);
                    if(response.length()>0) {  
                        String[] arflds = response.split(";");
                        if( arflds[5].contains(ServerEnums.ClientActions.QUIT.getName())){
                            gStopgamerequest=true;
                            gGameStatus = ServerEnums.GameStatus.GAME_ABORTED; 
                            this.ServerStatusString = "Stop request received. Game Aborted";
                            break;                    
                        }
                    }

                    String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s&winner=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
                        ServerEnums.ServerActions.WINNER.getName(),winnerInfo);
                    p.client.sendMessage(message);

                    // wait to recieve DONE
                    response = p.client.readMessage(true);
                    
                }

            }

            //  ============== GAME_ABORTED ===========================================
            if( gStopgamerequest==true || gGameStatus == ServerEnums.GameStatus.GAME_ABORTED  ) {
                this.ServerStatusString = "Stop request received. Game Aborted";
                System.out.println(String.format("SERVER prepare GAME_ABORTED "));

                for(int pt=0;pt<gNumOfPlayers;pt++) {
                    int pturn = playerNos[pt];
                    System.out.printf("SERVER GAME_ABORTED player turn = %d %n",pturn);
                    Player p=gPlayers[pturn];
                    //Player p=gPlayers[i];
                   // p.client.pollClient=false;
                    response = p.client.readMessage(false); // get last messages from client
                    String message=String.format("playmode=%d&client=%d&player=%d&king=%d&action=%s", gplayMode, p.client.clientNumber, p.playerNumber, p.kingNumber, 
                        ServerEnums.ServerActions.GAME_ABORTED.getName());    
                    System.out.println(String.format("SERVER sent to player %d, client %d, message: %s",p.playerNumber,p.clientNumber,message));
                    p.client.sendMessage(message);
                    response = p.client.readMessage(false); // get last messages from Client
                    p.client.closeConnection();
                    
                }
            }

        
            System.out.println(String.format("SERVER prepare CLOSING CONNECTIONS "));
            for (int i = 0; i < gNumOfPlayers; i++) {
                // for every client do: send GAME_END
                Player p = gPlayers[i];
                p.client.closeConnection();
            }
            this.ServerStatusString = "All connections closed";



        }
        catch (IOException e) {
            System.out.println(e);
        }

        

    }


    public void run() {
        startServer();
    }

   
    // /** 
    //  * static main. it creates a new MTServer object and starts it calling its method startServer();
    //  * @param args
    //  */
    // public static void main(String[] args) {

    //     //MTServer server = new MTServer();
    //     //server.startServer();

    // }



    
}
