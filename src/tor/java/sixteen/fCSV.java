package tor.java.sixteen;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import JCommonTools.AsRegister;
import JCommonTools.CC;
import JCommonTools.CodeText;
import JCommonTools.GBC;
import JCommonTools.TableTools;
import JCommonTools.DB.dDBConnection;

public class fCSV extends JFrame 
{
	private CSVData _csv;
	private Sixteen _wld;
	private String _currCSVDefFN;

	private JTextField			_txtCSVFileName;
	private JTextField			_txtCharSeparator;
	private JTextField			_txtDBTableName;
	private JTextField			_txtISCode;
	private JTextField			_txtISName;
	private JSpinner			_spnISDateDay;
	private JSpinner			_spnISDateMonth;
	private JSpinner			_spnISDateYear;
	private JTextField			_txtISRegion;
	private JTextField			_txtISNegDesc;
	private JTextField			_txtIS3FDesc;
	private JTextField			_txtISOptDesc;
	private JTextPane			_txtSQLCreate;
	private JTextPane			_txtSQLInsert;
	private JFormattedTextField	_txtMaxRowAsOnce;
	private JCheckBox 			_chkFirstRowHeader;
	private JCheckBox			_chkDoCreateTable;
	private JComboBox<CodeText> _cboISType;
	private JButton				_cmdReloadData;
	private JButton				_cmdGeneratColumns;
	private JButton				_cmdGeneratCreateSQLStatement;
	private JButton				_cmdGeneratInsertSQLStatement;
	private tmCSVFields 		_tmFields;
	private tmColCorr			_tmColCorr;
	private JTable				_tabData;
	private JTable				_tabFields;
	private JTable				_tabColCorr;
	
	private JTextArea _txtStatus;
	
	//private JSplitPane _splHPanel;
	private JSplitPane _splVPanel;
	private JSplitPane _splvSQLCI;
	private JSplitPane _splvSQL;

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
		
		
		JTabbedPane tp = new JTabbedPane();
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
		gblData.setConstraints(_txtCSVFileName, new GBC(1, 0).setFill(GBC.HORIZONTAL).setIns(2).setAnchor(GBC.WEST).setWeight(1.0, 0.0));
		pnlData.add(_txtCSVFileName);
		JButton _cmdSelectFile = new JButton(actSelectFile);
		_cmdSelectFile.setText("...");
		gblData.setConstraints(_cmdSelectFile, new GBC(2, 0).setIns(2).setAnchor(GBC.WEST));
		pnlData.add(_cmdSelectFile);
		/// second row
		JLabel lblBanksTab = new JLabel(_wld.getString("Label.fCSV.CSVData"));
		gblData.setConstraints(lblBanksTab, new GBC(0,1).setGridSpan(2, 1).setIns(2).setAnchor(GBC.CENTER));
		pnlData.add(lblBanksTab);
		_cmdReloadData = new JButton(actReloadData);
		_cmdReloadData.setText(_wld.getString("Button.fCSV.RefreshTable"));
		gblData.setConstraints(_cmdReloadData, new GBC(2,1).setIns(2).setAnchor(GBC.WEST));
		pnlData.add(_cmdReloadData);
		// third row
		//_tmData = new tmCSVData();
		_tabData = new JTable();
		JScrollPane scrollTabRes = new JScrollPane(_tabData); 
		gblData.setConstraints(scrollTabRes, new GBC(0,2).setGridSpan(3, 1).setIns(2).setFill(GBC.BOTH).setWeight(1.0, 1.0));
		pnlData.add(scrollTabRes);
		
		tp.addTab(_wld.getString("TitledBorder.fCSV.CSVFile"), pnlData);
		
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
		JLabel lbl—olTab = new JLabel(_wld.getString("Label.fCSV.ColumnsTab"));
		gblRight.setConstraints(lbl—olTab, new GBC(0,3).setAnchor(GBC.EAST).setIns(2));
		pnlRight.add(lbl—olTab);
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

		tp.addTab(_wld.getString("TitledBorder.fCSV.CSVProperties"), pnlRight);

