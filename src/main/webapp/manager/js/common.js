// 모바일 에이전트 구분
if(MobileAgent==undefined){
	var MobileAgent = {
			Android: function () {
					 return navigator.userAgent.match(/Android/i) == null ? false : true;
			},
			BlackBerry: function () {
					 return navigator.userAgent.match(/BlackBerry/i) == null ? false : true;
			},
			IOS: function () {
					 return navigator.userAgent.match(/iPhone|iPad|iPod/i) == null ? false : true;
			},
			Opera: function () {
					 return navigator.userAgent.match(/Opera Mini/i) == null ? false : true;
			},
			Windows: function () {
					 return navigator.userAgent.match(/IEMobile/i) == null ? false : true;
			},
			isMobile: function () {
					 return (MobileAgent.Android() || MobileAgent.BlackBerry() || MobileAgent.IOS() || MobileAgent.Opera() || MobileAgent.Windows());
			}
	};
}

if(String.trim==undefined){
	String.prototype.trim = function() {
		return this.replace(/(^\s*)|(\s*$)/g, "");
	};
}
if(!String.prototype.replaceAll){
	String.prototype.replaceAll = function() {
		if(arguments.length!=2){
			return null;
		}
		var v_regstr = arguments[0];
		v_regstr = v_regstr.replace(/\\/g, "\\\\");
		v_regstr = v_regstr.replace(/\^/g, "\\^");
		v_regstr = v_regstr.replace(/\$/g, "\\$");
		v_regstr = v_regstr.replace(/\*/g, "\\*");
		v_regstr = v_regstr.replace(/\+/g, "\\+");
		v_regstr = v_regstr.replace(/\?/g, "\\?");
		v_regstr = v_regstr.replace(/\./g, "\\.");
		v_regstr = v_regstr.replace(/\(/g, "\\(");
		v_regstr = v_regstr.replace(/\)/g, "\\)");
		v_regstr = v_regstr.replace(/\|/g, "\\|");
		v_regstr = v_regstr.replace(/\,/g, "\\,");
		v_regstr = v_regstr.replace(/\{/g, "\\{");
		v_regstr = v_regstr.replace(/\}/g, "\\}");
		v_regstr = v_regstr.replace(/\[/g, "\\[");
		v_regstr = v_regstr.replace(/\]/g, "\\]");
		v_regstr = v_regstr.replace(/\-/g, "\\-");
		var re = new RegExp(v_regstr, "g");
		return this.replace(re, arguments[1]);
	};
}
if(!String.prototype.startsWith){
	String.prototype.startsWith = function() {
		var tmp	= this.substring(0,arguments[0].length);
		if(tmp	== arguments[0]){
			return true;
		}
		return false;
	};
}
if(!String.prototype.endsWith){
	String.prototype.endsWith = function() {

		var tmp	= this.substring(this.length-arguments[0].length);

		if(tmp	== arguments[0]){
			return true;
		}
		return false;
	};
}

//ajax callback success
function fn_ajax_callback(data){
	if(data.RESULT_CD=='Y')
		return true;
	setTimeout(function(){malert(data.RESULT_MSG);},500);
	return false;
}
//ajax error
function fn_ajax_error(request,status,error){
	setTimeout(function(){malert("서버연결에 실패하였습니다.");},500);
}

var IME_MODE_NOKOREAN		= 1;//한글불가
var IME_MODE_NUMBERONLY		= 2;//숫자만
var IME_MODE_NUMENGUBAR		= 3;//숫자+영문+_
var IME_MODE_NUMBENGUBAR	= 4;//숫자+대문자영문+_
var IME_MODE_NUMSENGUBAR	= 5;//숫자+소문자영문+_
var IME_MODE_ENGLISH		= 6;//영문
var IME_MODE_NUMUBAR		= 7;//숫자+'-'
var IME_MODE_NUMDOT			= 8;//숫자+'.'
var IME_MODE_EMAIL			= 9;//EMAIL
var IME_MODE_PHONE			= 10;//PHONE
var IME_MODE_MOBILE			= 11;//MOBILE PHONE
var IME_MODE_EMAIL1			= 12;//숫자+영문+_+-
var IME_MODE_EMAIL2			= 13;//숫자+영문+_+-+.
var IME_MODE_YYYYMMDD		= 14;//yyyymmdd날짜
var IME_MODE_NUMENGDOTUBARDS	= 15;//영문,숫자,'.','-','_'
var IME_MODE_NUMDOTCOMA		= 16;//숫자+'.'+','
var IME_MODE_AMT			= 17;//금액
var IME_MODE_YYYYMM			= 18;//yyyymmdd날짜
var IME_MODE_KORENGNUM		= 19;//한글,영문,숫자
var IME_MODE_ID				= 20;//영문,숫자,_,-

