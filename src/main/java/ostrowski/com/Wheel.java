package ostrowski.com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.LineAttributes;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;

public class Wheel
{
   enum ReadFromSide {
      RIGHT,
      LEFT,
      TOP,
      BOTTOM
   }

   private final static double          INNER_HOLE_DIAMETER_IN_INCHES = .15;
   private final static String          FONT_NAME                     = "Arial";
   public static final  int             DPI_INITIAL                   = 184;
   public static        int             DPI                           = DPI_INITIAL;
   private final        double          diameterInInches;
   private final        double          borderInInches;
   private              double          topLeftXInInches              = 0;
   private              double          topLeftYInInches;
   private final        double          textSizeInInches;
   private              int             textStyle                     = SWT.NORMAL;
   private final        float           lineWidthInInches             = .02F;
   private final        List<TextBlock> coverText                     = new ArrayList<>();
   private final        List<String>    entries                       = new ArrayList<>();
   private final        RGB             edgeColorRgb                  = new RGB(0, 0, 0);
   private final        RGB             wheelColorRgb                 = new RGB(240, 240, 240);
   private final        RGB             textColorRgb                  = new RGB(0, 0, 0);
   private              int             colorBoundaryIndex            = -1;
   private              RGB             textColor2Rgb                 = new RGB(192, 0, 0);
   private final        RGB             outsideRgb                    = new RGB(255, 255, 255);
   private final        ReadFromSide    readFromSide;
   private final        int             startAngle;
   private final        int             arcAngle;
   private final        int             rotationAngle;
   private              int             drawDotsBetweenEvery          = -1;
   public               Point           centerCircle                  = null;
   private              Rectangle       mainTextRegion                = null;
   private              Rectangle       innerTextRegion               = null;

   public Wheel(double diameterInInches, double topLeftXInInches, double topLeftYInInches, TextBlock label,
                double textSizeInInches,  ReadFromSide readFromSide, int rotationOffset, String [] entries1) {
      this(diameterInInches, topLeftXInInches, topLeftYInInches, label, textSizeInInches, readFromSide, rotationOffset,
           entries1, null, null);
   }
   public Wheel(double diameterInInches, double topLeftXInInches, double topLeftYInInches,
                TextBlock label1, TextBlock label2, double textSizeInInches,  ReadFromSide readFromSide,
                int startAngle, int arcAngle, int rotationOffset, String [] entries1) {
      this(diameterInInches, topLeftXInInches, topLeftYInInches, label1, label2, textSizeInInches, readFromSide,
           startAngle, arcAngle, rotationOffset, entries1, null, null);
   }

   public Wheel(double diameterInInches, double topLeftXInInches, double topLeftYInInches, TextBlock label,
                double textSizeInInches, ReadFromSide readFromSide, int rotationOffset, int drawDotsBetweenEvery,
                String [] entries1) {
      this(diameterInInches, topLeftXInInches, topLeftYInInches, label, textSizeInInches, readFromSide,
           rotationOffset, entries1, null, null);
      this.drawDotsBetweenEvery = drawDotsBetweenEvery;
   }

   public Wheel(double diameterInInches, double topLeftXInInches, double topLeftYInInches, TextBlock label,
                double textSizeInInches, ReadFromSide readFromSide, int rotationOffset, String [] entries1,
                RGB textColor2Rgb, String [] entries2) {
      this(diameterInInches, topLeftXInInches, topLeftYInInches, label, null, textSizeInInches, readFromSide,
           0, 360, rotationOffset, entries1, textColor2Rgb, entries2);
   }

