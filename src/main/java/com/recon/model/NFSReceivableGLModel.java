package com.recon.model;

public class NFSReceivableGLModel {
	
	//WITHDRAWAL SETTLEMENT AMOUNT
	private String CYCLE;
	private String CREATEDBY;
	private String NO_OF_TXNS;
	private String DEBIT;
	private String CREDIT;
	private String DESCRIPTION;
	
	//LATE REVERSAL SETTLEMENT AMOUNT
	private String transtype;
	private String resp_code;
	private String cardno;
	private String RRN;
	private String stanno;
	private String ACQ;
	private String ISS;
	private String trasn_date;
	private String trans_time;
	private String atmid;
	private String settledate;
	private String requestamt;
	private String receivedamt;
	private String STATUS;
	private String dcrs_remarks;
	private String FPAN;
	
	/*JCB WITHDRAWAL SETTLEMENT AMOUNT*/
	private String sr_no;
	private String filedate;
	private String ttum_naration;

	/*DFS WITHDRAWAL SETTLEMENT AMOUNT*/ 

	private String ACCOUNT_NUMBER;
	private String CURRENCY_CODE;
	private String PART_TRAN_TYPE;
	private String TRANSACTION_AMOUNT;
	private String REFERENCE_AMOUNT;
	private String TRAN_DATE;
	private String TRANSACTION_PARTICULAR;
	
	/*NOT IN HOST TTUM*/
    private String MSGTYPE;
    private String PAN;
    private String TERMID;
    private String LOCAL_DATE;
    private String LOCAL_TIME;
    private String TRACE;
    private String AMOUNT;
    private String ACCEPTORNAME;
    private String TERMLOC;
    private String AMOUNT_EQUIV;
    private String ISSUER;
	
	
	
