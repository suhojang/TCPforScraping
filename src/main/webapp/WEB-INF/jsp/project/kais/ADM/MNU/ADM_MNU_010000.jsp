<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/session.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/header.jspf" %>
<%@ include file="/WEB-INF/jsp/project/kais/ADM/include/error.jspf" %>

<%
try{
	/***********************************************************************
	*프로그램명 : ADM_MNU_010000.jsp
	*설명 : 메뉴관리
	*작성자 : 장정훈
	*작성일자 : 2017.06.12
	*
	*수정자		수정일자		                                                            수정내용
	*----------------------------------------------------------------------
	*
	***********************************************************************/

	String menuXml	= mgrRec.getMenufullxml();
%>
<%@include file="/WEB-INF/jsp/project/kais/ADM/include/left.jspf" %>

		<div class="row">

			<div class="col-sm-6">

				<div class="tile-block tile-green">

					<div class="tile-header">
						<a href="#">
							메뉴 목록
						</a>
					</div>

					<div id="treeDiv1" class="tile-content" style="margin-left:2px;margin-right:2px;margin-top:2px;margin-bottom:2px;height:540px;overflow:auto;background-color:#ffffff;"></div>
					<div class="tile-footer">
						<span style='color:#99ff00;font-weight:bold;'>변경저장</span>을 하셔야 메뉴정보가 DB에 저장됩니다. &nbsp;&nbsp;<span style='color:#99ff00;font-weight:bold;'>초기화</span>는 트리를 시작 시로 되돌립니다.
					</div>
				</div>
				<p style="width:100%;text-align:right;margin-top:-10px;">
					<button type="button" class="btn btn-black btn-icon" onclick="javascript:fn_remove_change();">
						초기화
						<i class="entypo-cancel"></i>
					</button>

					<button type="button" class="btn btn-red btn-icon" onclick="javascript:fn_menu_save();" authUri='/ADM_MNU_U1020A/'>
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
									<td class="lefthead" width="100px;">메뉴분류</td>
									<td>
										<div class="col-sm-5">
											<input type="text" class="form-control" id="MNU_GRD" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;" disabled>
											<input type="hidden" id="jtreeId"/>
											<input type="hidden" id="jtreeElementId"/>
											<input type="hidden" id="tagName"/>
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">메뉴 ID</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="MNU_ID" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">메뉴명</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="MNU_NM" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">URL</td>
									<td>
										<div class="col-sm-12">
											<input type="text" class="form-control" id="MNU_URL" placeholder="" style="color:#555555;background-color:#fff;border:1px solid #ebebeb;">
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">메뉴등록</td>
									<td>
										<div class="col-sm-5">
											<select id='MNU_USYN' class="form-control" style="width:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
												<option value='Y'>Y</option>
												<option value='N'>N</option>
											</select>
										</div>
									</td>
								</tr>
								<tr>
									<td class="lefthead">메뉴권한</td>
									<td>
										<table class="itable">
											<tbody>
												<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
												<tr>
													<td class="lefthead">${MGRINF_GRD.CDDTL_NM}</td>
													<td>
														<div class="col-sm-5">
															<select id='MNU_AUTH${MGRINF_GRD.CDDTL_DTLCD}' class="form-control" style="width:100px;color:#555555;background-color:#fff;border:1px solid #ebebeb;">
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
						<span style='color:#33ffff;font-weight:bold;'>적용</span>하시면 입력사항이 메뉴목록에 반영됩니다.
					</div>
				</div>
				<p style="width:100%;text-align:right;margin-top:-10px;">
					<button type="button" class="btn btn-orange btn-icon" onclick="javascript:fn_menu_accept();">
						적용
						<i class="entypo-check"></i>
					</button>
				</p>
			</div>
		</div>

<script language="javascript" src="<%=JS_PATH%>JTree.js"></script>
<script type="text/javascript">
function fn_init(){
	fn_menu_init();
}

var jtree			= null;
function fn_menu_init(){
	jtree			= new JTree();
	jtree.imgFolder		= "/manager/img/jtree/";
	jtree.indentationSize= 40;
	jtree.onClick		= "fn_menun_click";
	jtree.onChange		= "fn_menu_reset";
	jtree.limitDepth	= 4;
	jtree.topIconVisible	= false;
	jtree.openTree		= true;
	jtree.displayArea	= $("#treeDiv1").get(0);
	jtree.displayAttr	= "NM";

	jtree.loadXml($("#treeXml").val());
	jtree.makeTree();

	fn_menu_reset();
}
//클릭된 시점에서 현재의 xml의 번호와 몇번째 노드인지 판별하여, element 변수에 담아둔다.
//ex) 번호0의 xml의 0-10번의 노드가 선택되었다면, 부모노드의 네임을 가지고 현재의 노드의 레벨을 정한다.
function fn_menun_click(jtreeSeq, elementId){
	var element		= document.getJTreeXmlElement(jtreeSeq, elementId);

	var mtag		= "";
	var mgrd		= "";
	if(element.parentNode.nodeName=="root"){
		return;
	}else if(element.parentNode.nodeName=="B-MENU"){
		mtag		= "M-MENU";
		mgrd		= "중메뉴";
	}else if(element.parentNode.nodeName=="M-MENU"){
		mtag		= "S-MENU";
		mgrd		= "소메뉴";
	}else{
		mtag		= "B-MENU";
		mgrd		= "대메뉴";
	}

	var jxp	= jtree.xml;
	$("#jtreeId").val(jtreeSeq);		//번호
	$("#jtreeElementId").val(elementId);	//몇번째
	$("#tagName").val(mtag);

	$("#MNU_GRD").val(mgrd);
	$("#MNU_ID").val(jxp.getAttribute(element,"ID")==null?"":jxp.getAttribute(element,"ID"));
	$("#MNU_NM").val(jxp.getAttribute(element,"NM")==null?"":jxp.getAttribute(element,"NM"));
	$("#MNU_URL").val(jxp.getAttribute(element,"URL")==null?"":jxp.getAttribute(element,"URL"));
	$("#MNU_USYN").val(jxp.getAttribute(element,"USYN")==null?"N":jxp.getAttribute(element,"USYN"));

	var auth	= null;
	<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
	auth	= jxp.getAttribute(element,'AUTH${MGRINF_GRD.CDDTL_DTLCD}');
	jQuery('#MNU_AUTH${MGRINF_GRD.CDDTL_DTLCD}').val(auth==null?'N':auth);
	</c:forEach>
}
//텍스트 창을 리셋시키는 기능
function fn_menu_reset(){
	$("#jtreeId").val("");
	$("#jtreeElementId").val("");
	$("#tagName").val("");
	$("#MNU_GRD").val("");
	$("#MNU_ID").val("");
	$("#MNU_NM").val("");
	$("#MNU_URL").val("");
	$("#MNU_USYN").val("Y");

	<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
	jQuery('#MNU_AUTH${MGRINF_GRD.CDDTL_DTLCD}').val('Y');
	</c:forEach>
}
//변경된 노드의 정보를 저장하는 기능
//xml의 번호와 몇번째 노드인지의 값을 받아 트리에  값을 반영
function fn_menu_accept(){
	$("#MNU_ID").val($("#MNU_ID").val().trim());
	$("#MNU_NM").val($("#MNU_NM").val().trim());
	$("#MNU_URL").val($("#MNU_URL").val().trim());

	if($("#jtreeElementId").val()==""){
		malert("메뉴 선택을 선행하여 주십시오.");
		return;
	}

	if($("#MNU_ID").val()==""){malert("메뉴ID를 입력하여 주십시오.",null,function(){$("#MNU_ID").focus();});return;}
	if($("#MNU_NM").val()==""){malert("메뉴명을 입력하여 주십시오.",null,function(){$("#MNU_NM").focus();});return;}
	if($("#MNU_URL").val()==""){malert("URL을 입력하여 주십시오.",null,function(){$("#MNU_URL").focus();});return;}

	if(!fn_imemode(IME_MODE_NUMBENGUBAR,document.getElementById("MNU_ID"),"메뉴ID")){return;}
	if(!fn_imemode(IME_MODE_NOKOREAN,document.getElementById("MNU_URL"),"URL")){return;}

	var jtreeSeq	= $("#jtreeId").val();
	var elementId	= $("#jtreeElementId").val();

	var menuObj	= new Object();
	menuObj['ID']	= $("#MNU_ID").val();
	menuObj['NM']	= $("#MNU_NM").val();
	menuObj['URL']	= $("#MNU_URL").val();
	menuObj['USYN']	= $("#MNU_USYN").val();

	<c:forEach var="MGRINF_GRD" items="${CD0001}" varStatus="status">
	menuObj['AUTH${MGRINF_GRD.CDDTL_DTLCD}']	= jQuery('#MNU_AUTH${MGRINF_GRD.CDDTL_DTLCD}').val();
	</c:forEach>

	if(jtreeSeq.trim()=="" || elementId.trim()=="")
		return;

	document.replaceJTreeElement(
			jtreeSeq
			,elementId
			,$("#tagName").val()
			,$('#MNU_NM').val()
			,menuObj
		);
}
//메뉴저장.
function fn_menu_save(){
	mconfirm("변경사항을 저장하시겠습니까?",null,function(){fn_menu_save_confirm();});
}
function fn_menu_save_confirm(){
	var MNLST_XML	= "<?xml version='1.0' encoding='utf-8'?>"+jtree.xml.toString("");
	$.ajax({
		type: "POST"
		,url: "/ADM_MNU_U1020A/"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"MNLST_XML":MNLST_XML
		}
		,timeout:"10000"
		,success: function(data){
			if(fn_ajax_callback(data))
				malert("메뉴정보가 저장되었습니다.");
		}
		,error: function(request,status,error){
			fn_ajax_error(request,status,error);
		}
	});
}
function fn_remove_change(){
	mconfirm("변경사항을 초기화하시겠습니까?\n\n(주의 : 초기화 후 [변경저장]을 하셔야만 DB에 반영됩니다.)",null,function(){fn_remove_change_confirm();});
}
function fn_remove_change_confirm(){
	fn_menu_init();
}
jQuery(document).ready(function($){
	fn_init();
});
</script>
<textarea id="treeXml" style="display:none;width:1;height:0"><%=menuXml %></textarea>

<%@include file="/WEB-INF/jsp/project/kais/ADM/include/footer.jspf" %>
<%}catch(Exception e){e.printStackTrace();}%>
