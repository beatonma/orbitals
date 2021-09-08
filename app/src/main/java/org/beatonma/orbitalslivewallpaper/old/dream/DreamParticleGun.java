package org.beatonma.orbitalslivewallpaper.old.dream;

import android.graphics.Point;

public class DreamParticleGun {

    DreamView g;

    int rateOfFire; // particles fire every ~1/rateOfFire counts
    //int direction; // 0 <= direction < 360
    int size; // ~particle size
    int xSpeed; // x-axis speed
    int ySpeed; //y-axis speed
    int spread; // particles will have perpendicular +-velocity 0 <= v < spread
    int ttl; // time to live
    Point point; // Firing point
    boolean move = true;
    boolean firework = false;
    boolean asteroids = false;

    public DreamParticleGun(DreamView g, Point p, int rate, int size, int xSpeed, int ySpeed, int spread, int ttl) {
        this.g = g;
        this.rateOfFire = firework ? 2 : rate;
        this.point = p;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.size = size;
        this.spread = spread;
        this.ttl = ttl;

        move = Math.random() >= 0.5;
    }

    public void shoot() {
        if (Math.random() < (1.0 / rateOfFire)) {

            int radius;
            int mass;
            if (firework) {
                radius = 1 + (int) (Math.random() * 5.0);
                mass = 10 + (int) (Math.random() * 50);
            }
            else if (asteroids && Math.random() < 0.5) {
                radius = 1 + (int) (Math.random() * 5.0);
                mass = 10 + (int) (Math.random() * 50);
            }
            else {
                radius = size + (int) (Math.random() * 10.0);
                mass = 1000 + (int) (Math.random() * 500.0);
            }
            //int mass = firework ? 10 + (int) (Math.random() * 50) : 1000 + (int) (Math.random() * 500.0);
            int dx = Math.random() < 0.5 ? xSpeed + (int) (Math.random() * spread) : xSpeed - (int) (Math.random() * spread);
            int dy = Math.random() < 0.5 ? ySpeed + (int) (Math.random() * spread) : ySpeed - (int) (Math.random() * spread);

            DreamEntity e = new DreamEntity(g, point.x, point.y, dx, dy, radius, mass, g.makeRandomColor());
            e.allowCollisions(false);
            if (firework) {
                e.setTTL(30);
            }
            g.addDreamEntity(e);

            ttl--;

            if (ttl <= 0) {
                if (firework) {
                    DreamEntity seed = new DreamEntity(g, point.x, point.y, 0, 0, 50, 5000, g.makeRandomColor());
                    seed.weWereExplodingAnyway();
                    g.explodeDreamEntity(seed);
                }
            }
        }
    }

    public void move() {
        if (move) {
            point.x -= xSpeed;
            point.y -= ySpeed;
        }
    }

    public void setMove(boolean move) {
        this.move = move;
    }

    public boolean canMove() {
        return move;
    }

    public void setFirework(boolean firework) {
        this.firework = firework;
    }

    public boolean isFirework() {
        return firework;
    }

    public int getTtl() {
        return ttl;
    }

    public void allowAsteroids(boolean b) {
        asteroids = true;
    }

}
