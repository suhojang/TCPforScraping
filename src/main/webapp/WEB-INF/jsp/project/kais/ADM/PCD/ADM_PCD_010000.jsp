<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/session.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/header.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/error.jspf" %>
 
 <%
try{
	/***********************************************************************
	*프로그램명 : ADM_PCD_010000.jsp
	*설명 : 메뉴관리
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

			<div class="col-sm-6">

				<div class="tile-block tile-green">

					<div class="tile-header">
						<a href="#">
							코드 분류
						</a>
					</div>

					<div class="tile-content" style="margin-left:2px;margin-right:2px;margin-top:2px;margin-bottom:2px;height:540px;overflow:auto;background-color:#ffffff;">
						<div id="TBKW_CDCLS-datatables" style="color:#000">
							<table width="100%" class="stripe row-border order-column nowrap" id="TBKW_CDCLS" cellspacing="0">
								<thead>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>

						<div style="margin-top:10px;">
							<table class="table table-bordered">
							<tbody>
								<tr>
									<td class="lefthead" width="100px;">분류코드</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="CDCLS_CLSCD" maxlength="4" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">분류명</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="CDCLS_NM" maxlength="16" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">상세코드 크기</td>
									<td>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="CDCLS_LEN" maxlength="3" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
							</tbody>
						</table>
					</div>

					</div>
				</div>
				<p style="width:100%;text-align:right;margin-top:-10px;">
					<button type="button" class="btn btn-black btn-icon" onclick="javascript:fn_cls_reflesh();" authUri='/ADM_PCD_S1100A/'>
						새로고침
						<i class="entypo-check"></i>
					</button>
					<button type="button" class="btn btn-blue btn-icon" onclick="javascript:fn_cls_insert();" authUri='/ADM_PCD_I1110A/'>
						추가
						<i class="entypo-check"></i>
					</button>
					<button type="button" class="btn btn-orange btn-icon" onclick="javascript:fn_cls_update();" authUri='/ADM_PCD_U1120A/'>
						수정
						<i class="entypo-check"></i>
					</button>
					<button type="button" class="btn btn-red btn-icon" onclick="javascript:fn_cls_delete();" authUri='/ADM_PCD_D1130A/'>
						삭제
						<i class="entypo-check"></i>
					</button>
				</p>
			</div>


			<div class="col-sm-6">
				<div class="tile-block" style="background:#3333cc;">
					<div class="tile-header">
						<a href="#">
							코드 상세
						</a>
					</div>

					<div class="tile-content" style="margin-left:2px;margin-right:2px;margin-top:2px;margin-bottom:2px;height:540px;background-color:#ffffff;">
						<div id="TBKW_CDDTL-datatables" style="color:#000">
							<table width="100%" class="stripe row-border order-column nowrap" id="TBKW_CDDTL" cellspacing="0">
								<thead>
								</thead>
								<tbody>
								</tbody>
							</table>
						</div>

						<div style="margin-top:10px;">
							<table class="table table-bordered">
							<tbody>
								<tr>
									<td class="lefthead" width="100px;">상세코드</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="CDDTL_DTLCD" maxlength="4" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">코드명</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="CDDTL_NM" maxlength="33" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">정렬순서</td>
									<td>
										<div class="col-sm-8">
											<input type="text" class="form-control" id="CDDTL_ORD" maxlength="4" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">사용여부</td>
									<td>
										<div class="col-sm-5">
											<select id='CDDTL_USEYN' class="form-control" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
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
				<button type="button" class="btn btn-black btn-icon" onclick="javascript:fn_dtl_reflesh();" authUri='/ADM_PCD_S1200A/'>
					새로고침
					<i class="entypo-check"></i>
				</button>
				<button type="button" class="btn btn-blue btn-icon" onclick="javascript:fn_dtl_insert();" authUri='/ADM_PCD_I1210A/'>
					추가
					<i class="entypo-check"></i>
				</button>
				<button type="button" class="btn btn-orange btn-icon" onclick="javascript:fn_dtl_update();" authUri='/ADM_PCD_U1220A/'>
					수정
					<i class="entypo-check"></i>
				</button>
				<button type="button" class="btn btn-red btn-icon" onclick="javascript:fn_dtl_delete();" authUri='/ADM_PCD_D1230A/'>
					삭제
					<i class="entypo-check"></i>
				</button>
			</p>
		</div>
	</div>
<script type="text/javascript">
function fn_init(){
	fn_set_table_cls();
	fn_set_table_dtl();
}

var tableCls	= null;
var tableDtl	= null;
function fn_set_table_cls(){
	tableCls	= $('#TBKW_CDCLS').DataTable( {
		scrollY:        260
		,fixedColumns:   {
			leftColumns: 0		//앞에 checkbox가 있다면 그 개수도 포함시켜야함
			,rightColumns: 0		//뒤에 hidden컬럼이 있다면 그 개수도 포함시켜야함
		}
		,columns: [
			/* 중요 : hidden field 는 무조건 맨 뒤로 몰아서 넣어야함!!! 매우 중료!!! */
			 { data:"CDCLS_CLSCD"  ,name: "CDCLS_CLSCD", title: "분류코드", align: "left", width:100, orderable: false}
			,{ data:"CDCLS_NM"  ,name: "CDCLS_NM", title: "분류명", align: "left", width: 200, orderable: false}
			,{ data:"CDCLS_LEN"  ,name: "CDCLS_LEN", title: "상세코드 크기", align: "center", width: 100, orderable: false}
		]
		,bSort : false	//자동 sorting 안함
		,rowClickFunc:		function(evt,rowData){fn_rowClick_Cls(rowData);}
		,pagination:		{display:false	, func:function(evt,pageNo){}}
		,resultInfo:		{display:false}
	} );
	fn_cls_reflesh();
}
function fn_set_table_dtl(){
	tableDtl	= $('#TBKW_CDDTL').DataTable( {
		scrollY:        230
		,fixedColumns:   {
			leftColumns: 0		//앞에 checkbox가 있다면 그 개수도 포함시켜야함
			,rightColumns: 0		//뒤에 hidden컬럼이 있다면 그 개수도 포함시켜야함
		}
		,columns: [
			/* 중요 : hidden field 는 무조건 맨 뒤로 몰아서 넣어야함!!! 매우 중료!!! */
			 { data:"CDDTL_DTLCD"  ,name: "CDDTL_DTLCD", title: "코드", align: "left", width:100, orderable: false}
			,{ data:"CDDTL_NM"  ,name: "CDDTL_NM", title: "코드명", align: "left", width: 200, orderable: false}
			,{ data:"CDDTL_ORD"  ,name: "CDDTL_ORD", title: "정렬순서", align: "center", width: 100, orderable: false}
			,{ data:"CDDTL_USEYN"  ,name: "CDDTL_USEYN", title: "사용여부", align: "center", width: 100, orderable: false}
		]
		,bSort : false	//자동 sorting 안함
		,rowClickFunc:		function(evt,rowData){fn_rowClick_Dtl(rowData);}
		,pagination:		{display:false	, func:function(evt,pageNo){}}
		,resultInfo:		{display:false}
	} );
	fn_dtl_reflesh();
}

