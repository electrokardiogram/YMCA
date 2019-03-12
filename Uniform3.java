package thesis;
public class Uniform3 implements PreferencesFactoryI{
	
	int counter=0;
	
	public int [] createPreferences(int numberOfSchools, int y)
	{
		int [] preferences = new int[3];
		if ( (counter == 0) || (counter == 1) )
		{
			preferences[0] = 1;
			preferences[1] = 2;
			preferences[2] = 3;
		}
		else if ( counter == 2 )
		{
			preferences[0] = 2;
			preferences[1] = 3;
			preferences[2] = 1;
		}
		else
		{
			preferences[0] = 2;
			preferences[1] = 1;
			preferences[2] = 3;
		}
		
		counter++;
		return preferences;
	}

}
