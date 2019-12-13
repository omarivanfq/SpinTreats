package com.planetas4.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Cat;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Utils.FontGenerator;
import com.planetas4.game.MainGame;
import com.planetas4.game.Planets.Planet;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends ScreenAdapter { //extends PantallaDeslizante{

    private Image logo;
    private Planet p1, p2;
    private Vector2 posicionP1;
    private Vector2 posicionP2;
    private Vector2 posicionLogo;
    private Music music;
    private Stage stage2;
    private Viewport viewport;
    private BitmapFont font;
    private TextButton.TextButtonStyle tbs;
    private Cat cat;
    private float timePressed = 0.0f;
    private boolean isPressed = false;
    private boolean catOnStage = false;

    private MainGame juego;

    private List<Screen> pantallas;
    private List<String> etiquetas;

    private final float porcentajeAlturaSeccionBotones = 0.44f;
    private final float porcentajeAnchoSeccionBotones = 1.0f;
    private final float porcentajeEspacioEntreBotones = 0.1f;
    private final float posicionYSeccionBotones = 0.5f;

    @Override
    public void dispose() {
        super.dispose();
    }

    public HomeScreen(final MainGame juego, AssetManager manager) {
        this.juego = juego;
        font = FontGenerator.getFont(25);
        pantallas = new ArrayList<Screen>();
        etiquetas = new ArrayList<String>();
        viewport = new FitViewport(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        stage2 = new Stage(viewport);
        posicionP1 = new Vector2(0.13f, 0.88f);
        posicionP2 = new Vector2(0.9f, 0.1f);

        setTextButtonStyle();
        setUpFondo();

       /*
        Screen1SL screenEditar = new Screen1SL(juego);
        screenEditar.getRegresar().setText("Editar");
        screenEditar.getRegresar().addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                juego.setScreen(new Constructor(juego));
            }
        });
        cargarPantalla(screenEditar, "editar");
        */
        cargarPantalla(juego.aboutScreen, "acerca");
        cargarPantalla(juego.settingsScreen, "ajustes");//new SettingsScreen(juego, manager), "ajustes");
        cargarPantalla(juego.levelsScreen, "jugar");
        cargarBotones();
        setUpGato(manager);
        setUpP1(manager);
        setUpLogo();
        setP2(manager);

        music = manager.get("music/background-music.mp3", Music.class);
        music.setLooping(true);
    }

    private void setUpGato(AssetManager manager) {
        cat = new Cat(manager);
        cat.setFloating(false);
        cat.setSize(57, 57);
        cat.setPosition(-57, -57);
    }

    private void setUpFondo() {
        Image fondo = new Image(new Texture("fondo.jpg"));
        fondo.setWidth(Values.SCREEN_WIDTH);
        fondo.setHeight(Values.SCREEN_HEIGHT);
        fondo.setPosition(0, 0);
        stage2.addActor(fondo);
    }

    private void setUpLogo() {
        logo = new Image(new Texture("logo.png"));
        logo.setWidth(Values.SCREEN_WIDTH * 0.95f);
        logo.setHeight(Values.SCREEN_HEIGHT * 0.2f);
        stage2.addActor(logo);
        posicionLogo = new Vector2(Values.SCREEN_WIDTH * 0.5f - logo.getWidth() * 0.5f,
                Values.SCREEN_HEIGHT * 0.82f - logo.getHeight() * 0.5f);
        logo.setPosition(posicionLogo.x, posicionLogo.y);
    }

    private void setUpP1(AssetManager manager) {
        this.p1 = new Planet(posicionP1.x, posicionP1.y, 0.7f, Planet.DIRECTION.NORMAL,
                manager);
        this.p1.setImage(new Texture("planets/planeta_display.png"));
        stage2.addActor(p1);
    }

    private void setP2(AssetManager manager) {
        this.p2 = new Planet(posicionP2.x, posicionP2.y, 0.86f, Planet.DIRECTION.NORMAL,
                manager);
        this.p2.setImage(new Texture("planets/planeta_display.png"));
        stage2.addActor(p2);
    }

    @Override
    public void show() {
        super.show();
        music.stop();
        Preferences ajustes = Gdx.app.getPreferences("ajustes");
        music.setVolume(ajustes.getFloat("volume", 1));
        music.play();
        p2.moverA(posicionP2.x, posicionP2.y, 2 * 230 / 1000f);
        p1.moverA(posicionP1.x, posicionP1.y, 2 * 230 / 1000f);
        logo.addAction(Actions.moveTo(posicionLogo.x, posicionLogo.y, 230 / 1000f));
        Gdx.input.setCatchBackKey(false);
        Gdx.input.setInputProcessor(stage2);
        cat.updateImage();
    }

    @Override
    public void hide() {
        super.hide();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage2.act();
        stage2.draw();
        if (catOnStage) {
            cat.rotate(p2);
        }
        else if (isPressed) {
            timePressed += delta;
            if (timePressed >= 3.0f) {
                p2.remove();
                stage2.addActor(cat);
                stage2.addActor(p2);
                catOnStage = true;
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    private void cargarPantalla(Screen screen, String etiqueta) {
        pantallas.add(screen);
        etiquetas.add(etiqueta);
    }

    private void setTextButtonStyle() {
        this.tbs = new TextButton.TextButtonStyle();
        this.tbs.up = new TextureRegionDrawable(new TextureRegion(new Texture("botonJugar.png")));
        this.tbs.down = new TextureRegionDrawable(new TextureRegion(new Texture("botonJugarPressed.png")));
        this.tbs.font = this.font;
    }

    private void cargarBotones(){
        float anchoBoton = Values.SCREEN_WIDTH * porcentajeAnchoSeccionBotones;
        float alturaBoton = (Values.SCREEN_HEIGHT * porcentajeAlturaSeccionBotones)
                / pantallas.size();
        float espacioEntreBotones = alturaBoton * porcentajeEspacioEntreBotones;
        alturaBoton = (alturaBoton - espacioEntreBotones);
        float iniciaSeccion = posicionYSeccionBotones * Values.SCREEN_HEIGHT
                - (Values.SCREEN_HEIGHT * porcentajeAlturaSeccionBotones) * 0.5f;

        for (int i = 0; i < pantallas.size(); i++) {
            TextButton tb = new TextButton(etiquetas.get(i), this.tbs);
            tb.setSize(anchoBoton, alturaBoton);
            tb.setPosition(Values.SCREEN_WIDTH * 0.5f
                            - tb.getWidth() * 0.5f,
                    iniciaSeccion + // posicionando section de botones
                            alturaBoton * 0.5f + espacioEntreBotones +// dejando un espacio de section inicial
                            (i * (alturaBoton  + espacioEntreBotones)) // posicion del boton
                            - tb.getHeight() * 0.5f); // centrando botón
            final int index = i;
            tb.addListener( new ClickListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    isPressed = true;
                    p1.visited = true;
                    p2.visited = true;
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    isPressed = false;
                    timePressed = 0.0f;
                    p2.visited = false;
                    p1.visited = false;
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    juego.setScreen(pantallas.get(index));
                }
            });

            stage2.addActor(tb);
        }
    }

}
