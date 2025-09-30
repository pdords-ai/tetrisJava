package org.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * 테트리스 게임의 메인 로직을 관리하는 클래스
 */
public class TetrisGame {
    private GameBoard gameBoard;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int score;
    private int level;
    private int linesCleared;
    private boolean gameRunning;
    private boolean gamePaused;
    private Timer gameTimer;
    
    // 게임 이벤트 리스너
    private GameEventListener eventListener;
    
    // 게임 속도 (밀리초)
    private int baseSpeed = 1000; // 1초
    private int currentSpeed;
    
    public interface GameEventListener {
        void onGameOver();
        void onScoreChanged(int score);
        void onLevelChanged(int level);
        void onLinesCleared(int lines);
        void onBoardChanged();
    }
    
    public TetrisGame() {
        gameBoard = new GameBoard();
        score = 0;
        level = 1;
        linesCleared = 0;
        gameRunning = false;
        gamePaused = false;
        currentSpeed = baseSpeed;
        
        setupGameTimer();
    }
    
    /**
     * 게임 타이머 설정
     */
    private void setupGameTimer() {
        gameTimer = new Timer(currentSpeed, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameRunning && !gamePaused) {
                    dropPiece();
                }
            }
        });
    }
    
    /**
     * 게임 시작
     */
    public void startGame() {
        gameBoard.clearBoard();
        score = 0;
        level = 1;
        linesCleared = 0;
        gameRunning = true;
        gamePaused = false;
        
        spawnNewPiece();
        updateSpeed();
        gameTimer.start();
        
        if (eventListener != null) {
            eventListener.onScoreChanged(score);
            eventListener.onLevelChanged(level);
            eventListener.onBoardChanged();
        }
    }
    
    /**
     * 게임 일시정지/재개
     */
    public void togglePause() {
        if (gameRunning) {
            gamePaused = !gamePaused;
            if (gamePaused) {
                gameTimer.stop();
            } else {
                gameTimer.start();
            }
        }
    }
    
    /**
     * 게임 종료
     */
    public void endGame() {
        gameRunning = false;
        gamePaused = false;
        gameTimer.stop();
        
        if (eventListener != null) {
            eventListener.onGameOver();
        }
    }
    
    /**
     * 새로운 블록 생성
     */
    private void spawnNewPiece() {
        if (nextPiece == null) {
            nextPiece = Tetromino.getRandom();
        }
        
        currentPiece = nextPiece;
        nextPiece = Tetromino.getRandom();
        
        currentPiece.setPosition(4, 0);
        
        // 새 블록이 바로 충돌하면 게임 오버
        if (!gameBoard.canPlace(currentPiece, 0, 0)) {
            endGame();
        }
        
        if (eventListener != null) {
            eventListener.onBoardChanged();
        }
    }
    
    /**
     * 블록을 아래로 이동
     */
    public void dropPiece() {
        if (currentPiece == null || !gameRunning || gamePaused) {
            return;
        }
        
        if (gameBoard.canPlace(currentPiece, 0, 1)) {
            currentPiece.move(0, 1);
        } else {
            // 블록을 보드에 고정
            gameBoard.placeTetromino(currentPiece);
            
            // 완성된 라인 제거
            int cleared = gameBoard.clearLines();
            if (cleared > 0) {
                linesCleared += cleared;
                updateScore(cleared);
                updateLevel();
            }
            
            // 게임 오버 확인
            if (gameBoard.isGameOver()) {
                endGame();
                return;
            }
            
            spawnNewPiece();
        }
        
        if (eventListener != null) {
            eventListener.onBoardChanged();
        }
    }
    
    /**
     * 블록을 왼쪽으로 이동
     */
    public void moveLeft() {
        if (currentPiece != null && gameRunning && !gamePaused) {
            if (gameBoard.canPlace(currentPiece, -1, 0)) {
                currentPiece.move(-1, 0);
                if (eventListener != null) {
                    eventListener.onBoardChanged();
                }
            }
        }
    }
    
    /**
     * 블록을 오른쪽으로 이동
     */
    public void moveRight() {
        if (currentPiece != null && gameRunning && !gamePaused) {
            if (gameBoard.canPlace(currentPiece, 1, 0)) {
                currentPiece.move(1, 0);
                if (eventListener != null) {
                    eventListener.onBoardChanged();
                }
            }
        }
    }
    
    /**
     * 블록 회전
     */
    public void rotatePiece() {
        if (currentPiece != null && gameRunning && !gamePaused) {
            currentPiece.rotate();
            
            // 회전 후 충돌하면 원래대로 되돌림
            if (!gameBoard.canPlace(currentPiece, 0, 0)) {
                currentPiece.rotate();
                currentPiece.rotate();
                currentPiece.rotate(); // 3번 더 회전해서 원래 상태로
            }
            
            if (eventListener != null) {
                eventListener.onBoardChanged();
            }
        }
    }
    
    /**
     * 블록을 즉시 바닥으로 떨어뜨림
     */
    public void hardDrop() {
        if (currentPiece == null || !gameRunning || gamePaused) {
            return;
        }
        
        while (gameBoard.canPlace(currentPiece, 0, 1)) {
            currentPiece.move(0, 1);
        }
        
        dropPiece(); // 블록을 보드에 고정
    }
    
    /**
     * 점수 업데이트
     */
    private void updateScore(int lines) {
        int[] points = {0, 40, 100, 300, 1200}; // 라인별 점수
        score += points[lines] * level;
        
        if (eventListener != null) {
            eventListener.onScoreChanged(score);
        }
    }
    
    /**
     * 레벨 업데이트 (10줄마다 레벨업)
     */
    private void updateLevel() {
        int newLevel = (linesCleared / 10) + 1;
        if (newLevel != level) {
            level = newLevel;
            updateSpeed();
            
            if (eventListener != null) {
                eventListener.onLevelChanged(level);
            }
        }
    }
    
    /**
     * 게임 속도 업데이트
     */
    private void updateSpeed() {
        currentSpeed = Math.max(50, baseSpeed - (level - 1) * 50); // 레벨이 올라갈수록 빨라짐
        gameTimer.setDelay(currentSpeed);
    }
    
    // Getter 메서드들
    public GameBoard getGameBoard() { return gameBoard; }
    public Tetromino getCurrentPiece() { return currentPiece; }
    public Tetromino getNextPiece() { return nextPiece; }
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLinesCleared() { return linesCleared; }
    public boolean isGameRunning() { return gameRunning; }
    public boolean isGamePaused() { return gamePaused; }
    
    /**
     * 이벤트 리스너 설정
     */
    public void setEventListener(GameEventListener listener) {
        this.eventListener = listener;
    }
}
