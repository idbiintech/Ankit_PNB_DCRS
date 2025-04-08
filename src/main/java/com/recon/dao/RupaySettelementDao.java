package com.recon.dao;


import com.recon.model.RupaySettlementBean;
import com.recon.model.RupayUploadBean;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface RupaySettelementDao {
  String uploadRupaySettlementData(List<RupaySettlementBean> paramList, RupaySettlementBean paramRupaySettlementBean);
  
  HashMap<String, Object> validatePrevFileUpload(RupaySettlementBean paramRupaySettlementBean);
  
  HashMap<String, Object> updateFileSettlement(RupaySettlementBean paramRupaySettlementBean, int paramInt);
  
  HashMap<String, List<RupaySettlementBean>> getTTUMData(String paramString);
  
  String uploadPresentmentData(RupayUploadBean paramRupayUploadBean, MultipartFile paramMultipartFile) throws IOException, Exception, SQLException;
}
