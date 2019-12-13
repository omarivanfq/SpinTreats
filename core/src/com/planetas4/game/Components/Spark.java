package com.planetas4.game.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import java.util.Comparator;

/*
*
* Component that shows the animation of a sparkle
*
* */

public class Spark extends Actor {

    private float animationTime;
    private Animation animation;

    public Spark(String atlasPath, float w, float h){
        setWidth(w);
        setHeight(h);
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(atlasPath));
        Array<TextureAtlas.AtlasRegion> dulceRegions = new Array<TextureAtlas.AtlasRegion>(atlas.getRegions());
        dulceRegions.sort(new RegionComparator());
        animation = new Animation(1.0f / 5.0f, dulceRegions, Animation.PlayMode.LOOP);
        animationTime = animation.getAnimationDuration();
    }

    float getAnimationDuration(){
        return animation.getAnimationDuration();
    }

    public void sparkles(){
        animationTime = animation.getAnimationDuration() * 0.25f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (!animation.isAnimationFinished(animationTime)){
            animationTime += Gdx.graphics.getDeltaTime();
            TextureRegion brilloFrame = (TextureRegion) animation.getKeyFrame(animationTime);
            batch.draw(brilloFrame, getX(), getY(), getWidth(), getHeight());
        }
    }

    private static class RegionComparator implements Comparator<TextureAtlas.AtlasRegion> {
        @Override
        public int compare(TextureAtlas.AtlasRegion region1, TextureAtlas.AtlasRegion region2) {
            return region1.name.compareTo(region2.name);
        }
    }

}
