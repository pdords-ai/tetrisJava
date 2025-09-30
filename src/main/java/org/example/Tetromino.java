package org.example;

import java.awt.Color;
import java.util.Arrays;

/**
 * 테트리스 블록(Tetromino)을 나타내는 클래스
 */
public class Tetromino {
    // 테트리스 블록의 7가지 타입
    public static final int I = 0; // 직선
    public static final int O = 1; // 정사각형
    public static final int T = 2; // T자
    public static final int S = 3; // S자
    public static final int Z = 4; // Z자
    public static final int J = 5; // J자
    public static final int L = 6; // L자
    
    private int type;
    private int rotation;
    private int x, y;
    private Color color;
    
    // 각 블록 타입별 모양 정의 (4x4 격자)
    private static final int[][][] SHAPES = {
        // I 블록
        {
            {0,0,0,0},
            {1,1,1,1},
            {0,0,0,0},
            {0,0,0,0}
        },
        // O 블록
        {
            {0,0,0,0},
            {0,1,1,0},
            {0,1,1,0},
            {0,0,0,0}
        },
        // T 블록
        {
            {0,0,0,0},
            {0,1,0,0},
            {1,1,1,0},
            {0,0,0,0}
        },
        // S 블록
        {
            {0,0,0,0},
            {0,1,1,0},
            {1,1,0,0},
            {0,0,0,0}
        },
        // Z 블록
        {
            {0,0,0,0},
            {1,1,0,0},
            {0,1,1,0},
            {0,0,0,0}
        },
        // J 블록
        {
            {0,0,0,0},
            {1,0,0,0},
            {1,1,1,0},
            {0,0,0,0}
        },
        // L 블록
        {
            {0,0,0,0},
            {0,0,1,0},
            {1,1,1,0},
            {0,0,0,0}
        }
    };
    
    // 각 블록 타입별 색상
    private static final Color[] COLORS = {
        Color.CYAN,    // I
        Color.YELLOW,  // O
        Color.MAGENTA, // T
        Color.GREEN,   // S
        Color.RED,     // Z
        Color.BLUE,    // J
        Color.ORANGE   // L
    };
    
    public Tetromino(int type) {
        this.type = type;
        this.rotation = 0;
        this.x = 4; // 게임 보드 중앙에 시작
        this.y = 0;
        this.color = COLORS[type];
    }
    
    /**
     * 블록을 시계방향으로 90도 회전
     */
    public void rotate() {
        rotation = (rotation + 1) % 4;
    }
    
    /**
     * 현재 회전 상태의 블록 모양을 반환
     */
    public int[][] getShape() {
        int[][] shape = SHAPES[type];
        int[][] rotated = new int[4][4];
        
        // 회전에 따라 블록 모양 변환
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                switch (rotation) {
                    case 0: // 원래 모양
                        rotated[i][j] = shape[i][j];
                        break;
                    case 1: // 90도 회전
                        rotated[i][j] = shape[3-j][i];
                        break;
                    case 2: // 180도 회전
                        rotated[i][j] = shape[3-i][3-j];
                        break;
                    case 3: // 270도 회전
                        rotated[i][j] = shape[j][3-i];
                        break;
                }
            }
        }
        
        return rotated;
    }
    
    /**
     * 블록을 특정 위치로 이동
     */
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }
    
    /**
     * 블록의 좌표 설정
     */
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    // Getter 메서드들
    public int getType() { return type; }
    public int getRotation() { return rotation; }
    public int getX() { return x; }
    public int getY() { return y; }
    public Color getColor() { return color; }
    
    /**
     * 랜덤한 테트리스 블록 생성
     */
    public static Tetromino getRandom() {
        int type = (int) (Math.random() * 7);
        return new Tetromino(type);
    }
}
