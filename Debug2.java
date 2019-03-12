package thesis;
public class Debug2 implements PreferencesFactoryI{
	
	int counter=0;
	
	public int [] createPreferences(int x, int y)
	{
		int [] preferences = new int[2];
		preferences[0] = 2;
		preferences[1] = 1;
		counter++;
		
		return preferences;
	}

}
