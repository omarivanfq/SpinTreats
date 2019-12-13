package com.planetas4.game.Treats;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.planetas4.game.Cat;
import com.planetas4.game.Managers.TimeManager;

import java.util.ArrayList;
import java.util.List;

public class TreatRevela extends Treat {

    private List<Treat> treats;
    private String id;
    private TimeManager timeManager;
    private boolean revelando;
    private float tiempoMuestra;

    public TreatRevela(float x, float y, String id, AssetManager manager){
        super(x, y, manager);
        treats = new ArrayList<Treat>();
        TextureRegion texReg = new TextureRegion(manager.get("treats/treatRevela.png", Texture.class));
        image = new Image(texReg);
        image.setPosition(getX(), getY());
        image.setSize(getWidth(), getHeight());
        this.id = id;
        tiempoMuestra = 2.2f;
        timeManager = new TimeManager(tiempoMuestra);
        revelando = false;
        TextureAtlas dulceAtlas = manager.get("destellos/spark_revela.pack", TextureAtlas.class);
        //    Array<TextureAtlas.AtlasRegion> dulceRegions = new Array<TextureAtlas.AtlasRegion>(dulceAtlas.getRegions());
        //  dulceRegions.sort(new RegionComparator());
        sparkleAnimation = new Animation(1.0f / 10.0f, dulceAtlas.getRegions(), Animation.PlayMode.NORMAL);

    }

    public String getId() { return id; }

    public boolean isRevealing() {
        return revelando;
    }

    public float getTiempoMuestra() { return tiempoMuestra; }

    public void agregarDulce(Treat treat) {
        treats.add(treat);
        treat.hide();
    }
    public List<Treat> getTreats() { return treats; }

    @Override
    public void act(float delta) {
        super.act(delta);
        timeManager.act(delta);
        if (revelando && timeManager.remainingTimePercentage() <= 0) {
            revelando = false;
            ocultarDulces();
            this.setCollected(false);
            this.setVisible(true);
        }
    }

    @Override
    public boolean collides(Cat aCat) {
        if (super.collides(aCat)) {
            for (Treat treat : treats) {
                treat.show();
            }
            timeManager.restart();
            revelando = true;
            return true;
        }
        return false;
    }

    private void ocultarDulces() {
        for (Treat treat : treats) {
            treat.hide();
        }
    }

    @Override
    public void restart() {
        super.restart();
        ocultarDulces();
    }
}
