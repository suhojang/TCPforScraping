<%@ page contentType="text/html;charset=euc-kr" errorPage="/common/error/error.jsp"  %>
<%
	String selectyn	= request.getParameter("selectyn");selectyn=selectyn==null?"Y":selectyn;
	String leftfixedcount	= request.getParameter("leftfixedcount");leftfixedcount=leftfixedcount==null?"0":leftfixedcount;
	String rightfixedcount	= request.getParameter("rightfixedcount");rightfixedcount=rightfixedcount==null?"0":rightfixedcount;
	String edittype	= request.getParameter("edittype");edittype=edittype==null?"row":edittype;
	String appendhead	= request.getParameter("appendhead");appendhead=appendhead==null?"N":appendhead;
	String bodywidth		= request.getParameter("bodywidth");bodywidth=bodywidth==null?"700":bodywidth;
	String bodyheight		= request.getParameter("bodyheight");bodyheight=bodyheight==null?"300":bodyheight;
%>
<!doctype html>
<html lang="en">
<head>
<meta charset="EUC-KR">
<meta http-equiv="x-ua-compatible" content="IE=Edge">
<meta name="viewport" content="initial-scale=1.0, maximum-scale=2.0">
<title>datatables</title>
<link type="text/css" href="css/jquery-ui.css" rel="StyleSheet" ></link>
<link type="text/css" href="css/jquery.dataTables.css" rel="StyleSheet" ></link>
<link type="text/css" href="css/fixedColumns.dataTables.css" rel="StyleSheet" ></link>
<link type="text/css" href="css/select.dataTables.css" rel="StyleSheet" ></link>
<link type="text/css" href="css/bootstrap.css" rel="StyleSheet" ></link>
<link type="text/css" href="css/bootstrap-theme.css" rel="StyleSheet" ></link>

<script type="text/javascript" src="js/jquery-1.12.4.js"></script>
<script type="text/javascript" src="js/jquery-ui.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/dataTables.fixedColumns.js"></script>
<script type="text/javascript" src="js/dataTables.select.js"></script>
<script type="text/javascript" src="js/bootstrap.js"></script>

<style type="text/css" class="init">
	div.container {
		max-width: <%=bodywidth%>px;
		margin: 0 auto;
	}
</style>
<script type="text/javascript">
var table2	= null;
var pageperblock=10,rowperpage=10;

var seq	= 0;
var tCnt	= 124;
var ageList	= [];
for(var i=0;i<130;i++)
	ageList[i]	= [i,'만'+i+'살'];

