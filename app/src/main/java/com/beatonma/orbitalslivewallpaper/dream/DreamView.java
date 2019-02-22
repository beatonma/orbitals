package com.beatonma.orbitalslivewallpaper.dream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.beatonma.orbitalslivewallpaper.R;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class DreamView extends ImageView implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	private Handler handler;
    Context context;

	public static final String SHARED_PREFS_NAME = "orbitalsDreamSettings";
	private static final String TAG = "Orbitals Dream";
    private final static String FILENAME = "active_notifications";
    private final static int INVALID_COLOR = -1234567890;

    private final static int RANDOM_SINGLE_SYSTEM = 0;
    private final static int STAR_SYSTEM = 1;
    private final static int CELL_SYSTEM = 2;
    private final static int RANDOM_SYSTEM = 3;
    private final static int POLY_SYSTEM = 4;
    private final static int STREAMER_SYSTEM = 5;

	private final static int CLOCK_ROTATION_0 = 0;
	private final static int CLOCK_ROTATION_90 = 90;
	private final static int CLOCK_ROTATION_180 = 180;
	private final static int CLOCK_ROTATION_270 = 270;

	public DreamView g = this;
	public DayDreamService dayDreamService = null;

	// Centre on centre of mass
	private boolean showCentreOfMass = false;
	private double totalMass = 0.0;
	private Point centreOfMass = new Point(0, 0);

	private List<DreamEntity> entities = new ArrayList<DreamEntity>();
	private List<DreamEntity> removeEntities = new ArrayList<DreamEntity>();
	private List<DreamEntity> newEntities = new ArrayList<DreamEntity>();
	private List<DreamParticleGun> dreamParticleGuns = new ArrayList<DreamParticleGun>();
	private List<DreamParticleGun> removeDreamParticleGuns = new ArrayList<DreamParticleGun>();

	// Initiate
	private int xStart = 0;
	private int yStart = 0;

	// Canvas scaling
	private float xSize = 0f;
	private float ySize = 0f;
	private float screenScale = 1f;

	// User options

	private int maxEntities = 10;
	private int frameRate = 30;

	private boolean allowStarSystems = true;
	private boolean allowCellSystems = true;
	private boolean allowRandomSystems = true;
	private boolean allowPolySystems = true;
	private boolean allowSystemGeneration = true;
	private boolean allowParticleGuns = true;
    private boolean allowAsteroids = true;
	private String touchGeneration = "3";
    private List<Integer> allowedSystems = new ArrayList<Integer>();

	// private boolean enableObjects = true;

	private boolean enableTraceLines = true;
	private Queue<Point> deadTraceLines = new LinkedList<Point>();
	private int traceLineLength = 25;

	private boolean enableAccelerationArrows = false;
	private boolean enableOutlines = true;

    // custom background image
    private boolean useBackgroundImage = false;
    private Bitmap backgroundImage;
    private boolean loadingBackground = false;
    private Point backgroundImageSize = new Point(0,0);
    private float backgroundScale = 0f;

	private String colorTheme = "0";
	private String[] objectColors = { "0" };
	private int backgroundColor = Color.BLACK;
	private int backgroundRenderColor = Color.BLACK;
	private int colorAlpha = 255;
	private boolean darkColors = false;
	private boolean desaturateColors = false;
	private boolean wireFrame = false;
	private int wireFrameWidth = 2;
	private double objectSize = 1.0;
	private boolean outlineColorCustom = false;
	private int outlineColor = Color.BLACK;

	private boolean fingerNotMoving = false;
	private long fingerNotMoved = 0;

	// Physics
	// Gravitational constant = 0.00000000006674 N(m/kg)^2. Changed magnitude
	// here to
	// make overall numbers easier to deal with. G = 0.06674
	private double G = 0.06674;
	private int gravityMultiplier = 10;
	private boolean mPhysicsBorderBounce = true;
	private boolean antiGravity = false;
	private int collisionStyle = 1; // 0 = simple crash and die; 1 = osmos-style
									// transfer; 2 = softened acceleration and
									// no collisions

    // Experiments
	private boolean pixelate = false;
	private boolean zAxis = false;
    private boolean shadowLayer = false;
    private boolean pathing = false;
    private Bitmap bitmap;
    private Canvas pathingCanvas;
    private Paint pathingPaint;
    private int pathingOverlayDelay = 0;


    // Time
    private boolean showTime = true;
    private boolean hhmm = false;
    private boolean increaseTimeOffset = true;
    private float timeOffset = 0f;
    private int timeColor = Color.WHITE;
    private boolean timeOutline = false;
    private double timeSize = 1.0;
    private int timeAlpha = 0;
	private int timeRotation = CLOCK_ROTATION_0;

    // Notifications
    private boolean showNotifications = false;
    private int notificationPulseDelay = 200;
    private int pulseTimer = 25;
    private int pulseLength = 100;
    private boolean pulse = false;
    private int pulseColor = INVALID_COLOR;
    private int pulseIndex = 0;
    private boolean assignNewColors = false;
    public int colorDelta = 1;

    private boolean isPreview = false;

    // Debug
    //long lastFrame = 0l;

    // Used for checking device orientation and maybe other things in the future.
    private int delay = 0;

	public DreamView(Context context, AttributeSet attrs) {
		super(context);
        this.context = context;
		handler = new Handler();

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DreamView,
                0, 0);

        try {
            isPreview = a.getBoolean(R.styleable.DreamView_isPreview, false);
        }
        finally {
            a.recycle();
        }

        if (isPreview) {
            loadPreviewSettiings();
        }
        else {
            loadPreferences();
        }

        if (shadowLayer) {
            Log.d(TAG, "Shadows enabled: Disabling hardware acceleration.");
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        if (isPreview) {
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        else {
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int action = event.getActionMasked();

                    float ff = g.screenScale;

                    switch (action) {

                        case MotionEvent.ACTION_DOWN:
                            xStart = (int) (event.getX() / ff);
                            yStart = (int) (event.getY() / ff);

                            fingerNotMoving = true;
                            fingerNotMoved = SystemClock.uptimeMillis();

                            return true;
                        case MotionEvent.ACTION_MOVE:
                            if (findDistanceBetweenPoints(
                                    new Point(xStart, yStart),
                                    new Point((int) (event.getX() / ff), (int) (event
                                            .getY() / ff))) > 50) {
                                fingerNotMoving = false;
                            }

                            return true;

                        case MotionEvent.ACTION_UP:
                            fingerNotMoving = false;
                            int distanceMoved = findDistanceBetweenPoints(new Point(
                                    xStart, yStart), new Point(
                                    (int) (event.getX() / ff),
                                    (int) (event.getY() / ff)));
                            // Create new System if tap event, ignore if swiping.
                            if (distanceMoved < 50) {
                                int touchType = 1;

                                touchType = Integer.valueOf(touchGeneration);

                                int allowance = maxEntities - entities.size();
                                if (allowance <= 0) {
                                    allowance = 1;
                                }

                                int n = (int) Math.floor(Math.random() * allowance) + 3;

                                switch (touchType) {
                                    case 0: // None
                                        break;
                                    case 1: // Any
                                        if (allowedSystems.size() == 0) {
                                            return false;
                                        }
                                        int r = allowedSystems.get((int) Math.floor(Math.random() * allowedSystems.size()));
                                        Log.d(TAG, "Allowed systems: " + allowedSystems.toString());
                                        Log.d(TAG, "Making touch system of type " + r);

                                        switch (r) {
                                            case RANDOM_SINGLE_SYSTEM:
                                                makeRandomSystem((int) xSize, (int) ySize, 1);
                                                break;
                                            case STAR_SYSTEM:
                                                if (allowStarSystems) {
                                                    makeStarSystem(
                                                            new Point(
                                                                    (int) ((event.getX()) / ff),
                                                                    (int) (event.getY() / ff)),
                                                            n);
                                                }
                                                break;
                                            case CELL_SYSTEM:
                                                if (allowCellSystems) {
                                                    makeCellularSystem(
                                                            new Point(
                                                                    (int) ((event.getX()) / ff),
                                                                    (int) (event.getY() / ff)),
                                                            n);
                                                }
                                                break;
                                            case RANDOM_SYSTEM:
                                                if (allowRandomSystems) {
                                                    makeRandomSystem((int) xSize, (int) ySize,
                                                            n);
                                                }
                                                break;
                                            case POLY_SYSTEM:
                                                if (allowPolySystems) {
                                                    int rad = (int) (Math.random() * 200) + 50;
                                                    makePolygonSystem(
                                                            new Point(
                                                                    (int) ((event.getX()) / ff),
                                                                    (int) (event.getY() / ff)),
                                                            rad, n);
                                                }
                                                break;
                                            default:
                                                makeRandomSystem((int) xSize, (int) ySize, 1);
                                                break;
                                        }
                                        break;
                                    case 2: // Cellular
                                        makeCellularSystem(
                                                new Point((int) ((event.getX()) / ff),
                                                        (int) (event.getY() / ff)), n);
                                        break;
                                    case 3: // Planetary
                                        makeStarSystem(
                                                new Point((int) ((event.getX()) / ff),
                                                        (int) (event.getY() / ff)), n);
                                        break;
                                    case 4: // Polygonal
                                        makePolygonSystem(
                                                new Point((int) (event.getX() / ff),
                                                        (int) (event.getY() / ff)), 100, n);
                                        break;
                                    case 5: // Random
                                        makeRandomSystem((int) xSize, (int) ySize, n);
                                        break;
                                    default:
                                        break;
                                }
                            } else if (distanceMoved > 50) {
                                int xSpeed = (int) (((event.getX() / ff) - xStart) / 50.0);
                                int ySpeed = (int) (((event.getY() / ff) - yStart) / 50.0);
                                makeParticleGun(new Point(xStart, yStart), 5, 10,
                                        xSpeed, ySpeed, 2,
                                        10 + (int) (Math.random() * 10));
                            }
                            return true;
                    }
                    return false;
                }
            });
        }
	}

    private void loadPreviewSettiings() {
        Log.d(TAG, "Loading preview settings");

        maxEntities = 15;
        frameRate = 45;

        objectSize = 0.75;

        Resources resources = getResources();
        backgroundColor = resources.getColor(R.color.Primary);
        objectColors = new String[] {whatColor(resources.getColor(R.color.Primary))};
        useBackgroundImage = false;
        colorAlpha = 80;
        darkColors = false;
        desaturateColors = true;

        gravityMultiplier = 1;
        collisionStyle = 2;
        showCentreOfMass = true;
        pathing = false;

        allowCellSystems = false;
        allowRandomSystems = false;
        allowStarSystems = true;
        allowPolySystems = false;
        allowParticleGuns = false;
        allowedSystems = new ArrayList<Integer>();
        allowedSystems.add(STAR_SYSTEM);

        enableOutlines = false;
        wireFrame = false;
        wireFrameWidth = 2;
        outlineColorCustom = false;
        outlineColor = 0;
        enableAccelerationArrows = false;
        enableTraceLines = false;
        traceLineLength = 10;
        touchGeneration = "3";
        antiGravity = false;
        showTime = false;
        showNotifications = false;

        entities.clear();
        removeEntities.clear();
        newEntities.clear();
        deadTraceLines.clear();
    }

	@SuppressLint("NewApi")
	public void loadPreferences(SharedPreferences settings) {
		maxEntities = settings.getInt("pref_performance_maxobjects", 25);
		frameRate = settings.getInt("pref_performance_framerate", 30);
        if (frameRate <= 0) {
            frameRate = 30;
        }

        try {
            dayDreamService.setScreenBright(settings.getBoolean(
                    "pref_daydream_bright", true));
            dayDreamService.setInteractive(settings.getBoolean(
                    "pref_daydream_interactive", true));
        }
        catch (Exception e) {
            Log.d(TAG, "Daydreamservice is not set.");
        }

		enableAccelerationArrows = settings.getBoolean("pref_render_accArrows",
				false);
		enableTraceLines = settings.getBoolean("pref_render_trace", true);
		traceLineLength = settings
				.getInt("pref_render_dialog_trace_length", 25);
		enableOutlines = settings.getBoolean("pref_render_outlines", true);
		wireFrame = settings.getBoolean("pref_render_wireframe", false);
		wireFrameWidth = settings.getInt("pref_render_wireframewidth", 2);
		objectSize = Double.valueOf(settings.getString("pref_render_size",
				"1.0"));

		allowCellSystems = settings.getBoolean("pref_systems_cell", true);
		allowRandomSystems = settings.getBoolean("pref_systems_random", true);
		allowStarSystems = settings.getBoolean("pref_systems_star", true);
		allowPolySystems = settings.getBoolean("pref_systems_poly", true);
		allowParticleGuns = settings.getBoolean("pref_systems_streamer", true);

        allowedSystems = new ArrayList<Integer>();
        if (allowCellSystems) allowedSystems.add(CELL_SYSTEM);
        if (allowRandomSystems) {
            allowedSystems.add(RANDOM_SINGLE_SYSTEM);
            allowedSystems.add(RANDOM_SYSTEM);
        }
        if (allowStarSystems) allowedSystems.add(STAR_SYSTEM);
        if (allowPolySystems) allowedSystems.add(POLY_SYSTEM);
        if (allowParticleGuns) allowedSystems.add(STREAMER_SYSTEM);

		touchGeneration = settings.getString("pref_systems_touch", "3");

		backgroundColor = settings.getInt("pref_color_background", Color.BLACK);
		if (backgroundColor == 0) {
			backgroundColor = Color.BLACK;
		}

        useBackgroundImage = settings.getBoolean("pref_color_background_image", false);

		objectColors = settings.getStringSet("pref_color_objectcolors",
				new HashSet<String>()).toArray(new String[] {});

		colorAlpha = settings.getInt("pref_color_alpha", 255);
		darkColors = settings.getBoolean("pref_color_dark", false);
		desaturateColors = settings.getBoolean("pref_color_desaturate", false);
		outlineColorCustom = settings.getBoolean("pref_outlines_custom_color", false);
		outlineColor = settings.getInt("pref_color_outline", 0);

		gravityMultiplier = settings.getInt("pref_physics_gravity", 10);
		antiGravity = settings.getBoolean("pref_physics_antigravity", false);
		collisionStyle = Integer.valueOf(settings.getString(
				"pref_physics_collision", "1"));
		mPhysicsBorderBounce = settings.getBoolean("pref_physics_border_bounce", false);

		showCentreOfMass = settings.getBoolean(
				"pref_experimental_centreofmass", false);
		pixelate = settings.getBoolean("pref_experimental_pixelate", false);
		zAxis = settings.getBoolean("pref_experimental_3d", false);
        shadowLayer = settings.getBoolean("pref_experimental_shadow", false);
        pathing = settings.getBoolean("pref_experimental_drip", false);

        showTime = settings.getBoolean("pref_time_show", false);
        hhmm = settings.getBoolean("pref_time_24hr", true);
        timeColor = settings.getInt("pref_time_color", Color.WHITE);
        timeOutline = settings.getBoolean("pref_time_outline", false);
        timeSize = Double.valueOf(settings.getString("pref_time_size", "1.0"));
        timeAlpha = 0;
		timeRotation = Integer.valueOf(settings.getString("pref_time_rotation", "0"));

        showNotifications = settings.getBoolean("pref_notify_pulse", false);
        colorDelta = Integer.valueOf(settings.getString("pref_notify_speed", "3"));
        if (showNotifications) {
            initPulseTiming();
        }

		entities.clear();
		removeEntities.clear();
		newEntities.clear();
		deadTraceLines.clear();

		Log.i(TAG, "LOADED PREFERENCES");
	}

	public int findDistanceBetweenPoints(Point p1, Point p2) {
		int x1 = p1.x;
		int y1 = p1.y;
		int x2 = p2.x;
		int y2 = p2.y;

		int r = (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

		return r;
	}

	public double getGravity() {
		if (gravityMultiplier <= 10) {
			return G * (gravityMultiplier * 0.1);
		} else {
			return G * (gravityMultiplier - 9.0);
		}
	}

	public boolean isZAxisEnabled() {
		return zAxis;
	}

	public boolean getAntiGravity() {
		return antiGravity;
	}

	public int getCollisionStyle() {
		return collisionStyle;
	}

	public double getObjectSize() {
		return objectSize;
	}

	public void setTraceLineLength(int n) {
		traceLineLength = n;
	}

	public int getTraceLineLength() {
		return traceLineLength;
	}

	public void addDreamEntity(DreamEntity e) {
		newEntities.add(e);
	}

	public void addDeadTracePoints(Queue<Point> dtp) {
		deadTraceLines.addAll(dtp);
	}

	public void removeDeadMass() {
		ListIterator<DreamEntity> iterator = removeEntities.listIterator();

		while (iterator.hasNext()) {
			DreamEntity e = iterator.next();
			totalMass -= e.getMass();
		}
	}

	public int makeRandomColor() {
        if (pulse) {
            return pulseColor;
        }
        else {
            int r = (int) Math.round(Math.random() * 255);
            int g = (int) Math.round(Math.random() * 255);
            int b = (int) Math.round(Math.random() * 255);

            int c = betterColor(r, g, b);

            return c;
        }
	}

	public int betterColor(int c) {
		return betterColor(Color.red(c), Color.green(c), Color.blue(c));
	}

	public int betterColor(int r, int g, int b) {
		float[] hsv = new float[3];

		Color.RGBToHSV(r, g, b, hsv);

		int colorFilter = 0;

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			if (objectColors.length >= 1) {
				colorFilter = Integer.valueOf(objectColors[((int) Math
						.floor(Math.random() * objectColors.length))]);
			} else {
				colorFilter = 0;
			}
		} else {
			colorFilter = Integer.valueOf(colorTheme);
		}

		// HUE
		switch (colorFilter) {
		case 0: // any color
			break;
		case 1: // greyscale
			break;
		case 2: // reddish
			hsv[0] = (float) (((hsv[0] / 360f * 20f) + 350f) % 360f);
			break;
		case 3: // orangish
			hsv[0] = (float) ((hsv[0] / 360f * 20f) + 15f);
			break;
		case 4: // yellowish
			hsv[0] = (float) ((hsv[0] / 360f * 15f) + 45f);
			break;
		case 5: // greenish
			hsv[0] = (float) ((hsv[0] / 360f * 80f) + 70f);
			break;
		case 6: // blueish
			hsv[0] = (float) ((hsv[0] / 360f * 50f) + 200);
			break;
		case 7: // purplish
			hsv[0] = (float) ((hsv[0] / 360f * 25f) + 260f);
			break;
		case 8: // pinkish
			hsv[0] = (float) (((hsv[0] / 360f * 80f) + 290f) % 360f);
			break;
		default:
			break;
		}

		// SATURATION
		if (colorFilter == 1) {
			hsv[1] = 0; // greyscale
		} else if (desaturateColors) {
			hsv[1] = (float) (Math.random() * 0.1f + 0.3f);
		} else if (colorFilter == 2) { // reddish
			hsv[1] = (float) (Math.random() * 0.2f + 0.8f);
		} else if (colorFilter == 8) { // pinkish
			hsv[1] = (float) (Math.random() * 0.25f + 0.35f);
		} else if (colorFilter == 4) { // yellowish
			hsv[1] = (float) (Math.random() * 0.2f + 0.8f);
		} else if (hsv[1] < 0.5) {
			hsv[1] = (float) (Math.random() * 0.6f + 0.4f);
		}

		// Value (~Brightness)
		if (darkColors) {
			hsv[2] = (float) (Math.random() * 0.35f + 0.15f);
		} else if (colorFilter == 1) { // Allow darker shades when greyscale
			hsv[2] = (float) (Math.random() * 0.85f + 0.15f);
		} else if (hsv[2] < 0.5f) {
			hsv[2] = (float) (Math.random() * 0.5f + 0.5f);
		}

		int col = Color.HSVToColor(hsv);

		r = Color.red(col);
		g = Color.green(col);
		b = Color.blue(col);

		int c = Color.argb(255, r, g, b);

		return c;
	}

    public String whatColor(int c) {
        return whatColor(Color.red(c), Color.green(c), Color.blue(c));
    }

    public String whatColor(int r, int g, int b) {
        float[] hsv = new float[3];
        int result;

        Color.RGBToHSV(r, g, b, hsv);

        if (hsv[1] == 0) {
            // GREY
            result = 1;
        }
        else {
            float hue = hsv[0];

            if (hue <= 14f) {
                // RED
                result = 2;
            }
            else if (hue <= 33f) {
                // ORANGE
                result = 3;
            }
            else if (hue <= 70f) {
                // YELLOW
                result = 4;
            }
            else if (hue <= 180) {
                // GREEN
                result = 5;
            }
            else if (hue <= 260f) {
                // BLUE
                result = 6;
            }
            else if (hue <= 300f) {
                // PURPLE
                result = 7;
            }
            else if (hue <= 360f) {
                // PINK
                result = 8;
            }
            else {
                // who knows?
                result = 0;
            }
        }

        Log.d(TAG, "This colour is kinda " + getResources().getStringArray(R.array.pref_color_color_entries)[result] + " (hue = " + hsv[0] + ")");

        return String.valueOf(result);
    }

	public void makeStarSystem(Point centre, int n) {
		if (n >= 1) {
			int dx = 0;
			int dy = 0;
			int size = 20 + (int) (Math.random() * 40);

			// Make central star.
			DreamEntity centralStar = new DreamEntity(g, centre.x, centre.y,
					dx, dy, size, 10000 + (int) (Math.random() * 200),
					makeRandomColor());

			entities.add(centralStar);

			for (int i = 1; i < n; i++) {
				int m = 500 + (int) Math.round(Math.random() * 2000);
				int rad = (int) (Math.round(2 + Math.random() * 15) + (m / 1000));
				int d = (int) (50 + (int) Math.round(Math.random() * 300));
				double speed = getOrbitalSpeed(m, 10000, d);

				int q = (int) Math.round(Math.random() * 3);
				if (q == 0) {
					entities.add(new DreamEntity(g, (int) centralStar.getX(),
							(int) centralStar.getY() - d, speed + dx, dy, rad,
							m, makeRandomColor()));
				} else if (q == 1) {
					entities.add(new DreamEntity(g, (int) centralStar.getX()
							+ d, (int) centralStar.getY(), dx, speed + dy, rad,
							m, makeRandomColor()));
				} else if (q == 2) {
					entities.add(new DreamEntity(g, (int) centralStar.getX(),
							(int) centralStar.getY() + d, -speed + dx, dy, rad,
							m, makeRandomColor()));
				} else if (q == 3) {
					entities.add(new DreamEntity(g, (int) centralStar.getX()
							- d, (int) centralStar.getY(), dx, -speed + dy,
							rad, m, makeRandomColor()));
				}
			}
		}
	}

	public double getOrbitalSpeed(double m1, double m2, int r) {
		// m1 = mass of orbiting object
		// m2 = mass of object being orbited
		double v = Math.sqrt(((m2 * m2) * getGravity()) / ((m1 + m2) * r));
		return v;
	}

	public void makePolygonSystem(Point centre, int r, int n) {

		if (n < 1) {
			n = 1;
		}
		double angle = (Math.PI * 2) / n;
		double orbitalSpeed = getOrbitalSpeed(500, (n - 1) * 500, r);

		for (int i = 1; i <= n; i++) {

			// Calculate polygon points
			int x1 = (int) (centre.x + r * Math.cos(2 * Math.PI * i / n));
			int y1 = (int) (centre.y + r * Math.sin(2 * Math.PI * i / n));

			// SORT OF KIND OF WORKS BUT NOT REALLY
			double dx = orbitalSpeed
					* Math.cos((i * angle) + Math.toRadians(90));
			double dy = orbitalSpeed
					* Math.sin((i * angle) + Math.toRadians(90));

			DreamEntity newEntity = new DreamEntity(g, x1, y1, (int) dx,
					(int) dy, (int) 8, (int) 500, makeRandomColor());

			entities.add(newEntity);
		}
	}

	public void makeRandomSystem(int boundX, int boundY, int n) {

		if (n >= 1) {

			for (int i = 1; i < n; i++) {

				int m = (int) (50 + Math.random() * 2000); // random mass, 50 <
															// m <
															// 2050

				int rad = (int) (Math.round(2 + Math.random() * 9) + (m / 1000)); // random
				// size, 1 <
				// rad < 10

				int x = (int) Math.round(Math.random() * boundX); // random
																	// x

				int y = (int) Math.round(Math.random() * boundY); // random
																	// y

				float dx = (float) (Math.random() * 5); // random horizontal
														// speed, 0 <
				// dx < 5

				float dy = (float) (Math.random() * 5); // random vertical
														// speed, 0 < dy
				// < 5

				float a = (float) Math.random();

				float b = (float) Math.random();

				if (a > 0.5)
					dx = -dx;

				if (b > 0.5)
					dy = -dy;

				entities.add(new DreamEntity(g, x, y, dx, dy, rad, m,
						makeRandomColor()));
			}
		}
	}

	public void makeCellularSystem(Point centre, int n) {
		Queue<Point> nodes = new LinkedList<Point>();
		int total = 2;
		int len = 120;
		int r = 12;
		int m = 500;

		int dx = (int) (Math.random() * 2);
		int dy = (int) (Math.random() * 2);

		double random = Math.random();
		if (random < 0.25)
			dx = -dx;
		if (random > 0.75)
			dy = -dy;

		Point a = centre;
		Point b = new Point(a.x + len, a.y);
		Point c = new Point();

		nodes.add(b);

		entities.add(new DreamEntity(g, a.x, a.y, dx, dy, r, m,
				makeRandomColor()));

		if (n >= 2) {
			entities.add(new DreamEntity(g, b.x, b.y, dx, dy, r, m,
					makeRandomColor()));
		}

		if (n >= 3) {

			while (total < n) {
				c = getThirdPoint(a, b);

				if (nodes.contains(c)) {
					a = nodes.remove();
				}

				else {
					nodes.add(c);
					b = c;

					entities.add(new DreamEntity(g, b.x, b.y, dx, dy, r, m,
							makeRandomColor()));

					total++;
				}
			}
		}
	}

	public Point getThirdPoint(Point a, Point b) {
		double cos60 = 0.5;
		double sin60 = Math.sqrt(3) / 2;
		double lenx = b.x - a.x;
		double leny = b.y - a.y;

		Point c = new Point();

		c.x = (int) Math.round((a.x + ((lenx * cos60) + (leny * sin60))));
		c.y = (int) Math.round((a.y + ((leny * cos60) - (lenx * sin60))));

		return c;
	}

	// x, y are the canvas width, height
	public void addRandomEntities(int x, int y) {
		//int r = isPreview ? 1 : (int) Math.floor(Math.random() * 6);
        if (allowedSystems.size() == 0) {
            return;
        }
        int r = isPreview ? 1 : allowedSystems.get((int) Math.floor(Math.random() * allowedSystems.size()));
		int rX = 0;
		int rY = 0;

		int allowance = maxEntities - entities.size();
		if (allowance <= 0) {
			allowance = 1;
		}

		int n = (int) Math.floor(Math.random() * allowance);

		switch (r) {
		case RANDOM_SINGLE_SYSTEM:
			makeRandomSystem(x, y, 1);
			break;
		case STAR_SYSTEM:
			if (allowStarSystems) {
				rX = (int) (Math.random() * x);
				rY = (int) (Math.random() * y);

				makeStarSystem(new Point(rX, rY), n);
			}
            break;
		case CELL_SYSTEM:
			if (allowCellSystems) {
				rX = (int) (Math.random() * x);
				rY = (int) (Math.random() * y);

				makeCellularSystem(new Point(rX, rY), n);
			}
            break;
		case RANDOM_SYSTEM:
			if (allowRandomSystems) {
				makeRandomSystem(x, y, n);
			}
            break;
		case POLY_SYSTEM:
			if (allowPolySystems) {
				rX = (int) (Math.random() * x);
				rY = (int) (Math.random() * y);

				int rad = (int) (Math.random() * 200) + 50;

				makePolygonSystem(new Point(rX, rY), rad, n);
			}
            break;
		case STREAMER_SYSTEM:
			if (allowParticleGuns) {
				rX = (int) (Math.random() * x);
				rY = (int) (Math.random() * y);

				int dx = 2 + (int) (Math.random() * 8);
				int dy = 2 + (int) (Math.random() * 8);

				dx = Math.random() < 0.5 ? dx : -dx;
				dy = Math.random() < 0.5 ? dy : -dy;

				makeParticleGun(new Point(rX, rY), 5, 10, dx, dy, 2,
						(int) (Math.random() * allowance));
			}
            break;
		default:
            break;
		}
	}

	public void makeParticleGun(Point point, int rate, int size, int xSpeed,
			int ySpeed, int spread, int ttl) {
		// ParticleGun(g, point, rate, size, xSpeed, ySpeed, spread, ttl)
        DreamParticleGun gun = new DreamParticleGun(g, point, rate, size, xSpeed, ySpeed, spread, ttl);
        gun.allowAsteroids(allowAsteroids);

		dreamParticleGuns.add(gun);
	}
	
	public void checkOutOfBounds(DreamEntity e, int boundX, int boundY) {
		Point position = e.getPosition();

		int pX = position.x;
		int pY = position.y;

		if ((pX > boundX) || (pX < -boundX) || (pY > boundY) || (pY < -boundY)) {
			removeEntities.add(e);
		}
	}

	public void explodeDreamEntity(DreamEntity e) {
		double dx, dy; // Explosion velocity vectors
		double escape = Math.sqrt((2 * getGravity() * e.getMass()) / 10);
		double radius, mass; // Physical attributes
        boolean isFirework = e.wereWeExplodingAnyway();

		int allowance = maxEntities - entities.size();
		if (allowance <= 1)
			allowance = 2;

		int n = isFirework ? 20 + (int) (Math.random() * 20.0)  : (int) (Math.floor(Math.random() * allowance) + 4);
		mass = e.getMass() / n;
		double area = (Math.PI * Math.pow(isFirework ? e.getRealRadius() : e.getRadius(), 2)) / n;
		radius = Math.sqrt(area / Math.PI);

		// Particle separation in radians
		double angle = Math.toRadians((360 / n) + (Math.random() * 360 / n));

		for (int i = 0; i < n; i++) {
			// Particle x, y velocities
			dx = escape * Math.cos(i * angle);
			dy = escape * Math.sin(i * angle);

			DreamEntity newDreamEntity = new DreamEntity(g, (int) e.getX(),
					(int) e.getY(), dx, dy, (int) radius, (int) mass,
					makeRandomColor());

            if (isFirework) {
                newDreamEntity.setTTL(50);
            }
			newDreamEntity.allowCollisions(false);

			newEntities.add(newDreamEntity); // Put in buffer to be added to
												// main
			// list at end of current animation
			// cycle.
		}

		e.setDestroy(true);
		e.setRadius(0);
		e.setMass(1.0);
	}

	public int getObjectAlpha() {
		return colorAlpha;
	}

    private String getTime() {
        Calendar c = Calendar.getInstance();
//        SimpleDateFormat format = new SimpleDateFormat(hhmm ? "HH:mm" : "K:mma");
//        String t = format.format(c.getTime());

        //String t = (hhmm ? c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR)) + ":" + c.get(Calendar.MINUTE);
        int hour = c.get( hhmm ? Calendar.HOUR_OF_DAY : Calendar.HOUR);
        if (!hhmm && hour == 0) {
            hour = 12;
        }
        int minute = c.get(Calendar.MINUTE);
        String t = hour + ":" + minute;

//        String t = DateFormat.format(hhmm ? "HH:mm" : "K:mm", c).toString();
        return t;
    }

	public void pixelate(Canvas c, DreamEntity e, Paint p) {
		int pixelSize = (int) (xSize / 70);
		int gridOffsetX = (int) (e.getX() % pixelSize);
		int gridOffsetY = (int) (e.getY() % pixelSize);
		int r = (int) e.getRadius();
		int gridSize = (int) (r * 2);
		Point gridOrigin = new Point(
				(int) (e.getX() - e.getRadius() - gridOffsetX), (int) (e.getY()
						- e.getRadius() - gridOffsetY));

		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (findDistanceBetweenPoints(new Point(gridOrigin.x
						+ (i * pixelSize), gridOrigin.y + (j * pixelSize)),
						new Point((int) e.getX() + gridOffsetX, (int) e.getY()
								+ gridOffsetY)) < r) {
					float left = gridOrigin.x + (i * pixelSize);
					float right = left + pixelSize;
					float top = gridOrigin.y + (j * pixelSize);
					float bottom = top + pixelSize;
					c.drawRect(left * screenScale, top * screenScale, right
							* screenScale, bottom * screenScale, p);
				}
			}
		}
	}

	public int getBackgroundColor() {

		int r1, g1, b1, r2, g2, b2, col, difference;

		difference = 10;
		col = backgroundColor;

		r1 = Color.red(backgroundRenderColor);
		r2 = Color.red(col);

		if (r1 > r2) {
			r1 -= (r1 - r2) / difference;
		} else if (r1 < r2) {
			r1 += (r2 - r1) / difference;
		}

		g1 = Color.green(backgroundRenderColor);
		g2 = Color.green(col);

		if (g1 > g2) {
			g1 -= (g1 - g2) / difference;
		} else if (g1 < g2) {
			g1 += (g2 - g1) / difference;
		}

		b1 = Color.blue(backgroundRenderColor);
		b2 = Color.blue(col);

		if (b1 > b2) {
			b1 -= (b1 - b2) / difference;
		} else if (b1 < b2) {
			b1 += (b2 - b1) / difference;
		}

		backgroundRenderColor = Color.rgb(r1, g1, b1);

		return backgroundRenderColor;
	}

    private void updatePulseColor() {
        String filename = FILENAME;
        String input = "";
        List<String> subs = new ArrayList<String>();

        try {
            FileInputStream fis = getContext().openFileInput(filename);
            InputStreamReader isReader = new InputStreamReader(fis);
            BufferedReader bf = new BufferedReader(isReader);
            String line;
            while ((line = bf.readLine()) != null) {
                if (!line.equals("")) {
                    subs.add(line);
                }
            }
            isReader.close();
        }
        catch (Exception e) {
            Log.e(TAG, "Error getting subs: " + e.toString());
        }

        if (subs.size() > 0) {
            if (pulseIndex > subs.size() - 1) {
                pulseIndex = 0;
            }
            try {
                String s = subs.get(pulseIndex);
                pulseColor = Integer.valueOf(s.substring(s.indexOf(";") + 1));
                Log.d(TAG, "Pulse index: " + pulseIndex + ". App: " + s + ". Pulse Color: " + pulseColor);
                pulseIndex++;
            } catch (Exception e) {
                Log.e(TAG, "Pulse index of bounds: " + e.toString());
                pulseIndex = 0;
            }
        }
        else {
            // If no subs are found, skip this cycle.
            Log.d(TAG, "No subscribed notifications active. Skipping this round.");
            pulseColor = INVALID_COLOR;
            pulse = false;
        }
    }

    private void initPulseTiming() {
        switch (colorDelta) {
            case 0:
                pulseLength = 150;
                notificationPulseDelay = 240;
                break;
            case 1:
                pulseLength = 100;
                notificationPulseDelay = 160;
                break;
            case 2:
                pulseLength = 50;
                notificationPulseDelay = 80;
                break;
            default:
                pulseLength = 150;
                notificationPulseDelay = 240;
                break;
        }
        /*pulseLength = 250 - (colorDelta * 25);
        notificationPulseDelay = (int) (pulseLength * 2);*/
        Log.d(TAG, "colorDelta = " + colorDelta + "; pulseLength = " + pulseLength + "; notificationPulseDelay = " + notificationPulseDelay);
    }

	public float getScreenScale() {
		return screenScale;
	}

	public void setScreenScale() {
		if ((xSize) > (ySize)) {
			g.screenScale = xSize / 1280;
		} else {
			g.screenScale = xSize / 768;
		}
	}

	public void setScreenScale(Canvas c) {
		xSize = c.getWidth();
		ySize = c.getHeight();

		float xScreen = xSize / 768;
		float yScreen = ySize / 1280;

		if ((xScreen) > (yScreen)) {
			g.screenScale = xScreen;
		} else {
			g.screenScale = yScreen;
		}
	}

	public boolean getBorderBounce() {
		return mPhysicsBorderBounce;
	}

	public void stop() {
		handler.removeCallbacks(runnable);
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			invalidate();
		}
	};

	// Animation loop
	@Override
	protected void onDraw(Canvas c) {
		
		// Used for centre of mass mode
		int xCentreOfMass = 0;
		int yCentreOfMass = 0;
		double translateX = 0.0, translateY = 0.0;
		totalMass = 0.0;

        if (pathing) {
            if (bitmap == null) {
                bitmap = Bitmap.createBitmap(c.getWidth(), c.getHeight(), Bitmap.Config.ARGB_8888);
            }

            if (pathingCanvas == null) {
                pathingCanvas = new Canvas(bitmap);
                pathingPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
                pathingPaint.setColor(getBackgroundColor());
            }
        }

		super.onDraw(c);

        if (delay > 240) {
            /*if (c.getWidth() > c.getHeight()) {
                Log.d(TAG, "LANDSCAPE");
            }*/
            delay = 0;
        }
        else {
            delay ++;
        }

        Paint p = new Paint();
        p.setAntiAlias(true);

        if (((int) xSize | (int) ySize) == 0) {
            setScreenScale(c);
        }

		c.drawColor(getBackgroundColor());

        if (useBackgroundImage) {
            if (backgroundImage == null) {
                if (!loadingBackground) {
                    Uri uri = Uri.parse("file://" + getContext().getCacheDir() + File.separator + "dream_bg_image");
                    try {
                        new LoadBackgroundImage().execute(uri);
                        loadingBackground = true;
                    }
                    catch (Exception e) {
                        Log.e(TAG, "Couldn't load background image: " + e.toString());
                        useBackgroundImage = false;
                        loadingBackground = false;
                    }
                }
                else if (backgroundImage != null) {
                    c.drawBitmap(backgroundImage, c.getWidth() / 2 - backgroundImageSize.x / 2, c.getHeight() / 2 - backgroundImageSize.y / 2, p);
                }
            }
            else {
                if (backgroundScale == 0f) {
                    backgroundScale = 1f;
                    int xDifference = 0;
                    int yDifference = 0;

                    if (backgroundImage.getWidth() < c.getWidth()) {
                        xDifference = c.getWidth() - backgroundImage.getWidth();
                    }
                    if (backgroundImage.getHeight() < c.getHeight()) {
                        yDifference = c.getHeight() - backgroundImage.getHeight();
                    }

                    int maxDifference = Math.max(xDifference, yDifference);
                    if (maxDifference != 0) {
                        if (maxDifference == xDifference) {
                            backgroundScale = (float) c.getWidth() / (float) backgroundImage.getWidth();
                        } else if (maxDifference == yDifference) {
                            backgroundScale = (float) c.getHeight() / (float) backgroundImage.getHeight();
                        }

                        Log.d(TAG, "Scaling background by " + backgroundScale);

                        backgroundImage = Bitmap.createScaledBitmap(backgroundImage, (int) (backgroundImage.getWidth() * backgroundScale), (int) (backgroundImage.getHeight() * backgroundScale), false);
                        backgroundImageSize.x = backgroundImage.getWidth();
                        backgroundImageSize.y = backgroundImage.getHeight();
                    }
                }
                c.drawBitmap(backgroundImage, c.getWidth() / 2 - backgroundImageSize.x / 2, c.getHeight() / 2 - backgroundImageSize.y / 2, p);
            }
        }

        if (pathing) {
            c.drawBitmap(bitmap, 0, 0, pathingPaint);
        }

		// exit daydream if finger hovers
		if (fingerNotMoving) {
			if (SystemClock.uptimeMillis() - fingerNotMoved > 500) {
				fingerNotMoving = false;
				fingerNotMoved = 0;
				dayDreamService.finish();
			}
		}

		float ff = g.getScreenScale();

		if (showCentreOfMass) {
            // 2.0 to center, anything else to offset
            double dividerX = isPreview ? 1.33 : 2.0;
            double dividerY = isPreview ? 3.0 : 2.0;

			if (centreOfMass.x > (xSize / dividerX)) { // shift right
				translateX = -(centreOfMass.x - (xSize / dividerX)) / 10;
			} else if (centreOfMass.x < (xSize / dividerX)) { // shift left
				translateX = ((xSize / dividerX) - centreOfMass.x) / 10;
			} else { // stay
				translateX = 0;
			}
			
			if (translateX > 10) {
				translateX = 10;
			}
			else if (translateX < -10) {
				translateX = -10;
			}

			if (centreOfMass.y > (ySize / dividerY)) { // shift down
				translateY = -(centreOfMass.y - (ySize / dividerY)) / 10;
			} else if (centreOfMass.y < (ySize / dividerY)) { // shift up
				translateY = ((ySize / dividerY) - centreOfMass.y) / 10;
			} else { // stay
				translateY = 0;
			}
			
			if (translateY > 10) {
				translateY = 10;
			}
			else if (translateY < -10) {
				translateY = -10;
			}

            if (isPreview) {
                translateX = translateX > 1 ? 1 : translateX;
                translateX = translateX < -1 ? -1 : translateX;
                translateY = translateY > 1 ? 1 : translateY;
                translateY = translateY < -1 ? -1 : translateY;
            }

			ListIterator<DreamEntity> iterator = entities.listIterator();

			while (iterator.hasNext()) {
				// Cycle through list of entities.
				DreamEntity e = iterator.next();

				e.setX(e.getX() + translateX);
				e.setY(e.getY() + translateY);
			}
		}

        if (showNotifications) {
            if (pulse) {
                if (pulseTimer >= pulseLength) {
                    pulse = false;
                    pulseTimer = 0;
                    assignNewColors = true;
                }
                else {
                    pulseTimer++;
                    assignNewColors = false;
                }
            }
            else {
                if (pulseTimer >= notificationPulseDelay) {
                    pulse = true;
                    pulseTimer = 0;
                    assignNewColors = true;
                    updatePulseColor();
                }
                else {
                    pulseTimer++;
                    assignNewColors = false;
                }
            }
        }

		if (allowSystemGeneration) {
			if (entities.size() < maxEntities) {
                if (isPreview) {
                    addRandomEntities(c.getWidth(), c.getHeight());
                }
				else if (Math.random() > 0.95) {
					addRandomEntities(c.getWidth(), c.getHeight());
				}
			}
		}

		// if (allowParticleGuns) {
		{
			ListIterator<DreamParticleGun> iterator = dreamParticleGuns.listIterator();

			while (iterator.hasNext()) {
				// Cycle through list of entities.
				DreamParticleGun gun = iterator.next();
				gun.shoot();
                gun.move();
				if (gun.ttl <= 0) {
					removeDreamParticleGuns.add(gun);
				}
			}
		}
		//}

		if (enableTraceLines) {
			float fft = ff;
			if (fft < 1f)
				fft = 0.5f;

			while (deadTraceLines.size() > 150) {
				deadTraceLines.remove();
			}
			if (deadTraceLines.size() > 0) {
				Iterator<Point> deadTraceIterator = deadTraceLines.iterator();
				p.setColor(Color.argb(getObjectAlpha(), 50, 50, 50));

				while (deadTraceIterator.hasNext()) {
					Point deadPoint = deadTraceIterator.next();

                    deadPoint.x += translateX;
                    deadPoint.y += translateY;

					c.drawCircle((deadPoint.x) * ff, deadPoint.y * ff,
							(int) (2 * fft), p);
				}
			}

			ListIterator<DreamEntity> iterator = entities.listIterator();

			while (iterator.hasNext()) {

				// Cycle through list of entities.

				DreamEntity e = iterator.next();

				p.setColor(e.getColor());
				p.setAlpha(getObjectAlpha());

				e.doTrace();

				Iterator<Point> traceIterator = e.getTrace().iterator();

				while (traceIterator.hasNext()) {

					Point point = traceIterator.next();
                    point.x += translateX;
                    point.y += translateY;

					c.drawCircle((point.x) * ff, point.y * ff, (int) (2 * fft),
							p);

				}
			}
		}

		// Update logic and draw entities to canvas
		ListIterator<DreamEntity> iterator = entities.listIterator();

		while (iterator.hasNext()) {

			// Cycle through list of entities.

			DreamEntity e = iterator.next();
			totalMass += e.getMass();

			// Check if DreamEntity is too far off the screen
			checkOutOfBounds(e, (int) (c.getWidth() * 1.2 / ff),
					(int) (c.getHeight() * 1.2 / ff));

			// Check mass,size and destroy if too large.

			if ((e.getMass() > (70000.0 * objectSize))
					|| (e.getRadius() > (65.0 * objectSize))) {
				explodeDreamEntity(e);
			}

			// Linear movement.

			e.updateLogic();

            // Assign new color if necessary
            if (assignNewColors) {
                if (pulse) {
                    e.setColor(pulseColor);
                }
                else {
                    if (pulseColor != INVALID_COLOR) {
                        e.setColor(makeRandomColor());
                    }
                }
            }

			// Calculate net direction and force of gravity.
			for (DreamEntity o : entities) {
				if (o != e) {
					e.doGravity(o);

					if (e.allowCollisions()) {
						if (e.detectCollision(o)) {
							e.doCollision(o);
							if (o.getDestroy()) {
								removeEntities.add(o);
							}
						}
					}
				}
			}

			if (enableAccelerationArrows) {

				p.setColor(Color.argb(getObjectAlpha(), 255, 255, 255));

				Point point = e.doAccelerationArrows();
				p.setStrokeCap(Paint.Cap.ROUND);
				p.setStrokeWidth(3);

				c.drawLine((int) (e.getX()) * ff, (int) e.getY() * ff,
						(point.x) * ff, point.y * ff, p);
			}

            if (shadowLayer) {
                p.setShadowLayer(8f, 6f, 6f, Color.parseColor("#44000000"));
            }

			// Draw DreamEntity to canvas.
            if (pathing) {
                e.plot(ff);
                pathingCanvas.drawPath(e.path, e.getPaint());
                e.resetPath();
            }
            else {
                if (pixelate) {
                    p.setColor(e.getColor());
                    p.setStyle(Paint.Style.FILL);
                    p.setAlpha(getObjectAlpha());

                    pixelate(c, e, p);

                    // show outline for debugging
                    // p.setColor(e.getColor());
                    // p.setStyle(Paint.Style.STROKE);
                    // p.setStrokeWidth(wireFrameWidth);
                    // p.setAlpha(30);

                    // c.drawCircle((float) (e.getX())
                    // * ff, (float) e.getY() * ff,
                    // (float) e.getRadius() * ff, p);
                }
                else if (wireFrame) {
                    p.setColor(e.getColor());
                    p.setStyle(Paint.Style.STROKE);
                    p.setStrokeWidth(wireFrameWidth);
                    p.setAlpha(getObjectAlpha());

                    c.drawCircle((float) (e.getX()) * ff, (float) e.getY() * ff,
                            (float) e.getRadius() * ff, p);
                }
                else {
                    p.setColor(e.getColor());

                    //p.setStyle(e.getPaint().getStyle());
                    p.setStyle(Paint.Style.FILL);
                    p.setAlpha(getObjectAlpha());

                    c.drawCircle((float) (e.getX()) * ff, (float) e.getY() * ff,
                            (float) e.getRadius() * ff, p);

                    if (enableOutlines) {
                        if (outlineColorCustom) {
                            p.setColor(outlineColor);
                        } else {
                            p.setColor(backgroundRenderColor);
                        }
                        p.setStyle(Style.STROKE);
                        p.setStrokeWidth(wireFrameWidth);
                        p.setAlpha(getObjectAlpha());

                        c.drawCircle((float) (e.getX()) * ff,
                                (float) e.getY() * ff, (float) e.getRadius() * ff,
                                p);
                    }
                }
            }

            if (shadowLayer) {
                p.clearShadowLayer();
            }

            // Display current time.
            if (showTime) {
                p.setColor(timeColor);
                p.setStyle((timeOutline ? Style.STROKE : Style.FILL ));
                p.setStrokeWidth(wireFrameWidth);

                if (timeAlpha < 540) {
                    p.setAlpha(timeAlpha / 3);
                    timeAlpha++;
                }
                else {
                    p.setAlpha(180);
                }

                p.setTextSize((float) (timeSize * 255f));
                String t = getTime();
                if (increaseTimeOffset) {
                    timeOffset += 0.01f;
					if (timeOffset >= p.getTextSize()) {
						increaseTimeOffset = false;
					}
                }
                else {
                    timeOffset -= 0.01f;
					if (timeOffset <= 0) {
						increaseTimeOffset = true;
					}
                }

				// Force clock timeRotation (relative to device orientation)
				c.save();
				c.rotate(timeRotation, c.getWidth() / 2, c.getHeight() / 2);
				c.drawText(t, (c.getWidth() - p.measureText(t)) / 2f, (c.getHeight() / 2f) + timeOffset, p);
				c.restore();
            }

            // Show fps
            /*{
                if (lastFrame == 0l) {
                    lastFrame = System.nanoTime();
                }
                else {
                    p.setStyle(Style.FILL);

                    p.setColor(Color.BLACK);
                    c.drawRect(10f, 10f, 150, 80f, p);

                    p.setColor(Color.YELLOW);
                    p.setStrokeWidth(1);
                    p.setTextSize(48f);
                    long current = System.nanoTime();
                    float difference = current - lastFrame;
                    if (difference == 0f) {
                        difference = 1f;
                    }
                    String fps = (float) (1000000f / (float) (difference)) + "fps";
                    c.drawText(fps, 15f, 50f, p);
                    lastFrame = current;
                }
            }*/

            /*if (isPreview) {
                int overlay = getBackgroundColor();
                overlay = Color.argb(120, Color.red(overlay), Color.green(overlay), Color.blue(overlay));
                c.drawColor(overlay);
            }*/


			if (e.getDestroy()) {
				if (e.getRadius() <= 0) {
					removeEntities.add(e);
				}
			}

			if (showCentreOfMass) {
				xCentreOfMass += e.getMass() * e.getX();
				yCentreOfMass += e.getMass() * e.getY();
			}
		} // ENTITY LOOP ENDS HERE

        if (pathing) { // Draw thin layer of background color so paths gradually fade away
            if (pathingOverlayDelay > 5) {
                pathingPaint.setAlpha(1);
                pathingCanvas.drawColor(pathingPaint.getColor());
                pathingPaint.setAlpha(255);
                pathingOverlayDelay = 0;
            }
            else {
                pathingOverlayDelay++;
            }
        }

		if (showCentreOfMass) {
			centreOfMass.x = (int) (xCentreOfMass / totalMass * ff);
			centreOfMass.y = (int) (yCentreOfMass / totalMass * ff);

            /*p.setColor(Color.GREEN);
            c.drawCircle(centreOfMass.x, centreOfMass.y, 5, p);*/

			removeDeadMass();
		}

		entities.removeAll(removeEntities);
		entities.addAll(newEntities);
		dreamParticleGuns.removeAll(removeDreamParticleGuns);
		newEntities.clear();
		removeEntities.clear();

		handler.postDelayed(runnable, 1000/frameRate);
	}

    public int getColorAlpha() {
        return colorAlpha;
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		loadPreferences(sharedPreferences);
	}

    public void loadPreferences() {
        SharedPreferences settings = getContext().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        loadPreferences(settings);
    }

    public class LoadBackgroundImage extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... uri) {
            try {
                ContentResolver cR = context.getContentResolver();
                backgroundImage = MediaStore.Images.Media.getBitmap(cR, uri[0]);
            }
            catch (Exception e) {
                useBackgroundImage = false;
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            loadingBackground = true;
            Log.d(TAG, "Loading background image...");
        }

        @Override
        protected void onPostExecute(String file) {
            loadingBackground = false;
            Log.d(TAG, "Background image loaded.");

            if (backgroundImage == null) {
                useBackgroundImage = false;
            }
            else {
                backgroundImageSize = new Point(backgroundImage.getWidth(), backgroundImage.getHeight());
            }
        }
    }
}
