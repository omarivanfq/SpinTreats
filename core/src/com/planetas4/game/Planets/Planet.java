package com.planetas4.game.Planets;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.planetas4.game.Cat;
import com.planetas4.game.Constants.Values;

import java.util.ArrayList;

/*
*
*   Regular planet
*
*/

public class Planet extends Actor {

    public enum DIRECTION { NORMAL, OPPOSITE }
    private String id;
    public boolean visited;
    float angle; // angle of the plane/cat
    float textureAngle; // angle of the texture (it keeps spinning when cat keeps still in planet)
    protected TextureRegion texReg;
    TextureRegion texGlow;
    float radius;
    Vector2 center;
    protected float size;
    int currentDirection;
    private int originalDirection;
    private float remainingMovingTime;
    private Vector2 moverA;
    private float speed;
    private boolean stopped; // stops its "visited" rotation
    float brilloAlpha;
    private boolean glowing;
    float scaleX, scaleY;
    private boolean enabled; // is visible and can be visited (enabled by default)
    private ArrayList<String> habilitaIds; // ids of the habilita treats that enable it
    float alpha;

    public Planet(AssetManager manager){
        this.originalDirection = -1;
        this.brilloAlpha = 0;
        scaleX = scaleY = 1;
        this.currentDirection = this.originalDirection;
        this.glowing = false;
        this.texReg = new TextureRegion(manager.get("planets/planeta1.png", Texture.class));
        this.texGlow = new TextureRegion(manager.get("planets/planeta_glow.png", Texture.class));
        visited = false;
        textureAngle = angle = 0;
        remainingMovingTime = 0;
        speed = 1;
        stopped = false;
        enabled = true;
        alpha = 1.0f;
        center = new Vector2();
        habilitaIds = new ArrayList<String>();
        moverA = new Vector2(getX(), getY());
    }

    public Planet(float x, float y, float sizeRel, DIRECTION direction, AssetManager manager){
        this(manager);
        if (direction == DIRECTION.NORMAL) {
            this.originalDirection = -1;
        }
        else {
            this.originalDirection = 1;
        }
        this.currentDirection = this.originalDirection;

        this.radius = Values.SCREEN_WIDTH * sizeRel * 0.5f;
        this.size = sizeRel;
        setSize(Values.SCREEN_WIDTH * sizeRel, Values.SCREEN_WIDTH * sizeRel);
        setPosition(x, y);
        moverA = new Vector2(getX(), getY());
    }

    public Planet(float xRel, float yRel, float size, DIRECTION direction, AssetManager manager, float speed){
        this(xRel, yRel, size, direction, manager);
        this.speed = speed;
    }

    public void setImage(Texture texture) {
        this.texReg = new TextureRegion(texture);
    }

    public void addHabilitaId(String id){
        habilitaIds.add(id);
    }

    public boolean matchesHabilitaId(String id){
        for (String habilitaId : habilitaIds) {
            if (habilitaId.equals(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x - radius, y - radius);
        center.x = x;
        center.y = y;
    }

    public void setCurrentDirection(DIRECTION direction) {
        if (direction == DIRECTION.NORMAL) {
            this.currentDirection = -1;
        }
        else {
            this.currentDirection = 1;
        }
    }

    public void setSize(float size) {
        this.size = size;
        setSize(Values.SCREEN_WIDTH * size, Values.SCREEN_WIDTH * size);
        this.radius = Values.SCREEN_WIDTH * size * 0.5f;
    }

    void setAngle(float angle){
        this.angle = angle;
    }
    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
    public float getSpeed() {
        return speed;
    }
    public void setOriginalDirection(DIRECTION direction) {
        if (direction == DIRECTION.NORMAL) {
            this.originalDirection = -1;
        }
        else {
            this.originalDirection = 1;
        }
    }

    public int getDirection() {
        return currentDirection;
    }
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public boolean isCollided(Cat aCat){
        if (visited || !enabled) return false;
        double radiusT = Math.sqrt(Math.pow(aCat.getCenter().x - center.x, 2) +
                Math.pow(aCat.getCenter().y - center.y, 2));
        return radiusT <= radius;
    }

    public void disable(){ enabled = false; }
    public void enable() { enabled = true; }

    public void reverseRotation(){
        currentDirection *= -1;
    }

    public void rotate(float delta){
        if (!stopped){
            angle = (angle + currentDirection * (visited ? Values.ANGLE_PER_SECOND_FAST * delta * speed
                    : Values.ANGLE_PER_SECOND_SLOW * delta) % 360);
        }
        textureAngle = (textureAngle + currentDirection
                * (visited ? Values.ANGLE_PER_SECOND_FAST * delta * speed
                : Values.ANGLE_PER_SECOND_SLOW * delta));
    }

    public void coordinateWithCat(Cat cat){
        setVisited(true);
        coordinateAngle((float) Math.toDegrees(
                Math.atan2(center.y  - cat.getCenter().y, center.x - cat.getCenter().x)));
    }

    private void coordinateAngle(float angle){
        if (angle >= 0 && angle < 180) {
            this.angle = angle - 180;
        }
        else {
            this.angle = angle + 180;
        }
    }

    public void moverA(float xRel, float yRel, float movingTime) {
        this.remainingMovingTime = movingTime;
        moverA.set(Values.SCREEN_WIDTH * xRel - radius,
                Values.SCREEN_HEIGHT * yRel - radius);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (remainingMovingTime > 0.000f){
            setPosition(
                    getX() + (moverA.x - getX()) * (delta / remainingMovingTime) + radius,
                    getY() + (moverA.y - getY()) * (delta / remainingMovingTime) + radius
            );
            remainingMovingTime -= delta;
        }
        updateGlow(delta);
        rotate(delta);
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
        if (!visited) {
            stopped = false;
        }
    }

    void glow(){
        glowing = true;
        brilloAlpha = 0.76f;
    }

    public void restart(){
        currentDirection = originalDirection;
        textureAngle = angle = 0;
    }

    public float getAngle(){ return angle; }
    public Vector2 getCenter(){ return center; }
    public float getSize() {
        return size;
    }

    public float getRadius(){ return radius; }

    public boolean isEnabled() {
        return enabled;
    }

    private void updateGlow(float delta){
        if (glowing) {
            brilloAlpha -= delta * 1;
            if (brilloAlpha <= 0) {
                glowing = false;
                brilloAlpha = 0;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (enabled || visited){
            batch.setColor(1, 1, 1, alpha);
            batch.draw(texReg, getX(), getY(), radius, radius, getWidth(), getHeight(), scaleX, scaleY, textureAngle);
            batch.setColor(1,1,1, brilloAlpha);
            batch.draw(texGlow, getX(), getY(), radius, radius, getWidth(), getHeight(), 1f, 1f, textureAngle);
            batch.setColor(Color.WHITE);
        }
    }

    public void dispose() { }

}