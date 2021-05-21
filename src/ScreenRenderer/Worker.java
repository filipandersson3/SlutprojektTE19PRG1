package ScreenRenderer;

import java.awt.*;

public class Worker extends Thread {
    private int WIDTH;
    private int HEIGTH;
    private int scale;
    private Screen screen;

    private int maxiter = 200;
    private double zoom;
    private double offsetx;
    private double offsety;
    private int startRow;
    private int endRow;

    public Worker(int width, int heigth, int scale, Screen screen,
                  double zoom, double offsetx, double offsety, int startRow, int endRow) {
        WIDTH = width;
        HEIGTH = heigth;
        this.scale = scale;
        this.screen = screen;
        this.zoom = zoom;
        this.offsetx = offsetx;
        this.offsety = offsety;
        this.startRow = startRow;
        this.endRow = endRow;
    }

    public void run() {
        // http://math.hws.edu/eck/cs124/javanotes7/c12/s2.html#threads.2.4 för fler trådar
        for (int c = WIDTH*startRow; c <= ((WIDTH*endRow)/scale)-1; c++) {
            //gör c till ett ställe på koordinatsystemet, zoom förändrar storleken på fraktalen, offset ändrar var den börjar
            Complex ccpx = new Complex ((c%(WIDTH)*0.01/zoom)-3+offsetx,((c/WIDTH)*0.01/zoom)-1.5+offsety);
            Complex z = new Complex(0,0);
            for (int iter = 0; iter <= maxiter; iter++) {
                z = (z.multiply(z)).add(ccpx);
                if (z.re*z.re+z.im*z.im >= 4.0) {
                    screen.drawPixel((c%(WIDTH/scale)),((c/WIDTH)/scale),
                            Color.HSBtoRGB(iter*0.001f+0.63f,iter*0.001f+0.8f,iter*0.01f+0.6f));
                    iter = maxiter;
                } else {
                    screen.drawPixel((c%(WIDTH/scale)),((c/WIDTH)/scale),
                            0x000000);
                }
                iter++;
            }
        }
    }
}
