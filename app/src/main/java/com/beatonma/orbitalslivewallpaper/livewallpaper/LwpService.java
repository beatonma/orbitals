package com.beatonma.orbitalslivewallpaper.livewallpaper;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;

import com.beatonma.orbitalslivewallpaper.Utils;

public class LwpService extends WallpaperService {

	public static final String SHARED_PREFS_NAME = "orbitalsLWPsettings";
	private static final String TAG = "Orbitals LWP";
    private final static String FILENAME = "active_notifications";
    private final static int INVALID_COLOR = -1234567890;

    private final static int RANDOM_SINGLE_SYSTEM = 0;
    private final static int STAR_SYSTEM = 1;
    private final static int CELL_SYSTEM = 2;
    private final static int RANDOM_SYSTEM = 3;
    private final static int POLY_SYSTEM = 4;
    private final static int STREAMER_SYSTEM = 5;

	public LwpService g = this;
	private SharedPreferences mPrefs;

	private List<LwpEntity> entities = new ArrayList<LwpEntity>();
	private List<LwpEntity> removeEntities = new ArrayList<LwpEntity>();
	private List<LwpEntity> newEntities = new ArrayList<LwpEntity>();
    private List<LwpParticleGun> particleGuns = new ArrayList<LwpParticleGun>();
    private List<LwpParticleGun> removeParticleGuns = new ArrayList<LwpParticleGun>();

	// Initiate
	private int scrollOffset = 0;
	private int xStart = 0;
	private int yStart = 0;

	// Canvas scaling
	private float xSize = 0f;
	private float ySize = 0f;
	private float screenScale = 1f;

	// Centre on centre of mass
	private boolean showCentreOfMass = false;
	private double totalMass = 0.0;
	private Point centreOfMass = new Point(0, 0);

	// User options
	private int maxEntities = 30;
	private int frameRate = 30;

	private boolean allowStarSystems = true;
	private boolean allowCellSystems = true;
	private boolean allowRandomSystems = true;
	private boolean allowPolySystems = true;
	private boolean allowSystemGeneration = true;
    private boolean allowParticleGuns = true;
	private String touchGeneration = "3";
    private List<Integer> allowedSystems = new ArrayList<Integer>();

	private boolean enableTraceLines = true;
	private Queue<Point> deadTraceLines = new LinkedList<>();
	private int traceLineLength = 25;

	private boolean enableAccelerationArrows = false;
	private boolean enableOutlines = false;

    // custom background image
    private boolean useBackgroundImage = false;
    private Bitmap backgroundImage;
    private boolean loadingBackground = false;
    private Point backgroundImageSize = new Point(0,0);
    private float backgroundScale = 0f;

	private String colorTheme = "0";
	private String[] objectColors = {""};
	private int backgroundColor = Color.BLACK;
	private int backgroundRenderColor = Color.BLACK;
	private int colorAlpha = 255;
	private boolean darkColors = false;
	private boolean desaturateColors = false;
	private boolean wireFrame = false;
	private int wireFrameWidth = 2;
	private double objectSize = 1.0;
	private boolean pixelate = false;
	private boolean outlineColorCustom = false;
	private int outlineColor = Color.BLACK;
	//private boolean shadowLayer = true;

	KeyguardManager keyguardManager = null;
	private boolean lockScreen = false;
	private String lockScreenColorTheme = "0";
	private String[] lockScreenObjectColors = {""};
	private int lockScreenBackgroundColor = Color.BLACK;
	private boolean lockScreenWireFrame = true;
	private int lockScreenWireFrameWidth = 2;
	private int lockScreenAlpha = 180;
	private boolean lockScreenDark = false;
	private boolean lockScreenDesaturate = true;
	private double lockScreenObjectSize = 1.0;
	private int lockScreenDelay = 1;
	private boolean lockScreenEnableOutlines = false;
	private boolean lockScreenOutlineColorCustom = false;
	private int lockScreenOutlineColor = 0;
	
	private boolean colorByCharge = false;
	private float batteryLevel = 100f;
	private String[] chargeColors = {""};


	// Gravitational constant = 0.00000000006674 N(m/kg)^2. Changed magnitude
	// here to make overall numbers easier to deal with.
	// G = 0.06674
	private double G = 0.06674;
	private int gravityMultiplier = 1;
	private boolean antiGravity = false;
	private int collisionStyle = 1; // 0 = simple crash and die; 1 = osmos-style
									// transfer; 2 = softened acceleration and
									// no collisions
	private boolean mPhysicsBorderBounce = false;

    // Notifications
    private boolean showNotifications = false;
    private int notificationPulseDelay = 200;
    private int pulseTimer = 25;
    private int pulseLength = 100;
    private boolean pulse = false;
    private int pulseColor = Color.BLUE;
    private int pulseIndex = 0;
    private boolean assignNewColors = false;
    public int colorDelta = 1;

