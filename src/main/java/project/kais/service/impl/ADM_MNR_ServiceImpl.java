package project.kais.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import project.kais.service.ADM_MNR_Service;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;

/**
 * @Class Name : ADM_MNR_ServiceImpl.java
 * @Description : 모니터링
 * @Modification Information
 * @
 * @  수정일      수정자              수정내용
 * @ ---------   ---------   -------------------------------
 * @ 2016.11.07           최초생성
 *
 * @author 기웅정보통신
 * @since 2016. 11.07
 * @version 1.0
 * @see
 *
 *  Copyright (C) by kwic All right reserved.
 */

@Service("ADM_MNR_Service")
public class ADM_MNR_ServiceImpl extends EgovAbstractServiceImpl implements ADM_MNR_Service {

	@Resource(name = "ADM_MNR_Dao")
	private ADM_MNR_Dao dao;

}
