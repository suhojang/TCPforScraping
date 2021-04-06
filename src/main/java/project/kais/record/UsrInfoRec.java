package project.kais.record;

import java.io.Serializable;

public class UsrInfoRec implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String cstinf_no;	//회원관리번호
	private String cstinf_id;	//아이디
	private String cstinf_pwd;	//패스워드
	private String cstinf_nm;	//성명
	private String cstinf_cmpnm;	//상호
	private String cstinf_bzno;	//사업자번호
	private String cstinf_rpnm;	//대표자명
	private String cstinf_bzctg;	//업종
	private String cstinf_bzcnd;	//업태
	private String cstinf_tl;	//전화
	private String cstinf_fx;	//팩스
	private String cstinf_hp;	//휴대폰
	private String cstinf_zpno;	//우편번호
	private String cstinf_adr1;	//주소1
	private String cstinf_adr2;	//주소2
	private String cstinf_eml;	//이메일
	private String cstinf_sts;	//고객상태
	private String cstinf_grd;	//고객등급
	private String cstinf_optnt;	//유입경로
	private String cstinf_dsc;	//메모
	private int chnl_seq;	//세무채널일련번호
	private String cstinf_rusr;	//등록자
	private String cstinf_rdt;	//등록일시
	private String chnl_hstnm;	//세무주치의 hostname
	private String chnl_hstnck;	//세무주치의 hostname nick
	
	public void setCSTINF_NO	( String cstinf_no		){ this.cstinf_no		= cstinf_no;	}
	public void setCSTINF_ID	( String cstinf_id		){ this.cstinf_id		= cstinf_id;	}
	public void setCSTINF_PWD	( String cstinf_pwd		){ this.cstinf_pwd		= cstinf_pwd;	}
	public void setCSTINF_NM	( String cstinf_nm		){ this.cstinf_nm		= cstinf_nm;	}
	public void setCSTINF_CMPNM	( String cstinf_cmpnm	){ this.cstinf_cmpnm	= cstinf_cmpnm;	}
	public void setCSTINF_BZNO	( String cstinf_bzno	){ this.cstinf_bzno		= cstinf_bzno;	}
	public void setCSTINF_RPNM	( String cstinf_rpnm	){ this.cstinf_rpnm		= cstinf_rpnm;	}
	public void setCSTINF_BZCTG	( String cstinf_bzctg	){ this.cstinf_bzctg	= cstinf_bzctg;	}
	public void setCSTINF_BZCND	( String cstinf_bzcnd	){ this.cstinf_bzcnd	= cstinf_bzcnd;	}
	public void setCSTINF_TL	( String cstinf_tl		){ this.cstinf_tl		= cstinf_tl;	}
	public void setCSTINF_FX	( String cstinf_fx		){ this.cstinf_fx		= cstinf_fx;	}
	public void setCSTINF_HP	( String cstinf_hp		){ this.cstinf_hp		= cstinf_hp;	}
	public void setCSTINF_ZPNO	( String cstinf_zpno	){ this.cstinf_zpno		= cstinf_zpno;	}
	public void setCSTINF_ADR1	( String cstinf_adr1	){ this.cstinf_adr1		= cstinf_adr1;	}
	public void setCSTINF_ADR2	( String cstinf_adr2	){ this.cstinf_adr2		= cstinf_adr2;	}
	public void setCSTINF_EML	( String cstinf_eml		){ this.cstinf_eml		= cstinf_eml;	}
	public void setCSTINF_STS	( String cstinf_sts		){ this.cstinf_sts		= cstinf_sts;	}
	public void setCSTINF_GRD	( String cstinf_grd		){ this.cstinf_grd		= cstinf_grd;	}
	public void setCSTINF_OPTNT	( String cstinf_optnt	){ this.cstinf_optnt	= cstinf_optnt;	}
	public void setCSTINF_DSC	( String cstinf_dsc		){ this.cstinf_dsc		= cstinf_dsc;	}
	public void setCHNL_SEQ		( int chnl_seq			){ this.chnl_seq		= chnl_seq;		}
	public void setCSTINF_RUSR	( String cstinf_rusr	){ this.cstinf_rusr		= cstinf_rusr;	}
	public void setCSTINF_RDT	( String cstinf_rdt		){ this.cstinf_rdt		= cstinf_rdt;	}
	public void setCHNL_HSTNM	( String chnl_hstnm		){ this.chnl_hstnm		= chnl_hstnm;	}
	public void setCHNL_HSTNCK	( String chnl_hstnck	){ this.chnl_hstnck		= chnl_hstnck;	}
	
	public String getCSTINF_NO		( ){ return cstinf_no;		}
	public String getCSTINF_ID		( ){ return cstinf_id;		}
	public String getCSTINF_PWD		( ){ return cstinf_pwd;		}
	public String getCSTINF_NM		( ){ return cstinf_nm;		}
	public String getCSTINF_CMPNM	( ){ return cstinf_cmpnm;	}
	public String getCSTINF_BZNO	( ){ return cstinf_bzno;	}
	public String getCSTINF_RPNM	( ){ return cstinf_rpnm;	}
	public String getCSTINF_BZCTG	( ){ return cstinf_bzctg;	}
	public String getCSTINF_BZCND	( ){ return cstinf_bzcnd;	}
	public String getCSTINF_TL		( ){ return cstinf_tl;		}
	public String getCSTINF_FX		( ){ return cstinf_fx;		}
	public String getCSTINF_HP		( ){ return cstinf_hp;		}
	public String getCSTINF_ZPNO	( ){ return cstinf_zpno;	}
	public String getCSTINF_ADR1	( ){ return cstinf_adr1;	}
	public String getCSTINF_ADR2	( ){ return cstinf_adr2;	}
	public String getCSTINF_EML		( ){ return cstinf_eml;		}
	public String getCSTINF_STS		( ){ return cstinf_sts;		}
	public String getCSTINF_GRD		( ){ return cstinf_grd;		}
	public String getCSTINF_OPTNT	( ){ return cstinf_optnt;	}
	public String getCSTINF_DSC		( ){ return cstinf_dsc;		}
	public int getCHNL_SEQ			( ){ return chnl_seq;		}
	public String getCSTINF_RUSR	( ){ return cstinf_rusr;	}
	public String getCSTINF_RDT		( ){ return cstinf_rdt;		}
	public String getCHNL_HSTNM		( ){ return chnl_hstnm;		}
	public String getCHNL_HSTNCK	( ){ return chnl_hstnck;	}
	
}
