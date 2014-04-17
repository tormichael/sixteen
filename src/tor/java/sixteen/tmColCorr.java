package tor.java.sixteen;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class tmColCorr extends AbstractTableModel 
{
	private Sixteen _wld;
	private ArrayList<CSVColCorr> _arrColCorr;
	
	public tmColCorr(Sixteen aWld, ArrayList<CSVColCorr> aArrColCorr)
	{
		_wld = aWld;
		_arrColCorr = aArrColCorr;
	}

	@Override
	public int getRowCount() 
	{
		return _arrColCorr.size();
	}

	@Override
	public int getColumnCount() 
	{
		return 5;
	}

	@Override
	public String getColumnName(int column) 
	{
		String ret =  null;
		switch (column) 
		{
		case 0:
			ret = _wld.getString("Table.Column.Name.ColCorr.TgtCol");
			break;
		case 1:
			ret = _wld.getString("Table.Column.Name.ColCorr.TgtColType");
			break;
		case 2:
			ret = _wld.getString("Table.Column.Name.ColCorr.SrcValue");
			break;
		case 3:
			ret = _wld.getString("Table.Column.Name.ColCorr.SrcFunction");
			break;
		case 4:
			ret = _wld.getString("Table.Column.Name.ColCorr.SrcFunArg");
			break;
		default:
			break;
		}
		return (ret != null ? ret : super.getColumnName(column));
	}

	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		Object ret = null;
		switch (columnIndex) 
		{
		case 0:
			ret = _arrColCorr.get(rowIndex).TgtColName;
			break;
		case 1:
			ret = _arrColCorr.get(rowIndex).TgtColType;
			break;
		case 2:
			ret = _arrColCorr.get(rowIndex).SrcValue;
			break;
		case 3:
			ret = _arrColCorr.get(rowIndex).SrcFunction;
			break;
		case 4:
			ret = _arrColCorr.get(rowIndex).SrcFunArg;
			break;
		default:
			break;
		}
		
		return ret;
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return columnIndex == 2 || columnIndex == 3 || columnIndex == 4;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
	{
		if (rowIndex < _arrColCorr.size())
		{
			switch (columnIndex)
			{
				case 2:
					_arrColCorr.get(rowIndex).SrcValue = aValue.toString();
					break;
				case 3:
					_arrColCorr.get(rowIndex).SrcFunction = aValue.toString();
					break;
				case 4:
					_arrColCorr.get(rowIndex).SrcFunArg = aValue.toString();
					break;
			}
		}	
		//super.setValueAt(aValue, rowIndex, columnIndex);
	}
	
}
