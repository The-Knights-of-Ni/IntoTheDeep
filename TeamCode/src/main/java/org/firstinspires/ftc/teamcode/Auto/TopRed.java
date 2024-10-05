package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.Util.Pose;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;


@Autonomous(name = "Auto Red Top", group = "Auto")
public class TopRed extends Auto {
    @SuppressWarnings("RedundantThrows")
    public void runOpMode() throws InterruptedException {
        //robot begins to function
        initAuto(AllianceColor.RED);
        waitForStart();
        timer.reset();

        robot.drive.move(new Pose(-4 * mmPerInch, 26 * mmPerInch, 90));
        //TODO: turn claw, open claw
        //robot.drive.move(90);
        robot.drive.move(new Pose(24 * mmPerInch, 40 * mmPerInch, 90));
        //robot.drive.move(90);
        robot.drive.move(new Pose(0, 34 * mmPerInch, 0));
        robot.drive.move(new Pose(0, -15 * mmPerInch, 0));
    }
}