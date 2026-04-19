// Archivo: src/com/docenlinea/snake/SnakePanel.java
package com.docenlinea.snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;

public class SnakePanel extends JPanel implements ActionListener {

    // Tamaño del tablero
    private static final int TILE_SIZE = 16;
    private static final int TILE_COUNT_X = 30;
    private static final int TILE_COUNT_Y = 25;
    private static final int PANEL_WIDTH = TILE_SIZE * TILE_COUNT_X;
    private static final int PANEL_HEIGHT = TILE_SIZE * TILE_COUNT_Y;

    // Velocidad del juego (ms)
    private static final int DELAY = 110;

    // Segmento de la culebra
    private static class Segment {
        int x;
        int y;
        Segment(int x, int y) { this.x = x; this.y = y; }
    }

    private final List<Segment> snake = new ArrayList<>();
    private int foodX;
    private int foodY;

    private enum Direction { LEFT, RIGHT, UP, DOWN }
    private Direction direction = Direction.RIGHT;
    private boolean running = true;

    private final Timer timer;
    private final Random random = new Random();

    public SnakePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(new Color(18, 26, 38));
        setFocusable(true);
        addKeyListener(new SnakeKeyAdapter());

        initGame();
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void initGame() {
        snake.clear();
        // Culebra inicial de 3 segmentos
        snake.add(new Segment(5, 5));
        snake.add(new Segment(4, 5));
        snake.add(new Segment(3, 5));
        spawnFood();
        direction = Direction.RIGHT;
        running = true;
    }

    private void spawnFood() {
        foodX = random.nextInt(TILE_COUNT_X);
        foodY = random.nextInt(TILE_COUNT_Y);

        // Evitar que aparezca encima del cuerpo
        for (Segment s : snake) {
            if (s.x == foodX && s.y == foodY) {
                spawnFood();
                return;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        var g2 = (Graphics) g;
        ((Graphics) g2).setColor(getBackground());

        // Activar un render un poco más suave
        if (g2 instanceof java.awt.Graphics2D g2d) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        drawGrid(g);
        drawFood(g);
        drawSnake(g);

        if (!running) {
            drawGameOver(g);
        }

        Toolkit.getDefaultToolkit().sync();
    }

    private void drawGrid(Graphics g) {
        g.setColor(new Color(31, 41, 55));
        for (int x = 0; x <= PANEL_WIDTH; x += TILE_SIZE) {
            g.drawLine(x, 0, x, PANEL_HEIGHT);
        }
        for (int y = 0; y <= PANEL_HEIGHT; y += TILE_SIZE) {
            g.drawLine(0, y, PANEL_WIDTH, y);
        }
    }

    private void drawSnake(Graphics g) {
        for (int i = 0; i < snake.size(); i++) {
            Segment s = snake.get(i);
            if (i == 0) {
                g.setColor(new Color(56, 189, 248)); // cabeza
            } else {
                g.setColor(new Color(96, 165, 250)); // cuerpo
            }
            g.fillRoundRect(s.x * TILE_SIZE + 1, s.y * TILE_SIZE + 1,
                    TILE_SIZE - 2, TILE_SIZE - 2, 6, 6);
        }
    }

    private void drawFood(Graphics g) {
        g.setColor(new Color(248, 113, 113));
        int px = foodX * TILE_SIZE + 2;
        int py = foodY * TILE_SIZE + 2;
        g.fillOval(px, py, TILE_SIZE - 4, TILE_SIZE - 4);
    }

    private void drawGameOver(Graphics g) {
        String msg = "Juego terminado";
        String msg2 = "Presiona ENTER para reiniciar";

        g.setColor(new Color(15, 23, 42, 220));
        g.fillRoundRect(PANEL_WIDTH / 2 - 160, PANEL_HEIGHT / 2 - 70, 320, 120, 18, 18);

        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();

        int x = (PANEL_WIDTH - fm.stringWidth(msg)) / 2;
        int y = PANEL_HEIGHT / 2 - 15;
        g.drawString(msg, x, y);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        fm = g.getFontMetrics();
        x = (PANEL_WIDTH - fm.stringWidth(msg2)) / 2;
        y = PANEL_HEIGHT / 2 + 20;
        g.drawString(msg2, x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFoodCollision();
            checkCollisions();
        }
        repaint();
    }

    private void move() {
        // Crear nueva cabeza a partir de la actual
        Segment head = snake.get(0);
        int newX = head.x;
        int newY = head.y;

        switch (direction) {
            case LEFT -> newX--;
            case RIGHT -> newX++;
            case UP -> newY--;
            case DOWN -> newY++;
        }

        // Insertar nueva cabeza al inicio de la lista
        snake.add(0, new Segment(newX, newY));
        // Eliminar la última cola, excepto si comimos (se controla en checkFoodCollision)
        snake.remove(snake.size() - 1);
    }

    private void checkFoodCollision() {
        Segment head = snake.get(0);
        if (head.x == foodX && head.y == foodY) {
            // crecer: duplicar el último segmento
            Segment tail = snake.get(snake.size() - 1);
            snake.add(new Segment(tail.x, tail.y));
            spawnFood();
        }
    }

    private void checkCollisions() {
        Segment head = snake.get(0);

        // Colisión con bordes
        if (head.x < 0 || head.x >= TILE_COUNT_X || head.y < 0 || head.y >= TILE_COUNT_Y) {
            running = false;
            return;
        }

        // Colisión con el propio cuerpo
        for (int i = 1; i < snake.size(); i++) {
            Segment s = snake.get(i);
            if (s.x == head.x && s.y == head.y) {
                running = false;
                return;
            }
        }
    }

    private class SnakeKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (!running && key == KeyEvent.VK_ENTER) {
                initGame();
                return;
            }

            if (key == KeyEvent.VK_LEFT && direction != Direction.RIGHT) {
                direction = Direction.LEFT;
            } else if (key == KeyEvent.VK_RIGHT && direction != Direction.LEFT) {
                direction = Direction.RIGHT;
            } else if (key == KeyEvent.VK_UP && direction != Direction.DOWN) {
                direction = Direction.UP;
            } else if (key == KeyEvent.VK_DOWN && direction != Direction.UP) {
                direction = Direction.DOWN;
            }
        }
    }
}
