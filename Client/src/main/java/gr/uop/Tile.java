package gr.uop;
import javafx.scene.paint.Color;

/*
YELLOW		farm farm1c             FAR
GREEN		field field1c field2c   FIE
GRAY		forest forest1c         FOR
BLUE		lake lake1c             LAK
LIGHT_GRAY	land land1c land2c      LAN
DARK_GRAY	rocks rocks1c rocks2c rocks3c  ROC
PINK		CASTLE                  CAS
RED	
 */

 /**
  * represents a domino tile which consists of two landscapes (squares)
  */
public class Tile implements Comparable<Tile> {
    /**
     * the unique domino number in deck
     * for the special KingTile, aa=0
     */
    public int aa;
    /**
     * the left landscape (as in dominos.txt line)
     */
    public Square Square1;
    /**
     * the right landscape (as in dominos.txt line)
     */
    public Square Square2;
    /**
     * the Castle color in Hex (jafafx Color) for a special KingTile that represents
     * this king castle
     */
    public Color castleColor;
    /**
     * the Castle color string for a special KingTile that represents
     * this king castle,  red|green|blue|yellow
     */
    public String castlecolor;
    
    /**
     * the king number that reserved this domino 
     * initial value -1 (none)
     */
    public int kingNumber;
    /**
     * the player number that reserved this domino 
     * initial value -1 (none)
     */
    public int playernumber;
    /**
     * the client number that reserved this domino 
     * initial value -1 (none)
     */
    public int clientnumber;
    /**
     * the player color/king color that reserved this domino 
     * initial value 'none'
     */
    public String playercolor;
    /**
     * the row in kingdom board its left square placed
     * initial value -1 (none)
     */
    public int row;
    /**
     * the col in kingdom board its left square placed
     * initial value -1 (none)
     */
    public int col;
    /**
     * the col in kingdom board its right square placed
     * initial value -1 (none)
     */
    public int row2;
    /**
     * the row in kingdom board its right square placed
     * initial value -1 (none)
     */
    public int col2;
    /**
     * true if this domin discarded
     * initial value false
     */
    boolean discarded;

    /** 
    * constructor for special KingTile
    * @param castleColor Castle color in hex (javafx Color) value
    */
    public Tile( Color castleColor){
        this.aa=0;      // castle tile always has aa=0
        this.castleColor=castleColor;
        if(this.castleColor==Color.RED) { this.castlecolor="red"; this.playercolor="red";}
        else if(this.castleColor == Color.GREEN) {this.castlecolor="green" ; this.playercolor="green";}
        else if(this.castleColor == Color.BLUE) {this.castlecolor="blue" ; this.playercolor="blue";}
        else if(this.castleColor == Color.YELLOW) {this.castlecolor="yellow" ; this.playercolor="yellow";}
        else this.castlecolor="white" ;
        this.kingNumber=-1;
        this.playernumber=-1;
        this.clientnumber=-1;        
        this.Square1 = new Square(this.aa, 1, 0, "castle",this.kingNumber,this.playernumber,this.clientnumber,this.playercolor,this); 
        this.Square2 = new Square(this.aa, 2, 0, "castle",this.kingNumber,this.playernumber,this.clientnumber,this.playercolor,this); 
        this.row=-1;
        this.col=-1;
        this.row2=-1;
        this.col2=-1;
        this.discarded=false;
    }

