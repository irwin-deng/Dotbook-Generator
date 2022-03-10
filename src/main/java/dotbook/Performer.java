/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dotbook;

/**
 *
 * @author Irwin
 */
public class Performer {

    public String id;
    public Set[] sets;

    public Performer(String id, Set[] sets) {
        this.id = id;
        this.sets = sets;
        assignTitles();
        calcActions();
        calcDirections();
    }

    public void calcActions() {
        for (int i = 1; i < sets.length; i++) {
            if (sets[i].counts != 0) {
                sets[i].setAction(!sets[i].positionEquals(sets[i - 1]));
            }
        }
    }

    public int getSetIndx(int set, char subset) {
        for (int i = 0; i < sets.length; i++) {
            if (sets[i].num == set && sets[i].sub == subset) {
                return i;
            }
        }
        return -1;
    }

    public void assignTitles() {
        for (int i = 0; i < sets.length; i++) {
            if (i < 23) {
                sets[i].setTitle("Segment 1");
            } else if (i < 35) {
                sets[i].setTitle("Segment 2");
            } else if (i < 44) {
                sets[i].setTitle("Segment 3");
            } else {
                sets[i].setTitle("Segment 4");
            }
        }
    }

    public void calcDirections() {
        for (int i = 1; i < sets.length; i++) {
            if (sets[i].move) {
                double slope = (sets[i].coords.vCoord - sets[i - 1].coords.vCoord) / (sets[i].coords.hCoord - sets[i - 1].coords.hCoord);
                sets[i].prevSlope = slope;

                if (sets[i].coords.hCoord == sets[i - 1].coords.hCoord) {
                    if (sets[i].coords.vCoord > sets[i - 1].coords.vCoord) {
                        sets[i].prevDirection = 90;
                        sets[i - 1].nextDirection = 90;
                    } else {
                        sets[i].prevDirection = -90;
                        sets[i - 1].nextDirection = -90;
                    }
                } else {
                    sets[i].prevDirection = Math.toDegrees(Math.atan(slope));
                    sets[i - 1].nextDirection = Math.toDegrees(Math.atan(slope));
                    if (sets[i].coords.hCoord < sets[i-1].coords.hCoord) {
                        sets[i].prevDirection += 180;
                        sets[i - 1].nextDirection += 180;
                    }
                }
            } else {
                sets[i].prevSlope = -1;
                sets[i - 1].nextSlope = -1;
                sets[i].prevDirection = -1;
                sets[i - 1].nextDirection = -1;
            }
        }
    }
}
