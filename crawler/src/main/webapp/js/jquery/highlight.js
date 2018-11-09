/*

highlight v3  !! Modified by Jon Raasch (http://jonraasch.com) to fix IE6 bug !!

Highlights arbitrary terms.

<http://johannburkard.de/blog/programming/javascript/highlight-javascript-text-higlighting-jquery-plugin.html>

MIT license.

Johann Burkard
<http://johannburkard.de>
<mailto:jb@eaio.com>

*/

jQuery.fn.highlight = function(pat) {
 function innerHighlight(node, pat) {
  var skip = 0;
  if (node.nodeType == 3) {
   var pos = node.data.toUpperCase().indexOf(pat);
   if (pos >= 0) {
    var spannode = document.createElement('span');
    spannode.className = 'highlight';
    var middlebit = node.splitText(pos);
		try {

			if ( middlebit.length >= pat.length) {
				var endbit = middlebit.splitText(pat.length);
			} else {
				var endbit = middlebit;
			}
		} catch (e) {
			console.log(e);
		}
    var middleclone = middlebit.cloneNode(true);
    spannode.appendChild(middleclone);
    middlebit.parentNode.replaceChild(spannode, middlebit);
    skip = 1;
   }
  }
  else if (node.nodeType == 1 && node.childNodes && !/(script|style)/i.test(node.tagName)) {
   for (var i = 0; i < node.childNodes.length; ++i) {
    i += innerHighlight(node.childNodes[i], pat);
   }
  }
  return skip;
 }
function sortArrayWordlength(wordArray) {
	var resultArray = [];
	var length = wordArray.length;
	for (var i = 0; i < length; i++) {
		var position = 0;
		var longest = 0;
		for (var j = 0; j < wordArray.length; j++) {
			if (longest < wordArray[j].length) {
				longest = wordArray[j].length;
				position = j;
			}
		}
		resultArray.push(wordArray[position]);
		wordArray.splice(position, 1);
	}
	return resultArray;
}
 return this.each(function() {
	var arr = sortArrayWordlength(pat);
	for (var i = 0; i < arr.length; i++) {
  	innerHighlight(this, arr[i].toUpperCase());
	}
 });
};
jQuery.fn.removeHighlight = function() {
 function newNormalize(node) {
    for (var i = 0, children = node.childNodes, nodeCount = children.length; i < nodeCount; i++) {
        var child = children[i];
        if (child.nodeType == 1) {
            newNormalize(child);
            continue;
        }
        if (child.nodeType != 3) { continue; }
        var next = child.nextSibling;
        if (next == null || next.nodeType != 3) { continue; }
        var combined_text = child.nodeValue + next.nodeValue;
        new_node = node.ownerDocument.createTextNode(combined_text);
        node.insertBefore(new_node, child);
        node.removeChild(child);
        node.removeChild(next);
        i--;
        nodeCount--;
    }
 }

 return this.find("span.highlight").each(function() {
    var thisParent = this.parentNode;
    thisParent.replaceChild(this.firstChild, this);
    newNormalize(thisParent);
 }).end();
};
