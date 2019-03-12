package thesis;
import java.util.Iterator;
import java.util.LinkedList;

public class SelectionRoundModel
{
	// Data for a given college and a give selection round
	private LinkedList<Student> preferredStudents; // students that have identified this college as their preferred choice for the given selection round
	private Student [] possibleStudents;
	
	// The next three numbers are all scaled by Student.ONE
	private long sumExpectedAvailability; // the sum of the expected availabilities of the preferred students, based on the sum of all previous selection rounds
	private long modelAvailableSpaces; // how many expected spaces the college has available for the round
	private int fractionToAdmit; // the expected fraction of the preferredStudents that the college will admit for this round (10000 = 1, 7500 = 0.75, etc.)
	private int round;
	
	public SelectionRoundModel(int _round)
	{
		preferredStudents = new LinkedList<Student>();
		sumExpectedAvailability = 0;
		modelAvailableSpaces = 0;
		fractionToAdmit = Student.ONE;
		round = _round;
	}
	
	public void addStudent(Student s){
		preferredStudents.add(s);
	}
	
	public Iterator<Student> getPreferredStudents(){
		return preferredStudents.iterator();
	}
	
	public int getNumberOfPreferredStudents(){
		return preferredStudents.size();
	}
	
	public long getSumExpectedAvailability(){
		return sumExpectedAvailability;
	}
	
	public long getAvailableSpaces(){
		return modelAvailableSpaces;
	}
	
	public int getFractionToAdmit(){
		return fractionToAdmit;
	}
	
	private long calculateFractionToAdmit()
	{
		Student nextStudent;
		Iterator<Student >it = preferredStudents.iterator();
		while(it.hasNext())
		{
			nextStudent = it.next();
			nextStudent.updateSumOfPrevExpected(round);
			sumExpectedAvailability += ( Student.ONE - nextStudent.getSumOfPrevExpected(round-1) );
		}
		
		if(modelAvailableSpaces == 0)
			fractionToAdmit = 0;
		else if(sumExpectedAvailability <= modelAvailableSpaces)
		{
			// Accept all the students, the expected enrollment will not exceed available spaces
			fractionToAdmit = Student.ONE;
		}
		else
		{
			// Too many students, admit a fraction of them.	
			float ffractionToAdmit = ( (float) modelAvailableSpaces ) / ( (float) sumExpectedAvailability );
			fractionToAdmit = (int) (ffractionToAdmit * Student.ONE);
			if(fractionToAdmit > Student.NEARLY_ONE)
				fractionToAdmit = Student.ONE;
		}
		return fractionToAdmit;
	}
	
	public void calculateModelData(int collegeId, long spaces, int round)
	{
		modelAvailableSpaces = spaces;
		
		// Are there any students that prefer this college for this round?
		if(preferredStudents.size() > 0)
		{
			calculateFractionToAdmit();
			
			// Update the student's data
			Iterator<Student >it = preferredStudents.iterator();
			while(it.hasNext())
			{
				Student nextStudent = it.next();
				nextStudent.setPickedForRound(round,  fractionToAdmit);
				if(fractionToAdmit == Student.ONE)
					nextStudent.setModelHasBeenPicked();
			}
		}
		
		// Convert list to array for faster processing in simulation runs
		possibleStudents = new Student [preferredStudents.size()];
		Iterator<Student >it = preferredStudents.iterator();
		int counter = 0;
		while(it.hasNext())
		{
			possibleStudents[counter] = it.next();
			counter++;
		}
	} // calculateModelData
	
	public Student [] getPossibleStudents()
	{
		return possibleStudents;
	}
}
