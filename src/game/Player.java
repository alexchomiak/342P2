package game;

import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import static game.PitchConstants.*;

public class Player {
    protected int currentBid;
    protected Deck hand;
    protected Deck wonTricks;

    protected boolean turnCompleted = false;
    protected boolean turnStarted = false;
    //Display elements
    protected FlowPane handDisplay;

    //parent instances of game
    protected Deck gameField;
    protected Deck currentTrick;
    protected Pitch parent;

    protected Player nextPlayer;

    protected int score;

    protected boolean startingPlayer;
    protected boolean madeBid = false;

    protected int playerNumber;

    public Player(Deck gameField, Deck currentTrick, Pitch parent) {
        //initialize flowpane for hand display
        handDisplay = new FlowPane();

        //add invisible rectangle to hold place so when hand becomes empty, the whole
        //gui doesnt glitch up and down to resize for the empty space
        Rectangle placeHolder = new Rectangle(1,160);
        placeHolder.setOpacity(0.0);

        //add placeholder
        handDisplay.getChildren().add(placeHolder);

        //set alignment of hand display to center
        handDisplay.setAlignment(Pos.CENTER);

        //initialize hand deck
        hand = new Deck(handDisplay);

        //set nextPlayer to self for now
        nextPlayer = this;

        //intialize placeholder flowpane for wonTricks deck
        FlowPane placeholder = new FlowPane();

        //initialize wonTricks deck
        wonTricks = new Deck(placeholder);

        //set gamefield and current trick datamembers
        this.gameField = gameField;
        this.currentTrick = currentTrick;

        //intialize score to 0
        score = 0;

        //set parent datamember to parent
        this.parent = parent;

        //initialize current bid to -1
        currentBid = -1;

        //add event handler to handDisplay to process turn information
        handDisplay.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                if(!turnCompleted && hand.getSelectedCard() != null && turnStarted) {
                    //if turn is started && card is clicked handle turn

                    //add selected card to gamefield
                    gameField.addCard(hand.getSelectedCard());

                    //add selected card to current trick
                    currentTrick.addCard(hand.getSelectedCard());

                    //if the player is the starting player
                    //set the lead suit to the face of the card played
                    if(startingPlayer) {
                        //set lead suit if player is start player
                        parent.setCurrentLeadSuit(hand.getSelectedCard().getFace());

                        if(parent.getTrickNum() == 1) {
                            //if it is first trick, set trump suit as played cards face
                            parent.setCurrentTrumpSuit(hand.getSelectedCard().getFace());
                            parent.getScoreboard().setTrumpSuit(hand.getSelectedCard().getFace());
                        }
                    }

                    //set turn completed to true
                    turnCompleted = true;

                    //set hand to not selectable
                    hand.setSelectable(false);

                    //start next turn
                    startNextTurn();
                }
            }
        });

    }



    protected void startNextTurn() {
        //start next players turn
        if(nextPlayer.getCompleted() == true) {
            //if next player already completed turn
            //all turns have been completed, return
            return;
        }

        //set current player to next player
        parent.setCurrentPlayer(getNextPlayer());
    }

    public void giveCard(Card card) {
        //add card to players hand
        hand.addCard(card);
    }

    public void reset() {
        //reset function resets players turn state booleans to false
        turnCompleted = false;
        turnStarted = false;
    }
    public void resetBids(){
        //resetBids function resets bid state booleans and sets current bid to -1
        //set madeBid to false
        madeBid = false;

        //set current bid to -1
        currentBid = -1;

        //update parent current bids
        if(parent != null )parent.getCurrentBids().set(playerNumber - 1,-1);
    }


    public void startTurn(boolean startingPlayer) {
        //if turn is already completed return
        if(turnCompleted) return;

        //if scoreboard exists, set turn prompt to player number
        if(parent.getScoreboard() != null) parent.getScoreboard().setTurnPrompt(playerNumber);
        turnStarted = true;

        //set startingPlayer data member to starting player
        this.startingPlayer = startingPlayer;

        //if player is not the starting player
        if(!startingPlayer) {
            //initialize hand as not selectable
            hand.setSelectable(false);

            //look for validCards in the trick
            boolean validCardsFound = false;

            //look at all cards in the hand
            for(int i = 0; i < hand.getCards().size(); i++) {
                //set cards that are valid to be used in turn
                if(hand.getCards().get(i).getFace() == parent.getCurrentLeadSuit() || hand.getCards().get(i).getFace() == parent.getCurrentTrumpSuit()) {
                    //if card face is equal to trump or lead suit set them to selectable
                    hand.getCards().get(i).setCardIsSelectable(true);

                    //set validCardsFound to true
                    validCardsFound = true;
                }
            }

            if(!validCardsFound) {
                //if there are no valid cards in the deck, set whole hand to selectable
                hand.setSelectable(true);
            }
        } else {
            //if player is startplayer, set whole hand to selectable
            hand.setSelectable(true);
        }

        //set selectedCard to null
        hand.setSelectedCard(null);

    }


    public void addWonCard(Card c){
        //add card to wontricks deck
        wonTricks.addCard(c);
    }

    public void handleBid(int bid) {
        //handle bid based off parameter value
        //set madeBid to true
        this.madeBid = true;

        //set currentBid data member to bid
        this.currentBid = bid;

        //if game is not started return (this is for testing purposes where
        // we only want bid state and value)
        if(parent.getGameStarted() != true) return;

        //update current bids arrayList in parent instance
        parent.getCurrentBids().set(playerNumber - 1,bid);

        //update scoreboard bids
        if(parent.getScoreboard() != null) parent.getScoreboard().setBids(parent.getCurrentBids());

        //start bidding for next player
        if(!nextPlayer.getBidded()) nextPlayer.makeBid();

        //if playerNumber is 1, once bid has been made set center pane to card stack
        if(playerNumber == 1 && currentBid != 0) {
            parent.getLayout().setCenter(parent.getCardStack());
        }
    }

    public void makeBid() {
        //open bid window if not opened
        if(!parent.getBidWindowOpen()) {
            //update game state for bid window in parent
            parent.setBidWindowOpen(true);

            //initialize bid menu flowpane
            FlowPane bidMenu = new FlowPane(Orientation.VERTICAL);

            //set bidmenu spacing
            bidMenu.setVgap(10);

            //intialize prompt label
            Label Prompt = new Label("Make your bid for the Round!");

            //set promot styling
            Prompt.setStyle("-fx-font: 18px arial");

            //set prompt fill color
            Prompt.setTextFill(textColor);

            //add prompt to bid menu
            bidMenu.getChildren().add(Prompt);

            //initaize hbox of bid options
            HBox bids = new HBox();

            //initalize bid buttons
            Button bidTwo = new Button("2");
            Button bidThree = new Button("3");
            Button bidFour = new Button("4");
            Button bidSmudge = new Button("Smudge");

            //add bid buttons
            bids.getChildren().addAll(bidTwo,bidThree,bidFour,bidSmudge);

            //set bid spacing
            bids.setSpacing(5);

            //set alignment of bids
            bids.setAlignment(Pos.CENTER);

            //add bids to bidmenu
            bidMenu.getChildren().add(bids);

            //initialize pass option container
            HBox passContainer = new HBox();

            //initialize pass button
            Button pass = new Button("Pass");

            //add pass button to pass container
            passContainer.getChildren().add(pass);

            //set alignment of pass container
            passContainer.setAlignment(Pos.CENTER);

            //add pass container to bid menu
            bidMenu.getChildren().add(passContainer);

            //set alignment of bidmenu
            bidMenu.setAlignment(Pos.CENTER);

            //set center of parent layout to bidmenu
            parent.getLayout().setCenter(bidMenu);


            //add button handlers for each bid
            bidTwo.setOnAction(e -> handleBid(2));
            bidThree.setOnAction(e -> handleBid(3));
            bidFour.setOnAction(e-> handleBid(4));
            bidSmudge.setOnAction(e->handleBid(5));
            pass.setOnAction(e->handleBid(0));
        }
    }

    public FlowPane display() {
        //return display pane of player hand
        return this.handDisplay;
    }

    //getters and setters
    public Deck getHand(){ return this.hand; }
    public Deck getTricks() {return this.wonTricks;}
    public int getCurrentBid(){return this.currentBid;}
    public boolean getTurnStarted(){return turnStarted;}
    public boolean getCompleted() { return turnCompleted;}
    public boolean getBidded(){return madeBid;}
    public void setPlayerNumber(int i){this.playerNumber = i;}
    public int getPlayerNumber(){return this.playerNumber;}
    public void setNextPlayer(Player p) {
        this.nextPlayer = p;
    }
    public Player getNextPlayer(){return this.nextPlayer;}
    public void setCurrentBid(int currentBid) {
        this.currentBid = currentBid;
    }
    public boolean isTurnStarted() {
        return turnStarted;
    }
    public void setTurnStarted(boolean turnStarted) {
        this.turnStarted = turnStarted;
    }
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public boolean isStartingPlayer() {
        return startingPlayer;
    }
    public void setStartingPlayer(boolean startingPlayer) {
        this.startingPlayer = startingPlayer;
    }
}

