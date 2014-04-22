package tor.java.sixteen;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.table.AbstractTableModel;

import JCommonTools.CC;

/**
 * 
 * Date in format - Comma Separated Value (CSV) 
 * 
 * @author M.Tor
 *
 */
public class CSVData  extends AbstractTableModel
{
	private CSVDefinition		_csvDef;
	private Sixteen 			_wld;
	
	private String 				_header;
	private ArrayList<String> 	_data;
	private long 				_totalRows;
	private String 				_separator;
	private String 				_result;
	private FileReader 			_file;
	private Scanner 			_inScan;
	
	public Sixteen getWld()
	{
		return _wld;
	}
	
	public CSVDefinition getCSVDef()
	{
		return _csvDef;
	}
	
	public int getMaxRowAtOnce()
	{
		return _csvDef.MaxRowAtOnce;
	}
	public void setMaxRowAtOnce(int aMaxRowAtOnce)
	{
		if (_csvDef.MaxRowAtOnce > 0)
			_csvDef.MaxRowAtOnce = aMaxRowAtOnce;
		else
			_csvDef.MaxRowAtOnce = Integer.MAX_VALUE;
	}

	public long getTotalRows()
	{
		return _totalRows;
	}

	public boolean IsFirstRowHeader()
	{
		return _csvDef.IsFirstRowHeader; 
	}
	public void IsFirstRowHeader(boolean aValue)
	{
		_csvDef.IsFirstRowHeader = aValue;
	}
	
	public String getSeparator ()
	{
		return _csvDef.Separator;
	}
	public void setSeparator(String aSeparator)
	{
		_csvDef.Separator = aSeparator;
		_separator = "\\" + aSeparator; 
		//_setColumns();
	}

	public String getResult ()
	{
		return _result;
	}
	
	public void GenerateColumns()
	{
		if (_header != null && _header.length() > 0 && _separator != null)
			_csvDef.initFields(_header.split(_separator, -1));
		else if (_data.size() > 0 && _data.get(0) != null && _data.get(0).length() > 0 && _separator != null)
			_csvDef.initFields(_data.get(0).split(_separator, -1));
		else
			_csvDef.Fields.clear();
	}
	
	public CSVData(Sixteen aWld)
	{
		_wld = aWld;
		
		_csvDef = new CSVDefinition();
		_data  = new ArrayList<String>();
		_separator = null;
		_file = null;
		_inScan = null;
	}
	
	public int LoadCSVDef (String aFileName)
	{
		int ret = 0;
		
		CSVDefinition csvDef = CSVDefinition.Load(aFileName);
		if (csvDef != null)
		{
			_csvDef = csvDef;
			if (_csvDef.FileName.length() > 0)
				ret = Load(_csvDef.FileName, _csvDef.IsFirstRowHeader);
			setSeparator(_csvDef.Separator);
		}

		return ret;
	}
	
	public int Load(String aFileName, boolean aIsFirstHeader)
	{
		int ret = 0;
		
		_csvDef.IsFirstRowHeader = aIsFirstHeader;
		
		try{
			_file = new FileReader(aFileName);
		}
		catch (FileNotFoundException ex){
			_result = ex.getMessage();
		}
		
		if (_file != null)
		{
			_inScan = new Scanner(_file);
			
			if (aIsFirstHeader)
				_header = _inScan.nextLine();
			else
				_header = null;
			
			//_setColumns();
			_totalRows = 0;
			ret = Load(); 
		}
		
		return ret;
	}
	
	public int Load()
	{
		int ret = 0;

		_data.clear();
		String inStr = null;
		while (_inScan.hasNext())
		{
			inStr = _inScan.nextLine();
			if (inStr != null && inStr.length() > 0)
			{
				_data.add(inStr);
				ret++;
				if (ret >= _csvDef.MaxRowAtOnce)
					break;
			}
		}
		
		_totalRows += ret;

		if (ret < _csvDef.MaxRowAtOnce)
		{
			_inScan.close();
			try
			{
				_file.close();
			}
			catch (IOException ex)
			{
				_result = ex.getMessage();
			}
		}
		
		return ret;
	}
	
