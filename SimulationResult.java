package thesis;
public class SimulationResult 
{
	float [] averageMatriculationRateByRound;
	public int nUnassigned;
	public float overallAcceptanceRate;
	
	public SimulationResult(int maxApplications)
	{
		averageMatriculationRateByRound = new float[maxApplications];
	}

}
