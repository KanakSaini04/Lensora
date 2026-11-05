package com.kanak.lensora.ui.graphics

/**
 * Contains AGSL (Android Graphics Shading Language) runtime shader definitions for Android 13+ (API 33+).
 */
object ViewfinderShaders {

    const val FOCUS_PEAKING_SHADER = """
        uniform shader composable;
        uniform float2 resolution;
        uniform float threshold;

        half4 main(float2 fragCoord) {
            half4 color = composable.eval(fragCoord);
            float2 step = float2(1.0) / resolution;
            half4 left  = composable.eval(fragCoord - float2(step.x, 0.0));
            half4 right = composable.eval(fragCoord + float2(step.x, 0.0));
            half4 top   = composable.eval(fragCoord - float2(0.0, step.y));
            half4 bot   = composable.eval(fragCoord + float2(0.0, step.y));

            float lumLeft  = dot(left.rgb,  half3(0.299, 0.587, 0.114));
            float lumRight = dot(right.rgb, half3(0.299, 0.587, 0.114));
            float lumTop   = dot(top.rgb,   half3(0.299, 0.587, 0.114));
            float lumBot   = dot(bot.rgb,   half3(0.299, 0.587, 0.114));

            float edge = abs(lumLeft - lumRight) + abs(lumTop - lumBot);
            if (edge > threshold) {
                return half4(0.0, 0.9, 1.0, 1.0);
            }
            return color;
        }
    """

    const val ZEBRA_STRIPES_SHADER = """
        uniform shader composable;
        uniform float2 resolution;
        uniform float time;

        half4 main(float2 fragCoord) {
            half4 color = composable.eval(fragCoord);
            float lum = dot(color.rgb, half3(0.299, 0.587, 0.114));

            if (lum > 0.94) {
                float stripe = sin((fragCoord.x + fragCoord.y + time * 30.0) * 0.25);
                if (stripe > 0.0) {
                    return half4(1.0, 0.85, 0.0, 1.0);
                } else {
                    return half4(0.0, 0.0, 0.0, 1.0);
                }
            }
            return color;
        }
    """

    const val FALSE_COLOR_SHADER = """
        uniform shader composable;

        half4 main(float2 fragCoord) {
            half4 color = composable.eval(fragCoord);
            float lum = dot(color.rgb, half3(0.299, 0.587, 0.114));

            if (lum > 0.99) return half4(1.0, 0.0, 0.0, 1.0);
            if (lum > 0.85) return half4(1.0, 1.0, 0.0, 1.0);
            if (lum > 0.52 && lum <= 0.58) return half4(1.0, 0.4, 0.7, 1.0);
            if (lum > 0.38 && lum <= 0.44) return half4(0.0, 1.0, 0.0, 1.0);
            if (lum > 0.05 && lum <= 0.15) return half4(0.0, 0.0, 1.0, 1.0);
            if (lum <= 0.05) return half4(0.5, 0.0, 0.5, 1.0);

            return half4(lum, lum, lum, 1.0);
        }
    """

    const val ANAMORPHIC_FLARE_SHADER = """
        uniform shader composable;
        uniform float2 resolution;

        half4 main(float2 fragCoord) {
            half4 color = composable.eval(fragCoord);
            float lum = dot(color.rgb, half3(0.299, 0.587, 0.114));

            if (lum > 0.88) {
                return color + half4(0.0, 0.4, 1.0, 0.5);
            }
            return color;
        }
    """
}
