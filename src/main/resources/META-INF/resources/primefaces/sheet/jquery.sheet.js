/*
jQuery.sheet() The Web Based Spreadsheet
Version: 1.1.0
http://code.google.com/p/jquerysheet/
		
Copyright (C) 2010 Robert Plummer
Dual licensed under the LGPL v2 and GPL v2 licenses.
http://www.gnu.org/licenses/
*/

/*
	Dimensions Info:
		When dealing with size, it seems that outerHeight is generally the most stable cross browser
		attribute to use for bar sizing.  We try to use this as much as possible.  But because col's
		don't have boarders, we subtract or add jS.s.boxModelCorrection for those browsers.
	tr/td column and row Index VS cell/column/row index
		DOM elements are all 0 based (tr/td/table)
		Spreadsheet elements are all 1 based (A1, A1:B4, TABLE2:A1, TABLE2:A1:B4)
		Column/Row/Cell
	DOCTYPE:
		It is recommended to use STRICT doc types on the viewing page when using sheet to ensure that the heights/widths of bars and sheet rows show up correctly
		Example of recommended doc type: <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
*/
jQuery.fn.extend({
	sheet: function(settings) {
		settings = jQuery.extend({
			urlGet: 		"sheets/enduser.documentation.html", //local url, if you want to get a sheet from a url
			urlSave: 		"save.html", 					//local url, for use only with the default save for sheet
			editable: 		true, 							//bool, Makes the jSheetControls_formula & jSheetControls_fx appear
			allowToggleState: true,							//allows the function that changes the spreadsheet's state from static to editable and back
			urlMenu: 		"menu.html", 					//local url, for the menu to the right of title
			newColumnWidth: 120, 							//int, the width of new columns or columns that have no width assigned
			title: 			null, 							//html, general title of the sheet group
			inlineMenu:		null, 							//html, menu for editing sheet
			buildSheet: 	false,							//bool, string, or object
																//bool true - build sheet inside of parent
																//bool false - use urlGet from local url
																//string  - '{number_of_cols}x{number_of_rows} (5x100)
																//object - table
			calcOff: 		false, 							//bool, turns calculationEngine off (no spreadsheet, just grid)
			log: 			false, 							//bool, turns some debugging logs on (jS.log('msg'))
			lockFormulas: 	false, 							//bool, turns the ability to edit any formula off
			parent: 		jQuery(this), 					//object, sheet's parent, DON'T CHANGE
			colMargin: 		18, 							//int, the height and the width of all bar items, and new rows
			fnBefore: 		function() {}, 					//fn, fires just before jQuery.sheet loads
			fnAfter: 		function() {},	 				//fn, fires just after all sheets load
			fnSave: 		function() { jS.saveSheet(); }, //fn, default save function, more of a proof of concept
			fnOpen: 		function() { 					//fn, by default allows you to paste table html into a javascript prompt for you to see what it looks likes if you where to use sheet
				var t = prompt('Paste your table html here');
				if (t) {
					jS.openSheet(t);
				}
			},
			fnClose: 		function() {}, 					//fn, default clase function, more of a proof of concept
			fnAfterCellEdit:function() {},					//fn, fires just after someone edits a cell
			joinedResizing: false, 							//bool, this joins the column/row with the resize bar
			boxModelCorrection: 2, 							//int, attempts to correct the differences found in heights and widths of different browsers, if you mess with this, get ready for the must upsetting and delacate js ever
			showErrors:		true,							//bool, will make cells value an error if spreadsheet function isn't working correctly or is broken
			calculations:	{},								//object, used to extend the standard functions that come with sheet
			cellSelectModel: 'excel',						//string, 'excel' || 'oo' || 'gdocs' Excel sets the first cell onmousedown active, openoffice sets the last, now you can choose how you want it to be ;)
			autoAddCells:	true,
			caseInsensitive: false
		}, settings);
		
		
		var o = settings.parent;
		if (jQuery.sheet.instance) {
			o.sheetInstance = jQuery.sheet.createInstance(settings, jQuery.sheet.instance.length, o);
			jQuery.sheet.instance.push(o.sheetInstance);
		} else {
			o.sheetInstance = jQuery.sheet.createInstance(settings, 0, o);
			jQuery.sheet.instance = [o.sheetInstance];
		}
		return o;
	}
});

