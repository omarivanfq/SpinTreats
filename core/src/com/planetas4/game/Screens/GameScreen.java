package com.planetas4.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Components.Advice;
import com.planetas4.game.Components.Background;
import com.planetas4.game.Cat;
import com.planetas4.game.Components.Challenge;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Components.CurrentChallengeBar;
import com.planetas4.game.Utils.FontGenerator;
import com.planetas4.game.Components.Level;
import com.planetas4.game.MainGame;
import com.planetas4.game.Managers.LevelsManager;
import com.planetas4.game.Managers.PlanetsManager;
import com.planetas4.game.Managers.SettingsManager;
import com.planetas4.game.Managers.TreatsManager;
import com.planetas4.game.Components.ProgressBar;
import com.planetas4.game.Planets.Planet3;
import com.planetas4.game.Components.ScreenMessageChallenge;
import com.planetas4.game.Components.ScreenToggleButton;
import com.planetas4.game.Components.SparkTrail;
import com.planetas4.game.Constants.Strings;

/*
*
* This is the screen where the magic happens
*
*/

public class GameScreen implements Screen, Planet3.DamageHandler {

    private Cat cat;
    private MainGame game;
    private SpriteBatch batch;
    private Viewport viewport;
    private Background background;
    private AssetManager manager;

    private Stage stageMenu; // stage that stores the menu elements

    private ScreenToggleButton screenToggleButton; // btn that shows the menu
    private Level level; // the level that is currently being played

    private boolean pause;
    private float pan_x; // indicates "how much" has the user touched and slided (panned) through the screen horizontally.. when pan_x > 7 then the rotation of the planets is inverted

    private ProgressBar timeBar; // indicates the time left of the current challenge

    private Label livesGameLabel, levelGameLabel; // labels on the top of the screen
    private Label livesMenuLabel, levelMenuLabel; // labels on the menu view

    private CurrentChallengeBar currentChallengeBar; // bar at the bottom of the screen that indicates the current challenge of the level
    private ScreenMessageChallenge screenMessageChallenge; // view that indicates the current challenge at the beginning of each challenge

    private SparkTrail sparkTrail; // trail of sparks left by the user when panning
    private Vector2 touchPosition; // position of panning on screen
    private int clicks; // number of clicks on the screen (touches¿)

    private float outTransitionTime, // seconds
            elapsedOutTransitionTime;

    private Advice advice; // advice/message at the beginning of the level
    private boolean showingAdvice;

    private Label.LabelStyle lbs, specialLbs;
    private PlanetsManager.HidingTreatsListener planetsHidingTreatsListener;
    private TreatsManager.HidingTreatsListener treatsHidingTreatsListener;

    @Override
    public void dispose(){
        screenToggleButton.dispose();
        timeBar.dispose();
    }

    public GameScreen(final MainGame game, AssetManager manager) {
        pan_x = 0;
        this.manager = manager;
        this.game = game;
        this.cat = new Cat(game.manager);
        outTransitionTime = 0.2f;
        touchPosition = new Vector2();
        setUpViewport();
        setUpBatch();
        setUpBackground();
        setUpSparkTrail();
        setUpBarTime();
        setUpScreenToggleButton();
        setUpLabelStyle();
        setUpSpecialLabelStyle();
        setUpLabels();
        setUpCurrentChallengeBar();
        setHidingCandiesListener();
        stageMenu = new Stage(viewport);
        stageMenu.addActor(screenToggleButton);
        stageMenu.addActor(currentChallengeBar);
    }

