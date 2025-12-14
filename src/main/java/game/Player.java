package game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GradientPaint;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;

public class Player {
    private double x, y;
    private boolean movingLeft, movingRight;
    private double walkCycle = 0;
    private double bobOffset = 0;
    private int direction = 1;
    
    // Pre-calculated colors
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 100);
    private static final Color EYE_COLOR = new Color(50, 100, 150);
    private static final Color SMILE_COLOR = new Color(150, 80, 80);
    private static final Color HEAD_LIGHT = new Color(255, 220, 180);
    
    public Player() {
        this.x = Constants.WINDOW_WIDTH / 2.0 - Constants.PLAYER_WIDTH / 2.0;
        this.y = Constants.GROUND_LEVEL - Constants.PLAYER_HEIGHT;
    }
    
    public void update() {
        // Direct movement - no acceleration/momentum
        if (movingLeft && !movingRight) {
            x -= Constants.PLAYER_SPEED;
            direction = -1;
            walkCycle += 0.2;
            bobOffset = Math.sin(walkCycle) * 1.5;
        } else if (movingRight && !movingLeft) {
            x += Constants.PLAYER_SPEED;
            direction = 1;
            walkCycle += 0.2;
            bobOffset = Math.sin(walkCycle) * 1.5;
        } else {
            // Immediately stop when no keys pressed
            walkCycle = 0;
            bobOffset = 0;
        }
        
        // Boundary checking
        if (x < 0) x = 0;
        if (x > Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH) {
            x = Constants.WINDOW_WIDTH - Constants.PLAYER_WIDTH;
        }
    }
    
    public void draw(Graphics2D g) {
        int drawX = (int)x;
        int drawY = (int)(y + bobOffset);
        
        // Simple shadow
        if (Constants.ENABLE_SHADOWS) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g.setColor(SHADOW_COLOR);
            g.fillOval(drawX + 5, Constants.GROUND_LEVEL - 5, 
                      Constants.PLAYER_WIDTH - 10, 8);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
        
        // Draw legs with simple animation
        int legOffset = (int)(Math.sin(walkCycle) * 2);
        g.setColor(Constants.COLOR_PLAYER_BODY_DARK);
        g.fillRect(drawX + 8, drawY + 30, 10, 20 + Math.abs(legOffset));
        g.fillRect(drawX + 22, drawY + 30, 10, 20 - Math.abs(legOffset));
        
        // Body with simple gradient
        GradientPaint bodyGradient = new GradientPaint(
            drawX, drawY, Constants.COLOR_PLAYER_BODY,
            drawX, drawY + 40, Constants.COLOR_PLAYER_BODY_DARK
        );
        g.setPaint(bodyGradient);
        g.fillRoundRect(drawX + 5, drawY + 10, Constants.PLAYER_WIDTH - 10, 30, 8, 8);
        
        // Arms
        int armSwing = (int)(Math.sin(walkCycle) * 4);
        g.setColor(Constants.COLOR_PLAYER_BODY);
        g.fillRoundRect(drawX, drawY + 15 - armSwing, 8, 18, 4, 4);
        g.fillRoundRect(drawX + Constants.PLAYER_WIDTH - 8, drawY + 15 + armSwing, 8, 18, 4, 4);
        
        // Simple weapon
        int weaponX = drawX + Constants.PLAYER_WIDTH / 2;
        int weaponY = drawY + 20;
        g.setColor(Constants.COLOR_PLAYER_WEAPON);
        g.fillRect(weaponX - 3, weaponY, 6, 15);
        g.fillRect(weaponX - 2, weaponY - 8, 4, 10);
        g.setColor(Constants.COLOR_PROJECTILE_CORE);
        g.fillOval(weaponX - 3, weaponY - 11, 6, 6);
        
        // Head with gradient
        GradientPaint headGradient = new GradientPaint(
            drawX + 5, drawY - 15, HEAD_LIGHT,
            drawX + 35, drawY + 10, Constants.COLOR_PLAYER_HEAD
        );
        g.setPaint(headGradient);
        g.fillOval(drawX + 5, drawY - 15, 30, 30);
        
        // Hair
        g.setColor(Constants.COLOR_PLAYER_HAIR);
        g.fillArc(drawX + 5, drawY - 20, 30, 25, 0, 180);
        
        // Eyes
        int eyeDirection = direction > 0 ? 2 : -2;
        g.setColor(Color.WHITE);
        g.fillOval(drawX + 10, drawY - 10, 8, 10);
        g.fillOval(drawX + 22, drawY - 10, 8, 10);
        
        g.setColor(EYE_COLOR);
        g.fillOval(drawX + 12 + eyeDirection, drawY - 7, 4, 5);
        g.fillOval(drawX + 24 + eyeDirection, drawY - 7, 4, 5);
        
        g.setColor(Color.WHITE);
        g.fillOval(drawX + 13 + eyeDirection, drawY - 6, 2, 2);
        g.fillOval(drawX + 25 + eyeDirection, drawY - 6, 2, 2);
        
        // Smile
        g.setColor(SMILE_COLOR);
        g.setStroke(new BasicStroke(1.5f));
        g.drawArc(drawX + 12, drawY - 5, 16, 10, 180, 180);
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public double getCenterX() { return x + Constants.PLAYER_WIDTH / 2.0; }
    
    public void setMovingLeft(boolean moving) { this.movingLeft = moving; }
    public void setMovingRight(boolean moving) { this.movingRight = moving; }
    
    public void reset() {
        this.x = Constants.WINDOW_WIDTH / 2.0 - Constants.PLAYER_WIDTH / 2.0;
        this.y = Constants.GROUND_LEVEL - Constants.PLAYER_HEIGHT;
        this.movingLeft = false;
        this.movingRight = false;
        this.walkCycle = 0;
        this.bobOffset = 0;
    }
}