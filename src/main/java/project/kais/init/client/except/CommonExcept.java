package project.kais.init.client.except;

import java.util.Map;

public class CommonExcept extends Except {

	@Override
	public Map<String, String> exceptRequest(Map<String, String> aibReqParam, Map<String, Object> request, Map<String, Object> sysParam) throws Exception {
		return aibReqParam;
	}

	@Override
	public Map<String, Object> exceptResponse(Map<String, Object> aibResParam, Map<String, Object> response, Map<String, Object> sysParam) throws Exception {
		return aibResParam;
	}

}
