package com.planetas4.game;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Components.SparkTrail;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Managers.SettingsManager;
import com.planetas4.game.Planets.Planet;
// import java.util.Comparator;

public class Cat extends Actor {

    private boolean floating; /* is the cat floating / jumping? */
    private float angle; /* texture angle */
    private TextureRegion texReg;
    private Vector2 center; /* coords center of the cat */
    private Rectangle rect;
    private SparkTrail sparkTrail;
    private AssetManager manager;

    public Cat(AssetManager manager){
        floating = true;
        this.manager = manager;
        angle = 45.0f;
        center = new Vector2(0, 0);
        setSize(35, 35);
        sparkTrail = new SparkTrail("destellos_gato/destellosGato.txt", 1,
                getWidth() * 0.35f, getHeight() * 0.35f);
        setPosition(center.x - getWidth() * 0.5f, center.y - getHeight() * 0.5f);
        Texture texture = manager.get("cats/cat-01.png", Texture.class);
        texReg = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
        rect = new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    /* changing the cat texture if a different one was chosen at settings */
    public void updateImage() {
        Texture texture = this.manager.get(SettingsManager.getCatImageFileName(), Texture.class);
        texReg = new TextureRegion(texture, 0, 0, texture.getWidth(), texture.getHeight());
    }

    /* returning the cats box collider */
    public Rectangle getRect(){
        rect.set(getX() + 2, getY() + 2, getWidth() * 0.36f, getHeight() * 0.36f);
        rect.setCenter(center.x, center.y);
        return rect;
    }

    /* updating the cat position considering the planets rotation angle */
    public void rotate(Planet planet) {
        if (planet != null) {
            angle = planet.getAngle();
            float radio = planet.getRadius() + 3;
            setX((float) (planet.getCenter().x + (Math.cos(angle / 57.2958) * radio) - getWidth() * 0.5f));
            setY((float) (planet.getCenter().y + (Math.sin(angle / 57.2958) * radio) - getHeight() * 0.5f));
            center.x = getX() + getWidth() * 0.5f;
            center.y = getY() + getHeight() * 0.5f;
        }
    }
    /* updating the cat position considering the direction it is jumping to */
    private void toFloat(float delta){
        setX((float)(getX() + Values.PIXELS_PER_SEC * delta * Math.cos(angle / 57.2958)));
        setY((float)(getY() + Values.PIXELS_PER_SEC * delta * Math.sin(angle / 57.2958)));
        center.x = getX() + getWidth() * 0.5f;
        center.y = getY() + getHeight() * 0.5f;
    }

    @Override
    public void act(float delta) {
        if (floating){
            toFloat(delta);
            sparkTrail.setPosition(center.x, center.y);
            sparkTrail.act(delta);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        center.x = getX() + getWidth() * 0.5f;
        center.y = getY() + getHeight() * 0.5f;
    }
    /* returning a code that represents which border of the screen was reached */
    public int reachesEdge(Viewport viewport){
        int out = 0;
        float upperLimit = viewport.getWorldHeight(),
                leftLimit = 0,
                rightLimit = viewport.getWorldWidth(),
                bottomLimit = viewport.getWorldHeight()
                    * Values.PAUSE_BAR_VERTICAL_SPACE
                    * Values.CHALLENGE_BAR_SPACE;

        if (center.x > rightLimit + 10 && center.y < bottomLimit - 10 || // 4
                center.x > rightLimit + 10 && center.y > upperLimit + 10 || // 1
                center.x < leftLimit - 10 && center.y > upperLimit + 10 || // 2
                center.x < leftLimit - 10 && center.y < bottomLimit - 10) { // 3
            out = 1; // a corner
        }
        else if (center.x >= rightLimit) {
            out = 2; // right edge
        }
        else if (center.x <= leftLimit) {
            out = 3; // left edge
        }
        else if (center.y >= upperLimit) {
            out = 4; // top edge
        }
        else if (center.y <= bottomLimit) {
            out = 5; // bottom edge
        }
        return out;
    }

    public void changeAngleAfterBouncing(Viewport viewport){
        switch (reachesEdge(viewport)){
            case 0: break;
            case 1:
                angle += 180;
                break;
            case 2:
                goBackToEdge(viewport);
                angle = 180 - angle;
                break; // der
            case 3:
                goBackToEdge(viewport);
                angle = 180 - angle;
                break; // izq
            case 4:
                goBackToEdge(viewport);
                angle = 360 - angle;
                break; // arr
            case 5:
                goBackToEdge(viewport);
                angle = 360 - angle;
                break; // abj
        }
    }

    public Vector2 getCenter() { return center; }

    public boolean isFloating() { return floating; }

    public void setFloating(boolean floating) {
        this.floating = floating;
    }

    private void goBackToEdge(Viewport viewport){
        float bottomLimit, upperLimit, leftLimit, rightLimit;
        leftLimit = 0;
        rightLimit = viewport.getWorldWidth();
        upperLimit = viewport.getWorldHeight();
        bottomLimit = viewport.getWorldHeight()
                * Values.PAUSE_BAR_VERTICAL_SPACE
                * Values.CHALLENGE_BAR_SPACE;
        if (center.x < leftLimit){
            setX(leftLimit - getWidth() * 0.5f);
        }
        else if (center.x > rightLimit){
            setX(rightLimit - getWidth() * 0.5f);
        }
        else if (center.y < bottomLimit){
            setY(bottomLimit - getHeight() * 0.5f);
        }
        else if (center.y > upperLimit){
            setY(upperLimit - getHeight() * 0.5f);
        }
    }

    /*
    private static class RegionComparator implements Comparator<TextureAtlas.AtlasRegion> {
        @Override
        public int compare(TextureAtlas.AtlasRegion region1, TextureAtlas.AtlasRegion region2) {
            return region1.name.compareTo(region2.name);
        }
    }
    */

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1);
        batch.draw(texReg, getX(), getY(), getWidth() * 0.5f, getHeight() * 0.5f,
                getWidth(), getHeight(), 1, 1, angle - 75);
        sparkTrail.draw(batch, parentAlpha);
    }

}