package project.kais.init;

/**
 * 
 * @author ykkim
 *
 */
public class KaisState {

	/** NICE스크래핑 요청 */	        public static final String SCR_REQ_S1 = "S1";	
	/** NICE스크래핑요청 수신응답 */	public static final String SCR_RES_S2 = "S2";

	/** AIB요청 대상 */	            public static final String AIB_TGT_A0 = "A0";	
	/** AIB요청 대상 지정 */	        public static final String AIB_ASN_AA = "AA";
	/** AIB요청 송신 */	            //public static final String AIB_SND_A1 = "A1";	
	/** AIB결과 수신 */	            //public static final String AIB_RCV_A2 = "A2";
	
	/** NICE정보등록 대상 */	        public static final String INFO_TGT_R0 = "R0";	
	/** NICE정보등록 대상 지정 */	    public static final String INFO_ASN_RR = "RR";	
	/** NICE정보등록 요청 송신 */	    //public static final String INFO_SND_R1 = "R1";	
	/** NICE정보등록 요청 수신응답 */	public static final String INFO_RCV_R2 = "R2";
	
	/** FTP 업로드 대상  */           public static final String FTP_F0 = "F0";
	
	/** AIB 원본저장 요청 대상  */       public static final String AIB_TGT_AF = "AF";
	/** AIB 원본저장 요청 대상지정  */	   public static final String AIB_ASN_AE = "AE";
	 
	/** NICE로그등록 대상 */	        public static final String LOG_TGT_L0 = "L0";	
	/** NICE로그등록 대상 지정 */	    public static final String LOG_ASN_LL = "LL";	
	/** NICE로그등록 요청 송신 */	   //public static final String LOG_SND_L1 = "L1";	
	/** NICE로그등록 요청 수신응답 */	public static final String LOG_RCV_L2 = "L2";
	
	/** 처리불가 */	public static final String CANNOT_PROC_E0 = "E0";
}
