package org.firstinspires.ftc.teamcode;

import android.util.Log;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareDevice;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.teamcode.Subsystems.Web.WebAction;
import org.firstinspires.ftc.teamcode.Subsystems.Web.WebLog;
import org.firstinspires.ftc.teamcode.Subsystems.Web.WebThread;
import org.firstinspires.ftc.teamcode.Util.AllianceColor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.util.HashMap;

import static org.mockito.Mockito.*;

public class RunTest {
    Robot init(HashMap<String, Boolean> flags) {
        HardwareMap hardwareMap = mock(HardwareMap.class);
        hardwareMap.dcMotor = (HardwareMap.DeviceMapping<DcMotor>) mock(HardwareMap.DeviceMapping.class);
        when(hardwareMap.get("imu")).thenReturn(mock(HardwareDevice.class)); // TODO: Fix this statement
        when(hardwareMap.dcMotor.get("fl")).thenReturn(new MockDcMotorEx());
        when(hardwareMap.dcMotor.get("fr")).thenReturn(new MockDcMotorEx());
        when(hardwareMap.dcMotor.get("rl")).thenReturn(new MockDcMotorEx());
        when(hardwareMap.dcMotor.get("rr")).thenReturn(new MockDcMotorEx());
        flags.put("vision", false);
        return new Robot(hardwareMap, new MockTelemetry(), new ElapsedTime(), AllianceColor.BLUE,
                new Gamepad(), new Gamepad(), flags);
    }

    @Test
    void testStart() {
        try (MockedStatic<Log> mocked = mockStatic(Log.class)) {
            HashMap<String, Boolean> flags = new HashMap<>();
            flags.put("web", true);
            Robot robot = init(flags);
        }
    }

    @Test
    void testWeb() {
        try (MockedStatic<Log> mocked = mockStatic(Log.class)) {
            HashMap<String, Boolean> flags = new HashMap<>();
            WebThread web = new WebThread();
            web.start();
            WebThread.addLog(new WebLog("test", "Testing stuff", WebLog.LogSeverity.INFO));
            WebThread.addAction(new WebAction("test", "Doing nothing for 100 seconds"));
            for (int i = 0; i < 101; i++) {
                Thread.sleep(50);
                WebThread.setPercentage("test", i);
            }
            WebThread.removeAction("test");
            WebThread.terminate = true;
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
