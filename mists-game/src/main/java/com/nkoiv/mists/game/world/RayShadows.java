/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nkoiv.mists.game.world;

import com.nkoiv.mists.game.Mists;
import com.nkoiv.mists.game.gameobject.Structure;
import java.awt.Shape;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Comparator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;


/**
 * Based on http://ncase.me/sight-and-light/
 * Most code taken and implemented from Marco13s helpful answer at StackOverflow
 * @author daedra
 */
public class RayShadows {
    private List<Shape> shapes;
    private List<Point2D> lightPositions;
    private Point2D lightPosition;
    private List<Line2D> shapesLineSegments;
    private double screenWidth;
    private double screenHeight;
    
    public RayShadows() {
        lightPositions = new ArrayList<>();
        shapes = new ArrayList<>();
        shapesLineSegments = new ArrayList<>();
    }
    
    public void paintLights(GraphicsContext gc, double xOffset, double yOffset) {
        gc.save();
        /*
        gc.setFill(new Color(0,0,0,0.8));
        gc.fillRect(0, 0, screenWidth, screenHeight);
        */
        /*
        gc.setStroke(Color.BLACK);
        for (Shape s : shapes)
        {
            gc.strokeRect(
                    s.getBounds().x, s.getBounds().y-yOffset,
                    s.getBounds().width, s.getBounds().height);
        }
        */
        
        List<Line2D> rays = createRays(lightPosition);
        //paintRays(gc, rays);

        List<Point2D> closestIntersections = 
            computeClosestIntersections(rays);
        Collections.sort(closestIntersections, 
            Points.byAngleComparator(lightPosition));

        paintClosestIntersections(gc, closestIntersections);
        paintLinesToIntersections(gc, lightPosition, closestIntersections);

        
        double[] pointsX = new double[closestIntersections.size()];
        double[] pointsY = new double[closestIntersections.size()];
        for (int i = 0; i < closestIntersections.size(); i++) {
            pointsX[i] = closestIntersections.get(i).getX();
            pointsY[i] = closestIntersections.get(i).getY();
        }
        gc.setFill(new Color(0,0,0,0.8));
        gc.fillRect(0, 0, screenWidth, screenHeight);
        
        gc.setFill(new Color(1,1,1,1));  
       
        gc.fillPolygon(pointsX, pointsY, closestIntersections.size());
        
        gc.setFill(Color.YELLOW);
        double r = 10; //TODO: LightPoint indicator size
        gc.fillOval(lightPosition.getX(), lightPosition.getY(), r, r);
        //fill(new Ellipse2D.Double(lightPosition.getX()-r, lightPosition.getY()-r, r+r, r+r));
        
        gc.restore();
    }
    
    private Path2D createLightShape(List<Point2D> closestIntersections) {
        Path2D shadowShape = new Path2D.Double();
        for (int i=0; i<closestIntersections.size(); i++)
        {
            Point2D p = closestIntersections.get(i);
            double x = p.getX();
            double y = p.getY();
            if (i == 0)
            {
                shadowShape.moveTo(x, y);
            }
            else
            {
                shadowShape.lineTo(x, y);
            }
        }
        shadowShape.closePath();
        return shadowShape;
    }

    private void paintRays(GraphicsContext gc, List<Line2D> rays)
    {
        gc.setStroke(Color.YELLOW);
        for (Line2D ray : rays)
        {
            gc.strokeLine(ray.getX1(), ray.getY1(), ray.getX2(), ray.getY2());
        }
    }

    private void paintClosestIntersections(GraphicsContext gc,
        List<Point2D> closestIntersections)
    {
        gc.setFill(Color.RED);
        double r = 3;
        for (Point2D p : closestIntersections)
        {
            gc.fillOval(p.getX(), p.getY(), r, r);
        }
    }

    private void paintLinesToIntersections(GraphicsContext gc, Point2D light,
        List<Point2D> closestIntersections)
    {
        gc.setStroke(Color.RED);
        for (Point2D p : closestIntersections)
        {
            gc.strokeLine(light.getX(), light.getY(), p.getX(), p.getY());
        }
    }
    
    private List<Point2D> computeClosestIntersections(List<Line2D> rays)
    {
        List<Point2D> closestIntersections = new ArrayList<Point2D>();
        for (Line2D ray : rays)
        {
            Point2D closestIntersection =
                computeClosestIntersection(ray, 1);
            if (closestIntersection != null)
            {
                closestIntersections.add(closestIntersection);
            }
        }
        return closestIntersections;
    }


