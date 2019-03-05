package game;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

import static game.PitchConstants.*;

public class Pitch implements DealerType{
    private Stage window;
    private Scene gameWindow;
    private BorderPane layout;

    private static  int windowWidth = 0;
    private static  int windowHeight = 0;

    private int playerCount = 0;

    private Player player;

    private ArrayList<AIPlayer> computerPlayers;



    //game elements
    private Deck gameField;
    private Deck currentTrick;
    private Deck playedCards;


    //timeline
    Timeline delay;

    //display elements
    private FlowPane placeholder;
    private StackPane cardStack;
    private Scoreboard scoreboard;
    private TrickList trickList;
    private Label Title;
    private Label RoundCount;
    private Label Prompt;

    //score calculator
    PitchScoreCalculator scoreCalculator;

    //gamestate managers
    private boolean bidWindowOpen = false;
    private boolean gameStarted = false;
    private boolean roundInProgress = false;
    private boolean roundEnded = false;
    private boolean roundSummaryInProgress = false;
    private boolean trickInProgress = false;
    private boolean bettingInProgress = false;
    private int roundCount = 0;
    private Player startPlayer = null;
    private Player currentPlayer = null;
    private boolean gameOver = false;
    private char currentTrumpSuit;
    private char currentLeadSuit;
    private int currentTrickNumber = 0;
    private int trickWinningIndex = 0;
    private PitchDealer pitchDealer;

    private ArrayList<Integer> currentScores;
    private ArrayList<Integer> currentBids;

    private  AnimationTimer gameThread;
    private AnimationTimer roundThread;
    private AnimationTimer trickThread;


    public void setCurrentTrumpSuit(char s){this.currentTrumpSuit = s;}
    public  void setCurrentLeadSuit(char s){this.currentLeadSuit = s;}
    public void setTrickWinningIndex(int i){this.trickWinningIndex = i;}
    public int getTrickNum(){return this.currentTrickNumber;}
    public  char getCurrentTrumpSuit(){return this.currentTrumpSuit;}
    public  char getCurrentLeadSuit(){return this.currentLeadSuit;}
    public void setCurrentTrick(Deck d){this.currentTrick = d;}
    public Deck getCurrentTrick(){return this.currentTrick;}
    public  Player getStartPlayer(){return this.startPlayer;}
    public Player getPlayer(){return this.player;}
    public void setCurrentPlayer(Player p){this.currentPlayer = p;}
    public  Player getCurrentPlayer(){return this.currentPlayer;}
    public void setRoundSummaryInProgress(boolean s){ this.roundSummaryInProgress = s;}
    public  void setBidWindowOpen(boolean s) { this.bidWindowOpen = s;}
    public  boolean getBidWindowOpen() {return this.bidWindowOpen;}
    public BorderPane getLayout(){return this.layout;}
    public StackPane getCardStack(){return this.cardStack;}
    public Scoreboard getScoreboard(){return this.scoreboard;}
    public boolean getGameStarted(){return this.gameStarted;}
    public Deck getGameField(){return this.gameField;}


    public ArrayList<Integer> getCurrentScores() {return this.currentScores;}
    public ArrayList<Integer> getCurrentBids() {return this.currentBids;}

    public int getPlayerCount() {return this.playerCount;}

    public PitchDealer createDealer(){
        return new PitchDealer();
    }