    /** 
    * constructor for special KingTile
    * @param color Castle color string value
    */
    public Tile( String color){
        if(color.equals("red")) this.castleColor = Color.RED;
        else if(color.equals("green")) this.castleColor = Color.GREEN;
        else if(color.equals("blue")) this.castleColor = Color.BLUE;
        else if(color.equals("yellow")) this.castleColor = Color.YELLOW;
        else this.castleColor = Color.WHITE;
        this.aa=0;      // castle tile always has aa=0
        this.castlecolor=color;
        this.kingNumber=-1;
        this.playernumber=-1;
        this.clientnumber=-1;
        this.playercolor="none";
        this.Square1 = new Square(this.aa, 1, 0, "castle",this.kingNumber,this.playernumber,this.clientnumber,this.playercolor,this); 
        this.Square2 = new Square(this.aa, 2, 0, "castle",this.kingNumber,this.playernumber,this.clientnumber,this.playercolor,this); 
        //this.crowns2=0;
        this.row=-1;
        this.col=-1;
        this.row2=-1;
        this.col2=-1;
        this.discarded=false;
    }

    /** 
    * constructor 
    * @param aa domino unique number in deck
    * @param crowns1 left square crowns
    * @param crowns2 right square crowns
    * @param sq1 left square type
    * @param sq2 right square type
    */
    public Tile(int aa, int crowns1, int crowns2, String sq1, String sq2){
        this.aa=aa;
        this.kingNumber=-1;
        this.playernumber=-1;
        this.clientnumber=-1;
        this.playercolor="none";
        this.row=-1;
        this.col=-1;
        this.row2=-1;
        this.col2=-1;
        this.discarded=false;
      //  this.crowns1=crowns1;
      //  this.crowns2=crowns2;
      //  this.Square1=sq1;
      //  this.Square2=sq2;
      //  this.strColor1="none";
      //  this.strColor2="none";

        this.Square1 = new Square(this.aa, 1, crowns1, sq1, this.kingNumber,this.playernumber,this.clientnumber,this.playercolor,this); // (int aa, int whichsqr, int crowns, String sqr, int kingn, int plrn, int cltn, String plrclr, Tile thetile)
        this.Square2 = new Square(this.aa, 2, crowns2, sq2, this.kingNumber,this.playernumber,this.clientnumber,this.playercolor,this); // (int aa, int whichsqr, int crowns, String sqr, int kingn, int plrn, int cltn, String plrclr, Tile thetile)

    }

    /** 
    * constructor builds a domino from two Squares
    * @param sqr1 left Square
    * @param sqr2 right Square crowns
    * @param isdiscarded true if it is discarded
    */
    public Tile(Square sqr1, Square sqr2, boolean isdiscarded){
        this.aa=sqr1.tileAa;
        this.kingNumber=sqr1.kingNumber;
        this.playernumber=sqr1.playernumber;
        this.clientnumber=sqr1.clientnumber;
        this.playercolor=sqr1.playercolor;
        this.row=sqr1.rowonBoard;
        this.col=sqr1.colonBoard;
        this.row2=sqr2.rowonBoard;
        this.col2=sqr2.colonBoard;
        this.discarded=isdiscarded;
        this.Square1=sqr1;
        this.Square2=sqr2; 
    }

   /** 
    * constructor builds a domino from another domino (copy)
    * @param fromTile the source Tile
    */
    public Tile( Tile fromTile){
        this.aa=fromTile.aa;
        this.Square1= new Square( fromTile.Square1, 1 );
        this.Square2= new Square( fromTile.Square2, 2 );
        this.castleColor=fromTile.castleColor;
        this.castlecolor=fromTile.castlecolor;
        this.kingNumber=fromTile.kingNumber;
        this.playernumber=fromTile.playernumber;
        this.clientnumber=fromTile.clientnumber;
        this.playercolor=fromTile.playercolor;
        this.row=fromTile.row;
        this.col=fromTile.col;
        this.row2=fromTile.row2;
        this.col2=fromTile.col2;
        this.discarded=fromTile.discarded;

    }

