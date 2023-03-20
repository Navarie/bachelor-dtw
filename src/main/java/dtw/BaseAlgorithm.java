package dtw;

import com.github.sh0nk.matplotlib4j.Plot;
import lombok.SneakyThrows;
import model.Point;
import utility.FileUtility;

import java.util.*;

import static utility.LogUtility.*;
import static utility.MathUtility.*;
import static utility.ProcessingUtility.*;

public class BaseAlgorithm {

    private static final Random random = new Random();

    @SneakyThrows
    public static void main(String[] args) {
        var trajectories = extractTrajectories(args);
        var randomIndex = generateIndexFrom(trajectories);
        var anotherRandomIndex = generateIndexFrom(trajectories);
        var sampleTrajectory = trajectories.get(2);
        var randomTrajectory = trajectories.get(3);
        var sampleWithNoise = addNoise(sampleTrajectory, 0.0005);

//        int randomBandwidth = random.nextInt(20, 30);
        double randomBandwidth = random.nextDouble(0, 1);

        var constraintRegion = computeConstraintRegion(sampleTrajectory.size(), randomTrajectory.size(), randomBandwidth);
        var lowerBounds = constraintRegion.get(0);
        var upperBounds = constraintRegion.get(1);

        if (isDtwPrinting()) {
            System.out.println("Sample size: " + sampleTrajectory.size());
            System.out.println("Random size: " + randomTrajectory.size());
            System.out.println("Bandwidth: " + randomBandwidth);
            System.out.println("lowerBounds: \n" + lowerBounds);
            System.out.println("upperBounds: \n" + upperBounds);
        }

        double[][] distanceMatrix = buildDistanceMatrix(sampleTrajectory, randomTrajectory, constraintRegion);
//        prettyPrintMatrix(distanceMatrix, ">>> Cumulative distance matrix:");

//        double[] slicedArray = Arrays.stream(distanceMatrix[0], 0, 16).toArray();

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
        plt.plot().add(xIndices, yIndices, "o");
        plt.legend().loc("best");
        plt.title("Optimal warping path");

        List<Double> distances = new ArrayList<>();
        for (double[] rows : distanceMatrix) {
            for (double number : rows) {
                distances.add(number);
            }
        }

//        List<Double> x = NumpyUtils.linspace(0, sampleTrajectory.size(), 100);
//        List<Double> y = NumpyUtils.linspace(0, randomTrajectory.size(), 100);
//        NumpyUtils.Grid<Double> grid = NumpyUtils.meshgrid(x, y);
//        List<List<Double>> cCalced = grid.calcZ((xi, yj) -> Math.sqrt(xi * xi + yj * yj));
//
//        plt.pcolor().add(x, y, cCalced).cmap("plt.cm.Greys");

//        plt.subplot(2, 1, 2);
//        plt.hist().add(xIndices, yIndices, "o").label("Distance matrix weighted by greyscale");
//        plt.xlim(-1, sampleTrajectory.size());
//        plt.ylim(-1, randomTrajectory.size());

//        plt.show();
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

    private static double[][] buildDistanceMatrix(
            List<Point> queryTrajectory,
            List<Point> compareTrajectory,
            List<List<Integer>> constraintRegion) {
        int rowSize = queryTrajectory.size();
        int columnSize = compareTrajectory.size();
        double[][] distanceMatrix = new double[rowSize][columnSize];

        for (int currentRow = 0; currentRow < rowSize; currentRow++) {
            for (int currentColumn = 0; currentColumn < columnSize; currentColumn++) {

                boolean withinConstraintRegion = isWithinConstraintRegion(constraintRegion, currentRow, currentColumn);
                if (withinConstraintRegion) {
                    distanceMatrix[currentRow][currentColumn] = round(getDistance(
                            queryTrajectory.get(currentRow),
                            compareTrajectory.get(currentColumn)),
                            4
                    );
                } else {
                    distanceMatrix[currentRow][currentColumn] = Integer.MAX_VALUE;
                }
            }
        }
        return distanceMatrix;
    }

    private static boolean isWithinConstraintRegion(List<List<Integer>> constraintRegion, int row, int column) {
        var lowerBound = constraintRegion.get(0).get(row);
        var upperBound = constraintRegion.get(1).get(row);

        return lowerBound <= column && column <= upperBound;
    }

    private static List<List<Point>> extractTrajectories(String[] runtimeParams) {
        String fileToRead = runtimeParams[0];
        boolean ignoreHeader = Boolean.parseBoolean(runtimeParams[1]);

        var file = FileUtility.readFile(fileToRead, ignoreHeader);

        return transformFileToTrajectories(file);
    }

}
