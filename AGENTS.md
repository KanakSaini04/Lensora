# Lensora - AI & Camera Performance Rules for Developer Agents

## Core Performance Rules

### 1. Zero Memory Allocations in Per-Frame Camera Loops
- **No Object Allocations on ImageAnalysis Frames**:
  - Never allocate objects (`Bitmap`, `ByteArray`, temporary buffers, closures, iterator wrappers, lambda allocations, or auto-boxing primitives) inside `ImageAnalysis.Analyzer.analyze()` or per-frame processing callbacks.
  - Re-use pre-allocated buffers (`ByteBuffer`, `FloatArray`, re-usable `Bitmap` targets) created outside the frame pipeline during initialization.
  - Close frame images (`ImageProxy.close()`) immediately after extraction to prevent CameraX frame drops and backpressure buffer starvation.

### 2. 120Hz Deferred Compose State Reads
- **Defer State Reads to Draw / Layout Phases**:
  - Avoid triggering Composable re-compositions on high-frequency state changes (e.g., camera zoom level changes, exposure value shifts, bounding box coordinate updates, optical flow offsets).
  - Use lambda modifiers such as `Modifier.graphicsLayer { ... }`, `Modifier.drawWithContent { ... }`, or custom layout phase state reads to isolate state reads to layout/draw phases.
  - Ensure zero frame drops or unnecessary re-compositions when running on 120Hz high-refresh-rate displays.

### 3. Display P3 Wide-Gamut Rendering
- **Wide Color Gamut Support**:
  - Enable and preserve Display P3 color space across image capture, viewfinder rendering, and preview pipelines where supported by hardware.
  - Configure window color mode and graphics context to support `ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT` or Display P3 surface output.
  - Prevent unwanted color gamut truncation or inaccurate color conversion during real-time image processing or preview rendering.
