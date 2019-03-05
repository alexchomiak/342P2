package game;

import javafx.scene.paint.Color;

public final class PitchConstants {
    private PitchConstants(){
        //prevent instantiation
    }

    //game constants
    public static final int windowWidth = 1000;
    public static final int windowHeight = 800;
    public static final int cardImageWidth = 90;
    public static final int cardImageHeight = 160;
    public static final int hoverAnimationOffset = 30;
    public static final int scoreLimit = 7;
    public static final Color player1 = Color.GOLD;
    public static final Color player2 = Color.rgb(249, 109, 240);
    public static final Color player3 = Color.rgb(0, 94, 247);
    public static final Color player4 = Color.rgb(71, 255, 150);
    public static final Color textColor = Color.rgb(255,255,255);
    public static final String rightSideBarStyle = "-fx-background-image: url('/Assets/sidebar.jpg');-fx-background-size: cover; -fx-border-color: red; -fx-border-style: solid; -fx-border-width: 3px 0px 3px 3px; ";
    public static final String leftSideBarStyle = "-fx-background-image: url('/Assets/sidebar.jpg');-fx-background-size: cover; -fx-border-color: red; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 0px; ";
    public static final String titleStyle = "-fx-background-image: url('/Assets/sidebar.jpg');-fx-background-size: cover; -fx-border-color: red; -fx-border-style: solid; -fx-border-width: 3px 3px 3px 3px; ";
    public static final String roundSummaryStyle = "-fx-background-image: url('/Assets/sidebar.jpg');-fx-background-size: cover; -fx-border-color: red; -fx-border-style: solid; -fx-border-width: 3px 0px 3px 0px; ";

}
