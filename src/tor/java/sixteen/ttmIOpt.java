package tor.java.sixteen;

import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

import JCommonTools.CC;
import JCommonTools.JTreeTable.AbstractTreeTableModel;
import JCommonTools.JTreeTable.TreeTableModel;
import JCommonTools.RefBook.rbNode;

public class ttmIOpt extends AbstractTreeTableModel 
{
	static protected String[] cNames; // = {"Column 1", "Column 2",  "Column 3", "Column 4"}; 
	static protected Class[]  cTypes = { TreeTableModel.class, String.class, String.class, String.class }; 

	private DefaultComboBoxModel<rbNode> _modCboBase;
	private DefaultComboBoxModel<rbNode> _modCboType;
	
	public void setRecordIOpt(RecordIOpt aRecIOpt)
	{
		root = aRecIOpt;
	}

	public ttmIOpt(RecordIOpt aRecIOpt, Sixteen aWld, DefaultComboBoxModel<rbNode> aMC, DefaultComboBoxModel<rbNode> aMCType)
	{
		super (aRecIOpt);
		_modCboBase = aMC;
		_modCboType = aMCType;
		cNames = new String[4];
		cNames[0] = aWld.getString("Table.Column.Name.IOpt.DBColName");
		cNames[1] = aWld.getString("Table.Column.Name.IOpt.CSVColName");
		cNames[2] = aWld.getString("Table.Column.Name.IOpt.BaseColName");
		cNames[3] = aWld.getString("Table.Column.Name.IOpt.ColType");
	}
	
	@Override
	public int getColumnCount() 
	{
	    return cNames.length; 
	}

	@Override
	public boolean isLeaf(Object node) 
	{
	    return ((RecordIOpt) node).isLeaf(); 
	}
	
	@Override
	public String getColumnName(int column) 
	{
	    return cNames[column]; 
	}

	@Override
	public Class getColumnClass(int column) 
	{
	    return cTypes[column]; 
	}
	
	@Override
	public Object getValueAt(Object node, int column) 
	{
		RecordIOpt rec = (RecordIOpt) node; 
	    switch(column) { 
	       case 0: 
	          return rec; 
	       case 1: 
	          return rec.getName(); 
	       case 2:
	          return _findRBNodeByID(_modCboBase, rec.get_colBaseR()); 
	       case 3: 
	          return _findRBNodeByID(_modCboType, rec.get_colTypeR()); 
	    } 
	   
	    return null;  
	}

	@Override
	public Object getChild(Object parent, int index) 
	{
	    return ((RecordIOpt) parent).getChildren().get(index);// .elementAt(index); 
	}

	@Override
	public int getChildCount(Object parent) 
	{
		if (!((RecordIOpt) parent).isLeaf()) 
		{ 
		    return ((RecordIOpt) parent).getChildren().size(); 
		} 
		return 0; 
	}

	@Override
	public boolean isCellEditable(Object node, int column) 
	{
		boolean ret = super.isCellEditable(node, column);
		if (column != 1)
			ret = true;
		return ret;
	}
	
	@Override
	public void setValueAt(Object aValue, Object node, int column) 
	{
	    switch(column) { 
	       case 0: 
	       {
				RecordIOpt rec = (RecordIOpt) node;
				rec.setNameDB(aValue.toString());
	       }
	       case 1:
				super.setValueAt(aValue, node, column);
	       case 2:
	       {
				RecordIOpt rec = (RecordIOpt) node; 
				rec.set_colBaseR(_findRBNodeByName(_modCboBase, aValue.toString()));
	       }
	       case 3:
	       {
				RecordIOpt rec = (RecordIOpt) node; 
				rec.set_colTypeR(_findRBNodeByName(_modCboType, aValue.toString()));
	       }
	       default: 
				super.setValueAt(aValue, node, column);
	    } 
	}
	
	private String _findRBNodeByID(DefaultComboBoxModel<rbNode> aModCbo, int aID)
	{
		String ret = CC.STR_EMPTY;
		for (int ii = 0; ii < aModCbo.getSize(); ii++)
			if (aID == aModCbo.getElementAt(ii).getId())
				ret = aModCbo.getElementAt(ii).getName();
		return ret;
	}

	private int _findRBNodeByName(DefaultComboBoxModel<rbNode> aModCbo, String aName)
	{
		int ret = 0;
		for (int ii = 0; ii < aModCbo.getSize(); ii++)
			if (aName.equals(aModCbo.getElementAt(ii).getName()))
				ret = aModCbo.getElementAt(ii).getId();
		return ret;
	}

}
