package com.planetas4.game.Components;

import java.util.List;

/*
*
* Component that stores and manages the data of each level: the challenges, current challenge,
 *   the remaining lives, etc..
*
*   The total time (completedTime) of a level is calculated adding the time of all the challenges that were
*   successfully finished, failed challenges tries don't have an effect on the final time
*
*   E.g. A level has three challenges and...
*           - You succeed on your first try of the first challenge       -> 10 seconds  ✓
*           - You fail on your first try of the second challenge         -> 5 seconds
*           - You succeed on your second try of the second challenge     -> 10 seconds  ✓
*           - You fail on your first try of the third challenge          -> 5 seconds
*           - You fail on your second try of the third challenge         -> 5 seconds
*           - You succeed on third second try of the third challenge     -> 10 seconds  ✓
*
*                                                       TOTAL TIME OF GAME: 30 seconds
*
* */

public class Level {

    private List<Challenge> challenges;
    private final int lives;
    private int remainingLives;
    private int currentChallengeIndex;
    private Advice advice;
    private float completedTime;

    public Level(List<Challenge> challenges, int lives){
        this.challenges = challenges;
        this.lives = lives;
        remainingLives = this.lives;
        currentChallengeIndex = -1;
        advice = null;
        completedTime = 0.0f;
    }

    public void setAdvice(Advice advice) {
        this.advice = advice;
    }

    public Advice getAdvice() {
        return advice;
    }

    public boolean challengesSurpassed(){
        return currentChallengeIndex == challenges.size()
                || currentChallengeIndex == -1;
    }

    // function that updates the current challenge to the next (it assumes that the current one
         // was successfully finished)
    public void nextChallenge() {
        if (currentChallengeIndex != -1) {
            // updating the total time (completedTime)
            completedTime += challenges.get(currentChallengeIndex).getTimeManager().realElapsedTimeInSeconds();
        }
        currentChallengeIndex = (currentChallengeIndex + 1) % (challenges.size() + 1);
    }

    public void restart(){
        currentChallengeIndex = -1;
        remainingLives = lives;
        completedTime = 0.0f;
    }

    public int getRemainingLives() {
        return remainingLives;
    }

    public void losesOneLife(){
        remainingLives--;
    }

    public Challenge getCurrentChallenge(){
        if (currentChallengeIndex < 0 || challenges.size() == 0)
            return null;
        return challenges.get(currentChallengeIndex);
    }

    public int getCurrentChallengeIndex() {
        return currentChallengeIndex;
    }

    public float getCompletedTime(){ return completedTime; }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void dispose() {
        for (Challenge challenge : challenges) {
            challenge.dispose();
        }
      //  challenges.clear();
    }
}
