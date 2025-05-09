package gr.uop;
import java.io.FileInputStream;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
/**
 * represents one square (landscape) in a domino tile
 * each domino tile has two squares (landscapes)
 * a spacial landscape of type castle is used for castle square on board
 */
public class Square implements Comparable<Square> {

    /**
     * enum for square types LANDSCAPE, CASTLE, EMPTY
     */
    public enum SquareType{
        LANDSCAPE("landscape"),
        CASTLE("castle"),
        EMPTY("empty"),
        TILE("tile"),
        UNKNWON("unknown");

        private final String name;

        private SquareType(final String name){
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
     * to be discarded , enum for square colors according to landscape, YELLOW, GREEN, GRAY, BLUE, LIGHTGRAY, BROWN, RED, WHITE, BLACK
     */
    public enum SquareColor{
           YELLOW("yellow"),
           GREEN("green"),
           GRAY("gray"),
           BLUE("blue"),
           LIGHTGRAY("lightgray"),
           BROWN("brown"),
           RED("red"),
           WHITE("white"),
           BLACK("black");

        private final String name;

        private SquareColor(final String pname){
            this.name = pname;
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
     * enum for square landscape type  FARM, FIELD, FOREST, LAKE, LAND, ROCKS, CASTLE,UNKNWON
     */ 
    public enum LandscapeType{
        FARM("farm"),
        FIELD("field"),
        FOREST("forest"),
        LAKE("lake"),
        LAND("land"),
        ROCKS("rocks"),
        CASTLE("castle"),
        UNKNWON("unknown"),
        NONE("none");

     private final String name;

     private LandscapeType(final String pname){
         this.name = pname;
     }
     /**
      * returns the name of the enum
      * @return
      */
     public String getName(){
         return this.name;
     }
 }
    /*
     * the domino number that this square belongs
     * 
     */
    public int tileAa;
    /**
     * the square number in domino 1=left , 2=right as in domins.txt
     */
    public int whichSqr;
    /**
     * the square type, eg. farm1c
     */
    public String Square;
    /**
     * number of crowns on this square
     */
    public int crowns;
    /**
     * square color in Hex (javafx Color) defined by its main type
     * this is for grouping same landscape squares and for
     * drawing background  wihtout pictures
     */
    public Color color;
    /**
     * square color string defined by its main type
     * this is for grouping same landscape squares and for
     * drawing background  wihtout pictures eg. yellow
     */
    public String strColor;
    /**
     * the king number that reserved this square-domino
     * initial value -1 = no king
     */
    public int kingNumber;
    /**
     * the player number that reserved this square-domino
     * initial value -1 = no player
     */
    public int playernumber;
    /**
     * the client number that reserved this square-domino
     * initial value -1 = no client
     */
    public int clientnumber;
    /**
     * the player/king color that reserved this square-domino
     * initial value 'none' = no player
     */
    public String playercolor;
    /**
     * the tile (domino) object this square belongs
     */
    public Tile theTile;
    /**
     * the row of board (GridPane - Kingdom) this square placed
     * initial value -1 = not in board
     */
    public int rowonBoard;
    /**
     * the column of board (GridPane - Kingdom) this square placed
     * initial value -1 = not in board
     */
    public int colonBoard;

    public ImageView castleImageView;
    public ImageView landscapeImageView;
    /**
     * the type of square LANDSCAPE, CASTLE, EMPTY
     */
    public SquareType squareType ;
    /**
     * the type of the landscape FARM, FIELD, FOREST, LAKE, LAND, ROCKS, CASTLE,UNKNWON
     */
    public LandscapeType landscapeType ;
    /**
     * to be discarded, square colors according to landscape, YELLOW, GREEN, GRAY, BLUE, LIGHTGRAY, BROWN, RED, WHITE, BLACK
     */
    public SquareColor enumSquareColor;


    public int relativeRow;
    public int relativeCol;

    /**
     * flag is counted when calculating score
     */
    public boolean countedInScore;
    public int hashc;

    /**
     * basic constructor
     * @param aa Tile number (domino number)
     * @param whichsqr the square number in domino 1=left , 2=right as in domins.txt 
     * @param crowns number of crowns on this square
     * @param sqr the square type, eg. farm1c
     * @param kingn the king number that reserved this square-domino
     * @param plrn the player number that reserved this square-domino
     * @param cltn the client number that reserved this square-domino
     * @param plrclr the player/king color that reserved this square-domino
     * @param thetile the tile (domino) object this square belongs
     */
    public Square(int aa, int whichsqr, int crowns, String sqr, int kingn, int plrn, int cltn, String plrclr, Tile thetile){
        this.tileAa=aa;
        this.whichSqr=whichsqr;
        this.crowns=crowns;
        this.Square=sqr;
        this.kingNumber=kingn;
        this.playernumber=plrn;
        this.clientnumber=cltn;
        this.playercolor=plrclr;
        this.strColor="none";
        this.theTile = thetile;
        this.rowonBoard=-1;
        this.colonBoard=-1;
        this.relativeRow=-1;
        this.relativeCol=-1;
        if( this.Square.equals("farm") || this.Square.equals("farm1c")){
            this.color=Color.YELLOW;
            this.strColor="yellow";
            this.squareType=SquareType.LANDSCAPE;
            this.landscapeType=LandscapeType.FARM;
            this.enumSquareColor = SquareColor.YELLOW ;
        }
        else if( this.Square.equals("field") || this.Square.equals("field1c") || this.Square.equals("field2c")){
            this.color=Color.GREEN;
            this.strColor="green";
            this.squareType=SquareType.LANDSCAPE;
            this.landscapeType=LandscapeType.FIELD;
            this.enumSquareColor = SquareColor.GREEN ;
        }
        else if( this.Square.equals("forest") || this.Square.equals("forest1c")){
            this.color=Color.GRAY;
            this.strColor="gray";
            this.squareType=SquareType.LANDSCAPE;
            this.landscapeType=LandscapeType.FOREST;
            this.enumSquareColor = SquareColor.GRAY ;

        }
        else if( this.Square.equals("lake") || this.Square.equals("lake1c")){
            this.color=Color.BLUE;
            this.strColor="blue";
            this.squareType=SquareType.LANDSCAPE;
            this.landscapeType=LandscapeType.LAKE;
            this.enumSquareColor = SquareColor.BLUE ;

        }
        else if( this.Square.equals("land") || this.Square.equals("land1c") || this.Square.equals("land2c")){
           this.color=Color.LIGHTGRAY;
           this.strColor="lightgray";
           this.squareType=SquareType.LANDSCAPE;
           this.landscapeType=LandscapeType.LAND;
           this.enumSquareColor = SquareColor.LIGHTGRAY ;

        }
        else if( this.Square.equals("rocks") || this.Square.equals("rocks1c") || this.Square.equals("rocks2c") || this.Square.equals("rocks3c")){
            this.color=Color.BROWN;
            this.strColor="brown";
            this.squareType=SquareType.LANDSCAPE;
            this.landscapeType=LandscapeType.ROCKS;
            this.enumSquareColor = SquareColor.BROWN ; 
        }
        else if( this.Square.equals("castle") ){

            if( this.playercolor.contains("red")) {
                this.color=Color.WHITE;   
                this.strColor="white";         
                this.squareType=SquareType.CASTLE;
                this.landscapeType=LandscapeType.CASTLE;
                this.enumSquareColor = SquareColor.RED ;    
            }
            else if( this.playercolor.contains("green")) {
                this.color=Color.WHITE;   
                this.strColor="white";         
                this.squareType=SquareType.CASTLE;
                this.landscapeType=LandscapeType.CASTLE;
                this.enumSquareColor = SquareColor.GREEN ;    
            }
            else if( this.playercolor.contains("blue")) {
                this.color=Color.WHITE;   
                this.strColor="white";         
                this.squareType=SquareType.CASTLE;
                this.landscapeType=LandscapeType.CASTLE;
                this.enumSquareColor = SquareColor.BLUE ;    
            }
            else if( this.playercolor.contains("yellow")) {
                this.color=Color.WHITE;   
                this.strColor="white";         
                this.squareType=SquareType.CASTLE;
                this.landscapeType=LandscapeType.CASTLE;
                this.enumSquareColor = SquareColor.YELLOW ;    
            }
        }
        else if( this.Square.equals("empty") ){
            this.color=Color.WHITE;   
            this.strColor="white";         
            this.squareType=SquareType.EMPTY;
            this.landscapeType=LandscapeType.NONE;
            this.enumSquareColor = SquareColor.WHITE ;
        }
        else {
            this.color=Color.BLACK;   
            this.strColor="black";         
            this.squareType=SquareType.UNKNWON;
            this.landscapeType=LandscapeType.UNKNWON;
            this.enumSquareColor = SquareColor.BLACK ; 
        }
        this.countedInScore=false;
        if(this.tileAa<0){
            this.hashc = -10000 + (-100)*this.whichSqr + (-1)*this.landscapeType.ordinal();    // -10000 -100 -1...
        }
        else if(this.tileAa==0){
            this.hashc = 100*this.whichSqr + this.landscapeType.ordinal();
        }
        else {
            this.hashc = 10000*this.tileAa + 100*this.whichSqr + this.landscapeType.ordinal(); 
        }
    }
    
    /**
     * constructor, creates a new square from existing one,
     * used for copying one square to a new one
     * @param sq the Square from which the new Square will be copied
     * @param whichsqr the square number in domino (tile)
     */
    public Square( Square sq , int whichsqr){
        this.tileAa=sq.tileAa;
        this.whichSqr=whichsqr;
        this.crowns=sq.crowns;
        this.Square=sq.Square;
        this.kingNumber=sq.kingNumber;
        this.playernumber=sq.playernumber;
        this.clientnumber=sq.clientnumber;
        this.playercolor=sq.playercolor;
        this.strColor=sq.strColor;
        this.color=sq.color;
        this.theTile = null;
        this.rowonBoard=sq.rowonBoard;
        this.colonBoard=sq.colonBoard; 
        this.squareType=sq.squareType;
        this.landscapeType=sq.landscapeType;
        this.enumSquareColor = sq.enumSquareColor ;
        this.squareType=sq.squareType;
        this.countedInScore=false;
        if(this.tileAa<0){
            this.hashc = -10000 + (-100)*this.whichSqr + (-1)*this.landscapeType.ordinal();    // -10000 -100 -1...
        }
        else if(this.tileAa==0){
            this.hashc = 100*this.whichSqr + this.landscapeType.ordinal();
        }
        else {
            this.hashc = 10000*this.tileAa + 100*this.whichSqr + this.landscapeType.ordinal(); 
        }
        
    }

    public SquareColor getEnumColor(){
        if(this.strColor.contains("red")) return SquareColor.RED;
        if(this.strColor.contains("yellow")) return SquareColor.YELLOW;
        if(this.strColor.contains("green")) return SquareColor.GREEN;
        if(this.strColor.contains("gray")) return SquareColor.GRAY;
        if(this.strColor.contains("blue")) return SquareColor.BLUE;
        if(this.strColor.contains("lightgray")) return SquareColor.LIGHTGRAY;
        if(this.strColor.contains("white")) return SquareColor.WHITE;
        return SquareColor.BLACK;
    }
    /**
     * sets the tile (domino) object this square belongs 
     * @param tile Tile
     */
    public void setTile(Tile tile){
        this.theTile = tile;
    }
    
    /** 
     * sets Tile and King Information from the tile this square belongs
     * @param tile Tile
     */
    public void setTileAndKingInfo(Tile tile){
        this.theTile = tile;
        this.kingNumber=tile.kingNumber;
        this.playernumber=tile.playernumber;
        this.clientnumber=tile.clientnumber;
        this.playercolor=tile.playercolor;
    }
    
    /** 
     * sets the king information for the square
     * @param pkingnumber the king number that reserved this square-domino 
     * @param pplayercolor the player color that reserved this square-domino
     * @param pplayernumber the player number that reserved this square-domino
     * @param pclientnumber the client number that reserved this square-domino
     */
    public void setKingInfo(int pkingnumber, String pplayercolor, int pplayernumber, int pclientnumber ){
        this.kingNumber = pkingnumber;
        this.playercolor = pplayercolor;
        this.playernumber = pplayernumber;
        this.clientnumber = pclientnumber;
        // ??
        // this.theTile.setKingInfo(pkingnumber, pplayercolor, pplayernumber, pclientnumber);
    }

    /** 
     * sets the board (kingdom) position this Square placed
     * @param row
     * @param col
     */
    public void setBoardPosition( int row, int col ){
        this.rowonBoard=row;
        this.colonBoard=col;    
    }

    /**
     * set the ImageView of a castle
     * @param imgHeight
     * @param imgWidth
     * @return
     */
    public ImageView setImageviewCastle(int imgHeight, int imgWidth){
        FileInputStream pnginput;
        try {
            if(this.playercolor.equals("red")){
                pnginput = new FileInputStream("castlered.png");
            }
            else if(this.playercolor.equals("green")){
                pnginput = new FileInputStream("castlegreen.png");
            }          
            else if(this.playercolor.equals("blue")){
                pnginput = new FileInputStream("castleblue.png");
            }           
            else if(this.playercolor.equals("yellow")){
                pnginput = new FileInputStream("castleyellow.png");
            }           
            else {
                pnginput = new FileInputStream("blank.png");
            }          
            Image img = new Image(pnginput);
            this.castleImageView = new ImageView(img);
            this.castleImageView.setFitHeight(imgHeight);
            this.castleImageView.setFitWidth(imgWidth);
            return this.castleImageView;
    
        } catch (Exception e) {
            System.out.println("castlexxxxx.png not found");
            return null;
        }      
    }
    /**
     * set the ImageView of a landscape
     * @param imgHeight
     * @param imgWidth
     * @return
     */
    public ImageView setImageviewLandscape( int imgHeight, int imgWidth){
        try {
            String sqrimagename = String.format("%s.png", this.Square);
            FileInputStream pnginput = new FileInputStream(sqrimagename);
            Image sqrimg = new Image(pnginput);
            this.landscapeImageView = new ImageView(sqrimg);
            this.landscapeImageView.setFitHeight(imgHeight);
            this.landscapeImageView.setFitWidth(imgWidth);
            return this.landscapeImageView;
    
        } catch (Exception e) {
            System.out.println("castlexxxxx.png not found");
            return null;
        }      
    }

    /**
     * returns a VBOX Node for this Square
     * @param imgHeight
     * @param imgWidth
     * @return
     */
    public VBox getLandscapeVBox(int imgHeight, int imgWidth) {
        try {
            Label lcolor = new Label(String.format("%s-%d",this.landscapeType.getName().substring(0, 3),this.crowns));
            VBox vb=new VBox();
            ImageView sqrImgview = this.setImageviewLandscape(imgHeight, imgWidth);
            sqrImgview.setUserData(this);
            vb.getChildren().addAll(sqrImgview,lcolor);
            vb.setUserData(this);
            return vb;
        }
        catch (Exception e) {
            System.out.println(String.format(" !!! EXCEPTION !!! Square.setLandscapeBox " ));
            return null;
        }
    } 

    /** 
     * returns Square data in csv format
     * tileno;sqrtype;strcolor;crown;playercolor;kingNumber;playernumber;clientnumber;color;type (always 'square')
     * @return String
     */
    @Override
    public String toString(){
        /*[0]  tilenum
          [1] sqr
          [2] strcolor
          [3] crown
          [4] playercolor
          [5] kingNumber
          [6] playernumber,
          [7] clientnumber
          [8] Color
          [9] whichsqr
          [10] rowonBoard;
          [11] colonBoard;
          [12] landscapeType.getName()
          [13] enumSquareColor.getName()
          [9] type=squareType.getName()
           */
        String retstr = String.format("%d;", this.tileAa);
        retstr += String.format("%s;",this.Square);
        retstr += String.format("%s;",this.strColor);
        retstr += String.format("%d;",this.crowns);
        retstr += String.format("%s;",this.playercolor);
        retstr += String.format("%d;",this.kingNumber);
        retstr += String.format("%d;",this.playernumber);
        retstr += String.format("%d;",this.clientnumber);
        retstr += String.format("%s;",this.color.toString());
        retstr += String.format("%d;",this.whichSqr);
        retstr += String.format("%d;",this.rowonBoard);
        retstr += String.format("%d;",this.colonBoard);
        retstr += String.format("%s;",this.landscapeType.getName());
        retstr += String.format("%s;",this.enumSquareColor.getName());
        retstr += String.format("type=%s",this.squareType.getName());
        //   String retstr = String.format("%d;%s;%s;%d;%s;%d;%d;%d;%s;type=square", this.tileAa, this.Square, this.strColor, this.crowns, this.playercolor, 
        //   this.kingNumber, this.playernumber, this.clientnumber, this.color.toString() );
        return retstr;
    }
    
    
    /** 
     * compares Squares by their domino tile number and their number in domino
     * @param o
     * @return int
     */
    @Override
    public int compareTo(Square o) {
        if(this.tileAa < o.tileAa) return -1;
        else if(this.tileAa > o.tileAa) return 1;
        else {
            if(this.whichSqr < o.whichSqr) return -1;
            else if(this.whichSqr > o.whichSqr) return 1;
            else return 0;
        }
    }


    /*@Override
    public boolean equals(Square obj){
        //if(this.row==obj.row && this.col==obj.col && this.landscape==obj.landscape) return true;
        if(this.tileAa==obj.tileAa && this.whichSqr==obj.whichSqr && this.landscapeType==obj.landscapeType) return true;
        else return false;
    }
    */
    @Override
    public int hashCode(){
        return this.hashc;
    }


}
