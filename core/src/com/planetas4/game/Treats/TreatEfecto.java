package com.planetas4.game.Treats;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import java.util.ArrayList;
import java.util.List;

public class TreatEfecto extends Treat {

    private List<Treat> treats;
    private List<TreatEfecto> efectoTreats;
    private List<TreatTiempo> tiempoTreats;
    private int collectionIndex;
    private String id;

    public TreatEfecto(float x, float y, String id, AssetManager manager) {
        super(x, y, manager);
        this.id = id;
        treats = new ArrayList<Treat>();
        efectoTreats = new ArrayList<TreatEfecto>();
        tiempoTreats = new ArrayList<TreatTiempo>();
        TextureRegion texReg = new TextureRegion(manager.get("treats/treatEfecto.png", Texture.class));
        image = new Image(texReg);
        image.setPosition(getX(), getY());
        image.setSize(getWidth(), getHeight());

        TextureAtlas dulceAtlas = manager.get("destellos/spark_efecto.pack", TextureAtlas.class);
    //    Array<TextureAtlas.AtlasRegion> dulceRegions = new Array<TextureAtlas.AtlasRegion>(dulceAtlas.getRegions());
      //  dulceRegions.sort(new RegionComparator());
        sparkleAnimation = new Animation(1.0f / 10.0f, dulceAtlas.getRegions(), Animation.PlayMode.NORMAL);

    }

    public String getId() {return id;}

    public void agregarDulce(Treat treat){
        treats.add(treat);
    }

    public void agregarDulceEfecto(TreatEfecto dulce){
        efectoTreats.add(dulce);
    }

    public void agregarDulceTiempo(TreatTiempo dulce){
        tiempoTreats.add(dulce);
    }

    public List<TreatTiempo> getTiempoTreats() {
        return tiempoTreats;
    }

    public List<Treat> getTreats(){
        return treats;
    }

    public  List<TreatEfecto> getEfectoTreats(){
        return efectoTreats;
    }

    public  void setCollectionIndex(int collectionIndex) {
        this.collectionIndex = collectionIndex;
    }

    public int getCollectionIndex() {
        return collectionIndex;
    }

    @Override
    public void restart() {
        super.restart();
        for (TreatTiempo treatTiempo : tiempoTreats) {
            treatTiempo.restart();
            treatTiempo.remove();
        }
    }

    TreatEfecto getTreatEfectoById(String id){
        for (TreatEfecto dulceEfecto : efectoTreats){
            if (dulceEfecto.getId().matches(id))
                return dulceEfecto;
            TreatEfecto d = dulceEfecto.getTreatEfectoById(id);
            if (d != null)
                return d;
        }
        return null;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }
}
