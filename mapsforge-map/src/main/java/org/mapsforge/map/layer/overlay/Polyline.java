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
package org.mapsforge.map.layer.overlay;
  	
import org.mapsforge.core.graphics.Align;
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.FontFamily;
import org.mapsforge.core.graphics.FontStyle;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Path;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;
import org.mapsforge.core.util.RotationUtil;
import org.mapsforge.map.layer.Layer;
  	
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
  	
/**
 * A {@code Polyline} draws a connected series of line segments specified by a list of {@link LatLong LatLongs}.
 * <p/>
 * A {@code Polyline} holds a {@link Paint} object which defines drawing parameters such as color, stroke width, pattern
 * and transparency.
 */
public class Polyline extends Layer {
  	public static enum PolylineType {
  	  	ROUTE, TRACK, TESTING
  	};
  	private final GraphicFactory graphicFactory;
  	private final PolylineType type;
  	private final boolean keepAligned;
  	private final List<LatLong> latLongs = new CopyOnWriteArrayList<LatLong>();
  	private Paint paintStroke;
  	private float initialStrokeWidth;
  	private BoundingBox boundingBoxLine;
  	private boolean developerModeEnabled = false;
  	private Paint paint = null;
  	
  	/**
  	 * @param paintStroke
  	 *  	  	  	the initial {@code Paint} used to stroke this polyline (may be null).
  	 * @param graphicFactory
  	 *  	  	  	the GraphicFactory
  	 * @param type
  	 *  	  	  	the {@code PolylineType}
  	 */
  	public Polyline(Paint paintStroke, GraphicFactory graphicFactory, PolylineType type) {
  	  	this(paintStroke, graphicFactory, type, false);
  	}
  	
  	/**
  	 * @param paintStroke
  	 *  	  	  	the initial {@code Paint} used to stroke this polyline (may be null).
  	 * @param graphicFactory
  	 *  	  	  	the GraphicFactory
  	 * @param type
  	 *  	  	  	the {@code PolylineType}
  	 * @param keepAligned
  	 *  	  	  	if set to true it will keep the bitmap aligned with the map,
  	 *  	  	  	to avoid a moving effect of a bitmap shader.
  	 */
  	public Polyline(Paint paintStroke, GraphicFactory graphicFactory, PolylineType type, boolean keepAligned) {
  	  	super();
  	
  	  	this.keepAligned = keepAligned;
  	  	this.type = type;
  	  	this.paintStroke = paintStroke;
  	  	this.graphicFactory = graphicFactory;
  	  	if (paintStroke != null) {
  	  	  	this.initialStrokeWidth = paintStroke.getStrokeWidth();
  	  	}
  	}
  	
