package grapher.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.BasicStroke;
import javax.swing.JPanel;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import static java.lang.Math.*;

import grapher.fc.*;
import java.awt.Cursor;
import static java.awt.Cursor.HAND_CURSOR;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.SwingUtilities;


public class Grapher extends JPanel {
	static final int MARGIN = 40;
	static final int STEP = 5;
	
	static final BasicStroke dash = new BasicStroke(1, BasicStroke.CAP_ROUND,
	                                                   BasicStroke.JOIN_ROUND,
	                                                   1.f,
	                                                   new float[] { 4.f, 4.f },
	                                                   0.f);
	                                                   
	protected int W = 400;
	protected int H = 300;
	
	protected double xmin, xmax;
	protected double ymin, ymax;
        
    protected Point pts;
    Graphics2D g2;

	protected Vector<Function> functions;
	protected Vector<Boolean> funcStates;
	protected Vector<Color> funcColors;
	
	public Grapher() {		
		xmin = -PI/2.; xmax = 3*PI/2;
		ymin = -1.5;   ymax = 1.5;
		
		functions = new Vector<Function>();
		funcStates = new Vector<Boolean>();
		funcColors = new Vector<Color>();
		
        this.addMouseListener(new GrapherListener());
        this.addMouseMotionListener(new GrapherListener());
        this.addMouseWheelListener(new GrapherListener());
	}
	
	public void add(String expression) {
		add(FunctionFactory.createFunction(expression));
	}
	
	public void add(Function function) {
		functions.add(function);
		funcStates.add(false);
		funcColors.add(Color.BLACK);
		repaint();
	}
	
	public void remove(int[] indices) {
		for(int i = 0; i < indices.length; ++i) {
			functions.remove(indices[i] - i);
			funcStates.remove(indices[i] - i);
			//" - i" car la table réduit de 1 à chaque suppression
    	}
		repaint();
	}
	
	public void edit(int index, int toEdit, Object newVal) {
		
		switch (toEdit) {
			case 0 :
				//edit formula
				/*le code fourni ne permet pas d'éditer une fonction existante
				 *solution : insérer une nouvelle fonction au meme index que 
				 *l'ancienne puis supprimer cette dernière */
				//ajout nouvelle
				functions.add(index, FunctionFactory.createFunction((String) newVal));
				funcColors.add(index, funcColors.get(index));
				funcStates.add(index, funcStates.get(index));
				//suppression ancienne
				functions.remove(index+1);
				funcColors.remove(index+1);
				funcStates.remove(index+1);
				break;
			case 1 :
				//edit color
				funcColors.set(index, (Color) newVal);
				break;
		}
		repaint();
	}
    
	public void changeActiveFunctions(int[] selectedRows) {
		//reset all to false
		for(int i = 0; i < functions.size(); ++i) {
    		funcStates.set(i, false);
    	}
		//set selected to true (functions will appear in bold)
		for(int i = 0; i < selectedRows.length; ++i) {
			funcStates.set(selectedRows[i], true);
    	}
	}
		
	public Dimension getPreferredSize() {
		return new Dimension(W, H);
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		W = getWidth();
		H = getHeight();

		g2 = (Graphics2D)g;

		// background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, W, H);
		
		g2.setColor(Color.BLACK);

		// box
		g2.translate(MARGIN, MARGIN);
		W -= 2*MARGIN;
		H -= 2*MARGIN;
		if(W < 0 || H < 0) { 
			return; 
		}
		
		g2.drawRect(0, 0, W, H);
		
		g2.drawString("x", W, H+10);
		g2.drawString("y", -10, 0);
		
	
		// plot
		g2.clipRect(0, 0, W, H);
		g2.translate(-MARGIN, -MARGIN);

		// x values
		final int N = W/STEP + 1;
		final double dx = dx(STEP);
		double xs[] = new double[N];
		int    Xs[] = new int[N];
		for(int i = 0; i < N; i++) {
			double x = xmin + i*dx;
			xs[i] = x;
			Xs[i] = X(x);
		}

		//draw functions
		for(int i = 0; i < functions.size(); ++i) {
			//set bold if function is "active"
			g2.setStroke(funcStates.get(i).booleanValue() == true ? new BasicStroke(2) : new BasicStroke(1));
			//set color
			g2.setColor(funcColors.get(i));
			// y values
			int Ys[] = new int[N];
			for(int j = 0; j < N; j++) {
				Ys[j] = Y(functions.get(i).y(xs[j]));
			}
			g2.drawPolyline(Xs, Ys, N);
		}
		
		//reset 
		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.BLACK);
		
		g2.setClip(null);

		// axes
		drawXTick(g2, 0);
		drawYTick(g2, 0);
		
