package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {
    private GameEngine engine;
    private Timer timer;
    private List<Star> backgroundStars;
    private int frameCount = 0;
    
    // Star class for animated background
    private class Star {
        double x, y, size, speed, twinklePhase;
        
        Star() {
            x = Math.random() * Constants.WINDOW_WIDTH;
            y = Math.random() * Constants.GROUND_LEVEL;
            size = Math.random() * 2 + 1;
            speed = Math.random() * 0.2 + 0.1;
            twinklePhase = Math.random() * Math.PI * 2;
        }
        
        void update() {
            twinklePhase += speed;
        }
        
        void draw(Graphics2D g) {
            float alpha = (float)(Math.sin(twinklePhase) * 0.3 + 0.7);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(Constants.COLOR_STAR_BRIGHT);
            g.fillOval((int)x, (int)y, (int)size, (int)size);
        }
    }
    
    public GamePanel() {
        this.engine = new GameEngine();
        this.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        this.setBackground(Constants.COLOR_BACKGROUND);
        this.setFocusable(true);
        
        // Initialize background stars
        backgroundStars = new ArrayList<>();
        if (Constants.ENABLE_BACKGROUND_STARS) {
            for (int i = 0; i < 100; i++) {
                backgroundStars.add(new Star());
            }
        }
        
        // Keyboard controls
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                    engine.getPlayer().setMovingLeft(true);
                }
                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                    engine.getPlayer().setMovingRight(true);
                }
                if (key == KeyEvent.VK_SPACE) {
                    engine.shoot();
                }
                if (key == KeyEvent.VK_ENTER) {
                    if (engine.isLevelComplete()) {
                        engine.nextLevel();
                    }
                    if (engine.isGameOver()) {
                        engine.restart();
                    }
                }
                if (key == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                int key = e.getKeyCode();
                
                if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) {
                    engine.getPlayer().setMovingLeft(false);
                }
                if (key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) {
                    engine.getPlayer().setMovingRight(false);
                }
            }
        });
        
        // Game loop timer (60 FPS)
        timer = new Timer(1000 / 60, this);
        timer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        engine.update();
        frameCount++;
        
        // Reset frame count when level changes
        if (engine.isLevelJustChanged()) {
            frameCount = 0;
        }
        
        // Update stars
        for (Star star : backgroundStars) {
            star.update();
        }
        
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Get current level theme
        Constants.LevelTheme theme = Constants.getThemeForLevel(engine.getLevel());
        
        // Apply screen shake
        g2d.translate(engine.getScreenShakeX(), engine.getScreenShakeY());
        
        // Draw gradient background with theme colors
        GradientPaint bgGradient = new GradientPaint(
            0, 0, theme.bgTop,
            0, Constants.GROUND_LEVEL, theme.bgBottom
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.GROUND_LEVEL);
        
        // Draw stars with theme color
        if (Constants.ENABLE_BACKGROUND_STARS) {
            for (Star star : backgroundStars) {
                float alpha = (float)(Math.sin(star.twinklePhase) * 0.3 + 0.7);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.6f));
                g2d.setColor(theme.starColor);
                g2d.fillOval((int)star.x, (int)star.y, (int)star.size, (int)star.size);
            }
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // Draw ground with theme gradient
        GradientPaint groundGradient = new GradientPaint(
            0, Constants.GROUND_LEVEL, theme.groundTop,
            0, Constants.WINDOW_HEIGHT, theme.groundBottom
        );
        g2d.setPaint(groundGradient);
        g2d.fillRect(0, Constants.GROUND_LEVEL, Constants.WINDOW_WIDTH, 
                    Constants.WINDOW_HEIGHT - Constants.GROUND_LEVEL);
        
        // Draw ground highlight line
        g2d.setColor(new Color(theme.groundTop.getRed() + 30, 
                               theme.groundTop.getGreen() + 30, 
                               theme.groundTop.getBlue() + 30, 100));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(0, Constants.GROUND_LEVEL, Constants.WINDOW_WIDTH, Constants.GROUND_LEVEL);
        
        // Draw particles (behind game objects)
        for (Particle particle : engine.getParticles()) {
            particle.draw(g2d);
        }
        
        // Draw game objects
        engine.getPlayer().draw(g2d);
        
        for (Bubble bubble : engine.getBubbles()) {
            bubble.draw(g2d);
        }
        
        for (Projectile proj : engine.getProjectiles()) {
            proj.draw(g2d);
        }
        
        // Reset translation for HUD
        g2d.translate(-engine.getScreenShakeX(), -engine.getScreenShakeY());
        
        // Draw level name display
        drawLevelName(g2d, theme);
        
        // Draw HUD
        drawHUD(g2d);
        
        // Draw overlays
        if (engine.isGameOver()) {
            drawGameOver(g2d);
        } else if (engine.isLevelComplete()) {
            drawLevelComplete(g2d);
        }
    }
    
    private void drawHUD(Graphics2D g) {
        // HUD background panel
        g.setColor(Constants.COLOR_UI_BACKGROUND);
        g.fillRoundRect(5, 5, 250, 150, 15, 15);
        g.setColor(Constants.COLOR_UI_BORDER);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(5, 5, 250, 150, 15, 15);
        
        // Level
        g.setColor(Constants.COLOR_TEXT_HIGHLIGHT);
        g.setFont(Constants.FONT_MEDIUM);
        g.drawString("LEVEL " + engine.getLevel(), 20, 35);
        
        // Score with glow
        drawTextWithShadow(g, "Score: " + engine.getScore(), 20, 65, Constants.FONT_SMALL, Constants.COLOR_TEXT);
        
        // Lives with heart icons
        g.setFont(Constants.FONT_SMALL);
        g.setColor(Constants.COLOR_TEXT);
        g.drawString("Lives:", 20, 95);
        for (int i = 0; i < Constants.LIVES; i++) {
            if (i < engine.getLives()) {
                g.setColor(Constants.COLOR_HEALTH_FULL);
            } else {
                g.setColor(Constants.COLOR_HEALTH_EMPTY);
            }
            g.fillOval(80 + i * 25, 82, 18, 18);
        }
        
        // Timer with color coding and progress bar
        int timeRemaining = engine.getTimeRemaining();
        Color timerColor;
        if (timeRemaining <= 10) {
            timerColor = Constants.COLOR_TEXT_WARNING;
        } else if (timeRemaining <= 20) {
            timerColor = Constants.COLOR_HEALTH_MEDIUM;
        } else {
            timerColor = Constants.COLOR_PROJECTILE_CORE;
        }
        
        g.setFont(Constants.FONT_SMALL);
        g.setColor(Constants.COLOR_TEXT);
        g.drawString("Time:", 20, 125);
        
        // Time bar
        int barWidth = 150;
        int barHeight = 15;
        int barX = 75;
        int barY = 112;
        
        g.setColor(Constants.COLOR_HEALTH_EMPTY);
        g.fillRoundRect(barX, barY, barWidth, barHeight, 5, 5);
        
        float timePercent = (float)timeRemaining / Constants.LEVEL_TIME;
        int fillWidth = (int)(barWidth * timePercent);
        g.setColor(timerColor);
        g.fillRoundRect(barX, barY, fillWidth, barHeight, 5, 5);
        
        g.setColor(Constants.COLOR_UI_BORDER);
        g.drawRoundRect(barX, barY, barWidth, barHeight, 5, 5);
        
        g.setColor(Constants.COLOR_TEXT);
        g.drawString(timeRemaining + "s", barX + barWidth / 2 - 10, barY + 12);
        
        // Combo multiplier
        if (engine.getComboMultiplier() > 1) {
            String comboText = "x" + engine.getComboMultiplier() + " COMBO!";
            g.setFont(Constants.FONT_MEDIUM);
            
            // Simple pulse without heavy calculations
            float pulse = (float)(Math.sin(frameCount * 0.15) * 0.1 + 0.95);
            int comboAlpha = (int)(220 * pulse);
            
            int comboX = Constants.WINDOW_WIDTH / 2 - 70;
            int comboY = 50;
            
            // Shadow
            g.setColor(new Color(0, 0, 0, 100));
            g.drawString(comboText, comboX + 2, comboY + 2);
            
            // Main text
            g.setColor(new Color(255, 200, 0, comboAlpha));
            g.drawString(comboText, comboX, comboY);
        }
        
        // Controls hint
        g.setColor(new Color(255, 255, 255, 150));
        g.setFont(Constants.FONT_SMALL);
        String controls = "A/D or ←→ Move | SPACE Shoot | ESC Exit";
        g.drawString(controls, Constants.WINDOW_WIDTH - 350, Constants.WINDOW_HEIGHT - 10);
    }
    
    private void drawGameOver(Graphics2D g) {
        // Animated overlay
        float alpha = 0.85f;
        g.setColor(new Color(0, 0, 0, (int)(alpha * 255)));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        
        // Pulsing effect
        float pulse = (float)(Math.sin(frameCount * 0.05) * 0.1 + 0.9);
        
        // Game over text with glow
        String gameOverText = "GAME OVER";
        g.setFont(Constants.FONT_TITLE);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(gameOverText);
        int x = (Constants.WINDOW_WIDTH - textWidth) / 2;
        int y = Constants.WINDOW_HEIGHT / 2 - 80;
        
        // Red glow
        for (int i = 5; i > 0; i--) {
            g.setColor(new Color(255, 0, 0, (int)(30 * i * pulse)));
            g.drawString(gameOverText, x - i, y - i);
        }
        
        g.setColor(new Color(255, 100, 100, (int)(255 * pulse)));
        g.drawString(gameOverText, x, y);
        
        // Score panel
        g.setColor(Constants.COLOR_UI_BACKGROUND);
        int panelWidth = 400;
        int panelHeight = 150;
        int panelX = (Constants.WINDOW_WIDTH - panelWidth) / 2;
        int panelY = Constants.WINDOW_HEIGHT / 2 - 20;
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        g.setColor(Constants.COLOR_UI_BORDER);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        // Final score
        g.setFont(Constants.FONT_LARGE);
        String scoreText = "Final Score: " + engine.getScore();
        textWidth = g.getFontMetrics().stringWidth(scoreText);
        drawTextWithShadow(g, scoreText, (Constants.WINDOW_WIDTH - textWidth) / 2, 
                          panelY + 60, Constants.FONT_LARGE, Constants.COLOR_TEXT_HIGHLIGHT);
        
        // Restart prompt
        g.setFont(Constants.FONT_MEDIUM);
        String restartText = "Press ENTER to Restart";
        textWidth = g.getFontMetrics().stringWidth(restartText);
        drawTextWithShadow(g, restartText, (Constants.WINDOW_WIDTH - textWidth) / 2, 
                          panelY + 110, Constants.FONT_MEDIUM, Constants.COLOR_TEXT);
    }
    
    private void drawLevelComplete(Graphics2D g) {
        // Animated overlay
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        
        // Pulsing effect
        float pulse = (float)(Math.sin(frameCount * 0.08) * 0.15 + 0.85);
        
        // Level complete text with glow
        String completeText = "LEVEL COMPLETE!";
        g.setFont(Constants.FONT_TITLE);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(completeText);
        int x = (Constants.WINDOW_WIDTH - textWidth) / 2;
        int y = Constants.WINDOW_HEIGHT / 2 - 100;
        
        // Cyan glow
        for (int i = 5; i > 0; i--) {
            g.setColor(new Color(0, 255, 255, (int)(40 * i * pulse)));
            g.drawString(completeText, x - i, y - i);
        }
        
        g.setColor(new Color(100, 255, 255, (int)(255 * pulse)));
        g.drawString(completeText, x, y);
        
        // Info panel
        g.setColor(Constants.COLOR_UI_BACKGROUND);
        int panelWidth = 450;
        int panelHeight = 180;
        int panelX = (Constants.WINDOW_WIDTH - panelWidth) / 2;
        int panelY = Constants.WINDOW_HEIGHT / 2 - 40;
        g.fillRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        g.setColor(Constants.COLOR_UI_BORDER);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(panelX, panelY, panelWidth, panelHeight, 20, 20);
        
        // Stats
        g.setFont(Constants.FONT_MEDIUM);
        g.setColor(Constants.COLOR_TEXT);
        int statY = panelY + 50;
        g.drawString("Score: " + engine.getScore(), panelX + 150, statY);
        
        // Next level prompt
        g.setFont(Constants.FONT_MEDIUM);
        String nextText = "Press ENTER for Next Level";
        textWidth = g.getFontMetrics().stringWidth(nextText);
        drawTextWithShadow(g, nextText, (Constants.WINDOW_WIDTH - textWidth) / 2, 
                          panelY + 140, Constants.FONT_MEDIUM, Constants.COLOR_TEXT_HIGHLIGHT);
    }
    
    private void drawTextWithShadow(Graphics2D g, String text, int x, int y, Font font, Color color) {
        g.setFont(font);
        // Shadow
        g.setColor(Constants.COLOR_TEXT_SHADOW);
        g.drawString(text, x + 2, y + 2);
        // Main text
        g.setColor(color);
        g.drawString(text, x, y);
    }
    
    private void drawLevelName(Graphics2D g, Constants.LevelTheme theme) {
        // Only show for first 3 seconds of level
        if (frameCount < 180) {
            float alpha = 1.0f;
            if (frameCount > 120) {
                alpha = (180 - frameCount) / 60.0f;
            }
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setFont(Constants.FONT_LARGE);
            String levelText = "Level " + engine.getLevel() + ": " + theme.name;
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(levelText);
            int x = (Constants.WINDOW_WIDTH - textWidth) / 2;
            int y = 100;
            
            // Glow effect
            for (int i = 3; i > 0; i--) {
                g.setColor(new Color(theme.bgBottom.getRed(), 
                                    theme.bgBottom.getGreen(), 
                                    theme.bgBottom.getBlue(), (int)(100 * alpha)));
                g.drawString(levelText, x - i, y - i);
            }
            
            g.setColor(Constants.COLOR_TEXT_HIGHLIGHT);
            g.drawString(levelText, x, y);
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}