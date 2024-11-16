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
    public final ServoEx pivot1; //The Servo that controls the pivot
    public final ServoEx pivot2; //The Servo that controls the pivot
    public final ServoEx pivotUChannel; //The Servo that controls the pivot
    public final DcMotorEx linearSlide; //The DcMotorEx that controls the linear slide
    public final DcMotorEx linearSlide2;


    public Control(Telemetry telemetry, Servo clawMotor, Servo pivotMotor1, Servo pivotMotor2, Servo pivotMotorUChannel,DcMotorEx linearSlideMotor, DcMotorEx linearSlideMotor2) {
        super(telemetry, "control");


        // Initializing instance variables
        this.claw = (ServoEx) clawMotor;
        this.pivot1 = (ServoEx) pivotMotor1;
        this.pivot2 = (ServoEx) pivotMotor2;
        this.pivotUChannel = (ServoEx) pivotMotorUChannel;
        this.linearSlide = linearSlideMotor;
        this.linearSlide2=linearSlideMotor2;
    }

    /**
     * Gets all defaults, directions,etc. ready for the autonomous period
     */
    public void initDevicesAuto() {
        linearSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //pivot1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //pivot1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //pivot2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //pivot2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //pivotUChannel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //pivotUChannel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //claw.setDirection(Servo.Direction.FORWARD);
    }

    /*public void initDevicesAuto() {

    }*/

    /**
     * Gets all defaults, directions,etc. ready for the teleop period
     */
    public void initDevicesTeleop() {
        linearSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //pivot1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //pivot1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //pivot2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //pivot2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //pivotUChannel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //pivotUChannel.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        //claw.setDirection(Servo.Direction.FORWARD);
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
        pivot1.setPower(-0.8);
        pivot2.setPower(0.8);
        while (pivot1.getCurrentPosition() < newPosition.pos) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
        pivot1.setPower(0);
        pivot2.setPower(0);
    }

    /**
     * Moves the pivot fully.
     * The method will not terminate until the pivot is fully moved, meaning that only the action
     * of the pivot can be occurring at the given time.
     * @param newPosition The position to move the pivot to
     */
    public void movePivotSync(PivotPosition newPosition) {
        movePivot(newPosition);
        while (pivot1.isBusy()) {
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
    public void moveLinearSlide(LinearSlidePosition newPosition) {
        linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        linearSlide.setTargetPosition(newPosition.pos);
        linearSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        linearSlide2.setTargetPosition(newPosition.pos);
    }


    public void moveLinearSlideSync(LinearSlidePosition newPosition) {
        moveLinearSlide(newPosition);
        while (Math.abs(linearSlide.getCurrentPosition() - newPosition.pos) > 25) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
    }


    public void moveArm(LinearSlidePosition slidePosition, PivotPosition pivotPosition) {
        moveLinearSlide(slidePosition);
        movePivot(pivotPosition);
    }
}


}