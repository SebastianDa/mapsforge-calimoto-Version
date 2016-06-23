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
  	
import org.mapsforge.core.graphics.Align;
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.core.util.RotationUtil;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;
  	
import android.graphics.Color;
  	
public class MarkerMovableLocation extends Marker {
  	private Bitmap bitmapNormal;
  	private Bitmap bitmapMoving;
  	private boolean wasMoved = false;
  	private String sViaIndex = "";
  	private Paint paint = AndroidGraphicFactory.INSTANCE.createPaint();
  	
  	public MarkerMovableLocation(LatLong latLong, Bitmap bitmapNormal, Bitmap bitmapMoving) {
  	  	super(latLong, bitmapNormal, 0, -bitmapNormal.getHeight()/2);
  	  	this.bitmapNormal = bitmapNormal;
  	  	this.bitmapMoving = bitmapMoving;
  	  	this.paint.setColor(Color.WHITE);
  	  	this.paint.setTextAlign(Align.CENTER);
  	  	this.paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
  	  	this.paint.setTextSize(bitmapNormal.getHeight()/2);
  	}
  	
  	public synchronized void setIsBeingMoved(boolean isBeingMoved)
  	{
  	  	if (isBeingMoved) {
  	  	  	this.bitmap = this.bitmapMoving;
  	  	} else {
  	  	  	this.bitmap = this.bitmapNormal;
  	  	}
  	  	this.paint.setTextSize(this.bitmap.getHeight()/2);
  	  	this.wasMoved = false;
  	  	requestRedraw();
  	}
  	
  	public synchronized void setViaIndex(int iViaIndex)
  	{
  	  	if (iViaIndex == -1) {
  	  	  	this.sViaIndex = "";
  	  	} else {
  	  	  	this.sViaIndex = String.valueOf(iViaIndex+1);
  	  	}
  	  	requestRedraw();
  	}
  	
  	@Override
  	public synchronized void setLatLong(LatLong latlong)
  	{
  	  	this.wasMoved = true;
  	  	super.setLatLong(latlong);
  	}
  	
  	public synchronized boolean getWasMoved()
  	{
  	  	return this.wasMoved;
  	}
  	
  	@Override
  	public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint, double degreeRad) {
  	  	if (this.latLong != null && this.bitmap != null && !this.bitmap.isDestroyed()) {
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
  	  	  	
  	  	  	int left = (int) (point.x - this.bitmap.getWidth()/2);
  	  	  	int top = (int) (point.y - this.bitmap.getHeight());
  	  	  	int right = left + this.bitmap.getWidth();
  	  	  	int bottom = top + this.bitmap.getHeight();
  	  	  	
  	  	  	Rectangle bitmapRectangle = new Rectangle(left, top, right, bottom);
  	  	  	Rectangle canvasRectangle = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
  	  	  	if (canvasRectangle.intersects(bitmapRectangle)) {
  	  	  	  	canvas.drawBitmap(this.bitmap, left, top);
  	  	  	  	if (!this.sViaIndex.isEmpty()) {
  	  	  	  	  	canvas.drawText(this.sViaIndex, left + this.bitmap.getWidth()/2, top + this.bitmap.getHeight()*6/10, this.paint);
  	  	  	  	}
  	  	  	}
  	  	}
  	}
  	
}