   public Wheel(double diameterInInches, double topLeftXInInches, double topLeftYInInches, TextBlock label1,
                TextBlock label2, double textSizeInInches, ReadFromSide readFromSide, int startAngle, int arcAngle,
                int rotationOffset, String[] entries1, RGB textColor2Rgb, String[] entries2) {
      this.diameterInInches = diameterInInches;
      this.topLeftXInInches = topLeftXInInches;
      this.topLeftYInInches = topLeftYInInches;
      this.textSizeInInches = textSizeInInches;
      coverText.add(label1);
      if (label2 != null) {
         coverText.add(label2);
      }

      if (textSizeInInches <= .12) {
         borderInInches = 0.15;
         textStyle = SWT.BOLD;
      }
      else {
         borderInInches = .25;
      }

      this.readFromSide = readFromSide;
      this.startAngle = startAngle;
      this.arcAngle = arcAngle;
      int entryCount = entries1.length;
      if (entries2 != null) {
         entryCount += entries2.length;
      }
      rotationAngle = (rotationOffset * this.arcAngle) / entryCount;

      entries.addAll(Arrays.asList(entries1));
      if ((textColor2Rgb != null) && (entries2 != null)) {
         this.textColor2Rgb = textColor2Rgb;
         colorBoundaryIndex = entries1.length;
         entries.addAll(Arrays.asList(entries2));
      }
   }

   public void paintCoverLabels(GC gc, Display display, Image image, Rectangle invalideArea) {
      Color textColor  = new Color(display, textColorRgb);
      gc.setForeground(textColor);
      for (TextBlock text : coverText) {
         text.paint(gc, display, image, invalideArea, centerCircle);
      }
      textColor.dispose();
   }
   public void setLocation(double topLeftXInInches, double topLeftYInInches) {
      this.topLeftXInInches = topLeftXInInches;
      this.topLeftYInInches = topLeftYInInches;
   }
   public double getDiameterInInches() {
      return diameterInInches;
   }

