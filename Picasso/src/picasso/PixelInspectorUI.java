package picasso;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class PixelInspectorUI extends JPanel {
	
	//PF: UI of PixelInspector Tool.
	//Contains JLabels for x and y coordinates and rgb values.

	private JLabel x_label;
	private JLabel y_label;
	private JLabel pixel_info;
	
	public PixelInspectorUI() {
		//PF: Labels are instantiated
		x_label = new JLabel("X: ");
		y_label = new JLabel("Y: ");
		pixel_info = new JLabel("(r,g,b)");

		//PF: UI is set to GridLayout (3x1)
		setLayout(new GridLayout(3,1));
		
		//PF: x, y, and rgb labels are added to PixelInspectorUI
		add(x_label);
		add(y_label);
		add(pixel_info);
	}
	
	//PF: When the mouse is pressed, the text 
	//in the JLabels are updated to the correct 
	//x, y, and rgb values.
	public void setInfo(int x, int y, Pixel p) {
		x_label.setText("X: " + x);
		y_label.setText("Y: " + y);
		pixel_info.setText(String.format("(%3.2f, %3.2f, %3.2f)", p.getRed(), p.getBlue(), p.getGreen()));		
	}
}
