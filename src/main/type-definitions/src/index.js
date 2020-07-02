//@ts-check

const { main, readArguments } = require("./cli");

Promise.resolve(void 0)
    .then(readArguments)
    .then(main)
    .catch(e => {
        console.error(e);
        process.exit(1);
    });