   public void paint(GC gc, Display display, Image image, Rectangle invalideArea) {
      Point innerDiameterInPixels = convertInchesToPixels(INNER_HOLE_DIAMETER_IN_INCHES);
      Point outerDiameterInPixels = convertInchesToPixels(diameterInInches);
      Point outerTextEdgeDiameterInPixels = convertInchesToPixels(diameterInInches - borderInInches);
      Point topLeftInPixels = convertInchesToPixels(topLeftXInInches, topLeftYInInches);
      centerCircle = new Point((outerDiameterInPixels.x / 2) + topLeftInPixels.x,
                                (outerDiameterInPixels.y/2) + topLeftInPixels.y);

      if ((mouseMoveStartLoc_ != null) && (mouseMoveEndLoc_ != null)) {
         centerCircle.x += (mouseMoveEndLoc_.x - mouseMoveStartLoc_.x);
         centerCircle.y += (mouseMoveEndLoc_.y - mouseMoveStartLoc_.y);
      }

      Rectangle drawArea = new Rectangle(centerCircle.x - (outerDiameterInPixels.x / 2),
                                         centerCircle.y - (outerDiameterInPixels.y / 2),
                                         outerDiameterInPixels.x, outerDiameterInPixels.y);
      if ((invalideArea != null) && (!drawArea.intersects(invalideArea))) {
         return;
      }

      Color wheelColor = new Color(display, wheelColorRgb);
      Color textColor  = new Color(display, textColorRgb);

      // draw the outer circle
      int lineWidth = Math.round(lineWidthInInches * DPI);
      drawCircle(gc, display, outsideRgb, edgeColorRgb, wheelColorRgb, centerCircle, outerDiameterInPixels,
                 lineWidth, startAngle, arcAngle);
      if (arcAngle != 360) {
         // draw the small Outer circle
         Point smallOuterDiameterInPixels = new Point (innerDiameterInPixels.x * 2, innerDiameterInPixels.y * 2);
         drawCircle(gc, display, outsideRgb, edgeColorRgb, wheelColorRgb, centerCircle, smallOuterDiameterInPixels,
                    lineWidth, 0, 360);

         // compute the end points of the large outer arc
         double outerRadius = (outerDiameterInPixels.x + outerDiameterInPixels.y)/4d;
         double sin = Math.sin(Math.toRadians(startAngle));
         double cos = Math.cos(Math.toRadians(startAngle));
         Point startEdgeOuter = new Point((int)Math.round(centerCircle.x + (cos * outerRadius)),
                                          (int)Math.round(centerCircle.y - (sin * outerRadius)));
         sin = Math.sin(Math.toRadians(startAngle + arcAngle));
         cos = Math.cos(Math.toRadians(startAngle + arcAngle));
         Point endEdgeOuter = new Point((int)Math.round(centerCircle.x + (cos * outerRadius)),
                                        (int)Math.round(centerCircle.y - (sin * outerRadius)));

         gc.drawLine(startEdgeOuter.x, startEdgeOuter.y, centerCircle.x, centerCircle.y);
         gc.drawLine(endEdgeOuter.x, endEdgeOuter.y, centerCircle.x, centerCircle.y);
         // equation of line:                X1 = m1Y1 + C1
         // equation of circle:             (X2 - c.x)^2 + (Y2 - c.y)^2 = r^2
         // equation of radius:              X2 = m2Y2 + C2
         // relation between line & radius   m1 = 1/m2
         // removing m2:                     X2 = Y2/m1 + C2   ;   X1 = m1Y1 + C1    ;    (X2 - c.x)^2 + (Y2 - c.y)^2 = r^2
         // removing X2:                     X1 = m1Y1 + C1    ;    ((Y2/m1 + C2) - c.x)^2 + (Y2 - c.y)^2 = r^2

      }
      // draw the inner circle, which is always a full circle
      drawInnercircle(gc, display, image, invalideArea);

      gc.setBackground(wheelColor);
      gc.setForeground(textColor);

      int textSize = (int) Math.round(textSizeInInches * DPI);
      Font fontMain = new Font(gc.getDevice(), FONT_NAME, textSize, textStyle);
      Font fontSmaller = new Font(gc.getDevice(), FONT_NAME, (textSize * 8) / 10, textStyle);
      gc.setFont(fontMain);
      gc.setTextAntialias(SWT.ON);

      Transform transform = new Transform(display);

      float angleInDegrees = 0;
      double angleIncrementInDegrees = ((double) arcAngle) / entries.size();
      int index = 0;
      Point maxTextExtent = new Point(0,0);
      for (String text : entries) {
         int indexTab = text.indexOf('\t');
         if (indexTab != -1) {
            text = text.substring(0, indexTab);
         }
         Point textExtent = gc.textExtent(text);
         if (textExtent.x > maxTextExtent.x) {
            maxTextExtent.x = textExtent.x;
         }
         if (textExtent.y > maxTextExtent.y) {
            maxTextExtent.y = textExtent.y;
         }
      }
      //Point dpi = display.getDPI();
      double maxTextExtentInInches = ((double)maxTextExtent.x) / DPI;

      Point innertextEdgeDiameterInPixels = convertInchesToPixels(diameterInInches
                                                                  - maxTextExtentInInches - (3 * borderInInches));

      mainTextRegion = null;
      innerTextRegion = null;
      for (String text : entries) {
         if (index++ == colorBoundaryIndex) {
            textColor.dispose();
            textColor = new Color(display, textColor2Rgb);
            gc.setForeground(textColor);
         }

         int indexTab = text.indexOf('\t');
         String textAfterTab = null;
         if (indexTab != -1) {
            textAfterTab = text.substring(indexTab + 1);
            text = text.substring(0, indexTab);
         }
         Point textExtent = gc.textExtent(text);

         transform.identity();
         transform.translate(centerCircle.x, centerCircle.y);
         transform.rotate(angleInDegrees + rotationAngle);
         int xOffset = 0;
         int yOffset = 0;
         if (readFromSide == ReadFromSide.RIGHT) {
            xOffset = (outerTextEdgeDiameterInPixels.x/2)-(textExtent.x/2);
         }
         else if (readFromSide == ReadFromSide.LEFT) {
            xOffset = (-(outerTextEdgeDiameterInPixels.x / 2)) + (textExtent.x / 2);
         }
         else if (readFromSide == ReadFromSide.TOP) {
            yOffset = (-(outerTextEdgeDiameterInPixels.y / 2)) + (textExtent.y / 2);
         }
         else if (readFromSide == ReadFromSide.BOTTOM) {
            yOffset = (outerTextEdgeDiameterInPixels.y/2)-(textExtent.y/2);
         }
         transform.translate(xOffset, yOffset);

         Rectangle textRegion = new Rectangle((centerCircle.x + xOffset) - (textExtent.x / 2),
                                              (centerCircle.y + yOffset) - (textExtent.y / 2),
                                              textExtent.x, textExtent.y);
         if (mainTextRegion == null) {
            mainTextRegion = textRegion;
         }
         else {
            mainTextRegion = mainTextRegion.union(textRegion);
         }

         gc.setTransform(transform );

         // Draw the text with the center of the text at {0,0}
         gc.drawText(text, -textExtent.x/2, -textExtent.y/2);

         if ((textAfterTab != null) && (textAfterTab.length() > 0)) {
            gc.setFont(fontSmaller);
            textExtent = gc.textExtent(textAfterTab);

            transform.identity();
            transform.translate(centerCircle.x, centerCircle.y);
            transform.rotate(angleInDegrees + rotationAngle);
            Point innerTextDiameterInPixels = new Point(innertextEdgeDiameterInPixels.x - textExtent.x,
                                                        innertextEdgeDiameterInPixels.y - textExtent.y);
            if (readFromSide == ReadFromSide.RIGHT) {
               xOffset = ((innerTextDiameterInPixels.x/2)-(textExtent.x/2));
            }
            else if (readFromSide == ReadFromSide.LEFT) {
               xOffset = ((-innerTextDiameterInPixels.x/2)+(textExtent.x/2));
            }
            else if (readFromSide == ReadFromSide.TOP) {
               yOffset = ((-(innerTextDiameterInPixels.y / 2)) + (textExtent.y / 2));
            }
            else if (readFromSide == ReadFromSide.BOTTOM) {
               yOffset = ((innerTextDiameterInPixels.y/2)-(textExtent.y/2));
            }
            transform.translate(xOffset, yOffset);

            textRegion = new Rectangle((centerCircle.x + xOffset) - (textExtent.x / 2),
                                       (centerCircle.y + yOffset) - (textExtent.y / 2),
                                       textExtent.x, textExtent.y);
            if (innerTextRegion == null) {
               innerTextRegion = textRegion;
            }
            else {
               innerTextRegion = innerTextRegion.union(textRegion);
            }

            gc.setTransform(transform );

            // Draw the text with the center of the text at {0,0}
            gc.drawText(textAfterTab, -textExtent.x/2, -textExtent.y/2);

            gc.setFont(fontMain);
         }

         angleInDegrees += angleIncrementInDegrees;
      }
      if (drawDotsBetweenEvery > 0) {
         angleIncrementInDegrees = 360.0/(entries.size() * drawDotsBetweenEvery);
         index = 0;
         for (double dotAngle = 0.0 ; dotAngle<360.0 ; dotAngle += angleIncrementInDegrees) {
            transform.identity();
            transform.translate(centerCircle.x, centerCircle.y);
            transform.rotate((float)dotAngle + rotationAngle);
            if (readFromSide == ReadFromSide.RIGHT) {
               transform.translate( (outerTextEdgeDiameterInPixels.x/2f)-maxTextExtent.x, 0);
            }
            else if (readFromSide == ReadFromSide.LEFT) {
               transform.translate((-outerTextEdgeDiameterInPixels.x/2f)+maxTextExtent.x, 0);
            }
            else if (readFromSide == ReadFromSide.TOP) {
               transform.translate(0, (-(outerTextEdgeDiameterInPixels.y/2f)) + maxTextExtent.y);
            }
            else if (readFromSide == ReadFromSide.BOTTOM) {
               transform.translate(0,   (outerTextEdgeDiameterInPixels.y/2f)-maxTextExtent.y);
            }

            gc.setTransform(transform );

            if ((index++ % drawDotsBetweenEvery) == 0) {
               gc.setBackground(textColor);
               int scale = (int) Math.round( DPI / 33d);
               if (readFromSide == ReadFromSide.RIGHT) {
                  gc.drawPolygon(new int[] {-1*scale,-2*scale,  -1*scale, 2*scale, scale, 0, -1 * scale, -2 * scale,});
               }
               else if (readFromSide == ReadFromSide.LEFT) {
                  gc.drawPolygon(new int[] {scale, -2 * scale, scale, 2 * scale, -1 * scale, 0, scale, -2 * scale,});
               }
               else if (readFromSide == ReadFromSide.TOP) {
                  gc.drawPolygon(new int[] {-1*scale, scale, scale, scale, 0, -1 * scale, -1 * scale, scale,});
               }
               else if (readFromSide == ReadFromSide.BOTTOM) {
                  gc.fillPolygon(new int[] {-1*scale,-1*scale, scale, -1 * scale, 0, scale, -1 * scale, -1 * scale,});
               }
               gc.setBackground(wheelColor);
            }
            else {
               int scale = (int) Math.round( DPI / 150d);
               if (readFromSide == ReadFromSide.RIGHT) {
                  gc.drawRectangle(-scale,-scale/2, 2*-scale, -scale);
               }
               else if (readFromSide == ReadFromSide.LEFT) {
                  gc.drawRectangle(-scale,-scale/2, 2*-scale, -scale);
               }
               else if (readFromSide == ReadFromSide.TOP) {
                  gc.drawRectangle(-scale/2,-scale, -scale, 2*-scale);
               }
               else if (readFromSide == ReadFromSide.BOTTOM) {
                  gc.drawRectangle(-scale/2,-scale, -scale, 2*-scale);
               }
            }
         }
      }
      transform.identity();
      gc.setTransform(transform );

      fontMain.dispose();
      fontSmaller.dispose();
      transform.dispose();
      wheelColor.dispose();
      textColor.dispose();
   }

