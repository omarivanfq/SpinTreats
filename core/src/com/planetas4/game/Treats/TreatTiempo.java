package com.planetas4.game.Treats;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.planetas4.game.Cat;
import com.planetas4.game.Managers.TimeManager;

public class TreatTiempo extends Treat {

    private TimeManager timeManager;

    public TreatTiempo(float x, float y, TimeManager timeManager, AssetManager manager) {
        super(x, y, manager);
        TextureRegion texReg = new TextureRegion(manager.get("treats/treatTiempo.png", Texture.class));
        image = new Image(texReg);
        image.setPosition(getX(), getY());
        image.setSize(getWidth(), getHeight());
        this.timeManager = timeManager;
        TextureAtlas dulceAtlas = manager.get("destellos/spark_tiempo.pack", TextureAtlas.class);
        sparkleAnimation = new Animation(1.0f / 10.0f, dulceAtlas.getRegions(), Animation.PlayMode.NORMAL);
    }

    @Override
    public boolean collides(Cat aCat) {
        if (super.collides(aCat)) {
            timeManager.goForward(-0.5f);
            return true;
        }
        return  false;
    }
}
