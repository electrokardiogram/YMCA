package thesis;
import java.util.Iterator;
import java.util.LinkedList;

public class SelectionRoundSimulation
{
	// Data for simulation
	private int round;
	private long availableSpaces;
	private SelectionRoundModel model;
	
	private LinkedList<Student> pickedStudents; // in a simulation run, list of students offered admission by the college, may include students who will not attend
	private LinkedList<Student> matriculatingStudents; // in a simulation run, list of students who will attend the college
	
	public SelectionRoundSimulation(int _round, SelectionRoundModel _model, long _availableSpaces)
	{
		round = _round;
		model = _model;
		availableSpaces = _availableSpaces;
	}
	
	public int getNumberOfApplyingStudents()
	{
		return model.getNumberOfPreferredStudents();
	}
	
	public Iterator<Student> getPickedStudents()
	{
		return pickedStudents.iterator();
	}
	
	public Iterator<Student> getMatriculatingStudents()
	{
		return matriculatingStudents.iterator();
	}
	
	public int getNumberOfPickedStudents()
	{
		return pickedStudents.size();
	}
	
	public int getNumberOfMatriculatingStudents()
	{
		return matriculatingStudents.size();
	}
	
	public void pickStudents()
	{
		Student [] possibleStudents = model.getPossibleStudents();
		int numberOfAvailableStudents = possibleStudents.length;
		int pickIndex = numberOfAvailableStudents -1;
		int sumPickedExpectedAvailability = 0;
		pickedStudents = new LinkedList<Student>();
		matriculatingStudents = new LinkedList<Student>();
		
		while( (pickIndex > -1) && (sumPickedExpectedAvailability < availableSpaces) )
		{
			// Randomly pick a possible student in the range from 0 to pickIndex
			int randomIndex = (int)(Math.random() * pickIndex);
			Student randomStudent = possibleStudents[randomIndex];
			
			// swap the student to last place so not picked again
			possibleStudents[randomIndex] = possibleStudents[pickIndex];
			possibleStudents[pickIndex] = randomStudent;
			pickIndex--;
			
			if(randomStudent.getExpected(round-1) == 0)
			{
				// No sense picking the student
			}
			else if(!randomStudent.getSimulationPicked())
			{
				randomStudent.setSimulationPicked();
				pickedStudents.add(randomStudent);
				matriculatingStudents.add(randomStudent);
			}
			else
			{
				// Student is picked but will not matriculate
				pickedStudents.add(randomStudent);
			}
			
			sumPickedExpectedAvailability += (Student.ONE - randomStudent.getSumOfPrevExpected(round-1));
		}
	}
	
	public void printPickedStudentIds()
	{
		Iterator<Student> it = getPickedStudents();
		while(it.hasNext())
		{
				Student student = it.next();
				System.out.println("Student " + student.getId());
		}
	}
}


