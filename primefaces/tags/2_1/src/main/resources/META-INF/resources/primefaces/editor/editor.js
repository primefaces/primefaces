PrimeFaces.widget.Editor = function(id, config) {
	PrimeFaces.widget.Editor.superclass.constructor.call(this, id, config);
	
	if(config.resizable)
		this.setupResize(config.widthHeightController);
	
	if(config.language != undefined)
		PrimeFaces.widget.EditorI18NProvider.applyLanguage(this, config.language);
	
	if(config.title != undefined)
		this.setTitle(config.title);
}

YAHOO.lang.extend(PrimeFaces.widget.Editor, YAHOO.widget.Editor,
{
	setupResize : function(widthHeightController) {
		this.on('editorContentLoaded', function() {
			
	        resize = new YAHOO.util.Resize(this.get('element_cont').get('element'), {
	            handles: ['br'],
	            autoRatio: true,
	            status: true,
	            proxy: true,
	            setSize: false
	        });

	        resize.on('startResize', function() {
	            this.hide();
	            this.set('disabled', true);
	        }, this, true);

	        resize.on('resize', function(args) {
	            var h = args.height;
	            var th = (this.toolbar.get('element').clientHeight + 2);
	            var dh = (this.dompath.clientHeight + 1);
	            var newH = (h - th - dh);
	            this.set('width', args.width + 'px');
	            this.set('height', newH + 'px');
	            this.set('disabled', false);
	            widthHeightController.value = args.width + ',' + newH;
	            this.show();
	        }, this, true);
	    });
	},
	
	setTitle : function(title) {
		this._defaultToolbar.titlebar = title;
	}
});

