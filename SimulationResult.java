package thesis;
public class SimulationResult 
{
	public float [] averageMatriculationRateByRound;
	public int nUnassigned;
	public float overallAcceptanceRate;
	public float [] acceptanceRates;
	
	public SimulationResult(int maxApplications, int nColleges)
	{
		averageMatriculationRateByRound = new float[maxApplications];
		acceptanceRates = new float[nColleges];
	}

}
