package com.planetas4.game.Managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.planetas4.game.Components.Level;
import com.planetas4.game.Utils.LevelsLoader;
import com.planetas4.game.MainGame;
import com.planetas4.game.Planets.Planet3;
import com.planetas4.game.Components.UnlockableButton;

import java.util.ArrayList;
import java.util.List;

/*
*   Stores on the device the data about levels and sections surpassed and
*   provides an api to retrieve the levels and extra levels from other sections of the project
*
*      the "extra state" (if true) means that the user is currently playing an extra level
*      the "winner state" (if true) means that last level that the user played was a win
*
* */

public class LevelsManager {

    public static boolean everythingUnlocked = false;
    private static List<Integer> dimensions = LevelsLoader.getDimensions();
    private static List<Integer> dimensionsExtra = LevelsLoader.getExtraDimensions();
    private static Preferences preferences = Gdx.app.getPreferences("niveles_data__");
    private static String winnerState = "estado_ganador",
            newRecord = "nuevo_record",
            currentSection = "S_actual",
            currentLevel = "N_actual",
            extraState = "estado_extra",
            completedTime = "tiempo_completado";

    public static void unlockAllSections() {
        for (int i = 0; i < dimensions.size(); i++) {
            setUnlockedSection(i, true);
        }
    }

    static private void setExtraState(boolean extra){
        preferences.putBoolean(extraState, extra);
        preferences.flush();
    }

    public static boolean getExtraState() {
        return preferences.getBoolean(extraState);
    }

    static void setNormalScore(int section, int level, UnlockableButton.SCORE score){
        preferences.putString("N" + section + "." + level + "_score", scoreToString(score));
        preferences.flush();
    }

    static UnlockableButton.SCORE getScore(int section, int level){
        return stringToScore(preferences.getString("N" + section + "." + level + "_score"));
    }

    static UnlockableButton.SCORE getScoreExtra(int section, int level){
        return stringToScore(preferences.getString("N" + section + "." + level + "_score"));
    }

    private static String scoreToString(UnlockableButton.SCORE score){
        switch (score) {
            case NONE:
                return "none";
            case LOW:
                return "low";
            case MEDIUM:
                return "med";
            case HIGH:
                return "high";
            default:
                return "none";
        }
    }
    private static UnlockableButton.SCORE stringToScore(String score){
        if (score.equals("none")) {
            return UnlockableButton.SCORE.NONE;
        }
        if (score.equals("low")) {
            return UnlockableButton.SCORE.LOW;
        }
        if (score.equals("med")) {
            return UnlockableButton.SCORE.MEDIUM;
        }
        if (score.equals("high")) {
            return UnlockableButton.SCORE.HIGH;
        }
        return UnlockableButton.SCORE.NONE;
    }

    public static void setWinnerState(boolean state){
        preferences.putBoolean(winnerState, state);
        preferences.flush();
    }

     public static int getLevelNumber() {
        if (getExtraState()) {
            return getExtraLevelNumber();
        }
        else {
            return getNormalLevelNumber();
        }
    }

    public static int getNextNormalLevelNumber() {
        if (getExtraState()) {
            int levelToShow = 0;
            for (int i = 0; i <= getCurrentSection(); i++) {
                levelToShow += dimensions.get(i);
            }
            return levelToShow + 1;
        }
        else {
            return getNormalLevelNumber() + 1;
        }
    }

    static private int getNormalLevelNumber(){
        int levelToShow = 0;
        for (int i = 0; i < getCurrentSection(); i++) {
            levelToShow += dimensions.get(i);
        }
        levelToShow += getCurrentLevel() + 1;
        return levelToShow;
    }

    public static int getFirstNormalLevelNumber(int sectionIndex) {
        int levelToShow = 0;
        for (int i = 0; i < sectionIndex; i++) {
            levelToShow += dimensions.get(i);
        }
        return levelToShow;
    }

    static private int getExtraLevelNumber() {
        int levelToShow = 0;
        for (int i = 0; i < getCurrentSection(); i++) {
            levelToShow += dimensionsExtra.get(i);
        }
        levelToShow += getCurrentLevel() + 1;
        return levelToShow;
    }

