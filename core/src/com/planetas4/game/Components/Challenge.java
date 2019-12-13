package com.planetas4.game.Components;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.planetas4.game.Cat;
import com.planetas4.game.Constants.Values;
import com.planetas4.game.Utils.Dimensions;
import com.planetas4.game.Managers.PlanetsManager;
import com.planetas4.game.Managers.SettingsManager;
import com.planetas4.game.Managers.TimeManager;
import com.planetas4.game.Managers.TreatsManager;
import com.planetas4.game.Planets.Planet;
import com.planetas4.game.Planets.Planet3;
import com.planetas4.game.Planets.Planet7;

/*
* This component contains the logic of each challenge in the game
*   (each level is formed by on or more challenges)
*   it implements the DamageHandler from Planet3 so it can do "the damage"
*   when cat is on Planet3s danger areas
* */

public class Challenge implements Planet3.DamageHandler {

    public enum BORDER_TYPE {NO_BORDER, BOUNCE, TRANSFER}
    /*
    * There are three types of borders
    *   NO_BORDER: if the cat reaches an edge of this kind of border it will go back to the planet
    *          it jumped from and there will be some time penalization.
    *   BOUNCE: if the cat reaches an edge of this kind of border it will bounce in a perpendicular
    *          angle and there will be some time penalization but it will be smaller than the
    *          NO_BORDER one.
    *   TRANSFER: if the cat reaches an edge of this kind of border it will be transfer to the
    *          opposite side of the screen and there is no time penalization.
    * */

    private TimeManager timeManager;  // practically a chronometer
    private TreatsManager treatsManager;  // manages the candies logic
    private PlanetsManager planetsManager;  // manages the planets logic
    private BORDER_TYPE borderType;
    private Border border;
    private long timeLimit;
    private Viewport viewport;
    private Planet3.DamageHandler damageHandler; // indicates what will happen when damage is
                                        // happening to the cat

    public Challenge(Viewport viewport, long timeLimit, BORDER_TYPE borderType,
                     AssetManager manager, PlanetsManager.HidingTreatsListener planetsHidingTreatsListener,
                     TreatsManager.HidingTreatsListener treatsHidingTreatsListener, Planet3.DamageHandler damageHandler){
        this.damageHandler = damageHandler;
        this.viewport = viewport;
        this.timeLimit = timeLimit;
        this.borderType = borderType;
        border = new Border(manager);
        if (borderType == BORDER_TYPE.NO_BORDER) {
            border.setColor(Color.BLACK);
        }
        else if (borderType == BORDER_TYPE.TRANSFER){
            border.setColor(Color.GREEN);
        }
        treatsManager = new TreatsManager(viewport, treatsHidingTreatsListener, manager);
        timeManager = new TimeManager(timeLimit);
        planetsManager = new PlanetsManager(viewport, planetsHidingTreatsListener, this, manager);
    }

    /* Planet3.DamageHandler interface implementation */

    @Override
    public void startDamage() {
        damageHandler.startDamage();
    }

    @Override
    public void doDamage(float delta) {
        damageHandler.doDamage(delta);
        timeManager.goForward(delta * 7.0f);
    }

    @Override
    public void stopDamage() {
        damageHandler.stopDamage();
    }


    public BORDER_TYPE getBorderType() {
        return borderType;
    }

    public void draw(SpriteBatch batch){
        border.draw(batch, 1);
        planetsManager.draw(batch);
        treatsManager.draw(batch);
    }

