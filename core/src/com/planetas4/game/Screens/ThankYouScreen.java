package com.planetas4.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Components.Background;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Utils.Dimensions;
import com.planetas4.game.Utils.FontGenerator;
import com.planetas4.game.MainGame;
import com.planetas4.game.Planets.Planet;
import com.planetas4.game.Constants.Strings;

public class ThankYouScreen extends ScreenAdapter {

    private Stage stage;
    private Viewport viewport;
    private MainGame game;
    private float animationTime;
    private Animation anim;

    public ThankYouScreen(final MainGame game, AssetManager manager) {
        this.game = game;
        viewport = new FitViewport(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        stage = new Stage(viewport);
        Background background = new Background();
        background.addBackgroundImage("thanks", new Texture("pantalla-gracias-fondo.png"));
        background.switchImage("thanks");
        Planet planet = new Planet(
                Dimensions.pantalla_PorcentajeAReal_X(0.377f),
                Dimensions.pantalla_PorcentajeAReal_Y(0.7719f),
                0.4f,
                Planet.DIRECTION.NORMAL,
                manager
        );
        planet.setSpeed(0.1f);
        planet.setVisited(true);

        BitmapFont font = FontGenerator.getFont(25);
        TextButton.TextButtonStyle tbs = new TextButton.TextButtonStyle();

        // tail_animation/tail_anim.txt
        TextureAtlas tailAtlas = manager.get("tail_animation/tail-anim.txt", TextureAtlas.class);
        anim = new Animation(1.0f / 10.0f, tailAtlas.getRegions(), Animation.PlayMode.LOOP);

        tbs.up = new TextureRegionDrawable(
                new TextureRegion(new Texture("sombra.png")));
        tbs.down = new TextureRegionDrawable(
                new TextureRegion(new Texture("blanco_transparente.png")));
        tbs.font = font;
        TextButton menuButton = new TextButton(Strings.THANK_YOU_OK_BTN, tbs);

        menuButton.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT * 0.075f);
        menuButton.setPosition(Values.SCREEN_WIDTH * 0.5f - menuButton.getWidth() * 0.5f, 85);
        menuButton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.finalScreen);
            }
        });
        stage.addActor(background);
        stage.addActor(planet);
        stage.addActor(menuButton);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        animationTime += delta;
        TextureRegion frame = (TextureRegion) anim.getKeyFrame(animationTime);
        stage.act();
        stage.draw();
        stage.getBatch().begin();
        stage.getBatch().draw(frame, 76, 172, 100, 100);
        stage.getBatch().end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        super.hide();
        Gdx.input.setInputProcessor(null);
    }

}
