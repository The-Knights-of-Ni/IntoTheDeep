package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.teamcode.Subsystems.Drive.Drive.thetaPIDCoefficients;
import static org.firstinspires.ftc.teamcode.Subsystems.Drive.Drive.xyPIDCoefficients;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.Controller.HolonomicPIDController;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.Drive;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.MotorGeneric;
import org.firstinspires.ftc.teamcode.Control.PID;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.PoseEstimationMethodChoice;
import org.firstinspires.ftc.teamcode.Util.Pose;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mockStatic;

class DriveTest {
    // The margins will get smaller over time, as the mocking improves and the PID becomes more calibrated.
    final static int PID_TICK_COUNT_MARGIN = 500;

    Drive init() {
        MockDcMotorEx mockFL = new MockDcMotorEx(DcMotor.RunMode.RUN_USING_ENCODER);
        MockDcMotorEx mockFR = new MockDcMotorEx(DcMotor.RunMode.RUN_USING_ENCODER);
        MockDcMotorEx mockRL = new MockDcMotorEx(DcMotor.RunMode.RUN_USING_ENCODER);
        MockDcMotorEx mockRR = new MockDcMotorEx(DcMotor.RunMode.RUN_USING_ENCODER);
        MockTelemetry telemetry = new MockTelemetry();
        BNO055IMU mockIMU = Mockito.mock(BNO055IMU.class);
        return new Drive(new MotorGeneric<>(mockFL, mockFR, mockRL, mockRR), null, PoseEstimationMethodChoice.NONE, mockIMU, telemetry);
    }

    @Test
    void testCalcMotorPower2D() {
        Drive drive = init();
        assertEquals(1, drive.calcMotorPowers(0, 1, 0).frontLeft, 0.4);
        assertEquals(1, drive.calcMotorPowers(0, 1, 0).frontRight, 0.4);
        assertEquals(1, drive.calcMotorPowers(0, 1, 0).rearLeft, 0.4);
        assertEquals(1, drive.calcMotorPowers(0, 1, 0).rearRight, 0.4);
        assertNotEquals(0, drive.calcMotorPowers(0, 1, 0).frontLeft);
        assertNotEquals(0, drive.calcMotorPowers(0, 1, 0).frontRight);
        assertNotEquals(0, drive.calcMotorPowers(0, 1, 0).rearLeft);
        assertNotEquals(0, drive.calcMotorPowers(0, 1, 0).rearRight);
    }

    @Test
    void testHolonomicMecanumController() {
        HolonomicPIDController controller = new HolonomicPIDController(new PID(xyPIDCoefficients), new PID(xyPIDCoefficients), new PID(thetaPIDCoefficients));
//        HolonomicLocalizer localizer = new MecanumLocalizer();
//        MotorGeneric<Double> powers = localizer.localize(controller.calculate(new Pose(0, 0, 0), new Pose(0, 0, 0)));
//        assertEquals(0, powers.frontLeft, 0.01);
//        assertEquals(0, powers.frontRight, 0.01);
//        assertEquals(0, powers.rearLeft, 0.01);
//        assertEquals(0, powers.rearRight, 0.01);
    }

    @Test
    void testHolonomicController() {
        HolonomicPIDController controller = new HolonomicPIDController(new PID(xyPIDCoefficients), new PID(xyPIDCoefficients), new PID(thetaPIDCoefficients));
        var powers = controller.calculate(new Pose(0, 0, 0), new Pose(1000, 0, 0));
        assertEquals(1, powers.x, 0.3);
    }

    @Test
    void testMecanumLocalizer() {
//        HolonomicLocalizer localizer = new MecanumLocalizer();
//        var resp = localizer.localize(new HolonomicControllerOutput(0, 1, 0, 0));
//        assertEquals(1, resp.frontLeft);
//        assertEquals(1, resp.frontRight);
//        assertEquals(1, resp.rearLeft);
//        assertEquals(1, resp.rearRight);
    }

    @Test
    void deadReckoningTest() {
        final double wheelDisplacePerEncoderCount = 0.1;
        var currentMotorLocations =  new MotorGeneric<>(1000, 0, 0, -1000);
        //Compute change in encoder positions
        var deltaMotorLocations = new MotorGeneric<>(currentMotorLocations.frontLeft - 0, currentMotorLocations.frontRight - 0, currentMotorLocations.rearLeft - 0, currentMotorLocations.rearRight - 0);
        //Compute displacements for each wheel
        double displ_m0 = ((double) deltaMotorLocations.frontLeft) * wheelDisplacePerEncoderCount;
        double displ_m1 = ((double) deltaMotorLocations.frontRight) * wheelDisplacePerEncoderCount;
        double displ_m2 = ((double) deltaMotorLocations.rearLeft) * wheelDisplacePerEncoderCount;
        double displ_m3 = ((double) deltaMotorLocations.rearRight) * wheelDisplacePerEncoderCount;

        //Compute the average displacement in order to untangle rotation
        //from displacment
        var forward_back = (displ_m0 + displ_m1 + displ_m2 + displ_m3) / 4.0;
        var strafe = (0 - displ_m1 - displ_m0 + displ_m2 + displ_m3) / 4.0;
        double delta_theta = (deltaMotorLocations.frontRight + deltaMotorLocations.rearRight - deltaMotorLocations.frontLeft - deltaMotorLocations.rearLeft) / (457.2);
        System.out.println(forward_back);
        System.out.println(strafe);
        System.out.println(delta_theta);

        //Move this holonomic displacement from robot to field frame of reference
        double robotTheta = delta_theta;  //Just make the accessor call once
        double delt_Xf = (forward_back * Math.cos(robotTheta) - strafe * Math.sin(robotTheta));
        double delt_Yf = (forward_back * Math.sin(robotTheta) + strafe * Math.cos(robotTheta));

        //Update the position
        var pose = new Pose(delt_Xf, delt_Yf, delta_theta);

        System.out.println(pose.toString());

    }
}
