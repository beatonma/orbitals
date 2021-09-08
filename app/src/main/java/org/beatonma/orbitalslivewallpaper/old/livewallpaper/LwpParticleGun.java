package org.beatonma.orbitalslivewallpaper.old.livewallpaper;

import android.graphics.Point;

public class LwpParticleGun {

    LwpService g;

    int rateOfFire; // particles fire every ~1/rateOfFire counts
    int size; // ~particle size
    int xSpeed; // x-axis speed
    int ySpeed; //y-axis speed
    int spread; // particles will have perpendicular +-velocity 0 <= v < spread
    int ttl; // time to live
    Point point; // Firing point

    public LwpParticleGun(LwpService g, Point p, int rate, int size, int xSpeed, int ySpeed, int spread, int ttl) {
        this.g = g;
        this.rateOfFire = rate;
        this.point = p;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.size = size;
        this.spread = spread;
        this.ttl = ttl;
    }

    public void shoot() {

        if (Math.random() < (1.0 / rateOfFire)) {

            int radius = size + (int) (Math.random() * 10.0);
            int mass = 1000 + (int) (Math.random() * 500.0);
            int dx = Math.random() < 0.5 ? xSpeed + (int) (Math.random() * spread) : xSpeed - (int) (Math.random() * spread);
            int dy = Math.random() < 0.5 ? ySpeed + (int) (Math.random() * spread) : ySpeed - (int) (Math.random() * spread);

            LwpEntity e = new LwpEntity(g, point.x, point.y, dx, dy, radius, mass, g.makeRandomColor());
            e.allowCollisions(false);
            g.addEntity(e);

            this.ttl--;
        }
    }

}
