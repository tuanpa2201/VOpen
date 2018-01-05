/**
 * author PVM
 */
$(document).ready(function() {
});

function hGridPerform(){
	var height=jq('$vGrid').height()-jq('$gFilter').height()-10;
	jq('$gridPerform').css('height',height);
	
	
}

function hGridPerformTT(){
	var height=jq('$vGrid').height()-jq('$gFilter').height()-10;
	jq('$gridPerformTT').css('height',height);
	
	
}

function hGridShiftUsers(){
	var height=jq('$vGrid1').height()-jq('$gFilter1').height()-10;
	jq('$gridShiftUsers').css('height',height);
	
	
}
function hGridShiftUsersTT(){
	var height=jq('$vGrid').height()-jq('$gridFilter').height()-10;
	jq('$gridShiftUsersTT').css('height',height);
	
	
}

function hGridTrip(){
	var height=jq('$vGrid').height()-jq('$gridFilter').height()-10;
	jq('$gridreportTrip').css('height',height);
	
	
}