    public void update(float delta, Cat cat) {
        treatsManager.act(cat);
        timeManager.act(delta);
        planetsManager.act(cat);
        border.act(delta);

        if (cat.reachesEdge(viewport) > 0) {
            border.collides();
            switch (borderType) {
                case BOUNCE:
                    timeManager.goForward((timeLimit * Values.MAX_FORWARD_TIME_BOUNCE)); // time penalization
                    planetsManager.leavePlanet();
                    planetsManager.forgetPlanet(); // the "current planet" (the one the cat is/was on) is no longer the one it jumped from
                    cat.changeAngleAfterBouncing(viewport); // changing cat direction angle
                    cat.setFloating(true);
                    if (SettingsManager.getVibration()){
                        Gdx.input.vibrate(Values.VIBRATION_TIME_DEFAULT); // vibration effect
                    }
                    break;
                case NO_BORDER:
                    timeManager.goForward(timeBecauseDistance(cat)); // time penalization
                    planetsManager.comesBack(cat); // cat returns to the "current planet" (the one it jumped from)
                    break;
                case TRANSFER:
                    switch (cat.reachesEdge(viewport)){
                        case 1:
                            break;
                        case 2:
                            cat.setX(Dimensions.juego_PorcentajeAReal_X(0.02f));
                            break;
                        case 3:
                            cat.setX(Dimensions.juego_PorcentajeAReal_X(0.95f));
                            break;
                        case 4:
                            cat.setY(Dimensions.juego_PorcentajeAReal_Y(0.0f));
                            break;
                        case 5:
                            cat.setY(Dimensions.juego_PorcentajeAReal_Y(0.95f));
                            break;
                    }
                    planetsManager.leavePlanet();
                    planetsManager.forgetPlanet(); // the "current planet" (the one the cat is/was on) is no longer the one it jumped from
                    break;
            }
        }

    }

    /* indicates if the challenge was succesfully finished*/
    public boolean wasBeaten(){
        return treatsManager.treatsAreOver();
    }

    /* indicates if the challenge was already failed */
    public boolean wasFailed(){
        return timeManager.remainingTimePercentage() <= 0 && !treatsManager.treatsAreOver() ;
    }

    /* indicates if there are planets7 or "candies revela" */
    public boolean hasToReveal(){
        return treatsManager.hasRevelaTreats() || planetsManager.hasPlanet7s();
    }

    /* indicates if there are treats being revealed (because of planet7s) */
    public boolean isRevealing() {
        return treatsManager.isRevealing();
    }

    /* function that must be called when the user touches the screen */
    public void touches(Cat cat){
        if (!cat.isFloating()) {
            cat.setFloating(true);
            planetsManager.leavePlanet();
        }
    }

    /* indicates that the first planet (the one the cat starts in) is planet7 */
    public boolean startsRevealing() {
        return planetsManager.getPlanets().size() > 0
                && planetsManager.getPlanet(0) instanceof Planet7;
    }

    public void pausePlanets(){
        planetsManager.pausePlanets();
    }

    /* restart the challenge */
    public void restart(Cat cat){
        planetsManager.restart();
        treatsManager.restart();
        timeManager.restart();
        cat.setFloating(false);
        cat.rotate(planetsManager.getCurrentPlanet()); // es necesario que comience a "rotate" en este momento para llevar al cat a la posición del planeta y evitar conflictos con el "choque de borde"
    }

    public PlanetsManager getPlanetsManager() {
        return planetsManager;
    }
    public TreatsManager getTreatsManager() {
        return treatsManager;
    }
    public TimeManager getTimeManager() { return timeManager;}

    public Planet getPlanet(int i) { return planetsManager.getPlanet(i); }

    /* calculates the time penalization when the cat reaches a NO_BORDER*/
    private float timeBecauseDistance(Cat cat){
        Vector2 planetCenter = planetsManager.getPlanet(planetsManager.getCurrentPlanetIndex()).getCenter();
        Vector2 catCenter = cat.getCenter();
        double distance = Math.sqrt(Math.pow((catCenter.x - planetCenter.x), 2) +
                Math.pow((catCenter.y - planetCenter.y), 2));
        float damagePercentage = (float) (distance / Values.MAX_DISTANCE_BORDER_DAMAGE);
        float tiempoBase = (timeLimit < Values.LOST_TIME_LIMIT_BASE ? timeLimit : Values.LOST_TIME_LIMIT_BASE);
        return tiempoBase * Values.MAX_FORWARD_TIME_DEFAULT * damagePercentage;
    }

    public void dispose(){
        planetsManager.dispose();
        treatsManager.dispose();
        border.dispose();
    }

}
