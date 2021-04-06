<%@page contentType="text/html;charset=utf-8"%>
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="x-ua-compatible" content="IE=Edge">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=2.0">
<title>datatables</title>

<link type="text/css" href="css/JPrintViewer3.0.css" rel="StyleSheet" ></link>

<script type="text/javascript" src="js/jquery-1.12.4.js"></script>
<script type="text/javascript" src="js/JPrintViewer3.0.js"></script>

<style type="text/css">


</style>

<script>
var jpv			= null;
function printStart(){
	jpv			= new JPrintViewer();
	jpv.contentId	= "jpvContent";
//	jpv.spTag		= "/TR/";
	jpv.name		= "jpv";
	jpv._err		= false;
	jpv.viewPageNo	= true;
//	jpv.pageStyle	= "border:4px double #000000";
	jpv.pageClass	= "jpvPage";
	//for browser--------------
	jpv.pageHeight	= 940;
	jpv.pageWidth	= 670;
	
	jpv.maxPageNo	= 30;
	jpv.cPageCls	= "jpvCPageNo";
	jpv.tPageCls	= "jpvTPageNo";
	jpv.kcPageCls	= "jpvKCPageNo";
	jpv.ktPageCls	= "jpvKTPageNo";
	
//	jpv.cloneTableHead	= true;
//	jpv.compareOrigin	= true;
	
	jpv.header		= "<center><div><span class='jpvKCPageNo'></span>번째 장 / 총 <span class='jpvKTPageNo'></span> 장</div></center>";
	jpv.footer		= "<center><div><span class='jpvCPageNo'></span> / <span class='jpvTPageNo'></span></div></center>";

	
//	jpv.initfunc	= function(){fn_make_pdf();};
	jpv.initfunc	= function(){if($(document).isMobile()){fn_make_pdf_mobile();}else{fn_make_pdf();}};
	jpv.start();
	
}

function fn_make_pdf(){
	
	if($("form[name='pdfForm']").length<1)
		$(document.body).append("<form name='pdfForm' method='post'><input type='hidden' name='PDF_HTML'/><input type='hidden' name='BASE_URL'/></form>");
	if($("iframe[name='pdfIframe']").length<1)
		$(document.body).append("<iframe name='pdfIframe' style='width:0px;height:0px;display:none;'></iframe>");

	var css	= ["/print/css/JPrintViewer3.0.css"];
	
	$("input[name='PDF_HTML']").val(encodeURIComponent(jpv.getPdfHtml(css)));
	$("input[name='BASE_URL']").val(encodeURIComponent("http://localhost:8080/"));
	
	document.pdfForm.target	= 'pdfIframe';
	document.pdfForm.action	= 'makeHtml2Pdf.jsp';
	document.pdfForm.submit();
}

function fn_make_pdf_mobile(){
	var css	= ["/print/css/JPrintViewer3.0.css"];
	
	$.ajax({
		type: "POST"
		,url: "makeHtml2PdfMobile.jsp"
		,dataType: "jsonp"
		,jsonp: "callback"
		,data: {
			"PDF_HTML":encodeURIComponent(jpv.getPdfHtml(css))
			,"BASE_URL":encodeURIComponent("http://localhost:8080/")
		}
		,timeout:"10000"
		,success: function(data){
			if(data.RESULT=='Y'){
				$('#download_pdf').html("<a href='pdfDownload.jsp?filename="+data.filename+"' target='_blank'>다운로드 "+data.filename+".pdf</a>");
				$('#download_pdf').show();
			}else{
				alert(data.MESSAGE);
			}
		}
		,error: function(request,status,error){

		}
	});
}
</script>

