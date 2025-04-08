package com.recon.dao.impl;


import static com.recon.model.RoleBean.ENTRY_BY;
import static com.recon.model.RoleBean.ENTRY_DT;
import static com.recon.model.RoleBean.PAGE_ID;
import static com.recon.model.RoleBean.PAGE_LEVEL;
import static com.recon.model.RoleBean.PAGE_NAME;
import static com.recon.model.RoleBean.PAGE_STATUS;
import static com.recon.model.RoleBean.PAGE_TITLE;
import static com.recon.model.RoleBean.PAGE_URL;
import static com.recon.model.RoleBean.PARENT_ID;
import static com.recon.model.RoleBean.UPDT_BY;
import static com.recon.model.RoleBean.UPDT_DT;
import static com.recon.model.RoleBean.USER_ID;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Component;

import com.recon.dao.RoleDao;
import com.recon.model.RoleBean;

import oracle.jdbc.OracleTypes;

@Component
@SuppressWarnings({"unused"})
public class RoleDaoImpl extends JdbcDaoSupport implements RoleDao {

	/**
	 * Input Field Constants.
	 */
	private static final String I_USER_ID = "i_user_id";
	private static final String I_PAGE_ID = "i_page_id";
	private static final String I_PAGE_LIST = "i_page_list";
	private static final String I_PAGE_URL = "i_page_url";
	private static final String I_PARENT_ID = "i_parent_id";
	private static final String I_ENTRY_BY = "i_entry_by";
	private static final String I_UPDT_BY = "i_updt_by";

	/**
	 * Output Field Constants.
	 */
	private static final String O_ROLE_STATUS = "o_role_status";
	private static final String O_ROLE_CUR = "o_role_cur";
	private static final String O_ERROR_CODE = "o_error_code";
	private static final String O_ERROR_MESSAGE = "o_error_message";

