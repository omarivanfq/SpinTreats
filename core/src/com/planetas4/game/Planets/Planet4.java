package com.planetas4.game.Planets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.planetas4.game.Cat;

public class Planet4 extends Planet {

    private float bounceAngle;
    private float landingAngle;
    private boolean expanding;
    private BounceHandler bounceHandler = null;

    public Planet4(AssetManager manager) {
        super(manager);
        bounceAngle = 0.0f;
        expanding = false;
        this.texReg = new TextureRegion(new Texture("planets/planeta4.png"));
    }

    public Planet4(float xRel, float yRel, float size, DIRECTION DIRECTION, AssetManager manager) {
        super(xRel, yRel, size, DIRECTION, manager);
        bounceAngle = 0.0f;
        expanding = false;
        this.texReg = new TextureRegion(new Texture("planets/planeta4.png"));
    }

    public interface BounceHandler {
        void onBounce(Planet planet, Cat cat);
    }

    public void setBounceHandler(BounceHandler bounceHandler) {
        this.bounceHandler = bounceHandler;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (expanding && scaleX < 1.15){
            scaleX += delta * 2;
            scaleY = scaleX;
        }
        else if (expanding && scaleX >= 1.15){
            expanding = false;
        }
        else if (!expanding && scaleX > 1) {
            scaleX -= delta;
            scaleY = scaleX;
        }

    }

    public void updateVisit(Cat cat) {
        if (mustLeave()) {
            setVisited(false);
            cat.rotate(this);
            cat.setFloating(true);
            if (bounceHandler != null) {
                bounceHandler.onBounce(this, cat);
            }
        }
    }

    @Override
    public void coordinateWithCat(Cat cat) {
        super.coordinateWithCat(cat);
        if (visited) landingAngle = angle;
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (visited) {
            landingAngle = angle;
            this.addAction(Actions.scaleBy(1.5f, 1.5f));
            expanding = true;
        }
    }

    private boolean mustLeave(){
        return visited && Math.abs(angle - landingAngle) >= bounceAngle;
    }

}
