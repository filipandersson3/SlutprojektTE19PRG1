package FractalViewer;

/**
 * This is a class
 * Created 2021-03-31
 *
 * @author Magnus Silverdal
 */
public class Screen {
    private int[] pixels;
    private int width;
    private int height;

    public Screen(int[] pixels, int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public void fill(int color) {
        for (int i = 0 ; i < pixels.length ; i++) {
            pixels[i] = color;
        }
    }

    public void drawPixel(int x, int y, int color) {
        pixels[y * width + x] = color;
    }
}