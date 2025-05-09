package gr.uop;

import java.io.FileInputStream;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
//import javafx.scene.paint.Color;

public class KingTile extends Tile {
    public int aa;
    public ImageView castleImageView;
    // Color castleColor; castleColor

    public KingTile(int guiking, int kingno, String color, int imgHeight, int imgWidth, int pplayernumber, int pclientnumber){
        super(color);
        aa=0;
        this.castleImageView = imageKingCastle(guiking, kingno, color, imgHeight, imgWidth);
        this.setKingInfo(kingno, color, pplayernumber, pclientnumber);
    }

    public KingTile(KingTile fromkingTile){
        super(fromkingTile);
        aa=0;
        this.castleImageView = fromkingTile.castleImageView;
    }

    public ImageView imageKingCastle(int guiking, int kingno, String color, int imgHeight, int imgWidth){
        FileInputStream pnginput;
        try {
            if(color.equals("red")){
                pnginput = new FileInputStream("castlered.png");
            }
            else if(color.equals("green")){
                pnginput = new FileInputStream("castlegreen.png");
            }          
            else if(color.equals("blue")){
                pnginput = new FileInputStream("castleblue.png");
            }           
            else if(color.equals("yellow")){
                pnginput = new FileInputStream("castleyellow.png");
            }           
            else {
                pnginput = new FileInputStream("blank.png");
            }          
            Image img = new Image(pnginput);
            ImageView imageView = new ImageView(img);
            imageView.setFitHeight(imgHeight/2);
            imageView.setFitWidth(imgWidth/2);
            return imageView;
    
        } catch (Exception e) {
            System.out.println("castlexxxxx.png not found");
            return null;
        }      
    }
    @Override
    public String toString(){
        /*[0]  aa
          [1]  Square1
          [1] castlecolor
          [3] crowns1
          [4] t.playercolor
          [5] ,t.kingNumber
          [6], t.playernumber,
          [7] t.clientnumber
          [2] castleColor
          [9] type=square
          */
           String retstr = String.format("%d;%s;%s;%d;%s;%d;%d;%d;%s;type=king", this.aa, this.Square1, this.castlecolor, 0, this.playercolor, 
           this.kingNumber, this.playernumber, this.clientnumber, this.castleColor.toString() );
           return retstr;
        }
}
