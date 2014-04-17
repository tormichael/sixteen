package tor.java.sixteen;

import javax.swing.table.AbstractTableModel;

public class tmCSVData extends AbstractTableModel 
{
	private CSVData _csv;
	
	public tmCSVData(CSVData aCSV)
	{
		_csv = aCSV;
	}
	
	@Override
	public int getRowCount() 
	{
		return _csv.getRowCount();
	}

	@Override
	public int getColumnCount() 
	{
		return _csv.getColumnCount();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		return _csv.getValueAt(rowIndex, columnIndex);
	}
	
	@Override
	public String getColumnName(int column) 
	{
		return _csv.getColumnName(column);
	}

}
