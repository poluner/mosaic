package mosaic;

public class Rgb {
	int a[] = new int[4];

	public Rgb(int x) {
		for (int i = 0; i < 4; i++) {
			a[i] = (x & 0xff);
			x >>= 8;
		}
	}

	public boolean same(Rgb r, int dif) {
		for (int i = 0; i < 4; i++) {
			if (Math.abs(a[i] - r.a[i]) > dif)
				return false;
		}
		return true;
	}

	public int get() {
		int ans = 0;
		for (int i = 3; i >= 0; i--) {
			ans <<= 8;
			ans += a[i];
		}
		return ans;
	}

	public void add(Rgb r) {
		for (int i = 0; i < 4; i++)
			a[i] += r.a[i];
	}

	public void div(int d) {
		for (int i = 0; i < 4; i++)
			a[i] /= d;
	}
}
