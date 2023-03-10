package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Point {

    OffsetDateTime timestamp;
    double latitude;
    double longitude;
    int uniqueId;

    @Override
    public String toString() {
        return "Point {" +
                " timestamp: " + timestamp
                + " latitude: " + latitude
                + " longitude: " + longitude
                + " uniqueId: " + uniqueId
                + " }";
    }
}
