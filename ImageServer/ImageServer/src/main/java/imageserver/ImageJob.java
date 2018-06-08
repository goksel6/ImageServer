package imageserver;

import java.awt.image.BufferedImage;

public class ImageJob {
	//job nesnesi
		public String url = "";
		public BufferedImage bufferedImage = null;
		public String width = "500";
		public String height = "500";
		public String color = "";
		public boolean isfinished = false;

		public ImageJob(String url2, String width2, String height2, String color2, BufferedImage img) {
			this.url = url2;
			this.width = width2;
			this.height = height2;
			this.color = color2;
		}

		@Override
		public String toString() {
			return "ImageJob [url=" + url + ", bufferedImage=" + bufferedImage + ", width=" + width + ", height="
					+ height + ", color=" + color + ", isfinished=" + isfinished +  "]" + this.hashCode();
		}

		
}
