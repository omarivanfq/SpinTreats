package com.planetas4.game.Components;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Utils.FontGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/*
*
*   component that stores several messages as labels or images and then can be called to be shown
*      on the screen
*
* */

public class ScreenMessage extends Actor {

    final float default_show_time;
    final private float default_background_alpha = 0.12f;
    final private float default_fade_time = 0.30f;
    final private float default_image_alpha = 0.70f;
    private Label.LabelStyle lbs;
    Image backgroundImage;
    private HashMap<String, Image> map = new HashMap();  // hash of images
    private HashMap<String, Label> labelMap = new HashMap();  // hash of labels
    private List<String> removable;  // keys of images or labels that can be suddenly removed from screen (usually when user touches the screen)

    ScreenMessage(float showTime) {
        this.default_show_time = showTime;
        /* the message is shown over a black background */
        backgroundImage = new Image(new Texture("negro.png"));
        backgroundImage.setPosition(0, 0);
        backgroundImage.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        backgroundImage.addAction(Actions.alpha(0.0f, 0));

        this.setPosition(0, 0);
        this.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        removable = new ArrayList<String>();
        setUpLabelStyle();
    }

    @Override
    public void act(float delta) {
        backgroundImage.act(delta);
        for (Map.Entry<String, Image> entry : map.entrySet()) {
            entry.getValue().act(delta);
        }
        for (Map.Entry<String, Label> entry : labelMap.entrySet()) {
            entry.getValue().act(delta);
        }
    }

    /* removing labels and / or images from screen instantly */
    public void stopView() {
        backgroundImage.clearActions();
        backgroundImage.addAction(Actions.alpha(0.0f, 0.1f));
        for (String key : removable) {
            map.get(key).clearActions();
            map.get(key).addAction(Actions.alpha(0.0f, 0.1f));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        backgroundImage.draw(batch, parentAlpha);
        for (Map.Entry<String, Image> entry : map.entrySet()) {
            entry.getValue().draw(batch, parentAlpha);
        }
        for (Map.Entry<String, Label> entry : labelMap.entrySet()) {
            entry.getValue().draw(batch, parentAlpha);
        }
    }

    private void setUpLabelStyle(){
        BitmapFont font = FontGenerator.getFont(28);
        this.lbs = new Label.LabelStyle();
        this.lbs.font = font;
        this.lbs.fontColor = new Color(255, 255, 255, 0.75f);
    }

    public void updateLabel(String key, String text) {
        labelMap.get(key).setText(text);
    }

    public void add(String key, String text) {
        Label label = new Label(text, this.lbs);
        label.addAction(Actions.alpha(0));
        label.setPosition((Values.SCREEN_WIDTH - label.getWidth()) * 0.5f,
                (Values.SCREEN_HEIGHT - label.getHeight()) * 0.5f);
        labelMap.put(key, label);
    }

    public void add(String key, Texture texture) {
        add(key, texture, true);
    }

    public void add(String key, Texture texture, Boolean isRemovable) {
        if (isRemovable) {
            removable.add(key);
        }
        Image image = new Image(texture);
        image.addAction(Actions.alpha(0));
        image.setSize(80, 80);
        image.setPosition((Values.SCREEN_WIDTH - image.getWidth()) * 0.5f,
                (Values.SCREEN_HEIGHT - image.getHeight()) * 0.5f);
        map.put(key, image);
    }

    public void showWithNoEnd(String hash) {
        backgroundImage.addAction(Actions.alpha(default_background_alpha, default_fade_time));
        map.get(hash).addAction(Actions.alpha(default_image_alpha, default_fade_time));
    }

    public void show(String hash) {
        show(hash, default_show_time);
    }

    private void show(String hash, float time) {
        show(hash, time, default_background_alpha);
    }

    private void show(String hash, float time, float alphaBackground) {
        show(hash, time, alphaBackground, default_fade_time);
    }

    private void show(String hash, float time, float alphaBackground, float fadeTime) {
      show(hash, time, alphaBackground, fadeTime, fadeTime);
    }

    public void show(String key, float time, float alphaBackground, float fadeInTime, float fadeOutTime) {
        backgroundImage.addAction(
                sequence(Actions.alpha(alphaBackground, fadeInTime),
                        Actions.delay(time, Actions.alpha(0.0f, fadeOutTime))));

        if (map.containsKey(key)) {
            map.get(key).addAction(
                    sequence(Actions.alpha(default_image_alpha, fadeInTime),
                            Actions.delay(time, Actions.alpha(0.0f, fadeOutTime))));
        }
        if (labelMap.containsKey(key)) {
            labelMap.get(key).addAction(
                    sequence(Actions.alpha(1, fadeInTime),
                            Actions.delay(time, Actions.alpha(0.0f, fadeOutTime))));
        }
    }

    public void dispose() {
       // for (Map.Entry<String, Image> entry : map.entrySet()) {
            // TODO
        // }
    }

}
