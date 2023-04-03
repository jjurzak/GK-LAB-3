package lab3;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.geom.*;

public class SubroutineHierarchy extends JPanel {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame window = new JFrame("Subroutine Hierarchy");
		window.setContentPane(new SubroutineHierarchy());
		window.pack();
		window.setLocation(100, 60);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}

	private final static int WIDTH = 800;
	private final static int HEIGHT = 600;

	private final static double X_LEFT = -4;
	private final static double X_RIGHT = 4;
	private final static double Y_BOTTOM = -3;
	private final static double Y_TOP = 3;

	private final static Color BACKGROUND = Color.WHITE;

	private float pixelSize;

	private int frameNumber = 0;

	private void drawWorld(Graphics2D g2) {
		rotatingPolygon(g2, 100, -1.02, -0.05);
		rotatingPolygon(g2, 100, 1.04, -0.9);
		rotatingPolygon(g2, 80, -1.3, 1.41);
		rotatingPolygon(g2, 80, -3.12, 2.22);
		rotatingPolygon(g2, 60, 0.85, 2.04);
		rotatingPolygon(g2, 60, 2.12, 1.44);

		R_Line(g2, 1, 1.05, 0, -0.46);
		R_Line(g2, 0.85, 0.95, -2.6, 1.87);
		R_Line(g2, 0.6, 0.7, 2.47, 2.47);

		Triangle(g2, 0.4, 0.5, 0, -2, Color.BLUE);
		Triangle(g2, 0.25, 0.35, -2.25, 0.75, new Color(200, 21, 132));
		Triangle(g2, 0.15, 0.25, 1.5, 1, new Color(0, 128, 0));
	}

	private void updateFrame() {
		frameNumber++;
	}

	private void Line(Graphics2D g2, double translate_x, double translate_y) {
		g2.setColor(Color.RED);
		g2.translate(translate_x, translate_y);
		g2.rotate(-Math.PI / 8);
		g2.scale(2.29, 0.14);
		filledRect(g2);
	}

	private void R_Line(Graphics2D g2, double scale_x, double scale_y, double translate_x, double translate_y) {
		AffineTransform saveTransform = g2.getTransform();
		g2.scale(scale_x, scale_y);
		Line(g2, translate_x, translate_y);
		g2.setTransform(saveTransform);
	}

	private void Triangle(Graphics2D g2, double scale_x, double scale_y, double translate_x, double translate_y,
			Color color) {
		AffineTransform saveTransform = g2.getTransform();
		g2.setColor(color);
		g2.translate(translate_x, translate_y);
		g2.scale(scale_x, scale_y);
		g2.fillPolygon(new int[] { 0, 1, -1 }, new int[] { 3, 0, 0 }, 3);
		g2.setTransform(saveTransform);
	}

	private void rotatingPolygon(Graphics2D g2, double r, double translate_x, double translate_y) {

		AffineTransform saveTransform = g2.getTransform();
		Color saveColor = g2.getColor();
		g2.setTransform(saveTransform);
		g2.setStroke(new BasicStroke(2));

		int n = 17;
		double t = 0, ang = (Math.PI * 2) / n;

		int[] x1 = new int[n];
		int[] y1 = new int[n];

		for (int i = 0; i < n; i++) {
			x1[i] = (int) (r * Math.sin(t));
			y1[i] = (int) (r * Math.cos(t));
			t += ang;
		}

		Polygon hexagon = new Polygon(x1, y1, n);
		g2.translate(translate_x, translate_y);
		g2.setColor(Color.black);
		g2.rotate(-Math.toRadians(frameNumber * 2));
		g2.scale(0.00475, 0.00475);

		for (int i = 0; i < n; i++) {
			g2.drawLine(x1[i], y1[i], 0, 0);
		}
		g2.draw(hexagon);
		g2.setColor(saveColor);
		g2.setTransform(saveTransform);
	}

	private static void filledRect(Graphics2D g2) {
		g2.fill(new Rectangle2D.Double(-0.5, -0.5, 1, 1));
	}

	private final JPanel display;

	public SubroutineHierarchy() {
		display = new JPanel() {
			private static final long serialVersionUID = 1L;

			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				applyLimits(g2, X_LEFT, X_RIGHT, Y_TOP, Y_BOTTOM, false);
				g2.setStroke(new BasicStroke(pixelSize));
				drawWorld(g2);
			}
		};
		display.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		display.setBackground(BACKGROUND);
		final Timer timer = new Timer(17, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				updateFrame();
				repaint();
			}
		});
		final JCheckBox animationCheck = new JCheckBox("Run Animation");
		animationCheck.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (animationCheck.isSelected()) {
					if (!timer.isRunning())
						timer.start();
				} else {
					if (timer.isRunning())
						timer.stop();
				}
			}
		});
		JPanel top = new JPanel();
		top.add(animationCheck);
		setLayout(new BorderLayout(5, 5));
		setBackground(Color.DARK_GRAY);
		setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 4));
		add(top, BorderLayout.NORTH);
		add(display, BorderLayout.CENTER);
	}

	private void applyLimits(Graphics2D g2, double xleft, double xright, double ytop, double ybottom,
			boolean preserveAspect) {
		int width = display.getWidth();
		int height = display.getHeight();
		if (preserveAspect) {
			double displayAspect = Math.abs((double) height / width);
			double requestedAspect = Math.abs((ybottom - ytop) / (xright - xleft));
			if (displayAspect > requestedAspect) {
				double excess = (ybottom - ytop) * (displayAspect / requestedAspect - 1);
				ybottom += excess / 2;
				ytop -= excess / 2;
			} else if (displayAspect < requestedAspect) {
				double excess = (xright - xleft) * (requestedAspect / displayAspect - 1);
				xright += excess / 2;
				xleft -= excess / 2;
			}
		}
		double pixelWidth = Math.abs((xright - xleft) / width);
		double pixelHeight = Math.abs((ybottom - ytop) / height);
		pixelSize = (float) Math.min(pixelWidth, pixelHeight);
		g2.scale(width / (xright - xleft), height / (ybottom - ytop));
		g2.translate(-xleft, -ytop);
	}

}