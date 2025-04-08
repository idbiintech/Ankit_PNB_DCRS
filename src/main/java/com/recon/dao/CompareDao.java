package com.recon.dao;



import com.recon.model.CompareBean;
import java.util.List;

public interface CompareDao {
  int moveData(List<String> paramList, CompareBean paramCompareBean, int paramInt) throws Exception;
  
  void updateMatchedRecords(List<String> paramList, CompareBean paramCompareBean, int paramInt) throws Exception;
  
  void moveToRecon(List<String> paramList, CompareBean paramCompareBean) throws Exception;
  
  void TTUMRecords(List<String> paramList, CompareBean paramCompareBean, int paramInt) throws Exception;
  
  List<Integer> getRec_set_id(String paramString) throws Exception;
  
  List<String> getTableName(int paramInt, String paramString) throws Exception;
  
  void clearTables(List<String> paramList, CompareBean paramCompareBean) throws Exception;
  
  void removeDuplicates(List<String> paramList, CompareBean paramCompareBean, int paramInt) throws Exception;
}
