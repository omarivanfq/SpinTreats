package com.planetas4.game.Components;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.planetas4.game.Constants.Values;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/*
*
*  Extends ScreenMessage to specifically show the current challenge of the level with dots
*    in the middle of the screen
*
* */


public class ScreenMessageChallenge extends ScreenMessage {

    private List<Image> dots; // each dot represents a challenge

    public ScreenMessageChallenge(int challengesQuantity, float showTime) {
        super(showTime);
        dots = new ArrayList<Image>();
        Texture dotTexture = new Texture("dot.png");

        /* positioning the dots */
        for (int i = 0; i < challengesQuantity; i++) {
            dots.add(new Image(dotTexture));
            dots.get(i).setSize(20, 20);
            dots.get(i).setPosition(
                    (i + 1) * (Values.SCREEN_WIDTH / (challengesQuantity + 1))
                            - dots.get(i).getWidth() * 0.5f,
                    Values.SCREEN_HEIGHT * 0.5f);
            dots.get(i).addAction(Actions.alpha(0));
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for (Image dot : dots){
            dot.act(delta);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        for (Image dot : dots){
            dot.draw(batch, parentAlpha);
        }
    }

    @Override
    public void stopView() {
        super.stopView();
        for (Image dot : dots){
            dot.clearActions();
            dot.addAction(Actions.alpha(0.0f, 0.1f));
        }
    }

    public void showWhichChallengeWithNoEnd(int challengeIndex){
        backgroundImage.clearActions();
        backgroundImage.addAction(sequence(Actions.alpha(0.9f,0),
                Actions.alpha(0.7f, 0.22f)));
        for (Image dot : dots){
            dot.addAction(
                    sequence(
                            Actions.delay(0.27f),
                            Actions.alpha(0.20f, 0.6f)
                    )
            );
        }
        dots.get(challengeIndex).clearActions();
        dots.get(challengeIndex).addAction(
                sequence(
                        Actions.delay(0.15f),
                        Actions.alpha(0.77f, 0.6f)
                )
        );
    }

    public void showWhichChallenge(int challengeIndex) {
        showWhichChallenge(challengeIndex, default_show_time);
    }

    private void showWhichChallenge(int challengeIndex, float time){
        backgroundImage.addAction(
                sequence(Actions.alpha(0.7f, 0.3f),
                        Actions.delay(time, Actions.alpha(0.0f, 0.3f))));
        for (Image dot : dots){
            dot.addAction(
                    sequence(Actions.alpha(0.20f, 0.3f),
                            Actions.delay(time, Actions.alpha(0.0f, 0.3f)))
            );
        }
        dots.get(challengeIndex).clearActions();
        dots.get(challengeIndex).addAction(
                sequence(Actions.alpha(0.77f, 0.3f),
                        Actions.delay(time, Actions.alpha(0.0f, 0.3f)))
        );
    }

}
