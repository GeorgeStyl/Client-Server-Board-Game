package gr.uop;

import java.io.FileInputStream;

import java.util.ArrayList;
// import java.util.Optional;
import java.util.Set;

// import gr.uop.Square.SquareType;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
// import javafx.animation.Animation.Status;

//import gr.uop.PlayerClient.Player;

//import gr.uop.Square.SquareColor;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
// import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
//import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
// import javafx.scene.control.ButtonType;
//import javafx.scene.control.Cell;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
// import javafx.scene.control.Alert.AlertType;
// import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.layout.Background;
//import javafx.scene.layout.BackgroundFill;width
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
//import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * main Client class extends Application
 * <p>
 * <b>This is the gui client</b>
 */
public class Client extends Application  {

    String serverip = "localhost";
    int port = 7777;


    /**
     * the main stage stored here so methods can access it
     */
    Stage this_stage;
     /**
     * the main scene stored here so methods can access it
     */   
    Scene scene;
    MenuBar menuBar;
    Menu menuGame;
    MenuItem connToserver;
    MenuItem stopGame;
    //MenuItem onlinePrompts;
    MenuItem exit;
    Menu menuHelp;
    MenuItem help;
    MenuItem about;
    /**
     * game status messages area 
     */
    final TextArea logtext = new TextArea();

    /**
     * displays information about registered player 1
     */
    final Label user1info = new Label("Player1 ...");;
    /**
     * displays information about registered player 2
     */
    final Label user2info = new Label("Player2 ...");
    /**
     * displays information about registered player 3
     */
    final Label user3info = new Label("Player3 ...");
    /**
     * displays information about registered player 4
     */
    final Label user4info = new Label("Player4 ...");
    /**
     * Hbox to display information for registerd players that 
     * are not players of this client
     */
    HBox hbotherplayers = new HBox();
    // final Button tile1 = new Button(" Tile1 ... ");;
    // final Button tile2 = new Button(" Tile2 ... ");;
    /**
     * button to complete a domino tile placement on board
     */

    Button btnplacementDone;
    /**
     * button that discards a domino (tile)
     */
    Button btnDiscardTile;
    /**
     * button to rotate 90 clockwhise - dexiostrofa
     */
    Button btnRotCW90;

    Button btnRotDone;

    // /**
    //  * button to flip the squares of a domino tile
    //  */
    // Button btnfipSquares ;
    
    //Button btnRotCCW90;
    //Button btnRot180;
    /**
     * number of rows of kingdom
     */
    final int boardRowNum = 9;
    /**
     * number of columns of kingdom
     */
    final int boardColNum = 9;
    /**
     * gridpane for drow domino tiles rows of ui player 1 
     */
    final GridPane pl1seldoms = new GridPane();
    /**
     * gridpane for drow domino tiles rows of ui player 2 
     */
    final GridPane pl2seldoms = new GridPane();
    /**
     * gridpane for drow domino tiles rows of ui player 3 
     */
    final GridPane pl3seldoms = new GridPane();
    /**
     * gridpane for drow domino tiles rows of ui player 4 
     */
    final GridPane pl4seldoms = new GridPane();
    /**
     * gridpane for rotating selected domino
     */
    final GridPane plposdominogrd = new GridPane();
    /**
     * VBOX for plposdominogrd gridpane
     */
    //HBox vbposdomgrd = new HBox();
    VBox vbposdomgrd = new VBox();
    
    /**
     * ui Player 1 board (Kingdom)
     */
    final GridPane pl1Board = new GridPane();
    /**
     * player 2 board (Kingdom) 
     * or
     * ui Player 2 board (Kingdom)
     */
    final GridPane pl2Board = new GridPane();
    /**
     * ui Player 3 board (Kingdom)
     */
    final GridPane pl3Board = new GridPane();
    /**
     * ui Player 4 board (Kingdom)
     */
    final GridPane pl4Board = new GridPane();
    /**
     * label for registered palyer 1 above kingdom
     */
    Label lbl1Board = new Label("Kingdom. current score=0");
    /**
     * label for registered palyer 2 above kingdom
     */
    Label lbl2Board = new Label("Kingdom. current score=0");
    /**
     * label for registered palyer 3 above kingdom
     */
    Label lbl3Board = new Label("Kingdom. current score=0");
    /**
     * label for registered palyer 4 above kingdom
     */
    Label lbl4Board = new Label("Kingdom. current score=0");
    /**
     * main TabPane containes the players/kings tabs, 
     * one for each player/king
     */
    final TabPane tabPane = new TabPane();
    
    final SingleSelectionModel<Tab> TabpansSelectionModel = tabPane.getSelectionModel();

   /**
     * Tab for Player 1 board (Kingdom)
     */
    Tab tab1 = null;
    /**
     * Tab for Player 2 board (Kingdom)
     */
    Tab tab2 = null;
   /**
     * Tab for Player 3 board (Kingdom)
     */
    Tab tab3 = null;
   /**
     * Tab for Player  4 board (Kingdom)
     */
    Tab tab4 = null;

    Image blankimg; // example: new Image("images/logo.png");
    Image imgcastle1;
    Image imgcastle2;

    /**
     * width of landscape image
     */
    final int imgWidth = 40;
    /**
     * height of landscape image
     */
    final int imgHeight = 40;

    final int cellWidth = 50;
    final int cellHeight = 50;


    /**
     * if user needs guidance for placement steps it can turn this on via corresponding menu option
     */
    boolean showonlinePrompts = false;   // if user wants to see on line prompts

    /**
     * see <b>PlayerClient class</b>
     * the object of that handles the communication with the kingdomino server
     */
    PlayerClient playerClient ;
    Thread clientThread;
    /**
     * keeps tracking the current player fot ui
     */
    int gCurrentGuiPlayerNumber = 0;