function fn_imemode(type,obj,name,str,showmsg){
	var msg		= "";
	var test	= false;

	str			= str==null?"":str;
	showmsg		= showmsg==null?true:false;

	var val		= obj==null?str:obj.value;
	var pattern	= null;

	if(type==IME_MODE_NOKOREAN){	//한글불가.
		pattern	= /[ㄱ-ㅎㅏ-ㅣ가-힣]/g;
		if(!pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [한글]을 입력할 수 없습니다.";

			if(obj!=null)
				obj.value	= obj.value.replaceAll(' ','').replace(/[ㄱ-ㅎ가-힣]/g,'');
		}
	}else if(type==IME_MODE_NUMBERONLY){//숫자only
		pattern	= /^[0-9]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [숫자]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\.\,\;\:\|\)\*\~\`\!\^\-\_\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
				obj.value	= obj.value.replace(/[\a-zA-Z]/g,'');
			}
		}
	}else if(type==IME_MODE_NUMENGUBAR){//숫자,영문,'_'
		pattern	= /^[a-zA-Z0-9\_]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [ 숫자 , 영문 , '_' ]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\.\,\;\:\|\)\*\~\`\!\^\-\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
			}
		}
	}else if(type==IME_MODE_NUMBENGUBAR){//대문자영문+숫자+'_'
		pattern	= /^[A-Z0-9\_]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [ 숫자 , 대문자영문 , '_' ]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\.\,\;\:\|\)\*\~\`\!\^\-\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
				obj.value	= obj.value.replace(/[\a-z]/g,'');
			}
		}
	}else if(type==IME_MODE_ENGLISH){//소문자영문+숫자+'_'
		pattern	= /^[a-zA-Z]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [영문]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\.\,\;\:\|\)\*\~\_`\!\^\-\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
				obj.value	= obj.value.replace(/[\0-9]/g,'');
			}
		}
	}else if(type==IME_MODE_NUMUBAR){//숫자+'-'
		pattern	= /^[0-9\-]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [ 숫자 , '-' ]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\.\,\;\:\|\)\*\~\_`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
				obj.value	= obj.value.replace(/[\a-zA-Z]/g,'');
			}
		}
	}else if(type==IME_MODE_NUMDOT){//숫자+'.'
		pattern	= /^[0-9\.]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [ 숫자 , '.' ]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\,\;\:\|\)\*\~\_`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
				obj.value	= obj.value.replace(/[\a-zA-Z]/g,'');
			}
		}
	}else if(type==IME_MODE_EMAIL){//EMAIL
		pattern	= /^[0-9a-zA-Z\_\-\.]{1,}@[0-9a-zA-Z\_\-]{1,}\.{1}[0-9a-zA-Z\_\-]{1,}\.{0,1}[0-9a-zA-Z\_\-]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]이 [EMAIL] 형식에 맞지 않습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\,\;\:\|\)\*\~`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
			}
		}
	}else if(type==IME_MODE_PHONE){//PHONE
		pattern	= /^[0-9\-]{9,13}$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]이 [전화번호/FAX] 형식에 맞지 않습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\,\;\:\|\)\*\~\_`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\a-zA-Z]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
			}
		}
	}else if(type==IME_MODE_MOBILE){//MOBILE PHONE
		pattern	= /^[0-9\-]{10,13}$/;

		if(!pattern.test(val))
			test	= false;
		else if(!"/010/011/016/018/019/".contain(val.substring(0,3)))
			test	= false;
		else
			test	= true;

		if(!test){
			msg		= "["+name+"]이 [MOBILE PHONE] 형식에 맞지 않습니다.";

			if(obj!=null){
				obj.val(obj.val().replaceAll(' ','').replace(/[\{\}\[\]\/\?\,\;\:\|\)\*\~\_`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,''));
				obj.val(obj.val().replace(/[\a-zA-Z]/g,''));
				obj.val(obj.val().replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,''));
			}
		}
	}else if(type==IME_MODE_EMAIL1){//숫자+영문+_+-
		pattern	= /^[0-9a-zA-Z\_\-\.]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [ 영문 , 숫자 , '_' , '-' ]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\,\;\:\|\)\*\~\.\`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
			}
		}
	}else if(type==IME_MODE_EMAIL2){//숫자+영문+_+-+.
		pattern	= /^[0-9a-zA-Z\_\-]{1,}\.{1}[0-9a-zA-Z\_\-]{1,}\.{0,1}[0-9a-zA-Z\_\-]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [ 영문 , 숫자 , '_' , '-' , '.' ]만 입력할 수 있으며, 형식에 맞게 입력하여야 합니다.\n\n" + "형식 : ex)[ abc123.com ]";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\,\;\:\|\)\*\~\`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
			}
		}
	}else if(type==IME_MODE_YYYYMMDD){
		msg		= "["+name+"]의 날짜 형식이 올바르지 않습니다. ex)20150401";

		if(!fn_imemode(IME_MODE_NUMDOT,obj,name))
			return;

		val		= obj.value.replace(/[\.]/g,'');
		test	= true;

		if(val.length!=8)
			test	= false;

		var yyyy	= val.substring(0,4);
		var mm		= val.substring(4,6);
		if(mm.startsWith("0"))
			mm		= mm.substring(1);
		var dd		= val.substring(6);
		if(dd.startsWith("0"))
			dd		= dd.substring(1);

		if(test && (yyyy=="0000" || mm=="0" || dd=="0"))
			test	= false;
		if( test && "/1/3/5/7/8/10/12/".indexOf("/"+mm+"/")>=0 && parseInt(dd)>31)
			test	= false;
		else if( test && "/4/6/9/11/".indexOf("/"+mm+"/")>=0 && parseInt(dd)>30)
			test	= false;
		else if(parseInt(mm)==2 && test){
			if( ((parseInt(yyyy) % 4 == 0) && (parseInt(yyyy) % 100 != 0))|| (parseInt(yyyy) % 400 == 0) ){
				if(parseInt(dd)>29)
					test	= false;
			}else{
				if(parseInt(dd)>28)
					test	= false;
			}
		}
		else if(test && "/1/2/3/4/5/6/7/8/9/10/11/12/".indexOf("/"+mm+"/")<0)
			test	= false;
	}else if(type==IME_MODE_YYYYMM){
		msg		= "["+name+"]의 년월 형식이 올바르지 않습니다. ex)201504";

		if(!fn_imemode(IME_MODE_NUMDOT,obj,name))
			return;

		val		= obj.value.replace(/[\.]/g,'');
		test	= true;

		if(val.length!=6)
			test	= false;

		if(test && "/01/02/03/04/05/06/07/08/09/10/11/12/".indexOf("/"+val.substring(4)+"/")<0)
			test	= false;
	}else if(type==IME_MODE_NUMENGDOTUBARDS){//숫자,영문,'.','-','_','@'
		pattern	= /^[0-9a-zA-Z\_\-\.\@]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [ 영문 , 숫자 , '_' , '-' , '.' , '@' ]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\,\;\:\|\)\*\~`\!\^\+\<\>\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
			}
		}
	}else if(type==IME_MODE_NUMDOTCOMA){//숫자+'.'+','
		pattern	= /^[0-9\.\,]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [ 숫자, '.' , ',' ]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\;\:\|\)\*\~\_`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
				obj.value	= obj.value.replace(/[\a-zA-Z]/g,'');
			}
		}
	}else if(type==IME_MODE_AMT){//금액
		if(val.indexOf(".")>=0 && val.length-val.indexOf(".")>3){
			test	= false;
			msg		= "["+name+"]은 소숫점 두자리까지만 가능합니다.";
		}else{
			pattern	= /^[0-9\,]*[\.]{0,1}[0-9]{0,2}$/;
			if(pattern.test(val)){
				test	= true;
			}else{
				test	= false;
				msg		= "["+name+"]은 올바른 금액이 아닙니다.";

				if(obj!=null){
					obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\;\:\|\)\*\~\-\_`\!\^\+\<\>\@\#\$\%\&\\\=\(\'\"]/g,'');
					obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
					obj.value	= obj.value.replace(/[\a-zA-Z]/g,'');
				}
			}
		}
	}else if(type==IME_MODE_KORENGNUM){	//한글,영문,숫자가능
		pattern	= /^[ㄱ-ㅎㅏ-ㅣ가-힣0-9a-zA-Z|\s]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= "["+name+"]은 [특수문자]를 입력할 수 없습니다.";
			if(obj!=null){
				obj.value	= obj.value.replace(/[\{\}\[\]\/\?\;\:\|\)\*\~\_`\!\^\+\<\>\@\#\$\%\&\\\=\-\(\'\"]/g,'');
			}
		}
	}else if(type==IME_MODE_ID){//숫자,영문,'.','-','_','@'
		pattern	= /^[0-9a-zA-Z\_\-]*$/;
		if(pattern.test(val)){
			test	= true;
		}else{
			test	= false;
			msg		= name+" [ 영문 , 숫자 , '-' , '_' ]만 입력할 수 있습니다.";

			if(obj!=null){
				obj.value	= obj.value.replaceAll(' ','').replace(/[\{\}\[\]\/\?\,\;\:\|\)\*\~`\!\^\+\<\>\#\$\%\&\\\=\(\'\"]/g,'');
				obj.value	= obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g,'');
			}
		}
	}

	if(!test){
		if(obj!=null)
			obj.select();
		if(showmsg)
			malert(msg);
	}
	return test;
}
/**
 * 주어진 위치에 주어진 문자를 전체 길이가 주어진 크기가 될때까지 추가
 * */
