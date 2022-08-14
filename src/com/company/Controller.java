package com.company;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Controller {
    private static final int SQUARE_SIZE = 50;
    private static final String PATH = "/Users/mrgeorge3597/Desktop/Java/MineSweeper/";

    private int fieldWidth;
    private int fieldHeight;
    private int bombCount;
    private int frameWidth;
    private int frameHeight;
    private View view;
    private Graphics graphics;
    private Cell[][] field;
    private boolean isClickPos = true;
    private boolean isFieldVirgin = true;

    public void start() {
        selectLevel();
        if (bombCount == 0) {
            return;
        }
        frameWidth = fieldWidth * SQUARE_SIZE;
        frameHeight = fieldHeight * SQUARE_SIZE;
        resetGame();
        view.create(frameWidth, frameHeight);
        render();
    }

    private void resetGame() {
        initializeField();
        setBombs();
        placeNumbers();
    }

    public void setView(View view) {
        this.view = view;
    }

    private void render() {
        BufferedImage image = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_RGB);
        graphics = image.getGraphics();
        drawCells();
        view.setImage(image);
    }

    private BufferedImage loadImage(String name) {
        try {
            return ImageIO.read(new File(PATH + name + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Image not found");
    }

    private void draw(BufferedImage image, int x, int y) {
        graphics.drawImage(image, x * SQUARE_SIZE, y * SQUARE_SIZE, null);
    }

    private void initializeField() {
        field = new Cell[fieldWidth][fieldHeight];
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                field[x][y] = new Cell();
            }
        }
    }

    private void drawCells() {
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                Cell cell = field[x][y];
                if (cell.isOpened()) {
                    draw(loadImage(cell.isBomb() ? "bomb" : "opened" + cell.getValue()), x, y);
                } else {
                    draw(loadImage(cell.isMarked() ? "flag" : "closed"), x, y);
                }
            }
        }
    }

    private void setBombs() {
        for (int i = 0; i < bombCount; i++) {
            generateBomb();
        }
    }

    private int getRandomInt(int max) {
        return (int) (Math.random() * max);
    }

    private void generateBomb() {
        Point point;
        do {
            point = new Point(getRandomInt(fieldWidth), getRandomInt(fieldHeight));
        } while (field[point.x][point.y].isBomb());
        field[point.x][point.y].makeBomb();
    }

    public void handleMouseClick(int mouseX, int mouseY, boolean isLeftMouseButton) {
        if (!isClickPos) {
            return;
        }
        int x = mouseX / SQUARE_SIZE;
        int y = mouseY / SQUARE_SIZE;
        Cell cell = field[x][y];
        if (isLeftMouseButton) {
            if (cell.isMarked()) {
                return;
            }
            if (cell.isOpened()) {
                if (countFlags(x, y) == cell.getValue()) {
                    openArea(x, y);
                }
            } else {
                while (isFieldVirgin && !field[x][y].isEmpty()) {
                    resetGame();
                }
                isFieldVirgin = false;
                open(x, y);
            }
            if (isWin()) {
                loseOrWin(true);
            }
        } else {
            cell.setMarked(!cell.isMarked());
        }
        render();
        isClickPos = true;
    }

    private void placeNumbers() {
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                if (field[x][y].isBomb()) {
                    growNumber(x, y);
                }
            }
        }
    }

    private void growNumber(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                growOrNot(x + i, y + j);
            }
        }
    }

    private boolean isPosCorrect(int x, int y) {
        return x >= 0 && x < fieldWidth && y < fieldHeight && y >= 0;
    }

    private void growOrNot(int x, int y) {
        if (isPosCorrect(x, y)) {
            field[x][y].growValue();
        }
    }

    private void open(int x, int y) {
        Cell cell = field[x][y];
        cell.open();
        render();
        if (cell.isBomb()) {
            loseOrWin(false);
        }
        delay(50);
        if (cell.isEmpty()) {
            openArea(x, y);
        }
    }

    private void openArea(int startX, int startY) {
        for (int x = startX - 1; x <= startX + 1; x++) {
            for (int y = startY - 1; y <= startY + 1; y++) {
                if (isPosCorrect(x, y) && !field[x][y].isOpened() && !field[x][y].isMarked()) {
                    open(x, y);
                }
            }
        }
    }

    private void delay(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
            isClickPos = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private int countFlags(int startX, int startY) {
        int counter = 0;
        for (int x = startX - 1; x <= startX + 1; x++) {
            for (int y = startY - 1; y <= startY + 1; y++) {
                if (isPosCorrect(x, y) && field[x][y].isMarked()) {
                    counter++;
                }
            }
        }
        return counter;
    }

    private boolean chooseDifficulty(String difficulty) {
        if (difficulty.equalsIgnoreCase("Easy")) {
            fieldWidth = 9;
            fieldHeight = 9;
            bombCount = 10;
            return true;
        }
        if (difficulty.equalsIgnoreCase("Normal")) {
            fieldWidth = 12;
            fieldHeight = 12;
            bombCount = 30;
            return true;
        }
        if (difficulty.equalsIgnoreCase("Pro")) {
            fieldWidth = 15;
            fieldHeight = 15;
            bombCount = 50;
            return true;
        }
        return false;
    }

    private void selectLevel() {
        String lvlDiff;
        do {
            lvlDiff = JOptionPane.showInputDialog("Write level difficulty: \"Easy\", \"Normal\" or \"Pro\"");
            if (lvlDiff == null) {
                break;
            }
        } while (!chooseDifficulty(lvlDiff));
    }

    private void loseOrWin(boolean isWin) {
        openBombOrMarkFlag(isWin ? cell -> cell.setMarked(true) : Cell::open);
        delay(500);
        view.close();
        Icon loseIcon = new ImageIcon(loadImage(isWin ? "youWin" : "gameOver"));
        JOptionPane.showMessageDialog(null, "", "", JOptionPane.INFORMATION_MESSAGE, loseIcon);
        bombCount = 0;
        isFieldVirgin = true;
        start();
    }

    private void openBombOrMarkFlag(Consumer<Cell> consumer) {
        int delay = 5000 / bombCount;
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                if (field[x][y].isBomb()) {
                    consumer.accept(field[x][y]);
                    delay(delay);
                    render();
                }
            }
        }
    }

    private boolean isWin() {
        for (int x = 0; x < fieldWidth; x++) {
            for (int y = 0; y < fieldHeight; y++) {
                if (!field[x][y].isBomb() && !field[x][y].isOpened()) {
                    return false;
                }
            }
        }
        return true;
    }
}
