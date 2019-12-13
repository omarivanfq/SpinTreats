package com.planetas4.game.Managers;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/*
*
*   Stores the game settings data on the device and provides and api to
*   retrieve this information
*
* */

public class SettingsManager {

    static private Preferences ajustes = Gdx.app.getPreferences("ajustes");

    public static float getMusicVolume(){
        return ajustes.getFloat("volume", 1);
    }
    public static void setMusicVolume(float volume){
        ajustes.putFloat("volume", volume);
        ajustes.flush();
    }

    // the name of the file that contains the current cat "skin"
    public static String getCatImageFileName() {
        return ajustes.getString("cat-file", "cats/cat-01.png");
    }

    public static void setCatImageFileName(String filename) {
        ajustes.putString("cat-file", filename);
        ajustes.flush();
    }

    public static boolean getSound(){
        return ajustes.getBoolean("sound", true);
    }

    public  static void setSound(boolean sound){
        ajustes.putBoolean("sound", sound);
        ajustes.flush();
    }

    public static boolean getVibration(){
        return ajustes.getBoolean("vibration", true);
    }

    public static void setVibration(boolean vibration){
        ajustes.putBoolean("vibration", vibration);
        ajustes.flush();
    }

}
