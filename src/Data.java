import java.awt.image.BufferedImage;

class Data {
    private int[] d;
    private BufferedImage img;

    Data(int[] d, BufferedImage img) {
        this.d = d;
        this.img = img;
    }

    int[] getD() {
        return d;
    }

    public BufferedImage getLabel() {
        return img;
    }
}