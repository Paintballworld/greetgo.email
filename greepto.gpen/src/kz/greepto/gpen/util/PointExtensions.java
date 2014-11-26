package kz.greepto.gpen.util;

import org.eclipse.swt.graphics.Point;
import org.eclipse.xtext.xbase.lib.Inline;
import org.eclipse.xtext.xbase.lib.Pure;

import com.google.common.annotations.GwtCompatible;

@GwtCompatible
public class PointExtensions {
  
  @Pure
  @Inline("new org.eclipse.swt.graphics.Point(-$1.x, -$1.y)")
  public static Point operator_minus(Point a) {
    return new Point(-a.x, -a.y);
  }
  
  @Pure
  @Inline("$1")
  public static Point operator_plus(Point a) {
    return a;
  }
  
  @Pure
  @Inline("new org.eclipse.swt.graphics.Point($1.x + $2.x, $1.y + $2.y)")
  public static Point operator_plus(Point a, Point b) {
    return new Point(a.x + b.x, a.y + b.y);
  }
  
  @Pure
  @Inline("new org.eclipse.swt.graphics.Point($1.x - $2.x, $1.y - $2.y)")
  public static Point operator_minus(Point a, Point b) {
    return new Point(a.x - b.x, a.y - b.y);
  }
  
  @Pure
  @Inline("new org.eclipse.swt.graphics.Point($1 * $2.x, $1 * $2.y)")
  public static Point operator_multiply(int a, Point b) {
    return new Point(a * b.x, a * b.y);
  }
  
  @Pure
  @Inline("new org.eclipse.swt.graphics.Point($1.x * $2, $1.y * $2)")
  public static Point operator_multiply(Point a, int b) {
    return new Point(a.x * b, a.y * b);
  }
  
}