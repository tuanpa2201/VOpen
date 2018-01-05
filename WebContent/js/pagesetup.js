function selectMenu(menuId, liId) {
	var data = {
			'menuId' : menuId
		};
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onSelectedMenu',
			data, {
				toServer : true
			}), 0);
	if ($(window).width() < 979) {
		$('#hide-menu > span > a').trigger("click"); 
	}
	$("nav li").removeClass("active");
	$("#" + liId).addClass("active");
	$(".breadcrumb").empty();
	var i;
	for (i = 0; i < $("nav ul li.open").children("a").size(); i++) {
		var a = $("nav ul li.open").children("a")[i];
		$(".breadcrumb").append("<li>" + $(a).attr("title") + "</li>");
	}
	$(".breadcrumb").append("<li>" + $("#" + liId).children("a").attr("title") + "</li>");
}

function logout() {
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onLogout',
			null, {
				toServer : true
			}), 0);
}

function refresh() {
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onRefresh',
			null, {
				toServer : true
			}), 0);
}
function changeModule(root_menu_id) {
	var data = {
			'root_menu_id' : root_menu_id
		};
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onModuleChange',
			data, {
				toServer : true
			}), 0);
}

function changeLanguate(language) {
	var data = {
			'language' : language
		};
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onLanguage',
			data, {
				toServer : true
			}), 0);
}

function changeLanguate_login(language) {
	var data = {
			'language' : language
		};
	zAu.send(new zk.Event(zk.Widget.$('$divLanguage'), 'onLanguage',
			data, {
				toServer : true
			}), 0);
}

function changeCompany(company_id) {
	var data = {
			'id' : company_id
		};
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onCompany',
			data, {
				toServer : true
			}), 0);
}
function showCompany(){
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onShowCompany',
			null, {
				toServer : true
			}), 0);
}
function setTableStyle(gridId, style) {
	if (zk.Widget.$('$' + gridId + ''))
		$("#" + zk.Widget.$('$' + gridId + '').$n().id).find('table').addClass(style);
}

function handlePopStateEvent() {
	var data = {
			'search' : window.location.search
		};
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onPopState',
			data, {
				toServer : true
			}), 0);
}

function minifyMenu() {
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onMinifyMenu',
			null, {
				toServer : true
			}), 0);
}

function setupHistorySystem() {
	handlePopStateEvent();
	window.addEventListener("popstate", function(e) {
		 handlePopStateEvent();
	});
}
function updateClientSize() {
	var mainHeight = window.innerHeight;
	mainHeight = mainHeight - $("#content").offset().top;
	var data = {
			'main_height' : mainHeight
		};
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onMainSize',
			data, {
				toServer : true
			}), 0);
	
}
function setupResizeEvent() {
	updateClientSize();
	window.addEventListener("resize", function(e) {
		updateClientSize();
	});
}
function buildJarvisMenu() {
	$("nav ul").jarvismenu({
        "accordion": menu_accordion || !0,
        "speed": menu_speed || !0,
        "closedSign": '<em class="fa fa-plus-square-o"></em>',
        "openedSign": '<em class="fa fa-minus-square-o"></em>'
    })
}
function showProfile() {
	zAu.send(new zk.Event(zk.Widget.$('$div_nav'), 'onShowProfile',
			null, {
				toServer : true
			}), 0);
}