jQuery.sheet = {
	createInstance: function(s, I, origParent) { //s = jQuery.sheet settings, I = jQuery.sheet Instance Integer
		var jS = {
			version: '1.1.0',
			i: 0,
			I: I,
			sheetCount: 0,
			s: {},//s = settings object, used for shorthand, populated from jQuery.sheet
			obj: {//obj = object references
				//Please note, class references use the tag name because it's about 4 times faster
				barCorner:			function() { return jQuery('#' + jS.id.barCorner + jS.i); },
				barCornerAll:		function() { return s.parent.find('div.' + jS.cl.barCorner); },
				barCornerParent:	function() { return jQuery('#' + jS.id.barCornerParent + jS.i); },
				barCornerParentAll: function() { return s.parent.find('td.' + jS.cl.barCornerParent); },
				barTop: 			function() { return jQuery('#' + jS.id.barTop + jS.i); },
				barTopAll:			function() { return s.parent.find('div.' + jS.cl.barTop); },
				barTopParent: 		function() { return jQuery('#' + jS.id.barTopParent + jS.i); },
				barTopParentAll:	function() { return s.parent.find('div.' + jS.cl.barTopParent); },
				barLeft: 			function() { return jQuery('#' + jS.id.barLeft + jS.i); },
				barLeftAll:			function() { return s.parent.find('div.' + jS.cl.barLeft); },
				barLeftParent: 		function() { return jQuery('#' + jS.id.barLeftParent + jS.i); },
				barLeftParentAll:	function() { return s.parent.find('div.' + jS.cl.barLeftParent); },
				cellActive:			function() { return jQuery(jS.cellLast.td); },
				cellHighlighted:	function() { return jQuery(jS.highlightedLast.td); },
				controls:			function() { return jQuery('#' + jS.id.controls); },
				formula: 			function() { return jQuery('#' + jS.id.formula); },
				fullScreen:			function() { return jQuery('div.' + jS.cl.fullScreen); },
				inPlaceEdit:		function() { return jQuery('#' + jS.id.inPlaceEdit); },
				label: 				function() { return jQuery('#' + jS.id.label); },
				log: 				function() { return jQuery('#' + jS.id.log); },
				menu:				function() { return jQuery('#' + jS.id.menu); },
				pane: 				function() { return jQuery('#' + jS.id.pane + jS.i); },
				paneAll:			function() { return s.parent.find('div.' + jS.cl.pane); },
				parent: 			function() { return s.parent; },
				sheet: 				function() { return jQuery('#' + jS.id.sheet + jS.i); },
				sheetAll: 			function() { return s.parent.find('table.' + jS.cl.sheet); },
				tab:				function() { return jQuery('#' + jS.id.tab + jS.i); },
				tabAll:				function() { return this.tabContainer().find('a.' + jS.cl.tab); },
				tabContainer:		function() { return jQuery('#' + jS.id.tabContainer); },
				tableBody: 			function() { return document.getElementById(jS.id.sheet + jS.i); },
				tableControl:		function() { return jQuery('#' + jS.id.tableControl + jS.i); },
				tableControlAll:	function() { return s.parent.find('table.' + jS.cl.tableControl); },
				ui:					function() { return jQuery('#' + jS.id.ui); },
				uiActive:			function() { return s.parent.find('div.' + jS.cl.uiActive); }
			},
			id: {//id = id's references
				barCorner:			'jSheetBarCorner_' + I + '_',
				barCornerParent:	'jSheetBarCornerParent_' + I + '_',
				barTop: 			'jSheetBarTop_' + I + '_',
				barTopParent: 		'jSheetBarTopParent_' + I + '_',
				barLeft: 			'jSheetBarLeft_' + I + '_',
				barLeftParent: 		'jSheetBarLeftParent_' + I + '_',
				controls:			'jSheetControls_' + I,
				formula: 			'jSheetControls_formula_' + I,
				inPlaceEdit:		'jSheetInPlaceEdit_' + I,
				label: 				'jSheetControls_loc_' + I,
				log: 				'jSheetLog_' + I,
				menu:				'jSheetMenu_' + I,
				pane: 				'jSheetEditPane_' + I + '_',
				sheet: 				'jSheet_' + I + '_',
				tableControl:		'tableControl_' + I + '_',
				tab:				'jSheetTab_' + I,
				tabContainer:		'jSheetTabContainer_' + I + '_',
				ui:					'jSheetUI_' + I
			},
			cl: {//cl = class references
				barCorner:			'jSheetBarCorner',
				barCornerParent:	'jSheetBarCornerParent',
				barLeftTd:			'barLeft',
				barLeft: 			'jSheetBarLeft',
				barLeftParent: 		'jSheetBarLeftParent',
				barTop: 			'jSheetBarTop',
				barTopParent: 		'jSheetBarTopParent',
				barTopTd:			'barTop',
				cellActive:			'jSheetCellActive',
				cellHighlighted: 	'jSheetCellHighighted',
				controls:			'jSheetControls',
				formula: 			'jSheetControls_formula',
				fullScreen:			'jSheetFullScreen',
				inPlaceEdit:		'jSheetInPlaceEdit',
				menu:				'jSheetMenu',
				sheet: 				'jSheet',
				sheetPaneTd:		'sheetPane',
				label: 				'jSheetControls_loc',
				log: 				'jSheetLog',
				pane: 				'jSheetEditPane',
				tab:				'jSheetTab',
				tabContainer:		'jSheetTabContainer',
				tabContainerFullScreen: 'jSheetFullScreenTabContainer',
				tableControl:		'tableControl',
				toggle:				'cellStyleToggle',
				ui:					'jSheetUI',
				uiActive:			'ui-state-active',
				uiBar: 				'ui-widget-header',
				uiCellActive:		'ui-state-active',
				uiCellHighlighted: 	'ui-state-highlight',
				uiControl: 			'ui-widget-header ui-corner-top',
				uiControlTextBox:	'ui-widget-content',
				uiFullScreen:		'ui-widget-content ui-corner-all',
				uiInPlaceEdit:		'ui-state-active',
				uiMenu:				'ui-state-highlight',
				uiMenuUl: 			'ui-widget-header',
				uiMenuLi: 			'ui-widget-header',
				uiMenuHighlighted: 	'ui-state-highlight',
				uiPane: 			'ui-widget-content',
				uiParent: 			'ui-widget-content ui-corner-all',
				uiSheet:			'ui-widget-content',
				uiTab:				'ui-widget-header',
				uiTabActive:		'ui-state-highlight'
			},
			kill: function() {
				jS.obj.tabContainer().remove();
				jS.obj.fullScreen().remove();
				jS.obj.inPlaceEdit().remove();
				origParent.removeClass(jS.cl.uiParent).html('');
				cE = s = jQuery.sheet.instance[I] = jS = origParent.sheetInstance = null;
				delete cE;
				delete s;
				delete jQuery.sheet.instance[I];
				delete jS;
				delete origParent.sheetInstance;
			},
			controlFactory: {
				addRowMulti: function(qty, isBefore) {
					if (!qty) {
						qty = prompt('How many rows would you like to add?');
					}
					if (qty) {
						jS.controlFactory.addCells(null, isBefore, null, qty, 'row');
					}
				},
				addColumnMulti: function(qty, isBefore) {
					if (!qty) {
						qty = prompt('How many columns would you like to add?');
					}
					if (qty) {
						jS.controlFactory.addCells(null, isBefore, null, qty, 'col');
					}
				},
				addCells: function(eq, isBefore, eqO, qty, type) {
					jS.setDirty(true);
					
					var sheet = jS.obj.sheet();
					var sheetWidth = sheet.width();
					
					//jS.evt.cellEditAbandon();
					
					qty = (qty ? qty : 1);
					type = (type ? type : 'col');
					
					//var barLast = (type == 'row' ? jS.rowLast : jS.colLast);
					var cellLastBar = (type == 'row' ? jS.cellLast.row : jS.cellLast.col);
					
					if (!eq) {
						if (cellLastBar == -1) {
							eq = ':last';
						} else {
							eq = ':eq(' + cellLastBar + ')';
						}
					} else if (!isNaN(eq)){
						eq = ':eq(' + (eq - 1) + ')';
					}
					
					/*
					if (!eqO) {
						if (!eq && barLast > -1) {
							eqO = ':eq(' + barLast + ')';
						} else if (!eq || barLast < 1) {
							//if eq has no value, lets just add it to the end.
							eqO = ':last';
							eq = false;
						} else if (eq === true) {//if eq is boolean, then lets add it just after the currently selected row.
							eqO = ':eq(' + (cellLastBar - 1) + ')';
						} else {
							//If eq is a number, lets add it at that row
							eqO = ':eq(' + (eq - 1) + ')';
						}
					}
					*/
					
					var o;
					switch (type) {
						case "row":
							o = {
								bar: jS.obj.barLeft().find('div' + eq),
								barParent: jS.obj.barLeft(),
								cells: function() {
									return sheet.find('tr' + eq);
								},
								col: function() { return ''; },
								newBar: '<div class="' + jS.cl.uiBar + '" style="height: ' + (s.colMargin - s.boxModelCorrection) + 'px;" />',
								loc: function() {
									return jS.getTdLocation(o.cells().find('td:last'));
								},
								newCells: function() {
									var j = o.loc()[1];
									var newCells = '';
									
									for (var i = 0; i <= j; i++) {
										newCells += '<td />';
									}
									
									return '<tr style="height: ' + s.colMargin + 'px;">' + newCells + '</tr>';
								},
								newCol: '',
								reLabel: function() {								
									o.barParent.children().each(function(i) {
										jQuery(this).text(i + 1);
									});
								},
								dimensions: function(loc, bar, cell, col) {
									bar.height(cell.height(s.colMargin).outerHeight() - s.boxModelCorrection);
								},
								offset: [qty, 0]
							};
							break;
						case "col":
							o = {
								bar: jS.obj.barTop().find('div' + eq),
								barParent: jS.obj.barTop(),
								cells: function() {
									var cellStart = sheet.find('tr:first td' + eq);
									var cellEnd = sheet.find('td:last');
									var loc1 = jS.getTdLocation(cellStart);
									var loc2 = jS.getTdLocation(cellEnd);
									
									//we get the first cell then get all the other cells directly... faster ;)
									var cells = jQuery(jS.getTd(jS.i, loc1[0], loc1[1]));
									var cell;
									for (var i = 1; i <= loc2[0]; i++) {
										cells.push(jS.getTd(jS.i, i, loc1[1]));
									}
									
									return cells;
								},
								col: function() {
									return sheet.find('col' + eq);
								},
								newBar: '<div class="' + jS.cl.uiBar + '"/>',
								newCol: '<col />',
								loc: function(cells) {
									cells = (cells ? cells : o.cells());
									return jS.getTdLocation(cells.first());
								},
								newCells: function() {
									return '<td />';
								},
								reLabel: function() {
									o.barParent.children().each(function(i) {
										jQuery(this).text(cE.columnLabelString(i + 1));
									});
								},
								dimensions: function(loc, bar, cell, col) {								
									var w = s.newColumnWidth;
									col
										.width(w)
										.css('width', w + 'px')
										.attr('width', w + 'px');
									
									bar
										.width(w - s.boxModelCorrection);
									
									sheet.width(sheetWidth + (w * qty));
								},
								offset: [0, qty]
							};
							break;
					}
					
					//make undoable
					jS.cellUndoable.add(jQuery(sheet).add(o.barParent));
					
					var cells = o.cells();
					var loc = o.loc(cells);	
					var col = o.col();
					
					var newBar = o.newBar;
					var newCell = o.newCells();
					var newCol = o.newCol;
					
					var newCols = '';
					var newBars = '';
					var newCells = '';
					
					for (var i = 0; i < qty; i++) { //by keeping these variables strings temporarily, we cut down on using system resources
						newCols += newCol;
						newBars += newBar;
						newCells += newCell;
					}
					
					newCols = jQuery(newCols);
					newBars = jQuery(newBars);
					newCells = jQuery(newCells);
					
					if (isBefore) {
						cells.before(newCells);
						o.bar.before(newBars);
						jQuery(col).before(newCols);
					} else {
						cells.after(newCells);
						o.bar.after(newBars);
						jQuery(col).after(newCols);
					}
					
					jS.setTdIds(sheet);
					
					o.dimensions(loc, newBars, newCells, newCols);
					o.reLabel();

					jS.obj.pane().scroll();
					
					//offset formulas
					jS.offsetFormulaRange((isBefore ? loc[0] - qty : loc[0]) , (isBefore ? loc[1] - qty : loc[0]), o.offset[0], o.offset[1], isBefore);
					
					//Because the line numbers get bigger, it is possible that the bars have changed in size, lets sync them
					jS.sheetSyncSize();
					
					//Let's make it redoable
					jS.cellUndoable.add(jQuery(sheet).add(o.barParent));
				},
				addRow: function(atRow, isBefore, atRowQ) {
					jS.controlFactory.addCells(atRow, isBefore, atRowQ, 1, 'row');
				},
				addColumn: function(atColumn, isBefore, atColumnQ) {
					jS.controlFactory.addCells(atColumn, isBefore, atColumnQ, 1, 'col');
				},
				barLeft: function(reload, o) {//Works great!
					jS.obj.barLeft().remove();
					var barLeft = jQuery('<div border="1px" id="' + jS.id.barLeft + jS.i + '" class="' + jS.cl.barLeft + '" />');
					var heightFn;
					if (reload) { //This is our standard way of detecting height when a sheet loads from a url
						heightFn = function(i, objSource, objBar) {
							objBar.height(parseInt(objSource.outerHeight()) - s.boxModelCorrection);
						};
					} else { //This way of detecting height is used becuase the object has some problems getting
							//height because both tr and td have height set
							//This corrects the problem
							//This is only used when a sheet is already loaded in the pane
						heightFn = function(i, objSource, objBar) {
							objBar.height(parseInt(objSource.css('height').replace('px','')) - s.boxModelCorrection);
						};
					}
					
					o.find('tr').each(function(i) {
						var child = jQuery('<div>' + (i + 1) + '</div>');
						jQuery(barLeft).append(child);
						heightFn(i, jQuery(this), child);
					});
					
					jS.evt.barMouseDown.height(
						jS.obj.barLeftParent().append(barLeft)
					);
				},
				barTop: function(reload, o) { //Works great!
					jS.obj.barTop().remove();
					var barTop = jQuery('<div id="' + jS.id.barTop + jS.i + '" class="' + jS.cl.barTop + '" />');
					barTop.height(s.colMargin);
					
					var parents;
					var widthFn;
					
					if (reload) {
						parents = o.find('tr:first td');
						widthFn = function(obj) {
							return jS.attrH.width(obj);
						};
					} else {
						parents = o.find('col');
						widthFn = function(obj) {
							return parseInt(jQuery(obj).css('width').replace('px','')) - s.boxModelCorrection;
						};
					}
					
					parents.each(function(i) {
						var v = cE.columnLabelString(i + 1);
						var w = widthFn(this);
						
						var child = jQuery("<div>" + v + "</div>")
							.width(w)
							.height(s.colMargin);
						barTop.append(child);
					});
					
					jS.evt.barMouseDown.width(
						jS.obj.barTopParent().append(barTop)
					);
				},
				header: function() {
					jS.obj.controls().remove();
					jS.obj.tabContainer().remove();
					
					var header = jQuery('<div id="' + jS.id.controls + '" class="' + jS.cl.controls + '"></div>');
					
					var firstRow = jQuery('<table cellpadding="0" cellspacing="0" border="0"><tr /></table>').prependTo(header);
					var firstRowTr = jQuery('<tr />');
					
					if (s.title) {
						var title;
						if (jQuery.isFunction(s.title)) {
							title = jS.title(jS);
						} else {
							title = s.title;
						}
						firstRowTr.append(jQuery('<td style="width: auto;text-align: center;" />').html(title));
					}
					
					if (s.inlineMenu && s.editable) {
						var inlineMenu;
						if (jQuery.isFunction(s.inlineMenu)) {
							inlineMenu = s.inlineMenu(jS);
						} else {
							inlineMenu = s.inlineMenu;
						}
						firstRowTr.append(jQuery('<td style="text-align: center;" />').html(inlineMenu));
					}
					
					if (s.editable) {
						//Page Menu Control	
						if (jQuery.mbMenu) {
							jQuery('<div />').load(s.urlMenu, function() {
								var menu = jQuery('<td style="width: 50px; text-align: center;" id="' + jS.id.menu + '" class="rootVoices ui-corner-tl ' + jS.cl.menu + '" />')
									.html(
										jQuery(this).html()
											.replace(/sheetInstance/g, "jQuery.sheet.instance[" + I + "]")
											.replace(/menuInstance/g, I));
											
									menu
										.prependTo(firstRowTr)
										.buildMenu({
											menuWidth:		100,
											openOnRight:	false,
											containment: 	s.parent.attr('id'),
											hasImages:		false,
											fadeInTime:		0,
											fadeOutTime:	0,
											adjustLeft:		2,
											minZindex:		"auto",
											adjustTop:		10,
											opacity:		.95,
											shadow:			false,
											closeOnMouseOut:true,
											closeAfter:		1000,
											hoverIntent:	0, //if you use jquery.hoverIntent.js set this to time in milliseconds; 0= false;
											submenuHoverIntent: 0
										})
										.hover(function() {
											//not going to add to jS.cl because this isn't our control
											jQuery(this).addClass(jS.cl.uiMenu);
										}, function() {
											jQuery(this).removeClass(jS.cl.uiMenu);
										});
							});
						}
						
						//Edit box menu
						var secondRow = jQuery('<table cellpadding="0" cellspacing="0" border="0">' +
								'<tr>' +
									'<td style="width: 35px; text-align: right;" id="' + jS.id.label + '" class="' + jS.cl.label + '"></td>' +
									'<td>' +
										'<textarea id="' + jS.id.formula + '" class="' + jS.cl.formula + '"></textarea>' +
									'</td>' +
								'</tr>' +
							'</table>')
							.keydown(jS.evt.keyDownHandler.formulaOnKeyDown)
							.keyup(function() {
								jS.obj.inPlaceEdit().val(jS.obj.formula().val());
							})
							.change(function() {
								jS.obj.inPlaceEdit().val(jS.obj.formula().val());
							})
							.appendTo(header);
					}
					
					firstRowTr.appendTo(firstRow);
					
					var tabParent = jQuery('<div id="' + jS.id.tabContainer + '" class="' + jS.cl.tabContainer + '">' + 
									(s.editable ? '<span class="' + jS.cl.uiTab + ' ui-corner-bottom" title="Add a spreadsheet" i="-1">+</span>' : '<span />') + 
								'</div>')
							.mousedown(jS.evt.tabOnMouseDown);

					s.parent
						.html('')
						.append(header) //add controls header
						.append('<div id="' + jS.id.ui + '" class="' + jS.cl.ui + '">') //add spreadsheet control
						.after(tabParent);
				},
				sheet: function(size) {
					if (!size) {
						size = s.buildSheet;
					}
					size = size.toLowerCase().split('x');

					var columnsCount = parseInt(size[0]);
					var rowsCount = parseInt(size[1]);
					
					//Create elements before loop to make it faster.
					var newSheet = jQuery('<table  cellpadding="0" cellspacing="0" border="0" border="1px" class="' + jS.cl.sheet + '" id="' + jS.id.sheet + jS.i + '"></table>');
					var standardTd = '<td> </td>';
					var tds = '';
					
					//Using -- is many times faster than ++
					for (var i = columnsCount; i >= 1; i--) {
						tds += standardTd;
					}

					var standardTr = '<tr height="' + s.colMargin + '" style="height: ' + s.colMarg + ';">' + tds + '</tr>';
					var trs = '';
					for (var i = rowsCount; i >= 1; i--) {
						trs += standardTr;
					}
					
					newSheet.html('<tbody>' + trs + '</tbody>');
					
					newSheet.width(columnsCount * s.newColumnWidth);
					
					return newSheet;
				},
				sheetUI: function(o, i, fn, reloadBars) {
					if (!i) {
						jS.sheetCount = 0;
						jS.i = 0;
					} else {
						jS.sheetCount++;
						jS.i = jS.sheetCount;
						i = jS.i;
					}
					
					var objContainer = jS.controlFactory.table().appendTo(jS.obj.ui());
					var pane = jS.obj.pane().html(o);
							
					o = jS.tuneTableForSheetUse(o);
								
					jS.sheetDecorate(o);
					
					jS.controlFactory.barTop(reloadBars, o);
					jS.controlFactory.barLeft(reloadBars, o);
				
					jS.sheetTab(true);
					
					if (s.editable) {
						pane
							.mousedown(function(e) {
								jS.evt.cellOnMouseDown(e);
								return false;
							});
						pane
							.dblclick(jS.evt.cellOnDblClick);
					}
					
					jS.themeRoller.start(i);

					jS.setTdIds(o);
					
					jS.evt.scrollBars();
					
					jS.addTab();
					
					if (fn) {
						fn();
					}
					
					jS.log('Sheet Initialized');
					
					return objContainer;
				},
				table: function() {
					return jQuery('<table cellpadding="0" cellspacing="0" border="0" id="' + jS.id.tableControl + jS.i + '" class="' + jS.cl.tableControl + '">' +
						'<tbody>' +
							'<tr>' + 
								'<td id="' + jS.id.barCornerParent + jS.i + '" class="' + jS.cl.barCornerParent + '">' + //corner
									'<div style="height: ' + s.colMargin + '; width: ' + s.colMargin + ';" id="' + jS.id.barCorner + jS.i + '" class="' + jS.cl.barCorner +'"' + (s.editable ? ' onClick="jQuery.sheet.instance[' + I + '].cellSetActiveBar(\'all\');"' : '') + ' title="Select All">&nbsp;</div>' +
								'</td>' + 
								'<td class="' + jS.cl.barTopTd + '">' + //barTop
									'<div id="' + jS.id.barTopParent + jS.i + '" class="' + jS.cl.barTopParent + '"></div>' +
								'</td>' +
							'</tr>' +
							'<tr>' +
								'<td class="' + jS.cl.barLeftTd + '">' + //barLeft
									'<div style="width: ' + s.colMargin + ';" id="' + jS.id.barLeftParent + jS.i + '" class="' + jS.cl.barLeftParent + '"></div>' +
								'</td>' +
								'<td class="' + jS.cl.sheetPaneTd + '">' + //pane
									'<div id="' + jS.id.pane + jS.i + '" class="' + jS.cl.pane + '"></div>' +
								'</td>' +
							'</tr>' +
						'</tbody>' +
					'</table>');
				},
				chart: function(type, data, legend, axisLabels, w, h, row) {
					if (jGCharts) {
						var api = new jGCharts.Api();
						function refine(v) {
							var refinedV = new Array();
							jQuery(v).each(function(i) {
								refinedV[i] = jS.manageHtmlToText(v[i] + '');
							});
							return refinedV;
						}
						var o = {};
						
						if (type) {
							o.type = type;
						}
						
						if (data) {
							data = data.filter(function(v) { return (v ? v : 0); }); //remove nulls
							o.data = data;
						}
						
						if (legend) {
							o.legend = refine(legend);
						}
						
						if (axisLabels) {
							o.axis_labels = refine(axisLabels);
						}
						
						if (w || h) {
							o.size = w + 'x' + h;
						}
						
						return jS.controlFactory.safeImg(api.make(o), row);
					} else {
						return jQuery('<div>Charts are not enabled</div>');
					}
				},
				safeImg: function(src, row) {
					return jQuery('<img />')
						.hide()
						.load(function() { //prevent the image from being too big for the row
							jQuery(this).fadeIn(function() {
								jQuery(this).addClass('safeImg');
								jS.attrH.setHeight(parseInt(row), 'cell', false);
							});
						})
						.attr('src', src);
				},
				inPlaceEdit: function(td) {
					jS.obj.inPlaceEdit().remove();
					var formula = jS.obj.formula();					
					var offset = td.offset();
					var style = td.attr('style');
					var w = td.width();
					var h = td.height();
					var textarea = jQuery('<textarea id="' + jS.id.inPlaceEdit + '" class="' + jS.cl.inPlaceEdit + ' ' + jS.cl.uiInPlaceEdit + '" />')
						.css('left', offset.left)
						.css('top', offset.top)
						.width(w)
						.height(h)
						.keydown(jS.evt.inPlaceEditOnKeyDown)
						.keyup(function() {
							formula.val(textarea.val());
						})
						.change(function() {
							formula.val(textarea.val());
						})
						.appendTo('body')
						.val(formula.val())
						.focus()
						.select();
					
					//Make the textarrea resizeable automatically
					if (jQuery.fn.elastic) {
						textarea.elastic();
					}
				},
				input: {
					select: function() {
						return jQuery('<select style="width: 100%;" onchange="jQuery.sheet.instance[' + I + '].controlFactory.input.setValue(jQuery(this).val(), jQuery(this).parent());" class="clickable" />');
					},
					radio: function(v, cell) {
						var radio = jQuery('<span class="clickable" />');
						var name = I + '_table' + cell.tableI + '_cell_c' + (cell.col - 1) + '_r' + (cell.row - 1) + 'radio';
						for (var i = 0; i < (v.length <= 25 ? v.length : 25); i++) {
							if (v[i]) {
								radio.append('<input onchange="jQuery.sheet.instance[' + I + '].controlFactory.input.setValue(jQuery(this).val(), jQuery(this).parent().parent());" type="radio" value="' + v[i] + '" name="' + name + '" />' + v[i] + '<br />');
							}
						}
						return radio;
					},
					checkbox: function(v) {
						return jQuery('<input onclick="jQuery.sheet.instance[' + I + '].controlFactory.input.setValue(jQuery(this).is(\':checked\') + \'\', jQuery(this).parent());" type="checkbox" value="' + v + '" />' + v + '<br />');
					},
					setValue: function(v, p) {
						p.attr('selectedvalue', v);
						jS.calc(cE.calcState.i);
					},
					getValue: function(cell) {
						return jQuery(jS.getTd(cell.tableI, cell.row - 1, cell.col - 1)).attr('selectedvalue');
					}
				}
			},
			sizeSync: {
			
			},
			evt: {
				keyDownHandler: {
					enterOnInPlaceEdit: function(e) {
						if (!e.shiftKey) {
							return jS.evt.cellSetFocusFromKeyCode(e);
						} else {
							return true;
						}
					},
					enter: function(e) {
						if (!jS.cellLast.isEdit && !e.ctrlKey) {
							jS.cellLast.td.dblclick();
							return false;
						} else {
							return this.enterOnInPlaceEdit(e);
						}
					},
					tab: function(e) {
						return jS.evt.cellSetFocusFromKeyCode(e);
					},
					pasteOverCells: function(e) { //used for pasting from other spreadsheets
						if (e.ctrlKey) {
							var formula = jS.obj.formula(); //so we don't have to keep calling the function and wasting memory
							var oldVal = formula.val();
							formula.val('');  //we use formula to catch the pasted data
							jQuery(document).one('keyup', function() {
								var loc = jS.getTdLocation(jS.cellLast.td); //save the currrent cell
								var val = formula.val(); //once ctrl+v is hit formula now has the data we need
								var firstValue = '';
								formula.val(''); 
								var tdsBefore = jQuery('<div />');
								var tdsAfter = jQuery('<div />');
								
								var row = val.split(/\n/g); //break at rows
								for (var i = 0; i < row.length; i++) {
									var col = row[i].split(/\t/g); //break at columns
									for (var j = 0; j < col.length; j++) {
										if (col[j]) {
											var td = jQuery(jS.getTd(jS.i, i + loc[0], j + loc[1]));
											
											tdsBefore.append(td.clone());
											
											
											if ((col[j] + '').charAt(0) == '=') { //we need to know if it's a formula here
												td.attr('formula', col[j]);
											} else {
												td
													.html(col[j])
													.removeAttr('formula'); //we get rid of formula because we don't know if it was a formula, to check may take too long
											}
											
											tdsAfter.append(td.clone());
											
											if (i == 0 && j == 0) { //we have to finish the current edit
												firstValue = col[j];
											}
										}
									}
								}
								
								jS.cellUndoable.add(tdsBefore.children());
								jS.cellUndoable.add(tdsAfter.children());
								
								formula.val(firstValue);
								jS.setDirty(true);
								jS.evt.cellEditDone(true);
							});
						}
						return true;
					},
					findCell: function(e) {
						if (e.ctrlKey) { 
							jS.cellFind();
							return false;
						}
						return true;
					},
					redo: function(e) {
						if (e.ctrlKey && !jS.cellLast.isEdit) { 
							jS.cellUndoable.undoOrRedo();
							return false;
						}
						return true;
					},
					undo: function(e) {
						if (e.ctrlKey && !jS.cellLast.isEdit) {
							jS.cellUndoable.undoOrRedo(true);
							return false;
						}
						return true;
					},
					pageUpDown: function(reverse) {
						var pane = jS.obj.pane();
						var left = jS.cellLast.td.position().left;
						var top = 0;
						
						if (reverse) {
							top = 0;
							pane.scrollTop(pane.scrollTop() - pane.height());
							
						} else {
							top = pane.height() - (s.colMargin * 3);
							pane.scrollTop(pane.scrollTop() + top);
						}
						
						return jS.evt.cellSetFocusFromCoordinates(left, top);
					},
					formulaOnKeyDown: function(e) {
						switch (e.keyCode) {
							case key.ESCAPE: 	jS.evt.cellEditAbandon();
								break;
							case key.TAB: 		return jS.evt.keyDownHandler.tab(e);
								break;
							case key.ENTER: 	return jS.evt.keyDownHandler.enter(e);
								break;
							case key.LEFT:
							case key.UP:
							case key.RIGHT:
							case key.DOWN:		return jS.evt.cellSetFocusFromKeyCode(e);
								break;
							case key.PAGE_UP:	return jS.evt.keyDownHandler.pageUpDown(true);
								break;
							case key.PAGE_DOWN:	return jS.evt.keyDownHandler.pageUpDown();
								break;
							case key.V:			return jS.evt.keyDownHandler.pasteOverCells(e);
								break;
							case key.Y:			return jS.evt.keyDownHandler.redo(e);
								break;
							case key.Z:			return jS.evt.keyDownHandler.undo(e);
								break;
							case key.F:			return jS.evt.keyDownHandler.findCell(e);
							case key.CONTROL: //we need to filter these to keep cell state
							case key.CAPS_LOCK:
							case key.SHIFT:
							case key.ALT:
							case key.UP:
							case key.DOWN:
							case key.LEFT:
							case key.RIGHT:
								break;
							case key.HOME:
							case key.END:		jS.evt.cellSetFocusFromKeyCode(e);
								break;
							default: 			jS.cellLast.isEdit = true;
						}
					}
				},
				inPlaceEditOnKeyDown: function(e) {
					switch (e.keyCode) {
						case key.ENTER: 	return jS.evt.keyDownHandler.enterOnInPlaceEdit(e);
							break;
						case key.TAB: 		return jS.evt.keyDownHandler.tab(e);
							break;
						case key.ESCAPE:	jS.evt.cellEditAbandon(); return false;
							break;
					}
				},
				formulaChange: function(e) {
					jS.obj.inPlaceEdit().val(jS.obj.formula().val());
				},
				inPlaceEditChange: function(e) {
					jS.obj.formula().val(jS.obj.inPlaceEdit().val());
				},
				cellEditDone: function(forceCalc) {
					switch (jS.cellLast.isEdit) {
						case true:
							jS.obj.inPlaceEdit().remove();
							var formula = jS.obj.formula();
							formula.unbind('keydown'); //remove any lingering events from inPlaceEdit
							var td = jS.cellLast.td;
							
							switch(jS.isFormulaEditable(td)) {
								case true:
									//Lets ensure that the cell being edited is actually active
									if (td) { 
										//first, let's make it undoable before we edit it
										jS.cellUndoable.add(td);
										
										//This should return either a val from textbox or formula, but if fails it tries once more from formula.
										var v = jS.manageTextToHtml(formula.val());
										var prevVal = td.html();

										if (v.charAt(0) == '=') {
											td
												.attr('formula', v)
												.html('');
										} else {
											td
												.removeAttr('formula')
												.html(v);
										}
										
										if (v != prevVal || forceCalc) {
											jS.calc(jS.i);
										}
										
										jS.attrH.setHeight(jS.cellLast.row, 'cell');
										
										//Save the newest version of that cell
										jS.cellUndoable.add(td);
										
										formula.focus().select();
										jS.cellLast.isEdit = false;
										
										jS.setDirty(true);
										
										//perform final function call
										s.fnAfterCellEdit({
											td: jS.cellLast.td,
											row: jS.cellLast.row,
											col: jS.cellLast.col,
											spreadsheetIndex: jS.i,
											sheetIndex: I
										});
									}
							}
							break;
						default:
							jS.attrH.setHeight(jS.cellLast.row, 'cell', false);
					}
				},
				cellEditAbandon: function(skipCalc) {
					jS.obj.inPlaceEdit().remove();
					jS.themeRoller.cell.clearActive();
					jS.themeRoller.bar.clearActive();
					jS.themeRoller.cell.clearHighlighted();
					
					if (!skipCalc) {
						jS.calc(jS.i);
					}
					
					jS.cellLast.td = jQuery('<td />');
					jS.cellLast.row = jS.cellLast.col = -1;
					jS.rowLast = jS.colLast = -1;
					
					jS.labelUpdate('', true);
					jS.obj.formula()
						.val('');
					return false;
				},
				cellSetFocusFromCoordinates: function(left, top, skipOffset) {
					var pane = jS.obj.pane();
					var paneOffset = (skipOffset ? {left: 0, top: 0} : pane.offset());
					
					top += paneOffset.top + 2;
					left += paneOffset.left + 2;
					
					//here we double check that the coordinates are inside that of the pane, if so then we can continue
					if ((top >= paneOffset.top && top <= paneOffset.top + pane.height()) &&
						(left >= paneOffset.left && left <= paneOffset.left + pane.width())) {
						var td = jQuery(document.elementFromPoint(left - $window.scrollLeft(), top - $window.scrollTop()));
						
						
						//I use this snippet to help me know where the point was positioned
						/*jQuery('<div class="ui-widget-content" style="position: absolute;">TESTING TESTING</div>')
							.css('top', top + 'px')
							.css('left', left + 'px')
							.appendTo('body');
						*/
						
						if (jS.isTd(td)) {
							jS.themeRoller.cell.clearHighlighted();
							jS.cellEdit(td);
							return false;
						} else {
							return true;
						}
					} else {
						return false;
					}
				},
				cellSetFocusFromKeyCode: function(e) { //invoces a click on next/prev cell
					var c = jS.cellLast.col; //we don't set the cellLast.col here so that we never go into indexes that don't exist
					var r = jS.cellLast.row;
					var overrideIsEdit = false;
					
					switch (e.keyCode) {
						case key.UP: 		r--; break;
						case key.DOWN: 		r++; break;
						case key.LEFT: 		c--; break;
						case key.RIGHT: 	c++; break;
						case key.ENTER:		r++;
							overrideIsEdit = true;
							if (s.autoAddCells) {
								if (jS.cellLast.row == jS.sheetSize()[0]) {
									jS.controlFactory.addCells(':last', false, null, 1, 'row');
								}
							}
							break;
						case key.TAB:
							overrideIsEdit = true;
							if (e.shiftKey) {
								c--;
							} else {
								c++;
							}
							if (s.autoAddCells) {
								if (jS.cellLast.col == jS.sheetSize()[1]) {
									jS.controlFactory.addCells(':last', false, null, 1, 'col');
								}
							}
							break;
						case key.HOME:		c = 0; break;
						case key.END:		c = jS.cellLast.td.parent().find('td').length - 1; break;
					}
					
					//we check here and make sure all values are above -1, so that we get a selected cell
					c = (c < 0 ? 0 : c);
					r = (r < 0 ? 0 : r);
					
					//to get the td could possibly make keystrokes slow, we prevent it here so the user doesn't even know we are listening ;)
					if (!jS.cellLast.isEdit || overrideIsEdit) {
						//get the td that we want to go to
						var td = jS.getTd(jS.i, r, c);
					
						//if the td exists, lets go to it
						if (td) {
							jS.themeRoller.cell.clearHighlighted();
							jS.cellEdit(jQuery(td));
							return false;
						}
					}
					
					//default, can be overridden above
					return true;
				},
				cellOnMouseDown: function(e) {
					if (e.shiftKey) {
						jS.getTdRange(e, jS.obj.formula().val());
						return false;
					} else {
						if (jS.isTd([e.target])) {
							return jS.cellEdit(jQuery(e.target), true);
						} else {
							return true;
						}
					}			
				},
				cellOnDblClick: function(e) {
					jS.cellLast.isEdit = jS.isSheetEdit = true;
					jS.controlFactory.inPlaceEdit(jS.cellLast.td);
					jS.log('click, in place edit activated');
				},
				tabOnMouseDown: function(e) {
					var i = jQuery(e.target).attr('i');
					
					if (i != '-1' && i != jS.i) {
						jS.setActiveSheet(jQuery('#' + jS.id.tableControl + i), i); jS.calc(i);
					} else if (i != '-1' && jS.i == i) {
						jS.sheetTab();
					} else {
						jS.addSheet('5x10');
					}
					return false;
				},
				resizeBar: function(e, o) {
					//Resize Column & Row & Prototype functions are private under class jSheet		
					var target = jQuery(e.target);
					var resizeBar = {
						start: function(e) {
							
							jS.log('start resize');
							//I never had any problems with the numbers not being ints but I used the parse method
							//to ensuev non-breakage
							o.offset = target.offset();
							o.tdPageXY = [o.offset.left, o.offset.top][o.xyDimension];
							o.startXY = [e.pageX, e.pageY][o.xyDimension];
							o.i = o.getIndex(target);
							o.srcBarSize = o.getSize(target);
							o.edgeDelta = o.startXY - (o.tdPageXY + o.srcBarSize);
							o.min = 10;
							
							if (s.joinedResizing) {
								o.resizeFn = function(size) {
									o.setDesinationSize(size);
									o.setSize(target, size);
								};
							} else {
								o.resizeFn = function(size) {
									o.setSize(target, size);
								};
							}
							
							//We start the drag sequence
							if (Math.abs(o.edgeDelta) <= o.min) {
								//some ui enhancements, lets the user know he's resizing
								jQuery(e.target).parent().css('cursor', o.cursor);
								
								jQuery(document)
									.mousemove(resizeBar.drag)
									.mouseup(resizeBar.stop);
								
								return true; //is resizing
							} else {
								return false; //isn't resizing
							}
						},
						drag: function(e) {
							var newSize = o.min;

							var v = o.srcBarSize + ([e.pageX, e.pageY][o.xyDimension] - o.startXY);
							if (v > 0) {// A non-zero minimum size saves many headaches.
								newSize = Math.max(v, o.min);
							}

							o.resizeFn(newSize);
							return false;
						},
						stop: function(e) {
							o.setDesinationSize(o.getSize(target));
							
							jQuery(document)
								.unbind('mousemove')
								.unbind('mouseup');

							jS.obj.formula()
								.focus()
								.select();
							
							target.parent().css('cursor', 'pointer');
							
							jS.log('stop resizing');
						}
					};
					
					return resizeBar.start(e);
				},
				scrollBars: function() {
					var o = { //cut down on recursion, grabe them once
						pane: jS.obj.pane(), 
						barLeft: jS.obj.barLeftParent(), 
						barTop: jS.obj.barTopParent()
					};
					
					jS.obj.pane().scroll(function() {
						o.barTop.scrollLeft(o.pane.scrollLeft());//2 lines of beautiful jQuery js
						o.barLeft.scrollTop(o.pane.scrollTop());
					});
				},
				barMouseDown: {
					select: function(o, e, selectFn, resizeFn) {
						var isResizing = jS.evt.resizeBar(e, resizeFn);
								
						if (!isResizing) {
							selectFn(e.target);
							o
								.unbind('mouseover')
								.mouseover(function(e) {
									selectFn(e.target);
								});
								
							jQuery(document)
								.one('mouseup', function() {
									o
										.unbind('mouseover')
										.unbind('mouseup');
								});
						}
						
						return false;
					},
					first: 0,
					last: 0,
					height: function(o) {			
						var selectRow = function () {};
						
						o //let any user resize
							.unbind('mousedown')
							.mousedown(function(e) {
								if (!jQuery(e.target).hasClass(jS.cl.barLeft)) {
									jS.evt.barMouseDown.first = jS.evt.barMouseDown.last = jS.rowLast = jS.getBarLeftIndex(e.target);
									jS.evt.barMouseDown.select(o, e, selectRow, jS.rowResizer);
								}
								return false;
							});
						if (s.editable) { //only let editable select
							selectRow = function(o) {
								if (!jQuery(o).attr('id')) {
									var i = jS.getBarLeftIndex(o);
									
									jS.rowLast = i; //keep track of last row for inserting new rows
									jS.evt.barMouseDown.last = i;
									
									jS.cellSetActiveBar('row', jS.evt.barMouseDown.first, jS.evt.barMouseDown.last);
								}
							};
						}
					},
					width: function(o) {
						var selectColumn = function() {};
						
						o //let any user resize
							.unbind('mousedown')
							.mousedown(function(e) {
								if (!jQuery(e.target).hasClass(jS.cl.barTop)) {
									jS.evt.barMouseDown.first = jS.evt.barMouseDown.last = jS.colLast = jS.getBarTopIndex(e.target);
									jS.evt.barMouseDown.select(o, e, selectColumn, jS.columnResizer);
								}
								
								return false;
							});
						if (s.editable) { //only let editable select
							selectColumn = function(o) {
								if (!jQuery(o).attr('id')) {
									var i = jS.getBarTopIndex(o);
									
									jS.colLast = i; //keep track of last column for inserting new columns
									jS.evt.barMouseDown.last = i;
									
									jS.cellSetActiveBar('col', jS.evt.barMouseDown.first, jS.evt.barMouseDown.last);
								}
							};
						}
					}
				}
			},
			isTd: function(o) {
				if (o[0]) {
					if (!isNaN(o[0].cellIndex)) { 
						return true;
					}
				}
				return false;
			},
			isFormulaEditable: function(o) {
				if (s.lockFormulas) {
					if(o.attr('formula') !== undefined) {
						return false;
					}
				}
				return true;
			},
			toggleFullScreen: function() {
				if (jS.obj.fullScreen().is(':visible')) { //here we remove full screen
					s.parent = origParent;
					
					var w = s.parent.width();
					var h = s.parent.height();
					s.width = w;
					s.height = h;
					
					jS.obj.tabContainer().insertAfter(
						s.parent.append(jS.obj.fullScreen().children())
					).removeClass(jS.cl.tabContainerFullScreen);
					
					jS.obj.fullScreen().remove();
					
					jS.sheetSyncSize();
				} else { //here we make a full screen
					var w = $window.width() - 15;
					var h = $window.height() - 35;
					s.width = w;
					s.height = h;
					
					jS.obj.tabContainer().insertAfter(
						jQuery('<div class="' + jS.cl.fullScreen + ' ' + jS.cl.uiFullScreen + '" />')
							.append(s.parent.children())
							.appendTo('body')
					).addClass(jS.cl.tabContainerFullScreen);
					
					s.parent = jS.obj.fullScreen();
					
					jS.sheetSyncSize();
				}
			},
			tuneTableForSheetUse: function(o) {
				o
					.addClass(jS.cl.sheet)
					.attr('id', jS.id.sheet + jS.i)
					.attr('border', '1px')
					.attr('cellpadding', '0')
					.attr('cellspacing', '0');
					
				o.find('td.' + jS.cl.cellActive).removeClass(jS.cl.cellActive);
				
				return o;
			},
			attrH: {//Attribute Helpers
			//I created this object so I could see, quickly, which attribute was most stable.
			//As it turns out, all browsers are different, thus this has evolved to a much uglier beast
				width: function(o, skipCorrection) {
					return jQuery(o).outerWidth() - (skipCorrection ? 0 : s.boxModelCorrection);
				},
				widthReverse: function(o, skipCorrection) {
					return jQuery(o).outerWidth() + (skipCorrection ? 0 : s.boxModelCorrection);
				},
				height: function(o, skipCorrection) {
					return jQuery(o).outerHeight() - (skipCorrection ? 0 : s.boxModelCorrection);
				},
				heightReverse: function(o, skipCorrection) {
					return jQuery(o).outerHeight() + (skipCorrection ? 0 : s.boxModelCorrection);
				},
				syncSheetWidthFromTds: function(o) {
					var w = 0;
					o = (o ? o : jS.obj.sheet());
					o.find('col').each(function() {
						w += jQuery(this).width();
					});
					o.width(w);
					return w;
				},
				setHeight: function(i, from, skipCorrection, o) {
					var correction = 0;
					var h = 0;
					var fn;
					
					switch(from) {
						case 'cell':
							o = (o ? o : jS.obj.barLeft().find('div').eq(i));
							h = jS.attrH.height(jQuery(jS.getTd(jS.i, i, 0)).parent().andSelf(), skipCorrection);
							break;
						case 'bar':
							o = (o ? o : jQuery(jS.getTd(jS.i, i, 0)).parent().andSelf());
							h = jS.attrH.heightReverse(jS.obj.barLeft().find('div').eq(i), skipCorrection);
							break;
					}
					
					if (h) {
						jQuery(o)
							.height(h)
							.css('height', h)
							.attr('height', h);
					}

					return o;
				}
			},
			setTdIds: function(o) {
				o = (o ? o : jS.obj.sheet());
				o.find('tr').each(function(row) {
					jQuery(this).find('td').each(function(col) {
						jQuery(this).attr('id', jS.getTdId(jS.i, row, col));
					});
				});
			},
			setControlIds: function() {
				var resetIds = function(o, id) {
					o.each(function(i) {
						jQuery(this).attr('id', id + i);
					});
				};
				
				resetIds(jS.obj.sheetAll().each(function() {
					jS.setTdIds(jQuery(this));
				}), jS.id.sheet);
				
				resetIds(jS.obj.barTopAll(), jS.id.barTop);
				resetIds(jS.obj.barTopParentAll(), jS.id.barTopParent);
				resetIds(jS.obj.barLeftAll(), jS.id.barLeft);
				resetIds(jS.obj.barLeftParentAll(), jS.id.barLeftParent);
				resetIds(jS.obj.barCornerAll(), jS.id.barCorner);
				resetIds(jS.obj.barCornerParentAll(), jS.id.barCornerParent);
				resetIds(jS.obj.tableControlAll(), jS.id.tableControl);
				resetIds(jS.obj.paneAll(), jS.id.pane);
				resetIds(jS.obj.tabAll().each(function(j) {
					jQuery(this).attr('i', j);
				}), jS.id.tab);
			},
			columnResizer: {
				xyDimension: 0,
				getIndex: function(o) {
					return jS.getBarTopIndex(o);
				},
				getSize: function(o) {
					return jS.attrH.width(o, true);
				},
				setSize: function(o, v) {
					o.width(v);
				},
				setDesinationSize: function(w) {
					jS.sheetSyncSizeToDivs();
					
					jS.obj.sheet().find('col').eq(this.i)
						.width(w)
						.css('width', w)
						.attr('width', w);
					
					jS.obj.pane().scroll();
				},
				cursor: 'w-resize'
			},
			rowResizer: {
				xyDimension: 1,
					getIndex: function(o) {
						return jS.getBarLeftIndex(o);
					},
					getSize: function(o) {
						return jS.attrH.height(o, true);
					},
					setSize: function(o, v) {
						if (v) {
						o
							.height(v)
							.css('height', v)
							.attr('height', v);
						}
						return jS.attrH.height(o);
					},
					setDesinationSize: function() {
						//Set the cell height
						jS.attrH.setHeight(this.i, 'bar', true);
						
						//Reset the bar height if the resized row don't match
						jS.attrH.setHeight(this.i, 'cell', false);
						
						jS.obj.pane().scroll();
					},
					cursor: 's-resize'
			},
			toggleHide: {//These are not ready for prime time
				row: function(i) {
					if (!i) {//If i is empty, lets get the current row
						i = jS.obj.cellActive().parent().attr('rowIndex');
					}
					if (i) {//Make sure that i equals something
						var o = jS.obj.barLeft().find('div').eq(i);
						if (o.is(':visible')) {//This hides the current row
							o.hide();
							jS.obj.sheet().find('tr').eq(i).hide();
						} else {//This unhides
							//This unhides the currently selected row
							o.show();
							jS.obj.sheet().find('tr').eq(i).show();
						}
					} else {
						alert('No row selected.');
					}
				},
				rowAll: function() {
					jS.obj.sheet().find('tr').show();
					jS.obj.barLeft().find('div').show();
				},
				column: function(i) {
					if (!i) {
						i = jS.obj.cellActive().attr('cellIndex');
					}
					if (i) {
						//We need to hide both the col and td of the same i
						var o = jS.obj.barTop().find('div').eq(i);
						if (o.is(':visible')) {
							jS.obj.sheet().find('tbody tr').each(function() {
								jQuery(this).find('td').eq(i).hide();
							});
							o.hide();
							jS.obj.sheet().find('colgroup col').eq(i).hide();
							jS.toggleHide.columnSizeManage();
						}
					} else {
						alert('Now column selected.');
					}
				},
				columnAll: function() {
				
				},
				columnSizeManage: function() {
					var w = jS.obj.barTop().width();
					var newW = 0;
					var newW = 0;
					jS.obj.barTop().find('div').each(function() {
						var o = jQuery(this);
						if (o.is(':hidden')) {
							newW += o.width();
						}
					});
					jS.obj.barTop().width(w);
					jS.obj.sheet().width(w);
				}
			},
			merge: function() {
				var cellsValue = "";
				var cellValue = "";
				var cells = jS.obj.cellHighlighted();
				var formula;
				var cellFirstLoc = jS.getTdLocation(cells.first());
				var cellLastLoc = jS.getTdLocation(cells.last());
				var colI = (cellLastLoc[1] - cellFirstLoc[1]) + 1;
				
				if (cells.length > 1 && cellFirstLoc[0]) {
					for (var i = cellFirstLoc[1]; i <= cellLastLoc[1]; i++) {
						var cell = jQuery(jS.getTd(jS.i, cellFirstLoc[0], i)).hide();
						formula = cell.attr('formula');
						cellValue = cell.html();
						
						cellValue = (cellValue ? cellValue + ' ' : '');
						
						cellsValue = (formula ? "(" + formula.replace('=', '') + ")" : cellValue) + cellsValue;
						
						if (i != cellFirstLoc[1]) {
							cell
								.attr('formula', '')
								.html('')
								.hide();
						}
					}
					
					var cell = cells.first()
						.show()
						.attr('colspan', colI)
						.html(cellsValue);
					
					jS.setDirty(true);
					jS.calc(jS.i);
				} else if (!cellFirstLoc[0]) {
					alert('Merging is not allowed on the first row.');
				}
			},
			unmerge: function() {
				var cell = jS.obj.cellHighlighted().first();
				var loc = jS.getTdLocation(cell);
				var formula = cell.attr('formula');
				var v = cell.html();
				v = (formula ? formula : v);
				
				var rowI = cell.attr('rowspan');
				var colI = cell.attr('colspan');
				
				//rowI = parseInt(rowI ? rowI : 1); //we have to have a minimum here;
				colI = parseInt(colI ? colI : 1);
				
				var td = '<td />';
				
				var tds = '';
				
				if (colI) {
					for (var i = 0; i < colI; i++) {
						tds += td;
					}
				}
				
				for (var i = loc[1]; i < colI; i++) {
					jQuery(jS.getTd(jS.i, loc[0], i)).show();
				}
				
				cell.removeAttr('colspan');
				
				jS.setDirty(true);
				jS.calc(jS.i);
			},
			fillUpOrDown: function(goUp, skipOffsetForumals) { //default behavior is to go down var goUp changes it
				var cells = jS.obj.cellHighlighted();
				var cellActive = jS.obj.cellActive();
				//Make it undoable
				jS.cellUndoable.add(cells);
				
				var startFromActiveCell = cellActive.hasClass(jS.cl.uiCellHighlighted);
				var locFirst = jS.getTdLocation(cells.first());
				var locLast = jS.getTdLocation(cells.last());
				
				var v = jS.obj.formula().val();
				var fn;
				
				var formulaOffset = (startFromActiveCell ? 0 : 1);
				
				if ((v + '').charAt(0) == '=') {
					fn = function(o, i) {
						o
							.attr('formula', (skipOffsetForumals ? v : jS.offsetFormula(v, i + formulaOffset, 0)))
							.html(''); //we subtract one here because cells are 1 based and indexes are 0 based
					};
				} else {
					fn = function (o) {
						o
							.removeAttr('formula')
							.html(v);
					};
				}
				
				function fill(r, c, i) {
					var td = jQuery(jS.getTd(jS.i, r, c));
					
					//make sure the formula isn't locked for this cell
					if (jS.isFormulaEditable(td)) {
						fn(td, i);
					}
				}
				
				var k = 0;
				if (goUp) {
					for (var i = locLast[0]; i >= locFirst[0]; i--) {
						for (var j = locLast[1]; j >= locFirst[1]; j--) {
							fill(i, j, k);
							k++;
						}
					}
				} else {
					for (var i = locFirst[0]; i <= locLast[0]; i++) {
						for (var j = locFirst[1]; j <= locLast[1]; j++) {
							fill(i, j, k);
							k++;
						}
					}
				}
				
				jS.setDirty(true);
				jS.calc(jS.i);
				
				//Make it redoable
				jS.cellUndoable.add(cells);
			},
			offsetFormulaRange: function(row, col, rowOffset, colOffset, isBefore) {//col = int; offset = int
				var shiftedRange = {
					first: [(row ? row : 0), (col ? col : 0)],
					last: jS.sheetSize()
				};
				
				if (!isBefore && rowOffset) { //this shift is from a row
					shiftedRange.first[0]++;
					shiftedRange.last[0]++;
				}
				
				if (!isBefore && colOffset) { //this shift is from a col
					shiftedRange.first[1]++;
					shiftedRange.last[1]++;
				}
				
				function isInFormula(loc) {
					if ((loc[0] - 1) >= shiftedRange.first[0] &&
						(loc[1] - 1) >= shiftedRange.first[1] &&
						(loc[0] - 1) <= shiftedRange.last[0] &&
						(loc[1] - 1) <= shiftedRange.last[1]
					) {
						return true;
					} else {
						return false;
					}
				}
				
				function isInFormulaRange(startLoc, endLoc) {
					if (
						(
							(startLoc[0] - 1) >= shiftedRange.first[0] &&
							(startLoc[1] - 1) >= shiftedRange.first[1]
						) && (
							(startLoc[0] - 1) <= shiftedRange.last[0] &&
							(startLoc[1] - 1) <= shiftedRange.last[1]
						) && (
							(endLoc[0] - 1) >= shiftedRange.first[0] &&
							(endLoc[1] - 1) >= shiftedRange.first[1]
						) && (
							(endLoc[0] - 1) <= shiftedRange.last[0] &&
							(endLoc[1] - 1) <= shiftedRange.last[1]
						)
					) {
						return true;
					} else {
						return false;
					}
				}
				
				function reparseFormula(loc) {
					return ( //A1
						cE.columnLabelString(loc[1] + colOffset) + (loc[0] + rowOffset)
					);
				}
				
				function reparseFormulaRange(startLoc, endLoc) {
					return ( //A1:B4
						(cE.columnLabelString(startLoc[1] + colOffset) + (startLoc[0] + rowOffset)) + ':' + 
						(cE.columnLabelString(endLoc[1] + colOffset) + (endLoc[0] + rowOffset))
					);
				}
				
				jS.cylceCells(function (td) {
					var formula = td.attr('formula');
					
					if (formula && jS.isFormulaEditable(td)) {
						formula = formula.replace(cE.regEx.cell, 
							function(ignored, colStr, rowStr, pos) {
								var charAt = [formula.charAt(pos - 1), formula.charAt(ignored.length + pos)]; //find what is exactly before and after formula
								if (!colStr.match(cE.regEx.sheet) &&
									charAt[0] != ':' &&
									charAt[1] != ':'
								) { //verify it's not a range or an exact location
									
									var colI = cE.columnLabelIndex(colStr);
									var rowI = parseInt(rowStr);
									
									if (isInFormula([rowI, colI])) {
										return reparseFormula([rowI, colI]);
									} else {
										return ignored;
									}
								} else {
									return ignored;
								}
						});
						formula = formula.replace(cE.regEx.range, 
							function(ignored, startColStr, startRowStr, endColStr, endRowStr, pos) {
								var charAt = [formula.charAt(pos - 1), formula.charAt(ignored.length + pos)]; //find what is exactly before and after formula
								if (!startColStr.match(cE.regEx.sheet) &&
									charAt[0] != ':'
								) {
									
									var startRowI = parseInt(startRowStr);
									var startColI = cE.columnLabelIndex(startColStr);
									
									var endRowI = parseInt(endRowStr);
									var endColI = cE.columnLabelIndex(endColStr);
									
									if (isInFormulaRange([startRowI, startColI], [endRowI, endColI])) {
										return reparseFormulaRange([startRowI, startColI], [endRowI, endColI]);
									} else {
										return ignored;
									}
								} else {
									return ignored;
								}
						});
						
						td.attr('formula', formula);
					}

				}, [0, 0], shiftedRange.last);
				
				jS.calc(jS.i);
			},
			cylceCells: function(fn, firstLoc, lastLoc) {
				for (var i = firstLoc[0]; i < lastLoc[0]; i++) {
					for (var j = firstLoc[1]; j < lastLoc[1]; j++) {
						fn(jQuery(jS.getTd(jS.i, i, j)));
					}
				}
			},
			cycleCellsAndMaintainPoint: function(fn, firstLoc, lastLoc) {
				var o = [];
				for (var i = (firstLoc[0] < lastLoc[0] ? firstLoc[0] : lastLoc[0]) ; i <= (firstLoc[0] > lastLoc[0] ? firstLoc[0] : lastLoc[0]); i++) {
					for (var j = (firstLoc[1] < lastLoc[1] ? firstLoc[1] : lastLoc[1]); j <= (firstLoc[1] > lastLoc[1] ? firstLoc[1] : lastLoc[[1]]); j++) {
						o.push(jS.getTd(jS.i, i, j));
						fn(o[o.length - 1]);
					}
				}
				return o;
			},
			offsetFormula: function(formula, rowOffset, colOffset, includeRanges) {		
				//Cell References Fixed
				var charAt = [];
				var col = '';
				var row = '';
				formula = formula.replace(cE.regEx.cell, 
					function(ignored, colStr, rowStr, pos) {
						charAt[0] = formula.charAt(pos - 1);
						charAt[1] = formula.charAt(ignored.length + pos);
						
						charAt[0] = (charAt[0] ? charAt[0] : '');
						charAt[1] = (charAt[1] ? charAt[1] : '');
						
						if (colStr.match(cE.regEx.sheet) || 
							charAt[0] == ':' || 
							charAt[1] == ':'
						) { //verify it's not a range or an exact location
							return ignored;
						} else {
							row = parseInt(rowStr) + rowOffset;
							col = cE.columnLabelIndex(colStr) + colOffset;
							row = (row > 0 ? row : '1'); //table rows are never negative
							col = (col > 0 ? col : '1'); //table cols are never negative
							
							return cE.columnLabelString(col) + row;
						}
					}
				);
				return formula;
			},
			addTab: function() {
				jQuery('<span class="' + jS.cl.uiTab + ' ui-corner-bottom">' + 
						'<a class="' + jS.cl.tab + '" id="' + jS.id.tab + jS.i + '" i="' + jS.i + '">' + jS.sheetTab(true) + '</a>' + 
					'</span>')
						.insertBefore(
							jS.obj.tabContainer().find('span:last')
						);
			},
			sheetDecorate: function(o) {	
				jS.formatSheet(o);
				jS.sheetSyncSizeToCols(o);
				jS.sheetDecorateRemove();
			},
			formatSheet: function(o) {
				var tableWidth = 0;
				if (o.find('tbody').length < 1) {
					o.wrapInner('<tbody />');
				}
				
				if (o.find('colgroup').length < 1 || o.find('col').length < 1) {
					o.remove('colgroup');
					var colgroup = jQuery('<colgroup />');
					o.find('tr:first').find('td').each(function() {
						var w = s.newColumnWidth;
						jQuery('<col />')
							.width(w)
							.css('width', (w) + 'px')
							.attr('width', (w) + 'px')
							.appendTo(colgroup);
						
						tableWidth += w;
					});
					o.find('tr').each(function() {
						jQuery(this)
							.height(s.colMargin)
							.css('height', s.colMargin + 'px')
							.attr('height', s.colMargin + 'px');
					});
					colgroup.prependTo(o);
				}
				
				o
					.removeAttr('width')
					.css('width', '')
					.width(tableWidth);
			},
			themeRoller: {
				start: function() {
					//Style sheet			
					s.parent.addClass(jS.cl.uiParent);
					jS.obj.sheet().addClass(jS.cl.uiSheet);
					//Style bars
					jS.obj.barLeft().find('div').addClass(jS.cl.uiBar);
					jS.obj.barTop().find('div').addClass(jS.cl.uiBar);
					jS.obj.barCornerParent().addClass(jS.cl.uiBar);
					
					jS.obj.controls().addClass(jS.cl.uiControl);
					jS.obj.label().addClass(jS.cl.uiControl);
					jS.obj.formula().addClass(jS.cl.uiControlTextBox);
				},
				cell: {
					setActive: function() {
						this.clearActive();
						this.setHighlighted(
							jS.cellLast.td
								.addClass(jS.cl.cellActive)
						);
					},
					setHighlighted: function(td) {
						jQuery(td)
							.addClass(jS.cl.cellHighlighted + ' ' + jS.cl.uiCellHighlighted);
					},
					clearActive: function() {
						jS.obj.cellActive()
							.removeClass(jS.cl.cellActive);
					},
					clearHighlighted: function() {
						jS.obj.cellHighlighted()
							.removeClass(jS.cl.cellHighlighted + ' ' + jS.cl.uiCellHighlighted);
						
						jS.highlightedLast.rowStart = -1;
						jS.highlightedLast.colStart = -1;
						jS.highlightedLast.rowEnd = -1;
						jS.highlightedLast.colEnd = -1;
					}
				},
				bar: {
					style: function(o) {
						jQuery(o).addClass(jS.cl.uiBar);
					},
					setActive: function(direction, i) {
						//We don't clear here because we can have multi active bars
						switch(direction) {
							case 'top': jS.obj.barTop().find('div').eq(i).addClass(jS.cl.uiActive);
								break;
							case 'left': jS.obj.barLeft().find('div').eq(i).addClass(jS.cl.uiActive);
								break;
						}
					},
					clearActive: function() {
						jS.obj.barTop().add(jS.obj.barLeft()).find('div.' + jS.cl.uiActive)
							.removeClass(jS.cl.uiActive);
					}
				},
				tab: {
					setActive: function(o) {
						this.clearActive();
						jS.obj.tab().parent().addClass(jS.cl.uiTabActive);
					},
					clearActive: function () {
						jS.obj.tabContainer().find('span.' + jS.cl.uiTabActive)
							.removeClass(jS.cl.uiTabActive);
					}
				},
				resize: function() {
					// add resizable jquery.ui if available
					if (jQuery.ui) {
						// resizable container div
						var o;
						var barTop;
						var barLeft;
						var controlsHeight;
						var parent = s.parent;
						
						parent.resizable('destroy').resizable({
							minWidth: s.width * 0.5,
							minHeight: s.height * 0.5,
							start: function() {
								jS.obj.ui().hide();
							},
							stop: function() {
								jS.obj.ui().show();
								s.width = parent.width();
								s.height = parent.height();
								jS.sheetSyncSize();
							}
						});
						// resizable formula area - a bit hard to grab the handle but is there!
						var formulaResizeParent = jQuery('<span />');
						jS.obj.formula().wrap(formulaResizeParent).parent().resizable({
							minHeight: jS.obj.formula().height(), 
							maxHeight: 78,
							handles: 's',
							resize: function(e, ui) {
								jS.obj.formula().height(ui.size.height);
								jS.sheetSyncSize();
							}
						});
					}
				}
			},
			manageHtmlToText: function(v) {
				v = jQuery.trim(v);
				if (v.charAt(0) != "=") {
					v = v.replace(/&nbsp;/g, ' ')
						.replace(/&gt;/g, '>')
						.replace(/&lt;/g, '<')
						.replace(/\t/g, '')
						.replace(/\n/g, '')
						.replace(/<br>/g, '\r')
						.replace(/<BR>/g, '\n');

					//jS.log("from html to text");
				}
				return v;
			},
			manageTextToHtml: function(v) {	
				v = jQuery.trim(v);
				if (v.charAt(0) != "=") {
					v = v.replace(/\t/g, '&nbsp;&nbsp;&nbsp;&nbsp;')
						.replace(/ /g, '&nbsp;')
						.replace(/>/g, '&gt;')
						.replace(/</g, '&lt;')
						.replace(/\n/g, '<br>')
						.replace(/\r/g, '<br>');
					
					//jS.log("from text to html");
				}
				return v;
			},
			sheetDecorateRemove: function(makeClone) {
				var o = (makeClone ? jS.obj.sheetAll().clone() : jS.obj.sheetAll());
				
				//Get rid of highlighted cells and active cells
				jQuery(o).find('td.' + jS.cl.cellActive)
					.removeClass(jS.cl.cellActive + ' ' + jS.cl.uiCellActive);
					
				jQuery(o).find('td.' + jS.cl.cellHighlighted)
					.removeClass(jS.cl.cellHighlighted + ' ' + jS.cl.uiCellHighlighted);
				/*
				//IE Bug, match width with css width
				jQuery(o).find('col').each(function(i) {
					var v = jQuery(this).css('width');
					v = ((v + '').match('px') ? v : v + 'px');
					jQuery(o).find('col').eq(i).attr('width', v);
				});
				*/
				return o;
			},
			labelUpdate: function(v, setDirect) {
				if (!setDirect) {
					jS.obj.label().html(cE.columnLabelString(v[1] + 1) + (v[0] + 1));
				} else {
					jS.obj.label().html(v);
				}
			},
			cellEdit: function(td, isDrag) {
				//This finished up the edit of the last cell
				jS.evt.cellEditDone();
				jS.followMe(td);
				var loc = jS.getTdLocation(td);
				
				//Show where we are to the user
				jS.labelUpdate(loc);
				
				var v = td.attr('formula');
				if (!v) {
					v = jS.manageHtmlToText(td.html());
				}
				
				jS.obj.formula()
					.val(v)
					.focus()
					.select();
				jS.cellSetActive(td, loc, isDrag);
			},
			cellSetActive: function(td, loc, isDrag) {
				if (typeof(loc[1]) != 'undefined') {
					jS.cellLast.td = td; //save the current cell/td
					
					jS.cellLast.row = jS.rowLast = loc[0];
					jS.cellLast.col = jS.colLast = loc[1];
					
					jS.themeRoller.bar.clearActive();
					jS.themeRoller.cell.clearHighlighted();
					
					jS.highlightedLast.td = td;
					
					jS.themeRoller.cell.setActive(); //themeroll the cell and bars
					jS.themeRoller.bar.setActive('left', jS.cellLast.row);
					jS.themeRoller.bar.setActive('top', jS.cellLast.col);
					
					var selectModel;
					var clearHighlightedModel;
					
					jS.highlightedLast.rowStart = loc[0];
					jS.highlightedLast.colStart = loc[1];
					jS.highlightedLast.rowLast = loc[0];
					jS.highlightedLast.colLast = loc[1];
					
					switch (s.cellSelectModel) {
						case 'excel':
						case 'gdocs':
							selectModel = function() {};
							clearHighlightedModel = jS.themeRoller.cell.clearHighlighted;
							break;
						case 'oo':
							selectModel = function(target) {
								var td = jQuery(target);
								if (jS.isTd(td)) {
									jS.cellEdit(td);
								}
							};
							clearHighlightedModel = function() {};
							break;
					}
					
					if (isDrag) {
						jS.obj.pane()
							.mousemove(function(e) {
								var endLoc = jS.getTdLocation([e.target]);
								
								if (loc[1] != endLoc[1] || loc[0] != endLoc[0]) { //this prevents this method from firing too much
									//clear highlighted cells if needed
									clearHighlightedModel();
									
									//set current bars
									jS.highlightedLast.colEnd = endLoc[1];
									jS.highlightedLast.rowEnd = endLoc[0];
									
									//select active cell if needed
									selectModel(e.target);
									
									//highlight the cells
									jS.highlightedLast.td = jS.cycleCellsAndMaintainPoint(jS.themeRoller.cell.setHighlighted, loc, endLoc);
								}
							});
						
						jQuery(document)
							.one('mouseup', function() {
	
								jS.obj.pane()
									.unbind('mousemove')
									.unbind('mouseup');
							});
					}
				}
			},
			colLast: 0,
			rowLast: 0,
			cellLast: {
				td: jQuery('<td />'), //this is a dud td, so that we don't get errors
				row: -1,
				col: -1,
				isEdit: false
			},
			highlightedLast: {
				td: jQuery('<td />'),
				rowStart: -1,
				colStart: -1,
				rowEnd: -1,
				colEnd: -1
			},
			cellStyleToggle: function(setClass, removeClass) {
				//Lets check to remove any style classes
				var uiCell = jS.obj.cellHighlighted();
				
				jS.cellUndoable.add(uiCell);
				
				if (removeClass) {
					uiCell.removeClass(removeClass);
				}
				//Now lets add some style
				if (uiCell.hasClass(setClass)) {
					uiCell.removeClass(setClass);
				} else {
					uiCell.addClass(setClass);
				}
				
				jS.cellUndoable.add(uiCell);
				
				jS.obj.formula()
					.focus()
					.select();
				return false;
			},
			context: {},
			calc: function(tableI, fuel) {
				jS.log('Calculation Started');
				if (!s.calcOff) {
					cE.calc(new jS.tableCellProvider(tableI), jS.context, fuel);
					jS.isSheetEdit = false;
				}
				jS.log('Calculation Ended');
			},
			refreshLabelsColumns: function(){
				var w = 0;
				jS.obj.barTop().find('div').each(function(i) {
					jQuery(this).text(cE.columnLabelString(i+1));
					w += jQuery(this).width();
				});
				return w;
			},
			refreshLabelsRows: function(){
				jS.obj.barLeft().find('div').each(function(i) {
					jQuery(this).text((i + 1));
				});
			},
			addSheet: function(size) {
				size = (size ? size : prompt(jS.newSheetDialog));
				if (size) {
					jS.evt.cellEditAbandon();
					jS.setDirty(true);
					var newSheetControl = jS.controlFactory.sheetUI(jS.controlFactory.sheet(size), jS.sheetCount + 1, function() { 
						jS.setActiveSheet(newSheetControl, jS.sheetCount + 1);
					}, true);
				}
			},
			deleteSheet: function() {
				jS.obj.tableControl().remove();
				jS.obj.tabContainer().children().eq(jS.i).remove();
				jS.i = 0;
				jS.sheetCount--;
				
				jS.setControlIds();
				
				jS.setActiveSheet(jS.obj.tableControl(), jS.i);
			},
			deleteRow: function() {
				var v = confirm("Are you sure that you want to delete that row?");
				if (v) {
					jS.obj.barLeft().find('div').eq(jS.rowLast).remove();
					jS.obj.sheet().find('tr').eq(jS.rowLast).remove();
					
					jS.evt.cellEditAbandon();
					
					jS.setTdIds();
					jS.refreshLabelsRows();
					jS.obj.pane().scroll();
					
					jS.rowLast = -1;
					
					jS.offsetFormulaRange(jS.rowLast, 0, -1, 0);
				}		
			},
			deleteColumn: function() {
				var v = confirm("Are you sure that you want to delete that column?");
				if (v) {
					jS.obj.barTop().find('div').eq(jS.colLast).remove();
					jS.obj.sheet().find('colgroup col').eq(jS.colLast).remove();
					jS.obj.sheet().find('tr').each(function(i) {
							jQuery(this).find('td').eq(jS.colLast).remove();
					});
					
					jS.evt.cellEditAbandon();
					
					var w = jS.refreshLabelsColumns();
					jS.setTdIds();
					jS.obj.sheet().width(w);
					jS.obj.pane().scroll();
					
					jS.colLast = -1;
					
					jS.offsetFormulaRange(0, jS.colLast, 0, -1);
				}		
			},
			sheetTab: function(get) {
				var sheetTab = '';
				if (get) {
					sheetTab = jS.obj.sheet().attr('title');
					sheetTab = (sheetTab ? sheetTab : 'Spreadsheet ' + (jS.i + 1));
				} else if (s.editable) { //ensure that the sheet is editable, then let them change the sheet's name
					var newTitle = prompt("What would you like the sheet's title to be?", jS.sheetTab(true));
					if (!newTitle) { //The user didn't set the new tab name
						sheetTab = jS.obj.sheet().attr('title');
						newTitle = (sheetTab ? sheetTab : 'Spreadsheet' + (jS.i + 1));
					} else {
						jS.setDirty(true);
						jS.obj.sheet().attr('title', newTitle);
						jS.obj.tab().html(newTitle);
						
						sheetTab = newTitle;
					}
				}
				return sheetTab;
			},
			print: function(o) {
				var w = window.open();
				w.document.write("<html><body><xmp>" + o + "\n</xmp></body></html>");
				w.document.close();
			},
			viewSource: function(pretty) {
				var sheetClone = jS.sheetDecorateRemove(true);
				
				var s = "";
				if (pretty) {
					jQuery(sheetClone).each(function() {
						s += jS.HTMLtoPrettySource(this);
					});
				} else {
					s += jQuery('<div />').html(sheetClone).html();
				}
				
				jS.print(s);
				
				return false;
			},
			saveSheet: function() {
				var v = jS.sheetDecorateRemove(true);
				var d = jQuery('<div />').html(v).html();

				jQuery.ajax({
					url: s.urlSave,
					type: 'POST',
					data: 's=' + d,
					dataType: 'html',
					success: function(data) {
						jS.setDirty(false);
						alert('Success! - ' + data);
					}
				});
			},
			HTMLtoCompactSource: function(node) {
				var result = "";
				if (node.nodeType == 1) {
					// ELEMENT_NODE
					result += "<" + node.tagName;
					hasClass = false;
					
					var n = node.attributes.length;
					for (var i = 0, hasClass = false; i < n; i++) {
						var key = node.attributes[i].name;
						var val = node.getAttribute(key);
						if (val) {
							if (key == "contentEditable" && val == "inherit") {
								continue;
								// IE hack.
							}
							if (key == "class") {
								hasClass = true;
							}
							
							if (typeof(val) == "string") {
								result += " " + key + '="' + val.replace(/"/g, "'") + '"';
							} else if (key == "style" && val.cssText) {
								result += ' style="' + val.cssText + '"';
							}
						}
					}

					if (node.tagName == "COL") {
						// IE hack, which doesn't like <COL..></COL>.
						result += '/>';
					} else {
						result += ">";
						var childResult = "";
						jQuery(node.childNodes).each(function() {
							childResult += jS.HTMLtoCompactSource(this);
						});
						result += childResult;
						result += "</" + node.tagName + ">";
					}

				} else if (node.nodeType == 3) {
					// TEXT_NODE
					result += node.data.replace(/^\s*(.*)\s*$/g, "$1");
				}
				return result;
			},
			HTMLtoPrettySource: function(node, prefix) {
				if (!prefix) {
					prefix = "";
				}
				var result = "";
				if (node.nodeType == 1) {
					// ELEMENT_NODE
					result += "\n" + prefix + "<" + node.tagName;
					var n = node.attributes.length;
					for (var i = 0; i < n; i++) {
						var key = node.attributes[i].name;
						var val = node.getAttribute(key);
						if (val) {
							if (key == "contentEditable" && val == "inherit") {
								continue; // IE hack.
							}
							if (typeof(val) == "string") {
								result += " " + key + '="' + val.replace(/"/g, "'") + '"';
							} else if (key == "style" && val.cssText) {
								result += ' style="' + val.cssText + '"';
							}
						}
					}
					if (node.childNodes.length <= 0) {
						result += "/>";
					} else {
						result += ">";
						var childResult = "";
						var n = node.childNodes.length;
						for (var i = 0; i < n; i++) {
							childResult += jS.HTMLtoPrettySource(node.childNodes[i], prefix + "  ");
						}
						result += childResult;
						if (childResult.indexOf('\n') >= 0) {
							result += "\n" + prefix;
						}
						result += "</" + node.tagName + ">";
					}
				} else if (node.nodeType == 3) {
					// TEXT_NODE
					result += node.data.replace(/^\s*(.*)\s*$/g, "$1");
				}
				return result;
			},
			followMe: function(td) {
				var pane = jS.obj.pane();
				var panePos = pane.offset();
				var paneWidth = pane.width();
				var paneHeight = pane.height();

				var tdPos = td.offset();
				var tdWidth = td.width();
				var tdHeight = td.height();
				
				var margin = 20;
				
				//jS.log('td: [' + tdPos.left + ', ' + tdPos.top + ']');
				//jS.log('pane: [' + panePos.left + ', ' + panePos.top + ']');
				
				if ((tdPos.left + tdWidth + margin) > (panePos.left + paneWidth)) { //right
					pane.stop().scrollTo(td, {
						axis: 'x',
						duration: 50,
						offset: - ((paneWidth - tdWidth) - margin)
					});
				} else if (tdPos.left < panePos.left) { //left
					pane.stop().scrollTo(td, {
						axis: 'x',
						duration: 50
					});
				}
				
				if ((tdPos.top + tdHeight + margin) > (panePos.top + paneHeight)) { //bottom
					pane.stop().scrollTo(td, {
						axis: 'y',
						duration: 50,
						offset: - ((paneHeight - tdHeight) - margin)
					});
				} else if (tdPos.top < panePos.top) { //top
					pane.stop().scrollTo(td, {
						axis: 'y',
						duration: 50
					});
				}
			},
			count: {
				rows: function() {
					return jS.getBarLeftIndex(jS.obj.barLeft().find('div:last').text());
				},
				columns: function() {
					return jS.getBarTopLocatoin(jS.obj.barTop().find('div:last').text());
				}
			},
			isRowHeightSync: [],
			setActiveSheet: function(o, i) {
				
				
				if (o) {
					o.show().siblings().hide();
					jS.i = i;			
				} else {
					jS.obj.tableControl().siblings().not('div').hide();
					i = 0;
				}
				
				jS.themeRoller.tab.setActive();
				
				if (!jS.isRowHeightSync[i]) { //this makes it only run once, no need to have it run every time a user changes a sheet
					jS.isRowHeightSync[i] = true;
					jS.obj.sheet().find('tr').each(function(j) {
						jS.attrH.setHeight(j, 'cell');
						/*
						fixes a wired bug with height in chrome and ie
						It seems that at some point during the sheet's initializtion the height for each
						row isn't yet clearly defined, this ensures that the heights for barLeft match 
						that of each row in the currently active sheet when a user uses a non strict doc type.
						*/
					});
				}
				
				jS.sheetSyncSize();
				jS.replaceWithSafeImg(jS.obj.sheet().find('img'));
			},
			openSheetURL: function ( url ) {
				s.urlGet = url;
				return jS.openSheet();
			},
			openSheet: function(o) {
				if (!jS.isDirty ? true : confirm("Are you sure you want to open a different sheet?  All unsaved changes will be lost.")) {
					jS.controlFactory.header();
					
					var fnAfter = function(i, l) {
						if (i == (l - 1)) {
							jS.i = 0;
							jS.setActiveSheet();
							jS.themeRoller.resize();
							for (var i = 0; i <= jS.sheetCount; i++) {
								jS.calc(i);
							}
							
							s.fnAfter();
						}
					};
					
					if (!o) {
						jQuery('<div />').load(s.urlGet, function() {
							var sheets = jQuery(this).find('table');
							sheets.each(function(i) {
								jS.controlFactory.sheetUI(jQuery(this), i, function() { 
									fnAfter(i, sheets.length);
								}, true);
							});
						});
					} else {
						var sheets = jQuery('<div />').html(o).find('table');
						sheets.show().each(function(i) {
							jS.controlFactory.sheetUI(jQuery(this), i,  function() { 
								fnAfter(i, sheets.length);
							}, false);
						});
					}
					
					jS.setDirty(false);
					
					return true;
				} else {
					return false;
				}
			},
			newSheetDialog: "What size would you like to make your spreadsheet? Example: '5x10' creates a sheet that is 5 columns by 10 rows.",
			newSheet: function() {
				var size = prompt(jS.newSheetDialog);
				if (size) {
					jS.openSheet(jS.controlFactory.sheet(size));
				}
			},
			importRow: function(rowArray) {
				jS.controlFactory.addRow(null, null, ':last');

				var error = "";
				jS.obj.sheet().find('tr:last td').each(function(i) {
					jQuery(this).removeAttr('formula');
					try {
						//To test this, we need to first make sure it's a string, so converting is done by adding an empty character.
						if ((rowArray[i] + '').charAt(0) == "=") {
							jQuery(this).attr('formula', rowArray[i]);					
						} else {
							jQuery(this).html(rowArray[i]);
						}
					} catch(e) {
						//We want to make sure that is something bad happens, we let the user know
						error += e + ';\n';
					}
				});
				
				if (error) {//Show them the errors
					alert(error);
				}
				//Let's recalculate the sheet just in case
				jS.setTdIds();
				jS.calc(jS.i);
			},
			importColumn: function(columnArray) {
				jS.controlFactory.addColumn();

				var error = "";
				jS.obj.sheet().find('tr').each(function(i) {
					var o = jQuery(this).find('td:last');
					try {
						//To test this, we need to first make sure it's a string, so converting is done by adding an empty character.
						if ((columnArray[i] + '').charAt(0) == "=") {
							o.attr('formula', columnArray[i]);					
						} else {
							o.html(columnArray[i]);
						}
					} catch(e) {
						//We want to make sure that is something bad happens, we let the user know
						error += e + ';\n';
					}
				});
				
				if (error) {//Show them the errors
					alert(error);
				}
				//Let's recalculate the sheet just in case
				jS.setTdIds();
				jS.calc(jS.i);
			},
			exportSheet: {
				xml: function (skipCData) {
					var sheetClone = jS.sheetDecorateRemove(true);			
					var document = "";
					
					var cdata = ['<![CDATA[',']]>'];
					
					if (skipCData) {
						cdata = ['',''];
					}

					jQuery(sheetClone).each(function() {
						var row = '';
						var table = jQuery(this);
						var colCount = 0;
						var col_widths = '';

						table.find('colgroup').children().each(function (i) {
							col_widths += '<c' + i + '>' + (jQuery(this).attr('width') + '').replace('px', '') + '</c' + i + '>';
						});
						
						var trs = table.find('tr');
						var rowCount = trs.length;
						
						trs.each(function(i){
							var col = '';
							
							var tr = jQuery(this);
							var h = tr.attr('height');
							var height = (h ? h : s.colMargin);
							var tds = tr.find('td');
							colCount = tds.length;
							
							tds.each(function(j){
								var td = jQuery(this);
								var colSpan = td.attr('colspan');
								colSpan = (colSpan > 1 ? colSpan : '');
								
								var formula = td.attr('formula');
								var text = (formula ? formula : td.text());
								var cl = td.attr('class');
								var style = td.attr('style');
									
								//Add to current row
								col += '<c' + j +
									(style ? ' style=\"' + style + '\"' : '') + 
									(cl ? ' class=\"' + cl + '\"' : '') + 
									(colSpan ? ' colspan=\"' + colSpan + '\"' : '') +
								'>' + text + '</c' + j + '>';
							});
							
							row += '<r' + i + ' h=\"' + height + '\">' + col + '</r' + i + '>';
						});

						document += '<document title="' + table.attr('title') + '">' +
									'<metadata>' +
										'<columns>' + colCount + '</columns>' +  //length is 1 based, index is 0 based
										'<rows>' + rowCount + '</rows>' +  //length is 1 based, index is 0 based
										'<col_widths>' + col_widths + '</col_widths>' +
									'</metadata>' +
									'<data>' + row + '</data>' +
								'</document>';
					});

					return '<documents>' + document + '</documents>';
				},
				json: function() {
					var sheetClone = jS.sheetDecorateRemove(true);
					var documents = []; //documents
					
					jQuery(sheetClone).each(function() {
						var document = {}; //document
						document['metadata'] = {};
						document['data'] = {};
						
						var table = jQuery(this);
						
						var trs = table.find('tr');
						var rowCount = trs.length;
						var colCount = 0;
						var col_widths = '';
						
						trs.each(function(i) {
							var tr = jQuery(this);
							var tds = tr.find('td');
							colCount = tds.length;
							
							document['data']['r' + i] = {};
							document['data']['r' + i]['h'] = tr.attr('height');
							
							tds.each(function(j) {
								var td = jQuery(this);
								var colSpan = td.attr('colspan');
								colSpan = (colSpan > 1 ? colSpan : null);
								var formula = td.attr('formula');

								document['data']['r' + i]['c' + j] = {
									'value': (formula ? formula : td.text()),
									'style': td.attr('style'),
									'colspan': colSpan,
									'cl': td.attr('class')
								};
							});
						});
						document['metadata'] = {
							'columns': colCount, //length is 1 based, index is 0 based
							'rows': rowCount, //length is 1 based, index is 0 based
							'title': table.attr('title'),
							'col_widths': {}
						};
						
						table.find('colgroup').children().each(function(i) {
							document['metadata']['col_widths']['c' + i] = (jQuery(this).attr('width') + '').replace('px', '');
						});
						
						documents.push(document); //append to documents
					});
					return documents;
				},
				html: function() {
					return jS.sheetDecorateRemove(true);
				}
			},
			sheetSyncSizeToDivs: function() {
				var newSheetWidth = 0;
				jS.obj.barTop().find('div').each(function() {
					newSheetWidth += parseInt(jQuery(this).outerWidth());
				});
				jS.obj.sheet().width(newSheetWidth);
			},
			sheetSyncSizeToCols: function(o) {
				var newSheetWidth = 0;
				o.find('colgroup col').each(function() {
					newSheetWidth += jQuery(this).width();
				});
				o.width(newSheetWidth);
			},
			sheetSyncSize: function() {
				var h = s.height;
				if (!h) {
					h = 400; //Height really needs to be set by the parent
				} else if (h < 200) {
					h = 200;
				}
				s.parent
					.height(h)
					.width(s.width);
					
				var w = s.width - jS.attrH.width(jS.obj.barLeftParent()) - (s.boxModelCorrection);
				
				h = h - jS.attrH.height(jS.obj.controls()) - jS.attrH.height(jS.obj.barTopParent()) - (s.boxModelCorrection * 2);
				
				jS.obj.pane()
					.height(h)
					.width(w)
					.parent()
						.width(w);
				
				jS.obj.ui()
					.width(w + jS.attrH.width(jS.obj.barLeftParent()));
						
				jS.obj.barLeftParent()
					.height(h);
				
				jS.obj.barTopParent()
					.width(w)
					.parent()
						.width(w);
			},
			cellFind: function(v) {
				if(!v) {
					v = prompt("What are you looking for in this spreadsheet?");
				}
				if (v) {//We just do a simple uppercase/lowercase search.
					var o = jS.obj.sheet().find('td:contains("' + v + '")');
					
					if (o.length < 1) {
						o = jS.obj.sheet().find('td:contains("' + v.toLowerCase() + '")');
					}
					
					if (o.length < 1) {
						o = jS.obj.sheet().find('td:contains("' + v.toUpperCase() + '")');
					}
					
					o = o.eq(0);
					if (o.length > 0) {
						jS.cellEdit(o);
					} else {
						alert('No results found.');
					}
				}
			},
			cellSetActiveBar: function(type, start, end) {
				var loc = jS.sheetSize();
				var first = (start < end ? start : end);
				var last = (start < end ? end : start);
				
				var setActive = function(td, rowStart, colStart, rowFollow, colFollow) {
					switch (s.cellSelectModel) {
						case 'oo': //follow cursor behavior
							jS.cellEdit(jQuery(jS.getTd(jS.i, rowFollow, colFollow)));
							break;
						default: //stay at initial cell
							jS.cellEdit(jQuery(jS.getTd(jS.i, rowStart, colStart)));
							break;
					}
					
					setActive = function(td) { //save resources
						return td;
					};
					
					return td;
				};

				var cycleFn;

				var td = [];
				
				switch (type) {
					case 'col':
						cycleFn = function() {
							for (var i = 0; i <= loc[0]; i++) { //rows
								for (var j = first; j <= last; j++) { //cols
									td.push(jS.getTd(jS.i, i, j));
									jS.themeRoller.cell.setHighlighted(setActive(td[td.length - 1], 0, start, 0, end));
								}
							}
						};
						break;
					case 'row':
						cycleFn = function() {
							for (var i = first; i <= last; i++) { //rows
								for (var j = 0; j <= loc[1]; j++) { //cols
									td.push(jS.getTd(jS.i, i, j));
									jS.themeRoller.cell.setHighlighted(setActive(td[td.length - 1], start, 0, end, 0));
								}
							}
						};
						break;
					case 'all':
						cycleFn = function() {
							setActive = function(td) {
								jS.cellEdit(jQuery(td));
								setActive = function() {};
							};
							for (var i = 0; i <= loc[0]; i++) {
								for (var j = 0; j <= loc[1]; j++) {
									td.push(jS.getTd(jS.i, i, j));
									setActive(td[td.length - 1]);
									jS.themeRoller.cell.setHighlighted(td[td.length - 1]);
								}
							}
							first = [0, 0];
							last = loc;
						};
						break;
				}
				
				cycleFn();
				
				jS.highlightedLast.td = td;
				jS.highlightedLast.rowStart = first[0];
				jS.highlightedLast.colStart = first[1];
				jS.highlightedLast.rowEnd = last[0];
				jS.highlightedLast.colEnd = last[1];
			},
			sheetClearActive: function() {
				jS.obj.formula().val('');
				jS.obj.barSelected().removeClass(jS.cl.barSelected);
			},
			getTdRange: function(e, v, newFn, notSetFormula) {
				jS.cellLast.isEdit = true;
				
				var range = function(loc) {
					if (loc.first[1] > loc.last[1] ||
						loc.first[0] > loc.last[0]
					) {
						return {
							first: cE.columnLabelString(loc.last[1] + 1) + (loc.last[0] + 1),
							last: cE.columnLabelString(loc.first[1] + 1) + (loc.first[0] + 1)
						};
					} else {
						return {
							first: cE.columnLabelString(loc.first[1] + 1) + (loc.first[0] + 1),
							last: cE.columnLabelString(loc.last[1] + 1) + (loc.last[0] + 1)
						};
					}
				};
				var label = function(loc) {
					var rangeLabel = range(loc);
					var v2 = v + '';
					v2 = (v2.match(/=/) ? v2 : '=' + v2); //make sure we can use this value as a formula
					
					if (newFn || v2.charAt(v2.length - 1) != '(') { //if a function is being sent, make sure it can be called by wrapping it in ()
						v2 = v2 + (newFn ? newFn : '') + '(';
					}
					
					var formula;
					var lastChar = '';
					if (rangeLabel.first != rangeLabel.last) {
						formula = rangeLabel.first + ':' + rangeLabel.last;
					} else {
						formula = rangeLabel.first;
					}
					
					if (v2.charAt(v2.length - 1) == '(') {
						lastChar = ')';
					}
					
					return v2 + formula + lastChar;
				};
				var newVal = '';
				
				if (e) { //if from an event, we use mousemove method
					var loc = {
						first: jS.getTdLocation([e.target])
					};
					
					var sheet = jS.obj.sheet().mousemove(function(e) {
						loc.last = jS.getTdLocation([e.target]);
						
						newVal = label(loc);
						
						if (!notSetFormula) {
							jS.obj.formula().val(newVal);
							jS.obj.inPlaceEdit().val(newVal);
						}
					});
					
					jQuery(document).one('mouseup', function() {
						sheet.unbind('mousemove');
						return newVal;
					});
				} else {
					var cells = jS.obj.cellHighlighted().not(jS.obj.cellActive());
					
					if (cells.length) {
						var loc = { //tr/td column and row index
							first: jS.getTdLocation(cells.first()),
							last: jS.getTdLocation(cells.last())
						};
						
						newVal = label(loc);
						
						if (!notSetFormula) {
							jS.obj.formula().val(newVal);
							jS.obj.inPlaceEdit().val(newVal);
						}
						
						return newVal;
					} else {
						return '';
					}
				}
			},
			getTdId: function(tableI, row, col) {
				return I + '_table' + tableI + '_cell_c' + col + '_r' + row;
			},
			getTd: function(tableI, row, col) {
				return document.getElementById(jS.getTdId(tableI, row, col));
			},
			getTdLocation: function(td) {
				var col = parseInt(td[0].cellIndex);
				var row = parseInt(td[0].parentNode.rowIndex);
				return [row, col];
				// The row and col are 1-based.
			},
			getBarLeftIndex: function(o) {
				var i = jQuery.trim(jQuery(o).text());
				return parseInt(i) - 1;
			},
			getBarTopIndex: function(o) {
				var i = cE.columnLabelIndex(jQuery.trim(jQuery(o).text()));
				return parseInt(i) - 1;
			},
			tableCellProvider: function(tableI) {
				this.tableBodyId = jS.id.sheet + tableI;
				this.tableI = tableI;
				this.cells = {};
			},
			tableCell: function(tableI, row, col) {
				this.tableBodyId = jS.id.sheet + tableI;
				this.tableI = tableI;
				this.row = row;
				this.col = col;
				this.value = jS.EMPTY_VALUE;
				
				//this.prototype = new cE.cell();
			},
			EMPTY_VALUE: {},
			time: {
				now: new Date(),
				last: new Date(),
				diff: function() {
					return Math.abs(Math.ceil(this.last.getTime() - this.now.getTime()) / 1000).toFixed(5);
				},
				set: function() {
					this.last = this.now;
					this.now = new Date();
				},
				get: function() {
					return this.now.getHours() + ':' + this.now.getMinutes() + ':' + this.now.getSeconds();
				}
			},
			log: function(msg) {  //The log prints: {Current Time}, {Seconds from last log};{msg}
				jS.time.set();
				jS.obj.log().prepend(jS.time.get() + ', ' + jS.time.diff() + '; ' + msg + '<br />\n');
			},
			replaceWithSafeImg: function(o) {  //ensures all pictures will load and keep their respective bar the same size.
				o.each(function() {			
					var src = jQuery(this).attr('src');
					jQuery(this).replaceWith(jS.controlFactory.safeImg(src, jS.getTdLocation(jQuery(this).parent())[0]));
				});
			},
			
			isDirty:  false,
			setDirty: function(dirty) { jS.isDirty = dirty; },
			appendToFormula: function(v, o) {
				var formula = jS.obj.formula();
				
				var fV = formula.val();
				
				if (fV.charAt(0) != '=') {
					fV = '=' + fV;
				}
				
				formula.val(fV + v);
			},
			cellUndoable: {
				undoOrRedo: function(undo) {
					if (!undo && this.i > 0) {
						this.i--;
					} else if (undo && this.i < this.stack.length) {
						this.i++;
					}
					
					this.get().clone().each(function() {
						var o = jQuery(this);
						var id = o.attr('undoable');
						if (id) {
							jQuery('#' + id).replaceWith(
								o
									.removeAttr('undoable')
									.attr('id', id)
							);
						} else {
							jS.log('Not available.');
						}
					});
					
					jS.themeRoller.cell.clearActive();
					jS.themeRoller.bar.clearActive();
					jS.themeRoller.cell.clearHighlighted();
				},
				get: function() { //gets the current cell
					return jQuery(this.stack[this.i]);
				},
				add: function(tds) {
					var oldTds = tds.clone().each(function() {
						var o = jQuery(this);
						var id = o.attr('id');
						o
							.removeAttr('id') //id can only exist in one location, on the sheet, so here we use the id as the attr 'undoable'
							.attr('undoable', id)
							.removeClass(jS.cl.cellHighlighted + ' ' + jS.cl.uiCellHighlighted);
					});
					if (this.stack.length > 0) {
						this.stack.unshift(oldTds);
					} else {
						this.stack = [oldTds];
					}
					this.i = -1;
					if (this.stack.length > 20) { //undoable count, we want to be careful of too much memory consumption
						this.stack.pop(); //drop the last value
					}
				},
				i: 0,
				stack: []
			},
			sheetSize: function() {
				return jS.getTdLocation(jS.obj.sheet().find('td:last'));
			},
			toggleState:  function(newS) {
				if (s.allowToggleState) {
					s.editable = !s.editable;
					jS.obj.tabContainer().remove();
					var sheets = jS.obj.sheetAll().clone();
					origParent.children().remove();
					jS.openSheet(sheets);
				}
			}
		};

		jS.tableCellProvider.prototype = {
			getCell: function(tableI, row, col) {
				if (typeof(col) == "string") {
					col = cE.columnLabelIndex(col);
				}
				var key = tableI + "," + row + "," + col;
				var cell = this.cells[key];
				if (!cell) {
					var td = jS.getTd(tableI, row - 1, col - 1);
					if (td) {
						cell = this.cells[key] = new jS.tableCell(tableI, row, col);
					}
				}
				return cell;
			},
			getNumberOfColumns: function(row) {
				var tableBody = document.getElementById(this.tableBodyId);
				if (tableBody) {
					var tr = tableBody.rows[row];
					if (tr) {
						return tr.cells.length;
					}
				}
				return 0;
			},
			toString: function() {
				result = "";
				jQuery('#' + (this.tableBodyId) + ' tr').each(function() {
					result += this.innerHTML.replace(/\n/g, "") + "\n";
				});
				return result;
			}
		};

		jS.tableCell.prototype = {
			td: null,
			getTd: function() {
				if (!this.td) { //this attempts to check if the td is cached, then cache it if not, then return it
					this.td = document.getElementById(jS.getTdId(this.tableI, this.row - 1, this.col - 1));	
				}
				
				return this.td;
			},
			setValue: function(v, e) {
				this.error = e;
				this.value = v;
				jQuery(this.getTd()).html(v ? v: ''); //I know this is slower than innerHTML = '', but sometimes stability just rules!
			},
			getValue: function() {
				var v = this.value;
				if (v === jS.EMPTY_VALUE && !this.getFormula()) {
					
					v = jQuery(this.getTd()).text(); //again, stability rules!

					v = this.value = (v.length > 0 ? cE.parseFormulaStatic(v) : null);
				}
				
				return (v === jS.EMPTY_VALUE ? null: v);
			},
			getFormat: function() {
				return jQuery(this.getTd()).attr("format");
			},
			setFormat: function(v) {
				jQuery(this.getTd()).attr("format", v);
			},
			getFormulaFunc: function() {
				return this.formulaFunc;
			},
			setFormulaFunc: function(v) {
				this.formulaFunc = v;
			},
			formula: null,
			getFormula: function() {
				if (!this.formula) { //this if statement takes line breaks out of formulas so that they calculate better, then they are cached because the formulas to not change, on the cell 
					var v = jQuery(this.getTd()).attr('formula'); 
					this.formula = (v ? v.replace(/\n/g, ' ') : v);
				}
				
				return this.formula;
			},
			setFormula: function(v) {
				if (v && v.length > 0) {
					jQuery(this.getTd()).attr('formula', v);
				} else {
					jQuery(this.getTd()).removeAttr('formula');
				}
			}
		};

		var cE = { //Calculations Engine
			TEST: {},
			ERROR: "#VALUE!",
			cFN: {//cFN = compiler functions, usually mathmatical
				sum: 	function(x, y) { return x + y; },
				max: 	function(x, y) { return x > y ? x: y; },
				min: 	function(x, y) { return x < y ? x: y; },
				count: 	function(x, y) { return (y != null) ? x + 1: x; },
				clean: function(v) {
					if (typeof(v) == 'string') {
						v = v.replace(cE.regEx.amp, '&')
								.replace(cE.regEx.nbsp, ' ')
								.replace(/\n/g,'')
								.replace(/\r/g,'');
					}
					return v;
				}
			},
			fn: {//fn = standard functions used in cells
				HTML: function(v) {
					return jQuery(v);
				},
				IMG: function(v) {
					return jS.controlFactory.safeImg(v, cE.calcState.row, cE.calcState.col);
				},
				AVERAGE:	function(values) { 
					var arr = cE.foldPrepare(values, arguments);
					return cE.fn.SUM(arr) / cE.fn.COUNT(arr); 
				},
				AVG: 		function(values) { 
					return cE.fn.AVERAGE(values);
				},
				COUNT: 		function(values) { return cE.fold(cE.foldPrepare(values, arguments), cE.cFN.count, 0); },
				SUM: 		function(values) { return cE.fold(cE.foldPrepare(values, arguments), cE.cFN.sum, 0, true); },
				MAX: 		function(values) { return cE.fold(cE.foldPrepare(values, arguments), cE.cFN.max, Number.MIN_VALUE, true); },
				MIN: 		function(values) { return cE.fold(cE.foldPrepare(values, arguments), cE.cFN.min, Number.MAX_VALUE, true); },
				ABS	: 		function(v) { return Math.abs(cE.fn.N(v)); },
				CEILING: 	function(v) { return Math.ceil(cE.fn.N(v)); },
				FLOOR: 		function(v) { return Math.floor(cE.fn.N(v)); },
				INT: 		function(v) { return Math.floor(cE.fn.N(v)); },
				ROUND: 		function(v, decimals) {
					return cE.fn.FIXED(v, (decimals ? decimals : 0), false);
				},
				RAND: 		function(v) { return Math.random(); },
				RND: 		function(v) { return Math.random(); },
				TRUE: 		function() { return 'TRUE'; },
				FALSE: 		function() { return 'FALSE'; },
				NOW: 		function() { return new Date ( ); },
				TODAY: 		function() { return Date( Math.floor( new Date ( ) ) ); },
				DAYSFROM: 	function(year, month, day) { 
					return Math.floor( (new Date() - new Date (year, (month - 1), day)) / 86400000);
				},
				IF:			function(v, t, f){
					t = cE.cFN.clean(t);
					f = cE.cFN.clean(f);
					
					try { v = eval(v); } catch(e) {};
					try { t = eval(t); } catch(e) {};
					try { t = eval(t); } catch(e) {};

					if (v == 'true' || v == true || v > 0 || v == 'TRUE') {
						return t;
					} else {
						return f;
					}
				},
				FIXED: 		function(v, decimals, noCommas) { 
					if (decimals == null) {
						decimals = 2;
					}
					var x = Math.pow(10, decimals);
					var n = String(Math.round(cE.fn.N(v) * x) / x); 
					var p = n.indexOf('.');
					if (p < 0) {
						p = n.length;
						n += '.';
					}
					for (var i = n.length - p - 1; i < decimals; i++) {
						n += '0';
					}
					if (noCommas == true) {// Treats null as false.
						return n;
					}
					var arr	= n.replace('-', '').split('.');
					var result = [];
					var first  = true;
					while (arr[0].length > 0) { // LHS of decimal point.
						if (!first) {
							result.unshift(',');
						}
						result.unshift(arr[0].slice(-3));
						arr[0] = arr[0].slice(0, -3);
						first = false;
					}
					if (decimals > 0) {
						result.push('.');
						var first = true;
						while (arr[1].length > 0) { // RHS of decimal point.
							if (!first) {
								result.push(',');
							}
							result.push(arr[1].slice(0, 3));
							arr[1] = arr[1].slice(3);
							first = false;
						}
					}
					if (v < 0) {
						return '-' + result.join('');
					}
					return result.join('');
				},
				TRIM:		function(v) { 
					if (typeof(v) == 'string') {
						v = jQuery.trim(v);
					}
					return v;
				},
				HYPERLINK: function(link, name) {
					name = (name ? name : 'LINK');
					return jQuery('<a href="' + link + '" target="_new" class="clickable">' + name + '</a>');
				},
				DOLLAR: 	function(v, decimals, symbol) { 
					if (decimals == null) {
						decimals = 2;
					}
					
					if (symbol == null) {
						symbol = '$';
					}
					
					var r = cE.fn.FIXED(v, decimals, false);
					
					if (v >= 0) {
						return symbol + r; 
					} else {
						return '-' + symbol + r.slice(1);
					}
				},
				VALUE: 		function(v) { return parseFloat(v); },
				N: 			function(v) { if (v == null) {return 0;}
								  if (v instanceof Date) {return v.getTime();}
								  if (typeof(v) == 'object') {v = v.toString();}
								  if (typeof(v) == 'string') {v = parseFloat(v.replace(cE.regEx.n, ''));}
								  if (isNaN(v))		   {return 0;}
								  if (typeof(v) == 'number') {return v;}
								  if (v == true)			 {return 1;}
								  return 0; },
				PI: 		function() { return Math.PI; },
				POWER: 		function(x, y) {
					return Math.pow(x, y);
				},
				
				//Note, form objects are experimental, they don't work always as expected
				INPUT: {
					SELECT:	function(v, noBlank) {
						if (s.editable) {
							v = cE.foldPrepare(v, arguments);
							
							var selectObj = jS.controlFactory.input.select();
							
							if (!noBlank) {
								selectObj.append('<option value="">Select a value</option>');
							}
							
							for (var i = 0; i < (v.length <= 50 ? v.length : 50); i++) {
								if (v[i]) {
									selectObj.append('<option value="' + v[i] + '">' + v[i] + '</option>');
								}
							}
							
							selectObj.val(jS.controlFactory.input.getValue(cE.thisCell));
							
							return selectObj;
						} else {
							return jS.controlFactory.input.getValue(cE.thisCell);
						}
						
					},
					SELECTVAL:	function(v) {
						//v = cE.foldPrepare(v, arguments);
						return (s.editable ? jQuery(v).val() : v);
					},
					RADIO: function(v) {
						if (s.editable) {
							v = cE.foldPrepare(v, arguments);
							var o = jS.controlFactory.input.radio(v, cE.thisCell);
							
							o.find('input[value="' + jS.controlFactory.input.getValue(cE.thisCell) + '"]').attr('CHECKED', 'true');
						} else {
							return jS.controlFactory.input.getValue(cE.thisCell);
						}
						return o;
					},
					RADIOVAL: function(v) {
						//v = cE.foldPrepare(v, arguments);
						return (s.editable ? jQuery(v).find('input:checked').val() : v);
					},
					CHECKBOX: function(v) {
						if (s.editable) {
							v = cE.foldPrepare(v, arguments)[0];
							var o = jS.controlFactory.input.checkbox(v, cE.thisCell);
							var checked = jS.controlFactory.input.getValue(cE.thisCell);
							if (checked == 'true' || checked == true) {
								o.attr('CHECKED', 'TRUE');
							} else {
								o.removeAttr('CHECKED');
							}
							return o;
						} else {
							return jS.controlFactory.input.getValue(cE.thisCell);
						}
					},
					CHECKBOXVAL: function(v) {
						return (s.editable ? jQuery(cE.foldPrepare(v, arguments)).val() : v);
					},
					ISCHECKED:		function(v) {
						var checked = jQuery(v).is(":checked");
						if (checked) {
							return 'TRUE';
						} else {
							return 'FALSE';
						}
					}
				},
				CHART: {
					BAR:	function(v, legend, axisLabels, w, h) {
						return jS.controlFactory.chart(null, cE.foldPrepare(v, arguments), legend, axisLabels, w, h, cE.calcState.row - 1);
					},
					BARH:	function(v, legend, axisLabels, w, h) {
						return jS.controlFactory.chart('bhg', cE.foldPrepare(v, arguments), legend, axisLabels, w, h, cE.calcState.row - 1);
					},
					SBAR:	function(v, legend, axisLabels, w, h) {
						return jS.controlFactory.chart('bvs', cE.foldPrepare(v, arguments), legend, axisLabels, w, h, cE.calcState.row - 1);
					},
					SBARH:	function(v, legend, axisLabels, w, h) {
						return jS.controlFactory.chart('bhs', cE.foldPrepare(v, arguments), legend, axisLabels, w, h, cE.calcState.row - 1);
					},
					LINE:	function(v, legend, axisLabels, w, h) {
						return jS.controlFactory.chart('lc', cE.foldPrepare(v, arguments), legend, axisLabels, w, h, cE.calcState.row - 1);
					},
					PIE:	function(v, legend, axisLabels, w, h) {
						return jS.controlFactory.chart('p', cE.foldPrepare(v, arguments), legend, axisLabels, w, h, cE.calcState.row - 1);
					},
					PIETHREED:	function(v, legend, axisLabels, w, h) {
						return jS.controlFactory.chart('p3', cE.foldPrepare(v, arguments), legend, axisLabels, w, h, cE.calcState.row - 1);
					},
					CUSTOM:	function(type, v, legend, axisLabels, w, h) {
						return jS.controlFactory.chart(type, cE.foldPrepare(v, arguments), legend, axisLabels,  w, h, cE.calcState.row - 1);
					}
				}
			},
			calcState: {},
			calc: function(cellProvider, context, startFuel) {
				// Returns null if all done with a complete calc() run.
				// Else, returns a non-null continuation function if we ran out of fuel.  
				// The continuation function can then be later invoked with more fuel value.
				// The fuelStart is either null (which forces a complete calc() to the finish) 
				// or is an integer > 0 to slice up long calc() runs.  A fuelStart number
				// is roughly matches the number of cells to visit per calc() run.
				cE.calcState = { 
					cellProvider:	cellProvider, 
					context: 		(context != null ? context: {}),
					row: 			1, 
					col: 			1,
					i:				cellProvider.tableI,
					done:			false,
					stack:			[],
					calcMore: 		function(moreFuel) {
										cE.calcState.fuel = moreFuel;
										return cE.calcLoop();
									}
				};
				return cE.calcState.calcMore(startFuel);
			},
			cell: function() {
				prototype: {// Cells don't know their coordinates, to make shifting easier.
					getError = 			function()	 { return this.error; },
					getValue = 			function()	 { return this.value; },
					setValue = 			function(v, e) { this.value = v; this.error = e; },
					getFormula	 = 		function()  { return this.formula; },	 // Like "=1+2+3" or "'hello" or "1234.5"
					setFormula	 = 		function(v) { this.formula = v; },
					getFormulaFunc = 	function()  { return this.formulaFunc; },
					setFormulaFunc = 	function(v) { this.formulaFunc = v; },
					toString = 			function() { return "Cell:[" + this.getFormula() + ": " + this.getValue() + ": " + this.getError() + "]"; };
				}
			}, // Prototype setup is later.
			columnLabelIndex: function(str) {
				// Converts A to 1, B to 2, Z to 26, AA to 27.
				var num = 0;
				for (var i = 0; i < str.length; i++) {
					var digit = str.toUpperCase().charCodeAt(i) - 65 + 1;	   // 65 == 'A'.
					num = (num * 26) + digit;
				}
				return num;
			},
			parseLocation: function(locStr) { // With input of "A1", "B4", "F20",
				if (locStr != null &&								  // will return [1,1], [4,2], [20,6].
					locStr.length > 0 &&
					locStr != "&nbsp;") {
					for (var firstNum = 0; firstNum < locStr.length; firstNum++) {
						if (locStr.charCodeAt(firstNum) <= 57) {// 57 == '9'
							break;
						}
					}
					return [ parseInt(locStr.substring(firstNum)),
							 cE.columnLabelIndex(locStr.substring(0, firstNum)) ];
				} else {
					return null;
				}
			},
			columnLabelString: function(index) {
				// The index is 1 based.  Convert 1 to A, 2 to B, 25 to Y, 26 to Z, 27 to AA, 28 to AB.
				// TODO: Got a bug when index > 676.  675==YZ.  676==YZ.  677== AAA, which skips ZA series.
				//	   In the spirit of billg, who needs more than 676 columns anyways?
				var b = (index - 1).toString(26).toUpperCase();   // Radix is 26.
				var c = [];
				for (var i = 0; i < b.length; i++) {
					var x = b.charCodeAt(i);
					if (i <= 0 && b.length > 1) {				   // Leftmost digit is special, where 1 is A.
						x = x - 1;
					}
					if (x <= 57) {								  // x <= '9'.
						c.push(String.fromCharCode(x - 48 + 65)); // x - '0' + 'A'.
					} else {
						c.push(String.fromCharCode(x + 10));
					}
				}
				return c.join("");
			},
			regEx: {
				n: 					/[\$,\s]/g,
				cell: 				/\$?([a-zA-Z]+)\$?([0-9]+)/g, //A1
				range: 				/\$?([a-zA-Z]+)\$?([0-9]+):\$?([a-zA-Z]+)\$?([0-9]+)/g, //A1:B4
				remoteCell:			/\$?(SHEET+)\$?([0-9]+):\$?([a-zA-Z]+)\$?([0-9]+)/g, //SHEET1:A1
				remoteCellRange: 	/\$?(SHEET+)\$?([0-9]+):\$?([a-zA-Z]+)\$?([0-9]+):\$?([a-zA-Z]+)\$?([0-9]+)/g, //SHEET1:A1:B4
				sheet: 				/SHEET/,
				cellInsensitive: 				/\$?([a-zA-Z]+)\$?([0-9]+)/gi, //a1
				rangeInsensitive: 				/\$?([a-zA-Z]+)\$?([0-9]+):\$?([a-zA-Z]+)\$?([0-9]+)/gi, //a1:a4
				remoteCellInsensitive:			/\$?(SHEET+)\$?([0-9]+):\$?([a-zA-Z]+)\$?([0-9]+)/gi, //sheet1:a1
				remoteCellRangeInsensitive: 	/\$?(SHEET+)\$?([0-9]+):\$?([a-zA-Z]+)\$?([0-9]+):\$?([a-zA-Z]+)\$?([0-9]+)/gi, //sheet1:a1:b4
				sheetInsensitive:	/SHEET/i,
				amp: 				/&/g,
				gt: 				/</g,
				lt: 				/>/g,
				nbsp: 				/&nbsp;/g
			},
			str: {
				amp: 	'&amp;',
				lt: 	'&lt;',
				gt: 	'&gt;',
				nbsp: 	'&nbps;'
			},
			parseFormula: function(formula, dependencies, thisTableI) { // Parse formula (without "=" prefix) like "123+SUM(A1:A6)/D5" into JavaScript expression string.
				var nrows = null;
				var ncols = null;
				if (cE.calcState.cellProvider != null) {
					nrows = cE.calcState.cellProvider.nrows;
					ncols = cE.calcState.cellProvider.ncols;
				}
				
				//Cell References Range - Other Tables
				formula = formula.replace(cE.regEx.remoteCellRange, 
					function(ignored, TableStr, tableI, startColStr, startRowStr, endColStr, endRowStr) {
						var res = [];
						var startCol = cE.columnLabelIndex(startColStr);
						var startRow = parseInt(startRowStr);
						var endCol   = cE.columnLabelIndex(endColStr);
						var endRow   = parseInt(endRowStr);
						if (ncols != null) {
							endCol = Math.min(endCol, ncols);
						}
						if (nrows != null) {
							endRow = Math.min(endRow, nrows);
						}
						for (var r = startRow; r <= endRow; r++) {
							for (var c = startCol; c <= endCol; c++) {
								res.push("SHEET" + (tableI) + ":" + cE.columnLabelString(c) + r);
							}
						}
						return "[" + res.join(",") + "]";
					}
				);
				
				//Cell References Fixed - Other Tables
				formula = formula.replace(cE.regEx.remoteCell, 
					function(ignored, tableStr, tableI, colStr, rowStr) {
						tableI = parseInt(tableI) - 1;
						colStr = colStr.toUpperCase();
						if (dependencies != null) {
							dependencies['SHEET' + (tableI) + ':' + colStr + rowStr] = [parseInt(rowStr), cE.columnLabelIndex(colStr), tableI];
						}
						return "(cE.calcState.cellProvider.getCell((" + (tableI) + "),(" + (rowStr) + "),\"" + (colStr) + "\").getValue())";
					}
				);
				
				//Cell References Range
				formula = formula.replace(cE.regEx.range, 
					function(ignored, startColStr, startRowStr, endColStr, endRowStr) {
						var res = [];
						var startCol = cE.columnLabelIndex(startColStr);
						var startRow = parseInt(startRowStr);
						var endCol   = cE.columnLabelIndex(endColStr);
						var endRow   = parseInt(endRowStr);
						if (ncols != null) {
							endCol = Math.min(endCol, ncols);
						}
						if (nrows != null) {
							endRow = Math.min(endRow, nrows);
						}
						for (var r = startRow; r <= endRow; r++) {
							for (var c = startCol; c <= endCol; c++) {
								res.push(cE.columnLabelString(c) + r);
							}
						}
						return "[" + res.join(",") + "]";
					}
				);
				
				//Cell References Fixed
				formula = formula.replace(cE.regEx.cell, 
					function(ignored, colStr, rowStr) {
						colStr = colStr.toUpperCase();
						if (dependencies != null) {
							dependencies['SHEET' + thisTableI + ':' + colStr + rowStr] = [parseInt(rowStr), cE.columnLabelIndex(colStr), thisTableI];
						}
						return "(cE.calcState.cellProvider.getCell((" + thisTableI + "),(" + (rowStr) + "),\"" + (colStr) + "\").getValue())";
					}
				);
				return formula;
			},	
			parseFormulaStatic: function(formula) { // Parse static formula value like "123.0" or "hello" or "'hello world" into JavaScript value.
				if (formula == null) {
					return null;
				} else {
					var formulaNum = formula.replace(cE.regEx.n, '');
					var value = parseFloat(formulaNum);
					if (isNaN(value)) {
						value = parseInt(formulaNum);
					}
					if (isNaN(value)) {
						value = (formula.charAt(0) == "\'" ? formula.substring(1): formula);
					}
					return value;
				}
			},
			calcLoop: function() {
				if (cE.calcState.done == true) {
					return null;
				} else {
					while (cE.calcState.fuel == null || cE.calcState.fuel > 0) {
						if (cE.calcState.stack.length > 0) {
							var workFunc = cE.calcState.stack.pop();
							if (workFunc != null) {
								workFunc(cE.calcState);
							}
						} else if (cE.calcState.cellProvider.formulaCells != null) {
							if (cE.calcState.cellProvider.formulaCells.length > 0) {
								var loc = cE.calcState.cellProvider.formulaCells.shift();
								cE.visitCell(cE.calcState.i, loc[0], loc[1]);
							} else {
								cE.calcState.done = true;
								return null;
							}
						} else {
							if (cE.visitCell(cE.calcState.i, cE.calcState.row, cE.calcState.col) == true) {
								cE.calcState.done = true;
								return null;
							}

							if (cE.calcState.col >= cE.calcState.cellProvider.getNumberOfColumns(cE.calcState.row - 1)) {
								cE.calcState.row++;
								cE.calcState.col =  1;
							} else {
								cE.calcState.col++; // Sweep through columns first.
							}
						}
						
						if (cE.calcState.fuel != null) {
							cE.calcState.fuel -= 1;
						}
					}
					return cE.calcState.calcMore;
				}
			},
			formula: null,
			formulaFunc: null,
			visitCell: function(tableI, r, c) { // Returns true if done with all cells.
				var cell = cE.calcState.cellProvider.getCell(tableI, r, c);
				if (cell == null) {
					return true;
				} else {
					var value = cell.getValue();
					if (value == null) {
						this.formula = cell.getFormula();
						if (this.formula) {
							if (this.formula.charAt(0) == '=') {
								this.formulaFunc = cell.getFormulaFunc();
								if (this.formulaFunc == null ||
									this.formulaFunc.formula != this.formula) {
									this.formulaFunc = null;
									try {
										var dependencies = {};
										var body = cE.parseFormula(this.formula.substring(1), dependencies, tableI);
										this.formulaFunc = function() {
											with (cE.fn) {
												return eval(body);
											}
										};
										
										this.formulaFunc.formula = this.formula;
										this.formulaFunc.dependencies = dependencies;
										cell.setFormulaFunc(this.formulaFunc);
									} catch (e) {
										cell.setValue(cE.ERROR + ': ' + e);
									}
								}
								if (this.formulaFunc) {
									cE.calcState.stack.push(cE.makeFormulaEval(cell, r, c, this.formulaFunc));

									// Push the cell's dependencies, first checking for any cycles. 
									var dependencies = this.formulaFunc.dependencies;
									for (var k in dependencies) {
										if (dependencies[k] instanceof Array &&
											(cE.checkCycles(dependencies[k][0], dependencies[k][1], dependencies[k][2]) == true) //same cell on same sheet
										) {
											cell.setValue(cE.ERROR + ': cycle detected');
											cE.calcState.stack.pop();
											return false;
										}
									}
									for (var k in dependencies) {
										if (dependencies[k] instanceof Array) {
											cE.calcState.stack.push(cE.makeCellVisit(dependencies[k][2], dependencies[k][0], dependencies[k][1]));
										}
									}
								}
							} else {
								cell.setValue(cE.parseFormulaStatic(this.formula));
							}
						}
					}
					return false;
				}
			},
			makeCellVisit: function(tableI, row, col) {
				var fn = function() { 
					return cE.visitCell(tableI, row, col);
				};
				fn.row = row;
				fn.col = col;
				return fn;
			},
			thisCell: null,
			makeFormulaEval: function(cell, row, col, formulaFunc) {
				cE.thisCell = cell;
				var fn = function() {
					var v = "";
					
					try {
						v = formulaFunc();

						switch(typeof(v)) {
							case "string":
								v = v
									.replace(cE.regEx.amp, cE.str.amp)
									.replace(cE.regEx.lt, cE.str.lt)
									.replace(cE.regEx.gt, cE.str.gt)
									.replace(cE.regEx.nbsp, cE.str.nbsp);
						}

						cell.setValue(v);
						
					} catch (e) {
						cE.makeError(cell, e);
					}
				};
				fn.row = row;
				fn.col = col;
				return fn;
			},
			makeError: function(cell, e) {
				var msg = cE.ERROR + ': ' + msg;
				e.message.replace(/\d+\.?\d*, \d+\.?\d*/, function(v, i) {
					try {
						v = v.split(', ');
						msg = ('Cell:' + cE.columnLabelString(parseInt(v[0]) + 1) + (parseInt(v[1])) + ' not found');
					} catch (e) {}
				});
				cell.setValue(msg);
			},
			checkCycles: function(row, col, tableI) {
				for (var i = 0; i < cE.calcState.stack.length; i++) {
					var item = cE.calcState.stack[i];
					if (item.row != null && 
						item.col != null &&
						item.row == row  &&
						item.col == col &&
						tableI == cE.calcState.i
					) {
						return true;
					}
				}
				return false;
			},
			foldPrepare: function(firstArg, theArguments) { // Computes the best array-like arguments for calling fold().
				if (firstArg != null &&
					firstArg instanceof Object &&
					firstArg["length"] != null) {
					return firstArg;
				} else {
					return theArguments;
				}
			},
			fold: function(arr, funcOfTwoArgs, result, castToN) {
				for (var i = 0; i < arr.length; i++) {
					result = funcOfTwoArgs(result, (castToN == true ? cE.fn.N(arr[i]): arr[i]));
				}
				return result;
			}
		};
		
		var $window = jQuery(window);
		
		//initialize this instance of sheet
		jS.s = s;
		
		s.fnBefore();
		
		var o; var emptyFN = function() {};
		if (s.buildSheet) {//override urlGet, this has some effect on how the topbar is sized
			if (typeof(s.buildSheet) == 'object') {
				o = s.buildSheet;
			} else if (s.buildSheet == true || s.buildSheet == 'true') {
				o = jQuery(s.parent.html());
			} else if (s.buildSheet.match(/x/i)) {
				o = jS.controlFactory.sheet(s.buildSheet);
			}
		}
		
		//We need to take the sheet out of the parent in order to get an accurate reading of it's height and width
		//jQuery(this).html(s.loading);
		s.parent.html('');
		
		s.width = s.parent.width();
		s.height = s.parent.height();
		
		
		// Drop functions if they are not needed & save time in recursion
		if (s.log) {
			s.parent.after('<textarea id="' + jS.id.log + '" class="' + jS.cl.log + '" />');
		} else {
			jS.log = emptyFN;
		}
		
		if (!s.showErrors) {
			cE.makeError = emptyFN;
		}
		
		if (!jQuery.support.boxModel) {
			s.boxModelCorrection = 0;
		}
		
		if (!jQuery.scrollTo) {
			jS.followMe = emptyFN;
		}
		
		jS.log('Startup');
		
		$window.resize(function() {
			s.width = s.parent.width();
			s.height = s.parent.height();
			jS.sheetSyncSize();
		});
		
		cE.fn = jQuery.extend(cE.fn, s.calculations);
		
		//this makes cells and functions case insensitive
		if (s.caseInsensitive) {
			cE.regEx.cell = cE.regEx.cellInsensitive;
			cE.regEx.range = cE.regEx.rangeInsensitive;
			cE.regEx.remoteCell = cE.regEx.remoteCellInsensitive;
			cE.regEx.remoteCellRange = cE.regEx.remoteCellRangeInsensitive;
			cE.regEx.sheet = cE.regEx.sheetInsensitive;
			
			//Make sheet functions upper and lower case compatible
			for (var k in cE.fn) {
				var kLower = k.toLowerCase();
				if (kLower != k) {
					cE.fn[kLower] = cE.fn[k];
				}
			}
		}
		
		jS.openSheet(o);
		
		return jS;
	},
	makeTable : {
		xml: function (data) { //Will not accept CDATA tags
			var tables = jQuery('<div />');
		
			jQuery(data).find('document').each(function(i) { //document
				var table = jQuery('<table />');
				var tableWidth = 0;
				var colgroup = jQuery('<colgroup />').appendTo(table);
				var tbody = jQuery('<tbody />');
			
				var metaData = jQuery(this).find('metadata');
				var columnCount = metaData.find('columns').text();
				var rowCount = metaData.find('rows').text();
				var title = jQuery(this).attr('title');
				var data = jQuery(this).find('data');
				var col_widths = metaData.find('col_widths').children();
				
				//go ahead and make the cols for colgroup
				for (var i = 0; i < parseInt(jQuery.trim(columnCount)); i++) {
					var w = parseInt(col_widths.eq(i).text().replace('px', ''));
					w = (w ? w : 120); //if width doesn't exist, grab default
					tableWidth += w;
					colgroup.append('<col width="' + w + 'px" style="width: ' + w + 'px;" />');
				}
				
				table
					.width(tableWidth)
					.attr('title', title);
				
				for (var i = 0; i < rowCount; i++) { //rows
					var tds = data.find('r' + i);
					var height = (data.attr('h') + '').replace('px', '');
					height = parseInt(height);
					
					var thisRow = jQuery('<tr height="' + (height ? height : 18) + 'px" />');
					
					for (var j = 0; j < columnCount; j++) { //cols, they need to be counted because we don't send them all on export
						var newTd = '<td />'; //we give td a default empty td
						var td = tds.find('c' + j);
						
						if (td) {
							var text = td.text() + '';
							var cl = td.attr('class');
							var style = td.attr('style');
							var colSpan = td.attr('colspan');
							
							var formula = '';
							if (text.charAt(0) == '=') {
								formula = ' formula="' + text + '"';
							}
							
							newTd = '<td' + formula + 
								(style ? ' style=\"' + style + '\"' : '') + 
								(cl ? ' class=\"' + cl + '\"' : '') +
								(colSpan ? ' colspan=\"' + colSpan + '\"' : '') +
								(height ? ' height=\"' + height + 'px\"' : '') +
							'>' + text + '</td>';
						}
						thisRow.append(newTd);
					}	
					tbody.append(thisRow);
				}
				table
					.append(tbody)
					.appendTo(tables);
			});
			
			return tables.children();
		},
		json: function(data, makeEval) {
			sheet = (makeEval == true ? eval('(' + data + ')') : data);
			
			var tables = jQuery('<div />');
			
			for (var i = 0; i < sheet.length; i++) {
				var colCount = parseInt(sheet[i].metadata.columns);
				var rowCount = parseInt(sheet[i].metadata.rows);
				title = sheet[i].metadata.title;
				title = (title ? title : "Spreadsheet " + i);
			
				var table = jQuery("<table />");
				var tableWidth = 0;
				var colgroup = jQuery('<colgroup />').appendTo(table);
				var tbody = jQuery('<tbody />');
				
				//go ahead and make the cols for colgroup
				if (sheet[i]['metadata']['col_widths']) {
					for (var x = 0; x < colCount; x++) {
						var w = 120;
						if (sheet[i]['metadata']['col_widths']['c' + x]) {
							var newW = parseInt(sheet[i]['metadata']['col_widths']['c' + x].replace('px', ''));
							w = (newW ? newW : 120); //if width doesn't exist, grab default
							tableWidth += w;
						}
						colgroup.append('<col width="' + w + 'px" style="width: ' + w + 'px;" />');
					}
				}
				
				table
					.attr('title', title)
					.width(tableWidth);
				
				for (var x = 0; x < rowCount; x++) { //tr
					var tr = jQuery('<tr />').appendTo(table);
					tr.attr('height', (sheet[i]['data']['r' + x].h ? sheet[i]['data']['r' + x].h : 18));
					
					for (var y = 0; y < colCount; y++) { //td
						var cell = sheet[i]['data']['r' + x]['c' + y];
						var cur_val;
						var colSpan;
						var style;
						var cl;
						
						if (cell) {
							cur_val = cell.value + '';
							colSpan = cell.colSpan + '';
							style = cell.style + '';
							cl = cell.cl + '';
						}

						var cur_td = jQuery('<td' + 
								(style ? ' style=\"' + style + '\"' : '' ) + 
								(cl ? ' class=\"' + cl + '\"' : '' ) + 
								(colSpan ? ' colspan=\"' + colSpan + '\"' : '' ) + 
							' />');
						try {
							if(typeof(cur_val) == "number") {
								cur_td.html(cur_val);
							} else {
								if (cur_val.charAt(0) == '=') {
									cur_td.attr("formula", cur_val);
								} else {
									cur_td.html(cur_val);
								}
							}
						} catch (e) {}
					
						tr.append(cur_td);

					}
				}
				
				tables.append(table);
			}
			return tables.children();
		},
		fromSize: function(size, h, w) {
			if (!size) {
				size = "5x10";
			}
			size = size.toLowerCase().split('x');

			var columnsCount = parseInt(size[0]);
			var rowsCount = parseInt(size[1]);
			
			//Create elements before loop to make it faster.
			var newSheet = jQuery('<table />');
			var standardTd = '<td></td>';
			var tds = '';
			
			//Using -- is many times faster than ++
			for (var i = columnsCount; i >= 1; i--) {
				tds += standardTd;
			}

			var standardTr = '<tr' + (h ? ' height="' + h + 'px" style="height: ' + h + 'px;"' : '') + '>' + tds + '</tr>';
			var trs = '';
			for (var i = rowsCount; i >= 1; i--) {
				trs += standardTr;
			}
			
			newSheet.html('<tbody>' + trs + '</tbody>');
			
			if (w) {
				newSheet.width(columnsCount * w);
			}
			
			return newSheet;
		}
	},
	killAll: function() {
		if (jQuery.sheet) {
			if (jQuery.sheet.instance) {
				for (var i = 0; i < jQuery.sheet.instance.length; i++) {
					if (jQuery.sheet.instance[i]) {
						if (jQuery.sheet.instance[i].kill) {
							jQuery.sheet.instance[i].kill();
						}
					}
				}
			}
		}
	}
};

var key = {
	BACKSPACE: 			8,
	CAPS_LOCK: 			20,
	COMMA: 				188,
	CONTROL: 			17,
	ALT:				18,
	DELETE: 			46,
	DOWN: 				40,
	END: 				35,
	ENTER: 				13,
	ESCAPE: 			27,
	HOME: 				36,
	INSERT: 			45,
	LEFT: 				37,
	NUMPAD_ADD: 		107,
	NUMPAD_DECIMAL: 	110,
	NUMPAD_DIVIDE: 		111,
	NUMPAD_ENTER: 		108,
	NUMPAD_MULTIPLY: 	106,
	NUMPAD_SUBTRACT: 	109,
	PAGE_DOWN: 			34,
	PAGE_UP: 			33,
	PERIOD: 			190,
	RIGHT: 				39,
	SHIFT: 				16,
	SPACE: 				32,
	TAB: 				9,
	UP: 				38,
	F:					70,
	V:					86,
	Y:					89,
	Z:					90
};