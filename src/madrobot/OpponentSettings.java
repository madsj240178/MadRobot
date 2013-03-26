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
    private Vector<OpponentSetting> settingList;

    public OpponentSettings() {
        settingList = new Vector<OpponentSetting>();
    }
    
    public void add(OpponentSetting o)
    {
        settingList.add(o);
    }
}
