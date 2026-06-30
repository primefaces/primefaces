// @ts-check

const { manifest } = require("pacote");
const { basename, normalize, resolve, dirname } = require("path");
const { createInterface } = require("readline");
const semver = require("semver");
const { publish: publishToNpm } = require("libnpmpublish");
const npmFetch = require("npm-registry-fetch");
const { pack } = require("tar-stream");
const { consumeBoolean, consumeInt, consumeString, consumeStringArray } = require('./cli-helper');
const { DefaultNpmRegistry, Names, Paths } = require('./constants');
const { entriesToObject, isNotNull, parseJsonObject } = require('./lang');
const { readFileUtf8 } = require('./lang-fs');
const zlib = require("zlib");

/** @type {PublishCliArgs} */
const DefaultCliArgs = {
    access: "public",
    askForMissingCredentials: true,
    credentials: {
        type: "token",
        token: "",
    },
    declarations: {
        entry: Paths.PrimeFacesDeclarationFile,
        referenced: [Paths.PrimeFacesModuleDeclarationFile],
    },
    dryRun: "",
    excludedPackageJsonFields: ["devDependencies", "jsdocs", "scripts"],
    extraFiles: [],
    packageJson: Paths.PackageJsonFile,
    readme: undefined,
    registry: DefaultNpmRegistry,
    rootDir: Paths.ProjectRootDir,
    version: {},
};

/**
 * @param {import("readline").Interface} readlineInterface
 * @param {string} query 
 * @return {Promise<string>}
 */
async function question(readlineInterface, query) {
    return new Promise((resolve, reject) => {
        readlineInterface.question(query, answer => resolve(answer || ""));
    });
}

/**
 * @param {import("readline").Interface} readlineInterface
 * @return {Promise<undefined | NpmRegistry.Credentials>}
 */
async function readCredentials(readlineInterface) {
    /** @type {undefined | NpmRegistry.Credentials} */
    let result = undefined;

    console.log("No credentials were given for publishing the type declarations to npm.")
    console.log("Enter credentials, or leave empty and press enter to skip publishing to npm.")

    do {
        const type = await question(readlineInterface, "Would you like to authenticate with an npm token or username & password ? (token / username) ");
        if (type.toLowerCase().startsWith("t")) {
            const token = await question(readlineInterface, "Enter npm auth token: ");
            if (token) {
                result = { type: "token", token };
            }
            else {
                console.log("Skipping npm publish as no token was entered.");
                result = undefined;
            }
            const otp = await question(readlineInterface, "Enter optional OTP for 2 factor authentication ? ");
            if (result && otp) {
                result.otp = otp;
            }
        }
        else if (type.toLowerCase().startsWith("u")) {
            const username = await question(readlineInterface, "Enter username: ");
            const password = await question(readlineInterface, "Enter password: ");
            const email = await question(readlineInterface, "Enter email: ");
            if (username && password && email) {
                result = { type: "username", email, password, username };
            }
            else {
                console.log("Skipping npm publish as one of username, password, token was not entered.");
                result = undefined;
            }
            const otp = await question(readlineInterface, "Enter optional OTP for 2 factor authentication ? ");
            if (result && otp) {
                result.otp = otp;
            }
        }
        else {
            console.log("Skipping npm publish as neither token nor username was selected.");
            result = undefined;
        }
    }
    while (!(await question(readlineInterface, "Is that OK ? (y / n) ")).toLowerCase().startsWith("y"));

    return result;
}

/**
 * Simple parser for CLI arguments. Throws an error in case an unknown option is passed.
 * @return {Promise<undefined | PublishCliArgs>}
 */
