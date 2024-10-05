package org.firstinspires.ftc.teamcode.Subsystems.Control;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Subsystem;


/**
 * Control subsystem for controlling arms and claws
 */
public class Control extends Subsystem {
    public final Servo claw; //The servo that controls the claw
    public final DcMotorEx pivot; //The DcMotorEx that controls the pivot

    public Control(Telemetry telemetry, Servo clawMotor, DcMotorEx pivotMotor) {
        super(telemetry, "control");

        //Initializing instance variables
        this.claw = clawMotor;
        this.pivot = pivotMotor;
    }

    /**
     * Gets all defaults, directions,etc. ready for the autonomous period
     */
    public void initDevicesAuto() {
        pivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        claw.setDirection(Servo.Direction.FORWARD);
    }

    /**
     * Gets all defaults, directions,etc. ready for the teleop period
     */
    public void initDevicesTeleop() {
        pivot.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        pivot.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        claw.setDirection(Servo.Direction.FORWARD);
    }


    /**
     * Begins the process of opening the claw.
     * Does not wait for the claw action to finish opening before terminating the method and
     * allowing other functions to begin.
     */
    public void openClaw() {
        claw.setPosition(0);
    }

    /**
     * Begins the process of closing the claw.
     * Does not wait for the claw action to finish opening before terminating the method and
     * allowing other functions to begin.
     */
    public void closeClaw() {
        claw.setPosition(1);
    }

    /**
     * Opens the claw fully.
     * The method will not terminate until the claw is fully open, meaning that only the action
     * of the claw opening can be occurring at the given time.
     */
    public void openClawSync() {
        claw.setPosition(0);
        while (Math.abs(claw.getPosition() - 0) > 0.05) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*Thread.sleep() can throw an exception called an InterruptedException, which is
                thrown if another thread interrupts the current running thread.
                Java requires that this be caught.
                To catch it, we simply throw a runtime exception, which means out code need not
                worry about this exception any more.
                For more information, see the Javadocs on Thread.sleep()
                 */
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Closes the claw fully.
     * The method will not terminate until the claw is fully closed, meaning that only the action
     * of the claw closing can be occurring at the given time.
     */
    public void closeClawSync() {
        claw.setPosition(1);
        while (Math.abs(claw.getPosition() - 1) > 0.05) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Begins the process of moving the pivot.
     * Does not wait for the pivot movement to finish before terminating the method an allowing
     * other functions to begin.
     * @param newPosition The position to move the pivot to
     */
    public void movePivot(PivotPosition newPosition) {
        pivot.setPower(-0.8);
        while (pivot.getCurrentPosition() < newPosition.pos) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
        pivot.setPower(0);
    }

    /**
     * Moves the pivot fully.
     * The method will not terminate until the pivot is fully moved, meaning that only the action
     * of the pivot can be occurring at the given time.
     * @param newPosition The position to move the pivot to
     */
    public void movePivotSync(PivotPosition newPosition) {
        movePivot(newPosition);
        while (pivot.isBusy()) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
    }


    /*TODO: Determine what values UP and DOWN should be, and/or replace UP and DOWN with any other positions we need*/
    /**
     * An enum to keep track of the positions we need the slide to be in regularly.
     */
    public enum PivotPosition {
        UP(2222),
        DOWN(0);
        public final int pos;

        PivotPosition(int pos) {
            this.pos = pos;
        }
    }
}