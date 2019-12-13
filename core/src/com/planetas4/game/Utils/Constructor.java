package com.planetas4.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.planetas4.game.Cat;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.MainGame;
import com.planetas4.game.Managers.PlanetsManager;
import com.planetas4.game.Managers.TreatsManager;
import com.planetas4.game.Planets.Planet;
import com.planetas4.game.Planets.Planet2;
import com.planetas4.game.Planets.Planet3;
import com.planetas4.game.Planets.Planet4;
import com.planetas4.game.Planets.Planet5;
import com.planetas4.game.Planets.Planet6;
import com.planetas4.game.Planets.Planet7;
import com.planetas4.game.Treats.Treat;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Constructor extends ScreenAdapter{

    private float velocidadDefault = 1.15f, sizeDefault = 0.235f;
    private List<Treat> treats;
    private boolean [] rodeado;
    private int [] rodeadoDulces;
    private int tipoDePlaneta;
    private boolean agregandoDulce;
    private boolean bModificandoPlaneta;
    private boolean bTocandoPlaneta, bPlay;
    private int iModificandoPlaneta;
    private Texture fondo;
    private TextButton create, createP1, createP2,
            createP3, createP4, createDulce,
            goPlay, done, play;
    private Skin skin;
    private FitViewport viewport;
    private SpriteBatch batch;
    private PlanetsManager planetsManager;
    private TreatsManager treatsManager;
    private Stage stage;
    private Cat cat;
    private TextureAtlas atlas;
    private boolean mostrandoMenu, creandoPlaneta;
    private Slider tamañoSlider, velocidadSlider;
    private Vector2 pos;
    private Label sizeLabel, velLabel, direccionLabel, dulcesLabel;
    private PrintWriter writer;
    private XmlReader reader;
    private Element root;
    private CheckBox checkboxDireccion, checkboxDulces;
    private Stage stageMenu;
    private InputMultiplexer multiplexer;
    private AssetManager manager;

    public Constructor(final MainGame juego){
        loadAssetManager();
        treats = new ArrayList<Treat>();
        agregandoDulce = false;
        rodeado = new boolean[200];
        rodeadoDulces = new int[200];
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        direccionLabel = new Label("direccionActual", skin);
        direccionLabel.setPosition(Values.SCREEN_WIDTH * 0.021f, Values.SCREEN_HEIGHT * 0.7885f);
        checkboxDireccion = new CheckBox();
        checkboxDireccion.setSize(Values.SCREEN_WIDTH * 0.03f, Values.SCREEN_WIDTH * 0.03f);
        checkboxDireccion.setPosition(direccionLabel.getWidth() + Values.SCREEN_WIDTH * 0.04f,
                Values.SCREEN_HEIGHT * 0.8f);

        dulcesLabel = new Label("treats", skin);
        dulcesLabel.setPosition(direccionLabel.getX(),
                direccionLabel.getY() - 13);
        checkboxDulces = new CheckBox();
        checkboxDulces.setSize(Values.SCREEN_WIDTH * 0.03f, Values.SCREEN_WIDTH * 0.03f);
        checkboxDulces.setPosition(dulcesLabel.getWidth() + Values.SCREEN_WIDTH * 0.04f,
                Values.SCREEN_HEIGHT * 0.8f - 13);

        pos = new Vector2();
        bPlay = bModificandoPlaneta = false;
        Slider.SliderStyle ss = new Slider.SliderStyle();
        ss.background = new TextureRegionDrawable(new
                TextureRegion(new Texture("negro.png")));
        ss.knob = new TextureRegionDrawable(new
                TextureRegion(new Texture("rojo.png")));

        sizeLabel = new Label("Tamaño: ", skin);
        sizeLabel.setPosition(Values.SCREEN_WIDTH * 0.021f, Values.SCREEN_HEIGHT * 0.945f);

        tamañoSlider = new Slider(0, 1, 0.01f, false, ss);
        tamañoSlider.setSize(Values.SCREEN_WIDTH * 0.7f, Values.SCREEN_HEIGHT *0.01f);
        tamañoSlider.setPosition(Values.SCREEN_WIDTH * 0.4f - tamañoSlider.getWidth() * 0.5f,
                Values.SCREEN_HEIGHT * 0.93f);
        tamañoSlider.getStyle().knob.setMinHeight(Values.SCREEN_HEIGHT * 0.017f);
        tamañoSlider.getStyle().knob.setMinWidth(tamañoSlider.getWidth() * 0.1f);
        tamañoSlider.getStyle().background.setMinHeight(Values.SCREEN_HEIGHT * 0.017f);

        velLabel = new Label("Velocidad: ", skin);
        velLabel.setPosition(Values.SCREEN_WIDTH * 0.021f, Values.SCREEN_HEIGHT * 0.87f);
        velocidadSlider = new Slider(0, 3, 0.01f, false, ss);
        velocidadSlider.setSize(Values.SCREEN_WIDTH * 0.7f, Values.SCREEN_HEIGHT *0.01f);
        velocidadSlider.setPosition(Values.SCREEN_WIDTH * 0.4f - tamañoSlider.getWidth() * 0.5f,
                Values.SCREEN_HEIGHT * 0.84f);
        velocidadSlider.getStyle().knob.setMinHeight(Values.SCREEN_HEIGHT * 0.017f);
        velocidadSlider.getStyle().knob.setMinWidth(tamañoSlider.getWidth() * 0.1f);
        velocidadSlider.getStyle().background.setMinHeight(Values.SCREEN_HEIGHT * 0.017f);

        tipoDePlaneta = 0;
        creandoPlaneta = false;
        fondo = new Texture("fondo.jpg");
        atlas = new TextureAtlas("data/Planetas.pack");
        cat = new Cat(this.manager);
        viewport = new FitViewport(Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);

        stage = new Stage(viewport);
        stageMenu = new Stage();

        treatsManager = new TreatsManager(stage.getViewport(), null, manager);
        planetsManager = new PlanetsManager(stage.getViewport(), null, new Planet3.DamageHandler() {
            @Override
            public void startDamage() {}
            @Override
            public void doDamage(float delta) {}
            @Override
            public void stopDamage() {}
        }, manager);

        batch = new SpriteBatch();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        play = new TextButton("play", skin);
        play.setPosition(Values.SCREEN_WIDTH * 0.15f,
                Values.SCREEN_HEIGHT * 0.07f);
        play.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bPlay = !bPlay;
            }
        });

        done = new TextButton("done", skin);
        done.setPosition(Values.SCREEN_WIDTH * 0.85f,
                Values.SCREEN_HEIGHT * 0.07f);
        done.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                bModificandoPlaneta = false;
                done.remove();
                sizeLabel.remove();
                velLabel.remove();
                tamañoSlider.remove();
                velocidadSlider.remove();
                checkboxDireccion.remove();
                direccionLabel.remove();
                dulcesLabel.remove();
                checkboxDulces.remove();
                guardarConfiguracion();
                for (Planet planet : planetsManager.getPlanets()) {
                    planet.setVisited(false);
                }
            }
        });

        createP1 = new TextButton("planeta1", skin);
        createP1.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuCreate();
                creandoPlaneta = true;
                tipoDePlaneta = 1;
            }
        });
        createP1.setPosition(Values.SCREEN_WIDTH * 0.8f, Values.SCREEN_HEIGHT * 0.85f);
        createP2 = new TextButton("planeta2", skin);
        createP2.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuCreate();
                creandoPlaneta = true;
                tipoDePlaneta = 2;
            }
        });
        createP2.setPosition(Values.SCREEN_WIDTH * 0.8f, Values.SCREEN_HEIGHT * 0.8f);
        createP3 = new TextButton("planeta3", skin);
        createP3.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuCreate();
                creandoPlaneta = true;
                tipoDePlaneta = 3;
            }
        });
        createP3.setPosition(Values.SCREEN_WIDTH * 0.8f, Values.SCREEN_HEIGHT * 0.75f);
        create = new TextButton("Agregar", skin);
        create.setPosition(Values.SCREEN_WIDTH * 0.82f, Values.SCREEN_HEIGHT *0.9f);
        create.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuCreate();
            }
        });
        createP3.setPosition(Values.SCREEN_WIDTH * 0.8f, Values.SCREEN_HEIGHT * 0.75f);

        create = new TextButton("Agregar", skin);
        create.setPosition(Values.SCREEN_WIDTH * 0.82f, Values.SCREEN_HEIGHT *0.9f);
        create.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuCreate();
            }
        });

        createP4 = new TextButton("planeta4", skin);
        createP4.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuCreate();
                creandoPlaneta = true;
                tipoDePlaneta = 4;
            }
        });
        createP4.setPosition(Values.SCREEN_WIDTH * 0.8f, Values.SCREEN_HEIGHT * 0.70f);

        //createDulce
        createDulce = new TextButton("dulce", skin);
        createDulce.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                menuCreate();
                agregandoDulce = true;
            //    creandoPlaneta = true;
              //  tipoDePlaneta = 4;
            }
        });
        createDulce.setPosition(Values.SCREEN_WIDTH * 0.8f, Values.SCREEN_HEIGHT * 0.65f);

        goPlay = new TextButton("Go to game", skin);
        goPlay.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                juego.setScreen(juego.homeScreen);
            }
        });
        goPlay.setPosition(Values.SCREEN_WIDTH * 0.8f, Values.SCREEN_HEIGHT * 0.60f);
        leer();
        stage.addActor(create);
        stage.addActor(play);
    }

    private void loadAssetManager() {
        manager = new AssetManager();
    //    manager.load("music/like-a-virgin.mp3", Music.class);
    //    manager.load("music/chainned.mp3", Music.class);
     //   manager.load("music/give-it-2-me.mp3", Music.class);
        manager.load("sounds/collect2.mp3", Sound.class);
        manager.load("sounds/collect3.mp3", Sound.class);
        manager.load("planets/planeta1.png", Texture.class);
        manager.load("planets/planeta2.png", Texture.class);
        manager.load("planets/planeta3.png", Texture.class);
        manager.load("planets/planeta4.png", Texture.class);
        manager.load("planets/planeta5.png", Texture.class);
        manager.load("planets/pico.png", Texture.class);
        manager.load("planets/planeta_glow.png", Texture.class);
        manager.load("dulce4.png", Texture.class);
        manager.load("data/destello.pack" , TextureAtlas.class);
        manager.load("borde_alt.png" , Texture.class);
        manager.load("planeta3_explosion/p3_explosion.txt" , TextureAtlas.class);
        manager.finishLoading();
    }

    private void menuCreate(){
        mostrandoMenu = !mostrandoMenu;
        if (mostrandoMenu){
            stage.addActor(createP1);
            stage.addActor(createP2);
            stage.addActor(createP3);
            stage.addActor(createP4);
            stage.addActor(goPlay);
            stage.addActor(createDulce);
        }
        else{
            createP1.remove();
            createP2.remove();
            createP3.remove();
            createP4.remove();
            goPlay.remove();
            createDulce.remove();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        writer.close();
    }

    private void crearPlaneta(){
        Vector2 vector2 = new Vector2();
        vector2.x = Gdx.input.getX();
        vector2.y = Gdx.input.getY();
        viewport.unproject(vector2);
        switch (tipoDePlaneta) {
            case 1:
                Planet p = new Planet(vector2.x , vector2.y, sizeDefault, Planet.DIRECTION.NORMAL, manager);
                planetsManager.addPlanet1(p);
                break;
            case 2:
                Planet2 p2 = new Planet2(vector2.x, vector2.y, 100, sizeDefault, Planet.DIRECTION.NORMAL, manager);
                planetsManager.addPlanet2(p2);
                break;
            case 3:
                Planet3 p3 = new Planet3(vector2.x, vector2.y, sizeDefault, Planet.DIRECTION.NORMAL, manager);
                planetsManager.addPlanet3(p3);
                break;
            case 4:
                Planet4 pR = new Planet4(vector2.x, vector2.y, sizeDefault, Planet.DIRECTION.NORMAL, manager);
                planetsManager.addPlanet4(pR);
                break;

        }
        final int index = planetsManager.getPlanets().size() - 1;
        planetsManager.getPlanet(index).setSpeed(velocidadDefault);
        addListener(planetsManager.getPlanet(index), index);
        stage.addActor(planetsManager.getPlanet(index));
        creandoPlaneta = false;
    }

    @Override
    public void show() {
        super.show();
        multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(stageMenu);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void modificarPlaneta(int i){
        tamañoSlider.setValue(
                planetsManager.getPlanets().get(i).getSize());
        velocidadSlider.setValue(
                planetsManager.getPlanets().get(i).getSpeed());
        checkboxDireccion.setPressed(
                (planetsManager.getPlanets().get(i).getDirection() == 1)
        );
        checkboxDulces.setPressed(false);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        Gdx.gl.glClearColor(0.09f, 0.01f, 0.3f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(fondo, 0, 0, Values.SCREEN_WIDTH, Values.SCREEN_HEIGHT);
        batch.end();

        if (bModificandoPlaneta){
            if (checkboxDulces.isPressed()){
                rodeadoDulces[iModificandoPlaneta] = 6;
                rodearConDulces(planetsManager.getPlanets()
                        .get(iModificandoPlaneta), 0, 6, 0, 360, manager);
            }

            planetsManager.getPlanets()
                    .get(iModificandoPlaneta)
                    .setOriginalDirection(checkboxDireccion.isPressed()? Planet.DIRECTION.NORMAL : Planet.DIRECTION.OPPOSITE);

            planetsManager.getPlanets()
                    .get(iModificandoPlaneta)
                    .setSize(tamañoSlider.getValue());

            planetsManager.getPlanets()
                    .get(iModificandoPlaneta)
                    .setSpeed(velocidadSlider.getValue());

            pos.x = Gdx.input.getX();
            pos.y = Gdx.input.getY();
            viewport.unproject(pos);
            float r = planetsManager.getPlanets()
                    .get(iModificandoPlaneta).getRadius();

            if (bTocandoPlaneta) {
                planetsManager.getPlanets()
                    .get(iModificandoPlaneta)
                    .setPosition(pos.x, pos.y);
            }

        }

        if (creandoPlaneta && Gdx.input.justTouched())
            crearPlaneta();

        if (agregandoDulce && Gdx.input.justTouched())
            crearDulce();

        if (!cat.isFloating() && Gdx.input.justTouched() && !create.isPressed()) {
            cat.setFloating(true);
            planetsManager.leavePlanet();
        }

        if (cat.reachesEdge(viewport) > 0)
            planetsManager.setCurrentPlanetIndex(-1);
        cat.changeAngleAfterBouncing(viewport);

        if (bPlay) {
            cat.act(delta);
            batch.begin();
            cat.draw(batch, 1);
            batch.end();
        }

        planetsManager.act(cat);
        batch.begin();
        planetsManager.draw(batch);
        treatsManager.draw(batch);
        batch.end();
        if (!cat.isFloating() && planetsManager.getCurrentPlanetIndex() != -1)
            cat.rotate(planetsManager.getCurrentPlanet());
        stage.act();
        stage.draw();
        stageMenu.act();
        stageMenu.draw();

    }

    public void rodearConDulces(Planet planet, int seccion, int num, float angInicial,
                                float amplitud, AssetManager manager){
        float x, y;
        float angulo = angInicial;
        float anguloEntreDulces;
        if (amplitud == 360)
            anguloEntreDulces = num == 1? amplitud : amplitud / (num);
        else
            anguloEntreDulces = num == 1? amplitud : amplitud / (num - 1);
        for (int i = 0; i < num; i++){
            x = (float)(planet.getCenter().x + Math.cos(angulo / 57.2958) * (planet.getRadius() + 10));
            y = (float)(planet.getCenter().y + Math.sin(angulo / 57.2958) * (planet.getRadius() + 10));
            x = (x / Values.SCREEN_WIDTH);
            y = (y / Values.SCREEN_HEIGHT);
            treatsManager.addTreat(new Treat(x, y, manager), seccion);
            angulo += anguloEntreDulces;
        }

    }

    public void leer(){
        reader = new XmlReader();
        try {
            root = reader.parse(Gdx.files.internal("reto_data.xml"));
            Array <Element> planetas1 = root.getChildrenByNameRecursively("planeta1");
            for (int k = 0; k < planetas1.size; k++){
                LevelsLoader.addPlanet1(planetsManager, planetas1.get(k), manager);
                final int index = planetsManager.getPlanets().size() - 1;
                addListener(planetsManager.getPlanet(index), index);
                stage.addActor(planetsManager.getPlanet(index));
            }

            Array <Element> planetas2 = root.getChildrenByNameRecursively("planeta2");
            for (int k = 0; k < planetas2.size; k++){
                LevelsLoader.addPlanet2(planetsManager, planetas2.get(k), manager);
                final int index = planetsManager.getPlanets().size() - 1;
                addListener(planetsManager.getPlanet(index), index);
                stage.addActor(planetsManager.getPlanet(index));
            }

            Array <Element> planetas3 = root.getChildrenByNameRecursively("planeta3");
            for (int k = 0; k < planetas3.size; k++){
                LevelsLoader.addPlanet3(planetsManager, planetas3.get(k), manager);
                final int index = planetsManager.getPlanets().size() - 1;
                addListener(planetsManager.getPlanet(index), index);
                stage.addActor(planetsManager.getPlanet(index));
            }

            Array <Element> planetasReb = root.getChildrenByNameRecursively("planeta4");
            for (int k = 0; k < planetasReb.size; k++){
                LevelsLoader.addPlanet4(planetsManager, planetasReb.get(k), manager);
                final int index = planetsManager.getPlanets().size() - 1;
                addListener(planetsManager.getPlanet(index), index);
                stage.addActor(planetsManager.getPlanet(index));
            }

            Array <Element> dulcesXML = root.getChildrenByNameRecursively("dulce");
            for (int k = 0; k < dulcesXML.size; k++){
                Treat d = obtenerDulce(dulcesXML.get(k));
                treats.add(d);
                stage.addActor(d);
                addListener(d);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Treat obtenerDulce(XmlReader.Element dulce){
        float x = dulce.getFloat("x");
        float y = dulce.getFloat("y");
        return new Treat(Dimensions.pantalla_PorcentajeAReal_X(x),
                Dimensions.pantalla_PorcentajeAReal_Y(y),
                manager);
    }

    private void crearDulce(){
        Vector2 vector2 = new Vector2();
        vector2.x = Gdx.input.getX();
        vector2.y = Gdx.input.getY();
        viewport.unproject(vector2);
        Treat d = new Treat(vector2.x, vector2.y, manager);
        addListener(d);
        treats.add(d);
        stage.addActor(d);
        agregandoDulce = false;
    }

    void addListener(final Treat treat){
        treat.addListener(new ClickListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return super.touchDown(event, x, y, pointer, button);
            }
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                super.touchDragged(event, x, y, pointer);
                treat.setPosition(treat.getX() + x, treat.getY() + y);
            }
        });
    }

    void addListener(Planet planet, final int index){
        planet.addListener(
            new ClickListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    bTocandoPlaneta = true;
                    return super.touchDown(event, x, y, pointer, button);
                }

                @Override
                public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                    bTocandoPlaneta = false;
                    super.touchUp(event, x, y, pointer, button);
                }

                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    for (Planet planet : planetsManager.getPlanets()) {
                        planet.setVisited(false);
                    }
                    bPlay = false;
                    planetsManager.getPlanets().get(index).setVisited(true);
                    bModificandoPlaneta = true;
                    iModificandoPlaneta = index;
                    tamañoSlider.setValue(planetsManager.getPlanets().get(index).getSize());
                    modificarPlaneta(index);
                    stage.addActor(done);
                    stage.addActor(tamañoSlider);
                    stage.addActor(velocidadSlider);
                    stage.addActor(sizeLabel);
                    stage.addActor(velLabel);
                    stage.addActor(checkboxDireccion);
                    stage.addActor(direccionLabel);
                    stage.addActor(dulcesLabel);
                    stage.addActor(checkboxDulces);
                }
            }
        );
    }

    void printPlaneta(Planet planet) {
        writer.print("\t<planet");
        if (planet instanceof Planet7) {
            writer.print("7 ");
        }
        else if (planet instanceof Planet6) {
            writer.print("6 ");
        }
        else if (planet instanceof Planet5) {
            writer.print("5 ");
        }
        else if (planet instanceof Planet4) {
            writer.print("4 ");
        }
        else if (planet instanceof Planet3) {
            writer.print("3 ");
        }
        else if (planet instanceof Planet2) {
            writer.print("2 ");
        }
        else {
            writer.print("1 ");
        }
    }

    void printPlaneta(Planet planet, int cuantosDulces){
        if (cuantosDulces > 0) {
            writer.println("\t\t<dulce cuantos=\"" + cuantosDulces + "\">");
            writer.print("\t");
        }
        printPlaneta(planet);
        writer.print("x=\"" + (planet.getX() + planet.getRadius())
                / Values.SCREEN_WIDTH + "\" ");

        float y = Dimensions.pantalla_RealAPorcentaje_Y((planet.getY() + planet.getRadius()));

        writer.print("y=\"" + y + "\" ");

        writer.print("size=\"" + planet.getSize() + "\" ");
        if (planet.getDirection() == 1){
            writer.print("direccion=\"contraria\" ");
        }
        writer.print("velocidad=\"" + (Math.round(planet.getSpeed() * 100.0) / 100.0) + "\"");

        if (planet instanceof Planet2)
            writer.print("visitas=\"" + ((Planet2) planet).getVisits() + "\"");
        writer.println("/>");
        if (cuantosDulces > 0)
            writer.println("\t\t</dulce>");
    }

    private void printDulce(Treat treat){
        writer.print("\t<treat ");
        writer.print("x=\"" + Dimensions.pantalla_RealAPorcentaje_X(treat.getX() + treat.getWidth() * 0.5f) + "\" ");
        writer.print("y=\"" + Dimensions.pantalla_RealAPorcentaje_Y(treat.getY() + treat.getHeight() * 0.5f) + "\" ");
        writer.println("/>");
    }

    public void guardarConfiguracion(){

        List<Planet> planets = new ArrayList<Planet>(planetsManager.getPlanets());
        Collections.sort(planets, new Comparator<Planet>() {
            @Override
            public int compare(Planet p1, Planet p2) {
                return p1.getY() > p2.getY() ? -1 : (p1.getY() < p2.getY()) ? 1 : 0; // p1.getY() > p2.getY()? -1 : 1;// o1.InvoiceNumber.compareTo(o2.InvoiceNumber);
            }
        });

        try {
            writer = new PrintWriter("reto_data.xml", "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        writer.println("<reto>");

        for (int i = 0; i < planets.size(); i++){
           printPlaneta(planets.get(i), rodeadoDulces[i]);
        }

        for (Treat treat : treats) {
            printDulce(treat);
        }

        writer.println("</reto>");
        writer.close();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        viewport.update(width, height);
    }

    private class CheckBox extends Actor{
        private Texture checkboxTexture;
        private boolean pressed;

        public CheckBox(){
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

        public void press(){
            pressed = !pressed;
        }

        @Override
        public void setPosition(float x, float y) {
            super.setPosition(x, y);
        }

        public void setPressed(boolean pressed) {
            this.pressed = pressed;
        }

        public boolean isPressed() {
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
            batch.setColor(batch.getColor().r, batch.getColor().g, batch.getColor().b, 1);
        }
    }

}
