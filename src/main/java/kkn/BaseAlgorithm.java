package kkn;

import lombok.SneakyThrows;

import java.util.*;

import static utility.LogUtility.*;
import static utility.MathUtility.*;
import static utility.MathUtility.computeSumQuantityEqualSignsUpToDepth;
import static utility.ProcessingUtility.*;

public class BaseAlgorithm {

    private static final Random random = new Random();

    @SneakyThrows
    public static void main(String[] args) {

        //region Create mock lists
        List<Double> Q = new ArrayList<>();
        Q.add(2.0);
        Q.add(4.0);
        Q.add(6.0);
        Q.add(8.0);
        Q.add(3.0);
        Q.add(5.0);
        Q.add(7.0);
        Q.add(5.0);
        List<Double> P1 = new ArrayList<>();
        P1.add(4.0);
        P1.add(8.0);
        P1.add(5.0);
        P1.add(7.0);
        P1.add(9.0);
        P1.add(1.0);
        P1.add(2.0);
        P1.add(8.0);
        List<Double> P2 = new ArrayList<>();
        P2.add(2.0);
        P2.add(6.0);
        P2.add(5.0);
        P2.add(7.0);
        P2.add(4.0);
        P2.add(6.0);
        P2.add(8.0);
        P2.add(4.0);
        List<Double> P3 = generateRandomList();
        List<Double> P4 = generateRandomList();
        List<Double> P5 = generateRandomList();
        List<Double> P6 = generateRandomList();
        List<Double> P7 = generateRandomList();
        List<Double> P8 = generateRandomList();
        List<Double> P9 = generateRandomList();
        List<Double> P10 = generateRandomList();

        List<Double> queryHaarArray = discreteHaarWaveletTransform(new ArrayList<>(Q));
        List<Double> p1HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P1));
        List<Double> p2HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P2));
        List<Double> p3HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P3));
        List<Double> p4HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P4));
        List<Double> p5HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P5));
        List<Double> p6HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P6));
        List<Double> p7HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P7));
        List<Double> p8HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P8));
        List<Double> p9HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P9));
        List<Double> p10HaarArray = discreteHaarWaveletTransform(new ArrayList<>(P10));
        //endregion

        //region Create data set object
        List<List<Double>> dataSetWaveletTransform = new ArrayList<>();
        dataSetWaveletTransform.add(P1);
        dataSetWaveletTransform.add(P2);
        dataSetWaveletTransform.add(P3);
        dataSetWaveletTransform.add(P4);
        dataSetWaveletTransform.add(P5);
        dataSetWaveletTransform.add(P6);
        dataSetWaveletTransform.add(P7);
        dataSetWaveletTransform.add(P8);
        dataSetWaveletTransform.add(P9);
        dataSetWaveletTransform.add(P10);
        //endregion

        if (isDebugging()) {
            printCurrentMethod();
            printIndentedLine("Q: " + queryHaarArray);
            printIndentedLine("P1: " + p1HaarArray);
            printIndentedLine("P2: " + p2HaarArray);
            printIndentedLine("P3: " + p3HaarArray);
            printIndentedLine("P4: " + p4HaarArray);
            printIndentedLine("P5: " + p5HaarArray);
            printIndentedLine("P6: " + p6HaarArray);
            printIndentedLine("P7: " + p7HaarArray);
            printIndentedLine("P8: " + p8HaarArray);
            printIndentedLine("P9: " + p9HaarArray);
            printIndentedLine("P10: " + p10HaarArray);
        }
        if (isDebugging()) {
            double distanceP1 = haarTreeEuclideanDistance(p1HaarArray, queryHaarArray, 3);
            double distanceP2 = haarTreeEuclideanDistance(p2HaarArray, queryHaarArray, 3);
            double distanceP1Depth3 = bottomUpHaarTreeEuclideanDistance(p1HaarArray, queryHaarArray, 3);
            double distanceP1Depth2 = bottomUpHaarTreeEuclideanDistance(p1HaarArray, queryHaarArray, 2);
            double distanceP2Depth3 = bottomUpHaarTreeEuclideanDistance(p2HaarArray, queryHaarArray, 3);
            double distanceP2Depth2 = bottomUpHaarTreeEuclideanDistance(p2HaarArray, queryHaarArray, 2);

            System.out.println("D(P1, Q) = " + distanceP1);
            System.out.println("D(P2, Q) = " + distanceP2);
            System.out.println("D(P1, Q) at starting level 3 is " + distanceP1Depth3);
            System.out.println("D(P1, Q) at starting level 2 is " + distanceP1Depth2);
            System.out.println("D(P2, Q) at starting level 3 is " + distanceP2Depth3);
            System.out.println("D(P2, Q) at starting level 2 is " + distanceP2Depth2 + "\n");
        }

        double sumQuantityDepth3Query = preComputeSumToDepth(queryHaarArray, 3);
        double sumQuantityDepth3P1 = preComputeSumToDepth(p1HaarArray, 3);
        double sumQuantityDepth3P2 = preComputeSumToDepth(p2HaarArray, 3);
        double sumQuantityDepth2Query = bottomUpPreComputeSumToDepth(queryHaarArray, 2);
        double sumQuantityDepth2P1 = bottomUpPreComputeSumToDepth(p1HaarArray, 2);
        double sumQuantityDepth2P2 = bottomUpPreComputeSumToDepth(p2HaarArray, 2);

        double sumQuantityP1 = preComputeSumQuantity(p1HaarArray, 3);
        double sumQuantityP2 = preComputeSumQuantity(p2HaarArray, 3);

        double sumQuantityEqualP1Depth3 = computeSumQuantityEqualSignsUpToDepth(queryHaarArray, p1HaarArray, 3);
        double sumQuantityEqualP1Depth2 = computeSumQuantityEqualSignsUpToDepth(queryHaarArray, p1HaarArray, 2);
        double sumQuantityEqualP2Depth3 = computeSumQuantityEqualSignsUpToDepth(queryHaarArray, p2HaarArray, 3);
        double sumQuantityEqualP2Depth2 = computeSumQuantityEqualSignsUpToDepth(queryHaarArray, p2HaarArray, 2);
        double sumQuantityOppositeP1Depth3 = computeSumQuantityOppositeSignsUpToDepth(queryHaarArray, p1HaarArray, 3);
        double sumQuantityOppositeP1Depth2 = computeSumQuantityOppositeSignsUpToDepth(queryHaarArray, p1HaarArray, 2);
        double sumQuantityOppositeP2Depth3 = computeSumQuantityOppositeSignsUpToDepth(queryHaarArray, p2HaarArray, 3);
        double sumQuantityOppositeP2Depth2 = computeSumQuantityOppositeSignsUpToDepth(queryHaarArray, p2HaarArray, 2);

