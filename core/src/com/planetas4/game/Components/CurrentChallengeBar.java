package com.planetas4.game.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;

/*
*
* component that indicates with white dots (checkboxes) in the bottom of the screen
*   which is the current challenge from the current level
*
* */

public class CurrentChallengeBar extends Actor {

    private float space;
    private float checkboxesSpace;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;
    private List<CheckBox> checkboxes;

    public CurrentChallengeBar(Viewport viewport, int challengesQuantity, float space){
        checkboxesSpace = 0.7f;
        setSize(viewport.getWorldWidth(), viewport.getWorldHeight() * space);
        setPosition(0, 0);
        float widthCh = viewport.getWorldWidth() * checkboxesSpace / (challengesQuantity + 1);
        float heightCh = viewport.getWorldHeight() * space * 0.3f;
        float leftPad = viewport.getWorldWidth() * (1 - checkboxesSpace) * 0.5f;
        float yPos = getY() + viewport.getWorldHeight() * space * 0.5f;
        checkboxes = new ArrayList<CheckBox>();

        for (int i = 0; i < challengesQuantity; i++){
            checkboxes.add(new CheckBox());
            checkboxes.get(i).setSize(heightCh, heightCh);
            checkboxes.get(i).setPosition(leftPad + widthCh * (i + 1), yPos);
        }

        this.viewport = viewport;
        this.space = space;

        shapeRenderer = new ShapeRenderer();
        Color backgroundColor = new Color(0.01f, 0.01f, 0.03f, 0.95f);
        shapeRenderer.setColor(backgroundColor);
    }

    public void setChallengesQuantity(int challengesQuantity) {
        checkboxes.clear();
        float widthCh = viewport.getWorldWidth() * checkboxesSpace / (challengesQuantity + 1);
        float heightCh = viewport.getWorldHeight() * space * 0.3f;
        float leftPad = viewport.getWorldWidth() * (1 - checkboxesSpace) * 0.5f;
        float yPos = getY() + viewport.getWorldHeight() * space * 0.5f;
        for (int i = 0; i < challengesQuantity; i++){
            checkboxes.add(new CheckBox());
            checkboxes.get(i).setSize(heightCh, heightCh);
            checkboxes.get(i).setPosition(leftPad + widthCh * (i + 1), yPos);
        }
    }

    /* activate one checkbox (dot) and let all the others deactivated */
    public void checkOnlyCheckbox(int i){
        for (CheckBox checkbox : checkboxes) {
            checkbox.setPressed(false);
        }
        checkboxes.get(i).setPressed(true);
    }

    public void checkCheckbox(int i){
        checkboxes.get(i).setPressed(true);
    }

    public void restart(){
        for (CheckBox checkBox : checkboxes){
            checkBox.setPressed(false);
        }
    }

    @Override
    public void setHeight(float height) {
        super.setHeight(height);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        for (CheckBox checkBox : checkboxes) {
            checkBox.setY(y + viewport.getWorldHeight() * space * 0.5f);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
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
        for (CheckBox checkBox : checkboxes)
            checkBox.draw(batch, parentAlpha);
    }

    /* a simple implementation of a checkbox */
    private class CheckBox extends Actor{
        private Texture checkboxTexture;
        private boolean pressed;

        CheckBox(){
            pressed = false;
            checkboxTexture = new Texture("checkbox1.png");
            this.addListener(
                new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        press();
                    }
                }
            );
        }

        void press(){
            pressed = !pressed;
        }
        void setPressed(boolean pressed) {
            this.pressed = pressed;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (!pressed)
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0.35f);
            else
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1);
            batch.draw(checkboxTexture,getX() - getWidth() * 0.5f, getY() - getWidth() * 0.5f,
                    getWidth(), getHeight());
        }

        public void dispose() {
            checkboxTexture.dispose();
        }

    }

    public void dispose() {
        for (CheckBox checkbox : checkboxes) {
            checkbox.dispose();
        }
        shapeRenderer.dispose();
    }

}
