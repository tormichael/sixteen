package tor.java.sixteen;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import JCommonTools.CC;

public class tmCSVFields extends AbstractTableModel 
{
	private CSVData _csv;
	private Point _currPos;
	
	public void setCSVDaata (CSVData aCSV)
	{
		_csv = aCSV;
	}
	public CSVData getCSVData ()
	{
		return _csv;
	}

	public void set_currPos(Point _currPos) 
	{
		this._currPos = _currPos;
	}
	
	
	public tmCSVFields(CSVData aCSV)
	{
		_csv = aCSV;
		this._currPos = null;
	}
	
	@Override
	public int getRowCount() 
	{
		if (_csv != null)
			return _csv.getCSVDef().getFieldsCount(true); //_csv.getCSVDef().Fields.size();
		else
			return 0;
	}

	@Override
	public int getColumnCount() 
	{
		return 6;
	}
	
	@Override
	public String getColumnName(int column) 
	{
		String ret =  null;
		switch (column) 
		{
		case 0:
			ret = _csv.getWld().getString("Table.Column.Name.CSVFld.Number");
			break;
		case 1:
			ret = "+"; //_csv.getWld().getString("Table.Column.Name.CSVFld.Separator");
			break;
		case 2:
			ret = _csv.getWld().getString("Table.Column.Name.CSVFld.Name");
			break;
		case 3:
			ret = _csv.getWld().getString("Table.Column.Name.CSVFld.Type");
			break;
		case 4:
			ret = _csv.getWld().getString("Table.Column.Name.CSVFld.IsPrimaryKey");
			break;
		case 5:
			ret = _csv.getWld().getString("Table.Column.Name.CSVFld.Template");
			break;
		default:
			break;
		}
		return (ret != null ? ret : super.getColumnName(column));
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) 
	{
		return columnIndex == 4 ? Boolean.class  : super.getColumnClass(columnIndex);
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		Object ret = null;
		
		if (_csv != null)
		{
			CSVDefinition csvDef = _csv.getCSVDef();
			switch (columnIndex) 
			{
			case 0:
				ret = csvDef.getField(rowIndex, true).Number;
				break;
			case 1:
			{
				String st = csvDef.getField(rowIndex, true).Separator; 
				if (_currPos != null 
					&& st != null 
					&& st.equals(" ") 
					&& !(_currPos.x == columnIndex && _currPos.y == rowIndex)
				)
					ret = _csv.getWld().getString("Text.Message.Space");
				else
					ret = st;
				break;
			}
			case 2:
				ret = csvDef.getField(rowIndex, true).Name;
				break;
			case 3:
				ret = csvDef.getField(rowIndex, true).Type;
				break;
			case 4:
				ret = csvDef.getField(rowIndex, true).IsPrimaryKey;
				break;
			case 5:
				ret = csvDef.getField(rowIndex, true).Template;
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
		//return columnIndex == 1 || columnIndex == 2 || columnIndex == 3 || columnIndex == 4 || columnIndex == 5;
		return true;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) 
	{
		if (_csv == null)
			return;

		CSVDefinition csvDef = _csv.getCSVDef();
		int fldCount = csvDef.getFieldsCount(true); 
		if ( fldCount>= rowIndex)
		{
			if (fldCount == rowIndex)
				csvDef.AddNewFiled();
			
			switch (columnIndex)
			{
				case 0:
					csvDef.getField(rowIndex, true).Number = aValue.toString();
					break;
				case 1:
					csvDef.getField(rowIndex, true).setSeparator(aValue.toString());
					break;
				case 2:
					csvDef.getField(rowIndex, true).Name = aValue.toString();
					break;
				case 3:
					csvDef.getField(rowIndex, true).Type = aValue.toString();
					break;
				case 4:
					csvDef.getField(rowIndex, true).IsPrimaryKey = Boolean.parseBoolean(aValue.toString());
					break;
				case 5:
					csvDef.getField(rowIndex, true).Template = aValue.toString();
					break;
			}
		}
			
		//super.setValueAt(aValue, rowIndex, columnIndex);
	}

	
}
