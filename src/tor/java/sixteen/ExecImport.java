package tor.java.sixteen;

import java.awt.event.ActionEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.spi.DirStateFactory.Result;

import JCommonTools.CC;

public class ExecImport extends ExecWithDB 
{
	private CSVData _csv;

	public ExecImport(Sixteen aWld, CSVData aCSV)
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
				st =String.format(mWld.getSQL("Command.Get.Next.Code"), "si_code", "srcinfo");
				infoNewLine("executing -> "+st);
				ResultSet rs =  stmt.executeQuery(st);
				if (rs.next())
					csd.SICode = rs.getInt(1);
				
				st =String.format(
						mWld.getSQL("Command.Insert.Into")
						, "srcinfo"
						, "si_code,si_name,si_datey,si_datem,si_dated,si_region,si_typer,si_negdesc,si_3fdesc,si_optdesc"
						, csd.SICode + ","
						+ "'"+ csd.SIName + "', "
						+ csd.SIYear + ","
						+ csd.SIMonth + ","
						+ csd.SIDay + ","
						+ "'"+ csd.SIRegion + "', "
						+ csd.SITypeR + ","
						+ getStrOrNull(csd.SINegDesc) + ","
						+ getStrOrNull(csd.SI3fDesc) + ","
						+ getStrOrNull(csd.SIOptDesc)
				);
			}
			else
			{
				st =String.format(
						mWld.getSQL("Command.Update")
						, "srcinfo"
						, "si_name = '"+ csd.SIName + "'" 
						+ ", si_datey = "+ csd.SIYear
						+ ", si_datem = "+ csd.SIMonth
						+ ", si_dated = "+ csd.SIDay
						+ ", si_region = '"+ csd.SIRegion + "'"
						+ ", si_typer = "+ csd.SITypeR
						+ ", si_negdesc = "+ getStrOrNull(csd.SINegDesc)
						+ ", si_3fdesc = "+ getStrOrNull(csd.SI3fDesc)
						+ ", si_optdesc = " + getStrOrNull(csd.SIOptDesc)
						, "si_code = " + csd.SICode 
				);
				
			}
			infoNewLine("executing -> "+st);
			stmt.executeUpdate(st);

			/**
			 * First delete old data
			 */
			st =String.format(
					mWld.getSQL("Command.Delete")
					, "negative.base"
					, "bs_src_code = " + csd.SICode 
			);
			infoNewLine("executing -> "+st);
			stmt.executeUpdate(st);
			/**
			 * Then import data 
			 */
			st = CC.STR_EMPTY;
			String st2 = CC.STR_EMPTY;
			for (CSVColCorr ccc : csd.ArrColCorr)
			{
				st += (st.length() > 0 ? "," : CC.STR_EMPTY) + ccc.TgtColName;
				st2 += (st2.length() > 0 ? "," : CC.STR_EMPTY) + "?";
			}
			
			st =String.format(mWld.getSQL("Command.Insert.Into"), "negative.base", st, st2);
			PreparedStatement pst = mConn.prepareStatement(st);
			mCurRow = 0;
			int RowQuantity = _csv.getRowCount();
			while (mCurRow < RowQuantity)
			{
				if (mPause)
					mGate.acquire();

				if ((mCurRow) % mWld.DISPLAYEACHNNROW == 0)
				{
					Thread.sleep(1);
					infoPosition("--> "+ mCurRow);
				}
				
				
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
						pst.setInt(colNum, mCurRow+1);
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
									strVal += (strVal.length() > 0 ? ccc.SrcFunArg : CC.STR_EMPTY) + _csv.getValueAt(mCurRow, sv.trim());
								pst.setString(colNum, strVal);
							}
							else if (ccc.SrcFunction.equals(Sixteen.FUNCTION_TO_DATE))
							{
								try
								{
									strVal = _csv.getValueAt(mCurRow, ccc.SrcValue);
									if (strVal != null && strVal.trim().length()>0)
									{
										SimpleDateFormat df = new SimpleDateFormat(ccc.SrcFunArg);
										Date dt = df.parse(strVal);
										df = new SimpleDateFormat("yyyyMMdd");
										pst.setString(colNum, df.format(dt.getTime()));
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
							strVal = _csv.getValueAt(mCurRow, ccc.SrcValue);
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
						pst.setString(colNum, null);
					}
				}
				mCurRow++;
				try
				{
					pst.executeUpdate();
				}
				catch(SQLException ex)
				{
					_errAction(String.format(mWld.getString("Text.Message.Error.Row"), mCurRow, ex.getMessage() + " ("+pst.toString()+")"));
					mErrQnt++;
				}
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
}
