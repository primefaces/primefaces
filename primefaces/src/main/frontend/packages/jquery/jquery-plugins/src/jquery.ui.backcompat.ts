// Separate module to set $.uiBackCompat = true
// This needs to be done before loading code from
// other imports. Imports are hoisted to the top,
// above all other code in a module file.
Object.assign($, { uiBackCompat: true });
