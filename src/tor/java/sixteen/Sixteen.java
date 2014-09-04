package tor.java.sixteen;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import JCommonTools.AsRegister;
import JCommonTools.DB.DBWork;

public class Sixteen 
{
	public final static String FN_RESOURCE_TEXT = "sixteen-text";
	public final static String FN_RESOURCE_SQL = "sixteen-sql";
	
	public final static String FD_RESOURCE_ICONS = "img/";

	public final static String PREFERENCE_PATH = "/sixteen";

	public final static String FN_RESOURCE_TEXT_CT = "JCommonTools.Rsc/JCTText";
	
	public final static String COLUMN_NAME_ROW_HASH = "id_row_hash";

	public final static String DEF_TGT_CATALOG = "depot";
	public final static String DEF_TGT_SCHEMA = "src";
	public final static String DEF_TGT_TABLE = "ival";

	
	public final static String FUNCTION_SRC_CODE = "SRC_CODE";
	public final static String FUNCTION_NEXT_CODE = "NEXT_CODE";
	public final static String FUNCTION_TO_DATE = "TO_DATE";
	public final static String FUNCTION_UNION = "UNION";

	public final static int DISPLAYEACHNNROW = 500;
	
	private ResourceBundle _bnd;
	private ResourceBundle _bndSQL;
	private ResourceBundle _bndCT;
	private DBWork _wdb;
	private AsRegister _reg;

	public ResourceBundle get_bnd() {
		return _bnd;
	}
	public ResourceBundle get_bndSQL() {
		return _bndSQL;
	}

	public String getString(String aKey)
	{
		return _bnd.getString(aKey);
	}
	public String getSQL(String aKey)
	{
		return _bndSQL.getString(aKey);
	}
	public  ImageIcon getImageIcon(String aName)
	{
		URL url = this.getClass().getClassLoader().getResource(FD_RESOURCE_ICONS+aName);
		if (url ==null)
			url = this.getClass().getResource(FD_RESOURCE_ICONS+aName);
		
		if (url != null)
		{
			ImageIcon ico = new ImageIcon(url);
			return new ImageIcon(ico.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH));
		}
		else
		{
			return new ImageIcon();
		}
	}

	public  Image getImage(String aName)
	{
		URL url = this.getClass().getClassLoader().getResource(FD_RESOURCE_ICONS+aName);
		if (url != null)
		{
			return Toolkit.getDefaultToolkit().getImage(url);
		}
		else
		{
			return new ImageIcon().getImage();
		}
		
		//return ImageTools.CreateIcon(aName, 24).getImage();
	}

	public String getStringCT(String aKey)
	{
		return _bndCT.getString(aKey);
	}
	
	public DBWork get_wdb() 
	{
		return _wdb;
	}
	public void set_wdb(DBWork _wdb) 
	{
		this._wdb = _wdb;
	}

	public AsRegister get_reg() 
	{
		return _reg;
	}
	

	public Sixteen()
	{
		_bnd = ResourceBundle.getBundle(FN_RESOURCE_TEXT);
		_bndSQL = ResourceBundle.getBundle(FN_RESOURCE_SQL);
		_bndCT = ResourceBundle.getBundle(FN_RESOURCE_TEXT_CT);
		_wdb =new DBWork();
		_reg = new AsRegister();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		fCSV prg = new fCSV(new Sixteen());
		prg.setVisible(true);
	}

}
