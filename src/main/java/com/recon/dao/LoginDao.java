package com.recon.dao;


import com.recon.model.LoginBean;
import com.recon.model.ProcessDtlBean;
import java.util.List;
import java.util.Map;

public interface LoginDao {
  LoginBean getUserDetail(LoginBean paramLoginBean) throws Exception;
  
  void invalidateUser(LoginBean paramLoginBean) throws Exception;
  
  void closeSession(LoginBean paramLoginBean) throws Exception;
  
  boolean checkIp(LoginBean paramLoginBean) throws Exception;
  
  Map<String, Object> getAllSession(LoginBean paramLoginBean) throws Exception;
  
  List<ProcessDtlBean> getProcessdtls(String paramString);
  
  String getUSerDetails(String paramString);
}
