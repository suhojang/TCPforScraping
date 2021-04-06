function fn_loading(display){
	if($('.fullcustomloader').length==0){
		$('body').append("<div class='fullcustomloader' style='display:none;'></div><div class='customloader' style='display:none;position:absolute;'></div>");
	}
	var h	= $('body').prop("scrollHeight");
	$('.fullcustomloader').height(h);
	$('.customloader').center();
	if(display){
		$('.fullcustomloader').show();
		$('.customloader').show();
		$('.customloader').get(0).style.top	= new String($('.customloader').offset().top+$(window).scrollTop())+'px';
//		$('.customloader').scrollTop($('.customloader').offset().top+$(window).scrollTop());
	}else{
		$('.fullcustomloader').hide();
		$('.customloader').hide();
	}
}