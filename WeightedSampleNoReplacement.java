package thesis;
import java.util.Random;

public class WeightedSampleNoReplacement implements PreferencesFactoryI
{
	
	private WeightedCollege [] colleges;
	private boolean safety;

	public WeightedSampleNoReplacement(boolean safetySchool, int [] weights)
	{
		safety = safetySchool;
		if(safety)
			colleges = new WeightedCollege[weights.length-1];
		else
			colleges = new WeightedCollege[weights.length];
		
		for(int collegeIdx=0; collegeIdx<colleges.length; collegeIdx++)
		{
			int collegeId = collegeIdx+1;
			colleges[collegeIdx] = new WeightedCollege(collegeId, weights[collegeIdx]);
		}
	}
	
	public void setMin(WeightedCollege [] colleges, int startIndex, int endIndex)
	{
		// Renumber the min value for each college with index between startIndex and endIndex (exclusive)
		for(int index = startIndex; index<endIndex; index++)
		{
			if(index == 0)
			{
				colleges[index].setMin(0);
			}
			else
			{
				int newMin = colleges[index-1].getMin() + colleges[index-1].getWeight();
				colleges[index].setMin(newMin);
			}
		}
	}
	
	public WeightedCollege getCollege(WeightedCollege [] colleges,  int randomVal, int startIdx, int endIdx)
	{
		WeightedCollege collegeMatch = colleges[0];
		
		int upperIdx = endIdx + 1;
		// Search the colleges array to find the matching college
		for(int collegeIdx = startIdx; collegeIdx<upperIdx; collegeIdx++)
		{
			int upper = colleges[collegeIdx].getMin() + colleges[collegeIdx].getWeight();
			if(randomVal < upper)
			{
				// Ding ding, we have a winner!
				collegeMatch = colleges[collegeIdx];
				colleges[collegeIdx] = colleges[endIdx];
				colleges[endIdx] = collegeMatch;
				startIdx = collegeIdx;
				break;
			}
			else if (collegeIdx == endIdx)
			{
				System.out.println("ERROR: I didn't find a match!");
			}
		}
		
		// The matched college was moved towards the end of the array
		// Now renumber
		setMin(colleges, startIdx, endIdx);
		
		return collegeMatch;
	}
	
	public int [] createPreferences(int numberOfSchools, int maxApps)
	{
		// Ignore the parameter numberOfSchools since that should be colleges.length
		
		// Set up
		Random rgen = new Random();  // Random number generator		
		int startIndex = 0;
		int endIndex = colleges.length - 1;
		this.setMin(colleges, startIndex, endIndex+1);
		int [] preferences = new int[maxApps];
		
		if(safety)
			maxApps--;
		
		for(int preferenceIdx = 0; preferenceIdx < maxApps; preferenceIdx++)
		{
			int randomMax = colleges[endIndex].getMin() + colleges[endIndex].getWeight();
			
			// Generate a random number between 0 (inclusive) and randomMax (exclusive)
			int randomVal = rgen.nextInt(randomMax);
			WeightedCollege nextCollege = getCollege(colleges, randomVal, startIndex, endIndex);
			endIndex--;
			preferences[preferenceIdx] = nextCollege.getId();
		}
		
		if(safety)
			preferences[maxApps] = colleges.length + 1;
		
		return preferences;
	}
	
}
