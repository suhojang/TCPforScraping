<%@ page contentType="text/html;charset=UTF-8" errorPage = "/WEB-INF/jsp/common/error.jsp"%><%@
taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"%><%@
taglib prefix="spring" uri="http://www.springframework.org/tags"%><%@
include file="/WEB-INF/jsp/project/kais/ADM/include/declare.jspf" %><%@
include file="/WEB-INF/jsp/project/kais/ADM/include/header.jspf" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="ko" xml:lang="ko">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="<c:url value='/css/egovframework/sample.css'/>" />
<title><spring:message code="view.title" arguments=""/></title>

<link rel="stylesheet" href="<%=NEON_PATH%>assets/css/font-icons/font-awesome/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=NEON_PATH%>assets/css/font-icons/entypo/css/entypo.css">
</head>

<body>
<div class="container" style="margin-top:100px;">
	<div class="row">
		<div class="col-md-12">
				<div class="panel panel-danger" data-collapsed="0">

					<!-- panel head -->
					<div class="panel-heading">
						<div class="panel-title" style="color:#ff6600;"><i class="fa fa-warning"></i> ERROR</div>

						<div class="panel-options">
							<!--
							<a href="#sample-modal" data-toggle="modal" data-target="#sample-modal-dialog-1" class="bg"><i class="entypo-cog"></i></a>
							<a href="#" data-rel="collapse"><i class="entypo-down-open"></i></a>
							<a href="#" data-rel="reload"><i class="entypo-arrows-ccw"></i></a>
							-->
							<a href="#" data-rel="home" style="padding-top:0px;" title="홈으로"><i class="fa fa-home" style="font-size:18px;" onclick="javascript:$(location).attr('href','/');"></i></a>
						</div>
					</div>

					<!-- panel body -->
					<div class="panel-body" style="text-align:left;font-size:13px;">

						<p>

						<strong>처리중 오류가 발생하였습니다.</strong>
						<br/>
						<br/>
						아래의 원인을 확인하시고 다시 시도하여 주십시오.
						<br/>
						<br/>
						- 비정상적인 입력값
						<br/>
						- 잘못된 URL
						<br/>
						- 권한이 없는 접근
						<br/>
						- 기타 비정상적인 조작
						</p>

					</div>

				</div>
		</div>
	</div>
</div>
</body>
</html>