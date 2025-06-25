// @types/inputmask is missing typings for this entry point
// TODO Contribute this to DefinitelyTyped
declare module "inputmask/dist/jquery.inputmask.js" {
    const ip: typeof import("inputmask").default;
    export default ip;
}