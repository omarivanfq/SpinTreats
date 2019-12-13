package com.planetas4.game.Treats;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.planetas4.game.Cat;

public class Treat extends Actor implements Cloneable {

    private Rectangle rect;
    private boolean collected;
    private boolean sparkling;
    Animation sparkleAnimation;
    private float animationTime;
    Image image;
    private Action sparkAction;
    private String efecto;
    private boolean visible;
    AssetManager manager;

    public Treat(float x, float y, AssetManager manager){
        animationTime = 0.0f;
        sparkling = false;
        collected = false;
        visible = true;
        efecto = null;
        this.manager = manager;

        TextureAtlas dulceAtlas = manager.get("destellos/spark.pack", TextureAtlas.class);
        sparkleAnimation = new Animation(1.0f / 10.0f, dulceAtlas.getRegions(), Animation.PlayMode.NORMAL);

        TextureRegion texReg = new TextureRegion(manager.get("treats/treat.png", Texture.class));
        image = new Image(texReg);
        setSize(12, 12);

        rect = new Rectangle();
        rect.setWidth(getWidth());
        rect.setHeight(getHeight());

        setPosition(x - getWidth() * 0.5f, y - getHeight() * 0.5f);
        image.setPosition(x - getWidth() * 0.5f, y - getHeight() * 0.5f);
        image.setSize(getWidth(), getHeight());
        sparkAction = Actions.forever(
            Actions.sequence(
                Actions.fadeIn(0.15f),
                Actions.delay(0.5f),
                Actions.fadeOut(0.15f),
                Actions.delay(0.15f)
            )
        );
    }

    public void hide() { visible = false; }
    public void show() { visible = true; }

    public void setEfecto(String efecto) {
        this.efecto = efecto;
    }

    public String getEfecto() {
        return efecto;
    }

    public boolean collides(Cat aCat){
        if (!collected && rect.overlaps(aCat.getRect())) {
            collected = true;
            sparkling = true;
            return true;
        }
        return false;
    }

    public void restartSparkle() {
        image.clearActions();
        // image.addAction(sparkAction); // this doesn't work.. idk why
        image.addAction(Actions.forever(
                Actions.sequence(
                        Actions.fadeIn(0.15f),
                        Actions.delay(0.5f),
                        Actions.fadeOut(0.15f),
                        Actions.delay(0.15f)
                )
        ));
    }

    public void sparkle(){
        if (!image.hasActions()) {
            image.addAction(sparkAction);
        }
    }

    public void detenerParpadeo(){
        image.clearActions();
    }

    @Override
    public void act(float delta) {
        image.act(delta);
    }

    public boolean isCollected() { return collected; }

    public void restart(){
        collected = false;
        sparkling = false;
        animationTime = 0;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1);
        if (sparkling && !sparkleAnimation.isAnimationFinished(animationTime)){
            animationTime += Gdx.graphics.getDeltaTime();
            TextureRegion brilloFrame = (TextureRegion) sparkleAnimation.getKeyFrame(animationTime);
            batch.draw(brilloFrame, getX(), getY(), getWidth(), getHeight());
        }
        else if (!collected && visible) {
            image.draw(batch, parentAlpha);
        }
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        image.setPosition(x, y);
        rect.setPosition(x, y);
    }

    @Override
    public void setX(float x) {
        super.setX(x);
        image.setX(x);
        rect.setX(x);
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        image.setY(y);
        rect.setY(y);
    }

    public void dispose() {

    }

}
