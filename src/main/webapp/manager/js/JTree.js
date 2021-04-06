/*===============================================================================================
* Supported Browser : Chrome , MSIE, FireFox
* required lib : JXParser.js , JDocument.js
*
* Object	: JTree.js
* Function	: tree object from xml string
* Author	: Jang,Junghoon .
* Date		: 2011.04.18
*
*
*	{tree xml sample}
*
*	<?xml version='1.0' encoding='utf-8' ?>
*	<FOOD>
*		<jtree-properties>
*			<key><![CDATA[F]]></key>
*			<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*			<display><![CDATA[음식]]></display>
*		</jtree-properties>
*		<FRUIT>
*			<jtree-properties>
*				<key><![CDATA[F0001]]></key>
*				<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*				<display><![CDATA[열매]]></display>
*				<onclick><![CDATA[javascript:alert(1);]]></onclick>
*				<prefix><![CDATA[<a href='javascript:alert(2);'>2</a>]]></prefix>
*				<suffix><![CDATA[<a href='javascript:alert(3);'>3</a>]]></suffix>
*			</jtree-properties>
*
*			<APPLE>
*				<jtree-properties>
*					<key><![CDATA[F0001001]]></key>
*					<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*					<display><![CDATA[사과]]></display>
*					<onclick><![CDATA[javascript:alert(11);]]></onclick>
*					<prefix><![CDATA[<a href='javascript:alert(21);'>21</a>]]></prefix>
*					<suffix><![CDATA[<a href='javascript:alert(31);'>31</a>]]></suffix>
*				</jtree-properties>
*				<PRODUCT>
*					<jtree-properties>
*						<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*						<onclick><![CDATA[javascript:alert(11);]]></onclick>
*						<suffix><![CDATA[<a href='javascript:alert(31);'>31</a>]]></suffix>
*					</jtree-properties>
*				</PRODUCT>
*				<COUNTRY></COUNTRY>
*			</APPLE>
*			<ORANGE>
*				<jtree-properties>
*					<key><![CDATA[F0001002]]></key>
*					<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*					<display><![CDATA[오렌지]]></display>
*					<onclick><![CDATA[javascript:alert(12);]]></onclick>
*					<prefix><![CDATA[<a href='javascript:alert(22);'>22</a>]]></prefix>
*					<suffix><![CDATA[<a href='javascript:alert(32);'>32</a>]]></suffix>
*				</jtree-properties>
*			</ORANGE>
*			<CHERRY>
*				<jtree-properties>
*					<key><![CDATA[F0001003]]></key>
*					<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*					<display><![CDATA[체리]]></display>
*					<onclick><![CDATA[javascript:alert(13);]]></onclick>
*					<prefix><![CDATA[<a href='javascript:alert(23);'>23</a>]]></prefix>
*					<suffix><![CDATA[<a href='javascript:alert(33);'>33</a>]]></suffix>
*				</jtree-properties>
*			</CHERRY>
*		</FRUIT>
*		<MEAT>
*			<jtree-properties>
*				<key><![CDATA[F0002]]></key>
*				<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*				<display><![CDATA[육고기]]></display>
*				<onclick><![CDATA[javascript:alert(1);]]></onclick>
*				<prefix><![CDATA[<a href='javascript:alert(2);'>2</a>]]></prefix>
*				<suffix><![CDATA[<a href='javascript:alert(3);'>3</a>]]></suffix>
*			</jtree-properties>
*			<PORK>
*				<jtree-properties>
*					<key><![CDATA[F0002001]]></key>
*					<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*					<display><![CDATA[돈육]]></display>
*					<onclick><![CDATA[javascript:alert(11);]]></onclick>
*					<prefix><![CDATA[<a href='javascript:alert(21);'>21</a>]]></prefix>
*					<suffix><![CDATA[<a href='javascript:alert(31);'>31</a>]]></suffix>
*				</jtree-properties>
*			</PORK>
*			<BEEF>
*				<jtree-properties>
*					<key><![CDATA[F0002002]]></key>
*					<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*					<display><![CDATA[우육]]></display>
*					<onclick><![CDATA[javascript:alert(11);]]></onclick>
*					<prefix><![CDATA[<a href='javascript:alert(21);'>21</a>]]></prefix>
*					<suffix><![CDATA[<a href='javascript:alert(31);'>31</a>]]></suffix>
*				</jtree-properties>
*			</BEEF>
*			<CHICKEN>
*				<jtree-properties>
*					<key><![CDATA[F0002003]]></key>
*					<icon><![CDATA[./icon/jtree-icon.gif]]></icon>
*					<display><![CDATA[계육]]></display>
*					<onclick><![CDATA[javascript:alert(11);]]></onclick>
*					<prefix><![CDATA[<a href='javascript:alert(21);'>21</a>]]></prefix>
*					<suffix><![CDATA[<a href='javascript:alert(31);'>31</a>]]></suffix>
*				</jtree-properties>
*			</CHICKEN>
*		</MEAT>
*	</FOOD>
=================================================================================================*/

