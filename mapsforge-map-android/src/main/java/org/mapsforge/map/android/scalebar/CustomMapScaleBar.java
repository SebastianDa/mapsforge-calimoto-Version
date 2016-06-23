/*
 * Copyright 2010, 2011, 2012, 2013 mapsforge.org
 * Copyright 2014 Ludwig M Brinckmann
 * Copyright 2014, 2015 devemux86
 * Copyright 2014 Erik Duisters
 * Copyright 2014 Christian Pesch
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
package org.mapsforge.map.android.scalebar;
  	
import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.GraphicContext;
import org.mapsforge.core.graphics.GraphicFactory;
import org.mapsforge.map.model.DisplayModel;
import org.mapsforge.map.model.MapViewDimension;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.scalebar.DefaultMapScaleBar;
  	
/**
 * Needed for map rotation.
 */
public class CustomMapScaleBar extends DefaultMapScaleBar {
  	public CustomMapScaleBar(MapViewPosition mapViewPosition, MapViewDimension mapViewDimension,
  	  	  	GraphicFactory graphicFactory, DisplayModel displayModel)
  	{
  	  	super(mapViewPosition, mapViewDimension, graphicFactory, displayModel);
  	}
  	
  	public Bitmap getMapScaleBitmap()
  	{
  	  	return this.mapScaleBitmap;
  	}
  	
  	@Override
  	public void draw(GraphicContext graphicContext)
  	{
  	  	if (!this.isVisible()) {
  	  	  	return;
  	  	}
  	  	if (this.mapViewDimension.getDimension() == null) {
  	  	  	return;
  	  	}
  	  	if (this.isRedrawNecessary()) {
  	  	  	redraw(this.mapScaleCanvas);
  	  	  	this.redrawNeeded = false;
  	  	}
  	  	graphicContext.drawBitmap(this.mapScaleBitmap, 0, 0);
  	}
  	
}