    private List<Line2D> createRays(Point2D lightPosition)
    {
        final double deltaRad = 0.0001;
        List<Line2D> rays = new ArrayList<Line2D>();
        
            for (Line2D line : shapesLineSegments)
            {
                Line2D ray0 = new Line2D.Double(lightPosition, line.getP1());
                Line2D ray1 = new Line2D.Double(lightPosition, line.getP2());
                rays.add(ray0);
                rays.add(ray1);

                rays.add(Lines.rotate(ray0, +deltaRad, null));
                rays.add(Lines.rotate(ray0, -deltaRad, null));
                rays.add(Lines.rotate(ray1, +deltaRad, null));
                rays.add(Lines.rotate(ray1, -deltaRad, null));
            }
        
        return rays;
    }


    private Point2D computeClosestIntersection(Line2D ray, int pierceLevel) {
        final double EPSILON = 1e-6;
        Point2D relativeLocation = new Point2D.Double();
        Point2D absoluteLocation = new Point2D.Double();
        Point2D closestIntersection = null;
        double minRelativeDistance = Double.MAX_VALUE;
        
        
        for (Line2D lineSegment : shapesLineSegments)
        {
            boolean intersect =
                Intersection.intersectLineLine(
                    ray, lineSegment, relativeLocation, absoluteLocation);
            if (intersect)
            {
                if (relativeLocation.getY() >= -EPSILON &&
                    relativeLocation.getY() <= 1+EPSILON)
                {
                    if (relativeLocation.getX() >= -EPSILON &&
                        relativeLocation.getX() < minRelativeDistance)
                    {
                        minRelativeDistance = relativeLocation.getX();
                        closestIntersection = new Point2D.Double(absoluteLocation.getX(),absoluteLocation.getY());
                    }
                }
            }
        }
        
        return closestIntersection;
    }

    
    public void updateStructures(List<Structure> structures, double xOffset, double yOffset) {
        shapesLineSegments.clear();
        for (Structure mob : structures) {
            if (mob.getCollisionLevel() == 0) continue;
            Shape r = new Rectangle2D.Double(mob.getXPos()-xOffset, mob.getYPos()-yOffset, mob.getWidth(), mob.getHeight());
            shapes.add(r);
            List<Line2D> l = Shapes.computeLineSegments(r, 1);
            mergeLineSegmentsIntoExisting(l, shapesLineSegments);
            for (Line2D ls : l) {
                shapesLineSegments.add(ls);
            }
        }
        for (Line2D bl : addBorderLines(screenWidth, screenHeight)) {
            shapesLineSegments.add(bl);
        }
        Mists.logger.info("ShapesLineSegment size: "+shapesLineSegments.size());
    }
    
    /**
     * Walls tend to be placed next to another,
     * so line segments can be merged into a longer wall,
     * instead of using several small segments.
     * This saves CPU quite a bit.
     * TODO:remove horizontal breaks in vertical walls and vice versa
     * @param newSegments Segments to add to the line list
     * @param oldSegments Existing list of lines
     */
    private void mergeLineSegmentsIntoExisting(List<Line2D> newSegments, List<Line2D> oldSegments) {
        Iterator lit = newSegments.iterator();
        while (lit.hasNext()) {
            boolean merged = false;
            Line2D l = (Line2D)lit.next();
            Point2D p1 = new Point2D.Double(l.getX1(), l.getY1());
            Point2D p2 = new Point2D.Double(l.getX2(), l.getY2());
            for (Line2D ol : oldSegments) {
                Point2D op1 = new Point2D.Double(ol.getX1(), ol.getY1());
                Point2D op2 = new Point2D.Double(ol.getX2(), ol.getY2());
                if ((closeEnough(p1, op1, 2) && closeEnough(p2, op2, 2)) || ((closeEnough(p1, op2, 2) && closeEnough(p2, op1, 2)))) {
                    //Line is identical to one we already have
                    merged=true; break;
                }
                
                if (closeEnough(p1, op1, 2)) {
                    if (closeEnough(p2.getX(),op2.getX(),2)  || closeEnough(p2.getY(), op2.getY(),2)) {
                        op1.setLocation(p2.getX(), p2.getY());
                        merged = true;
                    }
                } else if (closeEnough(p1, op2, 2)) {
                    if (closeEnough(p2.getX(), op1.getX(),2) || closeEnough(p2.getY(), op1.getY(), 2)) {
                        op2.setLocation(p2.getX(), p2.getY());
                        merged = true;
                    }
                } else if (closeEnough(p2, op1, 2)) {
                    if (closeEnough(p1.getX(), op2.getX(),2) || closeEnough(p1.getY(), op2.getY(),2)) {
                        op1.setLocation(p1.getX(), p1.getY());
                        merged = true;
                    }
                } else if (closeEnough(p2, op2, 2)) {
                    if (closeEnough(p1.getX(), op1.getX(),2)  || closeEnough(p1.getY(), op1.getY(),2)) {
                        op2.setLocation(p1.getX(), p1.getY());
                        merged = true;
                    }
                }
                
                if (merged) {
                    ol.setLine(op1, op2);
                    break;
                }
            }
            if (merged) lit.remove();
        }
    }
    
