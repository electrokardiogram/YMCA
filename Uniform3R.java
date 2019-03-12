package thesis;
import java.util.Random;

public class Uniform3R implements PreferencesFactoryI
{
	boolean safety;
	
	public Uniform3R(boolean safetySchool)
	{
		safety = safetySchool;
	}
	
	public int [] createPreferences(int nofSchools, int maxApps)
	{
		if(safety)
			return createPreferencesSafety(nofSchools, maxApps);
		else
			return createPreferencesNoSafety(nofSchools, maxApps);
	}
	
	private int [] createPreferencesNoSafety(int nofSchools, int maxApps)
	{
		int numberOfSchools = nofSchools;
		Random rgen = new Random();  // Random number generator		
		int[] array = new int[numberOfSchools];
 
		for(int i=0; i< numberOfSchools; i++)
		{
			array[i] = 1+i;
		}
 
		for (int i=0; i<array.length; i++) 
		{
		    int randomPosition = rgen.nextInt(array.length);
		    int temp = array[i];
		    array[i] = array[randomPosition];
		    array[randomPosition] = temp;
		}
		
		int [] preferences =  new int[maxApps]; // maxApps <= numberOfSchools
		for(int i=0; i<maxApps; i++)
			preferences[i] = array[i];
		
		return preferences;
	}
	
	private int [] createPreferencesSafety(int nofSchools, int maxApps)
	{
		int numberOfSchools = nofSchools-1;
		Random rgen = new Random();  // Random number generator		
		int[] array = new int[numberOfSchools];
 
		for(int i=0; i< numberOfSchools; i++)
		{
			array[i] = 1+i;
		}
 
		for (int i=0; i<array.length; i++) 
		{
		    int randomPosition = rgen.nextInt(array.length);
		    int temp = array[i];
		    array[i] = array[randomPosition];
		    array[randomPosition] = temp;
		}
		
		int [] preferences =  new int[maxApps]; // maxApps <= numberOfSchools
		for(int i=0; i<maxApps-1; i++)
			preferences[i] = array[i];
		preferences[maxApps-1] = nofSchools; // so the least preferred is the safety school
		
		return preferences;
	}

	
	
}
