package com.planetas4.game.Treats;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.planetas4.game.Cat;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Managers.TimeManager;
import com.planetas4.game.Planets.Planet;

import java.util.ArrayList;
import java.util.List;

public class TreatHabilita extends Treat {

    List<Planet> planets;
    private boolean enabling;
    private String id;
    private float time;
    private TimeManager timeManager;

    public TreatHabilita(float x, float y, String id, AssetManager manager) {
        super(x, y, manager);
        this.id = id;
        planets = new ArrayList<Planet>();
        TextureRegion texReg = new TextureRegion(manager.get("treats/treatHabilita.png", Texture.class));
        image = new Image(texReg);
        image.setPosition(getX(), getY());
        image.setSize(getWidth(), getHeight());
        enabling = false;
        time = Values.HABILITA_TIME_DEFAULT;
        timeManager = new TimeManager(time);
        TextureAtlas dulceAtlas = manager.get("destellos/spark_habilita.pack", TextureAtlas.class);
        //    Array<TextureAtlas.AtlasRegion> dulceRegions = new Array<TextureAtlas.AtlasRegion>(dulceAtlas.getRegions());
        //  dulceRegions.sort(new RegionComparator());
        sparkleAnimation = new Animation(1.0f / 10.0f, dulceAtlas.getRegions(), Animation.PlayMode.NORMAL);

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (enabling) {
            timeManager.act(delta);
            if (timeManager.remainingTimePercentage() <= 0) {
                disable();
                setCollected(false);
            }
        }
    }

    public void setTime(float time) {
        this.time = time;
        timeManager = new TimeManager(time);
    }

    public String getId() {
        return id;
    }

    public void addPlanet(Planet planet){
        planets.add(planet);
        planet.setAlpha(0.65f);
    }

    @Override
    public boolean collides(Cat aCat) {
        if (super.collides(aCat)){
            enable();
            timeManager.restart();
            return true;
        }
        return false;
    }

    private void disable() {
        for (Planet planet : planets){
            planet.disable();
        }
        enabling = false;
    }

    public void enable(){
        for (Planet planet : planets){
            planet.enable();
        }
        enabling = true;
    }

    @Override
    public void restart() {
        super.restart();
        enabling = false;
        disable();
    }
}
