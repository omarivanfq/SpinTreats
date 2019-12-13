package com.planetas4.game.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Cat;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Planets.Planet;
import com.planetas4.game.Planets.Planet2;
import com.planetas4.game.Planets.Planet3;
import com.planetas4.game.Planets.Planet4;
import com.planetas4.game.Planets.Planet5;
import com.planetas4.game.Planets.Planet6;
import com.planetas4.game.Planets.Planet7;

import java.util.ArrayList;
import java.util.List;

/*
*
*  Manages all the logic of the planets of a challenge
*
*   "current planet": planet that is being currently visited by the cat or last planet that was visited
*   "enabled planet": planet that is visible and can be visited
*   planet1: normal planet
*   planet2: planet that disappears after being visited n times
*   planet3: planet with thorns that can cause damage
*   planet4: planet that cannot be visited (if you try you get bounced back)
*   planet5: planet that transports you to another one
*   planet6: planet that needs to be manually rotated
*   planet7: planet that allows you to see the hidden candies when visited
*
* */

public class PlanetsManager {

    private Stage stage;
    private List<Planet> planets; // list that holds all the planets of the challenge
    private List<Planet6> planets6;
    private List<Planet7> planets7;
    private int currentPlanetIndex; // indicates the index of the list that points to the current planet
    private int previousPlanetIndex; // indicates the index of the list that points to the planet that
        // was visited before the current planet (needed for planet4 purposes)
    private HidingTreatsListener hidingTreatsListener;
    private Planet3.DamageHandler damageHandler;
    private Sound explosionSound;

    // interface needed for planet7s
    public interface HidingTreatsListener {
        void onHide();
        void onShow();
    }

    public PlanetsManager(
            Viewport viewport,
            HidingTreatsListener hidingTreatsListener, // what to do when treats needs to be hidden or shown (planet7)
            Planet3.DamageHandler damageHandler,       // what to do when cat is damaged (planet3)
            AssetManager manager
    ){
        this.damageHandler = damageHandler;
        planets = new ArrayList<Planet>();
        currentPlanetIndex = -1;
        this.stage = new Stage(viewport);
        planets6 = new ArrayList<Planet6>();
        planets7 = new ArrayList<Planet7>();
        this.hidingTreatsListener = hidingTreatsListener;
        explosionSound = manager.get("sounds/pop.mp3", Sound.class);
    }

    public List<Planet> getPlanets() {
        return planets;
    }
    public int getCurrentPlanetIndex() {return currentPlanetIndex;}
    public void setCurrentPlanetIndex(int currentPlanetIndex) { this.currentPlanetIndex = currentPlanetIndex; }
    public Planet getPlanet(int i) { return planets.get(i); }

    public Planet getPlanetById(String id){
        for (Planet planet : planets){
            if (planet.getId().matches(id))
                return planet;
        }
        return null;
    }
    
    public Planet getCurrentPlanet(){
        if (currentPlanetIndex == -1) { return null; }
        return planets.get(currentPlanetIndex);
    }

    public void forgetPlanet() {
        this.setCurrentPlanetIndex(-1); // there is no longer a current planet
    }

    /* when the cat jumps from a planet */
    public void leavePlanet() {
        if (currentPlanetIndex != -1) {
            planets.get(currentPlanetIndex).setVisited(false); // current planet is no longer being visited (but is still current planet)
            if (planets.get(currentPlanetIndex) instanceof Planet7) {
                hidingTreatsListener.onHide(); // when the cat leaves a planet7 treats must be hidden
                    // the hidingTreatsListener handles this situation
            }
        }
    }

    public boolean hasPlanet7s() {
        return planets7.size() > 0;
    }

    public void addPlanet1(Planet planet) {
        planets.add(planet);
        stage.addActor(planet);
    }

    public void addPlanet4(Planet4 planet){
        planet.setBounceHandler(new Planet4.BounceHandler() {
            @Override
            public void onBounce(Planet planet, Cat cat) {
                if (SettingsManager.getVibration()){
                    Gdx.input.vibrate(Values.VIBRATION_TIME_DEFAULT); // vibration when the bouncing happens
                }
            }
        });
        planets.add(planet);
        stage.addActor(planet);
    }

    public void addPlanet2(Planet2 planet){
        planet.setDisappearListener(new Planet2.DisappearListener() {
            @Override
            public void onDisappear() {
                if (SettingsManager.getSound()){
                    explosionSound.play(1);
                }
                if (SettingsManager.getVibration()){
                    Gdx.input.vibrate(Values.VIBRATION_TIME_DEFAULT * 2);
                }
            }
        });
        planets.add(planet);
        stage.addActor(planet);
    }

