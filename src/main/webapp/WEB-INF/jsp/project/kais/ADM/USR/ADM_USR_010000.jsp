<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/session.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/header.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/error.jspf" %>

<%
try{
	/***********************************************************************
	*프로그램명 : ADM_USR_010000.jsp
	*설명 : 사용자관리
	*작성자 : 장정훈
	*작성일자 : 2017.06.12
	*
	*수정자		수정일자		                                                            수정내용
	*----------------------------------------------------------------------
	*
	***********************************************************************/
%>
<%@include file="/WEB-INF/jsp/project/kais/ADM/include/left.jspf" %>

<script type="text/javascript" src="/datatables/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="/datatables/js/dataTables.fixedColumns.js"></script>
<script type="text/javascript" src="/datatables/js/dataTables.select.js"></script>
<link type="text/css" href="/datatables/css/jquery.dataTables.css" rel="StyleSheet" ></link>
<link type="text/css" href="/datatables/css/fixedColumns.dataTables.css" rel="StyleSheet" ></link>
<link type="text/css" href="/datatables/css/select.dataTables.css" rel="StyleSheet" ></link>

		<div class="row">

			<div class="col-sm-12">

				<div class="tile-block tile-green">
					<div class="tile-header">
						<a href="#">
							사용자 목록
						</a>
					</div>

					<div class="tile-content" style="margin-left:2px;margin-right:2px;margin-top:2px;margin-bottom:2px;overflow:auto;background-color:#ffffff;">
						<div id="TBKW_MGRINF-datatables" style="color:#000">
							<table width="100%" class="stripe row-border order-column nowrap" id="TBKW_MGRINF" cellspacing="0">
								<thead>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>

						<div class="col-md-14" style="margin-top:20px;">
							<table class="table table-bordered">
							<tbody>
								<tr>
									<td class="lefthead" width="100px;">ID</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="MGRINF_ID" maxlength="20" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
											<input type="hidden" id="MGRINF_SEQ"/>
										</div>
									</td>
								</tr>
								<tr>	
									<td class="lefthead">패스워드</td>
									<td>
										<div class="col-sm-12">
											<input type="password" class="form-control" id="MGRINF_PWD" maxlength="20" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">사용자명</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="MGRINF_NM" maxlength="16" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>	
									<td class="lefthead">연락처</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="MGRINF_TEL" maxlength="16" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">권한 등급</td>
									<td>
										<div class="col-sm-8">
											<select id='MGRINF_GRD' class="form-control" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
												<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
												<option value="${MGRINF_GRD.CDDTL_DTLCD}">${MGRINF_GRD.CDDTL_NM}</option>
												</c:forEach>
											</select>
										</div>
									</td>
								</tr>
								<tr>	
									<td class="lefthead">사용여부</td>
									<td>
										<div class="col-sm-5">
											<select id='MGRINF_USYN' class="form-control" style="width:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
												<option value='Y'>Y</option>
												<option value='N'>N</option>
											</select>
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
			<p style="width:100%;text-align:right;margin-top:-10px;">
				<button type="button" class="btn btn-black btn-icon" onclick="javascript:fn_reflesh();" authUri='/ADM_USR_S1000A/'>
					새로고침
					<i class="entypo-check"></i>
				</button>
				<button type="button" class="btn btn-blue btn-icon" onclick="javascript:fn_insert();" authUri='/ADM_USR_I1010A/'>
					추가
					<i class="entypo-check"></i>
				</button>
				<button type="button" class="btn btn-orange btn-icon" onclick="javascript:fn_update();" authUri='/ADM_USR_U1020A/'>
					수정
					<i class="entypo-check"></i>
				</button>
				<button type="button" class="btn btn-red btn-icon" onclick="javascript:fn_delete();" authUri='/ADM_USR_D1030A/'>
					삭제
					<i class="entypo-check"></i>
				</button>
			</p>
		</div>
	</div>
<script type="text/javascript">
function fn_init(){
	fn_set_table();
}

var table		= null;
function fn_set_table(){
	table	= $('#TBKW_MGRINF').DataTable( {
		scrollY:        300
		,fixedColumns:   {
			leftColumns: 0		//앞에 checkbox가 있다면 그 개수도 포함시켜야함
			,rightColumns: 0		//뒤에 hidden컬럼이 있다면 그 개수도 포함시켜야함
		}
		,columns: [
			/* 중요 : hidden field 는 무조건 맨 뒤로 몰아서 넣어야함!!! 매우 중료!!! */
			 { data:"MGRINF_ID"  ,name: "MGRINF_ID", title: "ID", align: "left", width:100, orderable: false}
			,{ data:"MGRINF_GRD_NM"  ,name: "MGRINF_GRD_NM", title: "권한등급", align: "left", width: 100, orderable: false}
			,{ data:"MGRINF_NM"  ,name: "MGRINF_NM", title: "사용자명", align: "left", width: 100, orderable: false}
			,{ data:"MGRINF_TEL"  ,name: "MGRINF_TEL", title: "연락처", align: "center", width: 100, orderable: false}
			,{ data:"MGRINF_USYN"  ,name: "MGRINF_USYN", title: "사용여부", align: "left", width: 100, orderable: false}
			,{ data:"MGRINF_RUSR"  ,name: "MGRINF_RUSR", title: "등록자", align: "left", width: 100, orderable: false}
			,{ data:"MGRINF_RDT"  ,name: "MGRINF_RDT", title: "등록일", align: "left", width: 100, orderable: false}

			,{ data:"MGRINF_SEQ" ,name: "MGRINF_SEQ", visible:false}
			,{ data:"MGRINF_GRD" ,name: "MGRINF_GRD", visible:false}
			,{ data:"MGRINF_PWD" ,name: "MGRINF_PWD", visible:false}
		]
		,bSort : false	//자동 sorting 안함
		,rowClickFunc:		function(evt,rowData){fn_rowClick(rowData);}
		,pagination:		{display:false	, func:function(evt,pageNo){}}
		,resultInfo:		{display:false}
	} );
	fn_reflesh();
}

function fn_rowClick(rowData){
	if(rowData==null)
		return;
	$('#MGRINF_ID').val(rowData['MGRINF_ID']);
	$('#MGRINF_PWD').val(rowData['MGRINF_PWD']);
	$('#MGRINF_GRD').val(rowData['MGRINF_GRD']);
	$('#MGRINF_NM').val(rowData['MGRINF_NM']);
	$('#MGRINF_TEL').val(rowData['MGRINF_TEL']);
	$('#MGRINF_USYN').val(rowData['MGRINF_USYN']);
	if($('#MGRINF_SEQ').val()==rowData['MGRINF_SEQ'])
		return;
	$('#MGRINF_SEQ').val(rowData['MGRINF_SEQ']);
}

//새로고침
function fn_reflesh(){
	$('#MGRINF_SEQ').val('');
	$('#MGRINF_ID').val('');
	$('#MGRINF_PWD').val('');
	$('#MGRINF_GRD').val('02');
	$('#MGRINF_NM').val('');
	$('#MGRINF_TEL').val('');
	$('#MGRINF_USYN').val('Y');

	$.ajax({
		type: "POST"
		,url: "/ADM_USR_S1000A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				table.fnSetDatas(true,data.LIST,null);
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}

//추가
function fn_insert(){
	if($('#MGRINF_ID').val().length<4){
		malert("ID를 4자리 이상으로 입력하여 주십시오.",null,function(){$('#MGRINF_ID').focus();});
		return;
	}
	if($('#MGRINF_PWD').val().length<8){
		malert("패스워드를 8자리 이상으로 입력하여 주십시오.",null,function(){$('#MGRINF_PWD').focus();});
		return;
	}
	if($('#MGRINF_PWD').val()=="00000000"){
		malert("패스워드에 00000000를 사용할 수 없습니다.",null,function(){$('#MGRINF_PWD').focus();});
		return;
	}
	if($('#MGRINF_NM').val()==''){
		malert("사용자명을 입력하여 주십시오.",null,function(){$('#MGRINF_NM').focus();});
		return;
	}
	mconfirm("사용자를 등록하시겠습니까?",null,function(){fn_insert_confirm();});
}
function fn_insert_confirm(){
	$.ajax({
		type: "POST"
		,url: "/ADM_USR_I1010A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"MGRINF_ID":$('#MGRINF_ID').val()
			,"MGRINF_PWD":$('#MGRINF_PWD').val()
			,"MGRINF_GRD":$('#MGRINF_GRD').val()
			,"MGRINF_NM":$('#MGRINF_NM').val()
			,"MGRINF_TEL":$('#MGRINF_TEL').val()
			,"MGRINF_USYN":$('#MGRINF_USYN').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_set_table();
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
//사용자 수정
function fn_update(){
	if($('#MGRINF_SEQ').val()==''){
		malert("사용자를 선택하여 주십시오.");
		return;
	}
	if($('#MGRINF_ID').val().length<4){
		malert("ID를 4자리 이상으로 입력하여 주십시오.",null,function(){$('#MGRINF_ID').focus();});
		return;
	}
	if($('#MGRINF_PWD').val().length<4){
		malert("패스워드를 4자리 이상으로 입력하여 주십시오.",null,function(){$('#MGRINF_PWD').focus();});
		return;
	}
	if($('#MGRINF_NM').val()==''){
		malert("사용자명을 입력하여 주십시오.",null,function(){$('#MGRINF_NM').focus();});
		return;
	}
	mconfirm("수정하시겠습니까?",null,function(){fn_update_confirm();});
}
function fn_update_confirm(){

	$.ajax({
		type: "POST"
		,url: "/ADM_USR_U1020A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"MGRINF_SEQ":$('#MGRINF_SEQ').val()
			,"MGRINF_ID":$('#MGRINF_ID').val()
			,"MGRINF_PWD":$('#MGRINF_PWD').val()
			,"MGRINF_GRD":$('#MGRINF_GRD').val()
			,"MGRINF_NM":$('#MGRINF_NM').val()
			,"MGRINF_TEL":$('#MGRINF_TEL').val()
			,"MGRINF_USYN":$('#MGRINF_USYN').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_set_table();
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
//사용자 삭제
function fn_delete(){
	if($('#MGRINF_SEQ').val()==''){
		malert("사용자를 선택하여 주십시오.");
		return;
	}
	mconfirm("사용자를 삭제하시겠습니까?",null,function(){fn_delete_confirm();});
}
function fn_delete_confirm(){

	$.ajax({
		type: "POST"
		,url: "/ADM_USR_D1030A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"MGRINF_SEQ":$('#MGRINF_SEQ').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				malert("삭제되었습니다.",null,function(){fn_set_table();});
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
