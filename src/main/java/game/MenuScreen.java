package game;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

public class MenuScreen extends JPanel {
    private JFrame frame;
    private JButton playButton, instructionsButton, exitButton;
    private List<FloatingBubble> floatingBubbles;
    private List<StarParticle> stars;
    private Timer animationTimer;
    private int frameCount = 0;
    
    // Animated floating bubbles
    private class FloatingBubble {
        double x, y, size, speed, wobble, rotation;
        Color color;
        
        FloatingBubble() {
            x = Math.random() * Constants.WINDOW_WIDTH;
            y = Math.random() * Constants.WINDOW_HEIGHT;
            size = Math.random() * 50 + 30;
            speed = Math.random() * 0.8 + 0.3;
            wobble = Math.random() * Math.PI * 2;
            rotation = Math.random() * Math.PI * 2;
            Color[] colors = {
                new Color(100, 200, 255, 180),
                new Color(255, 150, 200, 180),
                new Color(150, 255, 200, 180),
                new Color(255, 200, 100, 180)
            };
            color = colors[(int)(Math.random() * colors.length)];
        }
        
        void update() {
            y -= speed;
            wobble += 0.03;
            rotation += 0.02;
            x += Math.sin(wobble) * 0.8;
            
            if (y < -size) {
                y = Constants.WINDOW_HEIGHT + size;
                x = Math.random() * Constants.WINDOW_WIDTH;
            }
        }
        
        void draw(Graphics2D g) {
            int drawX = (int)(x + Math.sin(wobble) * 3);
            int drawY = (int)y;
            
            // Outer glow
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
            g.setColor(color);
            g.fillOval(drawX - 5, drawY - 5, (int)size + 10, (int)size + 10);
            
            // Main bubble
            GradientPaint gradient = new GradientPaint(
                drawX, drawY, new Color(color.getRed(), color.getGreen(), color.getBlue(), 120),
                drawX + (int)size, drawY + (int)size, new Color(color.getRed()/2, color.getGreen()/2, color.getBlue()/2, 80)
            );
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
            g.setPaint(gradient);
            g.fillOval(drawX, drawY, (int)size, (int)size);
            
            // Shine
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g.setColor(Color.WHITE);
            g.fillOval(drawX + (int)(size * 0.2), drawY + (int)(size * 0.15), 
                      (int)(size * 0.3), (int)(size * 0.3));
        }
    }
    
    // Twinkling stars
    private class StarParticle {
        int x, y, size;
        double twinkle, speed;
        
        StarParticle() {
            x = (int)(Math.random() * Constants.WINDOW_WIDTH);
            y = (int)(Math.random() * Constants.WINDOW_HEIGHT);
            size = (int)(Math.random() * 3) + 1;
            twinkle = Math.random() * Math.PI * 2;
            speed = Math.random() * 0.05 + 0.02;
        }
        
        void update() {
            twinkle += speed;
        }
        
        void draw(Graphics2D g) {
            float alpha = (float)(Math.sin(twinkle) * 0.4 + 0.6);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            g.setColor(new Color(255, 255, 255, (int)(200 * alpha)));
            g.fillOval(x, y, size, size);
        }
    }
    
    public MenuScreen(JFrame frame) {
        this.frame = frame;
        this.setLayout(null);
        this.setBackground(Constants.COLOR_BACKGROUND);
        this.setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
        
        // Initialize stars
        stars = new ArrayList<>();
        for (int i = 0; i < 80; i++) {
            stars.add(new StarParticle());
        }
        
        // Initialize floating bubbles
        floatingBubbles = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            floatingBubbles.add(new FloatingBubble());
        }
        
        // Create buttons
        playButton = createStyledButton("▶ PLAY GAME", 250, 280);
        instructionsButton = createStyledButton("📖 HOW TO PLAY", 250, 360);
        exitButton = createStyledButton("✕ EXIT", 250, 440);
        
        // Button actions
        playButton.addActionListener(e -> startGame());
        instructionsButton.addActionListener(e -> showInstructions());
        exitButton.addActionListener(e -> System.exit(0));
        
        this.add(playButton);
        this.add(instructionsButton);
        this.add(exitButton);
        
