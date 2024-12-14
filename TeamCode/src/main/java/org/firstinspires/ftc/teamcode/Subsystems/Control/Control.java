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
    public final Servo pivot; //The Servo that controls the pivot
    public final Servo pivot2; //The Servo that controls the pivot
    public final DcMotorEx linearSlideL; //The DcMotorEx that controls the left linear slide motor
    public final DcMotorEx linearSlideR; //The DcMotorEx that controls the right linear slide motor

    public Control(Telemetry telemetry, Servo clawMotor, Servo pivotMotor, Servo pivotMotor2, DcMotorEx linearSlideMotor, DcMotorEx linearSlideMotor2) {
        super(telemetry, "control");

        // Initializing instance variables
        this.claw = clawMotor;
        this.pivot = pivotMotor;
        this.pivot2 = pivotMotor2;
        this.linearSlideL = linearSlideMotor;
        this.linearSlideR = linearSlideMotor2;
    }

    /**
     * Gets all defaults, directions, etc. ready for the autonomous period
     */
    public void initDevicesAuto() {
        linearSlideL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlideL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlideR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlideR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Gets all defaults, directions, etc. ready for the teleop period
     */
    public void initDevicesTeleop() {
        linearSlideL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlideL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlideR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlideR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        while (Math.abs(pivot.getPosition() - newPosition.pos) > 0.05 && Math.abs(pivot2.getPosition() - newPosition.pos) > 0.05) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
    }

    public void moveLinearSlide(LinearSlidePosition newPosition) {
        linearSlideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        linearSlideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        linearSlideL.setTargetPosition(newPosition.pos);
        linearSlideR.setTargetPosition(newPosition.pos);

    }

    public void moveLinearSlideSync(LinearSlidePosition newPosition) {
        moveLinearSlide(newPosition);
        while ((Math.abs(linearSlideL.getCurrentPosition() - newPosition.pos) > 25) && (Math.abs(linearSlideR.getCurrentPosition() - newPosition.pos) > 25)) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
    }

    /*This function is used to sync the linear slide motors*/
    //TODO: WRITE BETTER DESCRIPTION
    //TODO: Tune PID constants
    //TODO: Add a rotations to ticks conversion so that the linear slide position can be given in rotations but the rest of the function works in ticks
    /*Main sources:
        1) https://github.com/acmerobotics/road-runner/blob/master/doc/notebook/road-runner-lite.ipynb)
     */
    public void moveLinearSlidePID(LinearSlidePosition newPosition) {
        int lastError = 0;
        int integral = 0;
        double error;
        double pFix;
        double iFix;
        double dFix;
        linearSlideL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        linearSlideR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //int motor1Abs = Math.abs(linearSlideL.getCurrentPosition());
        //int motor2Abs = Math.abs(linearSlideL.getCurrentPosition());

        //TODO: use the above resource to control acceleration and velocity
        //Tell the slide to keep moving until it's close to its final position (being careful not to overshoot)
        //Takes the average of the two slide motor positions and compares this to 25 ticks (keeps going only if it's greater than 25 ticks from the target position)
        while(((Math.abs(linearSlideL.getCurrentPosition() - newPosition.pos) + Math.abs(linearSlideR.getCurrentPosition() - newPosition.pos)) / 2)  > 25){
            error = Math.abs(linearSlideL.getCurrentPosition()) - Math.abs(linearSlideR.getCurrentPosition());
            pFix = error * 0.5;
            iFix = (integral + error)*0.01;
            dFix = (error - lastError) * 4;
            error = lastError;
            linearSlideL.setPower(0.3 + (pFix + iFix + dFix));
            linearSlideR.setPower(-0.3 - (pFix + iFix + dFix));
        }
    }


    public void moveArm(LinearSlidePosition slidePosition, PivotPosition pivotPosition) {
        moveLinearSlide(slidePosition);
        movePivot(pivotPosition);
    }
}
