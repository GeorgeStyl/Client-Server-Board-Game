package gr.uop;
import javafx.scene.paint.Color;

public class BoardCell {
    public int cell_aa;
    public Tile cell_tile;
    
    public BoardCell( int aa ){
        this.cell_aa = aa;
        this.cell_tile = null;
    }
    public void setCastleCell( int aa, Color castleColor ){
        this.cell_aa = aa;
        this.cell_tile = new Tile(castleColor);
    }
    public void setTileCell( int aa, Tile tile ){
        this.cell_aa = aa;
        this.cell_tile = tile;
    }


}
