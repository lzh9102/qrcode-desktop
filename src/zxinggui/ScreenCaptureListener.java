package zxinggui;

import java.awt.image.BufferedImage;

public interface ScreenCaptureListener {
	public void screenshotCaptured(BufferedImage image);
	public void screenshotCanceled();
}
