package dtw;

import com.github.sh0nk.matplotlib4j.Plot;
import lombok.SneakyThrows;
import model.Point;
import utility.FileUtility;
import utility.ProcessingUtility;

import java.util.*;

import static utility.MathUtility.*;

public class BaseAlgorithm {

    private static final Random random = new Random();

    @SneakyThrows
    public static void main(String[] args) {
        var trajectories = extractTrajectories(args);
        var randomIndex = generateIndexFrom(trajectories);
        var anotherRandomIndex = generateIndexFrom(trajectories);
        var sampleTrajectory = trajectories.get(13);
        var randomTrajectory = trajectories.get(1);
        var sampleWithNoise = addNoise(sampleTrajectory, 0.0005);
        var randomBandwidth = random.nextInt(20, 78);
        System.out.println("Sample size: " + sampleTrajectory.size());
        System.out.println("Random size: " + randomTrajectory.size());
        System.out.println("Bandwidth: " + randomBandwidth);

        Map<String, Integer> sakoeChibaMap = createSakoeChibaMap(sampleTrajectory.size(), randomTrajectory.size(), randomBandwidth);
        List<Integer> lowerBounds = calculateLowerBound(sakoeChibaMap, sampleTrajectory.size());
        List<Integer> upperBounds = calculateUpperBound(sakoeChibaMap, sampleTrajectory.size());
        List<List<Integer>> bandwidthIndeces = new ArrayList<>();
        bandwidthIndeces.add(lowerBounds);
        bandwidthIndeces.add(upperBounds);
        System.out.println(lowerBounds);
        System.out.println(upperBounds);
        // np.clip(bandwidthIndeces[:, :sampleTrajectory.size()], 0, randomTrajectory.size())


        double[][] distanceMatrix = buildDistanceMatrix(sampleTrajectory, randomTrajectory);
//        ProcessingUtility.prettyPrintMatrix(distanceMatrix, ">>> Cumulative distance matrix:");

        var indicesOfOptimalPath = warpingPath(
                sampleTrajectory.size() - 1,
                randomTrajectory.size() - 1,
                distanceMatrix
        );

        List<Integer> xIndices = new ArrayList<>();
        List<Integer> yIndices = new ArrayList<>();
        for (List<Integer> indexPair : indicesOfOptimalPath) {
            xIndices.add(indexPair.get(0));
            yIndices.add(indexPair.get(1));
        }
        Plot plt = Plot.create();
        plt.subplot(2, 1, 1);
        plt.plot().add(xIndices, yIndices, "o").label("Optimal warping path");
        plt.xlim(-1, sampleTrajectory.size());
        plt.ylim(-1, randomTrajectory.size());
        plt.legend().loc("best");

        List<Double> distances = new ArrayList<>();
        for (double[] rows : distanceMatrix) {
            for (double number : rows) {
                distances.add(number);
            }
        }

        plt.subplot(2, 1, 2);
        plt.plot().add(xIndices, yIndices, "o").label("Distance matrix weighted by greyscale");

        plt.show();
    }

    private static List<List<Integer>> warpingPath(int xDimension, int yDimension, double[][] distanceMatrix) {
        List<List<Integer>> indexPairs = new ArrayList<>();
        List<Integer> iteratedIndexPair = new ArrayList<>();
        indexPairs.add(List.of(xDimension, yDimension));
        while (xDimension > 0 || yDimension > 0) {
            iteratedIndexPair.clear();
            if (xDimension == 0) {
                iteratedIndexPair.add(0);
                iteratedIndexPair.add(yDimension - 1);
            } else if (yDimension == 0) {
                iteratedIndexPair.add(xDimension - 1);
                iteratedIndexPair.add(0);
            } else {
                double smallest = minimum(
                        distanceMatrix[xDimension - 1][yDimension - 1],
                        distanceMatrix[xDimension - 1][yDimension],
                        distanceMatrix[xDimension][yDimension - 1]
                );
                if (smallest == distanceMatrix[xDimension - 1][yDimension - 1]) {
                    iteratedIndexPair.add(xDimension - 1);
                    iteratedIndexPair.add(yDimension - 1);
                } else if (smallest == distanceMatrix[xDimension - 1][yDimension]) {
                    iteratedIndexPair.add(xDimension - 1);
                    iteratedIndexPair.add(yDimension);
                } else {
                    iteratedIndexPair.add(xDimension);
                    iteratedIndexPair.add(yDimension - 1);
                }
            }
            xDimension = iteratedIndexPair.get(0);
            yDimension = iteratedIndexPair.get(1);
            indexPairs.add(List.of(xDimension, yDimension));
        }
        Collections.reverse(indexPairs);
        return indexPairs;
    }

    private static double[][] buildDistanceMatrix(List<Point> queryTrajectory, List<Point> compareTrajectory) {
        int rowSize = queryTrajectory.size();
        int columnSize = compareTrajectory.size();
        double[][] distanceMatrix = new double[rowSize][columnSize];

        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < columnSize; j++) {
                distanceMatrix[i][j] = round(getDistance(queryTrajectory.get(i), compareTrajectory.get(j)), 4);
            }
        }
        return distanceMatrix;
    }

    private static List<List<Point>> extractTrajectories(String[] runtimeParams) {
        String fileToRead = runtimeParams[0];
        boolean ignoreHeader = Boolean.parseBoolean(runtimeParams[1]);

        var file = FileUtility.readFile(fileToRead, ignoreHeader);

        return ProcessingUtility.transformFileToTrajectories(file);
    }

}