function JTree(){
	this.uniqueID		= document.jtree_td_id++;
	this.uniqueSEQ		= 0;
	document.setJTreeObject(this.uniqueID,this);

	this.JTREE_ELEMENT					= 'jtree-properties';	//hidden element
	this.PREFIX_ELEMENT					= "jtree-prefix";
	this.ICON_ELEMENT					= "jtree-icon";
	this.KEY_ELEMENT					= "jtree-key";
	this.ONCLICK_ELEMENT				= "jtree-onclick";
	this.DISPLAY_ELEMENT				= "jtree-display";
	this.SUFFIX_ELEMENT					= "jtree-suffix";

	this.DISPLAY_BTN_TYPE_ALL			= 1;	//display add,remove button at all elements
	this.DISPLAY_BTN_TYPE_LAST_PARENT	= 2;	//display add,remove button just at last parent elements
	this.SPACE							= "&nbsp;";

	this.viewErrorMsg					= false;	//show error message
	this.viewWarningMsg					= false;	//show warning message

	this.indentationSize				= 30;		//indentation size
	this.rowHeight						= 20;		//row height

	this.openTree						= true;
	this.displayRoot					= false;
	this.dispalyDefaultIcon				= true;
	this.displayAddBtn					= true;		//display add button
	this.displayRemoveBtn				= true;		//display remove button
	this.displayInsertBtn				= true;		//display insert button
	this.displayBtnType					= 1;		//button display type [JTree.DISPLAY_BTN_TYPE_ALL , JTree.DISPLAY_BTN_TYPE_LAST_PARENT]

	this.iconWidth						= 15;
	this.addDeleteIconWidth				= 18;

	this.bgcolor						= "#FFFFFF";
	this.mouseOverColor					= "#FFCC99";
	this.selectedColor					= this.bgcolor;

	this.displayArea					= null;				//display area
	this.xml							= null;				//tree JXParser object

	this.imgFolder						= "./icon/";
	this.defaultFolderIcon				= "jtree-home-icon.gif";
	this.defaultFileIcon				= "jtree-man-icon.gif";
	this.defaultAddIcon					= "jtree-add.gif";
	this.defaultDeleteIcon				= "jtree-delete.gif";
	this.defaultInsertIcon				= "jtree-insert.gif";
	this.defaultDownIcon				= "jtree-down.gif";
	this.defaultUpIcon					= "jtree-up.gif";

	this.JTREE_TD_ID					= "jtree-td-id-";
	this.JTREE_XML_ID					= "jtree-id";
	this.JTREE_XML_VISIBLE				= "jtree-visible";
	this.JTREE_FOLDER_ICON_ID			= "jtree-folder-icon-id-";

	this.onClick						= "getJTreeElementInfo";
	this.onChange						= "resetScreenValues";

	this.reloadTime						= 0;
	this.limitDepth						= -1;
	this.topIconVisible					= true;
	this.topIconPlusVisible				= true;
	this.displayAttr					= "name";//MNU_NM
}

/**
* show error-message
*
* param count : 1
* param 1 : string		- warning-message
*
* return void
*
* ex)	jtree.error('u a so beautiful.');
*/
JTree.prototype.error	= function(){
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
* ex)	jtree.warn('u a so beautiful.');
*/
JTree.prototype.warn	= function(){
	if(this.viewWarningMsg)
		alert(arguments[0]);
};

/**
* load tree xml
*
* param count : 1
* param 1 : string		- xml string
*
* return void
*
* ex)	jtree.loadXml('u a so beautiful.');
*/
JTree.prototype.loadXml	= function(){
	if(arguments.length==0){
		this.xml	= new JXParser("<?xml version='1.0' encoding='UTF-8' standalone='true' ?><root></root>");
	}else if(arguments.length==1){
		this.xml	= new JXParser(arguments[0]);
	}else{
		throw new Error('arguments error : JTree.loadXml(String xmlString)');
	}
};

