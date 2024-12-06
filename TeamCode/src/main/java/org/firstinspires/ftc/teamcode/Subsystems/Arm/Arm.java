package org.firstinspires.ftc.teamcode.Subsystems.Arm;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Subsystem;


/**
 * Control subsystem for controlling arms and claws
 */
public class Arm extends Subsystem {
    public final Servo pivot; //The Servo that controls the pivot
    public final Servo pivot2; //The Servo that controls the pivot
    public final DcMotorEx linearSlide; //The DcMotorEx that controls the linear slide
    public final DcMotorEx linearSlide2;

    public Arm(Telemetry telemetry, Servo pivotMotor, Servo pivotMotor2, DcMotorEx linearSlideMotor, DcMotorEx linearSlideMotor2) {
        super(telemetry, "control");

        // Initializing instance variables
        this.pivot = pivotMotor;
        this.pivot2 = pivotMotor2;
        this.linearSlide = linearSlideMotor;
        this.linearSlide2 = linearSlideMotor2;
    }

    /**
     * Gets all defaults, directions, etc. ready for the autonomous period
     */
    public void initDevicesAuto() {
        linearSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Gets all defaults, directions, etc. ready for the teleop period
     */
    public void initDevicesTeleop() {
        linearSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        linearSlide2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
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
        linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        linearSlide2.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        linearSlide.setTargetPosition(newPosition.pos);
        linearSlide2.setTargetPosition(newPosition.pos);

    }

    public void moveLinearSlideSync(LinearSlidePosition newPosition) {
        moveLinearSlide(newPosition);
        while ((Math.abs(linearSlide.getCurrentPosition() - newPosition.pos) > 25) && (Math.abs(linearSlide2.getCurrentPosition() - newPosition.pos) > 25)) {
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
