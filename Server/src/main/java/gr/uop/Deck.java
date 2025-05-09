package gr.uop;

import java.util.ArrayList;

/**
 * represents the complete Deck of KingDomino  tiles
 * all 48 dominos
 * based on dominos.txt
 * Server usage
 */
public class Deck {
    /**
     * the deck tile
     */
    ArrayList<Tile> decTiles;
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
     * creates the deck as in dominos.txt
     */
    public Deck(){
        decTiles = new ArrayList<Tile>();
		decTiles.add(new Tile(1, 0, 0, "farm", "farm") );
        decTiles.add(new Tile(2, 0, 0,  "farm", "farm") );
        decTiles.add(new Tile(3, 0, 0,  "forest", "forest") );
        decTiles.add(new Tile(4, 0, 0,  "forest", "forest") );
        decTiles.add(new Tile(5, 0, 0,  "forest", "forest") );
        decTiles.add(new Tile(6, 0, 0,  "forest", "forest") );
        decTiles.add(new Tile(7, 0, 0,  "lake", "lake") );
        decTiles.add(new Tile(8, 0, 0,  "lake", "lake") );
        decTiles.add(new Tile(9, 0, 0,  "lake", "lake") );
        decTiles.add(new Tile(10, 0, 0, "field", "field") );
        decTiles.add(new Tile(11, 0, 0, "field", "field") );
        decTiles.add(new Tile(12, 0, 0,  "land", "land") );
        decTiles.add(new Tile(13, 0, 0,  "farm", "forest") );
        decTiles.add(new Tile(14, 0, 0,  "farm", "lake") );
        decTiles.add(new Tile(15, 0, 0,  "farm", "field") );
        decTiles.add(new Tile(16, 0, 0,  "farm", "land") );
        decTiles.add(new Tile(17, 0, 0,  "forest", "lake") );
        decTiles.add(new Tile(18, 0, 0,  "forest", "field") );
        decTiles.add(new Tile(19, 1, 0,  "farm1c", "forest") );
        decTiles.add(new Tile(20, 1, 0,  "farm1c", "lake") );
        decTiles.add(new Tile(21, 1, 0,  "farm1c", "field") );
        decTiles.add(new Tile(22, 1, 0,  "farm1c", "land") );
        decTiles.add(new Tile(23, 1, 0,  "farm1c", "rocks") );
        decTiles.add(new Tile(24, 1, 0,  "forest1c", "farm") );
        decTiles.add(new Tile(25, 1, 0,  "forest1c", "farm") );
        decTiles.add(new Tile(26, 1, 0,  "forest1c", "farm") );
        decTiles.add(new Tile(27, 1, 0,  "forest1c", "farm") );
        decTiles.add(new Tile(28, 1, 0,  "forest1c", "lake") );
        decTiles.add(new Tile(29, 1, 0,  "forest1c", "field") );
        decTiles.add(new Tile(30, 1, 0,  "lake1c", "farm") );
        decTiles.add(new Tile(31, 1, 0,  "lake1c", "farm") );
        decTiles.add(new Tile(32, 1, 0,  "lake1c", "forest") );
        decTiles.add(new Tile(33, 1, 0,  "lake1c", "forest") );
        decTiles.add(new Tile(34, 1, 0,  "lake1c", "forest") );
        decTiles.add(new Tile(35, 1, 0,  "lake1c", "forest") );
        decTiles.add(new Tile(36, 0, 1,  "farm", "field1c") );
        decTiles.add(new Tile(37, 0, 1,  "lake", "field1c") );
        decTiles.add(new Tile(38, 0, 1,  "farm", "land1c") );
        decTiles.add(new Tile(39, 0, 1,  "field", "land1c") );
        decTiles.add(new Tile(40, 1, 0,  "rocks1c", "farm") );
        decTiles.add(new Tile(41, 0, 2,  "farm", "field2c") );
        decTiles.add(new Tile(42, 0, 2,  "lake", "field2c") );
        decTiles.add(new Tile(43, 0, 2,  "farm", "land2c") );
        decTiles.add(new Tile(44, 0, 2,  "field", "land2c") );
        decTiles.add(new Tile(45, 2, 0,  "rocks2c", "farm") );
        decTiles.add(new Tile(46, 0, 2,  "land", "rocks2c") );
        decTiles.add(new Tile(47, 0, 2,  "land", "rocks2c") );
        decTiles.add(new Tile(48, 0, 3,  "farm", "rocks3c") );
    }

    /** 
     * getter returns the array of tiles, the comlete deck
     * @return ArrayList<Tile>
     */
    public ArrayList<Tile> getDecTiles(){
        return this.decTiles;
    }

    
    /** 
     * @param aa
     * @return Tile
     */
    public Tile getDecTileByAA(int aa){
        for(Tile tile : this.decTiles){
            if( tile.aa == aa) return tile;
        }
        return null;
    }

    
}
