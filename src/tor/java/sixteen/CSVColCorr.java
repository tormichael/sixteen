package tor.java.sixteen;

public class CSVColCorr 
{
	public String	TgtColName;
	public String	TgtColType;
	public String	SrcValue;
	public String	SrcFunction;
	public String	SrcFunArg;

	public CSVColCorr()
	{
		this (null, null, null, null, null);
	}
	
	public CSVColCorr(String aTCN, String aTCT, String aSV, String aSF, String aSFA)
	{
		TgtColName = aTCN;
		TgtColType = aTCT;
		SrcValue = aSV;
		SrcFunction = aSF;
		SrcFunArg = aSFA;
	}

}
