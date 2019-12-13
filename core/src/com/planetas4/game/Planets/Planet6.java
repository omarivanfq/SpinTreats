package com.planetas4.game.Planets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.planetas4.game.Cat;

public class Planet6 extends Planet {

    private boolean adjusting;
    private Vector2 pressedPoint;
    private float pressedAngle;
    private float texturePressedAngle;
    private TextureRegion glowTexture;

    public Planet6(AssetManager manager){
        super(manager);
        adjusting = false;
        this.texReg = new TextureRegion(manager.get("planets/planeta6.png", Texture.class));
        this.glowTexture = new TextureRegion(manager.get("planets/planeta_6_glow.png", Texture.class));
        pressedPoint = new Vector2(0,0);
    }

    public Planet6(float x, float y, float sizeRel, DIRECTION DIRECTION, AssetManager manager) {
        super(x, y, sizeRel, DIRECTION, manager);
        adjusting = false;
        this.texReg = new TextureRegion(manager.get("planets/planeta6.png", Texture.class));
        this.glowTexture = new TextureRegion(manager.get("planets/planeta_6_glow.png", Texture.class));
        pressedPoint = new Vector2(0,0);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!adjusting && visited && Gdx.input.justTouched()){
            adjusting = true;
            pressedPoint.set(Gdx.input.getX(), Gdx.input.getY());
            pressedAngle = this.angle;
            texturePressedAngle = this.textureAngle;
        }
        if (visited && Gdx.input.isTouched()) {
            updateAngle();
        }
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (!visited) adjusting = false;
        else if (Gdx.input.isTouched()) adjusting = true;
    }

    private void updateAngle(){
        double anglePerPixel = 360.0 / Gdx.graphics.getHeight() * 2;
        this.angle = pressedAngle + (float) ((pressedPoint.y - Gdx.input.getY()) * anglePerPixel);
        this.textureAngle = texturePressedAngle + (float)((pressedPoint.y - Gdx.input.getY()) * anglePerPixel);
    }

    @Override
    public void restart() {
        super.restart();
        adjusting = false;
    }

    private boolean mustLeave() {
        return visited && adjusting && !Gdx.input.isTouched();
    }

    public void updateVisit(Cat cat) {
        if (mustLeave()){
            setVisited(false);
            cat.rotate(this);
            cat.setFloating(true);
        }
    }

    @Override
    public void rotate(float delta) {
       if (!visited){
           super.rotate(delta);
       }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (adjusting)
            batch.draw(glowTexture, getX(), getY(), radius, radius, getWidth(), getHeight(), scaleX, scaleY, textureAngle);
        super.draw(batch, parentAlpha);
    }

    public void pause() {
        adjusting = false;
    }
}
