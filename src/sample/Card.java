package sample;

public class Card {
    private char face;
    private int rank;
    Card(int r, char f) {
        this.face = f;
        this.rank = r;
    }

    char getFace() {return this.face;}
    void setFace(char c) {this.face = c;}

    int getRank() {return this.rank;}
    void setRank(int r){this.rank = r;}
}
