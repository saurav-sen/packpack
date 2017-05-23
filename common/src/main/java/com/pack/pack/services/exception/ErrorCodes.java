package com.pack.pack.services.exception;

/**
 * 
 * @author Saurav
 *
 */
public interface ErrorCodes {

	public static final String PACK_ERR_01 = "pack_err_01"; // Entity Not found
	public static final String PACK_ERR_02 = "pack_err_02"; // No result exception
	public static final String PACK_ERR_03 = "pack_err_03"; // No unique result exception
	public static final String PACK_ERR_04 = "pack_err_04"; // Missing required field.
	
	public static final String PACK_ERR_11 = "pack_err_11"; //JPA initialization error
	public static final String PACK_ERR_12 = "pack_err_12"; // Persistence exception
	public static final String PACK_ERR_13 = "pack_err_13"; //JAXB initialization error
	public static final String PACK_ERR_14 = "pack_err_14"; //Error in transaction manager. No EM found.
	
	public static final String PACK_ERR_21 = "pack_err_21"; //Transaction required exception
	public static final String PACK_ERR_22 = "pack_err_22"; //Transaction roll back exception
	public static final String PACK_ERR_23 = "pack_err_23"; //Transaction commit exception
	public static final String PACK_ERR_24 = "pack_err_24"; //Transaction already active
	
	public static final String PACK_ERR_31 = "pack_err_31"; //Entity already exists
	public static final String PACK_ERR_32 = "pack_err_32"; 
	
	public static final String PACK_ERR_41 = "pack_err_41"; //Query/search error
	public static final String PACK_ERR_42 = "pack_err_42"; 
	public static final String PACK_ERR_43 = "pack_err_43"; // given phone associated with multiple users
	
	public static final String PACK_ERR_51 = "pack_err_51"; //Lock timed out
	public static final String PACK_ERR_52 = "pack_err_52"; //query timed out
	
	public static final String PACK_ERR_55 = "pack_err_55"; //optimistic lock exception
	public static final String PACK_ERR_56 = "pack_err_56"; //pessimistic lock exception
	public static final String PACK_ERR_57 = "pack_err_57"; //query timed out
	
	public static final String PACK_ERR_60 = "pack_err_60"; // RMI error (Remote function returned null value)
	
	public static final String PACK_ERR_61 = "pack_err_61"; //Internal Some other runtime error.
	public static final String PACK_ERR_62 = "pack_err_62"; //Wrapped PackPackException.
	public static final String PACK_ERR_63 = "pack_err_63"; //Wrapped PackPackException. But, with this server 
														  //should not unwrap it further to return wrapped cause to client 
														  //(as that may not be wise or make any sense for user/client)
	
	public static final String PACK_ERR_65 = "pack_err_65"; //Client Protocol Exception (HTTP invoke failed to dependent service or other portals/API(REST)
	public static final String PACK_ERR_66 = "pack_err_66"; //Response Parsing failed.
	public static final String PACK_ERR_67 = "pack_err_67"; //Request/Response IO Failed (Network issue, service not available/reachable)
	
	public static final String PACK_ERR_71 = "pack_err_71"; //Invalid Enum (user error)
	public static final String PACK_ERR_72 = "pack_err_72"; //Mandatory field (passed as NULL/Empty) (user error)
	public static final String PACK_ERR_73 = "pack_err_73"; //No valid entity found with ID given (client/user error)
	public static final String PACK_ERR_74 = "pack_err_74"; //Invalid JSON payload (client/user error)
	public static final String PACK_ERR_75 = "pack_err_75"; //Can't process request (Not supported)
	public static final String PACK_ERR_76 = "pack_err_76"; //Missing dependent info in patient profile (Not supported)
	public static final String PACK_ERR_77 = "pack_err_77"; //Invalid relationship type for patient.
	public static final String PACK_ERR_78 = "pack_err_78"; //User with same email already exists
	public static final String PACK_ERR_79 = "pack_err_79"; //
	public static final String PACK_ERR_80 = "pack_err_80"; //Old password doesn't match (in update info)
	public static final String PACK_ERR_81 = "pack_err_81"; //Invalid time range
	public static final String PACK_ERR_82 = "pack_err_82"; //Patient with same name is already related to owner.
	public static final String PACK_ERR_84 = "pack_err_84"; //Unsupported or Unimplemented operation exception.
	public static final String PACK_ERR_85 = "pack_err_85"; //Invalid date format, failed to parse.
	
	public static final String PACK_ERR_86 = "pack_err_86"; //Need to login or Authorization is missing.
	public static final String PACK_ERR_87 = "pack_err_87"; //UnAuthorized access.
	
	public static final String PACK_ERR_88 = "pack_err_88";//content-disposition is NULL or filename is NULL
	public static final String PACK_ERR_89 = "pack_err_89"; // email template not found
	
	public static final String PACK_ERR_90 = "pack_err_90"; // RMI error (GET is not supported for void type return of function)
	public static final String PACK_ERR_91 = "pack_err_91"; // Doctor with same email or phone number is found but having different name.
	
	public static final String PACK_ERR_92 = "pack_err_92"; // (Bad Request)
	public static final String PACK_ERR_93 = "pack_err_93"; // Forbidden to access details of some other member.
	
	public static final String PACK_ERR_94 = "pack_err_94"; // Unsupported Media Type.
	
	public static final String PACK_ERR_95 = "pack_err_94"; // Invalid OTP.
}