    public static boolean getWinnerState(){return preferences.getBoolean(winnerState, false);}
    public static int getCurrentSection(){
        return preferences.getInteger(currentSection, 0);
    }
    public static int getCurrentLevel(){
        return preferences.getInteger(currentLevel, 0);
    }
    static private void setCurrentSection(int section){
        preferences.putInteger(currentSection, section);
        preferences.flush();
    }
    static private void setCurrentLevel(int level){
        preferences.putInteger(currentLevel, level);
        preferences.flush();
    }
    private static void unlockLevel(int section, int level){
        preferences.putBoolean("N" + section + "." + level + "_desbloqueado", true);
        preferences.flush();
    }
    public static void unlockNextLevel() {
        if (getCurrentLevel() >= dimensions.get(getCurrentSection()) - 1){
            unlockLevel(getCurrentSection() + 1, 0);
        } else {
            unlockLevel(getCurrentSection(), getCurrentLevel() + 1);
        }
    }
    static private void lockLevel(int section, int level) {
        preferences.putBoolean("N" + section + "." + level + "_desbloqueado", false);
        preferences.flush();
    }

    public static boolean levelIsUnlocked(int section, int level){
        return preferences.getBoolean("N" + section + "." + level + "_desbloqueado", false);
    }

    public static void unlockAll(){
        List<Integer> dim = new ArrayList<Integer>();
        LevelsLoader.getDimensions(dim);
        for (int i = 0; i < dim.size(); i++){
            for (int j = 0; j < dim.get(i); j++) {
                unlockLevel(i,j);
            }
        }
        everythingUnlocked = true;
    }

    public static void lockAll(){
        List<Integer> dim = new ArrayList<Integer>();
        LevelsLoader.getDimensions(dim);
        for (int i = 0; i < dim.size(); i++){
            for (int j = 0; j < dim.get(i); j++)
                lockLevel(i, j);
        }
        unlockLevel(0,0);
        everythingUnlocked = false;
    }

    public static boolean isLastLevel() {
        return getCurrentSection() == dimensions.size() - 1
                &&  getCurrentLevel() == dimensions.get(getCurrentSection()) - 1;
    }

    public static void setNewRecord(boolean isNewRecord) {
        preferences.putBoolean(newRecord, isNewRecord);
        preferences.flush();
    }

    public static boolean getNewRecord() {
        return preferences.getBoolean(newRecord, false);
    }

    static private void setLevelTime(int section, int level, float time) {
        if (getExtraState()) {
            setExtraLevelTime(section, level, time);
        }
        else {
            setNormalLevelTime(section, level, time);
        }
    }

    static private void setNormalLevelTime(int section, int level, float time) {
        preferences.putFloat("N" + section + "." + level + "_tiempo", time);
        preferences.flush();
    }

    static private void setExtraLevelTime(int section, int level, float time) {
        preferences.putFloat("NX" + section + "." + level + "_tiempo", time);
        preferences.flush();
    }

    public static float getLevelTime(int section, int level) {
        if (getExtraState()) {
            return getExtraLevelTime(section, level);
        }
        else {
            return getNormalLevelTime(section, level);
        }
    }

    public static float getNormalLevelTime(int section, int level) {
        return preferences.getFloat("N" + section + "." + level + "_tiempo", -1);
    }

    public static float getExtraLevelTime(int section, int level) {
        return preferences.getFloat("NX" + section + "." + level + "_tiempo", -1);
    }

    public static void setCurrentLevelTime(float time) {
        int section = getCurrentSection();
        int level = getCurrentLevel();
        setLevelTime(section, level, time);
    }

    public static void setCompletedTime(float time) {
        preferences.putFloat(completedTime, time);
        preferences.flush();
    }

    public static float setCompletedTime() {
        return preferences.getFloat(completedTime);
    }

    public static float getCurrentLevelTime() {
        int section = getCurrentSection();
        int level = getCurrentLevel();
        return getLevelTime(section, level);
    }

    public static Level getLevel(int sectionIndex, int levelIndex,
                          PlanetsManager.HidingTreatsListener planetasHidingTreatsListener,
                          TreatsManager.HidingTreatsListener dulcesHidingTreatsListener,
                          Planet3.DamageHandler damageHandler,
                          MainGame game) {
        if (getExtraState()) {
            return LevelsLoader.getExtraLevel(sectionIndex, levelIndex,
                    planetasHidingTreatsListener, dulcesHidingTreatsListener, damageHandler, game);
        }
        else{
            return LevelsLoader.getNormalLevel(sectionIndex, levelIndex,
                    planetasHidingTreatsListener, dulcesHidingTreatsListener, damageHandler, game);
        }
    }

    static private boolean calculateUnlockedSection(int sectionIndex){
        for (int i = 0; i < dimensions.get(sectionIndex); i++) {
            if (getLevelTime(sectionIndex, i) == -1)
                return false;
        }
        setUnlockedSection(sectionIndex, true);
        return true;
    }

    static private void setUnlockedSection(int section, boolean unlocked) {
        preferences.putBoolean("S" + section  + "_desbloqueada", unlocked);
        preferences.flush();
    }

    public static boolean getUnlockedSection(int sectionIndex) {
        return preferences.getBoolean("S" + sectionIndex + "_desbloqueada", false)
                || calculateUnlockedSection(sectionIndex);
    }

