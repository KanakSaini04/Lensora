# Lensora - AI & Camera Performance Rules for Developer Agents

## Core Performance Architecture Guidelines

### 1. Zero Memory Allocations in Per-Frame Camera Loops
- **Zero Object Allocations inside `ImageAnalysis.Analyzer.analyze()`**:
  - Never allocate objects (`Bitmap`, `ByteArray`, temporary primitive wrappers, iterator closures, lambda allocations, or auto-boxing primitives) inside high-frequency per-frame callbacks.
  - Pre-allocate reusable buffers (`ByteBuffer`, `FloatArray`, reusable target Bitmaps) during initialization outside the camera frame loop.
  - Close `ImageProxy` objects (`imageProxy.close()`) immediately after extracting pixel data to eliminate CameraX backpressure buffer starvation and frame drops.

### 2. 120Hz Jetpack Compose UI Rendering
- **Defer State Reads to Draw / Layout Phases**:
  - Prevent high-frequency state updates (zoom ratios, exposure levels, bounding box coordinates, optical flow parameters) from triggering recompositions across the entire composable tree.
  - Utilize lambda modifiers (`Modifier.graphicsLayer { ... }`, `Modifier.drawWithContent { ... }`) to defer state reads directly to the layout/draw phases.
  - Guarantee smooth 120Hz display refresh rates on high-refresh-rate hardware (e.g. Redmi Note 13 Pro 5G) with zero frame jitter.

### 3. Strict Display P3 Wide-Gamut Rendering
- **Wide Color Gamut Pipeline**:
  - Enable and preserve Display P3 color space across viewfinder preview surfaces, image analysis, and captured snapshots where hardware-supported.
  - Configure window color modes (`ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT`) and preview surfaces to prevent unwanted gamut clipping or inaccurate color conversion.