    public Pitch(Stage window, int playerCount, int windowWidth, int windowHeight) {
        this.window = window;
        this.playerCount = playerCount;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;




        scoreCalculator = new PitchScoreCalculator();

        //placeholder flowpane for Deck objects that interact with gui
        placeholder = new FlowPane();
        gameField = new Deck(placeholder);
        currentTrick = new Deck(placeholder);
        playedCards = new Deck(null);


        currentBids = new ArrayList<Integer>();
        currentScores = new ArrayList<Integer>();
        for(int i = 0; i < playerCount; i++){
            currentScores.add(0);
            currentBids.add(-1);
        }


        placeholder.getChildren().addListener(new ListChangeListener<Node>() {
            //listener that updates gameField stack in the middle of the screem
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {

                //update deck
                cardStack = new StackPane();

                if(!gameStarted) return;

                for(int i = 0; i < gameField.getCards().size(); i++) {
                    //intialize card to be added to stack
                    Card card = new Card(null,gameField.getCards().get(i).getRank(),gameField.getCards().get(i).getFace(),false);
                    card.setScale(1.25);
                    card.rotate(45 * i);

                    //push new rotated stack
                    cardStack.getChildren().add(card.View());
                }
                //set alignment to center
                cardStack.setAlignment(Pos.CENTER);

                //move to center of screen
                layout.setCenter(cardStack);

            }
        });



        //intialize player instances
        player = new Player(gameField,currentTrick,this);
        player.setPlayerNumber(1);
        startPlayer = player;

        //intialize ai
        computerPlayers = new ArrayList<AIPlayer>();
        for(int i = 0; i < playerCount - 1; i++) {
            AIPlayer computer = new AIPlayer(gameField,currentTrick,this);
            computer.setPlayerNumber(i + 2);
            computerPlayers.add(computer);
        }

        //setup relationships (play order)
        player.setNextPlayer(computerPlayers.get(0));
        for(int i = 0; i < computerPlayers.size() - 1; i++){
            computerPlayers.get(i).setNextPlayer(computerPlayers.get(i+1));
        }
        computerPlayers.get(computerPlayers.size() - 1).setNextPlayer(player);






        pitchDealer = createDealer();



    }

    public void resetGameField() {
        if( cardStack != null) {
            gameField.clearDeck();
            cardStack.getChildren().clear();
            placeholder.getChildren().clear();
        }
    }



