package game;

import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

import static game.PitchConstants.*;

public class Pitch implements DealerType{
    //ui elements
    private Stage window;
    private Scene gameWindow;
    private BorderPane layout;
    private Scene mainMenu;

    //player count variable
    private int playerCount = 0;

    //current player
    private Player player;

    //list of AI players
    private ArrayList<AIPlayer> computerPlayers;

    //game elements
    private Deck gameField;
    private Deck currentTrick;
    private Deck playedCards;


    //display elements
    private FlowPane placeholder;
    private StackPane cardStack;
    private Scoreboard scoreboard;
    private TrickList trickList;
    private Label Title;
    private Label RoundCount;
    private Label Prompt;

    //score calculator
    private PitchScoreCalculator scoreCalculator;

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

    //createDealer implementation
    public PitchDealer createDealer(){
        return new PitchDealer();
    }



    public Pitch(Stage window, int playerCount, Scene mainMenu) {
        //initialize datamembers
        this.mainMenu = mainMenu;
        this.window = window;
        this.playerCount = playerCount;

        //initialize game scene and set window to game window
        intializeGameScene();

        //initialize scorecalculator
        scoreCalculator = new PitchScoreCalculator();

        //placeholder flowpane for Deck objects that interact with gui
        placeholder = new FlowPane();
        gameField = new Deck(placeholder);
        currentTrick = new Deck(placeholder);
        playedCards = new Deck(null);

        //initialize current bids array
        currentBids = new ArrayList<Integer>();
        currentScores = new ArrayList<Integer>();
        for(int i = 0; i < playerCount; i++){
            currentScores.add(0);
            currentBids.add(-1);
        }


        //add event listener to update card stack display in center of screen when it is changed
        placeholder.getChildren().addListener(new ListChangeListener<Node>() {
            //listener that updates gameField stack in the middle of the screem
            @Override
            public void onChanged(ListChangeListener.Change<? extends Node> c) {
                //rerender deck every time list is changed

                //update deck
                cardStack = new StackPane();

                //if game is already started, skip rerender
                if(!gameStarted) return;

                for(int i = 0; i < gameField.getCards().size(); i++) {
                    //intialize card to be added to stack
                    Card card = new Card(null,gameField.getCards().get(i).getRank(),gameField.getCards().get(i).getFace(),false);

                    //set card scale
                    card.setScale(1.25);

                    //rotate card
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

        //create game dealer
        pitchDealer = createDealer();
    }


    public void resetGameField() {
        //reset game field
        if( cardStack != null) {
            //clear gamefield deck
            gameField.clearDeck();

            //clear cardstack pane
            cardStack.getChildren().clear();

            //clear placeholder flowpane
            placeholder.getChildren().clear();
        }
    }


    public void resetPlayers(boolean cleardeck) {
        //iterate through each player and reset them
        Player iterator = startPlayer;
        for(int i = 0; i < playerCount; i++) {
            //reset turn state booleans
            iterator.reset();

            //if set to cleardeck, clear each deck iterator has
            if(cleardeck) iterator.getHand().clearDeck();
            if(cleardeck) iterator.getTricks().clearDeck();

            //go to next player
            iterator = iterator.getNextPlayer();
        }
    }


    public void resetPlayerBids() {
        //reset player bids
        Player iterator = getStartPlayer();
        for(int i = 0; i < playerCount; i++) {
            //reset bid state and bids
            iterator.resetBids();

            //iterate to next player
            iterator = iterator.getNextPlayer();
        }
    }


    public void dealPlayers() {
        //reset player decks
        resetPlayers(true);

        //if there are not enough cards for all players reset deck
        if(pitchDealer.getDeck().getCards().size() < (playerCount * 6)) pitchDealer.resetDeck();

        //give all players their cards
        Player iterator = startPlayer;
        while(iterator.getHand().getCards().size() == 0) {
            //iterate through each player and deal them cards
            ArrayList<Card> dealtHand = pitchDealer.dealHand();

            for(int i = 0; i < dealtHand.size(); i++) {
                //give each card in dealt hand to player
                iterator.giveCard(dealtHand.get(i));
            }

            //iterate to next player
            iterator = iterator.getNextPlayer();
        }
    }


    public void startTrick(int num) {
        //reset players before trick
        resetPlayers(false);

        //clear previous trick
        currentTrick.clearDeck();

        //set current player to startplayer
        currentPlayer = startPlayer;

        //set trick in progress game state to true
        trickInProgress = true;


        //set game to this
        Pitch game = this;


        //trick handling loop
        //starts the designated start players turn
        //and watches for all turns to be completed,
        //once all turns are completed the appropriate logic will be executed
        //to handle the user and computer choices
        trickThread = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //if current player did not start turn, start their turn
                if(!currentPlayer.getTurnStarted()) {
                    //start turn
                    currentPlayer.startTurn(currentPlayer == startPlayer);
                }

                //boolean for all players completed trick
                boolean allCompleted = true;
                for(int i = 0; i < computerPlayers.size(); i++) {
                    //calculate if all AI players completed trick
                    if(computerPlayers.get(i).getCompleted() != true) allCompleted = false;
                }

                //all completed = player completed and all AI players completed
                allCompleted = allCompleted && player.getCompleted();

                //if trick in progress and all players completed, and curren
                if(trickInProgress && allCompleted) {
                    //set trick in progress to false
                    trickInProgress = false;

                    //calculate trick winner
                    startPlayer = scoreCalculator.calculateTrickWinner(game);

                    //give cards from trick to winner
                    for(int i = 0 ; i < game.getCurrentTrick().getCards().size(); i++) {
                        startPlayer.addWonCard(currentTrick.getCards().get(i));
                    }

                    //set views as current trick
                    ArrayList<Card> views = currentTrick.getCards();

                    //add trick view to trickList
                    trickList.addToList(views,trickWinningIndex, startPlayer.playerNumber);

                    //update prompt for next start player
                    setPrompt(startPlayer.playerNumber);

                    //stop trick thread
                    this.stop();
                }

            }
        };

        //start trick
        trickThread.start();
    }


    void startBetting() {
        //start betting process
        startPlayer.makeBid();
    }


    void startRound() {
        //set startPlayer to player
        startPlayer = player;

        //reset player bids
        resetPlayerBids();

        //update bids on scoreboard
        scoreboard.setBids(currentBids);

        //empty prompt
        setPrompt(0);

        //update scoreboard
        scoreboard.setScores(this.currentScores);

        //reset trump truit
        scoreboard.setTrumpSuit('E');

        //reset turn prompt
        scoreboard.setTurnPrompt(0);

        //deal cards to players
        dealPlayers();

        //set bettingInProgress gamestate
        bettingInProgress = true;

        //start betting progress
        startBetting();

        //set current trick number to 0
        currentTrickNumber = 0;

        //set round in progress to true
        roundInProgress = true;

        //set game variable
        Pitch game = this;

        //start round state handling loop
        //watches for changes in round state,
        //if a trick is finished, it will start a new trick
        //if the player hands are not empty
        roundThread = new AnimationTimer() {

            @Override
            public void handle(long now) {
                //if betting in progress, watch for betting state changes
                if(bettingInProgress) {
                    //check if all players betted
                    boolean allPlayersBetted = true;

                    for(int i = 0; i < computerPlayers.size(); i++) {
                        //loop through each ai player checking if they betted
                        if(computerPlayers.get(i).getBidded() != true) allPlayersBetted = false;
                    }

                    //allPlayers betted = player betted & all ai players betted
                    allPlayersBetted = allPlayersBetted && player.getBidded();


                    //if all players betted, handle logic
                    if(allPlayersBetted){
                        //check if all players passed
                        boolean allPassed = true;

                        //loop through each player checking if their bid was 0
                        Player iterator = startPlayer;
                        for(int i = 0; i < playerCount; i++) {
                            //if bid != 0, all passed is set to false
                            if(iterator.getCurrentBid() != 0) allPassed = false;

                            //iterate to next player
                            iterator = iterator.getNextPlayer();
                        }

                        //if all passed, restart round, which will restart the betting process
                        if(allPassed) {
                            roundInProgress = false;
                            this.stop();
                        } else {
                            //other wise find max bid and
                            //set starting player, find max index of bid

                            //iterate over each player, find max bid
                            iterator = startPlayer;
                            int maxBid = iterator.getCurrentBid();

                            for(int i = 0; i < playerCount; i++) {
                                //if iterators bid is greater than max bid found
                                if(iterator.getCurrentBid() > maxBid) {
                                    //update maxbid found
                                    maxBid = iterator.getCurrentBid();

                                    //set start player to iterator
                                    startPlayer = iterator;
                                }

                                //iterate to next player
                                iterator = iterator.getNextPlayer();
                            }

                            //set betting in progress to false
                            bettingInProgress = false;
                        }


                    }

                    //return from handling to prevent logic below from being executed
                    return;
                }



                //if betting is not in progress, start tricks
                if(!trickInProgress && !bettingInProgress && !roundSummaryInProgress && roundInProgress){
                    //set bidWindowOpen to false
                    bidWindowOpen = false;

                    //check if player hands empty to signify round over, if so calculate scores
                    if(startPlayer.getHand().getCards().size() == 0) {

                        //calculate round score and set round summary to center pane
                        layout.setCenter(scoreCalculator.calculateRoundScore(game));

                        //set round in progress to false
                        roundInProgress = false;

                        //set roundEnded to true
                        roundEnded = true;

                        //stop round timer
                        this.stop();
                    }
                    //otherwise calculate the winner of the current trick, and set them as the start for the next trick
                    //then start the next trick
                    if(startPlayer.getHand().getCards().size() > 0) {
                        //increment trick number count
                        currentTrickNumber++;

                        //set players hand to be unselectable
                        player.getHand().setSelectable(false);

                        //reset game field
                        resetGameField();

                        //start trick
                        startTrick(currentTrickNumber);
                    }
                }

            }
        };

        //start round thread
        roundThread.start();
    }


    public void resetGame() {
        //End game threads
        if(gameThread != null) gameThread.stop();
        if(roundThread != null) roundThread.stop();
        if(trickThread != null) trickThread.stop();

        //start new pitch instance with same settings
        Pitch newGame = new Pitch(this.window,this.playerCount,this.mainMenu);
        newGame.start();
    }


    void start() {

        gameStarted = true;

        //initialize scoreboard instance
        scoreboard = new Scoreboard(playerCount,this);
        layout.setLeft(scoreboard.View());

        trickList = new TrickList();
        layout.setRight(trickList.View());

        player.display().setHgap(-30);
        layout.setBottom(player.display());

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

        //start game
        gameThread.start();

        //set window scene
        window.setScene(gameWindow);
    }


    private void intializeGameScene() {
        //intialize layout
        layout = new BorderPane();

        //set layout style
        layout.setStyle("-fx-background-image: url('/Assets/background_image.jpg');-fx-background-size: cover;");


        //initialize topbar
        VBox topBar = new VBox();

        //initialize title label
        Title = new Label("Pitch (Up to a score of " + Integer.toString(scoreLimit) + ")");

        //set title label style
        Title.setStyle("-fx-font: 18px arial");

        //initialize round count label
        RoundCount = new Label("");

        //initialize prompt label
        Prompt = new Label("");

        //set topbar alignment
        topBar.setAlignment(Pos.CENTER);

        //set text fill of labels
        Title.setTextFill(textColor);
        RoundCount.setTextFill(textColor);
        Prompt.setTextFill(textColor);

        //add all objects to topbar
        topBar.getChildren().addAll(Title,RoundCount,Prompt);

        //set layout top to topbar
        layout.setTop(topBar);

        //initialize gameWindow scene with layout
        gameWindow = new Scene(layout,windowWidth ,windowHeight);

        //set window stylesheet
        gameWindow.getStylesheets().add(getClass().getResource("/Assets/styles.css").toExternalForm());
    }


    public void end() {
        //update scoreboard
        scoreboard.setScores(this.currentScores);

        //hide exit & reset button on scoreboard
        scoreboard.hideExitButton();
        scoreboard.hideResetButton();

        //End game threads
        if(gameThread != null) gameThread.stop();
        if(roundThread != null) roundThread.stop();
        if(trickThread != null) trickThread.stop();

        //intialize vbox for end
        VBox end = new VBox(10);

        //Calculate winners
        ArrayList<Integer> winningPlayerIndexes = new ArrayList<Integer>();

        int maxScore = 0;
        for(int i = 0; i < playerCount; i++) {
            //if player > maxscore, reset player indexes
            //and set max score
            if(currentScores.get(i) > maxScore) {
                winningPlayerIndexes.clear();
                maxScore = currentScores.get(i);
            }

            if(currentScores.get(i) == maxScore) {
                //if player score == max score, add player to winning
                //player indexes
                winningPlayerIndexes.add(i);
            }
        }

        //initialize farewell label
        Label farewell = new Label("Thank you for playing Pitch!");

        //set farewell style
        farewell.setStyle("-fx-font: 24px arial");

        //set text color
        farewell.setTextFill(textColor);

        //initialize playersThatWon label
        Label playersThatWon = new Label();

        //set text color
        playersThatWon.setTextFill(textColor);

        //set players that won
        if(winningPlayerIndexes.size() == 1) {
            //if one player won the game, set the label accordingly and set text color
            playersThatWon.setText("Congratulations to Player " + Integer.toString(winningPlayerIndexes.get(0) + 1) + " for winning this game of Pitch!");
            switch(winningPlayerIndexes.get(0)) {
                case 0: playersThatWon.setTextFill(player1); break;
                case 1: playersThatWon.setTextFill(player2); break;
                case 2: playersThatWon.setTextFill(player3); break;
                case 3: playersThatWon.setTextFill(player4); break;
            }
        }
        else {
            //otherwise loop through each player that won
            //and concatenate each player that won to playersthat won string
            String prompt = "Congratulations to Player " + Integer.toString(winningPlayerIndexes.get(0) + 1) + " & ";

            for(int i = 1; i < winningPlayerIndexes.size(); i++) {
                prompt += "Player " + Integer.toString(winningPlayerIndexes.get(i) + 1) + " ";
                if(i != winningPlayerIndexes.size() - 1) prompt += "& ";
            }

            prompt += "for winning this game of Pitch!";
        }

        //set playersThatWon style
        playersThatWon.setStyle("-fx-font: 16px arial");

        //initialize playAgain button
        Button playAgain = new Button("Play Again");

        //set event listener for play again
        playAgain.setOnAction(e -> window.setScene(mainMenu));

        //initialize exit button
        Button exit = new Button("Exit Application");

        //set event listener for exit button
        exit.setOnAction(e -> window.close());

        //add all elements to end vbox
        end.getChildren().addAll(farewell,playersThatWon,playAgain,exit);

        //set style of end vbox
        end.setStyle(roundSummaryStyle);

        //set alignment of vbox
        end.setAlignment(Pos.CENTER);

        //set layout center to vbox
        layout.setCenter(end);
    }


    public void incrementRoundCounter() {
        //increment roundcounter (updates round count label)
        RoundCount.setText("Round: " + Integer.toString(roundCount));
    }


    public void setPrompt(int playerWon) {
        //if playerWon is 0
        if(playerWon == 0) {
            //empty text for prompt
            Prompt.setText("");
            return;
        }

        //otherwise set prompt text of player that won the trick
        if(playerWon == 1) {
            Prompt.setText("You won the previous trick!");
        }
        else {
            Prompt.setText("Player " + Integer.toString(playerWon) + " won the previous trick." );
        }
    }


    //getters and setters

    public Stage getWindow(){return this.window;}
    public Scene getMainMenu(){return this.mainMenu;}
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
}
