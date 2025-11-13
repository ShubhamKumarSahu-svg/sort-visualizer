# Sort Visualizer Pro - Refactored Project Structure

## Project Overview

A Java Swing application for visualizing sorting algorithms with sound effects and comparison mode.

## Project Structure (10+ Files)

```
src/
├── main/
│   └── Main.java                          # Application entry point
├── constants/
│   ├── UIConstants.java                   # UI colors, fonts, dimensions
│   └── AlgorithmConstants.java            # Algorithm names and descriptions
├── model/
│   └── SortingState.java                  # Manages sorting thread state
├── audio/
│   └── ToneGenerator.java                 # MIDI sound generation
├── util/
│   ├── ArrayGenerator.java                # Random array generation
│   └── TimeFormatter.java                 # Time formatting utilities
├── algorithms/
│   └── SortingAlgorithms.java            # All sorting algorithm implementations
├── ui/
│   ├── SortVisualizerFrame.java          # Main application frame
│   ├── callbacks/
│   │   └── VisualizationCallback.java    # Interface for visualization updates
│   ├── components/
│   │   ├── UIComponentFactory.java       # Factory for styled UI components
│   │   └── BarPanel.java                 # Custom panel for bar visualization
│   └── controllers/
│       └── SortController.java           # Controls sorting execution
```

## File Responsibilities

### 1. **Main.java** (Entry Point)

- Application initialization
- Sets up Look & Feel
- Creates and displays main frame

### 2. **UIConstants.java** (UI Configuration)

- Color definitions (background, text, buttons, bars)
- Font definitions
- Dimension constants
- Centralized UI styling

### 3. **AlgorithmConstants.java** (Algorithm Data)

- List of available algorithms
- Algorithm descriptions with Big-O complexity
- Centralized algorithm metadata

### 4. **SortingState.java** (State Management)

- Manages worker and timer threads
- Tracks start time and completion status
- Provides stop request mechanism
- Encapsulates sorting execution state

### 5. **ToneGenerator.java** (Audio)

- MIDI synthesizer initialization
- Plays tones for comparisons and swaps
- Manages audio playback lifecycle
- Thread-safe sound generation

### 6. **ArrayGenerator.java** (Utility)

- Generates random integer arrays
- Array copying functionality
- Centralized array creation logic

### 7. **TimeFormatter.java** (Utility)

- Formats elapsed time (seconds, minutes)
- Consistent time display across UI

### 8. **SortingAlgorithms.java** (Core Logic)

- Implements 12 sorting algorithms:
  - Bubble, Selection, Insertion
  - Merge, Quick, Heap
  - Shell, Cocktail, Comb
  - Gnome, Radix, Counting
- Uses callback interface for visualization
- Sound and animation integration

### 9. **VisualizationCallback.java** (Interface)

- Defines contract for visualization updates
- Methods: highlight, clear, repaint, increment stats, delay
- Decouples algorithms from UI

### 10. **UIComponentFactory.java** (Component Creation)

- Factory methods for styled components
- Creates ComboBoxes, Sliders, Buttons, Labels
- Consistent styling across application
- Reduces code duplication

### 11. **BarPanel.java** (Visualization)

- Custom JPanel for bar chart rendering
- Handles single and comparison modes
- Displays statistics overlay
- Gradient bars with highlights
- Grid background and legend

### 12. **SortVisualizerFrame.java** (Main UI)

- Main application window
- Layout management
- Component initialization
- Mode switching (single/comparison)
- Event listener setup

### 13. **SortController.java** (Control Logic)

- Orchestrates sorting execution
- Manages single and comparison sorting
- Creates and manages threads
- Handles start/stop operations
- Comparison result calculation

## Key Improvements

### ✅ Separation of Concerns

- UI separated from logic
- Constants extracted to dedicated files
- Controller pattern for business logic

### ✅ Maintainability

- Each file has a single, clear responsibility
- Easy to locate and modify specific features
- Reduced file sizes (all under 400 lines)

### ✅ Extensibility

- Easy to add new algorithms (just modify SortingAlgorithms.java and AlgorithmConstants.java)
- Easy to add new UI themes (modify UIConstants.java)
- Callback interface allows alternative visualizations

### ✅ Testability

- Algorithms can be tested independently
- UI components can be tested in isolation
- State management is centralized

## How to Run

```bash
# Compile
javac -d bin src/**/*.java

# Run
java -cp bin main.Main
```

## Adding New Features

### Adding a New Sorting Algorithm

1. Add algorithm name to `AlgorithmConstants.ALGORITHMS`
2. Add description to `AlgorithmConstants.DESCRIPTIONS`
3. Implement method in `SortingAlgorithms.java`
4. Add case to `SortController.runAlgorithm()`

### Changing UI Colors

1. Modify color constants in `UIConstants.java`
2. All components automatically use new colors

### Adding New Statistics

1. Add field to `BarPanel.java`
2. Add increment method
3. Update `paintComponent()` to display
4. Call from algorithm via callback

## Dependencies

- Java 11+
- Java Swing (built-in)
- Java Sound MIDI (built-in)

## Features

- ✨ 12 sorting algorithms
- 🎵 Real-time sound effects
- ⚖️ Side-by-side algorithm comparison
- 📊 Live statistics (comparisons, swaps, time)
- 🎨 Modern gradient UI with dark theme
- ⚡ Adjustable speed and array size
- 🎯 Visual highlighting of active elements
