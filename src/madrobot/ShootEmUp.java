/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package madrobot;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Map;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 *
 * @author sjaeger
 */
public class ShootEmUp extends AdvancedRobot {

    private static ShootSetting getSimilarSetting(ShootSetting set) {
        for (Map.Entry<Integer, ShootSetting> entry : settings.entrySet()) {
            Integer bulletHashCode = entry.getKey();
            ShootSetting shootSetting = entry.getValue();
            if (shootSetting.isSimilarTo(set)) {
                return shootSetting;
            }
        }
        return null;
    }
    double battleFieldWidth = 1500;
    double battleFieldHeight = 1500;
    double opponentDistance = 1500;
    double opponentBearing = 0;
    double opponentVelocity = 10;
    double opponentEnergy = 100;
    double opponentHeading;
    double speed = 20;
    int tickRobotScanned = 0;
    double previousX = 0;
    double previousY = 0;
    int missedBulletsinARow = 0;
    static int cSpeed = 40;
    static int cGunRotate = 0;
    static Hashtable<Integer, ShootSetting> settings = new Hashtable<Integer, ShootSetting>();

    @Override
    public void run() {
        try {
        out.println("cSpeed Start: " + ShootEmUp.cSpeed);
        out.println("cGunRotate Start: " + ShootEmUp.cGunRotate);
        this.setBodyColor(Color.RED);
        this.setScanColor(Color.yellow);
        this.setRadarColor(Color.BLACK);
        this.setGunColor(Color.RED);
        this.battleFieldWidth = getBattleFieldWidth();
        this.battleFieldHeight = getBattleFieldHeight();
        while (true) {
            this.speed = ShootEmUp.cSpeed * Math.random();
            double x = this.getX();
            double y = this.getY();
            this.setMaxVelocity(5);

            double r = 80 + Math.random() * 20;
            if (isGettingCloseToBorder()) {

                this.setTurnRight(r);
                this.setAhead(this.speed);
                execute();
            } else {
                r = 0;
                this.setAhead(this.speed);
                if (Math.random() > 0.9) {
                    r = 20;
                } else if (Math.random() > 0.8) {
                    r += -40;
                }
                this.setTurnRight(r);
                if (this.tickRobotScanned < (getTime() - 30)) {
                    this.setAdjustGunForRobotTurn(true);
                    this.setTurnGunLeft(170);
                    execute();
                    this.setAdjustGunForRobotTurn(true);

                    //this.speed = 100;
                    //turnRadarRight(20);
                }
            }
            execute();
        }
        }
        catch(Exception e)
        {
            out.println(e.getMessage());
        }
    }

    private boolean isGettingCloseToBorder() {
        boolean isFarAwayFromXBorder = false;
        boolean isFarAwayFromYBorder = false;
        double rightXDistance = this.battleFieldWidth - getX();
        double leftXDistance = getX();
        double rightYDistance = this.battleFieldHeight - getY();
        double leftYDistance = getY();
        if (Math.min(rightXDistance, leftXDistance) > 100) {
            isFarAwayFromXBorder = true;
        } else {
            if (this.previousX < Math.min(rightXDistance, leftXDistance)) {
                isFarAwayFromXBorder = true;
            }
        }
        if (Math.min(rightYDistance, leftYDistance) > 100) {
            isFarAwayFromYBorder = true;
        } else {
            if (this.previousY < Math.min(rightYDistance, leftYDistance)) {
                isFarAwayFromYBorder = true;
            }
        }
        this.previousX = Math.min(rightXDistance, leftXDistance);
        this.previousY = Math.min(rightYDistance, leftYDistance);
        return !(isFarAwayFromXBorder && isFarAwayFromYBorder);
    }

    @Override
    public void onDeath(DeathEvent event) {
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        double bearingBullet = event.getBearing();
        this.setTurnGunRight(normalRelativeAngleDegrees(bearingBullet));
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        this.missedBulletsinARow = 0;
        ShootEmUp.settings.get(event.getBullet().hashCode()).increaseHit();
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
    }

