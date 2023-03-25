package ostrowski.com;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

public class Spinner implements Listener, MouseListener, MouseMoveListener, KeyListener
{
   /**
    * @param args
    */
   public static void main(String[] args) {
      Spinner spinner = new Spinner();
      spinner.execute();
   }

   Shell shell_;
   Canvas canvas_;
   Cover mainCover_ = new Cover();
   Cover advancedCover_ = new Cover();
   List<Wheel> wheels_ = new ArrayList<>();
   private Wheel wheelHeld;
   Point topLeft = new Point(0,0);

   public Spinner() {
      Display display = new Display();
      shell_ = new Shell(display);
      shell_.setText("spinner"); // the window's title
      shell_.setLayout(new FillLayout());

      // Pain
      // wounds
      // actions
      // initiative
      // magic points
      // position
      // fatigue
      // left-arm injuries
      // right-arm injuries
      // movement penalties
      // retreat penalties

      Wheel actions     = new Wheel(1.5, 1.0, 0.1, new TextBlock("Actions", .20/*labelTextSizeInInches*/, 0.0/*labelXLocInInches*/, -0.29 /*labelYLocInInches*/),
                                    0.16/*textSizeInInches*/, Wheel.ReadFromSide.TOP, 6/*rotationOffset*/,
                                    getStringArrayWithSpace(0, 8));
      Wheel initiative  = new Wheel(2.5, 1.95, 0.1, new TextBlock("Initiative", .20/*labelTextSizeInInches*/, 0.0/*labelXLocInInches*/, -0.75 /*labelYLocInInches*/),
                                    0.16/*textSizeInInches*/, Wheel.ReadFromSide.TOP, 11/*rotationOffset*/,
                                    getStringArrayWithSpace(-7, 14));
      Wheel wounds      = new Wheel(1.5, 4.15, .27, new TextBlock("Wounds", .20/*labelTextSizeInInches*/, 0.0/*labelXLocInInches*/, -0.35 /*labelYLocInInches*/),
                                    0.18/*textSizeInInches*/, Wheel.ReadFromSide.RIGHT, 0/*rotationOffset*/,
                                    getStringArrayWithSpace(0, 12));
      Wheel pain        = new Wheel(2.5, 3.13, 1.1, new TextBlock("Pain", .20/*labelTextSizeInInches*/, 0.5/*labelXLocInInches*/, -0.35 /*labelYLocInInches*/),
                                    0.18/*textSizeInInches*/, Wheel.ReadFromSide.RIGHT, 0/*rotationOffset*/,
                                    getStringArray(0, 9), new RGB(192, 0, 0), getStringArray(10, 19));
      Wheel weaponState = new Wheel(1.5, 4.15, 2.9, new TextBlock("Weapon", .20/*labelTextSizeInInches*/, 0.0/*labelXLocInInches*/, -0.4 /*labelYLocInInches*/),
                                    0.10/*textSizeInInches*/, Wheel.ReadFromSide.RIGHT, 6/*rotationOffset*/,
                                    new String[] {""
                                                  ,"4 act."
                                                  ,"3 act."
                                                  ,"2 act."
                                                  ,"1 act."
                                                  ,"ready"
                                                  ,"loaded"
                                                  ,"raised"
                                                  ,"aim-1"
                                                  ,"aim-2"
                                                  ,"aim-3"});
      Wheel magicPoints = new Wheel(3.0, 1.23, 1.8, new TextBlock("Magic\nPoints", .20/*labelTextSizeInInches*/, 0.0/*labelXLocInInches*/, 0.5 /*labelYLocInInches*/),
                                    0.14/*textSizeInInches*/, Wheel.ReadFromSide.BOTTOM, 0/*rotationOffset*/,
                                    5/*drawDotsBetweenEvery*/, getStringArray(0, 95, 5, false));
      Wheel miscMage    = new Wheel(1.5, 0.5, 3.25, new TextBlock("Misc.", .20/*labelTextSizeInInches*/, 0.0/*labelXLocInInches*/, 0.3 /*labelYLocInInches*/),
                                    0.12/*textSizeInInches*/, Wheel.ReadFromSide.BOTTOM, 6/*rotationOffset*/,
                                    new String[] {"-5", "-4", "-3", "-2", "-1", "--", "+1", "+2", "+3", "+4", "+5", ""});
      Wheel miscWarrior = new Wheel(2.0, 1.6, 2.55, new TextBlock("Misc.", .20/*labelTextSizeInInches*/, 0.0/*labelXLocInInches*/, 0.4 /*labelYLocInInches*/),
                                    0.14/*textSizeInInches*/, Wheel.ReadFromSide.BOTTOM, 10/*rotationOffset*/,
                                    new String[] {"-9", "-8", "-7", "-6", "-5", "-4", "-3", "-2", "-1", "--", "+1", "+2", "+3", "+4", "+5", "+6", "+7", "+8", "+9", ""});
      Wheel position    = new Wheel(8.5, 0.1333, -1.9,
                                    new TextBlock("Position", .20/*labelTextSizeInInches*/, -3.5/*labelXLocInInches*/, 0.4 /*labelYLocInInches*/),
                                    new TextBlock("attack\n    parry / block\n        dodge\n            retreat", .10/*labelTextSizeInInches*/, -2.5/*labelXLocInInches*/, -0.4 /*labelYLocInInches*/, -45),
                                    0.16/*textSizeInInches*/, Wheel.ReadFromSide.LEFT, 152, 54, -5/*rotationOffset*/,
                                    new String[] {
                                                  "On belly"  + "\t --  -- -6 -6",
                                                  "On back"   + "\t-4 -4 -4 -4",
                                                  "Sitting"   + "\t-4  0 -4 -4",
                                                  "Kneeling"  + "\t 0  0 -4 -4",
                                                  "Crouching" + "\t 0  0  0 -4",
                                                  "Standing"  + "\t 0  0  0  0",
                                                  "Crouching" + "\t 0  0  0 -4",
                                                  "Kneeling"  + "\t 0  0 -4 -4",
                                                  "Sitting"   + "\t-4  0 -4 -4",
                                                  "On back"   + "\t-4 -4 -4 -4",
                                                  "On belly"  + "\t --  -- -6 -6",
                                                  ""});

      // advanced wheels:
      Wheel leftArm   = new Wheel(1.0, 1.2, 4.85, new TextBlock("Left-arm", .15/*labelTextSizeInInches*/, -0.15/*labelXLocInInches*/, 0.25 /*labelYLocInInches*/),
                                  0.10/*textSizeInInches*/, Wheel.ReadFromSide.TOP, 0/*rotationOffset*/,
                                  new String[] {"0", "-1", "-2", "-3" , "", "Crip.", "", "Svrd", "", "", ""});
      Wheel rightArm  = new Wheel(1.0, 1.85, 4.85, new TextBlock("Right-arm", .15/*labelTextSizeInInches*/, 0.15/*labelXLocInInches*/, 0.25 /*labelYLocInInches*/),
                                  0.10/*textSizeInInches*/, Wheel.ReadFromSide.TOP, 0/*rotationOffset*/,
                                  new String[] {"0", "-1", "-2", "-3" , "", "Crip.", "", "Svrd", "", "", ""});

      Wheel movement  = new Wheel(1.0, 2.8, 4.85, new TextBlock("Move /\nRetreat", .15/*labelTextSizeInInches*/, 0.65/*labelXLocInInches*/, -0.12 /*labelYLocInInches*/),
                                  0.10/*textSizeInInches*/, Wheel.ReadFromSide.TOP, 0/*rotationOffset*/,
                                  new String[] {"0", "-1", "-2", "-3" , "", "Crip.", "", "Svrd", "", "", ""});

      Wheel bloodLoss = new Wheel(3.0, 2.85, 5.15, new TextBlock("Blood\n Lost", .20/*labelTextSizeInInches*/, 0.05/*labelXLocInInches*/, 0.8 /*labelYLocInInches*/),
                                  0.14/*textSizeInInches*/, Wheel.ReadFromSide.BOTTOM, 0/*rotationOffset*/,
                                  getStringArrayWithSpace(0, 20));

      Wheel bleeding  = new Wheel(1.5, 2.1, 6.65, new TextBlock("Bleeding", .20/*labelTextSizeInInches*/, -0.75/*labelXLocInInches*/, 0.25 /*labelYLocInInches*/),
                                  0.14/*textSizeInInches*/, Wheel.ReadFromSide.BOTTOM, 0/*rotationOffset*/,
                                  getStringArrayWithSpace(0, 12));

      Wheel fatigue   = new Wheel(7.0, 0.85, 3.15,
                                  new TextBlock("Fatigue", .20/*labelTextSizeInInches*/, -2.75/*labelXLocInInches*/, 0.35 /*labelYLocInInches*/),
                                  new TextBlock("weapon speed\n    damage\n        actions / turn\n            move", .08/*labelTextSizeInInches*/, -1.85/*labelXLocInInches*/, -0.3 /*labelYLocInInches*/, -45),
                                  0.14/*textSizeInInches*/, Wheel.ReadFromSide.LEFT, 156, 48, -5/*rotationOffset*/,
                                  new String[] { "Breathless" + "\t+2 -5 -4 -3"
                                                ,"Exhausted"  + "\t+2 -4 -3 -2"
                                                ,"Weak"       + "\t+1 -3 -2 -2"
                                                ,"Tired"      + "\t+1 -2 -1 -1"
                                                ,"Winded"     + "\t  -  -1  -  -1"
                                                ,"Fine"       + "\t  -   -   -   -"
                                                ,"Winded"     + "\t  -  -1  -  -1"
                                                ,"Tired"      + "\t+1 -2 -1 -1"
                                                ,"Weak"       + "\t+1 -3 -2 -2"
                                                ,"Exhausted"  + "\t+2 -4 -3 -2"
                                                ,"Breathless" + "\t+2 -5 -4 -3"
                                                ,""});

      boolean mage = true;
      wheels_.add(actions);
      wheels_.add(initiative);
      wheels_.add(wounds);
      wheels_.add(pain);
      wheels_.add(weaponState);
      if (mage) {
         wheels_.add(magicPoints);
         wheels_.add(miscMage);
      }
      else {
         wheels_.add(miscWarrior);
      }
      wheels_.add(position);

      mainCover_.addWheel(actions);
      mainCover_.addWheel(initiative);
      mainCover_.addWheel(wounds);
      mainCover_.addWheel(pain);
      mainCover_.addWheel(weaponState);
      if (mage) {
         mainCover_.addWheel(magicPoints);
         mainCover_.addWheel(miscMage);
      }
      else {
         mainCover_.addWheel(miscWarrior);
      }
      mainCover_.addWheel(position);

      wheels_.add(leftArm);
      wheels_.add(rightArm);
      wheels_.add(movement);
      wheels_.add(bloodLoss);
      wheels_.add(bleeding);
      wheels_.add(fatigue);

      advancedCover_.addWheel(leftArm);
      advancedCover_.addWheel(rightArm);
      advancedCover_.addWheel(movement);
      advancedCover_.addWheel(bloodLoss);
      advancedCover_.addWheel(bleeding);
      advancedCover_.addWheel(fatigue);

      if (false) {
         List<Wheel[]> rowOfWheels = new ArrayList<>();
         rowOfWheels.add(new Wheel[] {leftArm, rightArm, movement}); // 1.0 diameters
         rowOfWheels.add(new Wheel[] {wounds, actions, miscMage, bleeding}); // 1.5 diameter
         rowOfWheels.add(new Wheel[] {pain, initiative, magicPoints, bloodLoss}); // 2.5, 2.5, 3.0, 3.0
         rowOfWheels.add(new Wheel[] {fatigue, position}); // 7.0, 8.5
         double buffer = 0.25;
         double topLeftY = buffer;
         for (Wheel[] row : rowOfWheels) {
            double topLeftX = buffer;
            double maxDiameter = 0.0;
            for (Wheel wheel : row) {
               if (wheel == fatigue) {
                  topLeftY -= 2.5;
               }
               if (wheel == position) {
                  topLeftX -= 3.2;
               }
               wheel.setLocation(topLeftX, topLeftY);
               double diameter = wheel.getDiameterInInches();
               topLeftX += diameter + buffer;
               if (diameter > maxDiameter) {
                  if ((wheel != fatigue) && (wheel != position)) {
                     maxDiameter = diameter;
                  }
               }
            }
            topLeftY += maxDiameter + buffer;
         }
      }
      canvas_ = new Canvas(shell_, SWT.BORDER);
      canvas_.addListener(SWT.Paint, this);
      canvas_.addMouseListener(this);
      canvas_.addMouseMoveListener(this);

      shell_.setSize(800, 600);
      shell_.addKeyListener(this);

      shell_.pack();
      shell_.open ();
   }
   public String[] getStringArray(int start, int end) {
      return getStringArray(start, end, 1, false);
   }
   public String[] getStringArrayWithSpace(int start, int end) {
      return getStringArray(start, end, 1, true);
   }
   public String[] getStringArray(int start, int end, int increment, boolean withSpace) {
      String[] result = new String[1 + ((end - start) / increment) + (withSpace ? 1 : 0)];
      for (int i=start, j=0 ; i<=end ; i += increment, j++) {
         result[j] = String.valueOf(i);
      }
      if (withSpace) {
         result[result.length-1] = " ";
      }

      return result;
   }

