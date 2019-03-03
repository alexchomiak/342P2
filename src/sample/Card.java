package sample;

import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static sample.PitchConstants.*;

public class Card {
    private char face;
    private int rank;


    public char getFace() {return this.face;}
    public void setFace(char c) {changeCard(this.rank,c);}

    public int getRank() {return this.rank;}
    public void setRank(int r){changeCard(r,this.face);}



    private String imgSrc;

    private int width = 0;
    private int height = 0 ;
    private int hoverOffset = 0 ;


    double scaleFactor = 1.0;

    private boolean mouseOver = false;

    private Image cardImage;
    private Rectangle cardRect;

    private Deck parent;

    boolean cardIsSelectable = false;




    public Card(Deck parent, int rank, char face, boolean selectable, boolean loadImageSource) {
        this.rank = rank;
        this.face = face;

        //set object data members
        this.cardIsSelectable = selectable;


        if(loadImageSource) {
            setScale(1.0);
        }



        this.parent = parent;

    }

    public Card(Deck parent, int rank, char face, boolean selectable) {
       this(parent, rank, face, selectable,true);
    }


    //handle click on card
    private void handleClick() {
        //if card is not currently selectable, ignore click event
        if (!cardIsSelectable) return;

        //if mouse is not currently over defined area, ignore click event
        if (!mouseOver) return;


        parent.setSelectedCard(this);
        parent.removeCard(this);
    }

    private boolean overCard;

    private void handleHover(MouseEvent e) {
        //if card is not currently selectable, ignore hover event
        if (!cardIsSelectable) return;

        //if hover y value is greater than the hoverAnimationOffset portion of the card,
        //reset styles and return
        if (overCard && e.getY() > this.y + (height - (hoverOffset + 10))) {
            cardRect.setStrokeWidth(0);
            cardRect.setTranslateY(this.y);
            this.mouseOver = false;
            return;
        }


        if (overCard) {
            cardRect.setStroke(Color.ORANGERED);
            cardRect.setStrokeWidth(2);

            if (this.x > -1 && this.y > -1) {
                cardRect.setTranslateY(this.y - hoverOffset);
            }

            this.mouseOver = true;

        } else {
            cardRect.setStrokeWidth(0);

            if (this.x > -1 && this.y > -1) {
                cardRect.setTranslateY(this.y);
            }

            this.mouseOver = false;

        }
    }


    //returns imageView object created
    public Rectangle View() {
        return this.cardRect;
    }


    public void changeCard(int rank, char face) {
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




            //add listeners
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



            //if the image is not loaded, an exception will be thrown, and the image file path will
            //be thrown to aid debugging
            //System.out.println("Image with path " + imgSrc + " was not found!");


    }



    private int x;
    private int y;

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

    public void rotate(double deg) {
        this.cardRect.setRotate(deg);
    }



    public void setCardIsSelectable(boolean s) {
        if(s) {
            cardRect.setOpacity(1.0);
        } else {
            cardRect.setOpacity(.5);
        }
        this.cardIsSelectable = s;
    }

    public void setScale(double scale) {
        scaleFactor = scale;
        hoverOffset = (int)Math.floor((double)hoverAnimationOffset * scale);
        width = (int)Math.floor((double)cardImageWidth * scale);
        height = (int)Math.floor((double)cardImageHeight * scale);
        //rerender card
        changeCard(rank,face);
    }

    public double getScale(){
        return this.scaleFactor;
    }


    public void highlight(Color color){
        cardRect.setStrokeWidth(2);
        cardRect.setStroke(color);
    }
}
