package com.company;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class View {
    private Controller controller;
    private JLabel label;
    private JFrame frame;

    public void create(int width, int height) {
        frame = new JFrame();
        frame.setUndecorated(true);
        frame.setLayout(null);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                new Thread(() -> controller.handleMouseClick(e.getX(), e.getY(), e.getButton() == 1)).start();
            }
        });

        label = new JLabel();
        label.setBounds(0, 0, width, height);
        frame.add(label);

        frame.setVisible(true);
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void setImage(BufferedImage image) {
        label.setIcon(new ImageIcon(image));
    }

    public void close() {
        frame.dispose();
    }
}
