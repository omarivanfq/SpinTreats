package com.planetas4.game.Planets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Planet7 extends Planet {

    private TextureRegion glowTexture;

    public Planet7(AssetManager manager) {
        super(manager);
        this.texReg = new TextureRegion(new Texture("planets/planeta7.png"));
        this.glowTexture = new TextureRegion(manager.get("planets/planeta_7_glow.png", Texture.class));
    }

    public Planet7(float x, float y, float sizeRel, DIRECTION DIRECTION, AssetManager manager) {
        super(x, y, sizeRel, DIRECTION, manager);
        this.texReg = new TextureRegion(new Texture("planets/planeta7.png"));
        this.glowTexture = new TextureRegion(manager.get("planets/planeta_7_glow.png", Texture.class));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (visited) {
            batch.draw(glowTexture, getX(), getY(), radius, radius, getWidth(), getHeight(), scaleX * 1.04f, scaleY * 1.04f, textureAngle);
        }
        super.draw(batch, parentAlpha);
    }
}
