package ScreenRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * A system for drawing pixelgraphics to the screen using native Java.
 * Created 2021-03-31
 *
 * @author Magnus Silverdal
 */
public class ScreenRenderer extends Canvas implements Runnable{
    private int WIDTH;
    private int HEIGTH;
    private int scale;

    private JFrame frame;
    private String title = "";
    private BufferedImage image;
    private Screen screen;

    private Thread thread;
    private boolean running = false;
    private int fps = 1;
    private int ups = 1;

    // App specific stuff
    int maxiter = 200;
    double zoom = 1;
    double offsetx = -3.5;
    double offsety = -1.9;
    private Worker worker;
    Worker[] workers = new Worker[1000];
    int startRow;
    int endRow;

    public ScreenRenderer(int width, int height, int scale) {
        // Screen data
        this.WIDTH = width*scale;
        this.HEIGTH = height*scale;
        this.scale = scale;
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        screen = new Screen(((DataBufferInt) image.getRaster().getDataBuffer()).getData(),image.getWidth(),
                image.getHeight());
        // Frame data
        setPreferredSize(new Dimension(WIDTH, HEIGTH));
        frame = new JFrame(title);
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.add(this);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.addKeyListener(new KL());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocus();

        // App specific stuff
        screen.fill(0xFFFFFF);

    }

    public synchronized void start() {
        running = true;
        Worker worker = new Worker(WIDTH,HEIGTH,scale,screen,zoom,offsetx,offsety,0,1079);
        worker.start();
        try {
            worker.setPriority( Thread.currentThread().getPriority() - 1 );
        }
        catch (Exception e) {
            System.out.println("Error: Can't set thread priority: " + e);
        }
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            worker.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        double nsFPS = 1000000000.0 / fps;
        double nsUPS = 1000000000.0 / ups;
        double deltaFPS = 0;
        double deltaUPS = 0;
        int frames = 0;
        int updates = 0;
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();

        while (running) {
            long now = System.nanoTime();
            deltaFPS += (now - lastTime) / nsFPS;
            deltaUPS += (now - lastTime) / nsUPS;
            lastTime = now;

            while(deltaUPS >= 1) {
                update();
                updates++;
                deltaUPS--;
            }

            if (deltaFPS >= 1) {
                render();
                frames++;
                deltaFPS--;
            }

            if(System.currentTimeMillis() - timer >= 1000) {
                timer += 1000;
                frame.setTitle(this.title + "  |  " + updates + " ups, " + frames + " fps");
                frames = 0;
                updates = 0;
            }
        }
        stop();
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH, HEIGTH, null);
        g.dispose();
        bs.show();
    }

    public void update() {
        // http://math.hws.edu/eck/cs124/javanotes7/c12/s2.html#threads.2.4 för fler trådar
        /*for (int c = 0; c <= ((WIDTH*HEIGTH)/scale)-1; c++) {
            //gör c till ett ställe på koordinatsystemet, zoom förändrar storleken på fraktalen, offset ändrar var den börjar
            Complex ccpx = new Complex (((c%(WIDTH/scale))*0.01/zoom)-3+offsetx,(((c/WIDTH)/scale)*0.01/zoom)-1.5+offsety);
            Complex z = new Complex(0,0);
            for (int iter = 0; iter <= maxiter; iter++) {
                z = (z.multiply(z)).add(ccpx);
                if (z.re*z.re+z.im*z.im >= 4.0) {
                    getScreen().drawPixel((c%(WIDTH/scale)),((c/WIDTH)/scale),
                            Color.HSBtoRGB(iter*0.001f+0.63f,iter*0.001f+0.8f,iter*0.01f+0.6f));
                    iter = maxiter;
                } else {
                    getScreen().drawPixel((c%(WIDTH/scale)),((c/WIDTH)/scale),
                            0x000000);
                }
                iter++;
            }
        }
        */
    }

    public Screen getScreen() {
        return screen;
    }

    public static void main(String[] args) {
        //1920x1080 with 4x4 pixels
        int height = 1080;
        int width = 1920;
        int scale = 1;
        ScreenRenderer example =  new ScreenRenderer(width,height,scale);
        example.start();
    }
    private class KL implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == 'a') {
                offsetx = offsetx-0.2;
            }
            if (keyEvent.getKeyChar() == 'd') {
                offsetx = offsetx+0.2;
            }
            if (keyEvent.getKeyChar() == 'w') {
                offsety = offsety-0.2;
            }
            if (keyEvent.getKeyChar() == 's') {
                offsety = offsety+0.2;
            }
            if (keyEvent.getKeyChar() == '+') {
                offsetx = offsetx+zoom;
                offsety = offsety+zoom;
                zoom = zoom*1.5;
            }
            if (keyEvent.getKeyChar() == '-') {
                zoom = zoom/1.5;
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) {
            for (int i = 1; i < Runtime.getRuntime().availableProcessors(); i++) {
                startRow = ((HEIGTH / Runtime.getRuntime().availableProcessors()) * (i - 1));
                endRow = ((HEIGTH / Runtime.getRuntime().availableProcessors()) * i);
                Worker worker = new Worker(WIDTH, HEIGTH, scale, screen, zoom, offsetx, offsety, startRow, endRow);
                worker.start();
            }
        }
    }

}
