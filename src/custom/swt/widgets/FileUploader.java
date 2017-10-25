package custom.swt.widgets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class FileUploader {

	private Composite generalComposite;

	private Composite stackLayoutComposite;
	private Label noFileUploadedLb;

	private StackLayout stackLayout;

	private Composite filePreviewComposite;

	private Label uploadFromFileSystemLb;
	private Label uploadFromSampleLb;
	private Label donwloadSampleLb;
	private Label infoLb;
	private Link cancelUploadLink;

	String browsTooltip = "Upload a file";
	String sampleTooltip = "Use sample file";
	String downloadTooltip = "Download sample file";
	String emptyTooltip = "Empty table";
	String infoTooltip = "More information";

	private Path uploadedFile;

	private Integer maxNumberToDisplayAtFirst = null;

	private boolean headerPresent;

	public FileUploader(Composite parent, Path sampleFile, String noFileUploadedText, Boolean dragAndDropAllowed,
			String txtLbInfo, Boolean headerPresent) {

		this.headerPresent = headerPresent;

		generalComposite = new Composite(parent, SWT.NONE);
		generalComposite.setBounds(0, 0, parent.getBounds().width, parent.getBounds().height);

		stackLayoutComposite = new Composite(generalComposite, SWT.NONE);
		stackLayoutComposite.setBounds(3, 30, generalComposite.getBounds().width - 10,
				generalComposite.getBounds().height - 35);

		stackLayout = new StackLayout();
		stackLayoutComposite.setLayout(stackLayout);

		// label to show the message when no file has been uploaded
		noFileUploadedLb = new Label(stackLayoutComposite, SWT.CENTER | SWT.BORDER);
		noFileUploadedLb.setForeground(stackLayoutComposite.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
		noFileUploadedLb.setBounds(stackLayoutComposite.getBounds());
		noFileUploadedLb.setText(noFileUploadedText);

		// click to upload
		noFileUploadedLb.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				userUpload(arg0);
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		// composite to contain the file preview
		filePreviewComposite = new Composite(stackLayoutComposite, SWT.NONE);
		filePreviewComposite.setBounds(stackLayoutComposite.getBounds());

		if (dragAndDropAllowed) {
			enableDragAndDrop();
		}

		// get the images for the upper icons
		Image browsImage = null;
		Image cancelImage = null;
		Image infoImage = null;
		Image uploadImage = null;
		Image downloadImage = null;

		// TODO pheraps it would be better if we let the exception be thrown
		// out:
		// this is a jar and we don't have any logger here

		try {
			browsImage = loadImageFromFile(getResourceAsStream("resources/rsz_open.png"));
			cancelImage = loadImageFromFile(getResourceAsStream("resources/rsz_cancel.png"));
			infoImage = loadImageFromFile(getResourceAsStream("resources/rsz_info.png"));
			uploadImage = loadImageFromFile(getResourceAsStream("resources/rsz_upload.png"));
			downloadImage = loadImageFromFile(getResourceAsStream("resources/rsz_download.png"));
		} catch (Exception e) {
			System.out.println("there were some problems in image loading");
		}

		// add the labels -icons- with their functionalities
		/*
		 ******** UPLOAD FROM FILE SYSTEM***********************************
		 */
		uploadFromFileSystemLb = new Label(generalComposite, SWT.NONE);
		uploadFromFileSystemLb.setBounds(3, 4, 22, 22);
		uploadFromFileSystemLb.setImage(browsImage);
		uploadFromFileSystemLb.setToolTipText(browsTooltip);
		// make the cursor an end when the cursor is hover
		uploadFromFileSystemLb.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent arg0) {
				final Cursor cursor = new Cursor(stackLayoutComposite.getDisplay(), SWT.CURSOR_HAND);
				uploadFromFileSystemLb.setCursor(cursor);
			}
		});
		uploadFromFileSystemLb.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				userUpload(arg0);
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		/*
		 * UPLOAD SAMPLE
		 */
		uploadFromSampleLb = new Label(generalComposite, SWT.NONE);
		uploadFromSampleLb.setBounds(28, 4, 22, 22);
		uploadFromSampleLb.setImage(uploadImage);
		uploadFromSampleLb.setToolTipText(sampleTooltip);
		// make the cursor an end when the cursor is hover
		uploadFromSampleLb.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent arg0) {
				final Cursor cursor = new Cursor(stackLayoutComposite.getDisplay(), SWT.CURSOR_HAND);
				uploadFromSampleLb.setCursor(cursor);
			}
		});
		// create the table with the sample file
		uploadFromSampleLb.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				// creates the table
				try {
					stackLayoutComposite.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							try {
								onFileUploaded(sampleFile);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});

		/*
		 *************** DOwNWLOAD SAMPLE**************************
		 */
		donwloadSampleLb = new Label(generalComposite, SWT.NONE);
		donwloadSampleLb.setBounds(53, 4, 22, 22);
		donwloadSampleLb.setImage(downloadImage);
		donwloadSampleLb.setToolTipText(downloadTooltip);
		// make the cursor an end when the cursor is hover
		donwloadSampleLb.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent arg0) {
				final Cursor cursor = new Cursor(donwloadSampleLb.getDisplay(), SWT.CURSOR_HAND);
				donwloadSampleLb.setCursor(cursor);
			}
		});
		// download the sample
		donwloadSampleLb.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				try {
					String[] filterNames = new String[] { "Text file (.txt)" };
					String[] filterExtensions = new String[] { "*.txt" };

					// open the file dialog
					FileDialog dialog = new FileDialog(generalComposite.getShell(), SWT.SAVE);

					String fileName = sampleFile.toFile().getName();
					if (fileName != null) {
						dialog.setFileName(fileName);
					}

					dialog.setFilterNames(filterNames);
					dialog.setFilterExtensions(filterExtensions);
					dialog.setOverwrite(true); // ask if overwrite
					String fn = dialog.open();

					if (fn != null) {
						Files.copy(sampleFile, Paths.get(fn));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		/*
		 ************** GET INFORMATION********************
		 */
		infoLb = new Label(generalComposite, SWT.NONE);
		infoLb.setBounds(78, 4, 22, 22);
		infoLb.setImage(infoImage);
		infoLb.setToolTipText(infoTooltip);
		// make the cursor an end when the cursor is hover
		infoLb.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent arg0) {
				final Cursor cursor = new Cursor(infoLb.getDisplay(), SWT.CURSOR_HAND);
				infoLb.setCursor(cursor);
			}
		});
		// display information when clicked
		infoLb.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
				InfoPopup infoPopup = new InfoPopup(stackLayoutComposite, txtLbInfo);
				infoPopup.setPosition(infoLb.toDisplay(arg0.x, arg0.y));
				infoPopup.open();

			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		/*
		 ************** CANCEL ********************
		 */
		cancelUploadLink = new Link(generalComposite, SWT.NONE);
		cancelUploadLink.setBounds(generalComposite.getBounds().x + generalComposite.getBounds().width - 50, 7, 50, 19);
		cancelUploadLink.setText("<a>Cancel</a>");
		cancelUploadLink.setToolTipText(emptyTooltip);
		// make the cursor an end when the cursor is hover
		cancelUploadLink.addMouseMoveListener(new MouseMoveListener() {

			@Override
			public void mouseMove(MouseEvent arg0) {
				final Cursor cursor = new Cursor(cancelUploadLink.getDisplay(), SWT.CURSOR_HAND);
				cancelUploadLink.setCursor(cursor);
			}
		});
		// cancel the uploaded file
		cancelUploadLink.addMouseListener(new MouseListener() {

			@Override
			public void mouseUp(MouseEvent arg0) {
				cancelUploadLink.getDisplay().asyncExec(new Runnable() {

					@Override
					public void run() {
						stackLayout.topControl = noFileUploadedLb;
						stackLayoutComposite.layout();

						for (Control child : filePreviewComposite.getChildren()) {
							child.dispose();
						}

						uploadedFile = null;
					}
				});
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				// TODO Auto-generated method stub
			}
		});

		// regulate the stackLayout to make it start from empty
		stackLayout.topControl = noFileUploadedLb;
		stackLayoutComposite.layout();
	}

	public void setBackgroundColor(Color backgroundColor) {
		noFileUploadedLb.setBackground(backgroundColor);
	}

	private void userUpload(MouseEvent arg0) {
		FileDialog dlg = new FileDialog(arg0.widget.getDisplay().getActiveShell());

		dlg.setFilterNames(new String[] { "txt files" });
		dlg.setFilterExtensions(new String[] { "*.txt", "*.*" });
		String filepath = dlg.open();
		if (filepath == null)
			return;

		String fileName = filepath.substring(filepath.lastIndexOf(File.separator) + 1);
		if (fileName.lastIndexOf('.') != -1) {
			fileName = fileName.substring(0, fileName.lastIndexOf('.'));
		}

		try {
			onFileUploaded(Paths.get(filepath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onFileUploaded(Path filePath) throws Exception {
		// empty the table content if it already exists

		for (Control child : filePreviewComposite.getChildren()) {
			child.dispose();
		}

		String fileContent = readFile(filePath.toFile(), Charset.defaultCharset());
		createTable(filePreviewComposite, fileContent);

		stackLayout.topControl = filePreviewComposite;
		stackLayoutComposite.layout();

		this.uploadedFile = filePath;
	}

	private void enableDragAndDrop() {
		// drop target
		// Receive data in Text or File format
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { fileTransfer };

		// Allow data to be copied or moved to the drop target
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		DropTarget target = new DropTarget(noFileUploadedLb, operations);
		target.setTransfer(types);

		target.addDropListener(new DropTargetListener() {
			public void dragEnter(final DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
			}

			public void dragOperationChanged(DropTargetEvent event) {

				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				// allow text to be moved but files should only be copied
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					if (event.detail != DND.DROP_COPY) {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragLeave(DropTargetEvent event) {

			}

			public void dropAccept(DropTargetEvent event) {

			}

			public void drop(DropTargetEvent event) {
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					String[] files = (String[]) event.data;

					if (files.length == 1) {
						// launch the listener
						try {
							onFileUploaded(Paths.get(files[0]));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
			}
		});
	}

	public Path getFileUploaded() {
		return uploadedFile;
	}

	public void cancelFileUploaded() {
		stackLayout.topControl = noFileUploadedLb;
		stackLayoutComposite.layout();

		for (Control child : filePreviewComposite.getChildren()) {
			child.dispose();
		}

		uploadedFile = null;
	}

	public Integer getMaxNumberToDisplayAtFirst() {
		return maxNumberToDisplayAtFirst;
	}

	public void setMaxNumberToDisplayAtFirst(Integer maxNumberToDisplayAtFirst) {
		this.maxNumberToDisplayAtFirst = maxNumberToDisplayAtFirst;
	}

	public void setAlreadyUploadedFile(Path path) {
		try {
			onFileUploaded(path);
		} catch (Exception e) {
		}
	}

	/*
	 * allow you to use an image from a file
	 */
	private Image loadImageFromFile(InputStream stream) throws IOException {

		try {
			Display display = Display.getCurrent();
			ImageData data = new ImageData(stream);
			if (data.transparentPixel > 0) {
				return new Image(display, data, data.getTransparencyMask());
			}
			return new Image(display, data);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private InputStream getResourceAsStream(String resourceName) {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream stream = classLoader.getResourceAsStream(resourceName);
		return stream;
	}

	private void createTable(Composite parent, String csvfile) throws Exception {

		// read the csv content -actually it's supposed to be a tsv not a csv-
		String fileAsString = csvfile;

		String[] lines = fileAsString.split("\n");

		String[] columnNames = null;
		if (headerPresent) {
			columnNames = lines[0].split("\t");

			// remove \n or \r if presents
			for (int i = 0; i < columnNames.length; i++) {
				if (columnNames[i].contains("\r")) {
					columnNames[i] = columnNames[i].replace("\r", "");
				}
			}
		}

		int rowNumber = headerPresent ? (lines.length - 1) : lines.length;

		int maxColumnNumber = 0;
		for (String line : lines) {
			String[] cells = line.split("\t");
			if (cells.length > maxColumnNumber) {
				maxColumnNumber = cells.length;
			}
		}

		int columnNumber = headerPresent ? columnNames.length : maxColumnNumber;

		String[][] contentCells = new String[rowNumber][columnNumber];
		for (int rowIndex = 0; rowIndex < rowNumber; rowIndex++) {

			int cellIndex = headerPresent ? rowIndex + 1 : rowIndex;

			String[] cells = lines[cellIndex].split("\t");
			for (int columnIndex = 0; columnIndex < columnNumber; columnIndex++) {
				try {
					contentCells[rowIndex][columnIndex] = cells[columnIndex];
				} catch (ArrayIndexOutOfBoundsException e) {
					contentCells[rowIndex][columnIndex] = "";
				}
			}
		}

		createTable(parent, columnNames, contentCells, headerPresent);

	}

	private String readFile(File file, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
		return new String(encoded, encoding);
	}

	/*
	 * creates a table in the parent composite (that should be empty)
	 */
	private void createTable(Composite parent, String[] columnNames, String[][] rows, Boolean headerPresent) {
		// dispose all the children of the composite
		for (Control control : parent.getChildren()) {
			control.dispose();
		}

		// build the table
		Table table = new Table(parent, SWT.V_SCROLL | SWT.H_SCROLL);
		if (headerPresent)
			table.setHeaderVisible(true);

		table.setBounds(0, 0, parent.getSize().x, parent.getSize().y);

		int columnNumber = headerPresent ? columnNames.length : rows[0].length;

		for (int loopIndex = 0; loopIndex < columnNumber; loopIndex++) {
			TableColumn column = new TableColumn(table, SWT.NULL);
			if (headerPresent)
				column.setText(columnNames[loopIndex]);
		}

		int rowsToBeDisplayed = (maxNumberToDisplayAtFirst == null) ? rows.length
				: Math.min(rows.length, maxNumberToDisplayAtFirst);

		for (int rowIndex = 0; rowIndex < rowsToBeDisplayed; rowIndex++) {
			TableItem item = new TableItem(table, SWT.NULL);
			for (int loopIndex = 0; loopIndex < columnNumber; loopIndex++) {
				item.setText(loopIndex, rows[rowIndex][loopIndex]);
			}
		}

		table.getColumn(0).setWidth(100);
		for (int loopIndex = 1; loopIndex < columnNumber; loopIndex++) {
			table.getColumn(loopIndex).pack();
		}
	}
}