    private void setHidingCandiesListener() {
        planetsHidingTreatsListener = new PlanetsManager.HidingTreatsListener() {
            @Override
            public void onHide() {
                background.goDark(0.22f);
                level.getCurrentChallenge().getTreatsManager().hide();
            }
            @Override
            public void onShow() {
                background.goBright(0.22f);
                level.getCurrentChallenge().getTreatsManager().show();
            }
        };
        treatsHidingTreatsListener = new TreatsManager.HidingTreatsListener() {
            @Override
            public void onShow(float time) {
                background.goBrightAndThenDark(time);
            }
        }; 
    }
    private void setUpViewport() {
        viewport = new FitViewport(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }
    private void setUpBatch() {
        batch = new SpriteBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);
    }
    private void setUpBackground() {
        background = new Background();
        background.addBackgroundImage("rebote", manager.get("fondo_azul_claro.png", Texture.class));
        background.addBackgroundImage("traslado", manager.get("fondo_verde.png", Texture.class));
    }
    private void setUpSparkTrail() {
        sparkTrail = new SparkTrail("destellos_toque/destellosToque.txt", 3,
                Values.SCREEN_WIDTH * 0.03f, Values.SCREEN_WIDTH * 0.03f);
    }
    private void setUpCurrentChallengeBar(){
        currentChallengeBar = new CurrentChallengeBar(viewport, 5, Values.PAUSE_BAR_VERTICAL_SPACE
                * Values.CHALLENGE_BAR_SPACE);
        currentChallengeBar.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                screenToggleButton.press();
                screenMessageChallenge.stopView();
                if (screenToggleButton.isPressed()) {
                    toPause();
                }
                else {
                    toResume();
                }
            }
        });
    }
    private void setUpLabels(){
        livesGameLabel = new Label("", lbs);
        livesGameLabel.setPosition(Values.SCREEN_WIDTH * 0.70f,
                Values.SCREEN_HEIGHT * 0.95f);
        levelGameLabel = new Label("", lbs);
        levelGameLabel.setPosition(Values.SCREEN_WIDTH * 0.05f,
                Values.SCREEN_HEIGHT * 0.95f);
    }
    private void setUpLabelStyle(){
        BitmapFont font = FontGenerator.getFont(18);
        this.lbs = new Label.LabelStyle();
        this.lbs.font = font;
        this.lbs.fontColor = new Color(255, 255, 255, 0.75f);
    }

    private void setUpSpecialLabelStyle() {
        BitmapFont font = FontGenerator.getSpecialFont(18);
        this.specialLbs = new Label.LabelStyle();
        this.specialLbs.font = font;
        this.specialLbs.fontColor = new Color(255, 255, 255, 0.65f);
    }

    private void setUpBarTime(){
        timeBar = new ProgressBar(0.5f, 0.5f, 1.0f, 1.0f);
        timeBar.setAngle(90);
        timeBar.setColor(Color.BLACK);
    }
    private void setUpScreenToggleButton(){

        screenToggleButton = new ScreenToggleButton(viewport, Values.PAUSE_BAR_VERTICAL_SPACE,
                0.26f, 0.67f);
        screenToggleButton.addElement(manager.get("iconos/pausa.png", Texture.class), "");

        levelMenuLabel = screenToggleButton.addLabel("");
        livesMenuLabel = screenToggleButton.addLabel("");

        screenToggleButton.addButton(Strings.RESUMEGAME_BTN, new ScreenToggleButton.ButtonListener() {
            @Override
            public void onClick() {
                screenToggleButton.press();
                if (pause) {
                    toResume();
                }
                else {
                    toPause();
                }
            }
        });

        screenToggleButton.addButton(Strings.RESTART_BTN, new ScreenToggleButton.ButtonListener() {
            @Override
            public void onClick() {
                toResume();
                if (level.getRemainingLives() > 1) {
                    screenToggleButton.press();
                }
                else {
                    screenToggleButton.restart();
                }
                level.getCurrentChallenge().restart(cat);
                timeBar.restart();
                loosesLife();
                updateBackground();
            }
        });

        screenToggleButton.addButton(Strings.GOOUT_BTN, new ScreenToggleButton.ButtonListener() {
            @Override
            public void onClick() {
                screenToggleButton.restart();
                game.setScreen(game.homeScreen);
                pause = false;
            }
        });

        screenToggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                screenMessageChallenge.stopView();
                if (screenToggleButton.isPressed()) {
                    toPause();
                }
                else {
                    toResume();
                }
            }
        });

    }

    @Override
    public void show() {
        clicks = 0;
        elapsedOutTransitionTime = 0.0f;
        this.cat.updateImage();

        int sectionIndex = LevelsManager.getCurrentSection();
        int levelSection = LevelsManager.getCurrentLevel();

        this.level = LevelsManager.getLevel(sectionIndex, levelSection,  // retrieving the current level just when game screen appears
                planetsHidingTreatsListener, treatsHidingTreatsListener, this, game);

        pause = true;
        level.restart();

        screenMessageChallenge = new ScreenMessageChallenge(level.getChallenges().size(), 0.6f);
        screenMessageChallenge.add("restart", manager.get("iconos/heart_icon.png", Texture.class),
                false);
        screenMessageChallenge.add("restart", Integer.toString(level.getRemainingLives()));
        screenMessageChallenge.add("adios", new Texture("transparent.png"));

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(screenToggleButton.getStage());
        multiplexer.addProcessor(stageMenu);
        multiplexer.addProcessor(new GestureDetector(new GestureHandler()));
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCatchBackKey(true);
        timeBar.update(0);
        screenToggleButton.restart();
        updateLabels();
        currentChallengeBar.setChallengesQuantity(level.getChallenges().size());
        currentChallengeBar.restart();
        nextChallenge();
        showingAdvice = false;
        advice = level.getAdvice();
        if (advice != null){
            showingAdvice = true;
            stageMenu.addActor(advice);
        }
        updateBackground();
        background.act(0);
    }

    private void updateLabels() {
        livesGameLabel.setText(Strings.LIVES_LABEL + ": " + level.getRemainingLives());
        livesMenuLabel.setText(Strings.LIVES_LABEL + ": " + level.getRemainingLives());
        int levelNumber = LevelsManager.getLevelNumber();
        if (LevelsManager.getExtraState()) {
            levelGameLabel.setStyle(this.specialLbs);
            levelGameLabel.setText(Strings.EXTRA_LEVEL_LABEL + ": " + levelNumber);
            levelMenuLabel.setText(Strings.EXTRA_LEVEL_LABEL + ": "  + levelNumber);
        }
        else {
            levelGameLabel.setStyle(this.lbs);
            levelGameLabel.setText(Strings.LEVEL_LABEL + ": " + levelNumber);
            levelMenuLabel.setText(Strings.LEVEL_LABEL + ": " + levelNumber);
        }
    }

    private void toPause(){
        if (level.getCurrentChallenge() != null){
            level.getCurrentChallenge().pausePlanets();
            if (level.getCurrentChallenge().hasToReveal()) {
                screenToggleButton.hideScreen(); // the screen toggle button is transparent by default but we do not want this behaviour when we want to hide the treats position
            }
        }
        pause = true;
    }

    private void toResume(){
        if (level.getCurrentChallenge() != null
                && level.getCurrentChallenge().isRevealing()){
            level.getCurrentChallenge().getTreatsManager().show();
        }
        pause = false;
    }

    @Override
    public void render(float delta){
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        screenMessageChallenge.act(delta);
        stageMenu.act();

        if (Gdx.input.isKeyPressed(Input.Keys.BACK) && !pause){
            toPause();
            screenToggleButton.press();
        }

        if (level.getCurrentChallenge().wasBeaten() && elapsedOutTransitionTime <= 0){
            nextChallenge();
        }
        else if (level.getCurrentChallenge().wasFailed() && timeBar.getExtension() >= 1){
            loosesLife();
            updateBackground();
            if (level.getRemainingLives() > 0) {
                level.getCurrentChallenge().restart(cat);
                timeBar.restart();
            }
        }
        else if (elapsedOutTransitionTime > 0.0f){
            elapsedOutTransitionTime -= delta;
        }
        else if (!pause){
            if (Gdx.input.isTouched()) {
                updateSparkTrail(delta, Gdx.input.getX(), Gdx.input.getY());
            }
            timeBar.update(level.getCurrentChallenge().getTimeManager().elapsedTimePercentage());
            timeBar.act(delta);
            cat.act(delta);
            level.getCurrentChallenge().update(delta, cat);
            background.act(delta);

            if (!cat.isFloating()){
                cat.rotate(level.getCurrentChallenge().getPlanetsManager().getCurrentPlanet());
            }
            if (level.getCurrentChallenge().wasBeaten()){
                elapsedOutTransitionTime = outTransitionTime;
                screenMessageChallenge.show("adios", 0,
                        0.9f, outTransitionTime * 1.1f, 0);
            }
        }

        batch.begin();
        background.draw(batch, 1);
        timeBar.draw(batch, 1);
        cat.draw(batch, 1);
        if (!level.challengesSurpassed())
            level.getCurrentChallenge().draw(batch);
        sparkTrail.draw(batch, 1);
        levelGameLabel.draw(batch, 1);
        livesGameLabel.draw(batch, 1);
        screenMessageChallenge.draw(batch, 1);
        batch.end();
        stageMenu.draw();
    }

    private void updateBackground() {
        if (level.getCurrentChallenge() != null){
            if (level.getCurrentChallenge().hasToReveal() && !level.getCurrentChallenge().startsRevealing()) {
                background.goDark(0);
                background.act(0.0f);
            }
            else {
                background.goBright(0);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
        if (!pause) {
            screenToggleButton.press();
            toPause();
        }
    }

    @Override
    public void resume() {}

    @Override
    public void hide() {
        timeBar.remove();
        level.dispose();
        Gdx.input.setInputProcessor(null);
    }

    private void updateSparkTrail(float delta, float x, float y){
        touchPosition.x = x;
        touchPosition.y = y;
        viewport.unproject(touchPosition);
        sparkTrail.setPosition(touchPosition.x, touchPosition.y);
        sparkTrail.act(delta);
    }

    private void nextChallenge() {
        level.nextChallenge();
        background.stopDamage();
        if (level.challengesSurpassed()) {
            unlockNextLevel();
            updateRecordTime();
            LevelsManager.setWinnerState(true);
            if (LevelsManager.isLastLevel()) {
                game.setScreen(game.thankYouScreen);
            }
            else {
                game.setScreen(game.finalScreen);
            }
        }
        else{
            currentChallengeBar.checkOnlyCheckbox(level.getCurrentChallengeIndex());
            if (level.getCurrentChallengeIndex() == 0) {
                screenMessageChallenge.showWhichChallengeWithNoEnd(level.getCurrentChallengeIndex());
            }
            else {
                screenMessageChallenge.showWhichChallengeWithNoEnd(level.getCurrentChallengeIndex());
                pause = true;
                clicks = 0;
            }
            updateBackgroundGivenBorder(level.getCurrentChallenge());
            level.getCurrentChallenge().restart(cat);
            updateBackground();
            timeBar.restart();
        }
    }

    private void updateRecordTime() {
        LevelsManager.setCompletedTime(level.getCompletedTime());
        if (LevelsManager.getCurrentLevelTime() == -1f
                || level.getCompletedTime() < LevelsManager.getCurrentLevelTime()) {
            LevelsManager.setCurrentLevelTime(level.getCompletedTime());
            LevelsManager.setNewRecord(true);
        }
        else {
            LevelsManager.setNewRecord(false);
        }
    }

    private void unlockNextLevel(){
          LevelsManager.unlockNextLevel();
    }

    private void loosesLife(){
        level.losesOneLife();
        livesMenuLabel.setText(Strings.LIVES_LABEL + ": " + level.getRemainingLives());
        livesGameLabel.setText(Strings.LIVES_LABEL + ": " + level.getRemainingLives());
        if (level.getRemainingLives() == 0) {
            gameOver();
        }
        else {
            screenMessageChallenge.updateLabel("restart", Integer.toString(level.getRemainingLives()));
            screenMessageChallenge.show("restart");
            stopDamage();
        }
    }

    private void updateBackgroundGivenBorder(Challenge challenge) {
        if (challenge.getBorderType() == Challenge.BORDER_TYPE.BOUNCE) {
            background.switchImage("rebote");
        }
        else if (challenge.getBorderType() == Challenge.BORDER_TYPE.TRANSFER) {
            background.switchImage("traslado");
        }
        else {
            background.switchImage("default");
        }
    }

    private void gameOver(){
        LevelsManager.setWinnerState(false);
        game.setScreen(game.finalScreen);
    }

    @Override
    public void startDamage() {
        background.startDamage();
    }

    @Override
    public void doDamage(float delta) {
        if (SettingsManager.getVibration()){
            Gdx.input.vibrate(Values.VIBRATION_TIME_DEFAULT / 10);
        }
    }

    @Override
    public void stopDamage() {
        background.stopDamage();
    }

    public class GestureHandler implements GestureListener {
        @Override
        public boolean tap(float x, float y, int count, int button) {
            if (showingAdvice) {
                advice.remove();
                showingAdvice = false;
            }
            else {
                screenMessageChallenge.stopView();
                if (!pause && !cat.isFloating()) {
                    level.getCurrentChallenge().touches(cat);
                    level.getCurrentChallenge().getPlanetsManager().getCurrentPlanet().setStopped(false);
                    return true;
                }
                if (clicks == 0){
                    pause = false;
                }
                clicks++;
            }
            return false;
        }

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            if (!pause && !cat.isFloating())
                level.getCurrentChallenge().getPlanetsManager().getCurrentPlanet().setStopped(true);
            return false;
        }

        @Override
        public void pinchStop() {
        }

        @Override
        public boolean longPress(float x, float y) {
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY){
            if (Math.abs(deltaX) > 3) {
                pan_x++;
            }
            return true;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            if (!pause) {
                if (!cat.isFloating()) {
                    level.getCurrentChallenge().getPlanetsManager().getCurrentPlanet().setStopped(false);
                }
                if (pan_x > 7) {
                    level.getCurrentChallenge().getPlanetsManager().reverseRotation();
                }
            }
            pan_x = 0;
            return false;
        }

        @Override
        public boolean zoom(float initialDistance, float distance) {
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                             Vector2 pointer1, Vector2 pointer2) {
            return false;
        }
    }

}
