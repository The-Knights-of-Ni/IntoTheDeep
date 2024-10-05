package org.firstinspires.ftc.teamcode.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.teamcode.Util.Pose;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;

@Autonomous(name = "Auto Blue Top", group = "Auto")
public class TopBlue extends Auto {
    @SuppressWarnings("RedundantThrows")
    public void runOpMode() throws InterruptedException {
        //robot begins to function
        initAuto(AllianceColor.BLUE);
        waitForStart();
        timer.reset();
        robot.drive.move(new Pose(-12 * mmPerInch, 60 * mmPerInch, -90));
        //robot.drive.move(-90);
        robot.drive.move(new Pose(0, 36 * mmPerInch, -90));
        //TODO: change x and y based on length of claw
        robot.drive.move(new Pose(12 * mmPerInch, 12 * mmPerInch, 45));
        //TODO: open claw, drop sample
        //TODO: change drive amounts
        robot.drive.move(new Pose(2 * mmPerInch, 2 * mmPerInch, 0));
    }
}