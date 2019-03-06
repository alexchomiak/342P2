package game;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static game.PitchConstants.*;

public class Card {
    private char face;
    private int rank;
    private String imgSrc;
    private int width = 0;
    private int height = 0 ;
    private int hoverOffset = 0 ;
    private double scaleFactor = 1.0;
    private boolean mouseOver = false;
    private Image cardImage;
    private Rectangle cardRect;
    private Deck parent;
    boolean cardIsSelectable = false;

    public Card(Deck parent, int rank, char face, boolean selectable, boolean loadImageSource) {
        //intialize rank and face memebrs
        this.rank = rank;
        this.face = face;

        //set object data members
        this.cardIsSelectable = selectable;

        //if loading image source, set scale of image to 1.0
        //which will cause a rerender and load the image source
        if(loadImageSource) {
            setScale(1.0);
        }

        //set parent deck
        this.parent = parent;

    }

    public Card(Deck parent, int rank, char face, boolean selectable) {
        //alternate constructor
       this(parent, rank, face, selectable,true);
    }


    //handle click on card
    private void handleClick() {
        //if card is not currently selectable, ignore click event
        if (!cardIsSelectable) return;

        //if mouse is not currently over defined area, ignore click event
        if (!mouseOver) return;

        //update selected card
        parent.setSelectedCard(this);

        //remove card from deck
        parent.removeCard(this);
    }

    private boolean overCard;

    private void handleHover(MouseEvent e) {
        //if card is not currently selectable, ignore hover event
        if (!cardIsSelectable) return;

        //if hover y value is greater than the hoverAnimationOffset portion of the card,
        //reset styles and return
        if (overCard && e.getY() > this.y + (height - (hoverOffset + 10))) {
            //set stroke width to 0
            cardRect.setStrokeWidth(0);

            //set translation back to regular y
            cardRect.setTranslateY(this.y);

            //set mouseOver to false
            this.mouseOver = false;
            return;
        }


        if (overCard) {
            //if overcard, set stroke color to orangered
            cardRect.setStroke(player1);

            //set stroke width to 2px
            cardRect.setStrokeWidth(2);

            //set scale to be slightly bigger
            cardRect.setScaleX(1.1);
            cardRect.setScaleY(1.1);

            //set translation value
            cardRect.setTranslateY(this.y - hoverOffset);

            //set mouseover to true
            this.mouseOver = true;

        } else {
            //set stroke width to 0px
            cardRect.setStrokeWidth(0);

            //set scale back to normal
            cardRect.setScaleX(1.0);
            cardRect.setScaleY(1.0);

            //set translation back to normal
            cardRect.setTranslateY(this.y);

            //set mouseover to false
            this.mouseOver = false;

        }
    }


    //returns imageView object created
    public Rectangle View() {
        return this.cardRect;
    }


    public void changeCard(int rank, char face) {
        //this function changes card rank and face
        //then reloads the image and rerenders the
        //rectangle object

        //update rank and face
        this.rank = rank;
        this.face = face;

        //if rank is greater than 1 and less than 11, there is a card corresponding to that rank
        if (rank > 1 && rank < 11) {
            //set image source accordingly
            imgSrc = "/Assets/PlayingCards/" + Integer.toString(rank) + face + ".png";
        } else {
            //otherwise, it is an Ace, Jack, Queen or Heart
            //and set image source accordingly
            switch (rank) {
                case 1:
                    imgSrc = "/Assets/PlayingCards/" + "A" + face + ".png";
                    break;
                case 11:
                    imgSrc = "/Assets/PlayingCards/" + "J" + face + ".png";
                    break;
                case 12:
                    imgSrc = "/Assets/PlayingCards/" + "Q" + face + ".png";
                    break;
                case 13:
                    imgSrc = "/Assets/PlayingCards/" + "K" + face + ".png";
                    break;
                case 50:
                    imgSrc = "/Assets/PlayingCards/red_back.png";
                    break;
            }
        }

        if(imgSrc == null){
            System.out.println(Integer.toString(rank) + face + " not found.");
            return;
        }

            //intialize cardimage
            cardImage = new Image(imgSrc);

            //initialize card as rectangle
            cardRect = new Rectangle(width, height);

            //set rectangle background to card image
            cardRect.setFill(new ImagePattern(cardImage));

            //add rectangle listeners
            cardRect.setOnMouseClicked(e -> handleClick());
            cardRect.setOnMouseEntered(e -> this.overCard = true);
            cardRect.setOnMouseExited(e -> {
                this.overCard = false;
                //makes sure on exit, handle is called and resets card position
                this.handleHover(e);
            });
            cardRect.setOnMouseMoved(e -> handleHover(e));

            //set card stroketype
            cardRect.setStrokeType(StrokeType.INSIDE);

    }

    private int x;
    private int y;

    public void rotate(double deg) {
        //rotate value
        this.cardRect.setRotate(deg);
    }

    public void setCardIsSelectable(boolean s) {
        //if card is selectable, add full opacity
        if(s) {
            cardRect.setOpacity(1.0);
        } else {
            //otherwise have half opacity
            cardRect.setOpacity(.5);
        }

        //set selectable boolean
        this.cardIsSelectable = s;
    }

    //getters and setters

    public void highlight(Color color){
        cardRect.setStrokeWidth(2);
        cardRect.setStroke(color);
    }

    public char getFace() {
        return this.face;
    }

    public void setFace(char c) {
        //rerender card with new face
        changeCard(this.rank,c);
    }

    public int getRank() {
        return this.rank;
    }

    public void setRank(int r){
        //rerender card with new rank
        changeCard(r,this.face);
    }

    public void setScale(double scale) {
        //set scaleFactore
        scaleFactor = scale;

        //update hoverOffset to appropriate value
        hoverOffset = (int)Math.floor((double)hoverAnimationOffset * scale);

        //update width and height to appropriate values
        width = (int)Math.floor((double)cardImageWidth * scale);
        height = (int)Math.floor((double)cardImageHeight * scale);

        //rerender card
        changeCard(rank,face);
    }

    public double getScale(){
        return this.scaleFactor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
        this.cardRect.setTranslateX(x);
    }

    public void setY(int y) {
        this.y = y;
        this.cardRect.setTranslateY(y);
    }

}
