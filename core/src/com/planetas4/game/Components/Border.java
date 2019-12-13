package com.planetas4.game.Components;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.planetas4.game.Constants.Values;

/*
* component that shows the border of the game screen when the cat collides on it it can change
*  its brightness
* */

public class Border extends Actor {

    private float alpha;
    private TextureRegion texReg;
    private boolean switchBright;
    private Color color;

    Border(AssetManager manager){
        color = Color.WHITE;
        switchBright = true;
        alpha = 0.5f;
        texReg = new TextureRegion(manager.get("borde.png", Texture.class));
    }

    void collides() {
        switchBright = true;
        alpha = 0.8f;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void act(float delta) {

        if (switchBright) {
            alpha += 0.01f;
        }
        else {
            alpha -= 0.01f;
        }

        if (alpha <= 0.25f) {
            switchBright = true;
        }
        else if (alpha >= 0.6f) {
            switchBright = false;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(color.r, color.g, color.b, alpha);
        batch.draw(texReg, 0, 0, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        batch.setColor(Color.WHITE);
    }

    public void dispose(){
     //   texReg.getTexture().dispose();
    }
}
