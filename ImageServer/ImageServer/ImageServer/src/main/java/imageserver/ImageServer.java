package imageserver;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;

public class ImageServer extends AbstractHandler {
//	ConcurrentHashMap<String, BufferedImage> cache = new ConcurrentHashMap<String, BufferedImage>(5000);

	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html;charset=utf-8");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("image/jpg");
		
		
		//Concurrrent Queue kullan burdaki gelen istekleri queue at bir thread class yarat run fonksiyonuna yukardaki 
		//ImageIO.read fonksiyonunu koy. thread kuyruktan alýp okuma yapsýn
		
		Queue<HttpServletRequest> queue = new ConcurrentLinkedQueue<HttpServletRequest>();
		queue.add(request);
		ExecutorService executor2 = Executors.newFixedThreadPool(5);
		ThreadCs a= new ThreadCs(queue, response,target);
		Future<BufferedImage> future = executor2.submit(a);
		try {
			BufferedImage img = future.get();
		
			
			ImageIO.write(img, "jpg", response.getOutputStream());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
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

	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		ContextHandler context = new ContextHandler();
		context.setContextPath("/img");
		context.setHandler(new ImageServer());
		server.setHandler(context);

		server.start();
		server.join();
	}

}
