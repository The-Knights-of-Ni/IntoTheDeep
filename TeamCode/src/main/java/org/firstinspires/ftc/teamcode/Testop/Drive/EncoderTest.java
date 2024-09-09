package org.firstinspires.ftc.teamcode.Testop.Drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.Auto.Auto;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;

@Autonomous(name = "Encoder Test", group = "Concept")
public class EncoderTest extends Auto {
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
        initAuto(AllianceColor.BLUE);
        waitForStart();
        timer.reset();
        while (opModeIsActive()) {
            telemetry.addData("Motor Encoder", "Positions: %d %d %d %d", robot.drive.localizer.frontLeft.getCurrentPosition(), robot.drive.localizer.frontRight.getCurrentPosition(), robot.drive.localizer.rearLeft.getCurrentPosition(), robot.drive.localizer.rearRight.getCurrentPosition());
            telemetry.update();
        }
    }
}
