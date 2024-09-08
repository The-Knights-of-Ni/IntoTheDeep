package org.firstinspires.ftc.teamcode.Subsystems.Control;

import android.util.Log;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Subsystems.Subsystem;


/**
 * Control subsystem for controlling arms and claws
 */
public class Control extends Subsystem {
    public Control(Telemetry telemetry) {
        super(telemetry, "control");
    }

    public void initDevicesAuto() {
    }

    public void initDevicesTeleop() {
    }
}
