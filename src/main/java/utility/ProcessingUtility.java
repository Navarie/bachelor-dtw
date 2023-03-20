package utility;

import lombok.experimental.UtilityClass;
import model.Point;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@UtilityClass
public class ProcessingUtility {

    private static final Random random = new Random();

    private final DateTimeFormatter dtFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US)
            .withZone(ZoneId.of("Europe/Copenhagen"));

    public List<List<Point>> transformFileToTrajectories(List<String[]> file) {

        List<Point> points = createPointsFromFile(file);

        return mapPointsToTrajectories(points);
    }

    private List<List<Point>> mapPointsToTrajectories(List<Point> points) {
        List<List<Point>> trajectories = new ArrayList<>();
        int trackId = Integer.MIN_VALUE;
        List<Point> sortedList = points.stream().sorted(Comparator.comparing(Point::getUniqueId)).toList();
        points = new ArrayList<>();
        for (Point point : sortedList) {
            if (trackId != Integer.MIN_VALUE && trackId != point.getUniqueId()) {
                trajectories.add(points);
                points = new ArrayList<>();
            }
            trackId = point.getUniqueId();
            points.add(point);
        }
        return trajectories;
    }

    private List<Point> createPointsFromFile(List<String[]> file) {
        List<Point> points = new ArrayList<>();
        for (String[] row : file) {
            var point = new Point(
                    OffsetDateTime.from(ZonedDateTime.parse(row[4], dtFormat)),
                    Double.parseDouble(row[1]),
                    Double.parseDouble(row[2]),
                    Integer.parseInt(row[3])
            );
            points.add(point);
        }
        return points;
    }

    public void prettyPrintMatrix(double[][] matrix, String printStatement) {
        var df = new DecimalFormat("####0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));

        System.out.println(printStatement);
        for (double[] row : matrix) {
            buildString(row, df);
        }
    }

    public void prettyPrintMatrix(double[] matrix, String printStatement) {
        var df = new DecimalFormat("####0.00");
        df.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.US));

        System.out.println(printStatement);
        buildString(matrix, df);
    }

    private void buildString(double[] matrix, DecimalFormat df) {
        var builtString = new StringBuilder();
        builtString.append("[");
        for (double value : matrix) {
            builtString.append(df.format(value)).append(", ");
        }
        builtString.deleteCharAt(builtString.length() - 1);
        builtString.deleteCharAt(builtString.length() - 1);
        builtString.append("]");

        System.out.println(builtString);
    }

    public List<Double> generateRandomList() {
        List<Double> randomList = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            randomList.add((double) random.nextInt(0, 8));
        }
        return randomList;
    }

    public List<Double> generateRandom16List() {
        List<Double> randomList = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            randomList.add((double) random.nextInt(0, 8));
        }
        return randomList;
    }
}
