/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2016 Sebastian Dambeck & Luca Osten
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.mapsforge.core.util;
  	
import org.mapsforge.core.model.Point;
  	
public final class RotationUtil {
  	private static double prevDegreeRad = 0.0;
  	private static double prevSin = 0.0;
  	private static double prevCos = 1.0;
  	
  	public static final Point getRotatedPoint(double x, double y, double degreeRad, double halfWidth, double halfHeight) {
  	  	if (degreeRad == 0) {
  	  	  	return new Point(x, y);
  	  	} else {
  	  	  	double pixelX = x - halfWidth;
  	  	  	double pixelY = y - halfHeight;
  	  	  	if (prevDegreeRad != degreeRad) {
  	  	  	  	prevDegreeRad = degreeRad;
  	  	  	  	prevSin = Math.sin(degreeRad);
  	  	  	  	prevCos = Math.cos(degreeRad);
  	  	  	}
  	  	  	return new Point(
  	  	  	  	  	pixelX*prevCos-pixelY*prevSin + halfWidth,
  	  	  	  	  	pixelX*prevSin+pixelY*prevCos + halfHeight);
  	  	}
  	}
  	
  	public static final Point getRotatedPoint(Point point, double degreeRad, double halfWidth, double halfHeight) {
  	  	if (degreeRad == 0) {
  	  	  	return point;
  	  	} else {
  	  	  	return getRotatedPoint(point.x, point.y, degreeRad, halfWidth, halfHeight);
  	  	}
  	}
  	
}
