package org.firstinspires.ftc.teamcode.Testop.Drive;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.Auto.Auto;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.MotorGeneric;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.PoseEstimation.MotorEncoders;

@Autonomous(name = "Pose Estimation Test", group = "Concept")
public class PoseEstimationTest extends Auto {

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        var frontLeftDriveMotor = (DcMotorEx) hardwareMap.dcMotor.get("fl");
        var frontRightDriveMotor = (DcMotorEx) hardwareMap.dcMotor.get("fr");
        var rearLeftDriveMotor = (DcMotorEx) hardwareMap.dcMotor.get("rl");
        var rearRightDriveMotor = (DcMotorEx) hardwareMap.dcMotor.get("rr");
        frontLeftDriveMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDriveMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rearLeftDriveMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rearRightDriveMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        frontRightDriveMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRightDriveMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        var estimation = new MotorEncoders(new MotorGeneric<>(frontLeftDriveMotor, frontRightDriveMotor, rearLeftDriveMotor, rearRightDriveMotor));
        estimation.start();
        while (opModeIsActive()) {
            estimation.update();
            var pose = estimation.getPose();
            var currentPositions = new MotorGeneric<>(frontLeftDriveMotor.getCurrentPosition(), frontRightDriveMotor.getCurrentPosition(), rearLeftDriveMotor.getCurrentPosition(), rearRightDriveMotor.getCurrentPosition());
            telemetry.addData("Motor Ticks", currentPositions.toString());
            telemetry.addData("Pose", pose.toString());
            telemetry.update();
            Thread.sleep(20);
        }
    }
}
