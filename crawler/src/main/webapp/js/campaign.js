// The .bind method from Prototype.js 
if (!Function.prototype.bind) { // check if native implementation available
  Function.prototype.bind = function(){ 
    var fn = this, args = Array.prototype.slice.call(arguments),
        object = args.shift(); 
    return function(){ 
      return fn.apply(object, 
        args.concat(Array.prototype.slice.call(arguments))); 
    }; 
  };
}

function Campaign(name) {
	this.stich = 0;
	this.scoreStich = 0;
	this.scoreMeta = 0;
	this.status = 0;
	this.name = name;
}

Campaign.prototype.getListElement = function() {
			return "<li><a id=" 
					+ this.name + " href='#' >" 
					+ this.name + " <span class='badge'>" 
					+ this.stich + "</span>" + " <span id='scoreStich' class='badge' style='background-color: #AEB404'>"
					+ this.scoreStich + "</span>" + " <span id='scoreVar' class='badge' style='background-color: #04B404'>"
					+ this.scoreMeta + "</span>" + " <span id='scoreVar' class='badge pull-right' style='align:right;background-color: "
					+ colorPicker[this.status].back + "'><span class='glyphicon " 
					+ processIcons[this.status] + "' aria-hidden='true' style='color:" 
					+ colorPicker[this.status].front + ";'></span>" + "</span></a></li>";
}

Campaign.prototype.compare = function(camp) {
	for (prop in this) {
		if (typeof this[prop] != "function") {
			if (this[prop] != camp[prop] ) {
				return false;
			}
		}
	}
	return true;
}

Campaign.prototype.assimilate = function(camp) {
	for (prop in this) {
		if (typeof this[prop] != "function") {
			this[prop] = camp[prop];
		}
	}
}
