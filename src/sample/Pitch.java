package sample;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

import static sample.PitchConstants.*;

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

    void setCurrentTrumpSuit(char s){this.currentTrumpSuit = s;}
    void setCurrentLeadSuit(char s){this.currentLeadSuit = s;}
    void setTrickWinningIndex(int i){this.trickWinningIndex = i;}
    int getTrickNum(){return this.currentTrickNumber;}
    char getCurrentTrumpSuit(){return this.currentTrumpSuit;}
    char getCurrentLeadSuit(){return this.currentLeadSuit;}
    Deck getCurrentTrick(){return this.currentTrick;}
    Player getStartPlayer(){return this.startPlayer;}
    Player getPlayer(){return this.player;}
    void setCurrentPlayer(Player p){this.currentPlayer = p;}
    Player getCurrentPlayer(){return this.currentPlayer;}
    void setRoundSummaryInProgress(boolean s){ this.roundSummaryInProgress = s;}
    void setBidWindowOpen(boolean s) { this.bidWindowOpen = s;}
    boolean getBidWindowOpen() {return this.bidWindowOpen;}
    BorderPane getLayout(){return this.layout;}
    StackPane getCardStack(){return this.cardStack;}
    Scoreboard getScoreboard(){return this.scoreboard;}


    ArrayList<Integer> getCurrentScores() {return this.currentScores;}
    ArrayList<Integer> getCurrentBids() {return this.currentBids;}

    int getPlayerCount() {return this.playerCount;}

    public PitchDealer createDealer(){
        return new PitchDealer();
    }


    Pitch(Stage window, int playerCount, int windowWidth, int windowHeight) {
        this.window = window;
        this.playerCount = playerCount;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    void resetGameField() {
        if( cardStack != null) {
            gameField.clearDeck();
            cardStack.getChildren().clear();
            placeholder.getChildren().clear();
        }
    }



    void startTrick(int num) {
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
        AnimationTimer trickThread = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if(!currentPlayer.getTurnStarted()) {
                    timeDelay(150);
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

                    ArrayList<CardView> views = currentTrick.getCardViews();
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




    void resetPlayers(boolean cleardeck) {
        Player iterator = startPlayer;
        for(int i = 0; i < playerCount; i++) {
            iterator.reset();
            if(cleardeck) iterator.getHand().clearDeck();
            if(cleardeck) iterator.getTricks().clearDeck();
            iterator = iterator.getNextPlayer();
        }
    }

    void resetPlayerBids() {
        Player iterator = getStartPlayer();
        for(int i = 0; i < playerCount; i++) {
            iterator.resetBids();
            iterator = iterator.getNextPlayer();
        }
    }

    void dealPlayers() {
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
        AnimationTimer roundThread = new AnimationTimer() {

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



                if(!trickInProgress && !bettingInProgress && roundInProgress){
                    bidWindowOpen = false;

                    //check if player hands empty to signify round over, if so calculate scores
                    if(startPlayer.getHand().getCards().size() == 0) {
                        roundInProgress = false;

                        layout.setCenter(scoreCalculator.calculateRoundWinner(game));
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

        pitchDealer = createDealer();
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
                for(int i = 0; i < gameField.getCardViews().size(); i++) {
                    //intialize card to be added to stack
                    CardView card = new CardView(null,gameField.getCards().get(i).getRank(),gameField.getCards().get(i).getFace(),false);

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




        //initialize game scene and set window to game window
        intializeGameScene();
        window.setScene(gameWindow);



        //initialize scoreboard instance
        scoreboard = new Scoreboard(playerCount);
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

        Random rand = new Random();
        AnimationTimer gameThread = new AnimationTimer() {
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
        layout.setStyle("-fx-background-image: url('/Assets/background_image.jpg');-fx-background-size: cover;");
    }

    void intializeGameScene() {
        Label hello = new Label("Hello");


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






        //layout.getChildren().add(hand);




    }



    void incrementRoundCounter() {
        RoundCount.setText("Round: " + Integer.toString(roundCount));
    }
    void setPrompt(int playerWon) {
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
