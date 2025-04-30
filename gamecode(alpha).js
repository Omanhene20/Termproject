let game;

function setup() {
  createCanvas(600, 400);
  game = new Game();
}

function draw() {
  background(220);
  game.update();
  game.display();
}

// ----------------------------------------
// Game class – manages game state & logic
// ----------------------------------------
class Game {
  constructor() {
    this.ball = new Ball();
    this.paddle = new Paddle();
    this.score = 0;
  }

  update() {
    this.ball.update();
    this.paddle.update();

    if (this.ball.hitsPaddle(this.paddle)) {
      this.ball.bounce();
      this.score++;
    }

    if (this.ball.isOffScreen()) {
      this.reset();
    }
  }

  display() {
    // Display score
    textSize(20);
    textAlign(CENTER);
    text('Score: ' + this.score, width / 2, 30);

    this.ball.display();
    this.paddle.display();
  }

  reset() {
    this.ball.reset();
    this.score = 0;
  }
}

// ----------------------------------------
// Ball class – handles ball behavior
// ----------------------------------------
class Ball {
  constructor() {
    this.radius = 20;
    this.reset();
  }

  update() {
    this.x += this.xSpeed;
    this.y += this.ySpeed;

    // Bounce off left/right walls
    if (this.x - this.radius < 0 || this.x + this.radius > width) {
      this.xSpeed *= -1;
    }

    // Bounce off top wall
    if (this.y - this.radius < 0) {
      this.ySpeed *= -1;
    }
  }

  display() {
    fill(0, 150, 255);
    ellipse(this.x, this.y, this.radius * 2, this.radius * 2);
  }

  bounce() {
    this.ySpeed *= -1;
  }

  hitsPaddle(paddle) {
    return (
      this.x > paddle.x &&
      this.x < paddle.x + paddle.width &&
      this.y + this.radius > paddle.y &&
      this.y - this.radius < paddle.y + paddle.height
    );
  }

  isOffScreen() {
    return this.y - this.radius > height;
  }

  reset() {
    this.x = width / 2;
    this.y = height / 2;
    this.xSpeed = 5;
    this.ySpeed = 5;
  }
}

// ----------------------------------------
// Paddle class – controls player paddle
// ----------------------------------------
class Paddle {
  constructor() {
    this.width = 100;
    this.height = 20;
    this.y = height - 30;
    this.x = width / 2 - this.width / 2;
  }

  update() {
    this.x = mouseX - this.width / 2;
    this.x = constrain(this.x, 0, width - this.width);
  }

  display() {
    fill(255, 0, 0);
    rect(this.x, this.y, this.width, this.height);
  }
}

