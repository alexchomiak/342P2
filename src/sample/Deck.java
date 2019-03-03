package sample;

import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

public class Deck {


    ArrayList<Card> cards;

    private boolean selectable = false;
    private boolean renderable = false;

    double scaleFactor = 1.0;


    private Card selectedCard;

    private Deck destinationGroup;
    FlowPane displayPane;

    public Deck(FlowPane displayPane) {
        this.displayPane = displayPane;
        this.selectable = false;
        this.renderable = displayPane != null;
        this.destinationGroup = destinationGroup;
        cards = new ArrayList<Card>();
        selectedCard = null;
    }


    public void addCard(Card card) {
        Card newCard = new Card(this,card.getRank(),card.getFace(),selectable);
        cards.add(newCard);
        if(renderable) {
            newCard.setScale(scaleFactor);
            if(displayPane != null) displayPane.getChildren().add(newCard.View());
            reRender();
        }


    }

    //remove card from deck
    public boolean removeCard(Card card) {
       if(moveCardTo(destinationGroup, card)) {
           reRender();
           return true;
       }
       else {
           return false;
       }
    }


    public FlowPane getDisplayPane(){return  this.displayPane;}

    public boolean moveCardTo(Deck dest, Card card) {
        //move card to destination deck
        for(int i = 0; i < cards.size(); i++) {
            //loop through group until card is found
            Card c = cards.get(i);
            if(c.getRank() == card.getRank() && c.getFace() == card.getFace()) {
                if(dest != null) {
                    //if dest != null, move card to destination deck
                    dest.addCard(cards.get(i));
                }

                //if the card was renderable, remove it from parent display pane
                if(renderable) {
                    if(displayPane != null) displayPane.getChildren().remove(cards.get(i).View());
                }

                //remove card from current deck
                cards.remove(i);


                reRender();
                return true;
            }
        }

        return false;

    }


    public ArrayList<Card> getCards() {return this.cards;}


    //set selected card in deck
    public boolean setSelectedCard(Card selection) {
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
    public Card getSelectedCard() {return this.selectedCard;}


    //set if cardviews in deck are selectable by mouse or not
    public void setSelectable(boolean s) {
        this.selectable = s;
        for(int i = 0; i < cards.size(); i++) {
            cards.get(i).setCardIsSelectable(s);
        }
    }

    public void setScale(double s) {
        scaleFactor = s;
        cards.forEach(card -> {
            displayPane.getChildren().remove(card.View());
            card.setScale(s);
            displayPane.getChildren().add(card.View());
        });

    }

    public double getScale() {
        return this.scaleFactor;
    }

    //set if renderable for other purposes
    public void setRenderable(boolean r) {
        this.renderable = r;
    }


    public void clearDeck() {
        if(displayPane != null){
            for(int i = 0; i < cards.size(); i++) {
                displayPane.getChildren().remove(cards.get(i).View());
            }
        }
        cards.clear();
    }

    public void reRender() {
        int angularOffset = 15;
        int yOffset = 50;

        for(int i = 0; i < cards.size(); i++) {

            //cardViews.get(i).rotate(30);

            cards.get(i).setY(0);

            int size = cards.size() - 1;
            int offset;
            if(size % 2 == 1) {
                if(i <= size/2) {
                    if(i == size/2) {
                        cards.get(i).rotate(-angularOffset/2);
                        cards.get(i).setY(0);
                    }
                    else {
                        offset = (size/2) - i;
                        cards.get(i).rotate(-1*((offset * angularOffset) + angularOffset/2));
                        cards.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                }
                else {
                    if(i == size/2 + 1) {
                        cards.get(i).rotate(angularOffset/2);
                        cards.get(i).setY(0);
                    }
                    else {
                        offset = i - ((size/2) + 1);
                        cards.get(i).rotate((offset * angularOffset) + angularOffset/2);
                        cards.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));

                    }
                }
            }
            else {
                if(i == size / 2) {
                    cards.get(i).rotate(0);
                    cards.get(i).setY(angularOffset / 2);
                }
                else {
                    if( i < size/2) {
                        offset = size/2 - i;
                        cards.get(i).rotate(offset * -angularOffset);
                        cards.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                    else{
                        offset = i - size/2;
                        cards.get(i).rotate(offset * angularOffset);
                        cards.get(i).setY( (int)((double)offset/((double)3) * (double)yOffset));
                    }
                }
            }
        }
    }
}
