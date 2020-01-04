package ostrowski.com;

import org.eclipse.swt.graphics.Point;

public class Line
{
   Point p1_;
   Point p2_;
   
   public Line(Point point1, Point point2) {
      p1_ = point1;
      p2_ = point2;
   }

   public Point intersect(Line other) {
      // y = p1.y + (p2.y-p1.y) * t  (t=0 at p1, t=1 at p2)
      // x = p1.x + (p2.x-p1.x) * t
      
      // x1 = p1.x + (p2.x-p1.x) * t
      // other.x1 = other.p1.x + (other.p2.x-other.p1.x) * other.t
      // x1 = other.x1
      // p1.x + (p2.x-p1.x) * t = other.p1.x + (other.p2.x-other.p1.x) * other.t
      // (p2.x-p1.x) * t = other.p1.x + (other.p2.x-other.p1.x) * other.t - p1.x
      // t = (other.p1.x + (other.p2.x-other.p1.x) * other.t - p1.x) / (p2.x-p1.x)
      // t = (other.p1.y + (other.p2.y-other.p1.y) * other.t - p1.y) / (p2.y-p1.y)
      
      // (other.p1.x + (other.p2.x-other.p1.x) * other.t - p1.x) / (p2.x-p1.x) = (other.p1.y + (other.p2.y-other.p1.y) * other.t - p1.y) / (p2.y-p1.y)
      // (other.p1.x - p1.x + (other.p2.x-other.p1.x) * other.t) * (p2.y-p1.y) = (other.p1.y - p1.y + (other.p2.y-other.p1.y) * other.t) * (p2.x-p1.x)
      // (other.p1.x - p1.x) * (p2.y-p1.y) + (other.p2.x-other.p1.x) * other.t * (p2.y-p1.y) = (other.p1.y - p1.y) * (p2.x-p1.x) + (other.p2.y-other.p1.y) * other.t * (p2.x-p1.x)
      // (other.p2.x-other.p1.x) * other.t * (p2.y-p1.y) = (other.p1.y - p1.y) * (p2.x-p1.x) + (other.p2.y-other.p1.y) * other.t * (p2.x-p1.x) - (other.p1.x - p1.x) * (p2.y-p1.y)
      // (other.p2.x-other.p1.x) * other.t * (p2.y-p1.y) - (other.p2.y-other.p1.y) * other.t * (p2.x-p1.x) = (other.p1.y - p1.y) * (p2.x-p1.x) - (other.p1.x - p1.x) * (p2.y-p1.y)
      // other.t * ((other.p2.x-other.p1.x) * (p2.y-p1.y) - (other.p2.y-other.p1.y) * (p2.x-p1.x)) = (other.p1.y - p1.y) * (p2.x-p1.x) - (other.p1.x - p1.x) * (p2.y-p1.y)
      // other.t = ((other.p1.y - p1.y) * (p2.x-p1.x) - (other.p1.x - p1.x) * (p2.y-p1.y)) / ((other.p2.x-other.p1.x) * (p2.y-p1.y) - (other.p2.y-other.p1.y) * (p2.x-p1.x))
      
      double otherT = ((double)((other.p1_.y - p1_.y) * (p2_.x-p1_.x) - (other.p1_.x - p1_.x) * (p2_.y-p1_.y))) / ((other.p2_.x-other.p1_.x) * (p2_.y-p1_.y) - (other.p2_.y-other.p1_.y) * (p2_.x-p1_.x));
      
      double x = other.p1_.x + (other.p2_.x - other.p1_.x) * otherT;
      double y = other.p1_.y + (other.p2_.y - other.p1_.y) * otherT;
      return new Point ((int)Math.round(x), (int)Math.round(y));
   }
//   public Point intersect(Line otherLine) {
//      //y = m1x + b1
//      //y = m2x + b2
//      //m1x + b1 = m2x + b2
//      //m1x - m2x  = b2 - b1
//      //x(m1-m2) = b2-b1
//      // x = (b2-b1) / (m1-m2)
//      double x = (otherLine.b - b) / (slope - otherLine.slope);
//      double y = slope * x + b;
//      return new Point ((int)Math.round(x), (int)Math.round(y));
//   }

}
