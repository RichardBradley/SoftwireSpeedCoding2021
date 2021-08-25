package q1;

import com.google.common.base.Stopwatch;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Q1Main {

    public static void main(String[] args) throws Exception {
        Stopwatch sw = Stopwatch.createStarted();
        try {
            new Q1Main().run();
        } finally {
            System.out.println("Took " + sw.elapsed(TimeUnit.MILLISECONDS) + "ms");
        }
    }

    private void run() throws Exception {
        Map<Integer, Double> probBySum = new HashMap<>();
        probBySum.put(0, 1.0);
        probBySum = updateProbs(10, 4, probBySum);
        probBySum = updateProbs(35, 6, probBySum);
        probBySum = updateProbs(10, 8, probBySum);
        probBySum = updateProbs(21, 10, probBySum);
        probBySum = updateProbs(11, 12, probBySum);
        probBySum = updateProbs(12, 20, probBySum);
        probBySum = updateProbs(1, 30, probBySum);
        System.out.println("500 = " + probBySum.get(500));
    }

    private Map<Integer, Double> updateProbs(int count, int numSides, Map<Integer, Double> probBySum) {
        for (int n = 0; n < count; n++) {
            Map<Integer, Double> newProbBySum = new HashMap<>();
            for (Map.Entry<Integer, Double> entry : probBySum.entrySet()) {
                int prevSum = entry.getKey();
                double nextProbPerFace = entry.getValue() / numSides;
                for (int roll = 1; roll <= numSides; roll++) {
                    newProbBySum.compute(prevSum + roll, (k, v) -> (v == null ? 0.0 : v) + nextProbPerFace);
                }
            }
            probBySum = newProbBySum;
        }
        return probBySum;
    }
}