		//
		//  TAB SQL:
		//
		GridBagLayout gblSQLCreate = new GridBagLayout();
		JPanel pnlSQLCreate = new JPanel(gblSQLCreate);
			_chkDoCreateTable = new JCheckBox(_wld.getString("CheckBox.fCSV.DoCreateTable"));
			gblSQLCreate.setConstraints(_chkDoCreateTable, new GBC(0,0).setInsets(2, 20, 2, 5));
			pnlSQLCreate.add(_chkDoCreateTable);
			_txtDBTableName = new JTextField();
			gblSQLCreate.setConstraints(_txtDBTableName, new GBC(1,0).setWeight(1.0, 0.0).setFill(GBC.HORIZONTAL));
			pnlSQLCreate.add(_txtDBTableName);
			_cmdGeneratCreateSQLStatement = new JButton(actGeneratSQLCreate);
			_cmdGeneratCreateSQLStatement.setText(_wld.getString("Button.fCSV.GeneratStatement"));
			gblSQLCreate.setConstraints(_cmdGeneratCreateSQLStatement, new GBC(2,0).setInsets(2, 20, 2, 20).setAnchor(GBC.EAST));
			pnlSQLCreate.add(_cmdGeneratCreateSQLStatement);
			_txtSQLCreate = new JTextPane();
			JScrollPane scrPane = new JScrollPane(_txtSQLCreate);
			gblSQLCreate.setConstraints(scrPane, new GBC(0,1).setInsets(2, 20, 2, 20).setGridSpan(3, 1).setWeight(1.0, 1.0).setFill(GBC.BOTH));
			pnlSQLCreate.add(scrPane);
			

		GridBagLayout gblSQLInsert = new GridBagLayout();
		JPanel pnlSQLInsert = new JPanel(gblSQLInsert);
			JLabel lblSQLInsert = new JLabel(_wld.getString("Label.fCSV.StatementInsert"));
			gblSQLInsert.setConstraints(lblSQLInsert, new GBC(0,0).setInsets(2, 20, 2, 5).setGridSpan(2, 1));
			pnlSQLInsert.add(lblSQLInsert);
			_cmdGeneratInsertSQLStatement = new JButton(actGeneratSQLInsert);
			_cmdGeneratInsertSQLStatement.setText(_wld.getString("Button.fCSV.GeneratStatement"));
			gblSQLInsert.setConstraints(_cmdGeneratInsertSQLStatement, new GBC(2,0).setInsets(2, 20, 2, 20).setAnchor(GBC.EAST));
			pnlSQLInsert.add(_cmdGeneratInsertSQLStatement);
			_txtSQLInsert = new JTextPane();
			scrPane = new JScrollPane(_txtSQLInsert);
			gblSQLInsert.setConstraints(scrPane, new GBC(0,1).setInsets(2, 20, 2, 20).setGridSpan(3, 1).setWeight(1.0, 1.0).setFill(GBC.BOTH));
			pnlSQLInsert.add(scrPane);
		
		GridBagLayout gblSQLParam = new GridBagLayout();
		JPanel pnlSQLParam = new JPanel(gblSQLParam);
		
