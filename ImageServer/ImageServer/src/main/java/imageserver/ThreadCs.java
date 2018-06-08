package imageserver;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.BlockingQueue;

import javax.imageio.ImageIO;

import imageserver.ImageJob;


public class ThreadCs extends Thread {

BlockingQueue<ImageJob> queueIn = null;
BlockingQueue<ImageJob> queueOut = null;

	public ThreadCs(BlockingQueue<ImageJob> imageQueue, BlockingQueue<ImageJob> imageOutQueue) {
		// TODO Auto-generated constructor stub
		queueIn= imageQueue;
		queueOut= imageOutQueue;
	}

	public void run(){
		ImageJob outJobNesne;
	
			try {
				while ((outJobNesne = queueIn.take()) != null) {
					System.out.println("taken from  queu1 "+outJobNesne.toString());
					URL url = new URL(outJobNesne.url);
					
					if (ImageServer.cache.get(url.getPath()) == null) {
						outJobNesne.bufferedImage = ImageIO.read(url);
						queueOut.put(outJobNesne); // okunan resimlerin kuyrugu			
						ImageServer.cache.put(url.getPath(), outJobNesne.bufferedImage);
						System.out.println("added to  queu2 "+outJobNesne.toString());
					} else {
						outJobNesne.bufferedImage = ImageServer.cache.get(url.getPath());
						queueOut.put(outJobNesne); // okunan resimlerin kuyrugu
						System.out.println("added to  queu2 "+outJobNesne.toString());
					}
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
	}	
 }
	

  
  
