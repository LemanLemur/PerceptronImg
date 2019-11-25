import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    static Perceptron[] perceprtons = new Perceptron[2500];
    static private int[] imgInput = new int[2501];
    static private int[] imgOutput = new int[2501];
    static private Point[] imgXY = new Point[2501];
    static int picturesCount = 7;
    static int counter = 0;
    static Data[] dataSet;
    private static OutputPanel mousePanel1 = new OutputPanel();
    private static MousePanel mousePanel = new MousePanel();

    static int learnTimes = 1000;
    static int reqTimes = 500;

    public static void main(String[] args) throws IOException {
        imgXYinArray();
        mousePanel.firstPaint();
        mousePanel1.paint();
        dataSet = prepareDataset();
        for (int i = 0; i < perceprtons.length; i++) {
            perceprtons[i] = new Perceptron();
        }
        for (int i = 0; i < 2501; i++) {
            imgInput[i] = 0;
            imgOutput[i] = 0;
        }
        imgInput[0] = 1;
        imgOutput[0] = 1;
        learn();
        createWindow();
    }

    private static void imgXYinArray() {
        for (int j = 0; j < 50; j++) {
            for (int k = 0; k < 50; k++) {
                int position = 1 + j + k * 50;
                imgXY[position] = new Point(k, j);
            }
        }
    }

    static void learn() {
        int life, lastLife;
        Random random = new Random();
        for (int i = 0; i < perceprtons.length; i++) {
            life = 0;
            lastLife = 0;
            Perceptron pocket = new Perceptron(perceprtons[i].weights);
            for (int j = 0; j < learnTimes; j++) {
                Data data = dataSet[random.nextInt(dataSet.length)];
                int[] d = data.getD();
                int O = perceprtons[i].guess(d, 1);

                int err;
                if (d[i + 1] == 0) {
                    err = -1 - O;
                } else {
                    err = 1 - O;
                }

                if (err != 0) {
                    perceprtons[i].train(data.getD(), err);
                    lastLife = life;
                    life = 0;
                } else {
                    life++;
                    if (lastLife < life) {
                        pocket = new Perceptron(perceprtons[i].weights);
                    }
                }
            }
            perceprtons[i] = new Perceptron(pocket.weights);
        }

    }

    private static Data[] prepareDataset() throws IOException {
        Data[] dataSet = new Data[picturesCount];
        for (int i = 1; i <= picturesCount; i++) {
            BufferedImage img = ImageIO.read(new File("C:\\Users\\Lemur\\IdeaProjects\\Perceptron\\src\\dataFiles\\" + i + ".png"));
            int[] d = new int[2501];
            d[0] = 1;

            for (int j = 0; j < 50; j++) {
                for (int k = 0; k < 50; k++) {
                    int position = 1 + j + k * 50;
                    if (new Color(img.getRGB(k, j)).equals(Color.BLACK))
                        d[position] = 1;
                    else
                        d[position] = 0;
                }
            }
            dataSet[i - 1] = new Data(d, img);
        }
        return dataSet;
    }

    static void output() {
        int odp = 0;
        for (int i = 0; i < perceprtons.length; i++) {
            if (perceprtons[i].guess(imgInput, 0) == 1) {
                odp = 1;
            } else {
                odp = 0;
            }
            imgOutput[i + 1] = odp;
        }
        mousePanel1.paint();
    }

    static void outputReq() {
        int odp = 0;
        for (int j = 0; j < reqTimes; j++) {
            for (int i = 0; i < perceprtons.length; i++) {
                if (perceprtons[i].guess(imgOutput.clone(), 0) == 1) {
                    odp = 1;
                } else {
                    odp = 0;
                }
                imgOutput[i + 1] = odp;
            }
        }
        mousePanel1.paint();
    }

    private static void createWindow() {
        JFrame jFrame = new JFrame("Programowanko");

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new CardLayout());
        jFrame.setMinimumSize(new Dimension(500, 500));
        jFrame.setLayout(new GridLayout(2, 4));

        JPanel empty1 = new JPanel();
        JPanel empty2 = new JPanel();

        JButton back = new JButton("Reset");
        back.setMaximumSize(new Dimension(100, 50));
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for (int i = 0; i < 2501; i++) {
                    imgInput[i] = 0;
                }
                mousePanel.firstPaint();
            }
        });
        JButton next = new JButton("-->");
        next.setSize(new Dimension(100, 50));
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                counter++;
                if (counter > picturesCount - 1) counter -= picturesCount;
                imgInput = dataSet[counter].getD().clone();
                mousePanel.imgPaint();
                output();
            }
        });
        JButton req = new JButton("rekurencyjnie");
        req.setSize(new Dimension(100, 50));
        req.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                outputReq();
            }
        });
        JButton out = new JButton("Out");
        out.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                output();
            }
        });

        jFrame.add(mousePanel);
        jFrame.add(empty2);
        jFrame.add(empty1);
        jFrame.add(mousePanel1);
        jFrame.add(back);
        jFrame.add(next);
        jFrame.add(req);
        jFrame.add(out);
        jFrame.setVisible(true);
    }

    /////////////////////////////////////////////////MOUSE PANEL//////////////////////////////////////////////////////////////////////////

    public static class MousePanel extends JPanel {

        private static final int WIDTH = 50;
        private static final int HEIGHT = 50;

        private int x, y;
        BufferedImage bufferedImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);

        ArrayList<Point> points = new ArrayList<Point>();
        boolean isLeftButton = false;

        public MousePanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setSize(new Dimension(WIDTH, HEIGHT));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    super.mousePressed(e);
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        isLeftButton = true;
                    } else {
                        isLeftButton = false;
                    }
                }
            });
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    super.mouseDragged(e);
                    if (isLeftButton && e.getX() <= 50 && e.getY() <= 50) {
                        paint(e.getPoint());

                        paint(new Point(e.getX() - 1, e.getY()));
                        paint(new Point(e.getX() + 1, e.getY()));
                        paint(new Point(e.getX(), e.getY() + 1));
                        paint(new Point(e.getX() - 1, e.getY() + 1));
                        paint(new Point(e.getX() + 1, e.getY() + 1));
                        paint(new Point(e.getX(), e.getY() - 1));
                        paint(new Point(e.getX() - 1, e.getY() - 1));
                        paint(new Point(e.getX() + 1, e.getY() - 1));

                    }
                    if (!isLeftButton && e.getX() <= 50 && e.getY() <= 50) {
                        removePaint(e.getPoint());

                        removePaint(new Point(e.getX() - 1, e.getY()));
                        removePaint(new Point(e.getX() + 1, e.getY()));
                        removePaint(new Point(e.getX(), e.getY() + 1));
                        removePaint(new Point(e.getX() - 1, e.getY() + 1));
                        removePaint(new Point(e.getX() + 1, e.getY() + 1));
                        removePaint(new Point(e.getX(), e.getY() - 1));
                        removePaint(new Point(e.getX() - 1, e.getY() - 1));
                        removePaint(new Point(e.getX() + 1, e.getY() - 1));
                    }
                    output();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bufferedImage, 0, 0, 50, 50, this);
        }

        private void paint(Point p) {
            if (p.x < 50 && p.x >= 0 && p.y < 50 && p.y >= 0) {
                bufferedImage.setRGB(p.x, p.y, Color.BLACK.getRGB());
                imgInput[1 + p.y + p.x * 50] = 1;
            }
            this.repaint();
        }

        private void firstPaint() {
            for (int i = 1; i < imgInput.length; i++) {
                if (imgInput[i] == 1) {
                    bufferedImage.setRGB(imgXY[i].x, imgXY[i].y, Color.BLACK.getRGB());
                } else {
                    bufferedImage.setRGB(imgXY[i].x, imgXY[i].y, Color.WHITE.getRGB());
                }
            }
            this.repaint();
        }

        private void imgPaint() {
            for (int i = 1; i < imgInput.length; i++) {
                if (imgInput[i] == 1) {
                    bufferedImage.setRGB(imgXY[i].x, imgXY[i].y, Color.BLACK.getRGB());
                } else {
                    bufferedImage.setRGB(imgXY[i].x, imgXY[i].y, Color.WHITE.getRGB());
                }
            }
            this.repaint();
        }

        private void removePaint(Point p) {
            if (p.x < 50 && p.x >= 0 && p.y < 50 && p.y >= 0) {
                bufferedImage.setRGB(p.x, p.y, Color.WHITE.getRGB());
                imgInput[1 + p.y + p.x * 50] = 0;
            }
            this.repaint();
        }

        private void drawRectangles(Graphics2D g2d) {
            int x, y;
            for (Point p : points) {
                x = (int) p.getX();
                y = (int) p.getY();
                g2d.fillRect(x, y, 1, 1);
            }
        }

    }

    public static class OutputPanel extends JPanel {

        private static final int WIDTH = 50;
        private static final int HEIGHT = 50;

        private int x, y;
        BufferedImage bufferedImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);

        ArrayList<Point> points = new ArrayList<Point>();
        boolean isLeftButton = false;

        public OutputPanel() {
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setSize(new Dimension(WIDTH, HEIGHT));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(bufferedImage, 0, 0, 50, 50, this);
        }

        private void paint() {
            for (int i = 1; i < imgOutput.length; i++) {
                if (imgOutput[i] == 1) {
                    bufferedImage.setRGB(imgXY[i].x, imgXY[i].y, Color.BLACK.getRGB());
                } else {
                    bufferedImage.setRGB(imgXY[i].x, imgXY[i].y, Color.WHITE.getRGB());
                }
            }
            this.repaint();
        }

        private void drawRectangles(Graphics2D g2d) {
            int x, y;
            for (Point p : points) {
                x = (int) p.getX();
                y = (int) p.getY();
                g2d.fillRect(x, y, 1, 1);
            }
        }

    }

}
