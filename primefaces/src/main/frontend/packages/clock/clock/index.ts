import { Clock, SimpleDateFormat } from "./src/clock.js";

PrimeFaces.component.Clock = Clock;
// TODO SimpleDateFormat is NOT a component, just a class, and should not be added to the PrimeFaces.component scope
// Add it for now to preserve backwards compatibility in case external code uses it.
PrimeFaces.component.SimpleDateFormat = SimpleDateFormat;

declare global {
    namespace PrimeFacesTypes {
        export interface ComponentRegistry {
            Clock: typeof Clock;
            SimpleDateFormat: typeof SimpleDateFormat;
        }
    }

    namespace PrimeFacesTypes.component {
        export type ClockCfg = import("./src/clock.js").ClockCfg;
        export type SimpleDateFormatCfg = import("./src/clock.js").SimpleDateFormatCfg;
    }

    namespace PrimeFacesTypes.component.Clock {
        /**
         * Indicates which time the clock component uses. `client` uses the time from the client (browser), `server` uses
         * the time from the server.
         */
        export type TimeMode = "client" | "server";

        /**
         * Display mode for the clock component. `analog` displays an analog clock, `digital` a digitial clock.
         */
        export type DisplayMode = "analog" | "digital";

        /**
         * Computed dimensions for the individual parts of the analog clock, all in pixels.
         */
        export interface Dimensions {
            /** Width of the clock element in pixels. */
            size: number;
            /** Half width of the clock element in pixels. */
            half: number;
            /** Width of the clock face in pixels. */
            clock_width: number;
            /** Distance in pixels from the center of the circle where the hour mark starts. */
            hour_sign_min_size: number;
            /** Distance in pixels from the center of the circle where the hour mark ends. */
            hour_sign_max_size: number;
            /** Stroke width in pixels of the hour marks. */
            hour_sign_stroke_width: number;
            /** Radial distance in pixels from the circumference of the circle where the hour hand starts. */
            hour_hand_start_position: number;
            /** Stroke width in pixels of the hour hand. */
            hour_hand_stroke_width: number;
            /** Radial distance in pixels from the circumference of the circle where the minute hand starts. */
            minute_hand_start_position: number;
            /** Stroke width in pixels of the minute hand. */
            minute_hand_stroke_width: number;
            /** Radial distance in pixels from the circumference of the circle where the seconds hand starts. */
            second_hand_start_position: number;
            /** Stroke width in pixels of the seconds hand. */
            second_hand_stroke_width: number;
            /** Radius in pixels of the pin at the center of the clock face. */
            pin_width: number;
        }
    }
}
