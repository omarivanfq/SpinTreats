package com.planetas4.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Utils.FontGenerator;
import com.planetas4.game.Utils.LevelsLoader;
import com.planetas4.game.MainGame;
import com.planetas4.game.Managers.LevelsManager;
import com.planetas4.game.Constants.Strings;
import com.planetas4.game.Components.UnlockableButton;

import java.util.ArrayList;
import java.util.List;

public class LevelsScreen extends ScreenAdapter {

    private OrthographicCamera camera, fixedCamera;
    private Viewport viewport, fixedViewport;
    private SpriteBatch batch;
    private int screensCount, currentScreen;
    private Stage stage, fixedStage;
    private ImageButton goRight, goLeft;
    private MainGame game;
    private Image background;
    private List<Table> sectionTables;
    private List<List<LevelButton>> buttonsList;
    private List<List<LevelButton>> extraButtonsList;
    private AssetManager manager;
    private List<Integer> dimensions;
    private List<Label> loadingLabel;

    public LevelsScreen(final MainGame game, AssetManager manager) {
        this.game = game;
        this.manager = manager;
        sectionTables = new ArrayList<Table>();
        background = new Image(manager.get("selection-bg.jpg", Texture.class));
        setUpGoButtons();
        currentScreen = 0;
        camera = new OrthographicCamera();
        fixedCamera = new OrthographicCamera();
        setUpViewports();
        this.stage = new Stage(viewport);
        this.fixedStage = new Stage(fixedViewport);
        this.dimensions = LevelsLoader.getDimensions();
        screensCount = dimensions.size();
        setUpCargandoLabels();
        initButtons();
        setButtons(LevelsManager.getCurrentSection());
     //   updateExtraLevels();
        fixedStage.addActor(goLeft);
        fixedStage.addActor(goRight);
        batch = new SpriteBatch();
        fixedCamera.position.x = camera.position.x = Values.SCREEN_WIDTH * 0.5f;
        fixedCamera.position.y = camera.position.y = Values.SCREEN_HEIGHT * 0.5f;
     //   updateScores();
        updateCameraPosition();
        setBackButton();
    }

    private void setUpCargandoLabels() {
        this.loadingLabel = new ArrayList<Label>();
        Label.LabelStyle lbs = new Label.LabelStyle();
        lbs.font = FontGenerator.getFont(17);
        for (int i = 0; i < this.dimensions.size(); i++) {
            Label label = new Label("cargando...", lbs);
            label.setPosition(Values.SCREEN_WIDTH * (i + 0.5f) - label.getWidth() * 0.5f,
                    Values.SCREEN_HEIGHT * 0.5f);
            stage.addActor(label);
            loadingLabel.add(label);
        }
    }

    private void setBackButton() {
        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(new TextureRegion(manager.get("sombra.png", Texture.class)));
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
        fixedStage.addActor(goBack);
    }

