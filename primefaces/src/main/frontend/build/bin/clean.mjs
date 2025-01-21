import { DistDir, DocsDir, TarBall } from "../common/environment.mjs";
import { findFrontendProjects } from "../common/frontend-project.mjs";
import { deleteIfExists } from "../lang/file.mjs";

/**
 * Deletes all generated files and directories. Usually clean is done by
 * Maven, but when working on the frontend, it may be helpful to clean
 * via this script.
 */
async function main() {
    const frontendProjects = await findFrontendProjects();
    await Promise.all([
        deleteIfExists(TarBall),
        deleteIfExists(DistDir),
        deleteIfExists(DocsDir),
        ...frontendProjects.map(async project => await deleteIfExists(project.dist)),
    ]);
}

main().catch(e => {
    console.error(e instanceof Error ? e.stack : e);
    process.exit(1);
});
