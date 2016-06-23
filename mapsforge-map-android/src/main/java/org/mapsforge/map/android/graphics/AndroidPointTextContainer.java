/*
 * Copyright 2014 Ludwig M Brinckmann
 * Copyright 2014-2016 devemux86
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
package org.mapsforge.map.android.graphics;
  	
import org.mapsforge.core.graphics.Canvas;
import org.mapsforge.core.graphics.Display;
import org.mapsforge.core.graphics.Filter;
import org.mapsforge.core.graphics.GraphicUtils;
import org.mapsforge.core.graphics.Matrix;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Position;
import org.mapsforge.core.mapelements.PointTextContainer;
import org.mapsforge.core.mapelements.SymbolContainer;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.model.Rectangle;
import org.mapsforge.core.util.RotationUtil;
  	
public class AndroidPointTextContainer extends PointTextContainer {
  	AndroidPointTextContainer(Point xy, Display display, int priority, String text, Paint paintFront, Paint paintBack,
  	  	  	  	  	  	  	  SymbolContainer symbolContainer, Position position, int maxTextWidth) {
  	  	super(xy, display, priority, text, paintFront, paintBack, symbolContainer, position, maxTextWidth);
  	  	
  	  	if (text.length() > 15 && text.contains(" ")) {
  	  	  	String[] splitText = text.split(" ");
  	  	  	String linebreakText = splitText[0];
  	  	  	int charCount = linebreakText.length();
  	  	  	for (int i=1;i<splitText.length;i++) {
  	  	  	  	String textFragment = splitText[i];
  	  	  	  	if (charCount+textFragment.length() > 15) {
  	  	  	  	  	linebreakText += "\n"+textFragment;
  	  	  	  	  	charCount = textFragment.length();
  	  	  	  	} else {
  	  	  	  	  	linebreakText += " "+textFragment;
  	  	  	  	  	charCount += 1+textFragment.length();
  	  	  	  	}
  	  	  	}
  	  	}
  	  	
  	  	final float boxWidth = textWidth;
  	  	final float boxHeight = textHeight;
  	
  	  	switch (this.position) {
  	  	  	case CENTER:
  	  	  	  	boundary = new Rectangle(-boxWidth / 2f, -boxHeight / 2f, boxWidth / 2f, boxHeight / 2f);
  	  	  	  	break;
  	  	  	case BELOW:
  	  	  	  	boundary = new Rectangle(-boxWidth / 2f, 0, boxWidth / 2f, boxHeight);
  	  	  	  	break;
  	  	  	case BELOW_LEFT:
  	  	  	  	boundary = new Rectangle(-boxWidth, 0, 0, boxHeight);
  	  	  	  	break;
  	  	  	case BELOW_RIGHT:
  	  	  	  	boundary = new Rectangle(0, 0, boxWidth, boxHeight);
  	  	  	  	break;
  	  	  	case ABOVE:
  	  	  	  	boundary = new Rectangle(-boxWidth / 2f, -boxHeight, boxWidth / 2f, 0);
  	  	  	  	break;
  	  	  	case ABOVE_LEFT:
  	  	  	  	boundary = new Rectangle(-boxWidth, -boxHeight, 0, 0);
  	  	  	  	break;
  	  	  	case ABOVE_RIGHT:
  	  	  	  	boundary = new Rectangle(0, -boxHeight, boxWidth, 0);
  	  	  	  	break;
  	  	  	case LEFT:
  	  	  	  	boundary = new Rectangle(-boxWidth, -boxHeight / 2f, 0, boxHeight / 2f);
  	  	  	  	break;
  	  	  	case RIGHT:
  	  	  	  	boundary = new Rectangle(0, -boxHeight / 2f, boxWidth, boxHeight / 2f);
  	  	  	  	break;
  	  	  	default:
  	  	  	  	break;
  	  	}
  	}
  	
  	@Override
  	public void draw(Canvas canvas, Point origin, Matrix matrix, Filter filter, double degreeRad) {
  	  	if (!this.isVisible) {
  	  	  	return;
  	  	}
  	
  	  	android.graphics.Canvas androidCanvas = AndroidGraphicFactory.getCanvas(canvas);
  	
  	  	// the origin of the text is the base line, so we need to make adjustments
  	  	// so that the text will be within its box
  	  	float textOffset = 0;
  	  	switch (this.position) {
  	  	  	case CENTER:
  	  	  	case LEFT:
  	  	  	case RIGHT:
  	  	  	  	textOffset = textHeight / 2f;
  	  	  	  	break;
  	  	  	case BELOW:
  	  	  	case BELOW_LEFT:
  	  	  	case BELOW_RIGHT:
  	  	  	  	textOffset = textHeight;
  	  	  	  	break;
  	  	  	default:
  	  	  	  	break;
  	  	}
  	
  	  	double halfWidth = 0.5*canvas.getWidth();
  	  	double halfHeight = 0.5*canvas.getHeight();
  	  	Point point = RotationUtil.getRotatedPoint(
  	  	  	  	this.xy.x - origin.x,
  	  	  	  	this.xy.y - origin.y + textOffset,
  	  	  	  	degreeRad,
  	  	  	  	halfWidth,
  	  	  	  	halfHeight);
  	
  	  	if (this.paintBack != null) {
  	  	  	int color = this.paintBack.getColor();
  	  	  	if (filter != Filter.NONE) {
  	  	  	  	this.paintBack.setColor(GraphicUtils.filterColor(color, filter));
  	  	  	}
  	  	  	androidCanvas.drawText(this.text, (float) point.x, (float) point.y, AndroidGraphicFactory.getPaint(this.paintBack));
  	  	  	if (filter != Filter.NONE) {
  	  	  	  	this.paintBack.setColor(color);
  	  	  	}
  	  	}
  	  	int color = this.paintFront.getColor();
  	  	if (filter != Filter.NONE) {
  	  	  	this.paintFront.setColor(GraphicUtils.filterColor(color, filter));
  	  	}
  	  	androidCanvas.drawText(this.text, (float) point.x, (float) point.y, AndroidGraphicFactory.getPaint(this.paintFront));
  	  	if (filter != Filter.NONE) {
  	  	  	this.paintFront.setColor(color);
  	  	}
  	}
}