    public void addPlanet3(Planet3 planet){
        planet.setDamageHandler(this.damageHandler);
        planets.add(planet);
        stage.addActor(planet);
    }

    public void addPlanet5(Planet5 planet){
        planet.setTransferHandler(new Planet5.TransferHandler() {
            @Override
            public void onTransfer(String destinationId) {
                setVisitedPlanet(
                        planets.indexOf(getPlanetById(destinationId)) // handling the transporting situation
                );
            }
        });
        planets.add(planet);
        stage.addActor(planet);
    }

    public void addPlanet6(Planet6 planet){
        planets.add(planet);
        planets6.add(planet);
        stage.addActor(planet);
    }

    public void addPlanet7(Planet7 planet) {
        planets.add(planet);
        planets7.add(planet);
        stage.addActor(planet);
    }

    public void act(Cat cat){
        stage.act();

        if (cat.isFloating()) {
            collides(cat);
        }

        if (currentPlanetIndex != -1) {
            Planet currentPlanet = planets.get(currentPlanetIndex);

            // some visited planets need to be updated constantly
            if (currentPlanet instanceof Planet3) {
                ((Planet3) currentPlanet).updateVisit(cat);  // to check if cat is being damaged by thorns
            }
            else if (currentPlanet instanceof Planet4) {
                ((Planet4) currentPlanet).updateVisit(cat); // to bounce back
            }
            else if (currentPlanet instanceof Planet5) {
                Planet5 planet5 = (Planet5) currentPlanet;
                Planet planetTo = getPlanetById(planet5.getTransferId());
                planet5.updateVisit(planetTo, cat);    // to transfer if is needed yet
            }
            else if (currentPlanet instanceof Planet6) {
                ((Planet6) currentPlanet).updateVisit(cat);  // to release cat if needed
            }
        }
    }

    public void draw(SpriteBatch batch){
        batch.end();
        stage.draw();
        batch.begin();
    }

    // function called to bring cat back to the current planet (after it jumped from it)
    public void comesBack(Cat cat){
        Planet planet = planets.get(currentPlanetIndex);
        if (planet instanceof Planet4) {
            setVisitedPlanet(previousPlanetIndex); // you cannot visit a planet4 so you must instead visit the previous planet
        }
        // the planet must exist (planet2) and be enabled
        else if (planet.isEnabled() && !(planet instanceof Planet2)
                || (planet instanceof Planet2 && ((Planet2) planet).exists()) && planet.isEnabled()) {
            setVisitedPlanet(currentPlanetIndex);
            cat.setFloating(false);
        }
    }
    
    private void setVisitedPlanet(int i) {
        if (i != -1) {
            previousPlanetIndex = i;
            setCurrentPlanetIndex(i);
            planets.get(currentPlanetIndex).setVisited(true);
            if (planets.get(i) instanceof Planet7) {
                hidingTreatsListener.onShow(); // if a planet7 is visited treats must be shown 
                    // the hidingTreatsListener handles this situation
            }
        }
    }
    
    public void restart(){
        for (Planet planet : planets){
            planet.restart();
        }
        leavePlanet();
        if (hasPlanet7s()){
            hidingTreatsListener.onHide();
        }
        setVisitedPlanet(0);
    }

    public void reverseRotation(){
        for (Planet planet : planets){
            planet.reverseRotation();
        }
    }

    public List<Planet> getPlanetsByHabilitaId(String id) {
        List<Planet> habilitaPlanets = new ArrayList<Planet>();
        for (Planet planet : planets) {
            if (planet.matchesHabilitaId(id)) {
                habilitaPlanets.add(planet);
            }
        }
        return habilitaPlanets;
    }

    // check if cat collides with a planet
    private void collides(Cat cat){
        for (Planet planet : planets) {
            if (planets.indexOf(planet) != currentPlanetIndex
                    && planet.isCollided(cat)) {
                if (currentPlanetIndex != -1 && !(planets.get(currentPlanetIndex) instanceof Planet4)) {
                    previousPlanetIndex = currentPlanetIndex;
                }
                if (planet instanceof Planet7) {
                    hidingTreatsListener.onShow();
                }
                currentPlanetIndex = planets.indexOf(planet);
                planet.coordinateWithCat(cat);
                cat.setFloating(false);
            }
        }
    }

    public void dispose(){
        for (Planet planet : planets) {
            planet.dispose();
        }
        planets.clear();
        stage.dispose();
    }

    public void pausePlanets(){
        for (Planet6 planet6 : planets6) {
            planet6.pause();
        }
    }

}