    private boolean closeEnough(double x1, double x2, int margin) {
        return (Math.abs(x1 - x2)<margin);
    }
    
    private boolean closeEnough(Point2D p1, Point2D p2, int margin) {
        return (Math.abs(p1.getX() - p2.getX()) < margin && Math.abs(p1.getY() - p2.getY()) < margin);
    }
    
    private List<Line2D> addBorderLines(double screenWidth, double screenHeight) {
        List<Line2D> borderLineSegments = new ArrayList<>();
        double sWidth = screenWidth;
        double sHeight = screenHeight;
        borderLineSegments.clear();
        borderLineSegments.add(
            new Line2D.Double(0,0,sWidth,0));
        borderLineSegments.add(
            new Line2D.Double(sWidth,0,sWidth,sHeight));
        borderLineSegments.add(
            new Line2D.Double(sWidth,sHeight,0,sHeight));
        borderLineSegments.add(
            new Line2D.Double(0,sHeight,0,0));
        
        return borderLineSegments;
    }
    
    public void clearLights() {
        this.lightPositions.clear();
    }
    
    public void addLight(Point2D light) {
        this.lightPositions.add(light);
    }
    
    public void setLight(double xCoor, double yCoor) {
        Point2D light = new Point2D.Double(xCoor, yCoor);
        this.lightPosition = light;
    }
    
    public void setScreenSize(double screenWidth, double screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
}    
class Points
{
    /**
     * Creates a comparator that compares points by the
     * angle of the line between the point and the given
     * center
     * 
     * @param center The center
     * @return The comparator
     */
    public static Comparator<Point2D> byAngleComparator(
        final Point2D center)
    {
        return new Comparator<Point2D>()
        {
            @Override
            public int compare(Point2D p0, Point2D p1)
            {
                double dx0 = p0.getX() - center.getX();
                double dy0 = p0.getY() - center.getY();
                double dx1 = p1.getX() - center.getX();
                double dy1 = p1.getY() - center.getY();
                double angle0 = Math.atan2(dy0, dx0);
                double angle1 = Math.atan2(dy1, dx1);
                return Double.compare(angle0, angle1);
            }
        };
    }
}


class Lines
{
    /**
     * Rotate the given line around its starting point, by
     * the given angle, and stores the result in the given
     * result line. If the result line is <code>null</code>,
     * then a new line will be created and returned.
     * 
     * @param line The line
     * @param angleRad The rotation angle
     * @param The result line
     * @return The result line
     */
    static Line2D rotate(Line2D line, double angleRad, Line2D result)
    {
        double x0 = line.getX1();
        double y0 = line.getY1();
        double x1 = line.getX2();
        double y1 = line.getY2();
        double dx = x1 - x0;;
        double dy = y1 - y0;
        double sa = Math.sin(angleRad);
        double ca = Math.cos(angleRad);
        double nx = ca * dx - sa * dy;
        double ny = sa * dx + ca * dy;
        if (result == null)
        {
            result = new Line2D.Double();
        }
        
        result.setLine(x0, y0, x0+nx, y0+ny);
        return result;
    }

}

class Intersection
{
    /**
     * Epsilon for floating point computations
     */
    private static final double EPSILON = 1e-6;


    /**
     * Computes the intersection of the given lines.
     * 
     * @param line0 The first line
     * @param line1 The second line
     * @param relativeLocation Optional location that stores the 
     * relative location of the intersection point on 
     * the given line segments
     * @param absoluteLocation Optional location that stores the 
     * absolute location of the intersection point
     * @return Whether the lines intersect
     */
    public static boolean intersectLineLine( 
        Line2D line0, Line2D line1,
        Point2D relativeLocation,
        Point2D absoluteLocation)
    {
        return intersectLineLine(
            line0.getX1(), line0.getY1(), 
            line0.getX2(), line0.getY2(),
            line1.getX1(), line1.getY1(), 
            line1.getX2(), line1.getY2(),
            relativeLocation, absoluteLocation);
    }

