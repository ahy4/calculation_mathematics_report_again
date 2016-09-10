package report;

public class Complex {
    private final double re;
    private final double im;

    public Complex(double re, double im) {
        this.re = re;
        this.im = im;
    }

    public double re() { return re; }
    public double im() { return im; }

    public Complex add(Complex c) {
        return new Complex(re + c.re, im + c.im());
    }

    public Complex sub(Complex c) {
        return new Complex(re - c.re, im - c.im);
    }

    public Complex mul(Complex c) {
        return new Complex(
            re*c.re - im*c.im, re*c.im + im*c.re);
    }

    public Complex div(Complex c) {
        double denominator = c.re*c.re + c.im*c.im;
        return new Complex(
            (re*c.re + im*c.im)/denominator,
            (im*c.re - re*c.im)/denominator);
    }

    public Complex Con() {
        return new Complex(re, -im);
    }

    public double abs() {
        return Math.sqrt(re*re + im*im);
    }

    public double arg() {
        return Math.atan(im/re);
    }

    public String toString() {
        if(im >= 0)
            return "(" + re + " + " + im + "i" + ")";
        else
            return "(" + re + " - " + -im + "i" + ")";
    }

}