package thesis;

import java.util.Hashtable;
import java.util.Set;

public class TestWeightPreferenceGeneration {	
	private class Preference implements Comparable{
		public int [] preference;
		
		public Preference(int [] _preference)
		{
			preference = new int[_preference.length];
			for(int counter=0; counter<_preference.length; counter++)
				preference[counter] = _preference[counter];
		}
		
		public int compareTo(Object obj)
		{
			int returnVal = 1;
			if( ! (obj instanceof Preference) )
				return returnVal;
			Preference pf2 = (Preference) obj;
			if(pf2.preference.length == this.preference.length )
			{
				boolean allMatch = true;
				for(int counter=0; counter<pf2.preference.length; counter++)
				{
					if(pf2.preference[counter] != this.preference[counter])
					{
						allMatch = false;
						break;
					}
				}
				if(allMatch)
					returnVal = 0;
			}
			return returnVal;
		}
	}

	public static void main(String[] args) 
	{
		
		// flags
		boolean safetySchool = false;
		

		// Input parameters
		int noc = 3; // number of Colleges
		int maxApps = 3; // maxApps
		int uniformClassSize = 20; 
		int safetySchoolSize = 5000;
		int nStudents = 100; 
		int numberOfPreferencesGenerated = 100000;
		int [] weights = new int[noc];
		int [] classSizes = new int[noc];
				
		// Suppose colleges are created as follows
		Hashtable<Integer, WeightedCollege> ht = new Hashtable<Integer, WeightedCollege>();
		WeightedCollege [] wc = new WeightedCollege[noc];
		int uniformWeight = 1000;
		int harvardWeight = (int) Math.exp(9.13);
		int yaleWeight = (int) Math.exp(8.52);
		int princetonWeight = (int) Math.exp(8.02);
		wc[0] = new WeightedCollege("Harvard", 1, harvardWeight, uniformClassSize);
		wc[1] = new WeightedCollege("Yale", 2, yaleWeight, uniformClassSize);
		wc[2] = new WeightedCollege("Princeton", 3, princetonWeight, uniformClassSize);
		
		for(int counter=0; counter<noc; counter++)
		{
					Integer key = wc[counter].getId();
					ht.put(key, wc[counter]);
		}
				
		for(int counter=0; counter<noc; counter++)
		{
					Integer id = counter+1;
					WeightedCollege nextWc = ht.get(id);
					if (nextWc != null)
					{
						weights[counter] = nextWc.getWeight();
						classSizes[counter] = nextWc.getClassSize();
					}
		}
				
		PreferencesFactoryI pfi = new WeightedSampleNoReplacement(safetySchool, weights);
		Hashtable<Preference, Integer> prefs = new Hashtable<Preference, Integer>();
		TestWeightPreferenceGeneration test = new TestWeightPreferenceGeneration();
		for(int counter=0; counter<numberOfPreferencesGenerated; counter++)
		{
			Preference nextPreference = test.new Preference(pfi.createPreferences(noc, maxApps));
			// Test to see if prefs already contains nextPreference
			boolean containsKey = false;
			Set<Preference> setOfPrefs = prefs.keySet();
			if(!setOfPrefs.isEmpty()){
				for(Preference nextPref2 : setOfPrefs){
					if( nextPref2.compareTo(nextPreference) == 0 )
					{
						containsKey = true;
						nextPreference = nextPref2;
						break;
					}
				}	
			}
			if(containsKey)
			{
				int currentCount = prefs.get(nextPreference).intValue();
				currentCount++;
				prefs.replace(nextPreference, currentCount);
			}
			else
				prefs.put(nextPreference, 1);
		} // for
		
		// See results
		Set<Preference> setOfPrefs = prefs.keySet();
		 
        // for-each loop
        for(Preference nextPref : setOfPrefs) {
        	StringBuffer sb = new StringBuffer();
        	for(int counter=0; counter<noc; counter++)
        	{
        		String nextName = ht.get(nextPref.preference[counter]).getName();
        		sb.append(nextName + " ");
        	}
        	float percent = ( (float) prefs.get(nextPref) ) * ((float) 100.) / ( (float) numberOfPreferencesGenerated );
        	sb.append(percent);
        	System.out.println(sb.toString());
        }	
	}

}
