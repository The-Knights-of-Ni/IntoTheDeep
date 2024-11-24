package org.firstinspires.ftc.teamcode.Subsystems.Control;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.Util.ServoEx;


/**
 * Control subsystem for controlling arms and claws
 */
public class Control extends Subsystem {
    public final ServoEx claw; //The servo that controls the claw
    public final ServoEx pivot; //The Servo that controls the pivot
    public final ServoEx pivot2; //The Servo that controls the pivot
    public final ServoEx pivotUChannel;
    public final DcMotorEx viperSlide; //The DcMotorEx that controls the linear slide
    public final DcMotorEx viperSlide2;

    public Control(Telemetry telemetry, Servo clawMotor, Servo pivotMotor, Servo pivotMotor2, Servo pivotUChannel, DcMotorEx viperSlideMotor, DcMotorEx viperSlideMotor2) {
        super(telemetry, "control");

        // Initializing instance variables
        this.claw = (ServoEx) clawMotor;
        this.pivot = (ServoEx) pivotMotor;
        this.pivot2 = (ServoEx) pivotMotor2;
        this.pivotUChannel=(ServoEx) pivotUChannel;
        this.viperSlide = viperSlideMotor;
        this.viperSlide2 = viperSlideMotor2;
    }

    /**
     * Gets all defaults, directions, etc. ready for the autonomous period
     */
    public void initDevicesAuto() {
        viperSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        viperSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        viperSlide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        viperSlide2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Gets all defaults, directions, etc. ready for the teleop period
     */
    public void initDevicesTeleop() {
        viperSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        viperSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        viperSlide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        viperSlide2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    public void moveClaw(ClawPosition newPosition) {
        claw.turnToAngle(newPosition.pos);
    }

    public void moveClawSync(ClawPosition newPosition) {
        moveClaw(newPosition);
        // Angles are all in degrees
        while (Math.abs(claw.getAngle() - newPosition.pos) > 20) {
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

    /**
     * Begins the process of moving the pivot.
     * Does not wait for the pivot movement to finish before terminating the method an allowing
     * other functions to begin.
     * @param newPosition The position to move the pivot to
     */
    public void movePivot(PivotPosition newPosition) {
        pivot.setPosition(newPosition.pos);
        pivot2.setPosition(newPosition.pos);
    }

    /**
     * Moves the pivot fully.
     * The method will not terminate until the pivot is fully moved, meaning that only the action
     * of the pivot can be occurring at the given time.
     * @param newPosition The position to move the pivot to
     */
    public void movePivotSync(PivotPosition newPosition) {
        movePivot(newPosition);
        while (Math.abs(pivot.getPosition() - newPosition.pos) > 0.05) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
    }

    public void moveviperSlide(LinearSlidePosition newPosition) {
        viperSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        viperSlide.setTargetPosition(newPosition.pos);
        viperSlide2.setTargetPosition(newPosition.pos);

    }

    public void moveviperSlideSync(LinearSlidePosition newPosition) {
        moveviperSlide(newPosition);
        while ((Math.abs(viperSlide.getCurrentPosition() - newPosition.pos) > 25) && (Math.abs(viperSlide2.getCurrentPosition() - newPosition.pos) > 25)) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
    }

    public void moveArm(LinearSlidePosition slidePosition, PivotPosition pivotPosition) {
        moveviperSlide(slidePosition);
        movePivot(pivotPosition);
    }
}
