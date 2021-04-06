/*===============================================================================================*
* Supported Browser : Chrome , MSIE, FireFox													 *
* 																								 *
* Object	: JXParser.js																		 *
* Function	: XML Parsing in Javascript															 *
* Author	: Jang,Junghoon KWIC Inc.															 *
* Date		: 2010.04.15																		 *
* 																								 *
* caution)																						 *
*		Empty text-node's removed automatically.												 *
*		Don't use browser-dependency Xpath statement.											 *
*		Must be use function [sq(num)] in Xpath. Do not use number directly.				     *
*																								 *
* Example	:																					 *
* 																								 *
* 	var jxp	= new JXParser(																		 *
* 							"<root><CUSTINFO><NAME></NAME>"										 *
* 							+ "<LICENCE>Driver</LICENCE><LICENCE>Computing</LICENCE>"			 *
* 							+ "<FAMILY></FAMILY></CUSTINFO>"									 *
* 							+"<CUSTINFO><NAME/></CUSTINFO></root>"								 *
* 				);																				 *
* 																								 *
* 	jxp.ENC	= "UTF-8";																			 *
* 	jxp.viewErrorMsg	= true;																	 *
* 	jxp.viewWarningMsg	= true;																	 *
* 																								 *
* 	jxp.trim();																					 *
* 																								 *
* 	var node	= jxp.setValue("CUSTINFO["+jxp.sq(0)+"]/NAME","Jang,Junghoon.");				 *
* 	var str		= jxp.getValue("CUSTINFO["+jxp.sq(0)+"]/NAME");									 *
* 																								 *
* 	var node	= jxp.appendChild("CUSTINFO["+jxp.sq(1)+"]","AGE"	,"13");						 *
* 	var node	= jxp.insertBefore("CUSTINFO["+jxp.sq(0)+"]/NAME"	,"AGE","29");				 *
* 	var node	= jxp.moveBefore(																 *
* 						"CUSTINFO["+jxp.sq(0)+"]/LICENCE[text()='Driver']"						 *
* 						,"CUSTINFO["+jxp.sq(0)+"]/LICENCE[text()='Computing']"					 *
* 					);																			 *
* 																								 *
* 	var jxp2	= new JXParser();																 *
* 	var jXParser=jxp2.loadXML("<root><FAMILY><SON>JooHyun.</SON></FAMILY></root>");				 *
* 	var node	= jxp.importNode("CUSTINFO["+jxp.sq(1)+"]"	, jxp2.getElement("FAMILY") );		 *
* 																								 *
* 	var nodeList= jxp.getElements("CUSTINFO["+jxp.sq(1)+"]/LICENCE");							 *
* 	var node	= jxp.getElement("CUSTINFO["+jxp.sq(0)+"]/LICENCE[text()='Driver']");			 *
* 																								 *
* 	var num		= jxp.getLoopCnt("CUSTINFO","");												 *
* 																								 *
* 	var jXParser= jxp.removeElement("CUSTINFO["+jxp.sq(1)+"]/NAME");							 *
* 																								 *
* 	var attribute	= jxp.setAttribute(															 *
* 								"PUBLIC"														 *
* 								,"CUSTINFO["+jxp.sq(0)+"]/LICENCE["+jxp.sq(0)+"]"				 *
* 								,"TYPE"															 *
* 					);																			 *
* 	var attribute	= jxp.setAttribute(															 *
* 								"CORP"															 *
* 								,"CUSTINFO["+jxp.sq(0)+"]/LICENCE["+jxp.sq(1)+"]"				 *
* 								,"TYPE"															 *
* 					);																			 *
* 																								 *
* 	var str	= jxp.getAttribute("CUSTINFO["+jxp.sq(0)+"]/LICENCE["+jxp.sq(0)+"]/@TYPE");			 *
* 	var str	= jxp.getAttribute("CUSTINFO["+jxp.sq(0)+"]/LICENCE["+jxp.sq(0)+"]","TYPE");		 *
* 																								 *
* 	var node	= jxp.setValue("CUSTINFO["+jxp.sq(0)+"]/FAMILY"	, "SOLO");						 *
* 	var node	= jxp.getValue("CUSTINFO["+jxp.sq(0)+"]/FAMILY[text()='SOLO']");				 *
* 																								 *
* 	var str	= jxp.toString();																	 *
=================================================================================================*/

