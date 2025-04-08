package com.recon.dao;

import java.util.List;

import com.recon.model.NFSGLSummaryPayable;
import com.recon.model.NFSGLSummaryReceivable;
import com.recon.model.NFSPayableGLModel;
import com.recon.model.NFSReceivableGLModel;



public interface NFSSystemGLDao  {

	void processNfsReportPayable(String date, String openingBal, String fundMovefr43Acc, String onlineFundMov,
			String finacleEODbal);

	List<NFSGLSummaryPayable> downloadNfsReportPayable(String date) throws Exception;

	List<NFSPayableGLModel> withdrawalStlmntAmtPayable(String date);

	List<NFSPayableGLModel> lateReversalStlmntAmtPayable(String date);

	List<NFSPayableGLModel> unreconciledTranPostPayable(String date);

	List<NFSPayableGLModel> pbgbStlmntAmtPostedPayable(String date);

	void processNfsReportReceivable(String date, String openingBal, String onlineFundMov, String finacleEODbal);

	List<NFSReceivableGLModel> withdrawalStlmntAmtReceivable(String date);

	List<NFSReceivableGLModel> lateReversalStlmntAmtReceivable(String date);

	List<NFSReceivableGLModel> jcbWithdrawalStlmntAmtReceivable(String date);

	List<NFSReceivableGLModel> dfsWithdrawalStlmntAmtReceivable(String date);

	List<NFSReceivableGLModel> notInHostTtumReceivable(String date);

	List<NFSGLSummaryReceivable> downloadNfsReportReceivable(String date) throws Exception;

}