    /**
     * Computes the intersection of the specified lines.
     * 
     * Ported from 
     * http://www.geometrictools.com/LibMathematics/Intersection/
     *     Wm5IntrSegment2Segment2.cpp
     * 
     * @param s0x0 x-coordinate of point 0 of line segment 0
     * @param s0y0 y-coordinate of point 0 of line segment 0
     * @param s0x1 x-coordinate of point 1 of line segment 0
     * @param s0y1 y-coordinate of point 1 of line segment 0
     * @param s1x0 x-coordinate of point 0 of line segment 1
     * @param s1y0 y-coordinate of point 0 of line segment 1
     * @param s1x1 x-coordinate of point 1 of line segment 1
     * @param s1y1 y-coordinate of point 1 of line segment 1
     * @param relativeLocation Optional location that stores the 
     * relative location of the intersection point on 
     * the given line segments
     * @param absoluteLocation Optional location that stores the 
     * absolute location of the intersection point
     * @return Whether the lines intersect
     */
    public static boolean intersectLineLine( 
        double s0x0, double s0y0,
        double s0x1, double s0y1,
        double s1x0, double s1y0,
        double s1x1, double s1y1,
        Point2D relativeLocation,
        Point2D absoluteLocation)
    {
        double dx0 = s0x1 - s0x0;
        double dy0 = s0y1 - s0y0;
        double dx1 = s1x1 - s1x0;
        double dy1 = s1y1 - s1y0;

        double invLen0 = 1.0 / Math.sqrt(dx0*dx0+dy0*dy0); 
        double invLen1 = 1.0 / Math.sqrt(dx1*dx1+dy1*dy1); 

        double dir0x = dx0 * invLen0;
        double dir0y = dy0 * invLen0;
        double dir1x = dx1 * invLen1;
        double dir1y = dy1 * invLen1;

        double c0x = s0x0 + dx0 * 0.5;
        double c0y = s0y0 + dy0 * 0.5;
        double c1x = s1x0 + dx1 * 0.5;
        double c1y = s1y0 + dy1 * 0.5;

        double cdx = c1x - c0x;
        double cdy = c1y - c0y;

        double dot = dotPerp(dir0x, dir0y, dir1x, dir1y);
        if (Math.abs(dot) > EPSILON)
        {
            if (relativeLocation != null || absoluteLocation != null)
            {
                double dot0 = dotPerp(cdx, cdy, dir0x, dir0y);
                double dot1 = dotPerp(cdx, cdy, dir1x, dir1y);
                double invDot = 1.0/dot;
                double s0 = dot1*invDot;
                double s1 = dot0*invDot;
                if (relativeLocation != null)
                {
                    double n0 = (s0 * invLen0) + 0.5;
                    double n1 = (s1 * invLen1) + 0.5;
                    relativeLocation.setLocation(n0, n1);
                }
                if (absoluteLocation != null)
                {
                    double x = c0x + s0 * dir0x;
                    double y = c0y + s0 * dir0y;
                    absoluteLocation.setLocation(x, y);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns the perpendicular dot product, i.e. the length
     * of the vector (x0,y0,0)x(x1,y1,0).
     * 
     * @param x0 Coordinate x0
     * @param y0 Coordinate y0
     * @param x1 Coordinate x1
     * @param y1 Coordinate y1
     * @return The length of the cross product vector
     */
    private static double dotPerp(double x0, double y0, double x1, double y1)
    {
        return x0*y1 - y0*x1;
    }

}



class Shapes
{
    /**
     * Create a list containing line segments that approximate the given 
     * shape.
     * 
     * @param shape The shape
     * @param flatness The allowed flatness
     * @return The list of line segments
     */
    static List<Line2D> computeLineSegments(Shape shape, double flatness) {
        List<Line2D> result = new ArrayList<>();
        PathIterator pi =
            new FlatteningPathIterator(
                shape.getPathIterator(null), flatness);
        double[] coords = new double[6];
        double previous[] = new double[2];
        double first[] = new double[2];
        while (!pi.isDone())
        {
            int segment = pi.currentSegment(coords);
            switch (segment)
            {
                case PathIterator.SEG_MOVETO:
                    previous[0] = coords[0];
                    previous[1] = coords[1];
                    first[0] = coords[0];
                    first[1] = coords[1];
                    break;

                case PathIterator.SEG_CLOSE:
                    result.add(new Line2D.Double(
                        previous[0], previous[1],
                        first[0], first[1]));
                    previous[0] = first[0];
                    previous[1] = first[1];
                    break;

                case PathIterator.SEG_LINETO:
                    result.add(new Line2D.Double(
                        previous[0], previous[1],
                        coords[0], coords[1]));
                    previous[0] = coords[0];
                    previous[1] = coords[1];
                    break;

                case PathIterator.SEG_QUADTO:
                    // Should never occur
                    throw new AssertionError(
                        "SEG_QUADTO in flattened path!");

                case PathIterator.SEG_CUBICTO:
                    // Should never occur
                    throw new AssertionError(
                        "SEG_CUBICTO in flattened path!");
            }
            pi.next();
        }
        return result;
    }
}
