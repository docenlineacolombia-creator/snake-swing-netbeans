// Archivo: src/com/docenlinea/snake/SnakeGame.java
package com.docenlinea.snake;

import javax.swing.JFrame;

public class SnakeGame extends JFrame {

    public SnakeGame() {
        initUI();
    }

    private void initUI() {
        add(new SnakePanel());
        setTitle("Culebrita básica - Java Swing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame game = new SnakeGame();
            game.setVisible(true);
        });
    }
}
