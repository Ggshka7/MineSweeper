package com.company;


public class Cell {
    private boolean isOpened;
    private boolean isMarked;
    private boolean isBomb;
    private int value;

    public void makeBomb() {
        if (isBomb) {
            throw new RuntimeException("The Bomb is already placed");
        }
        isBomb = true;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void open() {
        isOpened = true;
    }

    public boolean isMarked() {
        return isMarked;
    }

    public void setMarked(boolean marked) {
        isMarked = marked;
    }

    public boolean isBomb() {
        return isBomb;
    }

    public void growValue() {
        value++;
    }

    public int getValue() {
        return value;
    }

    public boolean isEmpty() {
        return value == 0;
    }
}