		double xstep = unit((xmax-xmin)/10);
		double ystep = unit((ymax-ymin)/10);

		g2.setStroke(dash);
		for(double x = xstep; x < xmax; x += xstep)  { drawXTick(g2, x); }
		for(double x = -xstep; x > xmin; x -= xstep) { drawXTick(g2, x); }
		for(double y = ystep; y < ymax; y += ystep)  { drawYTick(g2, y); }
		for(double y = -ystep; y > ymin; y -= ystep) { drawYTick(g2, y); }
	}
	
	protected double dx(int dX) { 
		return  (double)((xmax-xmin)*dX/W);
	}
	protected double dy(int dY) {
		return -(double)((ymax-ymin)*dY/H);
	}

	protected double x(int X) {
		return xmin+dx(X-MARGIN);
	}
	protected double y(int Y) {
		return ymin+dy((Y-MARGIN)-H);
	}
	
	protected int X(double x) { 
		int Xs = (int)round((x-xmin)/(xmax-xmin)*W);
		return Xs + MARGIN; 
	}
	protected int Y(double y) { 
		int Ys = (int)round((y-ymin)/(ymax-ymin)*H);
		return (H - Ys) + MARGIN;
	}
		
	protected void drawXTick(Graphics2D g2, double x) {
		if(x > xmin && x < xmax) {
			final int X0 = X(x);
			g2.drawLine(X0, MARGIN, X0, H+MARGIN);
			g2.drawString((new Double(x)).toString(), X0, H+MARGIN+15);
		}
	}
	
	protected void drawYTick(Graphics2D g2, double y) {
		if(y > ymin && y < ymax) {
			final int Y0 = Y(y);
			g2.drawLine(0+MARGIN, Y0, W+MARGIN, Y0);
			g2.drawString((new Double(y)).toString(), 5, Y0);
		}
	}
	
	protected static double unit(double w) {
		double scale = pow(10, floor(log10(w)));
		w /= scale;
		if(w < 2)      { w = 2; } 
		else if(w < 5) { w = 5; }
		else           { w = 10; }
		return w * scale;
	}
	

	protected void translate(int dX, int dY) {
		double dx = dx(dX);
		double dy = dy(dY);
		xmin -= dx; xmax -= dx;
		ymin -= dy; ymax -= dy;
		repaint();	
	}
	
	protected void zoom(Point center, int dz) {
		double x = x(center.x);
		double y = y(center.y);
		double ds = exp(dz*.01);
		xmin = x + (xmin-x)/ds; xmax = x + (xmax-x)/ds;
		ymin = y + (ymin-y)/ds; ymax = y + (ymax-y)/ds;
		repaint();	
	}
	
	protected void zoom(Point p0, Point p1) {
		double x0 = x(p0.x);
		double y0 = y(p0.y);
		double x1 = x(p1.x);
		double y1 = y(p1.y);
		xmin = min(x0, x1); xmax = max(x0, x1);
		ymin = min(y0, y1); ymax = max(y0, y1);
		repaint();	
	}
	
	//TODO : Refactor version Machine à états
	public class GrapherListener implements MouseListener, MouseMotionListener, MouseWheelListener {

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
	        if (e.getPreciseWheelRotation() < 0) {
	            //Zoom
	            zoom(e.getPoint(), 5);
	        }
	        else if (e.getPreciseWheelRotation() > 0) {
	            //Dézoom
	            zoom(e.getPoint(), -5);
	        }
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			//bouton gauche : déplace le repère
	        if (SwingUtilities.isLeftMouseButton(e)){
	        	System.out.println("DRAG LEFT");
	            setCursor(new Cursor(Cursor.HAND_CURSOR));
	            //translate(e.getX(),e.getY());
	            translate(e.getX() - (int)pts.getX(), e.getY() - (int)pts.getY());
	            pts = e.getPoint();
	        }
	    	//bouton droit : zoom sur zone sélectionnée
	        if (SwingUtilities.isRightMouseButton(e)) {
	        	System.out.println("DRAG RIGHT");
				Rectangle r = new Rectangle(pts);
				r.add(e.getPoint());
				g2.draw(r);
	        }
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
	        if (e.getButton() == 1){
	            // Zoom de 5% centré sur le curseur
	            zoom(e.getPoint(), 5);
	        }
	        if (e.getButton() == 3){
	            // Dézoom de 5% centré sur le curseur.
	            zoom(e.getPoint(), -5);
	        }
		}

		@Override
		public void mousePressed(MouseEvent e) {
			pts = e.getPoint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
	    	System.out.println("RELEASED");
	        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	        if (e.getButton() == 3) {
	            zoom(pts, e.getPoint());
	        }
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
