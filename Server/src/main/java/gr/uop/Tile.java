package gr.uop;

//import javafx.scene.paint.Color;

/** 
* represents a domino (tile) Server usage
*/
public class Tile implements Comparable<Tile> {
    /** 
     * domino unique number in deck
     */
    public int aa;
    /** 
     * Left square (landscape) type, eg. farm1c as in dominos.txt
     */
    public String Square1;
    /** 
     * Left square (landscape) type, eg. farm1c as in dominos.txt
     */
    public String Square2;
    /** 
     * Left square crowns as in dominos.txt
     */
    public int crowns1;
    /** 
     * Right square crowns as in dominos.txt
     */
    public int crowns2;
    /** 
     * Left square color based on type to group same squares farm, farm1c, farm2c
     */
    public String strColor1;
    /** 
     * Right square color based on type to group same squares farm, farm1c, farm2c
     */
    public String strColor2;
    /** 
     * not used
     */
    public String castlecolor;
    /** 
     * king number that reserved this domino
     */
    public int kingNumber;
    /** 
     * player number that reserved this domino
     */
    public int playernumber;
    /** 
     * client number that reserved this domino
     */
    public int clientnumber;
    /** 
     * player color that reserved this domino
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
     * the row in kingdom board its right square placed
     * initial value -1 (none)
     */
    public int row2;
 /**
     * the col in kingdom board its right square placed
     * initial value -1 (none)
     */
    public int col2;
    /**
     * true if this domino discarded
     * initial value false
     */
    boolean discarded;

    /*
    YELLOW		farm farm1c
    GREEN		field field1c field2c
    GRAY		forest forest1c
    BLUE		lake lake1c
    LIGHT_GRAY	land land1c land2c
    DARK_GRAY	rocks rocks1c rocks2c rocks3c
    PINK		CASTLE
    RED	
    */

     /** 
      * NOT USED
     * constructor for special King Tile 
     * @param color
     */   
    public Tile( String color){
        this.aa=0;      // castle tile always has aa=0
        this.castlecolor=color;
        this.crowns1=0;
        this.crowns2=0;
        this.Square1="castle";
        this.Square2="castle";
        this.row=-1;
        this.col=-1;
        this.row2=-1;
        this.col2=-1;
        this.discarded=false;
    }

      /** 
     * @param aa the tile number in deck
     * @param crowns1 number of crowns of left square
     * @param crowns2 number of crowns of right square
     * @param sq1 type of left square, eg. farm1c
     * @param sq2 type of left square, eg. farm1c
     */
    public Tile(int aa, int crowns1, int crowns2, String sq1, String sq2){
        this.aa=aa;
        this.crowns1=crowns1;
        this.crowns2=crowns2;
        this.Square1=sq1;
        this.Square2=sq2;
        this.kingNumber=-1;
        this.playernumber=-1;
        this.clientnumber=-1;
        this.playercolor="none";
        this.strColor1="none";
        this.strColor2="none";
        this.row=-1;
        this.col=-1;
        this.row2=-1;
        this.col2=-1;
        this.discarded=false;


        if( this.Square1.equals("farm") || this.Square1.equals("farm1c")){
            //this.color1=Color.YELLOW;
            this.strColor1="yellow";
        }
        else if( this.Square1.equals("field") || this.Square1.equals("field1c") || this.Square1.equals("field2c")){
           // this.color1=Color.GREEN;
            this.strColor1="green";
        }
        else if( this.Square1.equals("forest") || this.Square1.equals("forest1c")){
            //this.color1=Color.GRAY;
            this.strColor1="gray";
        }
        else if( this.Square1.equals("lake") || this.Square1.equals("lake1c")){
            //this.color1=Color.BLUE;
            this.strColor1="blue";
        }
        else if( this.Square1.equals("land") || this.Square1.equals("land1c") || this.Square1.equals("land2c")){
           //this.color1=Color.LIGHTGRAY;
           this.strColor1="lightgray";
        }
        else if( this.Square1.equals("rocks") || this.Square1.equals("rocks1c") || this.Square1.equals("rocks2c") || this.Square1.equals("rocks3c")){
           //this.color1=Color.DARKGRAY;
           this.strColor1="darkgray";
        }

        if( this.Square2.equals("farm") || this.Square2.equals("farm1c")){
            //this.color2=Color.YELLOW;
            this.strColor2="yellow";
         }
         else if( this.Square2.equals("field") || this.Square2.equals("field1c") || this.Square2.equals("field2c")){
            //this.color2=Color.GREEN;
            this.strColor1="green";
         }
         else if( this.Square2.equals("forest") || this.Square2.equals("forest1c")){
            //this.color2=Color.GRAY;
            this.strColor2="gray";
         }
         else if( this.Square2.equals("lake") || this.Square2.equals("lake1c")){
            //this.color2=Color.BLUE;
            this.strColor2="blue";
         }
         else if( this.Square2.equals("land") || this.Square2.equals("land1c") || this.Square2.equals("land2c")){
           //this.color2=Color.LIGHTGRAY;
           this.strColor2="lightgray";
        }
         else if( this.Square2.equals("rocks") || this.Square2.equals("rocks1c") || this.Square2.equals("rocks2c") || this.Square2.equals("rocks3c")) {
            // this.color2=Color.DARKGRAY;        
             this.strColor2="darkgray";
         }
    }

    
    /** 
    * sets king and player information that reserved this domino
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
    }

    public void setDiscarded (boolean discarded){
        this.discarded=discarded;
    }

    public void setboardrowcol(int r1, int c1, int r2, int c2){
        this.row=r1;
        this.col=c1;
        this.row2=r2;
        this.col2=c2;
    }
    /** 
     * returns the domino data in csv format
     * aa;Square1;strColor1;crowns1;Square2;strColor2;crowns2;playercolor;kingNumber;playernumber;clientnumber;0xFFFFFF;0xFFFFFF
     * @return String
     */
    public String toString(){
       /*  [0]  tilenum
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
       [13] discarded=no|yes
       [14] this.row=-1;
       [15] this.col=-1;
       [16] this.row2=-1;
       [17] this.col2=-1;
        */
        String retstr = String.format("%d", this.aa) + ";" + 
            this.Square1 + ";" + 
            this.strColor1 + ";" + 
            String.format("%d", this.crowns1) + ";" + 
            this.Square2 + ";" + 
            this.strColor2 + ";" + 
            String.format("%d", this.crowns2) + ";" +  
            this.playercolor + ";" +  
            String.format("%d", this.kingNumber) + ";" +  
            String.format("%d", this.playernumber) + ";" +  
            this.clientnumber + ";" +  
            "0xFFFFFF" + ";" +  
            "0xFFFFFF" + ";"  ;

        if(this.discarded==true) retstr += "yes"; else retstr += "no";
        
        retstr += String.format("%d", this.row) + ";" +
            String.format("%d", this.col) + ";" +
            String.format("%d", this.row2) + ";" +
            String.format("%d", this.col2) + ";" ;


        return retstr;
    }

    
    /** 
     * companre two tiles by teir number
     * @param o
     * @return int
     */
    @Override
    public int compareTo(Tile o) {
        if(this.aa < o.aa) return -1;
        else if(this.aa > o.aa) return 1;
        else return 0;
    }

    
}
