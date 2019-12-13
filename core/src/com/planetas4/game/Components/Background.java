package com.planetas4.game.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.planetas4.game.Constants.Values;

import java.util.HashMap;

/*
* Component that shows the background of a screen, it can load and change between several images
*/

public class Background extends Actor{

    private Image currentImage;
    private Image darkImage;
    private HashMap images = new HashMap();
    private ShapeRenderer shapeRenderer;

    public Background(){

        /* the shape renderer is used to place a red color over the background image
           when damage is happening to the cat */
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(new Color(0.26f, 0.01f, 0.03f, 0.0f));

        /* a hash is used to store the different images the background can use */
        images = new HashMap();

        /* loading and positioning the default background image */
        images.put("default", new Image(new Texture("fondo.jpg")));
        this.currentImage = (Image)(images.get("default"));
        this.currentImage.setPosition(0, -20);
        this.currentImage.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT * 1.2f);
        this.currentImage.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, 10, 0.88f),
                Actions.moveBy(0, -10, 0.88f))));

        /* loading and positioning a black background image that is going to be used when
         *   planet7s are present */
        this.darkImage = new Image(new Texture("negro.png"));
        this.darkImage.setPosition(0, 0);
        this.darkImage.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        this.darkImage.addAction(Actions.fadeOut(0.0f));
        this.darkImage.act(0);
    }

    public void addBackgroundImage(String key, Texture texture){
        Image image = new Image(texture);
        image.setPosition(0, 0);
        image.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        image.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, 10, 0.88f),
                Actions.moveBy(0, -10, 0.88f))));
        images.put(key, image);
    }

    public void switchImage(String key) {
        currentImage = (Image) images.get(key);
    }

    public void goDark(float time) {
        this.darkImage.clearActions();
        this.darkImage.addAction(Actions.fadeIn(time));
    }

    private void goDark() {
        goDark(0.22f);
    }

    void setImage(Texture texture) {
        this.currentImage.setDrawable(new SpriteDrawable(new Sprite(texture)));
    }

    public void goBright(float time){
        this.darkImage.clearActions();
        this.darkImage.addAction(Actions.fadeOut(time));
    }

    public void goBright() {
        goBright(0.22f);
    }

    public void startDamage() {
        shapeRenderer.getColor().a = 0.25f;
    }
    public void stopDamage() {
        shapeRenderer.getColor().a = 0.0f;
    }

    public void goBrightAndThenDark(float tiempo) {
        this.darkImage.addAction(Actions.sequence(
                Actions.fadeOut(0.22f),
                Actions.delay(tiempo - 0.44f > 0? tiempo - 0.44f : 0),
                Actions.fadeIn(0.22f)));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        currentImage.act(delta);
        darkImage.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        currentImage.draw(batch, parentAlpha);
        darkImage.draw(batch, parentAlpha);
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(0, 0, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

}