    @Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
		clientThread.interrupt();		
	}

    /** 
     * starts the main gui window of the client
     * @param stage JavaFX Stage
     */
    public void start(Stage stage) {

        playerClient = new PlayerClient(serverip,port,this);
        
        this_stage = stage;
        stage.setTitle("KingDomino client");
        VBox root = new VBox();
        this.scene = new Scene(root, 900, 800);
        this.scene.setFill(Color.OLDLACE);

        // ==== menu bar ================ top row
        this.menuBar = new MenuBar();       
        this.menuGame = new Menu("Game");
        
        this.connToserver = new MenuItem("Συμμετοχή σε παιχνίδι (register to game)");
        connToserver.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                connectToserver();
                // vbox.setVisible(true);
            }
        });
        
        this.stopGame = new MenuItem("Διακοπή παιχνιδιού (stop game)");
        this.stopGame.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                StopGame(false);
            }
        });

        // this.onlinePrompts = new MenuItem("Ενεργοποίηση Online Prompts");
        // this.onlinePrompts.setOnAction(new EventHandler<ActionEvent>() {
        //     public void handle(ActionEvent t) {
        //         if(showonlinePrompts==true) {
        //             showonlinePrompts=false;
        //             ((MenuItem)t.getSource()).setText("Ενεργοποίηση Online Prompts");
        //         }
        //         else  {
        //             showonlinePrompts=true;
        //             ((MenuItem)t.getSource()).setText("Απενεργοποίηση Online Prompts");
        //         }
        //     }
        // });

        this.exit = new MenuItem("Εξοδος (Exit)");
        this.exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                fmenuexit();
                // System.exit(0);
            }
        });

        this.menuGame.getItems().addAll(this.connToserver,new SeparatorMenuItem(), this.stopGame, new SeparatorMenuItem(), this.exit);

        this.stopGame.setDisable(true);
        
        this.menuHelp = new Menu("Βοήθεια (help)");
        this.help = new MenuItem("Βοήθεια (help)");
         this.help.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                Help(stage);
            }
        }); 
        this.about = new MenuItem("Σχετικά με το «Kingdomino» (About «Kingdomino»)");
        this.about.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                About(stage);
            }
        });    
        this.menuHelp.getItems().addAll(this.help,this.about);

        this.menuBar.getMenus().addAll(this.menuGame, this.menuHelp);
        
        // ======= log textarea ===============================
        this.logtext.setPrefRowCount(2);
        this.logtext.setEditable(false);

       
        // =========== board 1 / tab 1  ====================================
          // set up dominos select grid (two rows)
          this.pl1seldoms.setId("gl1selcards");
          for(int i=0;i<4;i++){        // col
               for(int j=0;j<2;j++){    //row

                    int col=3*i;
                    this.pl1seldoms.add( CreateEmptyGridpaneSquare(col, j, "g1selcard"), col,j,1,1);

                    col=(3*i)+1;
                    this.pl1seldoms.add( CreateEmptyGridpaneSquare(col, j, "g1selcard"), col,j,1,1);

                   col=(3*i)+2;
                   Label lbl3 = new Label(String.format(" (%d,%d) ", col, j));                   
                   HBox hbx3 = new HBox(lbl3);
                   this.pl1seldoms.add( hbx3, col, j, 1, 1);
                   
               }
           }
          // set up dominos booard (kingdom)
           for(int i=0;i<boardColNum;i++){
               for(int j=0;j<boardRowNum;j++){
                    this.pl1Board.add( CreateEmptyGridpaneSquare(i, j, "g1brd"), i, j, 1, 1 );       
               }
           }
           VBox vbxplayer1 = new VBox();
           vbxplayer1.getChildren().addAll(this.pl1seldoms,this.lbl1Board,this.pl1Board);   // this.tile1,
   
           //width, height
           //this.pl1Board.setMinSize(this.boardColNum*this.imgWidth, this.boardRowNum*this.imgHeight);
           RowConstraints rc = new RowConstraints();
            //rc.setPercentHeight(100d / this.boardRowNum);
            rc.setMinHeight(this.cellHeight);
            rc.setPrefHeight(this.cellHeight+10);
            //rc.setMaxHeight(this.imgHeight);
            for (int i = 0; i < this.boardRowNum; i++) {
                this.pl1Board.getRowConstraints().add(rc);
            }

            ColumnConstraints cc = new ColumnConstraints();
            // cc.setPercentWidth(100d / this.boardColNum);
            cc.setMinWidth(this.cellWidth);
            cc.setPrefWidth(this.cellWidth);
            //cc.setMaxWidth(this.imgWidth);
            for (int i = 0; i < this.boardColNum; i++) {
                this.pl1Board.getColumnConstraints().add(cc);
            }

        // =========== board 2 / tab 2  ====================================          
           for(int i=0;i<4;i++){
               for(int j=0;j<2;j++){                

                int col=3*i;
                this.pl2seldoms.add( CreateEmptyGridpaneSquare(col, j, "g2selcard"), col,j,1,1);

                col=(3*i)+1;
               this.pl2seldoms.add( CreateEmptyGridpaneSquare(col, j, "g2selcard"), col,j,1,1);

               col=(3*i)+2;
               Label lbl3 = new Label(String.format(" (%d,%d) ", col, j));
               HBox hbx3 = new HBox(lbl3);
               this.pl2seldoms.add( hbx3, col, j, 1, 1);
               }       
           }
           for(int i=0;i<boardColNum;i++){
               for(int j=0;j<boardRowNum;j++){
                   this.pl2Board.add( CreateEmptyGridpaneSquare(i, j, "g2brd"), i, j, 1, 1 );
               }
           }
           VBox vbxplayer2 = new VBox();
           vbxplayer2.getChildren().addAll(this.pl2seldoms,lbl2Board,this.pl2Board);
           vbxplayer2.setBorder(new Border(new BorderStroke(Color.BLACK, 
           BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );
           
        // =========== board 3 / tab 3  ====================================          
        for(int i=0;i<4;i++){
            for(int j=0;j<2;j++){             

                int col=3*i;
                this.pl3seldoms.add( CreateEmptyGridpaneSquare(col, j, "g3selcard"), col,j,1,1);

                col=(3*i)+1;
                this.pl3seldoms.add( CreateEmptyGridpaneSquare(col, j, "g3selcard"), col,j,1,1);

                col=(3*i)+2;
                Label lbl3 = new Label(String.format(" (%d,%d) ", col, j));
                HBox hbx3 = new HBox(lbl3);
                this.pl3seldoms.add( hbx3, col, j, 1, 1);
            }       
        }
        for(int i=0;i<boardColNum;i++){
            for(int j=0;j<boardRowNum;j++){ 
                this.pl3Board.add( CreateEmptyGridpaneSquare(i, j, "g3brd"), i, j, 1, 1 );     
            }
        }
        VBox vbxplayer3 = new VBox();
        vbxplayer3.getChildren().addAll(this.pl3seldoms,lbl3Board,this.pl3Board);
        vbxplayer3.setBorder(new Border(new BorderStroke(Color.BLACK, 
        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );

        // =========== board 4 / tab 4  ====================================          
        for(int i=0;i<4;i++){
            for(int j=0;j<2;j++){             

                int col=3*i;
                this.pl4seldoms.add( CreateEmptyGridpaneSquare(col, j, "g3selcard"), col,j,1,1);

                col=(3*i)+1;
               this.pl4seldoms.add( CreateEmptyGridpaneSquare(col, j, "g3selcard"), col,j,1,1);

               col=(3*i)+2;
               Label lbl3 = new Label(String.format(" (%d,%d) ", col, j));
               HBox hbx3 = new HBox(lbl3);
               this.pl4seldoms.add( hbx3, col, j, 1, 1);            }       
        }
        for(int i=0;i<boardColNum;i++){
            for(int j=0;j<boardRowNum;j++){
                this.pl4Board.add( CreateEmptyGridpaneSquare(i, j, "g4brd"), i, j, 1, 1 );      
            }
        }
        VBox vbxplayer4 = new VBox();
        vbxplayer4.getChildren().addAll(this.pl4seldoms,lbl4Board,this.pl4Board);
        vbxplayer4.setBorder(new Border(new BorderStroke(Color.BLACK, 
        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );
   

        // === gridpane to rotate dominos ===
        Square s1= new Square(1, 1, 0, "empty", 0, 0, 0, "white", null);
        HBox hbx1pdm = new HBox(s1.getLandscapeVBox(imgHeight, imgWidth));
        hbx1pdm.setPrefHeight(cellHeight);
        hbx1pdm.setPrefWidth(cellWidth);
        hbx1pdm.setBorder(new Border(new BorderStroke(Color.BLACK, 
        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );
        hbx1pdm.setId(String.format("hbx-gpos-%d-%d", 0, 0));
        hbx1pdm.setUserData(s1);
        // this.plposdominogrd.add( hbx1pdm, 0, 0, 1, 1);
        
        this.plposdominogrd.add( setLandscapeSelCell( new Square(1, 1, 0, "empty", 0, 0, 0, "white", null), -1),
        0, 0, 1, 1);


        Square s2= new Square(1, 2, 0, "empty", 0, 0, 0, "white", null);
        HBox hbx2pdm = new HBox(s2.getLandscapeVBox(imgHeight, imgWidth));
        hbx2pdm.setPrefHeight(cellHeight);
        hbx2pdm.setPrefWidth(cellWidth);
        hbx2pdm.setBorder(new Border(new BorderStroke(Color.BLACK, 
        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );
        hbx2pdm.setId(String.format("hbx-gpos-%d-%d", 1, 0));
        hbx2pdm.setUserData(s2);
        // this.plposdominogrd.add( hbx2pdm, 1, 0, 1, 1);
        this.plposdominogrd.add( setLandscapeSelCell( new Square(1, 1, 0, "empty", 0, 0, 0, "white", null), -1),
        1, 0, 1, 1);

        // this.plposdominogrd.add( CreateEmptyGridpaneSquare(0,1, "gpos"), 0, 1, 1, 1);
        // this.plposdominogrd.add( CreateEmptyGridpaneSquare(1,1, "gpos"), 1, 1, 1, 1);

        this.plposdominogrd.add( setLandscapeSelCell( new Square(1, 1, 0, "empty", 0, 0, 0, "white", null), -1),
        0, 1, 1, 1);
        this.plposdominogrd.add( setLandscapeSelCell( new Square(1, 1, 0, "empty", 0, 0, 0, "white", null), -1),
        1, 1, 1, 1);

        // btnfipSquares = new Button("Αντιμετάθεση Τοπίων");
        // btnfipSquares.setDisable(true);


        btnRotCW90 = new Button("Περιστρ. Δεξιόστρ. 90 μοιρες");
        //btnRotCW90.setDisable(true); 

        btnRotDone = new Button("Τέλος Περιστροφών");
        //btnRotDone.setDisable(true);

        btnDiscardTile = new Button("Αχρηστο domino");
        //btnDiscardTile.setDisable(true);

        btnplacementDone = new Button("Τέλος Τοποθέτησης");
        // btnplacementDone.setDisable(true);

        // btnfipSquares.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBtnFlipsquares);   
        btnRotCW90.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBtnRCW90);   
        btnRotDone.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBtrotDone);   
        //btnRotCCW90.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBtnRCCW90);
        //btnRot180.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBtnR180);   
        btnplacementDone.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBtnPlacement);   
        btnDiscardTile.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBtnDiscard);   


        HBox posButtons = new HBox();
        posButtons.getChildren().addAll(btnRotCW90,btnRotDone); 


        //VBox vbpldomgrl = new VBox();
        //vbpldomgrl.getChildren().add(new Label("Rotation of dominos area"));
        //vbpldomgrl.getChildren().add(posButtons);

        // vbposdomgrd.getChildren().addAll(this.plposdominogrd, vbpldomgrl);

        // changed to vbox ...
        HBox pos2Buttons = new HBox();
        pos2Buttons.getChildren().addAll(btnDiscardTile,btnplacementDone); 

        vbposdomgrd.getChildren().addAll(new Label("Rotation of dominos area"), 
            this.plposdominogrd, posButtons,pos2Buttons);

        String cssdomgrd = "-fx-border-color: gray;\n" +
                   "-fx-border-insets: 5;\n" +
                   "-fx-border-width: 3;\n" +
                   "-fx-border-style: dashed;\n";
        vbposdomgrd.setStyle(cssdomgrd);
        vbposdomgrd.setSpacing(10);
        vbposdomgrd.setDisable(true);
        //vbposdomgrd.setVisible(false);

        

        //TabPane tabPane = new TabPane();
        tab1 = new Tab("Player 1/King 1", vbxplayer1);
        tab2 = new Tab("Player 2/King 2", vbxplayer2);
        tab3 = new Tab("Player 3/King 3", vbxplayer3);
        tab4 = new Tab("Player 4/King 4", vbxplayer4);
           
        tabPane.getTabs().add(tab1);
        tabPane.getTabs().add(tab2);
        tabPane.getTabs().add(tab3);
        tabPane.getTabs().add(tab4);
        // Tab selectedTab = tabPane.getSelectionModel().getSelectedItem();

        VBox vbxplayers = new VBox(tabPane);

        HBox hbxplayers = new HBox();
        hbxplayers.getChildren().addAll(vbxplayers,vbposdomgrd);


        // canceled ...
        // HBox gameButtons = new HBox();
        // gameButtons.getChildren().addAll(btnDiscardTile,btnplacementDone);

        
        this.user1info.setId("user1info");
        this.user2info.setId("user2info");
        this.user3info.setId("user3info");
        this.user4info.setId("user4info");

        // HBox hbotherplayers = new HBox();
        this.hbotherplayers.getChildren().addAll( new Label("Registered players: "),
            this.user1info,
            new Label(" | "),
            this.user2info,
            new Label(" | "),
            this.user3info,
            new Label(" | "),
            this.user4info
        );

        // root.getChildren().addAll(this.menuBar, this.logtext, vbposdomgrd, gameButtons, hbxpleyers, hbotherplayers);
        
        root.getChildren().addAll(this.menuBar, this.logtext, hbxplayers, hbotherplayers);


        this.pl4seldoms.setDisable(true);
        this.pl4Board.setDisable(true);
        this.pl3seldoms.setDisable(true);
        this.pl3Board.setDisable(true);
        this.pl2seldoms.setDisable(true);
        this.pl2Board.setDisable(true);
        this.pl1seldoms.setDisable(true);
        this.pl1Board.setDisable(true);  

       stage.setScene(this.scene);
       stage.show();

    }

    
    /** 
     * @param args
     * no args needed. port and target ip are hardcoded
     */
    public static void main(String[] args) {
   
       launch(args);

    }

    /**
     * event handler for mouse click for domino selection gridpane cells
     */
    EventHandler<javafx.scene.input.MouseEvent> evhMouseclickSeldom = 
        new EventHandler<javafx.scene.input.MouseEvent>() { 
        @Override 
        public void handle(javafx.scene.input.MouseEvent e) { 
            hOnMouseClickedSelDomino(e);  
        } 
    }; 
    /**
     * event handler for mouse click for kingdom (board) gridpane cells
     */
    EventHandler<javafx.scene.input.MouseEvent> evhMouseclickBoardCell = 
        new EventHandler<javafx.scene.input.MouseEvent>() { 
        @Override 
        public void handle(javafx.scene.input.MouseEvent e) { 
            hOnMouseClickedBoard(e);  
        } 
    };
    /** 
     * event handler for mouse click on button Discard domino
     */
    EventHandler<javafx.scene.input.MouseEvent> evhMouseclickBtnDiscard = 
        new EventHandler<javafx.scene.input.MouseEvent>() { 
        @Override 
        public void handle(javafx.scene.input.MouseEvent e) { 
            e.consume();  
            hOnMouseTileDiscard(e);
            return;
        } 
    };
    /**
     * event handler for mouse click on button Flipsquares
     */
    EventHandler<javafx.scene.input.MouseEvent> evhMouseclickBtnFlipsquares = 
        new EventHandler<javafx.scene.input.MouseEvent>() { 
        @Override 
        public void handle(javafx.scene.input.MouseEvent e) { 
            e.consume();  
            return;
        } 
    };
    /**
     *  event handler for mouse click on button Placement Done
     */
    EventHandler<javafx.scene.input.MouseEvent> evhMouseclickBtnPlacement = 
        new EventHandler<javafx.scene.input.MouseEvent>() { 
        @Override 
        public void handle(javafx.scene.input.MouseEvent e) { 
            e.consume(); 
            hOnMouseTilePlaced(e) ;
            return;
        } 
    };
    /**
     * event handler for mouse click on button Rotate Clockwise 90
     */
    EventHandler<javafx.scene.input.MouseEvent> evhMouseclickBtnRCW90 = 
        new EventHandler<javafx.scene.input.MouseEvent>() { 
        @Override 
        public void handle(javafx.scene.input.MouseEvent e) { 
            System.out.println(" *** evhMouseclickBtnRCW90 evant hanlder called");
            e.consume(); 
            // RotateCW90(e); 
            hOnMouseRotateCW90(e); 
            return;
        } 
    };
    /**
     * event handler for mouse click on button Rotate Done (OK)
     */
    EventHandler<javafx.scene.input.MouseEvent> evhMouseclickBtrotDone = 
        new EventHandler<javafx.scene.input.MouseEvent>() { 
        @Override 
        public void handle(javafx.scene.input.MouseEvent e) { 
            e.consume();  
            hOnMouseRotateDone(e); 
            return;
        } 
    };

    /**
     * called when mouse clicked on kingdom board cell
     * places (draws) the castle in the selected cell
     * @param p Player
     * @param hbcell the cell mouse clicked
     * @param grdboard the kingdom board
     */
    void handleCastlePlacement(Player p, HBox hbcell, GridPane grdboard){
        p.castleSquare = new Square(0, 1, 0, "castle", p.kingNumber, p.playerNumber, p.ClientNumber, p.playerColor, null);
                //            public Square(int aa, int whichsqr, int crowns, String sqr, int kingn, int plrn, int cltn, String plrclr, Tile thetile){
        //Node node = (Node)e.getTarget();
        int row = GridPane.getRowIndex(hbcell);
        int col = GridPane.getColumnIndex(hbcell);
        hbcell.getChildren().clear();
        hbcell.getChildren().addAll(setKingBoradCell(p.castleSquare,p.GuiPlayerNumber));
        p.castleSquare.setBoardPosition(row, col);                        
        p.castlePlaced=true;
        p.nexttoking=true;  // next selection must be placed next to king
        hbcell.setUserData(p.castleSquare);
        
        // update server that castle has been placed
        playerClient.CastlePositioned(p.playerNumber, p.kingNumber, p.playerName, p.playerColor,  0, row, col) ;
        grdboard.setDisable(true); 
        
        // put castle in player's Landscapes array
        p.addLandscape(new Square(p.castleSquare,p.castleSquare.whichSqr)); 

        this.logtext.setText(String.format("Τοποθετήθηκε το κάστρο για το βασιλιά %s και στάλθηκε στον sever. Αναμονή για τους άλλους παίκτες...",
        p.playerColor.toUpperCase()));    
    }
    /**
     * when mouse clicked on dominos selection gridpane cell
     * selection of domino for king
     */
    public void hOnMouseClickedSelDomino(javafx.scene.input.MouseEvent event){
        
        event.consume();

        /* System.out.println(event.getTarget());
        System.out.println(event.getEventType());
        Node clickedNode = event.getPickResult().getIntersectedNode();
        Node parent = clickedNode.getParent();
        while (!(parent instanceof GridPane)) {
            clickedNode = parent;
            parent = clickedNode.getParent();
        }
        System.out.print("hOnMouseClickedSelDomino clickedNode=");   // clickedNode=HBox[id=hbx-g1brdcell-4-3]
        System.out.println(clickedNode);*/

        HBox hbcell = null;
        if( event.getTarget() instanceof ImageView ){
            ImageView imgvw = (ImageView)event.getTarget();
            System.out.print(" parent of ImageView : ");
            System.out.println( imgvw.getParent()); // parent of ImageView : VBox@....
            if(imgvw.getParent() instanceof VBox  ) {
                VBox vbimg = (VBox)imgvw.getParent();
                System.out.print(" parent of VBox : ");
                System.out.println( vbimg.getParent()); // parent of VBox : HBox@....
                if(vbimg.getParent() instanceof HBox  ) {
                    hbcell = (HBox)vbimg.getParent();
                }
            }
        }
        else if( event.getTarget() instanceof Text ){
             Text txt = (Text)event.getTarget();
             System.out.print(" parent of Text : ");
             System.out.println( txt.getParent());    // parent of Label : VBox...
         }
        else if( event.getTarget() instanceof HBox) {
            hbcell = (HBox)event.getTarget();
        }
        if( hbcell !=null) {
            System.out.print(" Found HBOX. parent of Hbox : ");
            System.out.println( hbcell.getParent());
            System.out.println( hbcell.getParent().getClass().getName());
            System.out.println( hbcell.getParent().getClass().getSimpleName());
            if( hbcell.getParent()  instanceof GridPane ) {
                GridPane domboard = (GridPane)hbcell.getParent();
                System.out.println(" -- parent is GridPane ");
                if( domboard.getUserData() instanceof Player){
                    System.out.print(" -- parent GridPane assigned a player ");
                    System.out.println(domboard.getUserData());
                    Player p = (Player)domboard.getUserData();
                    int row = GridPane.getRowIndex(hbcell);
                    int col = GridPane.getColumnIndex(hbcell);

                    /* castle has been placed, not domino selected by king placement so, 
                    * this mouse click is to select a domino for the king
                    */ 
                    if(p.castlePlaced==true && p.tileSelected == false) { 
                        selectSqrFirstRow(event, p.GuiPlayerNumber, col, row, domboard ); 
                        return;    
                    }
                    /* castle has been placed, domino selected by king placement, ... so , 
                    * this mouse click is ??
                    */ 
                    if(p.castlePlaced==true && p.tileSelected == true) {

                        return;
                    }
                }
                else {
                    System.out.print(" -- parent GridPane has not assigned a player ");
                }
            }
        }
        else {
            System.out.println(" event.getTarget not an instance of HBOX ");
        }
        return;
    }

    /**
     * when button Rotate Clockwise 90 clicked
     * @param e mouse event
     */
    void hOnMouseRotateCW90(Event e){
        // System.out.println(" *** PRotateCW90 called");

        Player p = playerClient.getPlayerByGuiNo(gCurrentGuiPlayerNumber);
        Node pivotn = null;
        Node rotaten = null;
        for(Node n : this.plposdominogrd.getChildren()){
            n.setStyle("-fx-background-color: white;"); 
        }
        if(p!=null) {
            pivotn = getNodeByRowColumnIndex(0, 0, this.plposdominogrd);  // square, pivot at 0,0
            for(Node n : this.plposdominogrd.getChildren()){
                if(n!=null && !(GridPane.getRowIndex(n)==0 && GridPane.getColumnIndex(n)==0) && n.getUserData() !=null){
                    System.out.printf(" *** PRotateCW90  found rotaten r=%d, c=%d %n",GridPane.getRowIndex(n),GridPane.getColumnIndex(n));
                    rotaten = getNodeByRowColumnIndex(GridPane.getRowIndex(n), GridPane.getColumnIndex(n), this.plposdominogrd);
                    break;
                }
            }
            if( rotaten==null) return;
            if( GridPane.getRowIndex(pivotn) == GridPane.getRowIndex(rotaten)){ // same row 0 
                // 0,0  - 1,0 => 0,0 - 0,1
                System.out.println(" *** PRotateCW90  same row moving rotate vertical");
                this.plposdominogrd.getChildren().clear();                
                this.plposdominogrd.add(pivotn,0,0,1,1);
                pivotn.setStyle("-fx-background-color: cyan;"); 
                this.plposdominogrd.add(CreateEmptyGridpaneSquare(1, 0, "gpos"),1,0,1,1); 
                this.plposdominogrd.add(rotaten,0,1,1,1); 
                rotaten.setStyle("-fx-background-color: lightsteelblue;"); 
                this.plposdominogrd.add(CreateEmptyGridpaneSquare(1, 1, "gpos"),1,1,1,1); 
                Square sq1 = (Square)(((HBox)pivotn).getUserData());
                Square sq2 = (Square)(((HBox)rotaten).getUserData());
                sq1.whichSqr=1;
                sq2.whichSqr=2; 
                sq1.relativeRow=0;
                sq1.relativeCol=0;
                sq2.relativeRow=1;
                sq2.relativeCol=0;
                p.avBoardPositions = FindAvPositions( sq1, sq2) ;
                // for(BoardTilePos bp : p.avBoardPositions){
                //    System.out.printf(" *** position 1) %s (%d,%d) 2) %s (%d,%d) %n",bp.landscape1.getName(), bp.col1,bp.row1, bp.landscape2, bp.col2, bp.row2);
                // }
            }
            else if(GridPane.getColumnIndex(pivotn) == GridPane.getColumnIndex(rotaten)) { // same col 0 
                System.out.println(" *** PRotateCW90  same col moving rotate horizontal");
                this.plposdominogrd.getChildren().clear();
                this.plposdominogrd.add(rotaten,0,0,1,1);
                rotaten.setStyle("-fx-background-color: cyan;");
                this.plposdominogrd.add(pivotn,1,0,1,1);
                pivotn.setStyle("-fx-background-color: lightsteelblue;");
                this.plposdominogrd.add(CreateEmptyGridpaneSquare(0, 1, "gpos"),0,1,1,1); 
                this.plposdominogrd.add(CreateEmptyGridpaneSquare(1, 1, "gpos"),1,1,1,1); 
                Square sq1 = (Square)(((HBox)rotaten).getUserData());
                Square sq2 = (Square)(((HBox)pivotn).getUserData()); 
                sq1.whichSqr=1;
                sq2.whichSqr=2;  
                sq1.relativeRow=0;
                sq1.relativeCol=0;
                sq2.relativeRow=0;
                sq2.relativeCol=1;
                p.avBoardPositions = FindAvPositions( sq1, sq2) ;
                // for(BoardTilePos bp : p.avBoardPositions){
                //    System.out.printf(" *** position 1) %s (%d,%d) 2) %s (%d,%d) %n",bp.landscape1.getName(), bp.col1,bp.row1, bp.landscape2, bp.col2, bp.row2);
                // }
            }              
        }            
    }

    /**
     * when butoon FlipTile clicked
     * @param e mouse event
     */
    void hOnMouseFlipTile(Event e){
        //TODO: complete FlipTile ...
    }

    /**
     * when button Rotate done clicked
     * @param e mouse event
     */
    void hOnMouseRotateDone(Event e){
        Player p = playerClient.getPlayerByGuiNo(1);
        if(p!=null) {
            if(p.avBoardPositions.size()==0){
                doAlert("Δεν υπάρχουν διαθέσιμες θέσεις για το domino", "Δεν υπάρχουν διαθέσιμες θέσεις για το domino", "");
                return;
            }
            Node n1 = getNodeByRowColumnIndex(0, 0, this.plposdominogrd);  // square, pivot at 0,0
            if( n1==null) return;
            Node n2 = null;
            for(Node n : this.plposdominogrd.getChildren()){
                if(n!=null && !(GridPane.getRowIndex(n)==0 && GridPane.getColumnIndex(n)==0) && n.getUserData() !=null){
                    // System.out.printf(" *** PRotateCW90  found rotaten r=%d, c=%d %n",GridPane.getRowIndex(n),GridPane.getColumnIndex(n));
                    n2 = getNodeByRowColumnIndex(GridPane.getRowIndex(n), GridPane.getColumnIndex(n), this.plposdominogrd);
                    break;
                }
            }
            if( n2==null) return;
            Square sqr1 = (Square)(((HBox)n1).getUserData());
            Square sqr2 = (Square)(((HBox)n2).getUserData());
            sqr1.whichSqr=1;
            sqr2.whichSqr=2;
            p.selSquare1=sqr1;
            p.selSquare2=sqr2;

            ((GridPane)p.boardPane).setDisable(false);
            for( BoardTilePos bp : p.avBoardPositions){
                if(bp.landscape1== p.selSquare1.landscapeType && bp.landscape2==p.selSquare2.landscapeType){
                    Node cell = getNodeByRowColumnIndex(bp.row1, bp.col1, (GridPane)p.boardPane);
                    cell.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBoardCell);
                }
            }
            doAlert("King "+p.playerColor.toUpperCase()+" place your selected domino on board", "King "+p.playerColor.toUpperCase()+" place your selected domino on board", "");
            this.logtext.setText(String.format("Τoποθέτηση του επιλεγμένου dominio στο βασίλειο για τον βασιλιά %s... ", p.playerColor.toUpperCase() ) );

            // this.btnplacementDone.setDisable(false);

            // ??????????????? this.vbposdomgrd.setDisable(true);
            // ---------------- this.vbposdomgrd.setVisible(false);

            // TabpansSelectionModel.select((Tab)p.tab);
            // doAlert("King "+p.playerColor.toUpperCase()+" place your selected domino on board", "King "+p.playerColor.toUpperCase()+" place your selected domino on board", "");
            // this.logtext.setText(String.format("Τoποθέτηση του επιλεγμένου dominio στο βασίλειο για τον βασιλιά %s... ", p.playerColor.toUpperCase() ) );
            // this.btnDiscardTile.setDisable(false);
            // p.kingTile = new Tile(sqr1, sqr2, false);

        }

    }

    /**
     * when mouse clicked on a kingdom board gridpane cell
     */
    public void hOnMouseClickedBoard(javafx.scene.input.MouseEvent event){
        event.consume();

        Node clickedNode = event.getPickResult().getIntersectedNode();
        Node parent = clickedNode.getParent();
        while (!(parent instanceof GridPane)) {
            clickedNode = parent;
            parent = clickedNode.getParent();
        }
        //System.out.print("clickedNode=");   // clickedNode=HBox[id=hbx-g1brdcell-4-3]
        //System.out.println(clickedNode);
        //Integer colIndex = GridPane.getColumnIndex(clickedNode);
        //Integer rowIndex = GridPane.getRowIndex(clickedNode);

        //System.out.print("event.getTarget()=");
        //System.out.println(event.getTarget());      // ImageView@2bd15719[styleClass=image-view]
        //System.out.print("event.getEventType()=");
        //System.out.println(event.getEventType());   // MOUSE_CLICKED
        //System.out.print("event.getPickResult()=");
        //System.out.println(event.getPickResult());  // PickResult [node = ImageView@2bd15719[styleClass=image-view], point = Point3D [x = 24.0, y = 31.0, z = 0.0], distance = 1675.690812598426 
        //System.out.print("event.getPickResult().getIntersectedNode()=");
        //System.out.println(event.getPickResult().getIntersectedNode()); // ImageView@2bd15719[styleClass=image-view]

        // if( event.getTarget() instanceof HBox) {
        if( clickedNode instanceof HBox) {
            // HBox hbcell = (HBox)event.getTarget();
            HBox hbcell = (HBox)clickedNode;
            //System.out.print(" parent of Hbox : ");
            //System.out.println( hbcell.getParent());
            //System.out.println( hbcell.getParent().getClass().getName());
            //System.out.println( hbcell.getParent().getClass().getSimpleName());
            //Parent grdparent = hbcell.getParent();
            GridPane grdboard = (GridPane)hbcell.getParent();
            System.out.println(" -- parent is GridPane ");
            if( grdboard.getUserData() instanceof Player){
                    //System.out.print(" -- parent GridPane assigned a player ");
                    //System.out.println(grdboard.getUserData());
                    Player p = (Player)grdboard.getUserData();

                    /* castle has not placed, so this click is for placing castle on board */
                    if( p.GuiPlayerNumber == gCurrentGuiPlayerNumber &&  p.castlePlaced==false){
                        handleCastlePlacement(p, hbcell, grdboard);
                        return;
                    }
                    /* castle placed, domino selected, so this click is for placing selected domino squares on board
                    * selectedBoardPlace will decide if it is a placment for 1st square or 2nd square */
                    if(p.GuiPlayerNumber == gCurrentGuiPlayerNumber &&  p.castlePlaced==true && p.tileSelected==true ){
                        selectedBoardPlace(event, p.GuiPlayerNumber, GridPane.getColumnIndex(hbcell),GridPane.getRowIndex(hbcell), (GridPane)p.boardPane);
                    }
            }
            else {
                System.out.print(" -- parent GridPane has not assigned a player ");
            }
        } 
        else {
            System.out.println(" ERROR, hOnMouseClickedBoard:: HBOX not found  ");
        }
        return;
    }

    /** 
     * when tile discard button clicked
     * @param e mouse event
     */
    void hOnMouseTileDiscard(Event e){
        Player p = playerClient.getPlayerByGuiNo(gCurrentGuiPlayerNumber);
        if(p!=null) {
            if(p.tileSelected==true && ( p.selSquare1 !=null || p.selSquare2 !=null )){
                GridPane grdb ;
                GridPane grds ; 
                grdb = (GridPane)p.boardPane;
                grds = (GridPane)p.seldomPane;
                grdb.setDisable(true);
                grds.setDisable(true);
                // this.btnplacementDone.setDisable(true);
                // this.btnDiscardTile.setDisable(true);
                playerClient.TilePlaced(p.playerNumber, p.kingNumber, p.playerName, p.playerColor, p.selSquare1.tileAa, "discarded",
                p.selSquare1.Square, p.selSquare1.crowns, p.selSquare1.strColor, p.selSquare1.rowonBoard,p.selSquare1.colonBoard,
                p.selSquare2.Square, p.selSquare2.crowns, p.selSquare2.strColor, p.selSquare2.rowonBoard,p.selSquare2.colonBoard );
                this.logtext.setText(String.format("Tile discarded sent to server for King %s. waiting for my turn...",p.playerColor));
                for(Node n : ((GridPane)p.boardPane).getChildren()){
                    n.setStyle("-fx-background-color: white;");
                }
                // p.boardPositions = new ArrayList<BoardPos>();
                p.avBoardPositions = new ArrayList<BoardTilePos>(); 

                animKingStop();
            
                p.square1Selected = false;
                p.square2Selected = false;
                p.square1Placed = false;
                p.square2Placed = false;
                p.selSquare1 = null;
                p.selSquare2 = null;
                p.tileSelected = false;
                p.kingTile=null;
                p.nexttoking=false;

                //this.btnfipSquares.setDisable(true);
                //this.btnDiscardTile.setDisable(true);
                //this.btnRotCCW90.setDisable(true);
                //this.btnRotCW90.setDisable(true);                    
                //this.btnRot180.setDisable(true); 

                int myscore = p.CalcScore();
                p.lblBoard.setText(String.format("Kingdom. current score=%d",myscore));

                this.logtext.setText(String.format("`Πετάχτηκε` το ελεύθερο domino για το βασιλιά %s και στάλθηκε στον sever. Αναμονή για τους άλλους παίκτες...",
                p.playerColor.toUpperCase()));

                this.vbposdomgrd.setDisable(true);
                // ----------- this.vbposdomgrd.setVisible(false);
                
            }
        } 
    }
 
    /** 
     * when button domino tile placed clicked
     * domino placed on board
     * calls playerclient to update server with the tile number placed for this player
     * @param e mouse event
     */
    void hOnMouseTilePlaced(Event e) {
        Player p = playerClient.getPlayerByGuiNo(gCurrentGuiPlayerNumber);
        if(p!=null) {
            if(p.tileSelected==true && p.square1Placed==true && p.selSquare1 !=null && p.square2Selected==true && p.selSquare2 !=null  && p.square2Placed==true){
                    ((GridPane)p.seldomPane).setDisable(true);
                    ((GridPane)p.boardPane).setDisable(true);
                    //this.btnplacementDone.setDisable(true);
                    //this.btnDiscardTile.setDisable(true);
                    playerClient.TilePlaced(p.playerNumber, p.kingNumber, p.playerName, p.playerColor, p.selSquare1.tileAa, "placed",
                        p.selSquare1.Square, p.selSquare1.crowns, p.selSquare1.strColor, p.selSquare1.rowonBoard,p.selSquare1.colonBoard,
                        p.selSquare2.Square, p.selSquare2.crowns, p.selSquare2.strColor, p.selSquare2.rowonBoard,p.selSquare2.colonBoard );
                    this.logtext.setText("Tile place sent to server for King "+p.playerColor.toUpperCase()+" waiting for my turn...");
;
                    for(Node n : ((GridPane)p.boardPane).getChildren()){
                        n.setStyle("-fx-background-color: white;");
                    }
                    // p.boardPositions = new ArrayList<BoardPos>();
                    p.avBoardPositions = new ArrayList<BoardTilePos>();
      
                    animKingStop();

                    p.addLandscape(new Square(p.selSquare1,p.selSquare1.whichSqr));
                    p.addLandscape(new Square(p.selSquare2,p.selSquare2.whichSqr));
                    p.square1Selected = false;
                    p.square2Selected = false;
                    p.square1Placed = false;
                    p.square2Placed = false;
                    p.selSquare1 = null;
                    p.selSquare2 = null;
                    p.tileSelected = false;
                    p.kingTile=null;
                    p.nexttoking=false;

                    // debug:
                    System.out.println("PLACEMENT Done. player Landscapes:");
                    for(Square dsq : p.Landscapes){
                        System.out.print(dsq);
                    }
                    System.out.println("========================================");
                    

                    int myscore = p.CalcScore();
                    p.lblBoard.setText(String.format("Kingdom. current score=%d",myscore));

                    this.logtext.setText(String.format("Ολοκληρώθηκε η τοποθέτηση ελεύθερου domino για το βασιλιά %s και στάλθηκε στον sever. Αναμονή για τους άλλους παίκτες...",
                    p.playerColor.toUpperCase()));  
                    
                    this.vbposdomgrd.setDisable(true);

            }
        }
    }

    /** 
     * when domino cell clicked on domino selection row 
     * selects a domino to place the king
     * @param e event
     * @param guiPlayerNumber
     * @param col
     * @param row
     * @param thispane  domino selection gridpane
     */ 
    void selectSqrFirstRow(Event e, int guiPlayerNumber, int col, int row, GridPane thispane ){
        //e.consume();

        Player p = playerClient.getPlayerByGuiNo(guiPlayerNumber);
        if(p!=null) {
            
            // System.out.printf(" !!!!!!!!!! selectedSqrFirstRow r=%d, c=%d castlePlaced=%b, tileSelected=%b %n",row,col, p.castlePlaced, p.tileSelected);

            if(p.castlePlaced==false) {          
                doAlert("Problem", "Castle has not been placed on board yet", "");
                // System.out.printf("selectedSqrFirstRow r=%d, c=%d castlePlaced==FALSE Castle has not been placed on board yet return %n",row,col);
                return; // must first select the board cell for king
            }
            if(p.tileSelected == true){
                doAlert("Problem", "You cannot select another tile", "");
                // System.out.printf("selectedSqrFirstRow r=%d, c=%d tileSelected==TRUE You cannot select another tile return %n",row,col);
                return; // must first select the board cell for king               
            }
            else {
                Node cell1 = null;
                Node cell2 = null;
                if(col==0 || col==1 || col==3 || col==4 || col==6 || col==7 || col==9 || col==10){
                    if(col==0 || col==1) { // tile 1
                        cell1 = getNodeByRowColumnIndex(row, 0, thispane);
                        cell2 = getNodeByRowColumnIndex(row, 1, thispane);
                    } 
                    else if(col==3 || col==4) { // tile 2
                        cell1 = getNodeByRowColumnIndex(row, 3, thispane);
                        cell2 = getNodeByRowColumnIndex(row, 4, thispane);
                    }
                    else if(col==6 || col==7) { // tile 3
                        cell1 = getNodeByRowColumnIndex(row, 6, thispane);
                        cell2 = getNodeByRowColumnIndex(row, 7, thispane);
                    }
                    else if(col==9 || col==10) { // tile 4
                        cell1 = getNodeByRowColumnIndex(row, 9, thispane);
                        cell2 = getNodeByRowColumnIndex(row, 10, thispane);
                    }
                    Square.SquareType targetcell1 = Celltype(cell1);
                    Square.SquareType targetcell2 = Celltype(cell2);
                    Square square1=null;
                    Square square2=null;
                    if(targetcell1 == Square.SquareType.LANDSCAPE && targetcell2 == Square.SquareType.LANDSCAPE){
                        square1=(Square)(cell1.getUserData());
                        if(square1.kingNumber>=0){
                            doAlert("Wrong selection", "This Tile has been reserved by another King", "");
                            //System.out.printf("selectedSqrFirstRow r=%d, c=%d kingPlaced==FALSE This Tile hase been reserved by another King return %n",row,col);
                            return; // must first select the board cell for king 
                        }
                        square2=(Square)(cell2.getUserData());
                        if(square2.kingNumber>=0){
                            doAlert("Wrong selection", "This Tile has been reserved by another King", "");
                            //System.out.printf("selectedSqrFirstRow r=%d, c=%d kingPlaced==FALSE This Tile hase been reserved by another King return %n",row,col);
                            return; // must first select the board cell for king 
                        }
                        p.selSquare1=new Square(square1, square1.whichSqr);
                        p.selSquare2=new Square(square2, square2.whichSqr);
                        p.selSquare1.setKingInfo(p.kingNumber, p.playerColor, p.playerNumber, p.ClientNumber);
                        p.selSquare1.setBoardPosition(-1, -1);
                        p.selSquare2.setKingInfo(p.kingNumber, p.playerColor, p.playerNumber, p.ClientNumber);
                        p.selSquare2.setBoardPosition(-1, -1);
                        p.square1Selected=true;
                        p.square2Selected=true;
                        p.tileSelected = true;
                        p.kingTile = new Tile(p.selSquare1, p.selSquare2, false);
                        // reply to server 
                        playerClient.kingPlaced(p.playerNumber, p.kingNumber, p.playerName, p.playerColor, p.kingTile.aa, "selected", 
                            p.kingTile.Square1.Square, p.kingTile.Square1.crowns, p.kingTile.Square1.strColor, p.kingTile.Square1.rowonBoard, p.kingTile.Square1.colonBoard,
                            p.kingTile.Square2.Square, p.kingTile.Square2.crowns, p.kingTile.Square2.strColor,p.kingTile.Square2.rowonBoard,p.kingTile.Square2.colonBoard);
                        this.logtext.setText(String.format("Player %s - %s is Your TURN. King placement done. waiting for your turn...", p.playerName.toUpperCase(), p.playerColor.toUpperCase()) );

                        thispane.setDisable(true);

                        this.logtext.setText(String.format("Επιλέχθηκε ελεύθερο domino για το βασιλιά %s και στάλθηκε στον sever. Αναμονή για τους άλλους παίκτες...",
                        p.playerColor.toUpperCase()));

                    }
                }
            }            

        }
    }

    /**
     * the gridpane to deside the orientation of the domino before placed on board
     * called before settileonboard 
     * when oriantation set it checks placement availability
     * @param p Player
     */
    void selectTileOrientation(Player p ){

        //System.out.print(" **** selectTileOrientation kingtile=");
        //System.out.println(p.kingTile);
        //System.out.print(" **** selectTileOrientation p.kingTile.Square1=");
        //System.out.println(p.kingTile.Square1);
        //System.out.print(" **** selectTileOrientation p.kingTile.Square2=");
        //System.out.println(p.kingTile.Square2);

        // rebuild/redraw positioning gridpane with domino selected
        this.plposdominogrd.getChildren().clear();

        HBox cell1 = new HBox();
        cell1.getChildren().add( setLandscapeSelCell( p.selSquare1, p.GuiPlayerNumber) );
        this.plposdominogrd.add( cell1, 0, 0, 1, 1);
        p.selSquare1.relativeRow=0;
        p.selSquare1.relativeCol=0;
        cell1.setUserData(p.selSquare1);
        cell1.setStyle("-fx-background-color: cyan;");

        HBox cell2 = new HBox();
        cell2.getChildren().add( setLandscapeSelCell( p.selSquare2, p.GuiPlayerNumber) );
        this.plposdominogrd.add( cell2, 1, 0, 1, 1);
        p.selSquare2.relativeRow=0;
        p.selSquare2.relativeCol=1;
        cell2.setUserData(p.selSquare2);
        cell2.setStyle("-fx-background-color: lightsteelblue;");

        this.plposdominogrd.add( CreateEmptyGridpaneSquare(0,1, "gpos"), 0, 1, 1, 1);
        this.plposdominogrd.add( CreateEmptyGridpaneSquare(1,1, "gpos"), 1, 1, 1, 1);
                
        p.avBoardPositions = FindAvPositions(p.selSquare1,p.selSquare2);

        this.vbposdomgrd.setDisable(false);
        this.vbposdomgrd.setVisible(true);

        //this.btnRotCW90.setDisable(false);
        //this.btnfipSquares.setDisable(false);
        //this.btnRotDone.setDisable(false);

    }

   /** 
     * when clicked on kingdomino board cell  to place the selected domino
     * @param e event
     * @param guiPlayerNumber
     * @param col
     * @param row
     * @param thispane the board gridpane
     */
    void selectedBoardPlace(Event e, int guiPlayerNumber, int col, int row, GridPane thispane ){
        //System.out.println(e.getSource())
        System.out.printf("selectedBoardPlace r=%d, c=%d ...%n",row,col);
        //e.consume();
        Player p = playerClient.getPlayerByGuiNo(guiPlayerNumber);
        if(p==null) return ;

        if(p.tileSelected==true && p.square1Selected==true && p.selSquare1 !=null && p.square1Placed==false && p.square2Selected==true && p.selSquare2 !=null  && p.square2Placed==false){
            //System.out.printf(" ** PLACEMENT selectedBoardPlace :: r=%d, c=%d square1Placed==false, square2Placed==false ...%n",row,col);
            if(p.avBoardPositions.size()<=0){
                doAlert("Δεν υπάρχουν διαθέσιμες θέσεις για το domino", "Πρέπει να το `πεταξετε`", "");
                return;
            }
            // here we have the domino tile and the two Squares....
            for(BoardTilePos bp : p.avBoardPositions){
                if( bp.row1==row && bp.col1==col && bp.landscape1==p.selSquare1.landscapeType && bp.landscape2==p.selSquare2.landscapeType){
                    //System.out.printf(" ** PLACEMENT selectedBoardPlace :: r=%d, c=%d, square1 %s at (%d, %d), square2 %s at (%d,%d) ...%n",
                    //row,col, p.selSquare1.landscapeType.getName(), row, col, 
                    //p.selSquare2.landscapeType.getName(), row+bp.row2, col+bp.col2  );
                    p.selSquare1.setBoardPosition(row, col);
                    Node node1 = getNodeByRowColumnIndex(row, col, thispane);
                    HBox cell1 = (HBox)node1;
                    cell1.getChildren().clear();
                    Square brdsqr1 = new Square(p.selSquare1, p.selSquare1.whichSqr);
                    cell1.getChildren().addAll( setLandscapeSelCell( brdsqr1, p.GuiPlayerNumber) );
                    brdsqr1.setBoardPosition(row, col);
                    cell1.setUserData(brdsqr1);
                    
                    p.selSquare2.setBoardPosition(bp.row2, bp.col2);
                    Node node2 = getNodeByRowColumnIndex(bp.row2, bp.col2, thispane);
                    HBox cell2 = (HBox)node2;
                    cell2.getChildren().clear();
                    Square brdsqr2 = new Square(p.selSquare2, p.selSquare2.whichSqr);
                    cell2.getChildren().addAll( setLandscapeSelCell( brdsqr2, p.GuiPlayerNumber) );
                    brdsqr2.setBoardPosition(bp.row2, bp.col2);
                    cell2.setUserData(brdsqr2);

                    p.square1Placed=true;
                    p.square2Placed=true;
                }
            }                
        }
        else {
                doAlert("Wrong click", "No domino serlected for placement", "");
        } 
    }

    //#region  methods for creating *ui* domino squares 
   
    /**
     * creates an empty board square type of HBox
     * @param i gridpane col
     * @param j gridpabe row
     * @param idpart example idpart=p1brd
     */
    HBox CreateEmptyGridpaneSquare(int co, int ro, String idpart){

        HBox hbx = new HBox(setLandscapeSelCell( new Square(1, 1, 0, "empty", 0, 0, 0, "white", null), -1));
        hbx.setUserData(null);
        hbx.setPrefHeight(cellHeight);
        hbx.setPrefWidth(cellWidth);
        hbx.setBorder(new Border(new BorderStroke(Color.RED, 
         BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );

        hbx.setId(String.format("hbx-%scell-%d-%d", idpart, co, ro));       

        return hbx;

    }

    /** 
     * sets and draws a Tile (2 squares) in to the domino selection row, col
     * @param guiplayerno
     * @param gridPane the GridPane of the domino rows
     * @param col
     * @param row
     * @param tile the Tile to be placed
     */
    private void SetTileToSelGridPane(int guiplayerno, GridPane gridPane, int col, int row, Tile tile) {
        //System.out.printf("==> SetTileToSelGridPane player %d col=%d, row=%d, tile={%s} ... %n ", guiplayerno, col, row, tile.toString());
        int c=col;
        int r=row;
        
        Node node1 = getNodeByRowColumnIndex (r, c, gridPane);
        HBox cell1 = (HBox) node1;
        cell1.getChildren().clear();
        cell1.getChildren().addAll( setLandscapeSelCell( tile.Square1, guiplayerno) );
        cell1.setUserData(tile.Square1);
        //System.out.printf("==> SetTileToSelGridPane player %d col=%d, row=%d, Square1=%s ... %n ", guiplayerno, c, r, tile.Square1.Square);
        //System.out.println(tile.Square1);
        c+=1;
        Node node2 = getNodeByRowColumnIndex (r, c, gridPane);
        HBox cell2 = (HBox) node2;
        cell2.getChildren().clear();
        cell2.getChildren().addAll( setLandscapeSelCell(tile.Square2, guiplayerno) );
        cell2.setUserData(tile.Square2);
        //System.out.printf("==> SetTileToSelGridPane player %d col=%d, row=%d, Square2=%s ... %n ", guiplayerno, c, r, tile.Square2.Square);
        //System.out.println(tile.Square2);
        c+=1;
        Node node3 = getNodeByRowColumnIndex (r, c, gridPane);
        HBox cell3 = (HBox) node3;
        cell3.getChildren().clear();
        //gridPane.getChildren().removeAll(node3);
        Label lbl3 = new Label(String.format(" %d ",tile.aa));
        ImageView img32 ;
        if(tile.Square1.kingNumber>-1){
            img32 = drawSmallKingCastle(tile.Square1.playercolor);
        }
        else {
            img32 = new ImageView();
        }
        String img32id = String.format("seldom_%d_%d_%s_%d", r,c, "kimg", tile.Square1.tileAa);
        img32.setId(img32id);
        VBox vbx3 = new VBox(lbl3,img32);
        cell3.getChildren().addAll(vbx3);
        //System.out.printf("==> SetTileToSelGridPane player %d col=%d, row=%d, info=%s ... %n ", guiplayerno, c, r, lbl3.getText());
        cell3.setBorder(new Border(new BorderStroke(Color.RED, 
        BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );

        //System.out.printf("<== SetTileToSelGridPane player %d col=%d, row=%d End  %n ", guiplayerno, col, row);
        
    }

    /** 
    * returns an HBox (node) with the imageview of the tile square and userData set for dominos gridpane 
    * @param col
    * @param row
    * @param square
    * @param guiplayerno
    * @return HBox
    */
    private VBox setLandscapeSelCell( Square square, int guiplayerno) {
        try {
            Label lcolor = new Label(String.format("%s-%d",square.landscapeType.getName().substring(0, 4),square.crowns));
            VBox vb=new VBox();
            ImageView sqrImgview = square.setImageviewLandscape(imgHeight, imgWidth);
            sqrImgview.setUserData(square);
            vb.getChildren().addAll(sqrImgview,lcolor);
            vb.setUserData(square);
            return vb;
        }
        catch (Exception e) {
            return null;
        }
    } 

    /**
     * draws a small king castle 
     * @param color
     * @return
     */
    ImageView drawSmallKingCastle(String color){
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
           ImageView imageView = new ImageView();
           imageView.setFitHeight(imgHeight/2);
           imageView.setFitWidth(imgWidth/2);
           return imageView;
      }    

    }
   
    /** 
     * creates castle HBox with ImageView and userData
     * @param col
     * @param row
     * @param ktile
     * @param guiplayerno
     * @return HBox
     */
    public HBox setKingBoradCell( Square ksquare, int guiplayerno) {
        try {
            HBox hbimg = new HBox();
            hbimg.getChildren().addAll(ksquare.setImageviewCastle(imgHeight,imgWidth));
            hbimg.setBorder(new Border(new BorderStroke(Color.BLACK, 
            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)) );            
            hbimg.setUserData(ksquare);
            return hbimg;
        }
        catch (Exception e) {
            System.out.println(String.format("<== setKingCell EXCEPTION" ));
            return null;
        }
    } 

    /**
     * animates king castle on domino selection gridpane next to selected domino
     */
    public void animmateKing(int tileaa){
        Player p = playerClient.getPlayerByGuiNo(gCurrentGuiPlayerNumber);
        if(p!=null){
            for(int c=0;c<((GridPane)p.seldomPane).getColumnCount();c++){
                Node n = getNodeByRowColumnIndex(0,c, (GridPane)p.seldomPane);
                String img32id = String.format("#seldom_%d_%d_%s_%d", 0,c, "kimg", tileaa);
                Set<Node> nodeimgs = n.lookupAll(img32id);
                if(nodeimgs!=null && nodeimgs.size()>0){
                    for(Node nimg : nodeimgs){
                        ImageView img32 = (ImageView)nimg;
                        p.KingAnimtimeline = new Timeline();
                        p.KingAnimtimeline.setCycleCount(Timeline.INDEFINITE);
                        p.KingAnimtimeline.setAutoReverse(true);
                        p.KingAnimtimeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                          new KeyValue (img32.translateXProperty(), 25)));
                        p.KingAnimtimeline.play();                      
                    }
                }
            }
        }

    }

    /**
     * stops king castle animation
     */
    public void animKingStop(){
        Player p = playerClient.getPlayerByGuiNo(gCurrentGuiPlayerNumber);
        if(p!=null){
            try{
                p.KingAnimtimeline.stop();
                p.KingAnimtimeline = null;
            } catch(Exception ex){}
        }
    }
    // #endregion  

    //#region  ============ menu actions ================================

    /** 
    * called by menu option when user selects, "Join a game" menu option
    * calls PlayerClient playerClient to connect to kingdom server
    * @return boolean true on success / false on failure
    */
    public boolean connectToserver() {
   
        String msg = ""; 
        if( playerClient.isConnected) {
            msg = String.format("Opening socket with host %s:%d ... Altready Connected.", serverip, port);
            playerClient.logMessage=msg;
            this.logtext.clear();
            this.logtext.setText(playerClient.logMessage);
            System.out.println("DEBUG:: " + msg);
            return true;
        }       

        boolean successconnect = playerClient.connectToserver();

        this.logtext.clear();
        this.logtext.setText(playerClient.logMessage);

        return successconnect;

    }

    /** 
    * called by menu option Exit
    */  
    public void fmenuexit(){
        // if(isListening) pauseListening();

        Platform.exit();
        System.exit(0);
    }
   
    /** 
     * called by menu option "Stop game"
     * calls playerClient
     * @param act
     */
    void StopGame( boolean act){

        playerClient.setQuitRequest( true );
        this.logtext.setText("H Αίτηση για διακοπή παιχνιδιού στάλθηκε στον sever");
        return ;

    }
   
    /** 
     * called by menu option Help
     * displays game instructions
     * @param parent the parent stage
     */
    public void Help(Stage parent){
        Stage stage = new Stage();
        //Text text1 = new Text("On line Help");
        Text text2 = new Text("Συμμετέχετε σε ένα νέο παιχνίδι επιλέγοντας από το κυρίως μενού Game -> `Συμμετοχή σε παιχνίδι (register to game)`");
        Text text2a = new Text("\nAN είστε ο 2ος ή ο 3ος παίκτης μπορείτε να επιλέξετε `έναρξη του παιχνιού τώρα` κατά την εγγραφή.");

        Text text3 = new Text("\nΔιακοπή: Αν κάποια στιγμή θέλετε να διακόψετε το παιχνιδι επιλέγετε από το κυρίως μενού  Game -> `Διακοπή παιχνιδιού (stop game)`\n");
        Text text4 = new Text("\n\nΕΠΙΛΟΓΗ DOMINO KAI ΤΟΠΟΘΕΤΗΣΗ");
        text4.setFont(Font.font("Helvetica", FontWeight.BOLD, 10));
        text4.setFill(Color.BLUE);
        Text text5 = new Text("\nΟταν ο server στείλει τη νέα γραμμή dominos και το μήνυμα να τοποθετηθεί O Βασιλιάς τότε,");
        Text text6 = new Text("\n\nΒήμα 1. Κάντε κλικ σε ένα από τα Τοπία ενός ελεύθερου domino από τη 2η γραμμή για να το επιλέξετε και να τοποθετήσετε το βασιλιά σας.");
        Text text7 = new Text("\n\nΒήμα 2. Οταν ο server ζητήσει, κάντε κλικ σε ελεύθερο τετραγωνάκι του ταμπλώ για να τοποθετήσετε το κάστρο σας.");
        Text text8 = new Text("\n\nΒήμα 3. Οταν ο server ζητήσει, επιλέξτε την κατεύθυνση του domino που διαλέξατε.");
        Text text9 = new Text("\n\nΜε το κουμπί `Περιστροφή Δεξιόστροφα 90 μοίρες` περιστρέφετε το domino για να βρείτε τη σωστή θέση πριν κάνετε κλικ στο ταμπλώ.");
        Text text10 = new Text("\n\nΜε το κουμπί `ΟΚ` τελειώνετε με την κατεύθυνση με με κλικ στο ταμπλώ τοποθετείτε το domino στο ταμπλώ.");
        Text text11 = new Text("\n\nΜε το κουμπί `Τέλος Τοποθέτησης` ολοκληρώνετε την επιλογή σας και τη στέλνετε στον server. Μετά, περιμένετε τη σειρά σας");
        Text text12 = new Text("\n\nΜε το κουμπί `Discrad` ''πετάτε'' στα άχρηστα το domino (κάποιο domino που πρέπει οπωσδήποτε να έχετε επιλέξει) αν κανένα από τα dominos δεν μπορείτε να τοποθετήσετε.");
        Text text13 = new Text("\n\nΣτο τέλος του παιχνιδιού ο server ανακοινώνει το νικητή.");
        TextFlow textFlow = new TextFlow( text2,text2a, text3, text4, text5, text6, text7, text8, text9, text10, text11,text12, text13);

        Scene scene = new Scene(textFlow, 600, 600);
        stage.setTitle("On line Help");
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.showAndWait();
    }

    /** 
     * called by menu option About
     * displays About 
     * @param stage the parent stage
     */
    public void About(Stage stage){
        doAlert("KingDomino creators", "George Stylianopoulos, AM:  2022201900219  \nGeorge Petrogiannopoulos AM: 2022201900182  \n", "our version of KingDomino game");
    }

    /** 
     * called by menu option "Stop game"
     * calls playerClient
     */
    void quitGame(){
        playerClient.setQuitRequest(true);
        this.logtext.setText("H Αίτηση για διακοπή παιχνιδιού στάλθηκε στον sever");
    }

// #endregion 

   
  
    
 
    /**
     * find and draws all available positions on the board for the selected domino 
     * orientation choosed before
     * @param sqr1
     * @param sqr2
     * @return an ArrayList<BoardTilePos> with all the available positions for placement or null
     */
    ArrayList<BoardTilePos> FindAvPositions(Square sqr1,Square sqr2){
        ArrayList<BoardTilePos> abp = new ArrayList<BoardTilePos>();

        Player p = playerClient.getPlayerByGuiNo(gCurrentGuiPlayerNumber);
        if(p==null) return null;

        // reset all cells to white background ...
        for(Node n : ((GridPane)p.boardPane).getChildren()){
            if(n!=null){
                if(n instanceof HBox){
                    //n.setStyle("-fx-border: 30px solid; -fx-background-color: white;-fx-border-color: red;");               
                    n.setStyle("-fx-background-color: white;"); 
                }
            }
        }
        // if must be placed next to castle - the first domino ...
        if(p.nexttoking==true){
            int r = p.castleSquare.rowonBoard;
            int c = p.castleSquare.colonBoard;
            // BoardTilePos bp =null;
            if(sqr1.relativeRow==sqr2.relativeRow) {  // horizontal
                if(r-1>=0){
                    if(c-1>=0) abp.add(new BoardTilePos(r-1, c-1, sqr1.landscapeType, r-1, c, sqr2.landscapeType));
                    if(c+1<boardColNum) abp.add( new BoardTilePos(r-1, c, sqr1.landscapeType, r-1, c+1, sqr2.landscapeType));
                }
                if(r+1<boardRowNum) {
                    if(c-1>=0) abp.add(new BoardTilePos(r+1, c-1, sqr1.landscapeType, r+1, c, sqr2.landscapeType));
                    if(c+1<boardColNum) abp.add(new BoardTilePos(r+1, c, sqr1.landscapeType, r+1, c+1, sqr2.landscapeType));
                }
                if( c-2>=0){
                    abp.add(new BoardTilePos(r, c-2, sqr1.landscapeType, r, c-1, sqr2.landscapeType));
                }
                if( c+2<boardColNum){
                    abp.add(new BoardTilePos(r, c+1, sqr1.landscapeType, r, c+2, sqr2.landscapeType));
                }
            }
            else {      // vertical
                if(r-2>=0){
                    abp.add(new BoardTilePos(r-2, c, sqr1.landscapeType, r-1, c, sqr2.landscapeType));
                }
                if(r+2<boardRowNum){
                    abp.add(new BoardTilePos(r+1, c, sqr1.landscapeType, r+2, c, sqr2.landscapeType));
                }
                if(c-1>=0 ){
                    if( r-1>=0) abp.add(new BoardTilePos(r-1, c-1, sqr1.landscapeType, r, c-1, sqr2.landscapeType));
                    if( r+1<boardRowNum) abp.add(new BoardTilePos(r, c-1, sqr1.landscapeType, r+1, c-1, sqr2.landscapeType));
                }
                if(c+1<boardColNum){
                    if( r-1>=0) abp.add(new BoardTilePos(r-1, c+1, sqr1.landscapeType, r, c+1, sqr2.landscapeType));
                    if( r+1<boardRowNum) abp.add(new BoardTilePos(r, c+1, sqr1.landscapeType, r+1, c+1, sqr2.landscapeType));
                }
            }
        }

        else {   // next to an existing same landscape

            for(Node n : ((GridPane)p.boardPane).getChildren()){
                if(n!=null && (Celltype(n)==Square.SquareType.LANDSCAPE)){
                    Square exsqr = (Square)((HBox)n).getUserData();

                    int r = GridPane.getRowIndex(n);
                    int c = GridPane.getColumnIndex(n);

                    // an synolo <=5 kai i thesi > min & thesi < max !!!!

                    if(sqr1.relativeRow==sqr2.relativeRow){ // horizontal

                        int rk = p.castleSquare.rowonBoard;
                        int ck = p.castleSquare.colonBoard;
                        if(rk-1>=0){
                            if(ck-1>=0) abp.add(new BoardTilePos(rk-1, ck-1, sqr1.landscapeType, rk-1, ck, sqr2.landscapeType));
                            if(ck+1<boardColNum) abp.add( new BoardTilePos(rk-1, ck, sqr1.landscapeType, rk-1, ck+1, sqr2.landscapeType));
                        }
                        if(r+1<boardRowNum) {
                            if(ck-1>=0) abp.add(new BoardTilePos(rk+1, ck-1, sqr1.landscapeType, rk+1, ck, sqr2.landscapeType));
                            if(ck+1<boardColNum) abp.add(new BoardTilePos(rk+1, ck, sqr1.landscapeType, rk+1, ck+1, sqr2.landscapeType));
                        }
                        if( ck-2>=0){
                            abp.add(new BoardTilePos(rk, ck-2, sqr1.landscapeType, rk, ck-1, sqr2.landscapeType));
                        }
                        if( ck+2<boardColNum){
                            abp.add(new BoardTilePos(rk, ck+1, sqr1.landscapeType, rk, ck+2, sqr2.landscapeType));
                        }

                        if(exsqr.landscapeType==sqr1.landscapeType){

                            if(r-1>=0 && getTypeofCell(r-1, c, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            c+1<boardColNum && getTypeofCell(r-1, c+1, (GridPane)p.boardPane)==Square.SquareType.EMPTY){
                                int countinrow1 = p.numofSquaresinRow(r-1);
                                int countincol1 = p.numofSquaresinCol(c);
                                int countincol2 = p.numofSquaresinCol(c+1);
  
                                if(countinrow1<=3 && countincol1<=4 && countincol2 <=4) {
                                    abp.add( new BoardTilePos(r-1, c, sqr1.landscapeType, r-1, c+1, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }
                            if(r+1<boardRowNum && getTypeofCell(r+1, c, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            c+1<boardColNum && getTypeofCell(r+1, c+1, (GridPane)p.boardPane)==Square.SquareType.EMPTY){
                                int countinrow1 = p.numofSquaresinRow( r+1);
                                int countincol1 = p.numofSquaresinCol(c);
                                int countincol2 = p.numofSquaresinCol(c+1);

                                if(countinrow1<=3 && countincol1<=4 && countincol2 <=4)  {
                                    abp.add( new BoardTilePos(r+1, c, sqr1.landscapeType, r+1, c+1, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }
                            if(c+1<boardRowNum && getTypeofCell(r, c+1, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            c+2<boardColNum && getTypeofCell(r, c+2, (GridPane)p.boardPane)==Square.SquareType.EMPTY){
                                int countinrow1 = p.numofSquaresinRow( r);
                                int countincol1 = p.numofSquaresinCol(c+1);
                                int countincol2 = p.numofSquaresinCol(c+2);

                                if(countinrow1<=3 && countincol1<=4 && countincol2 <=4) { 
                                    abp.add( new BoardTilePos(r, c+1, sqr1.landscapeType, r, c+2, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }                            

                        }
                        else if(exsqr.landscapeType==sqr2.landscapeType) {

                            if(r-1>=0 && getTypeofCell(r-1, c, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            c-1>=0 && getTypeofCell(r-1, c-1, (GridPane)p.boardPane)==Square.SquareType.EMPTY) {
                                int countinrow1 = p.numofSquaresinRow(r-1);
                                int countincol1 = p.numofSquaresinCol(c);
                                int countincol2 = p.numofSquaresinCol(c-1);

                                if(countinrow1<=3 && countincol1<=4 && countincol2 <=4)  {
                                    abp.add( new BoardTilePos(r-1, c-1, sqr1.landscapeType, r-1, c, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }                             
                            if(r+1<boardRowNum && getTypeofCell(r+1, c, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            c-1>=0 && getTypeofCell(r+1, c-1, (GridPane)p.boardPane)==Square.SquareType.EMPTY) {
                                int countinrow1 = p.numofSquaresinRow(r+1);
                                int countincol1 = p.numofSquaresinCol(c);
                                int countincol2 = p.numofSquaresinCol(c-1);

                                if(countinrow1<=3 && countincol1<=4 && countincol2 <=4)  {
                                    abp.add( new BoardTilePos(r+1, c-1, sqr1.landscapeType, r+1, c, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }
                            if(c-2>=0 && getTypeofCell(r, c-2, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            c-1>=0 && getTypeofCell(r, c-1, (GridPane)p.boardPane)==Square.SquareType.EMPTY) {
                                int countinrow1 = p.numofSquaresinRow(r);
                                int countincol1 = p.numofSquaresinCol(c-2);
                                int countincol2 = p.numofSquaresinCol(c-1);

                                if(countinrow1<=3 && countincol1<=4 && countincol2 <=4)  {
                                    abp.add( new BoardTilePos(r, c-2, sqr1.landscapeType, r, c-1, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }
                        }


                    }
                    else {  // vertical


                        int rk = p.castleSquare.rowonBoard;
                        int ck = p.castleSquare.colonBoard;
                        if(rk-2>=0){
                            abp.add(new BoardTilePos(rk-2, ck, sqr1.landscapeType, rk-1, ck, sqr2.landscapeType));
                        }
                        if(rk+2<boardRowNum){
                            abp.add(new BoardTilePos(rk+1, ck, sqr1.landscapeType, rk+2, ck, sqr2.landscapeType));
                        }
                        if(ck-1>=0 ){
                            if( rk-1>=0) abp.add(new BoardTilePos(rk-1, ck-1, sqr1.landscapeType, rk, ck-1, sqr2.landscapeType));
                            if( rk+1<boardRowNum) abp.add(new BoardTilePos(rk, ck-1, sqr1.landscapeType, rk+1, ck-1, sqr2.landscapeType));
                        }
                        if(ck+1<boardColNum){
                            if( rk-1>=0) abp.add(new BoardTilePos(rk-1, ck+1, sqr1.landscapeType, rk, ck+1, sqr2.landscapeType));
                            if( rk+1<boardRowNum) abp.add(new BoardTilePos(rk, ck+1, sqr1.landscapeType, rk+1, ck+1, sqr2.landscapeType));
                        }

                        if(exsqr.landscapeType==sqr1.landscapeType ){

                            if( r+1<boardRowNum  && getTypeofCell(r+1, c, (GridPane)p.boardPane)==Square.SquareType.EMPTY && 
                            r+2<boardRowNum  && getTypeofCell(r+2, c, (GridPane)p.boardPane)==Square.SquareType.EMPTY ) {
                                int countinrow1 = p.numofSquaresinRow(r+1);
                                int countinrow2 = p.numofSquaresinRow(r+2);
                                int countincol1 = p.numofSquaresinCol(c);

                                if(countinrow1<=4 && countincol1<=3 && countinrow2 <=4)  { 
                                    abp.add( new BoardTilePos(r+1, c, sqr1.landscapeType, r+2, c, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }
                            if( c-1>=0 && getTypeofCell(r, c-1, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            r+1<boardRowNum && getTypeofCell(r+1, c-1, (GridPane)p.boardPane)==Square.SquareType.EMPTY) {
                                int countinrow1 = p.numofSquaresinRow(r);
                                int countinrow2 = p.numofSquaresinRow(r+1);
                                int countincol1 = p.numofSquaresinCol(c-1);

                                if(countinrow1<=4 && countincol1<=3 && countinrow2 <=4)  { 
                                    abp.add( new BoardTilePos(r, c-1, sqr1.landscapeType, r+1, c-1, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }
                            if( c+1<boardColNum && getTypeofCell(r, c+1, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            r+1<boardRowNum && getTypeofCell(r+1, c+1, (GridPane)p.boardPane)==Square.SquareType.EMPTY){
                                int countinrow1 = p.numofSquaresinRow(r);
                                int countinrow2 = p.numofSquaresinRow(r+1);
                                int countincol1 = p.numofSquaresinCol(c+1);

                                if(countinrow1<=4 && countincol1<=3 && countinrow2 <=4)  { 
                                    abp.add( new BoardTilePos(r, c+1, sqr1.landscapeType, r+1, c+1, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                           }
                        }

                        else if(exsqr.landscapeType==sqr2.landscapeType ){

                            if( r-2>=0  && getTypeofCell(r-2, c, (GridPane)p.boardPane)==Square.SquareType.EMPTY && 
                            r-1>=0  && getTypeofCell(r-1, c, (GridPane)p.boardPane)==Square.SquareType.EMPTY ){
                                int countinrow1 = p.numofSquaresinRow(r-2);
                                int countinrow2 = p.numofSquaresinRow(r-1);
                                int countincol1 = p.numofSquaresinCol(c);

                                if(countinrow1<=4 && countincol1<=3 && countinrow2 <=4)  { 
                                    abp.add( new BoardTilePos(r-2, c, sqr1.landscapeType, r-1, c, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }
                            if( c-1>=0 && getTypeofCell(r, c-1, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            r-1>=0 && getTypeofCell(r-1, c-1, (GridPane)p.boardPane)==Square.SquareType.EMPTY){
                                int countinrow1 = p.numofSquaresinRow(r);
                                int countinrow2 = p.numofSquaresinRow(r-1);
                                int countincol1 = p.numofSquaresinCol(c-1);

                                if(countinrow1<=4 && countincol1<=3 && countinrow2 <=4)  { 
                                    abp.add( new BoardTilePos(r-1, c-1, sqr1.landscapeType, r, c-1, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                            }
                            if( c+1<boardColNum && getTypeofCell(r, c+1, (GridPane)p.boardPane)==Square.SquareType.EMPTY &&
                            r-1>=0 && getTypeofCell(r-1, c+1, (GridPane)p.boardPane)==Square.SquareType.EMPTY){
                                int countinrow1 = p.numofSquaresinRow(r);
                                int countinrow2 = p.numofSquaresinRow(r-1);
                                int countincol1 = p.numofSquaresinCol(c+1);

                                if(countinrow1<=4 && countincol1<=3 && countinrow2 <=4)  { 
                                    abp.add( new BoardTilePos(r-1, c+1, sqr1.landscapeType, r, c+1, sqr2.landscapeType) );
                                } else {
                                    System.out.printf(" *** will result to more than 5 landscapes in sequence %n");
                                }
                           }
                        }
                    }

                }
            }
        }
        for(BoardTilePos bp : abp) {
            System.out.printf(" *** findAvPosition, paint %d,%d  %d,%d %n", bp.col1,bp.row1, bp.col2,bp.row2);
            Node n1 = getNodeByRowColumnIndex(bp.row1, bp.col1, (GridPane)p.boardPane);            
            Node n2 = getNodeByRowColumnIndex(bp.row2, bp.col2, (GridPane)p.boardPane);            
            // n1.setStyle("-fx-border: 30px solid; -fx-background-color: cyan;-fx-border-color: black;");
            n1.setStyle("-fx-background-color: cyan;");
            n2.setStyle("-fx-background-color: lightsteelblue;");

        }
        return abp;
    }



    /** 
     * displays a general Alert dialog (wait)
     * @param title the Alert title
     * @param headerText the headet text
     * @param message the footer text
     */
    public void doAlert(String title, String headerText, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message); 
        alert.showAndWait();
    }
      
    /** 
     * changes main stage window title after gui has been established
     * @param title the new window title
     */
    public void setWindowTitle(String title){
        this_stage.setTitle(title);
    }
  
    //#region  methods called by PlayerClient to respond to server messages

 
    /** 
     * called by PlayerClient when server requested REGISTRATION
     * @param playernames comma (;) delimited already registered player names
     * @param KingColors comma (;) delimited already reserved king colors
     * @param canStartgame true is user is allowed to request start game
     * @param playernumber the player number assigned by server
     * @param kingnumber the player number assigned by server
     */
    public void doPlayerNameDialog(String playernames, String KingColors, boolean canStartgame, int playernumber, int kingnumber){
        System.out.printf("Client.doPlayerNameDialog called from PlayerCLient... %n");

        PlayerNameDialog(this_stage, canStartgame,playernames, KingColors, playernumber, kingnumber, false);
    }
      
    /** 
     * displays the registration modal dialog (asks for player name and color) and
     * sets the player info of this player (0 or 1) in PlayerClient.players table
     * @param parent parent Stage
     * @param canStartgame true is user is allowed to request start game
     * @param playernames comma (;) delimited already registered player names
     * @param KingColors comma (;) delimited already reserved king colors
     * @param playernumber the player number assigned by server
     * @param kingnumber the player number assigned by server
     * @param secondKing true if is registration for second king
     */
    public void PlayerNameDialog(Stage parent, boolean canStartgame,String playernames, String KingColors, int playernumber, int kingnumber
    , boolean secondKing) {
        Stage stage = new Stage();
        Label rn = new Label("Reserved Names="+playernames);
        Label rc = new Label("Reserved Colors="+KingColors);
        ComboBox<String> cbColors = new ComboBox<String>();
        
        if(!KingColors.contains("red") ){
            cbColors.getItems().add("red"); 
            //cbColors.setValue("red");           
        }
        if(!KingColors.contains("green") ){
            cbColors.getItems().add("green");
            //cbColors.setValue("green");            
        }
        if(!KingColors.contains("blue") ){
            cbColors.getItems().add("blue");
            //cbColors.setValue("blue");            
        }
        if(!KingColors.contains("yellow") ){
            cbColors.getItems().add("yellow");
            //cbColors.setValue("yellow");            
        }

        Label l = new Label("Type your player name: ");
        TextField playername = new TextField();
        Label l2 = new Label("Select your King Color:  (red/blue/green/yellow");
        TextField colorname = new TextField();  // defco
        colorname.setVisible(false);

        cbColors.setOnAction((event) -> {
            //int selectedIndex = cbColors.getSelectionModel().getSelectedIndex();
            Object selectedItem = cbColors.getSelectionModel().getSelectedItem();
            colorname.setText((String)(selectedItem));
        });

        Button btnRegister = new Button("Register");
        btnRegister.setOnAction(e -> {

            if(playername.getText().length()<=0){
                doAlert("Πρόβλημα", "δεν έχετε δώσει όνομα παίκτη", "");
                return;
            }
            if(colorname.getText().length()<=0){
                doAlert("Πρόβλημα", "δεν έχετε επιλέξει χρώμα", "");
                return;
            }

            playerClient.setRegPlayerName( playername.getText() );
            playerClient.setRegPlayerColor( colorname.getText() );
            playerClient.setRegPlayerNumber(playernumber);
            playerClient.setRegKingNumber(kingnumber);
            playerClient.setStartGameNow( false );
            stage.close();
            int cpl = 0;
            if(secondKing) cpl = 1;
            
            if( playerClient.Register() )
            {
                Player p = playerClient.players[cpl];

                String tablstylestring="";
                Color infoforegcolor = Color.BLACK;
                if(colorname.getText().contains("red")){
                    // stylestring = "-fx-background-color: red; -fx-color: white; -fx-text-background-color: white; -fx-text-base-color: white ;";
                    tablstylestring = "-fx-text-base-color: red;";
                    infoforegcolor = Color.RED;
                }
                else if(colorname.getText().contains("blue")){
                    tablstylestring = "-fx-text-base-color: blue;";
                    infoforegcolor = Color.BLUE;
                }
                else if(colorname.getText().contains("green")){
                    tablstylestring = "-fx-text-base-color: green;";
                    infoforegcolor = Color.GREEN;
                }
                else if(colorname.getText().contains("yellow")){
                    tablstylestring = "-fx-text-base-color: yellow;";
                    infoforegcolor = Color.YELLOWGREEN;
                }
                //loadCastle(playerClient.players[0].GuiPlayerNumber,playernumber,kingnumber,colorname.getText(),
                //playerClient.players[0].ClientNumber);
                Tab tab = new Tab();
                String tabText ="";
                //GridPane gp = new GridPane();;
                String textinfo ="";
                Label userinfo = new Label();
                if(p.playerNumber==0){
                    tab = this.tab1;
                    tabText=String.format("( ME )Player 1: %s-%s ",  playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    textinfo=String.format("( ME )Player 1: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    userinfo = this.user1info;
                    p.setboardPane(this.pl1Board);
                    p.setseldomPane(this.pl1seldoms);
                    p.settab(this.tab1);
                    p.setplboardLabel(this.lbl1Board);
                    this.pl1Board.setUserData(p);
                    this.pl1seldoms.setUserData(p);           
                }
                else if(p.playerNumber==1){
                    tab = this.tab2;
                    tabText=String.format("( ME )Player 2: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    textinfo=String.format("( ME )Player 2: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase()); 
                    userinfo = this.user2info;                       
                    p.setboardPane(this.pl2Board);
                    p.setseldomPane(this.pl2seldoms);
                    p.settab(this.tab2);
                    p.setplboardLabel(this.lbl2Board);
                    this.pl2Board.setUserData(p);
                    this.pl2seldoms.setUserData(p);
                }
                else if(p.playerNumber==2){
                    tab = this.tab3;
                    tabText=String.format("( ME )Player 3: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    textinfo=String.format("( ME )Player 3: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    userinfo = this.user3info;
                    p.setboardPane(this.pl3Board);
                    p.setseldomPane(this.pl3seldoms);
                    p.settab(this.tab3);
                    p.setplboardLabel(this.lbl3Board);
                    this.pl3Board.setUserData(p);
                    this.pl3seldoms.setUserData(p);                    
                }
                else if(p.playerNumber==3){
                    tab = this.tab4;
                    tabText=String.format("( ME )Player 4:  %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    textinfo=String.format("( ME )Player 4:  %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    userinfo = this.user4info;
                    p.setboardPane(this.pl4Board);
                    p.setseldomPane(this.pl4seldoms);
                    p.settab(this.tab4);
                    p.setplboardLabel(this.lbl4Board);
                    this.pl4Board.setUserData(p);
                    this.pl4seldoms.setUserData(p);
                }
                tab.setText(tabText);
                this.logtext.setText(String.format("(%d) Εγινε εγγραφή ως #%d %s - %s. Αναμονή για άλλους παίκτες... ", 
                    p.GuiPlayerNumber, playernumber,  playername.getText().toUpperCase(), colorname.getText().toUpperCase()));
                userinfo.setText(textinfo);
                
                tab.styleProperty().bind(Bindings.format(tablstylestring));
                userinfo.setTextFill(infoforegcolor);
            }
            this.connToserver.setDisable(true);
            this.stopGame.setDisable(false);


        });


        Button btnRegAndStart = new Button("Register and Start Game");
        btnRegAndStart.setOnAction(e -> {
            if(playername.getText().length()<=0){
                doAlert("Πρόβλημα", "δεν έχετε δώσει όνομα παίκτη", "");
                return;
            }
            if(colorname.getText().length()<=0){
                doAlert("Πρόβλημα", "δεν έχετε επιλέξει χρώμα", "");
                return;
            }

            playerClient.setRegPlayerName( playername.getText() );
            playerClient.setRegPlayerColor( colorname.getText() );
            playerClient.setRegPlayerNumber(playernumber);
            playerClient.setRegKingNumber(kingnumber);
            playerClient.setStartGameNow( true );
           stage.close();


            int cpl = 0;
            if(secondKing) cpl = 1;
            
            if( playerClient.Register() )
            {
                Player p = playerClient.players[cpl];

                String tablstylestring="";
                Color infoforegcolor = Color.BLACK;
                if(colorname.getText().contains("red")){
                    // stylestring = "-fx-background-color: red; -fx-color: white; -fx-text-background-color: white; -fx-text-base-color: white ;";
                    tablstylestring = "-fx-text-base-color: red;";
                    infoforegcolor = Color.RED;
                }
                else if(colorname.getText().contains("blue")){
                    tablstylestring = "-fx-text-base-color: blue;";
                    infoforegcolor = Color.BLUE;
                }
                else if(colorname.getText().contains("green")){
                    tablstylestring = "-fx-text-base-color: green;";
                    infoforegcolor = Color.GREEN;
                }
                else if(colorname.getText().contains("yellow")){
                    tablstylestring = "-fx-text-base-color: yellow;";
                    infoforegcolor = Color.YELLOWGREEN;
                }
                //loadCastle(playerClient.players[0].GuiPlayerNumber,playernumber,kingnumber,colorname.getText(),
                //playerClient.players[0].ClientNumber);
                Tab tab = new Tab();
                String tabText ="";
                //GridPane gp = new GridPane();;
                String textinfo ="";
                Label userinfo = new Label();
                if(p.playerNumber==0){
                    tab = this.tab1;
                    tabText=String.format("( ME )Player 1: %s-%s ",  playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    textinfo=String.format("( ME )Player 1: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    userinfo = this.user1info;
                    p.setboardPane(this.pl1Board);
                    p.setseldomPane(this.pl1seldoms);
                    p.settab(this.tab1);
                    p.setplboardLabel(this.lbl1Board);
                    this.pl1Board.setUserData(p);
                    this.pl1seldoms.setUserData(p);           
                }
                else if(p.playerNumber==1){
                    tab = this.tab2;
                    tabText=String.format("( ME )Player 2: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    textinfo=String.format("( ME )Player 2: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase()); 
                    userinfo = this.user2info;                       
                    p.setboardPane(this.pl2Board);
                    p.setseldomPane(this.pl2seldoms);
                    p.settab(this.tab2);
                    p.setplboardLabel(this.lbl2Board);
                    this.pl2Board.setUserData(p);
                    this.pl2seldoms.setUserData(p);
                }
                else if(p.playerNumber==2){
                    tab = this.tab3;
                    tabText=String.format("( ME )Player 3: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    textinfo=String.format("( ME )Player 3: %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    userinfo = this.user3info;
                    p.setboardPane(this.pl3Board);
                    p.setseldomPane(this.pl3seldoms);
                    p.settab(this.tab3);
                    p.setplboardLabel(this.lbl3Board);
                    this.pl3Board.setUserData(p);
                    this.pl3seldoms.setUserData(p);                    
                }
                else if(p.playerNumber==3){
                    tab = this.tab4;
                    tabText=String.format("( ME )Player 4:  %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    textinfo=String.format("( ME )Player 4:  %s-%s ", playername.getText().toUpperCase(), colorname.getText().toUpperCase());
                    userinfo = this.user4info;
                    p.setboardPane(this.pl4Board);
                    p.setseldomPane(this.pl4seldoms);
                    p.settab(this.tab4);
                    p.setplboardLabel(this.lbl4Board);
                    this.pl4Board.setUserData(p);
                    this.pl4seldoms.setUserData(p);
                }
                tab.setText(tabText);
                this.logtext.setText(String.format("(%d) Εγινε εγγραφή ως #%d %s - %s. Αναμονή για άλλους παίκτες... ", 
                    p.GuiPlayerNumber, playernumber,  playername.getText().toUpperCase(), colorname.getText().toUpperCase()));
                userinfo.setText(textinfo);
                
                tab.styleProperty().bind(Bindings.format(tablstylestring));
                userinfo.setTextFill(infoforegcolor);
            }
            this.connToserver.setDisable(true);           
          });

        if(canStartgame) btnRegAndStart.setDisable(false); else btnRegAndStart.setDisable(true);
        VBox vbbtns = new VBox();
        vbbtns.getChildren().addAll(btnRegister,btnRegAndStart);
        vbbtns.setAlignment(Pos.BASELINE_RIGHT);
        VBox vb = new VBox();
        vb.getChildren().addAll(rn,l,playername,rc,l2,cbColors,colorname,vbbtns);
        vb.setSpacing(10);
        Scene scene = new Scene(vb, 300, 300);
        stage.setTitle("Server requires Registration");
        stage.setScene(scene);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.showAndWait();
    }
 
    /** 
     * PlayerClient calls it when server sent register second king 
     * @param playernames
     * @param KingColors
     * @param canStartgame
     * @param playernumber
     * @param kingnumber
     */
    public void RegisterSecondKing(String playernames, String KingColors, boolean canStartgame, int playernumber, int kingnumber){
        System.out.printf("Client.RegisterSecondKing called from PlayerCLient... %n");
        PlayerNameDialog(this_stage, canStartgame,playernames, KingColors, playernumber, kingnumber,true);
    }
  
    /** 
     * PlayerClient calls it when server sent updatePlayersJoined
     * @param playmode
     * @param clientNum
     * @param playerNum
     * @param kingNum
     * @param registeredplayers a string with registered playerinfo for all registered players format
     * clnt0no;plr0no;plr0name;king0no;pllr0colr@clnt1no;plr1no;plr1name;king1no;pllr1colr@....
     */
    public void updatePlayersJoined( int playmode, int clientNum, int playerNum, int kingNum, String registeredplayers) {
        System.out.printf("Client.updatePlayersJoined called from PlayerCLient... %n");
        // registeredplayers=0;0;nik;0;red@0;1;geo;1;green@0;2;nik;2;blue@0;3;geo;3;yellow
        // registeredplayers=clnt0no;plr0no;plr0name;king0no;pllr0colr@clnt1no;plr1no;plr1name;king1no;pllr1colr@....
        //                     0    ;  1   ;   2    ;   3   ;   4
        Tab tab = new Tab();
        String tabtext = "";
        String infotext = "";
        Label userinfo = new Label();
        GridPane grdpbrd = new GridPane();
        GridPane grdpsel = new GridPane();
        if(registeredplayers.length()>0 && registeredplayers.contains("none")==false){
            String[] apl = registeredplayers.split("@");
            for(int i=0;i<apl.length;i++){
                String[] aflds = apl[i].split(";");                
                int kingno = Integer.parseInt(aflds[3]);
                Player p = playerClient.getPlayerByKingNo(kingno);

                if(i==0){
                    tab = this.tab1;
                    grdpbrd = this.pl1Board;
                    grdpsel = this.pl1seldoms;
                    userinfo = this.user1info;
                }
                else if(i==1){
                    tab = this.tab2;
                    grdpbrd = this.pl2Board;
                    grdpsel = this.pl2seldoms;
                    userinfo = this.user2info;
                }
                else if(i==2){
                    tab = this.tab3;
                    grdpbrd = this.pl3Board;
                    grdpsel = this.pl3seldoms;
                    userinfo = this.user3info;
                }
                else if(i==3){
                    tab = this.tab4;
                    grdpbrd = this.pl4Board;
                    grdpsel = this.pl4seldoms;
                    userinfo = this.user4info;
                }
                if( p != null){
                    // this king belongs to me 
                    tabtext = String.format("( ME ) %d %s-%s", p.kingNumber,p.playerName.toUpperCase(),p.playerColor.toUpperCase());
                    infotext = String.format("( ME )%d %s-%s", p.kingNumber,p.playerName.toUpperCase(),p.playerColor.toUpperCase());
                    grdpbrd.setUserData(p);
                    grdpsel.setUserData(p);
                    p.setboardPane(grdpbrd);
                    p.setseldomPane(grdpsel);
                    String tablstylestring="";
                    Color infoforegcolor = Color.BLACK;
                    if(p.playerColor.contains("red")){
                        // stylestring = "-fx-background-color: red; -fx-color: white; -fx-text-background-color: white; -fx-text-base-color: white ;";
                        tablstylestring = "-fx-text-base-color: red;";
                         infoforegcolor = Color.RED;
                    }
                    else if(p.playerColor.contains("blue")){
                        tablstylestring = "-fx-text-base-color: blue;";
                        infoforegcolor = Color.BLUE;
                    }
                    else if(p.playerColor.contains("green")){
                        tablstylestring = "-fx-text-base-color: green;";
                        infoforegcolor = Color.GREEN;
                    }
                    else if(p.playerColor.contains("yellow")){
                        tablstylestring = "-fx-text-base-color: yellow;";
                        infoforegcolor = Color.YELLOWGREEN;
                    }
                    tab.styleProperty().bind(Bindings.format(tablstylestring));
                    userinfo.setTextFill(infoforegcolor);
        
                }
                else {
                    tabtext = String.format("(other) %s %s-%s", aflds[3],aflds[2].toUpperCase(),aflds[4].toUpperCase());
                    infotext = String.format("(other) %s %s-%s", aflds[3],aflds[2].toUpperCase(),aflds[4].toUpperCase());
                    grdpbrd.setUserData(null);
                    grdpsel.setUserData(null);

                }
                tab.setText(tabtext);
                userinfo.setText(infotext);
            }
        }
    }
  
    /**
     * called by PlayerClient when Server requests PLACE_CASTLE_ON_BOARD
     * @param playmode
     * @param clientNum
     * @param playerNum
     * @param kingNum
     */
    public void positionCastle(int playmode,  int clientNum,  int playerNum,  int kingNum){
        System.out.printf("Client.positionCastle called from PlayerCLient... %n");
        Player p = playerClient.getPlayerByKingNo(kingNum);
        if( p != null ) {
            gCurrentGuiPlayerNumber = p.GuiPlayerNumber;
            GridPane grdboard = (GridPane)p.boardPane;
            grdboard.setDisable(false);
            for(int r=0;r<boardRowNum;r++){
                for(int c=0;c<boardColNum;c++){
                    Node cell = getNodeByRowColumnIndex(r, c, grdboard);
                    cell.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBoardCell);                 
                }
            }
            TabpansSelectionModel.select((Tab)p.tab);
            doAlert("Bασιλιά "+p.playerColor.toUpperCase()+" Κάστρο στο βασίλειο", "Βασιλιά "+p.playerColor.toUpperCase()+" βάλε το κάστρο σου στο βασίλιο", "");
            this.logtext.setText(String.format("Τoποθέτηση κάστρου στο βασίλειο για τον βασιλιά %s... ", p.playerColor.toUpperCase() ) );

        }
    }
    
    /**
     * called by PlayerClient when FIRST_ROW recieved from server to draw the available domino tiles row
     * @param playmode
     * @param clientNum
     * @param playerNum
     * @param kingNum
     * @param DrawLine ArrayList<Tile> the Tiles for the new drow
     * @param maxtiles
     */
    public void setTilesRow( int playmode, int clientNum, int playerNum, int kingNum, ArrayList<Tile> DrawLine, int maxtiles ){
        System.out.printf("Client.setTilesRow called from PlayerCLient... %n");
        Player p = playerClient.getPlayerByKingNo(kingNum);
        if( p != null ) {
            gCurrentGuiPlayerNumber = p.GuiPlayerNumber;
            System.out.printf("==> Client.setTilesRow player %d ... %n ", p.playerNumber);
            GridPane grdseldoms = (GridPane)p.seldomPane;
            int colno = 0;
            System.out.printf("==> Client.setTilesRow DrawLine : ");
            p.playerDrawLineTwo = new ArrayList<Tile>();
            for(Tile t: DrawLine) {  
                Tile nt = new Tile(t);                              
                System.out.println(nt);
                p.playerDrawLineTwo.add(nt);
                SetTileToSelGridPane(p.playerNumber,grdseldoms,colno,1,nt);
                colno +=3;
            }
            grdseldoms.setDisable(false);
        }
        System.out.printf("<== setTilesRow, player %d finished. %n", p.playerNumber);
        this.stopGame.setDisable(false);
        this.exit.setDisable(true);
    }  
            
    /** 
     * called by PlayerClient when UPDATE FIRST ROW update recieved from server ...
     * @param playmode
     * @param clientNum
     * @param playerNum
     * @param kingNum
     * @param DrawLine
     * @param maxtiles
     * @param isFirstrow
     */
    public void updateTilesRow(int playmode, int clientNum, int playerNum, int kingNum, ArrayList<Tile> DrawLine, int maxtiles, boolean isFirstrow,Boolean copytoback){
        System.out.printf("==> Client.updateTilesRow called from PlayerCLient with Kingno=%d... %n",kingNum);
        Player p = playerClient.getPlayerByKingNo(kingNum);
        if( p != null ) {
            gCurrentGuiPlayerNumber = p.GuiPlayerNumber;
            System.out.printf("    p.GuiPlayerNumber=%d",p.GuiPlayerNumber);
            // copy new draw line to player1DrawLineTwo - keep a backup
            if(copytoback==true) {
                System.out.print(" , copytoback = true ");
                p.playerDrawLineOne = new ArrayList<Tile>();
                for(Tile t : p.playerDrawLineTwo){
                    Tile nt = new Tile(t);
                    p.playerDrawLineOne.add(nt);
                }
                // drow top line -- the previous drow line ...
                int colno = 0;
                for(Tile t: p.playerDrawLineOne) {
                    System.out.printf("p.playerDrawLineOne : ... %n");
                    // System.out.println(player1DrawLineTwo);
                    SetTileToSelGridPane(p.playerNumber,(GridPane)p.seldomPane,colno,0,t);
                    colno +=3;
                }
            }

            System.out.println(" ");

            p.playerDrawLineTwo = new ArrayList<Tile>();
            for(Tile t: DrawLine){
                Tile nt = new Tile(t);
                p.playerDrawLineTwo.add(nt);
            }
 
            // make the new drow row of dominos (2nd row)            
            System.out.print( "=== Client.updateTilesRow :: DrawLine = [");
            System.out.print(DrawLine);
            System.out.println("]");
            System.out.print( "=== Client.updateTilesRow :: playerDrawLineTwo = [");
            System.out.print(p.playerDrawLineTwo);
            System.out.println("]");

            int colno = 0;           
            //for(Tile t: DrawLine) {
            for(Tile t: p.playerDrawLineTwo) {                
                System.out.print( "=== Client.updateTilesRow tile=");
                System.out.println(t);
                 SetTileToSelGridPane(p.playerNumber,(GridPane)p.seldomPane,colno,1,t);
                 colno +=3;
            }
            ((GridPane)p.seldomPane).setDisable(false);

            for(int c=0;c<boardColNum;c++){
                Node node = getNodeByRowColumnIndex(0, c, (GridPane)p.seldomPane);
                node.setDisable(true);
            }
        }
        System.out.printf("<== Client.updateTilesRow, player %d finished. %n", p.playerNumber);
    }

    /** 
     * called by PlayerClient when PLACE_KING recieved from server sets onmouseclickded selectedSqrFirstRow
     * so player can select the desired domino
     * * installs event handler evhMouseclickSeldom
     * @param playmode
     * @param clientNum
     * @param playerNum
     * @param kingNum
     */
    public void placeKing(int playmode, int clientNum, int playerNum, int kingNum){
        Player p = playerClient.getPlayerByKingNo(kingNum);
        if(p!=null) {
            gCurrentGuiPlayerNumber = p.GuiPlayerNumber;
            TabpansSelectionModel.select((Tab)p.tab); //select by object
                // TabpansSelectionModel.select(1); //select by index starting with 0
                // TabpansSelectionModel.clearSelection(); //clear your selection
        
            p.tileSelected = false;
            p.square1Selected = false;
            p.square2Selected = false;
            p.square1Placed = false;
            p.square2Placed = false;
            p.kingTile = null;
            p.selSquare1 = null;
            p.selSquare2 = null;

            System.out.printf("!!!! Client.placeKing called from PlayerCLient... %n");

            GridPane grdsel;
            this.logtext.setText(String.format("Player %s - %s is Your TURN. Place your king to an empty domino by cliking on the domino...", p.playerName.toUpperCase(), p.playerColor.toUpperCase()) );
            grdsel = (GridPane)p.seldomPane;

            int numofsel = 4;
            if(playerClient.gplaymode==3) numofsel=3;   // 3 cells for each domino, 0,1 landscapes, 2 info
            for(int i=0;i<3*numofsel;i++){
                Node cell = getNodeByRowColumnIndex(1, i, grdsel);
                if( Celltype(cell)==Square.SquareType.LANDSCAPE){
                    cell.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickSeldom);
                    // HBox hb = (HBox)cell;
                    // hb.setOnMouseClicked(e -> {
                    //     selectSqrFirstRow(e, p.GuiPlayerNumber, GridPane.getColumnIndex(cell), GridPane.getRowIndex(cell), grdsel );
                    // });
                }
            }
            grdsel.setDisable(false);
            String alertTitle = String.format("Player %s King %s ",p.playerName.toUpperCase(), p.playerColor.toUpperCase());
            String headerText=" Κάντε κλικ σε ένα από τα Τοπία ενός ελεύθερου domino από τη 2η γραμμή για να τοποθετήσετε το βασιλιά σας";
            String alertMessage = "";
            doAlert(alertTitle, headerText, alertMessage);
            this.logtext.setText(String.format("Τoποθέτηση βασιλιά %s σε ελεύθερο domino... ", p.playerColor.toUpperCase() ) );
        }
    }

    /**
     * called by PlayerClient when server send PLACE_TILE_ON_BOARD (after PLACE_KING)
     * installs event handler evhMouseclickBoardCell
     * @param playmode
     * @param clientNum
     * @param playerNum
     * @param kingNum
     */
    public void placeTileOnBoard(int playmode, int clientNum, int playerNum, int kingNum){
        Player p = playerClient.getPlayerByKingNo(kingNum);
        if(p!=null) {
            gCurrentGuiPlayerNumber = p.GuiPlayerNumber;
            if(p.castlePlaced==false) {          
                doAlert("Problem", "Castle has not been placed on board yet", "");
                System.out.printf("placeTileOnBoard castlePlaced==FALSE Castle has not been placed on board yet return %n");
                return; // must first select the board cell for king
            }
            if(p.tileSelected == false || p.kingTile==null){
                doAlert("Problem", "You have not placed your king on a domino in previous step", "game cannot continue");
                System.out.printf("placeTileOnBoard tileSelected==FALSE You have not placed your king on a domino in previous step return %n");
                return; // must first select the board cell for king               
            }
            
            animmateKing(p.kingTile.aa);

            this.logtext.setText(String.format("Τoποθέτηση επιλεγμένου domino στο board... " ) );

            this.btnDiscardTile.setDisable(false);

            selectTileOrientation(p);


            // if(p.nexttoking==true) p.boardPositions =  getAvailablePositions(p, Square.LandscapeType.CASTLE, Square.LandscapeType.CASTLE);
            // else p.boardPositions =  getAvailablePositions(p, p.selSquare1.landscapeType, p.selSquare2.landscapeType);

            // ((GridPane)p.boardPane).setDisable(false);
            // for(int r=0;r<boardRowNum;r++){
            //     for(int c=0;c<boardColNum;c++){
            //         Node cell = getNodeByRowColumnIndex(r, c, (GridPane)p.boardPane);
            //         cell.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, evhMouseclickBoardCell);
            //     }
            // }

            // TabpansSelectionModel.select((Tab)p.tab);
            // doAlert("King "+p.playerColor.toUpperCase()+" place your selected domino on board", "King "+p.playerColor.toUpperCase()+" place your selected domino on board", "");
            // this.logtext.setText(String.format("Τoποθέτηση του επιλεγμένου dominio στο βασίλειο για τον βασιλιά %s... ", p.playerColor.toUpperCase() ) );
            // this.btnDiscardTile.setDisable(false);
        }
    }

    /** 
     * called by PlayerClient for send score to server , called by  playerclient
     * @param playerNum
     * @param kingNum
     */
    public void sendScore(int playerNum, int kingNum){
        // int score = 0;
        Player p = playerClient.getPlayerByKingNo(kingNum);
        if(p!=null) {
            gCurrentGuiPlayerNumber = p.GuiPlayerNumber;
            p.CalcScore();
            playerClient.sendScore(p.playerNumber, p.kingNumber, p.playerName, p.playerColor, p.playerScore);
            this.logtext.setText(String.format("Αποστολή score στον server για τον βασιλιά %s... ", p.playerColor.toUpperCase() ) );
        }
    }

    /** 
     * called by PlayerClient when server seneds final score
     * @param aWinners
     */
    public void announceWinner(ArrayList<PlayerClient.Winner> aWinners){
        String message = "Οι νικητές είναι: \n";
        for (PlayerClient.Winner w : aWinners){
            message += String.format("%s, %s με score: %d \n",w.playerName.toUpperCase(), w.playerColor.toUpperCase(), w.playerScore);
        }
        doAlert("Οι νικητές του παιχνιδιού είναι", message, "");
    }

    /** 
     *  called by PlayerClient when game has stopped by server 
     * @param playernumber
     * @param kingno
     */
    void gameStopped(int playernumber, int kingno){
        System.out.printf("Client.gameStopped called from PlayerCLient... %n");
        doAlert("Το παιχνίδι διακόπηκε", "Ο διακομιστής ανακοίνωσε ότι το παιχνίδι διακόπηκε", "");
        this.logtext.setText(String.format("Το παιχνίδι διακόπηκε" ) );
        
        this.exit.setDisable(false);
    }

    /** 
     *  called by PlayerClient when  game has ended by server
     * @param playernumber
     * @param kingno
     */
    void gameEnd(int playernumber, int kingno){
        System.out.printf("Client.gameEnd called from PlayerCLient... %n");
        doAlert("Το παιχνίδι ολοκληρώθηκε", "Ο διακομιστής ανακοίνωσε ότι το παιχνίδι ολοκληρώθηκε", "");
        this.logtext.setText(String.format("Το παιχνίδι ολοκληρώθηκε" ) );
        this.exit.setDisable(false);
    }
 
    // #endregion 


  

//#region  ============ helper methods ================================
    /** 
     * utility returns a GridPane Node by its row,col
     * @param row
     * @param column
     * @param gridPane
     * @return Node
     */
    // utility function to find a gridpane node by coordinates, row, column
    public Node getNodeByRowColumnIndex (final int row, final int column, GridPane gridPane) {
        Node result = null;
        ObservableList<Node> childrens = gridPane.getChildren();        
        for (Node node : childrens) {
            if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }        
        return result;
    }
  
    /**
     * returns the Square object of the node in gridpane
     * @param node
     * @param gridPane
     * @return
     */
    public Square getSquarefromNode(Node node, GridPane gridPane){
        if(node==null) {
            return null;
        }
        HBox hb = (HBox) node;
        Object hbuserdata = hb.getUserData();
        //if (Objects.isNull(getUserObject) )
        if(hbuserdata == null) return null;
        else {
             if(hbuserdata instanceof Tile) return null;            
             else if (hbuserdata instanceof Square) return (Square)hbuserdata;
             else if (hbuserdata.getClass() == Square.class)  return (Square)hbuserdata;
             else if (hbuserdata.getClass() == Tile.class) return null; 
             else return null; 
        }       
    }

    /**
     * returns the square type of a node of a cell in gripdpane by row,col 
     * LANDSCAPE, CASTLE, EMPTY
     * @param row
     * @param col
     * @param gridPane
     * @return
     */
    Square.SquareType getTypeofCell( int row, int col, GridPane gridPane){
        return Celltype (getNodeByRowColumnIndex( row, col, gridPane) );
    }
    /**
     * returns the the Lanscape Type of square in cell
     * FARM, FIELD, ROCKS, ..., CASTLE, EMPTY
     * @param row
     * @param col
     * @param gridPane
     * @return
     */
    Square.LandscapeType getLandScapeofCell(int row, int col, GridPane gridPane){
        Node node = getNodeByRowColumnIndex( row, col, gridPane);
        if(node==null) {
            return Square.LandscapeType.UNKNWON;
        }
        if( Celltype(node) !=Square.SquareType.LANDSCAPE ) return Square.LandscapeType.UNKNWON;
        return ((Square)( ((HBox)node).getUserData()) ).landscapeType;
    }

    /** 
     * returns the square type of the node of the gridpane by Node
     * @param node
     * @return LANDSCAPE, CASTLE, EMPTY
     */
    private Square.SquareType Celltype( Node node ){
        if(node==null) {
            return Square.SquareType.EMPTY;
        }
        HBox hb = (HBox) node;
        Object hbuserdata = hb.getUserData();
        //if (Objects.isNull(getUserObject) )
        if(hbuserdata == null) return Square.SquareType.EMPTY;
        else {
             if(hbuserdata instanceof Tile) return Square.SquareType.TILE;            
             else if (hbuserdata instanceof Square) return ((Square)hbuserdata).squareType;
             else if (hbuserdata.getClass() == Square.class)  return ((Square)hbuserdata).squareType;
             else if (hbuserdata.getClass() == Tile.class) return Square.SquareType.TILE; 
             else return Square.SquareType.EMPTY; 
        }        
    }

// #endregion


}
