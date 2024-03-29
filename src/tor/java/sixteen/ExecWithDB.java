package tor.java.sixteen;

import java.sql.Connection;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.concurrent.Semaphore;

import javax.swing.Action;
import javax.swing.text.JTextComponent;

import org.omg.CORBA._PolicyStub;

import JCommonTools.CC;

public class ExecWithDB implements Runnable 
{
	protected Sixteen mWld;
	protected Connection mConn;
	
	protected volatile boolean mPause; 
	protected Semaphore mGate;
	protected int mCurRow;
	protected int mErrQnt;
	
	protected boolean mIsContinueIfError;
	
	private JTextComponent _extTextComponent;
	protected Action mActFinshed;
	protected Action mActError;

	public void infoNewLineTime(String aText)
	{
		infoNewLine("[" +new Time(System.currentTimeMillis()).toString() +"] " + aText);
	}
	public void infoNewLine(String aText)
	{
		String result =  _extTextComponent.getText() + CC.NEW_LINE + aText;
		if (_extTextComponent != null)
		{
			_extTextComponent.setText(result);
			_extTextComponent.setSelectionStart(result.length());
			_infoPos = _extTextComponent.getText().length();
		}
	}
	private int _infoPos;
	public void infoPosition(String aText)
	{
		if (_extTextComponent != null)
		{
			_extTextComponent.setText(
					_extTextComponent.getText().substring(0, _infoPos)
					 + CC.NEW_LINE + aText + CC.NEW_LINE 
			);
			_extTextComponent.select(_extTextComponent.getText().length(), _extTextComponent.getText().length());
		}
		
	}
	
	public int getErrorQuantity()
	{
		return mErrQnt;
	}
	
	public void set_extTextComponent(JTextComponent _extTextComponent) 
	{
		this._extTextComponent = _extTextComponent;
	}
	
	public void set_actFinshed(Action _actFinshed) {
		this.mActFinshed = _actFinshed;
	}
	public void set_actError(Action _actError) {
		this.mActError = _actError;
	}

	public void setContinueIfError(boolean aIsCIE)
	{
		mIsContinueIfError = aIsCIE;
	}
	
 	public void Pause()
	{
		mPause = true;
		mGate.release();
		infoNewLine(mWld.getString("Text.Pause.StringN") + mCurRow + " (" + new Time(System.currentTimeMillis()) + ")");
	}
	public void Continue()
	{
		mPause = false;
		mGate.release();
		infoNewLine(mWld.getString("Text.Continue") + " (" + new Time(System.currentTimeMillis()) + ")");
	}

	
	public ExecWithDB(Sixteen aWld)
	{
		mWld = aWld;
		mActFinshed = null;
		mActError = null;
		mGate = new Semaphore(1);
		mPause  = true;
		mCurRow = 0;
		_infoPos = 0;
		mIsContinueIfError = false;
	}
	
	@Override
	public void run() 
	{
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
			mCurRow = 0;
			int RowQuantity = Integer.MAX_VALUE;
			while (mCurRow < RowQuantity)
			{
				if (mPause)
					mGate.acquire();

				if ((++mCurRow) % 500 == 0)
				{
					Thread.sleep(1);
					//showState(RowQuantity, mCurRow);
				}
				
			}
		}
		catch (InterruptedException ie){
			Thread.currentThread().interrupt();
			infoNewLine(">>> Thread interrupt !!! ");
		}
		catch (Exception ex)
		{
			infoNewLine(ex.getMessage());
		}
		finally
		{
			infoNewLineTime("FINISHED !");
			
			//try
			//{
			//	if (mConn != null && !mConn.isClosed())
			//	{
			//		mConn.close();
			//	}
				mConn = null;
			//}
			//catch (SQLException ex)
			//{
			//	infoNewLine(ex.getMessage());
			//	showQuantityErrors();
			//}
		}
		
		if (mActFinshed != null)
			mActFinshed.actionPerformed(null);
	}
	
}
