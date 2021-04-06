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
							스므래핑 모니터링
						</a>
					</div>

					<div class="tile-content" style="margin-left:2px;margin-right:2px;margin-top:2px;margin-bottom:2px;overflow:auto;background-color:#ffffff;">
					</div>
				</div>
			</div>
		</div>
<script type="text/javascript">
function fn_init(){
	
}
jQuery(document).ready(function($){
	fn_init();
});
</script>

<%@include file="/WEB-INF/jsp/project/kais/ADM/include/footer.jspf" %>
<%}catch(Exception e){e.printStackTrace();}%>