   public static Point convertInchesToPixels(double diameterInInches) {
      return convertInchesToPixels(diameterInInches, diameterInInches);
   }
   public static Point convertInchesToPixels(double x, double y) {
      return new Point((int)(Math.round(DPI * x)),
                       (int)(Math.round(DPI * y)));
   }


   public static void drawCircle(GC gc, Display display, RGB outerColorRgb, RGB lineColorRgb, RGB innerColorRgb,
                                 Point centerCircle, Point diameterInPixels, int maxLineWidth,
                                 int startAngle, int arcAngle) {
      Color bgColor = new Color(display, innerColorRgb );
      gc.setBackground(bgColor);
      gc.fillArc(centerCircle.x - (diameterInPixels.x/2), centerCircle.y - (diameterInPixels.y/2),
                 diameterInPixels.x, diameterInPixels.y, startAngle, arcAngle);

      drawAntiAliasedCircle(gc, display, outerColorRgb, lineColorRgb, centerCircle, diameterInPixels, maxLineWidth, startAngle, arcAngle);
      Point smallerDiameter = new Point(diameterInPixels.x - 1, diameterInPixels.y - 1);
      drawAntiAliasedCircle(gc, display, innerColorRgb, lineColorRgb, centerCircle, smallerDiameter,
                            maxLineWidth-1, startAngle, arcAngle);

      // now draw the main line again.
      // This over-writes the outer edge of the inner circle, which we don't want to see.
      Color lineColor = new Color(display, lineColorRgb );
      gc.setForeground(lineColor);
      gc.setLineAttributes(new LineAttributes(maxLineWidth-1));
      gc.drawArc(centerCircle.x - (diameterInPixels.x/2), centerCircle.y - (diameterInPixels.y/2),
                 diameterInPixels.x, diameterInPixels.y, startAngle, arcAngle);
      lineColor.dispose();
      bgColor.dispose();
   }