function fn_append(str,appendChar,size,appendLeft){
	var val	= str;
	while(true){
		if(val.length>=size)
			break;
		val	= (appendLeft?new String(appendChar):"")+val+(appendLeft?"":new String(appendChar));
	}
	return val;
}
function fn_tonum(str){
	while(str.startsWith("0"))
		str	= str.substring(1);

	if(str=="")
		return 0;
	return 	parseInt(str);
}
function fn_init_select(obj){
	for (var i=obj.options.length; 0 < i; i--)
		obj.options[i-1]	= null;
}
function fn_add_select(obj,txt,val){
	obj.options[obj.options.length] = new Option(txt, val, false, false);
}
function fn_comma(str,yn){
	if(yn==null)
		yn	= true;
	var isMinus	= false;
	if(str.startsWith("-"))
		isMinus	= true;

	str	= str.trim().replaceAll(",","").replaceAll("-","");
	if(!yn)
		return (isMinus?"-":"")+str.replaceAll(",","");
	var left	= str;
	var right	= "";
	if(str.indexOf(".")>=0){
		left	= str.substring(0,str.indexOf("."));
		right	= str.substring(str.indexOf("."));
	}
	var tmp		= "";

	for(var i=1;i<=left.length;i++){
		tmp		= new String(left.charAt(left.length-i))+tmp;
		if(i!=0 && i%3==0 && (left.length-i)!=0)
			tmp	= ","+tmp;
	}
	tmp	= tmp+right;
	return (isMinus?"-":"")+tmp;
}

