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
public class Coordinates {

    public int side; //1 or 2
    public int yardLine; //nearest multiple of 5
    public double hAdj; //steps inside/outside (inside is +)
    public String hashLine; //which hash/side line
    public double vAdj; //steps behind/in front of hash/side line
    public double hCoord; //steps right of side 2 endline
    public double vCoord; //steps in front of back side line

    public Coordinates(int side, double hAdj, int yardLine, double vAdj, String hashLine) {
        this.side = side;
        this.hAdj = hAdj;
        this.yardLine = yardLine;
        this.vAdj = vAdj;
        this.hashLine = hashLine;
        
        calcHCoord();
        calcVCoord();
    }
    
    private void calcHCoord(){
        if(side == 1)
            hCoord = 160 - (yardLine * 8/5 + hAdj);
        else
            hCoord = yardLine * 8/5 + hAdj;
    }
    
    private void calcVCoord() {
        switch (hashLine) {
            case "front side line":
                vCoord = 84 + vAdj;
                break;
            case "front hash":
                vCoord = 56 + vAdj;
                break;
            case "back hash":
                vCoord = 28 + vAdj;
                break;
            case "back side line":
                vCoord = vAdj;
                break;
            default:
                System.out.println("INVALID HASH / SIDELINE" + hashLine);
                break;
        }
    }
}
