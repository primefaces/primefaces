import path from "node:path";
import { fileURLToPath } from "node:url";

const dirName = path.dirname(fileURLToPath(import.meta.url));

export const RootDir = path.normalize(path.resolve(dirName, "..", ".."));
export const MavenRootDir = path.resolve(RootDir, "..", "..", "..");
export const PackagesDir = path.resolve(RootDir, "packages");
export const DistDir = path.resolve(RootDir, "dist");
export const DocsDir = path.resolve(RootDir, "docs");

export const PnPDataPath = path.resolve(RootDir, ".pnp.data.json");

export const TargetResourceDir = path.resolve(RootDir, "..", "..", "..", "target", "generated-resources", "META-INF", "resources");
export const TargetPrimeFacesResourceDir = path.join(TargetResourceDir, "primefaces");

export const TarBall = path.resolve(RootDir, "package.tgz");

export const IsProduction = process.env.NODE_ENV !== "development";
export const IsVerbose = process.argv.includes("--verbose");

