/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2014 Ludwig M Brinckmann
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
package org.mapsforge.map.android.layer.overlay;
  	
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.core.util.RotationUtil;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;
  	
public class MarkerRotating extends Marker {
  	protected float fRotation = 0.0f;
  	private boolean isAlwaysDrawn = false;
  	
  	public MarkerRotating(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset) {
  	  	super(latLong, bitmap, horizontalOffset, verticalOffset);
  	}
  	
  	public void setRotation(float fRotation) {
  	  	this.fRotation = fRotation;
  	  	requestRedraw();
  	}
  	public void setIsAlwaysDrawn(boolean isAlwaysDrawn) {
  	  	this.isAlwaysDrawn = isAlwaysDrawn;
  	  	requestRedraw();
  	}
  	
  	@Override
  	public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint, double degreeRad) {
  	  	if (this.latLong != null && this.bitmap != null && !this.bitmap.isDestroyed()) {
  	  	  	if (isAlwaysDrawn || zoomLevel > 12) {
  	  	  	  	double halfWidth = 0.5*canvas.getWidth();
  	  	  	  	double halfHeight = 0.5*canvas.getHeight();
  	  	  	  	double latitude = this.latLong.latitude;
  	  	  	  	double longitude = this.latLong.longitude;
  	  	  	  	long mapSize = MercatorProjection.getMapSize(zoomLevel, displayModel.getTileSize());
  	  	  	  	Point point = RotationUtil.getRotatedPoint(
  	  	  	  	  	  	MercatorProjection.longitudeToPixelX(longitude, mapSize) - topLeftPoint.x,
  	  	  	  	  	  	MercatorProjection.latitudeToPixelY(latitude, mapSize) - topLeftPoint.y,
  	  	  	  	  	  	degreeRad,
  	  	  	  	  	  	halfWidth,
  	  	  	  	  	  	halfHeight);
  	  	  	  	
  	  	  	  	int halfBitmapWidth = this.bitmap.getWidth() / 2;
  	  	  	  	int halfBitmapHeight = this.bitmap.getHeight() / 2;
  	  	  	  	
  	  	  	  	int left = (int) (point.x - halfBitmapWidth + this.horizontalOffset);
  	  	  	  	int top = (int) (point.y - halfBitmapHeight + this.verticalOffset);
  	  	  	  	int right = left + this.bitmap.getWidth();
  	  	  	  	int bottom = top + this.bitmap.getHeight();
  	  	  	  	
  	  	  	  	Rectangle bitmapRectangle = new Rectangle(left, top, right, bottom);
  	  	  	  	Rectangle canvasRectangle = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
  	  	  	  	if (canvasRectangle.intersects(bitmapRectangle)) {
  	  	  	  	  	android.graphics.Canvas androidCanvas = AndroidGraphicFactory.getCanvas(canvas);
  	  	  	  	  	if (this.fRotation == 0) {
  	  	  	  	  	  	canvas.drawBitmap(this.bitmap, left, top);
  	  	  	  	  	} else {
  	  	  	  	  	  	androidCanvas.save();
  	  	  	  	  	  	androidCanvas.rotate((float) Math.toDegrees(degreeRad) + this.fRotation, (float) (point.x), (float) (point.y));
  	  	  	  	  	  	canvas.drawBitmap(this.bitmap, left, top);
  	  	  	  	  	  	androidCanvas.restore();
  	  	  	  	  	}
  	  	  	  	}
  	  	  	}
  	  	}
  	}
  	
}