$(document).ready(function() {

	table2	= $('#example').DataTable( {
		scrollY:        <%=bodyheight%>
		,fixedColumns:   {
			leftColumns: <%=leftfixedcount%>		//앞에 checkbox가 있다면 그 개수도 포함시켜야함
			,rightColumns: <%=rightfixedcount%>		//뒤에 hidden컬럼이 있다면 그 개수도 포함시켜야함
		}
		,columns: [
			/* 중요 : hidden field 는 무조건 맨 뒤로 몰아서 넣어야함!!! 매우 중료!!! */
			{ data:"col1"  ,name: "col1", title: "성", align: "left", width:100, render: $.fn.dataTable.render.cut(8)}
			,{ data:"col2"  ,name: "col2", title: "이름", align: "left", width: 100, orderable: false}
			,{ data:"col3"  ,name: "col3", title: "지위", align: "left", width: 100}
			,{ data:"col4"  ,name: "col4", title: "사무실", align: "left", width: 200, orderable: false}
			,{ data:"col5"  ,name: "col5", title: "나이", align: "center", width: 200, orderable: false}
			,{ data:"col6"  ,name: "col6", title: "시작일", align: "center", width: 200, orderable: false, render: $.fn.dataTable.render.date('.')}
			,{ data:"col7"  ,name: "col7", title: "월급", align: "right", width: 200, render: $.fn.dataTable.render.number( ',', '.', 0, '$' )}
			,{ data:"col8"  ,name: "col8", title: "Extn", align: "center", width: 200, orderable: false}
			,{ data:"col9"  ,name: "col9", title: "이메일", align: "left", width: 200,	orderable: true, render: $.fn.dataTable.render.cut(20)}
			,{ data:"col10" ,name: "col0", visible:false}
		]
		//상단헤더 넣기
		<%
		if("Y".equals(appendhead)){
			out.println("		,appendHead: \"<tr>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th colspan='2' style='border-top:1px #000000 solid;text-align:center;'>기타정보1</th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th colspan='2'  style='border-top:1px #000000 solid;text-align:center;'>기타정보2</th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"</tr>\"");
		}
		%>

		//선택용 체크박스 넣기
		<%
		if("Y".equals(selectyn)){
			out.println(",selectCheckbox:	{display:true	, keyColumn:'col10', period:'total'}	//period:total,page");
		}else{
			out.println(",selectCheckbox:	{display:false	, keyColumn:'col10', period:'total'}	//period:total,page");
		}
		%>
		//정렬이벤트 발생시의 수행 작업 정의
		,specialSortFunc:	function(evt,idx,direction){fn_sorting(idx,direction);}
		//edit 기능여부
		,editFields:{
				type:'<%=edittype%>'	//row , cell
				,def : [
					{name:"col1"	,label:"성"		,editable:true}
					,{name:"col2"	,label:"이름"		,editable:true}
					,{name:"col3"	,label:"지위"		,editable:true}
					,{name:"col4"	,label:"사무실"	,editable:true}
					,{name:"col5"	,label:"나이"		,editable:true	,type: "select"		,list:ageList}
					,{name:"col6"	,label:"시작일"	,editable:true	,type: "datetime"	,dateFormat:'yy.mm.dd'}
					,{name:"col7"	,label:"월급"		,editable:true	,type: "number"		}
					,{name:"col8"	,label:"Extn"	,editable:true	,type: "number"		}
					,{name:"col9"	,label:"이메일"	,editable:true}
					,{name:"col10"	,label:"번호"		}
				]
		}
		,editValidFunc:		function(evt,obj,name,label,target){fn_editValidate(obj,name,label,target);}//editFields.type=='row'일 경우는 각 변수는 단일 , editFields.type=='row'일 경우 각 변수는 배열로 전달된다.
		,pagination:		{display:true	, func:function(evt,pageNo){fn_paging(pageNo);}}
		,resultInfo:		{display:false}
	} );

	fn_search(1);
} );