	PreparedStatement preparedStatement;
	@Override
	public List<RoleBean> viewRole(RoleBean roleBean) throws Exception {
		try {
			Map<String, Object> inParams = new HashMap<String, Object>();
			System.out.println("I_PAGE_ID "+ roleBean.getPage_id());
			inParams.put(I_PAGE_ID, roleBean.getPage_id());

			ViewRoleProc viewRoleProc = new ViewRoleProc(getJdbcTemplate());
			Map<String, Object> outParams = viewRoleProc.execute(inParams);
System.out.println("outparam "+ outParams.toString());
			if (Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0 && outParams.get(O_ERROR_MESSAGE) != null) {
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
			return viewRoleProc.getRole_list();
		} catch (Exception e) {
			throw e;
		}
	}

	private class ViewRoleProc extends StoredProcedure {
		private static final String view_role_proc = "VIEW_ROLE";
		List<RoleBean> role_list = new ArrayList<RoleBean>();

		/**
		 * @return the role_list
		 */
		public List<RoleBean> getRole_list() {
			return role_list;
		}

		public ViewRoleProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, view_role_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_PAGE_ID, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ROLE_CUR, OracleTypes.CURSOR, new RoleMapper()));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}

		private class RoleMapper implements RowCallbackHandler {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				RoleBean roleBean = new RoleBean();

				roleBean.setPage_id(rs.getInt(PAGE_ID));
				roleBean.setPage_name(rs.getString(PAGE_NAME));
				roleBean.setPage_url(rs.getString(PAGE_URL));
				roleBean.setPage_title(rs.getString(PAGE_TITLE));
				roleBean.setPage_level(rs.getInt(PAGE_LEVEL));
				roleBean.setParent_id(rs.getInt(PARENT_ID));
				roleBean.setPage_status(rs.getString(PAGE_STATUS));
				roleBean.setEntry_dt(rs.getString(ENTRY_DT));
				roleBean.setEntry_by(rs.getString(ENTRY_BY));
				roleBean.setUpdt_dt(rs.getString(UPDT_DT));
				roleBean.setUpdt_by(rs.getString(UPDT_BY));

				role_list.add(roleBean);
			}
		}
	}

	@Override
	public List<RoleBean> viewUserRole(RoleBean roleBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_USER_ID, roleBean.getUser_id());

			ViewUserRoleProc viewUserRoleProc = new ViewUserRoleProc(getJdbcTemplate());
			Map<String, Object> outParams = viewUserRoleProc.execute(inParams);

			if (Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0 && outParams.get(O_ERROR_MESSAGE) != null) {
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
			return viewUserRoleProc.getRole_list();
		}catch(Exception e){
			throw e;
		}
	}

	private class ViewUserRoleProc extends StoredProcedure {
		private static final String view_user_role_proc = "VIEW_USER_ROLE";
		List<RoleBean> role_list = new ArrayList<RoleBean>();

		/**
		 * @return the role_list
		 */
		public List<RoleBean> getRole_list() {
			return role_list;
		}

		public ViewUserRoleProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, view_user_role_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_USER_ID, OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_ROLE_CUR, OracleTypes.CURSOR, new RoleMapper()));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}

		private class RoleMapper implements RowCallbackHandler {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				RoleBean roleBean = new RoleBean();

				roleBean.setUser_id(rs.getString(USER_ID));
				roleBean.setPage_id(rs.getInt(PAGE_ID));
				roleBean.setPage_name(rs.getString(PAGE_NAME));
				roleBean.setPage_url(rs.getString(PAGE_URL));
				roleBean.setPage_title(rs.getString(PAGE_TITLE));
				roleBean.setPage_level(rs.getInt(PAGE_LEVEL));
				roleBean.setParent_id(rs.getInt(PARENT_ID));
				roleBean.setPage_status(rs.getString(PAGE_STATUS));
				roleBean.setEntry_dt(rs.getString(ENTRY_DT));
				roleBean.setUpdt_by(rs.getString(UPDT_BY));

				role_list.add(roleBean);
			}
		}
	}

	@Override
	public void assignRole(List<Integer> roles, RoleBean roleBean) throws Exception {
		try {
			Map<String, Object> inParams = new HashMap<String, Object>();
			StringBuilder pageBuilder = new StringBuilder();
			for (int i = 0; i < roles.size(); i++) {
				pageBuilder.append(roles.get(i));

				if (i < roles.size() - 1) {
					pageBuilder.append(",");
				}
			}
			inParams.put(I_PAGE_LIST, pageBuilder.toString());
			inParams.put(I_USER_ID, roleBean.getUser_id());
			inParams.put(I_ENTRY_BY, roleBean.getEntry_by());

			AssignRoleProc assignRoleProc = new AssignRoleProc(getJdbcTemplate());
			Map<String, Object> outParams = assignRoleProc.execute(inParams);
			if (outParams.get(O_ERROR_MESSAGE) != null || Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0) {
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
		} catch (Exception e) {
			throw e;
		}
	}
	
	private class AssignRoleProc extends StoredProcedure{
		private static final String assign_roles_proc = "ASSIGN_ROLES";
		
		public AssignRoleProc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, assign_roles_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_PAGE_LIST, OracleTypes.VARCHAR));
			declareParameter(new SqlParameter(I_USER_ID, OracleTypes.VARCHAR));
			declareParameter(new SqlParameter(I_ENTRY_BY, OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}
	}

	@Override
	public void revokeRole(List<Integer> roles, RoleBean roleBean) throws Exception {
		try {
			StringBuilder roleBuilder = new StringBuilder();
			for (int i = 0; i < roles.size(); i++) {
				roleBuilder.append(roles.get(i));
				if (i < roles.size() - 1) {
					roleBuilder.append(",");
				}
			}
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_PAGE_LIST, roleBuilder.toString());
			inParams.put(I_USER_ID, roleBean.getUser_id());

			RevokeRoleProc revokeRoleProc = new RevokeRoleProc(getJdbcTemplate());
			Map<String, Object> outParams = revokeRoleProc.execute(inParams);
			if (outParams.get(O_ERROR_MESSAGE) != null || Integer.parseInt(String.valueOf(outParams.get(O_ERROR_CODE))) != 0) {
				throw new Exception(outParams.get(O_ERROR_MESSAGE).toString());
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private class RevokeRoleProc extends StoredProcedure {
		private static final String revoke_roles_proc = "REVOKE_ROLES";

		public RevokeRoleProc(JdbcTemplate jdbcTemplate) {
			super(jdbcTemplate, revoke_roles_proc);
			setFunction(false);
			declareParameter(new SqlParameter(I_PAGE_LIST, OracleTypes.VARCHAR));
			declareParameter(new SqlParameter(I_USER_ID, OracleTypes.VARCHAR));
			declareParameter(new SqlOutParameter(O_ERROR_CODE, OracleTypes.INTEGER));
			declareParameter(new SqlOutParameter(O_ERROR_MESSAGE, OracleTypes.VARCHAR));
			compile();
		}
	}

	@Override
	public boolean checkRole(RoleBean roleBean) throws Exception {
		try{
			Map<String, Object> inParams = new HashMap<String, Object>();
			inParams.put(I_PAGE_URL, roleBean.getPage_url());
			
			inParams.put(I_USER_ID, roleBean.getUser_id());
			
			CheckRoleFunc checkRoleFunc = new CheckRoleFunc(getJdbcTemplate());
			Map<String, Object> outParams = checkRoleFunc.execute(inParams);
			if(Integer.parseInt(outParams.get(O_ROLE_STATUS).toString()) == 1){
			
				return true;
			} else {
			
				return false;
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	private class CheckRoleFunc extends StoredProcedure{
		private static final String check_role_func = "IS_ROLE_AUTHORISED";
		
		public CheckRoleFunc(JdbcTemplate jdbcTemplate){
			super(jdbcTemplate, check_role_func);
			setFunction(true);
			declareParameter(new SqlOutParameter(O_ROLE_STATUS, OracleTypes.INTEGER));
			declareParameter(new SqlParameter(I_PAGE_URL, OracleTypes.VARCHAR));
			declareParameter(new SqlParameter(I_USER_ID, OracleTypes.VARCHAR));
			compile();
		}
	}
}