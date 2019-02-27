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

public class Pitch {
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


    //display elements
    private FlowPane cardStackPlaceholder;
    private StackPane cardStack;
    private Scoreboard scoreboard;
    private TrickList trickList;
    private Label Title;
    private Label RoundCount;
    private Label Prompt;

    //score calculator
    PitchScoreCalculator scoreCalculator;

    //gamestate managers
    private boolean roundInProgress = false;
    private boolean trickInProgress = false;
    private boolean bettingInProgress = false;
    private int roundCount = 0;
    private Player startPlayer = null;
    private boolean gameOver = false;
    private char currentTrumpSuit;
    private char currentLeadSuit;
    private int currentTrickNumber = 0;
    private int trickWinningIndex = 0;

    void setCurrentTrumpSuit(char s){this.currentTrumpSuit = s;}
    void setCurrentLeadSuit(char s){this.currentLeadSuit = s;}
    void setTrickWinningIndex(int i){this.trickWinningIndex = i;}
    int getTrickNum(){return this.currentTrickNumber;}
    char getCurrentTrumpSuit(){return this.currentTrumpSuit;}
    char getCurrentLeadSuit(){return this.currentLeadSuit;}
    Deck getCurrentTrick(){return this.currentTrick;}
    Player getStartPlayer(){return this.startPlayer;}
    Player getPlayer(){return this.player;}


    BorderPane getLayout(){return this.layout;}
    StackPane getCardStack(){return this.cardStack;}
    Scoreboard getScoreboard(){return this.scoreboard;}
    Pitch(Stage window, int playerCount, int windowWidth, int windowHeight) {
        this.window = window;
        this.playerCount = playerCount;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
    }

    void resetGameField() {



        if( cardStack != null) {


            if(roundInProgress == false) {
                timeDelay(1000);
            }


            gameField.clearDeck();
            cardStack.getChildren().clear();
            cardStackPlaceholder.getChildren().clear();
        }
    }



    void startTrick(int num) {
        resetGameField();

        currentTrick.clearDeck();

        startPlayer.startTurn(true);

        trickInProgress = true;

        Pitch game = this;
        AnimationTimer trickThread = new AnimationTimer() {
            @Override
            public void handle(long now) {

                boolean allCompleted = true;
                for(int i = 0; i < computerPlayers.size(); i++) {
                    if(computerPlayers.get(i).getCompleted() != true) allCompleted = false;
                }
                allCompleted = allCompleted && player.getCompleted();


                if(trickInProgress && allCompleted) {
                    trickInProgress = false;

                    player.reset();
                    computerPlayers.forEach(player-> player.reset());


                    //calculate score
                    startPlayer = scoreCalculator.calculateTrickWinner(game);
                    ArrayList<Card> wonCards = currentTrick.getCards();
                    for(int i = 0; i < wonCards.size(); i++) {
                        startPlayer.addWonCard(wonCards.get(i));
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




    void resetPlayers() {
        Player iterator = startPlayer;
        for(int i = 0; i < playerCount; i++) {
            iterator.reset();
            iterator.getHand().clearDeck();
            iterator = iterator.getNextPlayer();
        }
    }

    void dealPlayers() {
        resetPlayers();
        //initialize dealer for round
        PitchDealer Dealer = new PitchDealer();

        //give all players their cards
        Player iterator = startPlayer;
        while(iterator.getHand().getCards().size() == 0) {

            ArrayList<Card> dealtHand = Dealer.dealHand();
            for(int i = 0; i < dealtHand.size(); i++) {
                iterator.giveCard(dealtHand.get(i));
            }
            iterator = iterator.getNextPlayer();
        }
    }

    void startRound() {
        scoreboard.setTrumpSuit('E');
        incrementRoundCounter();
        dealPlayers();

        bettingInProgress = true;
        startBetting();

        currentTrickNumber = 0;


        roundInProgress = true;
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
                            if(iterator.getCurrentBid() != -1) allPassed = false;

                            iterator = iterator.getNextPlayer();
                        }

                        if(allPassed) {
                            dealPlayers();
                            startBetting();
                        } else {
                            bettingInProgress = false;
                        }


                    }


                    return;
                }



                if(!trickInProgress){


                    //check if player hands empty to signify round over, if so calculate scores
                    if(startPlayer.getHand().getCards().size() == 0) {
                        roundInProgress = false;


                        this.stop();
                    }
                    //otherwise calculate the winner of the current trick, and set them as the start for the next trick
                    //then start the next trick
                    if(startPlayer.getHand().getCards().size() > 0) {

                        //trick = new Timeline(new KeyFrame(Duration.ZERO, ae-> startTrick()), new KeyFrame(Duration.millis(1000),ae->resetGameField()));
                        currentTrickNumber++;
                        startTrick(currentTrickNumber);
                        //trick.play();
                    }
                }

            }
        };
        roundThread.start();


    }
    void start() {



        scoreCalculator = new PitchScoreCalculator();
         cardStackPlaceholder = new FlowPane();
        gameField = new Deck(cardStackPlaceholder);
        currentTrick = new Deck(cardStackPlaceholder);




        cardStackPlaceholder.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            //listener that updates gameField stack in the middle of the screem
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
                //update deck
                cardStack = new StackPane();
                for(int i = 0; i < gameField.getCardViews().size(); i++) {

                    //card selection
                    CardView card = gameField.getCardViews().get(i);


                    //creates rotatation offset effect of stack in the middle
                    if(i == gameField.cardViews.size() - 1) {
                        Random r = new Random();
                        double randomValue = ((i % 8) * 35) + (20 + ((20) * r.nextDouble()));
                        card.rotate(randomValue);
                    }


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





        intializeGameScene();
        window.setScene(gameWindow);



        //initialize scoreboard instance
        scoreboard = new Scoreboard(playerCount);
        layout.setLeft(scoreboard.View());

        trickList = new TrickList();
        layout.setRight(trickList.View());


        player.display().setHgap(5);
        player.display().setStyle(sideBarStyle);
        layout.setBottom(player.display());

        //layout.setAlignment(scoreboard.View(),Pos.CENTER);







        AnimationTimer gameThread = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if(!roundInProgress && !gameOver) {



                    roundInProgress = true;
                    if(roundCount > 0) {
                        System.out.println("round ended " +  Integer.toString(roundCount));
                        //calculate score
                    }
                    resetGameField();
                    trickList.clear();
                    roundCount++;
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

        topBar.setStyle(sideBarStyle);
        layout.setTop(topBar);



        gameWindow = new Scene(layout,windowWidth,windowHeight);






        //layout.getChildren().add(hand);




    }



    void incrementRoundCounter() {
        RoundCount.setText("Round: " + Integer.toString(roundCount));
    }
    void setPrompt(int playerWon) {
        if(playerWon == -1) {
            Prompt.setText("");
            return;
        }

        if(playerWon == 1) {
            Prompt.setText("You won the trick!");
        }
        else {
            Prompt.setText("Player " + Integer.toString(playerWon) + " won the trick." );
        }
    }

    private void timeDelay(long t) {
       try {
           Thread.sleep(t);
       } catch(InterruptedException e){}
    }
}
