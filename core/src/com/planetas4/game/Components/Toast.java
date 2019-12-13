package com.planetas4.game.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.planetas4.game.Utils.Dimensions;
import com.planetas4.game.Utils.FontGenerator;

/*
*
*   An android-like toast implementation
*
* */

public class Toast extends Actor{

    private ShapeRenderer shapeRenderer;
    private Label label;
    private boolean hidden;
    private float time;
    public ImageButton hideButton;

    public Toast(String text, ToastClickListener toastClickListener) {
        setUpLabel(text);
        setUpHideButton();
        setHeight(60);
        setWidth(Gdx.graphics.getWidth());
        setY(-getHeight());
        setX(-getHeight());
        shapeRenderer = new ShapeRenderer();
        Color backgroundColor = new Color(0.01f, 0.01f, 0.03f, 0.7f);
        shapeRenderer.setColor(backgroundColor);
        hidden = true;
        time = 0.4f;
        final ToastClickListener listener = toastClickListener;
        this.addCaptureListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                listener.onClick();
            }
        });
    }

    private void setUpHideButton(){
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture("iconos/x.png")));
        hideButton = new ImageButton(drawable);
        hideButton.setWidth(15);
        hideButton.setHeight(15);
        hideButton.addCaptureListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                hide();
            }
        });
    }

    private void setUpLabel(String text) {
        Label.LabelStyle lbs;
        BitmapFont font = FontGenerator.getFont(18);
        lbs = new Label.LabelStyle();
        lbs.font = font;
        lbs.fontColor = new Color(255, 255, 255, 0.75f);
        label = new Label(text, lbs);
    }

    void show(float time){
        if (hidden) {
            this.addAction(Actions.moveBy(0, getHeight(), time));
            this.label.addAction(Actions.moveBy(0, getHeight(), time));
            this.hideButton.addAction(Actions.moveBy(0, getHeight(), time));
        }
        hidden = false;
    }

    public void show() {
        show(this.time);
    }

    public void hide(float time) {
        if (!hidden) {
            this.addAction(Actions.moveBy(0, -getHeight(), time));
            this.label.addAction(Actions.moveBy(0, -getHeight(), time));
            this.hideButton.addAction(Actions.moveBy(0, -getHeight(), time));
        }
        hidden = true;
    }

    void hide() {
        hide(this.time);
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
        label.setPosition(Dimensions.pantalla_PorcentajeAReal_X(0.435f) - label.getWidth() * 0.5f,
                (getHeight() * 0.5f - label.getHeight() * 0.5f) - getHeight());
        hideButton.setPosition(Dimensions.pantalla_PorcentajeAReal_X(0.92f) - hideButton.getWidth() * 0.5f,
                (getHeight() * 0.5f - hideButton.getHeight() * 0.5f) - getHeight());
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        label.act(delta);
        hideButton.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        label.draw(batch, parentAlpha);
        hideButton.draw(batch, parentAlpha);
    }

    public interface ToastClickListener {
        void onClick();
    }

}