    @Override
    public void onHitWall(HitWallEvent event) {
    }

    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        this.missedBulletsinARow++;
        ShootEmUp.settings.get(event.getBullet().hashCode()).increaseMiss();
        out.println("missedBulletsinARow: " + this.missedBulletsinARow);

        if (this.missedBulletsinARow > 10) {
            // Setting
            this.missedBulletsinARow = 0;
            if (Math.random() >= 0.5) {
                ShootEmUp.cSpeed = (int) Math.round(20 * Math.random());
                ShootEmUp.cGunRotate = (int) Math.round(10 * Math.random()) - ShootEmUp.cGunRotate;
            } else {
                ShootEmUp.cSpeed = (int) Math.round(10 * Math.random());
                ShootEmUp.cGunRotate = (int) ((-1) * Math.round(10 * Math.random())) - ShootEmUp.cGunRotate;
            }

        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        this.opponentDistance = event.getDistance();
        this.opponentVelocity = event.getVelocity();
        this.opponentEnergy = event.getEnergy();
        this.opponentBearing = event.getBearing();
        this.opponentHeading = event.getHeading();

        double f = Math.round(Rules.MAX_BULLET_POWER * Math.random());
        
        int m1 = 1;
        if (Math.random() >= 0.5) {
            m1 = -1;
        }
        
        ShootSetting set = new ShootSetting(null, opponentBearing, opponentDistance, (m1 * ShootEmUp.cGunRotate), f);
        ShootSetting similarSet = ShootEmUp.getSimilarSetting(set);
        if(similarSet != null) {
            if(similarSet.getHitProbability() < 0.4) {
                onScannedRobot(event);
                return;
            }
        }
        
        this.setAdjustGunForRobotTurn(true);
        this.setTurnGunRight(m1 * ShootEmUp.cGunRotate);
        execute();
        this.setAdjustGunForRobotTurn(false);

        
        Bullet b = this.setFireBullet(f);
        set.bullet = b;
        if(similarSet == null) {
            ShootEmUp.settings.put(b.hashCode(), set);
        }
        else {
            ShootEmUp.settings.put(b.hashCode(), similarSet);
        }
        execute();
        this.tickRobotScanned = (int) this.getTime();
    }

    @Override
    public void onWin(WinEvent event) {
        for (int i = 0; i < 30; i++) {
            if (i % 2 == 0) {
                this.setBodyColor(Color.BLACK);
            } else {
                this.setBodyColor(Color.GREEN);
            }
            this.turnGunLeft(360);
            this.turnGunRight(360);
            this.turnGunLeft(360);
            this.turnGunRight(360);
            this.turnGunLeft(360);
            this.turnGunRight(360);
        }
    }
}

class ShootSetting {

    public Bullet bullet;
    public double opponentDistance;
    public double opponentBearing;
    public double turnGunBeforeFire;
    public double firePower;
    private double hitCount = 1;
    private double missCount = 1;

    public ShootSetting(Bullet bullet, double opponentBearing, double opponentDistance, double turnGunBeforeFire, double firePower) {
        this.bullet = bullet;
        this.opponentDistance = opponentDistance;
        this.opponentBearing = opponentDistance;
        this.turnGunBeforeFire = turnGunBeforeFire;
        this.firePower = firePower;
    }

    public void increaseHit() {
        this.hitCount++;
    }

    public void increaseMiss() {
        this.missCount++;
    }

    public double getHitProbability() {
        if((this.hitCount + this.missCount) < 10) return 1;
        return (this.hitCount / (this.hitCount + this.missCount));
    }

    public boolean isSimilarTo(ShootSetting settingToCompare) {
        if (this.firePower == settingToCompare.firePower) {
            if (this.opponentBearing * 0.9 < settingToCompare.opponentBearing && this.opponentBearing * 1.1 > settingToCompare.opponentBearing) {
                if (this.opponentDistance * 0.9 < settingToCompare.opponentDistance && this.opponentDistance * 1.1 > settingToCompare.opponentDistance) {
                    if (this.turnGunBeforeFire * 0.9 < settingToCompare.turnGunBeforeFire && this.turnGunBeforeFire * 1.1 > settingToCompare.turnGunBeforeFire) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}