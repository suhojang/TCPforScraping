package project.kais.record;

import java.io.Serializable;

import com.kwic.xml.parser.JXParser;

/**
 * 관리자 작업 이력 추적 정보
 * @author ykkim
 *
 */
public class MgrInfoRec implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 관리자ID
	 */
	private String mgrinf_id;
	
	/**
	 * 비밀번호
	 */
	private String mgrinf_pwd;
	
	/**
	 * 관리자 등급 코드
	 */
	private String mgrinf_grd;
	
	/**
	 * 관리자 등급명
	 */
	private String mgrinf_grd_nm;

	/**
	 * 관리자명
	 */
	private String mgrinf_nm;
	
	/**
	 * 비상연락처
	 */
	private String mgrinf_tel;
	
	/**
	 * 사용여부
	 */
	private String mgrinf_usyn;
	
	/**
	 * 메뉴 XML
	 */
	private String menuxml;
	
	/**
	 * 전체 메뉴 XML
	 */
	private String menufullxml;
	
	/**
	 * URL XML
	 */
	private JXParser urixml;

	public void setMgrinf_id	(String mgrinf_id	){this.mgrinf_id	= mgrinf_id;	}
	public void setMgrinf_pwd	(String mgrinf_pwd	){this.mgrinf_pwd	= mgrinf_pwd;	}
	public void setMgrinf_grd	(String mgrinf_grd	){this.mgrinf_grd	= mgrinf_grd;	}
	public void setMgrinf_grd_nm(String mgrinf_grd_nm){this.mgrinf_grd_nm	= mgrinf_grd_nm;	}
	public void setMgrinf_nm	(String mgrinf_nm	){this.mgrinf_nm	= mgrinf_nm;	}
	public void setMgrinf_tel	(String mgrinf_tel	){this.mgrinf_tel	= mgrinf_tel;	}
	public void setMgrinf_usyn	(String mgrinf_usyn	){this.mgrinf_usyn	= mgrinf_usyn;	}
	public void setMenuxml		(String menuxml	)    {this.menuxml	= menuxml;	}
	public void setMenufullxml	(String menufullxml	){this.menufullxml	= menufullxml;	}
	public void setUrixml		(JXParser urixml	){this.urixml	= urixml;	}

	public String getMgrinf_id		(){return  mgrinf_id;	}
	public String getMgrinf_pwd		(){return  mgrinf_pwd;	}
	public String getMgrinf_grd		(){return  mgrinf_grd;	}
	public String getMgrinf_grd_nm	(){return  mgrinf_grd_nm;	}
	public String getMgrinf_nm		(){return  mgrinf_nm;	}
	public String getMgrinf_tel		(){return  mgrinf_tel;	}
	public String getMgrinf_usyn	(){return  mgrinf_usyn;	}
	public String getMenuxml		(){return  menuxml;	}
	public String getMenufullxml	(){return  menufullxml;	}
	public JXParser getUrixml		(){return  urixml;	}
}
