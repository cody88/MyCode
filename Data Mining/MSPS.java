import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MSPS {
	
	private static Vector<PatternSequence> seqdb;
	public static ArrayList<Double> mis;
	private static double SDC;
	private static HashMap<Integer, Integer> supCounts;
	private static Integer[] sortedFreqItems;
	public static Integer underscoreInteger = -99;
	public static HashMap<Integer, Vector<PatternSequence>> results;
	
	private static void readParaFile() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					"/home/cody88/eclipse-jee-workspace/MSPS/para.txt"));
		} catch (FileNotFoundException e1) {
			System.out.println("Could not open para file.");
			e1.printStackTrace();
			System.exit(1);
		}
		String line;
		
		mis = new ArrayList<Double>();
		SDC = 0;
		try {
			int count = 0;
			while ((line = reader.readLine()) != null) {
				String s[] = line.split("=");
				if(line.matches("MIS.*")) {
					String s2[] = line.split("\\(");
					String s1[] = s2[1].split("\\)");
					int item = Integer.parseInt(s1[0].trim());
					while(count++ < item-1)
						mis.add(0.0);
					mis.add(Double.parseDouble(s[1].trim()));
				}
				else
					SDC = Double.parseDouble(s[1].trim());
			}
			reader.close();
			/*for(int i=0; i<mis.size(); i++) {
				System.out.println("MIS["+(i+1)+"]="+mis.get(i));
			}
			System.out.println("SDC="+SDC);
			System.exit(0);*/
		} catch (IOException e) {
			System.out.println("IOException while reading para file.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void readDataFile() {
		seqdb = new Vector<PatternSequence>();
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(
					"/home/cody88/eclipse-jee-workspace/MSPS/data.txt"));
		} catch (FileNotFoundException e1) {
			System.out.println("Could not open data file.");
			e1.printStackTrace();
			System.exit(1);
		}
		
		String line;
		Pattern pattern = Pattern.compile("\\{[^\\{\\}]+\\}");
		try {
			while ((line = reader.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				PatternSequence seq = new PatternSequence();
				while (matcher.find()) {
					line = matcher.group();
					String s[] = line.substring(1, line.length()-1).split(",");
					Vector<Integer> vect = new Vector<Integer>();
					for(int i=0; i<s.length; i++) {
						vect.add(Integer.parseInt(s[i].trim()));
					}
					seq.addElement(vect);
				}
				//System.out.println(seq.toString());
				seqdb.add(seq);
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("IOException while reading data file.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void findAndSortFrequentItems() {
		supCounts = new HashMap<Integer, Integer>();
		
		// Calculating counts
		for(PatternSequence seq: seqdb) {
			HashSet<Integer> uniq = new HashSet<Integer>();
			for(Vector<Integer> vect: seq.getSequence()) {
				for(Integer item: vect) {
					uniq.add(item);
				}
			}
			for(Integer item: uniq) {
				Integer count = null;
				if((count = supCounts.get(item)) != null)
					supCounts.put(item, count+1);
				else
					supCounts.put(item, 1);
			}
		}
		// Removing infrequent items
		ArrayList<Integer> freqs = new ArrayList<Integer>();
		for(Integer key: supCounts.keySet()) {
			if(((double)supCounts.get(key)/seqdb.size()) >= mis.get(key-1))
				freqs.add(key);
		}
		sortedFreqItems = freqs.toArray(new Integer[1]);
		
		// Sorting according to MIS values
		Comparator<Integer> comp = new Comparator<Integer>() {
			public int compare(Integer item1, Integer item2) {
				if(MSPS.mis.get(item1 - 1) < MSPS.mis.get(item2 - 1))
					return -1;
				else if(MSPS.mis.get(item1 - 1) > MSPS.mis.get(item2 - 1))
					return 1;
				else
					return 0;
			}
		};
		Arrays.sort(sortedFreqItems, comp); // O(nlgn) stable Mergesort
		/*System.out.print("Step 2 of MSPS: Overall SortedFreqItems...");
		for(int i=0; i<sortedFreqItems.length; i++)	System.out.print(sortedFreqItems[i]+" ");*/
	}
	
	public static void insertSequenceIntoResults(PatternSequence sequence) {
		// Store into results object
		Vector<PatternSequence> list = results.get(sequence.length());
		if(list == null) {
			list = new Vector<PatternSequence>();
			list.add(sequence);
			results.put(sequence.length(), list);
		}
		else {
			boolean exists = false;
			for(PatternSequence seq: list) {
				if(seq.toString().equals(sequence.toString())) {
					exists = true;
					break;
				}
			}
			if(!exists) {
				list.add(sequence);
				results.put(sequence.length(), list);
			}
		}
	}
	
	
	public static void prefixSpan(Integer freqItem, Vector<PatternSequence> Sk1,
			HashMap<Integer, Integer> supCountsK, int minsupCount, Integer[] freqItemsK,
			PatternSequence prefix) {
		if(prefix == null) {
			/* This means this is the first time calling prefixSpan(),
			 * hence, we take everything from freqItemsK[] as length-1 prefixes
			 */
			for(int i=0; i<freqItemsK.length; i++) {
				PatternSequence newprefix = new PatternSequence();
				Vector<Integer> vect = new Vector<Integer>();
				vect.add(freqItemsK[i]);
				newprefix.addElement(vect);
				if(freqItemsK[i] == freqItem)
					insertSequenceIntoResults(newprefix);
				prefixSpan(freqItem, Sk1, supCountsK, minsupCount, freqItemsK, newprefix);
			}
		}
		else {
			System.out.println("Working with Prefix " + prefix.toString() + "...");
			// First, find the projected db
			Vector<PatternSequence> SkProj = new Vector<PatternSequence>();
			for(PatternSequence seq: Sk1) {
				PatternSequence projSeq = seq.returnAfterPrefix(prefix);
				if(projSeq != null && !projSeq.isEmpty()) {
					// Remove non-frequent items and add to SkProj
					PatternSequence projSeq1 = new PatternSequence();
					for(Vector<Integer> vect: projSeq.getSequence()) {
						Vector<Integer> vect1 = new Vector<Integer>();
						for(Integer item: vect) {
							if(item == underscoreInteger || Arrays.binarySearch(freqItemsK, item) >= 0)
								vect1.add(item);
						}
						if(!vect1.isEmpty())
							projSeq1.addElement(vect1);
					}
					if(!projSeq1.isEmpty())
						SkProj.add(projSeq1);
				}
			}
			/*System.out.println("\nProjected DB for prefix "+prefix.toString()+" :");
			for(PatternSequence projSeq: SkProj) {
				System.out.println(projSeq.toString());
			}*/
			if(SkProj.isEmpty()) return;
			
			
			// Finally, match templates and generate sequential patterns
			// Firstly, find freq items out of projected db
			PatternSequence prefixLastItemset = new PatternSequence();
			prefixLastItemset.addElement(prefix.getSequence().lastElement());
			HashMap<Integer, Integer> supCountsKProj = new HashMap<Integer, Integer>();
			HashMap<Integer, Integer> supCountsKProjTempl = new HashMap<Integer, Integer>();
			for(PatternSequence seq: SkProj) {
				HashSet<Integer> uniq = new HashSet<Integer>();
				HashSet<Integer> uniqTempl = new HashSet<Integer>();
				for(Vector<Integer> vect: seq.getSequence()) {
					boolean addtoTempl = false;
					if(vect.get(0) == underscoreInteger) // For cases {_, x}
						addtoTempl = true;
					else {
						PatternSequence currentItemset = new PatternSequence();
						currentItemset.addElement(vect);
						PatternSequence ret = currentItemset.returnAfterPrefix(prefixLastItemset);
						if(ret != null && !ret.isEmpty()) {
							for(Integer item: ret.getSequence().get(0))  // There can be only one itemset
								if(item != underscoreInteger) uniqTempl.add(item);
						}
					}
					for(Integer item: vect) {
						if(item != underscoreInteger) {
							uniq.add(item);
							if(addtoTempl) uniqTempl.add(item);
						}	
					}
				}
				for(Integer item: uniq) {
					Integer count = null;
					if((count = supCountsKProj.get(item)) != null)
						supCountsKProj.put(item, count+1);
					else
						supCountsKProj.put(item, 1);
				}
				for(Integer item: uniqTempl) {
					Integer count = null;
					if((count = supCountsKProjTempl.get(item)) != null)
						supCountsKProjTempl.put(item, count+1);
					else
						supCountsKProjTempl.put(item, 1);
				}
			}
			HashMap<Integer, Integer> freqs = new HashMap<Integer, Integer>();
			for(Integer key: supCountsKProj.keySet()) {
				if(supCountsKProj.get(key) >= minsupCount)
					freqs.put(key, supCountsKProj.get(key));
				else
					supCountsKProjTempl.remove(key);
			}
			HashMap<Integer, Integer> freqsTempl = new HashMap<Integer, Integer>();
			for(Integer key: supCountsKProjTempl.keySet()) {
				if(supCountsKProjTempl.get(key) >= minsupCount)
					freqsTempl.put(key, supCountsKProjTempl.get(key));
				int diff = supCountsKProj.get(key) - supCountsKProjTempl.get(key);
				if(diff > 0)
					freqs.put(key, diff);
				else
					freqs.remove(key);
			}
			/* Finally, we have "freqs" containing items that'll form a new itemset
			 * and "freqsTempl" containing items that'll go into the last itemset
			 * of the prefix.
			 */
			Vector<PatternSequence> candidates = new Vector<PatternSequence>();
			for(Integer item: freqsTempl.keySet()) {
				if(true){//!prefix.containsItem(item)) {
					PatternSequence newprefix = new PatternSequence(prefix);
					newprefix.insertIntoLastItemset(item);
					// SDC check
					/*boolean SDC_violated = false;
					for(Vector<Integer> vect: newprefix.getSequence()) {
						for(Integer itemV: vect) {
							if( Math.abs( ((double)supCountsK.get(itemV)/Sk1.size()) -
									((double)supCountsK.get(freqItem)/Sk1.size()) ) > SDC) {
								SDC_violated = true;
								break;
							}
						}
						if(SDC_violated) break;
					}*/
					double maxSup = 0.0, minSup = 1.0;
					for(Vector<Integer> vect: newprefix.getSequence()) {
						for(Integer itemV: vect) {
							double supD = (double)supCounts.get(itemV)/seqdb.size();
							if(supD < minSup) minSup = supD;
							if(supD > maxSup) maxSup = supD;
						}
					}
					if(Math.abs(maxSup - minSup) <= SDC) {
						candidates.add(newprefix);
						if(newprefix.containsItem(freqItem)) {
							insertSequenceIntoResults(newprefix);
						}
					}
				}
			}
			for(Integer item: freqs.keySet()) {
				if(freqs.get(item) >= minsupCount){// && !prefix.containsItem(item)) {
					PatternSequence newprefix = new PatternSequence(prefix);
					Vector<Integer> vect1 = new Vector<Integer>();
					vect1.add(item);
					newprefix.addElement(vect1);
					// SDC check
					/*boolean SDC_violated = false;
					for(Vector<Integer> vect: newprefix.getSequence()) {
						for(Integer itemV: vect) {
							if( Math.abs( ((double)supCountsK.get(itemV)/Sk1.size()) -
									((double)supCountsK.get(freqItem)/Sk1.size()) ) > SDC) {
								SDC_violated = true;
								break;
							}
						}
						if(SDC_violated) break;
					}
					if(!SDC_violated) {*/
					double maxSup = 0.0, minSup = 1.0;
					for(Vector<Integer> vect: newprefix.getSequence()) {
						for(Integer itemV: vect) {
							double supD = (double)supCounts.get(itemV)/seqdb.size();
							if(supD < minSup) minSup = supD;
							if(supD > maxSup) maxSup = supD;
						}
					}
					if(Math.abs(maxSup - minSup) <= SDC) {
						candidates.add(newprefix);
						if(newprefix.containsItem(freqItem)) {
							insertSequenceIntoResults(newprefix);
						}
					}
				}
			}
			//System.out.println(candidates.size()+ " Candidates for recursion:-");
			for(PatternSequence seq: candidates) {
				//System.out.println(seq.toString());
				prefixSpan(freqItem, Sk1, supCountsK, minsupCount, freqItemsK, seq);
			}
		}
		
	}
	
	public static void rPrefixSpan(Integer freqItem, Vector<PatternSequence> Sk, int minsupCount) {
		Comparator<Integer> comp = new Comparator<Integer>() {
			public int compare(Integer item1, Integer item2) {
				if(item1 < item2)
					return -1;
				else if(item1 > item2)
					return 1;
				else
					return 0;
			}
		};
		
		// First, sort all items in each element
		Vector<PatternSequence> Sk1 = new Vector<PatternSequence>();
		for(PatternSequence seq: Sk) {
			PatternSequence seq1 = new PatternSequence();
			for(Vector<Integer> vect: seq.getSequence()) {
				Integer[] arr = new Integer[vect.size()]; int i = 0;
				for(Integer item: vect) arr[i++] = item;
				Arrays.sort(arr, comp); // O(nlgn) stable Mergesort
				Vector<Integer> vect1 = new Vector<Integer>();
				for(i=0; i<vect.size(); i++) vect1.add(arr[i]);
				seq1.addElement(vect1);
			}
			Sk1.add(seq1);
		}
		
		// Find all frequent items to have the Length-1 Sequential Patterns
		HashMap<Integer, Integer> supCountsK = new HashMap<Integer, Integer>();
		for(PatternSequence seq: Sk1) {
			HashSet<Integer> uniq = new HashSet<Integer>();
			for(Vector<Integer> vect: seq.getSequence()) {
				for(Integer item: vect) {
					uniq.add(item);
				}
			}
			for(Integer item: uniq) {
				Integer count = null;
				if((count = supCountsK.get(item)) != null)
					supCountsK.put(item, count+1);
				else
					supCountsK.put(item, 1);
			}
		}
		ArrayList<Integer> freqs = new ArrayList<Integer>();
		for(Integer key: supCountsK.keySet()) {
			if(supCountsK.get(key) >= minsupCount)
				freqs.add(key);
		}
		if(!freqs.isEmpty()) {
			Integer[] freqItemsK = freqs.toArray(new Integer[1]);
			Arrays.sort(freqItemsK, comp); // Needed for binarysearch later
			/*System.out.print(freqItemsK.length + " freq items from Sk:");
			for(int i=0; i<freqItemsK.length; i++)	System.out.print(freqItemsK[i]+" ");*/
			if(freqItemsK.length > 0) prefixSpan(freqItem, Sk1, supCountsK, minsupCount, freqItemsK, null);
		}
	}
	


	public static void main(String[] args) {
		readParaFile();
		readDataFile();
		findAndSortFrequentItems();
		Vector<PatternSequence> seqdb1 = new Vector<PatternSequence>();
		for(PatternSequence seq: seqdb) {
			seqdb1.add(new PatternSequence(seq));
		}
		results = new HashMap<Integer, Vector<PatternSequence>>();
		for(int i=0; i<sortedFreqItems.length; i++) {
			Vector<PatternSequence> Sk = new Vector<PatternSequence>();
			System.out.println("\ni="+sortedFreqItems[i]+"\n========");
			// Forming the (temp) new db Sk
			for(PatternSequence seq: seqdb) {
				if(seq.containsItem(sortedFreqItems[i])) {
					PatternSequence newseq = new PatternSequence();
					for(Vector<Integer> vect: seq.getSequence()) {
						Vector<Integer> newvect = new Vector<Integer>();
						for(Integer it: vect) {
							if(Math.abs( ((double)supCounts.get(it)/seqdb.size()) -
								((double)supCounts.get(sortedFreqItems[i])/seqdb.size()) ) <= SDC ) {
								newvect.add(it);
							}
						}
						if(!newvect.isEmpty())
							newseq.addElement(newvect);
					}
					if(!newseq.isEmpty())
						Sk.add(newseq);
				}
			}
			/*if(!Sk.isEmpty()) {
				System.out.println("Sk for i="+sortedFreqItems[i]+" :");
				for(PatternSequence seq: Sk) {
					System.out.println(seq.toString());
				}
			}*/
			
			// Calling r-PrefixSpan with Sk
			int minsupCount = (int)Math.ceil(mis.get(sortedFreqItems[i]-1) * seqdb.size());
			//System.out.println("minsupcount="+minsupCount);
			rPrefixSpan(sortedFreqItems[i], Sk, minsupCount);
			
			// Remove all occurrences of ik from S (i.e., seqdb)
			Sk = new Vector<PatternSequence>();
			for(PatternSequence seq1: seqdb) {
				PatternSequence seq2 = new PatternSequence();
				for(Vector<Integer> vect: seq1.getSequence()) {
					Vector<Integer> vect1 = new Vector<Integer>();
					for(Integer item: vect) {
						if(item != sortedFreqItems[i])
							vect1.add(item);
					}
					seq2.addElement(vect1);
				}
				Sk.add(seq2);
			}
			seqdb = null;  // Force GC
			seqdb = Sk;
		}
		
		// Write results into file
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
			          new FileOutputStream("results.txt"), "utf-8"));
			for(Integer count: results.keySet()) {
				Vector<PatternSequence> list = results.get(count);
				writer.write("The number of length "+count+" sequential patterns is "+list.size()+"\n");
				for(PatternSequence seq: list) {
					writer.write("Pattern: "+seq.toString()+" ");
					int count1 = 0;
					for(PatternSequence data: seqdb1) {
						//System.out.println("//Checking ")
						PatternSequence ret = data.returnAfterPrefix(seq);
						if(ret != null)
							count1++;
					}
					writer.write("Count: "+count1+"\n");
				}
				writer.write("\n");
			}
		} catch (UnsupportedEncodingException e) {
			System.out.println("Cannot write to file.");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("Cannot write to file.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Cannot write to file.");
			e.printStackTrace();
		}
		finally {
			try {
				writer.close();
			} catch (IOException e) {
				System.out.println("Cannot close output file.");
				e.printStackTrace();
			}
		}
	}

}
