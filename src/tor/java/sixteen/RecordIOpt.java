package tor.java.sixteen;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class RecordIOpt 
{
	private int _code;
	private String _nameDBCol;
	private String _nameCSVCol;
	private int _colBaseR;
	private int _colTypeR;
	private boolean _isLeaf; 
	
	private ArrayList<RecordIOpt> _children; 

    public RecordIOpt() 
    {
    	this ("#", false);
    }
    
    public RecordIOpt(String name, boolean isLeaf) 
    {
    	this._code = 0;
    	this._nameCSVCol = new String(name); 
    	this._nameDBCol =  new String(name);
    	this._colTypeR = 0;
    	this._colBaseR = 0;
    	this._isLeaf = isLeaf;
    	this._children = new ArrayList<RecordIOpt>();
    }

    public int getCode() 
    {
		return _code;
	}
	public void setCode(int _code) 
	{
		this._code = _code;
	}

	public ArrayList<RecordIOpt> getChildren() 
    { 
       return _children; 
    } 
	public void setChildren(ArrayList<RecordIOpt> aChildren) 
    { 
       _children = aChildren; 
    } 
 
    public boolean isLeaf() 
    { 
       return _isLeaf; 
    } 
    public void setLeaf(boolean aLeaf) 
    { 
       _isLeaf = aLeaf; 
    } 
 
    public String getName() 
    { 
       return _nameCSVCol; 
    }
    public void setName(String aName)
    {
    	_nameCSVCol = aName;
    }

    public String getNameDB() 
    { 
       return _nameDBCol; 
    }
    public void setNameDB(String aName)
    {
    	_nameDBCol = aName;
    }

	public int get_colBaseR() 
	{
		return _colBaseR;
	}

	public void set_colBaseR(int _colBaseR) 
	{
		this._colBaseR = _colBaseR;
	}

	public int get_colTypeR() 
	{
		return _colTypeR;
	}

	public void set_colTypeR(int _colTypeR) 
	{
		this._colTypeR = _colTypeR;
	}

	@Override
    public String toString() 
    { 
       return _nameDBCol; 
    } 
	
}
