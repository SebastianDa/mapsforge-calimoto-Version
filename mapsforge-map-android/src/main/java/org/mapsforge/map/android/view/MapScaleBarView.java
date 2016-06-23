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
package org.mapsforge.map.android.view;
  	
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.scalebar.CustomMapScaleBar;
import org.mapsforge.map.model.common.Observer;
  	
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
  	
/**
 * Needed for map rotation.
 */
public class MapScaleBarView extends View implements Observer {
  	private CustomMapScaleBar mapScaleBar;
  	
  	public MapScaleBarView(Context context)
  	{
  	  	this(context, null);
  	}
  	public MapScaleBarView(Context context, AttributeSet attrs)
  	{
  	  	super(context, attrs);
  	}
  	public MapScaleBarView(Context context, AttributeSet attrs, int defStyleAttr)
  	{
  	  	super(context, attrs, defStyleAttr);
  	}
  	
  	public void setMapScaleBar(CustomMapScaleBar mapScaleBar)
  	{
  	  	this.mapScaleBar = mapScaleBar;
  	}
  	
  	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
  	@Override
  	public void onChange()
  	{
  	  	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
  	  	{
  	  	  	if (isHardwareAccelerated())
  	  	  	{
  	  	  	  	postInvalidate();
  	  	  	}
  	  	}
  	}
  	
  	@Override
  	protected void onDraw(Canvas canvas)
  	{
  	  	org.mapsforge.core.graphics.Canvas graphicContext = AndroidGraphicFactory.createGraphicContext(canvas);
  	  	mapScaleBar.draw(graphicContext);
  	}
  	
  	@Override
  	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  	{
  	  	setMeasuredDimension(mapScaleBar.getMapScaleBitmap().getWidth(), mapScaleBar.getMapScaleBitmap().getHeight());
  	}
  	
}