        // Animation timer
        animationTimer = new Timer(1000 / 60, e -> {
            for (FloatingBubble bubble : floatingBubbles) {
                bubble.update();
            }
            for (StarParticle star : stars) {
                star.update();
            }
            frameCount++;
            repaint();
        });
        animationTimer.start();
    }
    
    private JButton createStyledButton(String text, int x, int y) {
        JButton button = new JButton(text) {
            private float hoverProgress = 0;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Animate hover
                if (getModel().isRollover()) {
                    hoverProgress = Math.min(1.0f, hoverProgress + 0.1f);
                } else {
                    hoverProgress = Math.max(0, hoverProgress - 0.1f);
                }
                
                // Outer glow on hover
                if (hoverProgress > 0) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f * hoverProgress));
                    g2d.setColor(new Color(0, 200, 255));
                    g2d.fillRoundRect(-8, -8, getWidth() + 16, getHeight() + 16, 25, 25);
                }
                
                // Button background
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                Color baseColor = getModel().isPressed() ? 
                    Constants.COLOR_BUTTON.darker() :
                    new Color(
                        Constants.COLOR_BUTTON.getRed() + (int)(30 * hoverProgress),
                        Constants.COLOR_BUTTON.getGreen() + (int)(40 * hoverProgress),
                        Constants.COLOR_BUTTON.getBlue() + (int)(60 * hoverProgress)
                    );
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, baseColor.brighter(),
                    0, getHeight(), baseColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Inner highlight
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() / 2, 15, 15);
                
                // Border with glow
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f + 0.2f * hoverProgress));
                g2d.setColor(new Color(100 + (int)(100 * hoverProgress), 
                                      150 + (int)(80 * hoverProgress), 
                                      255));
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 20, 20);
                
                // Text
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth(getText())) / 2;
                int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                
                // Text shadow
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(getText(), textX + 2, textY + 2);
                
                // Text with glow on hover
                if (hoverProgress > 0) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f * hoverProgress));
                    g2d.setColor(new Color(150, 220, 255));
                    for (int i = 1; i <= 2; i++) {
                        g2d.drawString(getText(), textX - i, textY - i);
                    }
                }
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), textX, textY);
            }
        };
        
        button.setBounds(x, y, 300, 60);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Animated gradient background
        int offset = (int)(Math.sin(frameCount * 0.01) * 20);
        GradientPaint bgGradient = new GradientPaint(
            0, offset, new Color(10, 15, 35),
            0, Constants.WINDOW_HEIGHT + offset, new Color(40, 25, 60)
        );
        g2d.setPaint(bgGradient);
        g2d.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        
        // Draw stars
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        for (StarParticle star : stars) {
            star.draw(g2d);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Draw floating bubbles
        for (FloatingBubble bubble : floatingBubbles) {
            bubble.draw(g2d);
        }
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Draw epic title
        drawEpicTitle(g2d);
        
        // Draw subtitle
        drawSubtitle(g2d);
        
        // Draw decorative elements
        drawDecorativeElements(g2d);
    }
    
    private void drawEpicTitle(Graphics2D g) {
        String title = "BUBBLE TROUBLE";
        g.setFont(new Font("Arial", Font.BOLD, 76));
        FontMetrics fm = g.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        int titleX = (Constants.WINDOW_WIDTH - titleWidth) / 2;
        int titleY = 140;
        
        // Pulsing effect
        float pulse = (float)(Math.sin(frameCount * 0.04) * 0.15 + 0.85);
        float breathe = (float)(Math.sin(frameCount * 0.02) * 3);
        
        // Multiple layer glow
        for (int layer = 5; layer > 0; layer--) {
            float layerAlpha = (0.08f * layer) * pulse;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, layerAlpha));
            g.setColor(new Color(100, 200, 255));
            g.drawString(title, titleX - layer * 2, titleY - layer * 2 + (int)breathe);
        }
        
        // Rainbow gradient title
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        for (int i = 0; i < title.length(); i++) {
            float hue = (frameCount * 0.003f + i * 0.05f) % 1.0f;
            Color letterColor = Color.getHSBColor(hue, 0.7f, 1.0f);
            
            String letter = String.valueOf(title.charAt(i));
            int letterX = titleX + fm.stringWidth(title.substring(0, i));
            
            // Letter shadow
            g.setColor(new Color(0, 0, 0, 150));
            g.drawString(letter, letterX + 3, titleY + 3);
            
            // Letter with glow
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g.setColor(letterColor.brighter());
            g.drawString(letter, letterX - 1, titleY - 1);
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            g.setColor(letterColor);
            g.drawString(letter, letterX, titleY);
        }
        
        // Shine effect
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f * pulse));
        g.setColor(Color.WHITE);
        g.drawString(title, titleX - 2, titleY - 3);
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private void drawSubtitle(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 20));
        String subtitle = "🎯 Pop All The Bubbles! 🎯";
        FontMetrics fm = g.getFontMetrics();
        int subtitleX = (Constants.WINDOW_WIDTH - fm.stringWidth(subtitle)) / 2;
        int subtitleY = 200;
        
        float pulse = (float)(Math.sin(frameCount * 0.05) * 0.3 + 0.7);
        
        // Subtitle glow
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f * pulse));
        g.setColor(new Color(255, 200, 100));
        g.drawString(subtitle, subtitleX - 1, subtitleY - 1);
        
        // Subtitle main
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g.setColor(new Color(255, 220, 150));
        g.drawString(subtitle, subtitleX, subtitleY);
    }
    
    private void drawDecorativeElements(Graphics2D g) {
        // Animated circles around the screen
        for (int i = 0; i < 4; i++) {
            double angle = (frameCount * 0.01 + i * Math.PI / 2);
            int x = (int)(Constants.WINDOW_WIDTH / 2 + Math.cos(angle) * 350);
            int y = (int)(250 + Math.sin(angle) * 100);
            int size = 15 + (int)(Math.sin(frameCount * 0.05 + i) * 5);
            
            float alpha = (float)(Math.sin(frameCount * 0.03 + i) * 0.3 + 0.4);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            Color circleColor = Color.getHSBColor((frameCount * 0.002f + i * 0.25f) % 1.0f, 0.6f, 1.0f);
            g.setColor(circleColor);
            g.fillOval(x - size/2, y - size/2, size, size);
        }
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private void startGame() {
        animationTimer.stop();
        frame.getContentPane().removeAll();
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel);
        frame.revalidate();
        frame.repaint();
        gamePanel.requestFocusInWindow();
    }
    
    private void showInstructions() {
        animationTimer.stop();
        InstructionsPanel instructionsPanel = new InstructionsPanel(frame);
        frame.getContentPane().removeAll();
        frame.add(instructionsPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    // Separate instructions panel class
    private class InstructionsPanel extends JPanel {
        private Timer instructionTimer;
        private int instructionFrame = 0;
        
        InstructionsPanel(JFrame parentFrame) {
            setLayout(null);
            setBackground(Constants.COLOR_BACKGROUND);
            setPreferredSize(new Dimension(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT));
            
            instructionTimer = new Timer(1000/60, e -> {
                instructionFrame++;
                repaint();
            });
            instructionTimer.start();
            
            JButton backButton = createStyledButton("← BACK TO MENU", 225, 500);
            backButton.addActionListener(e -> {
                instructionTimer.stop();
                parentFrame.getContentPane().removeAll();
                MenuScreen newMenu = new MenuScreen(parentFrame);
                parentFrame.add(newMenu);
                parentFrame.revalidate();
                parentFrame.repaint();
            });
            add(backButton);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Background gradient
            GradientPaint bgGradient = new GradientPaint(
                0, 0, new Color(15, 20, 40),
                0, Constants.WINDOW_HEIGHT, new Color(35, 25, 55)
            );
            g2d.setPaint(bgGradient);
            g2d.fillRect(0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
            
            // Title
            String title = "HOW TO PLAY";
            g2d.setFont(new Font("Arial", Font.BOLD, 52));
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = (Constants.WINDOW_WIDTH - fm.stringWidth(title)) / 2;
            
            // Title glow
            for (int i = 4; i > 0; i--) {
                g2d.setColor(new Color(100, 200, 255, 40 * i));
                g2d.drawString(title, titleX - i, 73 - i);
            }
            
            g2d.setColor(new Color(150, 220, 255));
            g2d.drawString(title, titleX, 75);
            
            // Panel
            g2d.setColor(new Color(20, 30, 50, 220));
            g2d.fillRoundRect(40, 110, 720, 360, 25, 25);
            g2d.setColor(new Color(100, 150, 255, 180));
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRoundRect(40, 110, 720, 360, 25, 25);
            
            // Instructions
            g2d.setFont(new Font("Arial", Font.PLAIN, 19));
            String[] instructions = {
                "⌨️  Move: LEFT/RIGHT Arrow Keys or A/D",
                "🚀  Shoot: Press SPACE to fire your weapon",
                "🎯  Pop all bubbles to complete each level",
                "💥  Large bubbles → 2 Medium bubbles",
                "💥  Medium bubbles → 2 Small bubbles", 
                "⚠️  Avoid touching bubbles or lose a life",
                "🏆  Complete all 5 levels to win!",
                "⏱️  Beat the timer for bonus points",
                "🔥  Hit bubbles quickly for combo multipliers!"
            };
            
            int y = 160;
            for (int i = 0; i < instructions.length; i++) {
                float alpha = Math.min(1.0f, (instructionFrame - i * 4) / 25.0f);
                if (alpha > 0) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                    
                    g2d.setColor(new Color(0, 0, 0, (int)(120 * alpha)));
                    g2d.drawString(instructions[i], 82, y + 2);
                    
                    g2d.setColor(new Color(220, 230, 255, (int)(255 * alpha)));
                    g2d.drawString(instructions[i], 80, y);
                    y += 40;
                }
            }
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}