function fn_getAllDatas(){
	var datas	= table2.fnGetAllDatas();

	if(datas.length>0)
		alert(JSON.stringify(datas));
	else
		alert('조회된 데이터가 없습니다.');
}
/**
* 현재 페이지의 선택된 목록 데이터 전부를 반환
* (현재 페이지에 보여지지 않는 선택 데이터들은 반환하지 않고 fnGetTotalSelectedKeys()를 통해 key 목록만 확인할 수 있다.)
*/
function fn_getSelectedDatas(){
	var datas	= table2.fnGetSelectedDatas();
	if(datas.length>0)
		alert(JSON.stringify(datas));
	else
		alert('선택된 데이터가 없습니다.');
}
/**
* 정렬 이벤트가 발생할 경우의 서버 작업을 정의한다.
* 재정렬이 일어나도 이전에 선택되어진 key 목록을 유지한다.(페이지 이동도 동일)
* idx : 선택된 정렬 인덱스, 0부터 시작(selectCheckbox:	true일 경우에는 1부터 실제 데이터임에 주의한다.)
* direction :
*/
function fn_sorting(idx,ord){
	var datas	= fn_sample(table2.fnGetPageNo());

	table2.fnSetDatas(false,datas,[idx, ord]);
}
/**
* 페이지 선택항목 초기화
*/
function fn_initPageSelected(){
	table2.fnInitPageSelected();
}
/**
* 모든 선택항목 초기화
*/
function fn_initTotalSelected(){
	table2.fnInitTotalSelected();
}
/**
* 편집항목 유효성 검사
*/
function fn_editValidate(obj,name,label,target){
	if(Array.isArray(obj)){
		var val	= [];
		//row 편집
		for(var i=0;i<obj.length;i++){
			val[val.length]	= $(obj[i]).val();
			if(name[i]=='col6'){
				var txt	= val[i].replaceAll('.','');
				if(txt.length!=8){
					alert(label[i]+' ['+val[i]+']은 올바르지 않은 날짜 형식입니다.');
					target.editFail(obj[i]);
					return;
				}
				val[i]	= txt.substring(0,4)+'.'+txt.substring(4,6)+'.'+txt.substring(6);
			}
		}

		if(!confirm('변경된 정보를 서버에 저장하시겠습니까?')){
			target.editFail(obj[0]);
			return;
		}
		target.editComplete(val);

	}else{
		//cell 편집
		var val	= $(obj).val();
		if(name=='col6'){
			var txt	= val.replaceAll('.','');
			if(txt.length!=8){
				alert(label+' ['+val+']은 올바르지 않은 날짜 형식입니다.');
				target.editFail();
				return;
			}
			val	= txt.substring(0,4)+'.'+txt.substring(4,6)+'.'+txt.substring(6);

		}else{
		}
		if(!confirm('변경된 정보를 서버에 저장하시겠습니까?')){
			target.editFail();
			return;
		}

		target.editComplete($(obj).val());
	}
}
/**
* 선택된 모든 row 삭제
* 여기서는 server-side 삭제를 바로 진행한다.
*/
function fn_delTotalSelected(){
	var keys	= table2.fnGetTotalSelectedKeys();

	if(!confirm('선택되어진 ['+keys.length+'] 건을 삭제하시겠습니까?'))
		return;

	fn_search(1);
}
/**
* 현재페이지의 선택 row 삭제
* 여기서는 server-side 삭제를 바로 진행한다.
*/
function fn_delPageSelected(){
	var keys	= table2.fnGetPageSelectedKeys();
	if(!confirm('선택되어진 ['+keys.length+'] 건을 삭제하시겠습니까?'))
		return;

	fn_search(1);
}
/**
* 모든 선택된 키값들을 반환
*/
function fn_getSelectedKeys(){
	var keys	= table2.fnGetTotalSelectedKeys()
	alert(keys);
}
/**
* 재조회 한다.
* 선택되어진 정보도 초기화된다.
*/
function fn_search(pageNo){
	var datas	= fn_sample(1);

	table2.fnSetDatas(true,datas,[[1,'asc']]);
//	table2.fnSetDatas(true,datas,[[10, 'asc'],[1,'asc']]);
	table2.setPageInfo(tCnt,pageNo,pageperblock,rowperpage);//총건수,페이지번호,블럭당페이지수,페이지당건수
}
/**
* 페이지 번호에 해당하는 데이터 조회
*/
function fn_paging(pageNo){
	var datas	= fn_sample(pageNo);
	table2.fnSetDatas(false,datas);

	table2.setPageInfo(tCnt,pageNo,pageperblock,rowperpage);//총건수,페이지번호,블럭당페이지수,페이지당건수
}

function fn_sample(pageNo){
	seq	= (pageNo-1)*10+1;
	var datas	= [];
	for(var i=0;i<10;i++){
		if(seq>tCnt)
			break;
		datas[i]	= {"col1": "Garrett"+seq,"col2": "Winters"+seq,"col3": "Accountant"+seq,"col4": "Tokyo"+seq,"col5": seq,"col6": "20110725","col7": "1707"+seq,"col8": "84"+seq,"col9": "g.winters"+seq+"@datatables.net","col10": seq++};
	}
	return datas;
}

</script>

</head>
<body>

