package com.recon.model;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class ForceMatchDataJson {

	 private String Result;
	    private List<ForceMatchBean> Records;
	    private int TotalRecordCount;
	    private String Message;
	    
	    

	    public void setParams(String Result, List<ForceMatchBean> Records,int TotalRecordCount) {
	        this.Result = Result;
	        this.Records = Records;
	        this.TotalRecordCount = TotalRecordCount;
	    }
	    
	    

	    public void setParams(String Result, String Message) {
	        this.Result = Result;
	        this.Message = Message;
	    }
	    

	    @JsonProperty("Result")
	    public String getResult() {
	        return Result;
	    }

	    public void setResult(String Result) {
	        this.Result = Result;
	    }

	    @JsonProperty("Records")
	    public List<ForceMatchBean> getRecords() {
	        return Records;
	    }

	    public void setRecords(List<ForceMatchBean> Records) {
	        this.Records = Records;
	    }

	    @JsonProperty("TotalRecordCount")
	    public int getTotalRecordCount() {
	        return TotalRecordCount;
	    }

	    public void setTotalRecordCount(int TotalRecordCount) {
	        this.TotalRecordCount = TotalRecordCount;
	    }

	    @JsonProperty("Message")
	    public String getMessage() {
	        return Message;
	    }

	    public void setMessage(String Message) {
	        this.Message = Message;
	    }


	

	   

}
