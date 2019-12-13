package com.planetas4.game.Constants;

public class Values {
    static final public  int LIVES_DEFAULT = 5;
    static final public  int CHALLENGE_TIME_DEFAULT = 20;
    static final public float PIXELS_PER_SEC = 430; // 500;
    public static final float SCREEN_WIDTH = 360; //
    public static final float SCREEN_HEIGHT = 640;
    static final public float ANGLE_PER_SECOND_FAST = 190f;
    static final public float ANGLE_PER_SECOND_SLOW = 50f;
    static final public float MAX_DISTANCE_BORDER_DAMAGE =
            (float) Math.sqrt(SCREEN_HEIGHT * SCREEN_HEIGHT + SCREEN_WIDTH * SCREEN_WIDTH);
    static final public float LOST_TIME_LIMIT_BASE = CHALLENGE_TIME_DEFAULT * 2;
    static final public float MAX_FORWARD_TIME_DEFAULT = 0.4f;
    static final public float MAX_FORWARD_TIME_BOUNCE = 0.033f;
    static final public float PAUSE_BAR_VERTICAL_SPACE = 0.09f;
    static final public float CHALLENGE_BAR_SPACE = 0.45f;
    static final public float DISTANCE_PER_TREAT = SCREEN_WIDTH / 7;
    static final public float HABILITA_TIME_DEFAULT = 3.7f;
    static final public int VIBRATION_TIME_DEFAULT = 50;
}