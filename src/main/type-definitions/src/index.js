//@ts-check

const { main, parseCliArgs } = require("./cli");

main(parseCliArgs()).catch(e => {
    console.error(e);
    process.exit(1);
});