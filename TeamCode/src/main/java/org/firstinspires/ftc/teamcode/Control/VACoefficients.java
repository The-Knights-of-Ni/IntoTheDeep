package org.firstinspires.ftc.teamcode.Control;

public class VACoefficients<V> {
    public V kV;
    public V kA;

    public VACoefficients(V kV, V kA) {
        this.kV = kV;
        this.kA = kA;
    }

    @Override
    public String toString() {
        return "VACoefficients{" +
                "kV=" + kV +
                ", kA=" + kA +
                '}';
    }
}
