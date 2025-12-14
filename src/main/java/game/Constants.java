package game;

import java.awt.Color;
import java.awt.Font;

public class Constants {
    // Window dimensions
    public static final int WINDOW_WIDTH = 800;
    public static final int WINDOW_HEIGHT = 600;
    public static final int GROUND_LEVEL = 550;
    
    // Player constants
    public static final int PLAYER_WIDTH = 40;
    public static final int PLAYER_HEIGHT = 50;
    public static final double PLAYER_SPEED = 6.0; // Slightly faster for better control
    
    // Bubble constants
    public static final int BUBBLE_LARGE = 80;
    public static final int BUBBLE_MEDIUM = 50;
    public static final int BUBBLE_SMALL = 25;
    public static final double BUBBLE_SPEED = 1.5;
    public static final double GRAVITY = 0.4;
    
    // Projectile constants
    public static final int PROJECTILE_WIDTH = 4; // Slightly wider for visibility
    public static final double PROJECTILE_SPEED = 10.0; // Faster projectile
    
    // Game constants
    public static final int INITIAL_BUBBLES = 1;
    public static final int MAX_LEVEL = 5;
    public static final int LIVES = 3;
    public static final int LEVEL_TIME = 60; // 60 seconds per level
    
    // Scoring
    public static final int SCORE_LARGE_BUBBLE = 100;
    public static final int SCORE_MEDIUM_BUBBLE = 200;
    public static final int SCORE_SMALL_BUBBLE = 300;
    public static final int SCORE_LEVEL_COMPLETE = 1000;
    public static final int SCORE_TIME_BONUS = 10; // Per second remaining
    
    // Enhanced Color Scheme - Modern Gaming Palette
    // Background colors with gradient
    public static final Color COLOR_BACKGROUND_TOP = new Color(15, 20, 40);
    public static final Color COLOR_BACKGROUND_BOTTOM = new Color(30, 40, 70);
    public static final Color COLOR_BACKGROUND = new Color(26, 26, 46); // Fallback
    
    // Ground with depth
    public static final Color COLOR_GROUND_TOP = new Color(35, 50, 85);
    public static final Color COLOR_GROUND_BOTTOM = new Color(20, 30, 55);
    public static final Color COLOR_GROUND = new Color(22, 33, 62); // Fallback
    public static final Color COLOR_GROUND_HIGHLIGHT = new Color(50, 70, 120, 100);
    
    // Player colors - Hero character
    public static final Color COLOR_PLAYER_BODY = new Color(20, 60, 120);
    public static final Color COLOR_PLAYER_BODY_DARK = new Color(15, 45, 90);
    public static final Color COLOR_PLAYER_OUTLINE = new Color(10, 30, 60);
    public static final Color COLOR_PLAYER_HEAD = new Color(255, 200, 150);
    public static final Color COLOR_PLAYER_HAIR = new Color(80, 50, 30);
    public static final Color COLOR_PLAYER_WEAPON = new Color(180, 180, 200);
    public static final Color COLOR_PLAYER_GLOW = new Color(100, 150, 255, 80);
    
    // Bubble colors - Vibrant and eye-catching
    public static final Color COLOR_BUBBLE_BASE = new Color(100, 150, 255);
    public static final Color COLOR_BUBBLE_LARGE = new Color(255, 100, 150);
    public static final Color COLOR_BUBBLE_MEDIUM = new Color(150, 100, 255);
    public static final Color COLOR_BUBBLE_SMALL = new Color(100, 255, 200);
    public static final Color COLOR_BUBBLE = new Color(233, 69, 96); // Fallback
    
    // Projectile colors - Energy beam effect
    public static final Color COLOR_PROJECTILE_CORE = new Color(0, 255, 255);
    public static final Color COLOR_PROJECTILE_GLOW = new Color(0, 200, 255, 180);
    public static final Color COLOR_PROJECTILE_OUTER = new Color(100, 220, 255, 100);
    public static final Color COLOR_PROJECTILE = new Color(0, 217, 255); // Fallback
    
    // UI Colors - Modern and readable
    public static final Color COLOR_TEXT = new Color(255, 255, 255);
    public static final Color COLOR_TEXT_SHADOW = new Color(0, 0, 0, 150);
    public static final Color COLOR_TEXT_HIGHLIGHT = new Color(255, 220, 100);
    public static final Color COLOR_TEXT_WARNING = new Color(255, 100, 100);
    