/**
* Get sequence number of element in xpath
* IE = return same number
* OTHER(FireFox,Chrome)	= return +1 number
*
* param count : 1
* param 1 : number		- IE sequence (from 0)
*
* return number
*
* ex)	jsq(0);
*/
function jsq(){
	var seq	= parseInt(arguments[0]);
	if (window.DOMParser){
		seq++;
	}
	return seq;
}
/**
* JXParser Object Constructor
*
* param count : 0-1
* param 1 : String		- xml String
*
* return void
*
* ex)	var jxp	= new JXParser();
*		var jxp	= new JXParser([xml String]);
*/
function JXParser(){
	this.ELEMENT_NODE	= 1;
	this.ATTRIBUTE_NODE	= 2;
	this.TEXT_NODE		= 3;

	this.FIRST			= 1;
	this.LAST			= 2;
	this.REPLACE		= 3;

	this.crNode			= null;
	//BROWSER = internet explorer ActiveXObject
	this.PARSER_AXO		= true;
	//broswer = firefox,Chrome DOMParser
	this.PARSER_DOM		= false;
	//current browser
	this.PARSER			= null;

	this.viewErrorMsg	= false;
	this.viewWarningMsg	= false;
	this.ENC			= "UTF-8";

	this.xmlDoc			= null;//dom document
	this.root			= null;//root element

	if(window.ActiveXObject || ("ActiveXObject" in window) || (window.ActiveXObject !== undefined)){
		this.PARSER	= this.PARSER_AXO;
	}else if (window.DOMParser){
		this.PARSER	= this.PARSER_DOM;
	}else{
		this.error("Not supported browser type. ");
		return;
	}

	if(arguments.length==1){
		this.loadXML(arguments[0]);
		this.trim();
	}
}

/**
* show error-message
*
* param count : 1
* param 1 : string		- warning-message
*
* return void
*
* ex)	jxp.error('u a so beautiful.');
*/
JXParser.prototype.error	= function(){
	if(this.viewErrorMsg)
		alert(arguments[0]);
};

/**
* show warning-message
*
* param count : 1
* param 1 : string		- warning-message
*
* return void
*
* ex)	jxp.warn('u a so beautiful.');
*/
JXParser.prototype.warn	= function(){
	if(this.viewWarningMsg)
		alert(arguments[0]);
};
/**
* Get sequence number of element in xpath
* IE = return same number
* OTHER(FireFox,Chrome)	= return +1 number
*
* param count : 1
* param 1 : number		- IE sequence (from 0)
*
* return number
*
* ex)	jxp.sq(0);
*/
JXParser.prototype.sq	= function(){
	var seq	= parseInt(arguments[0]);
	if(this.PARSER	== this.PARSER_DOM){
		seq++;
	}
	return seq;
};

