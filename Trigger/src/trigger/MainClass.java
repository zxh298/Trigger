package trigger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainClass {

	public static void main(String[] args) throws IOException {
		// This is the main class of the whole project.
		// All results will be printed in console window.

		int totalNumberOfTriggersFound = 0;

		/**
		 *     variable number      structures number                    structure number                 number of triggers
		 *                          (without disconnected variables)     (with disconnected variables)                                 
		 *           3                          4                                 6                               0
		 *           4                          24                                31                              2
		 *           5                          268                               302                             57
		 *           6                          5667                              5984                            2525
		 * */

		System.out.println("Please specify the number of variables:");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String str = br.readLine();

		// to store all correct un-repeated hidden models:
		ArrayList<int[][]> finalUnrepeatedHiddenModels = new ArrayList<int[][]>();
		
		int observedVariableNum = Integer.valueOf(str);

		// whether consider DAG with disconnected nodes.
		boolean allow_disconnected_nodes = true;
		
		long startTime = System.nanoTime();
		
		DAGList dl = new DAGList(observedVariableNum);

		dl.makeMatrixList(allow_disconnected_nodes);

		//Generate all possible DAGs for the current variable number:
		ArrayList<int[][]> observedDAGList = dl.getResultList();
		//dl.print();

		System.out.println("Total number of observed DAGs: " + observedDAGList.size());

		// Get all dependency matrices of every label combination of every observed DAG, and put them together:
	        ArrayList<int[]> allObservedDAGsDependencyMaticesValues = new ArrayList<int[]>();

		
		for(int[][] currentObservedDAG : observedDAGList)
		{

			GetFullLablesCombinationList gc = new GetFullLablesCombinationList(currentObservedDAG, false);

			ArrayList<int[][]> allObservedLabelsCombinationDAGList = gc.getLablesCombinationDAGList();

			for(int[][] currentDAG : allObservedLabelsCombinationDAGList)
			{
				GenerateAllDependencies gd = new GenerateAllDependencies(currentDAG, observedVariableNum, false);
				ArrayList<int[][]> dependencyMatricesOfCurrentObservedDAG = gd.getResult();
//				allObservedDAGsDependencyMatices.add(dependencyMatricesOfCurrentObservedDAG);
				
				ComputeMatrixValue cm = new ComputeMatrixValue(dependencyMatricesOfCurrentObservedDAG);
				int[] dependencyMatricesValuesOfCurrentObservedDAG = cm.getMatrixValues();
				allObservedDAGsDependencyMaticesValues.add(dependencyMatricesValuesOfCurrentObservedDAG);
			}

		}
        
		for(int t=0; t<observedDAGList.size(); t++)
		{	    	
			
			int[][] currentObservedDAG = observedDAGList.get(t);
			
			// For the current observed DAG, generate all possible DAG which contain a single hidden variable
			// (replacing one arc by a hidden common cause):
			GenerateHiddenVarDAGList.varNum = observedVariableNum;

			ArrayList<int[][]> hiddenModelOfCurrenObservedDAG = GenerateHiddenVarDAGList.makeHiddenMatrix(currentObservedDAG);

			// By default, we believe the dependency relationships among current observed DAG and 
			// corresponding hidden DAG are same:
			boolean isSame = true; 
                        int falseNum = 0;
			// For each hidden DAG, compare its dependency matrices with the current observed DAG's dependency
			// matrices:
			for(int[][] currentHiddenModel : hiddenModelOfCurrenObservedDAG)
			{
				// to store the number of label combination structures which have different 
				// dependency structures as the current candidate hidden model: 
				falseNum = 0;
				
				// Get all label combination DAGs of current hidden DAG:
				GetFullLablesCombinationList gcHidden = new GetFullLablesCombinationList(currentHiddenModel, true);
				ArrayList<int[][]> allHiddenLabelsCombinationDAGList = gcHidden.getLablesCombinationDAGList();
                								
				for(int[][] currentHiddenDAG : allHiddenLabelsCombinationDAGList)
				{
					isSame = true;
					GenerateAllDependencies ghd = new GenerateAllDependencies(currentHiddenDAG, observedVariableNum+1, true);
					ArrayList<int[][]> dependencyMatricesOfCurrentHiddenDAG = ghd.getResult();
					CompareHiddenAndObservedDependencyMatrices compare = new CompareHiddenAndObservedDependencyMatrices(dependencyMatricesOfCurrentHiddenDAG, 
							allObservedDAGsDependencyMaticesValues, observedVariableNum);
				    
					isSame = compare.hiddenAndObeservedDependencySame();
					
					if(isSame == false)
						falseNum++;
				}


				// Get all label combination DAGs of observed model which has same number of variables as 
				// the current candidate hidden model:
				int[] firstLabel = new int[observedVariableNum + 1];
				for(int i = 0; i < firstLabel.length; i++)
				{
					firstLabel[i] = i;
				}
				
				GetFullLablesCombinationList gcObserved = new GetFullLablesCombinationList(firstLabel,observedVariableNum+1);
				ArrayList<int[]> allLabelCombinations = gcObserved.getLabelsCombinationResult();
				
				
				//****************************************************
				// If there is even one dependency values in the two matrices are different, then 
				// the current hidden DAG provide a different dependency structure in terms of D-Separation:
				if(falseNum == allHiddenLabelsCombinationDAGList.size())
				{									
					boolean hasSameStructure = false;
					// to check whether the current candidate structure is already found before: 
					for(int[][] currentStoredHiddenModel: finalUnrepeatedHiddenModels)
					{
						for(int[] currentLabelCombination : allLabelCombinations)
						{
							int unitSame = 0;
							for(int n = 0; n < currentHiddenModel.length; n++)
							{
								for(int m = 0; m < currentHiddenModel[n].length; m++)
								{
									if(currentStoredHiddenModel[currentLabelCombination[n]][currentLabelCombination[m]] == currentHiddenModel[n][m])
									{
										unitSame++;
									}
								}
							}
							
							if(unitSame == (observedVariableNum+1) * (observedVariableNum+1))
							{
								hasSameStructure = true;
								break;
							}
						}
					}
					
					// if the current candidate hidden model has a new structure:
					if(hasSameStructure == false)
					{
						finalUnrepeatedHiddenModels.add(currentHiddenModel);
						
						totalNumberOfTriggersFound++;
                        						
						System.out.println("*********************************");
						System.out.println("Candidate hidden model:");

						for(int n = 0; n < currentHiddenModel.length; n++)
						{
							for(int m = 0; m < currentHiddenModel[n].length; m++)
							{
								System.out.print(currentHiddenModel[n][m] + " ");
							}

							System.out.println();
						}
					
						System.out.println();

						System.out.println("*********************************");
					}									
				
				}	

			}
			
//            double perc = (double) t/observedDAGList.size();
//            System.out.println(String.format("%.2f", perc*100) + "% completed.");

		}

		
		long endTime = System.nanoTime();
		float duration_seconds = (float) (endTime - startTime) / 1000000000;
		float duration_minutes = (float) duration_seconds / 60;
		float duration_hours = (float) duration_minutes / 60;
		
		System.out.println("*********************");
		System.out.println("There are " + totalNumberOfTriggersFound + " of triggers found.");
		System.out.println("Total running time: " + duration_seconds + " secs");
		System.out.println("Total running time: " + duration_minutes + " mins");			
		System.out.println("Total running time: " + duration_hours + " hours");
		System.out.println("*********************");

	}

}
