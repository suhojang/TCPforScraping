<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%>
<%@ page import="com.kwic.xml.parser.JXParser" %>
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
								<tr style="display:none;">
									<td class="lefthead">월 선택</td>
									<td>
										<div class="col-sm-4">
											<select id="YYMM" class="form-control" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
											<%
											int yyyy	= 0;
											int mm		= 0;
											Calendar cal2	= Calendar.getInstance();
											yyyy	= cal2.get(Calendar.YEAR);
											mm		= cal2.get(Calendar.MONTH);

											cal2.set(Calendar.YEAR, 2018);
											cal2.set(Calendar.MONTH, 3);
											cal2.set(Calendar.DAY_OF_MONTH, 1);
											String yyMM	= "";
											while(true){
												if(cal2.get(Calendar.YEAR)>yyyy)
													break;
												if(cal2.get(Calendar.YEAR)>=yyyy && cal2.get(Calendar.MONTH)>mm)
													break;
												yyMM	= String.valueOf(cal2.get(Calendar.YEAR)).substring(2)+""+(cal2.get(Calendar.MONTH)<9?"0":"")+(cal2.get(Calendar.MONTH)+1);

												out.println("<option value='"+yyMM+"'>"+cal2.get(Calendar.YEAR)+"년 "+(cal2.get(Calendar.MONTH)+1)+"월"+"</option>");

												cal2.add(Calendar.MONTH,1);
											}
											%>
											</select>
										</div>
									</td>
								</tr>

								<tr>
									<td class="lefthead">상품코드</td>
									<td>
										<div class="col-sm-2">
											<input type="text" class="form-control" id="SCDL_G_BZCD" maxlength="2" placeholder="나이스 상품코드" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;margin-right:5px;">
										</div>
										<div class="col-sm-2">
											<input type="text" class="form-control" id="SCDL_BZCD" maxlength="4" placeholder="기웅 전문코드" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
										<div class="col-sm-2">
											<input type="text" class="form-control" id="SCDL_JBCD" maxlength="3" placeholder="기웅 업무코드" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>

								<tr>
									<td class="lefthead">SCAF ID</td>
									<td>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="SCDL_SCAF_ID" maxlength="12" placeholder="SCAF ID" style="width:200px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">처리상태</td>
									<td>
										<div class="col-sm-8">
											<select id="SCDL_STS" class="form-control" style="width:300px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
												<option value=""> - 처리상태 -</option>
												<c:forEach var="SCDL_STS" items="${CD0002}" varStatus="status">
												<option value="${SCDL_STS.CDDTL_DTLCD}">(${SCDL_STS.CDDTL_DTLCD}) ${SCDL_STS.CDDTL_NM}</option>
												</c:forEach>
											</select>
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">처리결과</td>
									<td>
										<div class="col-sm-2">
											<select id="SCDL_RTCD" class="form-control" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
												<option value=""> - 오류구분 -</option>
												<c:forEach var="SCDL_RTCD" items="${CD0003}" varStatus="status">
												<option value="${SCDL_RTCD.CDDTL_DTLCD}">(${SCDL_RTCD.CDDTL_DTLCD}) ${SCDL_RTCD.CDDTL_NM}</option>
												</c:forEach>
											</select>
										</div>
									</td>
								</tr>
								<tr>
								<td class="lefthead">요청일</td>
								<td colspan="3">
									<div class="col-sm-2" style="padding-right:10px;">
										<input type="text" class="form-control datepicker" id="SCDL_RDT_FR" data-format="yyyy-mm-dd" maxlength="10" placeholder="요청일" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
									</div>
									<div class="col-sm-1" style='text-align:center;width:10px;padding-left:20px;padding-right:0px;'>
										~
									</div>
									<div class="col-sm-2" style="padding-left:20px;">
										<input type="text" class="form-control datepicker" id="SCDL_RDT_TO" data-format="yyyy-mm-dd" maxlength="10" placeholder="요청일" style="width:150px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
									</div>
									<b>(* 한달 단위로만 조회가 가능 합니다.)</b> ex) 20190201 ~ 20190331
								</td>
								</tr>
							</tbody>
							</table>
							<table style="width:100%;margin-top:-10px;">
							<tbody>
								<tr>
									<td style='width:100%;text-align:right;'>
										<button type="button" class="btn btn-info btn-icon icon-left" onclick="javascript:fn_refresh(1);" authUri='/ADM_PRS_S1000A/'>
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
						<div id="TBKW_SCDL-datatables" style="color:#000">
							<table width="100%" class="stripe row-border order-column nowrap" id="TBKW_SCDL" cellspacing="0">
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
						<tr><td class="lefthead" style='text-align:center;'>스크래핑 요청 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="NCRQ_SCRQ" placeholder="스크래핑 요청 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						<tr><td class="lefthead" style='text-align:center;'>스크래핑 응답 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="NCRQ_SCRS" placeholder="스크래핑 응답 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						<tr><td class="lefthead" style='text-align:center;'>요청 상품 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_OSTR" placeholder="요청 상품 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						<tr><td class="lefthead" style='text-align:center;'>AIB 요청 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_ABRQ" placeholder="AIB 요청 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						<tr><td class="lefthead" style='text-align:center;'>AIB 응답 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_ABRS" placeholder="AIB 응답 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						<tr><td class="lefthead" style='text-align:center;'>정보등록 요청 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_RTRQ" placeholder="정보등록 요청 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						<tr><td class="lefthead" style='text-align:center;'>정보등록 응답 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_RTRS" placeholder="정보등록 응답 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						
						<tr><td class="lefthead" style='text-align:center;'>원본저장 요청 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_AFRQ" placeholder="원본저장 요청 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						<tr><td class="lefthead" style='text-align:center;'>원본저장 응답 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_AFRS" placeholder="원본저장 응답 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						
            			<tr><td class="lefthead" style='text-align:center;'>로그등록 요청 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_LTRQ" placeholder="로그등록 요청 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
						<tr><td class="lefthead" style='text-align:center;'>로그등록 응답 전문</td></tr>
						<tr>
							<td>
								<div class="col-sm-12">
									<textarea class="form-control" id="SCDL_LTRS" placeholder="로그등록 응답 전문" style="height:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;"></textarea>
								</div>
							</td>
						</tr>
					</tbody>
					</table>
			</div>
	<input type="hidden" name="SCDL_SEQ" id="SCDL_SEQ"/>
			</div>
