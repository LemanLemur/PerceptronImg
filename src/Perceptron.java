import java.util.Random;

public class Perceptron {
    float[] weights = new float[35];
    Random rand = new Random();

    Perceptron(){
        for(int i=0; i<weights.length; i++){
            weights[i]= rand.nextFloat()*2-1;
        }
    }

    int guess(int[] inputs){
        float sum = 0;
        for(int i = 0; i<weights.length; i++){
            sum += inputs[i]*weights[i];
        }
        int output = (int) Math.signum(sum);
        return output;
    }

    void train(int[] inputs, double err){
        int guess = guess(inputs);

        for(int i = 0; i < weights.length; i++){
            weights[i] += err * inputs[i] * 0.1;
        }
    }

}
