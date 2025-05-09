package gr.uop;

/**
 * holds coordinates of a board position
 */
public class BoardPos {
    int row;
    int col;
    int hashc;
    String strcolor;
    Square.LandscapeType landscape;

    public BoardPos( int r, int c){
        this.row = r;
        this.col= c;
        this.hashc = (r*100)+c;
        this.landscape = Square.LandscapeType.UNKNWON;
    }
    public BoardPos( int r, int c, Square.LandscapeType ls){
        this.row = r;
        this.col= c;
        this.hashc = (r*100)+c;
        this.landscape = ls;
    }
    public boolean equals(BoardPos obj){
        if(this.row==obj.row && this.col==obj.col && this.landscape==obj.landscape) return true;
        else return false;
    }
    public int hashCode(){
        return this.hashc;
    }
}
