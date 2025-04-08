package com.recon.service.impl;

import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.recon.dao.RoleDao;
import com.recon.model.RoleBean;
import com.recon.service.RoleService;

@Component
@SuppressWarnings({"unchecked"})
public class RoleServiceImpl implements RoleService {

	@Autowired
	RoleDao roleDao;
	
	/**
	 * Field Name Constants.
	 */
	private static final String USER_ID = "user_id";
	private static final String PAGE_ID = "page_id";
	private static final String PAGE_NAME = "page_name";
	private static final String PAGE_URL = "page_url";
	private static final String PAGE_TITLE = "page_title";
	private static final String PAGE_LEVEL = "page_level";
	private static final String PARENT_ID = "parent_id";
	private static final String PAGE_STATUS = "page_status";
	private static final String ENTRY_DT = "entry_dt";
	private static final String UPDT_BY = "updt_by";
	

	@Override
	public List<RoleBean> viewRole(RoleBean roleBean) throws Exception {
		return roleDao.viewRole(roleBean);
	}

	@Override
	public String viewUserRole(RoleBean roleBean) throws Exception {
		JSONObject object = new JSONObject();
		JSONArray parentArray = new JSONArray();
		
		JSONObject parentObject;
		JSONObject childObject;
		JSONObject subChildObject;
		try {
			List<RoleBean> role_list = roleDao.viewUserRole(roleBean);
			List<RoleBean> child_list = role_list;
			List<RoleBean> sub_child_list = role_list;
			for(RoleBean parentBean : role_list){
				/**Fetching Main Parent Menu */
				if(parentBean.getParent_id() == 0){
					parentObject = new JSONObject();
					parentObject.put(USER_ID, parentBean.getUser_id());
					parentObject.put(PAGE_ID, parentBean.getPage_id());
					parentObject.put(PAGE_NAME, parentBean.getPage_name());
					parentObject.put(PAGE_URL, parentBean.getPage_url());
					parentObject.put(PAGE_TITLE, parentBean.getPage_title());
					parentObject.put(PAGE_LEVEL, parentBean.getPage_level());
					parentObject.put(PARENT_ID, parentBean.getParent_id());
					parentObject.put(PAGE_STATUS, parentBean.getPage_status());
					parentObject.put(ENTRY_DT, parentBean.getEntry_dt());
					parentObject.put(UPDT_BY, parentBean.getUpdt_by());

					/**Fetching Sub Menu **/
					JSONArray childArray = new JSONArray();
					for(RoleBean childBean : child_list){
						if(parentBean.getPage_id() == childBean.getParent_id()){
							childObject = new JSONObject();
							childObject.put(USER_ID, childBean.getUser_id());
							childObject.put(PAGE_ID, childBean.getPage_id());
							childObject.put(PAGE_NAME, childBean.getPage_name());
							childObject.put(PAGE_URL, childBean.getPage_url());
							childObject.put(PAGE_TITLE, childBean.getPage_title());
							childObject.put(PAGE_LEVEL, childBean.getPage_level());
							childObject.put(PARENT_ID, childBean.getParent_id());
							childObject.put(PAGE_STATUS, childBean.getPage_status());
							childObject.put(ENTRY_DT, childBean.getEntry_dt());
							childObject.put(UPDT_BY, childBean.getUpdt_by());

							/**Fetching Sub-sub Menu **/
							JSONArray subChildArray = new JSONArray();
							for(RoleBean subChildBean : sub_child_list){
								if(childBean.getPage_id() == subChildBean.getParent_id()){
									subChildObject = new JSONObject();
									subChildObject.put(USER_ID, subChildBean.getUser_id());
									subChildObject.put(PAGE_ID, subChildBean.getPage_id());
									subChildObject.put(PAGE_NAME, subChildBean.getPage_name());
									subChildObject.put(PAGE_URL, subChildBean.getPage_url());
									subChildObject.put(PAGE_TITLE, subChildBean.getPage_title());
									subChildObject.put(PAGE_LEVEL, subChildBean.getPage_level());
									subChildObject.put(PARENT_ID, subChildBean.getParent_id());
									subChildObject.put(PAGE_STATUS, subChildBean.getPage_status());
									subChildObject.put(ENTRY_DT, subChildBean.getEntry_dt());
									subChildObject.put(UPDT_BY, subChildBean.getUpdt_by());
									
									subChildArray.add(subChildObject);
								}
							}

							childObject.put("SubChild", subChildArray);
							
							childArray.add(childObject);
						}
					}

					parentObject.put("Child", childArray);
					parentArray.add(parentObject);

				}
			}
			object.put("Menu", parentArray);
			return object.toString();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void assignRole(List<Integer> roles, RoleBean roleBean) throws Exception {
		roleDao.assignRole(roles, roleBean);
	}

	@Override
	public void revokeRole(List<Integer> roles, RoleBean roleBean) throws Exception {
		roleDao.revokeRole(roles, roleBean);
	}

	@Override
	public boolean checkRole(RoleBean roleBean) throws Exception {
		return roleDao.checkRole(roleBean);
	}

}