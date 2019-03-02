package sample;

import javafx.scene.layout.FlowPane;

import java.util.ArrayList;

public class Deck {


    ArrayList<Card> cards;
    ArrayList<CardView> cardViews;

    private boolean selectable = false;
    private boolean renderable = false;

    double scaleFactor = 1.0;


    private Card selectedCard;

    private Deck destinationGroup;
    FlowPane displayPane;

    Deck(FlowPane displayPane) {
        this.displayPane = displayPane;
        this.selectable = false;
        this.renderable = displayPane != null;
        this.destinationGroup = destinationGroup;
        cards = new ArrayList<Card>();
        cardViews = new ArrayList<CardView>();
        selectedCard = null;
    }


    void addCard(Card card) {
        cards.add(new Card(card.getRank(),card.getFace()));


        if(renderable) {
            CardView cardView = new CardView(this,card.getRank(),card.getFace(),selectable);
            cardView.setScale(scaleFactor);
            cardViews.add(cardView);
            if(displayPane != null) displayPane.getChildren().add(cardView.View());
            reRender();
        }


    }

    //remove card from deck
    boolean removeCard(Card card) {
       if(moveCardTo(destinationGroup, card)) {
           reRender();
           return true;
       }
       else {
           return false;
       }
    }


    boolean moveCardTo(Deck dest, Card card) {
        //move card to destination deck
        for(int i = 0; i < cards.size(); i++) {
            //loop through group until card is found
            Card c = cards.get(i);
            if(c.getRank() == card.getRank() && c.getFace() == card.getFace()) {
                if(dest != null) {
                    //if dest != null, move card to destination deck
                    dest.addCard(cards.get(i));
                }

                //remove card from current deck
                cards.remove(i);

                //if the card was renderable, remove it from parent display pane
                if(renderable) {
                    if(displayPane != null) displayPane.getChildren().remove(cardViews.get(i).View());
                    cardViews.remove(i);
                }
                reRender();
                return true;
            }
        }

        return false;

    }


    ArrayList<Card> getCards() {return this.cards;}
    ArrayList<CardView> getCardViews(){return this.cardViews;}


    //set selected card in deck
    boolean setSelectedCard(Card selection) {
        if(selection == null) {
            this.selectedCard = null;
            return true;
        }

        for(int i = 0; i < cards.size(); i++) {
            Card c = cards.get(i);

            if(c.getRank() == selection.getRank() && c.getFace() == selection.getFace()) {
                selectedCard = selection;
            }
        }
        return false;
    }

    //get selected card in deck
    Card getSelectedCard() {return this.selectedCard;}


    //set if cardviews in deck are selectable by mouse or not
    void setSelectable(boolean s) {
        this.selectable = s;
        for(int i = 0; i < cardViews.size(); i++) {
            cardViews.get(i).setCardIsSelectable(s);
        }
    }

    void setScale(double s) {
        scaleFactor = s;
        cardViews.forEach(card -> {
            displayPane.getChildren().remove(card.View());
            card.setScale(s);
            displayPane.getChildren().add(card.View());
        });

    }

    double getScale() {
        return this.scaleFactor;
    }

    //set if renderable for other purposes
    void setRenderable(boolean r) {
        this.renderable = r;
    }


    public void clearDeck() {
        for(int i = 0; i < cardViews.size(); i++) {
            displayPane.getChildren().remove(cardViews.get(i).View());
        }
        cardViews.clear();
        cards.clear();
    }

    void reRender() {
        int angularOffset = 15;
        int yOffset = 50;

        for(int i = 0; i < cardViews.size(); i++) {

            //cardViews.get(i).rotate(30);

            cardViews.get(i).setY(0);

            int size = cardViews.size() - 1;
            int offset;
            if(size % 2 == 1) {
                if(i <= size/2) {
                    if(i == size/2) {
                        cardViews.get(i).rotate(-angularOffset/2);
                        cardViews.get(i).setY(0);
                    }
                    else {
                        offset = (size/2) - i;
                        cardViews.get(i).rotate(-1*((offset * angularOffset) + angularOffset/2));
                        cardViews.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                }
                else {
                    if(i == size/2 + 1) {
                        cardViews.get(i).rotate(angularOffset/2);
                        cardViews.get(i).setY(0);
                    }
                    else {
                        offset = i - ((size/2) + 1);
                        cardViews.get(i).rotate((offset * angularOffset) + angularOffset/2);
                        cardViews.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));

                    }
                }
            }
            else {
                if(i == size / 2) {
                    cardViews.get(i).rotate(0);
                    cardViews.get(i).setY(angularOffset / 2);
                }
                else {
                    if( i < size/2) {
                        offset = size/2 - i;
                        cardViews.get(i).rotate(offset * -angularOffset);
                        cardViews.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                    else{
                        offset = i - size/2;
                        cardViews.get(i).rotate(offset * angularOffset);
                        cardViews.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                }
            }
        }
    }
}
