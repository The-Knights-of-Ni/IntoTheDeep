package org.firstinspires.ftc.teamcode.Subsystems.Drive.Localizer;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.Controller.HolonomicControllerOutput;
import org.firstinspires.ftc.teamcode.Subsystems.Drive.MotorGeneric;

public class MecanumLocalizer extends HolonomicLocalizer {
    public DcMotorEx frontLeft;
    public DcMotorEx frontRight;
    public DcMotorEx rearLeft;
    public DcMotorEx rearRight;

    public MecanumLocalizer(DcMotorEx frontLeft, DcMotorEx frontRight, DcMotorEx rearLeft, DcMotorEx rearRight) {
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;
    }

    public MotorGeneric<Double> localize(HolonomicControllerOutput output) {
        var denominator = Math.max(Math.abs(output.y) + Math.abs(output.y) + Math.abs(output.heading), 1);
        return cropMotorPowers(
                new MotorGeneric<>(
                        (output.y + output.x + output.heading) / denominator,
                        (output.y - output.x - output.heading) / denominator,
                        (output.y - output.x + output.heading) / denominator,
                        (output.y + output.x - output.heading) / denominator)
        );
    }

    @Override
    public void setPowers(HolonomicControllerOutput output) {
        var powers = localize(output);
        frontLeft.setPower(powers.frontLeft);
        frontRight.setPower(powers.frontRight);
        rearLeft.setPower(powers.rearLeft);
        rearRight.setPower(powers.rearRight);
    }
}
