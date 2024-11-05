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
    public final ServoEx scoringClaw; //The servo that controls the claw
    public final ServoEx intakeClaw; //The servo that controls the intake claw
    public final ServoEx intake; //The servo that controls the intake
    public final ServoEx pivot; //The Servo that controls the pivot
    public final DcMotorEx linearSlide; //The DcMotorEx that controls the linear slide

    public Control(Telemetry telemetry, Servo intake, Servo scoringClawMotor, Servo intakeClawMotor, Servo pivotMotor, DcMotorEx linearSlideMotor) {
        super(telemetry, "control");

        // Initializing instance variables
        this.scoringClaw = (ServoEx) scoringClawMotor;
        this.intakeClaw = (ServoEx) intakeClawMotor;
        this.intake = (ServoEx) intake;
        this.pivot = (ServoEx) pivotMotor;
        this.linearSlide = linearSlideMotor;
    }

    /**
     * Gets all defaults, directions, etc. ready for the autonomous period
     */
    public void initDevicesAuto() {
        linearSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Gets all defaults, directions, etc. ready for the teleop period
     */
    public void initDevicesTeleop() {
        linearSlide.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }


    public void moveScoringClaw(ClawPosition newPosition) {
        scoringClaw.turnToAngle(newPosition.pos);
    }

    public void moveScoringClawSync(ClawPosition newPosition) {
        moveScoringClaw(newPosition);
        // Angles are all in degrees
        while (Math.abs(scoringClaw.getAngle() - newPosition.pos) > 20) {
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
    public void openScoringClaw() {
        moveScoringClaw(ClawPosition.OPEN);
    }

    /**
     * Begins the process of closing the claw.
     * Does not wait for the claw action to finish opening before terminating the method and
     * allowing other functions to begin.
     */
    public void closeScoringClaw() {
        moveScoringClaw(ClawPosition.CLOSE);
    }

    /**
     * Opens the claw fully.
     * The method will not terminate until the claw is fully open, meaning that only the action
     * of the claw opening can be occurring at the given time.
     */
    public void openScoringClawSync() {
        moveScoringClawSync(ClawPosition.OPEN);
    }

    /**
     * Closes the claw fully.
     * The method will not terminate until the claw is fully closed, meaning that only the action
     * of the claw closing can be occurring at the given time.
     */
    public void closeScoringClawSync() {
        moveScoringClawSync(ClawPosition.CLOSE);
    }

    public void moveIntakeClaw(ClawPosition newPosition) {
        intakeClaw.setPosition(newPosition.pos);
    }

    public void moveIntakeClawSync(ClawPosition newPosition) {
        moveIntakeClaw(newPosition);
        while (Math.abs(intakeClaw.getPosition() - newPosition.pos) > 0.05) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                /*See note on InterruptedException above*/
                throw new RuntimeException(e);
            }
        }
    }

    public void openIntakeClaw() {
        moveIntakeClaw(ClawPosition.OPEN);
    }

    public void closeIntakeClaw() {
        moveIntakeClaw(ClawPosition.CLOSE);
    }

    public void openIntakeClawSync() {
        moveIntakeClawSync(ClawPosition.OPEN);
    }

    public void closeIntakeClawSync() {
        moveIntakeClawSync(ClawPosition.CLOSE);
    }

    public void moveIntake(IntakePosition newPosition) {
        intakeClaw.setPosition(newPosition.pos);
    }

    public void moveIntakeSync(IntakePosition newPosition) {
        moveIntake(newPosition);
        while (Math.abs(intake.getPosition() - newPosition.pos) > 0.05) {
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
        scoringClaw.setPosition(newPosition.pos);
    }

    /**
     * Moves the pivot fully.
     * The method will not terminate until the pivot is fully moved, meaning that only the action
     * of the pivot can be occurring at the given time.
     * @param newPosition The position to move the pivot to
     */
    public void movePivotSync(PivotPosition newPosition) {
        movePivot(newPosition);
        while (Math.abs(scoringClaw.getPosition() - newPosition.pos) > 0.05) {
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
        linearSlide.setTargetPosition(newPosition.pos);
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
