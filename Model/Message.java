package Model;

public class Message {
	private int seqNum = 0;
	private String FEAddr = "";
	private String FEPort = "";
	private String operationMsg = "";
	
	public int getSeqNum() {
		return seqNum;
	}
	public void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}
	public String getFEAddr() {
		return FEAddr;
	}
	public void setFEAddr(String fEAddr) {
		FEAddr = fEAddr;
	}
	public String getFEPort() {
		return FEPort;
	}
	public void setFEPort(String fEPort) {
		FEPort = fEPort;
	}
	public String getOperationMsg() {
		return operationMsg;
	}
	public void setOperationMsg(String operationMsg) {
		this.operationMsg = operationMsg;
	}
}
