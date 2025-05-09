package gr.uop;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.paint.Color;

public class PlayerBoard {
    public ArrayList<BoardCell> board;
    public PlayerBoard(){
        board = new  ArrayList<BoardCell>();
        for(int i=0;i<81;i++){
            board.add(new BoardCell(i));
        }
    }
    public boolean setCastleCell(int aa, Color castleColor){
        if(this.board.get(aa).cell_tile==null){
            this.board.set(aa, new BoardCell(aa));
            this.board.get(aa).setCastleCell(aa, castleColor);
            return true;
        }
        return false;
    }

    public boolean setTileCell(int aa, Tile tile){
        if(this.board.get(aa).cell_tile==null){
            //TODO: checks for .....
            this.board.set(aa, new BoardCell(aa));
            this.board.get(aa).setTileCell(aa, tile);
            return true;
        }
        return false;
    }
}