async function parseCliArgs() {
    const result = Object.assign({}, DefaultCliArgs);
    const stack = process.argv.slice(2).reverse();
    /** @type {Partial<NpmRegistry.UsernameCredentials>} */
    const usernameCredentials = {
        email: undefined,
        password: undefined,
        username: undefined,
        alwaysAuth: true,
    };
    /** @type {Partial<NpmRegistry.TokenCredentials>} */
    const tokenCredentials = {
        token: undefined,
        alwaysAuth: true,
    };
    while (stack.length > 0) {
        const arg = stack.pop();
        if (arg) {
            switch (arg.toLowerCase()) {
                case "-h":
                case "--help":
                    console.log(`Usage:`);
                    console.log(` ${process.argv[0]} ${process.argv[1]} -- \\`);
                    console.log(`  --token <your-npm-token> \\`)
                    console.log(`  --major 9 \\`)
                    console.log(`  --minor 0`)
                    console.log("");
                    console.log(`Options:`);
                    console.log("");
                    console.log("== Files == ");
                    console.log(" (all paths can also be absolute)")
                    console.log(` -b`);
                    console.log(` --rootdir`);
                    console.log(`     Root dir to which other paths are relative to.`);
                    console.log(` -j`);
                    console.log(` --packagejson`);
                    console.log(`     package.json file to use, relative to the project root directory.`);
                    console.log(` -r`);
                    console.log(` --readme`);
                    console.log(`     Markdown file to use as the README.md of the package, relative to the project root directory.`);
                    console.log(` -d`);
                    console.log(` --entrydeclaration`);
                    console.log(`     Path to the entry declaration file, relative to the project root directory.`);
                    console.log(` -c`);
                    console.log(` --referenceddeclarations`);
                    console.log(`     Path to additional declaration files referenced by the entry declaration file, separated with commas and relative to the project root directory.`);
                    console.log(` -x`);
                    console.log(` --extrafiles`);
                    console.log(`     Path to additional files to include in the published package, separated with commas and relative to the project root directory.`);
                    console.log("");
                    console.log("== Authorization == ");
                    console.log(` -2`);
                    console.log(` --otp`);
                    console.log(`     Optional npm OTP token for 2 factor authentication.`);
                    console.log(` -t`);
                    console.log(` --token`);
                    console.log(`     Npm auth token for authorization. You must specify either "token"; or "username", "password" and "email".`);
                    console.log(` -u`);
                    console.log(` --username`);
                    console.log(`     Username for authorization. You must specify either "token"; or "username", "password" and "email".`);
                    console.log(` -e`);
                    console.log(` --email`);
                    console.log(`     Email for authorization. You must specify either "token"; or "username", "password" and "email".`);
                    console.log(` -p`);
                    console.log(` --password`);
                    console.log(`     Password for authorization. You must specify either "token"; or "username", "password" and "email".`);
                    console.log(` -o`);
                    console.log(` --prompt`);
                    console.log(`     When "true" and no credentials are given, ask the user to enter credentials. When "false", fail if no credentials were entered. Defaults to "true"`);
                    console.log("");
                    console.log("== Version == ");
                    console.log(` -m`);
                    console.log(` --major`);
                    console.log(`     Major version number for publishing`);
                    console.log(` -n`);
                    console.log(` --minor`);
                    console.log(`     Minor version number for publishing`);
                    console.log(` -z`);
                    console.log(` --patch`);
                    console.log(`     Optional. Patch version number for publishing. If not given, the latest version with the given major and minor number is looked up and its patch version is incremented by one`);
                    console.log("");
                    console.log("== Misc. == ");
                    console.log(` -a`);
                    console.log(` --access`);
                    console.log(`     Optional. Whether the package is public or not, one of "access" or "restricted". Defaults to public.`);
                    console.log(` -f`);
                    console.log(` --excludedpackagejsonfields`);
                    console.log(`     Optional. Fields to exclude from the package.json, separated with commas. Defaults to "devDependencies" and "scripts".`);
                    console.log(` -g`);
                    console.log(` --registry`);
                    console.log(`     Optional. URL to the npm registry. Default to the default npm registry.`);
                    console.log(` -y`);
                    console.log(` --dryrun`);
                    console.log(`     Optional. When set, do not publish to npm, but only write the tarball that would be published to the given file path (relative to the project root dir)`);
                    process.exit(0);
                case "-2":
                case "--otp":
                    consumeString(stack, v => result.otp = v);
                    break;
                case "-a":
                case "--access": {
                    consumeString(stack, access => {
                        if (access !== "public" && access !== "restricted") {
                            throw new Error("Access must be one of 'public' or 'restricted'");
                        }
                        result.access = access;
                    });
                    break;
                }
                case "-b":
                case "--rootdir":
                    consumeString(stack, v => result.rootDir = v);
                    break;
                case "-d":
                case "--entrydeclaration":
                    consumeString(stack, v => result.declarations.entry = v);
                    break;
                case "-c":
                case "--referenceddeclarations":
                    consumeStringArray(stack, v => result.declarations.referenced = v);
                    break;
                case "-e":
                case "--email":
                    consumeString(stack, v => usernameCredentials.email = v);
                    break;
                case "-f":
                case "--excludedpackagejsonfields":
                    consumeStringArray(stack, v => result.excludedPackageJsonFields = v);
                    break;
                case "-g":
                case "--registry":
                    consumeString(stack, v => result.registry = v);
                    break;
                case "-j":
                case "--packagejson":
                    consumeString(stack, v => result.packageJson = v);
                    break;
                case "-m":
                case "--major":
                    consumeInt(stack, v => result.version.major = v);
                    break;
                case "-n":
                case "--minor":
                    consumeInt(stack, v => result.version.minor = v);
                    break;
                case "-o":
                case "--prompt":
                    consumeBoolean(stack, v => result.askForMissingCredentials = v);
                    break;
                case "-p":
                case "--password":
                    consumeString(stack, v => usernameCredentials.password = v);
                    break;
                case "-r":
                case "--readme":
                    consumeString(stack, v => result.readme = v);
                    break;
                case "-t":
                case "--token":
                    consumeString(stack, v => tokenCredentials.token = v);
                    break;
                case "-u":
                case "--username":
                    consumeString(stack, v => usernameCredentials.username = v);
                    break;
                case "-x":
                case "--extrafiles":
                    consumeStringArray(stack, v => result.extraFiles = v);
                    break;
                case "-y":
                case "--dryrun":
                    consumeString(stack, v => result.dryRun = v);
                    break;
                case "-z":
                case "--patch":
                    consumeInt(stack, v => result.version.patch = v);
                    break;
                default:
                    throw new Error("Unknown CLI argument encountered: '" + arg + "'");
            }
        }
    }

    let credentialsCount = 0;
    if (usernameCredentials.email && usernameCredentials.password && usernameCredentials.username) {
        credentialsCount += 1;
        result.credentials = {
            type: "username",
            email: usernameCredentials.email,
            password: usernameCredentials.password,
            username: usernameCredentials.username,
        };
    }
    if (tokenCredentials.token) {
        credentialsCount += 1;
        result.credentials = {
            type: "token",
            token: tokenCredentials.token,
        };
    }
    if (result.otp) {
        result.credentials.otp = result.otp;
    }

    // Resolve relative paths
    result.packageJson = resolve(result.rootDir, result.packageJson);
    if (result.readme) {
        result.readme = resolve(result.rootDir, result.readme);
    }
    if (result.dryRun) {
        result.dryRun = resolve(result.rootDir, result.dryRun);
    }
    result.declarations.entry = resolve(result.rootDir, result.declarations.entry);
    result.declarations.referenced = result.declarations.referenced.map(file => resolve(result.rootDir, file));
    result.extraFiles = result.extraFiles.map(file => resolve(result.rootDir, file));

    if (credentialsCount === 0 && !result.dryRun) {
        if (!result.askForMissingCredentials) {
            console.log("No credentials were provided and --prompt is not set to true");
            return undefined
        }
        else {
            const readlineInterface = createInterface({
                input: process.stdin,
                output: process.stdout,
            });
            try {
                const credentials = await readCredentials(readlineInterface);
                if (credentials) {
                    result.credentials = credentials;
                }
                else {
                    return undefined;
                }
            }
            finally {
                readlineInterface.close();
            }
        }
    }
    else if (credentialsCount > 1) {
        throw new Error("Too many credentials, you must specify either token; or username, password, email.");
    }

    const echoArgs = Object.assign({}, result);
    echoArgs.credentials = Object.assign({}, echoArgs.credentials, {
        password: "password" in echoArgs.credentials ? "***" : undefined,
        token: "token" in result.credentials ? "***" : undefined,
    });
    console.log("Parsed CLI arguments:", JSON.stringify(echoArgs));

    return result;
}

