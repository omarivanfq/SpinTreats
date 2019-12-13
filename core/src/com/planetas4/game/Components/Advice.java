package com.planetas4.game.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Constants.Strings;
import com.planetas4.game.Utils.FontGenerator;

/*
* Component that shows an "advice" as a message on the screen at the start of the game
*   and maybe an image
*/

public class Advice extends Actor {

    private Table table;
    private ShapeRenderer shapeRenderer;
    private Label.LabelStyle labelStyleTitle, labelStyle;

    public Advice(String message, String imagePath, AssetManager manager) {

        this.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        this.setPosition(0, 0);

        setUpLabelStyle();
        Label label = new Label(Strings.ADVICE_LABEL, labelStyleTitle);
        label.setAlignment(Align.center); // Align
        Label messageLabel = new Label(message, labelStyle);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(Align.center); // Align
        table = new Table();
        table.setPosition(0,0);
        table.setSize(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        table.row();
        table.add(label).padTop(30f).colspan(2).width(getWidth());
        table.row();
        table.add(messageLabel).padTop(30f).colspan(2).width(getWidth() * 0.87f);
        table.row();
        if (imagePath != null) {
            try {
                Image image = new Image(manager.get(imagePath, Texture.class));
                table.add(image).padTop(30f).colspan(2).width(getWidth()).maxHeight(145);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        table.setFillParent(true);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(new Color(0.01f, 0.01f, 0.03f, 0.92f));
    }

    /* setting up the title and description labels style (fonts) */
    private void setUpLabelStyle() {
        BitmapFont fontTitle = FontGenerator.getFont(25);
        BitmapFont font = FontGenerator.getFont(17);
        labelStyleTitle = new Label.LabelStyle();
        labelStyleTitle.font = fontTitle;
        labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        // stage.draw();
        batch.begin();
        table.draw(batch, parentAlpha);
    }
}