/**
* make tree from xml
*
* param count : 0
*
* return void
*
* ex)	jtree.makeTree();
*/
JTree.prototype.makeTree	= function(){
	this.reloadTime++;
	this.uniqueSEQ		= 0;
	this.displayArea.innerHTML	= "";
	var table	= document.createElement("TABLE");
	var tbody	= document.createElement("TBODY");

	table.setAttribute("cellpadding",0);
	table.setAttribute("cellspacing",0);
	table.setAttribute("width","100%");
	table.setAttribute("border",0);
	table.setAttribute("bgcolor",this.bgcolor);

	table.appendChild(tbody);

	this.makeHTML(this.xml.root,tbody,0);

	this.displayArea.appendChild(table);
	this.flush();
};

/**
* make tree HTML from xml
*
* param count : 3
* param1 : parent xml element
* param2 : parent html element
* param3 : depth
*
* return void
*
* ex)	jtree.makeHTML();
*/
JTree.prototype.makeHTML	= function(){
	var element		= arguments[0];
	var parentBody	= arguments[1];
	var depth		= (arguments.length<2)?0:arguments[2];

	if(element.nodeType!=this.xml.ELEMENT_NODE)
		return;

	var childArr	= element.childNodes;

	for(var i=0;i<childArr.length;i++){
		if(childArr[i].nodeType!=this.xml.ELEMENT_NODE)
			continue;
		if(childArr[i].nodeName==this.JTREE_ELEMENT)
			continue;

		var tr		= document.createElement("TR");
		var td		= document.createElement("TD");
		tr.setAttribute("height",this.rowHeight);

		parentBody.appendChild(tr);
		tr.appendChild(td);

		var jnode	= new JTNode(this,td,childArr[i],depth);

		if(this.hasChildElement(childArr[i]))
			this.makeHTML(childArr[i],jnode.tbody,depth+1);
	}
};

/**
* display jtree
*
* param count : 0
*
* return void
*
* ex)	jtree.flush();
*/
JTree.prototype.flush	= function(){
	this.setVisible();
	this.displayArea.innerHTML	= this.displayArea.innerHTML;
	try{eval(this.onChange+"();");}catch(e){}
};

/**
* set visible by jtree-visible attribute
*
* param count : 1
* param1 : parent element
*
* return void
*
* ex)	jtree.setVisible();
*/
JTree.prototype.setVisible	= function(){
	if(arguments[0]==null)
		arguments[0]	= this.xml.root;
	if(arguments[0].nodeType!=this.xml.ELEMENT_NODE)
		return;

	var childs			= arguments[0].childNodes;
	var id				= null;
	var tdId			= null;
	var folderIconId	= null;
	var trs				= null;
	var folderIcon		= null;
	var visible			= true;
	var hasChild		= false;

	for(var i=0;i<childs.length;i++){
		if(childs[i].nodeType!=this.xml.ELEMENT_NODE)
			continue;

		id			= this.xml.getAttribute(childs[i],this.JTREE_XML_ID);
		hasChild	= this.hasChildElement(childs[i]);

		if(id!=null){
			if(this.xml.getAttribute(childs[i],this.JTREE_XML_VISIBLE)==null
				|| this.xml.getAttribute(childs[i],this.JTREE_XML_VISIBLE)!="N"){
				visible	= true;
			}else{
				visible	= false;
			}

			if(!visible && hasChild){
				tdId			= this.JTREE_TD_ID+id;
				folderIconId	= this.JTREE_FOLDER_ICON_ID+id;
				folderIcon		= document.getElementById(folderIconId);
				folderIcon.setAttribute("src",folderIcon.getAttribute("src").replaceAll("minus","plus"));
				trs				= document.getElementById(tdId).parentNode.parentNode.getElementsByTagName("TR");
				if(trs.length<=2)
					continue;

				for(var j=2;j<trs.length;j++){
					if(trs[j].style.display=="none"){
						trs[j].style.display	= "";
					}else{
						trs[j].style.display	= "none";
					}
				}
			}
		}
		if(hasChild)
			this.setVisible(childs[i]);
	}
};



/**
* whether element has child element
*
* param count : 1
* param1 : parent xml element
*
* return boolean
*
* ex)	jtree.hasChildElement(element);
*/
JTree.prototype.hasChildElement	= function(){
	var element		= arguments[0];
	var childArr	= element.childNodes;
	for(var i=0;i<childArr.length;i++){
		if(childArr[i].nodeName==this.JTREE_ELEMENT)
			continue;
		if(childArr[i].nodeType==this.xml.ELEMENT_NODE)
			return true;
	}
	return false;
};
/**
* whether element is last child
*
* param count : 1
* param1 : parent xml element
*
* return boolean
*
* ex)	jtree.isLastChild(element);
*/
JTree.prototype.isLastChild	= function(){
	var element		= arguments[0];
	if(element==null || element.parentNode==null)
		return true;

	var childArr	= element.parentNode.childNodes;
	var start	= false;
	for(var i=0;i<childArr.length;i++){
		if(childArr[i].nodeName==this.JTREE_ELEMENT)
			continue;
		if(childArr[i].nodeType!=this.xml.ELEMENT_NODE)
			continue;
		if(!start){
			if(childArr[i]==element)
				start	= true;
			continue;
		}
		return false;
	}
	return true;
};


