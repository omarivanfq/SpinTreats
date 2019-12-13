package com.planetas4.game.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.planetas4.game.Components.Advice;
import com.planetas4.game.Components.Challenge;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Components.Level;
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
import com.planetas4.game.Treats.TreatEfecto;
import com.planetas4.game.Treats.TreatHabilita;
import com.planetas4.game.Treats.TreatRevela;
import com.planetas4.game.Treats.TreatTiempo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class LevelsLoader {

    private static XmlReader.Element root;
    private static float x_llegada, y_llegada;
    private static String EFECTO = null;
    private static String REVELA = null;
    private static int DULCE_NORMAL = 1, DULCE_TIEMPO = 2, DULCE_AGREGANDO = DULCE_NORMAL;

    private LevelsLoader() {
        XmlReader reader = new XmlReader();
        try {
            root = reader.parse(Gdx.files.internal("planetas_data.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static LevelsLoader instancia = new LevelsLoader();

    public static void getDimensions(List<Integer> dimensiones) {
        Array<XmlReader.Element> secciones_niveles = root.getChildrenByNameRecursively("section");
        for (int i = 0; i < secciones_niveles.size; i++) {
            dimensiones.add(secciones_niveles.get(i).getChildrenByName("level").size);
        }
    }

    public static List<Integer> getDimensions() {
        List<Integer> dimensions = new ArrayList<Integer>();
        Array<XmlReader.Element> sections_xml = root.getChildrenByNameRecursively("section");
        for (int i = 0; i < sections_xml.size; i++) {
            dimensions.add(sections_xml.get(i).getChildrenByName("level").size);
        }
        return dimensions;
    }

    public static List<Integer> getExtraDimensions() {
        List<Integer> dimensions = new ArrayList<Integer>();
        Array<XmlReader.Element> sections_xml = root.getChildrenByName("section");
        for (int i = 0; i < sections_xml.size; i++) {
            XmlReader.Element extra = sections_xml.get(i).getChildByName("extra");
            dimensions.add(extra != null? extra.getChildrenByName("level").size : 0);
        }
        return dimensions;
    }

    public static int getExtraLevelsIndex(int seccionIndex) {
        int index = 0;
        Array<XmlReader.Element> sections_xml = root.getChildrenByName("section");
        for (int i = 0; i < seccionIndex && i < sections_xml.size; i++) {
            XmlReader.Element extra = sections_xml.get(i).getChildByName("extra");
            index += extra.getChildrenByName("level").size;
        }
        return index;
    }

    public static int getExtraLevelsQuantity(int seccionIndex) {
        Array<XmlReader.Element> sections_xml = root.getChildrenByName("section");
        XmlReader.Element extra = sections_xml.get(seccionIndex).getChildByName("extra");
        if (extra == null) {
            return 0;
        }
        return extra.getChildrenByName("level").size;
    }

    public static Level getExtraLevel(int sectionIndex, int levelIndex,
                                      PlanetsManager.HidingTreatsListener planetasHidingTreatsListenerMainGame,
                                      TreatsManager.HidingTreatsListener dulcesHidingTreatsListenerMainGame,
                                      Planet3.DamageHandler damageHandler,
                                      MainGame game) {
        Array<XmlReader.Element> sections_xml = root.getChildrenByNameRecursively("section");
        XmlReader.Element extra = sections_xml.get(sectionIndex).getChildByName("extra");
        Array<XmlReader.Element> levels_xml = extra.getChildrenByName("level");
        Array<XmlReader.Element> challenges_xml = levels_xml.get(levelIndex).getChildrenByNameRecursively("challenge");
        int livesQuantity = levels_xml.get(levelIndex).getInt("vidas", Values.LIVES_DEFAULT);
        Level level = getLevel(challenges_xml, game, livesQuantity, planetasHidingTreatsListenerMainGame,
                dulcesHidingTreatsListenerMainGame, damageHandler);
        levels_xml.get(levelIndex).getChildByName("advice");
        return level;
    }

    public static Level getNormalLevel(int sectionIndex, int levelIndex,
                                       PlanetsManager.HidingTreatsListener planetsHidingTreatsListener,
                                       TreatsManager.HidingTreatsListener treatsHidingTreatsListener,
                                       Planet3.DamageHandler damageHandler, MainGame juego) {
        Array<XmlReader.Element> sections_xml = root.getChildrenByNameRecursively("section");
        Array<XmlReader.Element> levels_xml = sections_xml.get(sectionIndex).getChildrenByName("level");
        Array<XmlReader.Element> challenges_xml = levels_xml.get(levelIndex).getChildrenByNameRecursively("challenge");
        int livesQuantity = levels_xml.get(levelIndex).getInt("vidas", Values.LIVES_DEFAULT);
        Level level = getLevel(challenges_xml, juego, livesQuantity, planetsHidingTreatsListener,
                treatsHidingTreatsListener, damageHandler);
        XmlReader.Element consejoElement = levels_xml.get(levelIndex).getChildByName("advice");
        if (consejoElement != null) {
            String message = consejoElement.getText(); // consejoElement.get("mensaje", "No description provided.");
            String imagePath = consejoElement.get("imagePath", null);
            Advice advice = new Advice(message, imagePath, juego.manager);
            level.setAdvice(advice);
        }
        return level;
    }

    private static Level getLevel(Array<XmlReader.Element> challenges_xml, MainGame juego,
                                  int livesQuantity, PlanetsManager.HidingTreatsListener planetasHidingTreatsListener,
                                  TreatsManager.HidingTreatsListener dulcesHidingTreatsListener,
                                  Planet3.DamageHandler damageHandler){
        List<Challenge> challenges = new ArrayList<Challenge>();
        for (XmlReader.Element challenge_xml : challenges_xml) {
            EFECTO = null;
            REVELA = null;
            DULCE_AGREGANDO = DULCE_NORMAL;
            String borderString = challenge_xml.get("border", "normal");

            Challenge.BORDER_TYPE border = Challenge.BORDER_TYPE.NO_BORDER;
            if (borderString.equalsIgnoreCase("rebote")){
                border = Challenge.BORDER_TYPE.BOUNCE;
            }
            else if (borderString.equalsIgnoreCase("traslado")){
                border = Challenge.BORDER_TYPE.TRANSFER;
            }

            long time = (long) challenge_xml.getFloat("tiempo", Values.CHALLENGE_TIME_DEFAULT);
            Challenge challenge = new Challenge(juego.viewport, time, border, juego.getManager(),
                    planetasHidingTreatsListener, dulcesHidingTreatsListener, damageHandler);

            ///// la siguientes funciones deben llamarse en este orden
            getPlanets(challenge, challenge_xml, juego.getManager());
            getHabilitaTreats(challenge, challenge_xml, juego.getManager());
            getRevelaTreats(challenge, challenge_xml, juego.getManager());
            getTreatsEfecto(challenge, challenge_xml, juego.getManager());
            getTreats(challenge, challenge_xml, juego.getManager());
            getTreatsTiempo(challenge, challenge_xml, juego.getManager());
            adjustTreatsEfecto(challenge, challenge_xml);
            challenges.add(challenge);
        }

        return new Level(challenges, livesQuantity);
    }

    private static void adjustTreatsEfecto(Challenge challenge, XmlReader.Element element) {
        Array<XmlReader.Element> dulceElements = element.getChildrenByNameRecursively("treatEfecto");
        for (int i = 0; i < dulceElements.size; i++) {
            Array<XmlReader.Element> planetElements = getAllPlanetElements(dulceElements.get(i));
            String id = dulceElements.get(i).get("id");

            if (planetElements.size > 0
                    && planetElements.first().get("id", null) != null) {
                String pid = planetElements.first().get("id", null);
                TreatEfecto treatEfecto = challenge.getTreatsManager().getEfectoTreatById(id);
                Planet p = challenge.getPlanetsManager().getPlanetById(pid);
                float angle = dulceElements.get(i).getFloat("angulo", 0);
                float x = (float) (p.getCenter().x + Math.cos(angle / 57.2958) * (p.getRadius() + 10));
                float y = (float) (p.getCenter().y + Math.sin(angle / 57.2958) * (p.getRadius() + 10));
                treatEfecto.setX(x - treatEfecto.getWidth() * 0.5f);
                treatEfecto.setY(y - treatEfecto.getHeight() * 0.5f);
            }
        }

    }

    private static void getTreatsTiempo(Challenge challenge, XmlReader.Element element, AssetManager manager) {
        Array<XmlReader.Element> dulceElements = element.getChildrenByNameRecursively("treatTiempo");
        if (dulceElements.size > 0) {
            DULCE_AGREGANDO = DULCE_TIEMPO;
        }
        for (int i = 0; i < dulceElements.size; i++){
            String type = dulceElements.get(i).get("tipo", "normal");
            if (type.matches("rebote")) {
                EFECTO = dulceElements.get(i).get("efecto", null);
                REVELA = dulceElements.get(i).get("revela", null);
                placeAReboteTreat(challenge, dulceElements.get(i), manager);
            }
        }
        for (int i = 0; i < dulceElements.size; i++) {
            EFECTO = dulceElements.get(i).get("efecto", null);
            REVELA = dulceElements.get(i).get("revela", null);
            Array<XmlReader.Element> planetaElements = getAllPlanetElements(dulceElements.get(i));
            if (planetaElements.size > 0) {
                placeSurroundingTreats(challenge, dulceElements.get(i), planetaElements, manager);
            } else {
                String tipo = dulceElements.get(i).get("tipo", "normal");
                if (tipo.matches("linea")) {
                    placeALineTreat(challenge, dulceElements.get(i), manager);
                } else if (tipo.matches("normal")) {
                    colocarDulceTipoNormal(challenge, dulceElements.get(i), manager);
                }
            }
        }
    }

    private static void setPlanet(XmlReader.Element element, Planet planet){
        String id = element.get("id", "");
      //  String habilita = element.get("habilita", null);
        String habilitas = element.get("habilita", null);
        if (habilitas != null){
            String[] habilitaIds = habilitas.trim().split("\\s+");
            for (String hId: habilitaIds) {
                planet.addHabilitaId(hId);
            }
            planet.disable();
        }
        float speed = element.getFloat("speed", 1);
        float xRel = element.getFloat("x", 0);
        float yRel = element.getFloat("y", 0);
        float size = element.getFloat("size");
        String directionString = element.get("direction", "normal");
        Planet.DIRECTION DIRECTION;
        if (directionString.matches("opposite")) {
            DIRECTION = Planet.DIRECTION.OPPOSITE;
        }
        else {
            DIRECTION = Planet.DIRECTION.NORMAL;
        }
        float x = (int) Dimensions.juego_PorcentajeAReal_X(xRel);
        float y = (int) Dimensions.juego_PorcentajeAReal_Y(yRel);
        planet.setSize(size);
        planet.setPosition(x, y);
        planet.setCurrentDirection(DIRECTION);
        planet.setOriginalDirection(DIRECTION);
        planet.setSpeed(speed);
        planet.setId(id);
      //  planet.setHabilitaId(habilita);
     //   if (habilita != null)
       //     planet.disable();
    }

    static void addPlanet1(PlanetsManager planetsManager,
                           XmlReader.Element element, AssetManager manager) {
        Planet p = new Planet(manager);
        setPlanet(element, p);
        planetsManager.addPlanet1(p);
    }

    static void addPlanet2(PlanetsManager planetsManager,
                           XmlReader.Element element, AssetManager manager) {
        Planet2 p = new Planet2(manager);
        setPlanet(element, p);
        int visits = element.getInt("visits", 1);
        p.setVisits(visits);
        p.setRemainingVisits(visits);
        planetsManager.addPlanet2(p);
    }

    static void addPlanet3(PlanetsManager planetsManager,
                           XmlReader.Element element, AssetManager manager) {
        Planet3 p = new Planet3(manager);
        setPlanet(element, p);
        int quantity = element.getInt("cuantos", 3);
        p.setThornsQuantity(quantity);
        String sPicosVel = element.get("picosVel", null);
        if (sPicosVel != null && !sPicosVel.equals("")) {
            String[] picosVel = sPicosVel.split(" ");
            for (int i = 0; i < picosVel.length && i < quantity; i++) {
                p.getThorns().get(i).setSpeed(Float.parseFloat(picosVel[i]));
            }
        }
        planetsManager.addPlanet3(p);
    }

    static void addPlanet4(PlanetsManager planetsManager,
                           XmlReader.Element element, AssetManager manager) {
        Planet4 p = new Planet4(manager);
        setPlanet(element, p);
        planetsManager.addPlanet4(p);
    }

    private static void addPlanet5(PlanetsManager planetsManager,
                                   XmlReader.Element element, AssetManager manager) {
        Planet5 p = new Planet5(manager);
        setPlanet(element, p);
        String idTraslado = element.get("idTraslado");
        float tiempoEstadia = element.getFloat("tiempo", 2.5f);
        float anguloInicio = element.getFloat("anguloInicial", 0.0f);
        p.setTransferId(idTraslado);
        p.setStayTime(tiempoEstadia);
        p.setInitialAngle(anguloInicio);
        planetsManager.addPlanet5(p);
    }

    private static void agregarPlaneta6(PlanetsManager planetsManager,
                                        XmlReader.Element element, AssetManager manager) {
        Planet6 p = new Planet6(manager);
        setPlanet(element, p);
        planetsManager.addPlanet6(p);
    }

    private static void addPlanet7(PlanetsManager planetsManager,
                                   XmlReader.Element element, AssetManager manager) {
        Planet7 p = new Planet7(manager);
        setPlanet(element, p);
        planetsManager.addPlanet7(p);
    }

    private static void getPlanets(Challenge challenge, XmlReader.Element element, AssetManager manager) {
        if (element.getName().matches("planet1"))
            addPlanet1(challenge.getPlanetsManager(), element, manager);
        else if (element.getName().matches("planet2"))
            addPlanet2(challenge.getPlanetsManager(), element, manager);
        else if (element.getName().matches("planet3"))
            addPlanet3(challenge.getPlanetsManager(), element, manager);
        else if (element.getName().matches("planet4"))
            addPlanet4(challenge.getPlanetsManager(), element, manager);
        else if (element.getName().matches("planet5"))
            addPlanet5(challenge.getPlanetsManager(), element, manager);
        else if (element.getName().matches("planet6"))
            agregarPlaneta6(challenge.getPlanetsManager(), element, manager);
        else if (element.getName().matches("planet7"))
            addPlanet7(challenge.getPlanetsManager(), element, manager);
        if (element.getChildCount() > 0) {
            for (int i = 0; i < element.getChildCount(); i++) {
                getPlanets(challenge, element.getChild(i), manager);
            }
        }
    }

    private static void getReboteTreats(Challenge challenge, XmlReader.Element element, AssetManager manager) {
        Array<XmlReader.Element> dulceElements = element.getChildrenByNameRecursively("treat");
        for (int i = 0; i < dulceElements.size; i++){
            EFECTO = dulceElements.get(i).get("efecto", null);
            REVELA = dulceElements.get(i).get("revela", null);
            if (EFECTO == null) {
                String tipo = dulceElements.get(i).get("tipo", "normal");
                if (tipo.matches("rebote")) {
                    placeAReboteTreat(challenge, dulceElements.get(i), manager);
                }
            }
        }
    }

    private static void getTreats(Challenge challenge, XmlReader.Element element, AssetManager manager) {
        Array<XmlReader.Element> dulceElements = element.getChildrenByNameRecursively("treat");
        for (int i = 0; i < dulceElements.size; i++){
            String tipo = dulceElements.get(i).get("tipo", "normal");
            if (tipo.matches("rebote")) {
                EFECTO = dulceElements.get(i).get("efecto", null);
                REVELA = dulceElements.get(i).get("revela", null);
                placeAReboteTreat(challenge, dulceElements.get(i), manager);
            }
        }
        for (int i = 0; i < dulceElements.size; i++) {
            EFECTO = dulceElements.get(i).get("efecto", null);
            REVELA = dulceElements.get(i).get("revela", null);
            Array<XmlReader.Element> planetaElements = getAllPlanetElements(dulceElements.get(i));
            if (planetaElements.size > 0) {
                placeSurroundingTreats(challenge, dulceElements.get(i), planetaElements, manager);
            } else {
                String tipo = dulceElements.get(i).get("tipo", "normal");
                if (tipo.matches("linea")) {
                    placeALineTreat(challenge, dulceElements.get(i), manager);
                } else if (tipo.matches("normal")) {
                    colocarDulceTipoNormal(challenge, dulceElements.get(i), manager);
                }
            }
        }
    }

    private static Array<XmlReader.Element> getAllPlanetElements(XmlReader.Element element){
        Array<XmlReader.Element> planetaElements = element.getChildrenByNameRecursively("planet1");
        planetaElements.addAll(element.getChildrenByNameRecursively("planet2"));
        planetaElements.addAll(element.getChildrenByNameRecursively("planet3"));
        planetaElements.addAll(element.getChildrenByNameRecursively("planet4"));
        planetaElements.addAll(element.getChildrenByNameRecursively("planet5"));
        planetaElements.addAll(element.getChildrenByNameRecursively("planet6"));
        planetaElements.addAll(element.getChildrenByNameRecursively("planet7"));
        return planetaElements;
    }

    private static void getHabilitaTreats(Challenge challenge, XmlReader.Element element, AssetManager manager) {
        Array<XmlReader.Element> dulceElements = element.getChildrenByNameRecursively("treatHabilita");
        for (int i = 0; i < dulceElements.size; i++) {
            Array<XmlReader.Element> planetaElements = getAllPlanetElements(dulceElements.get(i));
            if (planetaElements.size > 0) {
                float pX = Dimensions.juego_PorcentajeAReal_X(
                        planetaElements.get(0).getFloat("x"));
                float pY = Dimensions.juego_PorcentajeAReal_Y(
                        planetaElements.get(0).getFloat("y"));
                float pSize = planetaElements.get(0).getFloat("size");
                float angulo = dulceElements.get(i).getFloat("angulo", 0);

                Planet p = new Planet(pX, pY, pSize, Planet.DIRECTION.NORMAL, manager);
                float x = (float) (p.getCenter().x + Math.cos(angulo / 57.2958) * (p.getRadius() + 10));
                float y = (float) (p.getCenter().y + Math.sin(angulo / 57.2958) * (p.getRadius() + 10));
                String id = dulceElements.get(i).get("id");
                TreatHabilita dulceHabilita = new TreatHabilita(x, y, id, manager);
                Float tiempo = dulceElements.get(i).getFloat("tiempo", Values.HABILITA_TIME_DEFAULT);
                dulceHabilita.setTime(tiempo);
                List <Planet> planets = challenge.getPlanetsManager().getPlanetsByHabilitaId(id);
                for (Planet planet : planets) {
                    dulceHabilita.addPlanet(planet);
                }
                challenge.getTreatsManager().addHabilitaTreat(dulceHabilita);
            }
        }

    }

    private static void getRevelaTreats(Challenge challenge, XmlReader.Element element, AssetManager manager) {
        Array<XmlReader.Element> dulceElements = element.getChildrenByNameRecursively("treatRevela");
        for (int i = 0; i < dulceElements.size; i++) {
            Array<XmlReader.Element> planetaElements = getAllPlanetElements(dulceElements.get(i));
            if (planetaElements.size > 0) {
                float pX = Dimensions.juego_PorcentajeAReal_X(
                        planetaElements.get(0).getFloat("x"));
                float pY = Dimensions.juego_PorcentajeAReal_Y(
                        planetaElements.get(0).getFloat("y"));
                float pSize = planetaElements.get(0).getFloat("size");
                float angulo = dulceElements.get(i).getFloat("angulo", 0);
                Planet p = new Planet(pX, pY, pSize, Planet.DIRECTION.NORMAL, manager);
                float x = (float) (p.getCenter().x + Math.cos(angulo / 57.2958) * (p.getRadius() + 10));
                float y = (float) (p.getCenter().y + Math.sin(angulo / 57.2958) * (p.getRadius() + 10));
                String id = dulceElements.get(i).get("id");
                TreatRevela dulceRevela = new TreatRevela(x, y, id, manager);
                challenge.getTreatsManager().addRevelaTreat(dulceRevela);
            }
        }
    }

    private static void getTreatsEfecto(Challenge challenge, XmlReader.Element element, AssetManager manager){
        List<TreatEfecto> treatsEfecto = new  ArrayList<TreatEfecto>();
        Array<XmlReader.Element> dulceElements = element.getChildrenByNameRecursively("treatEfecto");

        for (int i = 0; i < dulceElements.size; i++) {
            Array<XmlReader.Element> planetaElements = getAllPlanetElements(dulceElements.get(i));
            EFECTO = dulceElements.get(i).get("efecto", null);
            REVELA = dulceElements.get(i).get("revela", null);
            String id = dulceElements.get(i).get("id");

            if (planetaElements.size > 0) {
                String pid = planetaElements.get(0).get("id", null);
                Planet p;
                if (pid == null) {
                    float pX = Dimensions.juego_PorcentajeAReal_X(
                            planetaElements.get(0).getFloat("x"));
                    float pY = Dimensions.juego_PorcentajeAReal_Y(
                            planetaElements.get(0).getFloat("y"));
                    float pSize = planetaElements.get(0).getFloat("size");
                    p = new Planet(pX, pY, pSize, Planet.DIRECTION.NORMAL, manager);
                } else {
                    p = challenge.getPlanetsManager().getPlanetById(pid);
                }
                float angulo = dulceElements.get(i).getFloat("angulo", 0);

                float x = (float) (p.getCenter().x + Math.cos(angulo / 57.2958) * (p.getRadius() + 10));
                float y = (float) (p.getCenter().y + Math.sin(angulo / 57.2958) * (p.getRadius() + 10));

                TreatEfecto dulceEfecto = new TreatEfecto(x, y, id, manager);
                challenge.getTreatsManager().addEfectoTreat(dulceEfecto);
                if (EFECTO == null){
                    challenge.getTreatsManager().addActiveEfectoTreat(dulceEfecto);
                }
                else {
                    treatsEfecto.add(dulceEfecto);
                    dulceEfecto.setEfecto(EFECTO);
                }
            } else {
                float xRel = dulceElements.get(i).getFloat("x");
                float yRel = dulceElements.get(i).getFloat("y");
                float x = Dimensions.juego_PorcentajeAReal_X(xRel);
                float y = Dimensions.juego_PorcentajeAReal_Y(yRel);
                TreatEfecto dulceEfecto = new TreatEfecto(x, y, id, manager);
                challenge.getTreatsManager().addEfectoTreat(dulceEfecto);
                if (EFECTO == null){
                    challenge.getTreatsManager().addActiveEfectoTreat(dulceEfecto);
                }
                else {
                    treatsEfecto.add(dulceEfecto);
                    dulceEfecto.setEfecto(EFECTO);
                }
            }
        }
        for (TreatEfecto dulceEfecto : treatsEfecto) {
            challenge.getTreatsManager().getEfectoTreatById(dulceEfecto.getEfecto()).agregarDulceEfecto(dulceEfecto);
        }
    }

    private static void placeSurroundingTreats(Challenge challenge, XmlReader.Element dulce,
                                               Array<XmlReader.Element> pla, AssetManager manager){
        int coleccion = dulce.getInt("coleccion", 0);
        int anguloInicial = dulce.getInt("angulo", 0);
        int amplitud = dulce.getInt("amplitud", 360);
        int cuantos = dulce.getInt("cuantos", 5);
        for (int l = 0; l < pla.size; l++) {
            String id = pla.get(l).get("id", null);
            float xRel, yRel, size;
            if (id == null) {
                xRel = pla.get(l).getFloat("x");
                yRel = pla.get(l).getFloat("y");
                size = pla.get(l).getFloat("size");
                float x = Dimensions.juego_PorcentajeAReal_X(xRel);
                float y = Dimensions.juego_PorcentajeAReal_Y(yRel);
                Planet p = new Planet(x, y, size, Planet.DIRECTION.NORMAL, manager);
                surroundWithTreats(challenge, p, coleccion, cuantos, anguloInicial, amplitud, manager);
                p.dispose();
            }
            else{
                surroundWithTreats(challenge, challenge.getPlanetsManager().getPlanetById(id),
                        coleccion, cuantos, anguloInicial, amplitud, manager);
            }

        }
    }

    private static void placeAReboteTreat(Challenge challenge, XmlReader.Element dulce, AssetManager manager){
        String idOrigen = dulce.get("idOrigen", null);
        float distancia = dulce.getFloat("distancia", -1);
        float angulo = dulce.getFloat("anguloPartida");
        int no = dulce.getInt("no");
        Planet pOrigen = challenge.getPlanetsManager().getPlanetById(idOrigen);
        String idDestino = dulce.get("idDestino", null);
        String sColeccion = dulce.get("coleccion", null);
        REVELA = dulce.get("revela", null);
        Queue<Integer> colecciones = new LinkedList();
        if (sColeccion != null) {
            if (sColeccion.matches("ascendente")){
                for (int i = 0; i < no; i++) {
                    colecciones.add(i);
                }
            }
            else if (sColeccion.matches("descendente")){
                for (int i = no - 1; i >= 0; i++) {
                    colecciones.add(i);
                }
            }
            else {
                String[] colec = sColeccion.split(" ");
                if (colec[0].matches("ascendente")){
                    for (int i = 0; i < no; i++) {
                        colecciones.add(Integer.parseInt(colec[1]) + i);
                    }
                }
                else if (colec.length == 1) {
                    for (int i = 0; i < no; i++) {
                        colecciones.add(Integer.parseInt(colec[0]));
                    }
                }
                else{
                    for (String aColec : colec) {
                        colecciones.add(Integer.parseInt(aColec));
                    }
                    for (int i = colec.length; i < no; i++) {
                        colecciones.add(0);
                    }
                }
            }
        }
        else {
            for (int i = 0; i < no; i++) {
                colecciones.add(0);
            }
        }
        if (distancia == -1) {
            reboteTreats(challenge, pOrigen, angulo, no, colecciones, manager);
        }
        else{
            if (idDestino == null) {
                reboteTreats(challenge, pOrigen, angulo, no, colecciones, distancia, manager);
            }
            else{
                Planet pDestino = challenge.getPlanetsManager().getPlanetById(idDestino);
                reboteTreats(challenge, pOrigen, pDestino, angulo, no, colecciones, distancia, manager);
            }
        }
    }

    private static void placeALineTreat(Challenge challenge, XmlReader.Element dulce, AssetManager manager){
        int coleccion = dulce.getInt("coleccion", 0);
        String idOrigen = dulce.get("idOrigen", null);
        int cuantos = dulce.getInt("cuantos", -1);
        float xi = dulce.getFloat("xi", -1);
        float xf = dulce.getFloat("xf", -1);
        float yi = dulce.getFloat("yi", -1);
        float yf = dulce.getFloat("yf", -1);
        String idDestino = dulce.get("idDestino", null);
        if (idOrigen != null) {
            if (xf != -1) {
                Planet pOrigen = challenge.getPlanetsManager().getPlanetById(idOrigen);
                if (cuantos != -1){
                    treatsLine(challenge, pOrigen,
                        Dimensions.juego_PorcentajeAReal_X(xf),
                        Dimensions.juego_PorcentajeAReal_Y(yf),
                        coleccion, cuantos, manager);
                }
                else{
                    treatsLine(challenge, pOrigen,
                        Dimensions.juego_PorcentajeAReal_X(xf),
                        Dimensions.juego_PorcentajeAReal_Y(yf),
                        coleccion, manager);
                }
            } else {
                Planet pOrigen = challenge.getPlanetsManager().getPlanetById(idOrigen);
                Planet pDestino = challenge.getPlanetsManager().getPlanetById(idDestino);
                try {
                    if (cuantos != -1) {
                        treatsLine(challenge, pOrigen, pDestino, coleccion, cuantos, manager);
                    }
                    else{
                        treatsLine(challenge, pOrigen, pDestino, coleccion, manager);
                    }
                } catch (Exception e) {
                    if (pOrigen == null || pDestino == null) {
                        System.out.println("ERROR: Id(s) no coincide(n) con ningún planeta.");
                    }
                }
            }

        } else if (xi != -1) {
            if (cuantos != -1) {
                treatsLine(challenge, Dimensions.juego_PorcentajeAReal_X(xi),
                        Dimensions.juego_PorcentajeAReal_Y(yi),
                        Dimensions.juego_PorcentajeAReal_X(xf),
                        Dimensions.juego_PorcentajeAReal_Y(yf),
                        coleccion, cuantos, manager);
            }
            else{
                treatsLine(challenge, Dimensions.juego_PorcentajeAReal_X(xi),
                        Dimensions.juego_PorcentajeAReal_Y(yi),
                        Dimensions.juego_PorcentajeAReal_X(xf),
                        Dimensions.juego_PorcentajeAReal_Y(yf),
                        coleccion, manager);
            }
        }
    }

    private static void placeATreat(float x, float y, AssetManager manager, int coleccion, Challenge challenge) {

        if (EFECTO == null) {
            if (DULCE_AGREGANDO == DULCE_NORMAL){
                Treat d = new Treat(x, y, manager);
                challenge.getTreatsManager().addTreat(d, coleccion);
                if (REVELA != null){
                    challenge.getTreatsManager().getRevelaTreatById(REVELA).agregarDulce(d);
                }
            }
            else if (DULCE_AGREGANDO == DULCE_TIEMPO) {
                TreatTiempo d = new TreatTiempo(x, y, challenge.getTimeManager(), manager);
                challenge.getTreatsManager().addTiempoTreat(d);
            }
        }
        else{
            if (DULCE_AGREGANDO == DULCE_NORMAL) {
                Treat d = new Treat(x, y, manager);
                challenge.getTreatsManager().getEfectoTreatById(EFECTO).agregarDulce(d);
                if (REVELA != null){
                    challenge.getTreatsManager().getRevelaTreatById(REVELA).agregarDulce(d);
                }
            }
            else if (DULCE_AGREGANDO == DULCE_TIEMPO) {
                TreatTiempo d = new TreatTiempo(x, y, challenge.getTimeManager(), manager);
                challenge.getTreatsManager().getEfectoTreatById(EFECTO).agregarDulceTiempo(d);
            }
        }
    }

    private static void colocarDulceTipoNormal(Challenge challenge, XmlReader.Element dulce, AssetManager manager){
        int coleccion = dulce.getInt("coleccion", 0);
        float xRel = dulce.getFloat("x");
        float yRel = dulce.getFloat("y");
        float x = Dimensions.juego_PorcentajeAReal_X(xRel);
        float y = Dimensions.juego_PorcentajeAReal_Y(yRel);
        placeATreat(x, y, manager, coleccion, challenge);
    }

    private static void surroundWithTreats(Challenge challenge, Planet planet, int coleccion, int num,
                                           float angInicial, float amplitud, AssetManager manager) {
        float x, y;
        float angulo = angInicial;
        float anguloEntreDulces;
        if (amplitud == 360) {
            anguloEntreDulces = num == 1 ? amplitud : amplitud / (num);
        }
        else {
            anguloEntreDulces = num == 1 ? amplitud : amplitud / (num - 1);
        }
        for (int i = 0; i < num; i++) {
            x = (float) (planet.getCenter().x + Math.cos(angulo / 57.2958) * (planet.getRadius() + 10));
            y = (float) (planet.getCenter().y + Math.sin(angulo / 57.2958) * (planet.getRadius() + 10));

            placeATreat(x, y, manager, coleccion, challenge);

            angulo += anguloEntreDulces;
        }
    }

    /* coloca dulces en línea desde un punto inicial a un punto final */
    private static void treatsLine(Challenge challenge, float xi, float yi, float xf,
                                   float yf, int coleccion, int cuantos, AssetManager manager) {
        float x = xi, y = yi;
        float distx = (xf - xi) / (cuantos + 1);
        float disty = (yf - yi) / (cuantos + 1);
        x += distx;
        y += disty;

        for (int i = 0; i < cuantos; i++) {
            placeATreat(x, y, manager, coleccion, challenge);
            x += distx;
            y += disty;
        }
    }
    private static void treatsLine(Challenge challenge, float xi, float yi, float xf, float yf, int coleccion, AssetManager manager) {
        treatsLine(challenge, xi, yi, xf, yf, coleccion, treatsQuantity(xi, yi, xf, yf), manager);
    }

    /* coloca dulces en línea desde un planeta inicial a un punto final */
    private static void treatsLine(Challenge challenge, Planet plaOrigen, float xf, float yf,
                                   int coleccion, int cuantos, AssetManager manager) {
        double angulo = Math.atan2((double)(yf - plaOrigen.getCenter().y), (double)(xf - plaOrigen.getCenter().x));
        float pXi = plaOrigen.getCenter().x + (float) Math.cos(angulo) * ((plaOrigen.getRadius() + 10) * 0.5f);
        float pYi = plaOrigen.getCenter().y + (float) Math.sin(angulo) * ((plaOrigen.getRadius() + 10) * 0.5f);
        treatsLine(challenge, pXi, pYi, xf, yf, coleccion, cuantos, manager);
    }

    private static void treatsLine(Challenge challenge, Planet planet, float xf, float yf, int coleccion, AssetManager manager) {
        treatsLine(challenge, planet, xf, yf, coleccion, treatsQuantity(planet.getCenter().x, planet.getCenter().y,xf,yf), manager);
    }

    /* coloca dulces en línea desde un planeta inicial a un planeta final */
    private static void treatsLine(Challenge challenge, Planet plaOrigen, Planet plaDestino,
                                   int coleccion, int cuantos, AssetManager manager) {
        float xi, yi, xf, yf;
        double angulo = Math.atan2((double) (plaDestino.getCenter().y - plaOrigen.getCenter().y),
                (double) (plaDestino.getCenter().x - plaOrigen.getCenter().x));
        xi = plaOrigen.getCenter().x + (float) Math.cos(angulo) * (plaOrigen.getRadius());// * 0.5f);
        yi = plaOrigen.getCenter().y + (float) Math.sin(angulo) * (plaOrigen.getRadius());// * 0.5f);
        xf = plaDestino.getCenter().x - (float) Math.cos(angulo) * (plaDestino.getRadius());// * 0.5f);
        yf = plaDestino.getCenter().y - (float) Math.sin(angulo) * (plaDestino.getRadius());// * 0.5f);
        treatsLine(challenge, xi, yi, xf, yf, coleccion, cuantos, manager);
    }

    private static void treatsLine(Challenge challenge, Planet plaOrigen, Planet plaDestino,
                                   int coleccion, AssetManager manager) {
        treatsLine(challenge, plaOrigen, plaDestino, coleccion,
                treatsQuantity(plaOrigen.getCenter().x, plaOrigen.getCenter().y,
                        plaDestino.getCenter().x, plaDestino.getCenter().y), manager);
    }

    private static float reboteTreats(Challenge challenge, Planet plaOrigen, float angulo, int no,
                                      Queue<Integer> colecciones, AssetManager manager) {
        float x_salida = plaOrigen.getCenter().x, y_salida = plaOrigen.getCenter().y;
        rebotePosition(angulo, x_salida, y_salida);
        treatsLine(challenge, plaOrigen, x_llegada, y_llegada, colecciones.remove(), manager);
        for (int i = 1; i < no; i++){
            angulo = angleAfterBouncing(angulo, x_llegada, y_llegada);
            x_salida = x_llegada;
            y_salida = y_llegada;
            rebotePosition(angulo, x_salida, y_salida);
            treatsLine(challenge, x_salida, y_salida, x_llegada, y_llegada, colecciones.remove(), manager);
        }
        x_salida = x_llegada;
        y_salida = y_llegada;
        angulo = angleAfterBouncing(angulo, x_salida, y_salida);
        return angulo;
    }

    private static void reboteTreats(Challenge challenge, Planet plaOrigen, float angulo, int no,
                                     Queue<Integer> colecciones, float distancia, AssetManager manager) {
        float anguloPartida = reboteTreats(challenge, plaOrigen, angulo, no - 1, colecciones, manager);
        if (no > 1) {
            float x_salida = x_llegada;
            float y_salida = y_llegada;
            rebotePosition(anguloPartida, x_salida, y_salida);
            float dx = (x_salida - x_llegada);
            float dy = (y_salida - y_llegada);
            float d = (float) (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
            distancia = distancia * d;
            x_llegada = x_salida + (float)(distancia * Math.cos(Math.toRadians(anguloPartida)));
            y_llegada = y_salida + (float)(distancia * Math.sin(Math.toRadians(anguloPartida)));
            treatsLine(challenge, x_salida, y_salida, x_llegada, y_llegada, colecciones.remove(), manager);
        }
    }

    private static void reboteTreats(Challenge challenge, Planet plaOrigen, Planet plaDestino, float angulo,
                                     int no, Queue<Integer> colecciones, float distancia, AssetManager manager) {
        if (no > 1) {
            float anguloPartida;
            anguloPartida = reboteTreats(challenge, plaOrigen, angulo, no - 1, colecciones, manager);
            float x_salida = x_llegada;
            float y_salida = y_llegada;
            rebotePosition(anguloPartida, x_salida, y_salida);
            float dx = x_salida - x_llegada;
            float dy = y_salida - y_llegada;
            float d = (float) (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
            distancia = distancia * d;
            x_llegada = x_salida + (float)(distancia * Math.cos(Math.toRadians(anguloPartida)));
            y_llegada = y_salida + (float)(distancia * Math.sin(Math.toRadians(anguloPartida)));
            plaDestino.setPosition(x_llegada, y_llegada);
            treatsLine(challenge, plaDestino, x_salida, y_salida, colecciones.remove(), manager);
        }
        else{
          //  float anguloPartida = angulo;
            float x_salida = plaOrigen.getCenter().x;
            float y_salida = plaOrigen.getCenter().y;
            rebotePosition(angulo, x_salida, y_salida);
            float dx = x_salida - x_llegada;
            float dy = y_salida - y_llegada;
            float d = (float) (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)));
            distancia = distancia * d;
            x_llegada = x_salida + (float)(distancia * Math.cos(Math.toRadians(angulo)));
            y_llegada = y_salida + (float)(distancia * Math.sin(Math.toRadians(angulo)));
            plaDestino.setPosition(x_llegada, y_llegada);
            treatsLine(challenge, plaOrigen, plaDestino, colecciones.remove(), manager);
        }
    }

    private static float angleAfterBouncing(float angle, float x, float y){
        if (x <= Dimensions.limiteIzquierdo
                || x >= Dimensions.limiteDerecho){
            if (angle < 180)
                angle = 180 - angle;
            else
                angle = (540 - angle);
        }
        else if (y <= Dimensions.limiteInferior
                || y >= Dimensions.limiteSuperior){
            angle = 360 - angle;
        }
        return angle;
    }

    private static void rebotePosition(float angle, float x_salida, float y_salida){
        if (angle >= 0 &&  angle < 90){
            rebotePosition1(angle, x_salida, y_salida);
        }
        else if (angle >= 90 && angle < 180){
            rebotePosition2(angle, x_salida, y_salida);
        }
        else if (angle >= 180 && angle < 270){
            rebotePosition3(angle, x_salida, y_salida);
        }
        else if (angle >= 270 && angle < 360){
            rebotePosition4(angle, x_salida, y_salida);
        }
    }
    private static void rebotePosition1(float angle, float x_salida, float y_salida) {
        double CA = (Dimensions.limiteDerecho - x_salida);
        double CO = (Dimensions.limiteSuperior - y_salida);
        double Hx = CA / Math.cos(Math.toRadians(angle));
        double Hy = CO / Math.sin(Math.toRadians(angle));
        float xf, yf;
        if (Hx < Hy) {
            xf = Dimensions.limiteDerecho;
            double xCO = Math.sqrt(Hx * Hx - CA * CA);
            yf = (float)(y_salida + xCO);
        }
        else {
            yf = Dimensions.limiteSuperior;
            double yCA = Math.sqrt(Hy * Hy - CO * CO);
            xf = (float) (x_salida + yCA);
        }
        x_llegada = xf;
        y_llegada = yf;
    }
    private static void rebotePosition2(float angle, float x_salida, float y_salida){
        double CA = (x_salida - Dimensions.limiteIzquierdo);
        double CO = (Dimensions.limiteSuperior - y_salida);
        angle = 180 - angle;
        double Hx = CA / Math.cos(Math.toRadians(angle));
        double Hy = CO / Math.sin(Math.toRadians(angle));
        float xf, yf;
        if (Hx < Hy) {
            xf = Dimensions.limiteIzquierdo;
            double xCO = Math.sqrt(Hx * Hx - CA * CA);
            yf = (float)(y_salida + xCO);
        }
        else {
            yf = Dimensions.limiteSuperior;
            double yCA = Math.sqrt(Hy * Hy - CO * CO);
            xf = (float)(x_salida - yCA);
        }
        x_llegada = xf;
        y_llegada = yf;
    }
    private static void rebotePosition3(float angle, float x_salida, float y_salida) {
        double CA = (x_salida - Dimensions.limiteIzquierdo);
        double CO = (y_salida - Dimensions.limiteInferior);
        angle = angle - 180;
        double Hx = CA / Math.cos(Math.toRadians(angle));
        double Hy = CO / Math.sin(Math.toRadians(angle));
        float xf, yf;
        if (Hx < Hy) {
            xf = Dimensions.limiteIzquierdo;
            double xCO = Math.sqrt(Hx * Hx - CA * CA);
            yf = (float)(y_salida - xCO);
        }
        else {
            yf = Dimensions.limiteInferior;
            double yCA = Math.sqrt(Hy * Hy - CO * CO);
            xf = (float)(x_salida - yCA);
        }
        x_llegada = xf;
        y_llegada = yf;
    }
    private static void rebotePosition4(float angle, float x_salida, float y_salida) {
        double CA = (Dimensions.limiteDerecho - x_salida);
        double CO = (y_salida - Dimensions.limiteInferior);
        angle = 360 - angle;
        double Hx = CA / Math.cos(Math.toRadians(angle));
        double Hy = CO / Math.sin(Math.toRadians(angle));
        float xf, yf;
        if (Hx < Hy) {
            xf = Dimensions.limiteDerecho;
            double xCO = Math.sqrt(Hx * Hx - CA * CA);
            yf = (float)(y_salida - xCO);
        }
        else {
            yf = Dimensions.limiteInferior;
            double yCA = Math.sqrt(Hy * Hy - CO * CO);
            xf = (float)(x_salida + yCA);
        }
        x_llegada = xf;
        y_llegada = yf;
    }
    private static int treatsQuantity(float xi, float yi, float xf, float yf){
        double x = Math.abs(xf - xi);
        double y = Math.abs(yf - yi);
        double distance = Math.sqrt(x * x + y * y);
        return (int) (distance / Values.DISTANCE_PER_TREAT);

    }
}