	public String getMSGTYPE() {
		return MSGTYPE;
	}
	public void setMSGTYPE(String mSGTYPE) {
		MSGTYPE = mSGTYPE;
	}
	public String getPAN() {
		return PAN;
	}
	public void setPAN(String pAN) {
		PAN = pAN;
	}
	public String getTERMID() {
		return TERMID;
	}
	public void setTERMID(String tERMID) {
		TERMID = tERMID;
	}
	public String getLOCAL_DATE() {
		return LOCAL_DATE;
	}
	public void setLOCAL_DATE(String lOCAL_DATE) {
		LOCAL_DATE = lOCAL_DATE;
	}
	public String getLOCAL_TIME() {
		return LOCAL_TIME;
	}
	public void setLOCAL_TIME(String lOCAL_TIME) {
		LOCAL_TIME = lOCAL_TIME;
	}
	public String getTRACE() {
		return TRACE;
	}
	public void setTRACE(String tRACE) {
		TRACE = tRACE;
	}
	public String getAMOUNT() {
		return AMOUNT;
	}
	public void setAMOUNT(String aMOUNT) {
		AMOUNT = aMOUNT;
	}
	public String getACCEPTORNAME() {
		return ACCEPTORNAME;
	}
	public void setACCEPTORNAME(String aCCEPTORNAME) {
		ACCEPTORNAME = aCCEPTORNAME;
	}
	public String getTERMLOC() {
		return TERMLOC;
	}
	public void setTERMLOC(String tERMLOC) {
		TERMLOC = tERMLOC;
	}
	public String getAMOUNT_EQUIV() {
		return AMOUNT_EQUIV;
	}
	public void setAMOUNT_EQUIV(String aMOUNT_EQUIV) {
		AMOUNT_EQUIV = aMOUNT_EQUIV;
	}
	public String getISSUER() {
		return ISSUER;
	}
	public void setISSUER(String iSSUER) {
		ISSUER = iSSUER;
	}
	public String getCYCLE() {
		return CYCLE;
	}
	public void setCYCLE(String cYCLE) {
		CYCLE = cYCLE;
	}
	public String getCREATEDBY() {
		return CREATEDBY;
	}
	public void setCREATEDBY(String cREATEDBY) {
		CREATEDBY = cREATEDBY;
	}
	public String getNO_OF_TXNS() {
		return NO_OF_TXNS;
	}
	public void setNO_OF_TXNS(String nO_OF_TXNS) {
		NO_OF_TXNS = nO_OF_TXNS;
	}
	public String getDEBIT() {
		return DEBIT;
	}
	public void setDEBIT(String dEBIT) {
		DEBIT = dEBIT;
	}
	public String getCREDIT() {
		return CREDIT;
	}
	public void setCREDIT(String cREDIT) {
		CREDIT = cREDIT;
	}
	public String getDESCRIPTION() {
		return DESCRIPTION;
	}
	public void setDESCRIPTION(String dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}
	public String getTranstype() {
		return transtype;
	}
	public void setTranstype(String transtype) {
		this.transtype = transtype;
	}
	public String getResp_code() {
		return resp_code;
	}
	public void setResp_code(String resp_code) {
		this.resp_code = resp_code;
	}
	public String getCardno() {
		return cardno;
	}
	public void setCardno(String cardno) {
		this.cardno = cardno;
	}
	public String getRRN() {
		return RRN;
	}
	public void setRRN(String rRN) {
		RRN = rRN;
	}
	public String getStanno() {
		return stanno;
	}
	public void setStanno(String stanno) {
		this.stanno = stanno;
	}
	public String getACQ() {
		return ACQ;
	}
	public void setACQ(String aCQ) {
		ACQ = aCQ;
	}
	public String getISS() {
		return ISS;
	}
	public void setISS(String iSS) {
		ISS = iSS;
	}
	public String getTrasn_date() {
		return trasn_date;
	}
	public void setTrasn_date(String trasn_date) {
		this.trasn_date = trasn_date;
	}
	public String getTrans_time() {
		return trans_time;
	}
	public void setTrans_time(String trans_time) {
		this.trans_time = trans_time;
	}
	public String getAtmid() {
		return atmid;
	}
	public void setAtmid(String atmid) {
		this.atmid = atmid;
	}
	public String getSettledate() {
		return settledate;
	}
	public void setSettledate(String settledate) {
		this.settledate = settledate;
	}
	public String getRequestamt() {
		return requestamt;
	}
	public void setRequestamt(String requestamt) {
		this.requestamt = requestamt;
	}
	public String getReceivedamt() {
		return receivedamt;
	}
	public void setReceivedamt(String receivedamt) {
		this.receivedamt = receivedamt;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getDcrs_remarks() {
		return dcrs_remarks;
	}
	public void setDcrs_remarks(String dcrs_remarks) {
		this.dcrs_remarks = dcrs_remarks;
	}
	public String getFPAN() {
		return FPAN;
	}
	public void setFPAN(String fPAN) {
		FPAN = fPAN;
	}
	public String getSr_no() {
		return sr_no;
	}
	public void setSr_no(String sr_no) {
		this.sr_no = sr_no;
	}
	public String getFiledate() {
		return filedate;
	}
	public void setFiledate(String filedate) {
		this.filedate = filedate;
	}
	public String getTtum_naration() {
		return ttum_naration;
	}
	public void setTtum_naration(String ttum_naration) {
		this.ttum_naration = ttum_naration;
	}
	public String getACCOUNT_NUMBER() {
		return ACCOUNT_NUMBER;
	}
	public void setACCOUNT_NUMBER(String aCCOUNT_NUMBER) {
		ACCOUNT_NUMBER = aCCOUNT_NUMBER;
	}
	public String getCURRENCY_CODE() {
		return CURRENCY_CODE;
	}
	public void setCURRENCY_CODE(String cURRENCY_CODE) {
		CURRENCY_CODE = cURRENCY_CODE;
	}
	public String getPART_TRAN_TYPE() {
		return PART_TRAN_TYPE;
	}
	public void setPART_TRAN_TYPE(String pART_TRAN_TYPE) {
		PART_TRAN_TYPE = pART_TRAN_TYPE;
	}
	public String getTRANSACTION_AMOUNT() {
		return TRANSACTION_AMOUNT;
	}
	public void setTRANSACTION_AMOUNT(String tRANSACTION_AMOUNT) {
		TRANSACTION_AMOUNT = tRANSACTION_AMOUNT;
	}
	public String getREFERENCE_AMOUNT() {
		return REFERENCE_AMOUNT;
	}
	public void setREFERENCE_AMOUNT(String rEFERENCE_AMOUNT) {
		REFERENCE_AMOUNT = rEFERENCE_AMOUNT;
	}
	public String getTRAN_DATE() {
		return TRAN_DATE;
	}
	public void setTRAN_DATE(String tRAN_DATE) {
		TRAN_DATE = tRAN_DATE;
	}
	public String getTRANSACTION_PARTICULAR() {
		return TRANSACTION_PARTICULAR;
	}
	public void setTRANSACTION_PARTICULAR(String tRANSACTION_PARTICULAR) {
		TRANSACTION_PARTICULAR = tRANSACTION_PARTICULAR;
	}
	@Override
	public String toString() {
		return "NFSReceivableGLModel [CYCLE=" + CYCLE + ", CREATEDBY=" + CREATEDBY + ", NO_OF_TXNS=" + NO_OF_TXNS
				+ ", DEBIT=" + DEBIT + ", CREDIT=" + CREDIT + ", DESCRIPTION=" + DESCRIPTION + ", transtype="
				+ transtype + ", resp_code=" + resp_code + ", cardno=" + cardno + ", RRN=" + RRN + ", stanno=" + stanno
				+ ", ACQ=" + ACQ + ", ISS=" + ISS + ", trasn_date=" + trasn_date + ", trans_time=" + trans_time
				+ ", atmid=" + atmid + ", settledate=" + settledate + ", requestamt=" + requestamt + ", receivedamt="
				+ receivedamt + ", STATUS=" + STATUS + ", dcrs_remarks=" + dcrs_remarks + ", FPAN=" + FPAN + ", sr_no="
				+ sr_no + ", filedate=" + filedate + ", ttum_naration=" + ttum_naration + ", ACCOUNT_NUMBER="
				+ ACCOUNT_NUMBER + ", CURRENCY_CODE=" + CURRENCY_CODE + ", PART_TRAN_TYPE=" + PART_TRAN_TYPE
				+ ", TRANSACTION_AMOUNT=" + TRANSACTION_AMOUNT + ", REFERENCE_AMOUNT=" + REFERENCE_AMOUNT
				+ ", TRAN_DATE=" + TRAN_DATE + ", TRANSACTION_PARTICULAR=" + TRANSACTION_PARTICULAR + ", MSGTYPE="
				+ MSGTYPE + ", PAN=" + PAN + ", TERMID=" + TERMID + ", LOCAL_DATE=" + LOCAL_DATE + ", LOCAL_TIME="
				+ LOCAL_TIME + ", TRACE=" + TRACE + ", AMOUNT=" + AMOUNT + ", ACCEPTORNAME=" + ACCEPTORNAME
				+ ", RESPCODE=" + ", TERMLOC=" + TERMLOC + ", AMOUNT_EQUIV=" + AMOUNT_EQUIV + ", ISSUER="
				+ ISSUER + "]";
	}
	
	
	

}
