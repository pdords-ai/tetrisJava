package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * 테트리스 게임을 그리는 패널
 */
public class TetrisPanel extends JPanel {
    private static final int CELL_SIZE = 25;
    private static final int BOARD_WIDTH = GameBoard.BOARD_WIDTH * CELL_SIZE;
    private static final int BOARD_HEIGHT = GameBoard.BOARD_HEIGHT * CELL_SIZE;
    private static final int NEXT_PIECE_SIZE = 100;
    
    private TetrisGame game;
    private Color[] blockColors;
    
    public TetrisPanel(TetrisGame game) {
        this.game = game;
        setPreferredSize(new Dimension(BOARD_WIDTH + NEXT_PIECE_SIZE + 50, BOARD_HEIGHT + 100));
        setBackground(Color.BLACK);
        setFocusable(true);
        
        // 블록 색상 배열 초기화
        blockColors = new Color[7];
        blockColors[0] = Color.CYAN;    // I
        blockColors[1] = Color.YELLOW;  // O
        blockColors[2] = Color.MAGENTA; // T
        blockColors[3] = Color.GREEN;   // S
        blockColors[4] = Color.RED;     // Z
        blockColors[5] = Color.BLUE;    // J
        blockColors[6] = Color.ORANGE;  // L
        
        // 키보드 이벤트 리스너 설정
        addKeyListener(new TetrisKeyListener());
        
        // 게임 이벤트 리스너 설정
        game.setEventListener(new TetrisGame.GameEventListener() {
            @Override
            public void onGameOver() {
                SwingUtilities.invokeLater(() -> repaint());
            }
            
            @Override
            public void onScoreChanged(int score) {
                SwingUtilities.invokeLater(() -> repaint());
            }
            
            @Override
            public void onLevelChanged(int level) {
                SwingUtilities.invokeLater(() -> repaint());
            }
            
            @Override
            public void onLinesCleared(int lines) {
                SwingUtilities.invokeLater(() -> repaint());
            }
            
            @Override
            public void onBoardChanged() {
                SwingUtilities.invokeLater(() -> repaint());
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 게임 보드 그리기
        drawGameBoard(g2d);
        
        // 현재 블록 그리기
        drawCurrentPiece(g2d);
        
        // 다음 블록 그리기
        drawNextPiece(g2d);
        
        // 게임 정보 그리기
        drawGameInfo(g2d);
        
        // 게임 오버 메시지 그리기
        if (!game.isGameRunning()) {
            drawGameOverMessage(g2d);
        }
    }
    
    /**
     * 게임 보드 그리기
     */
    private void drawGameBoard(Graphics2D g2d) {
        // 보드 배경
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
        
        // 보드 격자
        g2d.setColor(Color.GRAY);
        for (int i = 0; i <= GameBoard.BOARD_WIDTH; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, BOARD_HEIGHT);
        }
        for (int i = 0; i <= GameBoard.BOARD_HEIGHT; i++) {
            g2d.drawLine(0, i * CELL_SIZE, BOARD_WIDTH, i * CELL_SIZE);
        }
        
        // 고정된 블록들 그리기
        GameBoard board = game.getGameBoard();
        for (int y = 0; y < GameBoard.BOARD_HEIGHT; y++) {
            for (int x = 0; x < GameBoard.BOARD_WIDTH; x++) {
                int cellValue = board.getCell(x, y);
                if (cellValue > 0) {
                    drawCell(g2d, x, y, blockColors[cellValue - 1]);
                }
            }
        }
    }
    
    /**
     * 현재 블록 그리기
     */
    private void drawCurrentPiece(Graphics2D g2d) {
        Tetromino currentPiece = game.getCurrentPiece();
        if (currentPiece != null) {
            int[][] shape = currentPiece.getShape();
            Color color = currentPiece.getColor();
            
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (shape[i][j] != 0) {
                        int x = currentPiece.getX() + j;
                        int y = currentPiece.getY() + i;
                        
                        if (y >= 0) { // 화면 위쪽에 있는 부분은 그리지 않음
                            drawCell(g2d, x, y, color);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 다음 블록 그리기
     */
    private void drawNextPiece(Graphics2D g2d) {
        Tetromino nextPiece = game.getNextPiece();
        if (nextPiece != null) {
            int startX = BOARD_WIDTH + 20;
            int startY = 50;
            
            // "NEXT" 라벨
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            g2d.drawString("NEXT", startX, startY - 10);
            
            // 다음 블록 배경
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(startX, startY, NEXT_PIECE_SIZE, NEXT_PIECE_SIZE);
            
            // 다음 블록 그리기
            int[][] shape = nextPiece.getShape();
            Color color = nextPiece.getColor();
            
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    if (shape[i][j] != 0) {
                        int x = startX + j * (CELL_SIZE / 2);
                        int y = startY + i * (CELL_SIZE / 2);
                        
                        g2d.setColor(color);
                        g2d.fillRect(x + 1, y + 1, (CELL_SIZE / 2) - 2, (CELL_SIZE / 2) - 2);
                        
                        g2d.setColor(color.brighter());
                        g2d.drawRect(x + 1, y + 1, (CELL_SIZE / 2) - 2, (CELL_SIZE / 2) - 2);
                    }
                }
            }
        }
    }
    
    /**
     * 게임 정보 그리기
     */
    private void drawGameInfo(Graphics2D g2d) {
        int startX = BOARD_WIDTH + 20;
        int startY = NEXT_PIECE_SIZE + 100;
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        
        g2d.drawString("SCORE: " + game.getScore(), startX, startY);
        g2d.drawString("LEVEL: " + game.getLevel(), startX, startY + 25);
        g2d.drawString("LINES: " + game.getLinesCleared(), startX, startY + 50);
        
        if (game.isGamePaused()) {
            g2d.setColor(Color.YELLOW);
            g2d.drawString("PAUSED", startX, startY + 75);
        }
    }
    
    /**
     * 게임 오버 메시지 그리기
     */
    private void drawGameOverMessage(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 0, 150));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        
        String message = "GAME OVER";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(message)) / 2;
        int y = getHeight() / 2;
        
        g2d.drawString(message, x, y);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        String restartMessage = "Press SPACE to restart";
        fm = g2d.getFontMetrics();
        x = (getWidth() - fm.stringWidth(restartMessage)) / 2;
        g2d.drawString(restartMessage, x, y + 30);
    }
    
    /**
     * 셀 그리기
     */
    private void drawCell(Graphics2D g2d, int x, int y, Color color) {
        int pixelX = x * CELL_SIZE;
        int pixelY = y * CELL_SIZE;
        
        // 셀 배경
        g2d.setColor(color);
        g2d.fillRect(pixelX + 1, pixelY + 1, CELL_SIZE - 2, CELL_SIZE - 2);
        
        // 셀 테두리 (3D 효과)
        g2d.setColor(color.brighter());
        g2d.drawLine(pixelX + 1, pixelY + 1, pixelX + CELL_SIZE - 2, pixelY + 1);
        g2d.drawLine(pixelX + 1, pixelY + 1, pixelX + 1, pixelY + CELL_SIZE - 2);
        
        g2d.setColor(color.darker());
        g2d.drawLine(pixelX + CELL_SIZE - 2, pixelY + 1, pixelX + CELL_SIZE - 2, pixelY + CELL_SIZE - 2);
        g2d.drawLine(pixelX + 1, pixelY + CELL_SIZE - 2, pixelX + CELL_SIZE - 2, pixelY + CELL_SIZE - 2);
    }
    
    /**
     * 키보드 이벤트 리스너
     */
    private class TetrisKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!game.isGameRunning()) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    game.startGame();
                }
                return;
            }
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A:
                    game.moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D:
                    game.moveRight();
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S:
                    game.dropPiece();
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W:
                    game.rotatePiece();
                    break;
                case KeyEvent.VK_SPACE:
                    game.hardDrop();
                    break;
                case KeyEvent.VK_P:
                    game.togglePause();
                    break;
                case KeyEvent.VK_ESCAPE:
                    game.endGame();
                    break;
            }
        }
    }
}