/**
* append node on tree
*
* param count : 4
* param1 : jtree object
* param2 : parent html element
* param3 : xml element
* param4 : element depth
*
* return void
*
* ex)	new JTNode(jtree,tbody,element,2);
*/
function JTNode(){
	this.jtree			= arguments[0];
	this.parentBody		= arguments[1];
	this.element		= arguments[2];
	this.depth			= arguments[3];

	this.table			= null;
	this.tbody			= null;
	this.tr				= null;

	this.properties		= null;
	this.prefix			= "";
	this.onclick		= "";
	this.icon			= "";
	this.key			= "";
	this.display		= "";
	this.suffix			= "";

	if(this.parentBody==null || this.element==null || this.element.nodeType!=this.jtree.xml.ELEMENT_NODE)
		return null;

	if(this.element.nodeName==this.jtree.JTREE_ELEMENT)
		return null;
	if(this.depth>1 && this.parentBody.nodeName=="TD")
		this.parentBody.setAttribute("colspan",this.depth);

	this.table			= document.createElement("TABLE");
	this.table.setAttribute("cellpadding",0);
	this.table.setAttribute("cellspacing",0);
	this.table.setAttribute("width","100%");

	this.tbody			= document.createElement("TBODY");
	this.tr				= document.createElement("TR");
	this.tr.setAttribute("height",this.jtree.rowHeight);

	this.table.setAttribute("border",0);
	this.table.appendChild(this.tbody);
	this.tbody.appendChild(this.tr);
	this.appendIndentation();

	this.appendJTNode();

	this.parentBody.appendChild(this.table);
}



