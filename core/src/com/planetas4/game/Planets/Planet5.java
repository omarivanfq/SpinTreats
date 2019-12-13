package com.planetas4.game.Planets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.planetas4.game.Cat;
import com.planetas4.game.Components.Spark;

public class Planet5 extends Planet {

    private String transferId; // id of the planet the cat is going to be transferred to
    private float stayTime;
    private float elapsedStayTime;
    private Spark spark;
    private float initialAngle;
    private TransferHandler transferHandler = null;

    public interface TransferHandler {
        void onTransfer(String destinationId);
    }

    public Planet5(AssetManager manager) {
        super(manager);
        this.elapsedStayTime = 0;
        this.stayTime = 2.5f;
        this.texReg = new TextureRegion(new Texture("planets/planeta5.png"));
        spark = new Spark("destellos_gato/destellosGato.txt", 20, 20);
    }

    public Planet5(float x, float y, float sizeRel, DIRECTION DIRECTION,
                   String transferId, float stayTime, AssetManager manager) {
        super(x, y, sizeRel, DIRECTION, manager);
        this.stayTime = stayTime;
        this.transferId = transferId;
        this.elapsedStayTime = 0;
        this.texReg = new TextureRegion(new Texture("planets/planeta5.png"));
        spark = new Spark("destellos_gato/destellosGato.txt", 20, 20);
    }

    public void setTransferHandler(TransferHandler transferHandler) {
        this.transferHandler = transferHandler;
    }

    public void setInitialAngle(float initialAngle) {
        this.initialAngle = initialAngle;
    }
    public void setStayTime(float stayTime) {
        this.stayTime = stayTime;
    }
    public void setTransferId(String transferId) {this.transferId = transferId;}

    @Override
    public void act(float delta) {
        super.act(delta);
        if (visited) {
            this.elapsedStayTime += delta;
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        spark.draw(batch, parentAlpha);
    }

    public String getTransferId() {
        return transferId;
    }

    private boolean mustTransfer(){
        return elapsedStayTime >= stayTime;
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (visited){
            this.addAction(Actions.alpha(0, 0));
        }
        else{
            elapsedStayTime = 0;
        }
    }

    public void updateVisit(Planet planetTo, Cat cat) {
        if (mustTransfer()){
            spark.setPosition(cat.getX(), cat.getY());
            spark.sparkles();
            setVisited(false);
            glow();
            planetTo.glow();
            planetTo.setAngle(initialAngle);
            if (transferHandler != null) {
                transferHandler.onTransfer(this.transferId);
            }
        }
    }

    @Override
    public void restart() {
        super.restart();
        this.elapsedStayTime = 0;
    }
}