/**
* create xml document from String
*
* param count : 1
* param 1 : String		- xml string
*
* return JXParser
*
* ex)	jxp.loadXML([xmlstring]);
*/
JXParser.prototype.loadXML	= function(){
	try{
		if(arguments.length!=1){
			throw new Error("argument[0] = xml string");
		}
		var xmlParser;
		if(this.PARSER==this.PARSER_AXO){
			this.xmlDoc			= new ActiveXObject("Microsoft.XMLDOM");//MSXML2.DOMDocument.6.0
			this.xmlDoc.async	= false;
			this.xmlDoc.preserveWhiteSpace	= false;
			this.xmlDoc.loadXML( arguments[0] );
			this.root			= this.xmlDoc.documentElement;

			var error = this.xmlDoc.parseError;
			if (error.errorCode != 0){
				throw new Error(error.errorCode + "\n" + error.reason + "\n" + error.line);
			}
		}else if(this.PARSER==this.PARSER_DOM){
			xmlParser		= new DOMParser();
			this.xmlDoc		= xmlParser.parseFromString( arguments[0], 'text/xml');
			this.root		= this.xmlDoc.firstChild;
		}else{
			throw new Error("Not supported browser type. [XML DOM load fail.]");
		}
	}catch(error){
		this.error("JXParser.loadXML() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return this;
};

/**
* Returns a string representation of element and its values
*
* param count : 0/1/2
* param 1 : Element		- element (null=root element)
* param 2 : boolean		- add declaratives of xml strinig
*
* return String			- xml string
*
* ex)	jxp.toString();
*		jxp.toString(true);
*		jxp.toString("CUSTINFO["+jxp.sq(0)+"]");
*		jxp.toString(custElement);
*		jxp.toString(custElement,true);
*/
JXParser.prototype.toString	= function() {
	var str = null;
	try{
//		var isPerfectXml	= false;
//		var dom	= null;

		if(arguments.length==0){
			arguments	= new Array(this.xmlDoc,false);
		}else if(arguments.length==1 && (typeof arguments[0] == 'boolean') ){
			arguments	= new Array(this.xmlDoc,arguments[0]);
		}else if(arguments.length==1 && (typeof arguments[0] == 'string') ){
			arguments	= new Array(this.getElement(arguments[0]),false);
		}else if( arguments.length==1 && (arguments[0]==null || arguments[0].nodeType==undefined) ){
			throw new Error("Invalid arguments.");
		}

		if(this.PARSER==this.PARSER_AXO){
			str	= arguments[0].xml;
		}else if(this.PARSER==this.PARSER_DOM){
			try{
				var serializer = new XMLSerializer();
				str = serializer.serializeToString(arguments[0]);
				delete serializer;
			}catch(e){
				throw new Error("DOM serialization is not supported.");
			}
		}else{
			throw new Error("Not supported browser type. [XML DOM load fail.]");
		}
		str	= str.replace("<?xml version=\"1.0\"?>","");
		if(arguments[1])
			str	= "<?xml version=\"1.0\" encoding=\""+this.ENC+"\"?>\n"+str;
	}catch(error){
		this.error("JXParser.toString() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return str;
};


/**
* Returns the first element for the given local name and any namespace.
*	- Creates an element of the type specified if it's not found.
*
* param count : 1
* param 1 : String		- xpath string
*
* return Element		- selected element
*
* ex)	jxp.createElementTree("RS-ROW/NAME");
*/
/*
JXParser.prototype.createElementTree		= function(){
	var selNode	= null;
	try{
		if(arguments.length!=1){//normal
			this.error("JXParser.createElementTree() argument error!!!\n\nargument[0] = element xpath");
			return null;
		}
		if(arguments[0]==null || arguments[0]==""){
			return this.root;
		}
		selNode	= this.getElement(arguments[0]);
		if(selNode!=null){
			return selNode;
		}
		if(arguments[0].indexOf("[")>=0 || arguments[0].indexOf("@")>=0 ){
			this.error(" Not supported condition.\n\n Remove conditional statement.\n\n ex) parent[1]/child --> parent/child");
			return null;
		}
		var idx			= -1;
		var parent		= null;
		var child		= null;
		var tmpPath		= "";

		while( (idx = arguments[0].indexOf("/",idx+1)) >= 0 ){
			tmpPath	= arguments[0].substring(0,idx);
			if(this.getElement(tmpPath)==null){
				parent	= this.getElement(tmpPath.substring(0,tmpPath.lastIndexOf("/")));
				child	= this.xmlDoc.createElement(tmpPath.substring(tmpPath.lastIndexOf("/")+1));
				parent.appendChild(child);
			}
		}
		tmpPath	= arguments[0].replace(tmpPath,"").replace("/","");
		if(child==null){
			child	= this.xmlDoc.createElement(tmpPath);
			this.root.appendChild(child);
		}else{
			parent	= child;
			child	= this.xmlDoc.createElement(tmpPath)
			parent.appendChild(child);
		}
		selNode	= child;
	}catch(error){
		this.error("JXParser.createElementTree() process error!!! : "+error+" : "+error.description+"\n\nargument[0] = node path");
		return null;
	}
	return selNode;
}
*/

/**
* Return elements for the given local name and any namespace.
*
* param count : 1 - 2
* param 1 : String				- xpath string
* param 2 : Element/String		- base element or xpath
*
* return Element array	- selected elements
*
* ex)	jxp.getElements("CUSTINFO["+jxp.sq(2)+"]/LICENCE");
*		jxp.getElements("LICENCE"	,"CUSTINFO[2]");
*		jxp.getElements("LICENCE"	,custElement);
*/
JXParser.prototype.getElements		= function(){
	var elements	= null;
	try{
		if(arguments.length==1){
			arguments	= new Array(arguments[0], this.root);
		}
		if(arguments.length!=2){//normal
			throw new Error("argument[0] = element xpath");
		}
		if(arguments[0]==null || arguments[0]==""){
			return new Array(this.root);
		}
		if(arguments[1]==null || arguments[1].nodeType==undefined){
			arguments[1]	= this.getElement(arguments[1]);
		}
		if(this.PARSER==this.PARSER_AXO){
			elements	= arguments[1].selectNodes(arguments[0]);
		}else if(this.PARSER==this.PARSER_DOM){
			elements	= new Array();
			var nsResolver = this.xmlDoc.createNSResolver(
					this.xmlDoc.ownerDocument == null
					? this.xmlDoc.documentElement
					: this.xmlDoc.ownerDocument.documentElement
			);
			var xpathResult	= this.xmlDoc.evaluate(arguments[0], arguments[1], nsResolver, XPathResult.ANY_TYPE, null );
			var element	= xpathResult.iterateNext();

			var count	= 0;
			while(element){
				elements[count++]	= element;
				element	= xpathResult.iterateNext();
			}
		}else{
			throw new Error("Not supported browser type. [XML DOM load fail.]");
		}
	}catch(error){
		this.error("JXParser.getElements() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return this._normalize(elements);
};

/**
* Returns the element for the given local name and any namespace.
*
* param count : 1 - 2
* param 1 : String		- xpath string
* param 2 : Element/String		- base element or xpath
*
* return Element 	- selected element
*
* ex)	jxp.getElement("CUSTINFO["+jxp.sq(2)+"]/LICENCE");
*		jxp.getElement("LICENCE"	,"CUSTINFO[2]");
*		jxp.getElement("LICENCE"	,custElement);
*/
JXParser.prototype.getElement		= function(){
	var orival	= arguments[0];
	try{
		if(arguments.length==1){
			arguments	= new Array(arguments[0], this.root);
		}
		if(arguments.length!=2){
			throw new Error("ArgumentError","argument[0] = xpath string");
		}
		var elements	= this.getElements(arguments[0],arguments[1]);
		if( elements==null || elements==undefined || elements[0]==null || elements[0]==undefined ){
			return null;
		}
	}catch(error){
		this.error("JXParser.getElement("+orival+") error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return elements[0];
};


/**
* Append arguments[1] at arguments[0]'s last position with arguments[2] value.
*
* param count : 2~3
* param 1 : String/element		- parent xpath string or parent element
* param 2 : String/element		- child name or child element
* param 3 : String				- child node value
*
* return Element 	- child element
*
* ex)	jxp.appendChild("RS-ROW["+jxp.sq(2)+"]/CUST","NAME");
*		jxp.appendChild(custElement					,"NAME");
*		jxp.appendChild("RS-ROW["+jxp.sq(2)+"]/CUST",nameElement);
*		jxp.appendChild(custElement					,nameElement);
*
*		jxp.appendChild("RS-ROW["+jxp.sq(2)+"]/CUST"	,"NAME"			,"Jang,junghoon");
*		jxp.appendChild(custElement						,"NAME"			,"Jang,junghoon");
*		jxp.appendChild("RS-ROW["+jxp.sq(2)+"]/CUST"	,nameElement	,"Jang,junghoon");
*		jxp.appendChild(custElement						,nameElement	,"Jang,junghoon");
*/
JXParser.prototype.appendChild		= function(){
	try{
		if(arguments.length<2){
			throw new Error("argument[0] = parent xpath string or parent element\n\nargument[1] = inserted child node/node name\n\nargument[2] = child node value");
		}
		if(arguments[0]==null){
			arguments[0]	= this.root;
		}
		if(arguments[1]==null){
			throw new Error("argument[1] is null");
		}
		if( arguments[0]==null || arguments[0].nodeType==undefined ){
			arguments[0]	= this.getElement(arguments[0]);
		}
		if( arguments[1].nodeType==undefined ){
			arguments[1]	= this.xmlDoc.createElement(arguments[1]);
		}
		arguments[1]	= arguments[0].appendChild(arguments[1]);
		if(arguments.length>2){
			this.setValue(arguments[1],arguments[2]);
		}
	}catch(error){
		this.error("JXParser.appendChild() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return arguments[1];
};
/**
* Insert arguments[1] in front of arguments[0] with arguments[2] value
*
* param count : 2-3
* param 1 : Element/String	- target child node/node name
* param 2 : Element/String	- inserted child node/node name
* param 3 : String			- inserted child's nodeValue
*
* return Element		- inserted child element
*
* ex)	jxp.insertBefore("CUSTINFO["+jxp.sq(1)+"]/NAME"		,"AGE");
*		jxp.insertBefore("CUSTINFO["+jxp.sq(1)+"]/NAME"		,ageElement);
*		jxp.insertBefore(nameElement						,"AGE");
*		jxp.insertBefore(nameElement						,ageElement);
*
*		jxp.insertBefore("CUSTINFO["+jxp.sq(1)+"]/NAME"		,"AGE"		, "21");
*		jxp.insertBefore("CUSTINFO["+jxp.sq(1)+"]/NAME"		,ageElement	, "21");
*		jxp.insertBefore(nameElement						,"AGE"		, "21");
*		jxp.insertBefore(nameElement						,ageElement	, "21");
*/
JXParser.prototype.insertBefore		= function(){
	try{
		if(arguments[0]==null || arguments[0].nodeType==undefined){
			arguments[0]	= this.getElement(arguments[0]);
		}

		if(arguments[0].length<2 || arguments[0]==null || arguments[1]==null){
			throw new Error("argument[0] = target child node/node name\n\nargument[1] = inserted child node/node name\n\nargument[2] = node value");
		}
		if(arguments[1].nodeType==undefined){
			arguments[1]	= this.xmlDoc.createElement(arguments[1]);
		}
		arguments[0].parentNode.insertBefore(arguments[1],arguments[0]);
		if(arguments.length==3){
			this.setValue(arguments[1],arguments[2]);
		}
	}catch(error){
		this.error("JXParser.insertBefore() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return arguments[0];
};

/**
* Remove given node in document
*
* param count : 1
* param 1 : Element/String	- node/xpath
*
* return JXParser
*
* ex)	jxp.removeElement("CUSTINFO["+jxp.sq(0)+"]/AGE");
*		jxp.removeElement(ageElement);
*/
JXParser.prototype.removeElement	= function(){
	try{
		if(arguments.length!=1){
			throw new Error("argument[0] = node or xpath");
		}
		if(arguments[0]==null || arguments[0].nodeType==undefined){
			arguments[0]	= this.getElement(arguments[0]);
		}
		if(arguments[0]==null){
			throw new Error("arguments[0] Element is null.");
		}
		arguments[0].parentNode.removeChild(arguments[0]);
	}catch(error){
		this.error("JXParser.removeElement() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return this;
};

/**
* Move arguments[1] in front of arguments[0]
*
* param count : 2
* param 1 : Element/String	- target node/xpath
* param 2 : Element/String	- moved node/xpath
*
* return Element		- moved element
*
* ex)	jxp.moveBefore("CUSTINFO["+jxp.sq(0)+"]/AGE"	, "CUSTINFO["+jxp.sq(0)+"]/NAME");
*		jxp.moveBefore("CUSTINFO["+jxp.sq(0)+"]/AGE"	, nameElement);
*		jxp.moveBefore(ageElement						, "CUSTINFO["+jxp.sq(0)+"]/NAME");
*		jxp.moveBefore(ageElement						, nameElement);
*
*/
JXParser.prototype.moveBefore		= function(){
	var newNode	= null;
	try{
		if(arguments.length!=2){
			throw new Error("argument[0] = moved node or xpath\n\nargument[1] = target node or xpath");
		}
		if(arguments[0]==null || arguments[0].nodeType==undefined){
			arguments[0]	= this.getElement(arguments[0]);
		}
		if(arguments[1]==null || arguments[1].nodeType==undefined){
			arguments[1]	= this.getElement(arguments[1]);
		}
		if(arguments[0]==null){
			throw new Error("argument[0] is null.");
		}
		if(arguments[1]==null){
			throw new Error("argument[1] is null.");
		}
		var parent	= arguments[1].parentNode;
		newNode	= this.insertBefore(arguments[0],arguments[1].cloneNode(true));
		parent.removeChild(arguments[1]);
	}catch(error){
		this.error("JXParser.moveBefore() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return newNode;
};
/**
* Import element from other Document or position
*
* param count : 2
* param 1 : Element/String	- parent node or xpath
* param 2 : Element			- child node
*
* return Element		- imported new child node
*
* ex)	jxp.importNode("CUSINFO["+jxp.sq(0)+"]"	, nameElement);
*		jxp.importNode(custElement				, nameElement);
*/
JXParser.prototype.importNode		= function(){
	var child	= null;
	try{
		if(arguments.length!=2){
			throw new Error("argument[0] = parent xpath or node\n\nargument[1] = child node");
		}
		if(arguments[1]==null || arguments[1].nodeType==undefined){
			throw new Error("arguments[1] must be ELEMENT-NODE.");
		}
		if(arguments[0]==null || arguments[0].nodeType==undefined){
			arguments[0]	= this.getElement(arguments[0]);
		}
		child	= this.appendChild(arguments[0],arguments[1].cloneNode(true));
	}catch(error){
		this.error("JXParser.importNode() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return child;
};
/**
* Returns the count of elements.
*
* param count : 1
* param 1 : string				- child xpath or name
* param 2 : Element/String		- base element or xpath
*
* return number			- selectd nodes length
*
* ex)	jxp.getLoopCnt("CUSTINFO[1]/LICENCE");
*		jxp.getLoopCnt("LICENCE"	,"CUSTINFO[0]");
*		jxp.getLoopCnt("LICENCE"	,custElement);
*/
JXParser.prototype.getLoopCnt		= function(){
	var cnt	= 0;
	try{
		if(arguments.length==1){
			arguments	= new Array(arguments[0],this.root);
		}
		if(arguments.length!=2){
			throw new Error("argument[0] = child xpath\n\nargument[1] = base element");
		}
		if(arguments[1]==null || arguments[1].nodeType==undefined){
			arguments[1]	= this.getElement(arguments[1]);
		}

		cnt	= this.getElements(arguments[0],arguments[1]).length;
	}catch(error){
		this.error("JXParser.getLoopCnt() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return cnt;
};
/**
* Get attribute value for given Xpath or element
*
* param count : 1 - 2
* param 1 : attribute xpath / parent xpath or element
* param 2 : String		- attribute name
*
* return String		- attribute value
*
* ex)	jxp.getAttribute("CUSTINFO["+jxp.sq(0)+"]/FAMILY["+jxp.sq(0)+"]/@RELATION");
*		jxp.getAttribute("CUSTINFO["+jxp.sq(0)+"]/FAMILY["+jxp.sq(0)+"]"	,"RELATION");
*		jxp.getAttribute(familyElement				,"RELATION");
*/
JXParser.prototype.getAttribute		= function(){
	var value	= null;
	try{
		if(arguments.length==1){
			value	= this.getElement(arguments[0]).nodeValue;
		}else if(arguments.length==2){
			if(arguments[0]==null || arguments[0].nodeType==undefined){
				arguments[0]	= this.getElement(arguments[0]);
			}
			if(arguments[1]==null || arguments[1]=="" ){
				throw new Error("arguments[1] is null.");
			}
			value	= arguments[0].getAttribute(arguments[1]);
		}
	}catch(error){
		this.error("JXParser.getAttribute() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return value;
};

/**
* Set attribute value for given Xpath or element
*
* param count : 2-3
* param 1 : attribute value
* param 2 : attribute xpath or element / parent xpath or element
* param 3 : String		- attribute name
*
* return attribute node
*
* ex)	jxp.setAttribute("SON"	, "CUSTINFO["+jxp.sq(0)+"]/FAMILY["+jxp.sq(0)+"]/@TYPE");
*		jxp.setAttribute("SON"	, typeAttributeElement);
*		jxp.setAttribute("SON"	, "CUSTINFO["+jxp.sq(0)+"]/FAMILY[1]"	, "TYPE");
*		jxp.setAttribute("SON"	, familyElement							, "TYPE");
*/
JXParser.prototype.setAttribute		= function(){
	try{
		var attribute	= null;
		if(arguments[1].nodeType==undefined){
			arguments[1]	= this.getElement(arguments[1]);
		}
		if(arguments.length==2){
			arguments[1].nodeValue	= arguments[0];
			attribute		= arguments[1];
		}else if(arguments.length==3){
			arguments[1].setAttribute(arguments[2],arguments[0]);
			attribute		= this.getElement("@"+arguments[2],arguments[1]);
		}else{
			throw new Error("Invalid arguments.");
		}
	}catch(error){
		this.error("JXParser.setAttribute() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return attribute;
};

/**
* Set the element's node-value for the given local name and any namespace.
*
* param count : 2-3
* param 1 : String		- xpath string or element
* param 2 : String		- value
* param 3 : position	- position of new TextNode
*
* return Element 		- element
*
* ex)	jxp.setValue("RS-ROW["+jxp.sq(0)+"]/NAME","Jang,Junghoon");
* 		jxp.setValue("RS-ROW["+jxp.sq(0)+"]/NAME","Jang,Junghoon",jxp.FIRST);
*/
JXParser.prototype.setValue		= function(){
	try{
		arguments[0]	= arguments[0]==null?this.root:arguments[0];
		arguments[1]	= arguments[1]==null?"":arguments[1];
		if(arguments[1].trim()==""){
			return arguments[0];
		}
		if(arguments[0].nodeType==undefined){
			arguments[0]	= this.getElement(arguments[0]);
		}

		if(arguments[0]==null){
			throw new Error("arguments[0] is null.");
			return arguments[0];
		}
		if(arguments[1].indexOf("<")>0 || arguments[1].indexOf(">")>0 || arguments[1].indexOf("&")>0){
			arguments[1]	= this.xmlDoc.createCDATASection(arguments[1]);
		}else{
			arguments[1]	= this.xmlDoc.createTextNode(arguments[1]);
		}

		if(arguments.length<3){
			arguments	= new Array(arguments[0],arguments[1],this.REPLACE);
		}
		if(arguments[0].childNodes.length==0){
			arguments[0].appendChild(arguments[1]);
		}else if(arguments[2]==this.FIRST){
			if(arguments[0].firstChild.nodeType==this.TEXT_NODE){
				arguments[0].replaceChild(arguments[1],arguments[0].firstChild);
			}else{
				arguments[0].insertBefore(arguments[1],arguments[0].firstChild);
			}
		}else if(arguments[2]==this.LAST){
			if(arguments[0].childNodes[arguments[0].childNodes.length-1].nodeType==this.TEXT_NODE){
				arguments[0].replaceChild(arguments[1],arguments[0].childNodes[arguments[0].childNodes.length-1]);
			}else{
				arguments[0].appendChild(arguments[1]);
			}
		}else if(arguments[2]==this.REPLACE){
			arguments[0].replaceChild(arguments[1],arguments[0].firstChild);
		}
	}catch(error){
		this.error("JXParser.setValue() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return arguments[0];
};


/**
* Returns the element's node-value for the given local name and any namespace.
*
* param count : 1-2
* param 1 : String		- xpath string
* param 2 : number		- sequence of text-node (from 1)
*
* return string 		- selected element's value
*
* ex)	jxp.getElement("RS-ROW[2]/NAME");
* 		jxp.getElement("RS-ROW[2]/NAME",2);
*/
JXParser.prototype.getValue		= function(){
	var orival	= arguments[0];
	try{
		if(arguments.length==1){
			arguments	= new Array(arguments[0],1);
		}
		if(arguments[0]==null || arguments[0].nodeType==undefined){
			arguments[0]	= this.getElement(arguments[0]);
		}
		if( arguments[0]==null || arguments[0]==undefined || arguments[0].firstChild==null || arguments[0].firstChild==undefined ){
			return null;
		}

//		var childs	= arguments[0].childNodes;
		var value	= null;
		var seq		= 0;
		for(var i=0;i<arguments[0].childNodes.length;i++){
			if(arguments[0].childNodes[i].nodeType==this.TEXT_NODE || arguments[0].childNodes[i].nodeType == 4){
				seq++;
			}
			if(seq==arguments[1]){
			    value	= arguments[0].childNodes[i].nodeValue;
			    value   = value.replace( /(^\s*)|(\s*$)/g, "" );
				break;
			}
		}
	}catch(error){
		this.error("JXParser.getValue("+orival+") error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return value;
};



/**
* Remove white space from xmlResult.
*
* param count : 1
* param 1 : String		- element
*
* return Array			- element list
*
* ex)	jxp._normalize(parentElement);
*/
JXParser.prototype._normalize	= function(){
	var result = new Array();
	try{
		if(arguments.length==0 ){
			arguments	= new Array(this.root);
		}
		if( arguments[0]==null || arguments[0].length==undefined){
			arguments[0]	= new Array(arguments[0]);
		}
		for(var i=0;i<arguments[0].length;i++){
			if(arguments[0][i]==null || arguments[0][i]==undefined){
				continue;
			}
			if(arguments[0][i].nodeType == this.TEXT_NODE){
				if(arguments[0][i].nodeValue.trim() != ""){
					result.push(arguments[0][i]);
				}
			}else{
				result.push((arguments[0][i]));
			}
		}
	}catch(error){
		this.error("JXParser._normalize() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return result;
};

/**
* Get Xpath string for given element.
*
* param count : 1-2
* param 1 : Element
* param 2 : whether show sequence number or not
*
* return String xpath
*
* ex)	jxp.getAbsolutePath(element);
*		jxp.getAbsolutePath(element,false);
*/
JXParser.prototype.getAbsolutePath	= function(){
	var element	= null;
	var xpath	= "";
	var no		= this.sq(0);
	try{
		if(arguments.length==1){
			arguments	= new Array(arguments[0],true);
		}
		if(arguments.length!=2 || arguments[0].nodeType!=this.ELEMENT_NODE)
			throw new Error("arguments unresolved.");

		element	= arguments[0];
		while(true){
			no	= this.sq(0);
			if(element==this.root){
				xpath	= "/"+xpath;
				break;
			}
			var elementArr	= this.getElements(element.nodeName,element.parentNode);
			if(elementArr.length>1){
				for(var i=0;i<elementArr.length;i++){
					if(elementArr[i]==element){
						no	= this.sq(i);
					}
				}
			}
			if(!arguments[1]){
				no	= this.sq(0);
			}
			xpath	= "/"+element.nodeName+(no==this.sq(0)?"":"["+no+"]")+xpath;
			element	= element.parentNode;
		}
	}catch(error){
		this.error("JXParser.getAbsolutePath() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return xpath;
};


/**
* Remove empty text-nodes in document.
*
* param count : 0-1
* param 1 : String		- element
*
* return this
*
* ex)	jxp.trim(element);
*		jxp.trim();
*/
JXParser.prototype.trim	= function(){
	try{
		if(arguments.length==0){
			arguments	= new Array(this.root);
		}
		var childs	= arguments[0].childNodes;
		for(var i=childs.length-1;i>=0;i--){
			if(childs[i].nodeType == this.TEXT_NODE && (childs[i]==null || childs[i].nodeValue==null || childs[i].nodeValue.trim()=="")){
				arguments[0].removeChild(childs[i]);
			}else{
				this.trim(childs[i]);
			}
		}
	}catch(error){
		this.error("JXParser.trim() error!!! : ["+error+"] \n\n"+error.description);
		throw error;
	}
	return this;
};
/*
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
*/