	@Override
	public int getRowCount() 
	{
		if (_totalRows <= Integer.MAX_VALUE)
			return (int) _totalRows;
		else
			return Integer.MAX_VALUE;
	}

	@Override
	public int getColumnCount() 
	{
		int ret = _csvDef.getFieldsCount(false); // _csvDef.Fields.size(); 
		if (ret > 0)
			return ret; 
		else
			return 1;
	}

	@Override
	public String getColumnName(int column) 
	{
		if (_csvDef.getFieldsCount(false) > 0) //_csvDef.Fields.size() > 0)
		{
			CSVColumnDef cd = _csvDef.getField(column, false);
			if (cd != null)
				return  cd.Name; //_csvDef.Fields.get(column).Name;
			else
				return "DEBUG"+column;
		}
		else
		{
			return super.getColumnName(column);
		}
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) 
	{
		Object ret = null;
		if (_data.size() > 0)
		{
			if (_separator != null)
			{
				_ind = 0;
				ret = _getValInStr(_csvDef.Fields, _data.get(rowIndex), _separator, columnIndex);
				//String[] ss = _data.get(rowIndex).split(_separator, -1);
				//if (columnIndex < ss.length)
				//	ret = ss[columnIndex];
			}
			else
			{
				ret = _data.get(rowIndex);
			}
		}			
		return ret;
	}

	public String getValueAt(int rowIndex, String aColName) 
	{
		String ret = null;
		if (_data.size() > 0)
		{
			if (_separator != null)
			{
				_ind = 0;
				ret = _getValInStr(_csvDef.Fields, _data.get(rowIndex), _separator, aColName);
			}
			else
			{
				ret = _data.get(rowIndex);
			}
		}			
		return ret;
	}
	
	private int _ind;
	private String _getValInStr(ArrayList<CSVColumnDef> aFields, String aSrc, String aSep, int aInd)
	{
		String ret = null;
		
		String[] ss = aSrc.split(aSep, -1);

		int ii = 0;
		for (CSVColumnDef col: aFields)
		{
			if (col.Fields != null && col.Fields.size() > 0)
			{
				ret = _getValInStr(col.Fields, ss[ii], col.Separator, aInd);
				if (ret != null)
					break;
			}
			else
			{
				if (_ind == aInd)
				{
					if (ii < ss.length)
					{
						ret = ss[ii];
						// 
						for (int kk = ii+1; aFields.size() == (ii+1) && kk < ss.length; kk++)
							ret += aSep + ss[kk];
					}
					break;
				}
				_ind++;
			}
			ii++;
		}
		
		return ret;
		
	}

	private String _getValInStr(ArrayList<CSVColumnDef> aFields, String aSrc, String aSep, String aColName)
	{
		String ret = null;
		
		String[] ss = aSrc.split(aSep, -1);

		int ii = 0;
		for (CSVColumnDef col: aFields)
		{
			if (col.Fields != null && col.Fields.size() > 0)
			{
				ret = _getValInStr(col.Fields, ss[ii], col.Separator, aColName);
				if (ret != null)
					break;
			}
			else if (col.Name.equals(aColName))
			{
				if (ii < ss.length)
				{
					ret = ss[ii];
				}
				break;
			}
			ii++;
		}
		
		return ret;
	}
	
	
	public void ShiftRight(int aRowInd, int aColInd)
	{
		if (aColInd == 0)
		{
			_data.set(aRowInd, _csvDef.Separator + _data.get(aRowInd));
		}
		else
		{
			String [] ss = _data.get(aRowInd).split(_separator, -1);
			if (ss.length > 1)
			{
				String str = ss[0];
				for (int ii = 1; ii < ss.length; ii++)
				{
					if(ii==aColInd)
						str += _csvDef.Separator;
					str += _csvDef.Separator + ss[ii];
				}
				_data.set(aRowInd, str);
			}
		}
	}
}
