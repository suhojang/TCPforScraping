
if(String.ignoreCaseIndexOf==undefined){
	String.prototype.ignoreCaseIndexOf = function() {
		return (this.toUpperCase().indexOf(arguments[0].toUpperCase())>=0)?true:false;
	};
}
if(Array.push==undefined){
	Array.prototype.push=function(){
		for(var i=0;i!=arguments.length;i++){
			this[this.length]=arguments[i];
		}
		return this.length;
	};
}
if(String.trim==undefined){
	String.prototype.trim = function() {
		return this.replace(/(^\s*)|(\s*$)/g, "");
	};
}
if(String.prototype.replaceAll==undefined){
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
if(String.prototype.startsWith==undefined){
	String.prototype.startsWith = function() {
		if(this.length<arguments[0].length)
			return false;
		var tmp	= this.substring(0,arguments[0].length);
		if(tmp	== arguments[0]){
			return true;
		}
		return false;
	};
}
if(String.prototype.endsWith==undefined){
	String.prototype.endsWith = function() {
		if(this.length<arguments[0].length)
			return false;
		var tmp	= this.substring(this.length-arguments[0].length);

		if(tmp	== arguments[0]){
			return true;
		}
		return false;
	};
}
if(String.prototype.equalsIgnoreCase==undefined){
	String.prototype.equalsIgnoreCase = function() {
		if(arguments.length!=1){
			return false;
		}
		if(this.toUpperCase()==arguments[0].toUpperCase()){
			return true;
		}
		return false;
	};
}
if(String.prototype.ignoreCaseIndexOf==undefined){
	String.prototype.ignoreCaseIndexOf = function() {
		return (this.toUpperCase().indexOf(arguments[0].toUpperCase())>=0)?true:false;
	};
}
if(String.prototype.contained==undefined){
	String.prototype.contained = function() {
		if(arguments[0].indexOf(this)>=0){
			return true;
		}
		return false;
	};
}
if(String.prototype.contain==undefined){
	String.prototype.contain = function() {
		if(this.indexOf(arguments[0])>=0){
			return true;
		}
		return false;
	};
}
if(String.prototype.getBytes==undefined){
	String.prototype.getBytes = function() {
		if(arguments[0].length==0)
			return 0;
		var size	= 0;
		var c		= "";
		for(var i=0;i<arguments[0].length;i++){
			c	= escape(arguments[0].charAt(i));
			if(c.length>4)
				size	+= 2;
			else
				size++;
		}
		return size;
	};
}


/*-----------------------------------------------------------------------------
 * Object : JDate
 * description : Date에 대한 control
* param count : 0 or 3
* param 1 : select object - select 객체
*
* ex)	var date	= new JDate(2008,08,18);
*		var date	= new JDate();
*		var date	= new Date(true,'20090401');
*		var date	= new Date(new Date(2009,03,01));
-----------------------------------------------------------------------------*/
function JDate(){
	this.date		= null;
	this.weekKor	= new Array("일요일","월요일","화요일","수요일","목요일","금요일","토요일");
	this.weekEng	= new Array("Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday");

	try{
		if(arguments.length==0){
			this.date	= new Date();

		}else if(arguments.length==1){
			this.date	= arguments[0];

		}else if(arguments.length==2 && arguments[0]==true){

			arguments[1]	= arguments[1].replaceAll(".","").replaceAll("-","").replaceAll("/","");
			if(arguments[1].length>8){
				arguments[1]	= arguments[1].substring(0,8);
			}
			while(arguments[1].length<8)
				arguments[1]	= "0"+arguments[1];
			
			var yyyy	= arguments[1].substring(0,4);
			while(yyyy.startsWith("0")){
				if(yyyy=="0")
					break;
				yyyy	= yyyy.substring(1);
			}
			
			var mmstr	= arguments[1].substring(4,6);
			if(mmstr.startsWith("0"))
				mmstr	= mmstr.substring(1);

			var ddstr	= arguments[1].substring(6);
			if(ddstr.startsWith("0"))
				ddstr	= ddstr.substring(1);
			
			var mm		= parseInt(mmstr)-1;
			var	dd		= parseInt(ddstr);
			
			if(mm>11)
				mm	= 11;

			var monObj	= {
					1: 31,2: 28,3: 31,4: 30,5: 31,6: 30
					,7: 31,8: 31,9: 30,10: 31,11: 30,12: 31
				};

			if (((yyyy % 4 == 0) && (yyyy % 100 != 0)) || (yyyy % 400 == 0)){
				monObj[2]	= 29;
			}else{
				monObj[2]	= 28;
			}
			
			if(monObj[mm+1]<dd)
				dd	= monObj[mm+1];
			this.date	= new Date(yyyy,mm,dd);
		}else if(arguments.length==2){
			this.date	= new Date(arguments[0],(arguments[1]*1)-1,1);

		}else if(arguments.length==3){
			this.date	= new Date(arguments[0],(arguments[1]*1)-1,arguments[2]);
		}
	}catch(e){
		alert(e.description);
	}
	this.getFullYear		= function(){
		return this.date.getFullYear();
	};
	this.getMonth		= function(){
		return this.date.getMonth()+1;
	};
	this.getDate		= function(){
		return this.date.getDate();
	};
	this.getDay		= function(){
		return this.date.getDay();
	};
	this.getHours		= function(){
		return this.date.getHours();
	};
	this.getMinutes		= function(){
		return this.date.getMinutes();
	};
	this.getSeconds		= function(){
		return this.date.getSeconds();
	};
	this.getMilliseconds		= function(){
		return this.date.getMilliseconds();
	};
	this.getDaysOfYear	= function(){
		var monObj	= {
			1: 31,2: 28,3: 31,4: 30,5: 31,6: 30
			,7: 31,8: 31,9: 30,10: 31,11: 30,12: 31
		};
		if (((this.getFullYear() % 4 == 0) && (this.getFullYear() % 100 != 0)) || (this.getFullYear() % 400 == 0)){
			monObj[2]	= 29;
		}else{
			monObj[2]	= 28;
		}
		return monObj;
	};
	this.getDaysOfMonth	= function(){
		if(this.date!=null && arguments.length!=2){
			arguments[0]	= this.getFullYear();
			arguments[1]	= this.getMonth();
		}
		var monObj	= {
			1: 31,2: 28,3: 31,4: 30,5: 31,6: 30
			,7: 31,8: 31,9: 30,10: 31,11: 30,12: 31
		};

		if (((arguments[0] % 4 == 0) && (arguments[0] % 100 != 0)) || (arguments[0] % 400 == 0)){
			monObj[2]	= 29;
		}else{
			monObj[2]	= 28;
		}
		return monObj[arguments[1]];
	};
	this.getFirstDate	= function(){
		return new JDate(this.getFullYear(),this.getMonth(),1);
	};
	this.getStartDay	= function(){
		return new JDate(this.getFullYear(),this.getMonth(),1).getDay();
	};
	this.getLastDay	= function(){
		return new JDate(this.getFullYear(),this.getMonth(),this.getDaysOfMonth()).getDay();
	};
	this.beforeMonth	= function(){
		if(arguments[0]<0)
			return this.afterMonth(arguments[0]*(-1));
		
		if(new JDate(this.getFullYear(),this.getMonth()-arguments[0],1).getDaysOfMonth()<this.getDate())
			return new JDate(this.getFullYear(),this.getMonth()-arguments[0],new JDate(this.getFullYear(),this.getMonth()-arguments[0],1).getDaysOfMonth());
		else
			return new JDate(this.getFullYear(),this.getMonth()-arguments[0],this.getDate());
	};
	this.afterMonth	= function(){
		if(arguments[0]<0)
			return this.beforeMonth(arguments[0]*(-1));
		if(new JDate(this.getFullYear(),this.getMonth()+arguments[0],1).getDaysOfMonth()<this.getDate())
			return new JDate(this.getFullYear(),this.getMonth()+arguments[0],new JDate(this.getFullYear(),this.getMonth()+arguments[0],1).getDaysOfMonth());
		else
			return new JDate(this.getFullYear(),this.getMonth()+arguments[0],this.getDate());
	};
	this.beforeDay	= function(){
		if(arguments[0]<0)
			return this.afterDay(arguments[0]*(-1));
		return new JDate(this.getFullYear(),this.getMonth(),this.getDate()-arguments[0]);
	};
	this.afterDay	= function(){
		if(arguments[0]<0)
			return this.beforeDay(arguments[0]*(-1));
		return new JDate(this.getFullYear(),this.getMonth(),this.getDate()+arguments[0]);
	};

	/*-----------------------------------------------------------------------------
	*		yyyy	Year - 2007
	*		yy		Year - 07
	*		mm		Month in year - 01~12
	*		dd		Day in month - 01~31
	*		hh		Hour in day - 0-23
	*		kk		Hour in day - 1-24
	*		hk		Hour in day - 0-11
	*		kh		Hour in day - 1-12
	*		kap		Am/Pm marker - 오전/오후
	*		eap		Am/Pm marker - PM/AM
	*		mi		Minute in hour - 00~59
	*		ss		Second in minute - 00~59
	*		mls		Millisecond - 000~999
	*		kwf		Day Of Week(korean) - 일요일~토요일
	*		kw		Day Of Week(korean) - 일~토
	*		ewf		Day Of Week(English) - Sun~Sat
	*		ew		Day Of Week(English) - sunday~Saturday
	* @return : String
	*
	* ex) format('yyyy-mm-dd, {hk kap}{hk eap} hh:mi:ss mls [kwf][kw] [ewf][ew]');
	----------------------------------------------------------------------------*/
	this.format = function(){
		if(arguments[0]==null || arguments[0].length==0){ alert("Input Date Format, Please.");return null;}
		arguments[0]	= arguments[0].toLowerCase();

		var yyyy	= new String(this.getFullYear());
		var yy		= yyyy.substring(2);
		var mm		= new String(this.getMonth());
		var dd		= new String(this.getDate());
		var hh		= new String(this.getHours());
		var hk		= (this.getHours()%12==0)?"12":(new String(this.getHours()%12));
		var kk		= hh=="0"?"24":hh;
		var kap		= this.getHours()>11?"오후":"오전";
		var eap		= this.getHours()>11?"PM":"AM";
		var mi		= new String(this.getMinutes());
		var ss		= new String(this.getSeconds());
		var mls		= new String(this.getMilliseconds());
		var kwf		= this.weekKor[this.getDay()];
		var kw		= kwf.substring(0,1);
		var ewf		= this.weekEng[this.getDay()];
		var ew		= ewf.substring(0,3);

		while(new String(yyyy).length<4)
			yyyy	= "0".concat(new String(yyyy));
		while(new String(yy).length<2)
			yy		= "0".concat(new String(yy));

		mm			= mm.length==1?"0".concat(mm):mm;
		dd			= dd.length==1?"0".concat(dd):dd;
		hh			= hh.length==1?"0".concat(hh):hh;
		hk			= hk.length==1?"0".concat(hk):hk;
		kk			= kk.length==1?"0".concat(kk):kk;
		mi			= mi.length==1?"0".concat(mi):mi;
		ss			= ss.length==1?"0".concat(ss):ss;
		while(mls.length<3){mls = "0".concat(mls);}

		return arguments[0].replaceAll("yyyy",yyyy)
			.replaceAll("kwf",kwf)
			.replaceAll("mls",mls)
			.replaceAll("kap",kap)
			.replaceAll("eap",eap)
			.replaceAll("ewf",ewf)
			.replaceAll("yy",yy)
			.replaceAll("mm",mm)
			.replaceAll("dd",dd)
			.replaceAll("hh",hh)
			.replaceAll("hk",hk)
			.replaceAll("kk",kk)
			.replaceAll("mi",mi)
			.replaceAll("ss",ss)
			.replaceAll("kw",kw)
			.replaceAll("ew",ew)
		;
	};
	this.betweenDay = function(){
		var tDate	= new Date();
		try{
			arguments[0].getTime();
		}catch(e){
			if(arguments[0].length>=8){
				arguments[0]	= new JDate(true,arguments[0]).date;
			}else{
				return -1;
			}
		}

		if(arguments[0]!=null){
			tDate	= arguments[0];
		}else{
			return -1;
		}
		var times1	= this.date.getTime();
		var times2	= tDate.getTime();
		return parseInt((times1-times2)/(1000*3600*24));
	};
}

