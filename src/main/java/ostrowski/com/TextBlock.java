package ostrowski.com;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Display;

public class TextBlock
{
   private String text_              = "";
   private double textSizeInInches_  = 0.20;
   private double locationXinInches_ = 0.0;
   private double locationYinInches_ = 0.0;
   private float  angle_             = 0.0F;
   private String fontName_          = "Arial";
   private int    textStyle_         = SWT.NONE;
   
   public TextBlock(String text, double textSizeInInches, double locationXinInches, double locationYinInches) {
      this (text, textSizeInInches, locationXinInches, locationYinInches, 0.0F);
   }
   public TextBlock(String text, double textSizeInInches, double locationXinInches, double locationYinInches, float angle) {
      text_ = text;
      textSizeInInches_ = textSizeInInches;
      locationXinInches_ = locationXinInches;
      locationYinInches_ = locationYinInches;
      angle_ = angle;
   }

   public void paint(GC gc, Display display, Image image, Rectangle invalideArea, Point centerOffset) {
      int textSize = (int) Math.round(textSizeInInches_ * Wheel.DPI);
      int locXInPixels = (int) Math.round(locationXinInches_ * Wheel.DPI) + centerOffset.x;
      int locYInPixels = (int) Math.round(locationYinInches_ * Wheel.DPI) + centerOffset.y;
      
      Font fontMain = new Font(gc.getDevice(), fontName_, textSize, textStyle_ );
      gc.setFont(fontMain);
      gc.setTextAntialias(SWT.ON);

      Point textExtent = gc.textExtent(text_);
      
      Transform transform = new Transform(display);
      transform.identity();
      transform.translate(locXInPixels, locYInPixels);
      transform.rotate(angle_);
      gc.setTransform(transform );
      // Draw the text with the center of the text at {0,0}
      gc.drawText(text_, -textExtent.x/2, -textExtent.y/2, true/*isTransparent*/);

      transform.identity();
      gc.setTransform(transform );

      fontMain.dispose();
   }
   public String getText() {
      return text_;
   }

}
