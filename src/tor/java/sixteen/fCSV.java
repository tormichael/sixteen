package tor.java.sixteen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.concurrent.ExecutionException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import JCommonTools.AsRegister;
import JCommonTools.CC;
import JCommonTools.CodeText;
import JCommonTools.GBC;
import JCommonTools.TableTools;
import JCommonTools.DB.dDBConnection;
import JCommonTools.JTreeTable.JTreeTable;
import JCommonTools.RefBook.fRefBook;
import JCommonTools.RefBook.rbNode;

public class fCSV extends JFrame 
{
	private CSVData _csv;
	private Sixteen _wld;
	private String _currCSVDefFN;
	private SixteenRefBook _srb;

	private JTabbedPane 		_tp;
	private JTabbedPane 		_tpTabCo;
	private JTextField			_txtCSVFileName;
	private JFormattedTextField	_txtGoToRow;
	private JTextField			_txtCharSeparator;
	private JTextField			_txtDBMCondition;
	private JTextField			_txtISCode;
	private JTextField			_txtISName;
	private JSpinner			_spnISDateDay;
	private JSpinner			_spnISDateMonth;
	private JSpinner			_spnISDateYear;
	private JTextField			_txtISRegion;
	//private JTextField			_txtISNegDesc;
	//private JTextField			_txtIS3FDesc;
	//private JTextField			_txtISOptDesc;
	private JFormattedTextField	_txtMaxRowAsOnce;
	private JCheckBox 			_chkFirstRowHeader;
	private JComboBox<rbNode> 	_cboISType;
	private JComboBox<CodeText> _cboDBMCommand;
	private JButton				_cmdGoToRow;
	private JButton				_cmdReloadData;
	private JButton				_cmdGeneratColumns;
	private JButton				_cmdDBMExecute;
	private JToggleButton 		_cmdImportExecut;
	private JButton				_cmdImportInterrupt;
	private tmCSVFields 		_tmFields;
	private tmColCorr			_tmColCorr;
	private ttmIOpt				_ttmIOpt;
	private JTable				_tabData;
	private JTable				_tabFields;
	private JTable				_tabColCorr;
	private JTable				_tabDBMResult;
	private JTreeTable			_ttIOpt;
	private JComboBox<rbNode> 	_cboIOptBase;
	private JComboBox<rbNode> 	_cboIOptType;
	private JLabel				_lblDBMCondition;
	private JLabel				_lblRPR;
	private JTree				_treeDB;
	private TreeModel 			_trmDBMetaData;
	
	private JTextArea _txtStatus;

	private JScrollPane _pnlDBMResult;
	private JSplitPane _splVPanel;
	private JSplitPane _splvDBMan;

	private Thread _currThread;
	private ExecImportV2 _exeImp;
	
	private void info(String aText)
	{
		String result =  _txtStatus.getText() + aText;
		if (_txtStatus != null)
		{
			_txtStatus.setText(result);
			_txtStatus.setSelectionStart(result.length());
		}
	}
	private void infoNewLine(String aText)
	{
		if (aText == null)
			return;
		
		String result =  _txtStatus.getText()  + CC.NEW_LINE + aText;
		if (_txtStatus != null)
		{
			_txtStatus.setText(result);
			_txtStatus.setSelectionStart(result.length());
		}
	}

