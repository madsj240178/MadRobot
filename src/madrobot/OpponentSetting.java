/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package madrobot;

/**
 *
 * @author sjaeger
 */
public class OpponentSetting {
    private String opponentName;
    private double opponentHeading;

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public double getOpponentHeading() {
        return opponentHeading;
    }

    public void setOpponentHeading(double opponentHeading) {
        this.opponentHeading = opponentHeading;
    }

    public OpponentSetting(String opponentName, double opponentHeading, double opponentVelocity) {
        this.opponentName = opponentName;
        this.opponentHeading = opponentHeading;
        this.opponentVelocity = opponentVelocity;
    }

    public double getOpponentVelocity() {
        return opponentVelocity;
    }

    public void setOpponentVelocity(double opponentVelocity) {
        this.opponentVelocity = opponentVelocity;
    }
    private double opponentVelocity;
}
