package mosaic;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Image {

	BufferedImage bi;

	public Image(BufferedImage bi) {
		this.bi = bi;
	}

	public Image(BufferedImage bi, int w, int h) {// 将图片调整为w,h
		this.bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
		this.bi.createGraphics().drawImage(bi, 0, 0, w, h, null);
	}

	public BufferedImage same(Rgb src, int w, int h, File dir, int dif) throws Exception {// 找相差dif内的jpg图片，且修改过了
		for (File file : dir.listFiles()) {// foreach相当于函数
			if (file.isFile()) {
				String filename = file.getName();
				String type = filename.substring(filename.lastIndexOf(".") + 1);
				if (type.equalsIgnoreCase("jpg")) {
					Image image = new Image(ImageIO.read(file), w, h);// 将目标图片调整成本图片大小
					if (src.same(image.avg(0, 0, w, h), dif)) {
						return image.bi;// 匹配图
					}
				}
			}
		}
		return null;// 失配
	}

	public void mosaic(File dir, int d, int dif) throws Exception {// 匹配图文件夹dir，马赛克直径d，误差dif
		int w = bi.getWidth(), h = bi.getHeight();
		for (int i = 0; i < w; i += d) {
			for (int j = 0; j < h; j += d) {
				int curw = Math.min(w, i + d), curh = Math.min(h, j + d);
				BufferedImage curbi = same(avg(i, j, curw, curh), curw - i, curh - j, dir, dif);
				if (curbi == null) {// 失配就保持原图
					continue;
				} else {
					for (int x = i; x < curw; x++) {
						for (int y = j; y < curh; y++) {
							bi.setRGB(x, y, curbi.getRGB(x - i, y - j));
						}
					}
				}
			}
		}
	}

	public Rgb avg(int mx, int my, int w, int h) throws Exception {// 指定区域平均值
		Rgb avg = new Rgb(0);
		for (int i = mx; i < w; i++) {
			for (int j = my; j < h; j++) {
				avg.add(new Rgb(bi.getRGB(i, j)));
			}
		}
		avg.div((w - mx) * (h - my));
		return avg;
	}

	public static void main(String[] args) throws Exception {
		Image image = new Image(ImageIO.read(new File("A:/Documents/workspace/mosaic/img_src/e.jpg")));
		image.mosaic(new File("A:/Documents/workspace/mosaic/img"), 10, 10);
		ImageIO.write(image.bi, "jpg", new File("eee.jpg"));
		System.err.println("done");
	}
}
