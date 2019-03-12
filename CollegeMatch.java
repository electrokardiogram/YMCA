package thesis;
public class CollegeMatch {
	
	
	public static void main(String args[])
	{
		// flags
		boolean safetySchool = false;
		boolean printModelResults = false;
		boolean printSimResults = false;
		boolean printAverageSimResults = false;
		
		// Input parameters
		int noc = 10; // number of Colleges
		int maxApps = 10; // maxApps
		int classSize = 1000; 
		int safetySchoolSize = 5000;
		int nStudents = 10000; 
		int nSimulationRuns = 1;
		int numberOfPreferencesGenerated = 1000;
		PreferencesFactoryI pfi = new Uniform3R(safetySchool);
	
		// Show time
		SimulationResult averageResult = new SimulationResult(maxApps);
		SimulationResult [] runResults = new SimulationResult[numberOfPreferencesGenerated];
		for(int runCounter = 0; runCounter<numberOfPreferencesGenerated; runCounter++)
		{
			runResults[runCounter] = modelAndSimulation(noc, maxApps, classSize, safetySchoolSize, nStudents, nSimulationRuns, pfi, safetySchool, printModelResults, printSimResults, printAverageSimResults);
			//System.out.println("");
		}
		
		
		// Average results across different preference runs
		for(int round=1; round<=maxApps; round++)
		{
			int selectionRoundIndex = round-1;
			averageResult.averageMatriculationRateByRound[selectionRoundIndex] = 0;
			for(int preferenceRun=0; preferenceRun<numberOfPreferencesGenerated; preferenceRun++)
			{
				if(round == 1)
				{
					averageResult.nUnassigned += runResults[preferenceRun].nUnassigned;
					averageResult.overallAcceptanceRate += runResults[preferenceRun].overallAcceptanceRate;
							
				}
				averageResult.averageMatriculationRateByRound[selectionRoundIndex] += runResults[preferenceRun].averageMatriculationRateByRound[selectionRoundIndex];
			}
			averageResult.averageMatriculationRateByRound[selectionRoundIndex] /= ((float) numberOfPreferencesGenerated);
		}
				
						
		averageResult.nUnassigned = (int) Math.round( ((double) averageResult.nUnassigned) / ( (double) numberOfPreferencesGenerated) );
		averageResult.overallAcceptanceRate /= ((double) numberOfPreferencesGenerated);
				
				
				
		System.out.println("\nAveraging results across preference runs: ");
		for(int round=1; round<=maxApps; round++)
		{
					int selectionRoundIndex = round-1;
					System.out.println(Math.round(averageResult.averageMatriculationRateByRound[selectionRoundIndex]) + "% of students matriculated to their " + round + " choice school");
		}
				
		System.out.println("Average number of unassigned students per 10,000 students is " + 
				   Math.round( ( (float) averageResult.nUnassigned ) / ( 1  ) ) );
		System.out.println("Average acceptance rate: " + Math.round(averageResult.overallAcceptanceRate*100) + "%");
				

	}
	
	public static SimulationResult modelAndSimulation(int numberOfColleges, int maxApps, int classSize, int safetySchoolSize, int nStudents, int nSimulationRuns, PreferencesFactoryI pfi,
			 								boolean safetySchool, boolean printModelResults, boolean printSimResults, boolean printAverageSimResults)
	{	
		// Create colleges
		Student [] students = new Student [nStudents];
		College [] colleges;
		if(safetySchool)
			colleges = runModelSafety(students, numberOfColleges, nStudents, maxApps, classSize, safetySchoolSize, pfi);
		else
			colleges = runModel(students, numberOfColleges, nStudents, maxApps, classSize, pfi);
		
		if(printModelResults)
			printModelResults(students);
		
		return runSimulation(students, colleges, maxApps, nSimulationRuns, printSimResults, printAverageSimResults);
	}
	
	private static College [] runModel(Student [] students, int numberOfColleges, int nofStudents, int maxApplications, int classSize, PreferencesFactoryI pfi)
	{
		College [] colleges = new College [numberOfColleges];
		for (int counter=0; counter<numberOfColleges; counter++)
		{
		   colleges[counter] = new College(counter+1, classSize, maxApplications);
		}
		
		// Create Students
		for(int counter=0; counter< nofStudents; counter++)
			students[counter] = new Student(counter+1, pfi, numberOfColleges, maxApplications);
		
		// Apply to Colleges
		for(int counter=0; counter<nofStudents; counter++)
			students[counter].apply(colleges);
		
		for(int j=1; j<=maxApplications; j++){
			for(int k=0; k<numberOfColleges; k++)
				colleges[k].processApplicationsForModel(j);
		}
		return colleges;
	}
	
	private static College [] runModelSafety(Student [] students, int numberOfColleges, int nofStudents, int maxApplications, int classSize, int safetySchoolSize, PreferencesFactoryI pfi)
	{
		College [] colleges = new College [numberOfColleges];
		for (int counter=0; counter<numberOfColleges; counter++)
		{
		   if( counter == (numberOfColleges-1))
			   colleges[counter] = new College(counter+1, safetySchoolSize, maxApplications);
		   else
			   colleges[counter] = new College(counter+1, classSize, maxApplications);
		}
		
		// Create Students
		for(int counter=0; counter< nofStudents; counter++)
			students[counter] = new Student(counter+1, pfi, numberOfColleges, maxApplications);
		
		// Apply to Colleges
		for(int counter=0; counter<nofStudents; counter++)
			students[counter].apply(colleges);
		
		for(int j=1; j<=maxApplications; j++){
			for(int k=0; k<numberOfColleges; k++)
				colleges[k].processApplicationsForModel(j);
		}
		return colleges;
	}
	