    /** 
    * constructor builds a domino from en existing domino (copy)
    * @param fromTile the source Tile
    * @param reverseSqrs if true then the source Squares are reversed in new Tile
    */
    public Tile( Tile fromTile, boolean reverseSqrs){
        this.aa=fromTile.aa;
        if( reverseSqrs == true) {
            this.Square1=new Square( fromTile.Square2, 1 );
            this.Square2= new Square( fromTile.Square1, 2 );
        } else {
            this.Square1=new Square( fromTile.Square1, 1 );
            this.Square2= new Square( fromTile.Square2, 2 );
        }
        this.castleColor=fromTile.castleColor;
        this.castlecolor=fromTile.castlecolor;
        this.kingNumber=fromTile.kingNumber;
        this.playernumber=fromTile.playernumber;
        this.clientnumber=fromTile.clientnumber;
        this.playercolor=fromTile.playercolor;
        this.row=fromTile.row;
        this.col=fromTile.col;
        this.row2=fromTile.row2;
        this.col2=fromTile.col2;
        this.discarded=fromTile.discarded;
    }

    /** 
    * constructor builds a special domino KingTile form an existing KingTile
    * @param fromkingTile the source KingTile
    */
    public Tile( KingTile fromkingTile){
        this.aa=fromkingTile.aa;
        this.Square1=new Square( fromkingTile.Square1, 1 );
        this.Square2= new Square( fromkingTile.Square2, 2 );
        this.castleColor=fromkingTile.castleColor;
        this.castlecolor=fromkingTile.castlecolor;
        this.kingNumber=fromkingTile.kingNumber;
        this.playernumber=fromkingTile.playernumber;
        this.clientnumber=fromkingTile.clientnumber;
        this.playercolor=fromkingTile.playercolor;
        this.row=fromkingTile.row;
        this.col=fromkingTile.col;
        this.row2=fromkingTile.row2;
        this.col2=fromkingTile.col2;
        this.discarded=fromkingTile.discarded;
    }
    
    /** 
    * sets the King Information for the domino
    * @param pkingnumber the king number that reserved this domino
    * @param pplayercolor the player color that reserved this domino
    * @param pplayernumber the player number that reserved this domino
    * @param pclientnumber the client number that reserved this domino
    */
    public void setKingInfo(int pkingnumber, String pplayercolor, int pplayernumber, int pclientnumber ){
        this.kingNumber = pkingnumber;
        this.playercolor = pplayercolor;
        this.playernumber = pplayernumber;
        this.clientnumber = pclientnumber;
        this.Square1.setKingInfo(pkingnumber, pplayercolor, pplayernumber, pclientnumber);
        this.Square2.setKingInfo(pkingnumber, pplayercolor, pplayernumber, pclientnumber);
    }

    /** 
    * sets the kingdom row,col information for the Square 1
    */
    public void setTileRowCol1( int r, int c){
        this.row=r;
        this.col=c;
    }

    /** 
    * sets the kingdom row,col information for the Square 2
    */
    public void setTileRowCol2( int r, int c){
        this.row2=r;
        this.col2=c;
    }

    public void setDiscarded (boolean discarded){
        this.discarded=discarded;
    }


