package game;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.GradientPaint;

public class Bubble {
    private double x, y;
    private double velocityX, velocityY;
    private int size;
    private boolean active;
    private double wobbleOffset = 0;
    private double wobbleSpeed;
    private float pulsePhase = 0;
    
    // Pre-calculated colors to avoid object creation
    private static final Color BUBBLE_LIGHT = new Color(180, 220, 255, 200);
    private static final Color BUBBLE_MID = new Color(100, 150, 255, 180);
    private static final Color BUBBLE_EDGE = new Color(80, 120, 220, 160);
    private static final Color SHINE_COLOR = new Color(255, 255, 255, 150);
    private static final Color OUTLINE_COLOR = new Color(200, 230, 255, 180);
    
    public Bubble(double x, double y, int size, double velocityX) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.velocityX = velocityX;
        this.velocityY = 0;
        this.active = true;
        this.wobbleSpeed = Math.random() * 0.1 + 0.05;
    }
    
    public void update() {
        // Apply gravity
        velocityY += Constants.GRAVITY;
        
        // Update position
        x += velocityX;
        y += velocityY;
        
        // Add wobble effect for visual appeal
        wobbleOffset += wobbleSpeed;
        pulsePhase += 0.05f;
        
        // Bounce off walls
        if (x <= 0 || x >= Constants.WINDOW_WIDTH - size) {
            velocityX = -velocityX;
            x = Math.max(0, Math.min(x, Constants.WINDOW_WIDTH - size));
        }
        
        // Bounce off ground
        if (y >= Constants.GROUND_LEVEL - size) {
            y = Constants.GROUND_LEVEL - size;
            velocityY = -velocityY;
            
            if (velocityY > 0) {
                velocityY = -velocityY;
            }
            
            if (Math.abs(velocityY) < 5) {
                velocityY = -7;
            }
            
            if (velocityY < -12) {
                velocityY = -12;
            }
        }
        
        // Bounce off ceiling
        if (y <= 0) {
            y = 0;
            velocityY = Math.abs(velocityY);
        }
    }
    
    public void draw(Graphics2D g) {
        // Calculate bubble position with slight wobble
        int drawX = (int)(x + Math.sin(wobbleOffset) * 1.5);
        int drawY = (int)y;
        
        // Simple pulse effect
        float pulse = (float)(Math.sin(pulsePhase) * 0.03 + 1.0);
        int drawSize = (int)(size * pulse);
        int sizeOffset = (size - drawSize) / 2;
        drawX += sizeOffset;
        drawY += sizeOffset;
        
        // Single outer glow layer (simplified)
        if (Constants.ENABLE_GLOW_EFFECTS) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g.setColor(BUBBLE_MID);
            g.fillOval(drawX - 4, drawY - 4, drawSize + 8, drawSize + 8);
        }
        
        // Simple gradient for bubble body
        GradientPaint gradient = new GradientPaint(
            drawX, drawY, BUBBLE_LIGHT,
            drawX + drawSize, drawY + drawSize, BUBBLE_EDGE
        );
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
        g.setPaint(gradient);
        g.fillOval(drawX, drawY, drawSize, drawSize);
        
        // Single shine effect
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        int shineSize = (int)(drawSize * 0.3);
        int shineX = drawX + (int)(drawSize * 0.25);
        int shineY = drawY + (int)(drawSize * 0.2);
        g.setColor(SHINE_COLOR);
        g.fillOval(shineX, shineY, shineSize, shineSize);
        
        // Simple outline
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.setColor(OUTLINE_COLOR);
        g.setStroke(new BasicStroke(2f));
        g.drawOval(drawX, drawY, drawSize, drawSize);
        
        // Reset composite
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    public boolean collidesWith(Player player) {
        double bubbleCenterX = x + size / 2.0;
        double bubbleCenterY = y + size / 2.0;
        double playerCenterX = player.getX() + Constants.PLAYER_WIDTH / 2.0;
        double playerCenterY = player.getY() + Constants.PLAYER_HEIGHT / 2.0;
        
        double distance = Math.sqrt(
            Math.pow(bubbleCenterX - playerCenterX, 2) + 
            Math.pow(bubbleCenterY - playerCenterY, 2)
        );
        
        return distance < (size / 2.0 + Constants.PLAYER_WIDTH / 2.0);
    }
    
    public boolean collidesWithProjectile(Projectile projectile) {
        double projX = projectile.getX();
        double projTopY = projectile.getY();
        double projBottomY = Constants.GROUND_LEVEL;
        
        double bubbleCenterX = x + size / 2.0;
        double bubbleCenterY = y + size / 2.0;
        double bubbleRadius = size / 2.0;
        
        double closestX = projX;
        double closestY = Math.max(projTopY, Math.min(bubbleCenterY, projBottomY));
        
        double distanceX = bubbleCenterX - closestX;
        double distanceY = bubbleCenterY - closestY;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        
        return distance <= bubbleRadius;
    }
    
    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public int getSize() { return size; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}