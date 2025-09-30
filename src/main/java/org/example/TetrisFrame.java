package org.example;

import javax.swing.*;
import java.awt.*;

/**
 * 테트리스 게임의 메인 프레임
 */
public class TetrisFrame extends JFrame {
    private TetrisGame game;
    private TetrisPanel gamePanel;
    
    public TetrisFrame() {
        setTitle("테트리스 게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // 게임 객체 생성
        game = new TetrisGame();
        
        // 게임 패널 생성
        gamePanel = new TetrisPanel(game);
        
        // 프레임에 패널 추가
        add(gamePanel);
        
        // 프레임 크기 설정 및 중앙에 배치
        pack();
        setLocationRelativeTo(null);
        
        // 게임 시작
        game.startGame();
        
        // 게임 패널에 포커스 설정
        gamePanel.requestFocusInWindow();
    }
    
    public static void main(String[] args) {
        // Swing GUI를 EDT(Event Dispatch Thread)에서 실행
        SwingUtilities.invokeLater(() -> {
            try {
                // 시스템 룩앤필 설정
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new TetrisFrame().setVisible(true);
        });
    }
}
