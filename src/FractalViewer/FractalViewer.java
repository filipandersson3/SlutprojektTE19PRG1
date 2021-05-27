package FractalViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * An application for viewing the mandelbrot set.
 * Created 2021-05-27
 *
 * @author Filip Andersson
 */
public class FractalViewer extends Canvas implements Runnable{
    private int WIDTH;
    private int HEIGTH;
    private int scale;

    private JFrame frame;
    private String title = "";
    private BufferedImage image;
    private Screen screen;

    private Thread thread;
    private boolean running = false;
    private int fps = 60;
    private int ups = 1;

    // App specific stuff
    int maxiter;
    double zoom = 5.0625;
    double offsetx = -2.2635895612118744;
    double offsety = -1.0764178902267605;
    private Worker worker;
    int startRow;
    int endRow;

    public FractalViewer(int width, int height, int scale) {
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
        screen.fill(0x000000);
    }

    public synchronized void start() {
        running = true;
        do {
            try {
                maxiter = Integer.parseInt(JOptionPane.showInputDialog(frame, "Enter precision level (1-500)"));
            }catch (NumberFormatException e) {
                System.out.println("not an integer");
            }
        }
        while (maxiter <= 0 || maxiter > 500);
        thread = new Thread(this);
        thread.start();
        JOptionPane.showMessageDialog(frame, "Esc to quit, WASD to move, + and - keys to zoom");
    }

    public synchronized void stop() {
        running = false;
        try {
            worker.join();
            thread.join();
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
            createBufferStrategy(1);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, WIDTH, HEIGTH, null);
        g.dispose();
        bs.show();
    }

    public void update() {
        for (int i = 1; i < Runtime.getRuntime().availableProcessors(); i++) { //dela upp bilden i bitar beroende på antalet kärnor
            startRow = ((HEIGTH / (Runtime.getRuntime().availableProcessors()-1)) * (i - 1));
            endRow = ((HEIGTH / (Runtime.getRuntime().availableProcessors()-1)) * i);
            Worker worker = new Worker(WIDTH, HEIGTH, screen, zoom, offsetx, offsety, startRow, endRow, maxiter);
            worker.start();
        }
    }

    public static void main(String[] args) {
        int height = 1080;
        int width = 1920;
        int scale = 1;
        FractalViewer example =  new FractalViewer(width,height,scale);
        example.start();
    }
    private class KL implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {}

        @Override
        public void keyPressed(KeyEvent keyEvent) {
            if (keyEvent.getKeyChar() == 'a') {
                offsetx = offsetx-0.2/zoom;
            }
            if (keyEvent.getKeyChar() == 'd') {
                offsetx = offsetx+0.2/zoom;
            }
            if (keyEvent.getKeyChar() == 'w') {
                offsety = offsety-0.2/zoom;
            }
            if (keyEvent.getKeyChar() == 's') {
                offsety = offsety+0.2/zoom;
            }
            if (keyEvent.getKeyChar() == '+') {
                zoom = zoom*1.5;
                offsetx = offsetx-offsetx/zoom;
                offsety = offsety-offsety/zoom;

            }
            if (keyEvent.getKeyChar() == '-') {
                offsetx = offsetx+offsetx/zoom;
                offsety = offsety+offsety/zoom;
                zoom = zoom/1.5;
            }
            if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                System.exit(0);
            }
        }

        @Override
        public void keyReleased(KeyEvent keyEvent) { //man behöver inte vänta på ups
            update();
        }
    }

}
