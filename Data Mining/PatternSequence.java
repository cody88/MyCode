import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class PatternSequence {
	Vector<Vector<Integer>> sequence;
	
	public PatternSequence() {
		 sequence = new Vector<Vector<Integer>>();
	}
	
	public PatternSequence(PatternSequence seq2) {
		sequence = new Vector<Vector<Integer>>();
		for(Vector<Integer> vect2: seq2.getSequence()) {
			Vector<Integer> vect = new Vector<Integer>();
			for(Integer item: vect2)
				vect.add(item);
			sequence.add(vect);
		}
	}
	
	public PatternSequence(Vector<Vector<Integer>> sequence) {
		this();
		this.sequence.addAll(sequence);
	}
	
	public void addElement(Vector<Integer> elem) {
		sequence.add(elem);
	}
	
	public Vector<Vector<Integer>> getSequence() {
		return sequence;
	}
	
	public int length() {
		int length = 0;
		for(Vector<Integer> vect: sequence)
			length += vect.size();
		return length;
	}
	
	/*public PatternSequence copyOf() {
		return new PatternSequence(this.sequence);
	}*/
	
	public void insertIntoLastItemset(Integer item) {
		sequence.lastElement().add(item);
	}
	
	public boolean containsItem(Integer item) {
		for(Vector<Integer> vect: sequence) {
			for(Integer it: vect) {
				if(it == item) return true;
			}
		}
		return false;
	}
	
	public boolean containsSequence(PatternSequence S2) {
		Vector<Vector<Integer>> seq2 = S2.getSequence();
		if(sequence.isEmpty() || seq2.isEmpty())
			return false;
		
		int i, j = 0;
		for(i=0; i<seq2.size() && j<sequence.size(); i++) {
			Set<Integer> set2 = new HashSet<Integer>(seq2.get(i));
			Set<Integer> set;
			do {
				set = new HashSet<Integer>(sequence.get(j));
			}while((!set.containsAll(set2)) && ++j<sequence.size());
			if(j == sequence.size()) // the reason it may have come out of the loop
				return false;
			else
				j++;			
		}
		if(i == seq2.size())
			return true;
		return false;
	}
	
	public PatternSequence returnAfterPrefix(PatternSequence prefix) {
		int pIndex = 0, dIndex = 0, i = 0, j = 0;
		Integer[] pre = prefix.getSequence().elementAt(0).toArray(new Integer[1]);
		if(sequence.isEmpty()) return null;
		if(!this.containsSequence(prefix)) return null;
		Integer[] db = sequence.elementAt(0).toArray(new Integer[1]);
		while(true) {
			while(i<pre.length && j<db.length)
				if(pre[i] == db[j++])
					i++;
			// Why did it come out?
			if(i == pre.length) {
				if(++pIndex == prefix.getSequence().size()) {
					PatternSequence proj = new PatternSequence();
					Vector<Integer> vect = new Vector<Integer>();
					if(j < db.length) vect.add(MSPS.underscoreInteger);  // Equivalent of '_'
					for(; j<db.length; vect.add(db[j++]));
					if(!vect.isEmpty()) proj.addElement(vect);
					if(++dIndex < sequence.size())
						for(Vector<Integer> elem: sequence.subList(dIndex, sequence.size()))
							proj.addElement(elem);
					if(proj.isEmpty()) {
						// Adding just 1 empty element
						Vector<Integer> vect1 = new Vector<Integer>();
						proj.addElement(vect1);
					}
					return proj;
				}
				pre = prefix.getSequence().elementAt(pIndex).toArray(new Integer[1]);
				i = 0;
			}
			else {
				if(++dIndex == sequence.size())
					return null;
				db = sequence.elementAt(dIndex).toArray(new Integer[1]);
				j = 0;
			}
		}
	}
	
	public boolean isEmpty() {
		if(sequence.isEmpty()) return true;
		for(Vector<Integer> vect: sequence) {
			if(!vect.isEmpty())
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("<");
		for(Vector<Integer> vect: sequence) {
			if(vect.isEmpty()) continue;
			str.append("{");
			for(Integer item: vect)
				str.append(item+",");
			str.deleteCharAt(str.length()-1);
			str.append("}");
		}
		str.append(">");
		return str.toString();
	}

}
