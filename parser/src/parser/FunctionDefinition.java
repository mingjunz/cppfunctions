package parser;

public class FunctionDefinition {
	private String source;
	private String name;
	private String parameter;
	private String returnType;
	private int startLOC =0;
	private int endLOC = 0;

	private int startChar =0;
	private int endChar =0;

	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public int getEndLOC() {
		return endLOC;
	}
	public void setEndLOC(int endLOC) {
		this.endLOC = endLOC;
	}
	public String getDefBody() {
		return defBody;
	}
	public void setDefBody(String defBody) {
		this.defBody = defBody;
	}

	//function body : {...}
	private String defBody;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}
	public int getStartLOC() {
		return startLOC;
	}
	public void setStartLOC(int startLOC) {
		this.startLOC = startLOC;
	}



	boolean isValidFunction(){
		if(startLOC==endLOC){
			return false;
		}else{
			return true;
		}
	}
	public int getStartChar() {
		return startChar;
	}
	public void setStartChar(int startChar) {
		this.startChar = startChar;
	}
	public int getEndChar() {
		return endChar;
	}
	public void setEndChar(int endChar) {
		this.endChar = endChar;
	}
	@Override
	public String toString() {
		return "FunctionDefinition [source=" + source + ", name=" + name + ", parameter=" + parameter + ", returnType="
				+ returnType + ", startLOC=" + startLOC + ", endLOC=" + endLOC + ", startChar=" + startChar
				+ ", endChar=" + endChar + ", defBody=" + defBody + "]";
	}





}
