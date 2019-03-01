package sample;

import javafx.animation.AnimationTimer;
import javafx.collections.ListChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import static sample.PitchConstants.*;
import java.util.ArrayList;

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
    protected void setPlayerNumber(int i){this.playerNumber = i;}
    protected int getPlayerNumber(){return this.playerNumber;}

    Player(){}
    Player(Deck gameField, Deck currentTrick, Pitch parent) {
        handDisplay = new FlowPane();

        //add invisible rectangle to hold place so when hand becomes empty, the whole
        //gui doesnt glitch up and down to resize for the empty space
        Rectangle placeHolder = new Rectangle(1,160);
        placeHolder.setOpacity(0.0);
        handDisplay.getChildren().add(placeHolder);

        Rectangle otherPlaceHolder = placeHolder;


        handDisplay.setAlignment(Pos.CENTER);
        hand = new Deck(handDisplay);

        nextPlayer = this;


        FlowPane placeholder = new FlowPane();
        wonTricks = new Deck(placeholder);

        this.gameField = gameField;
        this.currentTrick = currentTrick;
        score = 0;

        this.parent = parent;

        handDisplay.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                if(!turnCompleted && hand.getSelectedCard() != null && turnStarted) {
                    gameField.addCard(hand.getSelectedCard());
                    currentTrick.addCard(hand.getSelectedCard());

                    if(startingPlayer) {
                        parent.setCurrentLeadSuit(hand.getSelectedCard().getFace());

                        if(parent.getTrickNum() == 1) {
                            parent.setCurrentTrumpSuit(hand.getSelectedCard().getFace());
                            //System.out.println("Set trump suit of " + hand.getSelectedCard().getFace());
                            parent.getScoreboard().setTrumpSuit(hand.getSelectedCard().getFace());
                        }
                        //System.out.println("Set lead suit of " + hand.getSelectedCard().getFace());
                    }
                    turnCompleted = true;
                    hand.setSelectable(false);
                    startNextTurn();
                }
            }
        });

    }


    void setNextPlayer(Player p) {
        this.nextPlayer = p;
    }
    Player getNextPlayer(){return this.nextPlayer;}

    void startNextTurn() {
        if(nextPlayer.getCompleted() == true) {
            return;
        }
        parent.setCurrentPlayer(getNextPlayer());
    }

    void giveCard(Card card) {
        hand.addCard(card);
    }

    void reset() { turnCompleted = false; turnStarted = false;}
    void resetBids(){madeBid = false; currentBid = -1; parent.getCurrentBids().set(playerNumber - 1,-1);}

    boolean getTurnStarted(){return turnStarted;}
    boolean getCompleted() { return turnCompleted;}
    boolean getBidded(){return madeBid;}

    void startTurn(boolean startingPlayer) {
        //parent.timeDelay(200);
        parent.getScoreboard().setTurnPrompt(playerNumber);
        turnStarted = true;


        this.startingPlayer = startingPlayer;


        if(!startingPlayer) {
            hand.setSelectable(false);
            boolean validCardsFound = false;
            for(int i = 0; i < hand.getCards().size(); i++) {
                //set cards that are valid to be used in turn
                if(hand.getCards().get(i).getFace() == parent.getCurrentLeadSuit() || hand.getCards().get(i).getFace() == parent.getCurrentTrumpSuit()) {
                    hand.getCardViews().get(i).setCardIsSelectable(true);
                    validCardsFound = true;
                }
            }

            if(!validCardsFound) {
                hand.setSelectable(true);
            }
        } else {
            hand.setSelectable(true);
        }

        if(turnCompleted) return;
        hand.setSelectedCard(null);

    }


    protected void addWonCard(Card c){
        wonTricks.addCard(c);
    }


    protected Deck getTricks() {return this.wonTricks;}

    protected int getCurrentBid(){return this.currentBid;}

    protected void handleBid(int bid) {
        madeBid = true;
        currentBid = bid;

        parent.getCurrentBids().set(playerNumber - 1,bid);
        parent.getScoreboard().setBids(parent.getCurrentBids());

        if(!nextPlayer.getBidded()) nextPlayer.makeBid();
        if(playerNumber == 1 && currentBid != 0) {
            parent.getLayout().setCenter(parent.getCardStack());
        }
    }

    void makeBid() {

        if(!parent.getBidWindowOpen()) {
            parent.setBidWindowOpen(true);
            FlowPane bidMenu = new FlowPane(Orientation.VERTICAL);
            Label Prompt = new Label("Make your bid for the Round!");
            bidMenu.setVgap(10);
            Prompt.setTextFill(textColor);
            bidMenu.getChildren().add(Prompt);

            HBox bids = new HBox();
            Button bidTwo = new Button("2");
            Button bidThree = new Button("3");
            Button bidFour = new Button("4");
            Button bidSmudge = new Button("Smudge");

            bids.getChildren().addAll(bidTwo,bidThree,bidFour,bidSmudge);
            bids.setSpacing(5);
            bids.setAlignment(Pos.CENTER);

            bidMenu.getChildren().add(bids);

            HBox passContainer = new HBox();
            Button pass = new Button("Pass");
            passContainer.getChildren().add(pass);
            passContainer.setAlignment(Pos.CENTER);

            bidMenu.getChildren().add(passContainer);
            bidMenu.setAlignment(Pos.CENTER);
            //bidMenu.setStyle(sideBarStyle);


            parent.getLayout().setCenter(bidMenu);


            //add button handlers
            bidTwo.setOnAction(e -> handleBid(2));
            bidThree.setOnAction(e -> handleBid(3));
            bidFour.setOnAction(e-> handleBid(4));
            bidSmudge.setOnAction(e->handleBid(5));
            pass.setOnAction(e->handleBid(0));
        }



    }



    Deck getWonTricks(){return this.getWonTricks();}
    Deck getHand(){ return this.hand; }

    FlowPane display() {
        return this.handDisplay;
    }



}

