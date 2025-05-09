package gr.uop;

import java.util.ArrayList;
import java.util.Comparator;

import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

/**
 * holds palyer information of a client Each King is another player
 */
public class Player { 
    /**
     * gui player number 1rst or 2nd player this client has registered to server
     */
    public int GuiPlayerNumber;
    /** 
     * playernumber server assigned to this player 
    */
    public int playerNumber ;
    /**
     * kingnumber server assigned to this player
     */
    public int kingNumber ;
    /**
     * palyername of this player
     */
    public String playerName;
    /**
     * player color - king color of this player
     */
    public String playerColor;
    /**
     * javafx Color player color - king color of this player 
     */
    public Color PCOLOR;
    /**
     * Clientnumber server assigned to this player
     */
    public int ClientNumber;
    /**
     * player score of this player/king
     */
    public int playerScore;
    /**
     * board GridPane for this player/king
     */
    public Object boardPane;
    /**
     * dominos selection GridPane for this player/king
     */
    public Object seldomPane;
    /**
     * Tab of tabpane for this player/king
     */
    public Object tab;
    /**
     * is castle placed on board for this player
     */
    public boolean castlePlaced;
    /**
     * the square that castle placed on board for this player
     */
    public Square castleSquare;

    public Label lblBoard ; // = new Label("Kingdom. current score=0")


    /**
     * domino tile where king placed in selection row OLD...placed on board for this player
     */
    public Tile kingTile;
    /**
     * is domino tile selected from available dominos and king placed on it for this player
     */
    public boolean tileSelected;
    /**
     * is Square 1 selected from available dominos for this player
     */
    public boolean square1Selected;
    /**
     * is Square 2 selected from available dominos for this player
     */
    public boolean square2Selected;
    /**
     * is Square 1 placed on board for this player
     */
    public boolean square1Placed ;
    /**
     * is Square 2 placed on board for this player
     */
    public boolean square2Placed;
    /**
     * Square 1 selected for the available domino tiles for this player
     */
    public Square selSquare1;
    /**
     * Square 2 selected for the available domino tiles for this player
     */
    public Square selSquare2 ;
    /**
     * should this domino tile (squares) be placed next to king square on board or not 
     */        
    boolean nexttoking=true;  

    /*
     * the square that is fixed on rotations
     */
    //public Square pivotSquare;

    // ArrayList<BoardPos> boardPositions = new ArrayList<BoardPos>();

    /**
     * list of available valid board positions for placement
     */
    ArrayList<BoardTilePos> avBoardPositions = new ArrayList<BoardTilePos>();

    /**
     * selection domino draw line 1
     */
    ArrayList<Tile> playerDrawLineOne ;
    /**
     * selection domino draw line 2
     */
    ArrayList<Tile> playerDrawLineTwo ;

    Timeline KingAnimtimeline; // = new Timeline();
    
    /**
     * the domino squares - Landscapes this player has placed on board
     */
    ArrayList<Square> Landscapes ;

    int countLSsqr = 0;
    int countLSCrowns=0;
    /**
     * comparator for Landscapes Ascending by RowOnBoard
     */
    static private Comparator<Square> ascRowOnBoard;
    /**
     * comparator for Landscapes Ascending by ColOnBoard
     */
    static private Comparator<Square> ascColOnBoard;

    static {    
        /**
         * comparator for ascending sorting Player landscape by row on board 
         */
        ascRowOnBoard = new Comparator<Square>(){
            @Override
            public int compare(Square Sq1, Square Sq2){
                int sq1row =-1;
                int sq2row=-1;
                if(Sq1 !=null) sq1row=Sq1.rowonBoard;
                if(Sq2 !=null) sq2row=Sq2.rowonBoard;
                // Java 7 has an Integer#compare function
                return Integer.compare(sq1row, sq2row);
                // For Java < 7, use 
                // Integer.valueOf(n1).compareTo(n2);
                // DO NOT subtract numbers to make a comparison such as n2 - n1.
                // This can cause a negative overflow if the difference is larger 
                // than Integer.MAX_VALUE (e.g., n1 = 2^31 and n2 = -2^31)
            }
        };
        /**
         * comparator for ascending sorting Player landscape by column on board 
         */
        ascColOnBoard = new Comparator<Square>(){
            @Override
            public int compare(Square Sq1, Square Sq2){
                int sq1col =-1;
                int sq2col=-1;
                if(Sq1 !=null) sq1col=Sq1.colonBoard;
                if(Sq2 !=null) sq2col=Sq2.colonBoard;
                // Java 7 has an Integer#compare function
                return Integer.compare(sq1col, sq2col);
                // For Java < 7, use 
                // Integer.valueOf(n1).compareTo(n2);
                // DO NOT subtract numbers to make a comparison such as n2 - n1.
                // This can cause a negative overflow if the difference is larger 
                // than Integer.MAX_VALUE (e.g., n1 = 2^31 and n2 = -2^31)
            }
        };
    }

