package tor.java.sixteen;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.table.AbstractTableModel;

public class tmDBMResult extends AbstractTableModel 
{
	private ResultSet _rsData;
	//private ResultSet _rsCol;
	
	public tmDBMResult(ResultSet aRSData) //, ResultSet aRSCol)
	{
		_rsData = aRSData;
		//_rsCol = aRSCol;
	}

	@Override
	public int getRowCount() 
	{
		int ret = 0;
		if (_rsData != null)
			try	
			{
				_rsData.last();
				ret =_rsData.getRow(); 
			}
			catch (Exception ex) {}
		return ret;
	}

	@Override
	public int getColumnCount() 
	{
		int ret = 0;
		if (_rsData != null)
			try	{ ret =_rsData.getMetaData().getColumnCount(); }
			catch (Exception ex) {}
		return ret;
	}

	@Override
	public String getColumnName(int column) 
	{
		String ret = super.getColumnName(column);
		if (_rsData != null)
			try	{ ret = _rsData.getMetaData().getColumnLabel(column+1); }
			catch (Exception ex){ }
		return ret;
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		Object ret = null;
		if (_rsData != null)
			try	
			{ 
				if (_rsData.absolute(rowIndex+1))
				{
					ret = _rsData.getObject(columnIndex+1);
				}
				
			}
			catch (Exception ex) {}
		
		return ret;
	}

}
