import raphael from "raphael";

// Expose Raphael to the global scope
// Not needed for our code, but might already be used by external code
Object.assign(window, { Raphael: raphael });