<div class="container">
<form name="myform" action="datatables.jsp" onsubmit="return false;">
	<table width="100%" style='border:1px #000000 solid;margin-top:5px;'>
	<tr style="height:37px;">
	<th style="padding-left:5px;width:100px;">선택항 표시</th>
	<td>
		<select name='selectyn'>
			<option value='Y'>표시함</option>
			<option value='N'>표시안함</option>
		</select>
		<script>document.myform.selectyn.value	= '<%=selectyn%>';</script>
	</td>
	<th style="padding-left:5px;">컬럼 고정</th>
	<td>
		Left : <select name='leftfixedcount'>
			<option value="0">0</option>
			<option value="1">1</option>
			<option value="2">2</option>
			<option value="3">3</option>
		</select>
		<script>document.myform.leftfixedcount.value	= '<%=leftfixedcount%>';</script>
		Right : <select name='rightfixedcount'>
			<option value="0">0</option>
			<option value="2">1</option>
			<option value="3">2</option>
		</select>
		<script>document.myform.rightfixedcount.value	= '<%=rightfixedcount%>';</script>
	</td>
	<td rowspan="3"><input type="button" value="설정변경" onclick="javascript:document.myform.submit();"/></td>
	</tr>
	<tr style="height:37px;">
	<th style="padding-left:5px;">편집형태</th>
	<td>
		<select name='edittype'>
			<option value='row'>Row 편집</option>
			<option value='cell'>Cell 편집</option>
		</select>
		<script>document.myform.edittype.value	= '<%=edittype%>';</script>
	</td>
	<th style="padding-left:5px;">상단헤더</th>
	<td>
		<select name='appendhead'>
			<option value='Y'>Y</option>
			<option value='N'>N</option>
		</select>
		<script>document.myform.appendhead.value	= '<%=appendhead%>';</script>
	</td>
	</tr>
	<tr style="height:37px;">
	<th style="padding-left:5px;">그리드 넓이</th>
	<td>
		<select name='bodywidth'>
			<option value='700'>700px</option>
			<option value='800'>800px</option>
			<option value='900'>900px</option>
			<option value='1000'>1000px</option>
			<option value='1100'>1100px</option>
		</select>
		<script>document.myform.bodywidth.value	= '<%=bodywidth%>';</script>
	</td>
	<th style="padding-left:5px;">그리드 높이</th>
	<td>
		<select name='bodyheight'>
			<option value='200'>200px</option>
			<option value='300'>300px</option>
			<option value='400'>400px</option>
			<option value='500'>500px</option>
			<option value='600'>600px</option>
		</select>
		<script>document.myform.bodyheight.value	= '<%=bodyheight%>';</script>
	</td>
	</tr>
	</table>
</form>
<br/>
<br/>

	<div id="example-datatables">
		<table width="100%" class="stripe row-border order-column nowrap" id="example" cellspacing="0">
			<thead>
			</thead>
			<tbody>
			</tbody>
		</table>
	</div>



	<table width="100%" style="margin-top:20px;">
	<tr>
	<td>
		<input type='button' value='페이지데이터 json' onclick='javascript:fn_getAllDatas();'/>
	</td>
	<td>
		<input type='button' value='페이지선택데이터 json' onclick='javascript:fn_getSelectedDatas();'/>
	</td>
	<td>
		<input type='button' value='모든선택초기화' onclick='javascript:fn_initTotalSelected();'/>
	</td>
	</tr>
	<tr>
	<td>
		<input type='button' value='페이지 선택초기화' onclick='javascript:fn_initPageSelected();'/>
	</td>
	<td>
		<input type='button' value='모든선택삭제' onclick='javascript:fn_delTotalSelected();'/>
	</td>
	<td>
		<input type='button' value='페이지선택삭제' onclick='javascript:fn_delPageSelected();'/>
	</td>
	</tr>
	<tr>
	<td>
		<input type='button' value='모든선택 Key 목록' onclick='javascript:fn_getSelectedKeys();'/>
	</td>
	<td>
		<input type='button' value='재조회(선택초기화)' onclick='javascript:fn_search(1);'/>
	</td>
	<td></td>
	</tr>
	</table>
</div>


</body>
</html>