    /**
     * constructor 
     * @param guiplayernum the ui palyer number client assign
     * @param plnum player number from server
     * @param kgnum king number from server
     * @param plname player name from registrtion
     * @param plcolor king name from registration
     * @param clientnum client number from server
     */
    public Player(int guiplayernum, int plnum, int kgnum, String plname, String plcolor, int clientnum ){
        this.GuiPlayerNumber = guiplayernum ;
        this.playerNumber=plnum;
        this.kingNumber=kgnum;
        this.playerName=plname;
        this.playerColor=plcolor;
        if(plcolor.equals("red")) this.PCOLOR=Color.RED;
        else if(plcolor.equals("green")) this.PCOLOR=Color.GREEN;
        else if(plcolor.equals("blue")) this.PCOLOR=Color.BLUE;
        else this.PCOLOR=Color.YELLOW;
        this.playerScore=0;
        this.boardPane=null;
        this.seldomPane=null;
        this.castlePlaced=false;
        this.castleSquare=null;
        this.kingTile=null;
        this.tileSelected=false;
        this.square1Selected = false;
        this.square2Selected = false;
        this.square1Placed = false;
        this.square2Placed = false;
        this.selSquare1 = null;
        this.selSquare2 = null;
        this.Landscapes = new ArrayList<Square>();
        this.lblBoard = null;
        //this.Sq1posrow=0;
        //this.Sq1poscol=0;
        //this.Sq2posrow=0;
        //this.Sq2poscol=-1;

    }

    /**
     * set the tab of tabpane for this player
     */
    public void settab(Object ptab){
        this.tab = ptab;
    }
    /**
     * sets the board GridPane for this player
     * @param grp Object GridPane
     */
    public void setboardPane(Object grp){
        
        this.boardPane = grp;
    }
    /**
     * sets the selection dominos GridPane for this player
     * @param grp Object GridPane
     */
    public void setseldomPane(Object grp){
        this.seldomPane = grp;
    }
    /**
     * sets the player label over board for this player
     * @param grp Object GridPane
     */
    public void setplboardLabel( Object l){
        this.lblBoard = (Label)l;
    }

    /*
     * sets the score for this player
     * @param int score
     */
    //public void setScore(int score){
    //    this.playerScore = score;
    //}

    /**
     * adds the placed lanscape of domino into arraylist of players placed landscapes
     * @param square Square
     */
    public void addLandscape( Square square){
        this.Landscapes.add(square);
    }
    /**
     * called to calculate current score of this player
     * @return
     */
    public int CalcScore(){
        this.countLSsqr = 0;
        this.countLSCrowns=0;
        this.playerScore=0;
        int arLandscapesSize = this.Landscapes.size();
        if(arLandscapesSize==0) {
            this.playerScore = 0; 
        }
        else {
            this.playerScore = 0;
            for( Square sq : Landscapes){
                sq.countedInScore=false;
            }
            for(int i=0;i<arLandscapesSize;i++){
                Square sq = this.Landscapes.get(i);
                System.out.printf(" ** Countscore #%d , %d-%s %d ", i, sq.tileAa, sq.landscapeType.getName(),  sq.crowns);
                if(sq.countedInScore==false){
                    // new island start again
                    this.countLSsqr=1;
                    this.countLSCrowns = sq.crowns;
                    sq.countedInScore=true;
                    System.out.printf(" cntsq=%d cntcrowns=%d  %n" , this.countLSsqr,this.countLSCrowns);
                    recCalcScore(sq, i+1);
                    this.playerScore += this.countLSsqr * this.countLSCrowns;
                }
            }
        }
        System.out.printf("** Countscore playerScore=%d  %n" , this.playerScore);
        return this.playerScore;
    }

