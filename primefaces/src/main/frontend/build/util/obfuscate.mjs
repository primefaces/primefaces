import nodeCrypto from "node:crypto";

/**
 * Applies a basic encryption algorithm to the given string.
 * The point is not to make it secure, but to to discourage
 * people from relying on the exact string value. We use a
 * random seed that is different for each release.
 * @param {string} seed The seed to use for the encryption.
 * @return {(value: string) => string} A function that obfuscates the given string.
 */
export function createObfuscator(seed) {
    const algorithm = "aes-192-cbc";
    const key = nodeCrypto.scryptSync(seed, "salt", 24);
    const iv = Buffer.alloc(16, 0);
    /** @type {Map<string, string>} */
    const cache = new Map();
    return value => {
        const cached = cache.get(value);
        if (cached !== undefined) {
            return cached;
        }
        const cipher = nodeCrypto.createCipheriv(algorithm, key, iv);
        let encrypted = cipher.update(value, "utf8", "base64");
        encrypted += cipher.final("base64");
        cache.set(value, encrypted);
        return encrypted;
    }
}