JTNode.prototype.appendJTNode	= function(){
	try{this.properties	= this.jtree.xml.getElement(this.jtree.JTREE_ELEMENT,this.element);}catch(ex){}
	var td				= document.createElement("TD");
	this.jtree.uniqueSEQ++;
	td.setAttribute("id",this.jtree.JTREE_TD_ID+this.jtree.uniqueID+"-"+(this.jtree.uniqueSEQ));

	this.jtree.xml.setAttribute(this.jtree.uniqueID+"-"+(this.jtree.uniqueSEQ),this.element,this.jtree.JTREE_XML_ID);
	if(!this.jtree.openTree && this.jtree.reloadTime==1)
		this.jtree.xml.setAttribute("N",this.element,this.jtree.JTREE_XML_VISIBLE);

	var limitX	= false;
	if(!this.jtree.topIconVisible && this.element.parentNode==this.jtree.xml.root){
		limitX	= true;
	}
	var showTopPlusIcon	= true;
	if(!this.jtree.topIconPlusVisible && this.element.parentNode==this.jtree.xml.root){
		showTopPlusIcon	= false;
	}
	var limit	= false;
	if(this.jtree.limitDepth>0){
		var cE		= this.element;
		try{
			for(var i=0;i<this.jtree.limitDepth-1;i++){
				cE	= cE.parentNode;
			}
			if(cE!=this.jtree.xml.xmlDoc && cE!=this.jtree.xml.root && cE!=null){
				limit	= true;
			}
		}catch(e){
		}
	}

	var cTable	= document.createElement("TABLE");
	cTable.setAttribute("cellpadding",0);
	cTable.setAttribute("cellspacing",0);
	cTable.setAttribute("border",0);
	cTable.style.tableLayout	= "fixed";

	var cTbody	= document.createElement("TBODY");
	var cTr		= document.createElement("TR");
	cTr.setAttribute("height",this.jtree.rowHeight);

	td.appendChild(cTable);
	cTable.appendChild(cTbody);
	cTbody.appendChild(cTr);

	var folderIconTd		= document.createElement("TD");
	folderIconTd.setAttribute("width",16);

	folderIconTd.innerHTML	= "<a href=\"javascript:document.setVisibleJTreeChild("+this.jtree.uniqueID+",'"
								+(this.jtree.uniqueID+"-"+this.jtree.uniqueSEQ)+"');\"><img id='"
								+(this.jtree.JTREE_FOLDER_ICON_ID+this.jtree.uniqueID+"-"+this.jtree.uniqueSEQ)
								+"' src='"+this.jtree.imgFolder+"jtree-minus"
								+"-"+(this.jtree.isLastChild(this.element)?"2":"3")+".gif"+"'/></a>";

	cTr.appendChild(folderIconTd);

	if(this.properties!=null){
		try{this.prefix		= this.jtree.xml.getValue( this.jtree.xml.getElement(this.jtree.PREFIX_ELEMENT,this.properties) );}catch(ex){}
		try{this.icon		= this.jtree.xml.getValue( this.jtree.xml.getElement(this.jtree.ICON_ELEMENT,this.properties) );}catch(ex){}
		try{this.key		= this.jtree.xml.getValue( this.jtree.xml.getElement(this.jtree.KEY_ELEMENT,this.properties) );}catch(ex){}
		try{this.onclick	= this.jtree.xml.getValue( this.jtree.xml.getElement(this.jtree.ONCLICK_ELEMENT,this.properties) );}catch(ex){}
		try{this.display	= this.jtree.xml.getValue( this.jtree.xml.getElement(this.jtree.DISPLAY_ELEMENT,this.properties) );}catch(ex){}
		try{this.suffix		= this.jtree.xml.getValue( this.jtree.xml.getElement(this.jtree.SUFFIX_ELEMENT,this.properties) );}catch(ex){}

		if(this.display==null || this.display.trim()==""){
			this.display	= ((this.jtree.xml.getAttribute(this.element,this.jtree.displayAttr)==null
										|| this.jtree.xml.getAttribute(this.element,this.jtree.displayAttr).trim()=="")
								?this.element.nodeName
								:this.jtree.xml.getAttribute(this.element,this.jtree.displayAttr));

		}
		if(this.icon.trim()!=""){
			var iconTd		= document.createElement("TD");
			iconTd.setAttribute("width",this.jtree.iconWidth);
			iconTd.innerHTML	= "<a href=\"javascript:document.setVisibleJTreeChild("+this.jtree.uniqueID+",'"
								+(this.jtree.uniqueID+"-"+this.jtree.uniqueSEQ)+"');\"><img src='"+this.icon+"'/></a>";
			cTr.appendChild(iconTd);
		}else if(this.jtree.dispalyDefaultIcon){
			var iconTd		= document.createElement("TD");
			iconTd.setAttribute("width",this.jtree.iconWidth);
			iconTd.innerHTML	= "<a href=\"javascript:document.setVisibleJTreeChild("+this.jtree.uniqueID+",'"
									+(this.jtree.uniqueID+"-"+this.jtree.uniqueSEQ)+"');\"><img src='"
									+this.jtree.imgFolder
									+(this.jtree.hasChildElement(this.element)?this.jtree.defaultFolderIcon:this.jtree.defaultFileIcon)
									+"'/></a>";
			cTr.appendChild(iconTd);
		}
		if(this.prefix.trim()!=""){
			var prefixTd		= document.createElement("TD");
			prefixTd.innerHTML	= this.jtree.SPACE+this.prefix;
			cTr.appendChild(prefixTd);
		}
		var displayTd			= document.createElement("TD");
		if(this.onclick!=""){
			displayTd.innerHTML		= "<a href=\""+this.onclick+"\" onmouseover=\"this.style.backgroundColor='"+this.jtree.mouseOverColor+"'\" onmouseout=\"this.style.backgroundColor='"+this.jtree.bgcolor+"'\">"
									+this.jtree.SPACE+(this.display.trim()==""?this.element.nodeName:this.display)
									+"</a>"
									+this.jtree.SPACE
									+this.jtree.SPACE
									+this.jtree.SPACE
									+((this.jtree.displayAddBtn&&!limit&&showTopPlusIcon)?"<a href=\"javascript:document.addJTreeChild("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultAddIcon+"' alt='add'/></a>":"")
									+this.jtree.SPACE
									+((this.jtree.displayRemoveBtn&&!limitX)?"<a href=\"javascript:document.removeJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultDeleteIcon+"' alt='remove'/></a>":"")
									+this.jtree.SPACE
									+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.insertJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultInsertIcon+"' alt='insert before'/></a>":"")
									+this.jtree.SPACE
									+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.upJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultUpIcon+"' alt='move up'/></a>":"")
									+this.jtree.SPACE
									+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.downJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultDownIcon+"' alt='move down'/></a>":"")
									;

		}else{
			displayTd.innerHTML		= this.jtree.SPACE
								+"<a href=\"javascript:"+this.jtree.onClick+"("+this.jtree.uniqueID+",'"+(this.jtree.uniqueID+"-"+this.jtree.uniqueSEQ)+"');\""
								+" onmouseover=\"this.style.backgroundColor='"+this.jtree.mouseOverColor+"'\" onmouseout=\"this.style.backgroundColor='"+this.jtree.bgcolor+"'\">"
								+(this.display.trim()==""?this.element.nodeName:this.display)
								+"</a>"
								+(this.suffix!=null?this.jtree.SPACE+this.suffix:"")
								+this.jtree.SPACE
								+this.jtree.SPACE
								+this.jtree.SPACE
								+((this.jtree.displayAddBtn&&!limit&&showTopPlusIcon)?"<a href=\"javascript:document.addJTreeChild("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultAddIcon+"' alt='add child'/></a>":"")
								+this.jtree.SPACE
								+((this.jtree.displayRemoveBtn&&!limitX)?"<a href=\"javascript:document.removeJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultDeleteIcon+"' alt='remove'/></a>":"")
								+this.jtree.SPACE
								+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.insertJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultInsertIcon+"' alt='insert before'/></a>":"")
								+this.jtree.SPACE
								+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.upJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultUpIcon+"' alt='move up'/></a>":"")
								+this.jtree.SPACE
								+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.downJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultDownIcon+"' alt='move down'/></a>":"")
								;
		}
		cTr.appendChild(displayTd);

	}else{
		if(this.jtree.dispalyDefaultIcon){
			var iconTd		= document.createElement("TD");
			iconTd.setAttribute("width",this.jtree.iconWidth);
			iconTd.innerHTML	= "<a href=\"javascript:document.setVisibleJTreeChild("+this.jtree.uniqueID+",'"
								+(this.jtree.uniqueID+"-"+this.jtree.uniqueSEQ)+"');\"><img src='"
								+this.jtree.imgFolder+(this.jtree.hasChildElement(this.element)?this.jtree.defaultFolderIcon:this.jtree.defaultFileIcon)
								+"'/></a>";
			cTr.appendChild(iconTd);
		}
		var displayTd		= document.createElement("TD");
		displayTd.innerHTML	= this.jtree.SPACE
							+"<a href=\"javascript:"+this.jtree.onClick+"("+this.jtree.uniqueID+",'"+(this.jtree.uniqueID+"-"+this.jtree.uniqueSEQ)+"');\""
							+" onmouseover=\"this.style.backgroundColor='"+this.jtree.mouseOverColor+"'\" onmouseout=\"this.style.backgroundColor='"+this.jtree.bgcolor+"'\">"
							+((this.jtree.xml.getAttribute(this.element,this.jtree.displayAttr)==null || this.jtree.xml.getAttribute(this.element,this.jtree.displayAttr).trim()=="")?this.element.nodeName:this.jtree.xml.getAttribute(this.element,this.jtree.displayAttr))
							+"</a>"
							+this.jtree.SPACE
							+this.jtree.SPACE
							+this.jtree.SPACE
							+((this.jtree.displayAddBtn&&!limit&&showTopPlusIcon)?"<a href=\"javascript:document.addJTreeChild("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultAddIcon+"' alt='add child'/></a>":"")
							+this.jtree.SPACE
							+((this.jtree.displayRemoveBtn&&!limitX)?"<a href=\"javascript:document.removeJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultDeleteIcon+"' alt='remove'/></a>":"")
							+this.jtree.SPACE
							+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.insertJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultInsertIcon+"' alt='insert before'/></a>":"")
							+this.jtree.SPACE
							+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.upJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultUpIcon+"' alt='move up'/></a>":"")
							+this.jtree.SPACE
							+((this.jtree.displayInsertBtn&&!limitX)?"<a href=\"javascript:document.downJTree("+(this.jtree.uniqueID+","+this.jtree.uniqueSEQ)+");\"><img src='"+this.jtree.imgFolder+this.jtree.defaultDownIcon+"' alt='move down'/></a>":"")
							;
		cTr.appendChild(displayTd);
	}

	this.tr.appendChild(td);
};