//        double lowerBoundsP1Depth3 = computeLowerBounds(queryHaarArray, p1HaarArray, 3);
//        double lowerBoundsP1Depth2 = computeLowerBounds(queryHaarArray, p1HaarArray, 2);
//        double lowerBoundsP1Depth1 = computeLowerBounds(queryHaarArray, p1HaarArray, 1);
//        double lowerBoundsP2Depth3 = computeLowerBounds(queryHaarArray, p2HaarArray, 3);
//        double lowerBoundsP2Depth2 = computeLowerBounds(queryHaarArray, p2HaarArray, 2);
//        double lowerBoundsP2Depth1 = computeLowerBounds(queryHaarArray, p2HaarArray, 1);
//
//        double upperBoundsP1Depth3 = computeUpperBounds(queryHaarArray, p1HaarArray, 3);
//        double upperBoundsP1Depth2 = computeUpperBounds(queryHaarArray, p1HaarArray, 2);
//        double upperBoundsP1Depth1 = computeUpperBounds(queryHaarArray, p1HaarArray, 1);
//        double upperBoundsP2Depth3 = computeUpperBounds(queryHaarArray, p2HaarArray, 3);
//        double upperBoundsP2Depth2 = computeUpperBounds(queryHaarArray, p2HaarArray, 2);
//        double upperBoundsP2Depth1 = computeUpperBounds(queryHaarArray, p2HaarArray, 1);

        List<List<Double>> kNearestNeighbors = stepwiseSimilaritySearch(dataSetWaveletTransform, queryHaarArray, 3);
    }

    private static List<List<Double>> stepwiseSimilaritySearch(List<List<Double>> candidateSet, List<Double> queryHaarSeries, int nearestNeighbours) {
        printCurrentMethod();

        // Step 1: Compute Σ_lq
        double sigma_lq = computeSigmaForEveryLevel(queryHaarSeries);

        // Step 2: Initialization
        int r = getBinaryExponent(queryHaarSeries);
        printIndentedLine("r := " + r);
        printIndentedLine("Initialized candidate set C := all records.");
        printIndentedLine(String.valueOf(candidateSet));

        // Step 3, 4: Compute Σ_lp, Σ_p, Σ_o_q, Σ_e_q for each record
        List<Double> sigma_lp_list = new ArrayList<>();
        List<Double> sigma_p_list = new ArrayList<>();
        List<Double> sigma_o_q_list = new ArrayList<>();
        List<Double> sigma_e_q_list = new ArrayList<>();
        for (List<Double> record : candidateSet) {
            sigma_lp_list.add(computeSigmaForEveryLevel(record));
            sigma_p_list.add(sumOfSquares(record));
            sigma_o_q_list.add(computeSigmaOppositeSignsForEveryLevel(queryHaarSeries, record));
            sigma_e_q_list.add(computeSigmaEqualSignsForEveryLevel(queryHaarSeries, record));
        }
        printIndentedLine("sigma_lp_list: " + sigma_lp_list);
        printIndentedLine("sigma_p_list: " + sigma_p_list);
        printIndentedLine("sigma_o_q_list: " + sigma_o_q_list);
        printIndentedLine("sigma_e_q_list: " + sigma_e_q_list);

        // Step 5: Begin looping
        List<Double> lowerBounds = new ArrayList<>();
        List<Double> upperBounds = new ArrayList<>();
        int candidateSize = candidateSet.size();
        while (candidateSize > nearestNeighbours) {
            System.out.println("\nStep r := " + r);

            // Step 6: Read records at level r
            List<Integer> recordIndices = getBottomUpIndices(r, queryHaarSeries);
            if (recordIndices.size() < 2) {
                throw new InputMismatchException("indices of Haar tree at level " + r + " had size < 2");
            }
            List<List<Double>> recordsAtLevel = candidateSet.stream()
                    .toList()
                    .subList(recordIndices.get(0), recordIndices.get(recordIndices.size() - 1) + 1);
            printIndentedLine("Record indices at depth " + r + " are " + recordIndices);
            printIndentedLine("Records:");
            printIndentedLine(String.valueOf(recordsAtLevel));

            // Step 7: Calculate sigma-up-to-level-r quantities, sigma query quantities
            List<Double> D_r_k = new ArrayList<>();
            List<Double> sigma_r_lp_list = new ArrayList<>();
            List<Double> sigma_r_p_list = new ArrayList<>();
            List<Double> sigma_r_o_q_list = new ArrayList<>();
            List<Double> sigma_r_e_q_list = new ArrayList<>();
            double sigma_r_lq = bottomUpPreComputeSumToDepth(queryHaarSeries, r);
            for (List<Double> record : recordsAtLevel) {
                D_r_k.add(bottomUpHaarTreeEuclideanDistance(record, queryHaarSeries, r));
                sigma_r_lp_list.add(bottomUpPreComputeSumToDepth(record, r));
                sigma_r_p_list.add(preComputeSumQuantity(record, r));
                sigma_r_o_q_list.add(computeSumQuantityOppositeSignsUpToDepth(queryHaarSeries, record, r));
                sigma_r_e_q_list.add(computeSumQuantityEqualSignsUpToDepth(queryHaarSeries, record, r));
            }
            printIndentedLine("D_r_k: " + D_r_k);
            printIndentedLine("sigma_r_lp: " + sigma_r_lp_list);
            printIndentedLine("sigma_r_lq: " + sigma_r_lq);
            printIndentedLine("sigma_r_p: " + sigma_r_p_list);
            printIndentedLine("sigma_r_o_q: " + sigma_r_o_q_list);
            printIndentedLine("sigma_r_e_q: " + sigma_r_e_q_list);

            // Step 8: Calculate double bounds for each candidate
            for (List<Double> record : recordsAtLevel) {
                lowerBounds.add(computeLowerBounds(queryHaarSeries, record, r));
                upperBounds.add(computeUpperBounds(queryHaarSeries, record, r));
            }
            printIndentedLine("lower bounds for depth " + r + ": " + lowerBounds);
            printIndentedLine("upper bounds for depth " + r + ": " + upperBounds);

            // Step 9: Find k-th upper bound value in C
            if (upperBounds.size() >= nearestNeighbours) {
                double upperBound = upperBounds.get(nearestNeighbours);
                printIndentedLine("k-th upper bound is " + upperBound);

                // Step 10: Prune records having lower bound greater than the above upper bound in C
                for (int i = 0; i < lowerBounds.size(); i++) {
                    if (lowerBounds.get(i) > upperBound) {
                        printIndentedLine(lowerBounds.get(i) + " was bigger than " + upperBound + "!");
                        printIndentedLine("Pruning record " + candidateSet.get(i) + "...");
                        candidateSet.set(i, null);
                        candidateSize--;
                    }
                }
            }

            if (r > 1) {
                r--;
            }
        }
        // Step 11: Cleanup
        candidateSet.removeIf(Objects::isNull);
        printIfLogging("\nFinal candidate set is: " + candidateSet);
        return candidateSet;
    }
}
