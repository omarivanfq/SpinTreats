package com.planetas4.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Components.Background;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Utils.Dimensions;
import com.planetas4.game.Utils.FontGenerator;
import com.planetas4.game.MainGame;
import com.planetas4.game.Managers.LevelsManager;
import com.planetas4.game.Planets.Planet;
import com.planetas4.game.Constants.Strings;
import com.planetas4.game.Components.Toast;

import java.text.DecimalFormat;

/*
*
*     Screen that shows after finishing a level
*        it displays whether the user lost or won, and the total time
*
* */

public class FinalScreen implements Screen {

    private Stage stage;
    private TextButton nextLevelBtn, nextExtraLevelBtn;
    private Planet planet;
    private MainGame game;
    private BitmapFont font;
    private TextButton.TextButtonStyle tbs, tbsExtra;
    private Viewport viewport;
    private Toast extraLevelsToast, newCatToast;
    private Background background;
    private Image checkmarkImage, xImage;
    private Label levelLabel;
    private Label timeLabel;
    private Label extraLabel;
    private int levelIndex, sectionIndex;

    public FinalScreen(final MainGame game, AssetManager manager) {
        this.game = game;
        setUpBackground();
        viewport = new FitViewport(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        stage = new Stage(viewport);
        font = FontGenerator.getFont(25);
        setUpTextButtonStyle(manager);
        setUpLevelLabel();
        setUpCheckmarkImage(manager);
        setUpXImage(manager);
        setUpPlanet(manager);
        setUpNextLevelButton();
        setUpNextExtraLevelButton();
        setUpMenuBtn();
        setUpPlayAgainBtn();
        setUpToast();
        setUpTimeLabel();
        setUpExtraLabel();
    }

    /* setting up the label that indicates that the level was an "extra" level */
    private void setUpExtraLabel() {
        Label.LabelStyle lbs = new Label.LabelStyle();
        lbs.font = FontGenerator.getSpecialFont(25);
        lbs.fontColor = new Color(0,0,0,1);
        extraLabel = new Label(Strings.EXTRA_LEVEL_INDICATOR_LABEL, lbs);
        extraLabel.setWidth(Values.SCREEN_WIDTH);
        extraLabel.setX(0);
        extraLabel.setAlignment(Align.center);
        extraLabel.setY(Dimensions.pantalla_PorcentajeAReal_Y(0.645f));
        stage.addActor(extraLabel);
    }

    /* setting up the button to go to home */
    private void setUpMenuBtn() {
        TextButton menuBtn = new TextButton(Strings.MENU_BTN_LABEL, tbs);
        menuBtn.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.homeScreen);
            }
        });
        menuBtn.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT * 0.075f);
        menuBtn.setPosition(Values.SCREEN_WIDTH * 0.5f - nextLevelBtn.getWidth() * 0.5f,
                Dimensions.pantalla_PorcentajeAReal_Y(0.87f));
        stage.addActor(menuBtn);
    }

    /* setting up the label that indicates the time needed to complete the level */
    private void setUpTimeLabel() {
        Label.LabelStyle lbs = new Label.LabelStyle();
        lbs.font = FontGenerator.getFont(26);
        timeLabel = new Label("", lbs);
        timeLabel.setWidth(Values.SCREEN_WIDTH);
        timeLabel.setX(0);
        timeLabel.setAlignment(Align.center);
        timeLabel.setY(Dimensions.pantalla_PorcentajeAReal_Y(0.81f));
        stage.addActor(timeLabel);
    }

    /* setting up a planet as decoration */
    private void setUpPlanet(AssetManager manager) {
        planet = new Planet(Dimensions.pantalla_PorcentajeAReal_X(0.5f),
                Dimensions.pantalla_PorcentajeAReal_Y(0.62f), 0.43f, Planet.DIRECTION.OPPOSITE, manager);
        planet.setVisited(true);
        planet.setImage(new Texture("planets/planeta_display.png"));
        stage.addActor(planet);
    }

    /* setting up an image displaying a checkmark (level won) */
    private void setUpCheckmarkImage(AssetManager manager) {
        checkmarkImage = new Image(manager.get("iconos/checkmark.png", Texture.class));
        checkmarkImage.setSize(Dimensions.pantalla_PorcentajeAReal_X(0.302f),
                Dimensions.pantalla_PorcentajeAReal_X(0.224f));
        checkmarkImage.setPosition(
                Dimensions.pantalla_PorcentajeAReal_X(0.5f) - checkmarkImage.getWidth() * 0.5f,
                Dimensions.pantalla_PorcentajeAReal_Y(0.617f) - checkmarkImage.getHeight() * 0.5f);
    }

    /* setting up an image displaying a X (level lost) */
    private void setUpXImage(AssetManager manager) {
        xImage = new Image(manager.get("iconos/x.png", Texture.class));
        xImage.setSize(Dimensions.pantalla_PorcentajeAReal_X(0.302f),
                Dimensions.pantalla_PorcentajeAReal_X(0.224f));
        xImage.setPosition(
                Dimensions.pantalla_PorcentajeAReal_X(0.5f) - xImage.getWidth() * 0.5f,
                Dimensions.pantalla_PorcentajeAReal_Y(0.617f) - xImage.getHeight() * 0.5f);
    }

    private void setUpLevelLabel() {
        BitmapFont fontLbs = FontGenerator.getFont(44);
        Label.LabelStyle lbs = new Label.LabelStyle();
        lbs.font = fontLbs;
        lbs.fontColor = new Color(0,0,0,1);
        levelLabel = new Label("", lbs);
        levelLabel.setPosition(Dimensions.pantalla_PorcentajeAReal_X(0.5f) - levelLabel.getWidth() * 0.5f,
                Dimensions.pantalla_PorcentajeAReal_Y(0.617f) - levelLabel.getHeight() * 0.5f);
        levelLabel.setAlignment(Align.center);
        stage.addActor(levelLabel);
    }

    private void setUpNextLevelButton() {
        nextLevelBtn = new TextButton("", tbs);
        nextLevelBtn.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LevelsManager.setNextLevel();
                game.setScreen(game.gameScreen);
            }
        });
        nextLevelBtn.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT * 0.075f);
        nextLevelBtn.setPosition(Values.SCREEN_WIDTH * 0.5f - nextLevelBtn.getWidth() * 0.5f,
                Dimensions.pantalla_PorcentajeAReal_Y(0.38f));
        stage.addActor(nextLevelBtn);
    }

    private void setUpPlayAgainBtn() {
        TextButton playAgainBtn = new TextButton(Strings.PLAYAGAIN_BTN, tbs);
        playAgainBtn.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.gameScreen);
            }
        });
        playAgainBtn.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT * 0.075f);
        playAgainBtn.setPosition(Values.SCREEN_WIDTH * 0.5f - playAgainBtn.getWidth() * 0.5f,
                Dimensions.pantalla_PorcentajeAReal_Y(0.27f));
        stage.addActor(playAgainBtn);
    }

    private void setUpNextExtraLevelButton() {
        nextExtraLevelBtn = new TextButton("", tbsExtra);
        nextExtraLevelBtn.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                LevelsManager.setNextExtraLevel();
                game.setScreen(game.gameScreen);
            }
        });
        nextExtraLevelBtn.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT * 0.075f);
        nextExtraLevelBtn.setPosition(Values.SCREEN_WIDTH * 0.5f - nextExtraLevelBtn.getWidth() * 0.5f,
                Dimensions.pantalla_PorcentajeAReal_Y(0.16f));
    }

    private void setUpToast() {
        extraLevelsToast = new Toast(Strings.EXTRA_LEVELS_UNLOCKED, new Toast.ToastClickListener() {
            @Override
            public void onClick() {
                game.setScreen(game.levelsScreen);
            }
        });
        newCatToast = new Toast(Strings.CAT_UNLOCKED, new Toast.ToastClickListener() {
            @Override
            public void onClick() {
                game.setScreen(game.settingsScreen);
            }
        });
        stage.addActor(extraLevelsToast);
        stage.addActor(newCatToast);
        stage.addActor(extraLevelsToast.hideButton);
        stage.addActor(newCatToast.hideButton);
    }

    private void setUpBackground() {
        background = new Background();
        background.addBackgroundImage("winner", new Texture("fondo_winner_2.png"));
        background.addBackgroundImage("loser", new Texture("fondo_loser_2.png"));
    }

    private void setUpTextButtonStyle(AssetManager manager) {
        tbs = new TextButton.TextButtonStyle();
        tbs.up = new TextureRegionDrawable(
                new TextureRegion(manager.get("sombra.png", Texture.class)));
        tbs.down = new TextureRegionDrawable(
                new TextureRegion(manager.get("blanco_transparente.png", Texture.class)));
        tbs.font = font;
        tbsExtra = new TextButton.TextButtonStyle();
        tbsExtra.up = new TextureRegionDrawable(
                new TextureRegion(manager.get("sombra_oscura.png", Texture.class)));
        tbsExtra.down = new TextureRegionDrawable(
                new TextureRegion(manager.get("blanco_transparente.png", Texture.class)));
        tbsExtra.font = font;
    }

    /* function to update the score/time of a level on its button on the levels screen  */
    private void updateScoreButtonTime() {
        game.levelsScreen.updateButtonScore(sectionIndex, levelIndex);
    }

    @Override
    public void show() {
        game.showAd();
        levelIndex = LevelsManager.getCurrentLevel(); // level that was just finished
        sectionIndex = LevelsManager.getCurrentSection(); // section of the level that was just finished
        updateScoreButtonTime();

        if (LevelsManager.getWinnerState()) { // the lvel was won
            setWinnerScreen();
        }
        else { // the lvel was lost
            setLoserScreen();
        }

        extraLabel.remove();
        if (LevelsManager.getExtraState()) { // the level that was just finished was an extra lvel
            stage.addActor(extraLabel);
        }
        else {
            extraLabel.remove();
        }

        updateTimeLabel();

        int levelNumber = LevelsManager.getLevelNumber(); // the actual level number displayed on the levels screen  // eg. the number of the first level of the second section will be "6" if the first section has 5 levels (levels "1" -> "5")

        levelLabel.remove();
        stage.addActor(levelLabel);

        levelLabel.setText(Integer.toString(levelNumber));

        int nextNormalLevel = LevelsManager.getNextNormalLevelNumber();
        nextLevelBtn.setText(Strings.GOTOLEVEL_BTN + Integer.toString(nextNormalLevel));

        if (LevelsManager.isLastLevel() ||
                LevelsManager.isCurrentSectionLast()
                && LevelsManager.getExtraState()) {
            nextLevelBtn.remove();  // last level of the game... so no there is no "next level"
        }
        else {
            stage.addActor(nextLevelBtn);
        }

        if (LevelsManager.justUnlockedCurrentSection()) { // the section was just unlocked (every regular level was successfully finished)
            extraLevelsToast.show();
            game.levelsScreen.setExtraLevelsButtons(sectionIndex); // unlocking extra levels from the levels screen
        }

        nextExtraLevelBtn.remove();
        if (LevelsManager.isExtraLevelNext()) {
            stage.addActor(nextExtraLevelBtn);
            int nextExtraLevelNumber = LevelsManager.getNextExtraLevelNumber();
            nextExtraLevelBtn.setText(Strings.GOTOLEVEL_EXTRA_BTN + nextExtraLevelNumber);
        }

        if (LevelsManager.justCompletedCurrentSection()) { // the section was just unlocked (every extra level was successfully finished)
            newCatToast.show();
            game.settingsScreen.unlockCat(sectionIndex + 1);
        }

        Gdx.input.setInputProcessor(stage);
    }

    private void setWinnerScreen(){
        background.switchImage("winner");
        planet.setCurrentDirection(Planet.DIRECTION.NORMAL);
        planet.setSpeed(0.7f);
        xImage.remove();
        stage.addActor(checkmarkImage);
    }

    private void setLoserScreen(){
        background.switchImage("loser");
        planet.setCurrentDirection(Planet.DIRECTION.OPPOSITE);
        planet.setSpeed(0.33f);
        checkmarkImage.remove();
        stage.addActor(xImage);
    }

    private void updateTimeLabel() {
        if (LevelsManager.getWinnerState()) {
            float time = LevelsManager.setCompletedTime();
            DecimalFormat df = new DecimalFormat("####0.00");
            if (LevelsManager.getNewRecord()) {
                timeLabel.setText(Strings.NEW_RECORD_LABEL + ": " + df.format(time)+"s");
            }
            else {
                timeLabel.setText(df.format(time)+"s");
            }
        }
        else {
            timeLabel.setText("");
        }
    }

    @Override
    public void hide() {
        extraLevelsToast.hide(0);
        extraLevelsToast.act(1);
        newCatToast.hide(0);
        newCatToast.act(1);
        stage.addActor(nextLevelBtn);
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f,0.0f,0.0f,0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        background.act(delta);
        stage.getBatch().begin();
        background.draw(stage.getBatch(), 1);
        stage.getBatch().end();
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

}