/**
 * @param {NpmRegistry.Credentials} auth
 * @returns {Partial<import("npm-registry-fetch").Options>}
 */
function createAuth(auth) {
    /** @type {import("npm-registry-fetch").Options} */
    const opts = {};
    /** @type {import("npm-registry-fetch").AuthOptions} */
    const forceAuth = {};
    if (forceAuth.alwaysAuth !== undefined) {
        forceAuth.alwaysAuth = auth.alwaysAuth;
    }
    if (auth.type === "token") {
        forceAuth.token = auth.token;
    }
    if (auth.type === "username") {
        forceAuth.username = auth.username;
        forceAuth.password = auth.password;
        forceAuth.email = auth.email;
    }
    if (Object.keys(forceAuth).length > 0) {
        opts.forceAuth = forceAuth;
    }
    if (auth.otp !== undefined) {
        // Currently only works when set at the opts,
        // not inside the forceAuth, but set it on
        // both anyways for the future
        forceAuth.otp = auth.otp;
        opts.otp = auth.otp;
    }
    return opts;
}

/**
 * @param {NpmRegistry.PublishParams} params
 * @return {Promise<void>}
 */
async function publishPromise(params) {
    /** @type {import("npm-registry-fetch").Options} */
    const opts = {
        registry: params.registry,
        ...createAuth(params.auth),
    };
    if (params.access !== undefined) {
        // @ts-expect-error Type defs are wrong
        opts.access = params.access;
    }

    // @ts-expect-error
    opts.npmVersion = `${params.metadata["name"]}@${params.metadata["version"]}`

    const buffer = await stream2Buffer(params.body);

    const response = await publishToNpm(
        /** @type {import("pacote").Manifest} */(params.metadata),
        buffer,
        opts
    );
    if (response.status < 200 || response.status >= 300) {
        throw new Error(`NPM publish returned response code ${response.status}`);
    }
}

