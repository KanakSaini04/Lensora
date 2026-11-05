# Lensora - AI & Camera Performance Laws and Master Expectations

## Master End-State Vision

Lensora is an elite, high-performance DSLR computational camera application custom-architected for high-density mobile sensor hardware, specifically optimized for the **Redmi Note 13 Pro 5G** featuring the **200MP Samsung ISOCELL HP3 sensor**.

### Key Architectural Pillars:
- **4-Tab Sci-Fi Flight Deck**: Futuristic, high-frequency HUD navigation featuring `RIG`, `OPTICS`, `NEURAL`, and `QUANTUM` control stations.
- **Tactical Hybrid Controls**: Seamlessly blending AI-autopilot real-time scene detection with tactile manual dials (ISO, Exposure, Focus Diopters, Shutter Speed).
- **AGSL GPU Shading & VN Cinematic Templates**: Custom Android Graphics Shading Language (AGSL) runtime shaders delivering real-time filmic grading templates.
- **Zero-Shutter-Lag (ZSL) Multi-Frame Buffers**: Pre-allocated circular ring buffers capturing zero-latency RAW/YUV frames for multi-frame HDR and noise reduction.

---

## Performance Laws for Developer Agents

### 1. Zero Memory Allocations in High-Frequency ImageAnalysis Analyzer Loops
- **Strict Prohibition inside `ImageAnalysis.Analyzer.analyze()`**:
  - Never allocate objects (`Bitmap`, `ByteArray`, primitive wrappers, closure iterators, lambda allocations, or auto-boxing primitives) inside per-frame processing callbacks.
  - Re-use pre-allocated direct `ByteBuffer` instances, primitive arrays, and reusable target Bitmaps initialized during setup.
  - Always close `ImageProxy` objects (`imageProxy.close()`) immediately after extracting frame data to prevent CameraX backpressure buffer starvation and frame drops.

### 2. 120Hz Jetpack Compose UI Rendering Optimizations
- **Defer High-Frequency State Reads to Draw / Layout Phases**:
  - Prevent high-frequency state updates (zoom ratios, exposure levels, bounding box coordinates, optical flow parameters) from triggering recompositions across the composable tree.
  - Use lambda modifiers (`Modifier.graphicsLayer { ... }`, `Modifier.drawWithContent { ... }`) and pointer input deferral to isolate state reads directly to layout/draw phases.
  - Guarantee smooth 120Hz refresh rates on high-refresh-rate displays with zero frame jitter or stutter.

### 3. Strict Display P3 Wide-Gamut Color Space Handling
- **Wide Color Gamut Preservation**:
  - Preserve Display P3 wide color space across viewfinder preview surfaces, image analysis, and captured snapshots where hardware-supported.
  - Configure window color modes (`ActivityInfo.COLOR_MODE_WIDE_COLOR_GAMUT`) and preview surface configurations to prevent unwanted gamut clipping.

### 4. Strict Coroutine `Dispatchers.IO` / `Dispatchers.Default` Isolation
- **Non-Blocking Main Thread Execution**:
  - Offload all disk I/O, image encoding, Tesseract/OCR processing, and computer vision matrix transforms to `Dispatchers.IO` or dedicated high-priority background threads.
  - Never execute file writes or matrix transforms on `Dispatchers.Main`.
