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

	public Image(BufferedImage bi, int w, int h) {// ��ͼƬ����Ϊw,h
		this.bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_BGR);
		this.bi.createGraphics().drawImage(bi, 0, 0, w, h, null);
	}

	static public void getAllRgb(File dir, int d) throws Exception {// Ԥ��������ͼƬ����СΪd*d
		bis = new Vector<>();
		rgbs = new Vector<>();
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				String filename = file.getName();
				String type = filename.substring(filename.lastIndexOf(".") + 1);
				if (type.equalsIgnoreCase("jpg")) {
					Image image = new Image(ImageIO.read(file), d, d);// ��Ŀ��ͼƬ�����ɱ�ͼƬ��С
					bis.add(image.bi);
					rgbs.add(image.avg(0, 0, d, d));
				}
			}
		}
	}

	public BufferedImage find(Rgb src) throws Exception {// ֻҪ��Сͼjiu
		int mind = Integer.MAX_VALUE;
		int mini = -1;
		for (int i = 0; i < rgbs.size(); i++) {
			int tmp = src.dif(rgbs.elementAt(i));
			if (tmp < mind) {// ���ܶ���rgbs��ֻ�ܱ���
				mind = tmp;
				mini = i;
			}
		}
		return bis.elementAt(mini);// ֻҪ
	}

	public void mosaic(int d) throws Exception {// ƥ��ͼ�ļ���dir��������ֱ��d�����dif
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

	public Rgb avg(int mx, int my, int w, int h) throws Exception {// ָ������ƽ��ֵ
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
		System.out.println("5M��ͼ,40ֱ��Ч�����");
		System.out.print("��Ԫͼ�ļ��У�");

		String cellCd = sin.next();

		System.out.print("��Ԫͼֱ����");

		int d = sin.nextInt();
		getAllRgb(new File(cellCd), d);

		System.out.println("��Ԫ��Ԥ�������");

		while (true) {
			System.out.print("��ͼ�ļ�����");
			String mainFile = sin.next();

			Image image = new Image(ImageIO.read(new File(mainFile)));
			image.mosaic(d);
			System.out.print("ת���ɹ����������������");
			String saveFile = sin.next();
			ImageIO.write(image.bi, "jpg", new File(saveFile));
			System.out.println("����ɹ�����������ͼƬ��y/n");
			String op = sin.next();
			if (op.equalsIgnoreCase("y") == false)
				break;
		}
	}
}
