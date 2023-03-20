package utility;

import lombok.experimental.UtilityClass;
import model.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static utility.LogUtility.*;

@UtilityClass
public class MathUtility {

    private final Random random = new Random();

    /**
     * Follows the haversine formula as laid out in https://www.movable-type.co.uk/scripts/latlong.html
     * @param from an object of type Point
     * @param to an object of type Point
     * @return The distance in kilometers, with up to 0.5% imprecision.
     */
    public double getDistance(Point from, Point to) {
        final int earthRadius = 6371;

        var deltaLatitude = Math.toRadians(to.getLatitude() - from.getLatitude());
        var deltaLongitude = Math.toRadians(to.getLongitude() - from.getLongitude());
        var cosineLatitude = Math.cos(Math.toRadians(from.getLatitude()));
        var cosineLongitude = Math.cos(Math.toRadians(from.getLongitude()));

        var chordLengthMeasure = sineSquared(deltaLatitude / 2) + cosineLatitude * cosineLongitude * sineSquared(deltaLongitude /2);
        var angularDistance = 2 * Math.atan2(Math.sqrt(chordLengthMeasure), Math.sqrt(1 - chordLengthMeasure));
        return earthRadius * angularDistance;
    }

    private double sineSquared(double radians) {
        return Math.sin(radians) * Math.sin(radians);
    }