PrimeFaces.widget.EditorI18NProvider = {
		
		messageBundle: {
			tr: {
				MSG_FONT_NAME_AND_SIZE : "Yaz\u0131 Tipi ve B\u00fcy\u00fckl\u00fc\u011f\u00fc",
				MSG_FONT_STYLE : "Yaz\u0131 Stili",
				MSG_UNDO_REDO : "Geri al/Yenile",
				MSG_ALIGNMENT : "Hizalama",
				MSG_PARAGRAPH_STYLE : "Paragraf Stili",
				MSG_INDENTING_AND_LISTS : "Girintiler ve Listeler",
				MSG_INSERT_ITEM : "Ekle",
				STR_IMAGE_BORDER : "\u0130maj kenarlar\u0131",
				STR_IMAGE_BORDER_SIZE: "\u0130maj kenar boyutu",
				STR_IMAGE_COPY: "\u0130maj kopyala",
				STR_IMAGE_BORDER_TYPE: "\u0130maj kenar tipi",
				STR_IMAGE_ORIG_SIZE: "\u0130maj ger\u00E7ek boyutu",
				STR_IMAGE_PADDING: "\u0130maj dolgusu",
				STR_IMAGE_PROP_TITLE: "\u0130maj ba\u015Fl\u0131\u011F\u0131",
				STR_IMAGE_SIZE: "\u0130maj b\u00FCy\u00FCkl\u00FC\u011F\u00FC",
				STR_IMAGE_TEXTFLOW: "\u0130maj yaz\u0131s\u0131",
				STR_IMAGE_TITLE: "\u0130maj \u00F6zellikleri",
				STR_IMAGE_URL: "\u0130maj adresini",
				STR_IMAGE_HERE: "\u0130maj adresi",
				STR_LINK_NEW_WINDOW: "Link yeni pencerede",
				STR_LINK_PROP_REMOVE: "Link \u00F6zellik sil ",
				STR_LINK_PROP_TITLE: "Link \u00F6zellikleri",
				STR_LINK_TITLE: "Link ba\u015Fl\u0131\u011F\u0131",
				STR_LOCAL_FILE_WARNING: "Link lokal dosya hatas\u0131",
				STR_NONE: "Bo\u015F"
			},
			
			pt : {
				MSG_FONT_NAME_AND_SIZE : "Nome e Tamanho da Fonte",
                MSG_FONT_STYLE : "Estilo da Fonte",
                MSG_UNDO_REDO : "Desfaz/Refaz",
                MSG_ALIGNMENT : "Alinhamento",
                MSG_PARAGRAPH_STYLE : "Estilo do Par\u00E1grafo",
                MSG_INDENTING_AND_LISTS : "Recuo/Listas",
                MSG_INSERT_ITEM : "Link / Img",
                STR_IMAGE_BORDER : "Borda",
                STR_IMAGE_BORDER_SIZE: "Tam",
                STR_IMAGE_COPY: "",
                STR_IMAGE_BORDER_TYPE: "Tipo",
                STR_IMAGE_ORIG_SIZE: "",
                STR_IMAGE_PADDING: "Margem",
                STR_IMAGE_PROP_TITLE: "Inserir Imagem",
                STR_IMAGE_SIZE: "Tam.",
                STR_IMAGE_TEXTFLOW: "Alinhar",
                STR_IMAGE_TITLE: "Descri\u00E7\00E3o",
                STR_IMAGE_URL: "Img. URL",
                STR_IMAGE_HERE: "",
                STR_LINK_NEW_WINDOW: "Abrir em uma nova janela",
                STR_LINK_PROP_REMOVE: "Remover Link",
                STR_LINK_PROP_TITLE: "Inserir Link",
                STR_LINK_TITLE: "Descri\u00E7\00E3o",
                STR_LOCAL_FILE_WARNING: "",
                STR_NONE: "none"
			}
		},
		
		applyLanguage : function(editor, languageKey) {
			if(this.messageBundle[languageKey] != undefined) {
				var buttons = editor._defaultToolbar.buttons;
				
				buttons[0].label = this.messageBundle[languageKey].MSG_FONT_NAME_AND_SIZE;
				buttons[2].label = this.messageBundle[languageKey].MSG_FONT_STYLE;
				buttons[6].label = this.messageBundle[languageKey].MSG_UNDO_REDO;
				buttons[8].label = this.messageBundle[languageKey].MSG_ALIGNMENT;
				buttons[10].label = this.messageBundle[languageKey].MSG_PARAGRAPH_STYLE;
				buttons[12].label = this.messageBundle[languageKey].MSG_INDENTING_AND_LISTS;
				buttons[14].label = this.messageBundle[languageKey].MSG_INSERT_ITEM;
				editor.STR_IMAGE_TITLE = this.messageBundle[languageKey].STR_IMAGE_TITLE;
				editor.STR_IMAGE_BORDER = this.messageBundle[languageKey].STR_IMAGE_BORDER;
				editor.STR_IMAGE_BORDER_SIZE = this.messageBundle[languageKey].STR_IMAGE_BORDER_SIZE;
				editor.STR_IMAGE_HERE = this.messageBundle[languageKey].STR_IMAGE_HERE;
				editor.STR_IMAGE_COPY = this.messageBundle[languageKey].STR_IMAGE_COPY;
				editor.STR_IMAGE_BORDER_TYPE = this.messageBundle[languageKey].STR_IMAGE_BORDER_TYPE;
				editor.STR_IMAGE_URL = this.messageBundle[languageKey].STR_IMAGE_URL;
				editor.STR_IMAGE_ORIG_SIZE = this.messageBundle[languageKey].STR_IMAGE_ORIG_SIZE;
				editor.STR_IMAGE_PADDING = this.messageBundle[languageKey].STR_IMAGE_PADDING;
				editor.STR_IMAGE_PROP_TITLE = this.messageBundle[languageKey].STR_IMAGE_PROP_TITLE;
				editor.STR_IMAGE_SIZE = this.messageBundle[languageKey].STR_IMAGE_SIZE;
				editor.STR_IMAGE_TEXTFLOW = this.messageBundle[languageKey].STR_IMAGE_TEXTFLOW;
				editor.STR_LINK_NEW_WINDOW = this.messageBundle[languageKey].STR_LINK_NEW_WINDOW;
				editor.STR_LINK_PROP_REMOVE = this.messageBundle[languageKey].STR_LINK_PROP_REMOVE;
				editor.STR_LINK_PROP_TITLE = this.messageBundle[languageKey].STR_LINK_PROP_TITLE;
				editor.STR_LINK_TITLE = this.messageBundle[languageKey].STR_LINK_TITLE;
				editor.STR_LOCAL_FILE_WARNING = this.messageBundle[languageKey].STR_LOCAL_FILE_WARNING;
				editor.STR_NONE = this.messageBundle[languageKey].STR_NONE;
			}
		}
};