   public static void drawAntiAliasedCircle(GC gc, Display display, RGB outerColorRgb, RGB lineColorRgb,
                                            Point centerCircle, Point diameterInPixels, int maxLineWidth,
                                            int startAngle, int arcAngle) {
      Color[] colorOuterToLine = new Color[] {new Color(display, mixColors(lineColorRgb, outerColorRgb, .25)),
                                               new Color(display, mixColors(lineColorRgb, outerColorRgb, .50)),
                                               new Color(display, mixColors(lineColorRgb, outerColorRgb, .75)),
                                               new Color(display, lineColorRgb ),
      };

      // draw the outer-most rings inward, using progressively smaller circles:
      float lineWidth = maxLineWidth;
      for (Color color : colorOuterToLine) {
         gc.setForeground(color);
         gc.setLineAttributes(new LineAttributes(lineWidth));
         gc.drawArc(centerCircle.x - (diameterInPixels.x/2), centerCircle.y - (diameterInPixels.y/2), diameterInPixels.x, diameterInPixels.y, startAngle, arcAngle);
         color.dispose();
         lineWidth -= (1.0 / colorOuterToLine.length);
      }
   }


   public static RGB mixColors(RGB rgb1, RGB rgb2, double percent1) {
      double percent2 = 1.0 - percent1;
      int red   = (int) Math.round((rgb1.red   * percent1) + (rgb2.red   * percent2));
      int green = (int) Math.round((rgb1.green * percent1) + (rgb2.green * percent2));
      int blue  = (int) Math.round((rgb1.blue  * percent1) + (rgb2.blue  * percent2));
      return new RGB(red, green, blue);
   }

