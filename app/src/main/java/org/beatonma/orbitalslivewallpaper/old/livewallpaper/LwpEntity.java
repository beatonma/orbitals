package org.beatonma.orbitalslivewallpaper.old.livewallpaper;

import java.util.LinkedList;
import java.util.Queue;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Color;

import org.beatonma.orbitalslivewallpaper.old.Utils;

public class LwpEntity {
    private LwpService game;

    // Current location of this entity.
    private double x;
    private double y;

    // Current speed of this entity (pixels/second).
    private double dx;
    private double dy;

    // Previous speed of this entity (for calculating acceleration).
    private double dxOld;
    private double dyOld;

    // Physical properties.
    private int color;
    private Paint paint;
    public Path path;
    private double radius;
    //private double realRadius;
    private double mass;
    private double momentum;

    // Modifiers for animation
    private int renderRadius = 1; // Used to smooth changes in size
    private int renderColor = 0; // Used to smooth changes in colour

    private boolean isBlackHole = false;
    private boolean increaseBlackHoleRadiusModifier = true;
    private int blackHoleRadiusModifier = 0;

    // Collisions and explosions!
    private boolean canCollide = true;
    private int allowCollisionDelay = 0;
    private boolean destroy = false;
    private int collisionStyle = 0; // 0 = destroy; 1 = osmos-style mass
                                    // transfer; 2 = softened acceleration and
                                    // no collision
    // private boolean remove = false;

    // Gravitational constant = 0.00000000006674 N(m/kg)^2. Increased here to
    // make overall numbers easier to deal with.
    private double G = 0.06674;

    // Other options
    // Trace lines
    private Queue<Point> trace = new LinkedList<Point>();
    private int traceDelay = 0;
    private int traceSize = 25;

    // Constructors

