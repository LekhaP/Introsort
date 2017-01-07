import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Sorting {

	static Iterator<ArrayList<String>> iterator;
	static int thresholdForSize = 16;
	static Integer numberOfAddress;
	static ArrayList<ArrayList<String>> allAddresses;
	static int firstName = 0;
	static int lastName = 1;
	static int streetAddress = 2;
	static int city = 3;
	static int state = 4;
	static int zipCode = 5;
	static int sortByField = 0;
	static String nameSortedRecords_filename = "name_output.txt";
	static String addressSortedRecords_filename = "address_output.txt";
	static String timetaken_nameSortedRecords_filename = "name_output_time.txt";
	static String timetaken_addressSortedRecords_filename = "address_output_time.txt";
	static long startTime,endTime,durationInNanoSeconds,durationInMicroSeconds = 0;

	
	public static void main(String[] args) throws IOException {

		allAddresses = new ArrayList<ArrayList<String>>();
		clearAllPreviousOutputs();

		ArrayList<ArrayList<String>> tempAllAddresses = new ArrayList<ArrayList<String>>();
		
		String filename = "input100.txt";
		if(args.length > 0)
			filename = args[0];
		
		ArrayList<ArrayList<String>> inputFromFile = getAddressesFromInputFile("input_unsorted100.txt");//(filename);
		if(inputFromFile.isEmpty())
			return;
		
		numberOfAddress = Integer.parseInt(inputFromFile.get(0).get(0));      
    	tempAllAddresses.addAll(inputFromFile.subList(1, inputFromFile.size()));
		
		for(int i=0;i<numberOfAddress;i++)
		{
    		allAddresses.add(i, tempAllAddresses.get(i));
			sortRecordsByName();
		}
		writeSortedAddressToFile(nameSortedRecords_filename);
		
		allAddresses.clear();
		
		ArrayList<ArrayList<String>> inputFromNameFile = getAddressesFromInputFile(nameSortedRecords_filename);
    	ArrayList<ArrayList<String>> tempAllNameSortedAddresses = new ArrayList<ArrayList<String>>();
    	tempAllNameSortedAddresses.addAll(inputFromNameFile.subList(0, inputFromNameFile.size()));

		for(int i=0;i<numberOfAddress;i++)
		{
    		allAddresses.add(i, tempAllNameSortedAddresses.get(i));
			sortRecordsByAddress();
		}
		writeSortedAddressToFile(addressSortedRecords_filename);
	}

	public static void clearAllPreviousOutputs()
	{
		File file1 = new File(nameSortedRecords_filename);
		File file2 = new File(addressSortedRecords_filename);
		File file3 = new File(timetaken_nameSortedRecords_filename);
		File file4 = new File(timetaken_addressSortedRecords_filename);

		file1.delete();
		file2.delete();
		file3.delete();
		file4.delete();
	}
	
	public static ArrayList<ArrayList<String>> getAddressesFromInputFile(String fileName) throws FileNotFoundException,IOException 
	{
		FileInputStream fileInputStream = new FileInputStream(fileName);
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
		String strLine ;
		int i=0;

		ArrayList<ArrayList<String>> arrayOfAddresses = new ArrayList<ArrayList<String>>();
		
		while ((strLine = bufferedReader.readLine()) != null && strLine.length()!=0)
		{              
	   		int j = 0;

            StringTokenizer st = new StringTokenizer(strLine, "|");  
           	ArrayList<String> strArr = new ArrayList<String>();
           	while (st.hasMoreElements()) {
           		String element = ((String)st.nextElement());

                if(j==0) {
           			j++;
                    StringTokenizer tokenizer = new StringTokenizer(element, " "); 
                    while (tokenizer.hasMoreElements()) 
                    {
                    	String subElement = ((String)tokenizer.nextElement());
                    	strArr.add(subElement);
                    }                		
                }
                else {
            		strArr.add(element);
                }
           	}
           	arrayOfAddresses.add(i, strArr);
            i++;
		}
		
		dataInputStream.close();
		return arrayOfAddresses;
	}
	
	public static void writeSortedAddressToFile(String fileName) throws FileNotFoundException,IOException 
	{
		try {
    		File file =new File(fileName);
     		if(!file.exists()){
    			file.createNewFile();
    		}

    		FileWriter fileWritter = new FileWriter(file.getName());
    	    BufferedWriter output = new BufferedWriter(fileWritter);
			for(int j=0;j<allAddresses.size();j++) {
				for(int k=0;k<allAddresses.get(j).size();k++) {
					String data = allAddresses.get(j).get(k);
					output.append(data);
					
					if( k == 0)
						output.append(" ");
					else if (k==1 || k==2 || k==3 || k==4)
						output.append("|");

				}
    		    output.newLine();
			}
			output.close();
     	}
		catch(IOException e) { 
    		e.printStackTrace();
    	}		
	}

	public static void writeDataToFile(String data, String fileName) throws FileNotFoundException,IOException 
	{
		try {
    		File file =new File(fileName);
     		if(!file.exists()){
    			file.createNewFile();
    		}

    		FileWriter fileWritter = new FileWriter(file.getName(),true);
    	    BufferedWriter output = new BufferedWriter(fileWritter);
			output.append(data);
    		output.newLine();
			output.close();
     	}
		catch(IOException e) { 
    		e.printStackTrace();
    	}
		
	}

	public static void sortRecordsByName() throws FileNotFoundException, IOException
	{
		startTime = endTime = durationInNanoSeconds = 0;
		sortByField = lastName;
		sortAddressByLastName();			
		durationInNanoSeconds = (endTime - startTime);
		
		System.out.println(durationInNanoSeconds);
		ArrayList<Integer> groupIndexes = findIndexesForSubGroupWithCommonLastName();
		
		startTime = endTime = 0;
		sortByField = firstName;
		sortSubGroupsByFirstName(groupIndexes);
		durationInNanoSeconds += (endTime - startTime);
		
		durationInMicroSeconds = (long) (durationInNanoSeconds*0.001);
		System.out.println(durationInNanoSeconds);

		String recordsVsDuration = new String (durationInMicroSeconds+","+allAddresses.size());
		writeDataToFile(recordsVsDuration, timetaken_nameSortedRecords_filename);
	}
	
	public static void sortRecordsByAddress() throws FileNotFoundException, IOException
	{		
		startTime = endTime = durationInNanoSeconds = 0;
		sortByField = state;
		sortAddressByState();
		durationInNanoSeconds = (endTime - startTime);		

		startTime = endTime = 0;
		sortByField = city;
		sortAddressByCity();
		durationInNanoSeconds += (endTime - startTime);
			
		startTime = endTime = 0;
		sortByField = zipCode;
		sortAddressByZipcode();
		durationInNanoSeconds += (endTime - startTime);
		
		startTime = endTime = 0;
		sortByField = streetAddress;
		sortAddressByStreetAddress();
		durationInNanoSeconds += (endTime - startTime);
		
		durationInMicroSeconds = (long) (durationInNanoSeconds*0.001);
		String recordsVsDuration = new String (durationInMicroSeconds +","+allAddresses.size());
		writeDataToFile(recordsVsDuration, timetaken_addressSortedRecords_filename);

	}
	
	public static void sortAddressByLastName() 
	{
		ArrayList<ArrayList<String>> tempAddressArray = new ArrayList<ArrayList<String>>();

		for(int i=1;i<=allAddresses.size();i++)
		{	
			tempAddressArray.clear();
			tempAddressArray.addAll(allAddresses.subList(0, i));
			if(tempAddressArray.size() == 1)continue;
			
			startTime += System.nanoTime();			
			sortAddresses(tempAddressArray);
			endTime += System.nanoTime();

			for(int j=0;j<tempAddressArray.size();j++)
				allAddresses.set(j, tempAddressArray.get(j));
		}
	}

	public static ArrayList<Integer> findIndexesForSubGroupWithCommonLastName() 
	{
		String key = allAddresses.get(0).get(lastName);
		ArrayList<Integer> groupIndexes = new ArrayList<Integer>();
		groupIndexes.add(0);

		for(int i=1;i<allAddresses.size();i++)
		{
			if(key.compareTo(allAddresses.get(i).get(lastName)) == 0)
				continue;
			else
			{
				key = allAddresses.get(i).get(lastName);
				groupIndexes.add(i);
			}
		}
		return groupIndexes;
	}

	public static void sortSubGroupsByFirstName(ArrayList<Integer> groupIndexes) 
	{
		ArrayList<ArrayList<String>> tempAddressArray = new ArrayList<ArrayList<String>>();

		for(int i=0;i<groupIndexes.size();i++)
		{
			int begin,end = 0;
			begin = groupIndexes.get(i);
			if(i==groupIndexes.size()-1)
				end = allAddresses.size();
			else
				end = groupIndexes.get(i+1);
		
			if(end-begin>0)
			{
				tempAddressArray.clear();
				tempAddressArray.addAll(allAddresses.subList(begin, end));
				
				if(tempAddressArray.size()>=1)
				{
					startTime += System.nanoTime();
					sortAddresses(tempAddressArray);
					endTime += System.nanoTime();
					
					int k=0;
					for(int j=begin;j<end;j++)
					{
						allAddresses.set(j, tempAddressArray.get(k));
						k++;
					}
				}
			}			
		}
	}
	
	public static void sortAddressByState() 
	{
		ArrayList<ArrayList<String>> tempArray = new ArrayList<ArrayList<String>>();
		String curName,preName;
		
		curName = preName = allAddresses.get(0).get(firstName)+allAddresses.get(0).get(lastName);

		for(int i=0;i<allAddresses.size();i++)
		{	
			if(tempArray.isEmpty())
			{
				curName = preName = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName);	
				tempArray.add(allAddresses.get(i));
			}
			else
			{
				curName = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName);
				if(preName.equalsIgnoreCase(curName))
				{
					tempArray.add(allAddresses.get(i));
					
					startTime += System.nanoTime();			
					sortAddresses(tempArray);
					endTime += System.nanoTime();			
	
					for(int j = i-tempArray.size()+1,k=0;j<=i && k<tempArray.size();j++,k++){
						allAddresses.set(j, tempArray.get(k));
					}
				}
				else
				{
					curName = preName = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName);	
					tempArray.clear();
					tempArray.add(allAddresses.get(i));
				}
			}
		}
	}
	
	public static void sortAddressByCity()
	{
		ArrayList<ArrayList<String>> tempArray = new ArrayList<ArrayList<String>>();
		String curFnameLnameState,preFnameLnameState;
		curFnameLnameState = preFnameLnameState = allAddresses.get(0).get(firstName)+allAddresses.get(0).get(lastName)+allAddresses.get(0).get(state);

		
		for(int i=0;i<allAddresses.size();i++)
		{	
			if(tempArray.isEmpty())
			{
				curFnameLnameState = preFnameLnameState = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state);
				tempArray.add(allAddresses.get(i));
			}
			else
			{
				curFnameLnameState = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state);
				if(preFnameLnameState.equalsIgnoreCase(curFnameLnameState))
				{
					tempArray.add(allAddresses.get(i));
					
					startTime += System.nanoTime();			
					sortAddresses(tempArray);
					endTime += System.nanoTime();			

					for(int j = i-tempArray.size()+1,k=0;j<=i && k<tempArray.size();j++,k++)
						allAddresses.set(j, tempArray.get(k));
				}
				else
				{
					curFnameLnameState = preFnameLnameState = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state);
					tempArray.clear();
					tempArray.add(allAddresses.get(i));
				}
			}
		}
	}
	
	public static void sortAddressByZipcode()
	{
		ArrayList<ArrayList<String>> tempArray = new ArrayList<ArrayList<String>>();
		String curFnameLnameStateCity,preFnameLnameStateCity;
		curFnameLnameStateCity = preFnameLnameStateCity = allAddresses.get(0).get(firstName)+allAddresses.get(0).get(lastName)+allAddresses.get(0).get(state)+allAddresses.get(0).get(city);

		
		for(int i=0;i<allAddresses.size();i++)
		{	
			if(tempArray.isEmpty())
			{
				curFnameLnameStateCity = preFnameLnameStateCity = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state)+allAddresses.get(i).get(city);
				tempArray.add(allAddresses.get(i));
			}
			else
			{
				curFnameLnameStateCity = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state)+allAddresses.get(i).get(city);
				if(preFnameLnameStateCity.equalsIgnoreCase(curFnameLnameStateCity))
				{
					tempArray.add(allAddresses.get(i));
					
					startTime += System.nanoTime();			
					sortAddresses(tempArray);
					endTime += System.nanoTime();			

					for(int j = i-tempArray.size()+1,k=0;j<=i && k<tempArray.size();j++,k++)
						allAddresses.set(j, tempArray.get(k));
				}
				else
				{
					curFnameLnameStateCity = preFnameLnameStateCity = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state)+allAddresses.get(i).get(city);
					tempArray.clear();
					tempArray.add(allAddresses.get(i));
				}
			}
		}
	}
	
	public static void sortAddressByStreetAddress()
	{
		ArrayList<ArrayList<String>> tempArray = new ArrayList<ArrayList<String>>();
		String curNameStateCityZipcode,preNameStateCityZipcode;
		curNameStateCityZipcode = preNameStateCityZipcode = allAddresses.get(0).get(firstName)+allAddresses.get(0).get(lastName)+allAddresses.get(0).get(state)+allAddresses.get(0).get(city)+allAddresses.get(0).get(zipCode);

		
		for(int i=0;i<allAddresses.size();i++)
		{	
			if(tempArray.isEmpty())
			{
				curNameStateCityZipcode = preNameStateCityZipcode = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state)+allAddresses.get(i).get(city)+allAddresses.get(i).get(zipCode);
				tempArray.add(allAddresses.get(i));
			}
			else
			{
				curNameStateCityZipcode = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state)+allAddresses.get(i).get(city)+allAddresses.get(i).get(zipCode);
				if(preNameStateCityZipcode.equalsIgnoreCase(curNameStateCityZipcode))
				{
					tempArray.add(allAddresses.get(i));
					
					startTime += System.nanoTime();			
					sortAddresses(tempArray);
					endTime += System.nanoTime();			

					for(int j = i-tempArray.size()+1,k=0;j<=i && k<tempArray.size();j++,k++)
						allAddresses.set(j, tempArray.get(k));
				}
				else
				{
					curNameStateCityZipcode = preNameStateCityZipcode = allAddresses.get(i).get(firstName)+allAddresses.get(i).get(lastName)+allAddresses.get(i).get(state)+allAddresses.get(i).get(city)+allAddresses.get(i).get(zipCode);
					tempArray.clear();
					tempArray.add(allAddresses.get(i));
				}
			}
		}
	}

	public static void sortAddresses(ArrayList<ArrayList<String>> addressArray)
	{
		introsortArrayOfAddresses(addressArray, 0, addressArray.size(), 2*getTheDepthLimit(addressArray.size()));
	}
	
	public static void sortAddresses(ArrayList<ArrayList<String>> addressArray, int begin, int end)
	{
	   if (begin < end)
	   {
			introsortArrayOfAddresses(addressArray, begin,end, 2*getTheDepthLimit(end-begin));
	   }
	}
	
	public static int getTheDepthLimit(int noOfAddress)
	 {
		int depth = (int)(Math.floor(Math.log(noOfAddress)/Math.log(2)));
		return depth;
	 }
	
	public static void introsortArrayOfAddresses (ArrayList<ArrayList<String>> addressArray, int low, int high, int depthLimit)
	{
		if(addressArray.size()<=1)
			return;

		while(high-low>thresholdForSize)
		{
			if(depthLimit == 0)
			{
				heapSortArrayOfAddress(addressArray,low,high);
				return;
			}
		    depthLimit = depthLimit - 1;
		    ArrayList<String> medianAddress = medianofLowMidHigh(addressArray,low,low+((high-low)/2)+1, high-1);	    
		    int pivot = partitionTheAddressArray(addressArray, low, high, medianAddress);
		    introsortArrayOfAddresses(addressArray,pivot,high,depthLimit);
		    high = pivot;
		}
		insertionsortAddressArray(addressArray,low,high);
	 }

	public static void insertionsortAddressArray(ArrayList<ArrayList<String>> addressArray, int low, int high)
	{
		int i,j;
		for(i=low+1;i<high;i++)
		{
			ArrayList<String> key = addressArray.get(i);
			j = i-1;
			while(j>=low && (key.get(sortByField).compareTo(addressArray.get(j).get(sortByField))) <0)
			{
				addressArray.set(j+1, addressArray.get(j));
				j=j-1;
			}
			addressArray.set(j+1,key);	
		}
	}

	public static ArrayList<String> medianofLowMidHigh(ArrayList<ArrayList<String>> addressArray, int low, int mid, int high)
	{
		String lowElement = addressArray.get(low).get(sortByField);
		String midElement = addressArray.get(mid).get(sortByField);
		String highElement = addressArray.get(high).get(sortByField);
		
		if (midElement.compareTo(lowElement) < 0)
		{
			if (highElement.compareTo(midElement) < 0)
				return addressArray.get(mid);
			else
			{
				if (highElement.compareTo(lowElement) < 0)
					return addressArray.get(high);
				else
					return addressArray.get(low);
			}
		}
		else
		{
			if (highElement.compareTo(midElement) < 0)
			{
				if (highElement.compareTo(lowElement) < 0)
					return addressArray.get(low);
				else
					return addressArray.get(high);
			}
			else
				return addressArray.get(mid);
		}		
	}

	private static int partitionTheAddressArray(ArrayList<ArrayList<String>> addressArray, int low, int high, ArrayList<String> medianAddress)
	{
		int i = low, j = high;
		while(true)
		{
			while((addressArray.get(i).get(sortByField).compareTo(medianAddress.get(sortByField)))<0) i++;
			j--;
			while((medianAddress.get(sortByField).compareTo(addressArray.get(j).get(sortByField)))<0) j--;
			if(i>=j) return i;
			swapAddresses(addressArray,i,j);
			i++;
		}
	}

	public static void swapAddresses(ArrayList<ArrayList<String>> addressArray, int i, int j)
	{	 
		ArrayList<String> tempList = addressArray.get(i);
		addressArray.set(i, addressArray.get(j));
		addressArray.set(j, tempList);
	}

	public static void heapSortArrayOfAddress(ArrayList<ArrayList<String>> addressArray, int low, int high)
	{
	   int n = high-low;
	   for (int i=n/2; i>=1; i--)
	   {
		   maxHeap(addressArray,i,n,low);
	   }
	   for (int i=n; i>1; i--)
	   {
		   swapAddresses(addressArray,low,low+i-1);
		    maxHeap(addressArray,1,i-1,low);
	   }
	}
	
	public static void maxHeap(ArrayList<ArrayList<String>> addressArray, int i, int n, int low)
	{
		ArrayList<String> rightChild = addressArray.get(low+i-1);
		int leftChild;
		while (i<=n/2)
		{
			leftChild = 2*i;
			if (leftChild<n  &&  (addressArray.get(low+leftChild-1).get(sortByField)).compareTo(addressArray.get(low+leftChild).get(sortByField))<0)
				leftChild++;
		    if ((rightChild.get(sortByField).compareTo(addressArray.get(low+leftChild-1).get(sortByField)))>=0) 
		    	break;
		    addressArray.set(low+i-1, addressArray.get(low+leftChild-1));
		    i = leftChild;
		}
		addressArray.set(low+i-1, rightChild);
	}	
}