function fn_rowClick_Cls(rowData){
	$('#CDCLS_NM').val(rowData['CDCLS_NM']);
	$('#CDCLS_LEN').val(rowData['CDCLS_LEN']);
	$('#CDDTL_DTLCD').attr('maxlength', 4);
	//$('#CDDTL_DTLCD').attr('maxlength',rowData['CDCLS_LEN']);
	$('#CDDTL_DTLCD').val('');
	$('#CDDTL_NM').val('');
	$('#CDDTL_ORD').val('');
	$('#CDDTL_USEYN').val('Y');

	if($('#CDCLS_CLSCD').val()==rowData['CDCLS_CLSCD'])
		return;
	$('#CDCLS_CLSCD').val(rowData['CDCLS_CLSCD']);
	//상세코드 조회
	fn_set_table_dtl();
}

function fn_rowClick_Dtl(rowData){
	$('#CDDTL_DTLCD').val(rowData['CDDTL_DTLCD']);
	$('#CDDTL_NM').val(rowData['CDDTL_NM']);
	$('#CDDTL_ORD').val(rowData['CDDTL_ORD']);
	$('#CDDTL_USEYN').val(rowData['CDDTL_USEYN']);
}

//분류코드 목록 새로고침
function fn_cls_reflesh(){
	$('#CDCLS_CLSCD').val('');
	$('#CDCLS_NM').val('');
	$('#CDCLS_LEN').val('');
	$('#CDDTL_DTLCD').val('');
	$('#CDDTL_NM').val('');
	$('#CDDTL_ORD').val('');
	$('#CDDTL_USEYN').val('Y');
	fn_set_table_dtl();

	$.ajax({
		type: "POST"
		,url: "/ADM_PCD_S1100A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				tableCls.fnSetDatas(true,data.LIST,null);
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}

//상세코드 목록 새로고침
function fn_dtl_reflesh(){
	if($('#CDCLS_CLSCD').val()==""){
		tableDtl.fnSetDatas(true,[],null);
		return;
	}
	$('#CDDTL_DTLCD').val('');
	$('#CDDTL_NM').val('');
	$('#CDDTL_ORD').val('');
	$('#CDDTL_USEYN').val('Y');
	$.ajax({
		type: "POST"
		,url: "/ADM_PCD_S1200A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"CDCLS_CLSCD":$('#CDCLS_CLSCD').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				tableDtl.fnSetDatas(true,data.LIST,null);
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}

//분류코드 추가
function fn_cls_insert(){
	if($('#CDCLS_CLSCD').val().length != 4){
		malert("분류코드를 4자리 숫자로 입력하여 주십시오.",null,function(){$('#CDCLS_CLSCD').focus();});
		return;
	}
	if($('#CDCLS_NM').val()==""){
		malert("분류명을 입력하여 주십시오.",null,function(){$('#CDCLS_NM').focus();});
		return;
	}
	if($('#CDCLS_LEN').val()==""){
		malert("상세코드 크기를 입력하여 주십시오.",null,function(){$('#CDCLS_LEN').focus();});
		return;
	}

	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDCLS_CLSCD').get(0),"분류코드")){return;}
	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDCLS_LEN').get(0),"상세코드 크기")){return;}

	$.ajax({
		type: "POST"
		,url: "/ADM_PCD_I1110A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"CDCLS_CLSCD":$('#CDCLS_CLSCD').val()
			,"CDCLS_NM":$('#CDCLS_NM').val()
			,"CDCLS_LEN":$('#CDCLS_LEN').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_set_table_cls();
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
//분류코드 수정
function fn_cls_update(){
	if($('#CDCLS_CLSCD').val().length!=4){
		malert("분류코드를 4자리 숫자로 입력하여 주십시오.",null,function(){$('#CDCLS_CLSCD').focus();});
		return;
	}
	if($('#CDCLS_NM').val()==""){
		malert("분류명을 입력하여 주십시오.",null,function(){$('#CDCLS_NM').focus();});
		return;
	}
	if($('#CDCLS_LEN').val()==""){
		malert("상세코드 크기를 입력하여 주십시오.",null,function(){$('#CDCLS_LEN').focus();});
		return;
	}

	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDCLS_CLSCD').get(0),"분류코드")){return;}
	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDCLS_LEN').get(0),"상세코드 크기")){return;}

	$.ajax({
		type: "POST"
		,url: "/ADM_PCD_U1120A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"CDCLS_CLSCD":$('#CDCLS_CLSCD').val()
			,"CDCLS_NM":$('#CDCLS_NM').val()
			,"CDCLS_LEN":$('#CDCLS_LEN').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_set_table_cls();
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
//분류코드 삭제
function fn_cls_delete(){
	if($('#CDCLS_CLSCD').val().length!=4){
		malert("분류코드를 4자리 숫자로 입력하여 주십시오.",null,function(){$('#CDCLS_CLSCD').focus();});
		return;
	}
	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDCLS_CLSCD').get(0),"분류코드")){return;}

	mconfirm("분류코드 ["+$('#CDCLS_CLSCD').val()+"]를 삭제하시겠습니까? <br/><br/>삭제 시 하위 상세코드들도 모두 삭제됩니다.",null,function(){fn_cls_delete_confirm();});
}
function fn_cls_delete_confirm(){

	$.ajax({
		type: "POST"
		,url: "/ADM_PCD_D1130A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"CDCLS_CLSCD":$('#CDCLS_CLSCD').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_set_table_cls();
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
//상세코드 추가
function fn_dtl_insert(){
	if($('#CDCLS_CLSCD').val().length != 4){
		malert("분류코드를 4자리 숫자로 선택하여 주십시오.",null,function(){$('#CDCLS_CLSCD').focus();});
		return;
	}
	if($('#CDDTL_DTLCD').val()==""){
		malert("상세코드를 입력하여 주십시오.",null,function(){$('#CDDTL_DTLCD').focus();});
		return;
	}
	if($('#CDDTL_NM').val()==""){
		malert("코드명을 입력하여 주십시오.",null,function(){$('#CDDTL_NM').focus();});
		return;
	}
	if($('#CDDTL_ORD').val()==""){
		malert("정렬순서를 입력하여 주십시오.",null,function(){$('#CDDTL_ORD').focus();});
		return;
	}

	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDCLS_CLSCD').get(0),"분류코드")){return;}
	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDDTL_ORD').get(0),"정렬순서")){return;}

	$.ajax({
		type: "POST"
		,url: "/ADM_PCD_I1210A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"CDCLS_CLSCD":$('#CDCLS_CLSCD').val()
			,"CDDTL_DTLCD":$('#CDDTL_DTLCD').val()
			,"CDDTL_NM":$('#CDDTL_NM').val()
			,"CDDTL_ORD":$('#CDDTL_ORD').val()
			,"CDDTL_USEYN":$('#CDDTL_USEYN').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_set_table_dtl();
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
//상세코드 수정
function fn_dtl_update(){
	if($('#CDCLS_CLSCD').val().length!=4){
		malert("분류코드를 선택하여 주십시오.",null,function(){$('#CDCLS_CLSCD').focus();});
		return;
	}
	if($('#CDDTL_DTLCD').val()==""){
		malert("상세코드를 입력하여 주십시오.",null,function(){$('#CDDTL_DTLCD').focus();});
		return;
	}
	if($('#CDDTL_NM').val()==""){
		malert("코드명을 입력하여 주십시오.",null,function(){$('#CDDTL_NM').focus();});
		return;
	}
	if($('#CDDTL_ORD').val()==""){
		malert("정렬순서를 입력하여 주십시오.",null,function(){$('#CDDTL_ORD').focus();});
		return;
	}

	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDCLS_CLSCD').get(0),"분류코드")){return;}
	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDDTL_ORD').get(0),"정렬순서")){return;}

	$.ajax({
		type: "POST"
		,url: "/ADM_PCD_U1220A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"CDCLS_CLSCD":$('#CDCLS_CLSCD').val()
			,"CDDTL_DTLCD":$('#CDDTL_DTLCD').val()
			,"CDDTL_NM":$('#CDDTL_NM').val()
			,"CDDTL_ORD":$('#CDDTL_ORD').val()
			,"CDDTL_USEYN":$('#CDDTL_USEYN').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_set_table_dtl();
			}
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
//상세코드 삭제
function fn_dtl_delete(){
	if($('#CDCLS_CLSCD').val().length!=4){
		malert("분류코드를 선택하여 주십시오.",null,function(){$('#CDCLS_CLSCD').focus();});
		return;
	}
	if($('#CDDTL_DTLCD').val()==""){
		malert("상세코드를 입력하여 주십시오.",null,function(){$('#CDDTL_DTLCD').focus();});
		return;
	}
	if(!fn_imemode(IME_MODE_NUMBERONLY,$('#CDCLS_CLSCD').get(0),"분류코드")){return;}

	mconfirm("상세코드 ["+$('#CDCLS_CLSCD').val()+" > "+$('#CDDTL_DTLCD').val()+"]를 삭제하시겠습니까?",null,function(){fn_dtl_delete_confirm();});
}
function fn_dtl_delete_confirm(){

	$.ajax({
		type: "POST"
		,url: "/ADM_PCD_D1230A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"CDCLS_CLSCD":$('#CDCLS_CLSCD').val()
			,"CDDTL_DTLCD":$('#CDDTL_DTLCD').val()
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data)){
				fn_set_table_dtl();
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
