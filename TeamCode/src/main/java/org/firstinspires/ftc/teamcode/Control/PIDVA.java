package org.firstinspires.ftc.teamcode.Control;

import androidx.core.math.MathUtils;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * <p>A Generic PID controller</p>
 * <p><b>Features:</b></p>
 * <ul>
 *     <li>Toggleable Low Pass Filter</li>
 *     <li>Integral Windup Prevention</li>
 *     <li>Integral Power Cap</li>
 * </ul>
 */
public class PIDVA {
    protected boolean hasRun = false;
    protected ElapsedTime timer = new ElapsedTime();
    protected double previousError = 0;
    protected double integralSum = 0;
    protected double derivative = 0;
    protected double previousDerivative = 0;
    public static final double derivativeInverseFilterStrength = 0.7; // TODO: Should be configurable
    private final PIDCoefficients<Double> pidCoefficients;
    private final VACoefficients<Double> vaCoefficients;
    private final boolean lowPass;

    public PIDVA(PIDCoefficients<Double> pidCoefficients, VACoefficients<Double> vaCoefficients) {
        this(pidCoefficients, vaCoefficients, true);
    }

    public PIDVA(PIDCoefficients<Double> pidCoefficients, VACoefficients<Double> vaCoefficients, boolean lowPass) {
        this.pidCoefficients = pidCoefficients;
        this.vaCoefficients = vaCoefficients;
        this.lowPass = lowPass;
    }

    /**
     * calculate PID output
     *
     * @param target   the target position
     * @param measured current system state
     * @return PID output
     */
    public double calculate(double target, double measured, double velocity, double acceleration) {
        double dt = getDT();
        double error = calculateError(target, measured);
        double derivative = calculateDerivative(error, dt);
//        if (Math.signum(error) != Math.signum(previousError)) {
//            integralSum = 0; // Prevents integral windup
//        }
        integrate(error, dt);
        previousError = error;

        // TODO: the abs of the integral sum*ki should be capped at 0.25 to not break everything.
        var iTerm = integralSum * pidCoefficients.kI;
        // Cap output at range (-1,1).
        var pidPower = error * pidCoefficients.kP + iTerm + derivative * pidCoefficients.kD;
        var vaPower = vaCoefficients.kV * velocity + vaCoefficients.kA * acceleration;
        return MathUtils.clamp(pidPower + vaPower, -1, 1);
    }

    /**
     * get the time constant
     *
     * @return time constant
     */
    public double getDT() {
        if (!hasRun) {
            hasRun = true;
            timer.reset();
        }
        double dt = timer.seconds();
        timer.reset();
        return dt;
    }

    protected double calculateError(double target, double measured) {
        return target - measured;
    }

    protected void integrate(double error, double dt) {
        integralSum += ((error + previousError) / 2) * dt;
    }

    protected double calculateDerivative(double error, double dt) {
        previousDerivative = derivative;
        derivative = (error - previousError) / dt;
        if (lowPass) {
            derivative = PIDVA.derivativeInverseFilterStrength * previousDerivative + derivative * (1 - PIDVA.derivativeInverseFilterStrength);
        }
        return derivative;
    }

    @Override
    public String toString() {
        return "PIDVA {" +
                "PIDCoefficients=" + pidCoefficients.toString() +
                "VACoefficients=" + vaCoefficients.toString() +
                ", lowPass=" + lowPass +
                ", dump=" + previousError + "|" + derivative + "|" + integralSum +
                '}';
    }

    public void reset() {
        integralSum = 0;
    }
}