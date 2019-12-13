package com.planetas4.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Components.Background;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Constants.Strings;
import com.planetas4.game.Utils.Dimensions;
import com.planetas4.game.Utils.FontGenerator;
import com.planetas4.game.MainGame;
import com.planetas4.game.Managers.LevelsManager;
import com.planetas4.game.Managers.SettingsManager;

import java.util.ArrayList;
import java.util.List;

public class SettingsScreen extends ScreenAdapter {

    private Label.LabelStyle lbs;
    private Viewport viewport;
    private Stage stage;
    private Music music;
    private Sound sound;
    private MainGame game;
    private Button catButton;
    private Stage catsStage;
    private boolean catsToggle;
    private List<Texture> catsTextures;
    private AssetManager manager;
    private List<Boolean> unlockedCats;
    private List<Button> catsButtons;

    public SettingsScreen(final MainGame game, AssetManager manager){
        this.game = game;
        this.manager = manager;
        music = manager.get("music/background-music.mp3", Music.class);
        Background background = new Background();
        setUpViewport();
        stage = new Stage(this.viewport);
        stage.addActor(background);
        catsStage = new Stage(this.viewport);
        loadCompletedLevels();
        setUpCatsButtons();
        setUpLabelStyle();
        setUpCheckBoxMusic();
        setUpCheckBoxVibration();
        setUpCheckBoxSound();
        setBackButton();
        setUpCatButton();
        catsStage.addAction(Actions.fadeOut(0));
    }

    private void loadCompletedLevels() {
        this.unlockedCats = new ArrayList<Boolean>();
        this.unlockedCats.add(true); // first cat is unlocked by default
        for (int i = 0; i < 18; i++) {
            unlockedCats.add(LevelsManager.getAlreadyCompletedSection(i));
        }
    }

    private String getCatFilename(int catIndex) {
        return "cats/cat-"+ String.format("%02d", catIndex + 1) + ".png";
    }

    private void loadCatsTextures() {
        catsTextures = new ArrayList<Texture>();
        for (int i = 0; i < 19; i++) {
            Texture texture = manager.get(getCatFilename(i), Texture.class);
            catsTextures.add(texture);
        }
    }

    private void switchCat(int catIndex) {
        catButton.getStyle().up = new TextureRegionDrawable(
                new TextureRegion(catsTextures.get(catIndex)));
        SettingsManager.setCatImageFileName(getCatFilename(catIndex));
    }

    public void unlockCat(int catIndex) {
        this.catsButtons.get(catIndex).getStyle().up = new TextureRegionDrawable(
                new TextureRegion(catsTextures.get(catIndex)));
        this.unlockedCats.set(catIndex, true);
        stage.addActor(catButton);
    }