    private boolean pathing = false;
    private Bitmap bitmap;
    private Canvas pathingCanvas;
    private Paint pathingPaint;
    private int pathingOverlayDelay = 0;

	public void onCreate() {
		super.onCreate();
	}

	public void onDestroy() {
		super.onDestroy();
		tidy();
	}

	public Engine onCreateEngine() {
		Log.d(TAG, "Starting wallpaper engine");
		return new WallpaperEngine();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void setGravitationalConstant(double g) {
		G = g;
	}

	public double getGravitationalConstant() {
		return G;
	}

	public double getGravity() {
		if (gravityMultiplier <= 10) {
			return G * (gravityMultiplier * 0.1);
		} else {
			return G * (gravityMultiplier - 9.0);
		}
	}

	public void tidy() {
		entities.clear();
		newEntities.clear();
		removeEntities.clear();
	}

	public boolean getAntiGravity() {
		return antiGravity;
	}

	public int getAlpha() {
		if (lockScreen) {
			return lockScreenAlpha;
		} else {
			return colorAlpha;
		}
	}

	public boolean getDesaturateColors() {
		return desaturateColors;
	}

	public boolean getDarkColors() {
		return darkColors;
	}

	public double getObjectSize() {
		if (lockScreen) {
			return lockScreenObjectSize;
		}
		else {
			return objectSize;
		}
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public int getChargeColor() {
		int chargeColor = Color.WHITE;
		String key = "";
		
		if (batteryLevel > 0.8f) {
			key = "pref_color_battery_high";
		}
		else if (batteryLevel > 0.5f) {
			key = "pref_color_battery_medhigh";
		}
		else if (batteryLevel > 0.2f) {
			key = "pref_color_battery_medlow";
		}
		else {
			key = "pref_color_battery_low";
		}
		
		if (mPrefs == null) {
			mPrefs = LwpService.this.getSharedPreferences(
					SHARED_PREFS_NAME, 0);
		}
		
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			chargeColors = mPrefs.getStringSet(key, new HashSet<String>()).toArray(new String[] {});
		}
		else {
			//chargeColor = Integer.valueOf(mPrefs.getString("pref_color_colortheme", "0"));
		}
		
		if (chargeColors.length >= 1) {
			chargeColor = Integer.valueOf(chargeColors[((int) Math.floor(Math.random() * chargeColors.length))]);
		}
		else {
			chargeColor = Color.WHITE;
		}
		
		
//		if (lockScreenObjectColors.length >= 1) {
//			colorFilter = Integer
//					.valueOf(lockScreenObjectColors[((int) Math
//							.floor(Math.random()
//									* lockScreenObjectColors.length))]);
//		} else {
//			colorFilter = 0;
//		}
		
		return chargeColor;
	}

    public void loadPreferences() {
        SharedPreferences settings = getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        loadPreferences(settings);
    }

	@SuppressLint("NewApi")
	public void loadPreferences(SharedPreferences settings) {
		mPrefs = settings;

		maxEntities = settings.getInt("pref_performance_maxobjects", 25);
		frameRate = settings.getInt("pref_performance_framerate", 30);

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
        if (allowedSystems.size() == 0) { // Make sure at least one
            allowedSystems.add(STAR_SYSTEM);
        }

		touchGeneration = settings.getString("pref_systems_touch", "3");

		colorByCharge = settings.getBoolean("pref_color_batterylevel", false);
		backgroundColor = settings.getInt("pref_color_background", Color.BLACK);
		if (backgroundColor == 0) {
			backgroundColor = Color.BLACK;
		}
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
			objectColors = settings.getStringSet("pref_color_objectcolors", new HashSet<String>()).toArray(new String[] {});
			lockScreenObjectColors = settings.getStringSet("pref_lockscreen_color_objectcolors", new HashSet<String>()).toArray(new String[] {});
		}
		else {
			colorTheme = settings.getString("pref_color_colortheme", "0");
			lockScreenColorTheme = settings.getString("pref_lockscreen_color_colortheme", "0");
		}
		colorAlpha = settings.getInt("pref_color_alpha", 255);
		darkColors = settings.getBoolean("pref_color_dark", false);
		desaturateColors = settings.getBoolean("pref_color_desaturate", false);
		outlineColorCustom = settings.getBoolean("pref_outlines_custom_color", false);
		outlineColor = settings.getInt("pref_color_outline", 0);

		lockScreenWireFrame = settings.getBoolean("pref_lockscreen_wireframe",
				false);
		lockScreenDark = settings.getBoolean("pref_lockscreen_dark", false);
		lockScreenDesaturate = settings.getBoolean(
				"pref_lockscreen_desaturate", true);
		lockScreenAlpha = settings.getInt("pref_lockscreen_alpha", 255);
		lockScreenBackgroundColor = settings.getInt(
				"pref_lockscreen_color_background", Color.BLACK);
		if (lockScreenBackgroundColor == 0) {
			lockScreenBackgroundColor = Color.BLACK;
		}

        useBackgroundImage = settings.getBoolean("pref_color_background_image", false);

		lockScreenWireFrameWidth = settings.getInt("pref_lockscreen_wireframewidth", 2);
		lockScreenObjectSize = Double.valueOf(settings.getString("pref_lockscreen_size", "1.0"));
		lockScreenEnableOutlines = settings.getBoolean("pref_lockscreen_outlines", false);
		lockScreenOutlineColorCustom = settings.getBoolean("pref_lockscreen_outlines_custom_color", false);
		lockScreenOutlineColor = settings.getInt("pref_lockscreen_outlines_color", Color.BLACK);
		

		gravityMultiplier = settings.getInt("pref_physics_gravity", 10);
		antiGravity = settings.getBoolean("pref_physics_antigravity", false);
		collisionStyle = Integer.valueOf(settings.getString(
				"pref_physics_collision", "1"));
		mPhysicsBorderBounce = settings.getBoolean("pref_physics_border_bounce", false);
		
		showCentreOfMass = settings.getBoolean("pref_experimental_centreofmass", false);
		pixelate = settings.getBoolean("pref_experimental_pixelate", false);
        //shadowLayer = settings.getBoolean("pref_experimental_shadow", false);

        showNotifications = settings.getBoolean("pref_notify_pulse", false);
        colorDelta = Integer.valueOf(settings.getString("pref_notify_speed", "3"));
        if (showNotifications) {
            initPulseTiming();
        }

        pathing = settings.getBoolean("pref_experimental_drip", false);

		entities.clear();
		removeEntities.clear();
		newEntities.clear();
		deadTraceLines.clear();

		keyguardManager = (KeyguardManager) getBaseContext().getSystemService(
				Context.KEYGUARD_SERVICE); // used to determine if the device is
											// currently locked to change render
											// parameters if required
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

			LwpEntity newLwpEntity = new LwpEntity(g, x1, y1, (int) dx, (int) dy,
					(int) 8, (int) 500, makeRandomColor());

			entities.add(newLwpEntity);
		}
	}

	public void makeStarSystem(Point centre, int n) {

		if (n >= 1) {

			int dx = 0;
			int dy = 0;

			// Make central star.

			LwpEntity centralStar = new LwpEntity(g, centre.x, centre.y, dx, dy,
					40 + (int) (Math.random() * 5),
					10000 + (int) (Math.random() * 200), makeRandomColor());

			entities.add(centralStar);

			for (int i = 1; i < n; i++) {

				int m = 500 + (int) Math.round(Math.random() * 2000); // random

				// mass,

				// 500

				// <

				// m

				// <

				// 5500

				int rad = (int) (Math.round(2 + Math.random() * 15) + (m / 1000)); // random

				// size, 1 <

				// s < 16

				int d = (int) (50 + (int) Math.round(Math.random() * 300)); // random

				// distance,

				// 50 < d <

				// 400

				double speed = getOrbitalSpeed(m, 10000, d);

				int q = (int) Math.round(Math.random() * 3);

				if (q == 0) {

					entities.add(new LwpEntity(g, (int) centralStar.getX(),
							(int) centralStar.getY() - d, speed + dx, dy, rad,
							m, makeRandomColor()));

				}

				else if (q == 1) {

					entities.add(new LwpEntity(g, (int) centralStar.getX() + d,
							(int) centralStar.getY(), dx, speed + dy, rad, m,
							makeRandomColor()));

				}

				else if (q == 2) {

					entities.add(new LwpEntity(g, (int) centralStar.getX(),
							(int) centralStar.getY() + d, -speed + dx, dy, rad,
							m, makeRandomColor()));

				}

				else if (q == 3) {

					entities.add(new LwpEntity(g, (int) centralStar.getX() - d,
							(int) centralStar.getY(), dx, -speed + dy, rad, m,
							makeRandomColor()));

				}

			}

		}

	}

	public double getOrbitalSpeed(double m1, double m2, int r) {

		// m1 = mass of orbiting object

		// m2 = mass of object being orbited

		// v = sqrt((m2^2) * G / (m1 + m2) * r)

		// double v = Math.sqrt(((m2 * m2) * 50 * G) / ((m1 + m2) * r));

		double v = Math.sqrt(((m2 * m2) * getGravity()) / ((m1 + m2) * r));

		// v /= 8;

		return v;

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

				entities.add(new LwpEntity(g, x, y, dx, dy, rad, m,
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

		// Point a = new Point((g.getWidth() / 2), (g.getHeight() / 2));
		Point a = centre;
		Point b = new Point(a.x + len, a.y);
		Point c = new Point();

		nodes.add(b);

		entities.add(new LwpEntity(g, a.x, a.y, dx, dy, r, m, makeRandomColor()));

		if (n >= 2) {
			entities.add(new LwpEntity(g, b.x, b.y, dx, dy, r, m,
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

					entities.add(new LwpEntity(g, b.x, b.y, dx, dy, r, m,
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

	public void addDeadTracePoints(Queue<Point> dtp) {
		deadTraceLines.addAll(dtp);
	}

	public int makeRandomColor() {

		int r = (int) Math.round(Math.random() * 255);
		int g = (int) Math.round(Math.random() * 255);
		int b = (int) Math.round(Math.random() * 255);

		int c = betterColor(r, g, b);

		return c;

	}

	public int betterColor(int c) {
		return betterColor(Color.red(c), Color.green(c), Color.blue(c));
	}

	public int betterColor(int r, int g, int b) {

		float[] hsv = new float[3];

		Color.RGBToHSV(r, g, b, hsv);

		int colorFilter = 0;

		if (colorByCharge) {
			colorFilter = getChargeColor();
		}
		else if (lockScreen) {
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
				if (lockScreenObjectColors.length >= 1) {
					colorFilter = Integer
							.valueOf(lockScreenObjectColors[((int) Math
									.floor(Math.random()
											* lockScreenObjectColors.length))]);
				} else {
					colorFilter = 0;
				}
			}
			else {
				colorFilter = Integer.valueOf(lockScreenColorTheme);
			}
		} else {
			// colorFilter = Integer.valueOf(colorTheme);
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
				if (objectColors.length >= 1) {
					colorFilter = Integer.valueOf(objectColors[((int) Math
							.floor(Math.random() * objectColors.length))]);
				} else {
					colorFilter = 0;
				}
			}
			else {
				colorFilter = Integer.valueOf(colorTheme);
			}
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
		} else if (desaturateColors && !lockScreen) {
			hsv[1] = (float) (Math.random() * 0.1f + 0.3f);
		} else if (lockScreenDesaturate && lockScreen) {
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
		if (darkColors && !lockScreen) {
			hsv[2] = (float) (Math.random() * 0.35f + 0.15f);
		} else if (lockScreenDark && lockScreen) {
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
	
	public int getBackgroundColor() {
		int r1, g1, b1, r2, g2, b2, col, difference;
		difference = 10;
		
		if (lockScreen) {
			col = lockScreenBackgroundColor;
		}
		else {
			col = backgroundColor;
		}
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

	// x, y are the canvas width, height
	public void addRandomEntities(int x, int y) {
		//int r = (int) Math.floor(Math.random() * 6);
        int r = allowedSystems.get((int) Math.floor(Math.random() * allowedSystems.size()));
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
        particleGuns.add(new LwpParticleGun(g, point, rate, size, xSpeed, ySpeed,
                spread, ttl));
        Log.d(TAG, "New particle gun ttl=" + ttl);
    }

	public void checkOutOfBounds(LwpEntity e, int boundX, int boundY) {

		Point position = e.getPosition();
		int pX = position.x;
		int pY = position.y;

		if ((pX > boundX) || (pX < -boundX) || (pY > boundY) || (pY < -boundY)) {
			removeEntities.add(e);
		}
	}

	public void explodeEntity(LwpEntity e) {

		double dx, dy; // Explosion velocity vectors
		double escape = Math.sqrt((2 * getGravity() * e.getMass()) / 10); // Escape
		// velocity

		double radius, mass; // Physical attributes

		int allowance = maxEntities - entities.size();
		if (allowance <= 1)
			allowance = 2;

		int n = (int) (Math.floor(Math.random() * allowance) + 4); // Number of
																	// new
																	// entities.

		mass = e.getMass() / n;

		double area = (Math.PI * Math.pow(e.getRadius(), 2)) / n;
		radius = Math.sqrt(area / Math.PI);

		// Particle separation in radians
		// double angle = Math.toRadians(360 / n);
		double angle = Math.toRadians((360 / n) + (Math.random() * 360 / n)); // Added
																				// random
																				// to
																				// vary
																				// explosion
																				// aesthetics

		for (int i = 0; i < n; i++) {

			// Particle x, y velocities
			dx = escape * Math.cos(i * angle);
			dy = escape * Math.sin(i * angle);

			LwpEntity newLwpEntity = new LwpEntity(g, (int) e.getX(), (int) e.getY(),
					dx, dy, (int) radius, (int) mass, makeRandomColor());

            //backgroundColor = newLwpEntity.getColor();
			newLwpEntity.allowCollisions(false);
			newEntities.add(newLwpEntity); // Put in buffer to be added to main
										// list at end of current animation
										// cycle.
		}

		e.setDestroy(true);
		e.setRadius(0);
		e.setMass(1.0);
	}

    private void updatePulseColor() {
        String filename = FILENAME;
        String input = "";
        List<String> subs = new ArrayList<String>();

        try {
            FileInputStream fis = this.openFileInput(filename);
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
            case 1:
                pulseLength = 150;
                notificationPulseDelay = 240;
                break;
            case 2:
                break;
            case 3:
                pulseLength = 100;
                notificationPulseDelay = 160;
                break;
            case 5:
                pulseLength = 50;
                notificationPulseDelay = 80;
                break;
            default:
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

		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay()
				.getMetrics(metrics);

		if ((xSize) > (ySize)) {
			g.screenScale = xSize / 1280;
		} else {
			g.screenScale = xSize / 768;
		}

	}

	public void setScreenScale(Canvas c) {

		xSize = c.getWidth();
		ySize = c.getHeight();

		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay()
				.getMetrics(metrics);

		float xScreen = xSize / 768;
		float yScreen = ySize / 1280;

		if ((xScreen) > (yScreen)) {
			g.screenScale = xScreen;
		} else {
			g.screenScale = yScreen;
		}
	}

	public float getWidth() {
		return xSize;
	}

	public float getHeight() {
		return ySize;
	}

	public float getScrollOffset() {
		return scrollOffset;
	}

	public boolean getBorderBounce() {
		return mPhysicsBorderBounce;
	}

	public void pixelate(Canvas c, LwpEntity e, Paint p) {
		int pixelSize = (int) (xSize / 70);
		int gridOffsetX = (int) (e.getX() % pixelSize);
		int gridOffsetY = (int) (e.getY() % pixelSize);
		int r = (int) e.getRadius();
		int gridSize = (int) (r * 2);
		Point gridOrigin = new Point((int) (e.getX() - e.getRadius() - gridOffsetX), (int) (e.getY() - e.getRadius() - gridOffsetY));
		
		for (int i=0; i < gridSize; i++) {
			for (int j=0;j < gridSize; j++) {
				if (findDistanceBetweenPoints(new Point(gridOrigin.x + (i * pixelSize), gridOrigin.y + (j * pixelSize)), new Point((int) e.getX() + gridOffsetX, (int) e.getY() + gridOffsetY)) < r) {
					float left =  gridOrigin.x + (i * pixelSize);
					float right = left + pixelSize;
					float top = gridOrigin.y + (j * pixelSize);
					float bottom = top + pixelSize;
					c.drawRect(left * screenScale, top * screenScale, right * screenScale, bottom * screenScale, p);
				}
			}
		}
	}

	public double getTotalMass() {
		return totalMass;
	}

	public void addToTotalMass(double m) {
		totalMass += m;
	}

	public int findDistanceBetweenPoints(Point p1, Point p2) {
		int x1 = p1.x;
		int y1 = p1.y;
		int x2 = p2.x;
		int y2 = p2.y;

		int r = (int) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));

		return r;
	}

	public void setTraceLineLength(int n) {
		traceLineLength = n;
	}

	public int getTraceLineLength() {
		return traceLineLength;
	}

    public void addEntity(LwpEntity e) {
        newEntities.add(e);
    }

	public void setCollisionStyle(int n) {
		if ((n == 1) || (n == 0)) {
			collisionStyle = n;
		}
	}

	public int getCollisionStyle() {
		return collisionStyle;
	}

	public void removeDeadMass() {
		ListIterator<LwpEntity> iterator = removeEntities.listIterator();

		while (iterator.hasNext()) {
			LwpEntity e = iterator.next();
			totalMass -= e.getMass();
		}
	}

	class WallpaperEngine extends Engine implements
			SharedPreferences.OnSharedPreferenceChangeListener {

		private final Handler handler = new Handler();
		private final Runnable drawRunner = new Runnable() {
			@Override
			public void run() {
//				try {
					draw();
				/*} catch (Exception e) {
					Log.e(TAG, "Error: " + e.toString());
				}*/
			}
		};
		private boolean visible = true;

		WallpaperEngine() {
			setTouchEventsEnabled(true);
			mPrefs = LwpService.this.getSharedPreferences(
					SHARED_PREFS_NAME, 0);
			mPrefs.registerOnSharedPreferenceChangeListener(this);
			onSharedPreferenceChanged(mPrefs, null);
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs,
				String key) {
			loadPreferences(prefs);
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
				Utils.updateWidgets(getApplicationContext());
			}
		}

		public void onCreate(SurfaceHolder surfaceHolder) {
			super.onCreate(surfaceHolder);

			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@TargetApi(Build.VERSION_CODES.FROYO)
		public void onTouchEvent(MotionEvent event) { // Respond to touch events
			int action = event.getActionMasked();
			float ff = g.screenScale;

			if (action == MotionEvent.ACTION_DOWN) {
				xStart = (int) (event.getX() / ff);
				yStart = (int) (event.getY() / ff);
			}

			if (action == MotionEvent.ACTION_UP) {

				// Create new System if tap event, ignore if swiping.
				if (findDistanceBetweenPoints(
						new Point(xStart, yStart),
						new Point((int) (event.getX() / ff), (int) (event
								.getY() / ff))) < 50) {

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
                            return;
                        }

						//int r = (int) Math.floor(Math.random() * 5);
                        int r = allowedSystems.get((int) Math.floor(Math.random() * allowedSystems.size()));

						switch (r) {
						case RANDOM_SINGLE_SYSTEM:
							makeRandomSystem((int) xSize, (int) ySize, 1);
							break;

						case STAR_SYSTEM:
							if (allowStarSystems) {
								makeStarSystem(
										new Point((int) ((event.getX()) / ff)
												- scrollOffset,
												(int) (event.getY() / ff)), n);
							}
							break;
						case CELL_SYSTEM:
							if (allowCellSystems) {
								makeCellularSystem(
										new Point((int) ((event.getX()) / ff)
												- scrollOffset,
												(int) (event.getY() / ff)), n);
							}
							break;
						case RANDOM_SYSTEM:
							if (allowRandomSystems) {
								makeRandomSystem((int) xSize, (int) ySize, n);
							}
							break;
						case POLY_SYSTEM:
							if (allowPolySystems) {
								int rad = (int) (Math.random() * 200) + 50;

								makePolygonSystem(
										new Point((int) ((event.getX()) / ff)
												- scrollOffset,
												(int) (event.getY() / ff)),
										rad, n);
							}
						default:
							break;
						}
						break;
					case 2: // Cellular
						makeCellularSystem(new Point(
								(int) ((event.getX()) / ff) - scrollOffset,
								(int) (event.getY() / ff)), n);
						break;
					case 3: // Planetary
						makeStarSystem(new Point((int) ((event.getX()) / ff)
								- scrollOffset, (int) (event.getY() / ff)), n);
						break;

					case 4: // Polygonal
						makePolygonSystem(new Point((int) (event.getX() / ff)
								- scrollOffset, (int) (event.getY() / ff)),
								100, n);
						break;

					case 5: // Random
						makeRandomSystem((int) xSize, (int) ySize, n);
						break;

					default:
						break;
					}
				}
			}
		}

		@Override
		public void onVisibilityChanged(boolean visible) {
			this.visible = visible;
			// if screen wallpaper is visible then draw the image otherwise do
			// not draw
			if (visible) {
				handler.post(drawRunner);
			} else {
				handler.removeCallbacks(drawRunner);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format,
				int width, int height) {
			super.onSurfaceChanged(holder, format, width, height);
			xSize = width;
			ySize = height;
			setScreenScale();
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder) {
			super.onSurfaceDestroyed(holder);
			this.visible = false;
			handler.removeCallbacks(drawRunner);
		}

		@Override
		public Bundle onCommand(String action, int x, int y, int z,
				Bundle extras, boolean resultRequested) {
			return null;
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep,
				float yStep, int xPixels, int yPixels) {
			if (showCentreOfMass) {
				scrollOffset = 0;
			}
			else {
				scrollOffset = (int) ((xOffset * xSize / 4) * screenScale);
			}
		}

		private void draw() {
			final SurfaceHolder holder = getSurfaceHolder();

			boolean updateBatteryLevel = false;
			Canvas c = null;
			Paint p = new Paint();
			p.setAntiAlias(true);
			
			int xCentreOfMass = 0;
			int yCentreOfMass = 0;
			double translateX = 0.0, translateY = 0.0;

			boolean oldLock = lockScreen;
			lockScreen = keyguardManager.inKeyguardRestrictedInputMode();
			if (oldLock != lockScreen) {
				lockScreenDelay = 1;
			}

			try {
				c = holder.lockCanvas();
				totalMass = 0;

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

				if (c != null) {
					if (colorByCharge) {
						float oldBatteryLevel = batteryLevel;
						IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
						Intent batteryStatus = g.registerReceiver(null, ifilter);
						
						int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
						int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
	
						batteryLevel = level / (float) scale;
						
						if ((oldBatteryLevel * 100f) % 10f > (batteryLevel * 100f) % 10f) {
							updateBatteryLevel = true;
						}
					}

					// clear the canvas
					c.drawColor(getBackgroundColor());

					if (((int) xSize | (int) ySize) == 0) {
						setScreenScale(c);
					}

                    if (useBackgroundImage) {
                        SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                        if (sp.getBoolean("updated_background", false)) {
                            Log.d(TAG, "Background file has changed. Updating...");
                            backgroundImage = null;
                            sp.edit().putBoolean("updated_background", false).commit();
                        }

                        if (backgroundImage == null) {
                            if (!loadingBackground) {
                                //Uri uri = Uri.parse("file://" + getCacheDir() + File.separator + "lwp_bg_image");
                                Uri uri = Uri.parse("file://" + getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE).getString("background_file", "lwp_bg_image"));
                                Log.d(TAG, "Loading file:" + uri);
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

					float ff = g.getScreenScale();

					if (showCentreOfMass) {
						if (!(centreOfMass.x == 0 && centreOfMass.y == 0)) {
							if (centreOfMass.x > (xSize / 2.0)) {
								translateX = -(centreOfMass.x - (xSize / 2.0)) / 10.0;
							} else if (centreOfMass.x < (xSize / 2.0)) {
								translateX = ((xSize / 2.0) - centreOfMass.x) / 10.0;
							} else {
								translateX = 0;
							}
							
							if (translateX > 10) {
								translateX = 10;
							}
							else if (translateX < -10) {
								translateX = -10;
							}
		
							if (centreOfMass.y > (ySize / 2.0)) {
								translateY = -(centreOfMass.y - (ySize / 2.0)) / 10.0;
							} else if (centreOfMass.y < (ySize / 2.0)) {
								translateY = ((ySize / 2.0) - centreOfMass.y) / 10.0;
							} else {
								translateY = 0;
							}
							
							if (translateY > 10) {
								translateY = 10;
							}
							else if (translateY < -10) {
								translateY = -10;
							}
							
							ListIterator<LwpEntity> iterator = entities.listIterator();

							while (iterator.hasNext()) {
								LwpEntity e = iterator.next();
								
								e.setX(e.getX() + translateX);
								e.setY(e.getY() + translateY);
							}
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
							if (Math.random() > 0.95) {
								addRandomEntities(c.getWidth(), c.getHeight());
							}
						}
					}

                    if (allowParticleGuns) {
                        ListIterator<LwpParticleGun> iterator = particleGuns.listIterator();

                        while (iterator.hasNext()) {
                            // Cycle through list of entities.
                            LwpParticleGun gun = iterator.next();
                            gun.shoot();
                            if (gun.ttl <= 0) {
                                removeParticleGuns.add(gun);
                            }
                        }
                    }


					if (enableTraceLines) {
						float fft = ff;
						if (fft < 1f)
							fft = 0.5f;

						while (deadTraceLines.size() > 150) {
							deadTraceLines.remove();
						}
						if (deadTraceLines.size() > 0) {
							Iterator<Point> deadTraceIterator = deadTraceLines
									.iterator();
							p.setColor(Color.argb(getAlpha(), 50, 50, 50));

							while (deadTraceIterator.hasNext()) {
								Point deadPoint = deadTraceIterator.next();

                                deadPoint.x += translateX;
                                deadPoint.y += translateY;

								c.drawCircle((deadPoint.x + scrollOffset) * ff,
										deadPoint.y * ff, (int) (2 * fft), p);
							}
						}

						ListIterator<LwpEntity> iterator = entities.listIterator();

						while (iterator.hasNext()) {
							// Cycle through list of entities.
							LwpEntity e = iterator.next();

							p.setColor(e.getColor());
							p.setAlpha(getAlpha());

							e.doTrace();

							Iterator<Point> traceIterator = e.getTrace()
									.iterator();

							while (traceIterator.hasNext()) {

								Point point = traceIterator.next();

                                point.x += translateX;
                                point.y += translateY;

								c.drawCircle((point.x + scrollOffset) * ff,
										point.y * ff, (int) (2 * fft), p);

							}
						}
					}

					// Update logic and draw entities to canvas
					ListIterator<LwpEntity> iterator = entities.listIterator();

					while (iterator.hasNext()) {

						// Cycle through list of entities.

						LwpEntity e = iterator.next();
						totalMass += e.getMass();

						// Check if entity is too far off the screen
						checkOutOfBounds(e, (int) (c.getWidth() * 1.2 / ff),
								(int) (c.getHeight() * 1.2 / ff));

						// Check mass,size and destroy if too large.
						if (lockScreenDelay < 1000)
							lockScreenDelay++;
						else {
							if (lockScreen) {
								if ((e.getMass() > (70000.0 * lockScreenObjectSize))
										|| (e.getRadius() > (65.0 * lockScreenObjectSize))) {
									explodeEntity(e);
								}
							} else {
								if ((e.getMass() > (70000.0 * objectSize))
										|| (e.getRadius() > (65.0 * objectSize))) {
									explodeEntity(e);
								}
							}
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

						// Change colour if battery status has changed
						if (colorByCharge && updateBatteryLevel) {
							e.setColor(betterColor(e.getColor()));
						}
						
						// Change colour if lockscreen status has changed
						else if (oldLock != lockScreen) {
							e.setColor(betterColor(e.getColor()));
							if (lockScreen) {
								e.setRadius(e.getRadius() * lockScreenObjectSize / objectSize);
								e.setMass(e.getMass() * lockScreenObjectSize / objectSize);
							}
							else {
								e.setRadius(e.getRadius() * objectSize / lockScreenObjectSize);
								e.setMass(e.getMass() * objectSize / lockScreenObjectSize);
							}
						}

						// Calculate net direction and force of gravity.
						for (LwpEntity o : entities) {
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

							p.setColor(Color.argb(getAlpha(), 255, 255, 255));

							Point point = e.doAccelerationArrows();
							p.setStrokeCap(Paint.Cap.ROUND);
							p.setStrokeWidth(3);

							c.drawLine((int) (e.getX() + scrollOffset) * ff,
									(int) e.getY() * ff,
									(point.x + scrollOffset) * ff,
									point.y * ff, p);
						}

						// Draw entity to canvas.
						if (lockScreen) {
							p.setStyle(e.getPaint().getStyle());

							p.setColor(e.getColor());
							p.setAlpha(getAlpha());

                            if (pathing) {
                                e.plot(ff);
                                pathingCanvas.drawPath(e.path, e.getPaint());
                                e.resetPath();
                            }
							else if (lockScreenWireFrame) {
								p.setStyle(Paint.Style.STROKE);
								p.setStrokeWidth(lockScreenWireFrameWidth);

								c.drawCircle((float) (e.getX() + scrollOffset)
										* ff, (float) e.getY() * ff,
										(float) e.getRadius() * ff, p);
							}
                            else {
								c.drawCircle((float) (e.getX() + scrollOffset)
										* ff, (float) e.getY() * ff,
										(float) e.getRadius() * ff, p);

								if (lockScreenEnableOutlines) {
									if (lockScreenOutlineColorCustom) {
										p.setColor(lockScreenOutlineColor);
									}
									else {
										p.setColor(backgroundRenderColor);
									}
									p.setStyle(Style.STROKE);
									p.setStrokeWidth(lockScreenWireFrameWidth);
									p.setAlpha(getAlpha());

									c.drawCircle(
											(float) (e.getX() + scrollOffset)
													* ff,
											(float) e.getY() * ff,
											(float) e.getRadius() * ff, p);
								}
							}

						} else {
                            if (pathing) {
                                e.plot(ff);
                                pathingCanvas.drawPath(e.path, e.getPaint());
                                e.resetPath();
                            }
							else {
                                if (pixelate) {
                                    p.setColor(e.getColor());
                                    p.setStyle(Paint.Style.FILL);
                                    p.setAlpha(getAlpha());

                                    pixelate(c, e, p);

                                    //show outline for debugging
//								p.setColor(e.getColor());
//								p.setStyle(Paint.Style.STROKE);
//								p.setStrokeWidth(wireFrameWidth);
//								p.setAlpha(30);
//
//								c.drawCircle((float) (e.getX() + scrollOffset)
//										* ff, (float) e.getY() * ff,
//										(float) e.getRadius() * ff, p);
                                }
                                else if (wireFrame) {
                                    p.setColor(e.getColor());
                                    p.setStyle(Paint.Style.STROKE);
                                    p.setStrokeWidth(wireFrameWidth);
                                    p.setAlpha(getAlpha());

                                    c.drawCircle((float) (e.getX() + scrollOffset)
                                                    * ff, (float) e.getY() * ff,
                                            (float) e.getRadius() * ff, p);
                                }
                                else {
                                    p.setColor(e.getColor());

                                    //p.setStyle(e.getPaint().getStyle());
                                    p.setStyle(Paint.Style.FILL);
                                    p.setAlpha(getAlpha());

                                    c.drawCircle((float) (e.getX() + scrollOffset)
                                                    * ff, (float) e.getY() * ff,
                                            (float) e.getRadius() * ff, p);

                                    if (enableOutlines) {
                                        if (outlineColorCustom) {
                                            p.setColor(outlineColor);
                                        } else {
                                            p.setColor(backgroundRenderColor);
                                        }
                                        p.setStyle(Style.STROKE);
                                        p.setStrokeWidth(wireFrameWidth);
                                        p.setAlpha(getAlpha());

                                        c.drawCircle(
                                                (float) (e.getX() + scrollOffset)
                                                        * ff,
                                                (float) e.getY() * ff,
                                                (float) e.getRadius() * ff, p);
                                    }
                                }
                            }
						}

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

					// Remove collided objects from entities list
					if (showCentreOfMass) {
						centreOfMass.x = (int) (xCentreOfMass / totalMass * ff);
						centreOfMass.y = (int) (yCentreOfMass / totalMass * ff);
//						Log.d(TAG, "Total Mass " + totalMass);
//						Log.d(TAG, "CoM:" + centreOfMass);

//						Paint com = new Paint(Paint.ANTI_ALIAS_FLAG);
//						com.setColor(Color.WHITE);
//						com.setStyle(Paint.Style.FILL);
//						com.setAlpha(100);
//						
//						c.drawCircle((centreOfMass.x),
//								centreOfMass.y, 20, com);
					}
					
					entities.removeAll(removeEntities);
					entities.addAll(newEntities);
                    particleGuns.removeAll(removeParticleGuns);
					newEntities.clear();
					removeEntities.clear();
				}
			}

			finally {
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			handler.removeCallbacks(drawRunner);
			if (visible) {
				handler.postDelayed(drawRunner, 1000/frameRate);
			}

		}
	}

    public class LoadBackgroundImage extends AsyncTask<Uri, Void, String> {

        @Override
        protected String doInBackground(Uri... uri) {
            try {
                ContentResolver cR = getContentResolver();
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
