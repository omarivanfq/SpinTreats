package com.planetas4.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Components.Background;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Utils.FontGenerator;
import com.planetas4.game.MainGame;
import com.planetas4.game.Constants.Strings;

/*
*
*    Screen that shows the credits of the game
*
* */

public class AboutScreen extends ScreenAdapter {

    private Label.LabelStyle lbs, lbsCredits;
    private Viewport viewport;
    private Stage stage;
    private MainGame game;
    private AssetManager manager;

    public AboutScreen(MainGame game, AssetManager manager) {
        this.game = game;
        this.manager = manager;
        Background background = new Background();
        setUpViewport();
        stage = new Stage(this.viewport);
        stage.addActor(background);
        setUpLabelStyle();
        Label labelTitle = new Label(Strings.DEV_ART_LABEL, lbs);
        labelTitle.setPosition(Values.SCREEN_WIDTH * 0.5f - labelTitle.getWidth() * 0.5f,
                Values.SCREEN_HEIGHT * 0.77f - labelTitle.getHeight() * 0.5f);
        Label labelName = new Label(Strings.DEV_NAME, lbs);
        labelName.setPosition(Values.SCREEN_WIDTH * 0.5f - labelName.getWidth() * 0.5f,
                Values.SCREEN_HEIGHT * 0.71f - labelName.getHeight() * 0.5f);
        Label labelEmail = new Label(Strings.DEV_EMAIL, lbs);
        labelEmail.setPosition(Values.SCREEN_WIDTH * 0.5f - labelEmail.getWidth() * 0.5f,
                Values.SCREEN_HEIGHT * 0.65f - labelEmail.getHeight() * 0.5f);
     /* Label labelUsername = new Label(Strings.USERNAME, lbs);
        labelUsername.setPosition(Values.SCREEN_WIDTH * 0.5f - labelUsername.getWidth() * 0.5f,
                Values.SCREEN_HEIGHT * 0.52f - labelUsername.getHeight() * 0.5f);
        stage.addActor(labelUsername);
     */
        Label labelMusic = new Label(Strings.MUSIC_CREDITS, lbsCredits);
        labelMusic.setWidth(Values.SCREEN_WIDTH * 0.92f);
        labelMusic.setPosition(Values.SCREEN_WIDTH * 0.04f,
                Values.SCREEN_HEIGHT * 0.47f - labelMusic.getHeight() * 0.5f);
        labelMusic.setWrap(true);
        labelMusic.setAlignment(Align.center); // Align

        Label labelSound = new Label(Strings.SOUND_CREDITS, lbsCredits);
        labelSound.setWidth(Values.SCREEN_WIDTH * 0.92f);
        labelSound.setPosition(Values.SCREEN_WIDTH * 0.04f,
                Values.SCREEN_HEIGHT * 0.27f - labelSound.getHeight() * 0.5f);
        labelSound.setWrap(true);
        labelSound.setAlignment(Align.center); // Align

        stage.addActor(labelSound);
        stage.addActor(labelName);
        stage.addActor(labelMusic);
        stage.addActor(labelTitle);
        stage.addActor(labelEmail);
        setUpBackButton();
    }

    private void setUpBackButton() {
        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(new TextureRegion(manager.get("sombra.png", Texture.class)));
        ibs.imageUp = new TextureRegionDrawable(new TextureRegion(manager.get("iconos/back.png", Texture.class)));
        ImageButton goBack = new ImageButton(ibs);
        goBack.setSize(Values.SCREEN_WIDTH * 0.15f, 100);
        goBack.setPosition(0, Values.SCREEN_HEIGHT - goBack.getHeight());
        goBack.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.homeScreen);
            }
        });
        stage.addActor(goBack);
    }

    private void setUpLabelStyle(){
        this.lbs = new Label.LabelStyle();
        this.lbs.font = FontGenerator.getFont(22);
        this.lbs.fontColor = new Color(255, 255, 255, 0.75f);
        this.lbsCredits = new Label.LabelStyle();
        this.lbsCredits.font = FontGenerator.getFont(13);
        this.lbsCredits.fontColor = new Color(255, 255, 255, 0.67f);
    }

    private void setUpViewport(){
        viewport = new FitViewport(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            game.setScreen(game.homeScreen);
        }
        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

}
