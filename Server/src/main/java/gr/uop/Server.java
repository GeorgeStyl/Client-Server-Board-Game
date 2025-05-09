package gr.uop;


import java.net.ServerSocket;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;



/**
 * <h4><b>Primary starting Class</b></h4>
 * Creates an MTServer runnable object and runs it in a separate thread. 
 * <p>The window of this Application displays the MTServer status every 10 miliseconds using a Timer</p>
 * <p>The Application cannot be closed while game is in progress</p>
 * 
 */
public class Server extends Application {


    ServerSocket serverSocket;
    int serverPort = 7777;
  
    MTServer mtserver ;
    /** the Thread in where MTServer mtserver runs */
    Thread tMtserver;
    /** the text area to display the (mt)server status */
    TextArea textArea ;
    /** timer to display every 10 miliseconds the (mt)server status (update the textArea) */
    Timer statusTimer;
  
    @Override
    public void start(Stage stage) {

        this.textArea = new TextArea();

        mtserver = null;

        Pane root = new Pane();
        root.getChildren().add(this.textArea);
        // root.getChildren().add(progressBar);

        stage.setTitle("KingDomino Server");
        Scene scene = new Scene(root, 500,300);


        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
          public void handle(WindowEvent we) {
            if( mtserver == null || mtserver.gGameStatus == ServerEnums.GameStatus.INIT || 
              (mtserver.gGameStatus == ServerEnums.GameStatus.REGISTRATION && mtserver.gNumOfPlayers==0) ||
              mtserver.gGameStatus == ServerEnums.GameStatus.GAME_ABORTED || 
              mtserver.gGameStatus == ServerEnums.GameStatus.GAME_FINISHED)
            {
              tMtserver.stop();
              statusTimer.cancel();
              statusTimer.purge();
              // oxi consume, exelisetai kanonika gia kleisimo ...
              Platform.exit();
              System.exit(0);
            }
            else {
              we.consume();
              
              Alert alert = new Alert(Alert.AlertType.WARNING);
              alert.setTitle("Cannot close server");
              alert.setHeaderText("Players have started the game .");
              alert.setContentText(""); 
              alert.showAndWait();

            }

          }
        });  
      
        mtserver = new MTServer();    // implements Runnable
        tMtserver = new Thread(mtserver);
        tMtserver.start();

        stage.setScene(scene);
        stage.show();

        statusTimer  = new Timer();
        TimerTask statusShowTask = new TimerTask() {
            public void run(){
              String serverstatus = mtserver.ServerStatusString;
              String gamestatus = mtserver.gGameStatus.getName();
              int numofclients = mtserver.gNumOfClients;
              int numofplayers = mtserver.gNumOfPlayers;
              String kingsreg = "";
              for(int i=0;i<numofplayers;i++){
                if(i==0)  kingsreg += mtserver.gPlayers[i].playerColor.toUpperCase();
                else kingsreg += ", " + mtserver.gPlayers[i].playerColor.toUpperCase();
              }
              String message = String.format("NumOfClients=%d, NumOfPlayers=%d, Kings=%s\nGame  Status:: %s \nServer Status:: %s",numofclients, numofplayers,kingsreg, gamestatus,serverstatus);
              Platform.runLater(new Runnable() {
                 @Override
                 public void run() {
                   textArea.setText(message);
                 }
               });              
            }
        };
        statusTimer.scheduleAtFixedRate(statusShowTask, 10, 10);

    }


    public static void main(String[] args) {

        launch(args);
    }

   
}





