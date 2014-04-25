package tor.java.sixteen;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

public class tmDBMColDef  extends AbstractTableModel  
{
	public class DBMColDef
	{
		String Name;
		String Type;
		Boolean IsPKey;
		
		public DBMColDef()
		{
			this (null, null, false);
		}
		public DBMColDef(String aName, String aType, Boolean aIsPKey)
		{
			Name = aName;
			Type = aType;
			IsPKey = aIsPKey;
		}
	}
	
	private Sixteen _wld;
	private ArrayList<DBMColDef> 	_arrColDef;
	
	public tmDBMColDef(Sixteen aWld)
	{
		_wld = aWld;
		_arrColDef = new ArrayList<tmDBMColDef.DBMColDef>();
	}

	@Override
	public int getRowCount() 
	{
		return _arrColDef.size() + 1;
	}

	@Override
	public int getColumnCount() 
	{
		return 3;
	}
	
	@Override
	public String getColumnName(int column) 
	{
		String ret =  null;
		switch (column) 
		{
		case 0:
			ret = _wld.getString("Table.Column.Name.CSVFld.Name");
			break;
		case 1:
			ret = _wld.getString("Table.Column.Name.CSVFld.Type");
			break;
		case 2:
			ret = _wld.getString("Table.Column.Name.CSVFld.IsPrimaryKey");
			break;
		default:
			break;
		}
		return (ret != null ? ret : super.getColumnName(column));
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		return columnIndex == 2 ? Boolean.class  : super.getColumnClass(columnIndex);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		Object ret = null;
		if (_arrColDef.size() > rowIndex)
		{
			DBMColDef col = _arrColDef.get(rowIndex);
			switch (columnIndex) 
			{
			case 0:
				ret = col.Name;
				break;
			case 1:
				ret = col.Type;
				break;
			case 2:
				ret = col.IsPKey;
				break;
			default:
				break;
			}
		}
		return ret;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) 
	{
		return true;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
	{
		int fldCount = _arrColDef.size(); 
		if ( fldCount>= rowIndex)
		{
			DBMColDef col = null;
			if (fldCount == rowIndex)
			{
				col = new DBMColDef();
				_arrColDef.add(col);
			}
			else
			{
				col = _arrColDef.get(rowIndex);
			}
			
			switch (columnIndex)
			{
				case 0:
					col.Name = aValue.toString();
					break;
				case 1:
					col.Type = aValue.toString();
					break;
				case 2:
					col.IsPKey = Boolean.parseBoolean(aValue.toString());
					break;
			}
		}
		//super.setValueAt(aValue, rowIndex, columnIndex);
	}

	
}
