package org.test.space.model;

import java.util.Objects;

public class Point {

    // chosen to keep public for better perfomance
    public double x;
    public double y;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Point)) return false;
        Point p = (Point) obj;
        return x == p.x && y == p.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