    private void setUpCatsButtons() {
        loadCatsTextures();
        catsButtons = new ArrayList<Button>();
        int catIndex = 0;
        float yOrigin = Dimensions.pantalla_PorcentajeAReal_Y(0.22f);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5 && catIndex < 19; j++) {
                Button catOption = new Button(
                        new TextureRegionDrawable(new TextureRegion(
                                this.unlockedCats.get(catIndex)?
                                this.catsTextures.get(catIndex)
                                : manager.get("cats/cat-incog.png", Texture.class)
                        )));
                catOption.setPosition(27 + Dimensions.pantalla_PorcentajeAReal_X(0.18f) * (j),
                        yOrigin - 50 * i);
                catOption.setSize(41, 41);
                final int catInd = catIndex;
                catOption.addListener(new ClickListener(){
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        super.clicked(event, x, y);
                        if (unlockedCats.get(catInd) && catsToggle) {
                            hideCatOptions(0.2f);
                            switchCat(catInd);
                            catsToggle = false;
                        }
                    }
                });
                catsStage.addActor(catOption);
                catsButtons.add(catOption);
                catIndex++;
            }
        }
    }

    private void setUpCatButton() {
        this.catButton = new Button(
                new TextureRegionDrawable(
                        new TextureRegion(
                                new Texture(
                                        SettingsManager.getCatImageFileName()
                                ))));
        catButton.setSize(80, 80);
        catButton.setPosition(Dimensions.pantalla_PorcentajeAReal_X(0.5f) - catButton.getWidth() * 0.5f,
                Dimensions.pantalla_PorcentajeAReal_Y(0.29f));
        catButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!catsToggle) {
                    showCatOptions(0.2f);
                    catsToggle = true;
                }
            }
        });
        int i = 1;
        while (i < unlockedCats.size()) {
            if (unlockedCats.get(i)) {
                stage.addActor(catButton);
                break;
            }
            i++;
        }
    }

    private void setBackButton() {
        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down =  new TextureRegionDrawable(new TextureRegion(manager.get("sombra.png", Texture.class)));
        ibs.imageUp = new TextureRegionDrawable(new TextureRegion(manager.get("iconos/back.png", Texture.class)));
        ImageButton goBack = new ImageButton(ibs);
        goBack.setSize(Values.SCREEN_WIDTH * 0.15f, 100);
        goBack.setPosition(0, Values.SCREEN_HEIGHT - goBack.getHeight());
        goBack.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.homeScreen);
            }
        });
        stage.addActor(goBack);
    }

    private void setVolume(float volume){
        music.setVolume(volume);
        SettingsManager.setMusicVolume(volume);
    }

    private void setUpCheckBoxMusic(){
        Label labelMusicVolume = new Label(Strings.SELECT_MUSIC_LABEL, lbs);
        labelMusicVolume.setX(Values.SCREEN_WIDTH * 0.54f - labelMusicVolume.getWidth() * 0.5f);
        labelMusicVolume.setY(Values.SCREEN_HEIGHT * 0.80f - labelMusicVolume.getHeight() * 0.5f);
        final CheckBox checkBoxMusicVolume = new CheckBox();
        checkBoxMusicVolume.setSize(16, 16);
        checkBoxMusicVolume.setPosition(labelMusicVolume.getX() - checkBoxMusicVolume.getWidth() * 3,
                labelMusicVolume.getY() + 4);
        float volume = SettingsManager.getMusicVolume();
        if (volume > 0){
            checkBoxMusicVolume.setPressed(true);
        }
        else {
            checkBoxMusicVolume.setPressed(false);
        }
        labelMusicVolume.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (checkBoxMusicVolume.isPressed()) {
                    setVolume(0);
                    checkBoxMusicVolume.setPressed(false);
                }
                else {
                    setVolume(1);
                    checkBoxMusicVolume.setPressed(true);
                }
            }
        });

        checkBoxMusicVolume.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (checkBoxMusicVolume.isPressed()) {
                    setVolume(1);
                }
                else {
                    setVolume(0);
                }
            }
        });
        this.stage.addActor(labelMusicVolume);
        stage.addActor(checkBoxMusicVolume);
    }

    private void showCatOptions(float time) {
        catsStage.addAction(Actions.moveBy(0, 100, time));
        catsStage.addAction(Actions.fadeIn(time));
        catButton.addAction(Actions.moveBy(0, 100, time));
        catButton.addAction(Actions.fadeOut(time));
    }

    private void hideCatOptions(float time) {
        catsStage.addAction(Actions.moveBy(0, -100, time));
        catsStage.addAction(Actions.fadeOut(time));
        catButton.addAction(Actions.moveBy(0, -100, time));
        catButton.addAction(Actions.fadeIn(time));
    }

    private void setUpCheckBoxSound(){
        Label labelSound= new Label(Strings.SELECT_SOUND_LABEL, lbs);
        labelSound.setX(Values.SCREEN_WIDTH * 0.54f - labelSound.getWidth() * 0.5f);
        labelSound.setY(Values.SCREEN_HEIGHT * 0.6f - labelSound.getHeight() * 0.5f);

        final CheckBox checkBoxSound = new CheckBox();
        checkBoxSound.setSize(16, 16);
        checkBoxSound.setPosition(labelSound.getX() - checkBoxSound.getWidth() * 3,
                labelSound.getY() +  4);

        boolean sound = SettingsManager.getSound();

        if (sound){
            checkBoxSound.setPressed(true);
        }
        else {
            checkBoxSound.setPressed(false);
        }

        labelSound.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (checkBoxSound.isPressed()) {
                    SettingsManager.setSound(false);
                    checkBoxSound.setPressed(false);
                }
                else {
                    SettingsManager.setSound(true);
                    checkBoxSound.setPressed(true);
                }
            }
        });

        checkBoxSound.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (checkBoxSound.isPressed()) {
                    SettingsManager.setSound(true);
                }
                else {
                    SettingsManager.setSound(false);
                }
            }
        });
        this.stage.addActor(labelSound);
        this.stage.addActor(checkBoxSound);
    }

    private void setUpCheckBoxVibration(){
        Label labelVibration = new Label(Strings.SELECT_VIBRATION_LABEL, lbs);
        labelVibration.setX(Values.SCREEN_WIDTH * 0.54f - labelVibration.getWidth() * 0.5f);
        labelVibration.setY(Values.SCREEN_HEIGHT * 0.70f - labelVibration.getHeight() * 0.5f);

        final CheckBox checkBoxVibration = new CheckBox();
        checkBoxVibration.setSize(16, 16);
        checkBoxVibration.setPosition(labelVibration.getX() - checkBoxVibration.getWidth() * 3,
                labelVibration.getY() +  4);

        boolean vibration = SettingsManager.getVibration();

        if (vibration){
            checkBoxVibration.setPressed(true);
        }
        else {
            checkBoxVibration.setPressed(false);
        }

        labelVibration.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (checkBoxVibration.isPressed()) {
                    SettingsManager.setVibration(false);
                    checkBoxVibration.setPressed(false);
                }
                else {
                    Gdx.input.vibrate(100);
                    SettingsManager.setVibration(true);
                    checkBoxVibration.setPressed(true);
                }
            }
        });

        checkBoxVibration.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (checkBoxVibration.isPressed()) {
                    Gdx.input.vibrate(100);
                    SettingsManager.setVibration(true);
                }
                else {
                    SettingsManager.setVibration(false);
                }
            }
        });
        this.stage.addActor(labelVibration);
        this.stage.addActor(checkBoxVibration);
    }

    private void setUpLabelStyle(){
        BitmapFont font = FontGenerator.getFont(22);
        this.lbs = new Label.LabelStyle();
        this.lbs.font = font;
        this.lbs.fontColor = new Color(255, 255, 255, 0.75f);
    }

    private void setUpViewport(){
        viewport = new FitViewport(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
        super.show();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(catsStage);
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCatchBackKey(true);
        catsToggle = false;
    }

    @Override
    public void hide() {
        super.hide();
        if (catsToggle) {
            hideCatOptions(0);
            stage.act(1);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            game.setScreen(game.homeScreen);
        }
        stage.act();
        stage.draw();
        catsStage.act();
        catsStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    private class CheckBox extends Actor{
        private Texture checkboxTexture;
        private boolean pressed;

        CheckBox(){
            pressed = false;
            checkboxTexture = new Texture("checkbox1.png");
            this.addListener(
                    new ClickListener(){
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            super.clicked(event, x, y);
                            press();
                        }
                    }
            );
        }

        void press(){
            pressed = !pressed;
        }

        void setPressed(boolean pressed) {
            this.pressed = pressed;
        }

        boolean isPressed() {
            return pressed;
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (!pressed)
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 0.35f);
            else
                batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1);
            batch.draw(checkboxTexture,getX(), getY(), getWidth(), getHeight());
        }
    }

}