  	@Override
  	public synchronized void draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point topLeftPoint, double degreeRad) {
  	  	if (this.latLongs.isEmpty() || this.paintStroke == null) {
  	  	  	return;
  	  	}
  	
  	  	Iterator<LatLong> iterator = this.latLongs.iterator();
  	  	if (!iterator.hasNext()) {
  	  	  	return;
  	  	}
  	  	
  	  	if (this.boundingBoxLine != null) {
  	  	  	if (!boundingBox.intersects(boundingBoxLine)) {
  	  	  	  	return;
  	  	  	}
  	  	}
  	  	
  	  	float strokeWidth = initialStrokeWidth;
  	  	int scaleDownLoops = Math.min(20 - zoomLevel, 6);
  	  	for (int i=0;i<scaleDownLoops;i++)
  	  	{
  	  	  	strokeWidth /= 1.5f;
  	  	}
  	  	paintStroke.setStrokeWidth(strokeWidth);
  	  	
  	  	int iCounter;
  	  	int iInterval = 1;
  	  	if (type.equals(PolylineType.ROUTE)) {
  	  	  	if (zoomLevel <= 8) {
  	  	  	  	iInterval = 8;
  	  	  	} else if (zoomLevel <= 10) {
  	  	  	  	iInterval = 4;
  	  	  	} else if (zoomLevel <= 12) {
  	  	  	  	iInterval = 2;
  	  	  	}
  	  	} else if (type.equals(PolylineType.TRACK)) {
  	  	  	if (zoomLevel <= 8) {
  	  	  	  	iInterval = 20;
  	  	  	} else if (zoomLevel <= 10) {
  	  	  	  	iInterval = 10;
  	  	  	} else if (zoomLevel <= 12) {
  	  	  	  	iInterval = 6;
  	  	  	} else if (zoomLevel <= 14) {
  	  	  	  	iInterval = 3;
  	  	  	} else if (zoomLevel <= 16) {
  	  	  	  	iInterval = 2;
  	  	  	}
  	  	}
  	  	
  	  	double halfWidth = 0.5*canvas.getWidth();
  	  	double halfHeight = 0.5*canvas.getHeight();
  	  	LatLong latLong = iterator.next();
  	  	long mapSize = MercatorProjection.getMapSize(zoomLevel, displayModel.getTileSize());
  	  	Point point = RotationUtil.getRotatedPoint(
  	  	  	  	MercatorProjection.longitudeToPixelX(latLong.longitude, mapSize) - topLeftPoint.x,
  	  	  	  	MercatorProjection.latitudeToPixelY(latLong.latitude, mapSize) - topLeftPoint.y,
  	  	  	  	degreeRad,
  	  	  	  	halfWidth,
  	  	  	  	halfHeight);
  	  	
  	  	if (developerModeEnabled) {
  	  	  	int iIndex = 0;
  	  	  	canvas.drawText(String.valueOf(iIndex), (int) point.x, (int) point.y, this.paint);
  	  	  	iIndex++;
  	  	  	while (iterator.hasNext()) {
  	  	  	  	for (iCounter=0;iCounter<iInterval;iCounter++) {
  	  	  	  	  	if (iterator.hasNext()) {
  	  	  	  	  	  	latLong = iterator.next();
  	  	  	  	  	}
  	  	  	  	}
  	  	  	  	
  	  	  	  	point = RotationUtil.getRotatedPoint(
  	  	  	  	  	  	MercatorProjection.longitudeToPixelX(latLong.longitude, mapSize) - topLeftPoint.x,
  	  	  	  	  	  	MercatorProjection.latitudeToPixelY(latLong.latitude, mapSize) - topLeftPoint.y,
  	  	  	  	  	  	degreeRad,
  	  	  	  	  	  	halfWidth,
  	  	  	  	  	  	halfHeight);
  	
  	  	  	  	canvas.drawText(String.valueOf(iIndex), (int) point.x, (int) point.y, this.paint);
  	  	  	  	iIndex++;
  	  	  	}
  	  	} else {
  	  	  	Path path = this.graphicFactory.createPath();
  	
  	  	  	path.moveTo((int) point.x, (int) point.y);
  	
  	  	  	while (iterator.hasNext()) {
  	  	  	  	for (iCounter=0;iCounter<iInterval;iCounter++) {
  	  	  	  	  	if (iterator.hasNext()) {
  	  	  	  	  	  	latLong = iterator.next();
  	  	  	  	  	}
  	  	  	  	}
  	  	  	  	
  	  	  	  	point = RotationUtil.getRotatedPoint(
  	  	  	  	  	  	MercatorProjection.longitudeToPixelX(latLong.longitude, mapSize) - topLeftPoint.x,
  	  	  	  	  	  	MercatorProjection.latitudeToPixelY(latLong.latitude, mapSize) - topLeftPoint.y,
  	  	  	  	  	  	degreeRad,
  	  	  	  	  	  	halfWidth,
  	  	  	  	  	  	halfHeight);
  	
  	  	  	  	path.lineTo((int) point.x, (int) point.y);
  	  	  	}
  	
  	  	  	if (this.keepAligned) {
  	  	  	  	this.paintStroke.setBitmapShaderShift(topLeftPoint);
  	  	  	}
  	  	  	canvas.drawPath(path, this.paintStroke);
  	  	}
  	}
  	
  	/**
  	 * @return a thread-safe list of LatLongs in this polyline.
  	 */
  	public List<LatLong> getLatLongs() {
  	  	return this.latLongs;
  	}
  	
  	/**
  	 * @return the {@code Paint} used to stroke this polyline (may be null).
  	 */
  	public synchronized Paint getPaintStroke() {
  	  	return this.paintStroke;
  	}
  	
  	/**
  	 * @return true if it keeps the bitmap aligned with the map, to avoid a
  	 * moving effect of a bitmap shader, false otherwise.
  	 */
  	public boolean isKeepAligned() {
  	  	return keepAligned;
  	}
  	
  	/**
  	 * @param paintStroke the new {@code Paint} used to stroke this polyline (may be null).
  	 */
  	public synchronized void setPaintStroke(Paint paintStroke) {
  	  	this.paintStroke = paintStroke;
  	  	if (paintStroke != null) {
  	  	  	this.initialStrokeWidth = paintStroke.getStrokeWidth();
  	  	}
  	  	requestRedraw();
  	}
  	
  	public void finalizeCoordinates() {
  	  	if (latLongs != null) {
  	  	  	Iterator<LatLong> iterator = this.latLongs.iterator();
  	  	  	LatLong latLong = iterator.next();
  	  	  	double latmin = latLongs.get(0).latitude;
  	  	  	double latmax = latLongs.get(0).latitude;
  	  	  	double longmin = latLongs.get(0).longitude;
  	  	  	double longmax = latLongs.get(0).longitude;
  	  	  	while (iterator.hasNext()) {
  	  	  	  	latLong = iterator.next();
  	  	  	  	latmin = Math.min(latmin,latLong.latitude);
  	  	  	  	latmax = Math.max(latmax, latLong.latitude);
  	  	  	  	longmin = Math.min(longmin,latLong.longitude);
  	  	  	  	longmax = Math.max(longmax, latLong.longitude);
  	  	  	}
  	  	  	boundingBoxLine = new BoundingBox(latmin, longmin, latmax, longmax);
  	  	}
  	}
  	
  	public void setDeveloperModeEnabled(boolean developerModeEnabled)
  	{
  	  	this.developerModeEnabled = developerModeEnabled;
  	  	this.paint = this.graphicFactory.createPaint();
  	  	this.paint.setColor(this.paintStroke.getColor());
  	  	this.paint.setTextAlign(Align.CENTER);
  	  	this.paint.setTypeface(FontFamily.DEFAULT, FontStyle.BOLD);
  	  	this.paint.setTextSize(0.3f*this.paintStroke.getStrokeWidth());
  	  	requestRedraw();
  	}
  	
}
