/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package madrobot;

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
}
