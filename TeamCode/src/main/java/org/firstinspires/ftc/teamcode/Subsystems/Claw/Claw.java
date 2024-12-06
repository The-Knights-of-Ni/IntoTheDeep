package org.firstinspires.ftc.teamcode.Subsystems.Claw;

import com.qualcomm.robotcore.hardware.Servo;

public class Claw {
    public final Servo claw; //The servo that controls the claw
    public Claw(Servo clawMotor) {
        this.claw = clawMotor;
    }

    public void moveClaw(ClawPosition newPosition) {
        claw.setPosition(newPosition.pos);
    }

    public void moveClawSync(ClawPosition newPosition) {
        moveClaw(newPosition);
        // Angles are all in degrees
        while (Math.abs(claw.getPosition() - newPosition.pos) > 20) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Begins the process of opening the claw.
     * Does not wait for the claw action to finish opening before terminating the method and
     * allowing other functions to begin.
     */
    public void openClaw() {
        moveClaw(ClawPosition.OPEN);
    }

    /**
     * Begins the process of closing the claw.
     * Does not wait for the claw action to finish opening before terminating the method and
     * allowing other functions to begin.
     */
    public void closeClaw() {
        moveClaw(ClawPosition.CLOSE);
    }

    /**
     * Opens the claw fully.
     * The method will not terminate until the claw is fully open, meaning that only the action
     * of the claw opening can be occurring at the given time.
     */
    public void openClawSync() {
        moveClawSync(ClawPosition.OPEN);
    }

    /**
     * Closes the claw fully.
     * The method will not terminate until the claw is fully closed, meaning that only the action
     * of the claw closing can be occurring at the given time.
     */
    public void closeClawSync() {
        moveClawSync(ClawPosition.CLOSE);
    }
}
