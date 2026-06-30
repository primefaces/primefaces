/**
 * Tests the ability to create a namespace for sub props
 * 
 * @namespace bar namespace bar
 * @prop {Prop.Sub.CreateNamespace.Bar} bar prop bar
 * @prop {string} bar.x prop bar-x
 * @prop {number} bar.y prop bar-y
 * 
 * @namespace {Prop.Sub.CreateNamespace.Z} bar.z namespace z
 * @prop {typeof Prop.Sub.CreateNamespace.Z} bar.z prop bar-z
 * @prop {boolean} bar.z.a1 prop bar-z-a1
 * @prop {boolean} bar.z.a2 prop bar-z-a2
 */
({
})