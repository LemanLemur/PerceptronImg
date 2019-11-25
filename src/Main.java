import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    static Perceptron[] perceprtons = new Perceptron[11];
    static JLabel score = new JLabel("Wynik: ");
    static JLabel inLearning = new JLabel("Uczę Się\n");
    static JLabel errorNumber = new JLabel("Procent błędów: ");
    static private int[] buttonInput = new int[36];
    static Data[] data;
    static int rn = 10000;
    static int en = 0;

    public static void main(String[] args) {
        try {
            data = prepareDataset();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < 32; i++) {
            buttonInput[i] = 0;
        }
        try {
            learn();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        createWindow();
    }

    static void learn() throws CloneNotSupportedException {
        int life, lastLife;
        Random random = new Random();
        for (int i = 0; i < perceprtons.length; i++) {
            life = 0;
            lastLife = 0;
            Perceptron pocket = perceprtons[i];
            for (int j = 0; j < 1000; j++) {
                Data d = data[random.nextInt(data.length)];
                int O = perceprtons[i].guess(d.getD());
                int T;
                if (i == d.getLabel()) {
                    T = 1;
                } else {
                    T = -1;
                }
                int err = T - O;

                if (err != 0) {
                    life = 0;
                    en++;
                    perceprtons[i].train(d.getD(), err);
                } else {
                    life++;
                    if (lastLife < life) {
                        pocket = perceprtons[i];
                    }
                }
            }
            perceprtons[i] = pocket;
        }
        inLearning.setText("Nauczony!\n");
        float errorPercent = (float)en / (float)rn * (float)perceprtons.length;
        errorNumber.setText("Procent błędów: " + errorPercent + "%");
    }

    static void output() {
        String s = "Wynik: ";
        for (int i = 0; i < perceprtons.length; i++) {
            if (perceprtons[i].guess(buttonInput) == 1) {
                if(i == 10){
                    s += "a, ";
                }else{
                    s += i + ", ";
                }

            }
        }
        score.setText(s);
    }


    private static Data[] prepareDataset() throws IOException {
        List<Data> dataSet = new ArrayList<>();

        for (int i = 0; i < perceprtons.length; i++) {
            File file = new File("C:\\Users\\Lemur\\IdeaProjects\\Perceptron\\src\\dataFiles\\" + i + ".txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                int[] data = new int[buttonInput.length];
                data[0] = 1;
                int l = 1;
                for (int j = 0; j < st.length(); j++) {
                    char c = st.charAt(j);
                    if (c == '1' || c == '0') {
                        data[l] = c - '0';
                        l++;
                    }
                }
                dataSet.add(new Data(data, i));
            }

            perceprtons[i] = new Perceptron();
        }

        return dataSet.toArray(new Data[0]);
    }

    private static void createWindow() {
        JFrame jFrame = new JFrame("Programowanko");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setLayout(new CardLayout());
        jFrame.setMinimumSize(new Dimension(500, 500));

        JPanel jPanel = new JPanel();
        jPanel.add(inLearning);

        JPanel grid = new JPanel();
        grid.setSize(new Dimension(500, 700));
        grid.setLayout(new GridLayout(7, 5));

        JPanel ans = new JPanel();
        ans.setMinimumSize(new Dimension(200, 200));
        ans.setLayout(new GridLayout(2, 1));
//        ans.add(errorNumber);
        ans.add(score);

        buttonInput[0] = 1;
        for (int i = 1; i < buttonInput.length; i++) {
            JButton button = new JButton();
            int buttonNum = i;
            button.addActionListener(ActionEvent -> {
                if (button.getBackground() == Color.GREEN) {
                    button.setBackground(Color.RED);
                    buttonInput[buttonNum] = 0;
                    output();
                } else {
                    button.setBackground(Color.GREEN);
                    buttonInput[buttonNum] = 1;
                    output();
                }
            });

            button.setBackground(Color.RED);
            button.setMinimumSize(new Dimension(100, 100));
            Border line = new LineBorder(Color.BLACK);
            Border margin = new EmptyBorder(25, 25, 25, 25);
            Border compound = new CompoundBorder(line, margin);
            button.setBorder(compound);
            grid.add(button);
        }

        jPanel.add(grid);
        jPanel.add(ans);
        jFrame.add(jPanel);
        jFrame.setVisible(true);
    }

}
