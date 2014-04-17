package tor.java.sixteen;

public class ExecImport extends ExecWithDB 
{

	public ExecImport(Sixteen aWld)
	{
		super(aWld);
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