   public boolean isAtLocation(int x, int y) {
      Point outerDiameterInPixels = convertInchesToPixels(diameterInInches);
      if (centerCircle == null) {
         return false;
      }

      int distSquared = ((centerCircle.x - x) * (centerCircle.x - x)) +
                        ((centerCircle.y - y) * (centerCircle.y - y));
      int aveRadius = (outerDiameterInPixels.x + outerDiameterInPixels.y)/4;
      int radiusSquared = aveRadius * aveRadius;
      if (radiusSquared >= distSquared) {
         if (arcAngle == 360) {
            return true;
         }

         double angleInRadsFromCenter = Math.atan2((y - centerCircle.y), (x - centerCircle.x));
         double degrees = 0 - Math.toDegrees(angleInRadsFromCenter);
         if (degrees < 0 ) {
            degrees += 360;
         }
         return (degrees > startAngle) && (degrees < (startAngle + arcAngle));
      }
      return false;
   }

   Point mouseMoveStartLoc_ = null;
   Point mouseMoveEndLoc_ = null;
   public void setMoveStart(int x, int y) {
      mouseMoveStartLoc_ = new Point(x, y);
   }
   public void move(MouseEvent e) {
      mouseMoveEndLoc_ = new Point(e.x, e.y);
   }
   public void onMouseUp(Display display, Canvas canvas) {
      boolean redraw = false;
      Point outerDiameterInPixels = convertInchesToPixels(diameterInInches);
      if ((centerCircle != null) && (mouseMoveEndLoc_ != null) && (mouseMoveStartLoc_ != null)) {
         //Point dpi = display.getDPI();
         topLeftXInInches += ((double)(mouseMoveEndLoc_.x - mouseMoveStartLoc_.x)) / DPI;
         topLeftYInInches += ((double)(mouseMoveEndLoc_.y - mouseMoveStartLoc_.y)) / DPI;
         System.out.println(coverText.get(0).getText() + " at {" + topLeftXInInches + ", " + topLeftYInInches);
         redraw = true;
      }
      mouseMoveStartLoc_ = null;
      mouseMoveEndLoc_ = null;
      if (redraw) {
         redraw(canvas);
      }
   }

