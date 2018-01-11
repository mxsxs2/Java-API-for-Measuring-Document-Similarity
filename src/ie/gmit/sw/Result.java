package ie.gmit.sw;

/**
 * Storage class for file comparison.
 * @author Krisztian Nagy
 *
 */
public class Result {
	private String fileName1;
	private String fileName2;
	private String result;
	private String comparisonType;
	/**
	 * Constructor with fields
	 * @param fileName1 Path for the first file to be compared
	 * @param fileName2 Path for the second file to be compared
	 * @param result Result of the comparison
	 * @param comparisonType Type of the comparison
	 */
	public Result(String fileName1, String fileName2, String result, String comparisonType) {
		super();
		this.fileName1 = fileName1;
		this.fileName2 = fileName2;
		this.result = result;
		this.comparisonType = comparisonType;
	}

	public String getFileName1() {
		return fileName1;
	}

	public void setFileName1(String fileName1) {
		this.fileName1 = fileName1;
	}

	public String getFileName2() {
		return fileName2;
	}

	public void setFileName2(String fileName2) {
		this.fileName2 = fileName2;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getComparisonType() {
		return comparisonType;
	}

	public void setComparisonType(String comparisonType) {
		this.comparisonType = comparisonType;
	}
	
}
