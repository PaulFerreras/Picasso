package picasso;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

public class ImageEditorController implements ToolChoiceListener, MouseListener, MouseMotionListener {

	//PF: All user inputs should come through here.
	//Tools are instantiated here.
	//User inputs are delegated to current tool.
	//Contains view(main_frame), model, and all tools.
	
	private ImageEditorView view;
	private ImageEditorModel model;
	private Tool current_tool;
	private PixelInspectorTool inspector_tool;
	private PaintBrushTool paint_brush_tool;
//	private MenuBar menu_bar;
	
	public ImageEditorController(ImageEditorView view, ImageEditorModel model) {
		this.view = view;
		this.model = model;

		//PF: This is where tools are instantiated
		inspector_tool = new PixelInspectorTool(model);
		paint_brush_tool = new PaintBrushTool(model);
		
		//PF: Controller gets added to view
		view.addToolChoiceListener(this);
		view.addMouseListener(this);
		view.addMouseMotionListener(this);
		
		//PF: This is where the Tool UI (Pixel Inspector UI) first gets chosen at startup.
		//This calls on View's current tool name which is delegated to Chooser Widget.
		//Since Chooser Widget's first tool is Pixel Inspector, it gets selected by default.
		this.toolChosen(view.getCurrentToolName());
	}
	
	//PF: This is where current tool is actually changed.
	//The Tool UI gets changed in View through the installToolUI method
	//and then the Current Tool gets set to the tool chosen.
	@Override
	public void toolChosen(String tool_name) {
		if (tool_name.equals("Pixel Inspector")) {
			view.installToolUI(inspector_tool.getUI());
			current_tool = inspector_tool;
		} else if (tool_name.equals("Paint Brush")) {
			view.installToolUI(paint_brush_tool.getUI());
			current_tool = paint_brush_tool;
		} 
	}
	
	public void setCurrent(ObservablePicture picture) {
		model.setCurrent(picture);
	}

	//PF: All Mouse Events gets delegated to the current tool.
	@Override
	public void mouseClicked(MouseEvent e) {
		current_tool.mouseClicked(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		current_tool.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		current_tool.mouseReleased(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		current_tool.mouseEntered(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		current_tool.mouseExited(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		current_tool.mouseDragged(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		current_tool.mouseMoved(e);
	}

}
