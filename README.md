🎮 Bubble Trouble – 2D Java Game

A classic Bubble Trouble–style 2D arcade game built in Java, featuring bubble physics, projectile shooting, collision detection, and multiple levels. This project is designed to demonstrate object-oriented programming, game loops, and basic 2D game mechanics using Java.

🚀 Features

🟢 Classic Bubble Trouble–style gameplay

🧍 Player movement (left / right)

🎯 Projectile shooting to pop bubbles

🔵 Bubble splitting mechanics

💥 Collision detection (player vs bubbles, projectile vs bubbles)

🧠 Level-based game structure

⏱️ Real-time game loop and rendering

🎨 Code-generated graphics (no external assets required)

🛠️ Tech Stack

Language: Java

GUI: Java Swing / AWT

Architecture: Object-Oriented Programming (OOP)

Game Loop: Custom loop using Java timing

Graphics: Java 2D API

📂 Project Structure
src/
└── game/
    ├── Main.java
    ├── GameEngine.java
    ├── GamePanel.java
    ├── MenuScreen.java
    ├── Player.java
    ├── Bubble.java
    ├── Projectile.java
    ├── Level.java
    └── GameConstants.java
resources/
└── (code-generated graphics)

▶️ How to Run

Clone the repository

git clone https://github.com/your-username/bubble-trouble-java.git


Open the project in VS Code / IntelliJ IDEA

Compile and run

javac src/game/Main.java
java src.game.Main


(Or simply run Main.java from your IDE)

🎮 Controls
Key	Action
⬅️ / ➡️	Move Player
Space	Shoot Projectile
Esc	Pause / Exit
🧩 Game Logic Overview

The player shoots vertical projectiles.

When a bubble is hit:

Large bubbles split into smaller ones.

Small bubbles disappear.

The level is cleared when all bubbles are destroyed.

The game ends if the player collides with a bubble.

🎯 Learning Objectives

This project helps in understanding:

Java game loops

Collision detection

Object-oriented design

2D rendering with Swing

Event handling (keyboard input)

Game state management

🔮 Future Improvements

🔊 Sound effects & background music

🧠 AI-based bubble movement

🏆 Score system & leaderboard

🎨 Improved graphics & animations

📱 Mobile version (JavaFX / LibGDX)

🤝 Contributing

Contributions are welcome!
Feel free to fork the repo, open issues, or submit pull requests.

📜 License

This project is open-source and available under the MIT License.

👨‍💻 Author

Malik Ahmad Rasheed
💡 Java | Game Development | AI Automation
📌 GitHub: https://github.com/ahmaddii
