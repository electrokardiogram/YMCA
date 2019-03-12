package thesis;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class College 
{
	
	private int id;
	private int classSize;
	private long [] modelAvailableSpaces;
	
	public Set<Student> allApplicants;
	public SelectionRoundModel [] selectionRounds;
	public SelectionRoundSimulation [] selectionRoundsSim;
	public CollegeSimulationResult sr;
	
	public College(int _id, int _classSize, int maxCollegeAppsPerStudent)
	{
		id = _id;
		classSize = _classSize;
		allApplicants = new HashSet<Student>();
		selectionRounds = new SelectionRoundModel[maxCollegeAppsPerStudent];
		selectionRoundsSim = new SelectionRoundSimulation[maxCollegeAppsPerStudent];
		
		for(int counter=0; counter<maxCollegeAppsPerStudent; counter++)
			selectionRounds[counter] = new SelectionRoundModel(counter+1);
		
		modelAvailableSpaces = new long[maxCollegeAppsPerStudent+1];
		modelAvailableSpaces[0] = classSize * Student.ONE;
	}
	
	public void reset()
	{
		for(int counter=0; counter<selectionRounds.length; counter++)
		{
				Student [] possibleStudents = selectionRounds[counter].getPossibleStudents();
				for(int k=0; k<possibleStudents.length; k++)
					possibleStudents[k].resetSimulationPicked();
		}
	}
	
	public int getId()
	{
		return id;
	}
	
	public int getTotalNumberOfApplicants()
	{
		return allApplicants.size();
	}
	
	public long getModelAvailableSpacesAfterRound(int round)
	{
		return modelAvailableSpaces[round];
	}
	
	public void acceptApplication(Student s, int round)
	{
		if(!allApplicants.contains(s))
			allApplicants.add(s);
		selectionRounds[round-1].addStudent(s);
	}
	
	public void processApplicationsForModel(int round)
	{
		int srIndex = round - 1; // selection round index, round 1 corresponds to array element zero
		selectionRounds[srIndex].calculateModelData(this.id, this.modelAvailableSpaces[srIndex], round);
		
		if(selectionRounds[srIndex].getFractionToAdmit() == Student.ONE)
		{
			// We are admitting all available students
			modelAvailableSpaces[round] = modelAvailableSpaces[srIndex] - selectionRounds[srIndex].getSumExpectedAvailability();
		}
		else
		{
			// We are admitting a fraction with the expectation of filling all available spaces
			modelAvailableSpaces[round] = 0;
		}
	}
	
	public void runSimulation(int round)
	{
		int srIndex = round - 1; // selection round index, round 1 corresponds to array element zero
		long availableSpaces = classSize * Student.ONE;
		for(int k=0; k<round-1; k++)
			availableSpaces -= (this.selectionRoundsSim[k].getNumberOfMatriculatingStudents() * Student.ONE);
		selectionRoundsSim[srIndex] =  new SelectionRoundSimulation(round, selectionRounds[srIndex], availableSpaces);
		selectionRoundsSim[srIndex].pickStudents();
	}
	
	public StringBuffer printSimulationResults()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("Results for College " + this.id + "\n");
		int maxApps = selectionRoundsSim.length;
		long availableSpaces = classSize * Student.ONE;
		for(int k=0; k<maxApps; k++)
		{
			int round = k+1;
			if(k>0)
			   availableSpaces -= (this.selectionRoundsSim[k-1].getNumberOfMatriculatingStudents() * Student.ONE);
			sb.append("Round " + round + " available spaces " + availableSpaces + " results: { (");
			if(selectionRounds[k].getNumberOfPreferredStudents() == 0)
				sb.append("--) --> (");
			else
			{
				Iterator<Student> it = selectionRounds[k].getPreferredStudents();
				while(it.hasNext())
				{
					int nextStudentId = it.next().getId();
					if(it.hasNext())
					   sb.append(nextStudentId + ", ");
					else
					   sb.append(nextStudentId + ") --> (");
				}
			}
			
			// now print the picked students
			if(selectionRoundsSim[k].getNumberOfPickedStudents() == 0)
				sb.append("--) --> (");
			else
			{
				Iterator<Student> it = selectionRoundsSim[k].getPickedStudents();
				while(it.hasNext())
				{
					int nextStudentId = it.next().getId();
					if(it.hasNext())
					   sb.append(nextStudentId + ", ");
					else
					   sb.append(nextStudentId + ") --> (");
				}
			}
			
			// now print the matriculating students
			if(selectionRoundsSim[k].getNumberOfMatriculatingStudents() == 0)
			sb.append("--) }\n");
			else
			{
				Iterator<Student> it = selectionRoundsSim[k].getMatriculatingStudents();
				while(it.hasNext())
				{
					int nextStudentId = it.next().getId();
					if(it.hasNext())
						sb.append(nextStudentId + ", ");
					else
						sb.append(nextStudentId + ") }\n");
				}
			}
		}
		return sb;
	}
	
	public StringBuffer stats()
	{
		StringBuffer sb = new StringBuffer();
		sr = new CollegeSimulationResult();
		sr.numberOfPicked= 0;
		sr.numberOfMatriculating = 0;
		sr.matriculatingByRound = new int[selectionRoundsSim.length];
		
		for(int counter=0; counter<selectionRoundsSim.length; counter++)
		{
			sr.numberOfPicked += selectionRoundsSim[counter].getNumberOfPickedStudents();
			sr.numberOfMatriculating += selectionRoundsSim[counter].getNumberOfMatriculatingStudents();
			sr.matriculatingByRound[counter] = selectionRoundsSim[counter].getNumberOfMatriculatingStudents();
		}
		// Note that if getTotalNumberOfApplicants() = 0, acceptanceRate will be float.NaN (not a number)
		sr.acceptanceRate = ((float) sr.numberOfPicked) / ((float) this.getTotalNumberOfApplicants()) * 100;
		sb.append("Summary simulation stats for college " + this.id + "\n");
		sb.append("Applied, picked, matriculating: " + this.getTotalNumberOfApplicants() + " " + sr.numberOfPicked + " " +sr.numberOfMatriculating + "\n");
		sb.append("Acceptance rate: " + Math.round(sr.acceptanceRate) + "%\n");
		return sb;
	}
}
