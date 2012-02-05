/*
 * Copyright 2012 Timothy Lin <lzh9102@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zxinggui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * This class implements an image displaying swing control that 
 * automatically resizes and centers the image to fit in the area.
 * It preserves the original aspect ratio when resizing the image.
 */
@SuppressWarnings("serial")
public class ImageDisplay extends JPanel {
	
	private BufferedImage image = null;
	
	public ImageDisplay() {
		
	}
	
	public void setImage(BufferedImage img) {
		if (img != null)
			image = new BufferedImage(img.getColorModel()
				, img.copyData(null)
				, img.isAlphaPremultiplied()
				, null); // deep copy image
		else
			image = null;
		
		this.repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		int w, h;
		int x, y;
		//super.paintComponent(g);
		
		if (image == null)
			return;
		
		if (getWidth() * image.getHeight() < getHeight() * image.getWidth()) {
			// panel_w/panel_h < img_w/img_h
			w = getWidth();
			h = getWidth() * image.getHeight() / image.getWidth();
			x = 0;
			y = (getHeight() - h) / 2;
		} else {
			// panel_w/panel_h > img_w/img_h
			w = getHeight() * image.getWidth() / image.getHeight();
			h = getHeight();
			x = (getWidth() - w) / 2;
			y = 0;
		}
		
		g.drawImage(image
				, x, y, x + w, y + h
				, 0, 0, image.getWidth(), image.getHeight()
				, null);
	}
}