</head>
<body onload="javascript:printStart();" style='font-family: MalgunGothic;margin:0 auto;'>
	<div id='download_pdf' style='display:none;'></div>
	<div id='jpvContent' class='jpv-print'>
		<table class='jpv-print' cellspacing="0" align="center" jpvCloneTableHead="true" style='margin-top:10px;margin-left:10px;margin-right:10px;width:650px;'>
			<thead style="background:#3399ff;">
				<tr>
					<th colspan='3' style='width:286px;' class='jpv'>상단1</th><!-- <img src="/img/naver.gif" style='width:100px;height:50px;'/> -->
					<th colspan='3' style='width:273px;' class='jpv'>상단2</th>
					<th rowspan='2' style='width:91px;' class='jpv'>타이틀7</th>
				</tr>
				<tr>
					<th style='width:104px;' class='jpv'>타이틀1</th>
					<th style='width:91px;' class='jpv'>타이틀2</th>
					<th style='width:91px;' class='jpv'>타이틀3</th>
					<th style='width:91px;' class='jpv'>타이틀4</th>
					<th style='width:91px;' class='jpv'>타이틀5</th>
					<th style='width:91px;' class='jpv'>타이틀6</th>
				</tr>
			</thead>
			<tbody style="background:#ffff99;">
				<tr><td class='jpv'><span>데이터1</span>dlqslek</td><td class='jpv'>데이터2</td><td class='jpv'>데이터3</td><td class='jpv'>데이터4</td><td class='jpv'>데이터5</td><td class='jpv'>데이터6</td><td class='jpv'>데이터7</td></tr>
				<tr><td class='jpv' rowspan='3'>01데이터1</td><td class='jpv'>01데이터2</td><td class='jpv'>01데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>02데이터2</td><td class='jpv'>02데이터5</td><td class='jpv'>02데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>03데이터2</td><td class='jpv'>03데이터5</td><td class='jpv'>03데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>04데이터1</td><td class='jpv'>04데이터2</td><td class='jpv'>04데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>05데이터2</td><td class='jpv'>05데이터5</td><td class='jpv'>05데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>06데이터2</td><td class='jpv'>06데이터5</td><td class='jpv'>06데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>07데이터1</td><td class='jpv'>07데이터2</td><td class='jpv'>07데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>08데이터2</td><td class='jpv'>08데이터5</td><td class='jpv'>08데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>09데이터2</td><td class='jpv'>09데이터5</td><td class='jpv'>09데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>10데이터1</td><td class='jpv'>10데이터2</td><td class='jpv'>10데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>11데이터2</td><td class='jpv'>11데이터5</td><td class='jpv'>11데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>12데이터2</td><td class='jpv'>12데이터5</td><td class='jpv'>12데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>13데이터1</td><td class='jpv'>13데이터2</td><td class='jpv'>13데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>14데이터2</td><td class='jpv'>14데이터5</td><td class='jpv'>14데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>15데이터2</td><td class='jpv'>15데이터5</td><td class='jpv'>15데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>16데이터1</td><td class='jpv'>16데이터2</td><td class='jpv'>16데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>17데이터2</td><td class='jpv'>17데이터5</td><td class='jpv'>17데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>18데이터2</td><td class='jpv'>18데이터5</td><td class='jpv'>18데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>19데이터1</td><td class='jpv'>19데이터2</td><td class='jpv'>19데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>20데이터2</td><td class='jpv'>20데이터5</td><td class='jpv'>20데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>21데이터2</td><td class='jpv'>21데이터5</td><td class='jpv'>21데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>22데이터1</td><td class='jpv'>22데이터2</td><td class='jpv'>22데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>23데이터2</td><td class='jpv'>23데이터5</td><td class='jpv'>23데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>24데이터2</td><td class='jpv'>24데이터5</td><td class='jpv'>24데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>25데이터1</td><td class='jpv'>25데이터2</td><td class='jpv'>25데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>26데이터2</td><td class='jpv'>26데이터5</td><td class='jpv'>26데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>27데이터2</td><td class='jpv'>27데이터5</td><td class='jpv'>27데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>28데이터1</td><td class='jpv'>28데이터2</td><td class='jpv'>28데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>29데이터2</td><td class='jpv'>29데이터5</td><td class='jpv'>29데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>30데이터2</td><td class='jpv'>30데이터5</td><td class='jpv'>30데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>31데이터1</td><td class='jpv'>31데이터2</td><td class='jpv'>31데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>32데이터2</td><td class='jpv'>32데이터5</td><td class='jpv'>32데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>33데이터2</td><td class='jpv'>33데이터5</td><td class='jpv'>33데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>34데이터1</td><td class='jpv'>34데이터2</td><td class='jpv'>34데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>35데이터2</td><td class='jpv'>35데이터5</td><td class='jpv'>35데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>36데이터2</td><td class='jpv'>36데이터5</td><td class='jpv'>36데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>37데이터1</td><td class='jpv'>37데이터2</td><td class='jpv'>37데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>38데이터2</td><td class='jpv'>38데이터5</td><td class='jpv'>38데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>39데이터2</td><td class='jpv'>39데이터5</td><td class='jpv'>39데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>40데이터1</td><td class='jpv'>40데이터2</td><td class='jpv'>40데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>41데이터2</td><td class='jpv'>41데이터5</td><td class='jpv'>41데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>42데이터2</td><td class='jpv'>42데이터5</td><td class='jpv'>42데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>43데이터1</td><td class='jpv'>43데이터2</td><td class='jpv'>43데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>44데이터2</td><td class='jpv'>44데이터5</td><td class='jpv'>44데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>45데이터2</td><td class='jpv'>45데이터5</td><td class='jpv'>45데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>46데이터1</td><td class='jpv'>46데이터2</td><td class='jpv'>46데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>47데이터2</td><td class='jpv'>47데이터5</td><td class='jpv'>47데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>48데이터2</td><td class='jpv'>48데이터5</td><td class='jpv'>48데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>49데이터1</td><td class='jpv'>49데이터2</td><td class='jpv'>49데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>50데이터2</td><td class='jpv'>50데이터5</td><td class='jpv'>50데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>51데이터2</td><td class='jpv'>51데이터5</td><td class='jpv'>51데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>52데이터1</td><td class='jpv'>52데이터2</td><td class='jpv'>52데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>53데이터2</td><td class='jpv'>53데이터5</td><td class='jpv'>53데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>54데이터2</td><td class='jpv'>54데이터5</td><td class='jpv'>54데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>55데이터1</td><td class='jpv'>55데이터2</td><td class='jpv'>55데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>56데이터2</td><td class='jpv'>56데이터5</td><td class='jpv'>56데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>57데이터2</td><td class='jpv'>57데이터5</td><td class='jpv'>57데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>58데이터1</td><td class='jpv'>58데이터2</td><td class='jpv'>58데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>59데이터2</td><td class='jpv'>59데이터5</td><td class='jpv'>59데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>60데이터2</td><td class='jpv'>60데이터5</td><td class='jpv'>60데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>61데이터1</td><td class='jpv'>61데이터2</td><td class='jpv'>61데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>62데이터2</td><td class='jpv'>62데이터5</td><td class='jpv'>62데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>63데이터2</td><td class='jpv'>63데이터5</td><td class='jpv'>63데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>64데이터1</td><td class='jpv'>64데이터2</td><td class='jpv'>64데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>65데이터2</td><td class='jpv'>65데이터5</td><td class='jpv'>65데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>66데이터2</td><td class='jpv'>66데이터5</td><td class='jpv'>66데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>67데이터1</td><td class='jpv'>67데이터2</td><td class='jpv'>67데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>68데이터268데이터</td><td class='jpv'>68데이터5</td><td class='jpv'>68데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>69데이터2</td><td class='jpv'>69데이터5</td><td class='jpv'>69데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>70데이터1</td><td class='jpv'>70데이터2</td><td class='jpv'>70데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>71데이터2</td><td class='jpv'>71데이터5</td><td class='jpv'>71데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>72데이터2</td><td class='jpv'>72데이터5</td><td class='jpv'>72데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>73데이터1</td><td class='jpv'>73데이터2</td><td class='jpv'>73데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>74데이터2</td><td class='jpv'>74데이터5</td><td class='jpv'>74데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>75데이터2</td><td class='jpv'>75데이터5</td><td class='jpv'>75데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>76데이터1</td><td class='jpv'>76데이터2</td><td class='jpv'>76데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>77데이터2</td><td class='jpv'>77데이터5</td><td class='jpv'>77데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>78데이터2</td><td class='jpv'>78데이터5</td><td class='jpv'>78데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>79데이터1</td><td class='jpv'>79데이터2</td><td class='jpv'>79데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>80데이터2</td><td class='jpv'>80데이터5</td><td class='jpv'>80데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>81데이터2</td><td class='jpv'>81데이터5</td><td class='jpv'>81데이터6</td></tr>
				<tr><td class='jpv' rowspan='3'>82데이터1</td><td class='jpv'>82데이터2</td><td class='jpv'>82데이터3</td><td class='jpv' rowspan='3'>01데이터4</td><td class='jpv'>01데이터5</td><td class='jpv'>01데이터6</td><td class='jpv' rowspan='3'>01데이터7</td></tr>
				<tr><td class='jpv' colspan='2'>83데이터2</td><td class='jpv'>83데이터5</td><td class='jpv'>83데이터6</td></tr>
				<tr><td class='jpv' colspan='2'>84데이터2</td><td class='jpv'>84데이터5</td><td class='jpv'>84데이터6</td></tr>
			</tbody>
		</table>
	</div>
	
</body>
</html>
