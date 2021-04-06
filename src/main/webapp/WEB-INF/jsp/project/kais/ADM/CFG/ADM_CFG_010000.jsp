<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%>
<%@ page import="com.kwic.xml.parser.JXParser" %>
<%@ page import="com.kwic.util.StringUtil"%>
<%@ page import="java.util.Calendar"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/session.jspf"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/header.jspf"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/error.jspf"%>

<%
try{
	/***********************************************************************
	*프로그램명 : ADM_PRS_011000.jsp
	*설명 : 처리현황 조회
	*작성자 : 장정훈
	*작성일자 : 2018.04.10
	*
	*수정자		수정일자		                                                            수정내용
	*----------------------------------------------------------------------
	*
	***********************************************************************/

	java.text.SimpleDateFormat sf	= new java.text.SimpleDateFormat("yyyy-MM-dd");
	Calendar cal	= Calendar.getInstance();
	String schEddt	= sf.format(cal.getTime());
	String schStdt	= schEddt.substring(0,8)+"01";

	String[][] pathArr	= new String[][]{
		{"log4j2.xml","/WEB-INF/classes/log4j2.xml"}
		,{"context-properties.xml","/WEB-INF/classes/egovframework/spring/context-properties.xml"}
		,{"dispatcher-servlet.xml","/WEB-INF/config/egovframework/springmvc/dispatcher-servlet.xml"}
		,{"init-service.xml","/WEB-INF/config/init/init-service.xml"}
		,{"ManageIPs.cfg","/WEB-INF/config/ManageIPs.cfg"}
		,{"web.xml","/WEB-INF/web.xml"}
	};
%>
<%@include file="/WEB-INF/jsp/project/kais/ADM/include/left.jspf" %>

<script type="text/javascript" src="/datatables/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="/datatables/js/dataTables.fixedColumns.js"></script>
<script type="text/javascript" src="/datatables/js/dataTables.select.js"></script>
<script type="text/javascript" src="/manager/js/calendar.js"></script>
<link type="text/css" href="/datatables/css/jquery.dataTables.css" rel="StyleSheet" ></link>
<link type="text/css" href="/datatables/css/fixedColumns.dataTables.css" rel="StyleSheet" ></link>
<link type="text/css" href="/datatables/css/select.dataTables.css" rel="StyleSheet" ></link>
<script src="https://ssl.daumcdn.net/dmaps/map_js_init/postcode.v2.js"></script>

		<div class="row">

			<div class="col-md-12">
				<div class="panel panel-primary">
					<div class="panel-body" style="display:block;">
						<table class="table table-bordered" onkeyup="javascript:if(event.keyCode==13){fn_refresh(1);}">
						<tbody>
								<tr>
									<td class="lefthead">설정파일 경로</td>
									<td>
										<div class="col-sm-9">
											<input type="text" class="form-control" id="CFG_PATH" maxlength="500" placeholder="설정파일 절대경로" style="width:100%;color:#555555;background-color:#fff;border:1px solid #ebebeb;margin-right:5px;">
										</div>
										<div class="col-sm-3">
											<select id="CFG_PATH_LIST" class="form-control" onchange="javascript:fn_set_cfgpath();" style="width:100%;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
												<option value="">- 직접입력 -</option>
												<%
												for(int i=0;i<pathArr.length;i++){
													out.println("<option value='"+StringUtil.replace(application.getRealPath(pathArr[i][1]),"\\","/")+"'>"+pathArr[i][0]+"</option>");
												}
												%>
											</select>
										</div>
									</td>
								</tr>
							</tbody>
							</table>
							<table style="width:100%;margin-top:-10px;">
							<tbody>
								<tr>
									<td style='width:100%;text-align:right;'>
										<button type="button" class="btn btn-info btn-icon icon-left" onclick="javascript:fn_load();" authUri='/ADM_CFG_S1000A/'>
											불러오기
											<i class="entypo-search"></i>
										</button>
									</td>
								</tr>
							</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>

			<div style="margin-top:20px;">
				<table class="table table-bordered">
				<tbody>
					<tr><td class="lefthead" style='text-align:center;'>설정 상세</td></tr>
					<tr>
						<td>
							<div class="col-sm-12">
								<textarea class="form-control" id="CFG_TEXT" placeholder="설정파일 내용" style="height:400px;color:#0066ff;background-color:#fff;border:1px solid #ebebeb;"></textarea>
							</div>
						</td>
					</tr>
				</tbody>
				</table>
			</div>
			<p style="width:100%;text-align:right;margin-top:-10px;">
				<button type="button" class="btn btn-orange btn-icon" onclick="javascript:fn_save();" authUri='/ADM_CFG_U1000A/'>
					저장
					<i class="entypo-check"></i>
				</button>
			</p>
	</div>

<script type="text/javascript">
function fn_init(){

}
function fn_set_cfgpath(){
	$('#CFG_TEXT').val('');
	$('#CFG_PATH').val($('#CFG_PATH_LIST').val());
	fn_load();
}
function fn_load(){
	if($('#CFG_PATH').val()=='')
		return;
	$('#CFG_TEXT').val('');
	$.ajax({
		type: "POST"
		,url: "/ADM_CFG_S1000A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			CFG_PATH:$('#CFG_PATH').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				$('#CFG_TEXT').val(data.CFG_TEXT);
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
function fn_save(){
	if($('#CFG_PATH').val()==''){
		malert('설정파일 내용이 입력되지 않았습니다.');
		return;
	}
	$.ajax({
		type: "POST"
		,url: "/ADM_CFG_U1000A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			CFG_PATH:$('#CFG_PATH').val()
			,CFG_TEXT:$('#CFG_TEXT').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				malert("저장되었습니다.");
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}

jQuery(document).ready(function($){
	fn_init();
});
</script>

<%@include file="/WEB-INF/jsp/project/kais/ADM/include/footer.jspf" %>
<%}catch(Exception e){e.printStackTrace();}%>
