package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;

public class Particle {
    private double x, y;
    private double velocityX, velocityY;
    private Color color;
    private int lifetime;
    private int maxLifetime;
    private double size;
    
    public Particle(double x, double y, double velocityX, double velocityY, Color color) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.color = color;
        this.maxLifetime = Constants.PARTICLE_LIFETIME;
        this.lifetime = maxLifetime;
        this.size = 2 + Math.random() * 3;
    }
    
    public void update() {
        x += velocityX;
        y += velocityY;
        
        // Apply gravity
        velocityY += 0.15;
        
        // Air resistance
        velocityX *= 0.98;
        velocityY *= 0.98;
        
        lifetime--;
    }
    
    public void draw(Graphics2D g) {
        // Calculate alpha based on lifetime
        float alpha = (float) lifetime / maxLifetime;
        alpha = Math.max(0, Math.min(1, alpha));
        
        int drawSize = (int) (size * (0.5 + alpha * 0.5));
        
        // Single glow layer
        if (Constants.ENABLE_GLOW_EFFECTS && drawSize > 1) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha * 0.3f));
            g.setColor(color);
            g.fillOval((int)x - drawSize - 2, (int)y - drawSize - 2, 
                      drawSize * 2 + 4, drawSize * 2 + 4);
        }
        
        // Main particle
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g.setColor(color);
        g.fillOval((int)x - drawSize, (int)y - drawSize, drawSize * 2, drawSize * 2);
        
        // Reset composite
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    public boolean isAlive() {
        return lifetime > 0 && y < Constants.WINDOW_HEIGHT + 50;
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
}