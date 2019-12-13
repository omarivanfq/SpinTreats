package com.planetas4.game.Planets;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.planetas4.game.Cat;
import com.planetas4.game.Constants.Values;

import java.util.ArrayList;
import java.util.List;

public class Planet3 extends Planet {

    private List<Thorn> thorns;
    private AssetManager manager;
    private DamageHandler damageHandler = null;
    private boolean damaging = false;

    public interface DamageHandler {
        void startDamage();
        void doDamage(float delta);
        void stopDamage();
    }

    public Planet3(AssetManager manager) {
        super(manager);
        texReg = new TextureRegion(manager.get("planets/planeta3.png", Texture.class));
        this.thorns = generateThorns(0, manager);
        this.manager = manager;
    }

    public Planet3(float xRel, float yRel, float size, int cuantos, DIRECTION DIRECTION, AssetManager manager) {
        super(xRel, yRel, size, DIRECTION, manager);
        texReg = new TextureRegion(manager.get("planets/planeta3.png", Texture.class));
        this.thorns = generateThorns(cuantos, manager);
        adjustThorns();
        rotateThorns(0);
        this.manager = manager;
    }

    public Planet3(float xRel, float yRel, float size, DIRECTION DIRECTION, AssetManager manager) {
        super(xRel, yRel, size, DIRECTION, manager);
        texReg = new TextureRegion(manager.get("planets/planeta3.png", Texture.class));
        this.thorns = generateThorns(3, manager);
        adjustThorns();
        rotateThorns(0);
        this.manager = manager;
    }

    public void setDamageHandler(DamageHandler damageHandler) {
        this.damageHandler = damageHandler;
    }

    public void setThornsQuantity(int quantity){
        this.thorns = generateThorns(quantity, manager);
        adjustThorns();
        rotateThorns(0); // to position from start
    }

    private void adjustThorns() {
        float a = 0;
        for (Thorn thorn : thorns) {
            a += 360 / thorns.size();
            thorn.angle = a;
        }
    }

    private List<Thorn> generateThorns(int quantity, AssetManager manager){
        List<Thorn> thorns = new ArrayList<Thorn>();
        for (int i = 0; i < quantity; i++){
            thorns.add(new Thorn(this.getSpeed(), manager));
        }
        return thorns;
    }

    public List<Thorn> getThorns() {
        return thorns;
    }

    private void rotateThorns(float delta) {
        for (Thorn thorn : thorns) {
            thorn.rotate(delta, center, radius);
        }
    }

    @Override
    public void setSpeed(float speed) {
        super.setSpeed(speed);
        for (Thorn thorn : thorns) {
            thorn.setSpeed(speed);
        }
    }

    private boolean collidesWithThorns(Cat cat) {
        for (Thorn thorn : thorns) {
            if (thorn.collides(cat)) {
                return true;
            }
        }
        return false;
    }

    public void updateVisit(Cat cat) {
        if (damageHandler != null) {
            if (collidesWithThorns(cat)) {
                damageHandler.doDamage(Gdx.graphics.getDeltaTime());
                if (!damaging) {
                    damaging = true;
                    damageHandler.startDamage();
                }
            }
            else if (damaging) {
                damaging = false;
                damageHandler.stopDamage();
            }
        }
    }

    public void act(float delta) {
        super.act(delta);
        rotateThorns(delta);
    }

    public void draw(Batch batch, float parentAlpha) {
        for (Thorn thorn : thorns) {
            thorn.draw(batch, parentAlpha);
        }
        super.draw(batch, parentAlpha);
    }

    @Override
    public void restart() {
        super.restart();
        adjustThorns();
        damaging = false;
    }

    public class Thorn extends Actor {
        private TextureRegion texture;
        private float speed;
        private float angle;

        Thorn(float speed, AssetManager manager){
            this.speed = speed;
            this.texture = new TextureRegion(manager.get("planets/pico.png", Texture.class));
            this.setHeight(texture.getRegionHeight() * size);
            this.setWidth(texture.getRegionWidth() * size);
        }

        private void rotate(float delta, Vector2 centro, float radio){
            this.angle = (this.angle + (currentDirection *
                    (visited ? Values.ANGLE_PER_SECOND_FAST * delta * this.speed
                            : Values.ANGLE_PER_SECOND_SLOW * delta))) % 360;
            setX((float)(centro.x + (Math.cos(this.angle / 57.2958) * radio)
                    - this.getWidth() * 0.5f));
            setY((float)(centro.y + (Math.sin(this.angle / 57.2958) * radio)
                    - this.getHeight() * 0.5f));
        }

        private boolean collides(Cat cat){
            Rectangle rectangle = new Rectangle(this.getX(), this.getY(),
                    this.getWidth(), this.getHeight());
            return rectangle.overlaps(cat.getRect());
        }

        public void setSpeed(float speed) {
            this.speed = speed;
        }

            @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (visited || isEnabled()){
                batch.setColor(1,1,1, alpha);
                batch.draw(texture, this.getX(), this.getY(),
                        this.getWidth() * 0.5f, this.getHeight() * 0.5f,
                        this.getWidth(), this.getHeight(),
                        1, 1, this.angle);
            }
        }
    }

}