<script type="text/javascript">
var pageNo	= 1;	//현재 페이지 번호

function fn_init(){
	$('#SCDL_RDT_FR').datepicker({ dateFormat: 'yyyy-mm-dd' });
	$('#SCDL_RDT_FR').data("datepicker")._setDate('<%=schStdt%>');
	$('#SCDL_RDT_TO').datepicker({ dateFormat: 'yyyy-mm-dd' });
	$('#SCDL_RDT_TO').data("datepicker")._setDate('<%=schEddt%>');

	fn_set_table();
}

var table		= null;
function fn_set_table(){
	$('#TBKW_SCDL').remove();
	$('#TBKW_SCDL-datatables').html("<table width='100%' class='stripe row-border order-column nowrap' id='TBKW_SCDL' cellspacing='0'><thead></thead><tbody></tbody></table>");
	table	= $('#TBKW_SCDL').DataTable( {
		scrollY:        365
		,fixedColumns:   {
			leftColumns: 0		//앞에 checkbox가 있다면 그 개수도 포함시켜야함
			,rightColumns: 0	//뒤에 hidden컬럼이 있다면 그 개수도 포함시켜야함
		}
		,columns: [
			/* 중요 : hidden field 는 무조건 맨 뒤로 몰아서 넣어야함!!! 매우 중료!!! */
			 { data:"NO",           name: "NO",           title: "순번",         align: "left", width: 80, orderable: false}
			,{ data:"NCRQ_BZCD",    name: "NCRQ_BZCD",    title: "거래코드",      align: "left", width: 100, orderable: false}
			,{ data:"SCDL_SEQ",     name: "SCDL_SEQ",     title: "AIB거래번호",   align: "left", width: 100, orderable: false}
			,{ data:"SCDL_SCAF_ID", name: "SCDL_SCAF_ID", title: "SCAF ID",     align: "left", width: 100, orderable: false}
			,{ data:"SCDL_ABIP",    name: "SCDL_ABIP",    title: "SCRAP IP",    align: "left", width: 100, orderable: false}
			,{ data:"NCRQ_RQTM",    name: "NCRQ_RQTM",    title: "요청일시",      align: "left", width: 100, orderable: false}
			,{ data:"SCDL_MBRNO",   name: "SCDL_MBRNO",   title: "고객사회원번호",  align: "left", width: 100, orderable: false}
			,{ data:"SCDL_BZCD",    name: "SCDL_BZCD",    title: "상품코드",      align: "left", width: 100, orderable: false}
			,{ data:"SCDL_STS",     name: "SCDL_STS",     title: "처리상태",      align: "left", width: 100, orderable: false}
			,{ data:"SCDL_RTCD",    name: "SCDL_RTCD",    title: "결과코드",      align: "left", width: 100, orderable: false}
			,{ data:"SCDL_RTMSG",   name: "SCDL_RTMSG",   title: "처리메시지",    align: "left", width: 100, orderable: false}
			,{ data:"SCDL_NQTM",    name: "SCDL_NQTM",    title: "정보등록시각",   align: "left", width: 100, orderable: false}
			,{ data:"SCDL_NRTCD",   name: "SCDL_NRTCD",   title: "정보등록결과",   align: "left", width: 100, orderable: false}

			,{ data:"YYMM" ,name: "YYMM", visible:false}
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
	$('#SCDL_SEQ').val(rowData['SCDL_SEQ']);

	$.ajax({
		type: "POST"
		,url: "/ADM_PRS_V1000A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			SCDL_SEQ:$('#SCDL_SEQ').val()
			,YYMM:rowData['YYMM']
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
	$('#NCRQ_SCRQ').val(info.NCRQ_SCRQ);
	$('#NCRQ_SCRS').val(info.NCRQ_SCRS);
	$('#SCDL_OSTR').val(info.SCDL_OSTR);
	$('#SCDL_ABRQ').val(info.SCDL_ABRQ);
	$('#SCDL_ABRS').val(info.SCDL_ABRS);
	$('#SCDL_AFRQ').val(info.SCDL_AFRQ);
	$('#SCDL_AFRS').val(info.SCDL_AFRS);
	$('#SCDL_RTRQ').val(info.SCDL_RTRQ);
	$('#SCDL_RTRS').val(info.SCDL_RTRS);
    $('#SCDL_LTRQ').val(info.SCDL_LTRQ);
	$('#SCDL_LTRS').val(info.SCDL_LTRS);
}

function fn_datainit(){
	$('#SCDL_SEQ').val( '');
}

//새로고침
function fn_refresh(no){
	var SCDL_RDT_FR	= $("#SCDL_RDT_FR").val().replaceAll('-','');
	var SCDL_RDT_TO	= $("#SCDL_RDT_TO").val().replaceAll('-','');
	
	var fr	= SCDL_RDT_FR.substring(2, 6);
	var to	= SCDL_RDT_TO.substring(2, 6);
	
	if(Number(to) - Number(fr) >= 2){
		alert("한달 단위로만 조회가 가능 합니다.");
		return;
	}
	
	pageNo	= no;
	fn_datainit();
	$.ajax({
		type: "POST"
		,url: "/ADM_PRS_S1000A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			SCDL_RDT_FR:SCDL_RDT_FR
			,SCDL_RDT_TO:SCDL_RDT_TO
			,SCDL_G_BZCD : $('#SCDL_G_BZCD').val()
			,SCDL_BZCD : $('#SCDL_BZCD').val()
			,SCDL_JBCD : $('#SCDL_JBCD').val()
			,SCDL_SCAF_ID : $('#SCDL_SCAF_ID').val()
			,SCDL_STS : $('#SCDL_STS').val()
			,SCDL_RTCD : $('#SCDL_RTCD').val()
			,PAGE_NO:pageNo
			,ROWPERPAGE:"10"
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				table.fnSetDatas(true,data.LIST,null);
				table.setPageInfo(data.TCNT,pageNo,10,10);//총건수,페이지번호,블럭당페이지수,페이지당건수
				$('#TCNT').html(data.TCNT);
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