/**
 * @param {NpmRegistry.GetParams} params
 * @return {Promise<NpmRegistry.PackageInfo>}
 */
async function getPromise(params) {
    /** @type {import("npm-registry-fetch").Options} */
    const opts = {
        registry: params.registry,
    };
    if (params.auth !== undefined) {
        Object.assign(opts, createAuth(params.auth));
    }
    const uri = `/${encodeURIComponent(params.packageName)}`;
    const response = await npmFetch(uri, opts);
    if (response.status < 200 || response.status >= 300) {
        throw new Error(`NPM fetch failed with status code ${response.status}`);
    }
    const text = await response.text();
    const json = JSON.parse(text);
    if (typeof json.error === "string") {
        throw new Error("NPM fetch failed: " + json.error);
    }
    return json;
}

/**
 * @param {string} registry
 * @param {string} packageName
 * @param {number | undefined} majorVersion
 * @param {number | undefined} minorVersion 
 * @param {number | undefined} patchVersion 
 * @return {Promise<string>}
 */
async function findNewVersion(registry, packageName, majorVersion, minorVersion, patchVersion) {
    if (majorVersion !== undefined && minorVersion !== undefined && patchVersion !== undefined) {
        console.log("Major, minor, and patch version given, attempting to publish that version.")
        return `${majorVersion}.${minorVersion}.${patchVersion}`;
    }
    else {
        // Find nearest non existing patch version
        try {
            const info = await getPromise({
                registry: registry,
                packageName: packageName,
                follow: true,
                fullMetadata: false,
                staleOk: false,
            });
            const versions = Object.keys((info.versions || {})) //
                .map(v => semver.parse(v)) //
                .filter(isNotNull)
                .filter(v => majorVersion === undefined || v.major === majorVersion)
                .filter(v => minorVersion === undefined || v.minor === minorVersion)
                .sort((lhs, rhs) => lhs.compare(rhs));
            const mostRecent = versions.pop();
            if (mostRecent !== undefined) {
                return mostRecent.inc("patch").format();
            }
            else {
                console.log(`No versions were found for package ${packageName}, just using specified version`);
                return `${majorVersion || 1}.${minorVersion || 0}.${patchVersion || 0}`;
            }
        }
        catch (e) {
            // Fall back to base version when package does not exist yet
            console.log(`Package ${packageName} does not exist yet, just using specified version`);
            if ((/** @type {any} */(e)).code === "E404") {
                return `${majorVersion || 1}.${minorVersion || 0}.${patchVersion || 0}`;
            }
            else {
                throw e;
            }
        }
    }
}

/**
 * 
 * @param {NodeJS.WritableStream} stream 
 * @return {Promise<void>}
 */
function streamPromise(stream) {
    return new Promise((resolve, reject) => {
        stream.on("error", reject).on("finish", resolve);
    });
}

