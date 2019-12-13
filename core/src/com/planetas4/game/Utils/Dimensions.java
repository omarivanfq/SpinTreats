package com.planetas4.game.Utils;

import com.badlogic.gdx.math.Vector2;
import com.planetas4.game.Constants.Values;

public class Dimensions {

    public static float limiteInferior = Values.SCREEN_HEIGHT * Values.CHALLENGE_BAR_SPACE
            * Values.PAUSE_BAR_VERTICAL_SPACE;
    public static float limiteSuperior = Values.SCREEN_HEIGHT;
    public static float limiteDerecho = Values.SCREEN_WIDTH;
    public static float limiteIzquierdo = 0;

    public static float juego_PorcentajeAPorcentaje_X(float x){
        return x;
    }

    public static float juego_PorcentajeAPorcentaje_Y(float y){
        return Values.PAUSE_BAR_VERTICAL_SPACE * Values.CHALLENGE_BAR_SPACE
                + y * (1 - Values.PAUSE_BAR_VERTICAL_SPACE * Values.CHALLENGE_BAR_SPACE);
    }

    public static float pantalla_PorcentajeAReal_X(float x){
        return x * Values.SCREEN_WIDTH;
    }
    public static float pantalla_RealAPorcentaje_X(float x){
        return x / Values.SCREEN_WIDTH;
    }
    public static float juego_PorcentajeAReal_X(float x){
        return pantalla_PorcentajeAReal_X(x);
    }

    public static float juego_RealAPorcentaje_X(float x){
        return pantalla_RealAPorcentaje_X(x);
    }

    public static float pantalla_PorcentajeAReal_Y(float y){return y * Values.SCREEN_HEIGHT;}
    public static float pantalla_RealAPorcentaje_Y(float y){
        return y / Values.SCREEN_HEIGHT;
    }
    public static float juego_PorcentajeAReal_Y(float y){
        return Values.SCREEN_HEIGHT * Values.PAUSE_BAR_VERTICAL_SPACE * Values.CHALLENGE_BAR_SPACE +
                Values.SCREEN_HEIGHT * (1 - Values.PAUSE_BAR_VERTICAL_SPACE * Values.CHALLENGE_BAR_SPACE) * y;
    }
    public static float juego_RealAPorcentaje_Y(float y){
        return (y - Values.SCREEN_HEIGHT * Values.PAUSE_BAR_VERTICAL_SPACE * Values.CHALLENGE_BAR_SPACE)
                / (Values.SCREEN_HEIGHT * (1 - Values.PAUSE_BAR_VERTICAL_SPACE * Values.CHALLENGE_BAR_SPACE));
    }

    public static Vector2 pantalla_PorcentajeAReal(float x, float y){
        return new Vector2(pantalla_PorcentajeAReal_X(x), pantalla_PorcentajeAReal_Y(y));
    }
    public static Vector2 pantalla_RealAPorcentaje(float x, float y){
        return new Vector2(pantalla_RealAPorcentaje_X(x), pantalla_RealAPorcentaje_Y(y));
    }
    public static Vector2 juego_PorcentajeAReal(float x, float y){
        return new Vector2(juego_PorcentajeAReal_X(x), juego_PorcentajeAReal_Y(y));
    }
    public static Vector2 juego_RealAPorcentaje(float x, float y){
        return new Vector2(juego_RealAPorcentaje_X(x), juego_RealAPorcentaje_Y(y));
    }

}