    public static boolean getAlreadyUnlockedSection(int sectionIndex) {
        return preferences.getBoolean("S" + sectionIndex + "_desbloqueada", false);
    }

    static private boolean isLastLevelOfSection(int sectionIndex, int levelIndex) {
        return levelIndex >= dimensions.get(sectionIndex) - 1;
    }

    public static boolean isCurrentSectionLast() {
        int sectionIndex = getCurrentSection();
        return isLastSection(sectionIndex);
    }

    static private boolean isLastSection(int sectionIndex) {
        return sectionIndex >= dimensions.size() - 1;
    }

    static private boolean isLastExtraLevelOfSection(int sectionIndex, int levelIndex) {
        return levelIndex >= dimensionsExtra.get(sectionIndex) - 1;
    }

    public static void setNextLevel() {
        int sectionIndex = getCurrentSection();
        int levelIndex = getCurrentLevel();
        if (getExtraState()) {
            if (!isLastSection(sectionIndex)) {
                setExtraState(false);
                setCurrentSection(sectionIndex + 1);
                setCurrentLevel(0);
            }
        }
        else {
            if (isLastLevelOfSection(sectionIndex, levelIndex)) {
                if (!isLastSection(sectionIndex)) {
                    setCurrentSection(sectionIndex + 1);
                    setCurrentLevel(0);
                }
            } else {
                setCurrentLevel(levelIndex + 1);
            }
        }

    }

    public static int getNextExtraLevelNumber() {
        if (getExtraState()) {
            return getExtraLevelNumber() + 1;
        }
        else {
            int levelToShow = 0;
            for (int i = 0; i < getCurrentSection(); i++) {
                levelToShow += dimensionsExtra.get(i);
            }
            return levelToShow + 1;
        }
    }

    public static void setNormalLevel(int sectionIndex, int levelIndex) {
        setExtraState(false);
        setCurrentLevel(levelIndex);
        setCurrentSection(sectionIndex);
    }

    public static void setExtraLevel(int sectionIndex, int levelIndex) {
        setExtraState(true);
        setCurrentLevel(levelIndex);
        setCurrentSection(sectionIndex);
    }

    public static boolean isExtraLevelNext() {
        int sectionIndex = getCurrentSection();
        int levelIndex = getCurrentLevel();
        return getUnlockedSection(sectionIndex)
                && isLastLevelOfSection(sectionIndex, levelIndex) ||
                getExtraState() && !isLastExtraLevelOfSection(sectionIndex, levelIndex);
    }

    public static void setNextExtraLevel() {
        int sectionIndex = getCurrentSection();
        int levelIndex = getCurrentLevel();
        if (getExtraState()) {
            if (!isLastExtraLevelOfSection(sectionIndex, levelIndex)) {
                setExtraLevel(sectionIndex, levelIndex + 1);
            }
        }
        else {
            setExtraLevel(sectionIndex, 0);
        }
    }

    static private void setCompletedSection(int sectionIndex, boolean completed) {
        System.out.println("writing section " + sectionIndex + " as completed." + completed);
        preferences.putBoolean("S" + sectionIndex + "_completed", completed);
        preferences.flush();
    }

    static private boolean getCompletedSection(int sectionIndex) {
        return preferences.getBoolean("S" + sectionIndex + "_completed", false) ||
                calculateCompletedSection(sectionIndex);
    }

    public static boolean getAlreadyCompletedSection(int sectionIndex) {
        return preferences.getBoolean("S" + sectionIndex + "_completed", false);
    }

    static private boolean calculateCompletedSection(int sectionIndex) {
        for (int i = 0; i < dimensionsExtra.get(sectionIndex); i++) {
            if (getExtraLevelTime(sectionIndex, i) == -1)
                return false;
        }
        setCompletedSection(sectionIndex, true);
        return true;
    }

    private static boolean justCompletedSection(int sectionIndex) {
        if (!getExtraState() || getAlreadyCompletedSection(sectionIndex)) {
            return false;
        }
        else {
            return calculateCompletedSection(sectionIndex);
        }
    }

    public static boolean justCompletedCurrentSection() {
        int sectionIndex = getCurrentSection();
        return justCompletedSection(sectionIndex);
    }

    static boolean justUnlockedSection(int sectionIndex) {
        if (getExtraState() || getAlreadyUnlockedSection(sectionIndex)) {
            return false;
        } else {
            return calculateUnlockedSection(sectionIndex);
        }
    }

    public static boolean justUnlockedCurrentSection() {
        int sectionIndex = getCurrentSection();
        return justUnlockedSection(sectionIndex);
    }

}
