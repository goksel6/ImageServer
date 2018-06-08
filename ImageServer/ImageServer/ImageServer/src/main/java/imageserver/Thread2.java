package imageserver;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.Enumeration;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Thread2 implements Callable {
	public Enumeration<String> parameternames;
	Queue<BufferedImage> queue2 = new ConcurrentLinkedQueue<BufferedImage>();
	HttpServletRequest request;
	public Thread2(HttpServletRequest request1,Enumeration<String> parameternames1,Queue<BufferedImage> queue) {
		// TODO Auto-generated constructor stub
		parameternames=parameternames1;
		queue2=queue;
		request=request1;
		
		
	}
	public BufferedImage call(){
		BufferedImage img=queue2.poll();
	while (parameternames.hasMoreElements()) {
		
		
		String parameter = parameternames.nextElement();
		
		if (parameter.equalsIgnoreCase("width")) {
			int type = img.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : img.getType();
			img = resizeImage(img, type,
					Integer.parseInt(request.getParameter(parameter).toString()), img.getHeight());

		} else if (parameter.equalsIgnoreCase("height")) {
			int type = img.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : img.getType();
			img = resizeImage(img, type, img.getWidth(),
					Integer.parseInt(request.getParameter(parameter).toString()));
		} else if (parameter.equalsIgnoreCase("color")) {
			img = changeToGray(img);
		}
	
	
	}
	return img;
	}
	private static BufferedImage changeToGray(BufferedImage alterImage) {
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);
		BufferedImage image = op.filter(alterImage, null);
		return image;
	}

	private static BufferedImage resizeImage(BufferedImage originalImage, int type, int IMG_WIDTH, int IMG_HEIGHT) {
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
		return resizedImage;
	}
	

}