		_splvSQLCI = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlSQLCreate, pnlSQLInsert);
		_splvSQL = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _splvSQLCI, pnlSQLParam);
		//tp.addTab(_wld.getString("TabbedPane.fCSV.CSV2DB"), _splvSQL);
		
		//
		//  TAB Column conformity:
		//
		GridBagLayout gblIS = new GridBagLayout();
		JPanel pnlIS = new JPanel(gblIS);
		pnlIS.setBorder(BorderFactory.createTitledBorder(_wld.getString("TitledBorder.fCSV.InfoSource")));
			JLabel lbl = new JLabel(_wld.getString("Label.fCSV.ISName"));
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
			_cboISType  = new JComboBox<CodeText>();
			gblIS.setConstraints(_cboISType, new GBC(1,3).setGridSpan(6, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(_cboISType);

			lbl = new JLabel(_wld.getString("Label.fCSV.ISNegDisc"));
			gblIS.setConstraints(lbl, new GBC(0,4).setIns(2).setAnchor(GBC.WEST));
			pnlIS.add(lbl);
			_txtISNegDesc  = new JTextField();
			gblIS.setConstraints(_txtISNegDesc, new GBC(1,4).setGridSpan(7, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(_txtISNegDesc);
			
			lbl = new JLabel(_wld.getString("Label.fCSV.IS3FDisc"));
			gblIS.setConstraints(lbl, new GBC(0,5).setIns(2).setAnchor(GBC.WEST));
			pnlIS.add(lbl);
			_txtIS3FDesc  = new JTextField();
			gblIS.setConstraints(_txtIS3FDesc, new GBC(1,5).setGridSpan(7, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(_txtIS3FDesc);
			
			lbl = new JLabel(_wld.getString("Label.fCSV.ISOptDisc"));
			gblIS.setConstraints(lbl, new GBC(0,6).setIns(2).setAnchor(GBC.WEST));
			pnlIS.add(lbl);
			_txtISOptDesc  = new JTextField();
			gblIS.setConstraints(_txtISOptDesc, new GBC(1,6).setGridSpan(7, 1).setIns(2).setFill(GBC.HORIZONTAL).setWeight(1.0, 0.0));
			pnlIS.add(_txtISOptDesc);
			
		//GridBagLayout gblTCC = new GridBagLayout();
		JPanel pnlTCC = new JPanel(new BorderLayout());
		pnlTCC.setBorder(BorderFactory.createTitledBorder(_wld.getString("TitledBorder.fCSV.TabColCon")));
			_tmColCorr = new tmColCorr(_wld, _csv.getCSVDef().ArrColCorr);
			_tabColCorr = new JTable(_tmColCorr);
			JScrollPane scrollTCC = new JScrollPane(_tabColCorr); 
			//gblTCC.setConstraints(scrollTCC, new GBC(0,1).setGridSpan(4, 1).setIns(2).setFill(GBC.BOTH).setWeight(1.0, 1.0));
			pnlTCC.add(scrollTCC, BorderLayout.CENTER);
			JPanel pnl = new JPanel(new BorderLayout());
			JButton cmdImportExecut = new JButton(actImportData);
			cmdImportExecut.setText(_wld.getString("Button.fCSV.ImportData"));
			pnl.add(cmdImportExecut, BorderLayout.EAST);
			JButton cmdInitColCorrTable = new JButton(actInitColCorrTable);
			cmdInitColCorrTable.setText(_wld.getString("Button.fCSV.InitColCorrTable"));
			pnl.add(cmdInitColCorrTable, BorderLayout.WEST);
			pnl.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
			pnlTCC.add(pnl, BorderLayout.SOUTH);
			
		//_splvTabColCon = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlIS, pnlTCC);
		JPanel pnlIS_TCC = new JPanel(new BorderLayout());
		pnlIS_TCC.add(pnlIS, BorderLayout.NORTH);
		pnlIS_TCC.add(pnlTCC, BorderLayout.CENTER);
		tp.addTab("***", pnlIS_TCC);
		//
		// DOWN PANEL:
		//
		JPanel pnlStatus = new JPanel(new BorderLayout());
		_txtStatus = new JTextArea();
		pnlStatus.add(new JScrollPane(_txtStatus), BorderLayout.CENTER);
		
		//_splHPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlData, pnlRight);
		//_splVPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _splHPanel, pnlStatus);
		_splVPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tp, pnlStatus);
		add(_splVPanel, BorderLayout.CENTER);

		_tabFields.getSelectionModel().addListSelectionListener(new ListSelectionListener() 
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				_tmFields.set_currPos(new Point(_tabFields.getSelectedColumn(), _tabFields.getSelectedRow()));
			}
		});
		
		LoadProgramPreference();

		_tabData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		_tabFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		_txtMaxRowAsOnce.setValue(new Integer(_csv.getMaxRowAtOnce()));
		_chkFirstRowHeader.setSelected(_csv.IsFirstRowHeader());
		_txtCharSeparator.setText(_csv.getSeparator());
		
		JPopupMenu pppParamTab = new JPopupMenu();
		JMenuItem mnpPTDelete = new JMenuItem(actCSVShiftRight);
		mnpPTDelete.setText(_wld.getString("PopupMenu.fCSV.tabData.ShiftRight"));
		pppParamTab.add(mnpPTDelete);
		_tabData.setComponentPopupMenu(pppParamTab);

		
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
				
				_csv.GenerateColumns();
				_tmFields.setCSVDaata(_csv);
				_tabFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
				_tabData.setModel(new tmCSVData(_csv));
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

	Action actGeneratSQLCreate = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String stm = _csv.getCSVDef().getFieldsTypeAsString(","+CC.NEW_LINE+"\t");
			stm = String.format(_wld.getSQL("Command.Create.Table"), _txtDBTableName.getText(), stm);
			_txtSQLCreate.setText(stm);
		}
	};

	Action actGeneratSQLInsert = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String stm = String.format(_wld.getSQL("Command.Insert.Into"), 
					_txtDBTableName.getText(), 
					_csv.getCSVDef().getFieldsAsString(", "), 
					"#?" + _csv.getCSVDef().getFieldsAsString("?#, #?") + "?#");
			_txtSQLInsert.setText(stm);
		}
	};
	
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
			//
			_txtISNegDesc.setText(csd.SINegDesc);
			_txtIS3FDesc.setText(csd.SI3fDesc);
			_txtISOptDesc.setText(csd.SIOptDesc);
			
			//_txtDBTableName.setText(csd.TableName);
			//_txtSQLCreate.setText(csd.SQLCreateTable);
			//_txtSQLInsert.setText(csd.SQLInsertInto);
			
			_tabData.setModel(new tmCSVData(_csv));
			_tmFields.setCSVDaata(_csv);
			//_tabFields.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			_tmColCorr = new tmColCorr(_wld, csd.ArrColCorr);
			_tabColCorr.setModel(_tmColCorr);
			
			
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

	Action actInitColCorrTable = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
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
	};
	
	Action actImportData = new AbstractAction() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			
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
			csd.FileName = _txtCSVFileName.getText();
			if (_txtMaxRowAsOnce.getValue() != null && _txtMaxRowAsOnce.isEditValid())
				csd.MaxRowAtOnce = ((Number)_txtMaxRowAsOnce.getValue()).intValue();
			csd.IsFirstRowHeader = _chkFirstRowHeader.isSelected();
			csd.Separator = _txtCharSeparator.getText();
			//csd.TableName = _txtDBTableName.getText();
			//csd.SQLCreateTable = _txtSQLCreate.getText();
			//csd.SQLInsertInto = _txtSQLInsert.getText();
			
			csd.SICode = Integer.parseInt(_txtISCode.getText());
			csd.SIName = _txtISName.getText();
			csd.SIDay = ((SpinnerNumberModel)_spnISDateDay.getModel()).getNumber().intValue();
			csd.SIMonth = ((SpinnerNumberModel)_spnISDateMonth.getModel()).getNumber().intValue();
			csd.SIYear = ((SpinnerNumberModel)_spnISDateYear.getModel()).getNumber().intValue();
			csd.SIRegion = _txtISRegion.getText();
			//
			csd.SINegDesc = _txtISNegDesc.getText();
			csd.SI3fDesc = _txtIS3FDesc.getText();
			csd.SIOptDesc = _txtISOptDesc.getText();

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
	
	private  void  _showCurrentConnectionURL()
	{
		infoNewLine(
			_wld.get_wdb().getDBConnParam().Driver.getDescription()
			+ CC.NEW_LINE
			+ _wld.get_wdb().getConnectDescription()
		);
	}
	

	private void LoadProgramPreference()
	{
		_wld.get_wdb().LoadDBConnectioParam2Reg(Sixteen.PREFERENCE_PATH);

		Preferences node = Preferences.userRoot().node(Sixteen.PREFERENCE_PATH+"/CSV" );
		AsRegister.LoadFrameStateSizeLocation(node, this);
		_splVPanel.setDividerLocation(node.getInt("SplitDividerLocation", 100));
		_splvSQLCI.setDividerLocation(node.getInt("SplitSQLCI", 100));
		_splvSQL.setDividerLocation(node.getInt("SplitSQL", 100));
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
		node.putInt("SplitSQLCI", _splvSQLCI.getDividerLocation());
		node.putInt("SplitSQL", _splvSQL.getDividerLocation());
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
