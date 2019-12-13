package com.planetas4.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.planetas4.game.MainGame;

/*
*    splash screen
*/

public class LoadingScreen extends ScreenAdapter {

    private MainGame mainGame;
    private Texture splash;
    private SpriteBatch spriteBatch;

    public LoadingScreen(MainGame mainGame){
        this.mainGame = mainGame;
        splash = new Texture("splash-screen.png");
        spriteBatch = new SpriteBatch();
    }

    @Override
    public void show() {
        super.show();
        mainGame.loadAssetManager();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(56/255, 32/255, 115/255, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        spriteBatch.draw(splash, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        spriteBatch.end();
        if (mainGame.getScreen() != mainGame.homeScreen &&
                mainGame.getManager().update()) {
            mainGame.prepare();
            mainGame.setScreen(mainGame.homeScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
    }
}