    public LwpEntity(LwpService g, int x, int y, double dx, double dy,
                     int radius, int mass, int color) {
        this.game = g;
        this.G = g.getGravity();
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        if (g.getAntiGravity()) {
            this.mass = -mass * game.getObjectSize();
            if (this.mass > -1.0) this.mass = -1.0;
        } else {
            this.mass = mass * game.getObjectSize();
            if (this.mass < 1.0) this.mass = 1.0;
        }
        this.momentum = this.mass
                * Math.sqrt((this.dx * this.dx) + (this.dy * this.dy));

        this.radius = radius * game.getObjectSize();
        if (this.radius < 1.0) this.radius = 1.0;
        this.color = color;
        this.renderColor = color;
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Math.random() > 0.8 ? Paint.Join.BEVEL : Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setAntiAlias(true);

        float ff = g.getScreenScale();
        path = new Path();
        path.moveTo(x * ff, y * ff);

        if ((dx < 0) || (dy < 0)) {
            this.momentum = -this.momentum;
        }

        allowCollisionDelay = 0;
        this.collisionStyle = g.getCollisionStyle();

    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getRadius() {

        if (destroy) {
            renderRadius -= 2;
            return renderRadius;
        }

        else if (renderRadius > radius) {
            renderRadius--;
            return renderRadius;
        } else if (renderRadius < radius) {
            renderRadius++;
            return renderRadius;
        }

        else if (this.isBlackHole) {

            if (increaseBlackHoleRadiusModifier) {
                if (blackHoleRadiusModifier > 10) {
                    increaseBlackHoleRadiusModifier = false;
                    blackHoleRadiusModifier--;
                } else {
                    blackHoleRadiusModifier++;
                }
            }

            else {
                if (blackHoleRadiusModifier < 0) {
                    increaseBlackHoleRadiusModifier = true;
                    blackHoleRadiusModifier++;
                } else {
                    blackHoleRadiusModifier--;
                }
            }

            return radius + blackHoleRadiusModifier;

        }

        else {
            return radius;
        }
    }

    public void setRadius(double r) {
        radius = r;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double m) {
        this.mass = m;
    }

    public int getColor() {
        int r1, g1, b1, r2, g2, b2;
        int redDelta = 0;
        int greenDelta = 0;
        int blueDelta = 0;

        r1 = Color.red(renderColor);
        r2 = Color.red(color);
        if (r1 != r2) {
            redDelta = Math.abs(r1 - r2) / game.colorDelta;
        }

        g1 = Color.green(renderColor);
        g2 = Color.green(color);
        if (g1 != g2) {
            greenDelta = Math.abs(g1 - g2) / game.colorDelta;
        }

        b1 = Color.blue(renderColor);
        b2 = Color.blue(color);
        if (b1 != b2) {
            blueDelta = Math.abs(b1 - b2) / game.colorDelta;
        }

        // normalise color deltas
        int maxValue = Math.max(Math.max(redDelta, greenDelta), blueDelta);
        if (maxValue != 0) {
            redDelta = (int) ((double) redDelta / (double) maxValue * 10);
            greenDelta = (int) ((double) greenDelta / (double) maxValue * 10);
            blueDelta = (int) ((double) blueDelta / (double) maxValue * 10);
        }

        if (r1 - r2 < 10 &&  r1 - r2 > -10) {
            r1 = r2;
        }

        if (r1 > r2) {
            r1 -= redDelta;
        } else if (r1 < r2) {
            r1 += redDelta;
        }

        if (g1 - g2 < 10 &&  g1 - g2 > -10) {
            g1 = g2;
        }

        if (g1 > g2) {
            g1 -= greenDelta;
        } else if (g1 < g2) {
            g1 += greenDelta;
        }

        if (b1 - b2 < 10 || b1 - b2 > -10) {
            b1 = b2;
        }

        if (b1 > b2) {
            b1 -= blueDelta;
        } else if (b1 < b2) {
            b1 += blueDelta;
        }

        renderColor = Color.argb(game.getAlpha(), r1, g1, b1);

        return renderColor;
    }

    public void setColor(int c) {
        this.color = c;
        this.paint.setColor(c);
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Point getPosition() {
        Point p = new Point();
        p.x = (int) this.x;
        p.y = (int) this.y;
        return p;
    }

    public Queue<Point> getTrace() {
        return trace;
    }

    public void setTrace(Queue<Point> trace) {
        this.trace = trace;
    }

    public void setDestroy(boolean b) {
        destroy = b;
    }

    public boolean getDestroy() {
        return destroy;
    }

    public void plot(float ff) {
        float difference = (float) (Math.abs(Math.sqrt((dyOld - dy) * (dyOld - dy) / (dxOld * dx) * (dxOld * dx)) + Math.random() - 0.5));
        //Log.d(TAG, "Difference = " + difference);
        float maxSize = 300f * ff;
        float strokeSize = Utils.map(difference, 0f, 0.5f, 2f * ff, (float) radius * ff);
        if (strokeSize > maxSize) {
            strokeSize = maxSize;
        }
        paint.setStrokeWidth(strokeSize);
        int alpha = 255 - (int) Utils.map(strokeSize, 0f, maxSize, 100f, 255f);
        alpha = (int) Utils.map(alpha, 0, 255, 0, game.getAlpha());
        paint.setAlpha(alpha);
        path.lineTo((int)  (x + Math.random() - 0.5) * ff, (int) (y + Math.random() - 0.5) * ff);
    }

    public void resetPath() {
        path.reset();
        float ff = game.getScreenScale();
        path.moveTo((float) x * ff, (float) y * ff);
    }

    // Linear movement.
    public void updateLogic() {

//        this.collisionStyle = game.getCollisionStyle();

        if (game.getBorderBounce()) {
            float gameWidth = game.getWidth() / game.getScreenScale();
            float gameHeight = game.getHeight() / game.getScreenScale();
            float offset = game.getScrollOffset();

            if (x - radius < 0) {
                x = radius;
                dx = Math.abs(dx);
            }
            else if (x + radius > gameWidth) {
                x = gameWidth - radius;
                dx = -(Math.abs(dx));
            }

            if (y - radius < 0) {
                y = radius;
                dy = Math.abs(dy);
            }
            else if (y + radius > gameHeight) {
                y = gameHeight - radius;
                dy = -(Math.abs(dy));
            }
        }

        dxOld = dx;
        dyOld = dy;

        x += dx;
        y += dy;

        // Allow grace period for new entities created by explosions.
        if (!canCollide) {
            if (allowCollisionDelay > 60) {
                canCollide = true;
            } else {
                allowCollisionDelay++;
            }
        }
    }

    public void doTrace() {
        if (traceDelay > 6) { // leave a gap between dots.
            Point d = new Point((int) this.x, (int) this.y);
            getTrace().add(d);
            traceSize = game.getTraceLineLength();

            // Make sure trace doesn't get too large.
            if (getTrace().size() > traceSize) {
                for (int i = 0; i < (getTrace().size() - traceSize); i++) {
                    getTrace().remove();
                }
            }

            traceDelay = 0;
        } else {
            traceDelay++;
        }
    }

    public Point doAccelerationArrows() {

        double dx1 = dxOld;
        double dy1 = dyOld;

        if ((dx1 < 0) && (this.x < 0))
            dx1 = -dx1;
        if ((dy1 < 0) && (this.y < 0))
            dy1 = -dy1;

        int ix = (int) Math.round(this.x + ((this.dx - dx1) * 200));
        int iy = (int) Math.round(this.y + ((this.dy - dy1) * 200));

        return new Point(ix, iy);

    }

    public void doGravity(LwpEntity other) {
        // F_g = G((m1*m2)/(r^2))
        // a = F/m

        double m1 = this.getMass(); // m1
        double m2 = other.getMass(); // m2

        double c1x = this.getX(); // object 1 centre point x
        double c2x = other.getX(); // object 2 centre point x

        double c1y = this.getY(); // object 1 centre point y
        double c2y = other.getY(); // object 2 centre point y

        double cdiff = c2x - c1x;

        double grad = (c2y - c1y) / (c2x - c1x);

        if (cdiff < 0)
            grad = -grad; // fix the sign if gradient denominator is negative

        double r = Math.sqrt((c2x - c1x) * (c2x - c1x) + (c2y - c1y)
                * (c2y - c1y)); // radius = distance between object centres

        if (collisionStyle == 2) {
            if (r < 20) {
                r = 20;
            }
        }

        double F = G * ((m1 * m2) / (r * r)); // calculate force
        double a = F / m1; // calculate acceleration

        double dx = Math.cos(Math.atan(grad)) * a;
        if (cdiff > 0) { // positive x distance
            this.dx += dx; // calculate horizontal component of acceleration
        }
        else { // negative x distance
            this.dx -= dx;
        }

        this.dy += Math.sin(Math.atan(grad)) * a; // calculate vertical component of acceleration

    }

    public boolean detectCollision(LwpEntity other) {
        double c1x, c2x, c1y, c2y, r, d;

        switch (collisionStyle) {
        case 0: // One centre inside another object
            c1x = this.getX(); // object 1 centre point x
            c2x = other.getX(); // object 2 centre point x

            c1y = this.getY(); // object 1 centre point y
            c2y = other.getY(); // object 2 centre point y

            r = Math.sqrt((c2x - c1x) * (c2x - c1x) + (c2y - c1y) * (c2y - c1y)); // radius
                                                                                    // =
                                                                                    // distance
                                                                                    // between
                                                                                    // object
                                                                                    // centres

            if ((r < this.radius) || (r < other.radius)) {
                if (this.mass >= other.mass) {
                    return true;
                } else {
                    return false;
                }
            }

            return false;

        case 1: // Contact
            c1x = this.getX(); // object 1 centre point x
            c2x = other.getX(); // object 2 centre point x

            c1y = this.getY(); // object 1 centre point y
            c2y = other.getY(); // object 2 centre point y

            r = Math.sqrt((c2x - c1x) * (c2x - c1x) + (c2y - c1y) * (c2y - c1y)); // radius
                                                                                    // =
                                                                                    // distance
                                                                                    // between
                                                                                    // object
                                                                                    // centres

            d = this.radius + other.radius;

            if (r <= d) {
                if (this.mass >= other.mass) {
                    return true;
                } else {
                    return false;
                }
            }

        case 2:
            return false;
        }

        return false;

    }

    public void doCollision(LwpEntity other) {

        if (game.getAntiGravity()) {
            this.mass = -this.mass;
            other.mass = -other.mass;
        }

        switch (collisionStyle) {
        case 0: // Straight up destruction

            this.dx = ((this.getMass() * this.dx) + (other.getMass() * other.dx))
                    / (this.getMass() + other.getMass());
            this.dy = ((this.getMass() * this.dy) + (other.getMass() * other.dy))
                    / (this.getMass() + other.getMass());

            this.mass += other.mass;

            // this.radius += other.radius / 10;
            if (this.isBlackHole) {
                this.radius += other.radius / 50;
            } else {
                if (this.radius >= other.radius) {
                    this.radius += other.radius / 10;
                } else {
                    this.radius = (this.radius + other.radius) / 2;
                }
                this.color = mixColors(this.color, other.color);
                this.paint.setColor(this.color);
                if ((mass > 500000) && (radius > 40)) {
                    collapseToBlackHole(this);
                }
            }

            other.destroy = true;
            game.addDeadTracePoints(other.getTrace());

            break;

        case 1: // Osmos-style transfer
            this.mass += other.mass / 10.0;
            other.mass *= 9.0 / 10.0;
            this.radius += other.radius / 100.0;
            other.radius *= 5.0 / 10.0;

            double c1x = this.getX(); // object 1 centre point x
            double c2x = other.getX(); // object 2 centre point x

            double c1y = this.getY(); // object 1 centre point y
            double c2y = other.getY(); // object 2 centre point y

            double r = Math.sqrt((c2x - c1x) * (c2x - c1x) + (c2y - c1y)
                    * (c2y - c1y)); // radius
            // =
            // distance
            // between
            // object
            // centres

            if ((r < this.radius) || (r < other.radius) || (other.radius < 4.0)
                    || (other.mass < 50.0)) {
                this.dx = ((this.getMass() * this.dx) + (other.getMass() * other.dx))
                        / (this.getMass() + other.getMass());
                this.dy = ((this.getMass() * this.dy) + (other.getMass() * other.dy))
                        / (this.getMass() + other.getMass());

                this.mass += other.mass;

                if (this.isBlackHole) {
                    this.radius += other.radius / 50.0;
                } else {
                    if (this.radius >= other.radius) {
                        this.radius += other.radius / 10.0;
                    } else {
                        this.radius = (this.radius + other.radius) / 2;
                    }
                    this.color = mixColors(this.color, other.color);
                    this.paint.setColor(this.color);
                    if ((mass > 500000.0) && (radius > 40.0)) {
                        collapseToBlackHole(this);
                    }
                }

                other.destroy = true;
                game.addDeadTracePoints(other.getTrace());

            }

            break;
        case 2:
            break;
        default:
            break;

        }

        if (game.getAntiGravity()) {
            this.mass = -this.mass;
            other.mass = -other.mass;
        }

    }

    public void collapseToBlackHole(LwpEntity e) {
        e.radius = 10;
        e.isBlackHole = true;
        e.color = Color.argb(180, 100, 100, 100);
        e.paint.setColor(e.color);
        e.paint.setStyle(Paint.Style.STROKE);
    }

    public void allowCollisions(boolean b) {
        canCollide = b;
    }

    public boolean allowCollisions() {
        return canCollide;
    }

    // Subtractive color mixing.
    public int mixColors(int c1, int c2) {
        int[] rgb1 = { Color.red(c1), Color.green(c1), Color.blue(c1) };
        int[] rgb2 = { Color.red(c2), Color.green(c2), Color.blue(c2) };

        int[] rgb = { 0, 0, 0 };
        int c3;

        for (int i = 0; i < 3; i++) {
            rgb[i] = (rgb1[i] + rgb2[i]) / 2;
        }

        c3 = game.betterColor(rgb[0], rgb[1], rgb[2]);

        return c3;
    }
}