   public void execute() {
      Display display = shell_.getDisplay();
      while (!shell_.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep ();
         }
      }
      display.dispose ();
   }

   @Override
   public void handleEvent(Event event) {
      if (event.type == SWT.Paint) {
         Display display = event.display;

         Rectangle bounds = canvas_.getBounds();
         bounds.width += topLeft.x;
         bounds.height += topLeft.y;
         Image image = new Image(event.display, bounds);
         // Setup an off-screen GC, onto which all drawing is done.
         // This will later be transferred to the events CG (real screen)
         // This allows double-buffering of the image, to reduce flashing in any animation
         GC gc = new GC(image);
         //gc.setAntialias(SWT.ON);

         Rectangle rect = new Rectangle(event.x + topLeft.x, event.y + topLeft.y, event.width, event.height);
         for (Wheel wheel : wheels_) {
            wheel.paint(gc, display, image, (mode_ == 1) ? null : rect);
         }
         if (mode_ == 1) {
            gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
            gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
            gc.fillRectangle(rect);
         }
         if (mode_ != 2) {
            mainCover_.paint(gc, display, image, rect);
            advancedCover_.paint(gc, display, image, rect);
         }

         // Draw the off-screen buffer to the screen
         event.gc.drawImage(image, -topLeft.x, -topLeft.y);

         gc.dispose();
         image.dispose();
      }
   }
   @Override
   public void mouseDoubleClick(MouseEvent e) {
   }
   @Override
   public void mouseDown(MouseEvent e) {
      if (wheelHeld != null) {
         wheelHeld.onMouseUp(e.display, canvas_);
      }
      for (Wheel wheel : wheels_) {
         if (wheel.isAtLocation(e.x + topLeft.x, e.y + topLeft.y)) {
            wheelHeld = wheel;
         }
      }
      if (wheelHeld != null) {
         // move this to the bottom of the list, so it appears on the top (because its drawn last):
         wheels_.remove(wheelHeld);
         wheels_.add(wheelHeld);
         wheelHeld.setMoveStart(e.x, e.y);
      }
   }
   @Override
   public void mouseUp(MouseEvent e) {
      if (wheelHeld != null) {
         wheelHeld.onMouseUp(e.display, canvas_);
         wheelHeld = null;
      }
   }
   @Override
   public void mouseMove(MouseEvent e) {
      if (wheelHeld != null) {
         wheelHeld.move(e);
         wheelHeld.redraw(canvas_);
      }
   }

   int zoom_ = 0;
   int mode_ = 0;
   @Override
   public void keyPressed(KeyEvent e) {
      boolean shiftHeld = (e.stateMask & SWT.SHIFT) != 0;
      int moveSize = shiftHeld ? 100 : 25;
      int oldZoom = zoom_;
      switch (e.keyCode) {
         case SWT.ARROW_UP:        topLeft.y = Math.max( 0, topLeft.y - moveSize); canvas_.redraw(); break;
         case SWT.ARROW_DOWN:      topLeft.y = Math.max( 0, topLeft.y + moveSize); canvas_.redraw(); break;
         case SWT.ARROW_LEFT:      topLeft.x = Math.max( 0, topLeft.x - moveSize); canvas_.redraw(); break;
         case SWT.ARROW_RIGHT:     topLeft.x = Math.max( 0, topLeft.x + moveSize); canvas_.redraw(); break;
         case SWT.PAGE_UP:         zoom_--; break;
         case SWT.PAGE_DOWN:       zoom_++; break;
         case SWT.KEYPAD_0:        mode_ = 0; canvas_.redraw(); break;
         case SWT.KEYPAD_1:        mode_ = 1; canvas_.redraw(); break;
         case SWT.KEYPAD_2:        mode_ = 2; canvas_.redraw(); break;
      }
      if (oldZoom != zoom_) {
         Wheel.DPI = (int) Math.round(Wheel.DPI_INITIAL * Math.pow(1.5, zoom_));
         canvas_.redraw();
      }
   }
   @Override
   public void keyReleased(KeyEvent e) {
   }
}
