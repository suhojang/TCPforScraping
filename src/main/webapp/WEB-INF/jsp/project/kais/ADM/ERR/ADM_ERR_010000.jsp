<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%><%@
 page import="com.kwic.xml.parser.JXParser" %><%@
 include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf" %><%@
 include file="/WEB-INF/jsp/project/kais/ADM/include/session.jspf" %><%@
 include file="/WEB-INF/jsp/project/kais/ADM/include/header.jspf" %><%@
 include file="/WEB-INF/jsp/project/kais/ADM/include/error.jspf" %><%
try{
	/***********************************************************************
	*프로그램명 : ADM_ERR_011000.jsp
	*설명 : 처리불가건 조회
	*작성자 : 장정훈
	*작성일자 : 2018.04.10
	*
	*수정자		수정일자		                                                            수정내용
	*----------------------------------------------------------------------
	*
	***********************************************************************/

	java.text.SimpleDateFormat sf	= new java.text.SimpleDateFormat("yyyy-MM-dd");
	java.util.Calendar cal	= java.util.Calendar.getInstance();
	String schEddt	= sf.format(cal.getTime());
	String schStdt	= schEddt.substring(0,8)+"01";
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
					<div class="panel-heading">
						<div class="panel-title">검색조건</div>

						<div class="panel-options">
							<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a>
						</div>
					</div>
					<div class="panel-body" style="display:block;">
						<table class="table table-bordered" onkeyup="javascript:if(event.keyCode==13){fn_refresh(1);}">
						<tbody>
							<tr>
								<td class="lefthead">요청일</td>
								<td colspan="3">
									<div class="col-sm-2" style="padding-right:10px;">
										<input type="text" class="form-control datepicker" id="NCRQER_RDT_FR" data-format="yyyy-mm-dd" maxlength="10" placeholder="요청일" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
									</div>
									<div class="col-sm-1" style='text-align:center;width:10px;padding-left:20px;padding-right:0px;'>
										~
									</div>
									<div class="col-sm-2" style="padding-left:20px;">
										<input type="text" class="form-control datepicker" id="NCRQER_RDT_TO" data-format="yyyy-mm-dd" maxlength="10" placeholder="요청일" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
									</div>
									<div class="col-sm-5" style="padding-left:20px;">
										<button type="button" class="btn btn-sm" style="border:0px;background-color:#33ccff;" onclick="javascript:fn_set_schprd('1d');">당일</button>
										<button type="button" class="btn btn-sm" style="border:0px;background-color:#33ccff;" onclick="javascript:fn_set_schprd('1m');">1개월</button>
										<button type="button" class="btn btn-sm" style="border:0px;background-color:#33ccff;" onclick="javascript:fn_set_schprd('2m');">2개월</button>
										<button type="button" class="btn btn-sm" style="border:0px;background-color:#33ccff;" onclick="javascript:fn_set_schprd('3m');">3개월</button>
									</div>
								</td>
								</tr>
								<tr>
									<td class="lefthead">오류코드</td>
									<td>
										<div class="col-sm-2">
											<select id="NCRQER_RTCD" class="form-control" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
												<option value=""> - 오류구분 -</option>
												<c:forEach var="NCRQER_RTCD" items="${CD0003}" varStatus="status">
												<option value="${NCRQER_RTCD.CDDTL_DTLCD}">(${NCRQER_RTCD.CDDTL_DTLCD}) ${NCRQER_RTCD.CDDTL_NM}</option>
												</c:forEach>
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
										<button type="button" class="btn btn-info btn-icon icon-left" onclick="javascript:fn_refresh(1);" authUri='/ADM_ERR_S1000A/'>
											검색
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
			<div class="col-sm-12" style="padding:0px;margin:0px;">
				<div style="width:100%;right-margin:5px;text-align:right;padding-bottom:5px;"><span id='TCNT' style='font-weight:bold;color:#ff6600;'></span>건이 조회되었습니다.</div>
				<div class="tile-block tile-green">
					<div class="tile-content" style="margin-left:2px;margin-right:2px;margin-top:2px;margin-bottom:2px;overflow:auto;background-color:#ffffff;">
						<div id="TBSS_NCRQER-datatables" style="color:#000">
							<table width="100%" class="stripe row-border order-column nowrap" id="TBSS_NCRQER" cellspacing="0">
								<thead>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>
					</div>
				</div>
				<div style="margin-top:20px;">
					<table class="table table-bordered">
					<tbody>
						<tr><td class="lefthead" style='text-align:center;'>요청 전문 확인</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="NCRQER_SCRQ" placeholder="요청전문" style="height:200px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
					</tbody>
					</table>
			</div>
		</div>
	</div>
	<input type="hidden" name="NCRQER_SEQ" id="NCRQER_SEQ"/>

<script type="text/javascript">
var pageNo	= 1;	//현재 페이지 번호

function fn_init(){
	$('#NCRQER_RDT_FR').datepicker({ dateFormat: 'yyyy-mm-dd' });
	$('#NCRQER_RDT_FR').data("datepicker")._setDate('<%=schStdt%>');
	$('#NCRQER_RDT_TO').datepicker({ dateFormat: 'yyyy-mm-dd' });
	$('#NCRQER_RDT_TO').data("datepicker")._setDate('<%=schEddt%>');

	fn_set_table();
}

var table		= null;
function fn_set_table(){
	$('#TBSS_NCRQER').remove();
	$('#TBSS_NCRQER-datatables').html("<table width='100%' class='stripe row-border order-column nowrap' id='TBSS_NCRQER' cellspacing='0'><thead></thead><tbody></tbody></table>");
	table	= $('#TBSS_NCRQER').DataTable( {
		scrollY:        300
		,fixedColumns:   {
			leftColumns: 0		//앞에 checkbox가 있다면 그 개수도 포함시켜야함
			,rightColumns: 0	//뒤에 hidden컬럼이 있다면 그 개수도 포함시켜야함
		}
		,columns: [
			/* 중요 : hidden field 는 무조건 맨 뒤로 몰아서 넣어야함!!! 매우 중료!!! */
			{ data:"NO"  ,name: "NO", title: "순번", align: "left", width: 80, orderable: false}	//, render: $.fn.dataTable.render.cut(30)
			,{ data:"NCRQER_RDT"  ,name: "NCRQER_RDT", title: "요청일시", align: "left", width: 100, orderable: false}
			,{ data:"NCRQER_RTCD"  ,name: "NCRQER_RTCD", title: "오류코드", align: "left", width: 100, orderable: false}
			,{ data:"NCRQER_RTMSG"  ,name: "NCRQER_RTMSG", title: "오류메시지", align: "left", width: 100, orderable: false}

			,{ data:"NCRQER_SEQ" ,name: "NCRQER_SEQ", visible:false}
		]
		,selectCheckbox:	{display:false}	//period:total,page
		,bSort : false	//자동 sorting 안함
		,rowClickFunc:		function(evt,rowData){fn_rowClick(rowData);}
		,pagination:		{display:true	, func:function(evt,pageNo){fn_refresh(pageNo);}}
		,resultInfo:		{display:false}
	} );
	fn_refresh(1);
}

function fn_rowClick(rowData){
	if(rowData==null)
		return;
	$('#NCRQER_SEQ').val(rowData['NCRQER_SEQ']);

	$.ajax({
		type: "POST"
		,url: "/ADM_ERR_V1000A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			NCRQER_SEQ:$('#NCRQER_SEQ').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_setinfo(data.INFO);
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}

function fn_setinfo(info){
	$('#NCRQER_SCRQ')	.val( info.NCRQER_SCRQ);
}

function fn_datainit(){
	$('#NCRQER_SCRQ')	.val( '');
}
//새로고침
function fn_refresh(no){
	pageNo	= no;
	fn_datainit();
	$.ajax({
		type: "POST"
		,url: "/ADM_ERR_S1000A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			NCRQER_RDT_FR:$("#NCRQER_RDT_FR").val().replaceAll('-','')
			,NCRQER_RDT_TO:$("#NCRQER_RDT_TO").val().replaceAll('-','')
			,NCRQER_RTCD : $('#NCRQER_RTCD').val()
			,PAGE_NO:pageNo
			,ROWPERPAGE:"50"
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				table.fnSetDatas(true,data.LIST,null);
				table.setPageInfo(data.TCNT,pageNo,10,50);//총건수,페이지번호,블럭당페이지수,페이지당건수
				$('#TCNT').html(data.TCNT);
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}

//조회기간셋팅
function fn_set_schprd(prd){
	$('#NCRQER_RDT_TO').val("<%=schEddt%>");

	var stdt	= "<%=schEddt%>";
	if(prd=='1d'){
		stdt	= "<%=schEddt%>";
	}else if(prd=='1m'){
		stdt	= new JDate(true,"<%=schEddt%>").beforeMonth(1).afterDay(1).format('yyyy-mm-dd');
	}else if(prd=='2m'){
		stdt	= new JDate(true,"<%=schEddt%>").beforeMonth(2).afterDay(1).format('yyyy-mm-dd');
	}else if(prd=='3m'){
		stdt	= new JDate(true,"<%=schEddt%>").beforeMonth(3).afterDay(1).format('yyyy-mm-dd');
	}
	$('#NCRQER_RDT_FR').val(stdt);
}

jQuery(document).ready(function($){
	fn_init();
});
</script>

<%@include file="/WEB-INF/jsp/project/kais/ADM/include/footer.jspf" %>
<%}catch(Exception e){e.printStackTrace();}%>