	private static SimulationResult runSimulation(Student [] students, College [] colleges, int maxApplications, 
							int nSimulationRuns, boolean printSimResults, boolean printAverageSimResults)
	{
		SimulationResult averageResult = new SimulationResult(maxApplications);
		SimulationResult [] simResults = new SimulationResult[nSimulationRuns];
		for(int simRunner=0; simRunner<nSimulationRuns; simRunner++)
		{
			simResults[simRunner] = new SimulationResult(maxApplications);
			if (printSimResults)
				System.out.println("*** *** Simulation Runner run " + (simRunner+1) + " *** ***\n");
			for(int k=0; k<colleges.length; k++)
				colleges[k].reset();
			
			// Run a simulation round
			for(int round=1; round<=maxApplications; round++)
			{
				for(int k=0; k<colleges.length; k++)
					colleges[k].runSimulation(round);
			}
			
	
			// Calculate and print summary statistics
			simulationResults(students, colleges, maxApplications, simResults[simRunner]);
			
			// Print results
			if (printSimResults)
			{
				for(int round=1; round<=maxApplications; round++)
				{
					int selectionRoundIndex = round-1;
					System.out.println(Math.round(simResults[simRunner].averageMatriculationRateByRound[selectionRoundIndex]) + "% of students matriculated to their " + round + " choice school");
					
				}
				System.out.println("Number of unassigned students: " + simResults[simRunner].nUnassigned);
				System.out.println("The overall acceptance rate: " + Math.round(100.*simResults[simRunner].overallAcceptanceRate) +"%\n");
			}
		}
		
		// Average results across runs
		for(int round=1; round<=maxApplications; round++)
		{
			int selectionRoundIndex = round-1;
			averageResult.averageMatriculationRateByRound[selectionRoundIndex] = 0;
			for(int simRun=0; simRun<nSimulationRuns; simRun++)
			{
				if(round == 1)
				{
					averageResult.nUnassigned += simResults[simRun].nUnassigned;
					averageResult.overallAcceptanceRate += simResults[simRun].overallAcceptanceRate;
					
				}
				averageResult.averageMatriculationRateByRound[selectionRoundIndex] += simResults[simRun].averageMatriculationRateByRound[selectionRoundIndex];
			}
			averageResult.averageMatriculationRateByRound[selectionRoundIndex] /= ((float) nSimulationRuns);
		}
		
				
		averageResult.nUnassigned = (int) Math.round( ( Student.ONE * ((double) averageResult.nUnassigned) ) / ( (double) nSimulationRuns * students.length ) );
		averageResult.overallAcceptanceRate /= ((double) nSimulationRuns);
		
		
		if(printAverageSimResults)
		{
			System.out.println("\nAveraging results across simulation runs: ");
			for(int round=1; round<=maxApplications; round++)
			{
				int selectionRoundIndex = round-1;
				System.out.println(Math.round(averageResult.averageMatriculationRateByRound[selectionRoundIndex]) + "% of students matriculated to their " + round + " choice school");
			}
			
			System.out.println("Average number of unassigned students per 10,000 students is " + 
					Math.round( ( (float) averageResult.nUnassigned ) / ( 1  ) ) );
			System.out.println("Average acceptance rate: " + Math.round(averageResult.overallAcceptanceRate*100) + "%");
		}
		
		return averageResult;
	}
	
	private static void printModelResults(Student [] students)
	{
		// See the results from student point of view
		for(int counter=0; counter<students.length; counter++)
		{
			System.out.println("");
			System.out.println( students[counter].printPreferences().toString() );
			System.out.println( students[counter].printModelResults().toString() );
		}
	}
	
	private static void simulationResults(Student [] students, College [] colleges, int maxApplications, SimulationResult sr)
	{
		// Simulation result
		for(int counter=0; counter<colleges.length; counter++)
		{
			//c@System.out.println(colleges[counter].printSimulationResults().toString());	
		}
			
		for(int counter=0; counter<colleges.length; counter++)
		{
			StringBuffer sb = colleges[counter].stats();
			//c@System.out.println(sb.toString());
		}
		// Find unassigned students
		sr.nUnassigned = 0;
		for(int counter=0; counter<students.length; counter++)
		{
			if(!students[counter].getSimulationPicked())
				sr.nUnassigned++;
		}
		
		// Calculate overall acceptance rate
		// For now, do this as a weighted average
		long weightedSum = 0;
		int numberToAverage = 0;
		for(int counter=0; counter<colleges.length; counter++)
		{
			weightedSum += colleges[counter].sr.numberOfPicked;
			numberToAverage += colleges[counter].getTotalNumberOfApplicants();
		}
		sr.overallAcceptanceRate = ((float) weightedSum) / ((float) numberToAverage);
	
		// Calculate number matriculating by round
		int [] nMatriculatingByRound = new int[maxApplications];
		for(int round=1; round<=maxApplications; round++)
		{
			int selectionRoundIndex = round-1;
			for(int collegeIdx = 0; collegeIdx<colleges.length; collegeIdx++)
				nMatriculatingByRound[selectionRoundIndex] += colleges[collegeIdx].sr.matriculatingByRound[selectionRoundIndex];
			sr.averageMatriculationRateByRound[selectionRoundIndex] = ((float) 100.) * ((float) nMatriculatingByRound[selectionRoundIndex]) / ((float) students.length);
		}

	}
}
