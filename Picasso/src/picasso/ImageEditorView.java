package picasso;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageEditorView extends JPanel implements ActionListener {

	//PF: This is the main UI.
	//This is what the user sees and can interact with.
	//Contains main_frame and model

	private JFrame main_frame;
	private PictureView frame_view;
	private ImageEditorModel model;
	private ToolChooserWidget chooser_widget;
	private JPanel tool_ui_panel;
	private JPanel tool_ui;
	private MenuBar menu_bar;
	private JFileChooser file_chooser;
	private BufferedImage bi;
	private File save_file;

	public ImageEditorView(JFrame main_frame, ImageEditorModel model) {
		this.main_frame = main_frame;
		this.model = model;

		//PF: Set View Layout to BorderLayout
		setLayout(new BorderLayout());

		//PF: (Edited) Frame View is set to Model's Picture View
		frame_view = model.getCurrentView();

		//PF: Frame View gets placed in the center
		add(frame_view, BorderLayout.CENTER);

		//PF: Tool UI Panel contains UI panel of current tool
		//as well as JComboBox from Chooser Widget.
		tool_ui_panel = new JPanel();
		tool_ui_panel.setLayout(new BorderLayout());

		//PF: Chooser Widget is a JComboBox that holds all the tools
		chooser_widget = new ToolChooserWidget();

		//PF: Chooser Widget is placed NORTH in the Tool UI Panel,
		//which is then placed SOUTH in the view
		tool_ui_panel.add(chooser_widget, BorderLayout.NORTH);
		add(tool_ui_panel, BorderLayout.SOUTH);

		//PF: This is the actual UI Panel of the current tool
		tool_ui = null;

		//PF: MenuBar is initialized
		menu_bar = new MenuBar(this);

		//PF: MenuBar is added to the Main Frame
		main_frame.setJMenuBar(menu_bar);

		//PF: File Chooser is instantiated. 
		//This is used for open and save options.
		file_chooser = new JFileChooser();

		//PF: Created FileFilter to filter for only images
		//(bmp, jpg, jpeg, wbmp, png, gif)
		FileFilter image_filter = new FileNameExtensionFilter( "Image files", ImageIO.getReaderFileSuffixes());

		//PF: Set Image Filter on File Chooser so it only displays images
		file_chooser.setFileFilter(image_filter);

		//PF: Buffered Image is instantiated
		bi = null;

		//PF: Saved File is instantiated
		save_file = null;
	}

	//PF: Converts a Buffered Image into a Picture
	private Picture BufferedImageToPicture(BufferedImage bi) {
		Picture picture = new PictureImpl(bi.getWidth(), bi.getHeight());
		for (int x=0; x<bi.getWidth(); x++) {
			for (int y=0; y<bi.getHeight(); y++) {
				picture.setPixel(x, y, ColorPixel.rgbToPixel(bi.getRGB(x, y)));
			}
		}
		return picture;
	}

	//PF: Converts a Picture into a Buffered Image
	private BufferedImage PictureToBufferedImage(Picture picture) {
		BufferedImage bi = new BufferedImage(picture.getWidth(), picture.getHeight(), BufferedImage.TYPE_INT_RGB);
		for (int x=0; x<picture.getWidth(); x++) {
			for (int y=0; y<picture.getHeight(); y++) {
				bi.setRGB(x, y, ColorPixel.pixelToRGB(picture.getPixel(x, y)));
			}
		}
		return bi;
	}

	//PF: Adds controller as a ToolChoiceListener to Chooser Widget.
	//NOTE that I said Chooser Widget and NOT View or Frame View.
	public void addToolChoiceListener(ToolChoiceListener l) {
		chooser_widget.addToolChoiceListener(l);
	}
	//NOTE: This gets added to Chooser Widget's ArrayList of ToolChoiceListeners.

	//PF: Gets Current Tool's name.
	//Delegated to Chooser Widget's getCurrentToolName method.
	public String getCurrentToolName() {
		return chooser_widget.getCurrentToolName();
	}

	//PF: This is where the Tool UI gets changed visually when the tool changes. 
	//View gets revalidated, and Main Frame gets repacked.
	public void installToolUI(JPanel ui) {
		if (tool_ui != null) {
			tool_ui_panel.remove(tool_ui);
		}
		tool_ui = ui;
		tool_ui_panel.add(tool_ui, BorderLayout.CENTER);
		validate();
		main_frame.pack();
	}

	//PF: Packs main_frame
	public void pack(){
		main_frame.pack();
	}

	//PF: Adds ImageEditorController as a MouseMotion and MouseListener to the Frame View.
	//NOTE these are to the Frame View and NOT View or Chooser Widget
	@Override
	public void addMouseMotionListener(MouseMotionListener l) {
		frame_view.addMouseMotionListener(l);
	}

	@Override
	public void removeMouseMotionListener(MouseMotionListener l) {
		frame_view.removeMouseMotionListener(l);
	}

	@Override
	public void addMouseListener(MouseListener l) {
		frame_view.addMouseListener(l);
	}

	@Override
	public void removeMouseListener(MouseListener l) {
		frame_view.removeMouseListener(l);
	}

	//PF: Action Listener for Menu Bar
	@Override
	public void actionPerformed(ActionEvent e) {

		//PF: Switch case is used to determine which menu item is pressed through its action command
		switch(e.getActionCommand()) {

		case "Open":

			//PF: Displays open dialog
			//Returns int value to determine if a file was chosen.
			int file_was_chosen = file_chooser.showOpenDialog(this);

			//PF: If user chose an image, fileChosen would equal APPROVE_OPTION, then continue.
			//If not, then break.
			if (file_was_chosen == JFileChooser.APPROVE_OPTION) {
				//PF: File chosen gets loaded into Buffered Image
				//**Throws IO Exception which is why it needs a try catch

				try {
					bi = ImageIO.read(file_chooser.getSelectedFile());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				//PF: Buffered Image gets turned into an Observable Picture
				ObservablePicture picture = BufferedImageToPicture(bi).createObservable();

				//PF: Controller calls model to change current picture to new picture
				model.setCurrent(picture);

				//PF: Main Frame gets repacked to fit new picture
				main_frame.pack();
			}

			break;

		case "Save":

			//PF: While loop allows for file chooser to reopen if file type chosen is not supported
			save:
				while (true) {

					//PF: Displays open dialog
					//Returns value to determine if a file was chosen.
					int file_was_saved = file_chooser.showSaveDialog(this);

					//PF: If user chose to save the image, fileChosen would equal APPROVE_OPTION, then continue.
					//If not, then break.
					if (file_was_saved == JFileChooser.APPROVE_OPTION) {

						//PF: Save File gets file chosen from File Chooser
						//This allows the user to save the file in a certain directory with the specified name
						save_file = file_chooser.getSelectedFile();

						//PF: The file name is gotten from File Chooser
						String file_name = file_chooser.getSelectedFile().getName();

						//PF: The file type is gotten from the file name by getting the substring after the period
						//**Surrounded by try/catch, so that if the file name does not contain a "."
						//the file type is defaulted to png.
						String file_type = null;

						try {
							file_type = file_name.substring(file_name.lastIndexOf("."));
						} catch (IndexOutOfBoundsException e1) {
							file_type = ".png";

							//PF: Adds .png to the end of the path of the file to turn the file into a png
							save_file = new File(file_chooser.getSelectedFile().getPath() + file_type);
						}

						//PF: Current Picture in model gets turned into a buffered image
						bi = PictureToBufferedImage(model.getCurrent());

						//PF: Saves the current picture into an image in one of the following formats:
						//bmp, jpg, jpeg, wbmp, png, gif
						try {
							switch (file_type) {
							case ".bmp": 
								ImageIO.write(bi, "bmp", save_file);
								break;
							case ".jpg":
								ImageIO.write(bi, "jpg", save_file);
								break;
							case ".jpeg":
								ImageIO.write(bi, "jpeg", save_file);
								break;
							case ".wbmp":
								ImageIO.write(bi, "wbmp", save_file);
								break;
							case ".png":
								ImageIO.write(bi, "png", save_file);
								break;
							case ".gif":
								ImageIO.write(bi, "gif", save_file);
								break;
							default:
								JOptionPane.showMessageDialog(null, "File must be saved as a bmp, wbmp, jpg, jpeg, png, or gif.", 
										"File Type Not Supported", JOptionPane.ERROR_MESSAGE);

								//PF: If file type chosen is not supported, goes back to save label to choose a new file
								continue save;
							}
						} catch (IOException e1) {

						}
						//**NOTE** This is done though the write method in ImageIO
						//It takes in a (buffered image, format, file output)
						//**This method throws an IO Exception which is why it needs the try/catch
					}

					break;
				}

		}

	}

}