	public fCSV(Sixteen aWld)
	{
		_wld = aWld;
		_csv = new CSVData(aWld);
		_srb = new SixteenRefBook(_wld);
		
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle(_wld.getString("Titles.CSVTools"));
		this.setIconImage(_wld.getImage("CSV02a.png"));

		JToolBar bar = new JToolBar();
		actLoadCSVDef.putValue(Action.SMALL_ICON, _wld.getImageIcon("open.png"));
		bar.add(actLoadCSVDef);
		actSaveCSVDef.putValue(Action.SMALL_ICON, _wld.getImageIcon("save.png"));
		bar.add(actSaveCSVDef);
		actSaveAsCSVDef.putValue(Action.SMALL_ICON, _wld.getImageIcon("save-as.png"));
		bar.add(actSaveAsCSVDef);
		add(bar, BorderLayout.NORTH);
		actSetDBConnection.putValue(Action.SMALL_ICON, _wld.getImageIcon("dbconnection.png"));
		bar.add(actSetDBConnection);
		bar.addSeparator();
		actToolsRefBook.putValue(Action.SMALL_ICON, _wld.getImageIcon("refbook.png"));
		bar.add(actToolsRefBook);
		
		
		_tp = new JTabbedPane();
		//
		// TAB CSV DATA:
		//
		GridBagLayout gblData = new GridBagLayout();
		JPanel pnlData = new JPanel(gblData);
		//pnlData.setBorder(BorderFactory.createTitledBorder(_wld.getString("TitledBorder.fCSV.CSVFile")));
		/// first row
		JLabel lblBnkDescr = new JLabel(_wld.getString("Label.fCSV.File"));
		gblData.setConstraints(lblBnkDescr, new GBC(0,0).setIns(2).setAnchor(GBC.EAST));
		pnlData.add(lblBnkDescr);
		_txtCSVFileName = new JTextField();
		gblData.setConstraints(_txtCSVFileName, new GBC(1, 0).setIns(2).setGridSpan(3, 1).setFill(GBC.HORIZONTAL).setIns(2).setAnchor(GBC.WEST).setWeight(1.0, 0.0));
		pnlData.add(_txtCSVFileName);
		JButton _cmdSelectFile = new JButton(actSelectFile);
		_cmdSelectFile.setText("...");
		gblData.setConstraints(_cmdSelectFile, new GBC(4, 0).setIns(2).setAnchor(GBC.WEST));
		pnlData.add(_cmdSelectFile);
		/// second row
		_lblRPR = new JLabel(String.format(_wld.getString("Label.fCSV.RPR"), "0"));
		gblData.setConstraints(_lblRPR, new GBC(0,1).setIns(2).setAnchor(GBC.WEST));
		pnlData.add(_lblRPR);
		_txtGoToRow = new JFormattedTextField(NumberFormat.getIntegerInstance());
		//_txtGoToRow.setMinimumSize(new Dimension(50, (int) _txtGoToRow.getMinimumSize().getHeight()));
		//_txtGoToRow.setSize(new Dimension((int) _txtGoToRow.getMinimumSize().getWidth(), (int) _txtGoToRow.getMinimumSize().getHeight()));
		_txtGoToRow.setText("0");
		gblData.setConstraints(_txtGoToRow, new GBC(1,1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(0.1, 0.0));
		pnlData.add(_txtGoToRow);
		_cmdGoToRow = new JButton(actGoToRow);
		_cmdGoToRow.setText(_wld.getString("Button.fCSV.GoToRow"));
		gblData.setConstraints(_cmdGoToRow, new GBC(2,1).setIns(2).setAnchor(GBC.EAST));
		pnlData.add(_cmdGoToRow);
		JLabel lblBanksTab = new JLabel(_wld.getString("Label.fCSV.CSVData"));
		gblData.setConstraints(lblBanksTab, new GBC(3,1).setIns(2).setWeight(1.0, 0.0).setAnchor(GBC.CENTER));
		pnlData.add(lblBanksTab);
		_cmdReloadData = new JButton(actReloadData);
		_cmdReloadData.setText(_wld.getString("Button.fCSV.RefreshTable"));
		gblData.setConstraints(_cmdReloadData, new GBC(4,1).setIns(2).setAnchor(GBC.WEST));
		pnlData.add(_cmdReloadData);
		// third row
		//_tmData = new tmCSVData();
		_tabData = new JTable();
		JScrollPane scrollTabRes = new JScrollPane(_tabData); 
		gblData.setConstraints(scrollTabRes, new GBC(0,2).setGridSpan(5, 1).setIns(2).setFill(GBC.BOTH).setWeight(1.0, 1.0));
		pnlData.add(scrollTabRes);
		
		_tp.addTab(_wld.getString("TitledBorder.fCSV.CSVFile"), pnlData);
		
		//
		// TAB CSV PARAM:
		//
		GridBagLayout gblRight = new GridBagLayout();
		JPanel pnlRight = new JPanel(gblRight);
		//pnlRight.setBorder(BorderFactory.createTitledBorder(_wld.getString("TitledBorder.fCSV.CSVProperties")));
		//
		_txtMaxRowAsOnce = new JFormattedTextField(NumberFormat.getIntegerInstance());
		//_txtMaxRowAsOnce.setMinimumSize(new Dimension(100, (int) _txtMaxRowAsOnce.getMinimumSize().getHeight()));
		gblRight.setConstraints(_txtMaxRowAsOnce, new GBC(0,0).setIns(2).setAnchor(GBC.WEST).setFill(GBC.HORIZONTAL).setWeight(0.07, 0.0));
		pnlRight.add(_txtMaxRowAsOnce);
		JLabel lblMaxRowsAtOnce = new JLabel(_wld.getString("Label.fCSV.MaxRowsAtOnce"));
		gblRight.setConstraints(lblMaxRowsAtOnce, new GBC(1,0).setIns(2).setAnchor(GBC.EAST));
		pnlRight.add(lblMaxRowsAtOnce);
		//
		_chkFirstRowHeader = new JCheckBox(_wld.getString("CheckBox.fCSV.FirstRowHeader"));
		gblRight.setConstraints(_chkFirstRowHeader, new GBC(0,1).setGridSpan(2, 1).setIns(2).setFill(GBC.HORIZONTAL));
		pnlRight.add(_chkFirstRowHeader);
		//
		_txtCharSeparator = new JTextField(1);
		//_txtCharSeparator.setMinimumSize(new Dimension(_txtCharSeparator.getFont().getSize(), _txtCharSeparator.getHeight()));
		_txtCharSeparator.setMinimumSize(new Dimension(20, (int) _txtCharSeparator.getMinimumSize().getHeight()));
		gblRight.setConstraints(_txtCharSeparator, new GBC(0,2).setIns(2).setAnchor(GBC.EAST));
		pnlRight.add(_txtCharSeparator);
		JLabel lblCharSep = new JLabel(_wld.getString("Label.fCSV.Separator"));
		gblRight.setConstraints(lblCharSep, new GBC(1,2).setIns(2).setAnchor(GBC.WEST));
		pnlRight.add(lblCharSep);
		//
		JLabel lbl�olTab = new JLabel(_wld.getString("Label.fCSV.ColumnsTab"));
		gblRight.setConstraints(lbl�olTab, new GBC(0,3).setAnchor(GBC.EAST).setIns(2));
		pnlRight.add(lbl�olTab);
		_cmdGeneratColumns = new JButton(actGeneratColumns);
		_cmdGeneratColumns.setText(_wld.getString("Button.fCSV.GeneratColumn"));
		gblRight.setConstraints(_cmdGeneratColumns, new GBC(1,3).setIns(2).setAnchor(GBC.EAST));
		pnlRight.add(_cmdGeneratColumns);
		//
		_tmFields = new tmCSVFields(_csv);
		_tabFields = new JTable(_tmFields); 
		JScrollPane scrollTabResFld = new JScrollPane(_tabFields); 
		gblRight.setConstraints(scrollTabResFld, new GBC(0,4).setGridSpan(2, 1).setIns(2).setFill(GBC.BOTH).setWeight(1.0, 1.0));
		pnlRight.add(scrollTabResFld);

		_tp.addTab(_wld.getString("TitledBorder.fCSV.CSVProperties"), pnlRight);

		
		//
		//  TAB DB manager:
		//
		_treeDB = new JTree();
		_trmDBMetaData = null;
		//_trmDBMetaData = new DefaultTreeModel(new DefaultMutableTreeNode(_wld.getString("Tree.fOrbit.DBStructure.InitTreeNode")));
		//_treeDB.setModel(_trmDBMetaData);

		GridBagLayout gblDBMan = new GridBagLayout();
		JPanel pnlDBMan = new JPanel(gblDBMan);
			JLabel lbl = new JLabel(_wld.getString("Label.fCSV.DBM.Command"));
			gblDBMan.setConstraints(lbl, new GBC(0,0).setIns(2).setAnchor(GBC.WEST));
			pnlDBMan.add(lbl);
			_cboDBMCommand = new JComboBox<CodeText>();
			gblDBMan.setConstraints(_cboDBMCommand, new GBC(1,0).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlDBMan.add(_cboDBMCommand);
			_cmdDBMExecute = new JButton(actDBMExecute);
			_cmdDBMExecute.setText(_wld.getString("Button.fCSV.DBM.Execute"));
			gblDBMan.setConstraints(_cmdDBMExecute, new GBC(2,0).setIns(2));
			pnlDBMan.add(_cmdDBMExecute);
			_lblDBMCondition = new JLabel(_wld.getString("Label.fCSV.DBM.Condition"));
			gblDBMan.setConstraints(_lblDBMCondition, new GBC(0,1).setIns(2).setAnchor(GBC.WEST));
			pnlDBMan.add(_lblDBMCondition);
			_txtDBMCondition = new JTextField();
			gblDBMan.setConstraints(_txtDBMCondition, new GBC(1,1).setGridSpan(2, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlDBMan.add(_txtDBMCondition);
			_tabDBMResult = new JTable();
			_pnlDBMResult = new JScrollPane(_tabDBMResult);
			_pnlDBMResult.setBorder(BorderFactory.createTitledBorder(_wld.getString("TitledBorder.fCSV.DBM.Result")));
			gblDBMan.setConstraints(_pnlDBMResult, new GBC(0,2).setGridSpan(3, 1).setIns(2).setFill(GBC.BOTH).setWeight(1.0, 1.0));
			pnlDBMan.add(_pnlDBMResult);
		_splvDBMan = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, _treeDB, pnlDBMan);
		_tp.addTab(_wld.getString("TabbedPane.fCSV.DBMan"), _splvDBMan);
		
		//
		//  TAB Column conformity:
		//
		JPanel pnlCommands = new JPanel(new BorderLayout());
		JPanel pnlCommands2 = new JPanel(new BorderLayout());
		_cmdImportExecut = new JToggleButton(actImportData);
		_cmdImportExecut.setText(_wld.getString("Button.fCSV.ImportData"));
		pnlCommands2.add(_cmdImportExecut, BorderLayout.EAST);
		_cmdImportInterrupt = new JButton(actImportDataInterrupt);
		_cmdImportInterrupt.setText(_wld.getString("Button.fCSV.ImportData.Interrupt"));
		pnlCommands2.add(_cmdImportInterrupt, BorderLayout.CENTER);
		_cmdImportInterrupt.setVisible(false);
		pnlCommands.add(pnlCommands2, BorderLayout.EAST);
		JButton cmdInitColCorrTable = new JButton(actInitColCorrTable);
		cmdInitColCorrTable.setText(_wld.getString("Button.fCSV.InitColCorrTable"));
		pnlCommands.add(cmdInitColCorrTable, BorderLayout.WEST);
		pnlCommands.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

		GridBagLayout gblIS = new GridBagLayout();
		JPanel pnlIS = new JPanel(gblIS);
		pnlIS.setBorder(BorderFactory.createTitledBorder(_wld.getString("TitledBorder.fCSV.InfoSource")));
			lbl = new JLabel(_wld.getString("Label.fCSV.ISName"));
			gblIS.setConstraints(lbl, new GBC(0,0).setIns(2).setAnchor(GBC.WEST));
			pnlIS.add(lbl);
			_txtISCode = new JTextField();
			gblIS.setConstraints(_txtISCode, new GBC(1,0).setIns(2).setFill(GBC.HORIZONTAL));
			pnlIS.add(_txtISCode);
			lbl = new JLabel(" - ");
			gblIS.setConstraints(lbl, new GBC(2,0).setIns(2).setAnchor(GBC.CENTER));
			pnlIS.add(lbl);
			_txtISName = new JTextField();
			gblIS.setConstraints(_txtISName, new GBC(3,0).setGridSpan(5, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(_txtISName);
			
			lbl = new JLabel(_wld.getString("Label.fCSV.ISDate"));
			gblIS.setConstraints(lbl, new GBC(0,1).setIns(2).setAnchor(GBC.WEST));
			pnlIS.add(lbl);
			_spnISDateDay = new JSpinner(new SpinnerNumberModel(0, 0, 31, 1));
			gblIS.setConstraints(_spnISDateDay, new GBC(1,1).setIns(2).setFill(GBC.HORIZONTAL));
			pnlIS.add(_spnISDateDay);
			lbl = new JLabel("/");
			gblIS.setConstraints(lbl, new GBC(2,1).setIns(2).setAnchor(GBC.CENTER));
			pnlIS.add(lbl);
			_spnISDateMonth = new JSpinner(new SpinnerNumberModel(0, 0, 12, 1));
			gblIS.setConstraints(_spnISDateMonth, new GBC(3,1).setIns(2).setFill(GBC.HORIZONTAL));
			pnlIS.add(_spnISDateMonth);
			lbl = new JLabel("/");
			gblIS.setConstraints(lbl, new GBC(4,1).setIns(2).setAnchor(GBC.CENTER));
			pnlIS.add(lbl);
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			_spnISDateYear = new JSpinner(new SpinnerNumberModel(currentYear, currentYear-100, currentYear+100, 1));
			gblIS.setConstraints(_spnISDateYear, new GBC(5,1).setIns(2).setFill(GBC.HORIZONTAL));
			pnlIS.add(_spnISDateYear);
			lbl = new JLabel(CC.STR_EMPTY);
			gblIS.setConstraints(lbl, new GBC(6,1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(lbl);
			lbl = new JLabel(CC.STR_EMPTY);
			gblIS.setConstraints(lbl, new GBC(7,1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(lbl);

			lbl = new JLabel(_wld.getString("Label.fCSV.ISRegion"));
			gblIS.setConstraints(lbl, new GBC(0,2).setIns(2).setAnchor(GBC.WEST));
			pnlIS.add(lbl);
			_txtISRegion  = new JTextField();
			gblIS.setConstraints(_txtISRegion, new GBC(1,2).setGridSpan(6, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(_txtISRegion);

			lbl = new JLabel(_wld.getString("Label.fCSV.ISType"));
			gblIS.setConstraints(lbl, new GBC(0,3).setIns(2).setAnchor(GBC.WEST));
			pnlIS.add(lbl);
			DefaultComboBoxModel<rbNode> modCboISType = new DefaultComboBoxModel<rbNode>();
			_cboISType  = new JComboBox<rbNode>();
			_cboISType.setModel(modCboISType);
			gblIS.setConstraints(_cboISType, new GBC(1,3).setGridSpan(6, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(_cboISType);

//			lbl = new JLabel(_wld.getString("Label.fCSV.ISNegDisc"));
//			gblIS.setConstraints(lbl, new GBC(0,4).setIns(2).setAnchor(GBC.WEST));
//			pnlIS.add(lbl);
//			_txtISNegDesc  = new JTextField();
//			gblIS.setConstraints(_txtISNegDesc, new GBC(1,4).setGridSpan(7, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
//			pnlIS.add(_txtISNegDesc);
//			
//			lbl = new JLabel(_wld.getString("Label.fCSV.IS3FDisc"));
//			gblIS.setConstraints(lbl, new GBC(0,5).setIns(2).setAnchor(GBC.WEST));
//			pnlIS.add(lbl);
//			_txtIS3FDesc  = new JTextField();
//			gblIS.setConstraints(_txtIS3FDesc, new GBC(1,5).setGridSpan(7, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
//			pnlIS.add(_txtIS3FDesc);
//			
//			lbl = new JLabel(_wld.getString("Label.fCSV.ISOptDisc"));
//			gblIS.setConstraints(lbl, new GBC(0,6).setIns(2).setAnchor(GBC.WEST));
//			pnlIS.add(lbl);
//			_txtISOptDesc  = new JTextField();
//			gblIS.setConstraints(_txtISOptDesc, new GBC(1,6).setGridSpan(7, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
//			pnlIS.add(_txtISOptDesc);
			
		//GridBagLayout gblTCC = new GridBagLayout();
		_tpTabCo =new JTabbedPane();	
		//JPanel pnlTCC = new JPanel(new BorderLayout());
		//pnlTCC.setBorder(BorderFactory.createTitledBorder());
			_tmColCorr = new tmColCorr(_wld, _csv.getCSVDef().ArrColCorr);
			_tabColCorr = new JTable(_tmColCorr);
			JScrollPane scrollTCC = new JScrollPane(_tabColCorr); 
			//gblTCC.setConstraints(scrollTCC, new GBC(0,1).setGridSpan(4, 1).setIns(2).setFill(GBC.BOTH).setWeight(1.0, 1.0));
			//pnlTCC.add(scrollTCC, BorderLayout.CENTER);
		_tpTabCo.addTab(_wld.getString("TitledBorder.fCSV.TabColCon"), scrollTCC);
		//JPanel pnlTIOpt = new JPanel(new BorderLayout());
			DefaultComboBoxModel<rbNode> modCboBase = new DefaultComboBoxModel<rbNode>();
			DefaultComboBoxModel<rbNode> modCboType = new DefaultComboBoxModel<rbNode>();
			_ttmIOpt = new ttmIOpt(_csv.getCSVDef().RecIOpt, _wld, modCboBase, modCboType);
			_ttIOpt = new JTreeTable(_ttmIOpt);
			scrollTCC = new JScrollPane(_ttIOpt);
			_cboIOptBase = new JComboBox<rbNode>();
			_cboIOptBase.setModel(modCboBase);	
			_ttIOpt.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(_cboIOptBase));
			_cboIOptType = new JComboBox<rbNode>();
			_cboIOptType.setModel(modCboType);
			_ttIOpt.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(_cboIOptType));
			_ttIOpt.getTree().setEditable(true);
			
			//pnlTIOpt.add(scrollTCC, BorderLayout.CENTER);
		_tpTabCo.addTab("***", scrollTCC);
		//_splvTabColCon = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlIS, pnlTCC);
		JPanel pnlIS_TCC = new JPanel(new BorderLayout());
		pnlIS_TCC.add(pnlIS, BorderLayout.NORTH);
		pnlIS_TCC.add(_tpTabCo, BorderLayout.CENTER);
		pnlIS_TCC.add(pnlCommands, BorderLayout.SOUTH);
		_tp.addTab(_wld.getString("TabbedPane.fCSV.ColCor"), pnlIS_TCC);
		
		//
		// DOWN PANEL:
		//
		JPanel pnlStatus = new JPanel(new BorderLayout());
		_txtStatus = new JTextArea();
		pnlStatus.add(new JScrollPane(_txtStatus), BorderLayout.CENTER);
		
		//_splHPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlData, pnlRight);
		//_splVPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _splHPanel, pnlStatus);
		_splVPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _tp, pnlStatus);
		add(_splVPanel, BorderLayout.CENTER);

		_tabFields.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				_tmFields.set_currPos(new Point(_tabFields.getSelectedColumn(), _tabFields.getSelectedRow()));
			}
		});
		
		try{_srb.LoadFromDB(_wld.get_wdb().getConn());}
		catch (Exception ex) {}
		
		_initComboModel();
		
		LoadProgramPreference();

		_tabData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_tabFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		_txtMaxRowAsOnce.setValue(new Integer(_csv.getMaxRowAtOnce()));
		_chkFirstRowHeader.setSelected(_csv.IsFirstRowHeader());
		_txtCharSeparator.setText(_csv.getSeparator());
		
		_tabData.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				_txtGoToRow.setText((_tabData.getSelectedRow()+1)+CC.STR_EMPTY);
			}
		});
		
		JPopupMenu pppParamTab = new JPopupMenu();
		JMenuItem mnpPTDelete = new JMenuItem(actCSVShiftRight);
		mnpPTDelete.setText(_wld.getString("PopupMenu.fCSV.tabData.ShiftRight"));
		pppParamTab.add(mnpPTDelete);
		_tabData.setComponentPopupMenu(pppParamTab);

		JPopupMenu pppIOptTab = new JPopupMenu();
		JMenuItem mnpIODelete = new JMenuItem(actIOptDelete);
		mnpIODelete.setText(_wld.getString("PopupMenu.fCSV.tabIOpt.Delete"));
		pppIOptTab.add(mnpIODelete);
		JMenuItem mnpIOUnion = new JMenuItem(actIOptUnoin);
		mnpIOUnion.setText(_wld.getString("PopupMenu.fCSV.tabIOpt.Union"));
		pppIOptTab.add(mnpIOUnion);
		_ttIOpt.setComponentPopupMenu(pppIOptTab);
		
		/**
		 * 
		 */
		_cboDBMCommand.addItem(new CodeText(1, _wld.getString("Text.DBM.Command.Show")));
		_cboDBMCommand.addItem(new CodeText(5, _wld.getString("Text.DBM.Command.Show.Quantity")));
		_cboDBMCommand.addItem(new CodeText(100, _wld.getString("Text.DBM.Command.DeleteRows")));
		_cboDBMCommand.addItem(new CodeText(200, _wld.getString("Text.DBM.Command.CreateTable")));
		_cboDBMCommand.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (((CodeText)_cboDBMCommand.getSelectedItem()).getCode() == 200)
				{
					_lblDBMCondition.setText(_wld.getString("Label.fCSV.DBM.TableName"));
					_pnlDBMResult.setBorder(BorderFactory.createTitledBorder(_wld.getString("TitledBorder.fCSV.DBM.ColDef")));
					_tabDBMResult.setModel(new tmDBMColDef(_wld));
					_tabDBMResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				}
				else
				{
					_lblDBMCondition.setText(_wld.getString("Label.fCSV.DBM.Condition"));
					_pnlDBMResult.setBorder(BorderFactory.createTitledBorder(_wld.getString("TitledBorder.fCSV.DBM.Result")));
					_tabDBMResult.setModel(new tmDBMResult(null));
					_tabDBMResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				}
			}
		});
		
		designTableColCorr();
		
		this.addWindowListener(new WindowAdapter() 
		{
			@Override
			public void windowClosing(WindowEvent e) 
			{
				SaveProgramPreference();
				super.windowClosing(e);
			}
		});
		
		_chkFirstRowHeader.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				_csv.Load(_txtCSVFileName.getText(), _chkFirstRowHeader.isSelected());
			}
		});
		
		_txtCharSeparator.getDocument().addDocumentListener(new DocumentListener() 
		{
			@Override
			public void removeUpdate(DocumentEvent e) 
			{
				_csv.setSeparator(_txtCharSeparator.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) 
			{
				_csv.setSeparator(_txtCharSeparator.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) 
			{
				_csv.setSeparator(_txtCharSeparator.getText());
			}
		});
		
		_tp.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) 
			{
				if(_tp.getTitleAt(_tp.getSelectedIndex()).equals(_wld.getString("TabbedPane.fCSV.DBMan")))
					_showDBManager();
			}
		});

		_exeImp = new ExecImportV2(_wld,  _csv);
		_exeImp.set_extTextComponent(_txtStatus);
		_exeImp.set_actFinshed(actImportDataFinished);
		_exeImp.set_actError(actImportDataError);
		_exeImp.setContinueIfError(true);
		
	}
	
	private void _initComboModel()
	{
		_initComboModel((DefaultComboBoxModel<rbNode>)_cboISType.getModel(), _srb.getNode_InfoType());
		_initComboModel((DefaultComboBoxModel<rbNode>)_cboIOptBase.getModel(), _srb.getNode_BaseName());
		_initComboModel((DefaultComboBoxModel<rbNode>)_cboIOptType.getModel(), _srb.getNode_ValueType());
	}
	
	private void _initComboModel(DefaultComboBoxModel<rbNode> modCbo, rbNode aRBNode)
	{
		modCbo.removeAllElements();
		modCbo.addElement(new rbNode(0, null, CC.STR_EMPTY));
		for (int ii = 0; ii < aRBNode.getChildCount(); ii++)
			modCbo.addElement((rbNode)aRBNode.getChildAt(ii));
	}
	
	
	Action actSetDBConnection = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			dDBConnection dlg = new dDBConnection(_wld.get_wdb());
			dlg.setPreferencesPath(Sixteen.PREFERENCE_PATH);
			dlg.setModal(true);
			dlg.setIconImage(_wld.getImage("dbconnection.png"));
			dlg.setVisible(true);
			if (dlg.isResultOk())
				_showCurrentConnectionURL();
		}
	};

	Action actToolsRefBook = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			fRefBook rb = new fRefBook(_srb);
			rb.setIconImage(_wld.getImageIcon("refbook.png").getImage());
			rb.ActLoad.putValue(Action.SMALL_ICON, _wld.getImageIcon("open.png"));
			rb.ActSave.putValue(Action.SMALL_ICON, _wld.getImageIcon("save.png"));
			//rb.ActLoad.setEnabled(false);
			//rb.ActSave.setEnabled(false);
			rb.ActNew.putValue(Action.SMALL_ICON, _wld.getImageIcon("plus.png"));
			rb.ActEdit.putValue(Action.SMALL_ICON, _wld.getImageIcon("edit.png"));
			rb.ActDelete.putValue(Action.SMALL_ICON, _wld.getImageIcon("trash.png"));
			rb.ActRefresh.putValue(Action.SMALL_ICON, _wld.getImageIcon("shredder.png"));
			rb.setPreferencePath( Sixteen.PREFERENCE_PATH+"/refbook");
			rb.addWindowListener(new WindowAdapter() 
			{
				@Override
				public void windowClosing(WindowEvent e) 
				{
					try
					{
						if (!_srb.SaveToDB(_wld.get_wdb().getConn()))
							fCSV.this.infoNewLine(_srb.getLastErrorMessage());
						else
							fCSV.this.infoNewLine("RefBook saved successfully!!!");
					}
					catch(Exception ex)
					{
						
					}
					super.windowClosing(e);
				}
			});
			rb.setVisible(true);
		}
	};
	
	Action actSelectFile = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{

			JFileChooser fDlg = new JFileChooser();
			//fDlg.setFileFilter(new FileNameExtensionFilter("Session", "sq3", "sq4"));
			if (_txtCSVFileName.getText().length() > 0)
				fDlg.setCurrentDirectory(new File(_txtCSVFileName.getText()));
			
			if (fDlg.showDialog(fCSV.this, _wld.getString("Text.FileChooser.Common")) == JFileChooser.APPROVE_OPTION)
			{
				_txtCSVFileName.setText(fDlg.getSelectedFile().getPath());
				
				int loadedRows = _csv.Load(_txtCSVFileName.getText(), _chkFirstRowHeader.isSelected());
				infoNewLine(_csv.getResult());
				infoNewLine("Loaded " + loadedRows + " rows");
				_lblRPR.setText(String.format(_wld.getString("Label.fCSV.RPR"), loadedRows+CC.STR_EMPTY));
				_txtGoToRow.setText("1");
				_csv.GenerateColumns();
				_tmFields.setCSVDaata(_csv);
				_tabFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				_tabData.setModel(new tmCSVData(_csv));
			}
		}
	};
	
	Action actGoToRow = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			int ri = ((Number)_txtGoToRow.getValue()).intValue();// Integer.parseInt(_txtGoToRow.getText());
			if (ri-- > 0)
			{
				_tabData.setRowSelectionInterval(ri, ri);
				_tabData.scrollRectToVisible(_tabData.getCellRect(ri, 0, true));
			}
		}
	};

	Action actReloadData = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			_tabData.setModel(new tmCSVData(_csv));
		}
	};
	
	Action actGeneratColumns = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			_csv.GenerateColumns();
			//_tabFields.setModel(new tmCSVFields(_csv));
			_tmFields.setCSVDaata(_csv);
			_tabFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
	};