    // Health/Lives colors
    public static final Color COLOR_HEALTH_FULL = new Color(100, 255, 100);
    public static final Color COLOR_HEALTH_MEDIUM = new Color(255, 200, 0);
    public static final Color COLOR_HEALTH_LOW = new Color(255, 80, 80);
    public static final Color COLOR_HEALTH_EMPTY = new Color(60, 60, 60);
    
    // UI Element colors
    public static final Color COLOR_UI_BACKGROUND = new Color(20, 30, 50, 200);
    public static final Color COLOR_UI_BORDER = new Color(100, 150, 255, 150);
    public static final Color COLOR_BUTTON = new Color(50, 80, 140);
    public static final Color COLOR_BUTTON_HOVER = new Color(70, 110, 180);
    public static final Color COLOR_BUTTON_TEXT = new Color(255, 255, 255);
    
    // Particle effect colors
    public static final Color COLOR_PARTICLE_EXPLOSION = new Color(255, 150, 50);
    public static final Color COLOR_PARTICLE_SPARKLE = new Color(255, 255, 200);
    public static final Color COLOR_PARTICLE_BUBBLE_POP = new Color(150, 200, 255);
    
    // Star/decoration colors for background
    public static final Color COLOR_STAR_BRIGHT = new Color(255, 255, 255, 200);
    public static final Color COLOR_STAR_DIM = new Color(200, 220, 255, 100);
    
    // Fonts
    public static final Font FONT_LARGE = new Font("Arial", Font.BOLD, 36);
    public static final Font FONT_MEDIUM = new Font("Arial", Font.BOLD, 24);
    public static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 16);
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 48);
    public static final Font FONT_SCORE = new Font("Courier New", Font.BOLD, 20);
    
    // Animation constants
    public static final int PARTICLE_COUNT_BUBBLE_POP = 15;
    public static final int PARTICLE_LIFETIME = 30; // frames
    public static final double PARTICLE_SPEED = 4.0;
    
    // Visual effects toggles
    public static final boolean ENABLE_PARTICLES = true;
    public static final boolean ENABLE_SCREEN_SHAKE = true;
    public static final boolean ENABLE_GLOW_EFFECTS = true;
    public static final boolean ENABLE_SHADOWS = true;
    public static final boolean ENABLE_BACKGROUND_STARS = true;
    
    // Screen shake
    public static final int SHAKE_DURATION = 10; // frames
    public static final int SHAKE_INTENSITY = 5; // pixels
    
    // Level Themes
    public static class LevelTheme {
        public Color bgTop, bgBottom, groundTop, groundBottom;
        public Color starColor;
        public String name;
        
        public LevelTheme(String name, Color bgTop, Color bgBottom, 
                         Color groundTop, Color groundBottom, Color starColor) {
            this.name = name;
            this.bgTop = bgTop;
            this.bgBottom = bgBottom;
            this.groundTop = groundTop;
            this.groundBottom = groundBottom;
            this.starColor = starColor;
        }
    }
    
    // Define themes for each level
    public static final LevelTheme[] LEVEL_THEMES = {
        // Level 1: Night Sky (Blue/Purple)
        new LevelTheme("Midnight Sky", 
            new Color(15, 20, 40), 
            new Color(30, 40, 70),
            new Color(35, 50, 85),
            new Color(20, 30, 55),
            new Color(255, 255, 255, 200)),
        
        // Level 2: Sunset (Orange/Pink)
        new LevelTheme("Crimson Sunset",
            new Color(60, 20, 40),
            new Color(80, 40, 60),
            new Color(70, 30, 50),
            new Color(50, 20, 35),
            new Color(255, 200, 150, 200)),
        
        // Level 3: Ocean Deep (Cyan/Teal)
        new LevelTheme("Ocean Depths",
            new Color(10, 30, 50),
            new Color(20, 50, 80),
            new Color(15, 40, 70),
            new Color(10, 25, 50),
            new Color(100, 200, 255, 200)),
        
        // Level 4: Aurora (Green/Purple)
        new LevelTheme("Aurora Borealis",
            new Color(20, 30, 50),
            new Color(40, 20, 60),
            new Color(50, 30, 70),
            new Color(30, 15, 45),
            new Color(150, 255, 200, 200)),
        
        // Level 5: Cosmic Space (Deep Purple/Black)
        new LevelTheme("Cosmic Void",
            new Color(10, 5, 20),
            new Color(30, 10, 40),
            new Color(40, 20, 50),
            new Color(25, 10, 35),
            new Color(200, 150, 255, 200))
    };
    
    // Get theme for specific level
    public static LevelTheme getThemeForLevel(int level) {
        int index = Math.min(level - 1, LEVEL_THEMES.length - 1);
        return LEVEL_THEMES[Math.max(0, index)];
    }
}