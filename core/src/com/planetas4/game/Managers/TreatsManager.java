package com.planetas4.game.Managers;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Cat;
import com.planetas4.game.Treats.Treat;
import com.planetas4.game.Treats.TreatEfecto;
import com.planetas4.game.Treats.TreatHabilita;
import com.planetas4.game.Treats.TreatRevela;
import com.planetas4.game.Treats.TreatTiempo;

import java.util.ArrayList;
import java.util.List;

/*
*
*   Manages all the logic of the treats of a challenge
*
*   "collection": list of treats
*
*   Each TreatsManager has a list of collections. Every treat in a collection must be collected by the
*   cat to enable the next collection. When all the treats of all the collections have been collected
*   the challenge is surpassed.
*   If the current collection is NOT the last collection then its treats will "sparkle" so the user
*   can know that there are more treats coming.
*
*   The most simple challenges have a list with only one collection with at least one treat.
*
*/

public class TreatsManager {

    private List<List<Treat>> collections; // list of lists of treats
    private List<TreatEfecto> efectoTreats; // all the efecto treats of the challenge
    private List<TreatEfecto> activeEfectoTreats;  // all the efecto treats that are currently enabled
    private List<TreatRevela> revelaTreats;
    private List<TreatHabilita> habilitaTreats;
    private List<TreatTiempo> tiempoTreats;
    private int treatsCount;  // remaining treats to be collected of the current collection
    private Stage stage;
    private int currentCollectionIndex;  // index of collections
    private HidingTreatsListener hidingTreatsListener;
    private boolean hiding;
    private Sound sound;
    private float soundVolume;

    public interface HidingTreatsListener {
        void onShow(float time);
    }

    public TreatsManager(
                Viewport viewport,
                HidingTreatsListener hidingTreatsListener, // what to when treats must be hidden or shown (planet7)
                AssetManager manager
    ){
        this.stage = new Stage(viewport);
        collections = new ArrayList<List<Treat>>();
        currentCollectionIndex = 0;
        efectoTreats = new ArrayList<TreatEfecto>();
        activeEfectoTreats = new ArrayList<TreatEfecto>();
        revelaTreats = new ArrayList<TreatRevela>();
        habilitaTreats = new ArrayList<TreatHabilita>();
        tiempoTreats = new ArrayList<TreatTiempo>();
        this.hidingTreatsListener = hidingTreatsListener;
        hiding = false;
        sound = manager.get("sounds/collect3.mp3", Sound.class);
        soundVolume = (SettingsManager.getSound()? 1.0f : 0.0f);
    }

    /* void setSoundVolume(float soundVolume) { this.soundVolume = soundVolume; } */

    // function that expands the list of collections (with empty lists) up to the index passed as parameter
    private void adjustCollections(int collectionIndex){
        while (collections.size() <= collectionIndex){
            collections.add(new ArrayList<Treat>());
        }
    }

    public void addTreat(Treat treat, int collectionIndex){
        adjustCollections(collectionIndex);
        if (collectionIndex > 0)
            treat.setCollected(true); // every treat that is not in the first collection is set as collected so it doesn't show
        else {
            treatsCount++;
        }
        collections.get(collectionIndex).add(treat);
        stage.addActor(treat);
    }

    public void addHabilitaTreat(TreatHabilita habilitaTreat) {
        stage.addActor(habilitaTreat);
        habilitaTreats.add(habilitaTreat);
    }

    public void addRevelaTreat(TreatRevela revelaTreat) {
        revelaTreats.add(revelaTreat);
        stage.addActor(revelaTreat);
    }

    public void addEfectoTreat(TreatEfecto efectoTreat){
        efectoTreats.add(efectoTreat);
    }

    public void addTiempoTreat(TreatTiempo tiempoTreat) {
        tiempoTreats.add(tiempoTreat);
        stage.addActor(tiempoTreat);
    }

    public void addActiveEfectoTreat(TreatEfecto efectoTreat){
        activeEfectoTreats.add(efectoTreat);
        stage.addActor(efectoTreat);
    }

    public TreatEfecto getEfectoTreatById(String id){
        for (TreatEfecto efectoTreat : efectoTreats){
            if (efectoTreat.getId().matches(id)) {
                return efectoTreat;
            }
        }
        return null;
    }

    public TreatRevela getRevelaTreatById(String id){
        for (TreatRevela revelaTreat : revelaTreats){
            if (revelaTreat.getId().matches(id)) {
                return revelaTreat;
            }
        }
        return null;
    }