/**
* make indentation string
*
* param count : 0
*
* return void
*
* ex)	jtnode.appendIndentation();
*/
JTNode.prototype.appendIndentation	= function(){
	var width		= this.jtree.indentationSize;
	var parent		= null;

	for(var i=0;i<this.depth;i++){
		parent		= this.element;

		for(var j=0;j<this.depth-i;j++){
			parent		= parent.parentNode;
		}

		var td		= document.createElement("TD");
		td.setAttribute("width",width);
		if(!this.jtree.isLastChild(parent))
			td.innerHTML	= "<img style='float:left;' src='"+this.jtree.imgFolder+"jtree-1.gif'>";
		this.tr.appendChild(td);
	}
};


/**
* set visible & unvisible tree
*
* param count : 2
* param1 : jtree sequence
* param2 : td Id
*
* return void
*
* ex)	jtnode.setVisibleJTreeChild();
*/
if(document.setVisibleJTreeChild==undefined){
	document.setVisibleJTreeChild	= function(){
		var jtree			= document.getJTreeObject(arguments[0]);

		var element			= jtree.xml.getElement("//*[@"+jtree.JTREE_XML_ID+"='"+arguments[1]+"']");

		var tdId			= jtree.JTREE_TD_ID+arguments[1];
		var folderIconId	= jtree.JTREE_FOLDER_ICON_ID+arguments[1];

		var trs		= document.getElementById(tdId).parentNode.parentNode.getElementsByTagName("TR");

		if(trs.length<=2)
			return;

		for(var i=2;i<trs.length;i++){
			if(trs[i].style.display=="none"){
				trs[i].style.display	= "";
			}else{
				trs[i].style.display	= "none";
			}
		}
		var folderIcon	= document.getElementById(folderIconId);

		if(jtree.xml.getAttribute(element,jtree.JTREE_XML_VISIBLE)=="N"){
			folderIcon.setAttribute("src",folderIcon.getAttribute("src").replaceAll("plus","minus"));
			jtree.xml.setAttribute("Y",element,jtree.JTREE_XML_VISIBLE);
		}else{
			folderIcon.setAttribute("src",folderIcon.getAttribute("src").replaceAll("minus","plus"));
			jtree.xml.setAttribute("N",element,jtree.JTREE_XML_VISIBLE);
		}
	};
}

