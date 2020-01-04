package ostrowski.com;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

public class Cover
{
   private List<Wheel> wheels_ = new ArrayList<>();
   public Cover()
   {
   }
   public void addWheel(Wheel wheel) {
      wheels_.add(wheel);
   }
   public void paint(GC gc, Display display, Image image, Rectangle invalideArea) {
      Rectangle outerEdge = null;
      for (Wheel wheel : wheels_) {
         // Rectangle {100, 35, 150, 125}
         Rectangle rect = wheel.getBoundingRect();
         if (outerEdge == null) {
            outerEdge = rect;
         }
         else {
            outerEdge = outerEdge.union(rect);
         }
      }
      
      List<Point> outerPolyLine = new ArrayList<>();
      outerPolyLine.add(new Point(outerEdge.x,                    outerEdge.y                     ));
      outerPolyLine.add(new Point(outerEdge.x + outerEdge.width, outerEdge.y                     ));
      outerPolyLine.add(new Point(outerEdge.x + outerEdge.width, outerEdge.y + outerEdge.height ));
      outerPolyLine.add(new Point(outerEdge.x,                    outerEdge.y + outerEdge.height ));

//      int boundaryWidth = 1;
//      int[] borderPolyLine = new int[] {outerPolyLine.get(0).x-boundaryWidth, outerPolyLine.get(0).y-boundaryWidth,
//                                        outerPolyLine.get(1).x+boundaryWidth, outerPolyLine.get(1).y-boundaryWidth,
//                                        outerPolyLine.get(2).x+boundaryWidth, outerPolyLine.get(2).y+boundaryWidth,
//                                        outerPolyLine.get(3).x-boundaryWidth, outerPolyLine.get(3).y+boundaryWidth,
//                                        outerPolyLine.get(0).x-boundaryWidth, outerPolyLine.get(0).y-boundaryWidth};
//
//      gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
//      gc.drawPolygon(borderPolyLine);
      
      Line topLine    = new Line(outerPolyLine.get(0), outerPolyLine.get(1));
      Line rightLine  = new Line(outerPolyLine.get(1), outerPolyLine.get(2));
      Line bottomLine = new Line(outerPolyLine.get(2), outerPolyLine.get(3));
      Line leftLine   = new Line(outerPolyLine.get(3), outerPolyLine.get(0));
      
      List<Point> polyLine = new ArrayList<>();
      for (Wheel.ReadFromSide side : new Wheel.ReadFromSide[] {Wheel.ReadFromSide.TOP, Wheel.ReadFromSide.RIGHT, Wheel.ReadFromSide.BOTTOM, Wheel.ReadFromSide.LEFT}) {
         polyLine.add(outerPolyLine.remove(0));
         for (Wheel wheel : wheels_) {
            if (wheel.getReadFromSide() == side) {
               Rectangle mainTextExtent = wheel.getMainTextRegion();
               if (mainTextExtent == null)
                  continue;
               Rectangle innerTextExtent = wheel.getInnerTextRegion();
//               Point topMidPoint    = new Point(textExtent.x + textExtent.width/2, textExtent.y);
//               Point bottomMidPoint = new Point(textExtent.x + textExtent.width/2, textExtent.y + textExtent.height);
//               Point rightMidPoint  = new Point(textExtent.x + textExtent.width,   textExtent.y + textExtent.height/2);
//               Point leftMidPoint   = new Point(textExtent.x,                      textExtent.y + textExtent.height/2);
//               Line topExtent    = new Line (new Point (textExtent.x, textExtent.y)                     , new Point (textExtent.x + textExtent.width, textExtent.y));
//               Line rightExtent  = new Line (new Point (textExtent.x + textExtent.width, textExtent.y ) , new Point (textExtent.x + textExtent.width, textExtent.y + textExtent.height));
//               Line bottomExtent = new Line (new Point (textExtent.x + textExtent.width, textExtent.y + textExtent.height) , new Point (textExtent.x, textExtent.y + textExtent.height));
//               Line leftExtent   = new Line (new Point (textExtent.x, textExtent.y + textExtent.height) , new Point (textExtent.x                   , textExtent.y                    ));
               
               Point topLeftPoint     = new Point(mainTextExtent.x,                         mainTextExtent.y);
               Point topRightPoint    = new Point(mainTextExtent.x + mainTextExtent.width,  mainTextExtent.y);
               Point bottomLeftPoint  = new Point(mainTextExtent.x,                         mainTextExtent.y + mainTextExtent.height);
               Point bottomRightPoint = new Point(mainTextExtent.x + mainTextExtent.width,  mainTextExtent.y + mainTextExtent.height);

               Line topExtent    = new Line (new Point (mainTextExtent.x,                        mainTextExtent.y),
                                             new Point (mainTextExtent.x + mainTextExtent.width, mainTextExtent.y));
               Line rightExtent  = new Line (new Point (mainTextExtent.x + mainTextExtent.width, mainTextExtent.y),
                                             new Point (mainTextExtent.x + mainTextExtent.width, mainTextExtent.y + mainTextExtent.height));
               Line bottomExtent = new Line (new Point (mainTextExtent.x + mainTextExtent.width, mainTextExtent.y + mainTextExtent.height),
                                             new Point (mainTextExtent.x,                        mainTextExtent.y + mainTextExtent.height));
               Line leftExtent   = new Line (new Point (mainTextExtent.x,                        mainTextExtent.y + mainTextExtent.height),
                                             new Point (mainTextExtent.x,                        mainTextExtent.y));
               
               Line outerLine = null;
               Line innerLine = null;
               Point pointOnFirstLine = null;
               Point pointOnSecondLine = null;
//                    if (side == Wheel.ReadFromSide.TOP)    { pointOnFirstLine = leftMidPoint;   pointOnSecondLine = rightMidPoint;  outerLine = topLine;    innerLine = bottomExtent;}
//               else if (side == Wheel.ReadFromSide.BOTTOM) { pointOnFirstLine = rightMidPoint;  pointOnSecondLine = leftMidPoint;   outerLine = bottomLine; innerLine = topExtent;}
//               else if (side == Wheel.ReadFromSide.RIGHT)  { pointOnFirstLine = topMidPoint;    pointOnSecondLine = bottomMidPoint; outerLine = rightLine;  innerLine = leftExtent;}
//               else if (side == Wheel.ReadFromSide.LEFT)   { pointOnFirstLine = bottomMidPoint; pointOnSecondLine = topMidPoint;    outerLine = leftLine;   innerLine = rightExtent;}
                    if (side == Wheel.ReadFromSide.TOP)    { pointOnFirstLine = bottomLeftPoint;  pointOnSecondLine = bottomRightPoint; outerLine = topLine;    innerLine = bottomExtent;}
               else if (side == Wheel.ReadFromSide.BOTTOM) { pointOnFirstLine = topRightPoint;    pointOnSecondLine = topLeftPoint;     outerLine = bottomLine; innerLine = topExtent;}
               else if (side == Wheel.ReadFromSide.RIGHT)  { pointOnFirstLine = topLeftPoint;     pointOnSecondLine = bottomLeftPoint;  outerLine = rightLine;  innerLine = leftExtent;}
               else if (side == Wheel.ReadFromSide.LEFT)   { pointOnFirstLine = bottomRightPoint; pointOnSecondLine = topRightPoint;    outerLine = leftLine;   innerLine = rightExtent;}
                    
               Line firstLine  = new Line(pointOnFirstLine, wheel.centerCircle);
               Line secondLine = new Line(pointOnSecondLine, wheel.centerCircle);
               Point point1 = outerLine.intersect(firstLine);
               polyLine.add(point1);
               polyLine.add(innerLine.intersect(firstLine));
               if (innerTextExtent != null) {
                  polyLine.add(new Point(innerTextExtent.x, innerTextExtent.y + innerTextExtent.height));
                  polyLine.add(new Point(innerTextExtent.x + innerTextExtent.width, innerTextExtent.y + innerTextExtent.height));
                  polyLine.add(new Point(innerTextExtent.x + innerTextExtent.width, innerTextExtent.y));
                  polyLine.add(new Point(innerTextExtent.x, innerTextExtent.y));
                  polyLine.add(new Point(innerTextExtent.x, innerTextExtent.y + innerTextExtent.height));
                  polyLine.add(innerLine.intersect(firstLine));
               }
               polyLine.add(innerLine.intersect(secondLine));
               polyLine.add(outerLine.intersect(secondLine));
            }
         }
      }
      // complete the polygon:
      polyLine.add(polyLine.get(0));
      
      Color bgColor = new Color(image.getDevice(), new RGB(225,225,225));
      Color fgColor = new Color(image.getDevice(), new RGB(0,0,0));
      gc.setBackground(bgColor);
      gc.setForeground(fgColor);

      if (outerEdge != null) {
         int[] polyArray = new int[polyLine.size()*2];
         int i=0;
         for (Point point : polyLine) {
            polyArray[i++] = point.x;
            polyArray[i++] = point.y;
         }
         gc.drawPolyline(polyArray );
         gc.fillPolygon(polyArray );
      }
      
      for (Wheel wheel : wheels_) {
         wheel.drawInnercircle(gc, display, image, invalideArea);
         wheel.paintCoverLabels(gc, display, image, invalideArea);
      }
      
      bgColor.dispose();
      fgColor.dispose();
   }

}
