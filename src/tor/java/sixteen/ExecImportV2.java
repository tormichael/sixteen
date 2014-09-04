package tor.java.sixteen;

import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.crypto.ExemptionMechanismException;

import JCommonTools.CC;
import JCommonTools.RefBook.RefBook;
import JCommonTools.RefBook.rbNode;

public class ExecImportV2 extends ExecWithDB 
{
	private CSVData _csv;

	public ExecImportV2(Sixteen aWld, CSVData aCSV)
	{
		super(aWld);
		_csv = aCSV;
	}

	
	@Override
	public void run() 
	{
		Statement stmt = null;
		String st = null;
		String strVal = null;
		try
		{
			mPause = false;
			mGate.release();
			Thread.sleep(500); // This sleep necessary one or more in loop

			//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			//!!!!!!!!!!!!!!!!!!!! RUN BODY !!!!!!!!!!!!!!!!!!!!
			//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			infoNewLineTime("STARTING ...");
			mErrQnt = 0;
			
			mConn = mWld.get_wdb().getConn();
			stmt = mConn.createStatement();
			CSVDefinition csd = _csv.getCSVDef();
			/**
			 * If SICode is 0, then generate next code
			 */
			if (csd.SICode == 0)
			{
				csd.SICode = getNextCode(mConn, "in_code", "src.info");
				st =String.format(
						mWld.getSQL("Command.Insert.Into")
						, "src.info"
						, "in_code,in_name,in_datey,in_datem,in_dated,in_region,in_typer"
						, csd.SICode + ","
						+ "'"+ csd.SIName + "', "
						+ csd.SIYear + ","
						+ csd.SIMonth + ","
						+ csd.SIDay + ","
						+ "'"+ csd.SIRegion + "', "
						+ csd.SITypeR
				);
			}
			else
			{
				st =String.format(
						mWld.getSQL("Command.Update")
						, "src.info"
						, "in_name = '"+ csd.SIName + "'" 
						+ ", in_datey = "+ csd.SIYear
						+ ", in_datem = "+ csd.SIMonth
						+ ", in_dated = "+ csd.SIDay
						+ ", in_region = '"+ csd.SIRegion + "'"
						+ ", in_typer = "+ csd.SITypeR
						, "in_code = " + csd.SICode 
				);
				
			}
			infoNewLine("executing -> "+st);
			stmt.executeUpdate(st);

			/**
			 * First delete old data
			 */
			st ="DELETE FROM src.ivo USING src.ival"
				+ " WHERE vo_iv_code = src.ival.iv_code AND src.ival.iv_in_code=" + csd.SICode;	
			infoNewLine("executing -> "+st);
			stmt.executeUpdate(st);
			st =String.format(
					mWld.getSQL("Command.Delete")
					, "src.ival"
					, "src.ival.iv_in_code = " + csd.SICode 
			);
			infoNewLine("executing -> "+st);
			stmt.executeUpdate(st);
			st =String.format(
					mWld.getSQL("Command.Delete")
					, "src.iopt"
					, "src.iopt.io_in_code = " + csd.SICode 
			);
			infoNewLine("executing -> "+st);
			stmt.executeUpdate(st);
			/**
			 * Then create rows in table [src.iopt]
			 */
			
			PreparedStatement pst = mConn.prepareStatement(
					String.format(mWld.getSQL("Command.Insert.Into"), 
							"src.iopt", 
							"io_code, io_owner, io_in_code, io_colname, io_colbaser, io_coltyper", 
							"?, ?, ?, ?, ?, ?"
			));
			infoNewLine("executing -> "+pst);
			_save_iopt(
					pst, 
					_csv.getCSVDef().RecIOpt.getChildren(), 
					getNextCode(mConn, "io_code", "src.iopt"), 
					0, 
					csd.SICode
			);
			
			/**
			 * Then import data
			 * FIRST TABLE [src.ival] 
			 */
			st = CC.STR_EMPTY;
			String st2 = CC.STR_EMPTY;
			for (CSVColCorr ccc : csd.ArrColCorr)
			{
				st += (st.length() > 0 ? "," : CC.STR_EMPTY) + ccc.TgtColName;
				st2 += (st2.length() > 0 ? "," : CC.STR_EMPTY) + "?";
			}
			
			st =String.format(mWld.getSQL("Command.Insert.Into"), "src.ival", st, st2);
			pst = mConn.prepareStatement(st);
			mCurRow = 0;
			_csv.OpenSourceFile();
			String rowData = null;
			int iv_code = getNextCode(mConn, "iv_code", "src.ival");
			int vo_code = getNextCode(mConn, "vo_code", "src.ivo");
			while ((rowData = _csv.GetNextDataRow()) != null)
			{
				if (rowData.trim().length() == 0)
					continue; // skip empty rows 
				
				if (mPause)
					mGate.acquire();

				if ((mCurRow % mWld.DISPLAYEACHNNROW) == 0)
				{
					Thread.sleep(1);
					infoPosition("--> "+ mCurRow);
				}
				
				/**
				 * add row to [src.ival]
				 */
				int colNum = 0;
				for (CSVColCorr ccc : csd.ArrColCorr)
				{
					colNum++;
					
					if (ccc.SrcFunction != null && ccc.SrcFunction.equals(Sixteen.FUNCTION_SRC_CODE))
					{
						pst.setInt(colNum, csd.SICode);
						//continue;
					}
					else if (ccc.SrcFunction != null && ccc.SrcFunction.equals(Sixteen.FUNCTION_NEXT_CODE))
					{
						pst.setInt(colNum, iv_code+mCurRow);
						//continue;
					}
					else if (ccc.SrcValue != null && ccc.SrcValue.length()>0)
					{
						if (ccc.SrcFunction != null && ccc.SrcFunction.length() > 0)
						{
							if (ccc.SrcFunction.equals(Sixteen.FUNCTION_UNION))
							{
								strVal = CC.STR_EMPTY;
								String [] ss = ccc.SrcValue.split(";", -1);
								for (String sv : ss)
									strVal += (strVal.length() > 0 ? ccc.SrcFunArg : CC.STR_EMPTY) + _csv.getValueInStr(rowData, sv.trim());
								pst.setString(colNum, strVal);
							}
							else if (ccc.SrcFunction.equals(Sixteen.FUNCTION_TO_DATE))
							{
								try
								{
									strVal = _csv.getValueInStr(rowData, ccc.SrcValue);
									if (strVal != null && strVal.trim().length()>0)
									{
										SimpleDateFormat df = new SimpleDateFormat(ccc.SrcFunArg);
										try
										{
											Date dt = df.parse(strVal);
											df = new SimpleDateFormat("yyyyMMdd");
											pst.setString(colNum, df.format(dt.getTime()));
										}
										catch (Exception ex) 
										{
											pst.setString(colNum, null);
										}
									}
									else
									{
										pst.setString(colNum, null);
									}
								}
								catch (Exception ex)
								{
									infoNewLine(String.format(mWld.getString("Text.Message.Error.Row"), mCurRow, ex.getMessage() + " ("+strVal+")"));
								}
							}
						}
						else
						{
							strVal = _csv.getValueInStr(rowData, ccc.SrcValue);
							if (strVal == null || strVal.length() == 0)
								pst.setString(colNum, null);
							else if (ccc.TgtColType.indexOf("int") != -1)
								pst.setInt(colNum,Integer.parseInt(strVal));
							else
								pst.setString(colNum, strVal);
						}
					}
					else
					{
						pst.setNull(colNum, java.sql.Types.NULL);
					}
				}
				try
				{
					pst.executeUpdate();
				}
				catch(SQLException ex)
				{
					_errAction(String.format(mWld.getString("Text.Message.Error.Row"), mCurRow, ex.getMessage() + " ("+pst.toString()+")"));
					mErrQnt++;
				}
				
				/**
				 * add rows to [src.ivo]
				 */
				PreparedStatement pst_ivo = mConn.prepareStatement(
						String.format(mWld.getSQL("Command.Insert.Into"), 
								"src.ivo", 
								"vo_code, vo_iv_code, vo_io_code, vo_val", 
								"?, ?, ?, ?"
				));
				vo_code = _save_ivo(
						pst_ivo, 
						_csv.getCSVDef().RecIOpt.getChildren(), 
						vo_code, 
						iv_code+mCurRow, 
						rowData
				);
				
				mCurRow++;
			}
		}
		catch (InterruptedException ie){
			Thread.currentThread().interrupt();
			infoNewLine(">>> Thread interrupt !!! ");
		}
		catch (Exception ex)
		{
			infoNewLine(String.format(mWld.getString("Text.Message.Error.Row"), mCurRow, ex.getMessage()));
			mErrQnt++;
		}
		finally
		{
			infoNewLineTime("FINISHED !");
			
			try
			{
				if (mConn != null && !mConn.isClosed())
				{
					mConn.close();
				}
				mConn = null;
			}
			catch (SQLException ex)
			{
				infoNewLine(ex.getMessage());
			//	showQuantityErrors();
			}
		}
		
		if (mActFinshed != null)
			mActFinshed.actionPerformed(null);
	}
	
