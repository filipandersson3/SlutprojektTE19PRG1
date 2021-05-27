package FractalViewer;

import java.awt.*;

public class Worker extends Thread {
    private int WIDTH;
    private int HEIGTH;
    private Screen screen;

    private int maxiter = 200;
    private double zoom;
    private double offsetx;
    private double offsety;
    private int startRow;
    private int endRow;

    public Worker(int width, int heigth, Screen screen,
                  double zoom, double offsetx, double offsety, int startRow, int endRow, int maxiter) {
        WIDTH = width;
        HEIGTH = heigth;
        this.screen = screen;
        this.zoom = zoom;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.startRow = startRow;
        this.endRow = endRow;
        this.maxiter = maxiter;
    }

    public void run() {
        // http://math.hws.edu/eck/cs124/javanotes7/c12/s2.html#threads.2.4 för fler trådar
        for (int c = WIDTH*startRow; c <= (WIDTH*endRow)-1; c++) {
            //gör c till ett ställe på koordinatsystemet, zoom förändrar storleken på fraktalen, offset ändrar var den börjar
            Complex ccpx = new Complex ((c%(WIDTH)*0.01/zoom)+offsetx,((c/WIDTH)*0.01/zoom)+offsety);
            Complex z = new Complex(0,0);
            for (int iter = 0; iter <= maxiter; iter++) {
                z = (z.multiply(z)).add(ccpx); //själva formeln
                if (z.re*z.re+z.im*z.im >= 4.0) { //inte del av mandelbrot set
                    screen.drawPixel(c%WIDTH,c/WIDTH,
                            Color.HSBtoRGB(iter*0.001f+0.63f,iter*0.001f+0.8f,iter*0.01f+0.6f));
                    iter = maxiter;
                } else { //del av mandelbrot set
                    screen.drawPixel(c%WIDTH,c/WIDTH,
                            0x000000);
                }
                iter++;
            }
        }
    }
}
