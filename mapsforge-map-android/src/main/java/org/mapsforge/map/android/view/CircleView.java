/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2014 Ludwig M Brinckmann
 * Copyright 2015 devemux86
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
package org.mapsforge.map.android.view;
  	
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.map.android.graphics.AndroidPaint;
  	
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
  	
/**
 * A {@code Circle} consists of a center {@link LatLong} and a non-negative radius in meters.
 * <p>
 * A {@code Circle} holds two {@link Paint} objects to allow for different outline and filling. These paints define
 * drawing parameters such as color, stroke width, pattern and transparency.
 */
public class CircleView extends View {
  	
  	private LatLong latLong;
  	private MapView mapView;
  	private AndroidPaint paintFill;
  	private AndroidPaint paintStroke;
  	private float radius;
  	
  	public CircleView(Context context) {
  	  	this(context, null);
  	}
  	
  	public CircleView(Context context, AttributeSet attributeSet) {
  	  	super(context, attributeSet);
  	}
  	
  	/**
  	 * @param mapView
  	 *  	  	  	the {@code MapView} to get the current zoom level when drawing
  	 * @param paintFill
  	 *  	  	  	the initial {@code Paint} used to fill this circle (may be null).
  	 * @param paintStroke
  	 *  	  	  	the initial {@code Paint} used to stroke this circle (may be null).
  	 * @throws IllegalArgumentException
  	 *  	  	  	 if the given {@code radius} is negative or {@link Float#NaN}.
  	 *
  	 */
  	public void initialize(MapView mapView, Paint paintFill, Paint paintStroke)
  	{
  	  	this.mapView = mapView;
  	  	this.paintFill = (AndroidPaint) paintFill;
  	  	this.paintStroke = (AndroidPaint) paintStroke;
  	}
  	
//  	public CircleView(LatLong latLong, float radius, Paint paintFill, Paint paintStroke, boolean keepAligned) {
//  	  	super();
//
//  	  	this.keepAligned = keepAligned;
//  	  	this.latLong = latLong;
//  	  	setRadiusInternal(radius);
//  	  	this.paintFill = paintFill;
//  	  	this.paintStroke = paintStroke;
//  	}
  	
  	@Override
  	public synchronized void draw(Canvas canvas) {
  	  	if (this.paintStroke == null && this.paintFill == null) {
  	  	  	return;
  	  	}
  	
  	  	int pixelX = getWidth()/2;
  	  	int pixelY = getHeight()/2;
  	  	int radiusInPixel = getRadiusInPixels(this.latLong.latitude, mapView.getModel().mapViewPosition.getZoomLevel());
  	
  	  	Rectangle canvasRectangle = new Rectangle(0, 0, canvas.getWidth(), canvas.getHeight());
  	  	if (!canvasRectangle.intersectsCircle(pixelX, pixelY, radiusInPixel)) {
  	  	  	return;
  	  	}
  	
  	  	if (this.paintStroke != null) {
  	  	  	canvas.drawCircle(pixelX, pixelY, radiusInPixel, this.paintStroke.getPaint());
  	  	}
  	  	if (this.paintFill != null) {
  	  	  	canvas.drawCircle(pixelX, pixelY, radiusInPixel, this.paintFill.getPaint());
  	  	}
  	}
  	
  	/**
  	 * @return the non-negative radius of this circle in pixels.
  	 */
  	protected int getRadiusInPixels(double latitude, byte zoomLevel) {
  	  	return (int) MercatorProjection.metersToPixels(this.radius, latitude, MercatorProjection.getMapSize(zoomLevel, mapView.getModel().displayModel.getTileSize()));
  	}
  	
  	/**
  	 * @param latLong
  	 *  	  	  	the new center point of this circle (may be null).
  	 */
  	public void setLatLong(LatLong latLong) {
  	  	this.latLong = latLong;
  	  	invalidate();
  	}
  	
  	/**
  	 * @param paintFill
  	 *  	  	  	the new {@code Paint} used to fill this circle (may be null).
  	 */
  	public void setPaintFill(Paint paintFill) {
  	  	this.paintFill = (AndroidPaint) paintFill;
  	  	invalidate();
  	}
  	
  	/**
  	 * @param paintStroke
  	 *  	  	  	the new {@code Paint} used to stroke this circle (may be null).
  	 */
  	public void setPaintStroke(Paint paintStroke) {
  	  	this.paintStroke = (AndroidPaint) paintStroke;
  	  	invalidate();
  	}
  	
  	/**
  	 * @param radius
  	 *  	  	  	the new non-negative radius of this circle in meters.
  	 * @throws IllegalArgumentException
  	 *  	  	  	 if the given {@code radius} is negative or {@link Float#NaN}.
  	 */
  	public synchronized void setRadius(float radius) {
  	  	setRadiusInternal(radius);
  	}
  	
  	private void setRadiusInternal(float radius) {
  	  	if (radius < 0 || Float.isNaN(radius)) {
  	  	  	throw new IllegalArgumentException("invalid radius: " + radius);
  	  	}
  	  	this.radius = radius;
  	  	invalidate();
  	}
  	
}