    public List<Point> addNoise(List<Point> inputPoints, double noiseCoefficient) {
        List<Point> outputPoints = new ArrayList<>();
        for (Point p: inputPoints) {
            outputPoints.add(new Point(
                    p.getTimestamp(),
                    p.getLatitude() + random.nextGaussian() * noiseCoefficient,
                    p.getLongitude() + random.nextGaussian() * noiseCoefficient,
                    p.getUniqueId()
            ));
        }
        return outputPoints;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        var bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public double minimum(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    public int generateIndexFrom(List<?> inputList) {
        return random.nextInt(0, inputList.size() - 1);
    }

    public List<List<Integer>> computeConstraintRegion(int sampleSize, int randomSize, int bandwidth) {
        List<List<Integer>> bandwidthIndeces = new ArrayList<>();
        List<Integer> lowerBounds = new ArrayList<>();
        List<Integer> upperBounds = new ArrayList<>();
        double diagonalY;
        int lowerBound;
        int upperBound;

        for (int i = 0; i < sampleSize; i++) {
            // Force double division
            diagonalY = (double) i / (sampleSize - 1) * randomSize;
            lowerBound = (int) Math.round(diagonalY - bandwidth);
            upperBound = (int) Math.round(diagonalY + bandwidth);
            if (isDebugging()) {
                System.out.println("diagonalY: " + diagonalY);
            }

            if (lowerBound < 0) {
                lowerBound = 0;
            } else if (lowerBound >= randomSize) {
                lowerBound = randomSize - bandwidth;
            }
            if (upperBound <= 0) {
                upperBound = bandwidth;
            } else if (upperBound > randomSize) {
                upperBound = randomSize;
            }

            lowerBounds.add(lowerBound);
            upperBounds.add(upperBound);
        }
        bandwidthIndeces.add(lowerBounds);
        bandwidthIndeces.add(upperBounds);

        return bandwidthIndeces;
    }
    public List<List<Integer>> computeConstraintRegion(int sampleSize, int randomSize, double bandwidth) {
        List<List<Integer>> bandwidthIndeces = new ArrayList<>();
        List<Integer> lowerBounds = new ArrayList<>();
        List<Integer> upperBounds = new ArrayList<>();
        double diagonalY;
        double relativeBandwidth = ((bandwidth * randomSize) / 2) + 1;
        int lowerBound;
        int upperBound;

        for (int i = 0; i < sampleSize; i++) {
            // Force double division
            diagonalY = (double) i / (sampleSize - 1) * randomSize;
            lowerBound = (int) Math.round(diagonalY - relativeBandwidth);
            upperBound = (int) Math.round(diagonalY + relativeBandwidth);
//            System.out.println("diagonalY: " + diagonalY);

            if (lowerBound < 0) {
                lowerBound = 0;
            } else if (lowerBound >= randomSize) {
                lowerBound = (int) Math.floor(randomSize - relativeBandwidth);
            }
            if (upperBound <= 0) {
                upperBound = (int) relativeBandwidth;
            } else if (upperBound > randomSize) {
                upperBound = randomSize;
            }

            lowerBounds.add(lowerBound);
            upperBounds.add(upperBound);
        }
        bandwidthIndeces.add(lowerBounds);
        bandwidthIndeces.add(upperBounds);

        return bandwidthIndeces;
    }

    public List<Double> discreteHaarWaveletTransform(List<Double> input) {
        if (!validateLength(input)) {
            throw new InputMismatchException("the input Haar array did not have length 2^k");
        }
        List<Double> output = new ArrayList<>(input);
        for (int size = input.size() / 2; ; size /= 2) {

            for (int i = 0; i < size; i++) {
                double sum = (input.get(i * 2) + input.get(i * 2 + 1)) / 2;
                double difference = (input.get(i * 2) - input.get(i * 2 + 1)) / 2;
                output.set(i, sum);
                output.set(size + i, difference);
            }
            if (size == 1) {
                return output;
            }

            // Copy elements for next iteration
            for (int i = 0; i < size; i++) {
                input.set(i, output.get(i));
            }
        }
    }

    public double sumOfSquares(List<Double> input) {
        double sum = 0;
        for (double value : input) {
            sum += Math.pow(value, 2);
        }
        return sum;
    }

    public double haarTreeEuclideanDistance(List<Double> timeSeriesHaar, List<Double> queryRecordHaar, int haarTreeDepth) {
        if (!validateLength(timeSeriesHaar) || !validateLength(queryRecordHaar)) {
            throw new InputMismatchException("one of the Haar arrays did not have length 2^k");
        } else if (timeSeriesHaar.size() != queryRecordHaar.size()) {
            throw new InputMismatchException("the arrays did not have equal length");
        }

        double sum = 0;
        int currentDepth = haarTreeDepth;
        for (int currentLevel = 1; currentLevel <= haarTreeDepth; currentLevel++) {
            sum += haarBranchEuclideanDistance(timeSeriesHaar, queryRecordHaar, currentLevel, currentDepth);
            currentDepth--;
        }
        return sum;
    }

    public double bottomUpHaarTreeEuclideanDistance(List<Double> timeSeriesHaar, List<Double> queryRecordHaar, int haarTreeDepth) {
        if (!validateLength(timeSeriesHaar) || !validateLength(queryRecordHaar)) {
            throw new InputMismatchException("one of the Haar arrays did not have length 2^k");
        } else if (timeSeriesHaar.size() != queryRecordHaar.size()) {
            throw new InputMismatchException("the arrays did not have equal length");
        }

        double sum = 0;
        for (int currentLevel = haarTreeDepth; currentLevel <= getBinaryExponent(timeSeriesHaar); currentLevel++) {
            sum += bottomUpHaarBranchEuclideanDistance(timeSeriesHaar, queryRecordHaar, currentLevel);
            printIfDebugging("At level " + currentLevel + " and depth " + haarTreeDepth + ". Adding sum " + bottomUpHaarBranchEuclideanDistance(timeSeriesHaar, queryRecordHaar, currentLevel));
        }
        return sum;
    }

    private double bottomUpHaarBranchEuclideanDistance(List<Double> timeSeriesHaar, List<Double> queryRecordHaar, int currentLevel) {
        List<Integer> coefficientIndeces = getBottomUpIndices(currentLevel, timeSeriesHaar);

        return euclideanDistanceAtLevel(timeSeriesHaar, queryRecordHaar, currentLevel, coefficientIndeces);
    }

    private double euclideanDistanceAtLevel(List<Double> timeSeriesHaar, List<Double> queryRecordHaar, int currentLevel, List<Integer> coefficientIndeces) {
        double sum = 0;
        for (Integer i : coefficientIndeces) {
            if (isDebugging()) {
                printIndentedLine(
                        "Adding sum " + (Math.pow(Math.abs(timeSeriesHaar.get(i) - queryRecordHaar.get(i)), 2))
                        + " at index " + i
                        + " with level " + currentLevel);
            }
            sum += Math.pow(Math.abs(timeSeriesHaar.get(i) - queryRecordHaar.get(i)), 2);
        }

        int haarCoefficient = (int) Math.pow(2, currentLevel);
        printIfDebugging("Resulting in haarCoefficient 2^" + currentLevel + " * " + sum);

        return haarCoefficient * sum;
    }

    public double bottomUpSumDataPointsUpToLevel(List<Double> timeSeriesHaar, int depth) {
        List<Integer> coefficientIndices = getBottomUpIndicesUpToLevel(depth - 1, timeSeriesHaar);
        double sum = 0;
        for (Integer i : coefficientIndices) {
            sum += Math.pow(timeSeriesHaar.get(i), 2);
        }
        return sum;
    }

    private List<Integer> getBottomUpIndicesUpToLevel(int bottomUpLevel, List<Double> timeSeriesHaar) {
        var lengthRange = IntStream.range(0, timeSeriesHaar.size())
                .boxed()
                .toList();
        int currentLength = timeSeriesHaar.size();
        for (int i = 0; i < bottomUpLevel; i++) {
            currentLength /= 2;
        }
        return lengthRange.subList(currentLength, timeSeriesHaar.size());
    }

    public double haarBranchEuclideanDistance(List<Double> timeSeriesHaar, List<Double> queryRecordHaar, int haarTreeLevel, int currentDepth) {
        List<Integer> coefficientIndeces = getCoefficientIndices(haarTreeLevel);
        return euclideanDistanceAtLevel(timeSeriesHaar, queryRecordHaar, currentDepth, coefficientIndeces);
    }

    public double computeLowerBounds(List<Double> querySeriesHaar, List<Double> timeSeriesHaar, int depth) {
        double Drk = bottomUpHaarTreeEuclideanDistance(timeSeriesHaar, querySeriesHaar, depth);
        double sigma_rl_p = bottomUpPreComputeSumToDepth(timeSeriesHaar, depth);
        double sigma_rl_query = bottomUpPreComputeSumToDepth(querySeriesHaar, depth);
        double sigma_rp = bottomUpSumDataPointsUpToLevel(timeSeriesHaar, depth);
        double sigma_re_q = computeSumQuantityEqualSignsUpToDepth(querySeriesHaar, timeSeriesHaar, depth);
        double lowerBound = Drk + sigma_rl_p + sigma_rl_query - 2 * Math.sqrt(sigma_rp * sigma_re_q);

        if (isDebugging()) {
            printCurrentStep();
            printIndentedLine("Drk is " + Drk);
            printIndentedLine("sigma_rl_p is "+ sigma_rl_p);
            printIndentedLine("sigma_rl_query is "+ sigma_rl_query);
            printIndentedLine("sigma_rp is " + sigma_rp);
            printIndentedLine("sigma_re_q is " + sigma_re_q);
            printIndentedLine("lower bound for depth " + depth + " is " + lowerBound);
        }

        return lowerBound;
    }

    public double computeUpperBounds(List<Double> querySeriesHaar, List<Double> timeSeriesHaar, int depth) {
        double Drk = bottomUpHaarTreeEuclideanDistance(timeSeriesHaar, querySeriesHaar, depth);
        double sigma_rl_p = bottomUpPreComputeSumToDepth(timeSeriesHaar, depth);
        double sigma_rl_query = bottomUpPreComputeSumToDepth(querySeriesHaar, depth);
        double sigma_rp = bottomUpSumDataPointsUpToLevel(timeSeriesHaar, depth);
        double sigma_ro_q = computeSumQuantityOppositeSignsUpToDepth(querySeriesHaar, timeSeriesHaar, depth);
        double upperBound = Drk + sigma_rl_p + sigma_rl_query + 2 * Math.sqrt(sigma_rp * sigma_ro_q);

        if (isDebugging()) {
            printCurrentStep();
            printIndentedLine("Drk is " + Drk);
            printIndentedLine("sigma_rl_p is "+ sigma_rl_p);
            printIndentedLine("sigma_rl_query is "+ sigma_rl_query);
            printIndentedLine("sigma_rp is " + sigma_rp);
            printIndentedLine("sigma_re_q is " + sigma_ro_q);
            printIndentedLine("upper bound for depth " + depth + " is " + upperBound);
        }

        return upperBound;
    }

    public double preComputeSumToDepth(List<Double> timeSeriesHaar, int depth) {
        if (!validateLength(timeSeriesHaar)) {
            throw new InputMismatchException("Haar array did not have length 2^k");
        }

        double sum = 0;
        int currentDepth = depth;
        for (int currentLevel = 1; currentLevel < depth; currentLevel++) {
            sum += preComputeSumQuantityAtLevelWithHaarCoefficient(timeSeriesHaar, currentLevel, currentDepth);
            currentDepth--;
        }

        printIfDebugging("pre-computed sum up to depth " + depth + " is " + sum);
        return sum;
    }

    public double bottomUpPreComputeSumToDepth(List<Double> timeSeriesHaar, int depth) {
        if (!validateLength(timeSeriesHaar)) {
            throw new InputMismatchException("Haar array did not have length 2^k");
        }

        double sum = 0;
        for (int currentLevel = 1; currentLevel < depth; currentLevel++) {
            sum += bottomUpPreComputeSumQuantityAtLevelWithHaarCoefficient(timeSeriesHaar, currentLevel);
            printIfDebugging("At level " + currentLevel + " with depth " + depth + ". Adding sum " + bottomUpPreComputeSumQuantityAtLevelWithHaarCoefficient(timeSeriesHaar, currentLevel));
        }
        printIfDebugging("pre-computed sum up to depth " + depth + " is " + sum);

        return sum;
    }

    public double preComputeSumQuantity(List<Double> timeSeriesHaar, int depth) {
        if (!validateLength(timeSeriesHaar)) {
            throw new InputMismatchException("Haar array did not have length 2^k");
        }

        double sum = 0;
        int currentDepth = depth;
        for (int currentLevel = 1; currentLevel < depth; currentLevel++) {
            sum += preComputeSumQuantityAtLevel(timeSeriesHaar, currentDepth);
            currentDepth--;
        }
        printIfDebugging("pre-computed sum up to depth " + depth + " is " + sum);
        
        return sum;
    }

    public double preComputeSumQuantityAtLevel(List<Double> timeSeriesHaar, int currentDepth) {
        List<Integer> coefficientIndeces = getCoefficientIndices(currentDepth);
        double sum = 0;
        for (Integer i : coefficientIndeces) {
            sum += Math.pow(timeSeriesHaar.get(i), 2);
        }

        return sum;
    }

    public double preComputeSumQuantityAtLevelWithHaarCoefficient(List<Double> timeSeriesHaar, int currentLevel, int currentDepth) {
        List<Integer> coefficientIndeces = getCoefficientIndices(currentDepth);
        double sum = 0;
        for (Integer i : coefficientIndeces) {
            sum += Math.pow(2, currentLevel) * Math.pow(timeSeriesHaar.get(i), 2);
        }
        return sum;
    }

    public double bottomUpPreComputeSumQuantityAtLevelWithHaarCoefficient(List<Double> timeSeriesHaar, int currentLevel) {
        List<Integer> coefficientIndeces = getBottomUpIndices(currentLevel, timeSeriesHaar);
        double sum = 0;
        for (Integer i : coefficientIndeces) {
            sum += Math.pow(2, currentLevel) * Math.pow(timeSeriesHaar.get(i), 2);
        }
        return sum;
    }

    public double computeSigmaEqualSignsForEveryLevel(List<Double> querySeriesHaar, List<Double> timeSeriesHaar) {
        if (!validateLength(timeSeriesHaar) || !validateLength(querySeriesHaar)) {
            throw new InputMismatchException("one of the Haar arrays did not have length 2^k");
        } else if (timeSeriesHaar.size() != querySeriesHaar.size()) {
            throw new InputMismatchException("the arrays did not have equal length");
        }

        int binaryExponent = getBinaryExponent(querySeriesHaar);
        double sum = 0;
        for (int currentLevel = 1; currentLevel <= binaryExponent; currentLevel++) {
            sum += computeSumQuantityAtLevelEqualSigns(querySeriesHaar, timeSeriesHaar, currentLevel);
        }
        if (isDebugging()) {
            printCurrentStep();
            printIndentedLine("Sigma equal signs is " + sum);
        }
        return sum;
    }

    public double computeSigmaOppositeSignsForEveryLevel(List<Double> querySeriesHaar, List<Double> timeSeriesHaar) {
        if (!validateLength(timeSeriesHaar) || !validateLength(querySeriesHaar)) {
            throw new InputMismatchException("one of the Haar arrays did not have length 2^k");
        } else if (timeSeriesHaar.size() != querySeriesHaar.size()) {
            throw new InputMismatchException("the arrays did not have equal length");
        }

        int binaryExponent = getBinaryExponent(querySeriesHaar);
        double sum = 0;
        for (int currentLevel = 1; currentLevel <= binaryExponent; currentLevel++) {
            sum += computeSumQuantityAtLevelOppositeSigns(querySeriesHaar, timeSeriesHaar, currentLevel);
        }
        if (isDebugging()) {
            printCurrentStep();
            printIndentedLine("Sigma equal signs is " + sum);
        }
        return sum;
    }

    public double computeSumQuantityEqualSignsUpToDepth(List<Double> querySeriesHaar, List<Double> timeSeriesHaar, int depth) {
        if (!validateLength(timeSeriesHaar) || !validateLength(querySeriesHaar)) {
            throw new InputMismatchException("one of the Haar arrays did not have length 2^k");
        } else if (timeSeriesHaar.size() != querySeriesHaar.size()) {
            throw new InputMismatchException("the arrays did not have equal length");
        }

        double sum = 0;
        for (int currentLevel = 1; currentLevel < depth; currentLevel++) {
            sum += computeSumQuantityAtLevelEqualSigns(querySeriesHaar, timeSeriesHaar, currentLevel);
        }
        if (isDebugging()) {
            printCurrentStep();
            printIndentedLine("sum quantity equal signs at depth " + depth + " is " + sum);
        }
        return sum;
    }

    public double computeSumQuantityOppositeSignsUpToDepth(List<Double> querySeriesHaar, List<Double> timeSeriesHaar, int depth) {
        if (!validateLength(timeSeriesHaar) || !validateLength(querySeriesHaar)) {
            throw new InputMismatchException("one of the Haar arrays did not have length 2^k");
        } else if (timeSeriesHaar.size() != querySeriesHaar.size()) {
            throw new InputMismatchException("the arrays did not have equal length");
        }

        double sum = 0;
        for (int currentLevel = 1; currentLevel < depth; currentLevel++) {
            sum += computeSumQuantityAtLevelOppositeSigns(querySeriesHaar, timeSeriesHaar, currentLevel);
        }
        if (isDebugging()) {
            printCurrentStep();
            printIndentedLine("sum quantity opposite signs at depth " + depth + " is " + sum);
        }
        return sum;
    }

    private double computeSumQuantityAtLevelOppositeSigns(List<Double> querySeriesHaar, List<Double> timeSeriesHaar, int currentLevel) {
        List<Integer> coefficientIndeces = getBottomUpIndices(currentLevel, querySeriesHaar);
        List<Integer> oppositeSignsIndices = signsOpposite(coefficientIndeces, querySeriesHaar, timeSeriesHaar);

        double sum = 0;
        for (Integer i : oppositeSignsIndices) {
            double levelCoefficient = Math.pow(2, Math.pow(2, currentLevel));
            sum += levelCoefficient * Math.pow(querySeriesHaar.get(i), 2);

        }
        return sum;
    }

    private double computeSumQuantityAtLevelEqualSigns(List<Double> querySeriesHaar, List<Double> timeSeriesHaar, int currentLevel) {
        List<Integer> coefficientIndeces = getBottomUpIndices(currentLevel, querySeriesHaar);
        List<Integer> equalSignsIndices = signsEqual(coefficientIndeces, querySeriesHaar, timeSeriesHaar);

        double sum = 0;
        for (Integer i : equalSignsIndices) {
            double levelCoefficient = Math.pow(2, Math.pow(2, currentLevel));
            sum += levelCoefficient * Math.pow(querySeriesHaar.get(i), 2);
        }
        return sum;
    }

    public double computeSigmaForEveryLevel(List<Double> inputSeriesHaar) {
        int noOfLevels = getBinaryExponent(inputSeriesHaar);
        double sum = 0;
        for (int currentLevel = 1; currentLevel <= noOfLevels; currentLevel++) {

            List<Integer> coefficientIndices = getCoefficientIndices(currentLevel);
            double innerSum = 0;
            for (Integer i : coefficientIndices) {
                innerSum += Math.pow(inputSeriesHaar.get(i), 2);
            }
            sum += Math.pow(2, currentLevel) * innerSum;
        }
        if (isDebugging()) {
            printCurrentStep();
            printIndentedLine("Sigma was " + sum);
        }
        return sum;
    }

    private List<Integer> signsEqual(List<Integer> coefficientIndices, List<Double> querySeriesHaar, List<Double> timeSeriesHaar) {
        List<Integer> equalSignsIndices = new ArrayList<>();
        for (Integer i : coefficientIndices){
            if ((querySeriesHaar.get(i) > 0 && timeSeriesHaar.get(i) > 0) || (querySeriesHaar.get(i) < 0 && timeSeriesHaar.get(i) < 0)) {
                equalSignsIndices.add(i);
            }
        }
        return equalSignsIndices;
    }

    private List<Integer> signsOpposite(List<Integer> coefficientIndices, List<Double> querySeriesHaar, List<Double> timeSeriesHaar) {
        List<Integer> oppositeSignsIndices = new ArrayList<>();
        for (Integer i : coefficientIndices){
            if ((querySeriesHaar.get(i) > 0 && timeSeriesHaar.get(i) < 0) || (querySeriesHaar.get(i) < 0 && timeSeriesHaar.get(i) > 0)) {
                oppositeSignsIndices.add(i);
            }
        }
        return oppositeSignsIndices;
    }

    public List<Integer> getCoefficientIndices(int haarTreeLevel) {
        int indexMax = (int) Math.pow(2, haarTreeLevel);
        List<Integer> coefficientIndeces;
        if (haarTreeLevel > 1) {
            coefficientIndeces = IntStream.range((int) Math.pow(2, haarTreeLevel - 1), indexMax)
                    .boxed()
                    .toList();
        } else {
            coefficientIndeces = List.of(0, 1);
        }
        return coefficientIndeces;
    }

    public List<Integer> getBottomUpIndices(int haarTreeLevel, List<Double> timeSeriesHaar) {
        var lengthRange = IntStream.range(0, timeSeriesHaar.size())
                .boxed()
                .toList();
        int currentLength = timeSeriesHaar.size();
        var accList = lengthRange;
        for (int i = 0; i < haarTreeLevel; i++) {
            if (currentLength != 2) {
                accList = lengthRange.subList(currentLength / 2, currentLength);
            } else {
                accList = lengthRange.subList(0, 2);
            }
            currentLength /= 2;
        }
        return accList;
    }

    private boolean validateLength(List<Double> input) {
        if (input.size() < 2) {
            return false;
        }

        int finalLength = 0;
        for (int i = 1; i <= input.size(); i *= 2) {
            finalLength = i;
        }
        return finalLength == input.size();
    }

    public int getBinaryExponent(List<Double> timeSeriesHaar) {
        if (!validateLength(timeSeriesHaar)) {
            throw new InputMismatchException("haar array was not of size 2^k");
        }
        int binaryExponent = 1;
        int currentLength = timeSeriesHaar.size();
        while (currentLength != 2) {
            currentLength /= 2;
            binaryExponent++;
        }
        return binaryExponent;
    }
}
