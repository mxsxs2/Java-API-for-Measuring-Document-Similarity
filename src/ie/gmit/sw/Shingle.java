package ie.gmit.sw;
/**
 * Container of a shingle has code from a given document.
 * Holds the document id and the hash code of the shingle
 * @author Krisztian Nagy
 *
 */
public class Shingle {
	//Id of the document which the has code is from
	private int docId;
	//Has code of the current shingle
	private int hashCode;
	
	public Shingle(int docId, int hashCode) {
		super();
		this.docId = docId;
		this.hashCode = hashCode;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getHashCode() {
		return hashCode;
	}
	public void setHashCode(int hashCode) {
		this.hashCode = hashCode;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + docId;
		result = prime * result + hashCode;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Shingle other = (Shingle) obj;
		if (docId != other.docId)
			return false;
		if (hashCode != other.hashCode)
			return false;
		return true;
	}
	
	
	
	
}
