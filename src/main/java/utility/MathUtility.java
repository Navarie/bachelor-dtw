package utility;

import lombok.experimental.UtilityClass;
import model.Point;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@UtilityClass
public class MathUtility {

    private static final Random random = new Random();

    /**
     * Follows the haversine formula as laid out in https://www.movable-type.co.uk/scripts/latlong.html
     * @param from an object of type Point
     * @param to an object of type Point
     * @return The distance in kilometers, with up to 0.5% imprecision.
     */
    public static double getDistance(Point from, Point to) {
        final int earthRadius = 6371;

        var deltaLatitude = Math.toRadians(to.getLatitude() - from.getLatitude());
        var deltaLongitude = Math.toRadians(to.getLongitude() - from.getLongitude());
        var cosineLatitude = Math.cos(Math.toRadians(from.getLatitude()));
        var cosineLongitude = Math.cos(Math.toRadians(from.getLongitude()));

        var chordLengthMeasure = sineSquared(deltaLatitude / 2) + cosineLatitude * cosineLongitude * sineSquared(deltaLongitude /2);
        var angularDistance = 2 * Math.atan2(Math.sqrt(chordLengthMeasure), Math.sqrt(1 - chordLengthMeasure));
        return earthRadius * angularDistance;
    }

    private static double sineSquared(double radians) {
        return Math.sin(radians) * Math.sin(radians);
    }

    public static List<Point> addNoise(List<Point> inputPoints, double noiseCoefficient) {
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

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        var bigDecimal = BigDecimal.valueOf(value);
        bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
        return bigDecimal.doubleValue();
    }

    public static double minimum(double a, double b, double c) {
        return Math.min(a, Math.min(b, c));
    }

    public static int generateIndexFrom(List<?> inputList) {
        return random.nextInt(0, inputList.size() - 1);
    }

    public static Map<String, Integer> createSakoeChibaMap(int sampleSize, int randomSize, int bandSize) {
        Map<String, Integer> sakoeChibaMap = new HashMap<>();

        int horizontalShift = 0;
        int verticalShift = 0;
        if (sampleSize > randomSize) {
            horizontalShift = bandSize;
        } else {
            verticalShift = bandSize;
        }
        sakoeChibaMap.put("bandwidth", bandSize);
        sakoeChibaMap.put("verticalShift", verticalShift);
        sakoeChibaMap.put("horizontalShift", horizontalShift);

        return sakoeChibaMap;
    }

    public static List<Integer> calculateLowerBound(Map<String, Integer> sakoeChibaMap, int sampleSize) {
        List<Integer> lowerBounds = new ArrayList<>();

        int verticalShift = sakoeChibaMap.get("verticalShift");
        int horizontalShift = sakoeChibaMap.get("horizontalShift");
        int lowerBoundUpperBound = sampleSize - horizontalShift - verticalShift;
        for (int i=0; i < lowerBoundUpperBound; i++) {
            lowerBounds.add(i);
        }

        return lowerBounds;
    }

    public static List<Integer> calculateUpperBound(Map<String, Integer> sakoeChibaMap, int sampleSize) {
        List<Integer> upperBounds = new ArrayList<>();

        int verticalShift = sakoeChibaMap.get("verticalShift");
        int horizontalShift = sakoeChibaMap.get("horizontalShift");
        int upperBoundLowerBound = horizontalShift + verticalShift;
        int upperBoundUpperBound = sampleSize + upperBoundLowerBound;
        for (int i = upperBoundLowerBound; i < upperBoundUpperBound; i++) {
            upperBounds.add(i);
        }

        return upperBounds;
    }
}
