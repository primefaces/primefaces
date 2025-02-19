// Custom JQuery UI bundle that includes only some components

// Separate module to set $.uiBackCompat = true
// This needs to be done before loading the remaining
// imports. Imports are hoisted to the top, above all
// other code in a module file.
import "./jquery.ui.backcompat.js";

import "jquery-ui/ui/version.js";
import "jquery-ui/ui/position.js";
import "jquery-ui/ui/data.js";
import "jquery-ui/ui/disable-selection.js";
import "jquery-ui/ui/focusable.js";
import "jquery-ui/ui/form-reset-mixin.js";
import "jquery-ui/ui/keycode.js";
import "jquery-ui/ui/labels.js";
import "jquery-ui/ui/scroll-parent.js";
import "jquery-ui/ui/tabbable.js";
import "jquery-ui/ui/unique-id.js";
import "jquery-ui/ui/plugin.js";

import "jquery-ui/ui/widget.js";
import "jquery-ui/ui/widgets/mouse.js";
import "jquery-ui/ui/widgets/draggable.js";
import "jquery-ui/ui/widgets/droppable.js";
import "jquery-ui/ui/widgets/resizable.js";
import "jquery-ui/ui/widgets/selectable.js";
import "jquery-ui/ui/widgets/sortable.js";
import "jquery-ui/ui/widgets/slider.js";

import "jquery-ui/ui/jquery-var-for-color.js";
import "jquery-ui/ui/vendor/jquery-color/jquery.color.js";

import "jquery-ui/ui/effect.js";
import "jquery-ui/ui/effects/effect-blind.js";
import "jquery-ui/ui/effects/effect-bounce.js";
import "jquery-ui/ui/effects/effect-clip.js";
import "jquery-ui/ui/effects/effect-drop.js";
import "jquery-ui/ui/effects/effect-explode.js";
import "jquery-ui/ui/effects/effect-fade.js";
import "jquery-ui/ui/effects/effect-fold.js";
import "jquery-ui/ui/effects/effect-highlight.js";
import "jquery-ui/ui/effects/effect-size.js";
import "jquery-ui/ui/effects/effect-scale.js";
import "jquery-ui/ui/effects/effect-puff.js";
import "jquery-ui/ui/effects/effect-pulsate.js";
import "jquery-ui/ui/effects/effect-shake.js";
import "jquery-ui/ui/effects/effect-slide.js";
import "jquery-ui/ui/effects/effect-transfer.js";
