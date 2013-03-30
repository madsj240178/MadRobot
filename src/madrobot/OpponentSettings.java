/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package madrobot;

import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author sjaeger
 */
public class OpponentSettings {
    private Hashtable<String, Vector<OpponentSetting>> settingByName = new Hashtable<String, Vector<OpponentSetting>>();

    public OpponentSettings() {
        
    }
    
    public void add(OpponentSetting o)
    {
        Vector<OpponentSetting> v = new Vector<OpponentSetting>();
        if(settingByName.get(o.getOpponentName()) == null) {
            v = new Vector<OpponentSetting>();
        } else {
            v = settingByName.get(o.getOpponentName());
        }
        v.add(o);
        settingByName.put(o.getOpponentName(), v);
    }
    
    public Vector<OpponentSetting> getSettingsByName(String name) {
        return settingByName.get(name);
    }
    
    public Point2D.Double getPredictedPosition(String name, long amountRounds) {
        Vector<OpponentSetting> oV = this.getSettingsByName(name);
        if(oV.size() < 2) return null;
        Point2D.Double currentP = oV.get(oV.size()-1).getOpponentPosition();
        Point2D.Double previousP = oV.get(oV.size()-2).getOpponentPosition();
        double diffX = currentP.getX() - previousP.getX();
        double diffY = currentP.getY() - previousP.getY();
        long diffRounds = oV.get(oV.size()-1).getTurnNum() - oV.get(oV.size()-2).getTurnNum(); 
        double ratioRounds = amountRounds / diffRounds;
        Point2D.Double nextP = new Point2D.Double(currentP.getX() + ratioRounds * diffX, currentP.getY() + ratioRounds * diffY);
        ShootEmUp.singleton.out.println("diffX "  +diffX);
        ShootEmUp.singleton.out.println("ratioRounds "  +ratioRounds);
        ShootEmUp.singleton.out.println("diffRounds "  +diffRounds + " amountRounds " + amountRounds);
        ShootEmUp.singleton.out.println("nextP "  +nextP);
        
        return nextP;
    }
}