/**
 * 
 * @param {NodeJS.ReadableStream} stream 
 * @returns {Promise<Buffer>}
 */
function stream2Buffer(stream) {
    return new Promise((resolve, reject) => {
        /** @type {Uint8Array[]} */
        const buffer = [];
        stream.on("data", chunk => {
            if (!(chunk instanceof Uint8Array)) {
                reject(new Error("Expected Uint8Array chunk, but got: " + chunk));
            }
            buffer.push(chunk);
        });
        stream.on("end", () => resolve(Buffer.concat(buffer)));
        stream.on("error", err => reject(err));
    });
}


/**
 * @param {NpmPublishFiles} files 
 * @return {NodeJS.ReadableStream}
 */
function createPackageTgz(files) {
    const packer = pack();
    for (const [name, data] of Object.entries(files)) {
        packer.entry({
            name: `package/${normalize(name)}`,
        }, data);
    }
    packer.finalize();
    return packer.pipe(zlib.createGzip());
}

/**
 * @param {{access: NpmRegistry.Access, credentials: NpmRegistry.Credentials, files: NpmPublishFiles, registry: string, dryRun: string | undefined}} param0
 */
async function publish({ access, credentials, files, registry, dryRun }) {
    const packageJson = parseJsonObject(files[Names.NpmPackageJson] ?? "{}");
    if (packageJson === undefined) {
        throw new Error("No package.json was provided");
    }
    const name = packageJson["name"];
    if (typeof name !== "string" || name.length === 0) {
        throw new Error("package.json must contain a valid name");
    }
    const packedFiles = createPackageTgz(files);

    if (dryRun) {
        console.log(`Dry run enabled, skipping npm publish and writing tarball that would be published to '${dryRun}'`);
        await streamPromise(packedFiles.pipe(require("fs").createWriteStream(dryRun)));
    }
    else {
        await publishPromise({
            access: access,
            auth: credentials,
            body: packedFiles,
            metadata: packageJson,
            registry: registry,
        });
    }
}

/**
 * @param {PublishCliArgs} cliArgs
 * @return {Promise<import("pacote").Manifest>}
 */
async function readAndAdjustPackageJson(cliArgs) {
    const packageJson = await manifest(dirname(cliArgs.packageJson));
    const packageName = typeof packageJson["name"] === "string"
        ? packageJson["name"]
        : "";
    if (!packageName) {
        throw new Error("package.json does not contain a 'name' field");
    }
    packageJson["main"] = "";
    packageJson["types"] = basename(cliArgs.declarations.entry);
    packageJson["version"] = await findNewVersion(cliArgs.registry, packageName, cliArgs.version.major, cliArgs.version.minor, cliArgs.version.patch);
    for (const excludedField of cliArgs.excludedPackageJsonFields) {
        delete packageJson[excludedField];
    }
    console.log(`Adjusted package.json with version=${packageJson["version"]} and types=${packageJson["types"]}`);
    return packageJson;
}

async function main() {
    const cliArgs = await parseCliArgs();
    if (cliArgs) {
        // Read required files

        /** @type {[string, string][]} */
        const filePaths = [
            cliArgs.declarations.entry,
            ...cliArgs.declarations.referenced,
            ...cliArgs.extraFiles,
        ].map(path => [basename(path), path]);
        const reading = filePaths.map(([key, path]) => readFileUtf8(path).then(value => ({ key, value })));
        const files = entriesToObject(await Promise.all(reading));

        // Add special files (package.json, README.md)

        const packageJson = await readAndAdjustPackageJson(cliArgs);
        files[Names.NpmPackageJson] = JSON.stringify(packageJson, null, 4);
        if (cliArgs.readme) {
            files[Names.NpmReadme] = await readFileUtf8(cliArgs.readme);
        }

        // Publish

        console.log(`Attempting to publish ${packageJson["name"]}@${packageJson["version"]} to ${cliArgs.registry}`);

        await publish({
            access: cliArgs.access,
            credentials: cliArgs.credentials,
            dryRun: cliArgs.dryRun,
            files: files,
            registry: cliArgs.registry,
        });

        console.log(`Successfully published ${packageJson["name"]}@${packageJson["version"]} to ${cliArgs.registry}`);
    }
    else {
        console.log("Skipping npm publish")
    }
}

main().catch(e => {
    console.error("Failure while publishing to npm", e);
    process.exit(1);
});