package gr.uop;

/**
 * Publishes messages ServerActions and ClientActions Enums , communications verbs (like http/smtp verbs)
 */
public class CommMessages {
    /**
     * server messages verbs
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

        private ServerActions(final String pname){
            name = pname;
        }
        /**
         * returns the name of the enum
         * @return
         */
        public String getName(){
            return this.name;
        }
    }

    /**
     * client messages verbs
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
         * returns the name of the enum
         * @return
         */
        public String getName(){
            return this.name;
        }        
    }

    /**
     * game status enums NOT USEFULL for Client
     */
    public enum GameStatus {        
        NEW_GAME("newgame"),
        REGISTRATION("registration"),
        NEWKINGREGISTRATION("newkingregistration"),
        STARTING_ROW("startingrow"),
        KINGS_PLACEMENT("kingsplacements"),
        SELECTED_KING("selectedking"),
        PLACED_KING("placedking"),
        SELECT_TILE("selecttile"),
        SELECTED_TILE("selectedtile"),
        PLACED_TILE("placedtile"),
        WAITING_MYTURN("waitingmyturn"),
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
