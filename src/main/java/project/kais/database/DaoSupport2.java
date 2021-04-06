package project.kais.database;

import javax.annotation.Resource;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.kwic.service.DaoSupport;

public class DaoSupport2 extends DaoSupport {

	@Resource(name = "sqlMapClient2")
    public void setSuperSqlMapClient(SqlMapClient sqlMapClient) {
        //set sqlMapclient
        super.setSuperSqlMapClient(sqlMapClient);
    }
}