    public void startTrick(int num) {
        resetPlayers(false);
        currentTrick.clearDeck();


        currentPlayer = startPlayer;
        trickInProgress = true;

        Pitch game = this;


        //trick handling loop
        //starts the designated start players turn
        //and watches for all turns to be completed,
        //once all turns are completed the appropriate logic will be executed
        //to handle the user and computer choices
        trickThread = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(!currentPlayer.getTurnStarted()) {
                    //timeDelay(100);
                    currentPlayer.startTurn(currentPlayer == startPlayer);
                }

                boolean allCompleted = true;
                for(int i = 0; i < computerPlayers.size(); i++) {
                    if(computerPlayers.get(i).getCompleted() != true) allCompleted = false;
                }
                allCompleted = allCompleted && player.getCompleted();




                if(trickInProgress && allCompleted && currentPlayer.getTurnStarted()) {

                    trickInProgress = false;


                    //calculate score
                    startPlayer = scoreCalculator.calculateTrickWinner(game);

                    for(int i = 0 ; i < game.getCurrentTrick().getCards().size(); i++) {
                        startPlayer.addWonCard(currentTrick.getCards().get(i));
                    }

                    ArrayList<Card> views = currentTrick.getCards();
                    //System.out.println(trickWinningIndex);


                    setPrompt(startPlayer.playerNumber);
                    trickList.addToList(views,trickWinningIndex, startPlayer.playerNumber);







                    this.stop();
                }

            }
        };
        trickThread.start();
    }




    void startBetting() {
        startPlayer.makeBid();
    }




    public void resetPlayers(boolean cleardeck) {
        Player iterator = startPlayer;
        for(int i = 0; i < playerCount; i++) {
            iterator.reset();
            if(cleardeck) iterator.getHand().clearDeck();
            if(cleardeck) iterator.getTricks().clearDeck();
            iterator = iterator.getNextPlayer();
        }
    }

    public void resetPlayerBids() {
        Player iterator = getStartPlayer();
        for(int i = 0; i < playerCount; i++) {
            iterator.resetBids();
            iterator = iterator.getNextPlayer();
        }
    }

    public void dealPlayers() {
        if(pitchDealer.getDeck().getCards().size() < (playerCount * 6)) {
            System.out.println("resetting " + Integer.toString(pitchDealer.getDeck().getCards().size()) + " Round: " + Integer.toString(roundCount));

            pitchDealer.resetDeck();

        }


        resetPlayers(true);





        //give all players their cards
        Player iterator = startPlayer;
        while(iterator.getHand().getCards().size() == 0) {

            ArrayList<Card> dealtHand = pitchDealer.dealHand();
            for(int i = 0; i < dealtHand.size(); i++) {

                iterator.giveCard(dealtHand.get(i));
            }
            iterator = iterator.getNextPlayer();
        }
    }

    void startRound() {
        startPlayer = player;
        resetPlayerBids();
        scoreboard.setBids(currentBids);

        setPrompt(0);
        scoreboard.setScores(this.currentScores);
        scoreboard.setTrumpSuit('E');
        scoreboard.setTurnPrompt(0);
        dealPlayers();
        bettingInProgress = true;
        startBetting();

        currentTrickNumber = 0;


        roundInProgress = true;


        Pitch game = this;


        //start round state handling loop
        //watches for changes in round state,
        //if a trick is finished, it will start a new trick
        //if the player hands are not empty
        roundThread = new AnimationTimer() {

            @Override
            public void handle(long now) {
                if(bettingInProgress) {
                    boolean allPlayersBetted = true;
                    for(int i = 0; i < computerPlayers.size(); i++) {
                        if(computerPlayers.get(i).getBidded() != true) allPlayersBetted = false;
                    }
                    allPlayersBetted = allPlayersBetted && player.getBidded();

                    if(allPlayersBetted){
                        boolean allPassed = true;


                        Player iterator = startPlayer;
                        for(int i = 0; i < playerCount; i++) {
                            if(iterator.getCurrentBid() != 0) allPassed = false;

                            iterator = iterator.getNextPlayer();
                        }

                        if(allPassed) {
                            roundInProgress = false;
                            this.stop();
                        } else {
                            //set starting player, find max index of bid

                            iterator = startPlayer;

                            int maxBid = iterator.getCurrentBid();
                            for(int i = 0; i < playerCount; i++) {
                                if(iterator.getCurrentBid() > startPlayer.getCurrentBid()) startPlayer = iterator;
                                iterator = iterator.getNextPlayer();
                            }
                            bettingInProgress = false;
                        }


                    }


                    return;
                }



                if(!trickInProgress && !bettingInProgress && !roundSummaryInProgress && roundInProgress){
                    bidWindowOpen = false;

                    //check if player hands empty to signify round over, if so calculate scores
                    if(startPlayer.getHand().getCards().size() == 0) {
                        roundInProgress = false;

                        layout.setCenter(scoreCalculator.calculateRoundScore(game));
                        roundEnded = true;

                        this.stop();
                    }
                    //otherwise calculate the winner of the current trick, and set them as the start for the next trick
                    //then start the next trick
                    if(startPlayer.getHand().getCards().size() > 0) {


                        currentTrickNumber++;
                        player.getHand().setSelectable(false);
                        resetGameField();

                        startTrick(currentTrickNumber);
                    }
                }

            }
        };
        roundThread.start();


    }
    void start() {
        gameStarted = true;

        //initialize game scene and set window to game window
        intializeGameScene();
        layout.setStyle("-fx-background-image: url('/Assets/background_image.jpg');-fx-background-size: cover;");

        window.setScene(gameWindow);



        //initialize scoreboard instance
        scoreboard = new Scoreboard(playerCount,window);
        layout.setLeft(scoreboard.View());

        trickList = new TrickList();
        layout.setRight(trickList.View());


        player.display().setHgap(-30);
        //player.display().setStyle(sideBarStyle);
        layout.setBottom(player.display());

        //layout.setAlignment(scoreboard.View(),Pos.CENTER);





        //set to round 1
        roundCount++;
        incrementRoundCounter();



        //start game state handling loop,
        //watches for round state change, and will start a new round if round
        //is finished



        gameThread = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(!roundSummaryInProgress && !roundInProgress && !gameOver) {



                    roundInProgress = true;
                    if(roundCount > 0) {
                        //System.out.println("round ended " +  Integer.toString(roundCount));
                        //calculate score
                    }
                    if(roundEnded) {
                        resetGameField();
                        trickList.clear();
                        roundCount++;
                        incrementRoundCounter();
                        roundEnded = false;
                    }

                    startRound();
                }
            }
        };



        gameThread.start();

        /*
        while(true) {
            //startRound
            //calculateScores
            //check for winners
        }
        */
    }

    private void intializeGameScene() {
        layout = new BorderPane();



        VBox topBar = new VBox();
        Title = new Label("Pitch");
        Title.setStyle("-fx-font: 18px arial");
        RoundCount = new Label("2");
        Prompt = new Label("");
        topBar.setAlignment(Pos.CENTER);


        Title.setTextFill(textColor);
        RoundCount.setTextFill(textColor);
        Prompt.setTextFill(textColor);

        topBar.getChildren().addAll(Title,RoundCount,Prompt);

        //topBar.setStyle(sideBarStyle);
        layout.setTop(topBar);




        gameWindow = new Scene(layout,window.getWidth(),window.getHeight());
        gameWindow.getStylesheets().add(getClass().getResource("/Assets/styles.css").toExternalForm());





        //layout.getChildren().add(hand);




    }


    public void end() {
        scoreboard.setScores(this.currentScores);
        scoreboard.hideExitButton();

        //End game Scene
        if(gameThread != null) gameThread.stop();
        if(roundThread != null) roundThread.stop();
        if(trickThread != null) trickThread.stop();


        VBox end = new VBox(10);

        //Calculate winners
        ArrayList<Integer> winningPlayerIndexes = new ArrayList<Integer>();
        int maxScore = 0;
        for(int i = 0; i < playerCount; i++) {
            if(currentScores.get(i) > maxScore) {
                winningPlayerIndexes.clear();
                maxScore = currentScores.get(i);
            }

            if(currentScores.get(i) == maxScore) {
                winningPlayerIndexes.add(i);
            }
        }

        Label farewell = new Label("Thank you for playing Pitch!");
        farewell.setStyle("-fx-font: 24px arial");
        farewell.setTextFill(textColor);

        Label playersThatWon = new Label();
        playersThatWon.setTextFill(textColor);

        //set players that won
        if(winningPlayerIndexes.size() == 1) {
            playersThatWon.setText("Congratulations to Player " + Integer.toString(winningPlayerIndexes.get(0) + 1) + " for winning this game of Pitch!");
            switch(winningPlayerIndexes.get(0)) {
                case 0: playersThatWon.setTextFill(player1); break;
                case 1: playersThatWon.setTextFill(player2); break;
                case 2: playersThatWon.setTextFill(player3); break;
                case 3: playersThatWon.setTextFill(player4); break;
            }
        }
        else {
            String prompt = "Congratulations to Player " + Integer.toString(winningPlayerIndexes.get(0) + 1) + " & ";
            for(int i = 1; i < winningPlayerIndexes.size(); i++) {
                prompt += "Player " + Integer.toString(winningPlayerIndexes.get(i) + 1) + " ";
                if(i != winningPlayerIndexes.size() - 1) prompt += "& ";
            }
            prompt += "for winning this game of Pitch!";
        }
        playersThatWon.setStyle("-fx-font: 16px arial");

        Button exit = new Button("Exit Application");
        exit.setOnAction(e -> window.close());

        end.getChildren().addAll(farewell,playersThatWon,exit);
        end.setStyle(roundSummaryStyle);
        end.setAlignment(Pos.CENTER);
        layout.setCenter(end);


    }


    public void incrementRoundCounter() {
        RoundCount.setText("Round: " + Integer.toString(roundCount));
    }
    public void setPrompt(int playerWon) {
        if(playerWon == 0) {
            Prompt.setText("");
            return;
        }

        if(playerWon == 1) {
            Prompt.setText("You won the previous trick!");
        }
        else {
            Prompt.setText("Player " + Integer.toString(playerWon) + " won the previous trick." );
        }
    }

    public void timeDelay(long t) {
       try {
           Thread.sleep(t);
       } catch(InterruptedException e){}
    }
}
