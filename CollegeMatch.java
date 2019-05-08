package thesis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;

public class CollegeMatch {
	
	
	public static void main(String args[])
	{
		// flags
		boolean safetySchool = false;
		boolean printModelResults = false;
		boolean printSimResults = false;
		boolean printAverageSimResults = false;
		boolean uniformClassSizeFlag  = false;
		
		// Input parameters
		int noc = 10; // number of Colleges
		int maxApps = 2; // maxApps
		int uniformClassSize = 200; 
		int safetySchoolSize = 5000;
		int nStudents = 1; 
		int nSimulationRuns = 1;
		int numberOfPreferencesGenerated = 100;
		int [] weights = new int[noc];
		int [] classSizes = new int[noc];
		
		// Suppose colleges are created as follows
		Hashtable<Integer, WeightedCollege> ht = new Hashtable<Integer, WeightedCollege>();
		WeightedCollege [] wc = new WeightedCollege[noc];
		String [] collegeNames = new String[noc];
		double[] collegeWeightsRaw = new double[noc];
		int [] collegeWeights = new int[noc];
		int [] collegeSizes = new int[noc];
		
		collegeNames[0] = "Harvard";
		collegeNames[1] = "Yale";
		collegeNames[2] = "Princeton";
		collegeNames[3] = "Stanford";
		collegeNames[4] = "MIT";
		collegeNames[5] = "Chicago";
		collegeNames[6] = "Columbia";
		collegeNames[7] = "Berkeley";
		collegeNames[8] = "Northwestern";
		collegeNames[9] = "U Penn";

			
			
		collegeWeightsRaw[0] = 9.13;
		collegeWeightsRaw[1] = 8.52;
		collegeWeightsRaw[2] = 8.02;
		collegeWeightsRaw[3] = 8.11;
		collegeWeightsRaw[4] = 8.16;
		collegeWeightsRaw[5] = 5.11;
		collegeWeightsRaw[6] = 6.77;
		collegeWeightsRaw[7] = 5.17;
		collegeWeightsRaw[8] = 5.3;
		collegeWeightsRaw[9] = 6.39;
		
		
		collegeSizes[0] = 170;
		collegeSizes[1] = 130;
		collegeSizes[2] = 120;
		collegeSizes[3] = 170;
		collegeSizes[4] = 100;
		collegeSizes[5] = 180;
		collegeSizes[6] = 140;
		collegeSizes[7] = 370;
		collegeSizes[8] = 200;
		collegeSizes[9] = 240;

		
		// The following overwrites the sizes and replaces with uniform size
		if(uniformClassSizeFlag)
		{
		   for(int counter=0; counter<noc; counter++)
			   collegeSizes[counter] = uniformClassSize;
		}
		
		nStudents = 0;
		long totalWeight = 0;
		for(int counter=0; counter<noc; counter++)
		{
			collegeWeights[counter] = (int) Math.exp( collegeWeightsRaw[counter] );
			nStudents += collegeSizes[counter];
			totalWeight += collegeWeights[counter];
		}
		
		// Rescale?
		final int bigWeight = 20000;
		//if(bigWeight < 0)
		if(totalWeight > bigWeight)
		{
			System.out.println("Rescaling: ");
			double ratio = ( (double) bigWeight ) / ( (double) totalWeight);
			for(int counter=0; counter<noc; counter++)
			{
				double temp = collegeWeights[counter] * ratio;
				collegeWeights[counter] = (int) temp;
			}
		}
		
		
		System.out.println("Total students: " + nStudents);
		System.out.println("Total weights: " + totalWeight);
		
		
		for(int counter=0; counter< noc; counter++)
		{
			System.out.println("Creating wc " + collegeNames[counter] + collegeWeights[counter]);
			wc[counter] = new WeightedCollege(collegeNames[counter], counter+1, collegeWeights[counter], 
					collegeSizes[counter]);
			Integer key = wc[counter].getId();
			ht.put(key, wc[counter]);
			weights[counter] = collegeWeights[counter];
			classSizes[counter] = collegeSizes[counter];
		}
		
		
		//PreferencesFactoryI pfi = new Uniform3R(safetySchool);
		PreferencesFactoryI pfi = new WeightedSampleNoReplacement(safetySchool, weights);
	
		// Show time
		SimulationResult averageResult = new SimulationResult(maxApps, noc);
		SimulationResult [] runResults = new SimulationResult[numberOfPreferencesGenerated];
		for(int runCounter = 0; runCounter<numberOfPreferencesGenerated; runCounter++)
		{
			runResults[runCounter] =modelAndSimulation(noc, maxApps, classSizes, safetySchoolSize, nStudents, nSimulationRuns, pfi, safetySchool, printModelResults, printSimResults, printAverageSimResults);
			//System.out.println("");
		}
		
		// Average results across different preference runs
		for(int preferenceRun=0; preferenceRun<numberOfPreferencesGenerated; preferenceRun++)
		{
			for(int counter=0; counter<noc; counter++)
			{
				averageResult.acceptanceRates[counter] += runResults[preferenceRun].acceptanceRates[counter];
			}
		}
		for(int counter=0; counter<noc; counter++)
			averageResult.acceptanceRates[counter] /= ((float) numberOfPreferencesGenerated);
		
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
		for(int counter=0; counter<noc; counter++)
		{
			Integer key = counter+1;
			String collegeName = ht.get(key).getName();
			System.out.println("The average acceptance rate for " + collegeName + " is: " + 
			   Math.round( averageResult.acceptanceRates[counter] ) + "%" );
		}
		System.out.println("");
		
		StringBuffer sbr2 = new StringBuffer();
		
		for(int round=1; round<=maxApps; round++)
		{
					int selectionRoundIndex = round-1;
					sbr2.append( Math.round(averageResult.averageMatriculationRateByRound[selectionRoundIndex]) + 
							"% of students matriculated to their " + round + " choice school\n");
		}
				
		sbr2.append("Average number of unassigned students per 10,000 students is " + 
				   Math.round( ( (float) averageResult.nUnassigned ) / ( 1  ) ) + "\n");
		sbr2.append("Average acceptance rate: " + Math.round(averageResult.overallAcceptanceRate*100) + "%\n");
		
		// Write results to a file
		String fileName = "CollegeMatchResults" + System.currentTimeMillis() + ".txt";
		File file = new File(fileName);
        FileWriter fr = null;
        StringBuffer sbr = new StringBuffer();
        sbr.append("Results for " + noc + " colleges, " + maxApps + " applications/student and " 
        		+ numberOfPreferencesGenerated + " preferences generated.");
        sbr.append("\nCollege data: \n");
        sbr.append("\nCollege name, class size, weight, average acceptance rate\n");
        
        for(int counter=0; counter<noc; counter++)
        {
        	sbr.append(collegeNames[counter] + ", " + collegeSizes[counter] + ", " + 
               collegeWeightsRaw[counter] + ", " + Math.round( averageResult.acceptanceRates[counter] ) + "%\n");
        }
        String data = sbr.toString() + "\n" + sbr2.toString();
        System.out.println(sbr2.toString());
        try 
        {
            fr = new FileWriter(file);
            fr.write(data);
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        finally
        {
            //close resources
            try 
            {
                fr.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
	}
	
	public static SimulationResult modelAndSimulation(int numberOfColleges, int maxApps, int [] classSizes, int safetySchoolSize, int nStudents, int nSimulationRuns, PreferencesFactoryI pfi,
			 								boolean safetySchool, boolean printModelResults, boolean printSimResults, boolean printAverageSimResults)
	{	
		// Create colleges
		Student [] students = new Student[nStudents];
		College [] colleges;
		if(safetySchool)
			colleges = runModelSafety(students, numberOfColleges, nStudents, maxApps, classSizes, safetySchoolSize, pfi);
		else
			colleges = runModel(students, numberOfColleges, nStudents, maxApps, classSizes, pfi);
		
		if(printModelResults)
			printModelResults(students);
		
		return runSimulation(students, colleges, maxApps, nSimulationRuns, printSimResults, printAverageSimResults);
	}
	
	private static College [] runModel(Student [] students, int numberOfColleges, int nofStudents, int maxApplications, int [] classSizes, PreferencesFactoryI pfi)
	{
		College [] colleges = new College [numberOfColleges];
		for (int counter=0; counter<numberOfColleges; counter++)
		{
		   colleges[counter] = new College(counter+1, classSizes[counter], maxApplications);
		}
		
		// Create Students
		for(int counter=0; counter< nofStudents; counter++)
			students[counter] = new Student(counter+1, pfi, numberOfColleges, maxApplications);
		
		// Apply to Colleges
		for(int counter=0; counter<nofStudents; counter++)
			students[counter].apply(colleges);
		
		for(int j=1; j<=maxApplications; j++)
		{
			for(int k=0; k<numberOfColleges; k++)
				colleges[k].processApplicationsForModel(j);
		}
		return colleges;
	}
	
	private static College [] runModelSafety(Student [] students, int numberOfColleges, int nofStudents, int maxApplications, int [] classSizes, int safetySchoolSize, PreferencesFactoryI pfi)
	{
		College [] colleges = new College [numberOfColleges];
		for (int counter=0; counter<numberOfColleges; counter++)
		{
		   if( counter == (numberOfColleges-1))
			   colleges[counter] = new College(counter+1, safetySchoolSize, maxApplications);
		   else
			   colleges[counter] = new College(counter+1, classSizes[counter], maxApplications);
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
		SimulationResult averageResult = new SimulationResult(maxApplications, colleges.length);
		SimulationResult [] simResults = new SimulationResult[nSimulationRuns];
		for(int simRunner=0; simRunner<nSimulationRuns; simRunner++)
		{
			simResults[simRunner] = new SimulationResult(maxApplications, colleges.length);
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
			simulationResults(students, colleges, maxApplications, simResults[simRunner], printSimResults);	
			
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
		
		// Acceptance rates averaged across simulation runs
		for(int simRun=0; simRun<nSimulationRuns; simRun++)
		{
			for(int counter=0; counter<colleges.length; counter++)
			{
				averageResult.acceptanceRates[counter] += simResults[simRun].acceptanceRates[counter];
			}
		}
		for(int counter=0; counter<colleges.length; counter++)
		{
			//NO float divisor = ( (float) nSimulationRuns) * ( (float) colleges[counter].getTotalNumberOfApplicants());
			float divisor = ( (float) nSimulationRuns);
			//NO divisor /= ((float) 100.0); // convert to percent
			averageResult.acceptanceRates[counter] /= divisor;
		}
		
				
		averageResult.nUnassigned = (int) Math.round( ( Student.ONE * ((double) averageResult.nUnassigned) ) / ( (double) nSimulationRuns * students.length ) );
		averageResult.overallAcceptanceRate /= ((double) nSimulationRuns);
		
		
		if(printAverageSimResults)
		{
			System.out.println("\nAveraging results across simulation runs: ");
			
			for(int counter=0; counter<colleges.length; counter++)
				System.out.println("The acceptance rate for college " + (counter+1) + " is: " + averageResult.acceptanceRates[counter]);
			
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
	
	private static void simulationResults(Student [] students, College [] colleges, int maxApplications, SimulationResult sr, boolean printSimResults)
	{
		for(int counter=0; counter<colleges.length; counter++)
		{
			StringBuffer sb = colleges[counter].printSimulationResults();
			if(printSimResults)
				System.out.println(sb.toString());
		}
			
		for(int counter=0; counter<colleges.length; counter++)
		{
			StringBuffer sb = colleges[counter].stats();
			if(printSimResults)
				System.out.println(sb.toString());
		}
		
		// Find unassigned students
		sr.nUnassigned = 0;
		for(int counter=0; counter<students.length; counter++)
		{
			if(!students[counter].getSimulationPicked())
				sr.nUnassigned++;
		}
		
		// Calculate overall acceptance rate and save individual acceptance rates from the CollegeSimulationResult to the SimulationResult
		// For now, do this as a weighted average
		long weightedSum = 0;
		int numberToAverage = 0;
		for(int counter=0; counter<colleges.length; counter++)
		{
			sr.acceptanceRates[counter] =  colleges[counter].sr.acceptanceRate;
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