    /**
     * recursive count near squares of same landscape
     * @param psq
     * @param index
     */
    void recCalcScore(Square psq, int index){
        for(int i=index;i<this.Landscapes.size();i++){
            Square sq = this.Landscapes.get(i);
            System.out.printf(" ** ** recurCountscore #%d , %d-%s %d ", i, sq.tileAa, sq.landscapeType.getName(),sq.crowns);
            if(psq.landscapeType==sq.landscapeType){
                if((psq.rowonBoard+1==sq.rowonBoard || psq.rowonBoard-1==sq.rowonBoard) && sq.countedInScore==false){
                    this.countLSsqr++;
                    this.countLSCrowns += sq.crowns;
                    sq.countedInScore=true;
                    System.out.printf(" cntsq=%d cntcrowns=%d |" , this.countLSsqr,this.countLSCrowns);
                    recCalcScore(sq, i+1);
                }
                if((psq.colonBoard+1==sq.colonBoard || psq.colonBoard-1==sq.colonBoard) && sq.countedInScore==false){
                    this.countLSsqr++;
                    this.countLSCrowns += sq.crowns;
                    sq.countedInScore=true;
                    System.out.printf(" cntsq=%d cntcrowns=%d |" , this.countLSsqr,this.countLSCrowns);
                    recCalcScore(sq, i+1);
                }
            }
        }
        System.out.printf(" %n" );
    }

    public int minColInRow( int row){
        if(this.Landscapes.size()==0) {
            return -1; 
        }
        int mincol =10;
        for(Square s : this.Landscapes){
            if(s.rowonBoard==row && s.colonBoard<mincol) mincol = s.colonBoard;
        }
        if(mincol==10) return -1;
        else return mincol;
    }
    public int maxColInRow( int row){
        if(this.Landscapes.size()==0) {
            return -1; 
        }
        int maxcol =-1;
        for(Square s : this.Landscapes){
            if(s.rowonBoard==row && s.colonBoard>maxcol) maxcol = s.colonBoard;
        }
        return maxcol;
    }
    public int minRowInCol(int col){
        if(this.Landscapes.size()==0) {
            return -1; 
        }
        int minrow =10;
        for(Square s : this.Landscapes){
            if(s.colonBoard==col && s.rowonBoard<minrow) minrow = s.rowonBoard;
        }
        if(minrow==10) return -1;
        else return minrow;

    }
    public int maxRowInCol( int col){
        if(this.Landscapes.size()==0) {
            return -1; 
        }
        int maxrow =-1;
        for(Square s : this.Landscapes){
            if(s.colonBoard==col && s.rowonBoard>maxrow) maxrow = s.rowonBoard;
        }
        return maxrow;
    }

    /**
     * counts number of synexomena squares se ayti ti grammi
     * @param row
     * @return
     */
    public int numofSquaresinRow(int row){
        int mincol = minColInRow( row);
        int maxcol =  maxColInRow(row);
        if(mincol<0 || maxcol<0 ) {
            System.out.printf(" *** numofSquaresinRow %d= %d mincol=%d<0, maxcol=%d<0  %n",row,0,mincol,maxcol );
            return 0;
        }
        System.out.printf(" *** numofSquaresinRow %d= %d mincol=%d, maxcol=%d  %n",row,(maxcol-mincol)+1,mincol,maxcol );
        return (maxcol-mincol)+1;
    }
    /**
     * counts number of synexomena squares se ayti ti stili
     * @param int col
     * @return
     */
    public int numofSquaresinCol(int col){
        int minrow = minRowInCol( col);
        if(minrow<0) {
            System.out.printf(" *** numofSquaresinCol %d= %d mincol=%d<0  %n",col,0,minrow );
            return 0;
        }
        int maxrow =  maxRowInCol(col);
        if(maxrow<0) {
            System.out.printf(" *** numofSquaresinCol %d= %d mincol=%d maxrow=%d<0  %n",col,0,minrow,maxrow );
            return 0;
        }
        System.out.printf(" *** numofSquaresinCol %d= %d mincol=%d maxrow=%d  %n",col,(maxrow-minrow)+1,minrow,maxrow );
        return (maxrow-minrow)+1;
    }

}