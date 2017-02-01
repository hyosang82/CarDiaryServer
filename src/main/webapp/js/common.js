function getDisplayDateArray(ts) {
	var dt = new Date(ts);
	var rtn = [];
	
	rtn[0] = dt.getFullYear();
	rtn[1] = leftZeroPadding(dt.getMonth() + 1);
	rtn[2] = leftZeroPadding(dt.getDate());
	rtn[3] = leftZeroPadding(dt.getHours());
	rtn[4] = leftZeroPadding(dt.getMinutes());
	rtn[5] = leftZeroPadding(dt.getSeconds());
	
	return rtn;
}

function leftZeroPadding(v) {
	if(v >= 10) return v;
	else return "0" + v;
}