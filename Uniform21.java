package thesis;
public class Uniform21 implements PreferencesFactoryI{
	
	public int [] createPreferences(int x, int y)
	{
		int [] preferences = new int[1];
		double randomPref = Math.random();
		int pick = (int) Math.round(randomPref);
		if (pick == 0)
		{
			preferences[0] = 1;
		}
		else if (pick == 1)
		{
			preferences[0] = 2;
		}
		return preferences;
	}

}
