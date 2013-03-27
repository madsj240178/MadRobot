/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package madrobot;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

/**
 *
 * @author sjaeger
 */
public class ShootEmUp extends AdvancedRobot {

    public static ShootEmUp singleton;
    public static OpponentSettings opponentSettings = new OpponentSettings();

    private static ShootSetting getSimilarSetting(ShootSetting set) {
        for (Map.Entry<Integer, ShootSetting> entry : settings.entrySet()) {
            Integer bulletHashCode = entry.getKey();
            ShootSetting shootSetting = entry.getValue();
            if (shootSetting.isSimilarTo(set)) {
                singleton.out.println(shootSetting);
                return shootSetting;
            }
        }
        return null;
    }

    private void writeShootSettingsToFile() {
        String output = "------------ Runde " + this.getRoundNum() + " ------------\n";
        for (Map.Entry<Integer, ShootSetting> entry : settings.entrySet()) {
            Integer integer = entry.getKey();
            ShootSetting shootSetting = entry.getValue();
            output += shootSetting.toString();
        }

        File f = this.getDataFile("shootsettings" + getRoundNum() + ".txt");
        RobocodeFileOutputStream fos;
        try {
            fos = new RobocodeFileOutputStream(f);
            fos.write(output.getBytes());
            fos.flush();
            fos.close();
        } catch (Exception ex) {
            Logger.getLogger(ShootEmUp.class.getName()).log(Level.SEVERE, null, ex);
        }


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
    static int cSpeed = 80;
    static int cGunRotate = 0;
    private boolean isOpponentWallRobot = false;
    static Hashtable<Integer, ShootSetting> settings = new Hashtable<Integer, ShootSetting>();

    @Override
    public void run() {
        singleton = this;
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
                if (isOpponentWallRobot) {
                    Bullet b = setFireBullet(Rules.MAX_BULLET_POWER);
                    execute();
                    setAhead(-getBattleFieldHeight() + getY() + 30);
                    waitFor(new MoveCompleteCondition(this));

                } else {
                    this.speed = ShootEmUp.cSpeed * Math.random();
                    double x = this.getX();
                    double y = this.getY();
                    this.setMaxVelocity(8);

                    double r = 80 + Math.random() * 20;
                    if (isGettingCloseToBorder()) {
                        this.setMaxVelocity(5);
                        this.setTurnRight(r);
                        this.setAhead(this.speed);
                        execute();
                    } else {
                        this.setMaxVelocity(8);
                        r = 0;
                        this.setAhead(this.speed);
                        if (Math.random() > 0.9) {
                            r = 20;
                        } else if (Math.random() > 0.8) {
                            r += -40;
                        }
                        this.setTurnRight(r);
                        double turnGun = 0;
                        if (this.tickRobotScanned < (getTime() - 40)) {
                            turnGun = 170;
                        } else {
                            turnGun = normalRelativeAngleDegrees(opponentBearing);
                        }
                        this.setAdjustGunForRobotTurn(true);
                        this.setTurnGunLeft(turnGun);
                        execute();
                        this.setAdjustGunForRobotTurn(true);
                    }
                    execute();
                }
            }
        } catch (Exception e) {
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
        if (!isOpponentWallRobot) {
            double bearingBullet = event.getBearing();
            this.setTurnGunRight(normalRelativeAngleDegrees(bearingBullet));
        }
    }

    @Override
    public void onBulletHit(BulletHitEvent event) {
        this.missedBulletsinARow = 0;
        if (event.getBullet() != null) ShootEmUp.settings.get(event.getBullet().hashCode()).increaseHit();
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
        if (event.getBullet() != null) ShootEmUp.settings.get(event.getBullet().hashCode()).increaseMiss();
        out.println("missedBulletsinARow: " + this.missedBulletsinARow);

        if (this.missedBulletsinARow > 20) {
            // Setting
            this.missedBulletsinARow = 0;
            if (Math.random() >= 0.5) {
                //ShootEmUp.cSpeed = (int) Math.round(80 * Math.random());
                //ShootEmUp.cGunRotate = (int) Math.round(5*Math.random()) - ShootEmUp.cGunRotate;
            } else {
                //ShootEmUp.cSpeed = (int) Math.round(40 * Math.random());
                //ShootEmUp.cGunRotate = (int) ((-1) * Math.round(5*Math.random())) - ShootEmUp.cGunRotate;
            }

        }
    }

    private Point2D.Double getOpponentPostion() {
        Point2D.Double p = new Point2D.Double();
        double angle = Math.toRadians((getHeading() + opponentBearing) % 360);

        // Calculate the coordinates of the robot
        double x = (getX() + Math.sin(angle) * opponentDistance);
        double y = (getY() + Math.cos(angle) * opponentDistance);
        p.setLocation(x, y);
        return p;
    }

    private static ScannedRobotEvent currentEvent = null;

