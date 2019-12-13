package com.planetas4.game.Components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Managers.SettingsManager;
import com.planetas4.game.Utils.FontGenerator;

import java.util.ArrayList;
import java.util.List;

/*
*
* Component that shows as a button at the bottom of the screen but when touched toggles a
*   menu over the screen
*
*   the component can be customized with "elements" which are practically just icons and some text
*
*   Images with labels and buttons can be added to the menu through some methods
*
* */

public class ScreenToggleButton extends Actor {

    private final float hiddenTime = 0.34f; // time that the menu takes to show or hide

    private float fixedPercentage;
    private int fixedElements;
    private ShapeRenderer shapeRenderer;
    private boolean isPressed;
    private List <Element> elements;
    private float buttonSpace;  // the percentage of screen space that the button occupies
    private float iconsVerticalSpace; // the percentage of button space that the icons occupies
    private float iconsVerticalPosition; // the percentage of vertical aligment of the icons (e.g. 0.5 for it to be in the middle)
    private Table table;
    private BitmapFont font;
    private TextButton.TextButtonStyle tbs;
    private boolean isHidden;
    private Stage stage;
    private Color backgroundColor;
    private boolean hideScreen = false;

    public interface ButtonListener {
        void onClick();
    }

    private ScreenToggleButton(float buttonSpace, float iconsVerticalSpace, float iconsVerticalPosition) {

        this.fixedPercentage = 0.0f;
        this.fixedElements = 0;
        this.buttonSpace = buttonSpace;// = 0.07f;
        this.iconsVerticalSpace = iconsVerticalSpace;// = 0.4f;
        this.iconsVerticalPosition = iconsVerticalPosition;// = 0.5f;

        elements = new ArrayList<Element>();
        isHidden = true;
        table = new Table();
        stage = new Stage();

        isPressed = false;
        setWidth(Values.SCREEN_WIDTH);
        setHeight(Values.SCREEN_HEIGHT);
        setX(0);
        setY(getHeight() * -(1 - buttonSpace));
        shapeRenderer = new ShapeRenderer();
        backgroundColor = new Color(0.01f, 0.01f, 0.03f, 0.7f);
        shapeRenderer.setColor(backgroundColor);
        font = FontGenerator.getFont(25);

        tbs = new TextButton.TextButtonStyle();
        tbs.up = new TextureRegionDrawable(
                new TextureRegion( new Texture("transparent.png") ) );
        tbs.down = new TextureRegionDrawable(
                new TextureRegion( new Texture("blanco_transparente.png") ) );
        tbs.font = font;

        table.setPosition(0, -getHeight() * (1 - buttonSpace));
        table.setFillParent(true);
        table.pack();
        stage.addActor(table);

        this.addListener(
            new ClickListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    goBright();
                    return super.touchDown(event, x, y, pointer, button);
                }
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    press();
                }
            }
        );
    }

    public void hideScreen() {
        this.hideScreen = true;
        this.setAlpha(1);
    }

    public boolean isHidden() {
        return isHidden;
    }

    public ScreenToggleButton(Viewport viewport){
        this(0.07f, 0.35f, 0.5f);
        stage.setViewport(viewport);
    }

    public ScreenToggleButton(Viewport viewport, float buttonSpace, float iconsVerticalSpace, float iconsVerticalPosition){
        this(buttonSpace, iconsVerticalSpace, iconsVerticalPosition);
        stage.setViewport(viewport);
    }

    public Label addElement(Texture image, String sTexto){
        return addElement(image, sTexto, 1.0f / (elements.size() + 1), false);
    }

    public Label addElement(Texture image, String text, float horizontalSpace){
        return addElement(image, text, horizontalSpace, true);
    }

    private Label addElement(Texture image, String text, float horizontalSpace, boolean fixed){
        Element element = new Element(image, text);
        elements.add(element);
        float size = getHeight() * buttonSpace * iconsVerticalSpace;
        element.setSize(size, size);
        element.setY(getHeight() * buttonSpace * iconsVerticalPosition - size * 0.5f);
        element.setFixed(fixed);

        if (fixed) {
            fixedPercentage += horizontalSpace;
            fixedElements++;
        }

        for (int i = 0; i < elements.size(); i++){
            if (!elements.get(i).isFixed())
                elements.get(i).setHorizontalSpace((1 - fixedPercentage) / (elements.size() - fixedElements + 1));
        }

        float posX = 0;
        elements.get(0).setX(posX + elements.get(0).getHorizontalSpace() * getWidth() - size * 0.5f);
        for (int i = 1; i < elements.size(); i++){
            posX += elements.get(i - 1).horizontalSpace * getWidth();
            elements.get(i).setX(posX  + elements.get(i).getHorizontalSpace() * getWidth() - size * 0.5f);
        }
        stage.addActor(element);
        return element.getText();
    }

    @Override
    public void setY(float y) {
        super.setY(y);
        float size = getHeight() * buttonSpace * 0.43f;
        float YY = (getHeight() * buttonSpace - size) * 0.5f;
        for (Element element : elements) {
            element.setY(getY() + YY + y);
        }
    }

    public void addButton(String text, final ButtonListener listener){
        TextButton tb = new TextButton(text, tbs);
        table.row();
        table.add(tb).padTop(20f).colspan(2).minHeight(40f).minWidth(getWidth());
        tb.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                listener.onClick();
            }
        });
     //   return tb;
    }

    @Override
    public Stage getStage() {
        return stage;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public Label addLabel(String text){
        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = font;
        Label label = new Label(text, ls);
        table.row();
        table.add(label).padBottom(12).colspan(2);
        return label;
    }

    public Label addImage(Texture texture, String text){
        addImage(texture);
        Label.LabelStyle ls = new Label.LabelStyle();
        ls.font = font;
        Label label = new Label(text, ls);
        table.add(label);
        return label;
    }

    private void addImage(Texture texture){
        Image image = new Image(new TextureRegionDrawable(new TextureRegion(texture)));
        image.setSize(25, 25);
        table.row();
        table.add(image).padLeft(30).padBottom(20).width(30).height(30).uniform();
    }

    private void hide(float time){
        if (!isHidden) {
            addAction(Actions.moveBy(0, -getHeight() * (1 - buttonSpace), time));
            stage.addAction(Actions.moveBy(0, -getHeight() * (1 - buttonSpace), time));
            for (Element element : elements)
                element.addAction(Actions.fadeIn(time * 0.45f));
        }
        isHidden = true;
    }

    private void show(float tiempo){
        if (isHidden) {
            stage.addAction(Actions.moveBy(0, getHeight() * (1 - buttonSpace), tiempo));
            addAction(Actions.moveBy(0, getHeight() * (1 - buttonSpace), tiempo));
            for (Element element : elements)
                element.addAction(Actions.fadeOut(tiempo * 0.45f));
        }
        isHidden = false;
    }

    public void press(){
        if (SettingsManager.getVibration()){
            Gdx.input.vibrate(Values.VIBRATION_TIME_DEFAULT);
        }
        isPressed = !isPressed;
        if (isPressed) {
            show(hiddenTime);
        }
        else {
            hide(hiddenTime);
        }
        goDark();
    }

    public void restart(){
        isPressed = false;
        hide(0);
        act(0);
        goDark();
        hideScreen = false;
    }

    private void goBright() {
        if (!hideScreen) {
            shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 0.7f);
        }
    }

    private void goDark() {
        shapeRenderer.setColor(Color.BLACK);
        if (isPressed) {
            shapeRenderer.getColor().a = 0.87f;
        }
        else {
            shapeRenderer.getColor().a = 0.7f;
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stage.act();
    }

    void setAlpha(float a){
        backgroundColor.a = a;
        shapeRenderer.setColor(backgroundColor);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        stage.draw();
        batch.begin();
    }

    public class Element extends Actor{
        private Texture icon;
        private Label text;
        private Skin skin;
        private float horizontalSpace;
        private boolean fixed;

        Element(Texture icon, String sText){
            this.fixed = false;
            this.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
            this.icon = icon;
            this.text = new Label(sText, skin);
        }

        void setHorizontalSpace(float horizontalSpace) {
            this.horizontalSpace = horizontalSpace;}
        float getHorizontalSpace() {return horizontalSpace;}
        boolean isFixed() {return fixed;}
        void setFixed(boolean fixed) {this.fixed = fixed;}
        Label getText() {
            return text;
        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x, y);
            text.setPosition(x + Element.this.getWidth(), y - text.getHeight() * 0.5f);
        }

        @Override
        public void setX(float x) {
            super.setX(x);
            text.setX(x + Element.this.getWidth());
        }

        @Override
        public void setY(float y) {
            super.setY(y);
            text.setY(y - text.getHeight() * 0.5f);
        }

        @Override
        public void addAction(Action action) {
            super.addAction(action);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            Color color = getColor();
            batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);
            batch.draw(icon, getX(), getY(), getWidth(), getHeight());
            text.draw(batch, color.a * parentAlpha);
        }
        public void dispose() {
            icon.dispose();
        }
    }

    public void dispose(){
        shapeRenderer.dispose();
        font.dispose();
        stage.dispose();
        for (Element element : elements) {
            element.dispose();
        }
    }
}