/**
malert('작업을 완료할 수 없습니다.');
malert('작업을 완료할 수 없습니다.','확인');
malert('작업을 완료할 수 없습니다.','확인',function(){fn_err();});
*/
function malert(){
	if($('#modal-alert').length==0){
		 var modalS = "<div class='modal fade modal-dialog-center' id='modal-alert'>"
		+"<div class='modal-dialog'>"
		+"<div class='modal-content'>"
		+"<div class='modal-header'>"
		+"<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"
		+"<h4 class='modal-title'>Dynamic Content</h4>"
		+"</div>"
		+"<div class='modal-body'>"
		+"Content is loading..."
		+"</div>"
		+"<div class='modal-footer'>"
		+"<button type='button' class='btn-confirm btn btn-info' data-dismiss='modal'>확인</button>"
		+"</div>"
		+"</div>"
		+"</div>"
		+"</div>"
		;
		$('body').append(modalS);
	}

	var mg	= null;//arguments[0] 메시지
	var tt	= null;//arguments[1] 타이틀
	var func= null;//arguments[2] 확인버튼 이벤트
	if(arguments.length>=1)
		mg	= arguments[0];
	if(arguments.length>=2)
		tt	= arguments[1];
	if(arguments.length>=3)
		func	= arguments[2];

	setTimeout(function(){
			var modal	= $('#modal-alert').modal('show', {backdrop: 'static'});
			$('.modal-backdrop').css('height',$('body').prop("scrollHeight")+'px');
			$('#modal-alert .modal-title').html(tt==null?"알림":tt);
			$('#modal-alert .modal-body').html("<span style='font-size:14px;'>"+mg+"</span>");
			$('#modal-alert .btn-confirm').html("확인");
			$(modal).unbind('onclose');
			$(modal).bind('onclose',function(){func==null?function(){}:setTimeout(func,200);});
		}
	,200);
}
//확인창
/**
mconfirm('저장하시겠습니까?');
mconfirm('저장하시겠습니까?','확인');
mconfirm('저장하시겠습니까?','확인',function(){fn_save();});
mconfirm('저장하시겠습니까?','확인',function(){fn_save();},function(){fn_cancel();});

*/
function mconfirm(){
	if($('#modal-confirm').length==0){
		 var modalS = "<div class='modal fade modal-dialog-center' id='modal-confirm'>"
		+"<div class='modal-dialog'>"
		+"<div class='modal-content'>"
		+"<div class='modal-header'>"
		+"<button type='button' class='close' data-dismiss='modal' aria-hidden='true'>&times;</button>"
		+"<h4 class='modal-title'>Dynamic Content</h4>"
		+"</div>"
		+"<div class='modal-body'>"
		+"Content is loading..."
		+"</div>"
		+"<div class='modal-footer'>"
		+"<button type='button' class='btn-cancel btn btn-default' data-dismiss='modal'>취소</button>"
		+"<button type='button' class='btn-confirm btn btn-info' data-dismiss='modal'>확인</button>"
		+"</div>"
		+"</div>"
		+"</div>"
		+"</div>"
		;
		$('body').append(modalS);
	}

	var mg	= null;//arguments[0] 메시지
	var tt	= null;//arguments[1] 타이틀
	var func1= null;//arguments[2] 확인버튼 이벤트
	var func2= null;//arguments[3] 취소버튼 이벤트
	if(arguments.length>=1)
		mg	= arguments[0];
	if(arguments.length>=2)
		tt	= arguments[1];
	if(arguments.length>=3)
		func1	= arguments[2];
	if(arguments.length>=4)
		func2	= arguments[3];

	setTimeout(function(){
			var modal	= $('#modal-confirm').modal('show', {backdrop: 'static'});
			$('.modal-backdrop').css('height',$('body').prop("scrollHeight")+'px');
			$('#modal-confirm .modal-title').html(tt==null?"확인":tt);
			$('#modal-confirm .modal-body').html("<span style='font-size:14px;'>"+mg+"</span>");
			$('#modal-confirm .btn-cancel').html("취소");
			$('#modal-confirm .btn-confirm').html("확인");
			$('#modal-confirm .btn-confirm').unbind('click');
			$('#modal-confirm .btn-confirm').bind('click',function(){
				$(modal).unbind('onclose');
				setTimeout(func1==null?function(){}:func1,200);
			});
			$(modal).unbind('onclose');
			$(modal).bind('onclose',function(){func2==null?function(){}:setTimeout(func2,500);});
		}
	,200);
};
/**
다음우편번호 서비스
*/
function DaumPostcode(postCodeObj,addr1Obj,func) {
	new daum.Postcode({
		oncomplete: function(data) {
			var fullRoadAddr = data.roadAddress; // 도로명 주소 변수
			var extraRoadAddr = ''; // 도로명 조합형 주소 변수

			// 법정동명이 있을 경우 추가한다. (법정리는 제외)
			// 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
			if(data.bname !== '' && /[동|로|가]$/g.test(data.bname))
				extraRoadAddr += data.bname;
			// 건물명이 있고, 공동주택일 경우 추가한다.
			if(data.buildingName !== '' && data.apartment === 'Y')
			   extraRoadAddr += (extraRoadAddr !== '' ? ', ' + data.buildingName : data.buildingName);
			// 도로명, 지번 조합형 주소가 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
			if(extraRoadAddr !== '')
				extraRoadAddr = ' (' + extraRoadAddr + ')';
			// 도로명, 지번 주소의 유무에 따라 해당 조합형 주소를 추가한다.
			if(fullRoadAddr !== '')
				fullRoadAddr += extraRoadAddr;

			// 우편번호와 주소 정보를 해당 필드에 넣는다.
			$(postCodeObj).val(data.zonecode);
			$(addr1Obj).val(fullRoadAddr);
			if(func!=null)
				setTimeout(func,1);
		}
	}).open();
};