    private static boolean isStillCurrentScan(ScannedRobotEvent event) {
        if (ShootEmUp.currentEvent == null || ShootEmUp.currentEvent.hashCode() == event.hashCode()) {
            return true;
        } else {
            ShootEmUp.currentEvent = event;
            return false;
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        this.opponentDistance = event.getDistance();
        this.opponentVelocity = event.getVelocity();
        this.opponentEnergy = event.getEnergy();
        this.opponentBearing = event.getBearing();
        this.opponentHeading = event.getHeading();
        this.tickRobotScanned = (int) this.getTime();

        Point2D.Double opponentPosition = getOpponentPostion();
        OpponentSetting oSetting = new OpponentSetting(event.getName(), opponentHeading, opponentVelocity, opponentPosition);
        ShootEmUp.opponentSettings.add(oSetting);
        if(getOthers() == 1 && this.isOpponentWallRobot) {
            return;
        }
        if (getOthers() == 1 && isWallRobot(event.getName())) {
            this.setAdjustGunForRobotTurn(true);
            
            this.isOpponentWallRobot = true;
            setTurnRight(normalRelativeAngleDegrees(90 - getHeading()));
            waitFor(new TurnCompleteCondition(this));
            setAhead(getBattleFieldWidth() - getX() - 16);
            waitFor(new MoveCompleteCondition(this));
            setTurnGunRight(normalRelativeAngleDegrees(360 - getGunHeading()));
            waitFor(new GunTurnCompleteCondition(this));
            setTurnRight(normalRelativeAngleDegrees(360 - getHeading()));
            waitFor(new TurnCompleteCondition(this));
            this.setAdjustGunForRobotTurn(false);
            
            return;
        } else {
            this.isOpponentWallRobot = false;
            double f = Math.round(Rules.MAX_BULLET_POWER * Math.random());

            int m1 = 1;
            if (Math.random() >= 0.5) {
                m1 = -1;
            }

            ShootSetting set = new ShootSetting(null, opponentBearing, opponentDistance, (m1 * ShootEmUp.cGunRotate), f);
            ShootSetting similarSet = ShootEmUp.getSimilarSetting(set);
            if (similarSet != null) {
                if (similarSet.getHitProbability() < 0.1) {
                    onScannedRobot(event);
                    return;
                }
            }

            this.setAdjustGunForRobotTurn(true);
            this.setTurnGunRight(m1 * ShootEmUp.cGunRotate);
            execute();
            this.setAdjustGunForRobotTurn(false);


            if (getEnergy() > 5) {
                Bullet b = this.setFireBullet(f);
                set.bullet = b;
                if (b != null) {
                    if (similarSet == null) {
                        ShootEmUp.settings.put(b.hashCode(), set);
                    } else {
                        ShootEmUp.settings.put(b.hashCode(), similarSet);
                    }
                }
            }
            execute();
        }
    }

    private boolean isWallRobot(String opponentName) {
        Vector<OpponentSetting> v = ShootEmUp.opponentSettings.getSettingsByName(opponentName);
        out.println("isWallRobot:" + v.toArray().toString());
        int i = 0;
        int calcSeemsToBe = 0;
        if (v != null) {
            for (Iterator<OpponentSetting> it = v.iterator(); it.hasNext();) {
                OpponentSetting opponentSetting = it.next();
                i++;
                if (v.size() - 10 > i) {
                    continue;
                }
                Point2D.Double p = opponentSetting.getOpponentPosition();
                if (p.getX() < 36 || p.getX() > getBattleFieldWidth() - 36) {
                    out.println(p);
                    calcSeemsToBe++;
                } else if (p.getY() < 36 || p.getY() > getBattleFieldHeight() - 36) {
                    calcSeemsToBe++;
                    out.println(p);
                }
            }
        }
        if (calcSeemsToBe > 8) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onRoundEnded(RoundEndedEvent event) {
        writeShootSettingsToFile();
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
        this.opponentBearing = opponentBearing;
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
        if ((this.hitCount + this.missCount) < 5) {
            return 1;
        }
        return (this.hitCount / (this.hitCount + this.missCount));
    }

    public boolean isSimilarTo(ShootSetting settingToCompare) {
        if (this.firePower == settingToCompare.firePower) {
            if (this.opponentBearing * 0.4 < settingToCompare.opponentBearing && this.opponentBearing * 1.6 > settingToCompare.opponentBearing) {
                if (this.opponentDistance * 0.4 < settingToCompare.opponentDistance && this.opponentDistance * 1.6 > settingToCompare.opponentDistance) {
                    if (this.turnGunBeforeFire * 0.4 < settingToCompare.turnGunBeforeFire && this.turnGunBeforeFire * 1.6 > settingToCompare.turnGunBeforeFire) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ShootSetting{" + this.getHitProbability() + " shootAmount" + (this.hitCount + this.missCount) + "}\n";
    }
}