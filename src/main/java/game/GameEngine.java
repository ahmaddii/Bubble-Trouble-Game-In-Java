package game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEngine {
    private Player player;
    private List<Bubble> bubbles;
    private List<Projectile> projectiles;
    private List<Particle> particles;
    private int level;
    private int lives;
    private int score;
    private boolean gameOver;
    private boolean levelComplete;
    private int timeRemaining;
    private long lastTimeUpdate;
    private int comboMultiplier;
    private long lastHitTime;
    private int screenShakeFrames;
    private int screenShakeX;
    private int screenShakeY;
    private boolean levelJustChanged;
    
    public GameEngine() {
        this.player = new Player();
        this.bubbles = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.particles = new ArrayList<>();
        this.level = 1;
        this.lives = Constants.LIVES;
        this.score = 0;
        this.gameOver = false;
        this.levelComplete = false;
        this.timeRemaining = Constants.LEVEL_TIME;
        this.lastTimeUpdate = System.currentTimeMillis();
        this.comboMultiplier = 1;
        this.lastHitTime = 0;
        this.screenShakeFrames = 0;
        this.screenShakeX = 0;
        this.screenShakeY = 0;
        this.levelJustChanged = false;
        initLevel();
    }
    
    private void initLevel() {
        bubbles.clear();
        projectiles.clear();
        particles.clear();
        levelComplete = false;
        timeRemaining = Constants.LEVEL_TIME;
        lastTimeUpdate = System.currentTimeMillis();
        comboMultiplier = 1;
        levelJustChanged = true; // Signal that level changed
        
        int numBubbles = Constants.INITIAL_BUBBLES + (level - 1);
        for (int i = 0; i < numBubbles; i++) {
            double x = 100 + i * 150;
            if (x > Constants.WINDOW_WIDTH - 150) {
                x = 100 + (i % 3) * 200;
            }
            double velocityX = (i % 2 == 0) ? Constants.BUBBLE_SPEED : -Constants.BUBBLE_SPEED;
            bubbles.add(new Bubble(x, 100, Constants.BUBBLE_LARGE, velocityX));
        }
    }
    
    public void update() {
        if (gameOver || levelComplete) return;
        
        // Update screen shake
        if (screenShakeFrames > 0) {
            screenShakeFrames--;
            if (screenShakeFrames > 0) {
                screenShakeX = (int)(Math.random() * Constants.SHAKE_INTENSITY * 2 - Constants.SHAKE_INTENSITY);
                screenShakeY = (int)(Math.random() * Constants.SHAKE_INTENSITY * 2 - Constants.SHAKE_INTENSITY);
            } else {
                screenShakeX = 0;
                screenShakeY = 0;
            }
        }
        
        // Update combo multiplier (reset after 2 seconds)
        if (System.currentTimeMillis() - lastHitTime > 2000) {
            comboMultiplier = 1;
        }
        
        // Update timer
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTimeUpdate >= 1000) {
            timeRemaining--;
            lastTimeUpdate = currentTime;
            
            // Time's up - lose a life
            if (timeRemaining <= 0) {
                lives--;
                createDeathParticles(player.getCenterX(), player.getY());
                triggerScreenShake();
                player.reset();
                if (lives <= 0) {
                    gameOver = true;
                } else {
                    timeRemaining = Constants.LEVEL_TIME;
                }
                return;
            }
        }
        
        player.update();
        
        // Update bubbles
        for (Bubble bubble : bubbles) {
            bubble.update();
            
            // Check collision with player
            if (bubble.collidesWith(player)) {
                lives--;
                createDeathParticles(player.getCenterX(), player.getY());
                triggerScreenShake();
                player.reset();
                if (lives <= 0) {
                    gameOver = true;
                }
                return;
            }
        }
        
        // Update projectiles
        Iterator<Projectile> projIt = projectiles.iterator();
        while (projIt.hasNext()) {
            Projectile proj = projIt.next();
            proj.update();
            
            if (!proj.isActive()) {
                projIt.remove();
                continue;
            }
            
            // Check collision with bubbles
            for (Bubble bubble : bubbles) {
                if (bubble.collidesWithProjectile(proj)) {
                    proj.setActive(false);
                    bubble.setActive(false);
                    
                    // Calculate score based on bubble size and combo
                    int baseScore = 0;
                    if (bubble.getSize() == Constants.BUBBLE_LARGE) {
                        baseScore = Constants.SCORE_LARGE_BUBBLE;
                    } else if (bubble.getSize() == Constants.BUBBLE_MEDIUM) {
                        baseScore = Constants.SCORE_MEDIUM_BUBBLE;
                    } else if (bubble.getSize() == Constants.BUBBLE_SMALL) {
                        baseScore = Constants.SCORE_SMALL_BUBBLE;
                    }
                    
                    // Apply combo multiplier
                    score += baseScore * comboMultiplier;
                    
                    // Update combo (simplified logic)
                    long currentHitTime = System.currentTimeMillis();
                    if (currentHitTime - lastHitTime < 2000) {
                        comboMultiplier = Math.min(comboMultiplier + 1, 5);
                    }
                    lastHitTime = currentHitTime;
                    
                    // Create particles at bubble location
                    createBubblePopParticles(
                        bubble.getX() + bubble.getSize() / 2.0,
                        bubble.getY() + bubble.getSize() / 2.0,
                        bubble.getSize()
                    );
                    
                    // Light screen shake for bubble pop
                    if (Constants.ENABLE_SCREEN_SHAKE) {
                        screenShakeFrames = 3;
                    }
                    
                    // Split bubble
                    if (bubble.getSize() == Constants.BUBBLE_LARGE) {
                        bubbles.add(new Bubble(bubble.getX(), bubble.getY(), 
                            Constants.BUBBLE_MEDIUM, Constants.BUBBLE_SPEED * 1.2));
                        bubbles.add(new Bubble(bubble.getX(), bubble.getY(), 
                            Constants.BUBBLE_MEDIUM, -Constants.BUBBLE_SPEED * 1.2));
                    } else if (bubble.getSize() == Constants.BUBBLE_MEDIUM) {
                        bubbles.add(new Bubble(bubble.getX(), bubble.getY(), 
                            Constants.BUBBLE_SMALL, Constants.BUBBLE_SPEED * 1.5));
                        bubbles.add(new Bubble(bubble.getX(), bubble.getY(), 
                            Constants.BUBBLE_SMALL, -Constants.BUBBLE_SPEED * 1.5));
                    }
                    break;
                }
            }
        }
        
        // Update particles
        Iterator<Particle> particleIt = particles.iterator();
        while (particleIt.hasNext()) {
            Particle p = particleIt.next();
            p.update();
            if (!p.isAlive()) {
                particleIt.remove();
            }
        }
        
        // Remove inactive bubbles
        bubbles.removeIf(b -> !b.isActive());
        
        // Check level complete
        if (bubbles.isEmpty()) {
            // Time bonus
            score += timeRemaining * Constants.SCORE_TIME_BONUS;
            score += Constants.SCORE_LEVEL_COMPLETE;
            levelComplete = true;
            
            // Create celebration particles
            createCelebrationParticles();
        }
    }
    
    public void shoot() {
        if (projectiles.isEmpty()) {
            projectiles.add(new Projectile(player.getCenterX()));
        }
    }
    
    public void nextLevel() {
        if (level < Constants.MAX_LEVEL) {
            level++;
            initLevel();
            player.reset();
        } else {
            gameOver = true;
        }
    }
    
    public void restart() {
        level = 1;
        lives = Constants.LIVES;
        score = 0;
        gameOver = false;
        timeRemaining = Constants.LEVEL_TIME;
        lastTimeUpdate = System.currentTimeMillis();
        comboMultiplier = 1;
        screenShakeFrames = 0;
        player.reset();
        initLevel();
    }
    
    private void createBubblePopParticles(double x, double y, int bubbleSize) {
        if (!Constants.ENABLE_PARTICLES) return;
        
        int count = Constants.PARTICLE_COUNT_BUBBLE_POP;
        for (int i = 0; i < count; i++) {
            double angle = (Math.PI * 2 * i) / count;
            double speed = Constants.PARTICLE_SPEED * (0.5 + Math.random() * 0.5);
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed;
            particles.add(new Particle(x, y, vx, vy, Constants.COLOR_PARTICLE_BUBBLE_POP));
        }
    }
    
    private void createDeathParticles(double x, double y) {
        if (!Constants.ENABLE_PARTICLES) return;
        
        for (int i = 0; i < 20; i++) {
            double angle = Math.random() * Math.PI * 2;
            double speed = Constants.PARTICLE_SPEED * (0.5 + Math.random());
            double vx = Math.cos(angle) * speed;
            double vy = Math.sin(angle) * speed - 2;
            particles.add(new Particle(x, y, vx, vy, Constants.COLOR_PARTICLE_EXPLOSION));
        }
    }
    
    private void createCelebrationParticles() {
        if (!Constants.ENABLE_PARTICLES) return;
        
        for (int i = 0; i < 50; i++) {
            double x = Math.random() * Constants.WINDOW_WIDTH;
            double y = Constants.GROUND_LEVEL;
            double vx = (Math.random() - 0.5) * 4;
            double vy = -Math.random() * 8 - 5;
            particles.add(new Particle(x, y, vx, vy, Constants.COLOR_PARTICLE_SPARKLE));
        }
    }
    
    private void triggerScreenShake() {
        if (Constants.ENABLE_SCREEN_SHAKE) {
            screenShakeFrames = Constants.SHAKE_DURATION;
        }
    }
    
    // Getters
    public Player getPlayer() { return player; }
    public List<Bubble> getBubbles() { return bubbles; }
    public List<Projectile> getProjectiles() { return projectiles; }
    public List<Particle> getParticles() { return particles; }
    public int getLevel() { return level; }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public boolean isGameOver() { return gameOver; }
    public boolean isLevelComplete() { return levelComplete; }
    public int getTimeRemaining() { return timeRemaining; }
    public int getComboMultiplier() { return comboMultiplier; }
    public int getScreenShakeX() { return screenShakeX; }
    public int getScreenShakeY() { return screenShakeY; }
    public boolean isLevelJustChanged() { 
        boolean result = levelJustChanged;
        levelJustChanged = false; // Reset after checking
        return result;
    }
}