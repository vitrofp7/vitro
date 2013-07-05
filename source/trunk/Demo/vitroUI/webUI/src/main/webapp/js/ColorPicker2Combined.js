     /*
 * #--------------------------------------------------------------------------
 * # Copyright (c) 2013 VITRO FP7 Consortium.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Lesser Public License v3.0 which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/lgpl-3.0.html
 * #
 * # Contributors:
 * #     Antoniou Thanasis (Research Academic Computer Technology Institute)
 * #     Paolo Medagliani (Thales Communications & Security)
 * #     D. Davide Lamanna (WLAB SRL)
 * #     Alessandro Leoni (WLAB SRL)
 * #     Francesco Ficarola (WLAB SRL)
 * #     Stefano Puglia (WLAB SRL)
 * #     Panos Trakadas (Technological Educational Institute of Chalkida)
 * #     Panagiotis Karkazis (Technological Educational Institute of Chalkida)
 * #     Andrea Kropp (Selex ES)
 * #     Kiriakos Georgouleas (Hellenic Aerospace Industry)
 * #     David Ferrer Figueroa (Telefonica Investigación y Desarrollo S.A.)
 * #
 * #--------------------------------------------------------------------------
 */

var perlineColorPicker = 9;
     var divSetColorPicker = false;
     var curIdColorPicker;
     var colorLevelsColorPicker = Array('0', '3', '6', '9', 'C', 'F');
     var colorArrayColorPicker = Array();
     var ieColorPicker = false;
     var nocolorColorPicker = 'none';
        
    if (document.all) 
        { 
            ieColorPicker = true; nocolorColorPicker = ''; 
        }
    function getObjColorPicker(id) 
        {
		if (ieColorPicker) { return document.all[id]; } 
		else {	return document.getElementById(id);	}
	 }

     function addColorColorPicker(r, g, b) {
     	var red = colorLevelsColorPicker[r];
     	var green = colorLevelsColorPicker[g];
     	var blue = colorLevelsColorPicker[b];
     	addColorValueColorPicker(red, green, blue);
     }

     function addColorValueColorPicker(r, g, b) {
     	colorArrayColorPicker[colorArrayColorPicker.length] = '#' + r + r + g + g + b + b;
     }
     
     function setColorColorPicker(color) {
     	var link = getObjColorPicker(curIdColorPicker);
     	var field = getObjColorPicker(curIdColorPicker + 'field');
     	var picker = getObjColorPicker('colorpicker');
     	field.value = color;
     	if (color == '') {
	     	link.style.background = nocolorColorPicker;
	     	link.style.color = nocolorColorPicker;
	     	color = nocolorColorPicker;
     	} else {
	     	link.style.background = color;
	     	link.style.color = color;
	    }
     	picker.style.display = 'none';
	    eval(getObjColorPicker(curIdColorPicker + 'field').title);
     }
        
     function setDivColorPicker() {     
     	if (!document.createElement) { return; }
        var elemDiv = document.createElement('div');
        if (typeof(elemDiv.innerHTML) != 'string') { return; }
        genColorsColorPicker();
        elemDiv.id = 'colorpicker';
	    elemDiv.style.position = 'absolute';
        elemDiv.style.display = 'none';
        elemDiv.style.border = '#000000 1px solid';
        elemDiv.style.background = '#FFFFFF';
        elemDiv.innerHTML = '<span style="font-family:Verdana; font-size:11px;">Pick a color: ' 
          	+ '(<a href="javascript:setColorColorPicker(\'\');">No color</a>)<br>' 
        	+ getColorTableColorPicker() 
        	+ '<center><a href="http://www.flooble.com/scripts/colorpicker.php"'
        	+ ' target="_blank">color picker</a> by <a href="http://www.flooble.com" target="_blank"><b>flooble</b></a></center></span>';

        document.body.appendChild(elemDiv);
        divSetColorPicker = true;
     }
     
     function pickColorColorPicker(id) {
     	if (!divSetColorPicker) { setDivColorPicker(); }
     	var picker = getObjColorPicker('colorpicker');     	
		if (id == curIdColorPicker && picker.style.display == 'block') {
			picker.style.display = 'none';
			return;
		}
     	curIdColorPicker = id;
     	var thelink = getObjColorPicker(id);
     	picker.style.top = getAbsoluteOffsetTopColorPicker(thelink) + 20;
     	picker.style.left = getAbsoluteOffsetLeftColorPicker(thelink);     
	picker.style.display = 'block';
     }
     
     function genColorsColorPicker() {
        addColorValueColorPicker('0','0','0');
        addColorValueColorPicker('3','3','3');
        addColorValueColorPicker('6','6','6');
        addColorValueColorPicker('8','8','8');
        addColorValueColorPicker('9','9','9');                
        addColorValueColorPicker('A','A','A');
        addColorValueColorPicker('C','C','C');
        addColorValueColorPicker('E','E','E');
        addColorValueColorPicker('F','F','F');                                
			
        for (a = 1; a < colorLevelsColorPicker.length; a++)
			addColorColorPicker(0,0,a);
        for (a = 1; a < colorLevelsColorPicker.length - 1; a++)
			addColorColorPicker(a,a,5);

        for (a = 1; a < colorLevelsColorPicker.length; a++)
			addColorColorPicker(0,a,0);
        for (a = 1; a < colorLevelsColorPicker.length - 1; a++)
			addColorColorPicker(a,5,a);
			
        for (a = 1; a < colorLevelsColorPicker.length; a++)
			addColorColorPicker(a,0,0);
        for (a = 1; a < colorLevelsColorPicker.length - 1; a++)
			addColorColorPicker(5,a,a);
			
			
        for (a = 1; a < colorLevelsColorPicker.length; a++)
			addColorColorPicker(a,a,0);
        for (a = 1; a < colorLevelsColorPicker.length - 1; a++)
			addColorColorPicker(5,5,a);
			
        for (a = 1; a < colorLevelsColorPicker.length; a++)
			addColorColorPicker(0,a,a);
        for (a = 1; a < colorLevelsColorPicker.length - 1; a++)
			addColorColorPicker(a,5,5);

        for (a = 1; a < colorLevelsColorPicker.length; a++)
			addColorColorPicker(a,0,a);			
        for (a = 1; a < colorLevelsColorPicker.length - 1; a++)
			addColorColorPicker(5,a,5);
			
       	return colorArrayColorPicker;
     }
     function getColorTableColorPicker() {
         var colors = colorArrayColorPicker;
      	 var tableCode = '';
         tableCode += '<table border="0" cellspacing="1" cellpadding="1">';
         for (i = 0; i < colors.length; i++) {
              if (i % perlineColorPicker == 0) { tableCode += '<tr>'; }
              tableCode += '<td bgcolor="#000000"><a style="outline: 1px solid #000000; color: ' 
              	  + colors[i] + '; background: ' + colors[i] + ';font-size: 10px;" title="' 
              	  + colors[i] + '" href="javascript:setColorColorPicker(\'' + colors[i] + '\');">���</a></td>';
              if (i % perlineColorPicker == perlineColorPicker - 1) { tableCode += '</tr>'; }
         }
         if (i % perlineColorPicker != 0) { tableCode += '</tr>'; }
         tableCode += '</table>';
      	 return tableCode;
     }
     function relateColorColorPicker(id, color) {
     	var link = getObjColorPicker(id);
     	if (color == '') {
	     	link.style.background = nocolorColorPicker;
	     	link.style.color = nocolorColorPicker;
	     	color = nocolorColorPicker;
     	} else {
	     	link.style.background = color;
	     	link.style.color = color;
	    }
	    eval(getObjColorPicker(id + 'field').title);
     }
     function getAbsoluteOffsetTopColorPicker(obj) {
     	var top = obj.offsetTop;
     	var parent = obj.offsetParent;
     	while (parent != document.body) {
     		top += parent.offsetTop;
     		parent = parent.offsetParent;
     	}
     	return top;
     }
     
     function getAbsoluteOffsetLeftColorPicker(obj) {
     	var left = obj.offsetLeft;
     	var parent = obj.offsetParent;
     	while (parent != document.body) {
     		left += parent.offsetLeft;
     		parent = parent.offsetParent;
     	}
     	return left;
     }
