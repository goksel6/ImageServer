package imageserver;


import java.awt.image.BufferedImage;

import java.io.IOException;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

import imageserver.ImageJob;

import imageserver.ThreadCs;
import imageserver.ImageServer;

import imageserver.Thread2;

public class ImageServer extends AbstractHandler {
	static int count = 5;
	static int queueCount = 20;
	
	public static ConcurrentHashMap<String, BufferedImage> cache = new ConcurrentHashMap<String, BufferedImage>(5000);
	
	
	//firstQueue request tutar.
	static BlockingQueue<ImageJob> firstQueue = new ArrayBlockingQueue<ImageJob>(queueCount);
	static BlockingQueue<ImageJob> imageOutQueue = new ArrayBlockingQueue<ImageJob>(queueCount);
	
	//scale
	static BlockingQueue<ImageJob> secQueue = new ArrayBlockingQueue<ImageJob>(queueCount);
	
	ImageJob imageJob = null;
	
    String color;
    String width;
    String height ;

	@SuppressWarnings("static-access")
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	
		//requesti ayıklıyoruz.
		width = request.getParameter("width");
		height = request.getParameter("height");
		color = request.getParameter("color");
		
		String imgN = baseRequest.getRequestURI();
		
		imageJob = new ImageJob("http://bihap.com/img" + imgN, width, height, color,null);
		
		//gelen isteği ilkQueue ya atıyoruz.
		try {
			System.out.println("added first queue "+imageJob.toString());
			firstQueue.put(imageJob);
			
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		while(imageJob.isfinished==false) {
			try {
				Thread.currentThread().sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    	ImageIO.write(imageJob.bufferedImage,"jpg",response.getOutputStream());
		baseRequest.setHandled(true);			
	}
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		ContextHandler context = new ContextHandler();
		// context.setContextPath("");
		context.setHandler(new ImageServer());
		server.setHandler(context);

		server.start();
		 ExecutorService executorImage = Executors.newFixedThreadPool(5);
		 ExecutorService executorScale = Executors.newFixedThreadPool(5);
		 
		for (int i = 0; i < count; i++) {
			Thread threadImage = new ThreadCs(firstQueue, imageOutQueue);
			executorImage.execute(threadImage);

			Thread threadScale = new Thread2(imageOutQueue);
			executorScale.execute(threadScale);
		}

		server.join();
	}
}



