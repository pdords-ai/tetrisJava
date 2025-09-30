package org.example;

import java.awt.Color;

/**
 * 테트리스 게임 보드를 관리하는 클래스
 */
public class GameBoard {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    
    // 게임 보드 (0: 빈 공간, 1 이상: 블록이 있는 공간)
    private int[][] board;
    
    public GameBoard() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        clearBoard();
    }
    
    /**
     * 게임 보드를 초기화
     */
    public void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = 0;
            }
        }
    }
    
    /**
     * 특정 위치에 블록이 있는지 확인
     */
    public boolean isOccupied(int x, int y) {
        if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
            return true; // 경계를 벗어나면 차단된 것으로 간주
        }
        return board[y][x] != 0;
    }
    
    /**
     * 테트로미노가 보드에 배치될 수 있는지 확인
     */
    public boolean canPlace(Tetromino tetromino, int offsetX, int offsetY) {
        int[][] shape = tetromino.getShape();
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (shape[i][j] != 0) {
                    int x = tetromino.getX() + j + offsetX;
                    int y = tetromino.getY() + i + offsetY;
                    
                    if (isOccupied(x, y)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * 테트로미노를 보드에 고정
     */
    public void placeTetromino(Tetromino tetromino) {
        int[][] shape = tetromino.getShape();
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (shape[i][j] != 0) {
                    int x = tetromino.getX() + j;
                    int y = tetromino.getY() + i;
                    
                    if (y >= 0 && y < BOARD_HEIGHT && x >= 0 && x < BOARD_WIDTH) {
                        board[y][x] = tetromino.getType() + 1; // 타입 + 1로 저장
                    }
                }
            }
        }
    }
    
    /**
     * 완성된 라인들을 제거하고 점수 반환
     */
    public int clearLines() {
        int linesCleared = 0;
        
        for (int y = BOARD_HEIGHT - 1; y >= 0; y--) {
            boolean isLineFull = true;
            
            // 라인이 완성되었는지 확인
            for (int x = 0; x < BOARD_WIDTH; x++) {
                if (board[y][x] == 0) {
                    isLineFull = false;
                    break;
                }
            }
            
            // 완성된 라인이 있으면 제거하고 위의 라인들을 아래로 이동
            if (isLineFull) {
                linesCleared++;
                
                // 위의 라인들을 아래로 이동
                for (int moveY = y; moveY > 0; moveY--) {
                    for (int x = 0; x < BOARD_WIDTH; x++) {
                        board[moveY][x] = board[moveY - 1][x];
                    }
                }
                
                // 맨 위 라인을 빈 공간으로 설정
                for (int x = 0; x < BOARD_WIDTH; x++) {
                    board[0][x] = 0;
                }
                
                y++; // 같은 라인을 다시 확인
            }
        }
        
        return linesCleared;
    }
    
    /**
     * 게임 오버 조건 확인
     */
    public boolean isGameOver() {
        // 맨 위 라인에 블록이 있으면 게임 오버
        for (int x = 0; x < BOARD_WIDTH; x++) {
            if (board[0][x] != 0) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 보드의 특정 위치 값 반환
     */
    public int getCell(int x, int y) {
        if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
            return 0;
        }
        return board[y][x];
    }
    
    /**
     * 보드 전체 반환
     */
    public int[][] getBoard() {
        return board;
    }
    
    /**
     * 보드 상태를 콘솔에 출력 (디버깅용)
     */
    public void printBoard() {
        System.out.println("=== 게임 보드 ===");
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                System.out.print(board[y][x] == 0 ? "." : board[y][x]);
            }
            System.out.println();
        }
        System.out.println("===============");
    }
}
