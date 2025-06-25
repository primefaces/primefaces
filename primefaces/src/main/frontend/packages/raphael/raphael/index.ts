import raphael from "raphael";

declare global {
    interface Window {
        Raphael: typeof raphael;
    }
}

// Expose Raphael to the global scope
// Not needed for our code, but might already be used by external code
Object.assign(window, { Raphael: raphael });