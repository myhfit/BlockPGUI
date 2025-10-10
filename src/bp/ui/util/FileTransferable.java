package bp.ui.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileTransferable implements Transferable
{
	protected List<String> m_filenames;

	public FileTransferable(List<String> filenames)
	{
		m_filenames = filenames;
	}

	public DataFlavor[] getTransferDataFlavors()
	{
		return new DataFlavor[] { DataFlavor.javaFileListFlavor, DataFlavor.stringFlavor };
	}

	public boolean isDataFlavorSupported(DataFlavor flavor)
	{
		return DataFlavor.javaFileListFlavor.equals(flavor);
	}

	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
	{
		if (flavor == DataFlavor.javaFileListFlavor)
			return m_filenames;
		else if (flavor == DataFlavor.stringFlavor)
			return String.join(File.pathSeparator, m_filenames);
		return null;
	}
}