   public void redraw(Canvas canvas) {
      Point outerDiameterInPixels = convertInchesToPixels(diameterInInches);
      canvas.redraw(centerCircle.x - (outerDiameterInPixels.x / 2) - 5,
                    centerCircle.y - (outerDiameterInPixels.y / 2) - 5,
                    outerDiameterInPixels.x + 10,
                    outerDiameterInPixels.y + 10, false);
   }
   public Rectangle getBoundingRect() {
      Point outerDiameterInPixels = convertInchesToPixels(diameterInInches);
      Rectangle result;
      int radiusX = outerDiameterInPixels.x/2;
      int radiusY = outerDiameterInPixels.y/2;
      Point outerTextEdgeDiameterInPixels = convertInchesToPixels(diameterInInches - (borderInInches / 2));
      Point borderEdgeInPixels = new Point((outerDiameterInPixels.x - outerTextEdgeDiameterInPixels.x),
                                           (outerDiameterInPixels.y - outerTextEdgeDiameterInPixels.y));

      Point topLeftInPixels = convertInchesToPixels(topLeftXInInches, topLeftYInInches);
      centerCircle = new Point((outerDiameterInPixels.x / 2) + topLeftInPixels.x,
                                (outerDiameterInPixels.y/2) + topLeftInPixels.y);
      if (arcAngle == 360) {
         int minX = centerCircle.x - radiusX;
         int minY = centerCircle.y - radiusY;
         int maxX = minX + outerDiameterInPixels.x;
         int maxY = minY + outerDiameterInPixels.y;
         if (readFromSide == ReadFromSide.RIGHT) {
            maxX -= borderEdgeInPixels.x;
         }
         else if (readFromSide == ReadFromSide.LEFT) {
            minX += borderEdgeInPixels.x;
         }
         else if (readFromSide == ReadFromSide.TOP) {
            minY += borderEdgeInPixels.y;
         }
         else if (readFromSide == ReadFromSide.BOTTOM) {
            maxY -= borderEdgeInPixels.y;
         }
         result = new Rectangle(minX, minY, maxX - minX, maxY-minY);

      }
      else {
         Point innerDiameterInPixels = convertInchesToPixels(INNER_HOLE_DIAMETER_IN_INCHES);
         result = new Rectangle(centerCircle.x - (innerDiameterInPixels.x / 2),
                                centerCircle.y - (innerDiameterInPixels.y / 2),
                                innerDiameterInPixels.x, innerDiameterInPixels.y);

         int textSize = (int) Math.round(textSizeInInches * DPI);
         Rectangle textRegion = new Rectangle(centerCircle.x - (outerTextEdgeDiameterInPixels.x / 2),
                                              centerCircle.y - textSize,
                                              outerTextEdgeDiameterInPixels.x/2, textSize * 2);
         result = result.union(textRegion);
      }
      return result;
   }
   public void drawInnercircle(GC gc, Display display, Image image, Rectangle invalideArea) {
      Point innerDiameterInPixels = convertInchesToPixels(INNER_HOLE_DIAMETER_IN_INCHES);
      Point outerDiameterInPixels = convertInchesToPixels(diameterInInches);
      Rectangle drawArea = new Rectangle(centerCircle.x - (innerDiameterInPixels.x / 2),
                                         centerCircle.y - (innerDiameterInPixels.y / 2),
                                         outerDiameterInPixels.x, outerDiameterInPixels.y);
      if ((invalideArea != null) && (!drawArea.intersects(invalideArea))) {
         return;
      }

      // draw the inner circle, which is always a full circle
      int lineWidth = Math.round(lineWidthInInches * DPI);
      drawCircle(gc, display, wheelColorRgb, edgeColorRgb, outsideRgb, centerCircle, innerDiameterInPixels,
                 lineWidth, 0, 360);
   }
   public ReadFromSide getReadFromSide() {
      return readFromSide;
   }
   public Rectangle getMainTextRegion() {
      if ((drawDotsBetweenEvery > 0) && (mainTextRegion != null)) {
         return new Rectangle(mainTextRegion.x - (mainTextRegion.width / 2),
                              mainTextRegion.y - (mainTextRegion.height / 4),
                              mainTextRegion.width * 2, (int)(mainTextRegion.height * 1.25));
      }
      return mainTextRegion;
   }
   public Rectangle getInnerTextRegion() {
      return innerTextRegion;
   }
}
