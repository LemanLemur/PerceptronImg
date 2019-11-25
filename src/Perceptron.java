import java.util.Random;

public class Perceptron {
    float[] weights = new float[2501];

    Perceptron(){
        Random rand = new Random();
        for(int i=0; i<weights.length; i++){
            weights[i]= rand.nextFloat()*2-1;
        }
    }

    Perceptron(float[] weights){
        this.weights = weights;
    }

    int guess(int[] inputs, int lol){
        float sum = 0;
        for(int i = 0; i<weights.length; i++){
            sum += inputs[i]*weights[i];
        }

        int output = (int) Math.signum(sum);
        return output;
    }

    void train(int[] inputs, double err){
        for(int i = 0; i < weights.length; i++){
            weights[i] += 0.1 * err * inputs[i];
        }
    }

}