//	Action actGeneratSQLCreate = new AbstractAction() 
//	{
//		@Override
//		public void actionPerformed(ActionEvent e) 
//		{
//			String stm = _csv.getCSVDef().getFieldsTypeAsString(","+CC.NEW_LINE+"\t");
//			stm = String.format(_wld.getSQL("Command.Create.Table"), _txtDBTableName.getText(), stm);
//			_txtSQLCreate.setText(stm);
//		}
//	};
//
//	Action actGeneratSQLInsert = new AbstractAction() 
//	{
//		@Override
//		public void actionPerformed(ActionEvent e) 
//		{
//			String stm = String.format(_wld.getSQL("Command.Insert.Into"), 
//					_txtDBTableName.getText(), 
//					_csv.getCSVDef().getFieldsAsString(", "), 
//					"#?" + _csv.getCSVDef().getFieldsAsString("?#, #?") + "?#");
//			_txtSQLInsert.setText(stm);
//		}
//	};
	
	Action actLoadCSVDef = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			_setCurrentCSVDefinitionFileName();
			_loadCSVData();
		}
	};
	
	Action actCSVShiftRight = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (_tabData.getSelectedRowCount() > 0)
			{
				int [] isr = _tabData.getSelectedRows();
				for (int ii = 0; ii < isr.length; ii++)
				{
					_csv.ShiftRight(isr[ii], _tabData.getSelectedColumn());
				}
				_tabData.setRowSelectionInterval(0, 0);
				_tabData.setRowSelectionInterval(isr[0], isr[0]);
			}
		}
	};
	
	private void _loadCSVData()
	{
		if (_currCSVDefFN!= null && _currCSVDefFN.length() > 0)
		{	
			int loadedRows = _csv.LoadCSVDef(_currCSVDefFN);
			
			infoNewLine(_csv.getResult());
			infoNewLine("Loaded " + loadedRows + " rows");
			_lblRPR.setText(String.format(_wld.getString("Label.fCSV.RPR"), loadedRows+CC.STR_EMPTY));
			_txtGoToRow.setText("1");
			
			if (loadedRows == 0)
				return;
			
			getCSVDefinition();
			
			CSVDefinition csd = _csv.getCSVDef();
			
			_tabData.setModel(new tmCSVData(_csv));
			_tmFields.setCSVDaata(_csv);
			//_tabFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			_tmColCorr = new tmColCorr(_wld, csd.ArrColCorr);
			_tabColCorr.setModel(_tmColCorr);
			
			_ttmIOpt.setRecordIOpt(_csv.getCSVDef().RecIOpt);
			_ttIOpt.updateUI();
			
			for (int ii = 0; ii < _tabFields.getRowCount(); ii++)
				_tabFields.setRowSelectionInterval(ii, ii);
			_tabFields.setRowSelectionInterval(0,0);
	
			for (int ii = 0; ii < csd.getFieldsCount(false) && ii < _tabData.getColumnModel().getColumnCount(); ii++)
				_tabData.getColumnModel().getColumn(ii).setPreferredWidth(csd.getField(ii, false).ColWidth);
		}
	}
	
	Action actSaveCSVDef = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (_currCSVDefFN == null || _currCSVDefFN.length() == 0	)
				_setCurrentCSVDefinitionFileName();
			
			_saveCSVDef();
		}
	};
	
	Action actSaveAsCSVDef = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			_setCurrentCSVDefinitionFileName();
			_saveCSVDef();
		}
	};

	Action actDBMExecute = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			switch (((CodeText)_cboDBMCommand.getSelectedItem()).getCode()) 
			{
			case 1:
				_execCommandShow();
				break;
			case 5:
				_execCommandShowQuantity();
				break;
			case 100:
				_execCommandDeleteRows();
				break;
			case 200:
				_execCommandCreateTable();
				break;
			default:
				break;
			}
				
		}
	};

	Action actInitColCorrTable = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (_tpTabCo.getSelectedIndex() == 0)
			{
				initColCorrTable();
			}
			else if (_tpTabCo.getSelectedIndex() == 1)
			{
				initColIOpt();
			}
			
		}
	};
	
	private void initColCorrTable()
	{
		// first check DB connection !
		if (_wld.get_wdb().IsDBConnectionParamDefined())
		{
			try
			{
				ArrayList<CSVColCorr> arrCC = _csv.getCSVDef().ArrColCorr;
				
				DatabaseMetaData md = _wld.get_wdb().getConn().getMetaData();
				ResultSet rsCol = md.getColumns(Sixteen.DEF_TGT_CATALOG, Sixteen.DEF_TGT_SCHEMA, Sixteen.DEF_TGT_TABLE, null);
				arrCC.clear();
				while (rsCol.next())
				{
					String cType = rsCol.getString("TYPE_NAME");
					int cTypeLen = rsCol.getInt("COLUMN_SIZE");
					if (cTypeLen > 0)
						cType += " [" + cTypeLen+"]";
					arrCC.add(new CSVColCorr(rsCol.getString(4), cType, null, null, null));
				}
				_tmColCorr = new tmColCorr(_wld, arrCC);
				_tabColCorr.setModel(_tmColCorr);
				_tabColCorr.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				
				designTableColCorr();
			}
			catch (SQLException ex)
			{
				infoNewLine("LoadDBMetadataTreeNode() - SQL error: [" + ex.getErrorCode() + "] - "+ex.getMessage());
			}
			catch (Exception ex)
			{
				infoNewLine("LoadDBMetadataTreeNode() - " + ex.getMessage());
			}
		}
		else
		{
			infoNewLine("------------- ");
			actSetDBConnection.actionPerformed(null);
		}
	}
	
	private void initColIOpt()
	{
		_csv.getCSVDef().RecIOpt.getChildren().clear();
		for(int ii = 0; ii < _csv.getColumnCount(); ii++)
		{
			RecordIOpt rec = new RecordIOpt(_csv.getColumnName(ii), true);
			_csv.getCSVDef().RecIOpt.getChildren().add(rec); // .addElement(rec);
		}
		_ttIOpt.updateUI();
		
	}

	Action actIOptDelete = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (_ttIOpt.getSelectedRowCount() >= 1)
			{
				int[] iSelRow = _ttIOpt.getSelectedRows(); 
				RecordIOpt rec = _csv.getCSVDef().RecIOpt;
				for(int ii=iSelRow.length-1; ii >= 0; ii--)
				{
					rec.getChildren().remove((RecordIOpt) _ttmIOpt.getChild(rec, iSelRow[ii]-1));
				}
				_ttIOpt.updateUI();
			}
		}
	};
	
	Action actIOptUnoin = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (_ttIOpt.getSelectedRowCount() > 1)
			{
				final JDialog dlg = new JDialog(fCSV.this);
				dlg.setLayout(new BorderLayout());
				dlg.setSize(400, 165);
				Dimension szScreen = Toolkit.getDefaultToolkit().getScreenSize();
				dlg.setLocation((int)(szScreen.width/2 - dlg.getWidth()/2), (int)(szScreen.height/2-dlg.getHeight()/2));
				
				JPanel pnlT = new JPanel(new BorderLayout());
				pnlT.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
				JLabel lbl = new JLabel(_wld.getString("Label.DlgIOUnion.Name"));
				lbl.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
				pnlT.add(lbl, BorderLayout.WEST);
				final JTextField txt = new JTextField();
				pnlT.add(txt, BorderLayout.CENTER);
				
				JPanel pnlB = new JPanel(); //new FlowLayout());
				pnlB.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
				JButton cmdOk = new JButton();//_wld.getStringCT("Button.Ok"));
				pnlB.add(cmdOk);
				JButton cmdCancel = new JButton();
				pnlB.add(cmdCancel);
				
				dlg.add(pnlT, BorderLayout.CENTER);
				dlg.add(pnlB, BorderLayout.SOUTH);
				
				cmdOk.setAction(new AbstractAction() 
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						doIOptUnoin(_csv.getCSVDef().RecIOpt, txt.getText());
						dlg.setVisible(false);
					}
				});
				cmdOk.setText(_wld.getStringCT("Button.Ok"));
				cmdCancel.setAction(new AbstractAction() 
				{
					@Override
					public void actionPerformed(ActionEvent e) 
					{
						dlg.setVisible(false);
						
					}
				});
				cmdCancel.setText(_wld.getStringCT("Button.Cancel"));
				
				dlg.setVisible(true);
			}
		}
	};
	
	private void doIOptUnoin(RecordIOpt aRecParent, String aName)
	{
		RecordIOpt rs = null;
		RecordIOpt newRecParent = new RecordIOpt(aName, false);
		int[] iSelRow = _ttIOpt.getSelectedRows(); 
		for(int ii= 0; ii < iSelRow.length; ii++ )
		{
			rs = (RecordIOpt) _ttmIOpt.getChild(aRecParent, iSelRow[ii]-1);
			newRecParent.getChildren().add(rs); // .addElement(rs);
//			for(RecordIOpt rec : aRecParent.getChildren())
//			{
//			}
		}
		for(int ii=iSelRow.length-1; ii >= 0; ii--)
		{
			rs = (RecordIOpt) _ttmIOpt.getChild(aRecParent, iSelRow[ii]-1);
			aRecParent.getChildren().remove(rs); //.removeElement(rs);
		}
		aRecParent.getChildren().add(newRecParent);//.addElement(newRecParent);
		_ttIOpt.updateUI();
	}
	
	Action actImportData = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			
			setCSVDefinition();
			
			if (_cmdImportExecut.isSelected())
			{
				_cmdImportExecut.setText(_wld.getString("Button.fCSV.ImportData.Pause"));
				if (_currThread == null)
				{
					_currThread = new Thread(_exeImp);
					_currThread.start();
				}
				else
				{
					_exeImp.Continue();
				}
			}
			else
			{
				_cmdImportExecut.setText(_wld.getString("Button.fCSV.ImportData.Continue"));
				_exeImp.Pause();
			}
			_cmdImportInterrupt.setVisible(true);
		}
	};
	
	Action actImportDataFinished = new AbstractAction() 
	{
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			_currThread = null;
			_cmdImportExecut.setEnabled(true);
			_cmdImportExecut.setText(_wld.getString("Button.fCSV.ImportData"));
			_cmdImportInterrupt.setVisible(false);
			
			if (_exeImp.getErrorQuantity() > 0)
				_exeImp.infoNewLine(String.format(_wld.getString("Text.Message.Error.Quantity"), _exeImp.getErrorQuantity()));
			
			getCSVDefinition();
			
			_cmdImportExecut.setSelected(false);
		}
	};

	Action actImportDataInterrupt = new AbstractAction() 
	{
		
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (_currThread != null)
			{
				_currThread.interrupt();
				_currThread = null;
				_cmdImportExecut.setEnabled(true);
				_cmdImportExecut.setText(_wld.getString("Button.fCSV.ImportData"));
				_cmdImportInterrupt.setVisible(false);
			}
			//getCSVDefinition();
		}
	};
	
	Action actImportDataError = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			_exeImp.infoNewLine(e.getActionCommand());
			if (e.getID() ==1)
			{
				_cmdImportExecut.setSelected(false);
				_cmdImportExecut.setText(_wld.getString("Button.fCSV.ImportData.Continue"));
			}
		}
	};
	
	private void _setCurrentCSVDefinitionFileName()
	{
		JFileChooser fDlg = new JFileChooser();
		FileNameExtensionFilter fnf = new FileNameExtensionFilter("CSV definition", CSVDefinition.FILENAME_EXT);
		fDlg.setFileFilter(fnf);
		if (_currCSVDefFN != null && _currCSVDefFN.length() > 0)
			fDlg.setCurrentDirectory(new File(_currCSVDefFN));
		
		if (fDlg.showDialog(fCSV.this, _wld.getString("Text.FileChooser.CSVDefinition")) == JFileChooser.APPROVE_OPTION)
		{
			_currCSVDefFN = fDlg.getSelectedFile().getPath();
			
			if (!_currCSVDefFN.endsWith(CSVDefinition.FILENAME_EXT))
				_currCSVDefFN += "." + CSVDefinition.FILENAME_EXT;
			
			fCSV.this.setTitle(_wld.getString("Titles.CSVTools") + " - [" + _currCSVDefFN + "]");
		}
		else
		{
			_currCSVDefFN = null;
			fCSV.this.setTitle(_wld.getString("Titles.CSVTools") + " - []");
		}
	}
	
	private void _saveCSVDef()
	{
		if (_currCSVDefFN != null && _currCSVDefFN.length() > 0)
		{
			CSVDefinition csd = _csv.getCSVDef(); 

			setCSVDefinition();
			
			TableColumnModel tcm = _tabData.getColumnModel();
			int ii = 0;
			for (Enumeration<TableColumn> etc = tcm.getColumns(); etc.hasMoreElements(); ii++)
				csd.getField(ii, false).ColWidth = etc.nextElement().getWidth();

			
			String res = csd.Save(_currCSVDefFN);
			if (res != null)
				infoNewLine(String.format(_wld.getString("Text.Message.SaveError"), res));
			else 
				infoNewLine(String.format(_wld.getString("Text.Message.Successful.Save.ToFile"), _currCSVDefFN));
		}
	}
	
	private void setCSVDefinition()
	{
		CSVDefinition csd = _csv.getCSVDef();
		
		csd.FileName = _txtCSVFileName.getText();
		if (_txtMaxRowAsOnce.getValue() != null && _txtMaxRowAsOnce.isEditValid())
			csd.MaxRowAtOnce = ((Number)_txtMaxRowAsOnce.getValue()).intValue();
		csd.IsFirstRowHeader = _chkFirstRowHeader.isSelected();
		csd.Separator = _txtCharSeparator.getText();
		
		//csd.TableName = _txtDBTableName.getText();
		//csd.SQLCreateTable = _txtSQLCreate.getText();
		//csd.SQLInsertInto = _txtSQLInsert.getText();
		
		try	{ csd.SICode = Integer.parseInt(_txtISCode.getText()); } 
		catch (Exception ex) { csd.SICode = 0;}
		
		csd.SIName = _txtISName.getText();
		csd.SIDay = ((SpinnerNumberModel)_spnISDateDay.getModel()).getNumber().intValue();
		csd.SIMonth = ((SpinnerNumberModel)_spnISDateMonth.getModel()).getNumber().intValue();
		csd.SIYear = ((SpinnerNumberModel)_spnISDateYear.getModel()).getNumber().intValue();
		csd.SIRegion = _txtISRegion.getText();
		rbNode rbn = (rbNode)_cboISType.getSelectedItem();
		if (rbn != null)
			csd.SITypeR = rbn.getId();
		//csd.SINegDesc = _txtISNegDesc.getText();
		//csd.SI3fDesc = _txtIS3FDesc.getText();
		//csd.SIOptDesc = _txtISOptDesc.getText();
	}
	
	private void getCSVDefinition()
	{
		CSVDefinition csd = _csv.getCSVDef();
		_txtCSVFileName.setText(csd.FileName);
		_txtMaxRowAsOnce.setValue(new Integer(_csv.getMaxRowAtOnce()));
		_chkFirstRowHeader.setSelected(_csv.IsFirstRowHeader());
		_txtCharSeparator.setText(_csv.getSeparator());

		_txtISCode.setText(csd.SICode+CC.STR_EMPTY);
		_txtISName.setText(csd.SIName);
		_spnISDateDay.setValue(csd.SIDay);
		_spnISDateMonth.setValue(csd.SIMonth);
		_spnISDateYear.setValue(csd.SIYear);
		_txtISRegion.setText(csd.SIRegion);
		DefaultComboBoxModel<rbNode> modCbo = (DefaultComboBoxModel<rbNode>)_cboISType.getModel(); 
		for (int ii = 0; ii < modCbo.getSize(); ii++)
			if (csd.SITypeR == modCbo.getElementAt(ii).getId())
				modCbo.setSelectedItem(modCbo.getElementAt(ii));
		
		//
		//_txtISNegDesc.setText(csd.SINegDesc);
		//_txtIS3FDesc.setText(csd.SI3fDesc);
		//_txtISOptDesc.setText(csd.SIOptDesc);
		
		//_txtDBTableName.setText(csd.TableName);
		//_txtSQLCreate.setText(csd.SQLCreateTable);
		//_txtSQLInsert.setText(csd.SQLInsertInto);
		
	}
	
	private  void  _showCurrentConnectionURL()
	{
		infoNewLine(
			_wld.get_wdb().getDBConnParam().Driver.getDescription()
			+ CC.NEW_LINE
			+ _wld.get_wdb().getConnectDescription()
		);
	}
	
	private void _showDBManager()
	{
		// first check DB connection !
		if (_wld.get_wdb().IsDBConnectionParamDefined())
		{
			if (_trmDBMetaData == null)
			{
				LoadDBMetadataTreeNode();
			}
		}
		else
		{
			infoNewLine("------------- ");
			actSetDBConnection.actionPerformed(null);
		}
	}

	private void LoadDBMetadataTreeNode()
	{
		try
		{
			DatabaseMetaData md = _wld.get_wdb().getConn().getMetaData();
			DefaultMutableTreeNode nodeDB = new DefaultMutableTreeNode(md.getDatabaseProductName());
			_trmDBMetaData = new DefaultTreeModel(nodeDB);
			ResultSet rsCat = md.getCatalogs();
			while(rsCat.next())
			{
				String catalog = rsCat.getString(1);
				if(catalog.equalsIgnoreCase(_wld.get_wdb().getDBConnParam().DBName))
				{
					DefaultMutableTreeNode nodeCat = new DefaultMutableTreeNode(catalog);
					nodeDB.add(nodeCat);
					//ResultSet rsSch = md.getSchemas(catalog, null);
					ResultSet rsTab = md.getTables(catalog, null, null, new String[] {"TABLE","VIEW"});
					DefaultMutableTreeNode prevNodeSch = null;
					while (rsTab.next())
					{
						String schema = rsTab.getString(2);
						if (schema.equals("sys"))
							continue;
						if (prevNodeSch == null || !prevNodeSch.toString().equals(schema))
						{
							DefaultMutableTreeNode nodeSch = new DefaultMutableTreeNode(schema);
							nodeCat.add(nodeSch);
							prevNodeSch = nodeSch;
						}
						//ResultSet rsTab = md.getTables(catalog, schema, null, null);
						//while (rsTab.next())
						//{
						String tab = rsTab.getString(3);
						if (rsTab.getString(2) != null && rsTab.getString(2).length() > 0)
							tab = rsTab.getString(2) + "." + tab;
						DefaultMutableTreeNode nodeTab = new DefaultMutableTreeNode(tab);
						prevNodeSch.add(nodeTab);
						ResultSet rsCol = md.getColumns(catalog, schema, rsTab.getString(3), null);
						while (rsCol.next())
						{
							String col = rsCol.getString(4)+" - " + rsCol.getString("TYPE_NAME");
							int colLen = rsCol.getInt("COLUMN_SIZE");
							if (colLen > 0)
								col += " [" + colLen+"]";
							DefaultMutableTreeNode nodeCol = new DefaultMutableTreeNode(col);
							nodeTab.add(nodeCol);
						}
						
						if (nodeTab.children().hasMoreElements())
						{
							ResultSet rsColPK = md.getPrimaryKeys(catalog, schema, rsTab.getString(3));
							if (rsColPK.next())
							{
								DefaultMutableTreeNode nodePK = new DefaultMutableTreeNode("[PK]");
								nodeTab.add(nodePK);
								do
								{
									String pkCol = rsColPK.getString("PK_NAME") + " [" + rsColPK.getString("COLUMN_NAME") + "]";
									DefaultMutableTreeNode nodePKI = new DefaultMutableTreeNode(pkCol);
									nodePK.add(nodePKI);
									
								} while (rsColPK.next());
							}
							
						}
						//}
						
					}
				}
			}
			
			_treeDB.setModel(_trmDBMetaData);
		}
		catch (SQLException ex)
		{
			infoNewLine("LoadDBMetadataTreeNode() - SQL error: [" + ex.getErrorCode() + "] - "+ex.getMessage());
		}
		catch (Exception ex)
		{
			infoNewLine("LoadDBMetadataTreeNode() - " + ex.getMessage());
		}
		
	}

	private void _execCommandShow()
	{
		try
		{
			String tabName = _treeDB.getSelectionPath().getLastPathComponent().toString(); 
			String strSelect = CC.STR_EMPTY;
			if (_txtDBMCondition.getText().length() > 0)
				strSelect = String.format(_wld.getSQL("Command.SELECT.WHERE"), tabName, _txtDBMCondition.getText());
			else
				strSelect = String.format(_wld.getSQL("Command.SELECT"), tabName);
	
			Statement stm = _wld.get_wdb().getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			infoNewLine(String.format(_wld.getString("Text.Message.ExecutingCommand"), strSelect)); 
			ResultSet rs = stm.executeQuery(strSelect);
			tmDBMResult tm = new tmDBMResult(rs);
			_tabDBMResult.setModel(tm);
			_tabDBMResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			infoNewLine(_wld.getString("Text.Message.ExecutedCommand")); 
			
		}
		catch (Exception ex)
		{
			infoNewLine(ex.getMessage());
		}
	}

	private void _execCommandShowQuantity()
	{
		try
		{
			String tabName = _treeDB.getSelectionPath().getLastPathComponent().toString(); 
			String strSelect = CC.STR_EMPTY;
			if (_txtDBMCondition.getText().length() > 0)
				strSelect = String.format(_wld.getSQL("Command.SELECT.COUNT.WHERE"), tabName, _txtDBMCondition.getText());
			else
				strSelect = String.format(_wld.getSQL("Command.SELECT.COUNT"), tabName);
	
			Statement stm = _wld.get_wdb().getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			infoNewLine(String.format(_wld.getString("Text.Message.ExecutingCommand"), strSelect)); 
			ResultSet rs = stm.executeQuery(strSelect);
			tmDBMResult tm = new tmDBMResult(rs);
			_tabDBMResult.setModel(tm);
			_tabDBMResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			infoNewLine(_wld.getString("Text.Message.ExecutedCommand")); 
			
		}
		catch (Exception ex)
		{
			infoNewLine(ex.getMessage());
		}
	}
	
	private void _execCommandDeleteRows()
	{
		try
		{
			String tabName = _treeDB.getSelectionPath().getLastPathComponent().toString(); 
			String strSelect = CC.STR_EMPTY;
			
			strSelect = String.format(_wld.getSQL("Command.Delete"), tabName, 
				_txtDBMCondition.getText().length() > 0 ? _txtDBMCondition.getText() : "1=1");
	
			Statement stm = _wld.get_wdb().getConn().createStatement();
			infoNewLine(String.format(_wld.getString("Text.Message.ExecutingCommand"), strSelect)); 
			stm.executeUpdate(strSelect);
			TableModel tm = _tabDBMResult.getModel();
			if (tm != null)
				tm = null;
			_tabDBMResult.setModel(new tmDBMResult(null));
			_tabDBMResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			infoNewLine(_wld.getString("Text.Message.ExecutedCommand")); 
		}
		catch (Exception ex)
		{
			infoNewLine(ex.getMessage());
		}
	}

	private void _execCommandCreateTable()
	{
		try
		{
//			String tabName = _treeDB.getSelectionPath().getLastPathComponent().toString(); 
//			String strSelect = CC.STR_EMPTY;
//			
//			strSelect = String.format(_wld.getSQL("Command.Delete"), tabName, 
//				_txtDBMCondition.getText().length() > 0 ? _txtDBMCondition.getText() : "1=1");
//	
//			Statement stm = _wld.get_wdb().getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
//			infoNewLine(String.format(_wld.getString("Text.Message.ExecutingCommand"), strSelect)); 
//			ResultSet rs = stm.executeQuery(strSelect);
//			TableModel tm = _tabDBMResult.getModel();
//			if (tm != null)
//				tm = null;
//			_tabDBMResult.setModel(null);
//			_tabDBMResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//			infoNewLine(_wld.getString("Text.Message.ExecutedCommand"));
			infoNewLine("DO IT LATTER !!!!!!!");
		}
		catch (Exception ex)
		{
			infoNewLine(ex.getMessage());
		}
	}

	private void designTableColCorr()
	{
		//
		JComboBox<String> cboFunction = new JComboBox<String>();
		_tabColCorr.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cboFunction));
		DefaultComboBoxModel<String> modCbo = new DefaultComboBoxModel<String>();
		modCbo.addElement(CC.STR_EMPTY);
		modCbo.addElement(Sixteen.FUNCTION_SRC_CODE);
		modCbo.addElement(Sixteen.FUNCTION_NEXT_CODE);
		modCbo.addElement(Sixteen.FUNCTION_UNION);
		modCbo.addElement(Sixteen.FUNCTION_TO_DATE);
		cboFunction.setModel(modCbo);
		_tabColCorr.setRowHeight(cboFunction.getPreferredSize().height);
	}
	
 	private void LoadProgramPreference()
	{
		_wld.get_wdb().LoadDBConnectioParam2Reg(Sixteen.PREFERENCE_PATH);

		Preferences node = Preferences.userRoot().node(Sixteen.PREFERENCE_PATH+"/CSV" );
		AsRegister.LoadFrameStateSizeLocation(node, this);
		_splVPanel.setDividerLocation(node.getInt("SplitDividerLocation", 100));
		_splvDBMan.setDividerLocation(node.getInt("SplitDBMan", 100));
		//_splHPanel.setDividerLocation(node.getInt("SplitHDividerLocation", 100));
		_txtCSVFileName.setText(node.get("PrevFileName", CC.STR_EMPTY));
		_currCSVDefFN = node.get("LastPath", CC.STR_EMPTY);
		_loadCSVData();
		
		//TableTools.SetColumnsWidthFromString(_tabDB, node.get("TabColWidth_Data", CC.STR_EMPTY));
		TableTools.SetColumnsWidthFromString(_tabFields, node.get("TabColWidth_Fields", CC.STR_EMPTY));
		TableTools.SetColumnsWidthFromString(_tabColCorr, node.get("TabColWidth_ColCorr", CC.STR_EMPTY));
	}
	
	private void SaveProgramPreference()
	{
		Preferences node = Preferences.userRoot().node(Sixteen.PREFERENCE_PATH+"/CSV" );
		
		AsRegister.SaveFrameStateSizeLocation(node, this);
		
		node.putInt("SplitDividerLocation", _splVPanel.getDividerLocation());
		node.putInt("SplitDBMan", _splvDBMan.getDividerLocation());
		//node.putInt("SplitHDividerLocation", _splHPanel.getDividerLocation());
		if (_txtCSVFileName.getText().length() > 0)
			node.put("PrevFileName", _txtCSVFileName.getText());
		
		if (_currCSVDefFN != null)
			node.put("LastPath", _currCSVDefFN);

		//node.put("TabColWidth_Data", TableTools.GetColumnsWidthAsString(_tabDB));
		node.put("TabColWidth_Fields", TableTools.GetColumnsWidthAsString(_tabFields));
		node.put("TabColWidth_ColCorr", TableTools.GetColumnsWidthAsString(_tabColCorr));

		_wld.get_wdb().SaveDBConnectioParam2Reg(Sixteen.PREFERENCE_PATH);
	}
	
}
