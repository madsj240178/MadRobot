/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package madrobot;

import java.awt.geom.Point2D;

/**
 *
 * @author sjaeger
 */
public class OpponentSetting {
    private String opponentName;
    private double opponentHeading;
    private Point2D.Double opponentPosition;

    public Point2D.Double getOpponentPosition() {
        return opponentPosition;
    }

    public void setOpponentPosition(Point2D.Double opponentPosition) {
        this.opponentPosition = opponentPosition;
    }

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

    public OpponentSetting(String opponentName, double opponentHeading, double opponentVelocity, Point2D.Double opponentPosition) {
        this.opponentName = opponentName;
        this.opponentHeading = opponentHeading;
        this.opponentVelocity = opponentVelocity;
        this.opponentPosition = opponentPosition;
    }

    public double getOpponentVelocity() {
        return opponentVelocity;
    }

    public void setOpponentVelocity(double opponentVelocity) {
        this.opponentVelocity = opponentVelocity;
    }
    private double opponentVelocity;
}
