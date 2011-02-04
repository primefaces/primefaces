/*
jQuery.sheet() The Web Based Spreadsheet
$Id: jquery.sheet.js 301 2010-11-09 12:59:13Z RobertLeePlummerJr $
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
		var o;
		settings = jQuery.extend({
			urlGet: 			"sheets/enduser.documentation.html", //local url, if you want to get a sheet from a url
			urlSave: 			"save.html", 					//local url, for use only with the default save for sheet
			editable: 			true, 							//bool, Makes the jSheetControls_formula & jSheetControls_fx appear
			allowToggleState: 	true,							//allows the function that changes the spreadsheet's state from static to editable and back
			urlMenu: 			"menu.html", 					//local url, for the menu to the right of title
			newColumnWidth: 	120, 							//int, the width of new columns or columns that have no width assigned
			title: 				null, 							//html, general title of the sheet group
			inlineMenu:			null, 							//html, menu for editing sheet
			buildSheet: 		false,							//bool, string, or object
																	//bool true - build sheet inside of parent
																	//bool false - use urlGet from local url
																	//string  - '{number_of_cols}x{number_of_rows} (5x100)
																	//object - table
			calcOff: 			false, 							//bool, turns calculationEngine off (no spreadsheet, just grid)
			log: 				false, 							//bool, turns some debugging logs on (jS.log('msg'))
			lockFormulas: 		false, 							//bool, turns the ability to edit any formula off
			parent: 			jQuery(this), 					//object, sheet's parent, DON'T CHANGE
			colMargin: 			18, 							//int, the height and the width of all bar items, and new rows
			fnBefore: 			function() {}, 					//fn, called just before jQuery.sheet loads
			fnAfter: 			function() {},	 				//fn, called just after all sheets load
			fnSave: 			function() {o.sheetInstance.saveSheet();}, //fn, default save function, more of a proof of concept
			fnOpen: 			function() { 					//fn, by default allows you to paste table html into a javascript prompt for you to see what it looks likes if you where to use sheet
									var t = prompt('Paste your table html here');
									if (t) {
										o.sheetInstance.openSheet(t);
									}
			},
			fnClose: 			function() {}, 					//fn, default clase function, more of a proof of concept
			fnAfterCellEdit:	function() {},					//fn, called just after someone edits a cell
			fnSwitchSheet:		function() {},					//fn, called when a spreadsheet is switched inside of an instance of sheet
			fnPaneScroll:		function() {},					//fn, called when a spreadsheet is scrolled
			joinedResizing: 	false, 							//bool, this joins the column/row with the resize bar
			boxModelCorrection: 2, 								//int, attempts to correct the differences found in heights and widths of different browsers, if you mess with this, get ready for the must upsetting and delacate js ever
			showErrors:			true,							//bool, will make cells value an error if spreadsheet function isn't working correctly or is broken
			calculations:		{},								//object, used to extend the standard functions that come with sheet
			cellSelectModel: 	'excel',						//string, 'excel' || 'oo' || 'gdocs' Excel sets the first cell onmousedown active, openoffice sets the last, now you can choose how you want it to be ;)
			autoAddCells:		true,							//bool, when user presses enter on the last row, this will allow them to add another cell, thus improving performance and optimizing modification speed
			caseInsensitive: 	false,							//bool, this makes all the calculations engine user functions case sensitive/insensitive
			resizable: 			true,							//bool, makes the $(obj).sheet(); object resizeable, also adds a resizable formula textarea at top of sheet
			autoFiller: 		false,							//bool, the little guy that hangs out to the bottom right of a selected cell, users can click and drag the value to other cells
			minSize: 			{rows: 15, cols: 5},			//object - {rows: int, cols: int}, Makes the sheet stay at a certain size when loaded in edit mode, to make modification more productive
			forceColWidthsOnStartup:true,						//bool, makes cell widths load from pre-made colgroup/col objects, use this if you plan on making the col items, makes widths more stable on startup
            editableTabs:       true                            //bool, makes tabs editable
        }, settings);

		o = settings.parent;
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
			version: '1.2.0',
			i: 0,
			I: I,
			sheetCount: 0,
			s: {},//s = settings object, used for shorthand, populated from jQuery.sheet
			obj: {//obj = object references
				//Please note, class references use the tag name because it's about 4 times faster
				autoFiller:			function() {return jQuery('#' + jS.id.autoFiller + jS.i);},
				barCorner:			function() {return jQuery('#' + jS.id.barCorner + jS.i);},
				barCornerAll:		function() {return s.parent.find('div.' + jS.cl.barCorner);},
				barCornerParent:	function() {return jQuery('#' + jS.id.barCornerParent + jS.i);},
				barCornerParentAll: function() {return s.parent.find('td.' + jS.cl.barCornerParent);},
				barTop: 			function() {return jQuery('#' + jS.id.barTop + jS.i);},
				barTopAll:			function() {return s.parent.find('div.' + jS.cl.barTop);},
				barTopParent: 		function() {return jQuery('#' + jS.id.barTopParent + jS.i);},
				barTopParentAll:	function() {return s.parent.find('div.' + jS.cl.barTopParent);},
				barLeft: 			function() {return jQuery('#' + jS.id.barLeft + jS.i);},
				barLeftAll:			function() {return s.parent.find('div.' + jS.cl.barLeft);},
				barLeftParent: 		function() {return jQuery('#' + jS.id.barLeftParent + jS.i);},
				barLeftParentAll:	function() {return s.parent.find('div.' + jS.cl.barLeftParent);},
				cellActive:			function() {return jQuery(jS.cellLast.td);},
				cellHighlighted:	function() {return jQuery(jS.highlightedLast.td);},
				chart:				function() {return jQuery('div.' + jS.cl.chart);},
				controls:			function() {return jQuery('#' + jS.id.controls);},
				formula: 			function() {return jQuery('#' + jS.id.formula);},
				fullScreen:			function() {return jQuery('div.' + jS.cl.fullScreen);},
				inlineMenu:			function() {return jQuery('#' + jS.id.inlineMenu);},
				inPlaceEdit:		function() {return jQuery('#' + jS.id.inPlaceEdit);},
				label: 				function() {return jQuery('#' + jS.id.label);},
				log: 				function() {return jQuery('#' + jS.id.log);},
				menu:				function() {return jQuery('#' + jS.id.menu);},
				pane: 				function() {return jQuery('#' + jS.id.pane + jS.i);},
				paneAll:			function() {return s.parent.find('div.' + jS.cl.pane);},
				parent: 			function() {return s.parent;},
				sheet: 				function() {return jQuery('#' + jS.id.sheet + jS.i);},
				sheetAll: 			function() {return s.parent.find('table.' + jS.cl.sheet);},
				tab:				function() {return jQuery('#' + jS.id.tab + jS.i);},
				tabAll:				function() {return this.tabContainer().find('a.' + jS.cl.tab);},
				tabContainer:		function() {return jQuery('#' + jS.id.tabContainer);},
				tableBody: 			function() {return document.getElementById(jS.id.sheet + jS.i);},
				tableControl:		function() {return jQuery('#' + jS.id.tableControl + jS.i);},
				tableControlAll:	function() {return s.parent.find('table.' + jS.cl.tableControl);},
				title:				function() {return jQuery('#' + jS.id.title);},
				ui:					function() {return jQuery('#' + jS.id.ui);},
				uiActive:			function() {return s.parent.find('div.' + jS.cl.uiActive);}
			},
			id: {
				/*
					id = id's references
					Note that these are all dynamically set
				*/
				autoFiller:			'jSheetAutoFiller_' + I + '_',
				barCorner:			'jSheetBarCorner_' + I + '_',
				barCornerParent:	'jSheetBarCornerParent_' + I + '_',
				barTop: 			'jSheetBarTop_' + I + '_',
				barTopParent: 		'jSheetBarTopParent_' + I + '_',
				barLeft: 			'jSheetBarLeft_' + I + '_',
				barLeftParent: 		'jSheetBarLeftParent_' + I + '_',
				controls:			'jSheetControls_' + I,
				formula: 			'jSheetControls_formula_' + I,
				inlineMenu:			'jSheetInlineMenu_' + I,
				inPlaceEdit:		'jSheetInPlaceEdit_' + I,
				label: 				'jSheetControls_loc_' + I,
				log: 				'jSheetLog_' + I,
				menu:				'jSheetMenu_' + I,
				pane: 				'jSheetEditPane_' + I + '_',
				sheet: 				'jSheet_' + I + '_',
				tableControl:		'tableControl_' + I + '_',
				tab:				'jSheetTab_' + I + '_',
				tabContainer:		'jSheetTabContainer_' + I,
				title:				'jSheetTitle_' + I,
				ui:					'jSheetUI_' + I
			},
			cl: {
				/*
					cl = class references
				*/
				autoFiller:				'jSheetAutoFiller',
				autoFillerHandle:		'jSheetAutoFillerHandle',
				autoFillerConver:		'jSheetAutoFillerCover',
				barCorner:				'jSheetBarCorner',
				barCornerParent:		'jSheetBarCornerParent',
				barLeftTd:				'barLeft',
				barLeft: 				'jSheetBarLeft',
				barLeftParent: 			'jSheetBarLeftParent',
				barTop: 				'jSheetBarTop',
				barTopParent: 			'jSheetBarTopParent',
				barTopTd:				'barTop',
				cellActive:				'jSheetCellActive',
				cellHighlighted: 		'jSheetCellHighighted',
				chart:					'jSheetChart',
				controls:				'jSheetControls',
				formula: 				'jSheetControls_formula',
				inlineMenu:				'jSheetInlineMenu',
				fullScreen:				'jSheetFullScreen',
				inPlaceEdit:			'jSheetInPlaceEdit',
				menu:					'jSheetMenu',
				parent:					'jSheetParent',
				sheet: 					'jSheet',
				sheetPaneTd:			'sheetPane',
				label: 					'jSheetControls_loc',
				log: 					'jSheetLog',
				pane: 					'jSheetEditPane',
				tab:					'jSheetTab',
				tabContainer:			'jSheetTabContainer',
				tabContainerFullScreen: 'jSheetFullScreenTabContainer',
				tableControl:			'tableControl',
				title:					'jSheetTitle',
				toggle:					'cellStyleToggle',
				ui:						'jSheetUI',
				uiAutoFiller:			'ui-state-active',
				uiActive:				'ui-state-active',
				uiBar: 					'ui-widget-header',
				uiCellActive:			'ui-state-active',
				uiCellHighlighted: 		'ui-state-highlight',
				uiControl: 				'ui-widget-header ui-corner-top',
				uiControlTextBox:		'ui-widget-content',
				uiFullScreen:			'ui-widget-content ui-corner-all',
				uiInPlaceEdit:			'ui-state-active',
				uiMenu:					'ui-state-highlight',
				uiMenuUl: 				'ui-widget-header',
				uiMenuLi: 				'ui-widget-header',
				uiMenuHighlighted: 		'ui-state-highlight',
				uiPane: 				'ui-widget-content',
				uiParent: 				'ui-widget-content ui-corner-all',
				uiSheet:				'ui-widget-content',
				uiTab:					'ui-widget-header',
				uiTabActive:			'ui-state-highlight'
			},
			msg: { /*msg = messages used throught sheet, for easy access to change them for other languages*/
				addRowMulti: 		"How many rows would you like to add?",
				addColumnMulti: 	"How many columns would you like to add?",
				newSheet: 			"What size would you like to make your spreadsheet? Example: '5x10' creates a sheet that is 5 columns by 10 rows.",
				deleteRow: 			"Are you sure that you want to delete that row?",
				deleteColumn: 		"Are you sure that you want to delete that column?",
				openSheet: 			"Are you sure you want to open a different sheet?  All unsaved changes will be lost.",
				cellFind: 			"No results found.",
				toggleHideRow:		"No row selected.",
				toggleHideColumn: 	"Now column selected.",
				merge:				"Merging is not allowed on the first row.",
				evalError:			"Error, functions as formulas not supported."
			},
			kill: function() { /* For ajax manipulation, kills this instance of sheet entirley */
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
			controlFactory: { /* controlFactory creates the different objects requied by sheet */
				addRowMulti: function(qty, isBefore, skipFormulaReparse) { /* creates multi rows
															qty: int, the number of cells you'd like to add, if not specified, a dialog will ask;
															isBefore: bool, places cells before the selected cell if set to true, otherwise they will go after, or at end
															skipFormulaReparse: bool, re-parses formulas if needed
														*/
					if (!qty) {
						qty = prompt(jS.msg.addRowMulti);
					}
					if (qty) {
						jS.controlFactory.addCells(null, isBefore, null, qty, 'row', skipFormulaReparse);
					}
				},
				addColumnMulti: function(qty, isBefore, skipFormulaReparse) { /* creates multi columns
															qty: int, the number of cells you'd like to add, if not specified, a dialog will ask;
															isBefore: bool, places cells before the selected cell if set to true, otherwise they will go after, or at end
															skipFormulaReparse: bool, re-parses formulas if needed
														*/
					if (!qty) {
						qty = prompt(jS.msg.addColumnMulti);
					}
					if (qty) {
						jS.controlFactory.addCells(null, isBefore, null, qty, 'col', skipFormulaReparse);
					}
				},
				addCells: function(eq, isBefore, eqO, qty, type, skipFormulaReparse) { /*creates cells for sheet and the bars that go along with them
																		eq: int, position where cells should be added;
																		isBefore: bool, places cells before the selected cell if set to true, otherwise they will go after, or at end;
																		eq0: no longer used, kept for legacy;
																		qty: int, how many rows/columsn to add;
																		type: string - "col" || "row", determans the type of cells to add;
																		skipFormulaReparse: bool, re-parses formulas if needed
																*/
					//hide the autoFiller, it can get confused
					if (s.autoFiller) {
						jS.obj.autoFiller().hide();
					}

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

					var o;
					switch (type) {
						case "row":
							o = {
								bar: jS.obj.barLeft().find('div' + eq),
								barParent: jS.obj.barLeft(),
								cells: function() {
									return sheet.find('tr' + eq);
								},
								col: function() {return '';},
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
									if (!cellStart[0]) {
										cellStart = sheet.find('tr:first th' + eq);
									}
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

					if (!skipFormulaReparse && eq != ':last' && !isBefore) {
						//offset formulas
						jS.offsetFormulaRange((isBefore ? loc[0] - qty : loc[0]) , (isBefore ? loc[1] - qty : loc[0]), o.offset[0], o.offset[1], isBefore);
					}

					//Because the line numbers get bigger, it is possible that the bars have changed in size, lets sync them
					jS.sheetSyncSize();

					//Let's make it redoable
					jS.cellUndoable.add(jQuery(sheet).add(o.barParent));
				},
				addRow: function(atRow, isBefore, atRowQ) {/* creates single row
															qty: int, the number of cells you'd like to add, if not specified, a dialog will ask;
															isBefore: bool, places cells before the selected cell if set to true, otherwise they will go after, or at end
														*/
					jS.controlFactory.addCells(atRow, isBefore, atRowQ, 1, 'row');
				},
				addColumn: function(atColumn, isBefore, atColumnQ) {/* creates single column
															qty: int, the number of cells you'd like to add, if not specified, a dialog will ask;
															isBefore: bool, places cells before the selected cell if set to true, otherwise they will go after, or at end
														*/
					jS.controlFactory.addCells(atColumn, isBefore, atColumnQ, 1, 'col');
				},
				barLeft: function(reloadHeights, o) { /* creates all the bars to the left of the spreadsheet
															reloadHeights: bool, reloads all the heights of each bar from the cells of the sheet;
															o: object, the table/spreadsheeet object
													*/
					jS.obj.barLeft().remove();
					var barLeft = jQuery('<div border="1px" id="' + jS.id.barLeft + jS.i + '" class="' + jS.cl.barLeft + '" />');
					var heightFn;
					if (reloadHeights) { //This is our standard way of detecting height when a sheet loads from a url
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
				barTop: function(reloadWidths, o) { /* creates all the bars to the top of the spreadsheet
															reloadWidths: bool, reloads all the widths of each bar from the cells of the sheet;
															o: object, the table/spreadsheeet object
													*/
					jS.obj.barTop().remove();
					var barTop = jQuery('<div id="' + jS.id.barTop + jS.i + '" class="' + jS.cl.barTop + '" />');
					barTop.height(s.colMargin);

					var parents;
					var widthFn;

					if (reloadWidths) {
						parents = o.find('tr:first').find('td,th');
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
				header: function() { /* creates the control/container for everything above the spreadsheet */
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
						firstRowTr.append(jQuery('<td id="' + jS.id.title + '" class="' + jS.cl.title + '" />').html(title));
					}

					if (s.inlineMenu && s.editable) {
						var inlineMenu;
						if (jQuery.isFunction(s.inlineMenu)) {
							inlineMenu = s.inlineMenu(jS);
						} else {
							inlineMenu = s.inlineMenu;
						}
						firstRowTr.append(jQuery('<td id="' + jS.id.inlineMenu + '" class="' + jS.cl.inlineMenu + '" />').html(inlineMenu));
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
									'<td id="' + jS.id.label + '" class="' + jS.cl.label + '"></td>' +
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
				sheetUI: function(o, i, fn, reloadBars) { /* creates the spreadsheet user interface
															o: object, table object to be used as a spreadsheet;
															i: int, the new count for spreadsheets in this instance;
															fn: function, called after the spreadsheet is created and tuned for use;
															reloadBars: bool, if set to true reloads id bars on top and left;
														*/
					if (!i) {
						jS.sheetCount = 0;
						jS.i = 0;
					} else {
						jS.sheetCount = parseInt(i);
						jS.i = jS.sheetCount;
						i = jS.i;
					}

					var objContainer = jS.controlFactory.table().appendTo(jS.obj.ui());
					var pane = jS.obj.pane().html(o);

					if (s.autoFiller && s.editable) {
						pane.append(jS.controlFactory.autoFiller());
					}

					o = jS.tuneTableForSheetUse(o);

					jS.sheetDecorate(o);

					jS.controlFactory.barTop(reloadBars, o);
					jS.controlFactory.barLeft(reloadBars, o);

					jS.sheetTab(true);

					if (s.editable) {
						var formula = jS.obj.formula();
						pane
							.mousedown(function(e) {
								if (jS.isTd(e.target)) {
									jS.evt.cellOnMouseDown(e);
									return false;
								}
							})
							.disableSelection()
							.dblclick(jS.evt.cellOnDblClick);
					}

					jS.themeRoller.start(i);

					jS.setTdIds(o);

					jS.checkMinSize(o);

					jS.evt.scrollBars(pane);

					jS.addTab();

					if (fn) {
						fn(objContainer, pane);
					}

					jS.log('Sheet Initialized');

					return objContainer;
				},
				table: function() { /* creates the table control the will contain all the other controls for this instance */
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
				chartCache: [],
				chart: function(o) { /* creates a chart for use inside of a cell
																piggybacks RaphealJS
										options:
											type
											data
											legend
											title
											x {data, legend}
											y {data, legend}
															*/
					function sanitize(v, toNum) {
						v = arrHelpers.foldPrepare((v ? v : ''), arguments);
						if (toNum) {
							v = arrHelpers.toNumbers(v);
						}
						return v;
					}

					o = jQuery.extend({
						x: {legend: "", data: [0]},
						y: {legend: "", data: [0]},
						title: "",
						data: [0],
						legend: "",
						chart: jQuery('<div class="' + jS.cl.chart + '" />')
					}, o);

					o.data = sanitize(o.data, true);
					o.x.data = sanitize(o.x.data, true);
					o.y.data = sanitize(o.y.data, true);
					o.legend = sanitize(o.legend);
					o.x.legend = sanitize(o.x.legend);
					o.y.legend = sanitize(o.y.legend);

					o.legend = (o.legend ? o.legend : o.data);

					if (Raphael) {
						jQuery(document).one('calculation', function() {
							var width = o.chart.width();
							var height = o.chart.height();
							var r = Raphael(o.chart[0]);
							if (r.g) {
								if (o.title) r.g.text(width / 2, 10, o.title).attr({"font-size": 20});
								switch (o.type) {
								case "bar":
									r.g.barchart(0, 0, width, height, o.data, o.legend)
										.hover(function () {
											this.flag = r.g.popup(
												this.bar.x,
												this.bar.y,
												this.bar.value || "0"
											).insertBefore(this);
										},function () {
											this.flag.animate({
												opacity: 0
												},300,
												function () {
													this.remove();
													}
												);
											});
									break;
								case "hbar":
									r.g.hbarchart(0, 0, width, height, o.data, o.legend)
										.hover(function () {
											this.flag = r.g.popup(this.bar.x, this.bar.y, this.bar.value || "0").insertBefore(this);
										},function () {
											this.flag.animate({
												opacity: 0
												},300,
												function () {
													this.remove();
													}
												);
											});
									break;
								case "line":
									r.g.linechart(width * 0.05, height * 0.03, width * 0.9, height * 0.9, [o.x.data], [o.y.data], {
										nostroke: false,
										axis: "0 0 1 1",
										symbol: "o",
										smooth: true
									})
									.hoverColumn(function () {
										this.tags = r.set();
										for (var i = 0, ii = this.y.length; i < ii; i++) {
											this.tags.push(r.g.tag(this.x, this.y[i], this.values[i], 160, 10).insertBefore(this).attr([{fill: "#fff"}, {fill: this.symbols[i].attr("fill")}]));
										}
									}, function () {
										this.tags && this.tags.remove();
									});

									break;
								case "pie":
									r.g.piechart(width / 2, height / 2, width / 5, o.data, {legend: o.legend})
										.hover(function () {
											this.sector.stop();
											this.sector.scale(1.1, 1.1, this.cx, this.cy);
											if (this.label) {
												this.label[0].stop();
												this.label[0].scale(1.5);
												this.label[1].attr({"font-weight": 800});
											}
										}, function () {
											this.sector.animate({scale: [1, 1, this.cx, this.cy]}, 500, "bounce");
											if (this.label) {
												this.label[0].animate({scale: 1}, 500, "bounce");
												this.label[1].attr({"font-weight": 400});
											}
										});
									break;
								case "dot":
									r.g.dotchart(width / 2, height / 2, width / 5, [o.x.data], [o.y.data], [o.data], {
										symbol: "o",
										max: 10,
										heat: true,
										axis: "0 0 1 1",
										axisxstep: legendX.length - 1,
										axisystep: legendY.length - 1,
										axisxlabels: legendX,
										axisxtype: " ",
										axisytype: " ",
										axisylabels: legendY
									})
										.hover(function () {
											this.tag = this.tag || r.g.tag(this.x, this.y, this.value, 0, this.r + 2).insertBefore(this);
											this.tag.show();
										}, function () {
											this.tag && this.tag.hide();
										});
									break;
								}

								jS.attrH.setHeight(jS.getTdLocation(o.chart.parent())[0], 'cell', false);
							}
						});
					}

					return o.chart;
				},
				safeImg: function(src, row) { /* creates and image and then resizes the cell's row for viewing
												src: string, location of image;
												row: int, the row number where the image is located;
											*/
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
				inPlaceEdit: function(td) { /* creates a teaxtarea for a user to put a value in that floats on top of the current selected cell
												td: object, the cell to be edited
											*/
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

					//Make the textarrea resizable automatically
					if (jQuery.fn.elastic) {
						textarea.elastic();
					}
				},
				input: { /* inputs for use from the calculations engine	*/
					select: function(v, noBlank) {
						var o = jQuery('<select style="width: 100%;" class="clickable" />')
							.change(function() {
								jS.controlFactory.input.setValue(jQuery(this));
							});

						if (!noBlank) {
							o.append('<option value="">Select a value</option>');
						}

						for (var i = 0; i < (v.length <= 50 ? v.length : 50); i++) {
							if (v[i]) {
								o.append('<option value="' + v[i] + '">' + v[i] + '</option>');
							}
						}

						jQuery(document).one('calculation', function() {
							var v = jS.controlFactory.input.getValue(o);
							o.val(v);
						});

						return o;
					},
					radio: function(v) {
						var radio = jQuery('<span class="clickable" />');
						for (var i = 0; i < (v.length <= 25 ? v.length : 25); i++) {
							if (v[i]) {
								radio
									.append(
										jQuery('<input type="radio" name="' + name + '" />')
											.val(v[i])
											.change(function() {
												radio.find('input').removeAttr('CHECKED');
												jQuery(this).attr('CHECKED', true);
												jS.controlFactory.input.setValue(jQuery(this), radio.parent());
											})
									)
									.append('<span class="clickable">' + v[i] + '</span>')
									.append('<br />');
							}
						}

						jQuery(document).one('calculation', function() {
							radio.find('input')
								.attr('name', radio.parent().attr('id') + '_radio');
							var val = jS.controlFactory.input.getValue(radio);
							radio.find('input[value="' + val + '"]')
								.attr('CHECKED', true);
						});

						return radio;
					},
					checkbox: function(v) {
						var o = jQuery('<span class="clickable" />')
							.append(
								jQuery('<input type="checkbox" />')
									.val(v)
									.change(function() {
										o.parent().removeAttr('selectedvalue');
										if (jQuery(this).is(':checked')) {
											jS.controlFactory.input.setValue(jQuery(this), o.parent());
										}
										jS.calc();
									})
							)
							.append('<span>' + v + '</span><br />');

						jQuery(document).one('calculation', function() {
							o.find('input').removeAttr('CHECKED');
							o.find('input[value="' + o.parent().attr('selectedvalue') + '"]').attr('CHECKED', 'TRUE');
						});
						return o;
					},
					setValue: function(o, parent) {
						jQuery(parent ? parent : o.parent()).attr('selectedvalue', o.val());;
						jS.calc();
					},
					getValue: function(o, parent) {
						return jQuery(parent ? parent : o.parent()).attr('selectedvalue');
					}
				},
				autoFiller: function() { /* created the autofiller object */
					return jQuery('<div id="' + (jS.id.autoFiller + jS.i) + '" class="' + jS.cl.autoFiller + ' ' + jS.cl.uiAutoFiller + '">' +
									'<div class="' + jS.cl.autoFillerHandle + '" />' +
									'<div class="' + jS.cl.autoFillerCover + '" />' +
							'</div>')
							.mousedown(function(e) {
								var td = jS.cellLast.td;
								if (td) {
									var loc = jS.getTdLocation(td);
									jS.cellSetActive(td, loc, true, jS.autoFillerNotGroup, function() {
										jS.fillUpOrDown();
										jS.autoFillerGoToTd(jS.obj.cellHighlighted().last());
										jS.autoFillerNotGroup = false;
									});
								}
							});
				}
			},
			autoFillerNotGroup: true,
			sizeSync: { /* future location of all deminsion sync/mods */

			},
			evt: { /* event handlers for sheet; e = event */
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
							var newValCount = 0;

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
										newValCount++;
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

								if (newValCount == 1) {//minimum is 2 for index of 1x1
									jS.fillUpOrDown(false, false, firstValue);
								}

								jS.setDirty(true);
								jS.evt.cellEditDone(true);
							});
						}
						jS.calc();
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

						return jS.evt.cellSetFocusFromXY(left, top);
					},
					formulaOnKeyDown: function(e) {
						switch (e.keyCode) {
							case key.ESCAPE:jS.evt.cellEditAbandon();
								break;
							case key.TAB:return jS.evt.keyDownHandler.tab(e);
								break;
							case key.ENTER:return jS.evt.keyDownHandler.enter(e);
								break;
							case key.LEFT:
							case key.UP:
							case key.RIGHT:
							case key.DOWN:return jS.evt.cellSetFocusFromKeyCode(e);
								break;
							case key.PAGE_UP:return jS.evt.keyDownHandler.pageUpDown(true);
								break;
							case key.PAGE_DOWN:return jS.evt.keyDownHandler.pageUpDown();
								break;
							case key.V:return jS.evt.keyDownHandler.pasteOverCells(e);
								break;
							case key.Y:return jS.evt.keyDownHandler.redo(e);
								break;
							case key.Z:return jS.evt.keyDownHandler.undo(e);
								break;
							case key.F:return jS.evt.keyDownHandler.findCell(e);
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
							case key.END:jS.evt.cellSetFocusFromKeyCode(e);
								break;
							default:jS.cellLast.isEdit = true;
						}
					}
				},
				inPlaceEditOnKeyDown: function(e) {
					switch (e.keyCode) {
						case key.ENTER:return jS.evt.keyDownHandler.enterOnInPlaceEdit(e);
							break;
						case key.TAB:return jS.evt.keyDownHandler.tab(e);
							break;
						case key.ESCAPE:jS.evt.cellEditAbandon();return false;
							break;
					}
				},
				formulaChange: function(e) {
					jS.obj.inPlaceEdit().val(jS.obj.formula().val());
				},
				inPlaceEditChange: function(e) {
					jS.obj.formula().val(jS.obj.inPlaceEdit().val());
				},
				cellEditDone: function(forceCalc) { /* called to edit a cells value from jS.obj.formula(), afterward setting "fnAfterCellEdit" is called w/ params (td, row, col, spreadsheetIndex, sheetIndex)
														forceCalc: bool, if set to true forces a calculation of the selected sheet
													*/
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
											jS.calc();
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
				cellEditAbandon: function(skipCalc) { /* removes focus of a selected cell and doesn't change it's value
															skipCalc: bool, if set to true will skip sheet calculation;
														*/
					jS.obj.inPlaceEdit().remove();
					jS.themeRoller.cell.clearActive();
					jS.themeRoller.bar.clearActive();
					jS.themeRoller.cell.clearHighlighted();

					if (!skipCalc) {
						jS.calc();
					}

					jS.cellLast.td = jQuery('<td />');
					jS.cellLast.row = jS.cellLast.col = -1;
					jS.rowLast = jS.colLast = -1;

					jS.labelUpdate('', true);
					jS.obj.formula()
						.val('');

					if (s.autoFiller) {
						jS.obj.autoFiller().hide();
					}

					return false;
				},
				cellSetFocusFromXY: function(left, top, skipOffset) { /* a handy function the will set a cell active by it's location on the browser;
																		left: int, pixels left;
																		top: int, pixels top;
																		skipOffset: bool, skips offset;
																	*/
					var td = jS.getTdFromXY(left, top, skipOffset);

					if (jS.isTd(td)) {
						jS.themeRoller.cell.clearHighlighted();

						jS.cellEdit(td);
						return false;
					} else {
						return true;
					}
				},
				cellSetFocusFromKeyCode: function(e) { /* invoke a click on next/prev cell */
					var c = jS.cellLast.col; //we don't set the cellLast.col here so that we never go into indexes that don't exist
					var r = jS.cellLast.row;
					var overrideIsEdit = false;

					switch (e.keyCode) {
						case key.UP:r--;break;
						case key.DOWN:r++;break;
						case key.LEFT:c--;break;
						case key.RIGHT:c++;break;
						case key.ENTER:r++;
							overrideIsEdit = true;
							if (s.autoAddCells) {
								if (jS.cellLast.row == jS.sheetSize()[0]) {
									jS.controlFactory.addRow(':last');
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
									jS.controlFactory.addColumn(':last');
								}
							}
							break;
						case key.HOME:c = 0;break;
						case key.END:c = jS.cellLast.td.parent().find('td').length - 1;break;
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
					} else {
						jS.cellEdit(jQuery(e.target), true);
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
						jS.setActiveSheet(i);
						jS.calc(i);
					} else if (i != '-1' && jS.i == i) {
						jS.sheetTab();
					} else {
						jS.addSheet('5x10');
					}

					s.fnSwitchSheet(i);
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

							jS.autoFillerGoToTd();

							jS.log('stop resizing');
						}
					};

					return resizeBar.start(e);
				},
				scrollBars: function(pane) { /* makes the bars scroll as the sheet is scrolled
												pane: object, the sheet's pane;
											*/
					var o = { //cut down on recursion, grab them once
						barLeft: jS.obj.barLeftParent(),
						barTop: jS.obj.barTopParent()
					};

					pane.scroll(function() {
						o.barTop.scrollLeft(pane.scrollLeft());//2 lines of beautiful jQuery js
						o.barLeft.scrollTop(pane.scrollTop());

						s.fnPaneScroll(pane, jS.i);
					});
				},
				barMouseDown: { /* handles bar events, including resizing */
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
			isTd: function(o) { /* ensures the the object selected is actually a td that is in a sheet
									o: object, cell object;
								*/
				o = (o[0] ? o[0] : [o]);
				if (o[0]) {
					if (!isNaN(o[0].cellIndex)) {
						return true;
					}
				}
				return false;
			},
			isFormulaEditable: function(o) { /* ensures that formula attribute of an object is editable
													o: object, td object being used as cell
											*/
				if (s.lockFormulas) {
					if(o.attr('formula') !== undefined) {
						return false;
					}
				}
				return true;
			},
			toggleFullScreen: function() { /* toggles full screen mode */
				if (jS.obj.fullScreen().is(':visible')) { //here we remove full screen
					jQuery('body').removeClass('bodyNoScroll');
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
					jQuery('body').addClass('bodyNoScroll');

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
			tuneTableForSheetUse: function(o) { /* makes table object usable by sheet
													o: object, table object;
												*/
				o
					.addClass(jS.cl.sheet)
					.attr('id', jS.id.sheet + jS.i)
					.attr('border', '1px')
					.attr('cellpadding', '0')
					.attr('cellspacing', '0');

				o.find('td.' + jS.cl.cellActive).removeClass(jS.cl.cellActive);

				return o;
			},
			attrH: {/* Attribute Helpers
						I created this object so I could see, quickly, which attribute was most stable.
						As it turns out, all browsers are different, thus this has evolved to a much uglier beast
					*/
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
							if (!o) {
								var tr = jQuery(jS.getTd(jS.i, i, 0)).parent();
								var td = tr.children();
								o = tr.add(td);
							}
							h = jS.attrH.heightReverse(jS.obj.barLeft().find('div').eq(i), skipCorrection);
							break;
					}

					if (h) {
						jQuery(o)
							.height(h)
							.css('height', h + 'px')
							.attr('height', h + 'px');
					}

					return o;
				}
			},
			setTdIds: function(o) { /* cycles through all the td in a sheet and sets their id so it can be quickly referenced later
										o: object, cell object;
									*/
				o = (o ? o : jS.obj.sheet());
				o.find('tr').each(function(row) {
					jQuery(this).find('td,th').each(function(col) {
						jQuery(this).attr('id', jS.getTdId(jS.i, row, col));
					});
				});
			},
			setControlIds: function() { /* resets the control ids, useful for when adding new sheets/controls between sheets/controls :) */
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
			columnResizer: { /* used for resizing columns */
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
			rowResizer: { /* used for resizing rows */
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
						alert(jS.msg.toggleHideRow);
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
								jQuery(this).find('td,th').eq(i).hide();
							});
							o.hide();
							jS.obj.sheet().find('colgroup col').eq(i).hide();
							jS.toggleHide.columnSizeManage();
						}
					} else {
						alert(jS.msg.toggleHideColumn);
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
			merge: function() { /* merges cells */
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
					jS.calc();
				} else if (!cellFirstLoc[0]) {
					alert(jS.msg.merge);
				}
			},
			unmerge: function() { /* unmerges cells */
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
				jS.calc();
			},
			fillUpOrDown: function(goUp, skipOffsetForumals, v) { /* fills values down or up to highlighted cells from active cell;
																	goUp: bool, default is down, when set to true value are filled from bottom, up;
																	skipOffsetForumals: bool, default is formulas will offest, when set to true formulas will stay static;
																	v: string, the value to set cells to, if not set, formula will be used;
																*/
				var cells = jS.obj.cellHighlighted();
				var cellActive = jS.obj.cellActive();
				//Make it undoable
				jS.cellUndoable.add(cells);

				var startFromActiveCell = cellActive.hasClass(jS.cl.uiCellHighlighted);
				var locFirst = jS.getTdLocation(cells.first());
				var locLast = jS.getTdLocation(cells.last());

				v = (v ? v : jS.obj.formula().val()); //allow value to be overridden

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
				jS.calc();

				//Make it redoable
				jS.cellUndoable.add(cells);
			},
			offsetFormulaRange: function(row, col, rowOffset, colOffset, isBefore) {/* makes cell formulas increment in a range
																						row: int;
																						col: int;
																						rowOffset: int, offsets row increment;
																						colOffset: int, offsets col increment;
																						isBefore: bool, makes increment backward;
																					*/
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

				jS.cycleCells(function (td) {
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

				jS.calc();
			},
			cycleCells: function(fn, firstLoc, lastLoc) { /* cylces through a certain group of cells in a spreadsheet and applies a function to them
															fn: function, the function to apply to a cell;
															firstLoc: array of int - [col, row], the group to start;
															lastLoc: array of int - [col, row], the group to end;
														*/
				for (var i = firstLoc[0]; i < lastLoc[0]; i++) {
					for (var j = firstLoc[1]; j < lastLoc[1]; j++) {
						var td = jS.getTd(jS.i, i, j);
						if (td) {
							fn(jQuery(td));
						}
					}
				}
			},
			cycleCellsAndMaintainPoint: function(fn, firstLoc, lastLoc) { /* cylces through a certain group of cells in a spreadsheet and applies a function to them, firstLoc can be bigger then lastLoc, this is more dynamic
																			fn: function, the function to apply to a cell;
																			firstLoc: array of int - [col, row], the group to start;
																			lastLoc: array of int - [col, row], the group to end;
																		*/
				var o = [];
				for (var i = (firstLoc[0] < lastLoc[0] ? firstLoc[0] : lastLoc[0]) ; i <= (firstLoc[0] > lastLoc[0] ? firstLoc[0] : lastLoc[0]); i++) {
					for (var j = (firstLoc[1] < lastLoc[1] ? firstLoc[1] : lastLoc[1]); j <= (firstLoc[1] > lastLoc[1] ? firstLoc[1] : lastLoc[[1]]); j++) {
						o.push(jS.getTd(jS.i, i, j));
						fn(o[o.length - 1]);
					}
				}
				return o;
			},
			offsetFormula: function(formula, rowOffset, colOffset) { /* makes cell formulas increment
																			formula: string, a cell's formula;
																			rowOffset: int, offsets row increment;
																			colOffset: int, offsets col increment;
																	*/
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
			addTab: function() { /* Adds a tab for navigation to a spreadsheet */
				jQuery('<span class="' + jS.cl.uiTab + ' ui-corner-bottom">' +
						'<a class="' + jS.cl.tab + '" id="' + jS.id.tab + jS.i + '" i="' + jS.i + '">' + jS.sheetTab(true) + '</a>' +
					'</span>')
						.insertBefore(
							jS.obj.tabContainer().find('span:last')
						);
			},
			sheetDecorate: function(o) { /* preps a table for use as a sheet;
											o: object, table object;
										*/
				jS.formatSheet(o);
				jS.sheetSyncSizeToCols(o);
				jS.sheetDecorateRemove();
			},
			formatSheet: function(o) { /* adds tbody, colgroup, heights and widths to different parts of a spreadsheet
											o: object, table object;
										*/
				var tableWidth = 0;
				if (o.find('tbody').length < 1) {
					o.wrapInner('<tbody />');
				}

				if (o.find('colgroup').length < 1 || o.find('col').length < 1) {
					o.remove('colgroup');
					var colgroup = jQuery('<colgroup />');
					o.find('tr:first').find('td,th').each(function() {
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
			checkMinSize: function(o) { /* ensure sheet minimums have been met, if not add columns and rows
											o: object, table object;
										*/
				var loc = jS.sheetSize();

				var addRows = 0;
				var addCols = 0;

				if ((loc[1]) < s.minSize.cols) {
					addCols = s.minSize.cols - loc[1] - 1;
				}

				if (addCols) {
					jS.controlFactory.addColumnMulti(addCols, false, true);
				}

				if ((loc[0]) < s.minSize.rows) {
					addRows = s.minSize.rows - loc[0] - 1;
				}

				if (addRows) {
					jS.controlFactory.addRowMulti(addRows, false, true);
				}
			},
			themeRoller: { /* jQuery ui Themeroller integration	*/
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
							case 'top':jS.obj.barTop().find('div').eq(i).addClass(jS.cl.uiActive);
								break;
							case 'left':jS.obj.barLeft().find('div').eq(i).addClass(jS.cl.uiActive);
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
				resize: function() {// add resizable jquery.ui if available
					// resizable container div
					jS.resizable(s.parent, {
						minWidth: s.width * 0.5,
						minHeight: s.height * 0.5,
						start: function() {
							jS.obj.ui().hide();
						},
						stop: function() {
							jS.obj.ui().show();
							s.width = s.parent.width();
							s.height = s.parent.height();
							jS.sheetSyncSize();
						}
					});
					// resizable formula area - a bit hard to grab the handle but is there!
					var formulaResizeParent = jQuery('<span />');
					jS.resizable(jS.obj.formula().wrap(formulaResizeParent).parent(), {
						minHeight: jS.obj.formula().height(),
						maxHeight: 78,
						handles: 's',
						resize: function(e, ui) {
							jS.obj.formula().height(ui.size.height);
							jS.sheetSyncSize();
						}
					});
				}
			},
			resizable: function(o, settings) { /* jQuery ui resizeable integration
													o: object, any object that neds resizing;
													settings: object, the settings used with jQuery ui resizable;
												*/
				if (jQuery.ui && s.resizable) {
					if (o.attr('resizable')) {
						o.resizable("destroy");
					}

					o
						.resizable(settings)
						.attr('resizable', true);
				}
			},
			manageHtmlToText: function(v) { /* converts html to text for use in textareas
												v: string, value to convert;
											*/
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
			manageTextToHtml: function(v) {	/* converts text to html for use in any object, probably a td/cell
												v: string, value to convert;
											*/
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
			sheetDecorateRemove: function(makeClone) { /* removes sheet decorations
															makesClone: bool, creates a clone rather than the actual object;
														*/
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
			labelUpdate: function(v, setDirect) { /* updates the label so that the user knows where they are currently positioned
													v: string or array of ints, new location value;
													setDirect: bool, converts the array of a1 or [0,0] to "A1";
												*/
				if (!setDirect) {
					jS.obj.label().html(cE.columnLabelString(v[1] + 1) + (v[0] + 1));
				} else {
					jS.obj.label().html(v);
				}
			},
			cellEdit: function(td, isDrag) { /* starts cell to be edited
												td: object, td object;

												isDrag: bool, should be determained by if the user is dragging their mouse around setting cells;
												*/
				jS.autoFillerNotGroup = true; //make autoFiller directional again.
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
			cellSetActive: function(td, loc, isDrag, directional, fnDone) { /* cell cell active to sheet, and highlights it for the user
																				td: object, td object;
																				loc: array of ints - [col, row];
																				isDrag: bool, should be determained by if the user is dragging their mouse around setting cells;
																				directional: bool, makes highlighting directional, only left/right or only up/down;
																				fnDone: function, called after the cells are set active;
																			*/
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
						var lastLoc = loc; //we keep track of the most recent location because we don't want tons of recursion here
						jS.obj.pane()
							.mousemove(function(e) {
								var endLoc = jS.getTdLocation([e.target]);
								var ok = true;

								if (directional) {
									ok = false;
									if (loc[1] == endLoc[1] || loc[0] == endLoc[0]) {
										ok = true;
									}
								}

								if ((lastLoc[1] != endLoc[1] || lastLoc[0] != endLoc[0]) && ok) { //this prevents this method from firing too much
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

								lastLoc = endLoc;
							});

						jQuery(document)
							.one('mouseup', function() {

								jS.obj.pane()
									.unbind('mousemove')
									.unbind('mouseup');

								if (jQuery.isFunction(fnDone)) {
									fnDone();
								}
							});
					}
				}
			},
			colLast: 0, /* the most recent used column */
			rowLast: 0, /* the most recent used row */
			cellLast: { /* the most recent used cell */
				td: jQuery('<td />'), //this is a dud td, so that we don't get errors
				row: -1,
				col: -1,
				isEdit: false
			}, /* the most recent highlighted cells */
			highlightedLast: {
				td: jQuery('<td />'),
				rowStart: -1,
				colStart: -1,
				rowEnd: -1,
				colEnd: -1
			},
			cellStyleToggle: function(setClass, removeClass) { /* sets a cells class for styling
																	setClass: string, class(es) to set cells to;
																	removeClass: string, class(es) to remove from cell if the setClass would conflict with;
																*/
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
			fontReSize: function (direction) { /* resizes fonts in a cell by 1 pixel
													direction: string, "up" || "down"
												*/
				var resize=0;
				switch (direction) {
					case 'up':
						resize=1;
						break;
					case 'down':
						resize=-1;
						break;
				}

				//Lets check to remove any style classes
				var uiCell = jS.obj.cellHighlighted();

				jS.cellUndoable.add(uiCell);

				uiCell.each(function(i) {
					cell = jQuery(this);
					var curr_size = (cell.css("font-size") + '').replace("px","")
					var new_size = parseInt(curr_size ? curr_size : 10) + resize;
					cell.css("font-size", new_size + "px");
				});

				jS.cellUndoable.add(uiCell);
			},
			context: {},
			calc: function(tableI, fuel) { /* harnesses calculations engine's calculation function
												tableI: int, the current table integer;
												fuel: variable holder, used to prevent memory leaks, and for calculations;
											*/
				tableI = (tableI ? tableI : jS.i);
				jS.log('Calculation Started');
				if (!jS.tableCellProviders[tableI]) {
					jS.tableCellProviders[tableI] = new jS.tableCellProvider(tableI);
				}
				if (!s.calcOff) {
					jS.tableCellProviders[tableI].cells = {};
					cE.calc(jS.tableCellProviders[tableI], jS.context, fuel);

					jQuery(document).trigger('calculation');
					jS.isSheetEdit = false;
				}
				jS.log('Calculation Ended');
			},
			refreshLabelsColumns: function(){ /* reset values inside bars for columns */
				var w = 0;
				jS.obj.barTop().find('div').each(function(i) {
					jQuery(this).text(cE.columnLabelString(i+1));
					w += jQuery(this).width();
				});
				return w;
			},
			refreshLabelsRows: function(){ /* resets values inside bars for rows */
				jS.obj.barLeft().find('div').each(function(i) {
					jQuery(this).text((i + 1));
				});
			},
			addSheet: function(size) { /* adds a spreadsheet
											size: string example "10x100" which means 10 columns by 100 rows;
										*/
				size = (size ? size : prompt(jS.msg.newSheet));
				if (size) {
					jS.evt.cellEditAbandon();
					jS.setDirty(true);
					var newSheetControl = jS.controlFactory.sheetUI(jQuery.sheet.makeTable.fromSize(size), jS.sheetCount + 1, function(o) {
						jS.setActiveSheet(jS.sheetCount);
					}, true);
				}
			},
			deleteSheet: function() { /* removes the currently selected sheet */
				jS.obj.tableControl().remove();
				jS.obj.tabContainer().children().eq(jS.i).remove();
				jS.i = 0;
				jS.sheetCount--;

				jS.setControlIds();

				jS.setActiveSheet(jS.i);
			},
			deleteRow: function() { /* removes the currently selected row */
				var v = confirm(jS.msg.deleteRow);
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
			deleteColumn: function() { /* removes the currently selected column */
				var v = confirm(jS.msg.deleteColumn);
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
			sheetTab: function(get) { /* manages a tabs inner value
											get: bool, makes return the current value of the tab;
										*/
				var sheetTab = '';
				if (get) {
					sheetTab = jS.obj.sheet().attr('title');
					sheetTab = (sheetTab ? sheetTab : 'Spreadsheet ' + (jS.i + 1));
				} else if (s.editable && s.editableTabs) { //ensure that the sheet is editable, then let them change the sheet's name
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
			print: function(o) { /* prints a value in a new window
									o: string, any string;
								*/
				var w = window.open();
				w.document.write("<html><body><xmp>" + o + "\n</xmp></body></html>");
				w.document.close();
			},
			viewSource: function(pretty) { /* prints the source of a sheet for a user to see
												pretty: bool, makes html a bit easier for the user to see;
											*/
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
			saveSheet: function() { /* saves the sheet */
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
			HTMLtoCompactSource: function(node) { /* prints html to 1 line
													node: object;
												*/
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
			HTMLtoPrettySource: function(node, prefix) {/* prints html to manu lines, formatted for easy viewing
															node: object;
															prefix: string;
														*/
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
			followMe: function(td) { /* scrolls the sheet to the selected cell
										td: object, td object;
									*/
				td = (td ? td : jQuery(jS.cellLast.td));
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

				jS.autoFillerGoToTd(td, tdHeight, tdWidth);
			},
			autoFillerGoToTd: function(td, tdHeight, tdWidth) { /* moves autoFiller to a selected cell
																	td: object, td object;
																	tdHeight: height of a td object;
																	tdWidth: width of a td object;
																*/
				td = (td ? td : jQuery(jS.cellLast.td));
				tdHeight = (tdHeight ? tdHeight : td.height());
				tdWidth = (tdWidth ? tdWidth : td.width());

				if (s.autoFiller) {
					if (td.attr('id')) { //ensure that it is a usable cell
						tdPos = td.position();
						jS.obj.autoFiller()
							.show('slow')
							.css('top', ((tdPos.top + (tdHeight ? tdHeight : td.height()) - 3) + 'px'))
							.css('left', ((tdPos.left + (tdWidth ? tdWidth : td.width()) - 3) + 'px'));
					}
				}
			},
			isRowHeightSync: [],
			setActiveSheet: function(i) { /* sets active a spreadsheet inside of a sheet instance
											i: int, a sheet integer desired to show;
											*/
				i = (i ? i : 0);

				jS.obj.tableControlAll().hide().eq(i).show();
				jS.i = i;

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
				//jS.replaceWithSafeImg();
			},
			openSheetURL: function ( url ) { /* opens a table object from a url, then opens it
												url: string, location;
											*/
				s.urlGet = url;
				return jS.openSheet();
			},
			openSheet: function(o, reloadBarsOverride) { /* opens a spreadsheet into the active sheet instance \
															o: object, a table object;
															reloadBarsOverride: if set to true, foces bars on left and top not be reloaded;
														*/
				if (!jS.isDirty ? true : confirm(jS.msg.openSheet)) {
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
						var sheets = jQuery('<div />').html(o).children('table');
						sheets.show().each(function(i) {
							jS.controlFactory.sheetUI(jQuery(this), i,  function() {
								fnAfter(i, sheets.length);
							}, (reloadBarsOverride ? true : false));
						});
					}

					jS.setDirty(false);

					return true;
				} else {
					return false;
				}
			},
			newSheet: function() { /* creates a new shet from size */
				var size = prompt(jS.msg.newSheet);
				if (size) {
					jS.openSheet(jQuery.sheet.makeTable.fromSize(size));
				}
			},
			importRow: function(rowArray) { /* creates a new row and then applies an array's values to each of it's new values
												rowArray: array;
											*/
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
				jS.calc();
			},
			importColumn: function(columnArray) { /* creates a new column and then applies an array's values to each of it's new values
													columnArray: array;
												*/
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
				jS.calc();
			},
			exportSheet: { /* exports sheets into xml, json, or html formats */
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
			sheetSyncSizeToDivs: function() { /* syncs a sheet's size from bars/divs */
				var newSheetWidth = 0;
				jS.obj.barTop().find('div').each(function() {
					newSheetWidth += parseInt(jQuery(this).outerWidth());
				});
				jS.obj.sheet().width(newSheetWidth);
			},
			sheetSyncSizeToCols: function(o) { /* syncs a sheet's size from it's col objects
													o: object, sheet object;
												*/
				var newSheetWidth = 0;
				o = (o ? o : jS.obj.sheet());
				o.find('colgroup col').each(function() {
					newSheetWidth += jQuery(this).width();
				});
				o.width(newSheetWidth);
			},
			sheetSyncSize: function() { /* syncs a sheet's size to that of the jQuery().sheet() caller object */
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
			cellChangeStyle: function(style, value) { /* changes a cell's style and makes it undoable/redoable
														style: string, css style name;
														value: string, css setting;
													*/
				jS.cellUndoable.add(jS.obj.cellHighlighted()); //save state, make it undoable
				jS.obj.cellHighlighted().css(style, value);
				jS.cellUndoable.add(jS.obj.cellHighlighted()); //save state, make it redoable

			},
			cellFind: function(v) { /* finds a cell in a sheet from a value
										v: string, value in a cell to find;
									*/
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
						alert(jS.msg.cellFind);
					}
				}
			},
			cellSetActiveBar: function(type, start, end) { /* sets a bar active
																type: string, "col" || "row" || "all";
																start: int, int to start highlighting from;
																start: int, int to end highlighting to;
															*/
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
			sheetClearActive: function() { /* clears formula and bars from being highlighted */
				jS.obj.formula().val('');
				jS.obj.barSelected().removeClass(jS.cl.barSelected);
			},
			getTdRange: function(e, v, newFn, notSetFormula) { /* gets a range of selected cells, then returns it */
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
			getTdId: function(tableI, row, col) { /* makes a td if from values given
													tableI: int, table integer;
													row: int, row integer;
													col: int, col integer;
												*/
				return I + '_table' + tableI + '_cell_c' + col + '_r' + row;
			},
			getTd: function(tableI, row, col) { /* gets a td
													tableI: int, table integer;
													row: int, row integer;
													col: int, col integer;
												*/
				return document.getElementById(jS.getTdId(tableI, row, col));
			},
			getTdLocation: function(td) { /* gets td column and row int
												td: object, td object;
											*/
				var col = parseInt(td[0].cellIndex);
				var row = parseInt(td[0].parentNode.rowIndex);
				return [row, col];
				// The row and col are 1-based.
			},
			getTdFromXY: function(left, top, skipOffset) { /* gets cell from point
																left: int, pixels left;
																top: int, pixels top;
																skipOffset: bool, skips pane offset;
															*/
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
						return td;
					}
					return false;
				}
			},
			getBarLeftIndex: function(o) { /* get's index from object */
				var i = jQuery.trim(jQuery(o).text());
				return parseInt(i) - 1;
			},
			getBarTopIndex: function(o) { /* get's index from object */
				var i = cE.columnLabelIndex(jQuery.trim(jQuery(o).text()));
				return parseInt(i) - 1;
			},
			tableCellProvider: function(tableI) { /* provider for calculations engine */
				this.tableBodyId = jS.id.sheet + tableI;
				this.tableI = tableI;
				this.cells = {};
			},
			tableCellProviders: [],
			tableCell: function(tableI, row, col) { /* provider for calculations engine */
				this.tableBodyId = jS.id.sheet + tableI;
				this.tableI = tableI;
				this.row = row;
				this.col = col;
				this.value = jS.EMPTY_VALUE;

				//this.prototype = new cE.cell();
			},
			EMPTY_VALUE: {},
			time: { /* time loggin used with jS.log, useful for finding out if new methods are faster */
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
				(o ? o : jS.obj.sheet().find('img')).each(function() {
					var src = jQuery(this).attr('src');
					jQuery(this).replaceWith(jS.controlFactory.safeImg(src, jS.getTdLocation(jQuery(this).parent())[0]));
				});
			},

			isDirty:  false,
			setDirty: function(dirty) {jS.isDirty = dirty;},
			appendToFormula: function(v, o) {
				var formula = jS.obj.formula();

				var fV = formula.val();

				if (fV.charAt(0) != '=') {
					fV = '=' + fV;
				}

				formula.val(fV + v);
			},
			cellUndoable: { /* makes cell editing undoable and redoable */
				undoOrRedo: function(undo) {
					//hide the autoFiller, it can get confused
					if (s.autoFiller) {
						jS.obj.autoFiller().hide();
					}

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
			toggleState:  function(replacementSheets) {
				if (s.allowToggleState) {
					if (s.editable) {
						jS.evt.cellEditAbandon();
						jS.saveSheet();
					}
					jS.setDirty(false);
					s.editable = !s.editable;
					jS.obj.tabContainer().remove();
					var sheets = (replacementSheets ? replacementSheets : jS.obj.sheetAll().clone());
					origParent.children().remove();
					jS.openSheet(sheets, true);
				}
			},
			setCellRef: function(ref) {
				var td = jS.obj.cellActive();
				var cellRef = td.attr('cellRef');
				td.removeClass(cellRef);

				cellRef = (ref ? ref : prompt('Enter the name you would like to reference the cell by.'));

				if (cellRef) {
					td
						.addClass(cellRef)
						.attr('cellRef', cellRef);
				}

				jS.calc();
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
				sum: 	function(x, y) {return x + y;},
				max: 	function(x, y) {return x > y ? x: y;},
				min: 	function(x, y) {return x < y ? x: y;},
				count: 	function(x, y) {return (y != null) ? x + 1: x;},
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
					var arr =arrHelpers.foldPrepare(values, arguments);
					return cE.fn.SUM(arr) / cE.fn.COUNT(arr);
				},
				AVG: 		function(values) {
					return cE.fn.AVERAGE(values);
				},
				COUNT: 		function(values) {return arrHelpers.fold(arrHelpers.foldPrepare(values, arguments), cE.cFN.count, 0);},
				COUNTA:		function(v) {
					var values =arrHelpers.foldPrepare(v, arguments);
					var count = 0;
					for (var i = 0; i < values.length; i++) {
						if (values[i]) {
							count++;
						}
					}
					return count;
				},
				SUM: 		function(values) {return arrHelpers.fold(arrHelpers.foldPrepare(values, arguments), cE.cFN.sum, 0, true, cE.fn.N);},
				MAX: 		function(values) {return arrHelpers.fold(arrHelpers.foldPrepare(values, arguments), cE.cFN.max, Number.MIN_VALUE, true, cE.fn.N);},
				MIN: 		function(values) {return arrHelpers.fold(arrHelpers.foldPrepare(values, arguments), cE.cFN.min, Number.MAX_VALUE, true, cE.fn.N);},
				MEAN:		function(values) {return this.SUM(values) / values.length;},
				ABS	: 		function(v) {return Math.abs(cE.fn.N(v));},
				CEILING: 	function(v) {return Math.ceil(cE.fn.N(v));},
				FLOOR: 		function(v) {return Math.floor(cE.fn.N(v));},
				INT: 		function(v) {return Math.floor(cE.fn.N(v));},
				ROUND: 		function(v, decimals) {
					return cE.fn.FIXED(v, (decimals ? decimals : 0), false);
				},
				RAND: 		function(v) {return Math.random();},
				RND: 		function(v) {return Math.random();},
				TRUE: 		function() {return 'TRUE';},
				FALSE: 		function() {return 'FALSE';},
				NOW: 		function() {return new Date ( );},
				TODAY: 		function() {return Date( Math.floor( new Date ( ) ) );},
				DAYSFROM: 	function(year, month, day) {
					return Math.floor( (new Date() - new Date (year, (month - 1), day)) / 86400000);
				},
				DAYS: function(v1, v2) {
					var date1 = new Date(v1);
					var date2 = new Date(v2);
					var ONE_DAY = 1000 * 60 * 60 * 24;
					return Math.round(Math.abs(date1.getTime() - date2.getTime()) / ONE_DAY);
				},
				DATEVALUE: function(v) {
					var d = new Date(v);
					return d.getDate() + '/' + (d.getMonth() + 1) + '/' + d.getFullYear();
				},
				IF:			function(v, t, f){
					t = cE.cFN.clean(t);
					f = cE.cFN.clean(f);

					try {v = eval(v);} catch(e) {};
					try {t = eval(t);} catch(e) {};
					try {t = eval(t);} catch(e) {};

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
				VALUE: 		function(v) {return parseFloat(v);},
				N: 			function(v) {if (v == null) {return 0;}
								  if (v instanceof Date) {return v.getTime();}
								  if (typeof(v) == 'object') {v = v.toString();}
								  if (typeof(v) == 'string') {v = parseFloat(v.replace(cE.regEx.n, ''));}
								  if (isNaN(v))		   {return 0;}
								  if (typeof(v) == 'number') {return v;}
								  if (v == true)			 {return 1;}
								  return 0;},
				PI: 		function() {return Math.PI;},
				POWER: 		function(x, y) {
					return Math.pow(x, y);
				},
				SQRT: function(v) {
					return Math.sqrt(v);
				},
				//Note, form objects are experimental, they don't work always as expected
				INPUT: {
					SELECT:	function(v, noBlank) {
						if (s.editable) {
							v = arrHelpers.foldPrepare(v, arguments, true);
							return jS.controlFactory.input.select(v, noBlank);
						} else {
							return jS.controlFactory.input.getValue(v);
						}
					},
					RADIO: function(v) {
						if (s.editable) {
							v = arrHelpers.foldPrepare(v, arguments, true);
							return jS.controlFactory.input.radio(v);
						} else {
							return jS.controlFactory.input.getValue(v);
						}
					},
					CHECKBOX: function(v) {
						if (s.editable) {
							v = arrHelpers.foldPrepare(v, arguments)[0];
							return jS.controlFactory.input.checkbox(v);
						} else {
							return jS.controlFactory.input.getValue(v);
						}
					},
					VAL: function(v) {
						return jS.controlFactory.input.getValue(v);
					},
					SELECTVAL:	function(v) {
						return jS.controlFactory.input.getValue(v);
					},
					RADIOVAL: function(v) {
						return jS.controlFactory.input.getValue(v);
					},
					CHECKBOXVAL: function(v) {
						return jS.controlFactory.input.getValue(v);
					},
					ISCHECKED:		function(v) {
						var val = jS.controlFactory.input.getValue(v);
						var length = jQuery(v).find('input[value="' + val + '"]').length;
						if (length) {
							return 'TRUE';
						} else {
							return 'FALSE';
						}
					}
				},
				CHART: {
					BAR:	function(values, legend, title) {
						return jS.controlFactory.chart({
							type: 'bar',
							data: values,
							legend: legend,
							title: title
						});
					},
					HBAR:	function(values, legend, title) {
						return jS.controlFactory.chart({
							type: 'hbar',
							data: values,
							legend: legend,
							title: title
						});
					},
					LINE:	function(valuesX, valuesY, legendX, legendY, title) {
						return jS.controlFactory.chart({
							type: 'line',
							x: {
								data: valuesX,
								legend: legendX
							},
							y: {
								data: valuesY,
								legend: legendY
							},
							title: title
						});
					},
					PIE:	function(values, legend, title) {
						return jS.controlFactory.chart({
							type: 'pie',
							data: values,
							legend: legend,
							title: title
						});
					},
					DOT:	function(valuesX, valuesY, values,legendX, legendY, title) {
						return jS.controlFactory.chart({
							type: 'dot',
							values: values,
							x: {
								data: valuesX,
								legend: legendX
							},
							y: {
								data: valuesY,
								legend: legendY
							},
							title: title
						});
					}
				},
				CELLREF: function(v, i) {
					var td;
					if (i) {
						td = jS.obj.sheetAll().eq(i).find('td.' + v);
					} else {
						td = jS.obj.sheet().find('td.' + v);
					}

					return td.html();
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
					context: 		(context != null ? context : {}),
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
											if (!body.match(/function/gi)) {
												with (cE.fn) {
													return eval(body);
												}
											} else {
												return jS.msg.evalError;
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
			cell: function() {
				prototype: {// Cells don't know their coordinates, to make shifting easier.
					getError = 			function()	 {return this.error;},
					getValue = 			function()	 {return this.value;},
					setValue = 			function(v, e) {this.value = v;this.error = e;},
					getFormula	 = 		function()  {return this.formula;},	 // Like "=1+2+3" or "'hello" or "1234.5"
					setFormula	 = 		function(v) {this.formula = v;},
					getFormulaFunc = 	function()  {return this.formulaFunc;},
					setFormulaFunc = 	function(v) {this.formulaFunc = v;},
					toString = 			function() {return "Cell:[" + this.getFormula() + ": " + this.getValue() + ": " + this.getError() + "]";};
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
			formula: null,
			formulaFunc: null,
			thisCell: null,
			makeFormulaEval: function(cell, row, col, formulaFunc) {
				cE.thisCell = cell;
				var fn = function() {
					var v = "";

					try {
						v = formulaFunc();
						/*
						switch(typeof(v)) {
							case "string":
								v = v
									.replace(cE.regEx.amp, cE.str.amp)
									.replace(cE.regEx.lt, cE.str.lt)
									.replace(cE.regEx.gt, cE.str.gt)
									.replace(cE.regEx.nbsp, cE.str.nbsp);
						}
						*/
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
			}
		};

		var $window = jQuery(window);

		//initialize this instance of sheet
		jS.s = s;

		s.fnBefore();

		var o;var emptyFN = function() {};
		if (s.buildSheet) {//override urlGet, this has some effect on how the topbar is sized
			if (typeof(s.buildSheet) == 'object') {
				o = s.buildSheet;
			} else if (s.buildSheet == true || s.buildSheet == 'true') {
				o = jQuery(s.parent.html());
			} else if (s.buildSheet.match(/x/i)) {
				o = jQuery.sheet.makeTable.fromSize(s.buildSheet);
			}
		}

		//We need to take the sheet out of the parent in order to get an accurate reading of it's height and width
		//jQuery(this).html(s.loading);
		s.parent
			.html('')
			.addClass(jS.cl.parent);

		//Use the setting height/width if they are there, otherwise use parent's
		s.width = (s.width ? s.width : s.parent.width());
		s.height = (s.height ? s.height : s.parent.height());


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

		$window
		.resize(function() {
			if (jS) { //We check because jS might have been killed
				s.width = s.parent.width();
				s.height = s.parent.height();
				jS.sheetSyncSize();
			}
		});

		//Extend the calculation engine plugins
		cE.fn = jQuery.extend(cE.fn, s.calculations);

		//Extend the calculation engine with advanced functions
		if (jQuery.sheet.advancedfn) {
			cE.fn = jQuery.extend(cE.fn, jQuery.sheet.advancedfn);
		}

		//Extend the calculation engine with finance functions
		if (jQuery.sheet.financefn) {
			cE.fn = jQuery.extend(cE.fn, jQuery.sheet.financefn);
		}

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

		jS.openSheet(o, s.forceColWidthsOnStartup);

		return jS;
	},
	makeTable : {
		xml: function (data) { /* creates a table from xml, note: will not accept CDATA tags
								data: object, xml object;
								*/
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
		json: function(data, makeEval) { /* creates a sheet from json data, for format see top
											data: json;
											makeEval: bool, if true evals json;
										*/
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
		fromSize: function(size, h, w) { /* creates a spreadsheet object from a size given
											size: string, example "10x100" which means 10 columns by 100 rows;
											h: int, height for each new row;
											w: int, width of each new column;
										*/
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
	killAll: function() { /* removes all sheets */
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
	},
	paneScrollLocker: function(obj, I) { //This can be used with setting fnPaneScroll to lock all loaded sheets together when scrolling, useful in history viewing
		jQuery(jQuery.sheet.instance).each(function(i) {
			this.obj.pane()
				.scrollLeft(obj.scrollLeft())
				.scrollTop(obj.scrollTop());
		});
	},
	switchSheetLocker: function(I) { //This can be used with setting fnSwitchSheet to locks sheets together when switching, useful in history viewing
		jQuery(jQuery.sheet.instance).each(function(i) {
			this.setActiveSheet(I);
		});
	}
};

var key = { /* key objects, makes it easier to develop */
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

var arrHelpers = {
	foldPrepare: function(firstArg, theArguments, unique) { // Computes the best array-like arguments for calling fold().
		var result;
		if (firstArg != null &&
			firstArg instanceof Object &&
			firstArg["length"] != null) {
			result = firstArg;
		} else {
			result = theArguments;
		}

		if (unique) {
			result = this.unique(result);
		}

		return result;
	},
	fold: function(arr, funcOfTwoArgs, result, castToN, N) {
		for (var i = 0; i < arr.length; i++) {
			result = funcOfTwoArgs(result, (castToN == true ? N(arr[i]): arr[i]));
		}
		return result;
	},
	toNumbers: function(arr) {
		arr = jQuery.makeArray(arr);

		for (var i = 0; i < arr.length; i++) {
			if (jQuery.isArray(arr[i])) {
				arr[i] = this.toNumbers(arr[i]);
			} else if (arr[i]) {
				if (isNaN(arr[i])) {
					arr[i] = 0;
				}
			} else {
				arr[i] = 0;
			}
		}

		return arr;
	},
	unique: function(arr) {
		var a = [];
		var l = arr.length;
		for (var i=0; i<l; i++) {
			for(var j=i+1; j<l; j++) {
				// If this[i] is found later in the array
				if (arr[i] === arr[j])
					j = ++i;
			}
			a.push(arr[i]);
		}
		return a;
	}
};

/**
 * PrimeFaces Spreadsheet Widget
 */
PrimeFaces.widget.Spreadsheet = function(id, cfg) {
	this.id = id;
	this.jqId = PrimeFaces.escapeClientId(id);
	this.cfg = cfg;
	
	this.cfg.buildSheet = true;
    this.cfg.calcOff = true;
    this.cfg.autoAddCells = false;
    this.cfg.minSize = {rows: 0, cols: 0};
    this.cfg.editableTabs = false;
	
	this.spreadsheet = jQuery(this.jqId + '_datasources').sheet(this.cfg);
	
	jQuery('.jSheetTabContainer span:last-child').hide();
}

PrimeFaces.widget.Spreadsheet.prototype.save = function() {
	var tables = this.spreadsheet.sheetInstance.exportSheet.html();
	
	for(var i = 0 ; i < tables.length; i++) {
		var dataCells = jQuery(tables[i]).find('tbody tr td');
		
		var transportCells = jQuery(this.jqId + '_datatransports').children().eq(i).find('tbody tr td');

		dataCells.each(function(j) {
			var cellValue = jQuery(this).html();
		
			jQuery(transportCells[j]).children('input').val(cellValue);
		});
	}	
}