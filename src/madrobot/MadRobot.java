package madrobot;

import java.awt.Color;
import robocode.*;

/**
 *
 * @author sjaeger
 */
public class MadRobot extends Robot {

    double battleFieldWidth = 1000;
    double battleFieldHeight = 1000;
    double opponentDistance = 1000;
    double opponentBearing = 0;
    double opponentVelocity = 10;
    double opponentEnergy = 100;
    int xTicks = 0;
    int yTicks = 0;
    double speed = 30;
    int tickRobotScanned = 0;        
    
    @Override
    public void run() {
        this.setBodyColor(Color.RED);
        this.battleFieldWidth = getBattleFieldWidth();
        this.battleFieldHeight = getBattleFieldHeight();
            
        while (true) {
            this.speed = 100 * Math.random();
            double x = this.getX();
            double y = this.getY();

            double r = 80 + Math.random()*20;
            if (isXBorderClose(x, this.speed)) {
                turnRight(r);
                this.ahead(this.speed);
            } else
            if (isYBorderClose(y, this.speed)) {
                turnRight(r);
                ahead(this.speed);
            } else {
                ahead(this.speed);
            }
            
            if(this.xTicks > 5 && Math.random()>0.5) {
                turnRight(10);
            }
            if(Math.random()>0.9 || this.tickRobotScanned < (getRoundNum()-200)) {
                turnGunRight(20);
                //this.speed = 100;
                //turnRadarRight(20);
            }
            
            this.xTicks++;
            if(getTime() / 20 == Math.abs(getTime() / 20)) {
                //scan();
            }
        }
    }

    @Override
    public void onDeath(DeathEvent event) {
    }

    @Override
    public void onHitByBullet(HitByBulletEvent event) {
    }

    @Override
    public void onHitRobot(HitRobotEvent event) {
    }

    @Override
    public void onHitWall(HitWallEvent event) {
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent event) {
        this.opponentDistance = event.getDistance();
        this.opponentVelocity = event.getVelocity();
        this.opponentEnergy = event.getEnergy();
        this.opponentBearing = event.getBearing();
        
        //this.turnGunRight(5);
        
        //this.turnRadarRight(this.opponentBearing);
        if(this.opponentDistance < 50) {
            fire(10);
        } else if(this.opponentDistance < 150) {
            fire(5);
        } else {
            fire(1);
        }
        this.tickRobotScanned = (int) this.getTime();
    }

    @Override
    public void onWin(WinEvent event) {
    }

    @Override
    public void setBodyColor(Color color) {
        super.setBodyColor(Color.RED);
    }

    @Override
    public void setRadarColor(Color color) {
        super.setRadarColor(Color.GRAY);
    }

    @Override
    public void setBulletColor(Color color) {
        super.setBulletColor(Color.RED);
    }

    @Override
    public void setScanColor(Color color) {
        super.setScanColor(Color.lightGray);
    }

    private boolean isXBorderClose(double x, double speed) {
        boolean right = this.xTicks > 5 && (x + (20 * speed)) >= this.battleFieldWidth;
        boolean left = this.xTicks > 5 && (x - (20 * speed)) <= 0;
        if(right || left) this.xTicks = 0;
                
        return left || right; 
    }
    
    private boolean isYBorderClose(double y, double speed) {
        boolean right = this.xTicks > 5 && (y + (20 * speed)) >= this.battleFieldHeight;
        boolean left = this.xTicks > 5 && (y - (20 * speed)) <= 0;
        if(right || left) this.xTicks = 0;
                
        return left || right; 
    }
}
