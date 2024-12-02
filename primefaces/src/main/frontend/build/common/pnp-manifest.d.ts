import type { PackageLocator } from "pnpapi";

/**
 * Represents a single entry in map from keys to values, stored as a tuple. The
 * first element of the tuple is the key, and the second element is the value.
 * @typeParam Key The type of the keys in the map.
 * @typeParam Value The type of the values in the map.
 */
export type PnpMapEntry<Key, Value> = readonly [key: Key, value: Value];

/**
 * Represents a mapping from keys to values, stored as an array of map entries.
 * @typeParam Key The type of the keys in the map.
 * @typeParam Value The type of the values in the map.
 */
export type PnpMap<Key, Value> = readonly PnpMapEntry<Key, Value>[];

/**
 * The main part of the PnP data file. Contains details about a package referenced by
 * the project.
 */
export interface PnpPackageRegistryData {
    /**
     * The location of the package on disk, relative to the Plug'n'Play manifest. This path must
     * begin by either `./` or `../`, and must end with a trailing `/`.
     * @example "./.yarn/cache/react-npm-18.0.0-a0b1c2d3.zip"
     */
    readonly packageLocation: string;

    /**
     * The set of dependencies that the package is allowed to access. Each entry is a tuple where the
     * first key is a package name, and the value a package reference. Note that this reference may
     * be null! This only happens when a peer dependency is missing.
     * @example [["react-dom", "npm:18.3.1"]]
     */
    readonly packageDependencies: PnpMap<string, string | null>;

    /**
     * Can be either `SOFT`, or `HARD`. Hard package links are the most common, and mean that the
     * target location is fully owned by the package manager. Soft links, on the other hand,
     * typically point to arbitrary user-defined locations on disk.
     * 
     * The link type shouldn't matter much for most implementors - it's only needed because of some
     * subtleties involved in turning a Plug'n'Play tree into a node_modules one.
     */
    readonly linkType: "SOFT" | "HARD";

    /**
     * If true, this optional field indicates that the package must not be considered when the
     * Plug'n'Play runtime tries to figure out the package that contains a given path. This is
     * for instance what we use when using the `link:` protocol, as they often point to
     * subfolders of a package, not to other packages.
     */
    readonly discardFromLookup?: boolean;

    /**
     * A list of packages that are peer dependencies. Just like `linkType`, this field isn't used
     * by the Plug'n'Play runtime itself, but only by tools that may want to leverage the data
     * file to create a node_modules folder.
     * @example ["react-dom"]
     */
    readonly packagePeers?: readonly string[];
}

/**
 * When `pnpEnableInlining` is explicitly set to `false`, Yarn will generate an additional
 * `.pnp.data.json` file. This interface describes the structure of that JSON file.
 */
export interface PnpData {
    /**
     * An array of arbitrary strings; only used as a header field to give some context to Yarn users.
     */
    readonly __info: string[];

    /**
     * A list of package locators that are roots of the dependency tree. There will typically be one
     * entry for each workspace in the project (always at least one, as the top-level package is a
     * workspace by itself).
     * 
     * @example [
     * {name: "@app/monorepo", reference: "workspace:."},
     * {name: "@app/website", reference: "workspace:website"}
     * ]
     */
    readonly dependencyTreeRoots: readonly PackageLocator[];

    /**
     * A nullable regexp. If set, all project-relative importer paths should be matched against it.
     * If the match succeeds, the resolution should follow the classic Node.js resolution algorithm
     * rather than the Plug'n'Play one. Note that unlike other paths in the manifest, the one checked
     * against this regexp won't begin by `./`.
     * 
     * @example "^examples(/|$)"
     */
    readonly ignorePatternData: string | null;

    /**
     * If true, should a dependency resolution fail for an importer that isn't explicitly listed in
     * `fallbackExclusionList`, the runtime must first check whether the resolution would succeed for
     * any of the packages in `fallbackPool`; if it would, transparently return this resolution. Note
     * that all dependencies from the top-level package are implicitly part of the fallback pool, even
     * if not listed here.
     */
    readonly enableTopLevelFallback: boolean;

    /**
     * A map of locators that all packages are allowed to access, regardless whether they list them in
     * their dependencies or not.
     * 
     * @example [["@app/monorepo", "workspace:."]]
     */
    readonly fallbackPool: readonly (readonly string[])[];

    /**
     * A map of packages that must never use the fallback logic, even when enabled. Keys are the package
     * idents, values are sets of references. Combining the ident with each individual reference yields
     * the set of affected locators.
     * @example [["@app/server", ["workspace:sources/server"]]]
     */
    readonly fallbackExclusionList: PnpMap<string, readonly string[]>;

    /**
     * This is the main part of the PnP data file. This table contains the list of all packages, first
     * keyed by package ident then by package reference. One entry will have `null` in both fields and
     * represents the absolute top-level package.
     */
    readonly packageRegistryData:
      | [PnpMapEntry<null, PnpMapEntry<null, PnpPackageRegistryData>>]
      | PnpMap<string, PnpMap<string, PnpPackageRegistryData>>;
}
