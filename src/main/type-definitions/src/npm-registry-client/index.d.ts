// https://github.com/microsoft/types-publisher/blob/master/src/types/npm-registry-client.d.ts

declare module "npm-registry-client" {

    class RegClient {
        constructor(config?: RegClient.Config);
        request(uri: string, params: RegClient.RequestParams, cb: (error?: Error, data?: unknown, json?: unknown, response?: unknown) => void): void;
        publish(uri: string, params: RegClient.PublishParams, cb: (error?: Error) => void): void;
        deprecate(uri: string, params: RegClient.DeprecateParams, cb: (error?: Error, data?: unknown, raw?: string, response?: unknown) => void): void;
        get(uri: string, params: RegClient.GetParams, cb: (error?: Error, info?: RegClient.PackageInfo) => void): void;
        distTags: {
            add(uri: string, params: RegClient.AddTagParams, cb: (error?: Error) => void): void;
            fetch(uri: string, params: RegClient.FetchTagParams, cb: (error?: Error, tags?: Record<string, string>) => void): void;
        }
    }

    namespace RegClient {
        type Access = "public" | "restricted";
        type Credentials = UsernameCredentials | TokenCredentials;
        interface Config {
            defaultTag?: string;
            log?: any;
            userAgent?: string;
            couchToken?: any;
            sessionToken?: string;
            maxSockets?: number;
            isFromCI?: boolean;
            scope?: string;
            proxy?: {
                http?: string,
                https?: string,
                localAddress?: string,
            };
            ssl?: {
                ca?: string,
                certificate?: string,
                key?: string,
                strict?: boolean,
            };
            retry?: {
                retries?: number,
                factor?: number,
                minTimeout?: number;
                maxTimeout?: number;
            };
        }
        interface RequestParams {
            method?: string;
            body?: {};
        }
        interface GetParams {
            timeout?: number;
            follow?: boolean;
            staleOk?: boolean;
            auth?: Credentials;
            fullMetadata?: boolean;
        }
        interface PublishParams {
            metadata: {};
            access: Access;
            body: NodeJS.ReadableStream;
            auth: Credentials;
        }
        interface AddTagParams {
            package: string;
            version: string;
            distTag: string;
            auth: Credentials;
        }
        interface FetchTagParams {
            package: string;
            auth?: Credentials;
        }
        interface DeprecateParams {
            version: string;
            message: string;
            auth: Credentials;
        }
        interface TokenCredentials {
            token: string;
            alwaysAuth?: boolean;
        }
        interface UsernameCredentials {
            username: string;
            password: string;
            email: string;
            alwaysAuth?: boolean;
        }
        interface Person {
            name: string;
            email?: string;
            url?: string;
            githubUsername?: string;
        }
        interface PackageInfo {
            homepage?: string;
            repository?: {type: string, url: string},
            keywords?: string[];
            time?: Record<string, string>;
            license?: string;
            users?: Record<string, boolean>;
            maintainers?: Person[];
            versions?: Record<string, Json>;
            author?: Person;
            name?: string;
            bugs?: {url: string};
            readme?: string;
            description?: string;
            readmeFilename?: string;
        }
    }

    export = RegClient;
}