    private void setUpGoButtons(){
        ImageButton.ImageButtonStyle ibs = new ImageButton.ImageButtonStyle();
        ibs.down = new TextureRegionDrawable(new TextureRegion(manager.get("sombra.png", Texture.class)));
        ibs.imageUp = new TextureRegionDrawable(new TextureRegion(manager.get("iconos/next.png", Texture.class)));
        goRight = new ImageButton(ibs);
        goRight.setSize(Values.SCREEN_WIDTH * 0.12f, Values.SCREEN_HEIGHT);
        goRight.setPosition(Values.SCREEN_WIDTH * 0.88f, 0);

        ImageButton.ImageButtonStyle ibss = new ImageButton.ImageButtonStyle();
        ibss.down = new TextureRegionDrawable(new TextureRegion(manager.get("sombra.png", Texture.class)));
        ibss.imageUp = new TextureRegionDrawable(new TextureRegion(manager.get("iconos/previous.png", Texture.class)));
        goLeft = new ImageButton(ibss);
        goLeft.setSize(Values.SCREEN_WIDTH * 0.12f, Values.SCREEN_HEIGHT);
        goLeft.setPosition(0, 0);

        goRight.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (buttonsList.get(currentScreen).size() != 0) {
                    if (currentScreen < screensCount - 1) {
                        currentScreen++;
                    }
                }
            }
        });

        goLeft.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (buttonsList.get(currentScreen).size() != 0) {
                    if (currentScreen > 0) {
                        currentScreen--;
                    }
                }
            }
        });
    }

    private void setUpViewports(){
        viewport = new FitViewport(Values.SCREEN_WIDTH,
                Values.SCREEN_HEIGHT, camera);
        fixedViewport = new FitViewport(Values.SCREEN_WIDTH,
                Values.SCREEN_HEIGHT, fixedCamera);
    }

    void updateButtonScore(int section, int level) {
        if (buttonsList.get(section).size() == 0) {
            setButtons(section);
        }
        float time = LevelsManager.getLevelTime(section, level);
        if (time != -1) {
            if (LevelsManager.getExtraState()) {
                this.extraButtonsList.get(section).get(level).unlockableButton.setTime(time);
            } else {
                this.buttonsList.get(section).get(level).unlockableButton.setTime(time);
            }
        }
    }

    private void updateScores(int sectionIndex) {
        for (LevelButton levelButton : buttonsList.get(sectionIndex)) {
            float time = LevelsManager.getNormalLevelTime(levelButton.section, levelButton.level);
            if (time != -1) {
                levelButton.unlockableButton.setTime(time);
            }
        }
        for (LevelButton levelButton : extraButtonsList.get(sectionIndex)) {
            float time = LevelsManager.getExtraLevelTime(levelButton.section, levelButton.level);
            if (time != -1) {
                levelButton.unlockableButton.setTime(time);
            }
        }
    }

    private void updateScores() {
        for (List<LevelButton> levelButtons : buttonsList) {
           for (LevelButton levelButton : levelButtons) {
               float time = LevelsManager.getNormalLevelTime(levelButton.section, levelButton.level);
               if (time != -1) {
                   levelButton.unlockableButton.setTime(time);
               }
           }
        }
        for (List<LevelButton> levelButtons : extraButtonsList) {
            for (LevelButton levelButton : levelButtons) {
                float time = LevelsManager.getExtraLevelTime(levelButton.section, levelButton.level);
                if (time != -1) {
                    levelButton.unlockableButton.setTime(time);
                }
            }
        }
    }

    void setExtraLevelsButtons(final int sectionIndex){
        Table table = sectionTables.get(sectionIndex);
        int count = LevelsLoader.getExtraLevelsQuantity(sectionIndex);
        int indexExtra = LevelsLoader.getExtraLevelsIndex(sectionIndex);
        ArrayList<LevelButton> buttonsList = new ArrayList<LevelButton>();
        for (int i = 0; i < count; i++) {
            final int index = i;
            UnlockableButton tb = new UnlockableButton(Strings.EXTRA_LEVEL_BTN + (indexExtra + index + 1),
                    new Color(0.01f, 0.01f, 0.05f, 0.76f));
            tb.addCaptureListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    LevelsManager.setExtraLevel(sectionIndex, index);
                    game.setScreen(game.gameScreen);
                }
            });
            buttonsList.add(new LevelButton(tb, sectionIndex, i));
            table.row();
            table.add(tb)
                .center()
                .minHeight(Values.SCREEN_HEIGHT * 0.1f)
                .minWidth(Values.SCREEN_WIDTH * 0.8f)
                .pad(15f);
        }
        this.extraButtonsList.set(sectionIndex, buttonsList);
    }

    private void initButtons() {
        buttonsList = new ArrayList<List<LevelButton>>();
        extraButtonsList = new ArrayList<List<LevelButton>>();
        for (int i = 0; i < this.dimensions.size(); i++) {
            buttonsList.add(new ArrayList<LevelButton>());
            extraButtonsList.add(new ArrayList<LevelButton>());
            sectionTables.add(new Table());
        }
    }

    private int getBase(int sectionIndex) {
        return LevelsManager.getFirstNormalLevelNumber(sectionIndex);
    }

    private void setButtons(final int sectionIndex) {
        Table table = new Table();
        int levelToShow = getBase(sectionIndex);
        for (int j = 0; j < dimensions.get(sectionIndex); j++) {
            levelToShow++;
            UnlockableButton tb = new UnlockableButton(Strings.LEVEL_BTN + levelToShow);
            final int levelIndex = j;
            buttonsList.get(sectionIndex).add(new LevelButton(tb, sectionIndex, levelIndex));
            tb.addCaptureListener(new ClickListener(){
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (LevelsManager.levelIsUnlocked(sectionIndex, levelIndex)){
                        LevelsManager.setNormalLevel(sectionIndex, levelIndex);
                        game.setScreen(game.gameScreen);
                    }
                }
            });
            table.row();
            table.add(tb)
                    .center()
                    .minHeight(Values.SCREEN_HEIGHT * 0.1f)
                    .minWidth(Values.SCREEN_WIDTH * 0.8f)
                    .pad(15f);
        }
        sectionTables.set(sectionIndex, table);
        ScrollPane.ScrollPaneStyle scrollStyle;
        Texture scrollTexture = manager.get("knob.png", Texture.class);
        NinePatch scrollNine = new NinePatch(new TextureRegion(scrollTexture,10,
                6),2,2,2,2);
        scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.vScrollKnob = new NinePatchDrawable(scrollNine);
        ScrollPane scrollPane = new ScrollPane(table, scrollStyle);
        Table table2 = new Table();
        table2.setFillParent(true);
        table2.add(scrollPane).fill().expand();
        table2.setPosition(Values.SCREEN_WIDTH * sectionIndex, 0);
        if (LevelsManager.getUnlockedSection(sectionIndex)) {
            setExtraLevelsButtons(sectionIndex);
        }
        updateScores(sectionIndex);
        this.stage.addActor(table2);
        this.loadingLabel.get(sectionIndex).remove();
    }

    private void setButtons(){
        for (int i = 0; i < dimensions.size(); i++) {
            setButtons(i);
        }
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        int step = (int)(delta * Values.SCREEN_WIDTH * 3);
        float distanceCameraScreen = Math.abs(camera.position.x - Values.SCREEN_WIDTH
                * (currentScreen + 0.5f));
        if (distanceCameraScreen > 0) {
            if (distanceCameraScreen >= step) {
                camera.position.x +=
                        (Values.SCREEN_WIDTH * (currentScreen + 0.5f)) > camera.position.x ? step : -step;
            }
            else{
                camera.position.x = (Values.SCREEN_WIDTH * (currentScreen + 0.5f));
            }
        }
        else if (buttonsList.get(currentScreen).size() == 0) {
            setButtons(currentScreen);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
            game.setScreen(game.homeScreen);
        }
        camera.update();
        fixedCamera.update();
        batch.setProjectionMatrix(fixedCamera.combined);
        batch.begin();
        background.draw(batch, 1);
        batch.end();
        stage.act();
        stage.draw();
        fixedStage.act();
        fixedStage.draw();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
        fixedViewport.update(width, height);
    }

    @Override
    public void show() {
        updateCameraPosition();
        InputMultiplexer multiplexer;
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(fixedStage);
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
        Gdx.input.setCatchBackKey(true);
    }

    private void updateExtraLevels() {
        for (int i = 0; i < sectionTables.size(); i++) {
            if (LevelsManager.getUnlockedSection(i)) {
                setExtraLevelsButtons(i);
            }
        }
    }

    private void updateCameraPosition(){
        currentScreen = LevelsManager.getCurrentSection();
        camera.position.x = (Values.SCREEN_WIDTH * (currentScreen + 0.5f));
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    class LevelButton {
        UnlockableButton unlockableButton;
        int section;
        int level;
        LevelButton(UnlockableButton unlockableButton, int section, int level){
            this.unlockableButton = unlockableButton;
            this.section = section;
            this.level = level;
        }
    }
}
