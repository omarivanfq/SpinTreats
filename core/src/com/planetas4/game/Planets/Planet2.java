package com.planetas4.game.Planets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.planetas4.game.Cat;

import java.util.Comparator;

/*
*   Planet2:
*   Disappears after being visited n times
*
*/

public class Planet2 extends Planet {
    private float animationTime;
    private Animation animation;
    private int visits, remainingVisits;
    private boolean exists;
    private Color tone;
    private float glowingPercentage;
    private float dif = 0.02f;
    private DisappearListener disappearListener = null;

    public Planet2(AssetManager manager){
        super(manager);
        glowingPercentage = 0.57f;
        TextureAtlas atlasExplosion = manager.get("planets/explosion/explosion.txt", TextureAtlas.class);
        Array<TextureAtlas.AtlasRegion> explosionRegions = new Array<TextureAtlas.AtlasRegion>(atlasExplosion.getRegions());
        explosionRegions.sort(new RegionComparator());
        animation = new Animation(1.0f / 26f, explosionRegions, Animation.PlayMode.LOOP);
        animationTime = 0.0f;
        exists = true;
        this.texReg = new TextureRegion(manager.get("planets/planeta2.png", Texture.class));
        tone = new Color(1.0f, 1f, 1.0f,1);
    }

    public Planet2(float xRel, float yRel, int visits, float size, DIRECTION DIRECTION, AssetManager manager) {
        super(xRel, yRel, size, DIRECTION, manager);
        glowingPercentage = 0.4f;
        TextureAtlas atlasExplosion = manager.get("planeta3_explosion/p3_explosion.txt", TextureAtlas.class);
        Array<TextureAtlas.AtlasRegion> explosionRegions = new Array<TextureAtlas.AtlasRegion>(atlasExplosion.getRegions());
        explosionRegions.sort(new RegionComparator());
        animation = new Animation(1.0f / 40f, explosionRegions, Animation.PlayMode.LOOP);
        animationTime = 0.0f;
        exists = true;
        this.visits = visits;
        this.remainingVisits = visits;
        this.texReg = new TextureRegion(manager.get("planets/planeta2.png", Texture.class));
        tone = new Color(1.0f, 1f, 1.0f,1);
    }

    public interface DisappearListener {
        void onDisappear();
    }

    public void setDisappearListener(DisappearListener disappearListener) {
        this.disappearListener = disappearListener;
    }

    @Override
    public void setVisited(boolean visited) {
        super.setVisited(visited);
        if (visited){
            remainingVisits--;
        }
        else if (remainingVisits <= 0) {
            if (exists && disappearListener != null) {
                disappearListener.onDisappear();
            }
            exists = false;
            tone = new Color(1.0f, 1f, 1.0f,1);
        }
    }

    public void setRemainingVisits(int remainingVisits) {
        this.remainingVisits = remainingVisits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public int getVisits() {
        return visits;
    }

    @Override
    public boolean isCollided(Cat aCat) { return exists && super.isCollided(aCat); }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (visited && remainingVisits == 0){
            if (tone.b >= 1 || tone.b < (1 - glowingPercentage)) {
                dif *= -1;
            }
            tone.b += dif;
            tone.g += dif;
        }
    }

    @Override
    public void restart(){
        super.restart();
        animationTime = 0.0f;
        tone.r = tone.g = tone.b = 1;
        dif = Math.abs(dif);
        exists = true;
        remainingVisits = visits;
    }

    public boolean exists() {
        return exists;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(tone);
        if (visited || exists && isEnabled()) {
            batch.setColor(tone.r, tone.g, tone.b, alpha);
            batch.draw(texReg, getX(), getY(), radius, radius, getWidth(), getHeight(), scaleX, scaleY, textureAngle);
            batch.setColor(1,1,1, brilloAlpha);
            batch.draw(texGlow, getX(), getY(), radius, radius, getWidth(), getHeight(), 1f, 1f, textureAngle);
            batch.setColor(Color.WHITE);
        }
        else if (!exists && !animation.isAnimationFinished(animationTime)){
            animationTime += Gdx.graphics.getDeltaTime();
            TextureRegion frame = (TextureRegion) animation.getKeyFrame(animationTime * 0.95f);
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
        }
        batch.setColor(Color.WHITE);
    }

    private static class RegionComparator implements Comparator<TextureAtlas.AtlasRegion> {
        @Override
        public int compare(TextureAtlas.AtlasRegion region1, TextureAtlas.AtlasRegion region2) {
            return region1.name.compareTo(region2.name);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

