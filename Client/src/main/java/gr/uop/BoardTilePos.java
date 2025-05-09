package gr.uop;

/**
 * coordinates of a board domino tile (2 squares) and landscape type of each square
 */
public class BoardTilePos {
    int row1;
    int col1;
    int row2;
    int col2;
    int hashc;
    Square.LandscapeType landscape1;
    Square.LandscapeType landscape2;

    public BoardTilePos( int r1, int c1, Square.LandscapeType ls1, int r2, int c2, Square.LandscapeType ls2){
        this.row1 = r1;
        this.col1= c1;
        this.row2 = r2;
        this.col2= c2;
        this.landscape1 = ls1;
        this.landscape2 = ls2;
        this.hashc = r2*10000 + c2*1000 + r1*100 + c1 ;
    }
    
    public boolean equals(BoardTilePos obj){
        //if(this.row==obj.row && this.col==obj.col && this.landscape==obj.landscape) return true;
        if(this.row1==obj.row1 && this.col1==obj.col1 && this.landscape1==obj.landscape1 && this.row2==obj.row2 && this.col2==obj.col2 && this.landscape2==obj.landscape2) return true;
        else return false;
    }
    public int hashCode(){
        return this.hashc;
    }
}
