package com.planetas4.game.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.planetas4.game.Utils.FontGenerator;

import java.text.DecimalFormat;

/*
*
*    Button that displays a score through a white line at the bottom
*
* */

public class UnlockableButton extends Actor {

    private ShapeRenderer shapeRenderer;
    private Label label;
    private Label labelTime;
    private Color color, normalColor, pressedColor;
    public enum SCORE {NONE, LOW, MEDIUM, HIGH}
    private float score;

    public UnlockableButton(String text, Color newColor) {
        this.normalColor = newColor;
        this.pressedColor = new Color(0.9f, 0.9f, 0.9f, 0.185f);
        this.color = normalColor;
        shapeRenderer = new ShapeRenderer();
        setLabel(text);
        setLabelTime();
        score = 0.0f;
        this.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                color = pressedColor;
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                color = normalColor;
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    public UnlockableButton(String text){
        this(text, new Color(0.01f, 0.01f, 0.03f, 0.35f));
    }

    public void setScore(SCORE score) {
        switch (score) {
            case LOW:
                this.score = 0.33f;
                break;
            case MEDIUM:
                this.score = 0.66f;
                break;
            case HIGH:
                this.score = 1.0f;
                break;
            default:
                this.score = 0.0f;
                break;
        }
    }

    private void setLabel(String text){
        LabelStyle lbs = new LabelStyle();
        lbs.font = FontGenerator.getFont(23);
        label = new Label(text, lbs);
        label.setPosition(getX(), getY());
    }

    private void setLabelTime() {
        LabelStyle lbs = new LabelStyle();
        lbs.font = FontGenerator.getFont(17, new Color(255, 255,255, 0.14f));
        labelTime = new Label("---", lbs);
    }

    public void setTime(float time) {
        LabelStyle lbs = new LabelStyle();
        lbs.font = FontGenerator.getFont(14, new Color(255, 255,255, 0.33f));
        DecimalFormat df = new DecimalFormat("####0.00");
        labelTime = new Label(df.format(time)+"s", lbs);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        label.setPosition(getX() + (getWidth() - label.getWidth()) * 0.5f,
                getY() + (getHeight() - label.getHeight()) * 0.65f);
        labelTime.setPosition(getX() + (getWidth() - labelTime.getWidth()) * 0.5f,
                getY() + (getHeight() - labelTime.getHeight()) * 0.17f);

        shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.setColor(pressedColor);
        shapeRenderer.rect(getX() + getWidth() * 0.05f, getY() + getHeight() * 0.12f,
                getWidth() * 0.9f * score, getHeight() * 0.1f);
        shapeRenderer.setColor(color);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
        label.draw(batch, parentAlpha);
        labelTime.draw(batch, parentAlpha);
    }
}