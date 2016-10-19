package mosaic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Scanner;
import java.util.Vector;

import javax.imageio.ImageIO;

public class Image {

	BufferedImage bi;
	static Vector<BufferedImage> bis;
	static Vector<Rgb> rgbs;

	public Image(BufferedImage bi) {
		this.bi = bi;
	}

	public Image(BufferedImage bi, int w, int h) {// 将图片调整为w,h
		this.bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
		this.bi.createGraphics().drawImage(bi, 0, 0, w, h, null);
	}

	static public void getAllRgb(File dir, int d) throws Exception {// 预处理所有图片，大小为d*d
		bis = new Vector<>();
		rgbs = new Vector<>();
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				String filename = file.getName();
				String type = filename.substring(filename.lastIndexOf(".") + 1);
				if (type.equalsIgnoreCase("jpg")) {
					Image image = new Image(ImageIO.read(file), d, d);// 将目标图片调整成本图片大小
					bis.add(image.bi);
					rgbs.add(image.avg(0, 0, d, d));
				}
			}
		}
	}

	public BufferedImage find(Rgb src) throws Exception {// 只要有小图jiu
		int mind = Integer.MAX_VALUE;
		int mini = -1;
		for (int i = 0; i < rgbs.size(); i++) {
			int tmp = src.dif(rgbs.elementAt(i));
			if (tmp < mind) {// 不能二分rgbs，只能遍历
				mind = tmp;
				mini = i;
			}
		}
		return bis.elementAt(mini);// 只要
	}

	public void mosaic(int d) throws Exception {// 匹配图文件夹dir，马赛克直径d，误差dif
		int w = bi.getWidth(), h = bi.getHeight();
		for (int i = 0; i < w; i += d) {
			for (int j = 0; j < h; j += d) {
				int curw = Math.min(w, i + d), curh = Math.min(h, j + d);
				BufferedImage curbi = find(avg(i, j, curw, curh));
				for (int x = i; x < curw; x++) {
					for (int y = j; y < curh; y++) {
						bi.setRGB(x, y, curbi.getRGB(x - i, y - j));
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
		Scanner sin = new Scanner(System.in);
		System.out.println("5M主图,40直径效果最好");
		System.out.print("单元图文件夹：");

		String cellCd = sin.next();

		System.out.print("单元图直径：");

		int d = sin.nextInt();
		getAllRgb(new File(cellCd), d);

		System.out.println("单元格预处理完毕");

		while (true) {
			System.out.print("主图文件名：");
			String mainFile = sin.next();

			Image image = new Image(ImageIO.read(new File(mainFile)));
			image.mosaic(d);
			System.out.print("转换成功，将结果保存至：");
			String saveFile = sin.next();
			ImageIO.write(image.bi, "jpg", new File(saveFile));
			System.out.println("保存成功，继续处理图片？y/n");
			String op = sin.next();
			if (op.equalsIgnoreCase("y") == false)
				break;
		}
	}
}