    public static class SquareInfoFields{
        static final int f_tilenum = 0;
        static final int f_sqr1 = 1;
        static final int f_strcolor1=2;
        static final int f_crown1=3;
        static final int f_sqr2=4;
        static final int f_strcolor2=5;
        static final int f_crown2 =6;
        static final int f_playercolor=7;
        static final int f_kingNumber=8;
        static final int f_playernumber=9;
        static final int f_clientnumber=10;
        static final int f_color1=11;
        static final int f_color2=12;
        static final int f_discarded=13; // no|yes          discarded
        static final int f_row=14;
        static final int f_col=15;
        static final int f_row2=16;
        static final int f_col2=17;
    }
    /** 
    * string returns the domino data in csv format
    * tilenum;sqr1type;strcolor1;crown1;sqr2type;strcolor2;crown2;playercolor;kingNumber;playernumber;clientnumber;color1;color2
    */
    @Override    
    public String toString(){
     /*  [0]  tilenum
       [1] sqr1
       [2] strcolor1
       [3] crown1 
       [4] sqr2
       [5] strcolor2
       [6] crown2 
       [7] t.playercolor
       [8] ,t.kingNumber
       [9], t.playernumber,
       [10] t.clientnumber
       [11] color1
       [12] color2 
        static final int f_tilenum = 0;
        static final int f_sqr1 = 1;
        static final int f_strcolor1=2;
        static final int f_crown1=3;
        static final int f_sqr2=4;
        static final int f_strcolor2=5;
        static final int f_crown2 =6;
        static final int f_playercolor=7;
        static final int f_kingNumber=8;
        static final int f_playernumber=9;
        static final int f_clientnumber=10;
        static final int f_color1=11;
        static final int f_color2=12;
        static final int f_discarded=13; // no|yes          discarded
        static final int f_row=14;
        static final int f_col=15;
        static final int f_row2=16;
        static final int f_col2=17;
       */
        String retstr = String.format("%d;", this.aa);
        retstr += String.format("%s;", this.Square1.Square);
        retstr += String.format("%s;", this.Square1.strColor);
        retstr += String.format("%d;", this.Square1.crowns);
        retstr += String.format("%s;", this.Square2.Square);
        retstr += String.format("%s;", this.Square2.strColor);
        retstr += String.format("%d;", this.Square2.crowns);
        retstr += String.format("%s;", this.playercolor);
        retstr += String.format("%d;", this.kingNumber);
        retstr += String.format("%d;", this.playernumber);
        retstr += String.format("%d;", this.clientnumber);
        retstr += String.format("%s;", this.Square1.color.toString());
        retstr += String.format("%s;", this.Square2.color.toString() );
        if(this.discarded) retstr += String.format("yes;" );
        else  retstr += String.format("no;" );
        retstr += String.format("%d;", this.Square1.rowonBoard);
        retstr += String.format("%d;", this.Square1.colonBoard);
        retstr += String.format("%d;", this.Square2.rowonBoard);
        retstr += String.format("%d;", this.Square2.colonBoard);
        retstr += "type=tile";
        //String retstr = String.format("%s;%s;%d;%s;%s;%d;%s;%d;%d;%d;%s;%s;type=tile", this.aa, this.Square1.Square, this.Square1.strColor, this.Square1.crowns,  this.Square2.Square, this.Square2.strColor, this.Square2.crowns, this.playercolor, this.kingNumber, this.playernumber, this.clientnumber, this.Square1.color.toString(), this.Square2.color.toString() );
        return retstr;
    }

    public String toDebugString(){
        /*  [0]  tilenum
          [1] sqr1
          [2] strcolor1
          [3] crown1 
          [4] sqr2
          [5] strcolor2
          [6] crown2 
          [7] t.playercolor
          [8] ,t.kingNumber
          [9], t.playernumber,
          [10] t.clientnumber
          [11] color1
          [12] color2 
          */
          String retstr = String.format("Square, tileAA=%d", this.aa);
          if( this.Square1 != null ){
            retstr +=  String.format("Square1=%s; Square1.strColor=%s; Square1.whichSqr=%d", this.Square1.Square, this.Square1.strColor, this.Square1.whichSqr);
          } else {
            retstr += " | Square1 = NULL";
          } 
          if( this.Square2 != null ){
            retstr += String.format(" | Square2=%s; Square2.strColor=%s; Square2.whichSqr=%d", this.Square2.Square, this.Square2.strColor, this.Square2.whichSqr);
          }
          else {
            retstr += " | Square2 = NULL";
          }
          retstr +=  String.format(" | plc=%s; kgn=%d; pln=%d; cln=%d; type=tile", this.playercolor, this.kingNumber, this.playernumber, this.clientnumber );
           return retstr;
    }

    /**
    * compares two dominos by their number
    */
    @Override
    public int compareTo(Tile o) {
        if(this.aa < o.aa) return -1;
        else if(this.aa > o.aa) return 1;
        else return 0;
    }

    
}
