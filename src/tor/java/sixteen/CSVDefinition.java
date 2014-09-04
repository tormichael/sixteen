package tor.java.sixteen;

import java.io.File;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import JCommonTools.CC;

/**
 * 
 * @author M.Tor
 * @date	25.02.2014
 *
 */
@XmlRootElement (name = "CSVDefinition")
public class CSVDefinition 
{
	public static final String FILENAME_EXT = "csd";
	
	public String FileName;
	public boolean	IsFirstRowHeader;
	public String Separator;
	public int	MaxRowAtOnce;
	
	//public String TableName;
	//public String SQLCreateTable;
	//public String SQLInsertInto;
	//public boolean IsExecuteCreateTable;
	
	public int		SICode;
	public String	SIName;
	public int		SIYear; 
	public int		SIMonth; 
	public int		SIDay;
	public String	SIRegion;
	public int		SITypeR;
	
	public String	SINegDesc;
	public String	SI3fDesc;
	public String	SIOptDesc;
	
    @XmlElementWrapper (name = "Fields")
    @XmlElement (name = "Field")
    public ArrayList<CSVColumnDef> Fields;

    @XmlElementWrapper (name = "ArrColCorr")
    @XmlElement (name = "ColCorr")
	public ArrayList<CSVColCorr> 		ArrColCorr;
    
    public RecordIOpt RecIOpt;
    
    public CSVDefinition()
    {
    	FileName = CC.STR_EMPTY;
    	IsFirstRowHeader = false;
    	Separator = null;
    	MaxRowAtOnce = Integer.MAX_VALUE;
    	//TableName = null;
    	//SQLCreateTable = null;
    	//SQLInsertInto = null;
    	Fields = new ArrayList<CSVColumnDef>();
    	ArrColCorr = new ArrayList<CSVColCorr>();
		RecIOpt = new RecordIOpt("", false);
    }
    
    public void initFields(String [] aCols)
    {
    	Fields.clear();
    	for (int ii = 0; ii < aCols.length; ii++)
    	{
    		String name = aCols[ii].toLowerCase();
    		String type = CSVColumnDef.TYPE_DEFUALT;
    		if (name.indexOf("datе") != -1 || name.indexOf("дата") != -1)
    			type = "datetime";
    		/// may tested integer in future
    		
    		Fields.add(new CSVColumnDef(CC.STR_EMPTY+(ii+1), aCols[ii], type));
    	}
    }
    
	public static CSVDefinition Load(String aFN)
	{
		CSVDefinition ret = null;
		
		if (aFN != null && aFN.length() > 0)
		{
	    	try
	    	{
	    		JAXBContext context = JAXBContext.newInstance(CSVDefinition.class);
	    		Unmarshaller um = context.createUnmarshaller();
	    		Object obj = um.unmarshal(new File(aFN));
	    		ret = (CSVDefinition) obj;
	    	}
	    	catch (JAXBException ex)
	    	{
	    		//ex.printStackTrace();
	    	}
		}
		return ret;
	}
	
	public String Save(String aFN)
	{
		String ret = null;
		
		if (aFN != null && aFN.length() > 0)
		{
	    	try
	    	{
	    		JAXBContext context = JAXBContext.newInstance(CSVDefinition.class);
	    		Marshaller m = context.createMarshaller();
	    		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    		m.marshal(this, new File(aFN));
	    	}
	    	catch (JAXBException ex)
	    	{
	    		ret = ex.getMessage();
	    	}
		}
		return ret;
	}
    
	public CSVColumnDef AddNewFiled()
	{
		CSVColumnDef fld = new CSVColumnDef();
		Fields.add(fld);
		return fld;
	}
	
	/**
	 * —читает количество всех полей/колонок.
	 * @param aIsAll
	 * @return возвращает количество всех полей/колонок с учетом 
	 * вложений; 
	 * если aIsAll-ложь, то сами пол€ имеющие вложени€ (состовные пол€) не считаютс€ !!!
	 */
	public int getFieldsCount(boolean aIsAll)
	{
		return getFieldsCount(Fields, aIsAll);	
	}
	public int getFieldsCount(ArrayList<CSVColumnDef> aFields, boolean aIsAll)
	{
		int ret = 0;
		
		for (CSVColumnDef col: aFields)
		{
			if (col.Fields != null && col.Fields.size() > 0)
			{
				ret += getFieldsCount(col.Fields, aIsAll);
				if (aIsAll)
					ret ++;
			}
			else
			{
				ret ++;
			}
		}
		
		return ret;
	}

	public CSVColumnDef getField (int index, boolean aIsAll)
	{
		_ind = index;
		
		/** DEBUG
		CSVColumnDef ret = _getField (Fields);
		int di = index;
		if (ret == null)
			ret = new CSVColumnDef(index, "DEBUG"+di, "---");
		return ret;
		**/
		
		return _getField (Fields, aIsAll);
	}
	
	private int _ind;
	private CSVColumnDef _getField (ArrayList<CSVColumnDef> aFields, boolean aIsAll)
	{
		CSVColumnDef ret = null;
		for (CSVColumnDef col: aFields)
		{
			boolean isHasSubCol = col.Fields != null && col.Fields.size() > 0;
			
			if (aIsAll || !isHasSubCol)
			{
				if (_ind == 0)
				{
					ret = col;
					break;
				}
				--_ind;
			}
			
			if (isHasSubCol)
			{
				ret = _getField(col.Fields, aIsAll);
				if (ret != null)
					break;
			}
		}
		
		return ret;
	}
	
	public String getFieldsAsString(String aDelim)
	{
		return _getFieldsAsString(Fields, aDelim, 0);
	}

	public String getFieldsTypeAsString(String aDelim)
	{
		return _getFieldsAsString(Fields, aDelim, 1);
	}
	
	private String _getFieldsAsString(ArrayList<CSVColumnDef> aFields, String aDelim, int aTypeStr)
	{
		String ret = CC.STR_EMPTY;
		for (CSVColumnDef col: aFields)
		{
			String str = CC.STR_EMPTY; 
			if (col.Fields != null && col.Fields.size() > 0)
			{
				str = _getFieldsAsString(col.Fields, aDelim, aTypeStr);
			}
			else
			{
				switch (aTypeStr)
				{
					case 0:
						str = col.Name;
						break;
					case 1:
						str = col.Name + " " + col.Type;
						break;
					default:
						str = CC.STR_EMPTY;
						break;
				}
			}
			ret += ret.length() > 0 ? aDelim + str : str;
		}
		
		return ret;
	}
	
	
}


