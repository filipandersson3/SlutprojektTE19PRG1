package ScreenRenderer;

public class Complex {
    double re;
    double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }
    public Complex multiply (Complex z2) {
        return new Complex((re*z2.re)-(im*z2.im), im*z2.re + z2.im*re);
    }
}
