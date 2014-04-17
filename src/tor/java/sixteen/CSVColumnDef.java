package tor.java.sixteen;

import java.util.ArrayList;

import JCommonTools.CC;

public class CSVColumnDef
{
	public final static String	 TYPE_DEFUALT = "varchar(50)";
	
	public String	Number;

	public String 	Name;
	public String 	Type;
	// this is may define if the type, as in [varchar(10)]
	//public int 		Length; 
	public boolean	IsPrimaryKey;
	public String 	Template;
	
	public int 		ColWidth;
	
	public String						Separator;
	public ArrayList<CSVColumnDef>		Fields;
	
	public CSVColumnDef()
	{
		this ("1", null, null);
	}
	
	public CSVColumnDef(String aNum, String aName, String aType)
	{
		Number = aNum;
		Name = aName;
		Type = aType;
		
		Separator = null;
		Fields = null; 
	}
	
    public void setSeparator (String aSep)
    {
    	if (Fields == null)
    		Fields = new ArrayList<CSVColumnDef>();
    	else
    		Fields.clear();

    	Separator = aSep;
    	
    	if (aSep.length() > 0)
    	{
	    	String [] cols = Name.split("\\" + aSep, -1);
	    	for (int ii = 0; ii < cols.length; ii++)
	    	{
	    		String name = cols[ii].toLowerCase();
	    		String type = CSVColumnDef.TYPE_DEFUALT;
	    		if (name.indexOf("datå") != -1 || name.indexOf("äàòà") != -1)
	    			type = "datetime";
	    		/// may tested integer in future
	    		
	    		Fields.add(new CSVColumnDef(Number+"."+(ii+1), cols[ii], type));
	    	}
    	}
    }
    

}
