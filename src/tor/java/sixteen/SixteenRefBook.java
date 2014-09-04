package tor.java.sixteen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import JCommonTools.RefBook.*;

public class SixteenRefBook extends RefBook 
{
	private Sixteen _wld;
	private String _lastErrorMessage;
	private rbNode _rbnObjType;
	private rbNode _rbnInfoType;
	private rbNode _rbnBaseName;
	private rbNode _rbnValueType;
	
	public String getLastErrorMessage()
	{
		return _lastErrorMessage;
	}
	
	public rbNode getNode_ObjType()
	{
		return _rbnObjType;
	}
	public rbNode getNode_InfoType()
	{
		return _rbnInfoType;
	}
	public rbNode getNode_BaseName()
	{
		return _rbnBaseName;
	}
	public rbNode getNode_ValueType()
	{
		return _rbnValueType;
	}
	
	public SixteenRefBook(Sixteen aWld)
	{
		_wld = aWld;
		_lastErrorMessage = null;
		_init();
	}

	private void _init()
	{
		mRBNodes = new rbNode(1, null, _wld.getString("RefBook.Root.Name"), _wld.getString("RefBook.Root.Alias"));
		
		_rbnObjType = new rbNode(100, mRBNodes, _wld.getString("RefBook.ObjectType.Name"), _wld.getString("RefBook.ObjectType.Alias"));
		mRBNodes.getNodes().add(_rbnObjType);
		_rbnObjType.getNodes().add(new rbNode(101, _rbnObjType, _wld.getString("RefBook.ObjectType.Person.Name"), _wld.getString("RefBook.ObjectType.Person.Alias")));
		_rbnObjType.getNodes().add(new rbNode(103, _rbnObjType, _wld.getString("RefBook.ObjectType.Org.Name"), _wld.getString("RefBook.ObjectType.Org.Alias")));
		_rbnObjType.getNodes().add(new rbNode(105, _rbnObjType, _wld.getString("RefBook.ObjectType.Auto.Name"), _wld.getString("RefBook.ObjectType.Auto.Alias")));
		
		_rbnInfoType = new rbNode(150, mRBNodes, _wld.getString("RefBook.InfoType.Name"), _wld.getString("RefBook.InfoType.Alias"));
		mRBNodes.getNodes().add(_rbnInfoType);
		_rbnInfoType.getNodes().add(new rbNode(155, _rbnInfoType, _wld.getString("RefBook.InfoType.Negative.Name"), _wld.getString("RefBook.InfoType.Negative.Alias")));
		//rbnInfoType.getNodes().add(new rbNode(153, rbnObjType, _wld.getString("RefBook.InfoType.2.Name"), _wld.getString("RefBook.InfoType.2.Alias")));
		//rbnInfoType.getNodes().add(new rbNode(155, rbnObjType, _wld.getString("RefBook.InfoType.3.Name"), _wld.getString("RefBook.InfoType.3.Alias")));
		
		_rbnValueType = new rbNode(200, mRBNodes, _wld.getString("RefBook.ValueType.Name"), _wld.getString("RefBook.ValueType.Alias"));
		mRBNodes.getNodes().add(_rbnValueType);
		_rbnValueType.getNodes().add(new rbNode(202, _rbnValueType, _wld.getString("RefBook.ValueType.String.Name"), _wld.getString("RefBook.ValueType.String.Alias")));
		_rbnValueType.getNodes().add(new rbNode(204, _rbnValueType, _wld.getString("RefBook.ValueType.Number.Name"), _wld.getString("RefBook.ValueType.Number.Alias")));
		_rbnValueType.getNodes().add(new rbNode(206, _rbnValueType, _wld.getString("RefBook.ValueType.Date.Name"), _wld.getString("RefBook.ValueType.Date.Alias")));
		
		_rbnBaseName = new rbNode(300, mRBNodes, _wld.getString("RefBook.BaseName.Name"), _wld.getString("RefBook.BaseName.Alias"));
		mRBNodes.getNodes().add(_rbnBaseName);
		_rbnBaseName.getNodes().add(new rbNode(302, _rbnBaseName, _wld.getString("RefBook.BaseName.LastName.Name")));
		_rbnBaseName.getNodes().add(new rbNode(304, _rbnBaseName, _wld.getString("RefBook.BaseName.FirstName.Name")));
		_rbnBaseName.getNodes().add(new rbNode(306, _rbnBaseName, _wld.getString("RefBook.BaseName.PatronymicName.Name")));

		//mRBNodes.getNodes().add(new ceNode(2, _wld.getString(""), _wld.getString("")));
	}

	public boolean LoadFromDB(Connection aConn)
	{
		mRBNodes = new rbNode(1, null, _wld.getString("RefBook.Root.Name"), _wld.getString("RefBook.Root.Alias"));
		return LoadFromDB(aConn, mRBNodes);
	}
	
	public boolean LoadFromDB(Connection aConn, rbNode aNodeOwner)
	{
		boolean ret = true;
		Statement stmt = null;
		try
		{
			stmt = aConn.createStatement();
			String st =String.format(
				_wld.getSQL("RefBook.Load.ByOwner")
				, aNodeOwner.getId()
			);
			ResultSet rs = stmt.executeQuery(st);
			while (rs.next())
			{
				rbNode node = new rbNode(rs.getInt(1), aNodeOwner, rs.getString(3), rs.getString(4));
				aNodeOwner.getNodes().add(node);
				if (!LoadFromDB(aConn, node))
				{
					ret = false;
					break;
				}
			}
		}
		catch (SQLException ex)
		{
			_lastErrorMessage = ex.getMessage();
			ret = false;
		}
		
		return ret;
	}

	public boolean SaveToDB(Connection aConn)
	{
		boolean ret = true;
		Statement stmt = null;
		try
		{
			aConn.setAutoCommit(false);
			stmt = aConn.createStatement();
			stmt.executeUpdate(_wld.getSQL("RefBook.Delete.All"));

			PreparedStatement ps = null;
			ps = aConn.prepareStatement(_wld.getSQL("RefBook.Insert"));
			
			ret = SaveToDB(ps, mRBNodes);
		}
		catch (SQLException ex)
		{
			_lastErrorMessage = ex.getMessage();
			ret = false;
			try
			{
				aConn.rollback();
			}
			catch (SQLException ex2)
			{
				//_lastErrorMessage = ex2.getMessage();
			}
		}
		finally
		{
			try
			{
				aConn.commit();
			}
			catch (SQLException ex)
			{
				_lastErrorMessage = ex.getMessage();
				ret = false;
			}
		}
		
		
		return ret;
		
	}
	
	public boolean SaveToDB(PreparedStatement aPS, rbNode aNodeOwner)
	{
		boolean ret = true;
		try
		{
			aPS.setInt(1, aNodeOwner.getId());
			if (aNodeOwner.getParent() != null )
				aPS.setInt(2, ((rbNode)aNodeOwner.getParent()).getId());
			else
				aPS.setInt(2, 0);
			aPS.setString(3, RefBook.getPath(aNodeOwner));
			aPS.setString(4, aNodeOwner.getName());
			aPS.setString(5, aNodeOwner.getAlias());
			aPS.executeUpdate();
			for (rbNode node : aNodeOwner.getNodes())
			{
				SaveToDB(aPS, node);
			}
			
		}
		catch (SQLException ex)
		{
			_lastErrorMessage = ex.getMessage();
			ret = false;
		}
		
		return ret;
		
	}
}