/**
* add child tree
*
* param count : 2
* param1 : jtree seq
* param2 : jtree Id
*
* return void
*
* ex)	document.addJTreeChild(1,'1-1');
*/
if(document.addJTreeChild==undefined){
	document.addJTreeChild	= function(){
		var jtree			= document.getJTreeObject(arguments[0]);
		var element			= jtree.xml.getElement("//*[@"+jtree.JTREE_XML_ID+"='"+jtree.uniqueID+"-"+arguments[1]+"']");
		jtree.xml.appendChild(element,"unidentified","");
		jtree.xml.setAttribute("Y",element,jtree.JTREE_XML_VISIBLE);
		jtree.makeTree();
	};
}
/**
* insert child tree
*
* param count : 2
* param1 : jtree seq
* param2 : jtree Id
*
* return void
*
* ex)	document.insertJTree(1,'1-1');
*/
if(document.insertJTree==undefined){
	document.insertJTree	= function(){
		var jtree			= document.getJTreeObject(arguments[0]);
		var element			= jtree.xml.getElement("//*[@"+jtree.JTREE_XML_ID+"='"+jtree.uniqueID+"-"+arguments[1]+"']");
		jtree.xml.insertBefore(element,"unidentified","");
		jtree.xml.setAttribute("Y",element,jtree.JTREE_XML_VISIBLE);
		jtree.makeTree();
	};
}
/**
* move up child
*
* param count : 2
* param1 : jtree seq
* param2 : jtree Id
*
* return void
*
* ex)	document.upJTree(1,'1-1');
*/
if(document.upJTree==undefined){
	document.upJTree	= function(){
		var jtree			= document.getJTreeObject(arguments[0]);
		var element			= jtree.xml.getElement("//*[@"+jtree.JTREE_XML_ID+"='"+jtree.uniqueID+"-"+arguments[1]+"']");

		if(!element.parentNode){
			return;
		}
		var bfElement	= null;
		var arr	= jtree.xml.getElements(element.nodeName,element.parentNode);
		if(element==arr[0]){
			return;
		}
		for(var i=0;i<arr.length;i++){
			if(arr[i]==element){
				if(bfElement==null){
					return;
				}else{
					jtree.xml.moveBefore(bfElement,element);
				}
				break;
			}
			bfElement	 = arr[i];
		}
		jtree.makeTree();
	};
}
/**
* move down child
*
* param count : 2
* param1 : jtree seq
* param2 : jtree Id
*
* return void
*
* ex)	document.downJTree(1,'1-1');
*/
if(document.downJTree==undefined){
	document.downJTree	= function(){
		var jtree			= document.getJTreeObject(arguments[0]);
		var element			= jtree.xml.getElement("//*[@"+jtree.JTREE_XML_ID+"='"+jtree.uniqueID+"-"+arguments[1]+"']");

		if(!element.parentNode){
			return;
		}

		var arr	= jtree.xml.getElements(element.nodeName,element.parentNode);
		if(element==arr[arr.length-1]){
			return;
		}
		for(var i=0;i<arr.length;i++){
			if(arr[i]==element){
				if(arr.length<=i+1 && !arr[i+1]){
					return;
				}else{
					jtree.xml.moveBefore(element,arr[i+1]);
				}
				break;
			}
		}
		jtree.makeTree();
	};
}
/**
* remove tree element
*
* param count : 2
* param1 : jtree seq
* param2 : jtree Id
*
* return void
*
* ex)	document.removeJTree(1,'1-1');
*/
if(document.removeJTree==undefined){
	document.removeJTree	= function(){
		var jtree			= document.getJTreeObject(arguments[0]);
		var element			= jtree.xml.getElement("//*[@"+jtree.JTREE_XML_ID+"='"+jtree.uniqueID+"-"+arguments[1]+"']");
		var name			= jtree.xml.getAttribute(element,jtree.displayAttr);

		if(name==null || name.trim()=="")
			name	= element.nodeName;

		mconfirm("[ "+name+" ] 항목을 삭제하시겠습니까?",null,function(){
			element.parentNode.removeChild(element);
			jtree.makeTree();
		});
	};
}
/**
* get jtree xml element
*
* param count : 2
* param1 : jtree seq
* param2 : jtree Id
*
* return xml element
*
* ex)	document.getJTreeXmlElement(1,'1-1');
*/
if(document.getJTreeXmlElement==undefined){
	document.getJTreeXmlElement	= function(){
		var jtree			= document.getJTreeObject(arguments[0]);
		return jtree.xml.getElement("//*[@"+jtree.JTREE_XML_ID+"='"+arguments[1]+"']");
	};
}
/**
* jtree object sequence
*/
if(document.jtree_td_id==undefined){
	document.jtree_td_id	= 0;
}
/**
* jtree object map
*/
if(document.jtreeObject==undefined){
	document.jtreeObject	= new Object();
}
/**
* save jtree object
*
* param count : 2
* param1 : jtree sequence
* param2 : jtree object
*
* return void
*
* ex)	document.setJTreeObject(1,jtree);
*/
if(document.setJTreeObject==undefined){
	document.setJTreeObject	= function(){
		document.jtreeObject[arguments[0]]	= arguments[1];
	};
}
/**
* get jtree object
*
* param count : 1
* param1 : jtree sequence
*
* return jtree
*
* ex)	document.getJTreeObject(1);
*/
if(document.getJTreeObject==undefined){
	document.getJTreeObject	= function(){
		return document.jtreeObject[arguments[0]];
	};
}
/**
* replace jtree element
*
* param count : 4
* param1 : jtree seq
* param2 : jtree Id
* param3 : tag name
* param4 : display name
*
* return void
*
* ex)	document.getJTreeObject(1);
*/
if(document.replaceJTreeElement==undefined){
	document.replaceJTreeElement	= function(){
		try{
			var jtreeSeq		= arguments[0];
			var jtreeElementId	= arguments[1];
			var tagName			= arguments[2];
			var dispalyName		= arguments[3];
			var menuObj			= arguments[4];

			if(jtreeSeq==null || jtreeSeq.trim()=="")
				return;
			if(jtreeElementId==null || jtreeElementId.trim()=="")
				return;
			if(tagName==null || tagName.trim()=="")
				return;
			if(dispalyName==null || dispalyName.trim()=="")
				dispalyName	= tagName;
			var jtree			= document.getJTreeObject(jtreeSeq);
			var element			= jtree.xml.getElement("//*[@"+jtree.JTREE_XML_ID+"='"+jtreeElementId+"']");

			var newElement		= jtree.xml.xmlDoc.createElement(tagName);
			var childs			= element.childNodes;
			for(var i=0;i<childs.length;i++){
				jtree.xml.importNode(newElement,childs[i]);
			}

			jtree.xml.setAttribute(dispalyName,newElement,jtree.displayAttr);
			var visible	= jtree.xml.getAttribute(element,jtree.JTREE_XML_VISIBLE);
			visible		= visible=="N"?"N":"Y";

			jtree.xml.setAttribute(visible,newElement,jtree.JTREE_XML_VISIBLE);

			for(key in menuObj){
				jtree.xml.setAttribute(menuObj[key],newElement,key);
			}

			element.parentNode.replaceChild(newElement,element);
			jtree.makeTree();
		}catch(e){
			throw e;
//			alert(e.description);
		}
	};
}
