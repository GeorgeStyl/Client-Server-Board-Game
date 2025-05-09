package gr.uop;

/** enums form Server actions, Client Actions, Game Status */
public class ServerEnums {

    /**
     * enumerator for Server Actions
     * must much with commmessages at Client    
     */
    public enum ServerActions {
        CONNECTION_ACCEPTED("connectionaccepted"),
        REGISTRATION("registration"),
        NEWKINGREGISTRATION("newkingregistration"),
        NEWKINGREGISTERED("newkingregistered"),
        PLAYERS_JOINED("playersjoined"),
        ENDREGISTRATION("endregistration"),
        POSITION_CASTLE("positioncastle"),
        STARTING_ROW("startingrow"),
        PLACE_KING("placeking"),
        UPDATE_FIRSTROW("updatefirstrow"),
        SUBSEQ_ROW("subseqrow"),
        PLACE_TILE_ON_BOARD("placetileonboard"),
        PLACEAGAIN_KING("placeagainking"),
        UPDATE_PLACEMENT("updateplacement"),
        SEND_BOARD("sendboard"),
        GAME_END("gameend"),
        SEND_SCORE("sendscore"),
        WINNER("winner"),
        GAME_ABORTED("gameaborted"),
        WRONG_MESSAGE("wrongmessage");

        private final String name;

        private ServerActions(final String name){
            this.name = name;
        }

        /**
        * returns the string representation of the action
        */
        public String getName(){
            return this.name;
        }
    }
    
    /**
    * enumerator for Client Actions
    * must much with commmessages at Client    
    */
    public enum ClientActions {
        REGISTER_PLAYER("registerplayer"),
        SET_NAMECOLOR("setnamecolor"),
        GET_REGISTERED_PLAYERS("getregisterplayers"),
        REQUEST_STARTGAME("requeststartgame"),
        REGISTER_NEWKING("registernewking"),
        CASTLE_PLACED("castleplaced"),
        TILE_PICKED("tilepicked"),
        TILE_PLACED("tileplaced"),
        MY_SCORE("myscore"),
        DONE("done"),
        QUIT("quit");
        private final String name;

        private ClientActions(final String name){
            this.name = name;
        }

         /**
        * returns the string representation of the action
        */
        public String getName(){
            return this.name;
        }        
    }

    /**
    * enumerator for Game Actions
    */
    public enum GameStatus {
        INIT("initialize"),
        REGISTRATION("registration"),
        NEWKINGREGISTRATION("newkingregistration"),
        CASTLE_POSITIONING("castlepositioning"),
        STARTING_ROW("startingrow"),
        KINGS_PLACEMENTS("kingsplacements"),
        UPDATE_FIRSTROW("updatefirstrow"),
        SUBSEQ_ROW("subseqrow"),
        DOMINO_POSITIONING("dominopositioning"),
        DROW_TILES("drowtiles"),
        GAME_FINISHED("gamefinished"),
        GAME_ABORTED("gameaborted");
        private final String name;
        private GameStatus(final String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }        
    }

}
