package com.planetas4.game.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.LinkedList;

/*
* 
*   Component that shows a trail of sparkles
*     in order to "move" across the screen is needed to update its position constantly
*     e.g.
*          sparkTrail.setPosition(xValue, yValue);
* 
* */

public class SparkTrail extends Actor {

    private final float timeBetweenSparkles;
    private float timeBetweenSparklesElapsed;
    private LinkedList<Spark> sparks;
    private Color color;

    public SparkTrail(String path, float intensity, float width, float height){
        setSize(width, height);
        sparks = new LinkedList<Spark>();
        for (int i = 0; i < 7 * intensity; i++){
            sparks.add(new Spark(path, getWidth(), getHeight()));
        }
        timeBetweenSparkles = sparks.getFirst().getAnimationDuration() / sparks.size();
        timeBetweenSparklesElapsed = timeBetweenSparkles * 0.5f;
        color = Color.WHITE;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        timeBetweenSparklesElapsed += delta;
        if (timeBetweenSparklesElapsed >= timeBetweenSparkles){
            timeBetweenSparklesElapsed = 0.0f;
            sparks.addLast(sparks.pop());
            sparks.getLast().sparkles();
            sparks.getLast().setX(getX() - sparks.getLast().getWidth() * 0.5f);
            sparks.getLast().setY(getY() - sparks.getLast().getHeight() * 0.5f);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.setColor(color);
        for (Spark spark : sparks) {
            spark.draw(batch, parentAlpha);
        }
        batch.setColor(Color.WHITE);
    }
}