    private void collides(Cat cat){

        // checking for regular treats
        for (Treat treat : collections.get(currentCollectionIndex)) {
            if (!treat.isCollected() && treat.collides(cat)) {
                sound.play(soundVolume);
                treatsCount--;
            }
        }

        List<TreatEfecto> newEfectoTreats = new ArrayList<TreatEfecto>(); // creating temp list for possible new enabled efecto treats
        // checking for efecto treats
        for (TreatEfecto efectoTreat : activeEfectoTreats){

            if (efectoTreat.collides(cat)) { // if cat collects an efecto treat...
                sound.play(soundVolume);
                // checking for conjoint regular treats and adding them to the current collection
                for (Treat treat : efectoTreat.getTreats()){
                    collections.get(currentCollectionIndex).add(treat);
                    if (currentCollectionIndex < collections.size() - 1){
                        treat.sparkle();
                    }
                    stage.addActor(treat);
                    if (hiding) {
                        treat.hide();
                    }
                    else{
                        treat.show();
                    }
                }
                // restarting sparkle animation if needed to sync with all treats
                if (efectoTreat.getTreats().size() > 0 && currentCollectionIndex < collections.size() - 1) {
                    restartCurrentTreatsSparkle();
                }
                // checking for conjoin efecto treats and adding them to the temp list "newEfectoTreats"
                for (TreatEfecto d : efectoTreat.getEfectoTreats()){
                    newEfectoTreats.add(d);
                    stage.addActor(d);
                }
                // checking for conjoin tiempo treats and adding them to the tiempoTreats list
                for (TreatTiempo d : efectoTreat.getTiempoTreats()){
                    tiempoTreats.add(d);
                    stage.addActor(d);
                }
                treatsCount += efectoTreat.getTreats().size(); // updating treats count with the new treats enabled
                efectoTreat.setCollectionIndex(currentCollectionIndex);
            }
        }
        activeEfectoTreats.addAll(newEfectoTreats); // new enabled efecto treats added to the list

        // checking for revela treats
        for (TreatRevela treat : revelaTreats) {
            if (treat.collides(cat)) {
                sound.play(soundVolume);
                hidingTreatsListener.onShow(treat.getTiempoMuestra());
            }
        }

        // checking for habilita treats
        for (TreatHabilita treat : habilitaTreats) {
            if (treat.collides(cat)) {
                sound.play(soundVolume);
                treat.enable();
            }
        }

        // checking for tiempo treats
        for (TreatTiempo treat : tiempoTreats) {
            if (treat.collides(cat)) {
                sound.play(soundVolume);
            }
        }
    }

    private void restartCurrentTreatsSparkle() {
        for (Treat treat : collections.get(currentCollectionIndex)) {
            treat.restartSparkle();
        }
    }

    // to hide all the treats
   public void hide(){
        for (List<Treat> list : collections) {
            for (Treat treat : list) {
                treat.hide();
            }
        }
        for (Treat treat : activeEfectoTreats) {
            treat.hide();
        }
        hiding = true;
    }

    // to show all the treats
    public void show(){
        for (List<Treat> list : collections) {
            for (Treat treat : list) {
                treat.show();
            }
        }
        for (Treat treat : activeEfectoTreats) {
            treat.show();
        }
        hiding = false;
    }

    public void act(Cat cat){
        stage.act();
        if (currentCollectionIndex < collections.size()) {
            collides(cat);
            if (treatsCount == 0) { // current collection is over
                currentCollectionIndex++; // next collection
                if (currentCollectionIndex != collections.size()) {
                    for (Treat treat : collections.get(currentCollectionIndex)) {
                        treat.setCollected(false); // setting the current collection treats collected value to false so they will show
                    }
                    treatsCount = collections.get(currentCollectionIndex).size();
                }
            }
        }
    }

    public void draw(SpriteBatch batch){
        batch.end();
        stage.draw();
        batch.begin();
    }

    public boolean treatsAreOver(){
        return currentCollectionIndex >= collections.size();
    }

    public void restart(){
        currentCollectionIndex = 0;
        for (List<Treat> collection : collections) {
            for (Treat treat : collection) {
                treat.restart();
                treat.setCollected(true);
            }
        }

        for (TreatRevela treat : revelaTreats) {
            treat.restart();
        }

        for (TreatHabilita treat : habilitaTreats) {
            treat.restart();
        }

        for (TreatTiempo treat : tiempoTreats) {
            treat.restart();
        }

        for (TreatEfecto efectoTreat : efectoTreats){
            if (efectoTreat.getEfecto() != null){
                activeEfectoTreats.remove(efectoTreat);
                efectoTreat.remove();
            }
            for (Treat treat : efectoTreat.getTreats()){
                treat.restart();
                treat.remove();
                collections.get(efectoTreat.getCollectionIndex()).remove(treat);
            }
            for (TreatTiempo treat : efectoTreat.getTiempoTreats()){
                treat.restart();
                treat.remove();
                tiempoTreats.remove(treat);
            }
            efectoTreat.restart();
        }

        try {
            for (Treat treat : collections.get(0)) {
                treat.setCollected(false);
            }
            for (int i = 0; i < collections.size() - 1; i++){
                for (int j = 0; j < collections.get(i).size(); j++)
                    collections.get(i).get(j).sparkle();
            }
        }
        catch (Exception e){
            System.out.println("ERROR: Challenge sin dulces.");
        }
        treatsCount = collections.get(0).size();
    }

    public boolean hasRevelaTreats() {
        return revelaTreats.size() > 0;
    }

    public boolean isRevealing() {
        for (TreatRevela revelaTreat : revelaTreats){
            if (revelaTreat.isRevealing()) {
                return true;
            }
        }
        return false;
    }

    public void dispose(){
        for (TreatEfecto treatEfecto : efectoTreats) {
            treatEfecto.dispose();
        }
        for (List<Treat> collection : collections) {
            for (Treat treat : collection) {
                treat.dispose();
            }
            collection.clear();
        }
        stage.dispose();
    }

}
