package game;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.AlphaComposite;
import java.awt.GradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

public class Projectile {
    private double x, y;
    private boolean active;
    private double pulsePhase = 0;
    private double electricPhase = 0;
    
    public Projectile(double x) {
        this.x = x;
        this.y = Constants.GROUND_LEVEL;
        this.active = true;
    }
    
    public void update() {
        y -= Constants.PROJECTILE_SPEED;
        pulsePhase += 0.3;
        electricPhase += 0.5;
        
        if (y < 0) {
            active = false;
        }
    }
    
    public void draw(Graphics2D g) {
        // Enable antialiasing
        g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                          java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        
        int drawX = (int)x;
        int drawY = (int)y;
        int groundY = Constants.GROUND_LEVEL;
        
        // Draw outer glow layers
        if (Constants.ENABLE_GLOW_EFFECTS) {
            for (int i = 4; i > 0; i--) {
                float alpha = 0.15f * i * (float)(Math.sin(pulsePhase) * 0.3 + 0.7);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g.setColor(Constants.COLOR_PROJECTILE_OUTER);
                g.setStroke(new BasicStroke(Constants.PROJECTILE_WIDTH + i * 3, 
                           BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.drawLine(drawX, drawY, drawX, groundY);
            }
        }
        
        // Draw middle glow layer
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g.setColor(Constants.COLOR_PROJECTILE_GLOW);
        g.setStroke(new BasicStroke(Constants.PROJECTILE_WIDTH + 2, 
                   BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(drawX, drawY, drawX, groundY);
        
        // Draw core beam with gradient
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        GradientPaint beamGradient = new GradientPaint(
            drawX, drawY, Constants.COLOR_PROJECTILE_CORE.brighter(),
            drawX, groundY, Constants.COLOR_PROJECTILE_CORE
        );
        g.setPaint(beamGradient);
        g.setStroke(new BasicStroke(Constants.PROJECTILE_WIDTH, 
                   BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(drawX, drawY, drawX, groundY);
        
        // Draw electric sparks along the beam
        if (Constants.ENABLE_GLOW_EFFECTS) {
            drawElectricSparks(g, drawX, drawY, groundY);
        }
        
        // Draw projectile tip with radial gradient
        float pulse = (float)(Math.sin(pulsePhase) * 0.3 + 1.0);
        int tipSize = (int)(12 * pulse);
        
        // Outer glow for tip
        for (int i = 3; i > 0; i--) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f * i));
            g.setColor(Constants.COLOR_PROJECTILE_GLOW);
            int glowSize = tipSize + i * 6;
            g.fillOval(drawX - glowSize / 2, drawY - glowSize / 2, glowSize, glowSize);
        }
        
        // Tip with radial gradient
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        Point2D center = new Point2D.Float(drawX, drawY);
        float radius = tipSize / 2f;
        float[] dist = {0.0f, 0.5f, 1.0f};
        Color[] colors = {
            Color.WHITE,
            Constants.COLOR_PROJECTILE_CORE,
            Constants.COLOR_PROJECTILE_GLOW
        };
        
        if (radius > 0) {
            RadialGradientPaint tipGradient = new RadialGradientPaint(center, radius, dist, colors);
            g.setPaint(tipGradient);
            g.fillOval(drawX - tipSize / 2, drawY - tipSize / 2, tipSize, tipSize);
        }
        
        // Tip shine
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
        g.setColor(Color.WHITE);
        int shineSize = tipSize / 3;
        g.fillOval(drawX - shineSize / 2 - 2, drawY - shineSize / 2 - 2, shineSize, shineSize);
        
        // Ground impact effect
        drawGroundImpact(g, drawX, groundY);
        
        // Reset composite
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private void drawElectricSparks(Graphics2D g, int x, int startY, int endY) {
        // Draw small electric sparks along the beam
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        int beamLength = endY - startY;
        int numSparks = 5;
        
        for (int i = 0; i < numSparks; i++) {
            double sparkY = startY + (beamLength * i / (double)numSparks) + 
                           Math.sin(electricPhase + i) * 10;
            double sparkOffset = Math.cos(electricPhase * 2 + i) * 8;
            
            float alpha = (float)(Math.abs(Math.sin(electricPhase + i * 0.5)) * 0.6 + 0.2);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            g.setColor(Color.WHITE);
            g.drawLine(x, (int)sparkY, (int)(x + sparkOffset), (int)(sparkY + 5));
            
            g.setColor(Constants.COLOR_PROJECTILE_CORE);
            g.drawLine(x, (int)sparkY, (int)(x - sparkOffset), (int)(sparkY - 5));
        }
    }
    
    private void drawGroundImpact(Graphics2D g, int x, int groundY) {
        // Draw energy spreading at ground level
        float impactAlpha = 0.4f * (float)(Math.sin(pulsePhase * 2) * 0.3 + 0.7);
        
        // Outer impact wave
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, impactAlpha * 0.5f));
        g.setColor(Constants.COLOR_PROJECTILE_OUTER);
        int waveSize = (int)(20 + Math.sin(pulsePhase) * 5);
        g.fillOval(x - waveSize, groundY - 5, waveSize * 2, 10);
        
        // Inner impact
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, impactAlpha));
        g.setColor(Constants.COLOR_PROJECTILE_GLOW);
        int innerSize = (int)(12 + Math.sin(pulsePhase) * 3);
        g.fillOval(x - innerSize, groundY - 3, innerSize * 2, 6);
        
        // Core impact
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, impactAlpha * 1.5f));
        g.setColor(Constants.COLOR_PROJECTILE_CORE);
        g.fillOval(x - 6, groundY - 2, 12, 4);
        
        // Sparkles
        for (int i = 0; i < 3; i++) {
            double angle = electricPhase + i * Math.PI * 2 / 3;
            int sparkX = x + (int)(Math.cos(angle) * 15);
            int sparkY = groundY + (int)(Math.sin(angle) * 3);
            
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
                          impactAlpha * (float)(Math.sin(pulsePhase + i) * 0.5 + 0.5)));
            g.setColor(Color.WHITE);
            g.fillOval(sparkX - 2, sparkY - 2, 4, 4);
        }
    }
    
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}