package thesis;
public class Student 
{
	
	/*
	 * The model assumes that a student will apply to "numberOfApplications" colleges
	 * 
	 * The preferences array lists the colleges by id in order of preference
	 * preferences[0] is the first choice college identified by number 1 .. number of colleges
	 * preferences[1] is the second choice college
	 * ...
	 * preferences[numberOfApplications-1] is the least preferred college
	 * 
	 * The model and simulation calculations are based on draft rounds, kind of like in sports
	 * First the model deals with all the first choices (first round)
	 * Then the model deals with all the second choices (second round)
	 * After the model calculations are complete, they are used to do simulation calculations
	 * 
	 * MODEL CALCULATIONS
	 * The picked and expected arrays are not simulation results, but are probabilities based on the model
	 * The picked array lists the probability that, in the kth round, the kth preferred college will pick this student
	 * There is an offset for arrays, where the zeroth member of the array is for the first choice/first round
	 * Also, to accurately determine true zero or true one, integers rather than floating numbers are used, with 10000 being used for 1 or 100%
	 * 
	 * So: 
	 * if preferences[0] = 6, then college 6 is the first choice college, and is dealt with in the first round.  As the student's first choice,
	 * then if picked, they can be expected to attend the college and thus ...
	 * if picked[0] = 7500, then the model predicts a 75% chance that college 6 will pick this student in the first round, and 
	 * if expected[0] = 7500, then there is a 75% chance the student would attend the college.
	 * 
	 * if preferences[1] = 8, then college 8 is the second choice college, and is dealt with in the second round
	 * if picked[1] = 10000, then the model predicts a 100% chance that college 8 will pick this student in the second round, and 
	 * if expected[1] = 2500, then there is a 25% chance the student would attend the college.  Why 25%? 
	 * In this example, there is a 75% chance the student was picked in round 1, so even though there is 100% chance that college 8 picks the student in round 2,
	 * there is a 75% chance the student was picked by college 6 in round 1.  As college 6 is a higher preference (1st) than college 8 (2nd), then if the student
	 * was picked by college 6 in round 1 the student would attend college 6.
	 * 
	 * More generally:
	 * 
	 * sumOfPrevExpected[0] = 0
	 * sumOfPrevExpected[k] = sumOfPrevExpected[k-1] + expected[k-1], k>=1
	 * 
	 * expected[0] = picked[0]
	 * expected[k] = picked[k] * { 1 - sum( j=0 to k-1: expected[j] ) }
	 * 
	 * sum(j=0 to k-1: expected[j]) = expected[0] + expected[1] + expected[2] + ... + expected[k-1]
	 *                              = 0 + expected[0] + expected[1] + expected[2] + ... + expected[k-1]
	 *                              = ( sumOfPrevExpected[0] + expected[0] ) + expected[1] + expected[2] + ... + expected[k-1]
	 *                              = ( sumOfPrevExpected[1] + expected[1] ) + expected[2] + ... + expected[k-1]
	 *                              = ( sumOfPrevExpected[2] + expected[2] ) + ... + expected[k-1]
	 *                              = sumOfPrevExpected[k-1] + expected[k-1]
	 *                              = sumOfPrevExpected[k]
	 *                              
	 * SO
	 * expected[k] = picked[k] * { 1 - sumOfPrevExpected[k]) }
	 *
	 * Note also that sumOfPrevExpected[k] <= 1, in many cases it will be exactly 1
	 * 
	 * SIMULATION CALCULATIONS
	 * Simulation takes place in the College class, where from a group of students that have some probability of being picked,
	 * a particular "pick set" is generated.
	 */
	public static final int ONE = 10000;
	public static final int NEARLY_ONE = 9950;
	private int id;
	private int [] preferences;
	private int [] picked; // 10,000 = 100%
	private int [] expected; // 10,000 = 1
	private int [] sumOfPrevExpected; // 10,000 = 1
	
	private int numberOfApplications; // This is the number of schools that the student will apply to.
	private boolean modelHasNotBeenPicked; // True if for the "model" the student has not been picked
	private boolean simulationPicked; // True if for a simulation run the student has been picked
	
	public Student(int studentId, PreferencesFactoryI pfi, int nofSchools, int nofApps)
	{
		id = studentId;
		numberOfApplications = nofApps;
		preferences = pfi.createPreferences(nofSchools, numberOfApplications);
		picked = new int[numberOfApplications];
		expected = new int[numberOfApplications];
		sumOfPrevExpected = new int[numberOfApplications];
		
		// For simulation
		modelHasNotBeenPicked = true;
		simulationPicked = false;
	}
	
	public boolean getModelHasNotBeenPicked(){
		return modelHasNotBeenPicked;
	}
	
	public void setModelHasBeenPicked(){
		modelHasNotBeenPicked = false;
	}
	
	public boolean getSimulationPicked(){
		return simulationPicked;
	}
	
	public void setSimulationPicked(){
		simulationPicked = true;
	}
	
	public void resetSimulationPicked(){
		simulationPicked = false;
	}

	public int getId()
	{
		return id;
	}
	
	public void apply(College [] colleges)
	{
		int nofApplicationRounds = numberOfApplications+1;
		for (int round=1; round<nofApplicationRounds; round++)
		{
			int collegeIndex = preferences[round-1] - 1; 
			colleges[collegeIndex].acceptApplication(this, round);
		}
	}
	
	public int getPreference(int index)
	{
		return preferences[index];
	}
	
	public int getPicked(int index)
	{
		return picked[index];
	}
	
	public int getExpected(int index)
	{
		return expected[index];
	}
	
	public int getSumOfPrevExpected(int index)
	{
		return sumOfPrevExpected[index];
	}
	
	public void setPickedForRound(int round, int pickedVal)
	{
		// Note this should only be called by index order and only once for a given "index" as the expectedTotal is increased
		int index = round - 1;
		if(modelHasNotBeenPicked)
			picked[index] = pickedVal;
		else
			picked[index] = 0;
		updateExpected(index);
	}
	
	public void updateSumOfPrevExpected(int round)
	{
		int index = round - 1;
		if(index ==0)
			sumOfPrevExpected[index] = 0;
		else
			sumOfPrevExpected[index] = sumOfPrevExpected[index-1] + expected[index-1];
	}
	
	private void updateExpected(int index)
	{
		if(index == 0)
			expected[index] = picked[index];
		else
		{
			double temp = ( (double) sumOfPrevExpected[index] ) / ( (double) Student.ONE );
			double expectedD = (double) picked[index] * (1.0 - temp);
			expected[index] = (int) expectedD;
		}
	}
	
	public StringBuffer printPreferences()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("The preferences for student " + id + " are: ");
		for(int k=0; k<numberOfApplications; k++)
		{
			int nextPreference = this.getPreference(k);
			sb.append(nextPreference + " ");
		}
		return sb;
	}
	
	public StringBuffer printModelResults()
	{	
		StringBuffer sb = new StringBuffer();
		for(int k=0; k<numberOfApplications; k++)
		{
			int round = k+1;
			sb.append("Round " + round + " the picked is: " + this.getPicked(k) + "\n");
			sb.append("Round " + round + " the expected is: " + this.getExpected(k) + "\n");
			sb.append("Round " + round + " the sum prev exp is: " + this.getSumOfPrevExpected(k) + "\n\n");
		}
		return sb;
	}
	
}
