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
	ageList[i]	= [i,'��'+i+'��'];

$(document).ready(function() {

	table2	= $('#example').DataTable( {
		scrollY:        <%=bodyheight%>
		,fixedColumns:   {
			leftColumns: <%=leftfixedcount%>		//�տ� checkbox�� �ִٸ� �� ������ ���Խ��Ѿ���
			,rightColumns: <%=rightfixedcount%>		//�ڿ� hidden�÷��� �ִٸ� �� ������ ���Խ��Ѿ���
		}
		,columns: [
			/* �߿� : hidden field �� ������ �� �ڷ� ���Ƽ� �־����!!! �ſ� �߷�!!! */
			{ data:"col1"  ,name: "col1", title: "��", align: "left", width:100, render: $.fn.dataTable.render.cut(8)}
			,{ data:"col2"  ,name: "col2", title: "�̸�", align: "left", width: 100, orderable: false}
			,{ data:"col3"  ,name: "col3", title: "����", align: "left", width: 100}
			,{ data:"col4"  ,name: "col4", title: "�繫��", align: "left", width: 200, orderable: false}
			,{ data:"col5"  ,name: "col5", title: "����", align: "center", width: 200, orderable: false}
			,{ data:"col6"  ,name: "col6", title: "������", align: "center", width: 200, orderable: false, render: $.fn.dataTable.render.date('.')}
			,{ data:"col7"  ,name: "col7", title: "����", align: "right", width: 200, render: $.fn.dataTable.render.number( ',', '.', 0, '$' )}
			,{ data:"col8"  ,name: "col8", title: "Extn", align: "center", width: 200, orderable: false}
			,{ data:"col9"  ,name: "col9", title: "�̸���", align: "left", width: 200,	orderable: true, render: $.fn.dataTable.render.cut(20)}
			,{ data:"col10" ,name: "col0", visible:false}
		]
		//������ �ֱ�
		<%
		if("Y".equals(appendhead)){
			out.println("		,appendHead: \"<tr>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th colspan='2' style='border-top:1px #000000 solid;text-align:center;'>��Ÿ����1</th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th colspan='2'  style='border-top:1px #000000 solid;text-align:center;'>��Ÿ����2</th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"<th style='border-top:1px #000000 solid;border-bottom:0px;'></th>\"");
			out.println("						+\"</tr>\"");
		}
		%>

		//���ÿ� üũ�ڽ� �ֱ�
		<%
		if("Y".equals(selectyn)){
			out.println(",selectCheckbox:	{display:true	, keyColumn:'col10', period:'total'}	//period:total,page");
		}else{
			out.println(",selectCheckbox:	{display:false	, keyColumn:'col10', period:'total'}	//period:total,page");
		}
		%>
		//�����̺�Ʈ �߻����� ���� �۾� ����
		,specialSortFunc:	function(evt,idx,direction){fn_sorting(idx,direction);}
		//edit ��ɿ���
		,editFields:{
				type:'<%=edittype%>'	//row , cell
				,def : [
					{name:"col1"	,label:"��"		,editable:true}
					,{name:"col2"	,label:"�̸�"		,editable:true}
					,{name:"col3"	,label:"����"		,editable:true}
					,{name:"col4"	,label:"�繫��"	,editable:true}
					,{name:"col5"	,label:"����"		,editable:true	,type: "select"		,list:ageList}
					,{name:"col6"	,label:"������"	,editable:true	,type: "datetime"	,dateFormat:'yy.mm.dd'}
					,{name:"col7"	,label:"����"		,editable:true	,type: "number"		}
					,{name:"col8"	,label:"Extn"	,editable:true	,type: "number"		}
					,{name:"col9"	,label:"�̸���"	,editable:true}
					,{name:"col10"	,label:"��ȣ"		}
				]
		}
		,editValidFunc:		function(evt,obj,name,label,target){fn_editValidate(obj,name,label,target);}//editFields.type=='row'�� ���� �� ������ ���� , editFields.type=='row'�� ��� �� ������ �迭�� ���޵ȴ�.
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
		alert('��ȸ�� �����Ͱ� �����ϴ�.');
}
/**
* ���� �������� ���õ� ��� ������ ���θ� ��ȯ
* (���� �������� �������� �ʴ� ���� �����͵��� ��ȯ���� �ʰ� fnGetTotalSelectedKeys()�� ���� key ��ϸ� Ȯ���� �� �ִ�.)
*/
function fn_getSelectedDatas(){
	var datas	= table2.fnGetSelectedDatas();
	if(datas.length>0)
		alert(JSON.stringify(datas));
	else
		alert('���õ� �����Ͱ� �����ϴ�.');
}
/**
* ���� �̺�Ʈ�� �߻��� ����� ���� �۾��� �����Ѵ�.
* �������� �Ͼ�� ������ ���õǾ��� key ����� �����Ѵ�.(������ �̵��� ����)
* idx : ���õ� ���� �ε���, 0���� ����(selectCheckbox:	true�� ��쿡�� 1���� ���� �������ӿ� �����Ѵ�.)
* direction :
*/
function fn_sorting(idx,ord){
	var datas	= fn_sample(table2.fnGetPageNo());

	table2.fnSetDatas(false,datas,[idx, ord]);
}
/**
* ������ �����׸� �ʱ�ȭ
*/
function fn_initPageSelected(){
	table2.fnInitPageSelected();
}
/**
* ��� �����׸� �ʱ�ȭ
*/
function fn_initTotalSelected(){
	table2.fnInitTotalSelected();
}
/**
* �����׸� ��ȿ�� �˻�
*/
function fn_editValidate(obj,name,label,target){
	if(Array.isArray(obj)){
		var val	= [];
		//row ����
		for(var i=0;i<obj.length;i++){
			val[val.length]	= $(obj[i]).val();
			if(name[i]=='col6'){
				var txt	= val[i].replaceAll('.','');
				if(txt.length!=8){
					alert(label[i]+' ['+val[i]+']�� �ùٸ��� ���� ��¥ �����Դϴ�.');
					target.editFail(obj[i]);
					return;
				}
				val[i]	= txt.substring(0,4)+'.'+txt.substring(4,6)+'.'+txt.substring(6);
			}
		}

		if(!confirm('����� ������ ������ �����Ͻðڽ��ϱ�?')){
			target.editFail(obj[0]);
			return;
		}
		target.editComplete(val);

	}else{
		//cell ����
		var val	= $(obj).val();
		if(name=='col6'){
			var txt	= val.replaceAll('.','');
			if(txt.length!=8){
				alert(label+' ['+val+']�� �ùٸ��� ���� ��¥ �����Դϴ�.');
				target.editFail();
				return;
			}
			val	= txt.substring(0,4)+'.'+txt.substring(4,6)+'.'+txt.substring(6);

		}else{
		}
		if(!confirm('����� ������ ������ �����Ͻðڽ��ϱ�?')){
			target.editFail();
			return;
		}

		target.editComplete($(obj).val());
	}
}
/**
* ���õ� ��� row ����
* ���⼭�� server-side ������ �ٷ� �����Ѵ�.
*/
function fn_delTotalSelected(){
	var keys	= table2.fnGetTotalSelectedKeys();

	if(!confirm('���õǾ��� ['+keys.length+'] ���� �����Ͻðڽ��ϱ�?'))
		return;

	fn_search(1);
}
/**
* ������������ ���� row ����
* ���⼭�� server-side ������ �ٷ� �����Ѵ�.
*/
function fn_delPageSelected(){
	var keys	= table2.fnGetPageSelectedKeys();
	if(!confirm('���õǾ��� ['+keys.length+'] ���� �����Ͻðڽ��ϱ�?'))
		return;

	fn_search(1);
}
/**
* ��� ���õ� Ű������ ��ȯ
*/
function fn_getSelectedKeys(){
	var keys	= table2.fnGetTotalSelectedKeys()
	alert(keys);
}
/**
* ����ȸ �Ѵ�.
* ���õǾ��� ������ �ʱ�ȭ�ȴ�.
*/
function fn_search(pageNo){
	var datas	= fn_sample(1);

	table2.fnSetDatas(true,datas,[[1,'asc']]);
//	table2.fnSetDatas(true,datas,[[10, 'asc'],[1,'asc']]);
	table2.setPageInfo(tCnt,pageNo,pageperblock,rowperpage);//�ѰǼ�,��������ȣ,������������,��������Ǽ�
}
/**
* ������ ��ȣ�� �ش��ϴ� ������ ��ȸ
*/
function fn_paging(pageNo){
	var datas	= fn_sample(pageNo);
	table2.fnSetDatas(false,datas);

	table2.setPageInfo(tCnt,pageNo,pageperblock,rowperpage);//�ѰǼ�,��������ȣ,������������,��������Ǽ�
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
	<th style="padding-left:5px;width:100px;">������ ǥ��</th>
	<td>
		<select name='selectyn'>
			<option value='Y'>ǥ����</option>
			<option value='N'>ǥ�þ���</option>
		</select>
		<script>document.myform.selectyn.value	= '<%=selectyn%>';</script>
	</td>
	<th style="padding-left:5px;">�÷� ����</th>
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
	<td rowspan="3"><input type="button" value="��������" onclick="javascript:document.myform.submit();"/></td>
	</tr>
	<tr style="height:37px;">
	<th style="padding-left:5px;">��������</th>
	<td>
		<select name='edittype'>
			<option value='row'>Row ����</option>
			<option value='cell'>Cell ����</option>
		</select>
		<script>document.myform.edittype.value	= '<%=edittype%>';</script>
	</td>
	<th style="padding-left:5px;">������</th>
	<td>
		<select name='appendhead'>
			<option value='Y'>Y</option>
			<option value='N'>N</option>
		</select>
		<script>document.myform.appendhead.value	= '<%=appendhead%>';</script>
	</td>
	</tr>
	<tr style="height:37px;">
	<th style="padding-left:5px;">�׸��� ����</th>
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
	<th style="padding-left:5px;">�׸��� ����</th>
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
		<input type='button' value='������������ json' onclick='javascript:fn_getAllDatas();'/>
	</td>
	<td>
		<input type='button' value='���������õ����� json' onclick='javascript:fn_getSelectedDatas();'/>
	</td>
	<td>
		<input type='button' value='��缱���ʱ�ȭ' onclick='javascript:fn_initTotalSelected();'/>
	</td>
	</tr>
	<tr>
	<td>
		<input type='button' value='������ �����ʱ�ȭ' onclick='javascript:fn_initPageSelected();'/>
	</td>
	<td>
		<input type='button' value='��缱�û���' onclick='javascript:fn_delTotalSelected();'/>
	</td>
	<td>
		<input type='button' value='���������û���' onclick='javascript:fn_delPageSelected();'/>
	</td>
	</tr>
	<tr>
	<td>
		<input type='button' value='��缱�� Key ���' onclick='javascript:fn_getSelectedKeys();'/>
	</td>
	<td>
		<input type='button' value='����ȸ(�����ʱ�ȭ)' onclick='javascript:fn_search(1);'/>
	</td>
	<td></td>
	</tr>
	</table>
</div>


</body>
</html>
