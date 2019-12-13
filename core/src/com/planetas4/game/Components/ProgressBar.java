package com.planetas4.game.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.planetas4.game.Constants.Values;

/*
*
* Component that is displayed as a bar that show progression
*
* */

public class ProgressBar extends Actor{

    private ShapeRenderer shapeRenderer;
    private Color color;
    private float extension;
    private float extensionReal;
    private float advancePortion;
    private float angle;

    public ProgressBar(float xRel, float yRel, float w, float h){
        setX(Values.SCREEN_WIDTH * xRel);
        setY(Values.SCREEN_HEIGHT * yRel);
        setWidth(Values.SCREEN_HEIGHT * w);
        setHeight(Values.SCREEN_WIDTH * h);
        shapeRenderer = new ShapeRenderer();
        extension = 1f;
        extensionReal = 1f;
        color = new Color(0.084f, 0.56f, 0.1f, 0.82f);
        advancePortion = 0.005f;
        angle = 0;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void act(float delta) {
        if (extension > extensionReal){
            extension -= advancePortion;
            if (extension < extensionReal) {
                extension = extensionReal;
            }
        }
        else if (extension < extensionReal){
            extension += advancePortion;
            if (extension > extensionReal) {
                extension = extensionReal;
            }
        }
    }

    public void setColor(Color color){
        color.set(color.r, color.g, color.b, 0.56f);
        this.color = color;
    }

    public void add(float valor){
        extensionReal += valor;
        extensionReal = (extensionReal < 0? 0 : extensionReal);
        extensionReal = (extensionReal > 1? 1 : extensionReal);
    }

    public void update(float valor){
        valor = (valor < 0? 0 : valor);
        valor = (valor > 1? 1 : valor);
        extensionReal = valor;
    }

    public void restart() {
        instantUpdate(0.0f);
    }

    void instantUpdate(float value){
        value = (value < 0? 0 : value);
        value = (value > 1? 1 : value);
        extension = value;
    }

    public float getExtension() {
        return extension;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color.r, color.g, color.b, 0.35f);
        shapeRenderer.rect(getX() - getWidth() * 0.5f, getY() - getHeight() * 0.5f,
                getWidth() * 0.5f, getHeight() * 0.5f,
                getWidth() * extension , getHeight(),
                1.0f, 1.0f,
                angle);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

}