	private int _save_iopt(PreparedStatement aPS, ArrayList<RecordIOpt> aChildren, int aCode, int aOwnerCode, int aInfoCode)
	{
		try
		{
			for (RecordIOpt recIOpt : aChildren)
			{
				recIOpt.setCode(aCode);
				aPS.setInt(1, recIOpt.getCode());
				aPS.setInt(2, aOwnerCode);
				aPS.setInt(3, aInfoCode);
				aPS.setString(4, recIOpt.getNameDB());
				aPS.setInt(5, recIOpt.get_colBaseR());
				aPS.setInt(6, recIOpt.get_colTypeR());
				aPS.executeUpdate();
				aCode++;
				if (recIOpt.getChildren().size() > 0)
					aCode = _save_iopt(aPS, recIOpt.getChildren(), aCode, aCode, aInfoCode);
			}
			
		}
		catch (SQLException ex)
		{
			infoNewLine(ex.getMessage());
		}
		
		return aCode;
		
	}
	
	private int _save_ivo(PreparedStatement aPS, ArrayList<RecordIOpt> aChildren, int aCode, int aIVCode, String aRowData)
	{
		try
		{
			for (RecordIOpt recIOpt : aChildren)
			{
				String val = _csv.getValueInStr(aRowData, recIOpt.getName());
				if (val != null && val.trim().length() > 0)
				{
					aPS.setInt(1, aCode);
					aPS.setInt(2, aIVCode);
					aPS.setInt(3, recIOpt.getCode());
					aPS.setString(4, val);
					aPS.executeUpdate();
					aCode++;
				}
				if (recIOpt.getChildren().size() > 0)
					aCode = _save_ivo(aPS, recIOpt.getChildren(), aCode, aIVCode, aRowData);
			}
			
		}
		catch (SQLException ex)
		{
			//_lastErrorMessage = ex.getMessage();
		}
		
		return aCode;
		
	}

	private void _errAction(String aMsg)
	{
		if (mActError != null)
		{
			if (!mIsContinueIfError)
				Pause();
			ActionEvent ev = new ActionEvent(this, mIsContinueIfError ? 0 : 1, aMsg);
			mActError.actionPerformed(ev);
		}
		else
		{
			infoNewLine(aMsg);
		}
		
	}
	
	private String getStrOrNull(String aSrc)
	{
		return (aSrc == null || aSrc.length() == 0 ? "null" : "'"+ aSrc + "'");
	}
	
	private int getNextCode(Connection aCon, String aColName, String aTabName) throws SQLException
	{
		int ret = 0;
		Statement  stmt = aCon.createStatement();
		String str = String.format(mWld.getSQL("Command.Get.Next.Code"), aColName, aTabName);
		ResultSet rs =  stmt.executeQuery(str);
		if (rs.next())
			ret = rs.getInt(1);
		
		return ret;
	}
}
