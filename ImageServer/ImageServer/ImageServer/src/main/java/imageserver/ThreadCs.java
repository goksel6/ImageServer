package imageserver;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ThreadCs implements Callable {

public Queue<HttpServletRequest> queue;
public HttpServletResponse response;
String target;
static ConcurrentHashMap<String, BufferedImage> cache = new ConcurrentHashMap<String, BufferedImage>(5000);

	public ThreadCs(Queue<HttpServletRequest> queue2, HttpServletResponse response2,String t) {
		// TODO Auto-generated constructor stub
		queue=queue2;
		response= response2;
		target=t;
	}

	public BufferedImage call(){
		ExecutorService executor = Executors.newFixedThreadPool(4);
		Queue<BufferedImage> queue2 = new ConcurrentLinkedQueue<BufferedImage>();
   	URL url = null;
    	HttpServletRequest request= queue.poll();
       System.out.println("MyRunnable running");
   	Enumeration<String> parameternames = request.getParameterNames();
	
   		if (cache.get(request.getPathInfo().toString()) == null) {						
		
			try {
				url = new URL("http://bihap.com/img/"+ target);
				
				
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			BufferedImage img = null;
			try {
				img = ImageIO.read(url);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cache.put(request.getPathInfo().toString(), img);
			
			if(parameternames.hasMoreElements()) {
			queue2.add(img)	;
			Thread2 ab = new Thread2(request,parameternames,queue2);
			Future<BufferedImage> future = executor.submit(ab);
			BufferedImage img1 = null;
			try {
				img1 = future.get();
				
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return img1;
			}else
			{
				return img;
			}
			

		} else {
			
			BufferedImage cachedImage = cache.get(request.getPathInfo().toString());
			return cachedImage;
			
		}
		
	
    }
	

    public BufferedImage ReturnImage (BufferedImage img) {
    	
		return img;
    	
    }
  }
