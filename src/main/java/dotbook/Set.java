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
public class Set {

    public int num;
    public char sub;
    public int startMeas;
    public int endMeas;
    public int counts;
    public Coordinates coords;
    public boolean move;
    public String title;
    public double prevDirection; //in degrees
    public double prevSlope; 
    public double nextDirection; //in degrees
    public double nextSlope;
    public String form;

    public Set(int num, char sub, int startMeas, int endMeas, int counts, Coordinates coords) {
        this.num = num;
        this.sub = sub;
        this.startMeas = startMeas;
        this.endMeas = endMeas;
        this.counts = counts;
        this.coords = coords;
        form = "";
    }
    
    public boolean positionEquals(Set s) {
        return coords.hCoord == s.coords.hCoord && coords.vCoord == s.coords.vCoord;
    }
    
    public void setAction(boolean move) {
        this.move = move;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void findForm() {
        
    }
}
