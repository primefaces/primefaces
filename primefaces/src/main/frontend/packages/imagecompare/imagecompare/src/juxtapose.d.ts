/**
 * [JuxtaposeJS](https://juxtapose.knightlab.com) is a simple, open source tool for creating before/after image sliders.
 * Just provide two image URLs and Juxtapose will do the rest of the work for you. Below are instructions for
 * implementing Juxtapose with HTML and Javascript but we also have a tool that lets you make a slider without needing
 * to know any code.
 * 
 * Used by the `ImageCompare` widget.
 */
declare namespace juxtapose {
    /**
     * The direction in which the slider should move.
     */
    export type SlideMode = "horizontal" | "vertical";

    /**
     * Represents an image to be used for the comparison. Must contain at least the `src` property.
     */
    export interface ImageSpecification {
        /**
         * URL to the image.
         */
        src: string;
        /**
         * Optional label text for the image.
         */
        label?: string;
        /**
         * Optional credit text for the image.
         */
        credit?: string;
    }

    /**
     * Represents an image with some metadata that is displayed in the image slider.
     */
    export interface Graphic {
        /**
         * Credit text for the image. `false` when there is none.
         */
        credit: string | false;
        /**
         * Image element of this image. 
         */
        image: HTMLImageElement;
        /**
         * Whether this image was loaded.
         */
        loaded: boolean;
        /**
         * Label text for the image. `false` when there is none.
         */
        label: string | false;
    }

    /**
     * Represents the configuration that may be specified when a new image slider is created.
     */
    export interface JXSliderConfiguration {
        /**
         * Whether the slider should be animated to make it look smoother.
         */
        animate: boolean;
        /**
         * Instance of the image handler for the left image.
         */
        imgAfter: Graphic;
        /**
         * Instance of the image handler for the right image.
         */
        imgBefore: Graphic;
        /**
         * The direction in which the slider should move. Defaults to `horizontal`.
         */
        mode: SlideMode;
        /**
         * Whether the specified labels should be shown on the images.
         */
        showLabels: boolean;
        /**
         * Whether the specified credit should be shown on the images.
         */
        showCredits: boolean;
        /**
         * Initial position of the slider, as a percentage, eg. `50%`.
         */
        startingPosition: string;
        /**
         * Whether the image slider should be responsive to support different screen sizes.
         */
        makeResponsive: boolean;
    }

    /**
     * A list of instantiated sliders created on the current page. Can be used to access instances when you did not
     * save them.
     */
    export const sliders: JXSlider[];

    /**
     * Represents an instantiated image slider.
     */
    export class JXSlider {
        /**
         * Container for the slider control (`.jx-control`).
         */
        control: JQuery;
        /**
         * The draggable slider element (`.jx-controller`).
         */
        controller: JQuery;
        /**
         * Container element with controller and the left and right arrow.
         */
        handler: JQuery;
        /**
         * Element for the left slider arrow (`.jx-arrow`).
         */
        leftArrow: JQuery;
        /**
         * Container element with the left image (`.jx-image`).
         */
        leftImage: JQuery;
        /**
         * Current configuration of this slider.
         */
        options: JXSliderConfiguration;
        /**
         * Element for the right slider arrow (`.jx-arrow`).
         */
        rightArrow: JQuery;
        /**
         * Container element with the right image (`.jx-image`).
         */
        rightImage: JQuery;
        /**
         * Container element for the image slider component (`.jx-slider`).
         */
        slider: JQuery;
        /**
         * Current position of the image slider, eg. `50.00%`.
         */
        sliderPosition: string;
        /**
         * Wrapper element with the image slider.
         */
        wrapper: JQuery;
        /**
         * 
         * @param selector CSS selector for the element you want to turn into a slider.
         * @param leftAndRightImage The two images to be shown in the comparison.
         * @param options Lets you set additional options for the image slider.
         */
        constructor(elementSelector: string, leftAndRightImage: [ImageSpecification, ImageSpecification], options: Partial<JXSliderConfiguration>);
        /**
         * Display the given label text for the left or right image. Adds to the exiting label(s).
         * @param image The image for which to display the label. Should be either the `leftImage` or `rightImage`
         * property of this image slider instance.
         * @param labelText Label text to display.
         */
        displayLabel(image: string, labelText: string): void;
        /**
         * Display the given label text for the left or right image. Adds to the existing credits
         * @param image The image for which to display the credits. Should be either the `leftImage` or `rightImage`
         * property of this image slider instance.
         * @param creditsText Label text to display.
         */
        displayLabel(image: string, creditsText: string): void;
        /**
         * Finds the current position of this slider.
         * @return The current position of this slider, eg. `50.00%`.
         */
        getPosition(): string;
        /**
         * Moves this slider to the specified position, optionally animating the move.
         * @param percentage A number in the range `[0...100]`. May also be a string in the format `50.00%` or `50`.
         * Where you want to set the handle, relative to the left side of the slider.
         * @param animate `true` to animate the transition, `false` to move this slider immediately.
         */
        updateSlider(percentage: number | string, animate?: boolean): void;
    }
}