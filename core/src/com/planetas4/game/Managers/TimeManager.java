package com.planetas4.game.Managers;

/*
*
*   Practically a chronometer 
*
* */

public class TimeManager {

    private float elapsedTime; // elapsed time that can be altered (be forwarded)
    private float realElapsedTime; // actual elapsed time it can only be altered when restarted
    private float limitTime;

    public TimeManager(float limitTime){
        this.elapsedTime = 0.0f;
        this.limitTime = limitTime;
    }

    public float remainingTimePercentage(){
        return 1 - elapsedTime / limitTime;
    }

    public float remainingTimeInSeconds(){return limitTime - elapsedTime;}

    public float elapsedTimePercentage(){
        return elapsedTime / limitTime;
    }

    public float elapsedTimeInSeconds(){
        return elapsedTime;
    }

    public float realElapsedTimeInSeconds(){
        return realElapsedTime;
    }

    public void act(float delta){
        elapsedTime += delta;
        realElapsedTime += delta;
    }

    public void goForward(float time){
        elapsedTime = (elapsedTime + time > 0? elapsedTime + time : 0); // elapsed time cannot be less than zero
    }

    public void restart(){ realElapsedTime = elapsedTime = 0.0f; }

}