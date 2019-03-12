package thesis;
public class Debug61 implements PreferencesFactoryI{
	
	int counter=0;
	
	public int [] createPreferences(int x, int y)
	{
		int [] preferences = new int[3];
		if( counter == 0)
		{
			preferences[0] = 1;
			preferences[1] = 2;
			preferences[2] = 3;
			
		}
		else if( (counter == 1) || (counter == 2) || (counter == 5) )
		{
			preferences[0] = 3;
			preferences[1] = 1;
			preferences[2] = 2;
		}
		else
		{
			preferences[0] = 1;
			preferences[1] = 3;
			preferences[2] = 2;
		}
		counter++;
		
		return preferences;
	}

}
