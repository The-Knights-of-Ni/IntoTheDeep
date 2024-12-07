package org.firstinspires.ftc.teamcode.Testop;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Auto.Auto;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;
import org.firstinspires.ftc.teamcode.Util.Pose;

@Autonomous(name = "Arm Calibration", group = "Concept")
public class ArmCalibration extends Auto {
    /**
     * Override of runOpMode()
     *
     * <p>Please do not swallow the InterruptedException, as it is used in cases where the op mode
     * needs to be terminated early.</p>
     *
     * @see com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
     */
    @Override
    @SuppressWarnings("RedundantThrows")
    public void runOpMode() throws InterruptedException {
        initAuto(AllianceColor.RED);
        waitForStart();
        timer.reset();
        robot.arm.linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.arm.linearSlide2.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        robot.arm.linearSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        robot.arm.linearSlide2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        var linearSlideLog = telemetry.addLine("Linear Slide Position");
        var linearSlide2Log = telemetry.addLine("Linear Slide 2 Position");
        var pivotLog = telemetry.addLine("Pivot Position");
        var pivot2Log = telemetry.addLine("Pivot 2 Position");
        while (opModeIsActive()) {
            var linearSlidePosition = robot.arm.linearSlide.getCurrentPosition();
            var linearSlide2Position = robot.arm.linearSlide2.getCurrentPosition();
            var pivotPosition = robot.arm.pivot.getPosition();
            var pivot2Position = robot.arm.pivot2.getPosition();
            linearSlideLog.addData("Linear Slide Position", linearSlidePosition);
            linearSlide2Log.addData("Linear Slide 2 Position", linearSlide2Position);
            pivotLog.addData("Pivot Position", pivotPosition);
            pivot2Log.addData("Pivot 2 Position", pivot2Position);
            telemetry.update();
        }
        sleep(2000);
    }
}
