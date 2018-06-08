package imageserver;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.concurrent.BlockingQueue;

import imageserver.ImageServer;

public class Thread2 extends Thread {

	int width = 500;
	int height = 500;

	BlockingQueue<ImageJob> queueOut = null;
	BlockingQueue<ImageJob> queueScaled = null;

	public Thread2(BlockingQueue<ImageJob> imageOutQueue) {
		// TODO Auto-generated constructor stub
		queueOut = imageOutQueue;

	}

	public void run() {
		ImageJob outJob = null;
		try {
			while ((outJob = queueOut.take()) != null) {
				System.out.println("taken from  queu2 "+outJob.toString());
				try {
					this.width = Integer.parseInt(outJob.width);
					this.height = Integer.parseInt(outJob.height);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				String color = outJob.color;

				if (color != null) {
					outJob.bufferedImage = changeToGray(outJob.bufferedImage);
				}
				int type = outJob.bufferedImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : outJob.bufferedImage.getType();
				System.out.println("boyut ve renk değiştirildi");

				outJob.bufferedImage = resizeImage(outJob.bufferedImage, type, width, height);

				ImageServer.cache.put(outJob.url, outJob.bufferedImage);;
				outJob.isfinished = true;
				
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
