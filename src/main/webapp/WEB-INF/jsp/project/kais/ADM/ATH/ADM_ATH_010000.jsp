<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%>
<%@ page import="com.kwic.xml.parser.JXParser" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/session.jspf"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/header.jspf"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/error.jspf"%>

<%
try{
	/***********************************************************************
	*프로그램명 : ADM_ATH_010000.jsp
	*설명 : 권한관리
	*작성자 : 장정훈
	*작성일자 : 2017.06.12
	*
	*수정자		수정일자		                                                            수정내용
	*----------------------------------------------------------------------
	*
	***********************************************************************/
%>
<%@include file="/WEB-INF/jsp/project/kais/ADM/include/left.jspf" %>

		<div class="row">

			<div class="col-sm-6">

				<div class="tile-block tile-green">

					<div class="tile-header">
						<a href="#">
							권한 목록
						</a>
					</div>

					<div id="treeDiv1" class="tile-content" style="margin-left:2px;margin-right:2px;margin-top:2px;margin-bottom:2px;height:540px;overflow:auto;background-color:#ffffff;"></div>
					<div class="tile-footer">
						<span style='color:#99ff00;font-weight:bold;'>변경저장</span>을 하셔야 권한정보가 DB에 저장됩니다. &nbsp;&nbsp;<span style='color:#99ff00;font-weight:bold;'>초기화</span>는 트리를 시작시로 되돌립니다.
					</div>
				</div>
				<p style="width:100%;text-align:right;margin-top:-10px;">
					<button type="button" class="btn btn-black btn-icon" onclick="javascript:fn_remove_change();">
						초기화
						<i class="entypo-cancel"></i>
					</button>

					<button type="button" class="btn btn-red btn-icon" onclick="javascript:fn_auth_save();" authUri='/ADM_ATH_U1020A/'>
						변경 저장
						<i class="entypo-check"></i>
					</button>
				</p>
			</div>


			<div class="col-sm-6">
				<div class="tile-block" style="background:#3333cc;">
					<div class="tile-header">
						<a href="#">
							상세정보
						</a>
					</div>

					<div class="tile-content" style="margin-left:2px;margin-right:2px;margin-top:2px;margin-bottom:2px;height:540px;overflow:auto;background-color:#ffffff;">
						<table class="table table-bordered">
							<tbody>
								<tr>
									<td class="lefthead">요청명</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="name" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
											<input type="hidden" id="jtreeId"/>
											<input type="hidden" id="jtreeElementId"/>
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">요청 URI</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="URI" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">요청 권한</td>
									<td>
										<table class="itable">
											<tbody>
												<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
												<tr>
													<td class="lefthead">${MGRINF_GRD.CDDTL_NM}</td>
													<td>
														<div class="col-sm-5">
															<select id='AUTH${MGRINF_GRD.CDDTL_DTLCD}' class="form-control" style="width:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
																<option value='Y'>Y</option>
																<option value='N'>N</option>
															</select>
														</div>
													</td>
												</tr>
												</c:forEach>
											</tbody>
										</table>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					<div class="tile-footer">
						<span style='color:#33ffff;font-weight:bold;'>적용</span>하시면 입력사항이 권한목록에 반영됩니다.
					</div>
				</div>
				<p style="width:100%;text-align:right;margin-top:-10px;">
					<button type="button" class="btn btn-orange btn-icon" onclick="javascript:fn_auth_accept();">
						적용
						<i class="entypo-check"></i>
					</button>
				</p>
			</div>
		</div>
<%@include file="/WEB-INF/jsp/project/kais/ADM/include/footer.jspf" %>

<script language="javascript" src="<%=JS_PATH%>JTree.js"></script>
<script type="text/javascript">
function fn_init(){
	fn_auth_init();
}

var jtree			= null;
function fn_auth_init(){
	jtree			= new JTree();
	jtree.imgFolder		= "/manager/img/jtree/";
	jtree.indentationSize= 40;
	jtree.onClick		= "fn_auth_click";
	jtree.onChange		= "fn_auth_reset";
	jtree.limitDepth	= 3;
	jtree.topIconVisible	= false;
	jtree.openTree		= true;
	jtree.displayArea	= $("#treeDiv1").get(0);
	jtree.displayAttr	= "name";

	jtree.loadXml($("#treeXml").val());
	jtree.makeTree();

	fn_auth_reset();
}
//클릭된 시점에서 현재의 xml의 번호와 몇번째 노드인지 판별하여, element 변수에 담아둔다.
//ex) 번호0의 xml의 0-10번의 노드가 선택되었다면, 부모노드의 네임을 가지고 현재의 노드의 레벨을 정한다.
var selectedElement	= null;
function fn_auth_click(jtreeSeq, elementId){
	var jxp	= jtree.xml;
	var element		= document.getJTreeXmlElement(jtreeSeq, elementId);
	if(element.parentNode==jxp.root){
		$("#jtreeId").val("");
		$("#jtreeElementId").val("");
		$("#name").val("");
		$("#URI").val("");
		return;
	}
	selectedElement	= element;

	$("#jtreeId").val(jtreeSeq);		//번호
	$("#jtreeElementId").val(elementId);	//몇번째

	$("#name").val(jxp.getAttribute(element,"name")==null?"":jxp.getAttribute(element,"name"));

	if(element.parentNode.parentNode!=jxp.root){
		$("#URI").val(jxp.getAttribute(element,"URI")==null?"":jxp.getAttribute(element,"URI"));
		$("#URI").attr('disabled',false);
		var auth	= null;
		<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
		auth	= jxp.getAttribute(element,'AUTH${MGRINF_GRD.CDDTL_DTLCD}');
		jQuery('#AUTH${MGRINF_GRD.CDDTL_DTLCD}').val(auth==null?'N':auth);
		jQuery('#AUTH${MGRINF_GRD.CDDTL_DTLCD}').attr('disabled',false);
		</c:forEach>
	}else{
		$("#URI").val("");
		$("#URI").attr('disabled',true);
		<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
		jQuery('#AUTH${MGRINF_GRD.CDDTL_DTLCD}').val('Y');
		jQuery('#AUTH${MGRINF_GRD.CDDTL_DTLCD}').attr('disabled',true);
		</c:forEach>
	}
}
//텍스트 창을 리셋시키는 기능
function fn_auth_reset(){
	$("#jtreeId").val("");
	$("#jtreeElementId").val("");
	$("#name").val("");
	$("#URI").val("");

	<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
	jQuery('#AUTH${MGRINF_GRD.CDDTL_DTLCD}').val('Y');
	</c:forEach>
}
//변경된 노드의 정보를 저장하는 기능
//xml의 번호와 몇번째 노드인지의 값을 받아 트리에  값을 반영
function fn_auth_accept(){
	$("#name").val($("#name").val().trim());
	$("#URI").val($("#URI").val().trim());

	if($("#jtreeElementId").val()==""){
		malert("URI 선택을 선행하여 주십시오.");
		return;
	}

	if($("#name").val()==""){malert("권한명을 입력하여 주십시오.",null,function(){$("#name").focus();});return;}
	if(selectedElement.parentNode.parentNode!=jtree.xml.root){
		if($("#URI").val()==""){malert("URI를 입력하여 주십시오.",null,function(){$("#URI").focus();});return;}
		if(!fn_imemode(IME_MODE_NOKOREAN,document.getElementById("URI"),"URI")){return;}
	}

	var jtreeSeq	= $("#jtreeId").val();
	var elementId	= $("#jtreeElementId").val();

	var authObj	= new Object();
	authObj['name']	= $("#name").val();
	authObj['URI']	= $("#URI").val();

	<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
	authObj['AUTH${MGRINF_GRD.CDDTL_DTLCD}']	= jQuery('#AUTH${MGRINF_GRD.CDDTL_DTLCD}').val();
	</c:forEach>

	if(jtreeSeq.trim()=="" || elementId.trim()=="")
		return;

	document.replaceJTreeElement(
			jtreeSeq
			,elementId
			,"URI"
			,$("#name").val()
			,authObj
		);
}
//권한저장.
function fn_auth_save(){
	mconfirm("변경사항을 저장하시겠습니까?",null,function(){fn_auth_save_confirm();});
}
function fn_auth_save_confirm(){
	var URLST_XML	= "<?xml version='1.0' encoding='utf-8'?>"+jtree.xml.toString("");
	$.ajax({
		type: "POST"
		,url: "/ADM_ATH_U1020A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"URLST_XML":URLST_XML
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data))
				malert("권한정보가 저장되었습니다.");
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
function fn_remove_change(){
	mconfirm("변경사항을 초기화하시겠습니까?<br/><br/>(주의 : 초기화 후 [변경저장]을 하셔야만 DB에 반영됩니다.)",null,function(){fn_auth_init();});
}
jQuery(document).ready(function($){
	fn_init();
});
</script>
<textarea id="treeXml" style="display:none;width:1;height:0">${URI_XML}</textarea>

<%}catch(Exception e){e.printStackTrace();}%>
