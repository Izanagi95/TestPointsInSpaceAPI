package org.test.space.controllers;

import org.springframework.web.bind.annotation.*;
import org.test.space.model.Point;

import java.util.*;

@RestController
@RequestMapping("/")
public class PointsController {

    private final Set<Point> points = new HashSet<>();

    /***
     * Get all line segments passing through at least N points. Note that a line segment should be a set of
     * points.
     * @return all the points in the space
     */
    @GetMapping(value = "/space", produces = "application/json")
    Set<Point> getSpace() {
        return points;
    }

    /***
     * Add a point to the space
     * @param point point in {x: ... , y:...} format
     * @return operation status
     */
    @PostMapping(value = "/point", consumes = "application/json", produces = "application/json")
    Map<String, String> insertPoint(@RequestBody Point point) {
        Map<String, String> response = new HashMap<>();
        try {
            points.add(point);
            response.put("status", "inserted");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error inserting point: " + e.getMessage());
        }
        return response;
    }

    /***
     * Get all line segments passing through at least N points. Note that a line segment should be a set of
     * points.
     * @param n The minimum number of points that must be part of a segment
     * @return List of line segments, empty list if not valid input
     */
    @GetMapping(value = "/lines/{n}", produces = "application/json")
    Set<Set<Point>> getSegments(@PathVariable("n") int n) {
        Set<Set<Point>> result = new HashSet<>();
        try {
            if (n < 2 || points.size() < n) return Collections.emptySet();

            List<Point> pointList = new ArrayList<>(points);
            Map<Double, Set<Point>> slopeMap = new HashMap<>();
            Map<Double, Set<Point>> verticalLines = new HashMap<>();

            // for each combination of two points, calculate the slope m = (y2-y1)/(x2-x1) or choose x for vertical line
            // and use them as key for considered points
            for (int i = 0; i < pointList.size(); i++) {
                Point p1 = pointList.get(i);
                for (int j = 0; j < pointList.size(); j++) {
                    if (i == j) continue;
                    Point p2 = pointList.get(j);
                    if (p1.x == p2.x) {
                        verticalLines.computeIfAbsent(p1.x, k -> new HashSet<>()).add(p1);
                        verticalLines.get(p1.x).add(p2);
                    } else {
                        double slope = (p2.y - p1.y) / (p2.x - p1.x);
                        slopeMap.computeIfAbsent(slope, k -> new HashSet<>()).add(p1);
                        slopeMap.get(slope).add(p2);
                    }
                }
            }
            for (Set<Point> line : slopeMap.values()) {
                if (line.size() >= n) {
                    result.add(new HashSet<>(line));
                }
            }
            for (Set<Point> line : verticalLines.values()) {
                if (line.size() >= n) {
                    result.add(new HashSet<>(line));
                }
            }
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "Error calculating segments: " + e.getMessage());
            return Collections.emptySet();
        }
        return result;
    }

    /***
     * Remove all points from the space
     * @return operation status
     */
    @DeleteMapping(value = "/space", produces = "application/json")
    Map<String, String> deleteSpace() {
        Map<String, String> response = new HashMap<>();
        try {
            points.clear();
            response.put("status", "cleared");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error clearing space: " + e.getMessage());
        }
        return response;
    }
}
